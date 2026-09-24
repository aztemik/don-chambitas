# Contratos de datos

Este documento define **qué operaciones existen y qué devuelve cada una**. Es el
contrato entre la interfaz y los datos, y se respeta esté quien esté del otro
lado.

**Si necesitas una operación que no está aquí, detente y repórtalo.** No la
inventes sobre la marcha. Esa regla es lo más importante del documento.

## Qué cambió con Supabase, y qué no

Antes esto era una lista de endpoints REST (`POST /auth/registro`, `GET
/servicios/mios`) de un backend que íbamos a escribir. Con DEC-16 ese backend
no existe: no hay servidor propio, no hay rutas que programar, no hay
`BASE_URL`. Esa parte del documento era ficción y se fue.

Lo que **no** se fue, porque no dependía del backend:

| Sigue en pie | Por qué es innegociable |
|---|---|
| **El catálogo cerrado de operaciones** | Es lo que impide que cada quien invente su propia forma de leer servicios. Si no está aquí, no se hace |
| **La forma de cada respuesta** | Las pantallas se programaron contra estas formas. Cambiar un campo rompe una pantalla |
| **La taxonomía de errores** | `TipoError` es lo que los ViewModels saben manejar y lo que `EstadoError` sabe pintar |
| **Qué es una operación atómica** | Aceptar una postulación son tres escrituras. Partirlas en tres llamadas es un error de corrección, no de estilo |
| **El contrato de la IA** | `429` y `503` son condiciones normales, no caídas. La aplicación tiene que seguir funcionando |

Lo que cambió es **con qué se cumple**: en vez de un endpoint nuestro, una
tabla, una vista, una función RPC o un servicio de Supabase.

## Cómo se usa este documento

La columna «Operación» es el método del repositorio. La columna «Con qué» es lo
que ese repositorio llama por dentro, y **no sale de `datos/`**: ninguna pantalla
ni ViewModel sabe que Supabase existe (ver `ARQUITECTURA.md`).

Todos los métodos son `suspend` y devuelven `Resultado<T>`.

---

## Autenticación · `RepositorioAuth`

| Operación | Entrada | Salida | Efecto de sesión | Con qué |
|---|---|---|---|---|
| `registrar(correo, contrasena, nombre, apellidos, telefono, rol)` | Correo normalizado, contraseña, nombre, apellidos, teléfono opcional y `RolUsuario` | `Resultado<Sesion>` | Abre la sesión del usuario creado | `auth.signUpWith(Email)` con confirmación de correo desactivada |
| `iniciarSesion(correo, contrasena)` | Correo y contraseña | `Resultado<Sesion>` | Abre o reemplaza la sesión activa | `auth.signInWith(Email)` |
| `recuperarContrasena(correo)` | Correo | `Resultado<Unit>` | No modifica la sesión actual | `auth.resetPasswordForEmail(correo, redirectUrl = URL_RECUPERACION)` |
| `cambiarContrasena(nueva)` | Contraseña nueva | `Resultado<Unit>` | Conserva la sesión activa | `auth.updateUser { password = nueva }` |
| `cerrarSesion()` | Sin parámetros | `Resultado<Unit>` | Elimina la sesión activa | `auth.signOut()` |
| `sesionActual()` | Sin parámetros | `Flow<Sesion?>` | Observa la sesión administrada por el cliente | `auth.sessionStatus` |

### Registro y sesión

`registrar` manda en `data` estos metadatos con sus nombres exactos:

```json
{
  "nombre": "Ana",
  "apellidos": "López Pérez",
  "telefono": "2221234567",
  "rol": "cliente"
}
```

`telefono` se omite cuando no fue capturado y `rol` usa el valor persistido de
`RolUsuario` (`cliente` o `trabajador`), no el nombre de la constante Kotlin.
El correo se recorta y se convierte a minúsculas antes de enviarlo.

