package mx.donchambitas.app.dominio.repositorio

import kotlinx.coroutines.flow.Flow
import mx.donchambitas.app.dominio.modelo.Conversacion
import mx.donchambitas.app.dominio.modelo.Mensaje
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones de mensajeria instantanea interna.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioChat {
    /**
     * Obtiene las conversaciones del usuario autenticado ordenadas por fecha del ultimo mensaje.
     */
    suspend fun obtenerConversaciones(): Resultado<List<Conversacion>>

    /**
     * Inicia una conversacion con un trabajador. Segun DEC-20, siempre la abre el cliente.
     */
    suspend fun abrirConversacion(trabajadorId: String, solicitudId: String? = null): Resultado<Conversacion>

    /**
     * Obtiene los mensajes paginados pertenecientes a una conversacion.
     */
    suspend fun obtenerMensajes(conversacionId: String, pagina: Int = 1): Resultado<List<Mensaje>>

    /**
     * Envia un nuevo mensaje en la conversacion.
     */
    suspend fun enviar(conversacionId: String, contenido: String): Resultado<Mensaje>

    /**
     * Marca los mensajes de la conversacion como leidos por el receptor.
     */
    suspend fun marcarLeidos(conversacionId: String): Resultado<Unit>

    /**
     * Suscripcion reactiva a nuevos mensajes en tiempo real para una conversacion.
     */
    fun mensajesNuevos(conversacionId: String): Flow<Mensaje>
}
