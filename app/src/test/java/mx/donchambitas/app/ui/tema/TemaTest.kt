package mx.donchambitas.app.ui.tema

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TemaTest {

    @Test
    fun debeTenerCarbonComoOnPrimary_cuandoSeConsultaEsquemaDeColor() {
        assertEquals(Carbon, EsquemaColoresTaller.onPrimary)
        assertNotEquals(Blanco, EsquemaColoresTaller.onPrimary)
    }

    @Test
    fun debeMapearColoresTallerCorrectamente_cuandoSeConsultaEsquemaDeColor() {
        assertEquals(Mostaza, EsquemaColoresTaller.primary)
        assertEquals(Terracota, EsquemaColoresTaller.secondary)
        assertEquals(Blanco, EsquemaColoresTaller.onSecondary)
        assertEquals(Crema, EsquemaColoresTaller.background)
        assertEquals(Carbon, EsquemaColoresTaller.onBackground)
        assertEquals(Arena, EsquemaColoresTaller.surface)
        assertEquals(Carbon, EsquemaColoresTaller.onSurface)
        assertEquals(Borde, EsquemaColoresTaller.outline)
        assertEquals(Error, EsquemaColoresTaller.error)
        assertEquals(Blanco, EsquemaColoresTaller.onError)
    }

    @Test
    fun debeTenerSeisEstilosTipograficosConTamanosYPesosCorrectos_cuandoSeConsultaTipografia() {
        // titulo: 24 sp, SemiBold
        assertEquals(24.sp, Titulo.fontSize)
        assertEquals(FontWeight.SemiBold, Titulo.fontWeight)
        assertEquals(Titulo, Tipografia.titulo)

        // subtitulo: 20 sp, Medium
        assertEquals(20.sp, Subtitulo.fontSize)
        assertEquals(FontWeight.Medium, Subtitulo.fontWeight)
        assertEquals(Subtitulo, Tipografia.subtitulo)

        // cuerpoFuerte: 16 sp, Medium
        assertEquals(16.sp, CuerpoFuerte.fontSize)
        assertEquals(FontWeight.Medium, CuerpoFuerte.fontWeight)
        assertEquals(CuerpoFuerte, Tipografia.cuerpoFuerte)

        // cuerpo: 16 sp, Normal
        assertEquals(16.sp, Cuerpo.fontSize)
        assertEquals(FontWeight.Normal, Cuerpo.fontWeight)
        assertEquals(Cuerpo, Tipografia.cuerpo)

        // secundario: 14 sp, Normal
        assertEquals(14.sp, Secundario.fontSize)
        assertEquals(FontWeight.Normal, Secundario.fontWeight)
        assertEquals(Secundario, Tipografia.secundario)

        // pie: 12 sp, Normal
        assertEquals(12.sp, Pie.fontSize)
        assertEquals(FontWeight.Normal, Pie.fontWeight)
        assertEquals(Pie, Tipografia.pie)
    }

    @Test
    fun debeExponerEscalaBase4_cuandoSeConsultaEspaciado() {
        assertEquals(4.dp, Espaciado.dp4)
        assertEquals(8.dp, Espaciado.dp8)
        assertEquals(12.dp, Espaciado.dp12)
        assertEquals(16.dp, Espaciado.dp16)
        assertEquals(24.dp, Espaciado.dp24)
        assertEquals(32.dp, Espaciado.dp32)
        assertEquals(48.dp, Espaciado.dp48)

        assertEquals(16.dp, Espaciado.margenPantalla)
        assertEquals(12.dp, Espaciado.separacionTarjetas)
        assertEquals(16.dp, Espaciado.rellenoTarjeta)
    }
}
