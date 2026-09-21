# Estado actual

> Archivo **vivo**. Quien termina una tarea lo actualiza. Es la primera cosa
> que lee el agente y la única fuente confiable sobre qué está pasando hoy.

**Última actualización:** 2026-09-20

---

## Sprint en curso

| Campo | Valor |
|---|---|
| Sprint | 1 |
| Fechas | PENDIENTE |
| Tareas del sprint | 16 |
| Terminadas | 9 |
| En curso | 0 |
| Bloqueadas | 0 |

## Tarea en curso

_Ninguna._

| Campo | Valor |
|---|---|
| ID | — |
| Título | — |
| Quién la tomó | — |
| Rama | — |
| Desde | — |

## Última tarea terminada

**`S1-T09` — Sistema de diseño en Jetpack Compose (Theme, Color, Typography, Shape).** 2026-09-20.
Rama `feat/S1-T09-sistema-diseno`, pull request **sin abrir todavía**.

Implementación completa de la paleta Taller como tema de Jetpack Compose (`DonChambitasTema`) en `mx.donchambitas.app.ui.tema`.
- `Color.kt`: los 8 colores de la paleta (`Mostaza`, `MostazaOscuro`, `Terracota`, `Carbon`, `Cafe`, `Crema`, `Arena`, `Borde`) y 3 semánticos (`Exito`, `Advertencia`, `Error`), más `Blanco`. Ni una sola declaración de `Color(0xFF...)` fuera de este archivo.
- `Tema.kt`: `ColorScheme` de Material 3 con `onPrimary` mapeado obligatoriamente a `Carbon` (relación 7.09:1 WCAG AAA), `secondary` a `Terracota` con `onSecondary` en blanco, `background` en `Crema`, `surface` en `Arena` con `onSurface` en `Carbon`, `outline` en `Borde` y `error` en `Error`.
- `Tipografia.kt`: los 6 estilos tipográficos de `DISENO.md` (`titulo`, `subtitulo`, `cuerpoFuerte`, `cuerpo`, `secundario`, `pie`) con la fuente del sistema, integrados en `Typography` de Material 3 y accesibles vía propiedades de extensión.
- `Espaciado.kt`: escala base de 4 (`dp4` a `dp48`) y valores semánticos (`margenPantalla`, `separacionTarjetas`, `rellenoTarjeta`).
- `Formas.kt`: `Shapes` de Material 3 y formas de componentes (botones y campos 12 dp, tarjetas 16 dp, chips círculo, hoja inferior 20 dp).
- `MainActivity.kt`: envuelta en `DonChambitasTema`, arrancando con fondo Crema verificado en emulador.
- Pruebas unitarias en `TemaTest.kt` comprobando los contrastes obligatorios, la escala de tipografía y espaciado. Compilación (`./gradlew assembleDebug`) y pruebas (`./gradlew testDebugUnitTest`) exitosas.

**`S1-T08` — Identidad visual: paleta de colores, tipografía e iconografia.** 2026-09-20.
Rama `feat/S1-T08-identidad-visual`, pull request **sin abrir todavía**.

Cierre y documentación completa de la identidad visual de la aplicación. Tabla exhaustiva de relaciones de contraste WCAG 2.1 (Carbon sobre Mostaza 7.09:1 pasa AAA, Blanco sobre Mostaza 2.26:1 falla y queda prohibido). Diseño del logotipo vectorial legible a 48 dp en `docs/tecnico/recursos/logo.svg`. Lámina visual de la paleta Taller y reglas de aplicación en `docs/tecnico/recursos/muestra-paleta.png`. Asignación formal de los 16 iconos de oficios con Material Icons Outlined en `docs/tecnico/DISENO.md` y reemplazo total de los nombres provisionales Tabler en `basedatos/04_datos_semilla.sql`. Icono adaptativo vectorial (background y foreground con casco de seguridad en Mostaza) e iconos rasterizados en todas las densidades de mipmap (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) para versión estándar y redonda. Probado e inspeccionado exitosamente en emulador Pixel 8 Pro (lanzador, cajón de apps y ajustes del sistema). Compilación (`./gradlew assembleDebug`) y pruebas unitarias (`./gradlew testDebugUnitTest`) exitosas.

**`S1-T07` — Diccionario de datos y modelado de entidades en Kotlin (data classes).** 2026-09-20.
Rama `feat/S1-T07-modelado-entidades`, pull request **sin abrir todavía**.

