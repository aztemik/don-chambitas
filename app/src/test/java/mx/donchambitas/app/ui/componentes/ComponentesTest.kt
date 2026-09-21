package mx.donchambitas.app.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import mx.donchambitas.app.dominio.modelo.EstadoSolicitud
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ComponentesTest {

    @Test
    fun debeContenerCuatroEstadosValidos_cuandoSeEvaluanEstadosDeSolicitud() {
        val estados = EstadoSolicitud.entries
        assertEquals(4, estados.size)
        assertEquals(EstadoSolicitud.ABIERTA, EstadoSolicitud.desdeValor("abierta"))
        assertEquals(EstadoSolicitud.ASIGNADA, EstadoSolicitud.desdeValor("asignada"))
        assertEquals(EstadoSolicitud.CERRADA, EstadoSolicitud.desdeValor("cerrada"))
        assertEquals(EstadoSolicitud.CANCELADA, EstadoSolicitud.desdeValor("cancelada"))
    }

    @Test
    fun debeConstruirDestinoBarraInferiorCorrectamente_cuandoSeInstancia() {
        val destino = DestinoBarraInferior(
            id = 0,
            etiqueta = "Inicio",
            icono = Icons.Outlined.Home
        )
        assertEquals(0, destino.id)
        assertEquals("Inicio", destino.etiqueta)
        assertEquals(Icons.Outlined.Home, destino.icono)
    }

    @Test
    fun debeManejarEstadoDesconocido_cuandoSePasaCadenaInvalida() {
        val estado = EstadoSolicitud.desdeValor("desconocido")
        assertNull(estado)
    }
}
