package mx.donchambitas.app.dominio.modelo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class ModelosTest {

    @Test
    fun debeTenerValoresExactosDeSql_cuandoSeRevisanLosEnums() {
        val valoresRol = RolUsuario.entries.map { it.valor }
        assertEquals(listOf("cliente", "trabajador"), valoresRol)

        val valoresSolicitud = EstadoSolicitud.entries.map { it.valor }
        assertEquals(listOf("abierta", "asignada", "cerrada", "cancelada"), valoresSolicitud)

        val valoresPostulacion = EstadoPostulacion.entries.map { it.valor }
        assertEquals(listOf("enviada", "aceptada", "rechazada", "retirada"), valoresPostulacion)

        val valoresIa = FuncionIa.entries.map { it.valor }
        assertEquals(listOf("redactar_perfil", "redactar_servicio", "categorizar", "sugerir"), valoresIa)
    }

    @Test
    fun debeMapearCorrectamente_cuandoSeUsaDesdeValor() {
        assertEquals(RolUsuario.CLIENTE, RolUsuario.desdeValor("cliente"))
        assertEquals(RolUsuario.TRABAJADOR, RolUsuario.desdeValor("TRABAJADOR"))
        assertNull(RolUsuario.desdeValor("invalido"))

        assertEquals(EstadoSolicitud.ABIERTA, EstadoSolicitud.desdeValor("abierta"))
        assertEquals(EstadoSolicitud.CERRADA, EstadoSolicitud.desdeValor("CERRADA"))
        assertNull(EstadoSolicitud.desdeValor("invalido"))

        assertEquals(EstadoPostulacion.ENVIADA, EstadoPostulacion.desdeValor("enviada"))
        assertEquals(EstadoPostulacion.RETIRADA, EstadoPostulacion.desdeValor("retirada"))
        assertNull(EstadoPostulacion.desdeValor("invalido"))

        assertEquals(FuncionIa.REDACTAR_PERFIL, FuncionIa.desdeValor("redactar_perfil"))
        assertEquals(FuncionIa.SUGERIR, FuncionIa.desdeValor("sugerir"))
        assertNull(FuncionIa.desdeValor("invalido"))
    }

    @Test
    fun debeCrearInstanciaUsuario_conValoresPorDefectoCorrectos() {
        val ahora = Instant.now()
        val usuario = Usuario(
            id = "usr-123",
            correo = "test@donchambitas.mx",
            nombre = "Juan",
            apellidos = "Pérez",
            rol = RolUsuario.CLIENTE,
            creadoEn = ahora,
            actualizadoEn = ahora
        )

        assertEquals("usr-123", usuario.id)
        assertNull(usuario.telefono)
        assertNull(usuario.fotoUrl)
        assertTrue(usuario.activo)
    }

    @Test
    fun debeCrearInstanciaPerfilTrabajador_conValoresPorDefectoCorrectos() {
        val ahora = Instant.now()
        val perfil = PerfilTrabajador(
            usuarioId = "trab-123",
            titulo = "Plomero certificado",
            creadoEn = ahora,
            actualizadoEn = ahora
        )

        assertEquals(RolUsuario.TRABAJADOR, perfil.rol)
        assertEquals(0, perfil.experienciaAnios)
        assertTrue(perfil.disponible)
        assertNull(perfil.descripcion)
        assertNull(perfil.estadoId)
        assertNull(perfil.municipioId)
    }

    @Test
    fun debeCrearInstanciaServicioYSusFotos() {
        val ahora = Instant.now()
        val servicio = Servicio(
            id = "srv-1",
            perfilId = "trab-1",
            categoriaId = 3,
            titulo = "Reparación de tuberías",
            descripcion = "Servicio urgente 24/7",
            precioDesde = BigDecimal("250.00"),
            creadoEn = ahora,
            actualizadoEn = ahora
        )
        val foto = ServicioFoto(
            id = "foto-1",
            servicioId = servicio.id,
            url = "https://storage.supabase.co/perfiles/foto.jpg",
            creadoEn = ahora
        )

        assertEquals(1, foto.posicion)
        assertEquals(BigDecimal("250.00"), servicio.precioDesde)
        assertNull(servicio.precioHasta)
        assertTrue(servicio.activo)
    }

    @Test
    fun debeCrearInstanciaSolicitud_conValoresPorDefecto() {
        val ahora = Instant.now()
        val solicitud = Solicitud(
            id = "sol-1",
            clienteId = "cli-1",
            categoriaId = 2,
            titulo = "Fuga de agua en baño",
            descripcion = "Se requiere plomero hoy",
            creadoEn = ahora,
            actualizadoEn = ahora
        )

        assertEquals(RolUsuario.CLIENTE, solicitud.rolCliente)
        assertEquals(EstadoSolicitud.ABIERTA, solicitud.estatus)
        assertNull(solicitud.trabajadorId)
        assertNull(solicitud.cerradaEn)
    }

    @Test
    fun debeCrearInstanciasChatYResena() {
        val ahora = Instant.now()
        val conversacion = Conversacion(
            id = "conv-1",
            clienteId = "cli-1",
            trabajadorId = "trab-1",
            creadoEn = ahora
        )
        val mensaje = Mensaje(
            id = "msg-1",
            conversacionId = conversacion.id,
            emisorId = "cli-1",
            contenido = "Hola, ¿estás disponible?",
            creadoEn = ahora
        )
        val resena = Resena(
            id = "res-1",
            solicitudId = "sol-1",
            clienteId = "cli-1",
            trabajadorId = "trab-1",
            calificacion = 5,
            comentario = "Excelente servicio",
            creadoEn = ahora
        )

        assertNotNull(conversacion.id)
        assertNull(mensaje.leidoEn)
        assertEquals(5, resena.calificacion)
    }
}
