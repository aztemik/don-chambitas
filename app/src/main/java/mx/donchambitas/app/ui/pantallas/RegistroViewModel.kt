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
import mx.donchambitas.app.dominio.validacion.LimitesRegistro
import mx.donchambitas.app.dominio.validacion.ValidacionesAuth
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

/**
 * ViewModel de la pantalla de registro (P-03).
 * Contrato de estado y eventos en docs/producto/DISENO-AUTENTICACION.md,
 * secciones 3.3 a 3.5.
 */
@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val repositorioAuth: RepositorioAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRegistro())
    val estado: StateFlow<EstadoRegistro> = _estado.asStateFlow()

    // Solo se valida al salir de un campo en el que ya se escribio: pasar por
    // uno vacio camino a otro no es un error todavia (1.5).
    private var tocados = emptySet<CampoRegistro>()

    fun alElegirRol(rol: RolUsuario) {
        _estado.update { it.copy(rol = rol, errorRol = null) }
    }

    fun alCambiarNombre(valor: String) {
        tocados = tocados + CampoRegistro.NOMBRE
        _estado.update {
            it.copy(nombre = valor.take(LimitesRegistro.LARGO_MAXIMO_NOMBRE), errorNombre = null)
        }
    }

    fun alCambiarApellidos(valor: String) {
        tocados = tocados + CampoRegistro.APELLIDOS
        _estado.update {
            it.copy(apellidos = valor.take(LimitesRegistro.LARGO_MAXIMO_APELLIDOS), errorApellidos = null)
        }
    }

    fun alCambiarCorreo(valor: String) {
        tocados = tocados + CampoRegistro.CORREO
        _estado.update {
            it.copy(correo = valor.take(LimitesRegistro.LARGO_MAXIMO_CORREO), errorCorreo = null)
        }
    }

    fun alCambiarContrasena(valor: String) {
        tocados = tocados + CampoRegistro.CONTRASENA
        _estado.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    /**
     * Se filtra al escribir en vez de validarse despues: no es una regla de
     * negocio, es no dejar teclear lo que el campo no admite.
     */
    fun alCambiarTelefono(valor: String) {
        tocados = tocados + CampoRegistro.TELEFONO
        _estado.update {
            it.copy(
                telefono = valor.filter(Char::isDigit).take(LimitesRegistro.LARGO_TELEFONO),
                errorTelefono = null
            )
        }
    }

    fun alPerderFoco(campo: CampoRegistro) {
        if (campo in tocados) _estado.update { it.validado(campo) }
    }

    fun alRegistrar() {
        if (_estado.value.cargando) return
        val inicial = _estado.value.copy(
            errorPantalla = null,
            mensajePantalla = null,
            errorRol = ValidacionesAuth.validarRol(_estado.value.rol)?.mensaje()
        )
        val validado = CampoRegistro.entries.fold(inicial) { parcial, campo -> parcial.validado(campo) }
        val rol = validado.rol
        if (rol == null || !validado.sinErrores()) {
            _estado.value = validado
            return
        }
        _estado.value = validado.copy(cargando = true)

        viewModelScope.launch {
            // Normalizacion de 1.6: el correo en minusculas por
            // ck_usuario_correo_minusculas. El telefono ya llega solo con
            // digitos porque asi se captura.
            val resultado = repositorioAuth.registrar(
                correo = validado.correo.trim().lowercase(),
                contrasena = validado.contrasena,
                nombre = validado.nombre.trim(),
                apellidos = validado.apellidos.trim(),
                telefono = validado.telefono,
                rol = rol
            )
            _estado.update {
                when (resultado) {
                    // DEC-25: el registro deja la sesion abierta, asi que se
                    // entra directo a la pantalla del rol.
                    is Resultado.Exito -> it.copy(
                        cargando = false,
                        destino = inicioDelRol(resultado.dato.usuario.rol)
                    )
                    // En VALIDACION el mensaje se muestra tal cual (3.3). De
                    // donde sale ese texto en el alta real esta pendiente:
                    // H-11 en CONTRATOS-API.md.
                    is Resultado.Error -> it.copy(
                        cargando = false,
                        errorPantalla = resultado.tipo,
                        mensajePantalla = resultado.mensaje.takeIf { resultado.tipo == TipoError.VALIDACION }
                    )
                }
            }
        }
    }

    fun alReintentar() {
        _estado.update { it.copy(errorPantalla = null, mensajePantalla = null) }
        alRegistrar()
    }

    fun alConsumirDestino() {
        _estado.update { it.copy(destino = null) }
    }
}

private fun EstadoRegistro.validado(campo: CampoRegistro): EstadoRegistro = when (campo) {
    CampoRegistro.NOMBRE ->
        copy(errorNombre = ValidacionesAuth.validarNombre(nombre)?.mensaje())
    CampoRegistro.APELLIDOS ->
        copy(errorApellidos = ValidacionesAuth.validarApellidos(apellidos)?.mensaje())
    CampoRegistro.CORREO ->
        copy(errorCorreo = ValidacionesAuth.validarCorreo(correo)?.mensaje())
    CampoRegistro.CONTRASENA ->
        copy(errorContrasena = ValidacionesAuth.validarContrasenaRegistro(contrasena)?.mensaje())
    CampoRegistro.TELEFONO ->
        copy(errorTelefono = ValidacionesAuth.validarTelefono(telefono)?.mensaje())
}

private fun EstadoRegistro.sinErrores(): Boolean =
    listOf(errorRol, errorNombre, errorApellidos, errorCorreo, errorContrasena, errorTelefono)
        .all { it == null }
