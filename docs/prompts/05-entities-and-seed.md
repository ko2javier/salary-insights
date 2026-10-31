# 05 — Entities, repositories and seed

Prompts verbatim, in Spanish as I wrote them, with an English translation below each one.

## 2026-10-31

> Lo siguiente son las entidades Country y Employee con su repositorio, y después el seed. ¿Seguimos igual? r/ si

*Next are the Country and Employee entities with their repositories, then the seed. Same way as before? Answer: yes.*

> Siguen pendientes las dos decisiones del seed:
>
> 1. Cuándo se ejecuta. Mi propuesta es al arrancar, solo si la tabla está vacía, con `seed.employees=10000` en producción y `0` en los tests. R/ Si
> 2. De dónde salen los nombres. Mi recomendación es DataFaker con el idioma de cada país, y emails sacados del número de empleado. R/ Si, para los nombres chinos usa el alfabeto latino , asi se pueden leer mejor .

*The two pending seed decisions. 1. When it runs: on startup, only into an empty table, seed.employees=10000 in production and 0 in tests. Answer: yes. 2. Where names come from: DataFaker in each country's language, emails from the employee number. Answer: yes, and use the Latin alphabet for Chinese names so they are easier to read.*
