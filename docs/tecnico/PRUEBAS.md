# Estrategia de pruebas

Este documento define la estrategia, convenciones, herramientas y comandos para
la ejecución y reporte de pruebas en **Don Chambitas**. Ninguna tarea posterior
tiene excusa para no probar su código.

---

## 1. Qué se prueba y qué no

### Lo que sí se prueba (obligatorio)

- **ViewModels:** Se prueban todos los ViewModels. Cada prueba valida las
  transiciones de estado de su `StateFlow`, las llamadas a métodos del
  repositorio, la gestión de errores y el cumplimiento de retardos o reglas de
  flujo (por ejemplo, el retardo mínimo de splash o resolución de destinos).
- **Lógica de dominio:** Modelos, funciones utilitarias (`Resultado`, mapeos,
  validadores) y reglas de negocio puras.
- **Repositorios falsos (`datos/falso/`):** Comportamiento en memoria de cada
  repositorio, validaciones de argumentos, simulación de fallos con
  `errorForzado` y reactividad de flujos (`Flow`).
- **Componentes interactivos de UI (Compose UI Test):** Se prueban componentes
  reutilizables cuando tienen interacción, estados visuales alternos
  (deshabilitado, cargando, errores) o cuando la lógica de renderizado lo
  amerita.

### Lo que no se prueba

- **Composables puramente visuales o pasivos:** Pantallas o componentes sin
  lógica interna cuyo único rol es ubicar elementos visuales estáticos.
- **Código generado:** Clases generadas por Hilt, KSP, Kotlinx Serialization o
  BuildConfig.
- **Framework o bibliotecas externas:** No se prueba que Compose dibuje píxeles ni
  que `StateFlow` emita valores internamente; se prueba la lógica de la
  aplicación.

---

## 2. Convenciones de nombres de pruebas

Seguimos estrictamente la convención establecida en `CONVENCIONES.md`:

```kotlin
debe<ResultadoEsperado>_cuando<Condicion>()
```

### Ejemplos válidos

```kotlin
@Test
fun debeResolverDestinoIniciarSesion_cuandoNoHaySesion()

@Test
fun debeMostrarTextoYResponderAlClic_cuandoSeUsaBotonPrincipal()

@Test
fun debeEmitirErrorAutenticacion_cuandoCredencialesSonInvalidas()
```

---

## 3. Infraestructura y utilidades de prueba

### `ReglaCorrutinas` (`app/src/test/.../util/ReglaCorrutinas.kt`)

Regla de JUnit 4 basada en `TestWatcher` que intercambia `Dispatchers.Main` por un
`StandardTestDispatcher` antes de cada prueba y restaura el hilo principal al
finalizar con `Dispatchers.resetMain()`.

Permite probar ViewModels y corrutinas en el hilo principal sin requerir el
Looper de Android ni ejecutar un emulador:

```kotlin
class MiViewModelTest {

    @get:Rule
    val reglaCorrutinas = ReglaCorrutinas()

    @Test
    fun debeEmitirEstado_cuandoOcurreAccion() = runTest(reglaCorrutinas.testDispatcher) {
        val viewModel = MiViewModel()
        viewModel.ejecutarAccion()
        advanceUntilIdle()
        assertEquals(..., viewModel.estado.value)
    }
}
```

### `DatosPrueba` (`app/src/test/.../util/DatosPrueba.kt`)

Fábrica centralizada de objetos de dominio con valores por defecto coherentes y
parámetros con nombre. Permite que cada prueba solo especifique los campos que le
interesan, evitando constructores verbosos y redundantes:

```kotlin
// Instancia con valores por defecto
val usuario = DatosPrueba.crearUsuario()

// Personalizando solo lo necesario para el caso de prueba
val trabajador = DatosPrueba.crearUsuario(
    id = "usr-custom",
    rol = RolUsuario.TRABAJADOR,
    nombre = "Carlos"
)
```

