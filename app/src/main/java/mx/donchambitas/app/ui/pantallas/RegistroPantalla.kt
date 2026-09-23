package mx.donchambitas.app.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.ui.componentes.BarraSuperior
import mx.donchambitas.app.ui.componentes.BotonPrincipal
import mx.donchambitas.app.ui.componentes.BotonTexto
import mx.donchambitas.app.ui.componentes.CampoContrasena
import mx.donchambitas.app.ui.componentes.CampoTexto
import mx.donchambitas.app.ui.componentes.ChipCategoria
import mx.donchambitas.app.ui.componentes.EstadoError
import mx.donchambitas.app.ui.tema.Cafe
import mx.donchambitas.app.ui.tema.Carbon
import mx.donchambitas.app.ui.tema.Crema
import mx.donchambitas.app.ui.tema.DonChambitasTema
import mx.donchambitas.app.ui.tema.Error
import mx.donchambitas.app.ui.tema.Espaciado
import mx.donchambitas.app.ui.tema.cuerpo
import mx.donchambitas.app.ui.tema.pie
import mx.donchambitas.app.ui.tema.subtitulo
import mx.donchambitas.app.util.TipoError

/**
 * Conserva lo capturado al girar el dispositivo. Solo se guardan los seis
 * valores de captura y el rol: los errores y el indicador de carga se vuelven
 * a calcular, guardarlos mostraria un error viejo sobre un formulario nuevo.
 */
private val GuardaEstadoRegistro = listSaver<EstadoRegistro, Any?>(
    save = {
        listOf(it.rol?.valor, it.nombre, it.apellidos, it.correo, it.contrasena, it.telefono)
    },
    restore = {
        EstadoRegistro(
            rol = (it[0] as String?)?.let(RolUsuario::desdeValor),
            nombre = it[1] as String,
            apellidos = it[2] as String,
            correo = it[3] as String,
            contrasena = it[4] as String,
            telefono = it[5] as String
        )
    }
)

/**
 * Pantalla de registro con seleccion de rol (P-03).
 * Especificada en docs/producto/DISENO-AUTENTICACION.md, seccion 3.
 *
 * El estado vive aqui de forma provisional hasta que S2-T05 traiga
 * RegistroViewModel: esta tarea entrega la pantalla, no el ViewModel ni las
 * reglas de validacion, que son S2-T04.
 *
 * @param alRegistrarConRol Se invoca con el rol elegido cuando el formulario se envia.
 * @param alRegresar Regresa a P-02 descartando lo capturado.
 * @param alIrAIniciarSesion Lleva a P-02 desde el pie de la pantalla.
 */
@Composable
fun RegistroPantalla(
    alRegistrarConRol: (RolUsuario) -> Unit,
    alRegresar: () -> Unit,
    alIrAIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var estado by rememberSaveable(stateSaver = GuardaEstadoRegistro) {
        mutableStateOf(EstadoRegistro())
    }

    RegistroContenido(
        estado = estado,
        alElegirRol = { rol -> estado = estado.copy(rol = rol, errorRol = null) },
        alCambiarNombre = { valor ->
            estado = estado.copy(
                nombre = valor.take(LimitesRegistro.LARGO_MAXIMO_NOMBRE),
                errorNombre = null
            )
        },
        alCambiarApellidos = { valor ->
            estado = estado.copy(
                apellidos = valor.take(LimitesRegistro.LARGO_MAXIMO_APELLIDOS),
                errorApellidos = null
            )
        },
        alCambiarCorreo = { valor ->
            estado = estado.copy(
                correo = valor.take(LimitesRegistro.LARGO_MAXIMO_CORREO),
                errorCorreo = null
            )
        },
        alCambiarContrasena = { valor ->
            estado = estado.copy(contrasena = valor, errorContrasena = null)
        },
        alCambiarTelefono = { valor ->
            // Se filtra al escribir en vez de validarse despues: no es una regla
            // de negocio, es no dejar teclear lo que el campo no admite.
            estado = estado.copy(
                telefono = valor.filter(Char::isDigit).take(LimitesRegistro.LARGO_TELEFONO),
                errorTelefono = null
            )
        },
        alPerderFocoRol = {
            estado = estado.copy(errorRol = validarRol(estado.rol))
        },
        alPerderFocoNombre = {
            estado = estado.copy(errorNombre = validarNombre(estado.nombre))
        },
        alPerderFocoApellidos = {
            estado = estado.copy(errorApellidos = validarApellidos(estado.apellidos))
        },
        alPerderFocoCorreo = {
            estado = estado.copy(errorCorreo = validarCorreo(estado.correo))
        },
        alPerderFocoContrasena = {
            estado = estado.copy(
                errorContrasena = validarContrasenaRegistro(estado.contrasena)
            )
        },
        alPerderFocoTelefono = {
            estado = estado.copy(errorTelefono = validarTelefono(estado.telefono))
        },
        alRegistrar = {
            val errorRol = validarRol(estado.rol)
            val errorNombre = validarNombre(estado.nombre)
            val errorApellidos = validarApellidos(estado.apellidos)
            val errorCorreo = validarCorreo(estado.correo)
            val errorContrasena = validarContrasenaRegistro(estado.contrasena)
            val errorTelefono = validarTelefono(estado.telefono)
            estado = estado.copy(
                errorRol = errorRol,
                errorNombre = errorNombre,
                errorApellidos = errorApellidos,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorTelefono = errorTelefono,
                errorPantalla = null,
                mensajePantalla = null
            )

            when {
                errorRol != null -> CampoAutenticacion.ROL
                errorNombre != null -> CampoAutenticacion.NOMBRE
                errorApellidos != null -> CampoAutenticacion.APELLIDOS
                errorCorreo != null -> CampoAutenticacion.CORREO
                errorContrasena != null -> CampoAutenticacion.CONTRASENA
                errorTelefono != null -> CampoAutenticacion.TELEFONO
                else -> {
                    alRegistrarConRol(requireNotNull(estado.rol))
                    null
                }
            }
        },
        alRegresar = alRegresar,
        alIrAIniciarSesion = alIrAIniciarSesion,
        modifier = modifier
    )
}

