package mx.donchambitas.app.dominio.repositorio

import java.math.BigDecimal
import mx.donchambitas.app.dominio.modelo.Postulacion
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones para postulaciones de trabajadores a solicitudes de trabajo.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioPostulaciones {
    /**
     * Obtiene las postulaciones enviadas por el trabajador autenticado.
     */
    suspend fun obtenerMisPostulaciones(): Resultado<List<Postulacion>>

    /**
     * Obtiene las postulaciones recibidas para una solicitud especifica.
     */
    suspend fun obtenerDeSolicitud(solicitudId: String): Resultado<List<Postulacion>>

    /**
     * Envia una propuesta o postulacion a una solicitud abierta.
     */
    suspend fun postularse(
        solicitudId: String,
        mensaje: String?,
        precio: BigDecimal?
    ): Resultado<Postulacion>

    /**
     * Retira una postulacion enviada previamente por el trabajador.
     */
    suspend fun retirar(id: String): Resultado<Postulacion>

    /**
     * Acepta una postulacion (RPC atomica fn_aceptar_postulacion).
     * Asigna la solicitud, acepta esta postulacion y rechaza las demas.
     */
    suspend fun aceptar(id: String): Resultado<Postulacion>
}
