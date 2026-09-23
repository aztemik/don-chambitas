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
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.repositorio.RepositorioAuth
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

@HiltViewModel
class IniciarSesionViewModel @Inject constructor(
    private val repositorioAuth: RepositorioAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoIniciarSesion())
    val estado: StateFlow<EstadoIniciarSesion> = _estado.asStateFlow()

    fun alCambiarCorreo(valor: String) {
        _estado.update {
            it.copy(correo = valor, errorCorreo = null, errorPantalla = null)
        }
    }

    fun alCambiarContrasena(valor: String) {
        _estado.update {
            it.copy(contrasena = valor, errorContrasena = null, errorPantalla = null)
        }
    }

    fun alIniciarSesion() {
        if (_estado.value.cargando) return

        val correo = _estado.value.correo.trim().lowercase()
        val contrasena = _estado.value.contrasena
        _estado.update { it.copy(cargando = true, errorPantalla = null, destino = null) }
        viewModelScope.launch {
            when (val resultado = repositorioAuth.iniciarSesion(correo, contrasena)) {
                is Resultado.Exito -> {
                    val destino = when (resultado.dato.usuario.rol) {
                        RolUsuario.CLIENTE -> Ruta.InicioCliente
                        RolUsuario.TRABAJADOR -> Ruta.InicioTrabajador
                    }
                    _estado.update { it.copy(cargando = false, destino = destino) }
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
        if (_estado.value.errorPantalla in ERRORES_REINTENTABLES) alIniciarSesion()
    }

    fun alConsumirDestino() {
        _estado.update { it.copy(destino = null) }
    }
}

internal val ERRORES_REINTENTABLES = setOf(
    TipoError.RED,
    TipoError.SERVIDOR,
    TipoError.DESCONOCIDO
)

internal fun TipoError.paraAutenticacion(): TipoError =
    if (this == TipoError.LIMITE_IA) TipoError.DESCONOCIDO else this
