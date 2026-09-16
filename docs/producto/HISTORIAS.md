# Historias de usuario

> Las historias son el contrato con el usuario: **qué tiene que poder hacer y
> cómo sabemos que quedó bien**. Los tickets las citan por ID (`HU-07`) en vez
> de volver a discutir el alcance.
>
> Salen de `PRODUCTO.md` y de `PANTALLAS.md`. Si una historia y el alcance se
> contradicen, gana `PRODUCTO.md` y se reporta al líder.

## Cómo se lee una historia

Cada una lleva un ID, el rol que la ejecuta, las pantallas que la soportan y
sus criterios de aceptación en formato **dado-cuando-entonces**. Los criterios
son la definición de terminado de esa historia: si uno no se cumple, la
historia no está hecha.

Dos marcas:

- **`OPCIONAL`** — la historia depende de una tarea marcada `opcional` en
  `INDICE.md`. Se desarrolla solo si sobra tiempo y el riesgo lo asume el
  equipo. Ninguna pantalla depende únicamente de una historia opcional.
- **Regla de la base** — el criterio no lo impone la interfaz, lo impone
  PostgreSQL con un trigger o una restricción. La aplicación lo respeta, pero
  aunque se equivoque, la base lo rechaza. Ver `docs/tecnico/MODELO-ER.md`.

## Los nueve módulos

| # | Módulo | Historias | Pantallas |
|---|---|---|---|
| 1 | Autenticación | HU-01 … HU-05 | P-01, P-02, P-03, P-04, P-18 |
| 2 | Perfil del trabajador | HU-06 … HU-08 | P-11, P-07, P-18 |
| 3 | Servicios | HU-09 … HU-11 | P-12, P-13, P-07 |
| 4 | Solicitudes | HU-12 … HU-15 | P-08, P-09, P-19 |
| 5 | Búsqueda | HU-16 … HU-19 | P-05, P-06, P-07 |
| 6 | Postulaciones | HU-20 … HU-23 | P-10, P-14, P-19 |
| 7 | Chat | HU-24 … HU-26 | P-07, P-15, P-16 |
| 8 | Reseñas | HU-27, HU-28 | P-17, P-07 |
| 9 | Inteligencia artificial | HU-29 … HU-33 | P-08, P-11, P-13, P-06 |

---

## 1 · Autenticación

### HU-01 · Crear mi cuenta eligiendo rol

**Rol:** visitante · **Pantallas:** P-03, P-02

> Como persona que llega a la aplicación quiero registrarme eligiendo si soy
> cliente o trabajador, para entrar directo a lo que vine a hacer.

- Dado que estoy en P-03 con nombre, apellidos, correo, contraseña, teléfono y
  rol completos, cuando confirmo el registro, entonces la cuenta se crea y
  entro a la pantalla de inicio que corresponde a mi rol.
- Dado que dejo un campo obligatorio vacío o el correo mal formado, cuando
  intento registrarme, entonces el campo se marca con un mensaje que dice qué
  corregir y no se envía nada.
- Dado que el correo ya está registrado, cuando confirmo, entonces veo
  "El correo ya está registrado, inicia sesión" con acceso directo a P-02.
- Dado que elegí un rol, cuando la cuenta queda creada, entonces ese rol es
  definitivo: en el MVP no se cambia ni se tienen los dos (`DEC-22`).
  **Regla de la base.**

### HU-02 · Iniciar sesión

**Rol:** cliente, trabajador · **Pantallas:** P-02

> Como usuario registrado quiero entrar con mi correo y mi contraseña, para
> retomar donde me quedé.

- Dado que mis credenciales son correctas, cuando inicio sesión, entonces entro
  a P-05 si soy cliente o a P-10 si soy trabajador.
- Dado que el correo o la contraseña no coinciden, cuando lo intento, entonces
  veo "Correo o contraseña incorrectos" sin que se me diga cuál de los dos
  falló.
- Dado que no tengo conexión, cuando lo intento, entonces veo "Revisa tu
  conexión e intenta de nuevo" y los campos conservan lo que escribí.

