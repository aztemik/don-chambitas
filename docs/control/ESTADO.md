# Estado actual

> Archivo **vivo**. Quien termina una tarea lo actualiza. Es la primera cosa
> que lee el agente y la única fuente confiable sobre qué está pasando hoy.

**Última actualización:** 2026-09-16

---

## Sprint en curso

| Campo | Valor |
|---|---|
| Sprint | 1 |
| Fechas | PENDIENTE |
| Tareas del sprint | 16 |
| Terminadas | 2 |
| En curso | 1 |
| Bloqueadas | 0 |

## Tarea en curso

**`S1-T03` — Modelo entidad-relación (ER) completo del sistema.** No está
terminada: le faltan las pruebas que exigen el proyecto de Supabase en vivo.

| Campo | Valor |
|---|---|
| ID | `S1-T03` |
| Título | Modelo entidad-relación (ER) completo del sistema |
| Quién la tomó | LMM |
| Rama | `docs/S1-T03-validacion-modelo-er` |
| Desde | 2026-09-16 |

**Lo que ya está hecho, y se puede revisar leyendo:**

- **El diagrama**, en `docs/tecnico/diagrama-er.png`: las 15 tablas de `public`
  más `auth.users`, con llaves y cardinalidades. Lo dibuja
  `docs/tecnico/diagrama-er.py`, para que la próxima vez que cambie el esquema
  se regenere en vez de retocarse a mano. *Ese generador no lo pedía el ticket:
  si el líder lo prefiere fuera, se borra y la imagen se queda.*
- **El cruce de las 33 historias de `S1-T01` contra las tablas**, historia por
  historia, en `MODELO-ER.md` → "Cobertura de las historias". Ninguna historia
  pide una tabla que no exista. **Tres quedan con hueco.**
- **Las pruebas 10, 12 y 13 de `91_prueba_funcional.sql` ya corren con
  `set role authenticated`**, que era lo que pedía el ticket. Antes corrían
  como `postgres`, que se salta RLS, y por eso no demostraban nada: con RLS
  apagado, a una función de trigger le puede faltar el `security definer` y
  comportarse igual de bien. Las tres exigen además de qué capa viene el
  rechazo, para que ninguna pueda pasar por la razón equivocada.
- **Corrido y verificado el 2026-09-16** contra el proyecto real: `01` a `04`
  sin error, `90` **42 de 42** y `91` **25 de 25**. Las tres pruebas
  reescritas pasaron trayendo el mensaje de su trigger, no el de una política,
  que es la prueba de que pasaron por la razón correcta. El desglose está en
  `MODELO-ER.md`.

- **El paso 2 quedó completo el 2026-09-16.** Cerrar una solicitud sin
  trabajador asignado se probó a mano y falló por las dos vías, como debe:
  `fn_cerrar_solicitud` con *"Solo se puede cerrar una solicitud asignada"* y
  un `update` suelto con *"No se puede cerrar una solicitud sin trabajador
  asignado"*. Con eso son cuatro de los cinco triggers demostrados; el quinto
  no se puede demostrar (`H-08`).

**Lo que falta:**

- **El paso 2c, y nada más: RLS con la `anon key` y dos sesiones reales contra
  PostgREST.** Necesita la URL del proyecto y la `anon key`, que no están en el
  repositorio —`local.properties` solo trae `sdk.dir`— y no deben estarlo.

De los siete criterios de aceptación, **seis están cumplidos**. El séptimo —la
`anon key`— es el único que queda, más la decisión del líder sobre `H-08`.

## Tres huecos nuevos para el líder

Salen del cruce contra las historias. **Ninguno se corrigió**: el esquema no se
rediseña en `S1-T03`. Están explicados al final de `MODELO-ER.md`.

- **`H-05` — La búsqueda por texto no ignora los acentos.** HU-17 promete que
  "plomeria" encuentre "plomería". `pg_trgm` acelera el `ilike`, no cambia lo
  que el `ilike` considera igual, y `unaccent` no está instalado; el contrato
  de `buscarTrabajadores` busca justamente con `ilike`. Es el más serio de los
  tres y **conviene cerrarlo antes de `S4-T04`**, porque decide si hace falta
  un índice sobre `unaccent(titulo)`, una columna normalizada o una RPC.