/**
 * Contenido visual puro de P-03, sin estado propio, para previsualizarlo y
 * probarlo con cualquier combinacion de valores y errores.
 */
@Composable
fun RegistroContenido(
    estado: EstadoRegistro,
    alElegirRol: (RolUsuario) -> Unit,
    alCambiarNombre: (String) -> Unit,
    alCambiarApellidos: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alCambiarTelefono: (String) -> Unit,
    alRegistrar: () -> CampoAutenticacion?,
    alRegresar: () -> Unit,
    alIrAIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    alReintentar: (() -> Unit)? = null,
    alPerderFocoRol: () -> Unit = {},
    alPerderFocoNombre: () -> Unit = {},
    alPerderFocoApellidos: () -> Unit = {},
    alPerderFocoCorreo: () -> Unit = {},
    alPerderFocoContrasena: () -> Unit = {},
    alPerderFocoTelefono: () -> Unit = {}
) {
    val administradorFoco = LocalFocusManager.current
    val focoRol = remember { FocusRequester() }
    val focoNombre = remember { FocusRequester() }
    val focoApellidos = remember { FocusRequester() }
    val focoCorreo = remember { FocusRequester() }
    val focoContrasena = remember { FocusRequester() }
    val focoTelefono = remember { FocusRequester() }
    val siguienteCampo = KeyboardActions(
        onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
    )

    fun enviar() {
        when (alRegistrar()) {
            CampoAutenticacion.ROL -> focoRol.requestFocus()
            CampoAutenticacion.NOMBRE -> focoNombre.requestFocus()
            CampoAutenticacion.APELLIDOS -> focoApellidos.requestFocus()
            CampoAutenticacion.CORREO -> focoCorreo.requestFocus()
            CampoAutenticacion.CONTRASENA -> focoContrasena.requestFocus()
            CampoAutenticacion.TELEFONO -> focoTelefono.requestFocus()
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.registro_titulo),
                alRegresar = alRegresar
            )
        },
        containerColor = Crema,
        modifier = modifier.fillMaxSize()
    ) { relleno ->
        Column(
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(
                    horizontal = Espaciado.margenPantalla,
                    vertical = Espaciado.dp24
                ),
            verticalArrangement = Arrangement.spacedBy(Espaciado.dp16)
        ) {
            SelectorRol(
                rol = estado.rol,
                errorRol = estado.errorRol,
                habilitado = !estado.cargando,
                alElegirRol = alElegirRol,
                alPerderFoco = alPerderFocoRol,
                focoPrimerRol = focoRol
            )

            CampoTexto(
                valor = estado.nombre,
                alCambiarValor = alCambiarNombre,
                etiqueta = stringResource(R.string.registro_nombre),
                error = estado.errorNombre?.let { stringResource(it) },
                habilitado = !estado.cargando,
                tecladoOpciones = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                tecladoAcciones = siguienteCampo,
                alPerderFoco = alPerderFocoNombre,
                modifier = Modifier.focusRequester(focoNombre)
            )

            CampoTexto(
                valor = estado.apellidos,
                alCambiarValor = alCambiarApellidos,
                etiqueta = stringResource(R.string.registro_apellidos),
                error = estado.errorApellidos?.let { stringResource(it) },
                habilitado = !estado.cargando,
                tecladoOpciones = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                tecladoAcciones = siguienteCampo,
                alPerderFoco = alPerderFocoApellidos,
                modifier = Modifier.focusRequester(focoApellidos)
            )

            CampoTexto(
                valor = estado.correo,
                alCambiarValor = alCambiarCorreo,
                etiqueta = stringResource(R.string.auth_correo),
                error = estado.errorCorreo?.let { stringResource(it) },
                habilitado = !estado.cargando,
                tecladoOpciones = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                tecladoAcciones = siguienteCampo,
                alPerderFoco = alPerderFocoCorreo,
                modifier = Modifier.focusRequester(focoCorreo)
            )

            Column(verticalArrangement = Arrangement.spacedBy(Espaciado.dp4)) {
                CampoContrasena(
                    valor = estado.contrasena,
                    alCambiarValor = alCambiarContrasena,
                    etiqueta = stringResource(R.string.auth_contrasena),
                    error = estado.errorContrasena?.let { stringResource(it) },
                    habilitado = !estado.cargando,
                    tecladoOpciones = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    tecladoAcciones = siguienteCampo,
                    alPerderFoco = alPerderFocoContrasena,
                    modifier = Modifier.focusRequester(focoContrasena)
                )
                Text(
                    text = stringResource(R.string.registro_ayuda_contrasena),
                    style = DonChambitasTema.tipografia.pie,
                    color = Cafe
                )
            }

            CampoTexto(
                valor = estado.telefono,
                alCambiarValor = alCambiarTelefono,
                etiqueta = stringResource(R.string.registro_telefono),
                error = estado.errorTelefono?.let { stringResource(it) },
                habilitado = !estado.cargando,
                tecladoOpciones = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                tecladoAcciones = KeyboardActions(
                    onDone = {
                        administradorFoco.clearFocus()
                        if (!estado.cargando) enviar()
                    }
                ),
                alPerderFoco = alPerderFocoTelefono,
                modifier = Modifier.focusRequester(focoTelefono)
            )

            ErrorDePantalla(
                tipoError = estado.errorPantalla,
                mensajePantalla = estado.mensajePantalla,
                alReintentar = alReintentar
            )

            BotonPrincipal(
                texto = stringResource(R.string.registro_accion),
                onClick = ::enviar,
                cargando = estado.cargando,
                modifier = Modifier.fillMaxWidth()
            )

            BotonTexto(
                texto = stringResource(R.string.registro_ir_iniciar_sesion),
                onClick = alIrAIniciarSesion,
                habilitado = !estado.cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Selector de rol unico. No trae ninguno preseleccionado a proposito: por
 * DEC-22 el rol es permanente, asi que la aplicacion no lo elige por el
 * usuario.
 */
@Composable
private fun SelectorRol(
    rol: RolUsuario?,
    errorRol: Int?,
    habilitado: Boolean,
    alElegirRol: (RolUsuario) -> Unit,
    alPerderFoco: () -> Unit,
    focoPrimerRol: FocusRequester,
    modifier: Modifier = Modifier
) {
    var teniaFoco by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Espaciado.dp8)
    ) {
        Text(
            text = stringResource(R.string.registro_pregunta_rol),
            style = DonChambitasTema.tipografia.subtitulo,
            color = Carbon
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .focusGroup()
                .onFocusChanged { estadoFoco ->
                    if (teniaFoco && !estadoFoco.hasFocus) alPerderFoco()
                    teniaFoco = estadoFoco.hasFocus
                }
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(Espaciado.dp12)
        ) {
            OpcionRol(
                texto = stringResource(R.string.registro_rol_cliente),
                seleccionado = rol == RolUsuario.CLIENTE,
                habilitado = habilitado,
                alSeleccionar = { alElegirRol(RolUsuario.CLIENTE) },
                modifier = Modifier
                    .focusRequester(focoPrimerRol)
                    .weight(1f)
            )
            OpcionRol(
                texto = stringResource(R.string.registro_rol_trabajador),
                seleccionado = rol == RolUsuario.TRABAJADOR,
                habilitado = habilitado,
                alSeleccionar = { alElegirRol(RolUsuario.TRABAJADOR) },
                modifier = Modifier.weight(1f)
            )
        }

        if (errorRol != null) {
            Text(
                text = stringResource(errorRol),
                style = DonChambitasTema.tipografia.pie,
                color = Error
            )
        }

        Text(
            text = stringResource(R.string.registro_rol_definitivo),
            style = DonChambitasTema.tipografia.pie,
            color = Cafe
        )
    }
}

/**
 * Un chip de rol. La semantica va sobre el chip, no sobre una envoltura: un
 * nodo que fusiona a sus descendientes no absorbe a otro que tambien fusiona,
 * y el chip es uno de esos. Asi el lector de pantalla anuncia la opcion como
 * seleccionada y no solo como boton.
 */
@Composable
private fun OpcionRol(
    texto: String,
    seleccionado: Boolean,
    habilitado: Boolean,
    alSeleccionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChipCategoria(
        texto = texto,
        seleccionado = seleccionado,
        alSeleccionar = { if (habilitado) alSeleccionar() },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.role = Role.RadioButton
                this.selected = seleccionado
            }
    )
}

