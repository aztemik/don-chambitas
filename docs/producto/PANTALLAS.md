# Pantallas del MVP

19 pantallas. No hay una vigésima. Si un ticket parece necesitar una pantalla
que no está aquí, detente y repórtalo.

Cada pantalla se identifica por su ID (`P-07`). Los tickets citan el ID en vez
de describir la pantalla otra vez.

---

## Autenticación

| ID | Pantalla | Qué hace |
|---|---|---|
| P-01 | Splash | Logo, verifica si hay sesión guardada y decide a dónde va. Sin onboarding detrás |
| P-02 | Iniciar sesión | Correo, contraseña, enlace a registro y a recuperación |
| P-03 | Registro | Nombre, apellidos, correo, contraseña, teléfono y **selección de rol** |
| P-04 | Recuperar contraseña | Se pide el correo, se avisa que se envió el enlace |

## Cliente

| ID | Pantalla | Qué hace |
|---|---|---|
| P-05 | Inicio cliente | Buscador, cuadrícula de categorías y trabajadores mejor calificados |
| P-06 | Resultados | Lista de trabajadores con filtros de categoría, precio y calificación |
| P-07 | Perfil público del trabajador | Datos, habilidades, **sus servicios**, **sus reseñas** y botón de contactar |
| P-08 | Publicar solicitud | Título, descripción, categoría, presupuesto y ubicación. Botón de redactar con IA |
| P-09 | Mis solicitudes | Lista de las solicitudes del cliente con su estado |

## Trabajador

| ID | Pantalla | Qué hace |
|---|---|---|
| P-10 | Inicio trabajador | Solicitudes abiertas, filtrables por categoría |
| P-11 | Mi perfil | Ver y editar título, descripción, habilidades, experiencia, contacto y ubicación |
| P-12 | Mis servicios | Lista de servicios publicados con editar, pausar y eliminar |
| P-13 | Crear o editar servicio | Título, descripción, categoría, precio, hasta 3 fotos. Botón de redactar con IA |
| P-14 | Mis postulaciones | Lista de postulaciones enviadas con su estado |

## Compartidas

| ID | Pantalla | Qué hace |
|---|---|---|
| P-15 | Conversaciones | Bandeja de chats ordenada por mensaje más reciente |
| P-16 | Chat | Conversación de texto plano con un usuario |
| P-17 | Dejar reseña | Calificación de 1 a 5 estrellas y comentario. Solo tras cerrar una solicitud |
| P-18 | Mi cuenta | Datos personales, foto, cambiar contraseña, cerrar sesión |
| P-19 | Detalle de solicitud | Datos de la solicitud, postulaciones recibidas y acciones según el rol |

---

## Navegación

**Antes de iniciar sesión:** P-01 → P-02 ↔ P-03 ↔ P-04

**Cliente, barra inferior de 4:** P-05 Inicio · P-09 Solicitudes · P-15 Chats ·
P-18 Cuenta. Botón flotante en P-05 que lleva a P-08.

**Trabajador, barra inferior de 4:** P-10 Inicio · P-12 Servicios ·
P-15 Chats · P-18 Cuenta. Acceso a P-11 y P-14 desde P-18 y P-12.

**Pantallas que se abren encima, sin barra inferior:** P-04, P-06, P-07, P-08,
P-13, P-16, P-17, P-19.

## Decisiones de recorte que están dentro de este mapa

**El detalle de servicio no es pantalla.** Los servicios se leen dentro del
perfil del trabajador (P-07). La búsqueda devuelve trabajadores, no servicios
sueltos.

**Las reseñas no tienen pantalla de listado.** Se leen dentro de P-07 y solo se
escriben en P-17.

**No hay onboarding.** P-01 lleva directo a P-02.

**El chat lo abre siempre el cliente** (DEC-20). El botón de contactar está en
P-07 y en P-19 del lado del cliente. El trabajador ve el hilo en P-15 y
responde en P-16, pero no tiene desde dónde iniciar uno.