Por `DEC-25`, **Confirm email debe estar desactivado en Supabase Auth**. El alta
termina con una sesión activa y por eso el resultado es `Sesion`, no `Usuario`.
Si `signUpWith` no deja sesión disponible, la operación falla como
`DESCONOCIDO`; nunca se reporta un registro exitoso sin sesión. El token lo
administra `supabase-kt` y no se persiste a mano.

La fila de `public.usuarios` **no se inserta desde la aplicación**: la crea el
trigger `tg_auth_usuario_creado` leyendo los metadatos del registro. Si el
registro no manda `rol`, el usuario queda como `cliente`.

### Recuperación de contraseña

La URL canónica de recuperación es:

```text
mx.donchambitas.app://auth/recuperar-contrasena
```

Ese valor exacto se pasa como `redirectUrl` a `resetPasswordForEmail` y se da
de alta en la lista **Redirect URLs** de Supabase Auth. El esquema es
`mx.donchambitas.app`, el host es `auth` y la ruta es
`/recuperar-contrasena`; no se admiten variantes ni comodines para producción.

El recorrido contractual es:

1. P-03 llama `recuperarContrasena(correo)` y siempre muestra la misma
   confirmación.
2. Supabase envía el correo solo cuando la cuenta existe.
3. El enlace abre el APK y `supabase-kt` importa la sesión de recuperación.
4. La navegación aterriza en P-18, directamente en la sección de contraseña.
5. P-18 llama `cambiarContrasena(nueva)` con esa sesión activa.

Los pasos 3 y 4, el `intent-filter` y el alta de la URL en la consola son
implementación de `S2-T07`; este documento fija su contrato.

`recuperarContrasena` **siempre reporta éxito**, exista o no el correo. Decir
cuáles correos están registrados es una fuga de información.

`cambiarContrasena(nueva)` **no recibe la contraseña anterior**, y eso es lo que
permite reutilizar P-18 para la recuperación: quien llega por el enlace no puede
dar la que olvidó. Requiere una sesión activa; sin ella devuelve
`Resultado.Error(AUTENTICACION, ...)`.

La sesión y su refresco los lleva `supabase-kt`. No se guarda el token a mano.

### Errores de autenticación

La implementación real traduce los errores antes de salir de `datos/`:

| Situación | `TipoError` | Conducta contractual |
|---|---|---|
| Credenciales inválidas | `AUTENTICACION` | No abre sesión |
| Sesión ausente, vencida o no recuperable | `AUTENTICACION` | No ejecuta la operación protegida |
| Correo ya registrado | `VALIDACION` | No altera la sesión existente |
| Correo, contraseña o metadatos rechazados | `VALIDACION` | Conserva el mensaje seguro en español |
| Sin red o tiempo agotado | `RED` | Permite reintentar |
| Respuesta 5xx de Auth | `SERVIDOR` | Permite reintentar más tarde |
| Error no clasificado o alta sin sesión | `DESCONOCIDO` | No expone detalles internos |

La recuperación es la excepción deliberada: una cuenta inexistente produce
`Exito(Unit)`, igual que una existente. Los mensajes concretos que ve el
usuario viven en recursos Android, no en la implementación real del
repositorio.

## Usuario · `RepositorioUsuario`

| Operación | Con qué |
|---|---|
| `obtenerMiUsuario()` | `usuarios` where `id = auth.uid()` |
| `actualizarMiUsuario(nombre, apellidos, telefono)` | `update` sobre `usuarios` |
| `subirFotoPerfil(bytes)` | Storage, cubeta `perfiles`, ruta `<uid>/<archivo>`, y luego `usuarios.foto_url` |

La ruta de Storage **tiene que empezar con el uid del usuario**: de ahí salen
las políticas. Ver `basedatos/03_almacenamiento.sql`.

