package mx.donchambitas.app.datos.falso

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import mx.donchambitas.app.dominio.modelo.EstadoPostulacion
import mx.donchambitas.app.dominio.modelo.EstadoSolicitud
import mx.donchambitas.app.dominio.modelo.Postulacion
import mx.donchambitas.app.dominio.repositorio.RepositorioPostulaciones
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

@Singleton
class RepositorioPostulacionesFalso @Inject constructor(
    private val fuente: FuenteDatosFalsa
) : RepositorioPostulaciones {

    var retrasoMs: Long = 300L
    var errorForzado: TipoError? = null

    private suspend fun verificarSimulacion(): Resultado.Error? {
        if (retrasoMs > 0) delay(retrasoMs)
        return errorForzado?.let { Resultado.Error(it, "Error simulado en RepositorioPostulaciones: $it") }
    }

    override suspend fun obtenerMisPostulaciones(): Resultado<List<Postulacion>> {
        verificarSimulacion()?.let { return it }

        val usuarioId = fuente.obtenerUsuarioActivoId()
            ?: return Resultado.Error(TipoError.AUTENTICACION, "No hay sesion activa")

        val lista = fuente.postulaciones.filter { it.trabajadorId == usuarioId }
        return Resultado.Exito(lista)
    }

    override suspend fun obtenerDeSolicitud(solicitudId: String): Resultado<List<Postulacion>> {
        verificarSimulacion()?.let { return it }

        val lista = fuente.postulaciones.filter { it.solicitudId == solicitudId }
        return Resultado.Exito(lista)
    }

    override suspend fun postularse(
        solicitudId: String,
        mensaje: String?,
        precio: BigDecimal?
    ): Resultado<Postulacion> {
        verificarSimulacion()?.let { return it }

        val usuarioId = fuente.obtenerUsuarioActivoId()
            ?: return Resultado.Error(TipoError.AUTENTICACION, "No hay sesion activa")

        val solicitud = fuente.solicitudes.firstOrNull { it.id == solicitudId }
            ?: return Resultado.Error(TipoError.VALIDACION, "Solicitud no encontrada")

        if (solicitud.estatus != EstadoSolicitud.ABIERTA) {
            return Resultado.Error(TipoError.VALIDACION, "Solo es posible postularse a solicitudes abiertas")
        }

        if (fuente.postulaciones.any { it.solicitudId == solicitudId && it.trabajadorId == usuarioId }) {
            return Resultado.Error(TipoError.VALIDACION, "Ya te has postulado previamente a esta solicitud")
        }

        val ahora = Instant.now()
        val nueva = Postulacion(
            id = "post-${UUID.randomUUID().toString().take(8)}",
            solicitudId = solicitudId,
            trabajadorId = usuarioId,
            mensaje = mensaje,
            precioPropuesto = precio,
            estatus = EstadoPostulacion.ENVIADA,
            creadoEn = ahora,
            actualizadoEn = ahora
        )
        fuente.postulaciones.add(nueva)
        return Resultado.Exito(nueva)
    }

    override suspend fun retirar(id: String): Resultado<Postulacion> {
        verificarSimulacion()?.let { return it }

        val usuarioId = fuente.obtenerUsuarioActivoId()
            ?: return Resultado.Error(TipoError.AUTENTICACION, "No hay sesion activa")

        val index = fuente.postulaciones.indexOfFirst { it.id == id && it.trabajadorId == usuarioId }
        if (index == -1) {
            return Resultado.Error(TipoError.VALIDACION, "Postulacion no encontrada o sin permisos")
        }

        val actual = fuente.postulaciones[index]
        if (actual.estatus == EstadoPostulacion.ACEPTADA) {
            return Resultado.Error(TipoError.VALIDACION, "No se puede retirar una postulacion ya aceptada")
        }

        val retirada = actual.copy(estatus = EstadoPostulacion.RETIRADA, actualizadoEn = Instant.now())
        fuente.postulaciones[index] = retirada
        return Resultado.Exito(retirada)
    }

    override suspend fun aceptar(id: String): Resultado<Postulacion> {
        verificarSimulacion()?.let { return it }

        val usuarioId = fuente.obtenerUsuarioActivoId()
            ?: return Resultado.Error(TipoError.AUTENTICACION, "No hay sesion activa")

        val postIndex = fuente.postulaciones.indexOfFirst { it.id == id }
        if (postIndex == -1) {
            return Resultado.Error(tipo = TipoError.VALIDACION, mensaje = "Postulacion no encontrada")
        }

        val postulacionGanadora = fuente.postulaciones[postIndex]
        val solIndex = fuente.solicitudes.indexOfFirst { it.id == postulacionGanadora.solicitudId && it.clienteId == usuarioId }
        if (solIndex == -1) {
            return Resultado.Error(TipoError.VALIDACION, "Solo el cliente propietario puede aceptar postulaciones")
        }

        val ahora = Instant.now()

        // 1. Aceptar postulacion seleccionada
        val aceptada = postulacionGanadora.copy(estatus = EstadoPostulacion.ACEPTADA, actualizadoEn = ahora)
        fuente.postulaciones[postIndex] = aceptada

        // 2. Rechazar las demas postulaciones de esta solicitud
        fuente.postulaciones.indices.forEach { i ->
            val p = fuente.postulaciones[i]
            if (p.solicitudId == postulacionGanadora.solicitudId && p.id != id && p.estatus == EstadoPostulacion.ENVIADA) {
                fuente.postulaciones[i] = p.copy(estatus = EstadoPostulacion.RECHAZADA, actualizadoEn = ahora)
            }
        }

        // 3. Asignar la solicitud
        val solicitudActual = fuente.solicitudes[solIndex]
        fuente.solicitudes[solIndex] = solicitudActual.copy(
            estatus = EstadoSolicitud.ASIGNADA,
            trabajadorId = postulacionGanadora.trabajadorId,
            actualizadoEn = ahora
        )

        return Resultado.Exito(aceptada)
    }
}
