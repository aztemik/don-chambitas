package mx.donchambitas.app.ui.tema

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Formas de componentes segun DISENO.md
val FormaBoton = RoundedCornerShape(12.dp)
val FormaCampo = RoundedCornerShape(12.dp)
val FormaTarjeta = RoundedCornerShape(16.dp)
val FormaChip = CircleShape
val FormaHojaInferior = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
val FormaFotoPerfil = CircleShape

// Mapeo a Shapes de Material 3
val Formas = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
)