/**
 * Error que no pertenece a ningun campo. Se pinta en linea, encima del boton
 * y sin tapar el formulario: lo que el usuario escribio sigue ahi.
 */
@Composable
private fun ErrorDePantalla(
    tipoError: TipoError?,
    mensajePantalla: String?,
    alReintentar: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (tipoError == null) return

    // Reintentar solo donde volver a intentar puede servir de algo. En
    // VALIDACION lo que hay que cambiar es lo que esta escrito.
    val reintento = alReintentar.takeIf {
        tipoError in setOf(TipoError.RED, TipoError.SERVIDOR, TipoError.DESCONOCIDO)
    }

    if (mensajePantalla != null) {
        EstadoError(
            mensaje = mensajePantalla,
            alReintentar = reintento,
            modifier = modifier
        )
    } else {
        EstadoError(
            tipoError = tipoError,
            alReintentar = reintento,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-03 vacío")
@Composable
private fun RegistroVacioPreview() {
    DonChambitasTema {
        RegistroContenido(
            estado = EstadoRegistro(),
            alElegirRol = {},
            alCambiarNombre = {},
            alCambiarApellidos = {},
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alCambiarTelefono = {},
            alRegistrar = { null },
            alRegresar = {},
            alIrAIniciarSesion = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-03 con errores")
@Composable
private fun RegistroConErroresPreview() {
    DonChambitasTema {
        RegistroContenido(
            estado = EstadoRegistro(
                nombre = "Refugio",
                correo = "refugio@",
                telefono = "477123",
                errorRol = R.string.validacion_rol_sin_elegir,
                errorApellidos = R.string.validacion_apellidos_vacio,
                errorCorreo = R.string.validacion_correo_formato,
                errorContrasena = R.string.validacion_contrasena_corta,
                errorTelefono = R.string.validacion_telefono_digitos
            ),
            alElegirRol = {},
            alCambiarNombre = {},
            alCambiarApellidos = {},
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alCambiarTelefono = {},
            alRegistrar = { null },
            alRegresar = {},
            alIrAIniciarSesion = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-03 cargando")
@Composable
private fun RegistroCargandoPreview() {
    DonChambitasTema {
        RegistroContenido(
            estado = EstadoRegistro(
                rol = RolUsuario.TRABAJADOR,
                nombre = "Refugio",
                apellidos = "Martínez Luna",
                correo = "refugio@ejemplo.mx",
                contrasena = "12345678",
                telefono = "4771234567",
                cargando = true
            ),
            alElegirRol = {},
            alCambiarNombre = {},
            alCambiarApellidos = {},
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alCambiarTelefono = {},
            alRegistrar = { null },
            alRegresar = {},
            alIrAIniciarSesion = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-03 correo duplicado")
@Composable
private fun RegistroCorreoDuplicadoPreview() {
    DonChambitasTema {
        RegistroContenido(
            estado = EstadoRegistro(
                rol = RolUsuario.CLIENTE,
                nombre = "Refugio",
                apellidos = "Martínez Luna",
                correo = "refugio@ejemplo.mx",
                contrasena = "12345678",
                telefono = "4771234567",
                errorPantalla = TipoError.VALIDACION,
                mensajePantalla = "El correo ya está registrado, inicia sesión"
            ),
            alElegirRol = {},
            alCambiarNombre = {},
            alCambiarApellidos = {},
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alCambiarTelefono = {},
            alRegistrar = { null },
            alRegresar = {},
            alIrAIniciarSesion = {}
        )
    }
}
