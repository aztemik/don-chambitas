package mx.donchambitas.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultadoTest {

    @Test
    fun debeRetornarDato_cuandoResultadoEsExito() {
        val resultado: Resultado<String> = Resultado.Exito("prueba")
        assertTrue(resultado is Resultado.Exito)
        assertEquals("prueba", (resultado as Resultado.Exito).dato)
    }

    @Test
    fun debeRetornarTipoYMensaje_cuandoResultadoEsError() {
        val resultado: Resultado<String> = Resultado.Error(TipoError.RED, "Sin conexion")
        assertTrue(resultado is Resultado.Error)
        val error = resultado as Resultado.Error
        assertEquals(TipoError.RED, error.tipo)
        assertEquals("Sin conexion", error.mensaje)
    }
}
