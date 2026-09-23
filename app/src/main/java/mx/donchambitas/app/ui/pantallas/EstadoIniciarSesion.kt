package mx.donchambitas.app.ui.pantallas

import androidx.annotation.StringRes
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.TipoError

/**
 * Estado inmutable de la pantalla de inicio de sesion (P-02).
 * Especificado en docs/producto/DISENO-AUTENTICACION.md, seccion 2.2.
 *
 * Los mensajes de error viajan como identificador de recurso y no como texto,
 * por la misma razon que en [EstadoRegistro]: CONVENCIONES.md prohibe cadenas
 * de interfaz fuera de strings.xml.
 *
 * @property destino Evento de navegacion de un solo uso. La pantalla navega y
 *   lo devuelve a nulo de inmediato; sin eso, una rotacion vuelve a navegar.
 */
data class EstadoIniciarSesion(
    val correo: String = "",
    val contrasena: String = "",
    @StringRes val errorCorreo: Int? = null,
    @StringRes val errorContrasena: Int? = null,
    val errorPantalla: TipoError? = null,
    val cargando: Boolean = false,
    val destino: Ruta? = null
)
