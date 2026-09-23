package mx.donchambitas.app.ui.pantallas

import mx.donchambitas.app.R
import mx.donchambitas.app.dominio.modelo.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesAutenticacionTest {

    @Test
    fun debeValidarCorreo_cuandoEstaVacioTieneFormatoIncorrectoOEsLargo() {
        assertEquals(R.string.validacion_correo_vacio, validarCorreo("   "))
        assertEquals(R.string.validacion_correo_formato, validarCorreo("refugio@ejemplo"))
        assertEquals(
            R.string.validacion_correo_largo,
            validarCorreo("a".repeat(150) + "@ejemplo.com")
        )
        assertNull(validarCorreo("  Refugio@Ejemplo.MX  "))
    }

    @Test
    fun debeExigirSoloQueNoEsteVacia_cuandoValidaContrasenaDeInicio() {
        assertEquals(R.string.validacion_contrasena_vacia, validarContrasenaInicio(""))
        assertNull(validarContrasenaInicio("1"))
    }

    @Test
    fun debeExigirOchoCaracteres_cuandoValidaContrasenaDeRegistro() {
        assertEquals(R.string.validacion_contrasena_corta, validarContrasenaRegistro("1234567"))
        assertNull(validarContrasenaRegistro("12345678"))
    }

    @Test
    fun debeExigirRol_cuandoValidaRegistro() {
        assertEquals(R.string.validacion_rol_sin_elegir, validarRol(null))
        assertNull(validarRol(RolUsuario.CLIENTE))
    }

    @Test
    fun debeValidarNombreYApellidos_cuandoEstanVaciosOLargos() {
        assertEquals(R.string.validacion_nombre_vacio, validarNombre("  "))
        assertEquals(
            R.string.validacion_nombre_largo,
            validarNombre("a".repeat(LimitesRegistro.LARGO_MAXIMO_NOMBRE + 1))
        )
        assertNull(validarNombre("Refugio"))

        assertEquals(R.string.validacion_apellidos_vacio, validarApellidos("  "))
        assertEquals(
            R.string.validacion_apellidos_largo,
            validarApellidos("a".repeat(LimitesRegistro.LARGO_MAXIMO_APELLIDOS + 1))
        )
        assertNull(validarApellidos("López Díaz"))
    }

    @Test
    fun debeExigirDiezDigitos_cuandoValidaTelefono() {
        assertEquals(R.string.validacion_telefono_vacio, validarTelefono(""))
        assertEquals(R.string.validacion_telefono_digitos, validarTelefono("477123456"))
        assertEquals(R.string.validacion_telefono_digitos, validarTelefono("477123456a"))
        assertNull(validarTelefono("4771234567"))
    }
}
