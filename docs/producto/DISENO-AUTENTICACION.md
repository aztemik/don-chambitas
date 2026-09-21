# Diseño de las pantallas de autenticación · P-02, P-03 y P-04

> Entregable de **`S2-T01`**. Es el documento que consumen `S2-T02` (registro),
> `S2-T03` (inicio de sesión), `S2-T04` (validaciones), `S2-T05` (ViewModels) y
> `S2-T10` (recuperar contraseña). Lo que aquí no esté, no se implementa; lo
> que aquí esté, se implementa tal cual.

**Fecha:** 2026-09-21 · **Responsable:** BCJL ·
**Rama:** `docs/S2-T01-diseno-pantallas-autenticacion`

---

## 0. Alcance de esta tarea

`docs/tareas/S2-T01.md` **no existe**: los tickets del Sprint 2 no se
redactaron. El líder autorizó avanzar sin ticket el 2026-09-21, así que el
alcance queda escrito aquí y hace las veces de ticket.

### Qué entra

`S1-T14` ya dibujó P-02, P-03 y P-04 y describió a dónde lleva cada toque; eso
está en [`WIREFRAMES.md`](WIREFRAMES.md) y **no se repite**. Esta tarea
convierte ese dibujo en algo implementable sin volver a decidir nada:

1. La anatomía de cada pantalla con medidas, en orden de arriba hacia abajo.
2. El contrato de estado y de eventos de cada pantalla: qué `Estado...`
   expone, qué eventos recibe, qué hace cada uno.
3. Qué estados de pantalla existen y cuáles no aplican, con el porqué.
4. Las reglas de validación campo por campo, cuándo se disparan y con qué
   texto exacto.
5. El mapa de `TipoError` a lo que ve el usuario en cada pantalla.
6. Las claves de `strings.xml` que hay que crear, con su texto.
7. Teclado, orden de foco y accesibilidad.

### Qué NO entra

- **Código.** Esta tarea no escribe Kotlin. Las pantallas las construyen
  `S2-T02`, `S2-T03` y `S2-T10`; los ViewModels, `S2-T05`.
- **Cadenas en `strings.xml`.** Se especifican aquí con su clave, pero las
  agrega la tarea que construye cada pantalla, para no dejar recursos sin usar.
- **El contrato de la API.** Es `S2-T06`. Aquí solo se consume
  `RepositorioAuth`, que ya existe desde `S1-T13`.
- **Supabase.** `S2-T01` no es una de las cinco tareas de implementación real
  (AGENTS.md §6). La fuente activa sigue siendo `FuenteDatosFalsa`.
- **P-18 (Mi cuenta) y el cambio de contraseña dentro de la aplicación.** Son
  `S2-T11` y HU-05.
- **Definir la contraseña nueva desde el enlace del correo.** Ese segundo
  tramo de HU-04 ocurre fuera de la aplicación (enlace de Supabase Auth) y no
  tiene pantalla en `PANTALLAS.md`. Ver el hallazgo H-09 al final.

### De dónde sale cada cosa

| Fuente | Qué aporta |
|---|---|
| `PANTALLAS.md` | Qué hace cada pantalla y el mapa P-01 → P-02 ↔ P-03 ↔ P-04 |
| `WIREFRAMES.md` | El dibujo y el comportamiento de cada elemento tocable |
| `DISENO.md` | Paleta, tipografía, espaciado, formas y catálogo de componentes |
| `HISTORIAS.md` | HU-01, HU-02 y HU-04 con sus criterios de aceptación |
| `CONTRATOS-API.md` | Firma de `RepositorioAuth` y tabla de errores |
| `basedatos/01_esquema.sql` | Restricciones de `public.usuarios` que la interfaz debe respetar antes de enviar |
| `CONVENCIONES.md` | Nombres de archivos, clases y pruebas |

---

## 1. Reglas comunes a las tres pantallas

### 1.1 Estructura

Las tres comparten el mismo esqueleto y **ninguna tiene barra inferior**: son
pantallas de sesión cerrada, y la barra inferior solo existe dentro de los
subgrafos de cliente y de trabajador (`PANTALLAS.md`).

```
Scaffold
 ├─ topBar: BarraSuperior (Mostaza, título Carbon)
 └─ content: Column con scroll vertical
      ├─ margen lateral 16 dp
      ├─ bloques separados 24 dp
      ├─ campos del mismo bloque separados 16 dp
      └─ relleno inferior 24 dp
```

| Medida | Valor | De dónde sale |
|---|---|---|
| Margen lateral de pantalla | 16 dp | `DISENO.md` · Espaciado |
| Separación entre bloques | 24 dp | Escala de 4 |
| Separación entre campos | 16 dp | Escala de 4 |
| Separación etiqueta ↔ mensaje de error | 4 dp | Ya implementado en `Campos.kt` |
| Alto del botón principal | 48 dp | `DISENO.md` · `BotonPrincipal` |
| Radio de botones y campos | 12 dp | `DISENO.md` · Formas |
| Fondo | `Crema` | `DISENO.md` · Colores |

**Scroll obligatorio en las tres.** Con el teclado abierto en un equipo de
360 × 640 dp, P-03 no cabe. La `Column` lleva `verticalScroll` y
`imePadding()`; sin eso el botón "Crear cuenta" queda debajo del teclado.

### 1.2 Componentes que se usan

Solo estos, todos de `DISENO.md` y ya construidos en `S1-T10` y `S1-T11`:

`BarraSuperior`, `CampoTexto`, `CampoContrasena`, `ChipCategoria`,
`BotonPrincipal`, `BotonTexto`, `EstadoError`.

