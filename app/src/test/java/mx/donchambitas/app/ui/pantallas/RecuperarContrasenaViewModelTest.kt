package mx.donchambitas.app.ui.pantallas

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import mx.donchambitas.app.util.ReglaCorrutinas
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecuperarContrasenaViewModelTest {

    @get:Rule
    val reglaCorrutinas = ReglaCorrutinas()

    @Test
    fun debeNormalizarCorreoYConfirmarEnvio_cuandoRepositorioRespondeExito() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                barreraRecuperacion = CompletableDeferred()
            }
            val viewModel = RecuperarContrasenaViewModel(repositorio)
            viewModel.alCambiarCorreo("  Persona@Ejemplo.MX  ")

            viewModel.alEnviar()
            runCurrent()

            assertTrue(viewModel.estado.value.cargando)
            assertEquals("persona@ejemplo.mx", repositorio.ultimoCorreoRecuperacion)
            viewModel.alEnviar()
            assertEquals(1, repositorio.llamadasRecuperacion)

            repositorio.barreraRecuperacion?.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.estado.value.cargando)
            assertTrue(viewModel.estado.value.enviado)
        }

    @Test
    fun debeConservarCorreoYReintentar_cuandoFallaLaRed() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoRecuperacion = Resultado.Error(TipoError.RED, "sin red")
            }
            val viewModel = RecuperarContrasenaViewModel(repositorio)
            viewModel.alCambiarCorreo("persona@ejemplo.mx")
            viewModel.alEnviar()
            advanceUntilIdle()

            assertEquals("persona@ejemplo.mx", viewModel.estado.value.correo)
            assertEquals(TipoError.RED, viewModel.estado.value.errorPantalla)

            repositorio.resultadoRecuperacion = Resultado.Exito(Unit)
            viewModel.alReintentar()
            advanceUntilIdle()
            assertEquals(2, repositorio.llamadasRecuperacion)
            assertTrue(viewModel.estado.value.enviado)
        }

    @Test
    fun debeConvertirLimiteIaEnDesconocido_cuandoLlegaTipoFueraDelModulo() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoRecuperacion = Resultado.Error(TipoError.LIMITE_IA, "no aplica")
            }
            val viewModel = RecuperarContrasenaViewModel(repositorio)

            viewModel.alEnviar()
            advanceUntilIdle()

            assertEquals(TipoError.DESCONOCIDO, viewModel.estado.value.errorPantalla)
        }
}
