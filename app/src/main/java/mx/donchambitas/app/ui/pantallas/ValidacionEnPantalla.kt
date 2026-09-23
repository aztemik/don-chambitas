package mx.donchambitas.app.ui.pantallas

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.validacion.FalloValidacion

/** Recurso de strings.xml de cada fallo, con las claves de la seccion 7 de DISENO-AUTENTICACION.md. */
@StringRes
fun FalloValidacion.mensaje(): Int = when (this) {
    FalloValidacion.CORREO_VACIO -> R.string.validacion_correo_vacio
    FalloValidacion.CORREO_FORMATO -> R.string.validacion_correo_formato
    FalloValidacion.CORREO_LARGO -> R.string.validacion_correo_largo
    FalloValidacion.CONTRASENA_VACIA -> R.string.validacion_contrasena_vacia
    FalloValidacion.CONTRASENA_CORTA -> R.string.validacion_contrasena_corta
    FalloValidacion.ROL_SIN_ELEGIR -> R.string.validacion_rol_sin_elegir
    FalloValidacion.NOMBRE_VACIO -> R.string.validacion_nombre_vacio
    FalloValidacion.NOMBRE_LARGO -> R.string.validacion_nombre_largo
    FalloValidacion.APELLIDOS_VACIO -> R.string.validacion_apellidos_vacio
    FalloValidacion.APELLIDOS_LARGO -> R.string.validacion_apellidos_largo
    FalloValidacion.TELEFONO_VACIO -> R.string.validacion_telefono_vacio
    FalloValidacion.TELEFONO_DIGITOS -> R.string.validacion_telefono_digitos
}

/**
 * Avisa cuando el foco sale del campo, no cuando entra ni al componerse por
 * primera vez: es la regla 1 de 1.5, validar al perder el foco.
 */
internal fun Modifier.alPerderFoco(accion: () -> Unit): Modifier = composed {
    var teniaFoco by remember { mutableStateOf(false) }
    onFocusChanged { foco ->
        if (teniaFoco && !foco.hasFocus) accion()
        teniaFoco = foco.hasFocus
    }
}
