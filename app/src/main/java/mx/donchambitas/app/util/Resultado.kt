package mx.donchambitas.app.util

sealed interface Resultado<out T> {
    data class Exito<T>(val dato: T) : Resultado<T>
    data class Error(val tipo: TipoError, val mensaje: String) : Resultado<Nothing>
}

enum class TipoError { RED, AUTENTICACION, VALIDACION, LIMITE_IA, SERVIDOR, DESCONOCIDO }
