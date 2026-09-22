package mx.donchambitas.app.dominio.repositorio

import mx.donchambitas.app.dominio.modelo.Usuario
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones del perfil base del usuario autenticado.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioUsuario {
    /**
     * Obtiene los datos del usuario autenticado actual.
     */
    suspend fun obtenerMiUsuario(): Resultado<Usuario>

    /**
     * Actualiza nombre, apellidos y telefono del usuario actual.
     */
    suspend fun actualizarMiUsuario(
        nombre: String,
        apellidos: String,
        telefono: String?
    ): Resultado<Usuario>

    /**
     * Sube y asocia la foto de perfil en el almacenamiento, devolviendo la URL publica.
     */
    suspend fun subirFotoPerfil(bytes: ByteArray): Resultado<String>
}
