package mx.donchambitas.app.ui.pantallas

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.modelo.Sesion
import mx.donchambitas.app.dominio.modelo.Usuario
import mx.donchambitas.app.dominio.repositorio.RepositorioAuth
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
class IniciarSesionViewModelTest {

    @get:Rule
    val reglaCorrutinas = ReglaCorrutinas()

    @Test
    fun debeNormalizarCorreoYMostrarCarga_cuandoIniciaSesion() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba()
            repositorio.barreraInicio = CompletableDeferred()
            val viewModel = IniciarSesionViewModel(repositorio)
            viewModel.alCambiarCorreo("  Persona@Ejemplo.MX  ")
            viewModel.alCambiarContrasena("secreto")

            viewModel.alIniciarSesion()
            runCurrent()

            assertTrue(viewModel.estado.value.cargando)
            assertEquals("persona@ejemplo.mx", repositorio.ultimoCorreoInicio)
            assertEquals("secreto", repositorio.ultimaContrasenaInicio)

            repositorio.barreraInicio?.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.estado.value.cargando)
            assertEquals(Ruta.InicioCliente, viewModel.estado.value.destino)
        }

    @Test
    fun debeNavegarAInicioTrabajador_cuandoLaSesionEsDeTrabajador() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoInicio = Resultado.Exito(
                    DatosPrueba.crearSesion(
                        usuario = DatosPrueba.crearUsuario(rol = RolUsuario.TRABAJADOR)
                    )
                )
            }
            val viewModel = IniciarSesionViewModel(repositorio)

            viewModel.alIniciarSesion()
            advanceUntilIdle()

            assertEquals(Ruta.InicioTrabajador, viewModel.estado.value.destino)
        }

    @Test
    fun debeConservarCapturaYPermitirReintento_cuandoFallaLaRed() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoInicio = Resultado.Error(TipoError.RED, "sin red")
            }
            val viewModel = IniciarSesionViewModel(repositorio)
            viewModel.alCambiarCorreo("persona@ejemplo.mx")
            viewModel.alCambiarContrasena("secreto")
            viewModel.alIniciarSesion()
            advanceUntilIdle()

            assertEquals(TipoError.RED, viewModel.estado.value.errorPantalla)
            assertEquals("persona@ejemplo.mx", viewModel.estado.value.correo)
            assertEquals("secreto", viewModel.estado.value.contrasena)

            repositorio.resultadoInicio = Resultado.Exito(DatosPrueba.crearSesion())
            viewModel.alReintentar()
            advanceUntilIdle()

            assertEquals(2, repositorio.llamadasInicio)
            assertEquals(Ruta.InicioCliente, viewModel.estado.value.destino)
        }

    @Test
    fun debeIgnorarSegundoEnvio_cuandoYaEstaCargando() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                barreraInicio = CompletableDeferred()
            }
            val viewModel = IniciarSesionViewModel(repositorio)

            viewModel.alIniciarSesion()
            runCurrent()
            viewModel.alIniciarSesion()
            runCurrent()

            assertEquals(1, repositorio.llamadasInicio)
            repositorio.barreraInicio?.complete(Unit)
        }

    @Test
    fun debeConvertirLimiteIaYConsumirDestino_cuandoSeRecibenEventos() =
        runTest(reglaCorrutinas.testDispatcher) {
            val repositorio = RepositorioAuthPrueba().apply {
                resultadoInicio = Resultado.Error(TipoError.LIMITE_IA, "no aplica")
            }
            val viewModel = IniciarSesionViewModel(repositorio)
            viewModel.alIniciarSesion()
            advanceUntilIdle()
            assertEquals(TipoError.DESCONOCIDO, viewModel.estado.value.errorPantalla)

            repositorio.resultadoInicio = Resultado.Exito(DatosPrueba.crearSesion())
            viewModel.alCambiarCorreo("otro@ejemplo.mx")
            viewModel.alIniciarSesion()
            advanceUntilIdle()
            viewModel.alConsumirDestino()

            assertNull(viewModel.estado.value.destino)
        }
}

internal class RepositorioAuthPrueba : RepositorioAuth {
    var resultadoInicio: Resultado<Sesion> = Resultado.Exito(DatosPrueba.crearSesion())
    var resultadoRegistro: Resultado<Usuario> = Resultado.Exito(DatosPrueba.crearUsuario())
    var resultadoRecuperacion: Resultado<Unit> = Resultado.Exito(Unit)
    val sesion = MutableStateFlow<Sesion?>(DatosPrueba.crearSesion())
    var barreraInicio: CompletableDeferred<Unit>? = null
    var barreraRegistro: CompletableDeferred<Unit>? = null
    var barreraRecuperacion: CompletableDeferred<Unit>? = null
    var llamadasInicio = 0
    var llamadasRegistro = 0
    var llamadasRecuperacion = 0
    var ultimoCorreoInicio: String? = null
    var ultimaContrasenaInicio: String? = null
    var ultimoCorreoRegistro: String? = null
    var ultimaContrasenaRegistro: String? = null
    var ultimoNombreRegistro: String? = null
    var ultimosApellidosRegistro: String? = null
    var ultimoTelefonoRegistro: String? = null
    var ultimoRolRegistro: RolUsuario? = null
    var ultimoCorreoRecuperacion: String? = null

    override suspend fun iniciarSesion(correo: String, contrasena: String): Resultado<Sesion> {
        llamadasInicio++
        ultimoCorreoInicio = correo
        ultimaContrasenaInicio = contrasena
        barreraInicio?.await()
        return resultadoInicio
    }

    override suspend fun registrar(
        correo: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        telefono: String?,
        rol: RolUsuario
    ): Resultado<Usuario> {
        llamadasRegistro++
        ultimoCorreoRegistro = correo
        ultimaContrasenaRegistro = contrasena
        ultimoNombreRegistro = nombre
        ultimosApellidosRegistro = apellidos
        ultimoTelefonoRegistro = telefono
        ultimoRolRegistro = rol
        barreraRegistro?.await()
        return resultadoRegistro
    }

    override suspend fun recuperarContrasena(correo: String): Resultado<Unit> {
        llamadasRecuperacion++
        ultimoCorreoRecuperacion = correo
        barreraRecuperacion?.await()
        return resultadoRecuperacion
    }

    override suspend fun cambiarContrasena(nueva: String): Resultado<Unit> = Resultado.Exito(Unit)

    override suspend fun cerrarSesion(): Resultado<Unit> = Resultado.Exito(Unit)

    override fun sesionActual(): Flow<Sesion?> = sesion
}
