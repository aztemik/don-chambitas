package mx.donchambitas.app.datos.remoto.supabase

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

/**
 * Traduccion de los errores de Supabase Auth a TipoError.
 * Es la tabla "Errores de autenticacion" de docs/tecnico/CONTRATOS-API.md;
 * si una cambia, la otra cambia igual.
 *
 * Los codigos se comparan con el texto crudo de RestException.error y no con
 * AuthErrorCode: email_address_invalid lo devuelve el servidor pero no esta
 * en el enum de supabase-kt 3.0.3.
 */
private val CODIGOS_AUTENTICACION = setOf(
    "invalid_credentials",
    "email_not_confirmed",
    "user_banned",
    "session_not_found",
    "session_expired",
    "refresh_token_not_found",
    "bad_jwt"
)

private val CODIGOS_CORREO_DUPLICADO = setOf("user_already_exists", "email_exists")

private val CODIGOS_VALIDACION = setOf(
    "weak_password",
    "same_password",
    "validation_failed",
    "email_address_invalid"
)

private val CODIGOS_SERVIDOR = setOf(
    "over_request_rate_limit",
    "over_email_send_rate_limit",
    "signup_disabled",
    "email_provider_disabled",
    "unexpected_failure"
)

private val CODIGOS_LIMITE_DE_ENVIO = setOf("over_email_send_rate_limit", "over_request_rate_limit")

private const val PRIMER_CODIGO_SERVIDOR = 500
private const val ULTIMO_CODIGO_SERVIDOR = 599

private fun esFallaDeRed(error: Throwable): Boolean =
    error is HttpRequestException || error is HttpRequestTimeoutException

/** Cualquier error de una operacion de RepositorioAuth. */
fun traducirErrorAuth(error: Throwable): Resultado.Error {
    val tipo = when {
        esFallaDeRed(error) -> TipoError.RED
        error is RestException -> when (error.error) {
            in CODIGOS_AUTENTICACION -> TipoError.AUTENTICACION
            in CODIGOS_CORREO_DUPLICADO -> TipoError.CORREO_DUPLICADO
            in CODIGOS_VALIDACION -> TipoError.VALIDACION
            in CODIGOS_SERVIDOR -> TipoError.SERVIDOR
            else -> if (error.statusCode in PRIMER_CODIGO_SERVIDOR..ULTIMO_CODIGO_SERVIDOR) {
                TipoError.SERVIDOR
            } else {
                TipoError.DESCONOCIDO
            }
        }
        else -> TipoError.DESCONOCIDO
    }
    return Resultado.Error(tipo, error.message ?: error::class.simpleName.orEmpty())
}

/**
 * Error al leer la ficha de public.usuarios despues de una operacion de Auth.
 * Fuera de la red, siempre es un defecto nuestro: la ficha la crea el trigger
 * tg_auth_usuario_creado en la misma transaccion que la credencial.
 */
fun traducirErrorFicha(error: Throwable): Resultado.Error = Resultado.Error(
    tipo = if (esFallaDeRed(error)) TipoError.RED else TipoError.SERVIDOR,
    mensaje = error.message ?: error::class.simpleName.orEmpty()
)

/**
 * Los limites de envio que recuperarContrasena reporta como exito: uno por
 * correo solo salta si la cuenta existe, asi que responder distinto la
 * delataria.
 */
fun esLimiteDeEnvio(error: Throwable): Boolean =
    error is RestException && error.error in CODIGOS_LIMITE_DE_ENVIO
