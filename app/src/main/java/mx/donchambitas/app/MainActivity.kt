package mx.donchambitas.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject
import mx.donchambitas.app.datos.repositorio.URL_RECUPERACION
import mx.donchambitas.app.ui.navegacion.GrafoNavegacion
import mx.donchambitas.app.ui.tema.DonChambitasTema

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabase: SupabaseClient

    private var esRecuperacionContrasena by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        procesarIntent(intent)
        setContent {
            DonChambitasTema {
                GrafoNavegacion(
                    esRecuperacionContrasena = esRecuperacionContrasena
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntent(intent)
    }

    private fun procesarIntent(intent: Intent) {
        supabase.handleDeeplinks(intent)
        esRecuperacionContrasena = intent.data?.toString()
            ?.substringBefore('#')
            ?.substringBefore('?') == URL_RECUPERACION
    }
}
