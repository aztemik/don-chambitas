# Modelo entidad relación

El modelo vive en `basedatos/`. **Ese SQL es la fuente de verdad.** Este
documento explica el porqué; si los dos se contradicen, gana el SQL.

Motor: PostgreSQL 15 o superior, sobre Supabase (DEC-16).

| Archivo | Qué deja |
|---|---|
| `01_esquema.sql` | Tablas, tipos, triggers, vistas y funciones RPC |
| `02_politicas_rls.sql` | Permisos, políticas RLS y tiempo real |
| `03_almacenamiento.sql` | Cubetas de Storage y sus políticas |
| `04_datos_semilla.sql` | 16 categorías, 32 estados, 26 municipios |

Se corren en ese orden. El detalle operativo está en `basedatos/README.md`.

## Las tablas

| Tabla | Para qué |
|---|---|
| `estados` | Catálogo de las 32 entidades |
| `municipios` | Catálogo de municipios por estado |
| `categorias` | Los 16 oficios |
| `usuarios` | Perfil público. Su `id` **es** el de `auth.users` |
| `perfiles_trabajador` | Datos profesionales, uno a uno con usuario |
| `perfil_habilidades` | Habilidades del trabajador |
| `servicios` | Lo que el trabajador ofrece |
| `servicio_fotos` | Hasta 3 fotos por servicio |
| `solicitudes` | Lo que el cliente necesita |
| `postulaciones` | El trabajador se ofrece a una solicitud |
| `conversaciones` | Hilo de chat entre un cliente y un trabajador |
| `mensajes` | Los mensajes del hilo |
| `resenas` | Calificación de 1 a 5 más comentario |
| `ia_consumo` | Tope de llamadas por usuario y día |
| `ia_cache` | Respuestas de IA ya pagadas |

**No hay tabla de contraseñas ni de recuperación.** Las dos existían cuando el
backend podía ser propio. Con Supabase Auth, la credencial y el correo de
recuperación (P-04) los lleva Supabase.

## Relaciones

```
auth.users 1 ──── 1 usuarios
                      │
                      ├── 0..1 perfiles_trabajador
                      │          ├── N perfil_habilidades
                      │          ├── N servicios ──── N servicio_fotos
                      │          ├── N postulaciones
                      │          └── N resenas
                      │
                      ├── N solicitudes (como cliente) ──── N postulaciones
                      │                       └── 0..1 resenas
                      └── N conversaciones ──── N mensajes

categorias 1 ──── N servicios, N solicitudes
estados    1 ──── N municipios
```

## Decisiones que conviene entender

**La identidad la lleva Supabase Auth.** `usuarios.id` es el mismo uuid que
`auth.users.id` y que devuelve `auth.uid()`. La aplicación **nunca** consulta
`auth.users`. La fila de `usuarios` la crea sola el trigger
`tg_auth_usuario_creado` al registrarse, leyendo nombre, apellidos, teléfono y
rol del metadata (`options.data` de `signUpWith`).

Consecuencia práctica: **los usuarios no se pueden sembrar con un `INSERT`**.
Para datos de prueba, hay que registrar cuentas desde la aplicación o desde la
consola de Supabase.

**`perfiles_trabajador` usa `usuario_id` como llave primaria.** La relación es
estrictamente uno a uno, y todo lo que apunta a un trabajador apunta a su
perfil. Así es imposible que exista un servicio de alguien sin perfil.

**Los roles son excluyentes y la base lo sostiene.** `perfiles_trabajador` y
`solicitudes` llevan una columna `rol` fija por `CHECK` y una llave foránea
compuesta contra `usuarios(id, rol)`. Un cliente no puede tener perfil de
trabajador ni aunque la aplicación se equivoque, y el rol de un usuario no se
puede cambiar mientras tenga perfil o solicitudes. Eso es `DEC-22`: en el MVP no
se cambia de rol.

