package mx.donchambitas.app.ui.pantallas

import androidx.annotation.StringRes
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.TipoError

/**
 * Estado inmutable de la pantalla de registro (P-03).
 * Especificado en docs/producto/DISENO-AUTENTICACION.md, seccion 3.3.
 *
 * Los mensajes de error viajan como identificador de recurso y no como texto:
 * CONVENCIONES.md prohibe cadenas de interfaz fuera de strings.xml, y un
 * estado que ya trae el texto en espanol es una cadena de interfaz fuera de
 * su lugar. El error de pantalla tambien: se pinta con el mensaje de su
 * TipoError, porque el texto que manda Supabase Auth llega en ingles (H-11).
 *
 * @property rol Rol elegido. Nulo mientras el usuario no elige, que es el estado inicial.
 * @property destino Evento de navegacion de un solo uso, igual que en [EstadoIniciarSesion].
 */
data class EstadoRegistro(
    val rol: RolUsuario? = null,
    val nombre: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val telefono: String = "",
    @StringRes val errorRol: Int? = null,
    @StringRes val errorNombre: Int? = null,
    @StringRes val errorApellidos: Int? = null,
    @StringRes val errorCorreo: Int? = null,
    @StringRes val errorContrasena: Int? = null,
    @StringRes val errorTelefono: Int? = null,
    val errorPantalla: TipoError? = null,
    val cargando: Boolean = false,
    val destino: Ruta? = null
)