### HU-03 · Que la aplicación me recuerde

**Rol:** cliente, trabajador · **Pantallas:** P-01

> Como usuario que ya entró antes quiero que la aplicación me reconozca al
> abrirla, para no escribir mi contraseña cada vez.

- Dado que tengo sesión válida, cuando abro la aplicación, entonces P-01 me
  lleva a mi pantalla de inicio sin pedirme nada.
- Dado que no tengo sesión o venció, cuando abro la aplicación, entonces P-01
  me lleva a P-02.
- Dado que estoy en P-01, cuando termina la verificación, entonces no puedo
  volver a esa pantalla con el botón de atrás.

### HU-04 · Recuperar mi contraseña

**Rol:** visitante · **Pantallas:** P-04, P-02

> Como usuario que olvidó su contraseña quiero pedir un enlace de
> recuperación, para volver a entrar sin ayuda de nadie.

- Dado que escribo un correo en P-04, cuando confirmo, entonces veo el mismo
  aviso de "te enviamos el enlace" **exista o no ese correo**: decir cuáles
  están registrados es una fuga de información.
- Dado que recibí el enlace, cuando lo abro y defino una contraseña nueva,
  entonces puedo iniciar sesión con ella.

### HU-05 · Administrar mi cuenta

**Rol:** cliente, trabajador · **Pantallas:** P-18

> Como usuario quiero ver y corregir mis datos, cambiar mi contraseña y cerrar
> sesión, para mantener mi cuenta bajo control.

- Dado que estoy en P-18, cuando edito mi nombre, apellidos o teléfono y
  guardo, entonces el cambio se refleja de inmediato.
- Dado que estoy en P-18, cuando intento cambiar mi correo o mi rol, entonces
  no encuentro dónde hacerlo: ninguno de los dos se edita (`DEC-22`).
  **Regla de la base.**
- Dado que estoy en P-18, cuando cierro sesión, entonces vuelvo a P-02 y la
  sesión guardada se borra del dispositivo.

---

## 2 · Perfil del trabajador

### HU-06 · Armar mi perfil profesional

**Rol:** trabajador · **Pantallas:** P-11

> Como trabajador quiero describir mi oficio, mi experiencia y dónde trabajo,
> para que un cliente entienda qué hago antes de escribirme.

- Dado que estoy en P-11, cuando lleno título, descripción, habilidades, años
  de experiencia, teléfono de contacto, estado y municipio y guardo, entonces
  el perfil queda publicado y aparezco en la búsqueda.
- Dado que elijo un estado, cuando abro el municipio, entonces solo veo los
  municipios de ese estado.
- Dado que mi perfil está incompleto, cuando entro a P-11, entonces veo qué
  falta y por qué me conviene completarlo.
- Dado que me marco como no disponible, cuando un cliente ve mi tarjeta,
  entonces se indica que no estoy tomando trabajos.

### HU-07 · Ponerle cara a mi cuenta

**Rol:** cliente, trabajador · **Pantallas:** P-18, P-07

> Como usuario quiero subir una foto de perfil, para que la otra persona vea
> con quién está tratando.

- Dado que elijo una imagen de la galería o la cámara, cuando la recorto y
  confirmo, entonces se sube y reemplaza a la anterior.
- Dado que la imagen pesa más de 5 MB o no es JPG, PNG o WebP, cuando intento
  subirla, entonces se rechaza con un mensaje que dice el motivo.
  **Regla de la base.**
- Dado que no tengo foto, cuando alguien ve mi perfil, entonces se muestra un
  marcador con mis iniciales, nunca un espacio roto.

### HU-08 · Ver mi perfil como lo ve un cliente

**Rol:** trabajador · **Pantallas:** P-07, P-11

> Como trabajador quiero ver mi perfil público tal como lo ve un cliente, para
> corregir lo que se vea mal antes de que alguien más lo note.

- Dado que estoy en P-11, cuando abro la vista previa, entonces veo P-07 con
  mis datos, habilidades, servicios activos, calificación y reseñas.
- Dado que todavía no tengo servicios ni reseñas, cuando abro la vista previa,
  entonces cada sección muestra un estado vacío que explica qué hacer, no una
  lista en blanco.

