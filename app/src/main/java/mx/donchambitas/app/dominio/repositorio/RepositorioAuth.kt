package mx.donchambitas.app.dominio.repositorio

import kotlinx.coroutines.flow.Flow
import mx.donchambitas.app.dominio.modelo.RolUsuario
import mx.donchambitas.app.dominio.modelo.Sesion
import mx.donchambitas.app.dominio.modelo.Usuario
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones de autenticacion y sesion de usuario.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioAuth {
    /**
     * Registra un nuevo usuario en el sistema.
     */
    suspend fun registrar(
        correo: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        telefono: String?,
        rol: RolUsuario
    ): Resultado<Usuario>

    /**
     * Inicia sesion con credenciales de correo y contrasena.
     */
    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Resultado<Sesion>

    /**
     * Solicita restablecimiento de contrasena por correo electronico.
     * Siempre devuelve Exito por proteccion contra enumeracion de usuarios.
     */
    suspend fun recuperarContrasena(correo: String): Resultado<Unit>

    /**
     * Actualiza la contrasena del usuario con sesion activa.
     */
    suspend fun cambiarContrasena(nueva: String): Resultado<Unit>

    /**
     * Cierra la sesion activa.
     */
    suspend fun cerrarSesion(): Resultado<Unit>

    /**
     * Flujo reactivo del estado de la sesion actual.
     */
    fun sesionActual(): Flow<Sesion?>
}
