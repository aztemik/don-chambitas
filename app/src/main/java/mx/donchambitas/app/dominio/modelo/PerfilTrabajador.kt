package mx.donchambitas.app.dominio.modelo

import java.time.Instant

/**
 * Perfil profesional y de oficio del trabajador.
 * Corresponde a la tabla `public.perfiles_trabajador`.
 * Mantiene relacion 1:1 con [Usuario], donde [usuarioId] es la llave primaria.
 */
data class PerfilTrabajador(
    val usuarioId: String,
    val rol: RolUsuario = RolUsuario.TRABAJADOR,
    val titulo: String,
    val descripcion: String? = null,
    val experienciaAnios: Int = 0,
    val telefonoContacto: String? = null,
    val estadoId: Int? = null,
    val municipioId: Int? = null,
    val disponible: Boolean = true,
    val creadoEn: Instant,
    val actualizadoEn: Instant
)

/**
 * Habilidad declarada en el perfil de un trabajador.
 * Corresponde a la tabla `public.perfil_habilidades`.
 */
data class PerfilHabilidad(
    val perfilId: String,
    val habilidad: String
)