---

## 3 · Servicios

### HU-09 · Publicar un servicio

**Rol:** trabajador · **Pantallas:** P-13, P-12

> Como trabajador quiero publicar lo que ofrezco con su precio, para que el
> cliente sepa qué pedirme y cuánto cuesta.

- Dado que estoy en P-13, cuando lleno título, descripción, categoría y precio
  y guardo, entonces el servicio aparece en P-12 y dentro de mi perfil público.
- Dado que elijo la categoría, cuando abro la lista, entonces veo las 16 del
  catálogo y ninguna más.
- Dado que escribo un precio desde mayor que el precio hasta, cuando guardo,
  entonces se rechaza con un mensaje que lo explica. **Regla de la base.**
- Dado que dejo el precio vacío, cuando guardo, entonces se acepta: el precio
  es opcional y la tarjeta se muestra sin él.

### HU-10 · Administrar mis servicios

**Rol:** trabajador · **Pantallas:** P-12, P-13

> Como trabajador quiero editar, pausar o eliminar lo que publiqué, para que mi
> perfil refleje lo que de verdad estoy tomando hoy.

- Dado que estoy en P-12, cuando pauso un servicio, entonces deja de aparecer
  en la búsqueda y en mi perfil público, pero sigue en mi lista para
  reactivarlo.
- Dado que elimino un servicio, cuando confirmo, entonces desaparece junto con
  sus fotos y la acción me pide confirmación antes.
- Dado que no tengo ningún servicio, cuando entro a P-12, entonces veo un
  estado vacío que me invita a publicar el primero.

### HU-11 · Mostrar mi trabajo con fotos

**Rol:** trabajador · **Pantallas:** P-13

> Como trabajador quiero acompañar cada servicio con fotos de trabajos
> anteriores, para que el cliente vea la calidad antes de escribirme.

- Dado que estoy editando un servicio, cuando agrego fotos, entonces puedo
  subir **hasta 3** y la opción de agregar desaparece al llegar a la tercera.
  **Regla de la base.**
- Dado que elimino una foto, cuando confirmo, entonces se borra el archivo
  además del registro: no quedan archivos huérfanos.
- Dado que una foto tarda en subir, cuando espero, entonces veo el progreso y
  puedo cancelar sin perder lo demás del formulario.

---

## 4 · Solicitudes

### HU-12 · Publicar lo que necesito

**Rol:** cliente · **Pantallas:** P-08

> Como cliente quiero describir el trabajo que necesito, para que los
> trabajadores interesados se acerquen a mí en vez de buscarlos uno por uno.

- Dado que estoy en P-08, cuando lleno título, descripción, categoría,
  presupuesto y ubicación y publico, entonces la solicitud queda `abierta` y
  visible para los trabajadores.
- Dado que dejo el presupuesto vacío, cuando publico, entonces se acepta: es
  opcional.
- Dado que soy trabajador, cuando busco dónde publicar una solicitud, entonces
  no existe esa opción en mi interfaz. **Regla de la base.**

### HU-13 · Seguir mis solicitudes

**Rol:** cliente · **Pantallas:** P-09

> Como cliente quiero ver todas mis solicitudes y en qué van, para saber cuál
> necesita que haga algo.

- Dado que tengo solicitudes, cuando entro a P-09, entonces veo cada una con su
  estado: `abierta`, `asignada`, `cerrada` o `cancelada`.
- Dado que una solicitud tiene postulaciones sin revisar, cuando la veo en la
  lista, entonces se distingue cuántas hay.
- Dado que no he publicado ninguna, cuando entro a P-09, entonces veo un estado
  vacío que me lleva a P-08.

### HU-14 · Revisar y cancelar una solicitud

**Rol:** cliente · **Pantallas:** P-19

> Como cliente quiero abrir una solicitud para ver su detalle y cancelarla si
> ya no la necesito, para no dejar a nadie esperando respuesta.

- Dado que abro P-19, cuando la solicitud está `abierta`, entonces veo sus
  datos y las postulaciones recibidas.
