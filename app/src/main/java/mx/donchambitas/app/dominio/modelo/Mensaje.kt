package mx.donchambitas.app.dominio.modelo

import java.time.Instant

/**
 * Mensaje individual perteneciente a una conversacion.
 * Corresponde a la tabla `public.mensajes`.
 */
data class Mensaje(
    val id: String,
    val conversacionId: String,
    val emisorId: String,
    val contenido: String,
    val leidoEn: Instant? = null,
    val creadoEn: Instant
)
