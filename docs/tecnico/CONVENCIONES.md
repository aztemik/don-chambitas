# Convenciones

Todo en **español**, salvo las palabras que el framework impone (`onClick`,
`ViewModel`, `Composable`, `suspend`). No se traducen a la fuerza.

## Kotlin

| Elemento | Estilo | Ejemplo |
|---|---|---|
| Clase e interfaz | PascalCase | `RepositorioServicios` |
| Función y variable | camelCase | `obtenerPorTrabajador` |
| Constante | MAYUSCULAS_CON_GUION | `LIMITE_FOTOS_SERVICIO` |
| Paquete | minúsculas | `mx.donchambitas.app.dominio` |
| Composable | PascalCase | `TarjetaTrabajador` |
| Archivo | igual que su clase principal | `RepositorioServicios.kt` |

**Sin acentos ni eñes en identificadores.** `contrasena`, no `contraseña`.
`resenas`, no `reseñas`. Los acentos sí van en los textos de la interfaz.

### Sufijos

| Sufijo | Para |
|---|---|
| `...ViewModel` | ViewModels |
| `...Pantalla` | Composable de pantalla completa |
| `Estado...` | Data class de estado de pantalla |
| `Repositorio...` | Interfaz de repositorio |
| `...Real` | Implementación contra el backend |
| `...Falso` | Implementación en memoria |
| `...Dto` | Objeto de transferencia de red |
| `Modulo...` | Módulo de Hilt |

Ejemplo completo de una pantalla:

```
IniciarSesionPantalla.kt
IniciarSesionViewModel.kt
EstadoIniciarSesion.kt
```

## Base de datos

`snake_case`, tablas en plural, columnas en singular. Sin acentos ni eñes.
Llaves foráneas terminan en `_id`. Marcas de tiempo: `creado_en`,
`actualizado_en`.

## Git

### Ramas

```
<tipo>/<ID-tarea>-<descripcion-corta>
```

Tipos: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`.

```
feat/S2-T04-validaciones-formularios
fix/S3-T08-contrato-servicios
docs/S1-T11-arquitectura
```

Descripción en minúsculas, con guiones, sin acentos, máximo 5 palabras.

### Commits

```
<tipo>(<ID-tarea>): <qué hace, en presente>
```

```
feat(S2-T02): agrega pantalla de registro con seleccion de rol
fix(S2-T04): corrige validacion de correo con mayusculas
docs(S1-T11): documenta la arquitectura de capas
```

Máximo 72 caracteres en la primera línea, en presente, sin punto final. El
cuerpo se usa solo si hace falta explicar el porqué.

### Pull requests

Título: `[S2-T04] Validaciones de formularios`

Cuerpo:

```
## Qué hace
Dos o tres renglones.

## Criterios de aceptación
- [x] Cada criterio del ticket, marcado

## Cómo probarlo
Pasos concretos en el dispositivo.

## Documentación actualizada
- [x] ESTADO.md
- [x] INDICE.md
- [x] BITACORA.md
```

## Comentarios

Se comenta **por qué**, no **qué**. El código ya dice qué hace.

```kotlin
// Mal
// Suma uno al contador
contador++

// Bien
// El tope diario se cuenta en total y no por funcion (DEC-18): lo que
// limita es el costo, y el costo no distingue de que funcion vino.
```

En español, sin acentos si van dentro de código que se compila en varios
entornos.

## Pruebas

`debe<LoQueEsperamos>_cuando<Condicion>`

```kotlin
@Test
fun debeRechazarCorreo_cuandoNoTieneArroba()

@Test
fun debeDevolverError_cuandoSeAlcanzaElTopeDiarioDeIa()
```

Los ViewModels y la lógica de dominio se prueban. Los composables solo cuando
tienen lógica propia que valga la pena.

## Lo que no se hace

- Mezclar español e inglés en un mismo nombre: `getUsuario`, `listaDeServices`.
- Abreviar sin necesidad: `usr`, `srv`, `cat`.
- Cadenas de interfaz fuera de `strings.xml`.
- Números mágicos. Van a una constante con nombre.
- Dejar `TODO` en una rama que se va a integrar.