Entidades principales soportadas:
`Usuario`, `PerfilTrabajador`, `PerfilHabilidad`, `Servicio`, `ServicioFoto`,
`Solicitud`, `Postulacion`, `Conversacion`, `Mensaje`, `Resena`, `Categoria`,
`Estado`, `Municipio`, `Sesion`, `PerfilPublicoTrabajador`,
`ResumenTrabajadorBusqueda` y `DetalleSolicitud`.

---

## 4. Plantillas de ejemplo

El proyecto cuenta con tres pruebas de referencia que sirven como plantilla
oficial para el equipo:

### 1. Unitaria de ViewModel con corrutinas
- **Archivo:** `app/src/test/java/mx/donchambitas/app/ui/pantallas/SplashViewModelTest.kt`
- **Uso:** Emplea `@get:Rule val reglaCorrutinas = ReglaCorrutinas()`, controla el
  avance virtual del tiempo con `advanceTimeBy(...)` y `advanceUntilIdle()`, y
  valida el estado inmutable expuesto vía `StateFlow`.

### 2. Unitaria de Repositorio falso en memoria
- **Archivo:** `app/src/test/java/mx/donchambitas/app/datos/falso/RepositoriosFalsosTest.kt`
- **Uso:** Instancia `FuenteDatosFalsa`, configura `retrasoMs = 0L` para pruebas
  rápidas en memoria, ejecuta operaciones de negocio y valida retornos de
  `Resultado.Exito` o `Resultado.Error`.

### 3. De interfaz sobre componente en Compose (instrumentada)
- **Archivo:** `app/src/androidTest/java/mx/donchambitas/app/ui/componentes/ComponentesTest.kt`
- **Uso:** Emplea `createComposeRule()`, monta el componente dentro de
  `DonChambitasTema`, interactúa mediante `performClick()` o `performTextInput()`,
  y verifica semántica mediante `assertIsDisplayed()`, `assertIsEnabled()` o
  `assertIsNotEnabled()`.

---

## 5. Comandos de ejecución

### Pruebas unitarias locales (JVM)

Se ejecutan directamente en la máquina de desarrollo sin necesidad de emulador:

```bash
./gradlew testDebugUnitTest
```

El reporte HTML se genera en:
`app/build/reports/tests/testDebugUnitTest/index.html`

### Pruebas instrumentadas (Emulador / Dispositivo físico)

Requieren un dispositivo o emulador Android conectado (`adb devices`):

```bash
./gradlew connectedDebugAndroidTest
```

El reporte HTML se genera en:
`app/build/reports/androidTests/connected/debug/index.html`

---

## 6. Cobertura de código (JaCoCo)

La cobertura está habilitada en el build type `debug` dentro de `app/build.gradle.kts`:

```kotlin
buildTypes {
    debug {
        enableUnitTestCoverage = true
        enableAndroidTestCoverage = true
    }
}
```

### Generación del reporte de cobertura unitaria

Para calcular la cobertura de las pruebas unitarias locales y generar el informe
HTML de JaCoCo:

```bash
./gradlew createDebugUnitTestCoverageReport
```

El informe interactivo de cobertura se genera en:
`app/build/reports/coverage/test/debug/index.html`

### Generación del reporte de cobertura instrumentada

Para generar el informe tras ejecutar pruebas en dispositivo/emulador:

```bash
./gradlew createDebugCoverageReport
```

El informe se genera en:
`app/build/reports/coverage/androidTest/debug/connected/index.html`

---

## 7. Alcance y limitaciones

- **Sin integración continua obligatoria en local:** Las pruebas se ejecutan
  localmente antes de finalizar cada tarea.
- **Sin umbral de cobertura forzado:** No se exige un porcentaje numérico mínimo
  rígido que fomente pruebas superficiales; se prioriza la cobertura real de
  toda la lógica de negocio, ViewModels y flujos críticos.
