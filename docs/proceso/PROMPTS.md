# Prompts

Cópialos tal cual. No los improvises: la mitad del valor de este sistema está
en que todos le pidan al agente exactamente lo mismo.

---

## 1. Siguiente tarea

El de todos los días.

```
Lee AGENTS.md, docs/control/ESTADO.md, docs/control/PENDIENTES.md y
docs/tareas/INDICE.md. No abras nada más todavía.

Dime:
1. Cuántas decisiones pendientes requieren al líder y cuántas tareas bloquean.
2. Cuál es la siguiente tarea desbloqueada según las reglas de AGENTS.md.
3. Qué archivos vas a tocar.

Espera mi confirmación antes de escribir código.
```

---

## 2. Desarrollar la tarea

Después de confirmar.

```
Adelante con <ID>.

Abre su ticket en docs/tareas/ y solo los documentos que el ticket cite.
Sigue AGENTS.md al pie de la letra.

Cuando termines: actualiza ESTADO.md, INDICE.md y BITACORA.md, deja la rama
lista sin abrir el pull request, y repórtame el bloque de tarea terminada.

No empieces la siguiente tarea.
```

Se pueden juntar 1 y 2 en un solo mensaje si ya sabes qué sigue y no quieres
la confirmación intermedia.

---

## 3. Reporte de pendientes

Para el líder, cuando quiere ver dónde está atorado el proyecto.

```
Lee docs/control/PENDIENTES.md y docs/tareas/INDICE.md.

Dame:
1. Cada pendiente abierto, qué decide y cuántas tareas bloquea.
2. Las tareas bloqueadas, con su sprint y prioridad.
3. Cuántas tareas quedan desbloqueadas en el sprint actual.
4. Si al ritmo actual el pendiente va a frenar el sprint, dímelo.

No propongas la decisión. Solo repórtala.
```

---

## 4. Estado del proyecto

```
Lee docs/control/ESTADO.md, docs/tareas/INDICE.md y las últimas 15 líneas de
docs/control/BITACORA.md.

Dame un resumen de media cuartilla: qué se ha hecho, qué falta del sprint
actual, qué está bloqueado y cuál es el riesgo principal.
```

---

## 5. Revisar un pull request

Para el líder antes de integrar.

```
Revisa los cambios de la rama <rama> contra el ticket docs/tareas/<ID>.md.

Verifica una por una:
1. Cada criterio de aceptación del ticket.
2. Que no se haya tocado ninguna tarea marcada como bloqueada.
3. Que no se haya agregado nada de la lista "FUERA del MVP" de PRODUCTO.md.
4. Que se respeten CONVENCIONES.md y DISENO.md.
5. Que ESTADO.md, INDICE.md y BITACORA.md estén actualizados.

Dime qué falta. No lo arregles.
```

---

## 6. Redactar los tickets de un sprint

Para el líder al iniciar cada sprint.

```
Lee docs/tareas/INDICE.md, docs/tareas/PLANTILLA-TICKET.md,
docs/producto/PANTALLAS.md, docs/tecnico/ARQUITECTURA.md,
docs/tecnico/CONTRATOS-API.md y docs/tecnico/DISENO.md.

Redacta los tickets del Sprint <N> siguiendo la plantilla, uno por archivo en
docs/tareas/.

Criterios de aceptación verificables: nada de "funciona bien". Cita los IDs de
pantalla en vez de describirlas. Las tareas bloqueadas llevan el aviso de que
no se implementan sin autorización del líder.
```

---

## 7. Ponerme al día

Cuando alguien vuelve después de unos días.

```
Lee docs/control/ESTADO.md, docs/control/BITACORA.md y
docs/control/DECISIONES.md.

Dime qué cambió desde <fecha>: tareas terminadas, decisiones nuevas y qué
debería saber antes de tomar la siguiente tarea.
```

---

## Lo que NO se le pide al agente

- Que abra o integre un pull request. Eso lo hace una persona.
- Que edite `PENDIENTES.md` o `DECISIONES.md`. Solo el líder.
- Que haga dos tareas seguidas.
- Que revierta o reinterprete una decisión ya registrada en `DECISIONES.md`.
- Que agregue algo que está en la lista de "FUERA del MVP".

Si el agente hace cualquiera de estas cinco, deténlo y revierte.