Modelado completo de las entidades del esquema relacional en Kotlin dentro de `mx.donchambitas.app.dominio.modelo` (`Usuario`, `PerfilTrabajador`, `PerfilHabilidad`, `Servicio`, `ServicioFoto`, `Solicitud`, `Postulacion`, `Conversacion`, `Mensaje`, `Resena`, `Estado`, `Municipio`, `Categoria`) y los 4 tipos enumerados exactos (`RolUsuario`, `EstadoSolicitud`, `EstadoPostulacion`, `FuncionIa`). Mapeo estricto de tipos (`UUID` a `String`, `TIMESTAMPTZ` a `Instant`, `NUMERIC(10,2)` a `BigDecimal`, `SMALLINT`/`SERIAL` a `Int`) y nulabilidad correspondiente. Sin anotaciones de serialización ni dependencias de `supabase-kt` o `android.*`. Documentado en `docs/tecnico/DICCIONARIO-DATOS.md`. Pruebas unitarias en `ModelosTest.kt`. Compilación (`./gradlew assembleDebug`), pruebas unitarias (`./gradlew testDebugUnitTest`) e instalación y ejecución en emulador Pixel 8 Pro exitosas.


- **El diagrama**, en `docs/tecnico/diagrama-er.png`, con su generador al lado.
- **El cruce de las 33 historias contra las tablas**, en `MODELO-ER.md`.
  Ninguna historia pide una tabla que no exista.
- **Las pruebas 10, 12 y 13 de `91` ahora corren con `set role authenticated`.**
  Antes corrían como `postgres`, que se salta RLS, y por eso no demostraban
  nada: con RLS apagado, a una función de trigger le puede faltar el
  `security definer` y comportarse igual de bien. Las tres exigen además de qué
  capa viene el rechazo, y las tres trajeron el mensaje de su trigger.
- **`92_usuarios_prueba.sql` y `92_prueba_rls_anon.py`**, que son el paso 2c.
  Cuatro de sus diez comprobaciones son controles a propósito: si las tablas
  estuvieran rotas del todo, todo volvería vacío y las otras seis pasarían sin
  que nada funcionara.

**Dos cosas que conviene saber antes de tocar esto:**

- `ia_cache` devuelve `HTTP 403`, no una lista vacía como pedía el ticket. Es
  el `revoke all` quitando el permiso antes de que RLS entre a filtrar. Más
  estricto, no menos.
- **El dominio `@prueba.donchambitas.mx` no sirve para el alta por la API.**
  Supabase valida que el dominio exista y ese es ficticio. `91` nunca se topó
  con ello porque inserta directo en `auth.users`. Le va a tocar a `S2-T07`.

**Las cuentas de prueba siguen en el proyecto.** Borrarlas necesita la
`service_role`, que no entra al repositorio:

```sql
delete from auth.users where email like '%@prueba.donchambitas.mx';
```

## Cuatro huecos que esperan al líder

Salen del cruce contra las historias y de revisar el paso 2 del ticket.
**Ninguno se corrigió**: el esquema no se rediseña en `S1-T03`. Están
explicados al final de `MODELO-ER.md`.

- **`H-05` — La búsqueda por texto no ignora los acentos.** HU-17 promete que
  "plomeria" encuentre "plomería". `pg_trgm` acelera el `ilike`, no cambia lo
  que el `ilike` considera igual, y `unaccent` no está instalado; el contrato
  de `buscarTrabajadores` busca justamente con `ilike`. Es el más serio y
  **conviene cerrarlo antes de `S4-T04`**.
- **`H-06` — Nada borra el archivo de Storage cuando desaparece su fila.** La
  cascada se lleva la URL antes de que nadie pueda usarla. Le toca a `S3-T08`
  y `S2-T12` escribir el orden de operaciones.
- **`H-07` — "Postulaciones sin revisar" no existe como dato.** `enviada` es
  "no decidida", no "no vista". Probablemente sea la redacción de HU-13.
- **`H-08` — Uno de los cinco triggers del paso 2 no se puede probar, y eso
  toca un criterio de aceptación de este ticket.** "Postularse a tu propia
  solicitud" exigiría un uuid que fuera cliente y trabajador a la vez, y los
  roles excluyentes lo impiden antes de que el trigger opine. Los otros cuatro
  están demostrados. **Este es el único punto por el que `S1-T03` podría
  devolverse:** o el criterio baja a cuatro triggers, o se anota que al quinto
  lo sostiene el esquema.

## Siguiente en la cola

`S1-T10` — Componentes reutilizables base (botones, campos de texto, tarjetas, chips)
(prioridad 600, depende de S1-T09)


## Decisiones recientes

**2026-09-16 · La base se volvió a levantar y a verificar.** `01` a `04` sin
error, `90_verificacion.sql` **42 de 42** y `91_prueba_funcional.sql`
**25 de 25**, ya con las pruebas 10, 12 y 13 corriendo con RLS activo. Las tres
pasaron por la razón correcta.

**2026-09-16 · `S1-T03` terminada.** Diagrama, cruce de las 33 historias, las
tres pruebas de `91` corriendo por fin con RLS activo, y el paso 2c cerrado con
la `anon key` contra PostgREST. Salieron cuatro huecos —`H-05` a `H-08`— y
ninguno se corrigió: son del líder. `H-08` toca un criterio de aceptación de
este mismo ticket.

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