**El `update` sobre `usuarios` manda esas columnas y ninguna más.** RLS filtra
filas, nunca columnas, así que quien cierra esto son los permisos por columna de
`02_politicas_rls.sql`: `authenticated` puede actualizar `nombre`, `apellidos`,
`telefono` y `foto_url`. El correo y el rol no se cambian —el correo vive en
`auth.users` y el cambio de rol está fuera del MVP—. Consecuencia para S2-T12:
un DTO que serialice la fila entera falla con `permission denied for column`.
Lo mismo aplica a `marcarLeidos`, que solo puede tocar `leido_en`.

## Perfil del trabajador · `RepositorioTrabajador`

| Operación | Con qué |
|---|---|
| `obtenerPerfilPublico(id)` | RPC `fn_perfil_publico_trabajador(id)` |
| `obtenerMiPerfil()` | `perfiles_trabajador` where `usuario_id = auth.uid()` |
| `guardarMiPerfil(perfil)` | `upsert` sobre `perfiles_trabajador` |
| `reemplazarHabilidades(lista)` | `delete` + `insert` sobre `perfil_habilidades` |

`obtenerPerfilPublico` es RPC y no cinco consultas porque **P-07 necesita perfil,
habilidades, servicios, fotos, calificación y reseñas de un golpe**. Devuelve:

```json
{
  "id": "...", "nombre": "...", "apellidos": "...", "foto_url": "...",
  "titulo": "...", "descripcion": "...", "experiencia_anios": 10,
  "telefono_contacto": "...", "disponible": true,
  "estado": "Puebla", "municipio": "Cholula",
  "habilidades": ["..."],
  "calificacion": { "promedio": 4.7, "total_resenas": 23 },
  "servicios": [ { "id": "...", "titulo": "...", "descripcion": "...",
                   "categoria": "...", "categoria_id": 1,
                   "precio_desde": 300, "precio_hasta": 800,
                   "unidad_precio": "por trabajo", "fotos": ["..."] } ],
  "resenas": [ { "calificacion": 5, "comentario": "...",
                 "cliente_nombre": "...", "creado_en": "..." } ]
}
```

## Servicios · `RepositorioServicios`

| Operación | Con qué |
|---|---|
| `obtenerMisServicios()` | `servicios` where `perfil_id = auth.uid()` |
| `crear(servicio)` / `actualizar(servicio)` / `eliminar(id)` | `servicios` |
| `pausar(id, activo)` | `update servicios set activo` |
| `subirFoto(servicioId, bytes, posicion)` | Storage `servicios/<uid>/…` y luego `servicio_fotos` |
| `eliminarFoto(fotoId)` | `servicio_fotos` y el archivo en Storage |

**Máximo 3 fotos por servicio**, y lo impone la base (`ck_foto_posicion` más
`uq_foto_posicion_unica`), no la aplicación. Se sube el archivo primero y se
inserta la fila después; si la fila falla por el tope, hay que borrar el archivo
recién subido o quedan huérfanos.

## Búsqueda y catálogos · `RepositorioCatalogos`

| Operación | Con qué |
|---|---|
| `buscarTrabajadores(filtros, pagina)` | Vista `vw_busqueda_trabajadores` con filtros de postgrest |
| `obtenerCategorias()` / `obtenerEstados()` / `obtenerMunicipios(estadoId)` | Catálogos. Se cachean localmente |

Filtros, todos opcionales: texto libre contra `titulo` (`ilike`), `estado_id`,
`municipio_id`, `precio_desde` máximo, `promedio` mínimo. Orden por `promedio`,
`creado_en` o `precio_desde`. Paginación de 20 con `range()`.

**El filtro por categoría va contra `categorias`, que es un arreglo**, no contra
un `categoria_id` suelto: un trabajador ofrece varios oficios a la vez. Desde
postgrest se usa el operador de contención: `categorias=cs.{3}`.

