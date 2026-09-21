package mx.donchambitas.app.dominio.modelo

import java.time.Instant

/**
 * Hilo de chat interno entre un cliente y un trabajador.
 * Corresponde a la tabla `public.conversaciones`.
 */
data class Conversacion(
    val id: String,
    val clienteId: String,
    val trabajadorId: String,
    val solicitudId: String? = null,
    val creadoEn: Instant,
    val ultimoMensajeEn: Instant? = null
)
