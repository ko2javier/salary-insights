# 03 — Schema and seed

Prompts verbatim, in Spanish as I wrote them, with an English translation below each one.

## 2026-10-31

> España	EUR
> Alemania	EUR
> Reino Unido	GBP
> Estados Unidos	USD
> China	CNY
> India	INR
> México	MXN
>
> tasas
>
> | País | Moneda | usd_per_unit | Origen |
> |---|---|---|---|
> | España | EUR | 1.1199 | publicada |
> | Alemania | EUR | 1.1199 | publicada |
> | Reino Unido | GBP | 1.3375 | publicada |
> | Estados Unidos | USD | 1 | — |
> | China | CNY | 0.149921 | 1 / 6.6702 |
> | India | INR | 0.010533 | 1 / 94.94 |
> | México | MXN | 0.053580 | 1 / 18.6638 |

*Countries: Spain, Germany, United Kingdom, United States, China, India, Mexico, with their currencies. Rates as USD per unit of local currency; "publicada" = published rate, the others derived as the inverse of the published units-per-USD rate.*

> Sources:
>
> * [US Federal Reserve (H.10) Exchange Rates — AllRatesToday](https://allratestoday.com/central-bank-rates-api/fed/)

*Source of the rates.*

> la tabla de pesos .. no pueden ser todos directivos.. es necesario afinar un poco + .. dame los pesos y como saldrian mas menos los resultados

*The weights table: they can't all be managers, it needs some tuning. Give me the weights and roughly what the results would look like.*

> veo bien lo de los pesos, deja recogido esto en una tabla/docs sobre esta desicion .

*The weights look right. Record this decision in a table / doc.*

> Lo siguiente son las bandas salariales por cargo y país. ¿Seguimos? R/ Adelante..

*Next are the salary bands per job title and country. Shall we continue? Answer: go ahead.*

> india 0.23 .. lo subimos .

*India 0.23, let's raise it.*

> 2 Añadir un ajuste por departamento en algunos países, por ejemplo ingeniería ×1,3 en India y China. Son 6-10 números más, y la Q5 enseña diferencias de verdad.
>
> R/ hacemos esto

*Option 2, add a per-department adjustment in some countries. Answer: let's do this.*

> Las decisiones de datos ya están cerradas. Lo siguiente es la migración V1 de Flyway. ¿La escribes tú o la escribo yo para que la revises?
> R/ escribela tu y yo la reviso

*The data decisions are closed; next is the V1 Flyway migration. Answer: you write it and I'll review it.*

> Escribe la fecha desde mi zona, léela con la JVM en UTC y confirma que Java recibe 2024-01-15. Déjalo como test.  Si pasa, el problema está cerrado del todo.??

*Write the date from my time zone, read it with the JVM in UTC and confirm Java gets 2024-01-15. Leave it as a test. If it passes, is the problem fully closed?*