- Dado que cancelo una solicitud, cuando confirmo, entonces pasa a `cancelada`
  y deja de recibir postulaciones. **Regla de la base.**
- Dado que soy el trabajador asignado, cuando abro P-19, entonces veo los datos
  del trabajo y el acceso a la conversación, pero ninguna acción de cliente.

### HU-15 · Cerrar el trabajo terminado

**Rol:** cliente · **Pantallas:** P-19, P-17

> Como cliente quiero marcar el trabajo como terminado, para cerrar el trato y
> poder calificar a quien lo hizo.

- Dado que la solicitud está `asignada`, cuando la cierro, entonces pasa a
  `cerrada`, se guarda la fecha de cierre y se me ofrece dejar una reseña.
- Dado que la solicitud está `abierta` y sin trabajador, cuando busco cerrarla,
  entonces la acción no está disponible: cerrar exige trabajador asignado.
  **Regla de la base.**

---

## 5 · Búsqueda

### HU-16 · Explorar por oficio

**Rol:** cliente · **Pantallas:** P-05, P-06

> Como cliente quiero ver los oficios disponibles de un vistazo, para encontrar
> a quien necesito sin saber cómo se llama lo que busco.

- Dado que entro a P-05, cuando veo la pantalla, entonces encuentro el buscador,
  la cuadrícula de las 16 categorías y los trabajadores mejor calificados.
- Dado que toco una categoría, cuando se abre P-06, entonces veo solo
  trabajadores que ofrecen servicios activos de esa categoría.

### HU-17 · Buscar por texto

**Rol:** cliente · **Pantallas:** P-05, P-06

> Como cliente quiero escribir lo que necesito con mis palabras, para no
> depender de adivinar la categoría correcta.

- Dado que escribo "plomeria" sin acento, cuando busco, entonces los resultados
  incluyen a quienes escribieron "plomería" con acento.
- Dado que mi búsqueda no encuentra nada, cuando termina, entonces veo un
  estado vacío que sugiere quitar filtros o revisar la ubicación, no una lista
  en blanco.

### HU-18 · Afinar los resultados

**Rol:** cliente · **Pantallas:** P-06

> Como cliente quiero filtrar y ordenar lo que encontré, para quedarme con
> quien de verdad me sirve.

- Dado que estoy en P-06, cuando aplico filtros de categoría, estado,
  municipio, precio máximo o calificación mínima, entonces la lista se reduce a
  lo que cumple todos a la vez.
- Dado que ordeno por calificación, precio o más recientes, cuando cambia el
  orden, entonces los filtros aplicados se conservan.
- Dado que llego al final de la lista, cuando sigo bajando, entonces se carga
  la siguiente página sin perder mi posición.

### HU-19 · Conocer a un trabajador antes de escribirle

**Rol:** cliente · **Pantallas:** P-07

> Como cliente quiero ver todo lo que un trabajador ofrece y lo que otros
> dijeron de él, para decidir con información y no por la foto.

- Dado que abro P-07, cuando carga, entonces veo en una sola pantalla sus
  datos, habilidades, experiencia, servicios activos con fotos, calificación
  promedio y reseñas.
- Dado que el trabajador no tiene reseñas, cuando veo su perfil, entonces la
  sección lo dice explícitamente en vez de mostrar cero estrellas.
- Dado que estoy en P-07, cuando quiero contactarlo, entonces tengo el botón
  que abre la conversación (`DEC-20`).

---

## 6 · Postulaciones

### HU-20 · Ver en qué puedo trabajar

**Rol:** trabajador · **Pantallas:** P-10

> Como trabajador quiero ver las solicitudes abiertas, para encontrar trabajo
> sin esperar a que alguien me busque.

- Dado que entro a P-10, cuando carga, entonces veo las solicitudes `abiertas`,
  las más recientes primero.
- Dado que filtro por categoría, cuando aplico el filtro, entonces solo quedan
  las de esa categoría.
- Dado que una solicitud dejó de estar abierta, cuando actualizo la lista,
  entonces ya no aparece.

### HU-21 · Ofrecerme para un trabajo

