package mx.donchambitas.app.ui.pantallas

import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.modelo.RolUsuario

// Espejo de ck_usuario_correo_valido en basedatos/01_esquema.sql.
// Si aquella cambia, esta cambia el mismo dia.
private val CORREO_VALIDO = Regex("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$")

enum class CampoAutenticacion {
    ROL,
    NOMBRE,
    APELLIDOS,
    CORREO,
    CONTRASENA,
    TELEFONO
}

fun validarCorreo(correo: String): Int? {
    val normalizado = correo.trim().lowercase()
    return when {
        normalizado.isEmpty() -> R.string.validacion_correo_vacio
        normalizado.length > LimitesRegistro.LARGO_MAXIMO_CORREO ->
            R.string.validacion_correo_largo
        !CORREO_VALIDO.matches(normalizado) -> R.string.validacion_correo_formato
        else -> null
    }
}

fun validarContrasenaInicio(contrasena: String): Int? =
    if (contrasena.isEmpty()) R.string.validacion_contrasena_vacia else null

fun validarContrasenaRegistro(contrasena: String): Int? =
    if (contrasena.length < 8) R.string.validacion_contrasena_corta else null

fun validarRol(rol: RolUsuario?): Int? =
    if (rol == null) R.string.validacion_rol_sin_elegir else null

fun validarNombre(nombre: String): Int? {
    val normalizado = nombre.trim()
    return when {
        normalizado.isEmpty() -> R.string.validacion_nombre_vacio
        normalizado.length > LimitesRegistro.LARGO_MAXIMO_NOMBRE ->
            R.string.validacion_nombre_largo
        else -> null
    }
}

fun validarApellidos(apellidos: String): Int? {
    val normalizado = apellidos.trim()
    return when {
        normalizado.isEmpty() -> R.string.validacion_apellidos_vacio
        normalizado.length > LimitesRegistro.LARGO_MAXIMO_APELLIDOS ->
            R.string.validacion_apellidos_largo
        else -> null
    }
}

fun validarTelefono(telefono: String): Int? = when {
    telefono.isEmpty() -> R.string.validacion_telefono_vacio
    telefono.length != LimitesRegistro.LARGO_TELEFONO || telefono.any { !it.isDigit() } ->
        R.string.validacion_telefono_digitos
    else -> null
}
