package mx.donchambitas.app.ui.pantallas

import androidx.annotation.StringRes
import mx.donchambitas.app.util.TipoError

/** Estado inmutable de P-04, cuya pantalla se implementa en S2-T10. */
data class EstadoRecuperarContrasena(
    val correo: String = "",
    @StringRes val errorCorreo: Int? = null,
    val errorPantalla: TipoError? = null,
    val cargando: Boolean = false,
    val enviado: Boolean = false
)
