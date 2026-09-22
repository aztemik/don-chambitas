package mx.donchambitas.app.datos.falso

import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import mx.donchambitas.app.dominio.modelo.Categoria
import mx.donchambitas.app.dominio.modelo.Estado
import mx.donchambitas.app.dominio.modelo.FiltrosBusquedaTrabajadores
import mx.donchambitas.app.dominio.modelo.Municipio
import mx.donchambitas.app.dominio.modelo.OrdenBusqueda
import mx.donchambitas.app.dominio.modelo.ResumenTrabajadorBusqueda
import mx.donchambitas.app.dominio.repositorio.RepositorioCatalogos
import mx.donchambitas.app.util.Resultado
import mx.donchambitas.app.util.TipoError

@Singleton
class RepositorioCatalogosFalso @Inject constructor(
    private val fuente: FuenteDatosFalsa
) : RepositorioCatalogos {

    var retrasoMs: Long = 300L
    var errorForzado: TipoError? = null

    private suspend fun verificarSimulacion(): Resultado.Error? {
        if (retrasoMs > 0) delay(retrasoMs)
        return errorForzado?.let { Resultado.Error(it, "Error simulado en RepositorioCatalogos: $it") }
    }

    override suspend fun buscarTrabajadores(
        filtros: FiltrosBusquedaTrabajadores,
        pagina: Int
    ): Resultado<List<ResumenTrabajadorBusqueda>> {
        verificarSimulacion()?.let { return it }

        val items = fuente.perfilesTrabajador.mapNotNull { perfil ->
            val usuario = fuente.usuarios.firstOrNull { it.id == perfil.usuarioId } ?: return@mapNotNull null
            val estado = fuente.estados.firstOrNull { it.id == perfil.estadoId }
            val municipio = fuente.municipios.firstOrNull { it.id == perfil.municipioId }

            val resenas = fuente.resenas.filter { it.trabajadorId == perfil.usuarioId }
            val promedio = if (resenas.isNotEmpty()) {
                Math.round(resenas.map { it.calificacion }.average() * 10.0) / 10.0
            } else {
                0.0
            }

            val servsActivos = fuente.servicios.filter { it.perfilId == perfil.usuarioId && it.activo }
            val precioMin = servsActivos.mapNotNull { it.precioDesde }.minOrNull()
            val categoriasIds = servsActivos.map { it.categoriaId }.distinct()

            ResumenTrabajadorBusqueda(
                trabajadorId = perfil.usuarioId,
                nombre = usuario.nombre,
                apellidos = usuario.apellidos,
                fotoUrl = usuario.fotoUrl,
                titulo = perfil.titulo,
                disponible = perfil.disponible,
                estadoId = perfil.estadoId ?: 0,
                municipioId = perfil.municipioId ?: 0,
                estado = estado?.nombre ?: "",
                municipio = municipio?.nombre ?: "",
                promedio = promedio,
                totalResenas = resenas.size,
                serviciosActivos = servsActivos.size,
                precioDesde = precioMin,
                categorias = categoriasIds,
                creadoEn = perfil.creadoEn
            )
        }

        // Filtros
        var filtrados = items.filter { item ->
            val cumpleTexto = filtros.texto.isNullOrBlank() ||
                    item.titulo.contains(filtros.texto, ignoreCase = true) ||
                    "${item.nombre} ${item.apellidos}".contains(filtros.texto, ignoreCase = true)

            val cumpleCategoria = filtros.categoriaId == null ||
                    item.categorias.contains(filtros.categoriaId)

            val cumpleEstado = filtros.estadoId == null || item.estadoId == filtros.estadoId
            val cumpleMunicipio = filtros.municipioId == null || item.municipioId == filtros.municipioId

            val cumplePrecio = filtros.precioDesdeMax == null ||
                    (item.precioDesde != null && item.precioDesde <= filtros.precioDesdeMax)

            val cumplePromedio = filtros.promedioMin == null || item.promedio >= filtros.promedioMin

            cumpleTexto && cumpleCategoria && cumpleEstado && cumpleMunicipio && cumplePrecio && cumplePromedio
        }

        // Orden
        filtrados = when (filtros.orden) {
            OrdenBusqueda.PROMEDIO -> filtrados.sortedByDescending { it.promedio }
            OrdenBusqueda.CREADO_EN -> filtrados.sortedByDescending { it.creadoEn }
            OrdenBusqueda.PRECIO_DESDE -> filtrados.sortedWith(
                compareBy(nullsLast()) { it.precioDesde }
            )
            null -> filtrados
        }

        val tamanoPagina = 20
        val inicio = (pagina - 1) * tamanoPagina
        val paginados = if (inicio < filtrados.size) {
            filtrados.drop(inicio).take(tamanoPagina)
        } else {
            emptyList()
        }

        return Resultado.Exito(paginados)
    }

    override suspend fun obtenerCategorias(): Resultado<List<Categoria>> {
        verificarSimulacion()?.let { return it }
        val lista = fuente.categorias.filter { it.activa }.sortedBy { it.orden }
        return Resultado.Exito(lista)
    }

    override suspend fun obtenerEstados(): Resultado<List<Estado>> {
        verificarSimulacion()?.let { return it }
        val lista = fuente.estados.sortedBy { it.nombre }
        return Resultado.Exito(lista)
    }

    override suspend fun obtenerMunicipios(estadoId: Int): Resultado<List<Municipio>> {
        verificarSimulacion()?.let { return it }
        val lista = fuente.municipios.filter { it.estadoId == estadoId }.sortedBy { it.nombre }
        return Resultado.Exito(lista)
    }
}
