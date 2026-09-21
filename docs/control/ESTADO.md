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
| Terminadas | 15 |
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

**`S1-T15` — Pantalla de bienvenida (splash).** 2026-09-20.
Rama `feat/S1-T15-pantalla-splash`, pull request **sin abrir todavía**.

Implementación completa de P-01 (Splash) según `PANTALLAS.md`, `WIREFRAMES.md` y `DISENO.md`:
- `ui/pantallas/EstadoSplash.kt`: Data class de estado inmutable con propiedades `cargando: Boolean` y `destino: Ruta?`.
- `ui/pantallas/SplashViewModel.kt`: ViewModel con `@HiltViewModel` que consulta el estado de sesión temporal (según S1-T12 hasta S2-T09) e impone un retraso mínimo de 800 ms para evitar parpadeos, resolviendo los tres destinos posibles:
  - Sin sesión -> `P-02` (Iniciar sesión)
  - Cliente -> `P-05` (Inicio cliente)
  - Trabajador -> `P-10` (Inicio trabajador)
- `ui/pantallas/SplashPantalla.kt`: Composable con fondo `Crema` (`#FFFDF8`), isotipo del casco oficial de seguridad Don Chambitas, nombre de aplicación en `Carbon` (negrita), eslogan "Tu oficio, tu chamba" en `Cafe`, e indicador circular `Cargando` en color `Mostaza` (48 dp) con etiqueta "Verificando sesión…".
- `ui/navegacion/GrafoNavegacion.kt`: Sustitución del marcador provisional por `SplashPantalla`, saliendo de la pila de navegación con `popUpTo(Ruta.Splash.ruta) { inclusive = true }` de modo que presionar el botón Atrás desde el destino cierra la aplicación.
- `themes.xml` y `colors.xml`: Configuración de `android:windowBackground` con `color_crema` (`#FFFDF8`) eliminando cualquier parpadeo de fondo blanco antes de renderizar Compose.
- Pruebas unitarias: 7 pruebas unitarias nuevas en `SplashViewModelTest.kt` cubriendo estados iniciales, resolución de destinos y temporizador mínimo (51 pruebas totales en el proyecto pasando limpiamente).
- Compilación (`./gradlew assembleDebug`), instalación y verificación interactiva en emulador `emulator-5554` comprobando arranque en Splash, transición a destino y cierre limpio con botón Atrás.

**`S1-T14` — Wireframes de las pantallas de autenticación e inicio.** 2026-09-20.
Rama `docs/S1-T14-wireframes-pantallas`, pull request **sin abrir todavía**.

Diseño y documentación completa de las pantallas para el arranque del Sprint 2 conforme a `PANTALLAS.md` y `DISENO.md`:
- `docs/producto/wireframes/`:
  - 11 wireframes vectorizados a escala móvil estándar **360 × 800 dp** con la paleta oficial **Taller** (Mostaza, MostazaOscuro, Terracota, Carbon, Cafe, Crema, Arena, Borde y semánticos Exito, Advertencia y Error):
    - `P-01-splash.png`: Splash con casco de seguridad, slogan y widget de carga.
    - `P-02-iniciar-sesion.png`: Login con campos de captura, visibilidad y enlaces de navegación.
    - `P-03-registro.png`: Alta de cuenta con selector de rol único (`ChipCategoria`), 5 campos y enlaces.
    - `P-04-recuperar.png`: Formulario de recuperación de contraseña con aviso de vigencia (24h).
    - `P-05-inicio-cliente.png`: Inicio cliente con buscador, chips de oficios, tarjetas de trabajadores con estrellas, botón flotante "+ Publicar solicitud" y barra inferior de 4 destinos.
    - `P-05-vacio.png`: Estado vacío de búsqueda con icono, explicación y acción de limpiar filtros.
    - `P-05-error.png`: Estado error con icono de advertencia, mensaje explicativo y botón de reintentar.
    - `P-10-inicio-trabajador.png`: Inicio trabajador con filtro de oficios, tarjetas de solicitudes abiertas, etiqueta de estado y barra inferior.
    - `P-10-vacio.png`: Estado vacío de solicitudes por categoría con sugerencias y botón de ver todas.
    - `P-10-error.png`: Estado error con mensaje de reintento.
    - `P-18-cuenta.png`: Mi cuenta con cabecera de perfil, badge de rol, acciones de configuración y cierre de sesión.
  - Cada zona cuenta con anotaciones exactas de componentes (`BarraSuperior`, `BarraInferior`, `BotonPrincipal`, `BotonSecundario`, `BotonDestacado`, `BotonTexto`, `CampoTexto`, `CampoContrasena`, `TarjetaTrabajador`, `TarjetaSolicitud`, `ChipCategoria`, `Estrellas`, `EtiquetaEstado`, `Cargando`, `EstadoVacio`, `EstadoError`). Cero componentes ajenos a `DISENO.md`.
