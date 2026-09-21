package mx.donchambitas.app.ui.pantallas

import androidx.annotation.StringRes
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.util.TipoError

/**
 * Estado inmutable de la pantalla de registro (P-03).
 * Especificado en docs/producto/DISENO-AUTENTICACION.md, seccion 3.3.
 *
 * Los mensajes de error viajan como identificador de recurso y no como texto:
 * CONVENCIONES.md prohibe cadenas de interfaz fuera de strings.xml, y un
 * estado que ya trae el texto en espanol es una cadena de interfaz fuera de
 * su lugar. La unica excepcion es [mensajePantalla].
 *
 * @property rol Rol elegido. Nulo mientras el usuario no elige, que es el estado inicial.
 * @property mensajePantalla Mensaje que llega desde la capa de datos en un error de
 *   VALIDACION. CONTRATOS-API.md indica que ese texto ya viene escrito en espanol desde
 *   la base, asi que se muestra tal cual en vez de volver a traducirlo aqui.
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
    val mensajePantalla: String? = null,
    val cargando: Boolean = false
)

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
}
