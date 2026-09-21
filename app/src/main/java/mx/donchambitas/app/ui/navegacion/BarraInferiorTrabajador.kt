package mx.donchambitas.app.ui.navegacion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
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

/**
 * Destino representativo en la barra inferior del trabajador.
 */
data class ElementoBarraTrabajador(
    val ruta: Ruta,
    val etiquetaRes: Int,
    val icono: ImageVector
)

/**
 * Los 4 destinos exactos de la barra inferior del trabajador según PANTALLAS.md:
 * P-10 Inicio · P-12 Servicios · P-15 Chats · P-18 Cuenta.
 */
val DESTINOS_BARRA_TRABAJADOR: List<ElementoBarraTrabajador> = listOf(
    ElementoBarraTrabajador(
        ruta = Ruta.InicioTrabajador,
        etiquetaRes = R.string.navegacion_inicio,
        icono = Icons.Outlined.Home
    ),
    ElementoBarraTrabajador(
        ruta = Ruta.MisServicios,
        etiquetaRes = R.string.navegacion_servicios,
        icono = Icons.Outlined.Build
    ),
    ElementoBarraTrabajador(
        ruta = Ruta.Conversaciones,
        etiquetaRes = R.string.navegacion_chats,
        icono = Icons.AutoMirrored.Outlined.Chat
    ),
    ElementoBarraTrabajador(
        ruta = Ruta.MiCuenta,
        etiquetaRes = R.string.navegacion_cuenta,
        icono = Icons.Outlined.Person
    )
)

/**
 * Barra de navegación inferior para el rol de Trabajador.
 * Contiene exactamente 4 destinos y respeta la paleta Taller de DISENO.md.
 */
@Composable
fun BarraInferiorTrabajador(
    rutaActual: String?,
    alSeleccionarRuta: (Ruta) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Arena,
        contentColor = Carbon,
        modifier = modifier
    ) {
        DESTINOS_BARRA_TRABAJADOR.forEach { elemento ->
            val seleccionado = when (elemento.ruta) {
                Ruta.MisServicios -> {
                    rutaActual == Ruta.MisServicios.ruta || rutaActual == Ruta.MisPostulaciones.ruta
                }
                Ruta.MiCuenta -> {
                    rutaActual == Ruta.MiCuenta.ruta || rutaActual == Ruta.MiPerfil.ruta
                }
                else -> {
                    rutaActual == elemento.ruta.ruta
                }
            }
            val etiqueta = stringResource(elemento.etiquetaRes)

            NavigationBarItem(
                selected = seleccionado,
                onClick = { alSeleccionarRuta(elemento.ruta) },
                icon = {
                    Icon(
                        imageVector = elemento.icono,
                        contentDescription = etiqueta
                    )
                },
                label = {
                    Text(
                        text = etiqueta,
                        style = if (seleccionado) {
                            DonChambitasTema.tipografia.cuerpoFuerte
                        } else {
                            DonChambitasTema.tipografia.pie
                        }
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
private fun PreviaBarraInferiorTrabajador() {
    DonChambitasTema {
        BarraInferiorTrabajador(
            rutaActual = Ruta.InicioTrabajador.ruta,
            alSeleccionarRuta = {}
        )
    }
}
