# Diccionario de datos

Este documento define la correspondencia oficial entre el esquema relacional
en PostgreSQL (`basedatos/01_esquema.sql`) y los modelos de dominio en Kotlin
(`app/src/main/java/mx/donchambitas/app/dominio/modelo/`).

---

## Reglas generales de equivalencia de tipos

| Tipo PostgreSQL | Tipo Kotlin | Notas |
|---|---|---|
| `uuid` | `String` | Formato canónico UUID en minúsculas con guiones. |
| `varchar(n)`, `char(n)`, `text` | `String` | Cadenas de texto. |
| `smallint` | `Int` | Enteros de 16 bits en base de datos mapeados a `Int` en Kotlin. |
| `integer`, `serial` | `Int` | Enteros de 32 bits. |
| `numeric(10,2)` | `BigDecimal` | Precisión exacta para montos y precios. |
| `boolean` | `Boolean` | Valores de verdad (`true` / `false`). |
| `timestamptz` | `Instant` | Marcas de tiempo UTC (`java.time.Instant`). |
| `enum` | `enum class` | Mapeo 1:1 con valores idénticos a los del esquema SQL. |

---

## Tipos enumerados

### `RolUsuario` (`public.rol_usuario`)

| Valor Kotlin | Valor SQL | Descripción |
|---|---|---|
| `CLIENTE` | `'cliente'` | Usuario que solicita servicios y publica trabajos. |
| `TRABAJADOR` | `'trabajador'` | Usuario profesional que ofrece oficios y se postula a solicitudes. |

### `EstadoSolicitud` (`public.estado_solicitud`)

| Valor Kotlin | Valor SQL | Descripción |
|---|---|---|
| `ABIERTA` | `'abierta'` | Solicitud recién publicada que admite postulaciones de trabajadores. |
| `ASIGNADA` | `'asignada'` | El cliente aceptó una postulación y asignó el trabajo al trabajador. |
| `CERRADA` | `'cerrada'` | Trabajo completado satisfactoriamente; habilita la reseña del cliente. |
| `CANCELADA` | `'cancelada'` | Solicitud cancelada por el cliente antes o durante la asignación. |

### `EstadoPostulacion` (`public.estado_postulacion`)

| Valor Kotlin | Valor SQL | Descripción |
|---|---|---|
| `ENVIADA` | `'enviada'` | Oferta enviada por el trabajador a la espera de decisión del cliente. |
| `ACEPTADA` | `'aceptada'` | Postulación elegida por el cliente para realizar el trabajo. |
| `RECHAZADA` | `'rechazada'` | Postulación descartada al aceptar a otro trabajador o por el cliente. |
| `RETIRADA` | `'retirada'` | Postulación cancelada voluntariamente por el propio trabajador. |

### `FuncionIa` (`public.funcion_ia`)

| Valor Kotlin | Valor SQL | Descripción |
|---|---|---|
| `REDACTAR_PERFIL` | `'redactar_perfil'` | Asistencia en la redacción de la descripción profesional del trabajador. |
| `REDACTAR_SERVICIO` | `'redactar_servicio'` | Asistencia en la redacción del título y detalle de un servicio ofrecido. |
| `CATEGORIZAR` | `'categorizar'` | Clasificación automática de una solicitud en uno de los 16 oficios. |
| `SUGERIR` | `'sugerir'` | Sugerencia y ordenamiento de trabajadores pertinentes para una solicitud. |

---

## Entidades y tablas

### 1. `Usuario` (`public.usuarios`)

Clase: `mx.donchambitas.app.dominio.modelo.Usuario`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único del usuario, coincidente con `auth.users.id`. |
| `correo` | `correo` | `String` | `varchar(160)` | No | Correo electrónico en minúsculas y validado. |
| `nombre` | `nombre` | `String` | `varchar(80)` | No | Nombre de pila del usuario. |
| `apellidos` | `apellidos` | `String` | `varchar(120)` | No | Apellidos del usuario. |
| `telefono` | `telefono` | `String?` | `varchar(20)` | Sí | Número de teléfono de contacto (opcional). |
| `rol` | `rol` | `RolUsuario` | `rol_usuario` | No | Rol del usuario (`CLIENTE` o `TRABAJADOR`). Excluyente y definitivo. |
| `fotoUrl` | `foto_url` | `String?` | `text` | Sí | URL pública de la imagen de perfil alojada en Storage. |
| `activo` | `activo` | `Boolean` | `boolean` | No | Indica si la cuenta se encuentra activa (`true` por defecto). |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de creación de la cuenta en el sistema. |
| `actualizadoEn` | `actualizado_en` | `Instant` | `timestamptz` | No | Fecha y hora de última modificación del registro. |