La vista devuelve por renglón: `trabajador_id`, `nombre`, `apellidos`,
`foto_url`, `titulo`, `disponible`, `estado_id`, `municipio_id`, `estado`,
`municipio`, `promedio`, `total_resenas`, `servicios_activos`, `precio_desde`,
`categorias` y `creado_en`.

Los catálogos son de lectura pública, incluso sin sesión.

## Solicitudes · `RepositorioSolicitudes`

| Operación | Con qué |
|---|---|
| `obtenerMisSolicitudes()` | `solicitudes` where `cliente_id = auth.uid()` |
| `obtenerAbiertas(categoriaId?)` | `solicitudes` where `estatus = 'abierta'` |
| `obtenerDetalle(id)` | `solicitudes` con sus `postulaciones` |
| `crear(solicitud)` / `actualizar(solicitud)` / `cancelar(id)` | `solicitudes` |
| `cerrar(id)` | RPC `fn_cerrar_solicitud(id)` |

Estados válidos de `estatus`: `abierta`, `asignada`, `cerrada`, `cancelada`.

**Pasar a `asignada` no se hace a mano**: es consecuencia de aceptar una
postulación. **Cerrar es RPC** porque exige trabajador asignado y sella
`cerrada_en`.

Ojo con el primero: eso es **regla de este contrato, no de la base** (DEC-21).
La base deja que el cliente lleve su propia solicitud a `asignada` con un
`update` suelto. Nadie te va a detener; simplemente no se hace, y un `update` a
`estatus` que no sea `cancelada` es un bug en revisión de código.

> Ojo: la columna del flujo se llama `estatus`. `estado_id` es la entidad
> federativa. Son cosas distintas.

## Postulaciones · `RepositorioPostulaciones`

| Operación | Con qué |
|---|---|
| `obtenerMisPostulaciones()` | `postulaciones` where `trabajador_id = auth.uid()` |
| `obtenerDeSolicitud(solicitudId)` | `postulaciones` de esa solicitud |
| `postularse(solicitudId, mensaje, precio)` | `insert` en `postulaciones` |
| `retirar(id)` | `update estatus = 'retirada'` |
| `aceptar(id)` | **RPC `fn_aceptar_postulacion(id)`** |

**`aceptar` es RPC y no se negocia.** Son tres escrituras: aceptar esa, rechazar
las demás y asignar la solicitud. Sueltas desde la aplicación, una caída a medio
camino deja a dos trabajadores creyendo que ganaron.

Un trabajador **no ve** las postulaciones de sus competidores. Lo impide RLS.

## Chat · `RepositorioChat`

| Operación | Con qué |
|---|---|
| `obtenerConversaciones()` | `conversaciones` ordenadas por `ultimo_mensaje_en` |
| `abrirConversacion(trabajadorId, solicitudId?)` | RPC `fn_abrir_conversacion` |
| `obtenerMensajes(conversacionId, pagina)` | `mensajes` paginados |
| `enviar(conversacionId, contenido)` | `insert` en `mensajes` |
| `marcarLeidos(conversacionId)` | `update mensajes set leido_en` |
| `mensajesNuevos(conversacionId): Flow<Mensaje>` | **Supabase Realtime** sobre `mensajes` |

El tiempo real es una suscripción, no un sondeo. El sondeo cada 5 segundos era
el plan de respaldo mientras PEND-01 estaba abierto y ya no aplica.

`abrirConversacion` es RPC porque es un upsert con una condición de unicidad que
incluye un `COALESCE`, y eso no se expresa bien desde postgrest.

**La conversación la abre siempre el cliente** (DEC-20). La RPC fija
`cliente_id = auth.uid()`, así que si la llama un trabajador, falla. El
trabajador responde en un hilo que ya existe; nunca inicia uno. Para S5-T05 y
S5-T06 eso significa que el botón de contactar vive en P-07 y en P-19 del lado
del cliente, y **no** en las pantallas del trabajador.

## Reseñas · `RepositorioResenas`

