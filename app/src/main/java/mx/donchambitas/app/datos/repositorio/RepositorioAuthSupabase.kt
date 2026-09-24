package mx.donchambitas.app.datos.repositorio

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.time.Instant
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.modelo.Sesion
import mx.donchambitas.app.dominio.modelo.Usuario
import mx.donchambitas.app.dominio.repositorio.RepositorioAuth
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

internal const val URL_RECUPERACION =
    "mx.donchambitas.app://auth/recuperar-contrasena"

/** Datos mínimos de una sesión remota, separados del SDK para poder probar sin red. */
internal data class SesionAuthRemota(
    val tokenAcceso: String,
    val usuario: UsuarioAuthRemoto
)

internal data class UsuarioAuthRemoto(
    val id: String,
    val correo: String?,
    val nombre: String?,
    val apellidos: String?,
    val telefono: String?,
    val rol: String?,
    val fotoUrl: String?,
    val creadoEn: Instant?,
    val actualizadoEn: Instant?
)

internal sealed interface EstadoSesionAuthRemota {
    data class Autenticada(val sesion: SesionAuthRemota) : EstadoSesionAuthRemota
    data object SinSesion : EstadoSesionAuthRemota
}

/** Frontera pequeña que mantiene las clases de Supabase dentro de datos. */
internal interface ClienteAuthRemoto {
    val estadoSesion: Flow<EstadoSesionAuthRemota>

    suspend fun registrar(
        correo: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        telefono: String?,
        rol: RolUsuario
    ): SesionAuthRemota?

    suspend fun iniciarSesion(correo: String, contrasena: String): SesionAuthRemota?
    suspend fun recuperarContrasena(correo: String)
    suspend fun cambiarContrasena(nueva: String)
    suspend fun cerrarSesion()
    fun sesionActual(): SesionAuthRemota?
}

private class ClienteAuthSupabase(
    private val supabase: SupabaseClient
) : ClienteAuthRemoto {

    override val estadoSesion: Flow<EstadoSesionAuthRemota> =
        supabase.auth.sessionStatus.map { estado ->
            when (estado) {
                is SessionStatus.Authenticated -> {
                    EstadoSesionAuthRemota.Autenticada(estado.session.aSesionRemota())
                }
                SessionStatus.Initializing,
                is SessionStatus.NotAuthenticated,
                is SessionStatus.RefreshFailure -> EstadoSesionAuthRemota.SinSesion
            }
        }

    override suspend fun registrar(
        correo: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        telefono: String?,
        rol: RolUsuario
    ): SesionAuthRemota? {
        supabase.auth.signUpWith(Email) {
            email = correo
            password = contrasena
            data = buildJsonObject {
                put("nombre", nombre)
                put("apellidos", apellidos)
                telefono?.let { put("telefono", it) }
                put("rol", rol.valor)
            }
        }
        return sesionActual()
    }

    override suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): SesionAuthRemota? {
        supabase.auth.signInWith(Email) {
            email = correo
            password = contrasena
        }
        return sesionActual()
    }

    override suspend fun recuperarContrasena(correo: String) {
        supabase.auth.resetPasswordForEmail(
            email = correo,
            redirectUrl = URL_RECUPERACION
        )
    }

    override suspend fun cambiarContrasena(nueva: String) {
        supabase.auth.updateUser {
            password = nueva
        }
    }

    override suspend fun cerrarSesion() {
        supabase.auth.signOut()
    }

    override fun sesionActual(): SesionAuthRemota? =
        supabase.auth.currentSessionOrNull()?.aSesionRemota()
}

@Singleton
class RepositorioAuthSupabase internal constructor(
    private val cliente: ClienteAuthRemoto
) : RepositorioAuth {

    @Inject
    constructor(supabase: SupabaseClient) : this(ClienteAuthSupabase(supabase))

    override suspend fun registrar(
        correo: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        telefono: String?,
        rol: RolUsuario
    ): Resultado<Sesion> = ejecutar {
        val sesion = cliente.registrar(
            correo = correo.trim().lowercase(),
            contrasena = contrasena,
            nombre = nombre.trim(),
            apellidos = apellidos.trim(),
            telefono = telefono?.trim()?.takeIf(String::isNotEmpty),
            rol = rol
        ) ?: return@ejecutar errorDesconocido("El registro no abrió una sesión")
        Resultado.Exito(sesion.aDominio())
    }

    override suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Resultado<Sesion> = ejecutar {
        val sesion = cliente.iniciarSesion(correo.trim().lowercase(), contrasena)
            ?: return@ejecutar Resultado.Error(
                TipoError.AUTENTICACION,
                "Correo o contraseña incorrectos"
            )
        Resultado.Exito(sesion.aDominio())
    }

    override suspend fun recuperarContrasena(correo: String): Resultado<Unit> = ejecutar {
        cliente.recuperarContrasena(correo.trim().lowercase())
        Resultado.Exito(Unit)
    }

    override suspend fun cambiarContrasena(nueva: String): Resultado<Unit> = ejecutar {
        if (cliente.sesionActual() == null) {
            return@ejecutar Resultado.Error(
                TipoError.AUTENTICACION,
                "La sesión terminó, solicita un enlace nuevo"
            )
        }
        cliente.cambiarContrasena(nueva)
        Resultado.Exito(Unit)
    }

    override suspend fun cerrarSesion(): Resultado<Unit> = ejecutar {
        cliente.cerrarSesion()
        Resultado.Exito(Unit)
    }

    override fun sesionActual(): Flow<Sesion?> = cliente.estadoSesion
        .map { estado ->
            when (estado) {
                is EstadoSesionAuthRemota.Autenticada -> estado.sesion.aDominio()
                EstadoSesionAuthRemota.SinSesion -> null
            }
        }
        .distinctUntilChanged()

    private suspend fun <T> ejecutar(
        operacion: suspend () -> Resultado<T>
    ): Resultado<T> = try {
        operacion()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        traducirErrorAuth(error)
    }
}

