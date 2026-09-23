package mx.donchambitas.app.dominio.validacion

import mx.donchambitas.app.dominio.modelo.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesAuthTest {

    @Test
    fun debePasar_cuandoElCorreoEsValido() {
        assertNull(ValidacionesAuth.validarCorreo("refugio@ejemplo.mx"))
    }

    @Test
    fun debePasar_cuandoElCorreoTraeEspaciosAlrededorOMayusculas() {
        assertNull(ValidacionesAuth.validarCorreo("  Refugio@Ejemplo.MX  "))
    }

    @Test
    fun debeFallarPorVacio_cuandoElCorreoSoloTraeEspacios() {
        assertEquals(FalloValidacion.CORREO_VACIO, ValidacionesAuth.validarCorreo("   "))
    }

    @Test
    fun debeFallarPorFormato_cuandoElCorreoNoTieneArroba() {
        assertEquals(FalloValidacion.CORREO_FORMATO, ValidacionesAuth.validarCorreo("refugio.ejemplo.mx"))
    }

    @Test
    fun debeFallarPorFormato_cuandoElCorreoNoTieneDominio() {
        assertEquals(FalloValidacion.CORREO_FORMATO, ValidacionesAuth.validarCorreo("refugio@"))
    }

    @Test
    fun debeFallarPorFormato_cuandoLaTerminacionTieneUnaSolaLetra() {
        assertEquals(FalloValidacion.CORREO_FORMATO, ValidacionesAuth.validarCorreo("refugio@ejemplo.m"))
    }

    @Test
    fun debeFallarPorFormato_cuandoElCorreoTieneDosArrobas() {
        assertEquals(FalloValidacion.CORREO_FORMATO, ValidacionesAuth.validarCorreo("a@b@ejemplo.mx"))
    }

    @Test
    fun debeFallarPorFormato_cuandoElCorreoTieneEspaciosEnMedio() {
        assertEquals(FalloValidacion.CORREO_FORMATO, ValidacionesAuth.validarCorreo("refu gio@ejemplo.mx"))
    }

    @Test
    fun debePasar_cuandoElCorreoMideJustoElMaximo() {
        val correo = "a".repeat(LimitesRegistro.LARGO_MAXIMO_CORREO - "@ejemplo.mx".length) + "@ejemplo.mx"
        assertNull(ValidacionesAuth.validarCorreo(correo))
    }

    @Test
    fun debeFallarPorLargo_cuandoElCorreoPasaDelMaximo() {
        val correo = "a".repeat(LimitesRegistro.LARGO_MAXIMO_CORREO) + "@ejemplo.mx"
        assertEquals(FalloValidacion.CORREO_LARGO, ValidacionesAuth.validarCorreo(correo))
    }

    @Test
    fun debeFallar_cuandoLaContrasenaDeInicioDeSesionEstaVacia() {
        assertEquals(FalloValidacion.CONTRASENA_VACIA, ValidacionesAuth.validarContrasenaInicioSesion(""))
    }

    @Test
    fun debePasar_cuandoLaContrasenaDeInicioDeSesionTieneUnCaracter() {
        assertNull(ValidacionesAuth.validarContrasenaInicioSesion("1"))
    }

    @Test
    fun debeFallar_cuandoLaContrasenaDeRegistroTieneSieteCaracteres() {
        assertEquals(FalloValidacion.CONTRASENA_CORTA, ValidacionesAuth.validarContrasenaRegistro("1234567"))
    }

    @Test
    fun debeFallarPorCorta_cuandoLaContrasenaDeRegistroEstaVacia() {
        assertEquals(FalloValidacion.CONTRASENA_CORTA, ValidacionesAuth.validarContrasenaRegistro(""))
    }

    @Test
    fun debePasar_cuandoLaContrasenaDeRegistroTieneOchoCaracteresSinComplejidad() {
        assertNull(ValidacionesAuth.validarContrasenaRegistro("abcdefgh"))
    }

    @Test
    fun debeFallar_cuandoNoSeEligioRol() {
        assertEquals(FalloValidacion.ROL_SIN_ELEGIR, ValidacionesAuth.validarRol(null))
    }

    @Test
    fun debePasar_cuandoSeEligioCualquierRol() {
        assertNull(ValidacionesAuth.validarRol(RolUsuario.CLIENTE))
        assertNull(ValidacionesAuth.validarRol(RolUsuario.TRABAJADOR))
    }

    @Test
    fun debePasar_cuandoElNombreEsValido() {
        assertNull(ValidacionesAuth.validarNombre("Refugio"))
    }

    @Test
    fun debeFallarPorVacio_cuandoElNombreSoloTraeEspacios() {
        assertEquals(FalloValidacion.NOMBRE_VACIO, ValidacionesAuth.validarNombre("   "))
    }

    @Test
    fun debeFallarPorLargo_cuandoElNombrePasaDelMaximo() {
        val nombre = "a".repeat(LimitesRegistro.LARGO_MAXIMO_NOMBRE + 1)
        assertEquals(FalloValidacion.NOMBRE_LARGO, ValidacionesAuth.validarNombre(nombre))
    }

    @Test
    fun debePasar_cuandoElNombreMideJustoElMaximo() {
        assertNull(ValidacionesAuth.validarNombre("a".repeat(LimitesRegistro.LARGO_MAXIMO_NOMBRE)))
    }

    @Test
    fun debePasar_cuandoLosApellidosSonValidos() {
        assertNull(ValidacionesAuth.validarApellidos("Martínez Luna"))
    }

    @Test
    fun debeFallarPorVacio_cuandoLosApellidosSoloTraenEspacios() {
        assertEquals(FalloValidacion.APELLIDOS_VACIO, ValidacionesAuth.validarApellidos(" "))
    }

    @Test
    fun debeFallarPorLargo_cuandoLosApellidosPasanDelMaximo() {
        val apellidos = "a".repeat(LimitesRegistro.LARGO_MAXIMO_APELLIDOS + 1)
        assertEquals(FalloValidacion.APELLIDOS_LARGO, ValidacionesAuth.validarApellidos(apellidos))
    }

    @Test
    fun debePasar_cuandoElTelefonoTieneDiezDigitos() {
        assertNull(ValidacionesAuth.validarTelefono("4771234567"))
    }

    @Test
    fun debeFallarPorVacio_cuandoNoHayTelefono() {
        assertEquals(FalloValidacion.TELEFONO_VACIO, ValidacionesAuth.validarTelefono(""))
    }

    @Test
    fun debeFallarPorDigitos_cuandoElTelefonoTieneNueve() {
        assertEquals(FalloValidacion.TELEFONO_DIGITOS, ValidacionesAuth.validarTelefono("477123456"))
    }

    @Test
    fun debeFallarPorDigitos_cuandoElTelefonoTieneOnce() {
        assertEquals(FalloValidacion.TELEFONO_DIGITOS, ValidacionesAuth.validarTelefono("47712345678"))
    }

    @Test
    fun debeFallarPorDigitos_cuandoElTelefonoTraeGuiones() {
        assertEquals(FalloValidacion.TELEFONO_DIGITOS, ValidacionesAuth.validarTelefono("477-123-45"))
    }
}
