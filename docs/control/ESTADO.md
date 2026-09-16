# Estado actual

> Archivo **vivo**. Quien termina una tarea lo actualiza. Es la primera cosa
> que lee el agente y la única fuente confiable sobre qué está pasando hoy.

**Última actualización:** 2026-09-15

---

## Sprint en curso

| Campo | Valor |
|---|---|
| Sprint | 1 |
| Fechas | PENDIENTE |
| Tareas del sprint | 16 |
| Terminadas | 2 |
| En curso | 0 |
| Bloqueadas | 0 |

## Tarea en curso

_Ninguna._

| Campo | Valor |
|---|---|
| ID | — |
| Título | — |
| Quién la tomó | — |
| Rama | — |
| Desde | — |

## Última tarea terminada

**`S1-T02` — Definición del alcance del MVP y lista completa de pantallas.**
2026-09-15. Rama `docs/S1-T02-alcance-mvp-pantallas`, salida de `main`, pull
request **sin abrir todavía**. `S1-T01` se integró a `main` en el pull request
**#1**.

El cruce de las 33 historias contra las 19 pantallas **dio limpio en las tres
revisiones del ticket**: ninguna historia sin pantalla, ninguna pantalla sin
historia, y ninguna historia que exija algo de la lista "FUERA del MVP". El
mapa se reconstruyó historia por historia y coincide con el que dejó `S1-T01`.
Queda documentado en `PANTALLAS.md`, en la sección nueva "Verificación de
cobertura". No se agregó ni se quitó ninguna pantalla y `PRODUCTO.md` no se
tocó.

**Los cuatro hallazgos que dejó, ya resueltos.** El líder los cerró el
2026-09-15, fuera del alcance de `S1-T02`. Quedan explicados al final de
`PANTALLAS.md`:

- **H-01** — Los filtros de búsqueda eran cinco en HU-18 y tres en
  `PRODUCTO.md`, P-06 y `S4-T05`. **Se amplió el alcance a cinco:** categoría,
  estado, municipio, precio y calificación. El dato ya existía porque HU-06
  obliga a capturar estado y municipio.
- **H-02** — **Se actualizó el renglón de P-06** con el ordenamiento y la
  paginación que HU-18 exige y que `S4-T07` ya cubría como tarea.
- **H-03** — Las notificaciones locales **se quedan sin historia propia**,
  cubiertas por los criterios de HU-22 y HU-26. Qué pasa al tocarlas lo define
  el ticket de `S5-T10`.
- **H-04** — No era decisión: el esquema ya la tenía tomada. **Se escribió la
  regla en HU-24**, un hilo por cliente-trabajador-solicitud más uno suelto
  para el contacto que nace en P-07.

## Siguiente en la cola

`S1-T03` — Modelo entidad-relación (ER) completo del sistema
(prioridad 950, depende de `S1-T01`, que ya está hecha)

## Decisiones recientes

**2026-09-15 · Dos decisiones nuevas: `DEC-23` y `DEC-24`.** Salen de los
hallazgos que dejó `S1-T02`. La primera amplía los filtros de búsqueda de tres
a cinco —se agregan estado y municipio— y con eso cambia `PRODUCTO.md`, el
renglón de P-06 y el título de `S4-T05`. La segunda pone por escrito la regla
de unicidad del hilo de chat que el esquema ya aplicaba, y que HU-24 no
describía para el caso de P-07.

Los otros dos hallazgos no necesitaron número: H-02 era la descripción de P-06,
que había quedado corta frente a HU-18, y H-03 se cerró aceptando que las
notificaciones locales sigan cubiertas por los criterios de HU-22 y HU-26 en
vez de tener historia propia. Los cuatro están explicados al final de
`PANTALLAS.md`.

**2026-09-15 · La base de datos está levantada y verificada.** Reinstalación
desde cero contra el proyecto de Supabase: `00_reinicio.sql` dejó todo en cero
—las once comprobaciones en `OK`, cubetas incluidas— y `01` a `04` corrieron sin
un solo error. `90_verificacion.sql` dio **42 de 42** y `91_prueba_funcional.sql`
**25 de 25**.

Falta lo que ningún script puede hacer solo, y es lo que cierra `S1-T03`:
probar RLS con la `anon key` y dos sesiones reales, mover las pruebas 10, 12 y
13 de `91` al bloque de `set role authenticated` (hoy corren como `postgres` y
pasan siempre), el diagrama ER y el cruce contra las historias de `S1-T01`.

**2026-09-15 · Revisión de `basedatos/` antes de volver a levantar el esquema.**
Se encontraron cinco defectos que `91_prueba_funcional.sql` **no detecta**,
porque corre como `postgres` y se salta RLS: cinco funciones de trigger sin
`security definer` que dejaban sus reglas sin aplicar en silencio, la vista de
búsqueda sin con qué filtrar por categoría, `fn_ia_registrar_llamada`
ejecutable con la `anon key`, permisos de `update` más anchos que el contrato,
y el semillero de categorías sin poder reconciliar los iconos de S1-T08. Los
cinco están corregidos en los archivos; `90_verificacion.sql` pasó de 36 a 42
comprobaciones y las seis nuevas son las que vigilan justo eso.

`00_reinicio.sql` ahora borra también los archivos y las dos cubetas de
Storage: el reinicio es total.

**2026-09-15 · Tres decisiones tomadas: `DEC-19`, `DEC-20` y `DEC-21`.** El
correo del trabajador es visible para cualquier usuario con sesión, el chat lo
abre siempre el cliente, y la atomicidad al aceptar una postulación es regla
del contrato y no de la base. Las tres están explicadas en `MODELO-ER.md`, en
"Lo que las políticas NO impiden, a propósito". `DEC-22` solo pone número a
algo que ya estaba decidido: los roles no se cambian.

**2026-09-14 · PEND-01 resuelto: el backend es Supabase.** Ver `DEC-16` y
`DEC-17`. Las seis tareas que estaban `bloqueada` pasaron a `pendiente` y ya no
queda ninguna tarea bloqueada en toda la cola. El cliente es `supabase-kt`:
Retrofit y OkHttp salieron del stack.

Esto **no** cambia cómo se trabaja hoy. La implementación activa sigue siendo
`FuenteDatosFalsa` hasta que cada tarea real llegue en su turno.

---

## Cómo se llena

Al **tomar** una tarea: llena "Tarea en curso" y pon la tarea en `en curso`
dentro de `docs/tareas/INDICE.md`.

Al **terminarla**: mueve la tarea a "Última tarea terminada", vacía "Tarea en
curso", recalcula el contador del sprint y actualiza "Siguiente en la cola".

Si dos personas van a trabajar el mismo día, la segunda revisa este archivo
**antes** de pedirle nada al agente. Si dice que hay una tarea en curso, esa
tarea no se toca.