- `docs/producto/WIREFRAMES.md`:
  - Índice maestro con tabla resumen, imágenes incrustadas, desglose de componentes por zona y documentación exhaustiva del comportamiento de cada elemento tocable (eventos al pulsar, navegación, validaciones y cambios de estado).
- Verificación del proyecto:
  - Pruebas unitarias pasando limpiamente (44 pruebas, `./gradlew testDebugUnitTest`).
  - Compilación exitosa (`./gradlew assembleDebug`).
  - Instalación y ejecución interactiva limpia en emulador `emulator-5554` (`Displayed MainActivity`).

**`S1-T13` — Interfaces de repositorio y fuente de datos falsa (fake) para desbloquear la UI.** 2026-09-20.
Rama `feat/S1-T13-repositorios-falsos`, pull request **sin abrir todavía**.

Implementación completa de la capa de datos en memoria y contratos de repositorio según `ARQUITECTURA.md` y `CONTRATOS-API.md`:
- `dominio/repositorio/`:
  - 10 interfaces de dominio puras (`RepositorioAuth`, `RepositorioUsuario`, `RepositorioTrabajador`, `RepositorioServicios`, `RepositorioSolicitudes`, `RepositorioPostulaciones`, `RepositorioChat`, `RepositorioResenas`, `RepositorioCatalogos`, `RepositorioIa`) con métodos `suspend` devolviendo `Resultado<T>` y `Flow` reactivo para sesiones y mensajes. Cero dependencias de Android o Supabase.
- `dominio/modelo/ModelosRepositorio.kt`:
  - Modelos de soporte de dominio (`Sesion`, `PerfilPublicoTrabajador`, `CalificacionTrabajador`, `ServicioPublico`, `ResenaPublica`, `FiltrosBusquedaTrabajadores`, `ResumenTrabajadorBusqueda`, `DetalleSolicitud`, `ResultadoIa`).
- `datos/falso/`:
  - `FuenteDatosFalsa.kt`: Singleton en memoria con semillero coherente de `04_datos_semilla.sql` (16 categorías, 32 estados, 26 municipios, 8 trabajadores completos con servicios y fotos, 2 clientes, 5 solicitudes en estados abierta/asignada/cerrada/cancelada, 3 chats con mensajes y reseñas válidas respetando las restricciones relacionales del esquema).
  - 10 implementaciones falsas correspondientes (`Repositorio*Falso`) aplicando simulación de latencia de red (300 ms) y propiedad `errorForzado: TipoError?` para pruebas de `EstadoError`.
- `di/ModuloRepositorios.kt`:
  - Módulo de Hilt vinculando las 10 interfaces de dominio a sus implementaciones falsas con `@Binds` en `SingletonComponent`. Único archivo a modificar cuando entren las implementaciones reales.
