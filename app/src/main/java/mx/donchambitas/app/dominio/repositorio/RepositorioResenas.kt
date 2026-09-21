package mx.donchambitas.app.dominio.repositorio

import mx.donchambitas.app.dominio.modelo.Resena
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones para calificar y dejar resenas de trabajos realizados.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioResenas {
    /**
     * Deja una resena con calificacion (1-5) para una solicitud cerrada.
     * Solo la puede crear el cliente propietario y sobre el trabajador asignado.
     */
    suspend fun dejarResena(
        solicitudId: String,
        calificacion: Int,
        comentario: String?
    ): Resultado<Resena>
}
