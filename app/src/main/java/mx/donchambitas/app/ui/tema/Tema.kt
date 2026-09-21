package mx.donchambitas.app.ui.tema

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de color Taller de Material 3 segun DISENO.md.
 * Sin modo oscuro en el MVP (DEC-15).
 */
val EsquemaColoresTaller = lightColorScheme(
    primary = Mostaza,
    onPrimary = Carbon,
    primaryContainer = Arena,
    onPrimaryContainer = Carbon,
    secondary = Terracota,
    onSecondary = Blanco,
    secondaryContainer = Arena,
    onSecondaryContainer = Terracota,
    tertiary = MostazaOscuro,
    onTertiary = Blanco,
    tertiaryContainer = Arena,
    onTertiaryContainer = MostazaOscuro,
    background = Crema,
    onBackground = Carbon,
    surface = Arena,
    onSurface = Carbon,
    surfaceVariant = Arena,
    onSurfaceVariant = Cafe,
    outline = Borde,
    outlineVariant = Borde,
    error = Error,
    onError = Blanco,
    errorContainer = Arena,
    onErrorContainer = Error,
    scrim = Carbon
)

@Composable
fun DonChambitasTema(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaColoresTaller,
        typography = Tipografia,
        shapes = Formas,
        content = content
    )
}

object DonChambitasTema {
    val colores: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme

    val tipografia: Typography
        @Composable
        get() = MaterialTheme.typography

    val formas: Shapes
        @Composable
        get() = MaterialTheme.shapes

    val espaciado: Espaciado
        get() = Espaciado
}