- Pruebas unitarias:
  - 10 pruebas unitarias nuevas en `RepositoriosFalsosTest.kt` cubriendo las 10 implementaciones, validaciones de negocio, operaciones atómicas (aceptar postulación) y control de errores (44 pruebas totales en el proyecto pasando limpiamente).
  - Compilación (`./gradlew assembleDebug`), instalación y ejecución limpia en emulador Pixel 8 Pro.

**`S1-T12` — Navegación con Navigation Compose y definición del grafo de rutas.** 2026-09-20.
Rama `feat/S1-T12-navegacion-compose`, pull request **sin abrir todavía**.

Implementación completa de la arquitectura de navegación en Jetpack Compose según `ARQUITECTURA.md`, `PANTALLAS.md` y `DISENO.md`:
- `Rutas.kt`:
  - Las 19 rutas del sistema modeladas con la clase sellada `Ruta` (`P-01` a `P-19`), con identificadores únicos, títulos y argumentos fuertemente tipados (`NavType.StringType`, nulabilidad y valores por defecto).
  - Subgrafos definidos en `Subgrafo`: `Autenticacion`, `Cliente`, `Trabajador`.
  - Cero cadenas de ruta sueltas fuera de `Rutas.kt` (verificado con `grep` y pruebas unitarias).
  - Las 8 pantallas que se abren encima y no muestran barra inferior registradas en `PANTALLAS_ENCIMA`.
  - Sistema de guardas de navegación reactivo (`resolverGuarda` y `MarcadorSesionTemporal`) con control temporal de rol (Sin sesión, Cliente, Trabajador):
    - Sin sesión: cualquier ruta privada redirige a `P-02` (Iniciar sesión).
    - Con sesión Cliente: las rutas de trabajador redirigen a `P-05` (Inicio cliente).
    - Con sesión Trabajador: las rutas de cliente redirigen a `P-10` (Inicio trabajador).
- `BarraInferiorCliente.kt`: Barra inferior con exactamente 4 destinos para Cliente (`Inicio` P-05, `Solicitudes` P-09, `Chats` P-15, `Cuenta` P-18) con indicador Mostaza e iconos Carbon/Cafe.
- `BarraInferiorTrabajador.kt`: Barra inferior con exactamente 4 destinos para Trabajador (`Inicio` P-10, `Servicios` P-12, `Chats` P-15, `Cuenta` P-18).
- `GrafoNavegacion.kt`:
  - Grafo completo con tres subgrafos y pantallas compartidas.
  - Botón flotante (+) en `P-05` (Inicio cliente) que navega a `P-08` (Publicar solicitud).
  - Marcador interactivo para las 19 pantallas con información de ruta, argumentos recibidos, conmutador de sesión en tiempo real, pruebas de guardas y mapa completo de navegación.
  - Botón de regreso del sistema integrado con `Scaffold` y `BarraSuperior`.
- `MainActivity.kt`: Envoltorio limpio llamando a `GrafoNavegacion()` dentro de `DonChambitasTema`.
- 9 pruebas unitarias nuevas en `NavegacionTest.kt` (34 pruebas totales en el proyecto pasando limpiamente), compilación (`./gradlew assembleDebug`), instalación y verificación interactiva en emulador (`emulator-5554`).

**`S1-T11` — Componentes de estado: carga, vacío, error y mensajes al usuario.** 2026-09-20.
Rama `feat/S1-T11-componentes-estado`, pull request **sin abrir todavía**.

Construcción completa de los componentes de estado y el patrón de pantalla con datos según `DISENO.md` y `ARQUITECTURA.md`:
- `Estados.kt`:
  - `Cargando`: Indicador circular centrado en color Mostaza (48 dp) y mensaje opcional, con descripción semántica de accesibilidad.
  - `EstadoVacio`: Icono grande (56 dp en color Cafe), título (subtítulo en Carbon), mensaje (cuerpo en Cafe) y botón opcional (`BotonPrincipal`). Soporta valores por defecto desde `strings.xml`.
  - `EstadoError`: Icono de advertencia en color Error, título (subtítulo en Carbon), mensaje de error que dice qué hacer derivado de cada `TipoError` (o mensaje personalizado) y botón de reintentar opcional (`BotonPrincipal` con texto "Reintentar").
  - Mapeo de `TipoError` a recursos de cadenas (`obtenerMensajeErrorRes` y `obtenerTituloErrorRes`), garantizando mensajes distintos orientados a la acción para `RED`, `AUTENTICACION`, `VALIDACION`, `LIMITE_IA`, `SERVIDOR` y `DESCONOCIDO`.
