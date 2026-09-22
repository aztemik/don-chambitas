package mx.donchambitas.app.ui.pantallas

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import mx.donchambitas.app.ui.navegacion.EstadoSesionTemporal
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.ReglaCorrutinas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias para SplashViewModel y EstadoSplash.
 * Sirve como plantilla oficial para pruebas unitarias de ViewModels con corrutinas.
 *
 * Utiliza [ReglaCorrutinas] para fijar el despachador de pruebas sin requerir el Looper de Android.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val reglaCorrutinas = ReglaCorrutinas()

    @Test
    fun debeTenerCargandoVerdaderoYSinDestino_cuandoIniciaEstado() {
        val viewModel = SplashViewModel()
        val estado = viewModel.estado.value
        assertTrue("El estado inicial debe tener cargando en true", estado.cargando)
        assertNull("El estado inicial no debe tener destino", estado.destino)
    }

    @Test
    fun debeResolverDestinoIniciarSesion_cuandoNoHaySesion() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.SIN_SESION)
        assertEquals(Ruta.IniciarSesion, destino)
        assertEquals("P-02", destino.idPantalla)
    }

    @Test
    fun debeResolverDestinoInicioCliente_cuandoSesionEsCliente() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.CLIENTE)
        assertEquals(Ruta.InicioCliente, destino)
        assertEquals("P-05", destino.idPantalla)
    }

    @Test
    fun debeResolverDestinoInicioTrabajador_cuandoSesionEsTrabajador() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.TRABAJADOR)
        assertEquals(Ruta.InicioTrabajador, destino)
        assertEquals("P-10", destino.idPantalla)
    }

    @Test
    fun debeResolverDestinoIniciarSesion_cuandoVerificaSinSesionYTerminaTiempoMinimo() =
        runTest(reglaCorrutinas.testDispatcher) {
            val viewModel = SplashViewModel()
            viewModel.verificarSesion(
                tiempoMinimoMs = 800L,
                proveedorSesion = { EstadoSesionTemporal.SIN_SESION }
            )

            // Antes de transcurrir los 800 ms
            advanceTimeBy(400L)
            assertTrue(viewModel.estado.value.cargando)
            assertNull(viewModel.estado.value.destino)

            // Tras completar los 800 ms
            advanceTimeBy(401L)
            advanceUntilIdle()

            assertFalse(viewModel.estado.value.cargando)
            assertEquals(Ruta.IniciarSesion, viewModel.estado.value.destino)
        }

    @Test
    fun debeResolverDestinoInicioCliente_cuandoVerificaClienteYTerminaTiempoMinimo() =
        runTest(reglaCorrutinas.testDispatcher) {
            val viewModel = SplashViewModel()
            viewModel.verificarSesion(
                tiempoMinimoMs = 800L,
                proveedorSesion = { EstadoSesionTemporal.CLIENTE }
            )

            advanceTimeBy(800L)
            advanceUntilIdle()

            assertFalse(viewModel.estado.value.cargando)
            assertEquals(Ruta.InicioCliente, viewModel.estado.value.destino)
        }

    @Test
    fun debeResolverDestinoInicioTrabajador_cuandoVerificaTrabajadorYTerminaTiempoMinimo() =
        runTest(reglaCorrutinas.testDispatcher) {
            val viewModel = SplashViewModel()
            viewModel.verificarSesion(
                tiempoMinimoMs = 800L,
                proveedorSesion = { EstadoSesionTemporal.TRABAJADOR }
            )

            advanceTimeBy(800L)
            advanceUntilIdle()

            assertFalse(viewModel.estado.value.cargando)
            assertEquals(Ruta.InicioTrabajador, viewModel.estado.value.destino)
        }
}