- **`H-06` — Nada borra el archivo de Storage cuando desaparece su fila.**
  HU-11 promete que no queden archivos huérfanos. Las filas se limpian solas
  por cascada; los objetos de las cubetas no los toca nadie, y la cascada se
  lleva la URL antes de que alguien pueda usarla. Es un orden de operaciones
  que ningún contrato dice todavía: le toca a `S3-T08` y `S2-T12`.
- **`H-07` — "Postulaciones sin revisar" no existe como dato.** HU-13 las
  cuenta; la base solo sabe de `enviada`, que es "no decidida", no "no vista".
  Lo más probable es que sea la redacción y no el esquema; si es eso, se
  aclara la historia y no se toca nada.
- **`H-08` — Uno de los cinco triggers del paso 2 no se puede probar.**
  "Postularse a tu propia solicitud" exigiría un uuid que fuera cliente y
  trabajador a la vez, y los roles excluyentes lo impiden antes de que el
  trigger opine. Esa rama de `fn_validar_postulacion` es código defensivo que
  nunca se ejecuta. No es un defecto, pero el criterio de aceptación pide
  demostrarlo y no se puede: o baja a cuatro triggers, o se anota que al
  quinto lo sostiene el esquema.

## Última tarea terminada

**`S1-T02` — Definición del alcance del MVP y lista completa de pantallas.**
2026-09-15. Rama `docs/S1-T02-alcance-mvp-pantallas`, salida de `main`, pull
request **sin abrir todavía**. `S1-T01` se integró a `main` en el pull request
**#1**.

El cruce de las 33 historias contra las 19 pantallas **dio limpio en las tres
revisiones del ticket**: ninguna historia sin pantalla, ninguna pantalla sin
historia, y ninguna historia que exija algo de la lista "FUERA del MVP". El
mapa se reconstruyó historia por historia y coincide con el que dejó `S1-T01`.
Queda documentado en `PANTALLAS.md`, en la sección nueva "Verificación de
cobertura". No se agregó ni se quitó ninguna pantalla y `PRODUCTO.md` no se
tocó.

**Los cuatro hallazgos que dejó, ya resueltos.** El líder los cerró el
2026-09-15, fuera del alcance de `S1-T02`. Quedan explicados al final de
`PANTALLAS.md`:

- **H-01** — Los filtros de búsqueda eran cinco en HU-18 y tres en
  `PRODUCTO.md`, P-06 y `S4-T05`. **Se amplió el alcance a cinco:** categoría,
  estado, municipio, precio y calificación. El dato ya existía porque HU-06
  obliga a capturar estado y municipio.
- **H-02** — **Se actualizó el renglón de P-06** con el ordenamiento y la
  paginación que HU-18 exige y que `S4-T07` ya cubría como tarea.
- **H-03** — Las notificaciones locales **se quedan sin historia propia**,
  cubiertas por los criterios de HU-22 y HU-26. Qué pasa al tocarlas lo define
  el ticket de `S5-T10`.
- **H-04** — No era decisión: el esquema ya la tenía tomada. **Se escribió la
  regla en HU-24**, un hilo por cliente-trabajador-solicitud más uno suelto
  para el contacto que nace en P-07.

## Siguiente en la cola

`S1-T04` — Configuración del proyecto Android (Gradle, Kotlin, Compose, Hilt)
(prioridad 900, sin dependencias)

No se toma hasta que `S1-T03` cierre: una tarea a la vez por persona.

## Decisiones recientes

**2026-09-16 · La base se volvió a levantar y a verificar.** `01` a `04` sin
error, `90_verificacion.sql` **42 de 42** y `91_prueba_funcional.sql`
**25 de 25**, ya con las pruebas 10, 12 y 13 corriendo con RLS activo. Las tres
pasaron por la razón correcta.

**2026-09-16 · `S1-T03` casi cierra.** Quedan hechos el diagrama, el cruce
contra las 33 historias y las tres pruebas con RLS. Falta el paso 2c —la
`anon key` con dos sesiones reales— y un caso suelto del paso 2. Salieron
cuatro huecos nuevos —`H-05` a `H-08`— y ninguno se corrigió: son del líder.

