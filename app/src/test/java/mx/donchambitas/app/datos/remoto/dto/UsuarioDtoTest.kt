package mx.donchambitas.app.datos.remoto.dto

import java.time.Instant
import kotlinx.serialization.json.Json
import mx.donchambitas.app.dominio.modelo.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UsuarioDtoTest {

    /** Fila como la devuelve PostgREST para public.usuarios, con los nombres de columna reales. */
    private val filaDePostgrest = """
        {
          "id": "0b8f7c1e-2d3a-4b5c-8d9e-0f1a2b3c4d5e",
          "correo": "refugio@ejemplo.mx",
          "nombre": "Refugio",
          "apellidos": "Martinez Luna",
          "telefono": "4771234567",
          "rol": "trabajador",
          "foto_url": null,
          "activo": true,
          "creado_en": "2026-09-27T18:46:04.656123+00:00",
          "actualizado_en": "2026-09-27T19:00:00+00:00"
        }
    """.trimIndent()

    @Test
    fun debeLeerLasColumnasConSuNombreReal_cuandoLlegaUnaFilaDePostgrest() {
        val dto = Json.decodeFromString<UsuarioDto>(filaDePostgrest)

        assertEquals("refugio@ejemplo.mx", dto.correo)
        assertNull(dto.fotoUrl)
        assertEquals("2026-09-27T18:46:04.656123+00:00", dto.creadoEn)
    }

    @Test
    fun debeConvertirAUsuario_cuandoLaFilaEsValida() {
        val usuario = Json.decodeFromString<UsuarioDto>(filaDePostgrest).aUsuario()

        assertEquals(RolUsuario.TRABAJADOR, usuario.rol)
        assertEquals("4771234567", usuario.telefono)
        assertEquals(Instant.parse("2026-09-27T18:46:04.656123Z"), usuario.creadoEn)
        assertEquals(Instant.parse("2026-09-27T19:00:00Z"), usuario.actualizadoEn)
    }

    @Test(expected = IllegalArgumentException::class)
    fun debeFallar_cuandoElRolNoExiste() {
        Json.decodeFromString<UsuarioDto>(filaDePostgrest.replace("\"trabajador\"", "\"admin\"")).aUsuario()
    }
}