- `ContenedorEstado.kt`:
  - `ContenedorEstado`: Patrón de pantalla con datos que recibe `cargando`, `error` (`TipoError?`), `vacio`, `alReintentar` y `contenido`. Utiliza `resolverEstadoVisual` para garantizar orden de precedencia estricto (cargando > error > vacío > contenido) asegurando que ningún estado se pinte encima de otro. Permite personalización total mediante slots de vista.
- `strings.xml`: Cadenas agregadas para reintentar, títulos de error y mensajes explicativos por `TipoError` centrados en la acción.
- 8 pruebas unitarias nuevas en `EstadosTest.kt` (25 pruebas totales en el proyecto pasando limpiamente), compilación (`./gradlew assembleDebug`), instalación y verificación en emulador.

**`S1-T10` — Componentes reutilizables base (botones, campos de texto, tarjetas, chips).** 2026-09-20.
Rama `feat/S1-T10-componentes-base`, pull request **sin abrir todavía**.

Construcción completa de los 14 componentes reutilizables base de la tabla de `DISENO.md` dentro de `mx.donchambitas.app.ui.componentes`:
- `Botones.kt`: `BotonPrincipal` (Mostaza con texto Carbon 7.09:1 WCAG AAA), `BotonSecundario` (contorno Mostaza Oscuro), `BotonDestacado` (Terracota con texto blanco 5.12:1 WCAG AA) y `BotonTexto` (sin fondo, texto Mostaza Oscuro). Soportan estados `habilitado` y `cargando` con indicador de progreso y bloqueo táctil.
- `Campos.kt`: `CampoTexto` (fondo Arena, contorno Borde/Mostaza Oscuro y texto de error en color Error) y `CampoContrasena` (con alternador de visibilidad e iconos de ojo).
- `Estrellas.kt`: `Estrellas` en color Terracota permitiendo media estrella en modo lectura con iconos vectoriales y descripción de accesibilidad.
- `Chips.kt`: `ChipCategoria` (Mostaza activo con texto Carbon, Arena inactivo con borde) y `EtiquetaEstado` con los cuatro colores de estado (`abierta` en Exito, `asignada` en Advertencia con texto Carbon, `cerrada` en Cafe, `cancelada` en Error con texto blanco).
- `Barras.kt`: `BarraSuperior` (fondo Mostaza, texto Carbon, flecha de regreso opcional) y `BarraInferior` (4 destinos: Inicio, Buscar, Solicitudes, Perfil con indicador en Mostaza e iconos Carbon/Cafe).
- `Tarjetas.kt`: `TarjetaTrabajador` (foto, nombre, oficio, estrellas, municipio), `TarjetaServicio` (foto, titulo, categoria, precio) y `TarjetaSolicitud` (titulo, categoria, presupuesto, estado, fecha).
- Cada componente acepta `modifier: Modifier = Modifier` como último parámetro con valor por defecto, no importa capas de repositorio ni datos, no contiene colores literales y cuenta con su `@Preview` funcional.
- 17 pruebas unitarias pasando (`./gradlew testDebugUnitTest`), compilación (`./gradlew assembleDebug`) e instalación/ejecución limpia en emulador Pixel 8 Pro.

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

`S1-T16` — Estrategia de pruebas y configuración de las pruebas base (JUnit / Compose test)
(prioridad 250, depende de S1-T04)


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
