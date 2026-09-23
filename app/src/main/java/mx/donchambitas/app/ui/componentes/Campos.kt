package mx.donchambitas.app.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.donchambitas.app.R
import mx.donchambitas.app.ui.tema.Arena
import mx.donchambitas.app.ui.tema.Borde
import mx.donchambitas.app.ui.tema.Cafe
import mx.donchambitas.app.ui.tema.Carbon
import mx.donchambitas.app.ui.tema.DonChambitasTema
import mx.donchambitas.app.ui.tema.Error
import mx.donchambitas.app.ui.tema.FormaCampo
import mx.donchambitas.app.ui.tema.MostazaOscuro
import mx.donchambitas.app.ui.tema.cuerpo
import mx.donchambitas.app.ui.tema.pie
import mx.donchambitas.app.ui.tema.secundario

private val EspacioTextoError = 4.dp

/**
 * Campo de texto reutilizable segun DISENO.md.
 * Fondo Arena, contorno Borde en reposo, Mostaza Oscuro al enfocar y Error cuando hay fallo.
 */
@Composable
fun CampoTexto(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String? = null,
    marcadorPosicion: String? = null,
    error: String? = null,
    habilitado: Boolean = true,
    soloLectura: Boolean = false,
    iconoInicio: (@Composable () -> Unit)? = null,
    iconoFin: (@Composable () -> Unit)? = null,
    tecladoOpciones: KeyboardOptions = KeyboardOptions.Default,
    tecladoAcciones: KeyboardActions = KeyboardActions.Default,
    alPerderFoco: () -> Unit = {},
    lineasMaximas: Int = 1,
    lineasMinimas: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier
) {
    val hayError = !error.isNullOrBlank()
    var teniaFoco by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiarValor,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { estadoFoco ->
                    if (teniaFoco && !estadoFoco.isFocused) alPerderFoco()
                    teniaFoco = estadoFoco.isFocused
                },
            enabled = habilitado,
            readOnly = soloLectura,
            textStyle = DonChambitasTema.tipografia.cuerpo,
            label = etiqueta?.let {
                {
                    Text(
                        text = it,
                        style = DonChambitasTema.tipografia.secundario
                    )
                }
            },
            placeholder = marcadorPosicion?.let {
                {
                    Text(
                        text = it,
                        style = DonChambitasTema.tipografia.secundario,
                        color = Cafe
                    )
                }
            },
            leadingIcon = iconoInicio,
            trailingIcon = iconoFin,
            isError = hayError,
            visualTransformation = visualTransformation,
            keyboardOptions = tecladoOpciones,
            keyboardActions = tecladoAcciones,
            singleLine = lineasMaximas == 1,
            maxLines = lineasMaximas,
            minLines = lineasMinimas,
            shape = FormaCampo,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Arena,
                unfocusedContainerColor = Arena,
                disabledContainerColor = Arena,
                errorContainerColor = Arena,
                focusedBorderColor = MostazaOscuro,
                unfocusedBorderColor = Borde,
                disabledBorderColor = Borde.copy(alpha = 0.5f),
                errorBorderColor = Error,
                focusedTextColor = Carbon,
                unfocusedTextColor = Carbon,
                disabledTextColor = Cafe,
                errorTextColor = Carbon,
                focusedLabelColor = Carbon,
                unfocusedLabelColor = Cafe,
                disabledLabelColor = Cafe.copy(alpha = 0.5f),
                errorLabelColor = Error,
                cursorColor = MostazaOscuro,
                errorCursorColor = Error
            )
        )

        if (hayError) {
            Text(
                text = error.orEmpty(),
                style = DonChambitasTema.tipografia.pie,
                color = Error,
                modifier = Modifier.padding(start = EspacioTextoError, top = EspacioTextoError)
            )
        }
    }
}

/**
 * Campo de texto especializado para contraseñas, con boton de alternar visibilidad.
 */
@Composable
fun CampoContrasena(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String? = null,
    marcadorPosicion: String? = null,
    error: String? = null,
    habilitado: Boolean = true,
    tecladoOpciones: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    tecladoAcciones: KeyboardActions = KeyboardActions.Default,
    alPerderFoco: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var contrasenaVisible by rememberSaveable { mutableStateOf(false) }

    CampoTexto(
        valor = valor,
        alCambiarValor = alCambiarValor,
        etiqueta = etiqueta,
        marcadorPosicion = marcadorPosicion,
        error = error,
        habilitado = habilitado,
        tecladoOpciones = tecladoOpciones,
        tecladoAcciones = tecladoAcciones,
        alPerderFoco = alPerderFoco,
        visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
        iconoFin = {
            val descripcion = if (contrasenaVisible) {
                stringResource(R.string.ocultar_contrasena)
            } else {
                stringResource(R.string.mostrar_contrasena)
            }
            val icono = if (contrasenaVisible) {
                Icons.Outlined.VisibilityOff
            } else {
                Icons.Outlined.Visibility
            }

            IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                Icon(
                    imageVector = icono,
                    contentDescription = descripcion,
                    tint = Cafe
                )
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF8)
@Composable
private fun PreviaCampoTextoNormal() {
    DonChambitasTema {
        CampoTexto(
            valor = "Juan Pérez",
            alCambiarValor = {},
            etiqueta = "Nombre completo"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF8)
@Composable
private fun PreviaCampoTextoConError() {
    DonChambitasTema {
        CampoTexto(
            valor = "correo-invalido",
            alCambiarValor = {},
            etiqueta = "Correo electrónico",
            error = "Ingresa un correo válido con formato usuario@dominio.com"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF8)
@Composable
private fun PreviaCampoContrasena() {
    DonChambitasTema {
        CampoContrasena(
            valor = "123456",
            alCambiarValor = {},
            etiqueta = "Contraseña"
        )
    }
}
