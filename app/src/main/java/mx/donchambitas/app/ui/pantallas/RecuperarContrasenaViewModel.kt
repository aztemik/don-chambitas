package mx.donchambitas.app.ui.pantallas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.donchambitas.app.dominio.repositorio.RepositorioAuth
import mx.donchambitas.app.util.Resultado

@HiltViewModel
class RecuperarContrasenaViewModel @Inject constructor(
    private val repositorioAuth: RepositorioAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRecuperarContrasena())
    val estado: StateFlow<EstadoRecuperarContrasena> = _estado.asStateFlow()

    fun alCambiarCorreo(valor: String) {
        _estado.update {
            it.copy(correo = valor, errorCorreo = null, errorPantalla = null)
        }
    }

    fun alEnviar() {
        if (_estado.value.cargando) return

        val correo = _estado.value.correo.trim().lowercase()
        _estado.update { it.copy(cargando = true, errorPantalla = null) }
        viewModelScope.launch {
            when (val resultado = repositorioAuth.recuperarContrasena(correo)) {
                is Resultado.Exito -> {
                    _estado.update { it.copy(cargando = false, enviado = true) }
                }
                is Resultado.Error -> {
                    _estado.update {
                        it.copy(
                            cargando = false,
                            errorPantalla = resultado.tipo.paraAutenticacion()
                        )
                    }
                }
            }
        }
    }

    fun alReintentar() {
        if (_estado.value.errorPantalla in ERRORES_REINTENTABLES) alEnviar()
    }
}
