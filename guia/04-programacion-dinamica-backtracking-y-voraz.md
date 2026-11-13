# Programación dinámica, backtracking y voraz

[← Anterior: Búsqueda de caminos](03-busqueda-de-caminos.md) · [Índice](README.md) · [Siguiente: Rangos e intervalos →](05-rangos-e-intervalos.md)

Cuando la pregunta es "¿de cuántas formas…?" o el árbol de opciones crece exponencialmente, **la solución es encontrar un estado chico que se repite** y guardar su resultado. Con el estado correcto, una respuesta de 10¹⁴ sale en milisegundos.

Días de este tipo: 2023: 12 · 2024: 7, 9, 11, 19, 21 · 2025: 3, 11, 12.

## Programación dinámica y memoización

- Definir el estado con lo mínimo que determina el futuro: (posición en el texto, grupo actual) en 2023 día 12; (posición en el diseño) en 2024 día 19; (número, pasos restantes) en 2024 día 11.
- Contar en vez de enumerar: en 2024 día 11 hay billones de piedras pero solo unos 3800 números distintos; se cuenta cuántas hay de cada uno.
- Niveles anidados: en 2024 día 21 el costo de un movimiento en el teclado `k` depende del nivel `k − 1`; con memoria por (movimiento, nivel) pasar de 2 a 25 niveles no cuesta nada.
- Caminos en un grafo sin ciclos (DAG): cantidad de caminos = suma de los caminos de los vecinos (2025 día 11). Pasar por dos nodos obligatorios es multiplicar los tramos.
- Memoización en Java: `HashMap.computeIfAbsent` recursivo lanza `ConcurrentModificationException`. Hay que consultar, calcular y después guardar (en el repo, `Memoizador`).
- La recursión profunda puede agotar la pila: convertir a tabla iterativa, o aumentar el tamaño de la pila del hilo.

## Backtracking y búsqueda exhaustiva

- Podar temprano: en 2024 día 7 se trabaja hacia atrás desde el resultado, deshaciendo cada operador; si una división no es exacta, esa rama muere.
- Cotas que deciden sin buscar: en 2025 día 12, una cota por área y otra por bloques de 3 × 3 resuelven casi todos los casos; el backtracking queda para los dudosos.

## Voraz (greedy)

- Vale cuando una elección local nunca se arrepiente: el mayor dígito que deja lugar a los restantes en 2025 día 3. Antes de usarlo, hay que convencerse de por qué es óptimo, o compararlo con fuerza bruta.
- Compactar con dos punteros, o poner cada elemento en el primer hueco donde entra (2024 día 9).

**En el repo**: `Memoizador`.