private fun UserSession.aSesionRemota(): SesionAuthRemota {
    val info = requireNotNull(user) { "La sesión de Supabase no contiene usuario" }
    val metadata = info.userMetadata
    return SesionAuthRemota(
        tokenAcceso = accessToken,
        usuario = UsuarioAuthRemoto(
            id = info.id,
            correo = info.email,
            nombre = metadata?.get("nombre")?.jsonPrimitive?.contentOrNull,
            apellidos = metadata?.get("apellidos")?.jsonPrimitive?.contentOrNull,
            telefono = metadata?.get("telefono")?.jsonPrimitive?.contentOrNull,
            rol = metadata?.get("rol")?.jsonPrimitive?.contentOrNull,
            fotoUrl = metadata?.get("foto_url")?.jsonPrimitive?.contentOrNull,
            creadoEn = info.createdAt?.let { Instant.parse(it.toString()) },
            actualizadoEn = info.updatedAt?.let { Instant.parse(it.toString()) }
        )
    )
}

private fun SesionAuthRemota.aDominio(): Sesion {
    val creado = usuario.creadoEn ?: Instant.EPOCH
    val correo = requireNotNull(usuario.correo) { "El usuario autenticado no contiene correo" }
    val rol = usuario.rol?.let(RolUsuario::desdeValor) ?: RolUsuario.CLIENTE
    return Sesion(
        usuario = Usuario(
            id = usuario.id,
            correo = correo,
            nombre = usuario.nombre ?: "Sin nombre",
            apellidos = usuario.apellidos ?: "Sin apellidos",
            telefono = usuario.telefono,
            rol = rol,
            fotoUrl = usuario.fotoUrl,
            activo = true,
            creadoEn = creado,
            actualizadoEn = usuario.actualizadoEn ?: creado
        ),
        tokenAcceso = tokenAcceso
    )
}

internal fun traducirErrorAuth(error: Throwable): Resultado.Error {
    if (error is HttpRequestException ||
        error is HttpRequestTimeoutException ||
        error is IOException
    ) {
        return Resultado.Error(TipoError.RED, "Revisa tu conexión e intenta de nuevo")
    }

    if (error is AuthRestException) {
        val tipo = when (error.errorCode) {
            AuthErrorCode.InvalidCredentials,
            AuthErrorCode.BadJwt,
            AuthErrorCode.NoAuthorization,
            AuthErrorCode.UserNotFound,
            AuthErrorCode.SessionNotFound,
            AuthErrorCode.SessionExpired,
            AuthErrorCode.RefreshTokenNotFound,
            AuthErrorCode.RefreshTokenAlreadyUsed,
            AuthErrorCode.UserBanned,
            AuthErrorCode.EmailNotConfirmed -> TipoError.AUTENTICACION

            AuthErrorCode.EmailExists,
            AuthErrorCode.UserAlreadyExists,
            AuthErrorCode.Conflict,
            AuthErrorCode.WeakPassword,
            AuthErrorCode.ValidationFailed,
            AuthErrorCode.BadJson,
            AuthErrorCode.CaptchaFailed,
            AuthErrorCode.EmailAddressNotAuthorized,
            AuthErrorCode.SamePassword -> TipoError.VALIDACION

            else -> tipoPorCodigoHttp(error.statusCode)
        }
        return Resultado.Error(tipo, mensajeSeguro(tipo, error.errorCode))
    }

    if (error is RestException) {
        val tipo = tipoPorCodigoHttp(error.statusCode)
        return Resultado.Error(tipo, mensajeSeguro(tipo, null))
    }

    return errorDesconocido("Algo salió mal, intenta de nuevo")
}

private fun tipoPorCodigoHttp(codigo: Int): TipoError = when (codigo) {
    401, 403 -> TipoError.AUTENTICACION
    400, 409, 422 -> TipoError.VALIDACION
    in 500..599, 429 -> TipoError.SERVIDOR
    else -> TipoError.DESCONOCIDO
}

private fun mensajeSeguro(tipo: TipoError, codigo: AuthErrorCode?): String = when {
    codigo == AuthErrorCode.EmailExists || codigo == AuthErrorCode.UserAlreadyExists ->
        "El correo ya está registrado, inicia sesión"
    tipo == TipoError.AUTENTICACION -> "Correo o contraseña incorrectos"
    tipo == TipoError.VALIDACION -> "Revisa los datos e intenta de nuevo"
    tipo == TipoError.SERVIDOR -> "Algo falló de nuestro lado, intenta más tarde"
    tipo == TipoError.RED -> "Revisa tu conexión e intenta de nuevo"
    else -> "Algo salió mal, intenta de nuevo"
}

private fun errorDesconocido(mensaje: String) =
    Resultado.Error(TipoError.DESCONOCIDO, mensaje)