**`estatus` no es `estado_id`.** En `solicitudes` y `postulaciones`, `estatus`
es el estado del flujo (`abierta`, `asignada`, `cerrada`, `cancelada`) y
`estado_id` es la entidad federativa. Antes las dos columnas se llamaban
`estado` y era una trampa esperando a alguien.

**Cinco reglas viven en triggers, no en la aplicación.** La aplicación las va a
olvidar tarde o temprano:

1. Nadie se postula a su propia solicitud, ni a una que no esté abierta.
2. Solo se reseña una solicitud cerrada, solo el cliente que la publicó, y solo
   sobre el trabajador que quedó asignado.
3. Nadie escribe en una conversación de la que no es parte.
4. Cerrar una solicitud exige trabajador asignado, y sella `cerrada_en` sola.
5. Al borrar un trabajador, sus solicitudes `asignada` vuelven a `abierta` y las
   `cerrada` conservan el historial sin trabajador.

**Las cinco funciones son `security definer` con `search_path` fijo.** No es
adorno ni exceso de celo: sin eso corren con los permisos de quien dispara el
trigger, RLS les esconde las filas de los demás, el `SELECT` devuelve `NULL`,
el `IF` se evalúa a `NULL` y la regla **no se aplica, en silencio y sin error**.
Un `UPDATE` en esa situación afecta cero filas y tampoco avisa: así es como
`ultimo_mensaje_en` se quedaba en `NULL` para siempre y la bandeja de P-15 se
quedaba sin criterio de orden.

Ojo al probarlo: `91_prueba_funcional.sql` corre como `postgres`, que se salta
RLS, así que estas fallas **no se ven ahí**. Quien agregue un trigger que lea o
escriba una fila ajena tiene que repetir el `security definer`.

La quinta merece explicación. `solicitudes.trabajador_id` es `ON DELETE SET
NULL`, y el `CHECK` que exige trabajador aplica **solo a `asignada`**, no a
`cerrada`. Si aplicara a las dos, borrar la cuenta de un trabajador que alguna
vez cerró un trabajo violaría el `CHECK` y abortaría la transacción: el usuario
quedaría atrapado sin poder darse de baja. Que no se pueda *cerrar* sin
trabajador lo impone `fn_validar_transicion_solicitud`, sobre la transición y no
sobre la fila.

**Búsqueda con `pg_trgm`.** Índices GIN sobre los títulos de perfiles,
servicios y solicitudes. Eso hace que "plomeria" encuentre "plomería" y que un
`ilike '%plom%'` no se arrastre. En Supabase la extensión vive en el esquema
`extensions`, por eso los índices califican el operador:
`extensions.gin_trgm_ops`.

**Máximo 3 fotos por servicio** por restricción de posición entre 1 y 3 más un
único por servicio y posición. No es una regla de la aplicación, es de la base.

**La caché de IA se indexa por función y hash.** La llave primaria es
`(funcion, entrada_hash)`. Con el hash solo, el mismo texto pedido a
`redactar_perfil` y a `categorizar` se pisaba y devolvía la respuesta
equivocada.

**El tope de IA son 10 llamadas al día en total** (DEC-18), no 10 por función.
La llave primaria de `ia_consumo` conserva la desagregación por función para
poder medir, pero `fn_ia_registrar_llamada` suma las cuatro.

## Vistas

Las dos llevan `security_invoker = on`: se ejecutan con los permisos de quien
consulta. Sin eso, una vista se salta las políticas RLS de las tablas que tiene
debajo y se convierte en una fuga.

`vw_trabajador_calificacion` devuelve promedio y total de reseñas por
trabajador.

`vw_busqueda_trabajadores` devuelve la tarjeta de resultados de P-06 ya armada:
nombre, foto, título, ubicación, promedio, número de servicios activos y precio
mínimo. Los filtros de P-06 se aplican encima de esta vista desde postgrest.

