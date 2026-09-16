# El producto

## Objetivo

Desarrollar una aplicación móvil Android que permita a las personas que
trabajan de manera informal dar a conocer los servicios, oficios o trabajos que
realizan, facilitando la conexión entre trabajadores y personas que requieren
algún servicio específico.

El cliente publica solicitudes indicando el trabajo que necesita, para que los
trabajadores interesados se postulen. El trabajador crea y publica su perfil
mostrando sus servicios, habilidades, experiencia y datos de contacto, para que
los clientes lo encuentren y lo contacten directamente. La aplicación
incorpora inteligencia artificial para redactar automáticamente perfiles y
descripciones de servicios, clasificar solicitudes por categoría y sugerir
trabajadores adecuados.

## Los dos roles

**Cliente.** Publica lo que necesita, busca trabajadores, revisa postulaciones,
elige a uno, se comunica con él por chat y lo califica al terminar.

**Trabajador.** Crea su perfil, publica los servicios que ofrece, ve las
solicitudes abiertas, se postula, conversa con el cliente y acumula reseñas.

Un usuario elige su rol al registrarse. En el MVP no se cambia de rol ni se
tienen los dos a la vez.

## El recorrido completo

1. El trabajador se registra, arma su perfil y publica sus servicios. Puede
   pedirle a la IA que le redacte la descripción.
2. El cliente se registra y publica una solicitud, o busca directamente en el
   catálogo de trabajadores.
3. El trabajador ve la solicitud abierta y se postula con un mensaje y un
   precio.
4. El cliente revisa las postulaciones, acepta una y la solicitud pasa a
   asignada.
5. Los dos conversan por el chat interno.
6. El cliente cierra la solicitud y deja una reseña con calificación de 1 a 5.
7. Esa reseña sube el promedio del trabajador y lo hace más visible.

## Lo que está DENTRO del MVP

- Registro e inicio de sesión con correo y contraseña, con recuperación.
- Perfil de trabajador con título, descripción, habilidades, experiencia,
  teléfono y ubicación por catálogo.
- Publicación de servicios con categoría, precio y hasta 3 fotos.
- Publicación de solicitudes de trabajo.
- Búsqueda por texto y filtros por categoría, estado, municipio, precio y
  calificación.
- Sistema de postulaciones con aceptar y rechazar.
- Chat interno de texto plano.
- Reseñas y calificación promedio.
- Notificaciones locales.
- Redacción de perfiles y servicios con IA.

## Lo que está FUERA del MVP

> Antes de agregar cualquier cosa, revisa esta lista. Si está aquí, no se hace.
> Si crees que hace falta, se reporta al líder; no se implementa.

| Fuera | Por qué |
|---|---|
| Pagos dentro de la aplicación | Requisitos de Play Store y responsabilidad legal |
| Mapas y geolocalización | Cuesta dinero y permisos. Se usa catálogo |
| Panel administrativo y moderación | Se administra desde la consola de la base de datos |
| Reportar contenido inapropiado | Sin panel de moderación no tiene a dónde llegar |
| Notificaciones push | Depende del backend y cuesta configuración |
| Onboarding | No aporta al objetivo |
| Portafolio o galería del trabajador | Se resuelve con foto de perfil y 3 fotos por servicio |
| Historial y sugerencias en el buscador | Buscador simple por texto y ya |
| Modo sin conexión | La aplicación requiere internet |
| Pantalla propia de detalle de servicio | Los servicios viven dentro del perfil del trabajador |
| Listado de reseñas como pantalla | Se leen dentro del perfil del trabajador |
| Modo oscuro | Duplica el trabajo de diseño |
| Inicio de sesión con Google o teléfono | Configuración extra y costo |
| Adjuntos, audio o llamadas en el chat | Texto plano |
| Versión web | El alcance no da |
| Cambio de rol o rol doble | Complica el modelo sin aportar |
| Verificación de identidad del trabajador | Fuera de alcance académico |

## Cómo sabemos que el MVP está listo

Un cliente y un trabajador, en dos teléfonos distintos, completan el recorrido
de los 7 pasos de arriba de principio a fin sin que nadie toque la base de
datos a mano.
