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
| 2026-09-15 | S1-T02 | BCJL | docs/S1-T02-alcance-mvp-pantallas | _sin abrir_ | Cruce limpio: 19/19 pantallas con historia, 33/33 historias con pantalla, 0 choques con FUERA del MVP. Cuatro hallazgos para el líder al final de PANTALLAS.md; H-01 conviene cerrarlo antes de S4-T05 |

---

## Formato

```
| AAAA-MM-DD | S1-T01 | GRI | feat/S1-T01-analisis-requerimientos | #12 | Nota breve si hace falta |
```

La columna de nota se usa solo cuando pasó algo que el siguiente necesita
saber: un desvío del ticket, una deuda técnica que se dejó a propósito, o algo
que se descubrió y no estaba previsto. Si no pasó nada, se deja vacía.
