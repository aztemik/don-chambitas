package mx.donchambitas.app.ui.pantallas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.repositorio.RepositorioAuth
import mx.donchambitas.app.ui.navegacion.Ruta
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val repositorioAuth: RepositorioAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRegistro())
    val estado: StateFlow<EstadoRegistro> = _estado.asStateFlow()

    fun alElegirRol(rol: RolUsuario) = actualizarCaptura {
        copy(rol = rol, errorRol = null)
    }

    fun alCambiarNombre(valor: String) = actualizarCaptura {
        copy(
            nombre = valor.take(LimitesRegistro.LARGO_MAXIMO_NOMBRE),
            errorNombre = null
        )
    }

    fun alCambiarApellidos(valor: String) = actualizarCaptura {
        copy(
            apellidos = valor.take(LimitesRegistro.LARGO_MAXIMO_APELLIDOS),
            errorApellidos = null
        )
    }

    fun alCambiarCorreo(valor: String) = actualizarCaptura {
        copy(
            correo = valor.take(LimitesRegistro.LARGO_MAXIMO_CORREO),
            errorCorreo = null
        )
    }

    fun alCambiarContrasena(valor: String) = actualizarCaptura {
        copy(contrasena = valor, errorContrasena = null)
    }

    fun alCambiarTelefono(valor: String) = actualizarCaptura {
        copy(
            telefono = valor.filter(Char::isDigit).take(LimitesRegistro.LARGO_TELEFONO),
            errorTelefono = null
        )
    }

    private fun actualizarCaptura(transformar: EstadoRegistro.() -> EstadoRegistro) {
        _estado.update {
            it.transformar().copy(errorPantalla = null, mensajePantalla = null)
        }
    }

    fun alRegistrar() {
        if (_estado.value.cargando) return
        val captura = _estado.value
        val rol = captura.rol
        if (rol == null) {
            _estado.update { it.copy(errorRol = R.string.validacion_rol_sin_elegir) }
            return
        }

        _estado.update {
            it.copy(
                cargando = true,
                errorPantalla = null,
                mensajePantalla = null,
                destino = null
            )
        }
        viewModelScope.launch {
            when (
                val resultado = repositorioAuth.registrar(
                    correo = captura.correo.trim().lowercase(),
                    contrasena = captura.contrasena,
                    nombre = captura.nombre.trim(),
                    apellidos = captura.apellidos.trim(),
                    telefono = captura.telefono.trim(),
                    rol = rol
                )
            ) {
                is Resultado.Exito -> {
                    val sesion = repositorioAuth.sesionActual().first()
                    val destino = when (sesion?.usuario?.rol) {
                        RolUsuario.CLIENTE -> Ruta.InicioCliente
                        RolUsuario.TRABAJADOR -> Ruta.InicioTrabajador
                        null -> Ruta.IniciarSesion
                    }
                    _estado.update { it.copy(cargando = false, destino = destino) }
                }
                is Resultado.Error -> {
                    val tipo = resultado.tipo.paraAutenticacion()
                    _estado.update {
                        it.copy(
                            cargando = false,
                            errorPantalla = tipo,
                            mensajePantalla = resultado.mensaje.takeIf {
                                tipo == TipoError.VALIDACION
                            }
                        )
                    }
                }
            }
        }
    }

    fun alReintentar() {
        if (_estado.value.errorPantalla in ERRORES_REINTENTABLES) alRegistrar()
    }

    fun alConsumirDestino() {
        _estado.update { it.copy(destino = null) }
    }
}
