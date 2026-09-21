package mx.donchambitas.app.ui.pantallas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mx.donchambitas.app.ui.navegacion.EstadoSesionTemporal
import mx.donchambitas.app.ui.navegacion.Ruta
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para SplashViewModel y EstadoSplash.
 * Valida los criterios de aceptación del ticket S1-T15:
 * - Tiempo mínimo visible de 800 ms.
 * - Destinos correctos según el estado de sesión (P-02, P-05 y P-10).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun preparar() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicialTieneCargandoVerdaderoYSinDestino() {
        val viewModel = SplashViewModel()
        val estado = viewModel.estado.value
        assertTrue("El estado inicial debe tener cargando en true", estado.cargando)
        assertNull("El estado inicial no debe tener destino", estado.destino)
    }

    @Test
    fun resolverDestinoSinSesionLlevaAIniciarSesion() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.SIN_SESION)
        assertEquals(Ruta.IniciarSesion, destino)
        assertEquals("P-02", destino.idPantalla)
    }

    @Test
    fun resolverDestinoClienteLlevaAInicioCliente() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.CLIENTE)
        assertEquals(Ruta.InicioCliente, destino)
        assertEquals("P-05", destino.idPantalla)
    }

    @Test
    fun resolverDestinoTrabajadorLlevaAInicioTrabajador() {
        val viewModel = SplashViewModel()
        val destino = viewModel.resolverDestino(EstadoSesionTemporal.TRABAJADOR)
        assertEquals(Ruta.InicioTrabajador, destino)
        assertEquals("P-10", destino.idPantalla)
    }

    @Test
    fun verificarSesionSinSesionResuelveDestinoP02TrasTiempoMinimo() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        viewModel.verificarSesion(
            tiempoMinimoMs = 800L,
            proveedorSesion = { EstadoSesionTemporal.SIN_SESION }
        )

        // Antes de que transcurran los 800 ms
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
    fun verificarSesionClienteResuelveDestinoP05TrasTiempoMinimo() = runTest(testDispatcher) {
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
    fun verificarSesionTrabajadorResuelveDestinoP10TrasTiempoMinimo() = runTest(testDispatcher) {
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
