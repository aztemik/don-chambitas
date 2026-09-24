package mx.donchambitas.app.datos.repositorio

import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.request.HttpRequestBuilder
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioAuthSupabaseTest {

    private lateinit var cliente: ClienteAuthRemotoFalso
    private lateinit var repositorio: RepositorioAuthSupabase

    @Before
    fun preparar() {
        cliente = ClienteAuthRemotoFalso()
        repositorio = RepositorioAuthSupabase(cliente)
    }

    @Test
    fun `debe normalizar metadatos y devolver la sesion activa al registrar`() = runBlocking {
        val resultado = repositorio.registrar(
            correo = "  ANA@EJEMPLO.COM ",
            contrasena = "secreto123",
            nombre = " Ana ",
            apellidos = " López ",
            telefono = " 2221234567 ",
            rol = RolUsuario.TRABAJADOR
        )

        assertTrue(resultado is Resultado.Exito)
        val sesion = (resultado as Resultado.Exito).dato
        assertEquals("usr-1", sesion.usuario.id)
        assertEquals(RolUsuario.TRABAJADOR, sesion.usuario.rol)
        assertEquals("token-real", sesion.tokenAcceso)
        assertEquals("ana@ejemplo.com", cliente.correoRegistrado)
        assertEquals("Ana", cliente.nombreRegistrado)
        assertEquals("López", cliente.apellidosRegistrados)
        assertEquals("2221234567", cliente.telefonoRegistrado)
    }

    @Test
    fun `debe omitir el telefono vacio al registrar`() = runBlocking {
        repositorio.registrar(
            correo = "ana@ejemplo.com",
            contrasena = "secreto123",
            nombre = "Ana",
            apellidos = "López",
            telefono = "   ",
            rol = RolUsuario.CLIENTE
        )

        assertNull(cliente.telefonoRegistrado)
    }

    @Test
    fun `debe fallar como desconocido cuando el registro no abre sesion`() = runBlocking {
        cliente.sesionDevuelta = null

        val resultado = repositorio.registrar(
            "ana@ejemplo.com",
            "secreto123",
            "Ana",
            "López",
            null,
            RolUsuario.CLIENTE
        )

        assertEquals(TipoError.DESCONOCIDO, (resultado as Resultado.Error).tipo)
    }

    @Test
    fun `debe traducir credenciales rechazadas al iniciar sesion`() = runBlocking {
        cliente.error = AuthRestException(
            errorCode = "invalid_credentials",
            message = "Invalid login credentials",
            statusCode = 400
        )

        val resultado = repositorio.iniciarSesion("ana@ejemplo.com", "incorrecta")

        assertEquals(TipoError.AUTENTICACION, (resultado as Resultado.Error).tipo)
        assertEquals("Correo o contraseña incorrectos", resultado.mensaje)
    }

    @Test
    fun `debe solicitar recuperacion sin revelar si existe el correo`() = runBlocking {
        val resultado = repositorio.recuperarContrasena(" NO-EXISTE@EJEMPLO.COM ")

        assertTrue(resultado is Resultado.Exito)
        assertEquals("no-existe@ejemplo.com", cliente.correoRecuperacion)
        assertEquals(
            "mx.donchambitas.app://auth/recuperar-contrasena",
            URL_RECUPERACION
        )
    }

    @Test
    fun `debe rechazar cambio de contrasena cuando no hay sesion`() = runBlocking {
        cliente.sesionDevuelta = null

        val resultado = repositorio.cambiarContrasena("nuevoSecreto123")

        assertEquals(TipoError.AUTENTICACION, (resultado as Resultado.Error).tipo)
        assertFalse(cliente.cambioContrasenaEjecutado)
    }

    @Test
    fun `debe cambiar contrasena y cerrar la sesion activa`() = runBlocking {
        assertTrue(repositorio.cambiarContrasena("nuevoSecreto123") is Resultado.Exito)
        assertTrue(cliente.cambioContrasenaEjecutado)

        assertTrue(repositorio.cerrarSesion() is Resultado.Exito)
        assertTrue(cliente.cierreEjecutado)
    }

    @Test
    fun `debe reflejar sesion autenticada y ausencia de sesion en el flujo`() = runBlocking {
        cliente.estado.value = EstadoSesionAuthRemota.Autenticada(sesionRemota())
        assertEquals("usr-1", repositorio.sesionActual().first()?.usuario?.id)

        cliente.estado.value = EstadoSesionAuthRemota.SinSesion
        assertNull(repositorio.sesionActual().first())
    }

    @Test
    fun `debe traducir correo duplicado como validacion`() {
        val resultado = traducirErrorAuth(
            AuthRestException("email_exists", "User already registered", 422)
        )

        assertEquals(TipoError.VALIDACION, resultado.tipo)
        assertEquals("El correo ya está registrado, inicia sesión", resultado.mensaje)
    }

    @Test
    fun `debe traducir errores de red servidor y desconocido`() {
        val errorRed = traducirErrorAuth(
            HttpRequestException("sin red", HttpRequestBuilder())
        )
        val errorServidor = traducirErrorAuth(
            RestException("internal", "fallo", 503, "fallo")
        )
        val errorDesconocido = traducirErrorAuth(IllegalStateException("interno"))

        assertEquals(TipoError.RED, errorRed.tipo)
        assertEquals(TipoError.SERVIDOR, errorServidor.tipo)
        assertEquals(TipoError.DESCONOCIDO, errorDesconocido.tipo)
    }

    private class ClienteAuthRemotoFalso : ClienteAuthRemoto {
        val estado = MutableStateFlow<EstadoSesionAuthRemota>(EstadoSesionAuthRemota.SinSesion)
        override val estadoSesion: Flow<EstadoSesionAuthRemota> = estado

        var sesionDevuelta: SesionAuthRemota? = sesionRemota()
        var error: Throwable? = null
        var correoRegistrado: String? = null
        var nombreRegistrado: String? = null
        var apellidosRegistrados: String? = null
        var telefonoRegistrado: String? = null
        var correoRecuperacion: String? = null
        var cambioContrasenaEjecutado = false
        var cierreEjecutado = false

        override suspend fun registrar(
            correo: String,
            contrasena: String,
            nombre: String,
            apellidos: String,
            telefono: String?,
            rol: RolUsuario
        ): SesionAuthRemota? {
            lanzarError()
            correoRegistrado = correo
            nombreRegistrado = nombre
            apellidosRegistrados = apellidos
            telefonoRegistrado = telefono
            return sesionDevuelta?.copy(
                usuario = sesionDevuelta!!.usuario.copy(rol = rol.valor)
            )
        }

        override suspend fun iniciarSesion(
            correo: String,
            contrasena: String
        ): SesionAuthRemota? {
            lanzarError()
            return sesionDevuelta
        }

        override suspend fun recuperarContrasena(correo: String) {
            lanzarError()
            correoRecuperacion = correo
        }

        override suspend fun cambiarContrasena(nueva: String) {
            lanzarError()
            cambioContrasenaEjecutado = true
        }

        override suspend fun cerrarSesion() {
            lanzarError()
            cierreEjecutado = true
            sesionDevuelta = null
            estado.value = EstadoSesionAuthRemota.SinSesion
        }

        override fun sesionActual(): SesionAuthRemota? = sesionDevuelta

        private fun lanzarError() {
            error?.let { throw it }
        }
    }

    companion object {
        private fun sesionRemota() = SesionAuthRemota(
            tokenAcceso = "token-real",
            usuario = UsuarioAuthRemoto(
                id = "usr-1",
                correo = "ana@ejemplo.com",
                nombre = "Ana",
                apellidos = "López",
                telefono = "2221234567",
                rol = "cliente",
                fotoUrl = null,
                creadoEn = Instant.parse("2026-09-23T12:00:00Z"),
                actualizadoEn = Instant.parse("2026-09-23T12:00:00Z")
            )
        )
    }
}
