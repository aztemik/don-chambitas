package mx.donchambitas.app.dominio.repositorio

import mx.donchambitas.app.dominio.modelo.Categoria
import mx.donchambitas.app.dominio.modelo.Estado
import mx.donchambitas.app.dominio.modelo.FiltrosBusquedaTrabajadores
import mx.donchambitas.app.dominio.modelo.Municipio
import mx.donchambitas.app.dominio.modelo.ResumenTrabajadorBusqueda
import mx.donchambitas.app.util.Resultado

/**
 * Contrato de operaciones para consulta de catalogos base y busqueda de trabajadores.
 * Especificado en docs/tecnico/CONTRATOS-API.md.
 */
interface RepositorioCatalogos {
    /**
     * Busca trabajadores aplicando filtros opcionales (texto, estado, municipio, precio, rating, orden).
     */
    suspend fun buscarTrabajadores(
        filtros: FiltrosBusquedaTrabajadores = FiltrosBusquedaTrabajadores(),
        pagina: Int = 1
    ): Resultado<List<ResumenTrabajadorBusqueda>>

    /**
     * Obtiene el listado completo de categorias de oficios activas.
     */
    suspend fun obtenerCategorias(): Resultado<List<Categoria>>

    /**
     * Obtiene el catalogo de las entidades federativas del pais.
     */
    suspend fun obtenerEstados(): Resultado<List<Estado>>

    /**
     * Obtiene los municipios correspondientes a un estado especifico.
     */
    suspend fun obtenerMunicipios(estadoId: Int): Resultado<List<Municipio>>
}