| Operación | Con qué |
|---|---|
| `dejarResena(solicitudId, calificacion, comentario)` | `insert` en `resenas` |

Una sola por solicitud, solo el cliente que la publicó, solo si está cerrada, y
solo sobre el trabajador asignado. Lo verifica el trigger `tg_resenas_validar`
además de RLS. **No se editan ni se borran.**

La calificación promedio no se pide aparte: viene en
`fn_perfil_publico_trabajador` y en `vw_busqueda_trabajadores`.

## Inteligencia artificial · `RepositorioIa`

| Operación | Con qué |
|---|---|
| `generar(funcion, entrada)` | **Edge Function** `ia-generar` |

Un solo punto de entrada, contra nuestra Edge Function, **nunca contra OpenAI**.
La llave de OpenAI vive en las variables de entorno de esa función y en ningún
otro lugar. Ver `docs/producto/IA.md`.

```
POST functions/v1/ia-generar
{ "funcion": "redactar_perfil" | "redactar_servicio" | "categorizar" | "sugerir",
  "entrada": "texto del usuario" }

200 → { "resultado": "...", "desde_cache": false, "llamadas_restantes_hoy": 7 }
429 → { "error": { "codigo": "LIMITE_DIARIO_IA",
                   "mensaje": "Alcanzaste el límite de hoy" } }
503 → { "error": { "codigo": "IA_NO_DISPONIBLE", ... } }
```

La función hace, en este orden: valida la sesión, consulta `ia_cache`, llama a
`fn_ia_registrar_llamada` para el tope, y solo entonces llama a OpenAI. Si
cualquiera de los tres primeros resuelve, no se paga nada.

**`429` y `503` son situaciones normales, no caídas.** La aplicación muestra un
aviso y deja al usuario escribir a mano. Una pantalla que se rompe con un 429
está mal hecha.

---

## Errores

Nada de excepciones cruzando capas. El repositorio atrapa lo que venga de
Supabase y devuelve `Resultado.Error(tipo, mensaje)`.

| Qué llega de Supabase | `TipoError` | Qué ve el usuario |
|---|---|---|
| Sin red, tiempo agotado | `RED` | "Revisa tu conexión e intenta de nuevo" |
| 401, sesión vencida, credenciales malas | `AUTENTICACION` | "Correo o contraseña incorrectos" |
| 403, RLS rechaza la fila | `AUTENTICACION` | "No tienes permiso para hacer eso" |
| Violación de CHECK o de trigger | `VALIDACION` | El mensaje del trigger, ya está escrito en español |
| 409, correo o llave duplicada | `VALIDACION` | "El correo ya está registrado, inicia sesión" |
| 429 de la Edge Function | `LIMITE_IA` | "Alcanzaste el límite de hoy" |
| 5xx, 503 de la Edge Function | `SERVIDOR` | "Algo falló de nuestro lado, intenta más tarde" |
| Cualquier otra cosa | `DESCONOCIDO` | "Algo salió mal, intenta de nuevo" |

**Un `403` de RLS casi nunca es un problema de permisos del usuario: es un bug
nuestro.** Si en pruebas sale un 403 donde debería funcionar, la política está
mal, no el usuario. No lo tapes con un mensaje bonito.

Los mensajes **dicen qué hacer**, no solo qué falló. Es la regla de `DISENO.md`.
Y todos viven en `strings.xml`.

---

## Lo que esto no cubre

Las traducciones concretas de arriba son **el contrato**, no la
implementación. Cada tarea de contrato detalla su módulo con los nombres de
columna exactos y los DTO: S2-T06 autenticación, S2-T12 perfil de usuario,
S3-T08 trabajador y servicios, S3-T12 la Edge Function de IA, S4-T08 solicitudes
y búsqueda, S5-T06 mensajería y postulaciones.

Mientras esas tareas no lleguen, la implementación activa de todos estos
repositorios es la falsa, en memoria. Ver `AGENTS.md` §6.