**Ningún componente nuevo.** Si al implementar parece faltar uno, se detiene la
tarea y se reporta: primero se agrega a `DISENO.md`.

### 1.3 Los cuatro estados, y cuál no aplica

`DISENO.md` exige cargando, vacío, error y contenido en **toda pantalla con
datos**. Las tres de autenticación son formularios: no listan datos, así que
el estado vacío no tiene qué representar. Queda escrito para que no se discuta
en revisión de pull request:

| Estado | P-02 | P-03 | P-04 | Cómo se pinta |
|---|---|---|---|---|
| Contenido | sí | sí | sí | El formulario. Es el estado por omisión |
| Cargando | sí | sí | sí | **Dentro del botón**, con `cargando = true` en `BotonPrincipal`, no con `Cargando` a pantalla completa |
| Error | sí | sí | sí | Ver 1.4: de campo o de pantalla según el tipo |
| Vacío | **no aplica** | **no aplica** | **no aplica** | No hay lista ni colección que pueda venir vacía |

**Por qué el cargando va dentro del botón.** Tapar el formulario con un
indicador a pantalla completa borra lo que el usuario escribió de su vista y,
si la llamada falla, lo obliga a reubicarse. HU-02 pide explícitamente que los
campos conserven lo escrito cuando falla por red. Mientras `cargando = true`:
el botón queda bloqueado, los campos quedan en `habilitado = false` y los
`BotonTexto` de navegación no responden.

### 1.4 Dónde se pinta cada error

Dos lugares, y la regla decide cuál:

- **Error de campo** — lo pinta el propio `CampoTexto` en su parámetro `error`.
  Es para lo que el usuario puede corregir en ese campo: formato de correo,
  contraseña corta, teléfono incompleto, campo vacío.
- **Error de pantalla** — un `EstadoError` **por encima del formulario**, sin
  reemplazarlo, con su botón de reintentar. Es para lo que no pertenece a
  ningún campo: sin red, servidor caído, credenciales rechazadas.

`EstadoError` aquí se usa **en línea**, no como pantalla completa: el
formulario sigue visible y editable debajo. Es la diferencia con P-05 y P-10,
donde `ContenedorEstado` sí sustituye el contenido, porque allá el error
significa que no hay nada que mostrar; aquí sí lo hay, es lo que el usuario
acaba de escribir.

### 1.5 Cuándo se valida

**Nunca mientras se escribe.** Marcar en rojo un correo a medio teclear es
hostil. La regla, para las tres pantallas:

1. **Al perder el foco** un campo que ya fue tocado: se valida ese campo solo.
2. **Al pulsar el botón principal**: se validan todos. Si alguno falla, no se
   llama al repositorio, se marcan todos los que fallaron y el foco salta al
   primero.
3. **Al volver a escribir** en un campo marcado: su error se limpia de
   inmediato, sin esperar a que pierda el foco.

El error de pantalla (1.4) se limpia al pulsar de nuevo el botón principal o
"Reintentar".

### 1.6 Normalización antes de enviar

El esquema tiene dos restricciones que la interfaz debe respetar **antes** de
llamar al repositorio, o el rechazo llega desde la base con un mensaje que no
es para el usuario:

| Restricción de `public.usuarios` | Qué hace la interfaz |
|---|---|
| `ck_usuario_correo_minusculas` (`correo = lower(correo)`) | El correo se envía con `trim()` y `lowercase()`. En pantalla se muestra tal cual lo escribió el usuario; la conversión ocurre en el ViewModel al enviar |
| `ck_usuario_correo_valido` (expresión regular) | La validación de la interfaz usa **esa misma** expresión. Ver 5.1 |

Nombre, apellidos y teléfono se envían con `trim()`. El teléfono se envía solo
con dígitos, sin espacios ni guiones.

### 1.7 Teclado y foco

| Campo | `KeyboardType` | `ImeAction` | Qué hace la acción |
|---|---|---|---|
| Correo | `Email` | `Next` | Pasa al siguiente campo |
| Contraseña (último campo de P-02) | `Password` | `Done` | Cierra el teclado y dispara el botón principal |
| Contraseña (en P-03, no es el último) | `Password` | `Next` | Pasa a teléfono |
| Nombre, apellidos | `Text` con `capitalization = Words` | `Next` | Pasa al siguiente campo |
| Teléfono | `Phone` | `Done` | Cierra el teclado y dispara el botón principal |

El orden de foco es el orden visual, de arriba hacia abajo. `Done` dispara el
botón principal **solo si no está en estado de carga**.

### 1.8 Accesibilidad

- Todo icono tocable lleva `contentDescription`. El ojo de `CampoContrasena`
  ya lo trae de `S1-T10` (`mostrar_contrasena` / `ocultar_contrasena`).
- Los chips de rol de P-03 se exponen como grupo de selección única
  (`Role.RadioButton` con `selectableGroup`), no como dos botones sueltos: un
  lector de pantalla debe anunciar "1 de 2 seleccionado".
- El mensaje de error de un campo se anuncia junto con el campo, mediante
  `semantics { error(...) }`, no como texto suelto debajo.
- Objetivo táctil mínimo de 48 × 48 dp en los `BotonTexto` de navegación:
  con `secundario` a 14 sp el texto mide menos, así que llevan relleno
  vertical hasta alcanzarlo.
- Contrastes: los de `DISENO.md`, sin excepción. Texto `Carbon` sobre
  `Mostaza` en el botón principal y en la barra superior.

---
