package mx.donchambitas.app.ui.pantallas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.donchambitas.app.ui.navegacion.EstadoSesionTemporal
import mx.donchambitas.app.ui.navegacion.MarcadorSesionTemporal
import mx.donchambitas.app.ui.navegacion.Ruta
import javax.inject.Inject

/**
 * ViewModel para la pantalla de bienvenida (P-01 Splash).
 * Consulta el estado de sesión y resuelve la ruta de destino garantizando
 * un tiempo mínimo de permanencia en pantalla de 800 ms para evitar parpadeos.
 */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _estado = MutableStateFlow(EstadoSplash())
    val estado: StateFlow<EstadoSplash> = _estado.asStateFlow()

    init {
        verificarSesion()
    }

    /**
     * Resuelve la ruta hacia la cual navegar en función del estado de sesión:
     * - [EstadoSesionTemporal.SIN_SESION] -> [Ruta.IniciarSesion] (P-02)
     * - [EstadoSesionTemporal.CLIENTE] -> [Ruta.InicioCliente] (P-05)
     * - [EstadoSesionTemporal.TRABAJADOR] -> [Ruta.InicioTrabajador] (P-10)
     */
    fun resolverDestino(sesion: EstadoSesionTemporal): Ruta = when (sesion) {
        EstadoSesionTemporal.SIN_SESION -> Ruta.IniciarSesion
        EstadoSesionTemporal.CLIENTE -> Ruta.InicioCliente
        EstadoSesionTemporal.TRABAJADOR -> Ruta.InicioTrabajador
    }

    /**
     * Realiza la verificación del estado de sesión con un tiempo de espera mínimo.
     * Permite inyectar parámetros para pruebas unitarias deterministas.
     */
    fun verificarSesion(
        tiempoMinimoMs: Long = TIEMPO_MINIMO_SPLASH_MS,
        proveedorSesion: () -> EstadoSesionTemporal = { MarcadorSesionTemporal.estado }
    ) {
        viewModelScope.launch {
            _estado.value = EstadoSplash(cargando = true, destino = null)
            val tiempoInicio = System.currentTimeMillis()

            val sesion = proveedorSesion()
            val destino = resolverDestino(sesion)

            val tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio
            val tiempoRestante = tiempoMinimoMs - tiempoTranscurrido
            if (tiempoRestante > 0) {
                delay(tiempoRestante)
            }

            _estado.value = EstadoSplash(
                cargando = false,
                destino = destino
            )
        }
    }

    companion object {
        const val TIEMPO_MINIMO_SPLASH_MS: Long = 800L
    }
}
