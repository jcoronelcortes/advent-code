# Texto, listas y secuencias

[← Índice](README.md) · [Siguiente: Grillas y simulación →](02-grillas-y-simulacion.md)

El primer paso de cada día es leer el input sin suponer nada: **validar el formato y fallar con un mensaje claro** ahorra horas de buscar un error en la lógica que en realidad era de lectura.

Días de este tipo: 2023: 1, 2, 4, 7, 9, 15 · 2024: 1, 2, 3, 5 · 2025: 6.

## Leer el input

- Formatos que se repiten: una grilla, un número o una lista de números por línea, bloques separados por una línea en blanco, `clave: valores`, y una sola línea enorme separada por comas.
- Expresiones regulares (`Pattern`, `Matcher`) con `matches()` para exigir la línea entera, y grupos para extraer partes. Ej.: `mul\((\d{1,3}),(\d{1,3})\)` en 2024 día 3.
- `String.split(",", -1)`: el `-1` conserva los campos vacíos del final, que sin él desaparecen en silencio.
- Exigir cantidades exactas (seis números por línea, dos secciones) en vez de indexar y rezar. En el repo: `Utiles.leerNumeros(texto, n)` y `Utiles.secciones(lineas, n)`.
- Normalizar al leer: extremos en cualquier orden (2023 día 22), líneas de distinto largo, espacios finales.

## Trampas típicas del texto

- Coincidencias que se solapan: en `oneight` están `one` y `eight` (2023 día 1). Buscar desde cada punta, no reemplazar.
- Los espacios significan algo: en 2025 día 6 las columnas de espacios separan problemas, así que no se puede hacer `strip()`.
- Códigos ASCII y aritmética de caracteres: `c - '0'` para un dígito, `(valor + c) * 17 % 256` en 2023 día 15.

## Ordenar, contar y comparar

- Un `Comparator` compuesto (`comparing(…).thenComparing(…)`) para órdenes con desempate, como las manos de 2023 día 7.
- Reglas de orden parcial usadas como comparador (2024 día 5). Solo vale si las reglas cubren todos los pares que se comparan.
- Contar apariciones con `Map.merge(clave, 1, Integer::sum)` o un `Multiset` de Guava (2024 día 1).
- Ventanas consecutivas con `Gatherers.windowSliding(2)` (Java 24+) para diferencias entre vecinos (2024 día 2).
- Una pasada en orden en vez de simular copias: las tarjetas de 2023 día 4 se cuentan acumulando instancias, sin crearlas.
- Estructuras con orden de inserción: `LinkedHashMap` reemplaza sin mover y quita sin desordenar (2023 día 15).

## Secuencias numéricas

- Diferencias sucesivas hasta llegar a ceros: es extrapolar un polinomio (2023 día 9). La parte 2 suele ser la misma recursión hacia el otro lado.

**En el repo**: `Utiles` (números con y sin cantidad exacta, líneas no vacías, secciones).
