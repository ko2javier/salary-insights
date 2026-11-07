# 10 — Search normalization

Prompts verbatim, in Spanish as I wrote them, with an English translation below each one.

## 2026-11-01

> es que son varios casos ..
> no es lo mismo
> Jose!= jose!= José!= JOSE!= JOSË= !SOSÉ
> etc..
> y para eso haria falta un criterio unico normalizador a la hora de introducir los datos y realizar las busquedas !!!!
> para colmo tenemos personas de GE que tambien tiene nombres raros?

*"The thing is there are several cases... it's not the same: Jose != jose != José != JOSE != JOSË != JOSÉ, etc. And for that we'd need a single normalizing criterion both when entering the data and when searching!!!! On top of that, we have people from Germany who also have unusual names?"*

> si añadimos una nueva col.. tambien sera necesario para la V1/V2 o vamos directo ya a la v3 ??

*"If we add a new column... will we also need to change V1/V2, or do we go straight to V3??"*

> Si

*"Yes."*

> Revierte los commits anteriores. Añadiremos esto a las posibles mejoras. Esto estaria out of scope.
> Añade a las posibles mejoras alcomo como :
> La búsqueda por nombre no ignora acentos: lower() de SQLite solo trata ASCII, así que “jose” no encuentra “José”. Mejora: una columna search_name normalizada en Java (NFD sin marcas, minúsculas con Locale.ROOT), calculada al guardar y usada en la búsqueda. Fuera de alcance porque el seed no usa acentos y el impacto en este ejercicio es mínimo.

*"Revert the previous commits. We'll add this to the possible improvements. This would be out of scope. Add to the possible improvements something like: Name search does not ignore accents: SQLite's lower() only handles ASCII, so "jose" does not find "José". Improvement: a search_name column normalized in Java (NFD without marks, lower case with Locale.ROOT), computed on save and used by the search. Out of scope because the seed uses no accents and the impact on this exercise is minimal."*
