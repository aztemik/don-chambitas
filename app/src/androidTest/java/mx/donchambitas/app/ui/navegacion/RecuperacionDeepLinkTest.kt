package mx.donchambitas.app.ui.navegacion

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import mx.donchambitas.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecuperacionDeepLinkTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun debeAbrirMiCuenta_cuandoLlegaEnlaceDeRecuperacion() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("mx.donchambitas.app://auth/recuperar-contrasena"),
            contexto,
            MainActivity::class.java
        )

        ActivityScenario.launch<MainActivity>(intent).use {
            composeRule.onNodeWithText("P-18 · Mi cuenta").assertIsDisplayed()
        }
    }
}