---

### 2. `PerfilTrabajador` (`public.perfiles_trabajador`)

Clase: `mx.donchambitas.app.dominio.modelo.PerfilTrabajador`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `usuarioId` | `usuario_id` | `String` | `uuid` | No | Llave primaria y foránea que referencia a `usuarios.id` (1:1). |
| `rol` | `rol` | `RolUsuario` | `rol_usuario` | No | Rol forzado a `TRABAJADOR` por restricción de integridad referencial. |
| `titulo` | `titulo` | `String` | `varchar(100)` | No | Título u oficio principal del trabajador (ej. "Plomero y electricista"). |
| `descripcion` | `descripcion` | `String?` | `text` | Sí | Descripción detallada de servicios, experiencia y forma de trabajo. |
| `experienciaAnios` | `experiencia_anios` | `Int` | `smallint` | No | Años de experiencia en el oficio (0 a 70). |
| `telefonoContacto` | `telefono_contacto` | `String?` | `varchar(20)` | Sí | Teléfono profesional público visible para clientes. |
| `estadoId` | `estado_id` | `Int?` | `smallint` | Sí | Llave foránea hacia la entidad federativa (`estados.id`). |
| `municipioId` | `municipio_id` | `Int?` | `integer` | Sí | Llave foránea hacia el municipio o alcaldía (`municipios.id`). |
| `disponible` | `disponible` | `Boolean` | `boolean` | No | Bandera que indica si el trabajador acepta nuevos trabajos actualmente. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de registro del perfil profesional. |
| `actualizadoEn` | `actualizado_en` | `Instant` | `timestamptz` | No | Fecha y hora de última actualización del perfil. |

---

### 3. `PerfilHabilidad` (`public.perfil_habilidades`)

Clase: `mx.donchambitas.app.dominio.modelo.PerfilHabilidad`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `perfilId` | `perfil_id` | `String` | `uuid` | No | Llave foránea hacia `perfiles_trabajador.usuario_id`. |
| `habilidad` | `habilidad` | `String` | `varchar(60)` | No | Nombre de la habilidad o especialidad técnica (ej. "Soldadura de cobre"). |

---

### 4. `Servicio` (`public.servicios`)

Clase: `mx.donchambitas.app.dominio.modelo.Servicio`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único del servicio ofrecido. |
| `perfilId` | `perfil_id` | `String` | `uuid` | No | Llave foránea hacia el trabajador que publica el servicio. |
| `categoriaId` | `categoria_id` | `Int` | `smallint` | No | Llave foránea hacia la categoría u oficio del catálogo (`categorias.id`). |
| `titulo` | `titulo` | `String` | `varchar(120)` | No | Título claro del servicio ofertado. |
| `descripcion` | `descripcion` | `String` | `text` | No | Explicación detallada de lo que incluye el trabajo. |
| `precioDesde` | `precio_desde` | `BigDecimal?` | `numeric(10,2)` | Sí | Precio base o inicial de referencia (opcional). |
| `precioHasta` | `precio_hasta` | `BigDecimal?` | `numeric(10,2)` | Sí | Precio tope o final estimado del trabajo (opcional). |
| `unidadPrecio` | `unidad_precio` | `String?` | `varchar(30)` | Sí | Unidad de cobro (ej. "por hora", "por metro", "por visita"). |
| `activo` | `activo` | `Boolean` | `boolean` | No | Indica si el servicio está activo o pausado por el trabajador. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de publicación del servicio. |
| `actualizadoEn` | `actualizado_en` | `Instant` | `timestamptz` | No | Fecha y hora de última modificación del servicio. |

---

### 5. `ServicioFoto` (`public.servicio_fotos`)

