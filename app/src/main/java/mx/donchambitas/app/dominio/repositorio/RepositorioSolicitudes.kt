package mx.donchambitas.app.dominio.repositorio

import mx.donchambitas.app.dominio.modelo.DetalleSolicitud
import mx.donchambitas.app.dominio.modelo.Solicitud
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones para solicitudes de trabajo de clientes.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioSolicitudes {
    /**
     * Obtiene las solicitudes publicadas por el cliente autenticado.
     */
    suspend fun obtenerMisSolicitudes(): Resultado<List<Solicitud>>

    /**
     * Obtiene el listado de solicitudes abiertas disponibles, con filtro opcional por categoria.
     */
    suspend fun obtenerAbiertas(categoriaId: Int? = null): Resultado<List<Solicitud>>

    /**
     * Obtiene el detalle de una solicitud incluyendo sus postulaciones recibidas.
     */
    suspend fun obtenerDetalle(id: String): Resultado<DetalleSolicitud>

    /**
     * Publica una nueva solicitud de trabajo.
     */
    suspend fun crear(solicitud: Solicitud): Resultado<Solicitud>

    /**
     * Actualiza la informacion de una solicitud de trabajo existente.
     */
    suspend fun actualizar(solicitud: Solicitud): Resultado<Solicitud>

    /**
     * Cancela una solicitud de trabajo propia.
     */
    suspend fun cancelar(id: String): Resultado<Solicitud>

    /**
     * Cierra una solicitud que cuenta con trabajador asignado (mediante RPC fn_cerrar_solicitud).
     */
    suspend fun cerrar(id: String): Resultado<Solicitud>
}
