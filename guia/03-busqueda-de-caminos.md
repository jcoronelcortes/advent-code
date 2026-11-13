# Búsqueda de caminos

[← Anterior: Grillas y simulación](02-grillas-y-simulacion.md) · [Índice](README.md) · [Siguiente: Programación dinámica, backtracking y voraz →](04-programacion-dinamica-backtracking-y-voraz.md)

Un "camino más corto" casi nunca se busca solo sobre casillas: **el nodo del grafo es el estado completo** (casilla, dirección, pasos en línea recta…), y el grafo se genera sobre la marcha, sin construirlo.

Días de este tipo: 2023: 17 · 2024: 10, 16, 18, 20.

## Qué algoritmo

| Situación | Algoritmo | Días |
| --- | --- | --- |
| Todos los pasos cuestan lo mismo | BFS | 2024: 10, 18, 20 · 2023: 21 |
| Costos distintos, no negativos | Dijkstra | 2023: 17 · 2024: 16 |
| Costos distintos y una buena estimación del resto | A* (Dijkstra con heurística admisible) | — |
| Distancia a todas las casillas | Un BFS desde el origen (y otro desde el destino) | 2024: 20 |
| Costos 0 o 1 | BFS con cola de doble extremo | — |

## Modelar el estado

- Girar cuesta: el estado es (posición, dirección) y girar es una arista con su costo (2024 día 16).
- Restricciones de movimiento: en 2023 día 17 el crisol no puede ir más de 3 casillas derecho. En vez de un contador, cada arista es un tramo completo de 1 a 3 casillas y siempre se gira: menos estados y sin casos borde.
- Varios orígenes: se meten todos en la cola con costo 0.
- Caminos mínimos empatados: guardar todos los anteriores con el mismo costo y recorrer hacia atrás (2024 día 16, parte 2).
- Atajos (2024 día 20): con distancias desde el inicio y hasta el final, el camino con un atajo de `a` a `b` mide `desdeInicio(a) + salto + hastaFin(b)`, y lo que ahorra sale de restarlo al original, sin buscar de nuevo.
- "¿Cuándo deja de existir un camino?": la condición es monótona, así que es una búsqueda binaria con un BFS por paso (2024 día 18).

## Detalles que importan

- Dijkstra con `PriorityQueue` y entradas obsoletas: no hay `decrease-key` en Java, así que se agrega una entrada nueva y se descarta la vieja al sacarla.
- Paridad: en grillas, llegar en exactamente `n` pasos equivale a distancia ≤ `n` con la misma paridad (2023 día 21).
- BFS acotado en mapas infinitos: limitar por distancia Manhattan para que termine.

**En el repo**: `BFS` y `Dijkstra` genéricos sobre cualquier tipo de nodo (con `equals`/`hashCode`), que devuelven `Camino<N>`; `Dijkstra` admite heurística y `nodosEnCaminosMinimos`; `Busqueda.primero` para la búsqueda binaria.