Lleva además `categorias`, un arreglo con las categorías de sus servicios
activos, y `creado_en`. Sin esos dos la vista no podía sostener lo que promete
`CONTRATOS-API.md`: **el filtro por categoría es el principal de P-06 y de la
cuadrícula de P-05**, y no había con qué. Es arreglo y no columna suelta porque
un trabajador ofrece varios oficios; se filtra con `categorias=cs.{3}`.

## Funciones RPC

Lo que **no** se resuelve con una consulta suelta, porque son varias escrituras
que tienen que pasar juntas o porque serían cinco viajes de red:

| Función | Por qué existe |
|---|---|
| `fn_perfil_publico_trabajador(uuid)` | P-07 entera en un viaje |
| `fn_aceptar_postulacion(uuid)` | Acepta una, rechaza las demás y asigna la solicitud, o no hace nada |
| `fn_cerrar_solicitud(uuid)` | Valida y sella el cierre, habilitando la reseña |
| `fn_abrir_conversacion(uuid, uuid)` | Upsert con unicidad sobre un `COALESCE` |
| `fn_ia_registrar_llamada(...)` | Tope diario. Solo la llama la Edge Function |

## Seguridad

**`02_politicas_rls.sql` no es opcional.** La aplicación lleva la `anon key`,
que es pública por diseño: cualquiera la saca del APK. Lo único que impide leer
la base entera con esa llave son las políticas RLS. Sin ese archivo, el proyecto
está abierto.

Lo que las políticas garantizan, en corto:

- Si eres **cliente**, tu correo y tu teléfono solo los ves tú y la contraparte
  con la que ya estás tratando.
- Un trabajador **no ve** las postulaciones de sus competidores.
- Nadie lee una conversación de la que no es parte.
- `ia_consumo` e `ia_cache` tienen RLS activo y **cero políticas**: con la
  `anon key` ahí no entra nadie. Las escribe solo la Edge Function con la
  `service_role` key.

**RLS no basta para cerrar una función.** PostgreSQL le da `EXECUTE` a `PUBLIC`
en cuanto se crea una, y postgrest publica como RPC toda función del esquema
`public` que el rol de quien llama pueda ejecutar. Una función `security
definer` se salta las políticas por definición, así que `fn_ia_registrar_llamada`
quedaba llamable con la `anon key`: cualquiera podía escribir en `ia_consumo` y
quemarle el tope diario a otro usuario con solo conocer su uuid. Por eso
`02_politicas_rls.sql` termina revocando `EXECUTE` función por función. Quien
agregue una RPC nueva tiene que decidir quién puede ejecutarla; no hacerlo la
deja abierta.

**RLS tampoco distingue columnas**, solo filas. `usuarios` y `mensajes` llevan
permisos por columna para que nadie se cambie el correo o el rol, ni reescriba
el contenido de un mensaje que recibió.

Las reseñas no se editan ni se borran. Si hubiera que moderar alguna, se hace
desde la consola (DEC-13).

### Lo que las políticas NO impiden, a propósito

Tres cosas que el modelo deja abiertas con decisión tomada. Están aquí para que
nadie las descubra en una revisión y crea que encontró un hueco.

**La ficha de un trabajador se ve entera, correo y teléfono incluidos, para
cualquier usuario con sesión** (DEC-19). La política `usuario ve la ficha de
cualquier trabajador` abre la fila completa, y tiene que hacerlo:
`vw_busqueda_trabajadores` es `security_invoker` y necesita el `join` contra
`usuarios`. El trabajador publica su contacto a propósito, que es a lo que vino
a la aplicación. Lo que sigue cerrado es la ficha del **cliente**. Esto hay que
declararlo en el formulario de seguridad de los datos de Play Store (S6-T08).

