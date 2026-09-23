package mx.donchambitas.app.ui.pantallas

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.DatosPrueba
import mx.donchambitas.app.util.ReglaCorrutinas
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    @get:Rule
    val reglaCorrutinas = ReglaCorrutinas()

    @Test
    fun debeFiltrarTelefonoYRespetarLimites_cuandoCambiaLaCaptura() {
        val viewModel = RegistroViewModel(RepositorioAuthPrueba())

        viewModel.alCambiarNombre("n".repeat(LimitesRegistro.LARGO_MAXIMO_NOMBRE + 5))
        viewModel.alCambiarApellidos("a".repeat(LimitesRegistro.LARGO_MAXIMO_APELLIDOS + 5))
        viewModel.alCambiarCorreo("c".repeat(LimitesRegistro.LARGO_MAXIMO_CORREO + 5))
        viewModel.alCambiarTelefono("477-123 45ab6789")

        assertEquals(LimitesRegistro.LARGO_MAXIMO_NOMBRE, viewModel.estado.value.nombre.length)
        assertEquals(LimitesRegistro.LARGO_MAXIMO_APELLIDOS, viewModel.estado.value.apellidos.length)
        assertEquals(LimitesRegistro.LARGO_MAXIMO_CORREO, viewModel.estado.value.correo.length)
        assertEquals("4771234567", viewModel.estado.value.telefono)
    }

    @Test
    fun debeReclamarRolSinLlamarRepositorio_cuandoNoSeEligioRol() {
        val repositorio = RepositorioAuthPrueba()
        val viewModel = RegistroViewModel(repositorio)

        viewModel.alRegistrar()

        assertEquals(R.string.validacion_rol_sin_elegir, viewModel.estado.value.errorRol)
        assertEquals(0, repositorio.llamadasRegistro)
    }

    @Test
    fun debeNormalizarDatosYMostrarCarga_cuandoRegistra() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                barreraRegistro = CompletableDeferred()
            }
            val viewModel = RegistroViewModel(repositorio)
            viewModel.alElegirRol(RolUsuario.TRABAJADOR)
            viewModel.alCambiarNombre("  Refugio  ")
            viewModel.alCambiarApellidos("  Martínez Luna  ")
            viewModel.alCambiarCorreo("  Refugio@Ejemplo.MX  ")
            viewModel.alCambiarContrasena("12345678")
            viewModel.alCambiarTelefono("4771234567")

            viewModel.alRegistrar()
            runCurrent()

            assertTrue(viewModel.estado.value.cargando)
            assertEquals("refugio@ejemplo.mx", repositorio.ultimoCorreoRegistro)
            assertEquals("Refugio", repositorio.ultimoNombreRegistro)
            assertEquals("Martínez Luna", repositorio.ultimosApellidosRegistro)
            assertEquals("4771234567", repositorio.ultimoTelefonoRegistro)
            assertEquals(RolUsuario.TRABAJADOR, repositorio.ultimoRolRegistro)

            repositorio.barreraRegistro?.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.estado.value.cargando)
        }

    @Test
    fun debeNavegarSegunSesion_cuandoRegistroEsExitoso() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                sesion.value = DatosPrueba.crearSesion(
                    usuario = DatosPrueba.crearUsuario(rol = RolUsuario.TRABAJADOR)
                )
            }
            val viewModel = RegistroViewModel(repositorio)
            viewModel.alElegirRol(RolUsuario.TRABAJADOR)

            viewModel.alRegistrar()
            advanceUntilIdle()

            assertEquals(Ruta.InicioTrabajador, viewModel.estado.value.destino)
        }

    @Test
    fun debeVolverAInicioSesion_cuandoRegistroExitosoNoAbreSesion() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply { sesion.value = null }
            val viewModel = RegistroViewModel(repositorio)
            viewModel.alElegirRol(RolUsuario.CLIENTE)

            viewModel.alRegistrar()
            advanceUntilIdle()

            assertEquals(Ruta.IniciarSesion, viewModel.estado.value.destino)
        }

    @Test
    fun debeConservarCapturaYMensaje_cuandoCorreoYaExiste() =
        runTest(reglaCorrutinas.testDispatcher) {
            val mensaje = "El correo ya está registrado, inicia sesión"
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoRegistro = Resultado.Error(TipoError.VALIDACION, mensaje)
            }
            val viewModel = RegistroViewModel(repositorio)
            viewModel.alElegirRol(RolUsuario.CLIENTE)
            viewModel.alCambiarCorreo("persona@ejemplo.mx")

            viewModel.alRegistrar()
            advanceUntilIdle()

            assertEquals("persona@ejemplo.mx", viewModel.estado.value.correo)
            assertEquals(TipoError.VALIDACION, viewModel.estado.value.errorPantalla)
            assertEquals(mensaje, viewModel.estado.value.mensajePantalla)
        }

    @Test
    fun debeReintentarSinDuplicarYConsumirDestino_cuandoFallaLaRed() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoRegistro = Resultado.Error(TipoError.RED, "sin red")
            }
            val viewModel = RegistroViewModel(repositorio)
            viewModel.alElegirRol(RolUsuario.CLIENTE)
            viewModel.alRegistrar()
            advanceUntilIdle()

            repositorio.resultadoRegistro = Resultado.Exito(DatosPrueba.crearUsuario())
            repositorio.barreraRegistro = CompletableDeferred()
            viewModel.alReintentar()
            runCurrent()
            viewModel.alRegistrar()
            runCurrent()
            assertEquals(2, repositorio.llamadasRegistro)

            repositorio.barreraRegistro?.complete(Unit)
            advanceUntilIdle()
            viewModel.alConsumirDestino()
            assertNull(viewModel.estado.value.destino)
        }
}
