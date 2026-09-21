package mx.donchambitas.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regla de JUnit 4 que reemplaza [Dispatchers.Main] con un [TestDispatcher] durante la ejecución
 * de pruebas unitarias.
 *
 * Permite probar ViewModels y clases que interactúan con el despachador principal de Android
 * sin inicializar el Looper real.
 *
 * Uso típico en una clase de prueba:
 * ```kotlin
 * @get:Rule
 * val reglaCorrutinas = ReglaCorrutinas()
 *
 * @Test
 * fun debeEmitirEstado_cuandoOcurreEvento() = runTest(reglaCorrutinas.testDispatcher) {
 *     // ...
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReglaCorrutinas(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    val testScope: TestScope = TestScope(testDispatcher)

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