**2026-09-15 · Dos decisiones nuevas: `DEC-23` y `DEC-24`.** Salen de los
hallazgos que dejó `S1-T02`. La primera amplía los filtros de búsqueda de tres
a cinco —se agregan estado y municipio— y con eso cambia `PRODUCTO.md`, el
renglón de P-06 y el título de `S4-T05`. La segunda pone por escrito la regla
de unicidad del hilo de chat que el esquema ya aplicaba, y que HU-24 no
describía para el caso de P-07.

Los otros dos hallazgos no necesitaron número: H-02 era la descripción de P-06,
que había quedado corta frente a HU-18, y H-03 se cerró aceptando que las
notificaciones locales sigan cubiertas por los criterios de HU-22 y HU-26 en
vez de tener historia propia. Los cuatro están explicados al final de
`PANTALLAS.md`.

**2026-09-15 · La base de datos está levantada y verificada.** Reinstalación
desde cero contra el proyecto de Supabase: `00_reinicio.sql` dejó todo en cero
—las once comprobaciones en `OK`, cubetas incluidas— y `01` a `04` corrieron sin
un solo error. `90_verificacion.sql` dio **42 de 42** y `91_prueba_funcional.sql`
**25 de 25**.

Falta lo que ningún script puede hacer solo, y es lo que cierra `S1-T03`:
probar RLS con la `anon key` y dos sesiones reales, mover las pruebas 10, 12 y
13 de `91` al bloque de `set role authenticated` (hoy corren como `postgres` y
pasan siempre), el diagrama ER y el cruce contra las historias de `S1-T01`.

**2026-09-15 · Revisión de `basedatos/` antes de volver a levantar el esquema.**
Se encontraron cinco defectos que `91_prueba_funcional.sql` **no detecta**,
porque corre como `postgres` y se salta RLS: cinco funciones de trigger sin
`security definer` que dejaban sus reglas sin aplicar en silencio, la vista de
búsqueda sin con qué filtrar por categoría, `fn_ia_registrar_llamada`
ejecutable con la `anon key`, permisos de `update` más anchos que el contrato,
y el semillero de categorías sin poder reconciliar los iconos de S1-T08. Los
cinco están corregidos en los archivos; `90_verificacion.sql` pasó de 36 a 42
comprobaciones y las seis nuevas son las que vigilan justo eso.

`00_reinicio.sql` ahora borra también los archivos y las dos cubetas de
Storage: el reinicio es total.

**2026-09-15 · Tres decisiones tomadas: `DEC-19`, `DEC-20` y `DEC-21`.** El
correo del trabajador es visible para cualquier usuario con sesión, el chat lo
abre siempre el cliente, y la atomicidad al aceptar una postulación es regla
del contrato y no de la base. Las tres están explicadas en `MODELO-ER.md`, en
"Lo que las políticas NO impiden, a propósito". `DEC-22` solo pone número a
algo que ya estaba decidido: los roles no se cambian.

**2026-09-14 · PEND-01 resuelto: el backend es Supabase.** Ver `DEC-16` y
`DEC-17`. Las seis tareas que estaban `bloqueada` pasaron a `pendiente` y ya no
queda ninguna tarea bloqueada en toda la cola. El cliente es `supabase-kt`:
Retrofit y OkHttp salieron del stack.

Esto **no** cambia cómo se trabaja hoy. La implementación activa sigue siendo
`FuenteDatosFalsa` hasta que cada tarea real llegue en su turno.

---

## Cómo se llena

Al **tomar** una tarea: llena "Tarea en curso" y pon la tarea en `en curso`
dentro de `docs/tareas/INDICE.md`.

Al **terminarla**: mueve la tarea a "Última tarea terminada", vacía "Tarea en
curso", recalcula el contador del sprint y actualiza "Siguiente en la cola".

Si dos personas van a trabajar el mismo día, la segunda revisa este archivo
**antes** de pedirle nada al agente. Si dice que hay una tarea en curso, esa
tarea no se toca.
