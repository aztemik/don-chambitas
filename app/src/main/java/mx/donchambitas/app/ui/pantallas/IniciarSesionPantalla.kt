package mx.donchambitas.app.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.donchambitas.app.R
import mx.donchambitas.app.ui.componentes.BarraSuperior
import mx.donchambitas.app.ui.componentes.BotonPrincipal
import mx.donchambitas.app.ui.componentes.BotonTexto
import mx.donchambitas.app.ui.componentes.CampoContrasena
import mx.donchambitas.app.ui.componentes.CampoTexto
import mx.donchambitas.app.ui.componentes.EstadoError
import mx.donchambitas.app.ui.tema.Carbon
import mx.donchambitas.app.ui.tema.Crema
import mx.donchambitas.app.ui.tema.DonChambitasTema
import mx.donchambitas.app.ui.tema.Espaciado
import mx.donchambitas.app.ui.tema.titulo
import mx.donchambitas.app.util.TipoError

/** Lado del isotipo del casco en P-02. En P-01 mide 130 dp; aqui comparte espacio con el formulario. */
private val LadoIsotipo = 72.dp

/** Alto minimo tactil de los enlaces de navegacion, por 1.8 de DISENO-AUTENTICACION.md. */
private val AltoMinimoEnlace = 48.dp

/**
 * Conserva lo capturado al girar el dispositivo. Solo se guardan los dos
 * valores de captura: los errores, el indicador de carga y el destino se
 * vuelven a calcular. Guardar el destino haria que la pantalla navegara otra
 * vez al restaurarse, que es justo lo que evita consumirlo.
 */
private val GuardaEstadoIniciarSesion = listSaver<EstadoIniciarSesion, Any?>(
    save = { listOf(it.correo, it.contrasena) },
    restore = {
        EstadoIniciarSesion(
            correo = it[0] as String,
            contrasena = it[1] as String
        )
    }
)

/**
 * Pantalla de inicio de sesion (P-02).
 * Especificada en docs/producto/DISENO-AUTENTICACION.md, seccion 2.
 *
 * El estado vive aqui de forma provisional hasta que S2-T05 traiga
 * IniciarSesionViewModel: esta tarea entrega la pantalla, no el ViewModel ni
 * las reglas de validacion, que son S2-T04.
 *
 * @param alIniciarSesion Se invoca con el correo ya normalizado cuando el formulario se envia.
 * @param alIrARegistro Lleva a P-03. No pasa por el estado: no hay nada que decidir.
 * @param alIrARecuperarContrasena Lleva a P-04, por la misma razon.
 */
@Composable
fun IniciarSesionPantalla(
    alIniciarSesion: (String) -> Unit,
    alIrARegistro: () -> Unit,
    alIrARecuperarContrasena: () -> Unit,
    modifier: Modifier = Modifier
) {
    var estado by rememberSaveable(stateSaver = GuardaEstadoIniciarSesion) {
        mutableStateOf(EstadoIniciarSesion())
    }

    IniciarSesionContenido(
        estado = estado,
        alCambiarCorreo = { valor ->
            estado = estado.copy(correo = valor, errorCorreo = null)
        },
        alCambiarContrasena = { valor ->
            estado = estado.copy(contrasena = valor, errorContrasena = null)
        },
        alPerderFocoCorreo = {
            estado = estado.copy(errorCorreo = validarCorreo(estado.correo))
        },
        alPerderFocoContrasena = {
            estado = estado.copy(
                errorContrasena = validarContrasenaInicio(estado.contrasena)
            )
        },
        alIniciarSesion = {
            val errorCorreo = validarCorreo(estado.correo)
            val errorContrasena = validarContrasenaInicio(estado.contrasena)
            estado = estado.copy(
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorPantalla = null
            )

            when {
                errorCorreo != null -> CampoAutenticacion.CORREO
                errorContrasena != null -> CampoAutenticacion.CONTRASENA
                else -> {
                    // La normalizacion ocurre solo al enviar; en pantalla se
                    // conserva exactamente lo que escribio el usuario.
                    alIniciarSesion(estado.correo.trim().lowercase())
                    null
                }
            }
        },
        alIrARegistro = alIrARegistro,
        alIrARecuperarContrasena = alIrARecuperarContrasena,
        modifier = modifier
    )
}

/**
 * Contenido visual puro de P-02, sin estado propio, para previsualizarlo y
 * probarlo con cualquier combinacion de valores y errores.
 */