Clase: `mx.donchambitas.app.dominio.modelo.ServicioFoto`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único de la fotografía. |
| `servicioId` | `servicio_id` | `String` | `uuid` | No | Llave foránea hacia el servicio al que pertenece. |
| `url` | `url` | `String` | `text` | No | URL pública de la imagen en Supabase Storage. |
| `posicion` | `posicion` | `Int` | `smallint` | No | Orden de visualización de la foto en la galería (entre 1 y 3). |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de carga de la fotografía. |

---

### 6. `Solicitud` (`public.solicitudes`)

Clase: `mx.donchambitas.app.dominio.modelo.Solicitud`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único de la solicitud de trabajo. |
| `clienteId` | `cliente_id` | `String` | `uuid` | No | Llave foránea hacia el cliente que emite la solicitud (`usuarios.id`). |
| `rolCliente` | `rol_cliente` | `RolUsuario` | `rol_usuario` | No | Rol forzado a `CLIENTE` por restricción de integridad referencial. |
| `categoriaId` | `categoria_id` | `Int` | `smallint` | No | Llave foránea hacia la categoría requerida (`categorias.id`). |
| `titulo` | `titulo` | `String` | `varchar(120)` | No | Título breve de la necesidad o desperfecto. |
| `descripcion` | `descripcion` | `String` | `text` | No | Detalle del trabajo que se necesita realizar. |
| `presupuesto` | `presupuesto` | `BigDecimal?` | `numeric(10,2)` | Sí | Presupuesto aproximado ofrecido por el cliente (opcional). |
| `estadoId` | `estado_id` | `Int?` | `smallint` | Sí | Llave foránea hacia la entidad federativa donde se realizará el trabajo. |
| `municipioId` | `municipio_id` | `Int?` | `integer` | Sí | Llave foránea hacia el municipio o alcaldía correspondiente. |
| `estatus` | `estatus` | `EstadoSolicitud` | `estado_solicitud` | No | Estado en el flujo de vida (`ABIERTA`, `ASIGNADA`, `CERRADA`, `CANCELADA`). |
| `trabajadorId` | `trabajador_id` | `String?` | `uuid` | Sí | Llave foránea hacia el trabajador asignado para el trabajo. |
| `categorizadaPorIa` | `categorizada_por_ia` | `Boolean` | `boolean` | No | Indica si la categoría fue inferida automáticamente mediante IA. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de publicación de la solicitud. |
| `actualizadoEn` | `actualizado_en` | `Instant` | `timestamptz` | No | Fecha y hora de última modificación de la solicitud. |
| `cerradaEn` | `cerrada_en` | `Instant?` | `timestamptz` | Sí | Fecha y hora en que la solicitud pasó al estado `cerrada`. |

---

### 7. `Postulacion` (`public.postulaciones`)

Clase: `mx.donchambitas.app.dominio.modelo.Postulacion`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único de la postulación. |
| `solicitudId` | `solicitud_id` | `String` | `uuid` | No | Llave foránea hacia la solicitud a la que se postula el trabajador. |
| `trabajadorId` | `trabajador_id` | `String` | `uuid` | No | Llave foránea hacia el perfil del trabajador postulante. |
| `mensaje` | `mensaje` | `String?` | `text` | Sí | Propuesta textual o mensaje explicativo dirigido al cliente. |
| `precioPropuesto` | `precio_propuesto` | `BigDecimal?` | `numeric(10,2)` | Sí | Cotización económica propuesta por el trabajador para el trabajo. |
| `estatus` | `estatus` | `EstadoPostulacion` | `estado_postulacion` | No | Estado de la oferta (`ENVIADA`, `ACEPTADA`, `RECHAZADA`, `RETIRADA`). |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de emisión de la postulación. |
| `actualizadoEn` | `actualizado_en` | `Instant` | `timestamptz` | No | Fecha y hora de última modificación del estatus de la oferta. |

---

### 8. `Conversacion` (`public.conversaciones`)

