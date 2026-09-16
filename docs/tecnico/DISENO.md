# Sistema de diseño

Paleta **Taller**. La identidad nace del casco de seguridad amarillo: mostaza
como color principal, terracota como acento, fondo cálido.

## Colores

```kotlin
val Mostaza        = Color(0xFFE0A11B)  // primario
val MostazaOscuro  = Color(0xFFB37D08)  // presionado, bordes de énfasis
val Terracota      = Color(0xFFC1440E)  // acento, acciones destacadas
val Carbon         = Color(0xFF2B1F14)  // texto principal
val Cafe           = Color(0xFF7A6A58)  // texto secundario
val Crema          = Color(0xFFFFFDF8)  // fondo
val Arena          = Color(0xFFF7F0E4)  // superficie, tarjetas
val Borde          = Color(0xFFE6DAC6)  // separadores

val Exito       = Color(0xFF2E7D32)
val Advertencia = Color(0xFFED6C02)
val Error       = Color(0xFFC62828)
```

### Reglas de contraste, obligatorias

**Sobre mostaza va texto `Carbon`, nunca blanco.** El amarillo con texto blanco
no pasa contraste y se ve barato. Es la regla que más se rompe.

**Sobre terracota va texto blanco.**

**Nunca terracota y mostaza pegados** en dos elementos grandes. La terracota es
acento: aparece en un botón, un chip activo, una insignia, no en bloques.

### A qué se aplica cada uno

| Color | Dónde |
|---|---|
| Mostaza | Botón principal, barra superior, chip activo, indicador de la barra inferior |
| Mostaza oscuro | Estado presionado, borde del campo enfocado |
| Terracota | Botón de acción destacada (postularse, publicar solicitud), estrellas |
| Carbon | Títulos y texto de lectura |
| Cafe | Etiquetas, ayudas, fechas, texto deshabilitado |
| Crema | Fondo de pantalla |
| Arena | Tarjetas, campos, hojas inferiores |
| Borde | Separadores y contorno de campos en reposo |

**Sin modo oscuro en el MVP.** Es DEC-15.

## Tipografía

Fuente del sistema, sin descargar nada.

| Estilo | Tamaño | Peso | Uso |
|---|---|---|---|
| `titulo` | 24 sp | SemiBold | Encabezado de pantalla |
| `subtitulo` | 20 sp | Medium | Sección |
| `cuerpoFuerte` | 16 sp | Medium | Título de tarjeta |
| `cuerpo` | 16 sp | Normal | Texto de lectura |
| `secundario` | 14 sp | Normal | Etiquetas y apoyos |
| `pie` | 12 sp | Normal | Fechas, contadores |

Nada por debajo de 12 sp. Un usuario en obra ve la pantalla con el sol encima.

## Espaciado

Escala de 4: `4, 8, 12, 16, 24, 32, 48`.

Margen lateral de pantalla: **16 dp**. Separación entre tarjetas: **12 dp**.
Relleno interno de tarjeta: **16 dp**.

## Formas

| Elemento | Radio |
|---|---|
| Botones y campos | 12 dp |
| Tarjetas | 16 dp |
| Chips | completamente redondeado |
| Hoja inferior | 20 dp arriba |
| Foto de perfil | círculo |

## Componentes base

Se construyen en el Sprint 1 y **nadie escribe un botón a mano después**. Si
falta un componente, se agrega aquí primero.

| Componente | Notas |
|---|---|
| `BotonPrincipal` | Mostaza, texto Carbon, ancho completo, 48 dp de alto |
| `BotonSecundario` | Contorno mostaza oscuro, fondo transparente |
| `BotonDestacado` | Terracota, texto blanco. Para postularse y publicar |
| `BotonTexto` | Sin fondo, texto mostaza oscuro |
| `CampoTexto` | Fondo Arena, borde Borde, borde mostaza oscuro al enfocar |
| `CampoContrasena` | Igual, con ojo para mostrar y ocultar |
| `TarjetaTrabajador` | Foto, nombre, título, estrellas, municipio |
| `TarjetaServicio` | Foto, título, categoría, rango de precio |
| `TarjetaSolicitud` | Título, categoría, presupuesto, estado, fecha |
| `ChipCategoria` | Mostaza cuando está activo, Arena cuando no |
| `Estrellas` | Terracota, media estrella permitida en lectura |
| `EtiquetaEstado` | Color según estado de la solicitud |
| `Cargando` | Indicador centrado, mostaza |
| `EstadoVacio` | Icono, texto y botón opcional |
| `EstadoError` | Mensaje y botón de reintentar |
| `BarraSuperior` | Mostaza, título Carbon, flecha de regreso |
| `BarraInferior` | 4 destinos, indicador mostaza |

### Colores de los estados de la solicitud

| Estado | Color |
|---|---|
| abierta | Éxito |
| asignada | Advertencia |
| cerrada | Cafe |
| cancelada | Error |

## Iconos

Material Icons, variante `Outlined`. Sin librerías externas y sin emoji en la
interfaz.

**Los 16 iconos de categoría todavía no están elegidos.** La columna
`categorias.icono` de `basedatos/04_datos_semilla.sql` está sembrada con nombres
de Tabler Icons (`ti-droplet`, `ti-bolt`, …), que son de otra librería y
contradicen esta regla. Son provisionales y hoy no los consume nadie.

**S1-T08** elige los 16 de Material, los anota aquí en una tabla de categoría a
icono, y reemplaza los 16 valores del semillero. Hasta entonces son datos
muertos.

## Textos

Todo en español. Tuteo, sin usted. Frases cortas.

**Los mensajes de error dicen qué hacer**, no solo qué falló. "El correo ya
está registrado, inicia sesión" y no "Error 409".

Todas las cadenas van en `strings.xml`. Ninguna cadena de interfaz vive dentro
de un `@Composable`.

## Estados que toda pantalla con datos debe tener

Cargando, vacío, error y contenido. **Las cuatro.** Una pantalla que solo
maneja el caso feliz no pasa revisión de pull request.
