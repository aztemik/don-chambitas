# Proceso de trabajo

## La idea en una frase

Hay una cola de tareas ordenada por prioridad. Cada quien toma la siguiente
desbloqueada, la termina completa, la sube en una rama y abre un pull request.
El líder revisa e integra.

## Un día de trabajo

1. `git checkout main && git pull`
2. Abre `docs/control/ESTADO.md`. **Si hay una tarea en curso, no la toques.**
3. Copia el prompt de "siguiente tarea" de `PROMPTS.md` y dáselo a tu agente.
4. El agente reporta los pendientes y propone la siguiente tarea. Confirma.
5. Crea la rama con la nomenclatura de `CONVENCIONES.md`.
6. El agente trabaja. Tú pruebas en un dispositivo real.
7. Verifica la definición de hecho, entera.
8. Sube la rama y abre el pull request.
9. Avisa al líder.

## Reglas de la cola

**Una tarea a la vez por persona.** Dos tareas abiertas por alguien es cómo se
generan los conflictos que nadie quiere resolver.

**No se adelanta.** Si la siguiente tarea te parece aburrida y la de más abajo
interesante, mala suerte. La cola existe justamente para eso.

**No se toca lo bloqueado.** Una tarea marcada `bloqueada` espera una decisión
del líder. No se implementa ni "parcialmente" ni "para dejarla lista".

**Si un sprint no cierra sus tareas, no pasa nada.** Las abiertas se arrastran
al siguiente conservando su prioridad. Es preferible eso a entregar cinco
tareas a medias. Nadie se queda de más ni gasta tokens de su suscripción por
alcanzar un número.

## Correr Gradle desde la terminal

Android Studio trae su propio JDK y lo usa solo. Desde la terminal hay que
apuntarle, o `./gradlew` falla con `java: command not found`:

```bash
export JAVA_HOME=/opt/android-studio/jbr     # Linux
./gradlew assembleDebug
```

Ponlo en tu `~/.bashrc` y olvídate. La ruta cambia según dónde esté instalado
Android Studio y según el sistema operativo.

## Git

Rama por tarea, pull request por tarea. `main` protegida: nadie empuja directo,
ni el líder.

### Repositorio

- **Nombre:** `don-chambitas` (organización / usuario: `aztemik/don-chambitas`)
- **URL:** `https://github.com/aztemik/don-chambitas`
- **URL de clonación:** `https://github.com/aztemik/don-chambitas.git`

### Protección de la rama main

En GitHub (`Settings > Branches` o `Settings > Rules`):
1. **Rama objetivo:** `main`.
2. **Requerir pull request para integrar:** al menos 1 aprobación requerida (`Require approvals: 1`).
3. **Revisión obligatoria de propietarios:** `Require review from Code Owners` activo para que el líder revise todo PR.
4. **Sin excepciones:** `Do not allow bypassing the above settings` marcado (aplica también al líder y administradores).
5. **Sin force push ni eliminaciones:** bloqueado para todos los colaboradores.

Nomenclatura completa en `docs/tecnico/CONVENCIONES.md`.

Si `main` avanzó mientras trabajabas: `git rebase main`, resuelves y vuelves a
subir. No merges de `main` hacia tu rama.

## Definición de hecho

Una tarea está terminada cuando **todo** esto se cumple:

- [ ] Los criterios de aceptación del ticket se cumplen, uno por uno.
- [ ] `./gradlew assembleDebug` compila sin errores.
- [ ] `./gradlew testDebugUnitTest` pasa.
- [ ] Se instaló y se probó en un dispositivo o emulador real.
- [ ] No quedaron `TODO`, código comentado ni funciones muertas.
- [ ] Los cuatro estados de pantalla existen donde aplica: cargando, vacío,
      error y contenido.
- [ ] `docs/control/ESTADO.md` actualizado.
- [ ] `docs/tareas/INDICE.md` con la tarea en `hecha`.
- [ ] `docs/control/BITACORA.md` con un renglón nuevo.
- [ ] Rama, commits y pull request con la nomenclatura correcta.

**Los últimos cuatro no son burocracia.** Sin ellos el siguiente integrante no
sabe dónde quedó el proyecto, y el sistema completo deja de funcionar. Un pull
request sin documentación actualizada se rechaza.

## Revisión de pull requests

**Solo el líder aprueba e integra.** Nadie más.

Qué revisa:

1. Los criterios de aceptación, marcados y ciertos.
2. Que no se haya tocado nada bloqueado.
3. Que no se haya agregado alcance fuera de `PRODUCTO.md`.
4. Que las convenciones de nombres se respeten.
5. Que la documentación viva esté actualizada.
6. Que la interfaz respete `DISENO.md`, en particular la regla de texto Carbon
   sobre mostaza.

Si algo falla, se comenta y se devuelve. No se integra "y luego lo arreglamos".

## Los tres archivos vivos

| Archivo | Quién lo actualiza | Cuándo |
|---|---|---|
| `docs/control/ESTADO.md` | Quien trabaja | Al tomar y al terminar |
| `docs/tareas/INDICE.md` | Quien trabaja | Al cambiar de estado una tarea |
| `docs/control/BITACORA.md` | Quien trabaja | Al terminar, agregando un renglón |

`PENDIENTES.md` y `DECISIONES.md` los escribe **solo el líder**. El agente los
lee y los reporta.

## Ceremonias

Al iniciar cada sprint el líder redacta los tickets de ese sprint. No se
redactan los seis sprints por adelantado: los requisitos cambian y detallar el
Sprint 5 hoy es trabajo que se va a tirar.

Al cerrar cada sprint: revisión de lo terminado, arrastre de lo abierto y una
nota corta en la bitácora sobre qué salió mal.
