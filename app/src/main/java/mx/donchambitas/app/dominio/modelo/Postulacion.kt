package mx.donchambitas.app.dominio.modelo

import java.math.BigDecimal
import java.time.Instant

/**
 * Postulacion u oferta enviada por un trabajador a una solicitud de trabajo.
 * Corresponde a la tabla `public.postulaciones`.
 */
data class Postulacion(
    val id: String,
    val solicitudId: String,
    val trabajadorId: String,
    val mensaje: String? = null,
    val precioPropuesto: BigDecimal? = null,
    val estatus: EstadoPostulacion = EstadoPostulacion.ENVIADA,
    val creadoEn: Instant,
    val actualizadoEn: Instant
)

/**
 * Estados posibles del flujo de una postulacion.
 * Corresponde al tipo enum `public.estado_postulacion` en PostgreSQL.
 */
enum class EstadoPostulacion(val valor: String) {
    ENVIADA("enviada"),
    ACEPTADA("aceptada"),
    RECHAZADA("rechazada"),
    RETIRADA("retirada");

    companion object {
        fun desdeValor(valor: String): EstadoPostulacion? =
            entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) }
    }
}
