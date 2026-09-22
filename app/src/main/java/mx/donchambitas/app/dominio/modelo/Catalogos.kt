package mx.donchambitas.app.dominio.modelo

/**
 * Entidad federativa mexicana.
 * Corresponde a la tabla `public.estados`.
 */
data class Estado(
    val id: Int,
    val nombre: String,
    val clave: String
)

/**
 * Municipio o alcaldia perteneciente a un estado.
 * Corresponde a la tabla `public.municipios`.
 */
data class Municipio(
    val id: Int,
    val estadoId: Int,
    val nombre: String
)

/**
 * Categoria u oficio disponible en el sistema.
 * Corresponde a la tabla `public.categorias`.
 */
data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null,
    val activa: Boolean = true,
    val orden: Int = 0
)

/**
 * Funciones de inteligencia artificial admitidas en el backend/proxy.
 * Corresponde al tipo enum `public.funcion_ia` en PostgreSQL.
 */
enum class FuncionIa(val valor: String) {
    REDACTAR_PERFIL("redactar_perfil"),
    REDACTAR_SERVICIO("redactar_servicio"),
    CATEGORIZAR("categorizar"),
    SUGERIR("sugerir");

    companion object {
        fun desdeValor(valor: String): FuncionIa? =
            entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) }
    }
}
