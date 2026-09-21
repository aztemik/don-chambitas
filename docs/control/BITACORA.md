# Bitácora

> Archivo **vivo**. Quien termina una tarea agrega su renglón al final, y ese
> renglón queda a su cargo: cuando abre el pull request vuelve y le pone el
> número. Los renglones de otros no se tocan y ninguno se borra.
>
> Sirve para dos cosas: que nadie rehaga algo ya hecho, y que el avance del
> proyecto sea auditable de un vistazo.

| Fecha | Tarea | Quién | Rama | Pull request | Nota |
|---|---|---|---|---|---|
| 2026-09-15 | S1-T01 | GRI | docs/S1-T01-analisis-requerimientos | #1 | 33 historias, 9 módulos, 93 criterios. Las 19 pantallas cubiertas |
| 2026-09-15 | S1-T02 | BCJL | docs/S1-T02-alcance-mvp-pantallas | #2 | Cruce limpio: 19/19 pantallas con historia, 33/33 historias con pantalla, 0 choques con FUERA del MVP. Cuatro hallazgos para el líder al final de PANTALLAS.md; H-01 conviene cerrarlo antes de S4-T05 |
| 2026-09-16 | S1-T03 | LMM | docs/S1-T03-validacion-modelo-er | #6 | 90 dio 42/42, 91 25/25 y el paso 2c 10/10 con la anon key contra PostgREST. Diagrama ER y cruce de las 33 historias contra las tablas. Cuatro huecos para el líder, H-05 a H-08; H-08 toca un criterio de aceptación de este mismo ticket |
| 2026-09-20 | S1-T04 | RRC | feat/S1-T04-configuracion-proyecto-android |  | Proyecto configurado en mx.donchambitas.app con AGP 9.2.1, Kotlin 2.2.10, Hilt 2.60.1 con KSP 2.2.10-2.0.2 y supabase-kt 3.1.1 en libs.versions.toml. Compila, pasa pruebas unitarias y corre en emulador Pixel 8 Pro |
| 2026-09-20 | S1-T05 | GRI | chore/S1-T05-configuracion-repositorio-github |  | Repositorio documentado (aztemik/don-chambitas), CODEOWNERS asigna al líder (@aztemik), plantilla de PR en .github/pull_request_template.md, gitignore unificado y reglas de protección para main |

---

## Formato

```
| AAAA-MM-DD | S1-T01 | GRI | feat/S1-T01-analisis-requerimientos | #12 | Nota breve si hace falta |
```

La columna de nota se usa solo cuando pasó algo que el siguiente necesita
saber: un desvío del ticket, una deuda técnica que se dejó a propósito, o algo
que se descubrió y no estaba previsto. Si no pasó nada, se deja vacía.
