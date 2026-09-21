package mx.donchambitas.app.ui.componentes

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import mx.donchambitas.app.R
import mx.donchambitas.app.ui.tema.Arena
import mx.donchambitas.app.ui.tema.Cafe
import mx.donchambitas.app.ui.tema.Carbon
import mx.donchambitas.app.ui.tema.DonChambitasTema
import mx.donchambitas.app.ui.tema.Mostaza
import mx.donchambitas.app.ui.tema.cuerpoFuerte
import mx.donchambitas.app.ui.tema.pie
import mx.donchambitas.app.ui.tema.subtitulo

/**
 * Modelo de datos para un destino en la barra de navegacion inferior.
 */
data class DestinoBarraInferior(
    val id: Int,
    val etiqueta: String,
    val icono: ImageVector
)

/**
 * Destinos base de navegacion para la barra inferior segun DISENO.md.
 */
@Composable
fun obtenerDestinosPorDefecto(): List<DestinoBarraInferior> = listOf(
    DestinoBarraInferior(0, stringResource(R.string.navegacion_inicio), Icons.Outlined.Home),
    DestinoBarraInferior(1, stringResource(R.string.navegacion_buscar), Icons.Outlined.Search),
    DestinoBarraInferior(2, stringResource(R.string.navegacion_solicitudes), Icons.AutoMirrored.Outlined.Assignment),
    DestinoBarraInferior(3, stringResource(R.string.navegacion_perfil), Icons.Outlined.Person)
)

/**
 * Barra superior de la aplicacion segun DISENO.md.
 * Fondo Mostaza, titulo e iconos en Carbon (7.09:1 WCAG AAA), flecha de regreso opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    alRegresar: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                style = DonChambitasTema.tipografia.subtitulo,
                color = Carbon
            )
        },
        navigationIcon = {
            if (alRegresar != null) {
                IconButton(onClick = alRegresar) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.regresar),
                        tint = Carbon
                    )
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Mostaza,
            titleContentColor = Carbon,
            navigationIconContentColor = Carbon,
            actionIconContentColor = Carbon
        ),
        modifier = modifier
    )
}

/**
 * Barra inferior con 4 destinos y el indicador en color Mostaza segun DISENO.md.
 * Sobre el indicador Mostaza, el icono es Carbon (7.09:1 WCAG AAA).
 */
@Composable
fun BarraInferior(
    destinoActual: Int,
    alSeleccionarDestino: (Int) -> Unit,
    destinos: List<DestinoBarraInferior> = obtenerDestinosPorDefecto(),
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Arena,
        contentColor = Carbon,
        modifier = modifier
    ) {
        destinos.forEach { destino ->
            val seleccionado = destino.id == destinoActual
            NavigationBarItem(
                selected = seleccionado,
                onClick = { alSeleccionarDestino(destino.id) },
                icon = {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = destino.etiqueta
                    )
                },
                label = {
                    Text(
                        text = destino.etiqueta,
                        style = if (seleccionado) DonChambitasTema.tipografia.cuerpoFuerte else DonChambitasTema.tipografia.pie
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Carbon,
                    selectedTextColor = Carbon,
                    indicatorColor = Mostaza,
                    unselectedIconColor = Cafe,
                    unselectedTextColor = Cafe
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaBarraSuperiorConRegreso() {
    DonChambitasTema {
        BarraSuperior(
            titulo = "Detalle del servicio",
            alRegresar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaBarraSuperiorPrincipal() {
    DonChambitasTema {
        BarraSuperior(
            titulo = "Don Chambitas"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaBarraInferior() {
    DonChambitasTema {
        BarraInferior(
            destinoActual = 0,
            alSeleccionarDestino = {}
        )
    }
}