**El trabajador no puede abrir una conversación** (DEC-20). `fn_abrir_conversacion`
fija `cliente_id = auth.uid()`, y `conversaciones.trabajador_id` apunta a
`perfiles_trabajador`: si la llama un trabajador, la llave foránea la rechaza.
El hilo nace en P-07 o al aceptarle la postulación, y de ahí el trabajador
responde. No es una limitación accidental: evita el mensaje en frío.

**Que aceptar una postulación sea atómico es regla del contrato, no de la
base** (DEC-21). El cliente puede llevar su propia solicitud a `asignada` con un
`update` suelto y poner al trabajador que quiera, sin postulación de por medio:
es su fila, la política se lo permite y el `CHECK` solo exige que
`trabajador_id` no sea nulo. Cerrarlo pedía un trigger de transición de estado
que hoy no cabe. El único que puede hacerlo es el dueño de la solicitud, así que
el daño se lo hace a sí mismo. La aplicación usa `fn_aceptar_postulacion`
siempre; un `update` suelto a `estatus` es un bug de quien lo escriba.

### La trampa de la recursión

Una política **no puede consultar directamente una tabla cuya política
consulte de vuelta la primera.** Leer cualquiera de las dos dispara la política
de la otra, que dispara la de la primera, y PostgreSQL aborta con
`infinite recursion detected in policy`.

Pasó exactamente entre `solicitudes` y `postulaciones`: una preguntaba «¿me
postulé a esta?» y la otra «¿es mi solicitud?». Se detectó el **2026-09-16 en
UTC** al correr `91_prueba_funcional.sql` contra el proyecto real, la primera
vez que se levantó el esquema. El reloj de Supabase va en UTC, así que las
fechas que salen de la base pueden ir un día por delante de la local; no es un
error de quien lo anotó.

La salida es meter la consulta dentro de una función `security definer`, que
corre como el dueño de la tabla y no evalúa políticas. De ahí salen
`fn_es_mi_solicitud` y `fn_me_postule_a`. **No es un atajo: es la única forma
de expresarlo.**

Quien agregue una política nueva que mire otra tabla tiene que comprobar que
no cierra un ciclo. Las pruebas 21 a 24 de `91_prueba_funcional.sql` lo
vigilan: hacen 30 lecturas cruzadas con tres usuarios distintos.

## Lo que queda pendiente

**Catálogo completo de municipios.** Hoy hay 26 de zonas de prueba. Los 2,469
del país se cargan del INEGI con un `COPY` desde CSV. Es PEND-02 y no bloquea a
nadie.

## Estado

**El esquema está levantado y verificado.** El 2026-09-15 se reinstaló desde
cero —`00` dejó el proyecto vacío, incluidas las cubetas— y corrieron `01` a
`04` sin un solo error. Las dos verificaciones pasaron completas:
`90_verificacion.sql` **42 de 42**, `91_prueba_funcional.sql` **25 de 25**.

Eso cubre la estructura y el comportamiento: los cinco triggers rechazan lo que
tienen que rechazar, aceptar una postulación asigna y rechaza a las demás,
cerrar sella `cerrada_en`, el tope de IA corta en la llamada 11, la baja de un
trabajador con historial deja la solicitud cerrada con `trabajador_id` nulo, y
las 30 lecturas cruzadas de las pruebas 21 a 24 no vuelven a entrar en
recursión.

**Lo que todavía no está probado** y sigue siendo `S1-T03`:

- RLS **con la `anon key` y dos sesiones reales**. Las pruebas 21 a 24 usan
  `set role authenticated` dentro de una transacción, que es una buena
  aproximación, pero no es lo mismo que dos clientes contra PostgREST.
- Las pruebas 10, 12 y 13 de `91` corren como `postgres`, que se salta RLS, así
  que **pasan siempre**: no demuestran nada sobre las reglas que vigilan.
  Moverlas al bloque de `set role authenticated` es trabajo pendiente.
- El diagrama `docs/tecnico/diagrama-er.png`.
- El cruce contra las historias de `S1-T01`.
