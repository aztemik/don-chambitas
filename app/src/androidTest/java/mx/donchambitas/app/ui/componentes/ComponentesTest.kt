package mx.donchambitas.app.ui.componentes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import mx.donchambitas.app.ui.tema.DonChambitasTema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas para componentes reutilizables de UI en Jetpack Compose.
 * Sirve como plantilla oficial para pruebas de interfaz de usuario en el proyecto.
 */
@RunWith(AndroidJUnit4::class)
class ComponentesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun debeMostrarTextoYResponderAlClic_cuandoSeUsaBotonPrincipal() {
        var clicDado = false

        composeTestRule.setContent {
            DonChambitasTema {
                BotonPrincipal(
                    texto = "Confirmar acción",
                    onClick = { clicDado = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Confirmar acción")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()

        assertTrue("El callback onClick debio ejecutarse", clicDado)
    }

    @Test
    fun debeDeshabilitarseYBloquearClic_cuandoBotonPrincipalNoEstaHabilitado() {
        var clicDado = false

        composeTestRule.setContent {
            DonChambitasTema {
                BotonPrincipal(
                    texto = "Guardando cambios",
                    habilitado = false,
                    onClick = { clicDado = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Guardando cambios")
            .assertIsNotEnabled()
            .performClick()

        org.junit.Assert.assertFalse("El callback no debio ejecutarse", clicDado)
    }

    @Test
    fun debePermitirIngresoDeTexto_cuandoSeUsaCampoTexto() {
        var textoCapturado by mutableStateOf("")

        composeTestRule.setContent {
            DonChambitasTema {
                CampoTexto(
                    valor = textoCapturado,
                    alCambiarValor = { textoCapturado = it },
                    etiqueta = "Correo electrónico"
                )
            }
        }

        composeTestRule.onNodeWithText("Correo electrónico")
            .assertIsDisplayed()
            .performTextInput("usuario@donchambitas.mx")

        assertEquals("usuario@donchambitas.mx", textoCapturado)
    }

    @Test
    fun debeMostrarTituloMensajeYEjecutarAccion_cuandoSeUsaEstadoVacio() {
        var accionPulsada = false

        composeTestRule.setContent {
            DonChambitasTema {
                EstadoVacio(
                    titulo = "Sin resultados",
                    mensaje = "No se encontraron trabajadores en esta zona",
                    textoBoton = "Limpiar filtros",
                    alHacerClickBoton = { accionPulsada = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Sin resultados").assertIsDisplayed()
        composeTestRule.onNodeWithText("No se encontraron trabajadores en esta zona").assertIsDisplayed()
        composeTestRule.onNodeWithText("Limpiar filtros")
            .assertIsDisplayed()
            .performClick()

        assertTrue("La accion del boton debio ejecutarse", accionPulsada)
    }
}
