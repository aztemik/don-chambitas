package mx.donchambitas.app.dominio.modelo

import java.time.Instant

/**
 * Calificacion y resena de un cliente sobre el trabajo realizado por un trabajador en una solicitud cerrada.
 * Corresponde a la tabla `public.resenas`.
 */
data class Resena(
    val id: String,
    val solicitudId: String,
    val clienteId: String,
    val trabajadorId: String,
    val calificacion: Int,
    val comentario: String? = null,
    val creadoEn: Instant
)
