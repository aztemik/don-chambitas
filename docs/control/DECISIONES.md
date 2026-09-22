# Decisiones tomadas

> **Este archivo lo escribe únicamente el líder.** Registra lo que ya se
> decidió y, sobre todo, **por qué**. Sirve para que dentro de dos meses nadie
> vuelva a discutir lo mismo.
>
> Una decisión aquí no se revierte por conveniencia de una tarea. Se revierte
> agregando una decisión nueva que diga que la anterior queda sin efecto.

---

| ID | Decisión | Por qué |
|---|---|---|
| DEC-01 | Android nativo con Kotlin y Jetpack Compose | El equipo ya sabe Kotlin. Se deja la puerta abierta a KMP más adelante sin rehacer la capa de datos. |
| DEC-02 | Solo Android. No hay versión web | El alcance no da para dos plataformas en 3 meses. El objetivo es publicar en Play Store. |
| DEC-03 | Autenticación con correo y contraseña | Google requiere configuración adicional y el OTP por SMS cuesta dinero. |
| DEC-04 | Chat interno de texto plano | El objetivo pide contacto directo. Sin adjuntos, sin audio, sin llamadas. |
| DEC-05 | Ubicación por catálogo de estado y municipio | Google Maps requiere tarjeta y permisos de ubicación. El catálogo resuelve el filtro sin ninguna de las dos cosas. |
| DEC-06 | Sin pagos dentro de la aplicación | Los pagos disparan requisitos de Play Store y responsabilidad legal que este equipo no puede sostener. |
| DEC-07 | Base de datos PostgreSQL | Es el motor común a las dos opciones de backend, así que el esquema se escribe una sola vez. |
| DEC-08 | OpenAI como proveedor de IA, consumido desde un proxy | La llave de la API no puede vivir en el APK. Ver `docs/producto/IA.md`. |
| DEC-09 | Paleta "Taller": mostaza y terracota | La identidad nace del casco de seguridad. Ver `docs/tecnico/DISENO.md`. |
| DEC-10 | 19 pantallas en el MVP, ni una más | Ver `docs/producto/PANTALLAS.md`. Todo lo demás quedó fuera a propósito. |
| DEC-11 | Todo en español: código, commits y documentación | Un solo idioma evita el híbrido que nadie lee. Se exceptúan las palabras del framework. |
| DEC-12 | Repositorio personal, un pull request por tarea, aprueba solo el líder | Mantiene la calidad y le da al líder visibilidad real del avance. |
| DEC-13 | Sin panel administrativo | Un equipo de 4 no modera nada en 3 meses. La administración se hace desde la consola de la base de datos. |
| DEC-14 | Sin notificaciones push | Depende del backend y cuesta configuración. Se usan notificaciones locales. |
| DEC-15 | Sin modo oscuro en el MVP | Duplica el trabajo de diseño y no aporta al objetivo. |
| DEC-16 | **Supabase como backend y base de datos.** Cierra PEND-01 | Resuelve autenticación, PostgreSQL, almacenamiento de imágenes, tiempo real y funciones de servidor de un solo golpe. Un backend propio desde cero no cabe en 3 meses con 4 personas, y la parte que el requisito académico pide implementar se cubre con el esquema, las políticas RLS y las Edge Functions, que son trabajo real de backend. Tomada el 2026-09-14. |
| DEC-17 | `supabase-kt` como cliente. Se retira Retrofit y OkHttp del stack | Un solo cliente cubre Postgrest, Auth, Storage, Realtime y Functions, con la sesión y el refresco de token ya resueltos. Mantener Retrofit contra PostgREST obligaba a escribir a mano justo lo que DEC-16 vino a ahorrar. Deja sin efecto el renglón "Red" anterior de `ARQUITECTURA.md`. |
| DEC-18 | El tope diario de IA son 10 llamadas por usuario al día **en total**, no por función | `IA.md` y `CONVENCIONES.md` se contradecían. Gana el total: el presupuesto es el costo, y el costo no distingue de qué función viene la llamada. La tabla `ia_consumo` conserva la desagregación por función para poder medir, pero el tope se evalúa sobre la suma. |
| DEC-19 | La ficha de un trabajador es visible **entera** —correo y teléfono incluidos— para cualquier usuario con sesión | `vw_busqueda_trabajadores` es `security_invoker` y necesita el join contra `usuarios`; cerrarlo pedía una vista curada aparte o permisos por columna que le quitarían al propio usuario su teléfono en P-18. El trabajador publica su contacto a propósito: es a lo que vino a la aplicación. La ficha del **cliente** sigue cerrada: solo él y su contraparte. Hay que declararlo en el formulario de seguridad de los datos de Play Store (S6-T08). Tomada el 2026-09-15. |
| DEC-20 | La conversación la abre **siempre el cliente**. El trabajador responde, no inicia | `fn_abrir_conversacion` fija `cliente_id = auth.uid()` y `conversaciones.trabajador_id` apunta a `perfiles_trabajador`, así que un trabajador no puede crear el hilo. Coincide con el recorrido de `PRODUCTO.md`: el contacto nace en P-07 o al aceptar la postulación. Evita que el chat se use para ofrecerse en frío. Tomada el 2026-09-15. |
| DEC-21 | Que aceptar una postulación sea atómico es regla del **contrato**, no de la base | El cliente puede llevar su propia solicitud a `asignada` con un `update` suelto: es su fila, la política se lo permite y el `CHECK` solo exige que `trabajador_id` no sea nulo. Cerrarlo pedía un trigger de transición de estado que hoy no cabe. El único que puede hacerlo es el dueño de la solicitud, así que el daño se lo hace a sí mismo. La aplicación usa `fn_aceptar_postulacion` siempre. Tomada el 2026-09-15. |
| DEC-22 | Los roles son excluyentes y no se cambian en el MVP | Ya estaba decidido en `PRODUCTO.md` —"un usuario elige su rol al registrarse", y "cambio de rol o rol doble" está en la lista FUERA del MVP— pero sin número, y `MODELO-ER.md` lo citaba como un `DEC` que no existía. Se registra para cerrar esa referencia. La base lo sostiene con llaves foráneas compuestas contra `usuarios(id, rol)`. Tomada el 2026-09-15. |
| DEC-23 | Los filtros de búsqueda de P-06 son **cinco**: categoría, estado, municipio, precio y calificación | `PRODUCTO.md`, el renglón de P-06 y el título de `S4-T05` decían tres; HU-18 pedía cinco y el estado vacío de HU-17 ya hablaba de la ubicación. Gana la versión de cinco por tres razones: `DEC-05` adoptó el catálogo de estado y municipio precisamente porque "resuelve el filtro", HU-06 ya obliga al trabajador a capturar ambos, así que el dato existe sin costo extra, y filtrar por catálogo no es "mapas y geolocalización", que es lo que quedó fuera del MVP. Cuesta dos desplegables más en `S4-T05`. Al implementarla, notar que `PEND-02` sigue abierto y hoy solo hay 26 municipios sembrados. Tomada el 2026-09-15 a partir del hallazgo H-01 de `S1-T02`. |
| DEC-24 | Hay **un hilo de chat por cliente, trabajador y solicitud**, más **un hilo suelto** para el contacto que nace en P-07 sin solicitud | HU-24 solo hablaba de "ese mismo trabajo" y dejaba sin definir el contacto desde P-07, donde no hay solicitud de por medio. No hubo nada que elegir: el esquema ya lo resolvía. `conversaciones.solicitud_id` es nulable, el índice único `uq_conversacion_unica` va sobre `(cliente_id, trabajador_id, coalesce(solicitud_id, uuid-cero))` y `fn_abrir_conversacion` recibe `p_solicitud_id` con valor por omisión nulo y devuelve el hilo si ya existe. Se registra para que `S1-T03` y `S5-T06` no lo vuelvan a discutir. Complementa `DEC-20`. Tomada el 2026-09-15 a partir del hallazgo H-04 de `S1-T02`. |
| DEC-25 | **El registro deja sesión abierta.** Al confirmar el alta el usuario entra directo a la pantalla de su rol, y la sesión vive hasta que él la cierre | Cierra `H-10`. HU-01 ya lo daba por hecho y el líder lo confirmó: pedirle al recién registrado que inicie sesión otra vez es fricción sin ganancia. Tiene dos consecuencias técnicas que no son opcionales. **Primera: la confirmación por correo de Supabase Auth queda desactivada**, porque con ella activa `signUp` no abre sesión y la decisión no se puede cumplir. **Segunda: `registrar` tiene que devolver `Sesion`, no `Usuario`**, así que `CONTRATOS-API.md` y `RepositorioAuth` cambian. Lo recoge `S2-T06` y lo implementa `S2-T07`. De paso deja de estorbar lo que anotó `S1-T03`: sin confirmación por correo, el dominio de prueba `@prueba.donchambitas.mx` ya no necesita ser un dominio que exista. Tomada el 2026-09-22 a partir del hallazgo H-10 de `S2-T01`. |
| DEC-26 | **El catálogo de municipios se queda en los 26 sembrados.** Cierra PEND-02 | El líder lo cerró el 2026-09-22: con los de las zonas de prueba basta para el MVP. Cargar los 2,469 del INEGI era un `COPY` desde CSV que no le urgía a ninguna tarea. `S4-T05` construye y prueba los cinco filtros de `DEC-23` igual, y `S6-T10` corre las pruebas cerradas dentro de esas zonas. Si algún día hace falta el catálogo completo, se carga sin tocar código: es dato, no esquema. |

---

## Plantilla

```
| DEC-NN | Qué se decidió | Por qué se decidió eso y no la alternativa |
```