**Rol:** trabajador · **Pantallas:** P-10, P-19

> Como trabajador quiero postularme con un mensaje y mi precio, para competir
> por el trabajo con algo más que mi perfil.

- Dado que abro una solicitud abierta, cuando me postulo con mensaje y precio
  propuesto, entonces mi postulación queda `enviada` y aparece en P-14.
- Dado que ya me postulé a esa solicitud, cuando vuelvo a abrirla, entonces veo
  mi postulación en vez del formulario: solo se permite una.
  **Regla de la base.**
- Dado que la solicitud ya no está `abierta`, cuando intento postularme,
  entonces se rechaza con un mensaje que lo explica. **Regla de la base.**

### HU-22 · Seguir mis postulaciones

**Rol:** trabajador · **Pantallas:** P-14

> Como trabajador quiero ver a qué me postulé y cómo va cada una, para saber
> dónde estoy parado.

- Dado que entro a P-14, cuando carga, entonces veo cada postulación con su
  estado: `enviada`, `aceptada`, `rechazada` o `retirada`.
- Dado que retiro una postulación `enviada`, cuando confirmo, entonces pasa a
  `retirada` y el cliente deja de considerarla.
- Dado que el cliente acepta la mía, cuando abro la aplicación, entonces recibo
  una notificación local y la postulación aparece como `aceptada`.
- Dado que estoy en P-14, cuando miro una solicitud, entonces **no** veo las
  postulaciones de otros trabajadores ni cuántos compiten conmigo.
  **Regla de la base.**

### HU-23 · Elegir a quién le doy el trabajo

**Rol:** cliente · **Pantallas:** P-19

> Como cliente quiero comparar las postulaciones y quedarme con una, para
> cerrar el trato con quien me convenga.

- Dado que abro P-19, cuando hay postulaciones, entonces veo de cada una el
  mensaje, el precio propuesto, y el nombre y la calificación del trabajador,
  con acceso a su perfil.
- Dado que acepto una postulación, cuando confirmo, entonces esa queda
  `aceptada`, **todas las demás pasan a `rechazada`** y la solicitud queda
  `asignada` a ese trabajador, todo junto o nada. **Regla de la base.**
- Dado que rechazo una postulación suelta, cuando confirmo, entonces esa queda
  `rechazada` y la solicitud sigue `abierta` para las demás.

---

## 7 · Chat

### HU-24 · Ponerme en contacto

**Rol:** cliente · **Pantallas:** P-07, P-19, P-16

> Como cliente quiero escribirle a un trabajador dentro de la aplicación, para
> acordar los detalles sin darle mi teléfono a un desconocido.

- Dado que estoy en P-07 o en P-19, cuando toco contactar, entonces se abre la
  conversación en P-16.
- Dado que ya había escrito antes a esa persona por ese mismo trabajo, cuando
  vuelvo a contactarla, entonces entro al hilo existente y no se crea uno nuevo.
- Dado que soy trabajador, cuando busco cómo iniciar una conversación, entonces
  no existe: el hilo lo abre siempre el cliente y yo respondo (`DEC-20`).

### HU-25 · Encontrar mis conversaciones

**Rol:** cliente, trabajador · **Pantallas:** P-15

> Como usuario quiero ver todos mis hilos en un solo lugar, para retomar el que
> tiene algo pendiente.

- Dado que entro a P-15, cuando carga, entonces veo mis conversaciones
  ordenadas por el mensaje más reciente.
- Dado que tengo mensajes sin leer, cuando veo la bandeja, entonces esos hilos
  se distinguen de los demás.
- Dado que no tengo ninguna conversación, cuando entro a P-15, entonces veo un
  estado vacío que explica cómo empieza un contacto.

### HU-26 · Acordar los detalles

**Rol:** cliente, trabajador · **Pantallas:** P-16

> Como usuario quiero conversar por texto con la otra persona, para ponernos de
> acuerdo en fecha, lugar y precio.

- Dado que estoy en P-16, cuando la otra persona escribe, entonces su mensaje
  aparece sin que yo actualice nada.
