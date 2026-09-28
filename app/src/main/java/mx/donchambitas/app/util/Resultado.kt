package mx.donchambitas.app.util

sealed interface Resultado<out T> {
    data class Exito<T>(val dato: T) : Resultado<T>
    data class Error(val tipo: TipoError, val mensaje: String) : Resultado<Nothing>
}

/**
 * CORREO_DUPLICADO tiene tipo propio porque Supabase Auth rechaza el correo
 * repetido en ingles y datos/ no puede leer strings.xml: la pantalla lo
 * traduce, como a los demas tipos (H-11, CONTRATOS-API.md).
 */
enum class TipoError { RED, AUTENTICACION, VALIDACION, CORREO_DUPLICADO, LIMITE_IA, SERVIDOR, DESCONOCIDO }
