package mx.donchambitas.app.datos.falso

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import mx.donchambitas.app.dominio.modelo.EstadoSolicitud
import mx.donchambitas.app.dominio.modelo.Resena
import mx.donchambitas.app.dominio.repositorio.RepositorioResenas
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

@Singleton
class RepositorioResenasFalso @Inject constructor(
    private val fuente: FuenteDatosFalsa
) : RepositorioResenas {

    var retrasoMs: Long = 300L
    var errorForzado: TipoError? = null

    private suspend fun verificarSimulacion(): Resultado.Error? {
        if (retrasoMs > 0) delay(retrasoMs)
        return errorForzado?.let { Resultado.Error(it, "Error simulado en RepositorioResenas: $it") }
    }

    override suspend fun dejarResena(
        solicitudId: String,
        calificacion: Int,
        comentario: String?
    ): Resultado<Resena> {
        verificarSimulacion()?.let { return it }

        val usuarioId = fuente.obtenerUsuarioActivoId()
            ?: return Resultado.Error(TipoError.AUTENTICACION, "No hay sesion activa")

        if (calificacion !in 1..5) {
            return Resultado.Error(TipoError.VALIDACION, "La calificacion debe estar entre 1 y 5 estrellas")
        }

        val solicitud = fuente.solicitudes.firstOrNull { it.id == solicitudId }
            ?: return Resultado.Error(TipoError.VALIDACION, "Solicitud no encontrada")

        if (solicitud.clienteId != usuarioId) {
            return Resultado.Error(TipoError.VALIDACION, "Solo el cliente que publico la solicitud puede calificar")
        }

        if (solicitud.estatus != EstadoSolicitud.CERRADA) {
            return Resultado.Error(TipoError.VALIDACION, "Solo se pueden calificar solicitudes cerradas")
        }

        val trabajadorId = solicitud.trabajadorId
            ?: return Resultado.Error(TipoError.VALIDACION, "La solicitud no cuenta con trabajador asignado")

        if (fuente.resenas.any { it.solicitudId == solicitudId }) {
            return Resultado.Error(TipoError.VALIDACION, "Ya se ha registrado una resena para esta solicitud")
        }

        val nueva = Resena(
            id = "res-${UUID.randomUUID().toString().take(8)}",
            solicitudId = solicitudId,
            clienteId = usuarioId,
            trabajadorId = trabajadorId,
            calificacion = calificacion,
            comentario = comentario,
            creadoEn = Instant.now()
        )
        fuente.resenas.add(nueva)
        return Resultado.Exito(nueva)
    }
}
