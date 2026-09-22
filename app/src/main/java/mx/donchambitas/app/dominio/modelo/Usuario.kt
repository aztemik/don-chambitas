package mx.donchambitas.app.dominio.modelo

import java.time.Instant

/**
 * Representa un usuario en el sistema Don Chambitas.
 * Corresponde a la tabla `public.usuarios`.
 * Su [id] corresponde al identificador generado por Supabase Auth (`auth.users.id`).
 */
data class Usuario(
    val id: String,
    val correo: String,
    val nombre: String,
    val apellidos: String,
    val telefono: String? = null,
    val rol: RolUsuario,
    val fotoUrl: String? = null,
    val activo: Boolean = true,
    val creadoEn: Instant,
    val actualizadoEn: Instant
)

/**
 * Roles de usuario admitidos por el sistema.
 * Corresponde al tipo enum `public.rol_usuario` en PostgreSQL.
 */
enum class RolUsuario(val valor: String) {
    CLIENTE("cliente"),
    TRABAJADOR("trabajador");

    companion object {
        fun desdeValor(valor: String): RolUsuario? =
            entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) }
    }
}
