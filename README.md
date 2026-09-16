# Don Chambitas

Aplicación Android que conecta a trabajadores informales con personas que
necesitan un servicio. El trabajador publica su perfil y sus oficios; el
cliente publica lo que necesita, busca, se pone en contacto y califica.
La aplicación usa inteligencia artificial para redactar perfiles y
descripciones de servicios.

Proyecto académico. Equipo de 4 integrantes, 6 sprints.

## Equipo

| Siglas | Nombre | Rol |
|---|---|---|
| GRI | Gonzalez Ramirez Ivan | Scrum Master y líder |
| BCJL | Bautista Carlos Jose Luis | Product Owner |
| LMM | Leon Moso Michael | Equipo de desarrollo |
| RRC | Rodriguez Calderon Ricardo | Equipo de desarrollo |

El líder es el único que aprueba e integra pull requests, y el único que
puede desbloquear las decisiones pendientes.

## Cómo trabajamos

Hay una cola de 86 tareas en `docs/tareas/INDICE.md`, ordenadas por prioridad.
Hoy **ninguna está bloqueada**.
Cada quien toma la siguiente tarea desbloqueada, la termina completa, la sube
en una rama y abre un pull request. No se toman dos tareas a la vez y no se
adelanta trabajo.

Buena parte del desarrollo se hace con un agente de IA. El protocolo está en
[AGENTS.md](AGENTS.md) y los prompts listos para copiar están en
[docs/proceso/PROMPTS.md](docs/proceso/PROMPTS.md).

## Cómo empiezo hoy

1. Clona el repositorio y ábrelo en Android Studio.
2. Lee `docs/proceso/PROCESO.md`.
3. Copia el prompt de "siguiente tarea" de `docs/proceso/PROMPTS.md` y pásaselo
   a tu agente.
4. El agente te dirá qué sigue y qué está bloqueado. Confirma y déjalo trabajar.
5. Prueba en un dispositivo, sube la rama, abre el pull request.

## Estado del proyecto

Consulta `docs/control/ESTADO.md`. Es la única fuente confiable sobre qué está
pasando ahora mismo.

## Documentación

| Carpeta | Contiene |
|---|---|
| `docs/control/` | Estado, pendientes, decisiones y bitácora |
| `docs/producto/` | Producto, pantallas e inteligencia artificial |
| `docs/tecnico/` | Arquitectura, modelo de datos, contratos, diseño y convenciones |
| `docs/proceso/` | Flujo de trabajo y prompts |
| `docs/tareas/` | Índice de la cola y un ticket por tarea |
| `basedatos/` | Esquema, políticas RLS, almacenamiento y datos semilla |

## Stack

Kotlin, Jetpack Compose, MVVM, Hilt y Navigation Compose.

El backend es **Supabase**: PostgreSQL, Auth, Storage, Realtime y Edge
Functions. La aplicación habla con él a través de `supabase-kt`. Se decidió el
2026-09-14; el porqué está en `docs/control/DECISIONES.md` (DEC-16 y DEC-17).

Mientras cada implementación real llega en su turno, la aplicación corre contra
una fuente de datos en memoria. Eso es a propósito: ver
`docs/tecnico/ARQUITECTURA.md`.