- Dado que envío un mensaje, cuando se entrega, entonces lo veo con su hora y
  la bandeja de la otra persona sube ese hilo al principio.
- Dado que abro un hilo con mensajes sin leer, cuando entro, entonces quedan
  marcados como leídos.
- Dado que recibo un mensaje con la aplicación cerrada, cuando la abro,
  entonces una notificación local me lo hizo saber.
- Dado que intento adjuntar una foto, un audio o hacer una llamada, entonces no
  existe esa opción: el chat es de texto plano (`DEC-04`).

---

## 8 · Reseñas

### HU-27 · Calificar el trabajo recibido

**Rol:** cliente · **Pantallas:** P-17

> Como cliente quiero calificar y comentar el trabajo que me hicieron, para
> ayudar a quien busque después de mí.

- Dado que cerré la solicitud, cuando abro P-17, entonces puedo dar de 1 a 5
  estrellas y escribir un comentario.
- Dado que la solicitud no está `cerrada`, cuando busco calificar, entonces la
  acción no está disponible. **Regla de la base.**
- Dado que ya dejé mi reseña de esa solicitud, cuando vuelvo, entonces la veo
  publicada y no puedo editarla ni borrarla: una por solicitud, y son
  definitivas. **Regla de la base.**

### HU-28 · Que mi reputación me consiga trabajo

**Rol:** trabajador · **Pantallas:** P-07, P-06

> Como trabajador quiero que las buenas reseñas me hagan más visible, para que
> hacer bien el trabajo se note.

- Dado que recibo una reseña nueva, cuando se publica, entonces mi promedio se
  recalcula y se ve actualizado en mi perfil y en las tarjetas de resultados.
- Dado que un cliente ordena por calificación en P-06, cuando se aplica el
  orden, entonces aparezco según mi promedio y mi número de reseñas.

---

## 9 · Inteligencia artificial

### HU-29 · Que la IA redacte mi presentación

**Rol:** trabajador · **Pantallas:** P-11

> Como trabajador al que no se le da escribir quiero que la aplicación redacte
> mi descripción a partir de unos datos sueltos, para no quedarme sin perfil
> por no saber cómo presentarme.

- Dado que escribí mi oficio y mis años de experiencia, cuando pido redactar
  con IA, entonces recibo un texto en primera persona de entre 40 y 80 palabras.
- Dado que recibo el texto, cuando lo leo, entonces puedo editarlo o
  descartarlo antes de guardar: la IA propone, yo decido.
- Dado que el texto se generó, cuando lo reviso, entonces no inventa
  certificaciones, premios ni precios que yo no escribí.

### HU-30 · Que la IA describa mi servicio

**Rol:** trabajador · **Pantallas:** P-13

> Como trabajador quiero que la IA convierta cuatro datos en una descripción
> presentable de mi servicio, para publicar más rápido y mejor.

- Dado que estoy en P-13 con título y categoría, cuando pido redactar con IA,
  entonces la descripción se llena y queda editable.
- Dado que ya había escrito algo, cuando la IA propone un texto, entonces se me
  pregunta antes de reemplazarlo.

### HU-31 · Que la IA nunca me deje atorado

**Rol:** trabajador, cliente · **Pantallas:** P-08, P-11, P-13

> Como usuario quiero poder escribir a mano siempre, para que un fallo o un
> límite de la IA no me impida publicar.

- Dado que agoté mis 10 usos del día, cuando abro la pantalla, entonces el
  botón está deshabilitado con el mensaje "Alcanzaste el límite de hoy" y el
  campo sigue siendo mío para escribir. El tope son 10 **en total**, no 10 por
  función (`DEC-18`).
- Dado que la IA no responde, cuando falla, entonces el campo se queda como
  estaba, veo un aviso y la pantalla sigue funcionando.
- Dado que pido dos veces exactamente lo mismo, cuando llega la respuesta,
  entonces es inmediata y no me consume un uso del día.

### HU-32 · `OPCIONAL` · Que la IA adivine la categoría

**Rol:** cliente · **Pantallas:** P-08