@Composable
fun IniciarSesionContenido(
    estado: EstadoIniciarSesion,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alIniciarSesion: () -> CampoAutenticacion?,
    alIrARegistro: () -> Unit,
    alIrARecuperarContrasena: () -> Unit,
    modifier: Modifier = Modifier,
    alReintentar: (() -> Unit)? = null,
    alPerderFocoCorreo: () -> Unit = {},
    alPerderFocoContrasena: () -> Unit = {}
) {
    val administradorFoco = LocalFocusManager.current
    val focoCorreo = remember { FocusRequester() }
    val focoContrasena = remember { FocusRequester() }

    fun enviar() {
        when (alIniciarSesion()) {
            CampoAutenticacion.CORREO -> focoCorreo.requestFocus()
            CampoAutenticacion.CONTRASENA -> focoContrasena.requestFocus()
            else -> Unit
        }
    }

    Scaffold(
        // Sin flecha de regreso: P-02 es la raiz del subgrafo. P-01 sale de la
        // pila en S1-T15, asi que no hay a donde volver.
        topBar = { BarraSuperior(titulo = stringResource(R.string.iniciar_sesion_titulo)) },
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
                )
        ) {
            BloqueDeMarca()

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
                tecladoAcciones = KeyboardActions(
                    onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
                ),
                alPerderFoco = alPerderFocoCorreo,
                modifier = Modifier
                    .focusRequester(focoCorreo)
                    .padding(bottom = Espaciado.dp16)
            )

            CampoContrasena(
                valor = estado.contrasena,
                alCambiarValor = alCambiarContrasena,
                etiqueta = stringResource(R.string.auth_contrasena),
                error = estado.errorContrasena?.let { stringResource(it) },
                habilitado = !estado.cargando,
                tecladoOpciones = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                tecladoAcciones = KeyboardActions(
                    onDone = {
                        administradorFoco.clearFocus()
                        if (!estado.cargando) enviar()
                    }
                ),
                alPerderFoco = alPerderFocoContrasena,
                modifier = Modifier.focusRequester(focoContrasena)
            )

            BotonTexto(
                texto = stringResource(R.string.iniciar_sesion_olvide),
                onClick = alIrARecuperarContrasena,
                habilitado = !estado.cargando,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = Espaciado.dp8)
                    .defaultMinSize(minHeight = AltoMinimoEnlace)
            )

            ErrorDeInicioSesion(
                tipoError = estado.errorPantalla,
                alReintentar = alReintentar,
                modifier = Modifier.padding(vertical = Espaciado.dp16)
            )

            BotonPrincipal(
                texto = stringResource(R.string.iniciar_sesion_accion),
                onClick = ::enviar,
                cargando = estado.cargando,
                modifier = Modifier.fillMaxWidth()
            )

            BotonTexto(
                texto = stringResource(R.string.iniciar_sesion_ir_registro),
                onClick = alIrARegistro,
                habilitado = !estado.cargando,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Espaciado.dp24)
                    .defaultMinSize(minHeight = AltoMinimoEnlace)
            )
        }
    }
}

/**
 * Isotipo del casco y nombre de la aplicacion. Reutiliza el mismo vector de
 * P-01, no es un recurso nuevo.
 */
@Composable
private fun BloqueDeMarca(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Espaciado.dp32, bottom = Espaciado.dp24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Espaciado.dp8)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.splash_logo_descripcion),
            modifier = Modifier.size(LadoIsotipo)
        )
        Text(
            text = stringResource(id = R.string.splash_nombre_app),
            style = DonChambitasTema.tipografia.titulo,
            color = Carbon,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Error que no pertenece a ningun campo, pintado en linea sobre el boton y sin
 * tapar el formulario. El mapeo de TipoError es el de S1-T11; la unica
 * sustitucion es AUTENTICACION, cuyo mensaje generico ("Inicia sesion de nuevo
 * para continuar") no dice nada util justo en la pantalla de inicio de sesion.
 */
@Composable
private fun ErrorDeInicioSesion(
    tipoError: TipoError?,
    alReintentar: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (tipoError == null) return

    // Reintentar solo donde volver a intentar puede servir de algo. En
    // AUTENTICACION y VALIDACION lo que hay que cambiar es lo que esta escrito.
    val reintento = alReintentar.takeIf {
        tipoError in setOf(TipoError.RED, TipoError.SERVIDOR, TipoError.DESCONOCIDO)
    }

    EstadoError(
        tipoError = tipoError,
        alReintentar = reintento,
        mensajePersonalizado = if (tipoError == TipoError.AUTENTICACION) {
            stringResource(R.string.error_credenciales_invalidas)
        } else {
            null
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-02 vacío")
@Composable
private fun IniciarSesionVacioPreview() {
    DonChambitasTema {
        IniciarSesionContenido(
            estado = EstadoIniciarSesion(),
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alIniciarSesion = { null },
            alIrARegistro = {},
            alIrARecuperarContrasena = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-02 con errores")
@Composable
private fun IniciarSesionConErroresPreview() {
    DonChambitasTema {
        IniciarSesionContenido(
            estado = EstadoIniciarSesion(
                correo = "refugio@",
                errorCorreo = R.string.validacion_correo_formato,
                errorContrasena = R.string.validacion_contrasena_corta
            ),
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alIniciarSesion = { null },
            alIrARegistro = {},
            alIrARecuperarContrasena = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-02 cargando")
@Composable
private fun IniciarSesionCargandoPreview() {
    DonChambitasTema {
        IniciarSesionContenido(
            estado = EstadoIniciarSesion(
                correo = "refugio@ejemplo.mx",
                contrasena = "12345678",
                cargando = true
            ),
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alIniciarSesion = { null },
            alIrARegistro = {},
            alIrARecuperarContrasena = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "P-02 credenciales rechazadas")
@Composable
private fun IniciarSesionCredencialesRechazadasPreview() {
    DonChambitasTema {
        IniciarSesionContenido(
            estado = EstadoIniciarSesion(
                correo = "refugio@ejemplo.mx",
                contrasena = "12345678",
                errorPantalla = TipoError.AUTENTICACION
            ),
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alIniciarSesion = { null },
            alIrARegistro = {},
            alIrARecuperarContrasena = {}
        )
    }
}
