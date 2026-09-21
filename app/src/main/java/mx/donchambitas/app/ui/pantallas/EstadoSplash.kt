package mx.donchambitas.app.ui.pantallas

import mx.donchambitas.app.ui.navegacion.Ruta

/**
 * Estado inmutable de la pantalla de bienvenida (P-01 Splash).
 * Conforme a CONVENCIONES.md (prefijo Estado... y data class).
 *
 * @property cargando Indica si la verificación y el tiempo mínimo en pantalla siguen en curso.
 * @property destino Ruta hacia la que debe navegarse una vez resuelto el estado de sesión.
 */
data class EstadoSplash(
    val cargando: Boolean = true,
    val destino: Ruta? = null
)