> Como cliente que no sabe en qué categoría cae lo que necesita quiero que la
> aplicación la sugiera a partir de mi descripción, para no equivocarme y no
> recibir postulaciones de quien no me sirve.

- Dado que escribí la descripción del trabajo, cuando la IA propone una
  categoría, entonces se preselecciona y puedo cambiarla libremente.
- Dado que la IA no responde, cuando publico, entonces elijo la categoría a
  mano como siempre.

### HU-33 · `OPCIONAL` · Que la IA me sugiera a quién llamar

**Rol:** cliente · **Pantallas:** P-06

> Como cliente con una solicitud publicada quiero ver qué trabajadores encajan
> mejor con lo que pedí, para no revisar la lista entera.

- Dado que tengo una solicitud, cuando pido sugerencias, entonces veo
  trabajadores de esa categoría ordenados por qué tan bien encajan.
- Dado que la sugerencia falla, cuando abro la lista, entonces se muestra
  ordenada por calificación promedio y la pantalla no se rompe.

---

## Cobertura de las 19 pantallas

Cada pantalla de `PANTALLAS.md` aparece al menos en una historia. Ninguna
depende únicamente de una historia `OPCIONAL`.

| Pantalla | Historias |
|---|---|
| P-01 Splash | HU-03 |
| P-02 Iniciar sesión | HU-01, HU-02, HU-04 |
| P-03 Registro | HU-01 |
| P-04 Recuperar contraseña | HU-04 |
| P-05 Inicio cliente | HU-16, HU-17 |
| P-06 Resultados | HU-16, HU-17, HU-18, HU-28, HU-33 |
| P-07 Perfil público | HU-07, HU-08, HU-19, HU-24, HU-28 |
| P-08 Publicar solicitud | HU-12, HU-31, HU-32 |
| P-09 Mis solicitudes | HU-13 |
| P-10 Inicio trabajador | HU-20, HU-21 |
| P-11 Mi perfil | HU-06, HU-08, HU-29, HU-31 |
| P-12 Mis servicios | HU-09, HU-10 |
| P-13 Crear o editar servicio | HU-09, HU-10, HU-11, HU-30, HU-31 |
| P-14 Mis postulaciones | HU-22 |
| P-15 Conversaciones | HU-25 |
| P-16 Chat | HU-24, HU-26 |
| P-17 Dejar reseña | HU-15, HU-27 |
| P-18 Mi cuenta | HU-05, HU-07 |
| P-19 Detalle de solicitud | HU-14, HU-15, HU-21, HU-23, HU-24 |

## Cobertura del recorrido de los 7 pasos

Los siete pasos de `PRODUCTO.md`, en orden:

| # | Paso | Historias |
|---|---|---|
| 1 | El trabajador se registra, arma su perfil y publica sus servicios | HU-01, HU-06, HU-09, HU-11, HU-29, HU-30 |
| 2 | El cliente se registra y publica una solicitud, o busca en el catálogo | HU-01, HU-12, HU-16, HU-17, HU-18, HU-19 |
| 3 | El trabajador ve la solicitud abierta y se postula | HU-20, HU-21 |
| 4 | El cliente acepta una y la solicitud pasa a asignada | HU-23 |
| 5 | Los dos conversan por el chat interno | HU-24, HU-25, HU-26 |
| 6 | El cliente cierra la solicitud y deja una reseña | HU-15, HU-27 |
| 7 | Esa reseña sube el promedio y lo hace más visible | HU-28 |

## Lo que estas historias NO cubren

Nada de la lista "FUERA del MVP" de `PRODUCTO.md` tiene historia, y es a
propósito: pagos en la aplicación, mapas y geolocalización, panel
administrativo, reportar contenido, notificaciones push, onboarding,
portafolio del trabajador, historial de búsqueda, modo sin conexión, pantalla
propia de detalle de servicio, listado de reseñas como pantalla, modo oscuro,
inicio de sesión con Google o teléfono, adjuntos en el chat, versión web,
cambio de rol y verificación de identidad.

Si una historia futura necesita algo de esa lista, **no se escribe**: se
reporta al líder y él decide.
