package mx.donchambitas.app.datos.remoto.dto

import java.time.OffsetDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.modelo.Usuario

/**
 * Fila de public.usuarios tal como la devuelve PostgREST.
 * Columnas en docs/tecnico/CONTRATOS-API.md, "Como se arma la Sesion".
 *
 * Las marcas de tiempo llegan como texto con desplazamiento
 * ("2026-09-27T18:46:04.656123+00:00"), por eso se leen con OffsetDateTime.
 */
@Serializable
data class UsuarioDto(
    val id: String,
    val correo: String,
    val nombre: String,
    val apellidos: String,
    val telefono: String? = null,
    val rol: String,
    @SerialName("foto_url") val fotoUrl: String? = null,
    val activo: Boolean,
    @SerialName("creado_en") val creadoEn: String,
    @SerialName("actualizado_en") val actualizadoEn: String
) {
    fun aUsuario(): Usuario = Usuario(
        id = id,
        correo = correo,
        nombre = nombre,
        apellidos = apellidos,
        telefono = telefono,
        rol = requireNotNull(RolUsuario.desdeValor(rol)) { "Rol desconocido en public.usuarios: $rol" },
        fotoUrl = fotoUrl,
        activo = activo,
        creadoEn = OffsetDateTime.parse(creadoEn).toInstant(),
        actualizadoEn = OffsetDateTime.parse(actualizadoEn).toInstant()
    )
}
