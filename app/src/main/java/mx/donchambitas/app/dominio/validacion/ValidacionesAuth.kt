package mx.donchambitas.app.dominio.validacion

import mx.donchambitas.app.dominio.modelo.RolUsuario

/**
 * Motivo por el que un campo de autenticacion no pasa. Es Kotlin puro: la capa
 * ui lo traduce a su recurso validacion_* de strings.xml, porque el dominio no
 * conoce R (ARQUITECTURA.md).
 */
enum class FalloValidacion {
    CORREO_VACIO,
    CORREO_FORMATO,
    CORREO_LARGO,
    CONTRASENA_VACIA,
    CONTRASENA_CORTA,
    ROL_SIN_ELEGIR,
    NOMBRE_VACIO,
    NOMBRE_LARGO,
    APELLIDOS_VACIO,
    APELLIDOS_LARGO,
    TELEFONO_VACIO,
    TELEFONO_DIGITOS
}

/**
 * Topes de captura de P-03. Son los de public.usuarios en
 * basedatos/01_esquema.sql: el campo impide escribir de mas en lugar de
 * dejar que la base rechace el registro cuando ya no hay nada que corregir.
 */
object LimitesRegistro {
    const val LARGO_MAXIMO_NOMBRE = 80
    const val LARGO_MAXIMO_APELLIDOS = 120
    const val LARGO_MAXIMO_CORREO = 160
    const val LARGO_TELEFONO = 10
    const val LARGO_MINIMO_CONTRASENA = 8
}

/**
 * Reglas de los formularios de P-02 y P-03.
 * Especificadas en docs/producto/DISENO-AUTENTICACION.md, seccion 5.2.
 *
 * Cada funcion devuelve el primer fallo del campo, o nulo si esta bien. No
 * normalizan lo que se envia: eso pasa en el ViewModel al enviar (1.6).
 */
object ValidacionesAuth {

    // Espejo de ck_usuario_correo_valido en basedatos/01_esquema.sql.
    // Si aquella cambia, esta cambia el mismo dia, o la base rechaza
    // registros que la aplicacion dio por buenos.
    private val CORREO_VALIDO = Regex("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$")

    fun validarCorreo(correo: String): FalloValidacion? {
        val limpio = correo.trim()
        return when {
            limpio.isEmpty() -> FalloValidacion.CORREO_VACIO
            !CORREO_VALIDO.matches(limpio) -> FalloValidacion.CORREO_FORMATO
            limpio.length > LimitesRegistro.LARGO_MAXIMO_CORREO -> FalloValidacion.CORREO_LARGO
            else -> null
        }
    }

    /**
     * En P-02 solo se exige que no este vacia: pedir el minimo al entrar
     * delataria la longitud y dejaria fuera a cuentas anteriores a la regla.
     */
    fun validarContrasenaInicioSesion(contrasena: String): FalloValidacion? =
        if (contrasena.isEmpty()) FalloValidacion.CONTRASENA_VACIA else null

    fun validarContrasenaRegistro(contrasena: String): FalloValidacion? =
        if (contrasena.length < LimitesRegistro.LARGO_MINIMO_CONTRASENA) {
            FalloValidacion.CONTRASENA_CORTA
        } else {
            null
        }

    fun validarRol(rol: RolUsuario?): FalloValidacion? =
        if (rol == null) FalloValidacion.ROL_SIN_ELEGIR else null

    fun validarNombre(nombre: String): FalloValidacion? {
        val limpio = nombre.trim()
        return when {
            limpio.isEmpty() -> FalloValidacion.NOMBRE_VACIO
            limpio.length > LimitesRegistro.LARGO_MAXIMO_NOMBRE -> FalloValidacion.NOMBRE_LARGO
            else -> null
        }
    }

    fun validarApellidos(apellidos: String): FalloValidacion? {
        val limpio = apellidos.trim()
        return when {
            limpio.isEmpty() -> FalloValidacion.APELLIDOS_VACIO
            limpio.length > LimitesRegistro.LARGO_MAXIMO_APELLIDOS -> FalloValidacion.APELLIDOS_LARGO
            else -> null
        }
    }

    fun validarTelefono(telefono: String): FalloValidacion? {
        val limpio = telefono.trim()
        return when {
            limpio.isEmpty() -> FalloValidacion.TELEFONO_VACIO
            limpio.length != LimitesRegistro.LARGO_TELEFONO || !limpio.all(Char::isDigit) ->
                FalloValidacion.TELEFONO_DIGITOS
            else -> null
        }
    }
}