Clase: `mx.donchambitas.app.dominio.modelo.Conversacion`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único del hilo de chat. |
| `clienteId` | `cliente_id` | `String` | `uuid` | No | Llave foránea hacia el cliente que inició la conversación. |
| `trabajadorId` | `trabajador_id` | `String` | `uuid` | No | Llave foránea hacia el trabajador participante. |
| `solicitudId` | `solicitud_id` | `String?` | `uuid` | Sí | Llave foránea opcional a la solicitud que originó la conversación. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de inicio del hilo. |
| `ultimoMensajeEn` | `ultimo_mensaje_en` | `Instant?` | `timestamptz` | Sí | Fecha y hora del mensaje más reciente (usado para ordenar la bandeja). |

---

### 9. `Mensaje` (`public.mensajes`)

Clase: `mx.donchambitas.app.dominio.modelo.Mensaje`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único del mensaje individual. |
| `conversacionId` | `conversacion_id` | `String` | `uuid` | No | Llave foránea hacia la conversación a la que pertenece. |
| `emisorId` | `emisor_id` | `String` | `uuid` | No | Llave foránea hacia el usuario que envió el texto. |
| `contenido` | `contenido` | `String` | `text` | No | Texto del mensaje (no vacío tras recorte de espacios). |
| `leidoEn` | `leido_en` | `Instant?` | `timestamptz` | Sí | Fecha y hora en que la contraparte visualizó el mensaje. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de envío del mensaje. |

---

### 10. `Resena` (`public.resenas`)

Clase: `mx.donchambitas.app.dominio.modelo.Resena`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `String` | `uuid` | No | Identificador único de la reseña. |
| `solicitudId` | `solicitud_id` | `String` | `uuid` | No | Llave foránea única hacia la solicitud cerrada calificada (1:1). |
| `clienteId` | `cliente_id` | `String` | `uuid` | No | Llave foránea hacia el cliente que emite la reseña. |
| `trabajadorId` | `trabajador_id` | `String` | `uuid` | No | Llave foránea hacia el trabajador evaluado. |
| `calificacion` | `calificacion` | `Int` | `smallint` | No | Puntuación entera del 1 al 5. |
| `comentario` | `comentario` | `String?` | `text` | Sí | Opinión textual voluntaria sobre la calidad del servicio. |
| `creadoEn` | `creado_en` | `Instant` | `timestamptz` | No | Fecha y hora de publicación de la calificación. |

---

### 11. `Estado` (`public.estados`)

Clase: `mx.donchambitas.app.dominio.modelo.Estado`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `Int` | `smallint` | No | Identificador numérico de la entidad federativa (1 a 32). |
| `nombre` | `nombre` | `String` | `varchar(80)` | No | Nombre oficial del estado de la República Mexicana. |
| `clave` | `clave` | `String` | `char(3)` | No | Clave oficial de 3 letras de la entidad federativa. |

---

### 12. `Municipio` (`public.municipios`)

Clase: `mx.donchambitas.app.dominio.modelo.Municipio`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `Int` | `serial` | No | Identificador único del municipio o alcaldía. |
| `estadoId` | `estado_id` | `Int` | `smallint` | No | Llave foránea hacia el estado al que pertenece. |
| `nombre` | `nombre` | `String` | `varchar(120)` | No | Nombre del municipio o alcaldía. |

---

### 13. `Categoria` (`public.categorias`)

Clase: `mx.donchambitas.app.dominio.modelo.Categoria`

| Campo Kotlin | Campo SQL | Tipo Kotlin | Tipo SQL | Nulable | Significado |
|---|---|---|---|:---:|---|
| `id` | `id` | `Int` | `smallint` | No | Identificador único de la categoría u oficio. |
| `nombre` | `nombre` | `String` | `varchar(80)` | No | Nombre descriptivo del oficio (ej. "Plomería", "Electricidad"). |
| `descripcion` | `descripcion` | `String?` | `varchar(200)` | Sí | Explicación breve del alcance del oficio. |
| `icono` | `icono` | `String?` | `varchar(60)` | Sí | Identificador del icono de Material Icons Outlined (no URL). |
| `activa` | `activa` | `Boolean` | `boolean` | No | Bandera que indica si el oficio está disponible para selección. |
| `orden` | `orden` | `Int` | `smallint` | No | Prioridad de despliegue en cuadrículas y listados. |
