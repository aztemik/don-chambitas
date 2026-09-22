package mx.donchambitas.app.dominio.modelo

import java.math.BigDecimal
import java.time.Instant

/**
 * Oficio o servicio ofrecido por un trabajador.
 * Corresponde a la tabla `public.servicios`.
 */
data class Servicio(
    val id: String,
    val perfilId: String,
    val categoriaId: Int,
    val titulo: String,
    val descripcion: String,
    val precioDesde: BigDecimal? = null,
    val precioHasta: BigDecimal? = null,
    val unidadPrecio: String? = null,
    val activo: Boolean = true,
    val creadoEn: Instant,
    val actualizadoEn: Instant
)

/**
 * Fotografia de muestra asociada a un servicio (maximo 3 por servicio).
 * Corresponde a la tabla `public.servicio_fotos`.
 */
data class ServicioFoto(
    val id: String,
    val servicioId: String,
    val url: String,
    val posicion: Int = 1,
    val creadoEn: Instant
)
