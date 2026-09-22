package mx.donchambitas.app.ui.tema

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Escala de espaciados base 4 segun DISENO.md.
 * Expone las dimensiones fijas para evitar numeros magicos en el codigo.
 */
object Espaciado {
    // Escala base en dp
    val dp4: Dp = 4.dp
    val dp8: Dp = 8.dp
    val dp12: Dp = 12.dp
    val dp16: Dp = 16.dp
    val dp24: Dp = 24.dp
    val dp32: Dp = 32.dp
    val dp48: Dp = 48.dp

    // Alias descriptivos por magnitud
    val espacio4: Dp = dp4
    val espacio8: Dp = dp8
    val espacio12: Dp = dp12
    val espacio16: Dp = dp16
    val espacio24: Dp = dp24
    val espacio32: Dp = dp32
    val espacio48: Dp = dp48

    // Usos semanticos definidos en DISENO.md
    val margenPantalla: Dp = dp16
    val separacionTarjetas: Dp = dp12
    val rellenoTarjeta: Dp = dp16
}
