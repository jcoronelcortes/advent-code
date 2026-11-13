# Grafos avanzados

[← Anterior: Ciclos, periodicidad y extrapolación](08-ciclos-periodicidad-y-extrapolacion.md) · [Índice](README.md) · [Siguiente: Analizar el input →](10-analizar-el-input.md)

Más allá de los caminos más cortos, AoC pide agrupar, cortar, ordenar y buscar el camino más largo. **Reconocer el problema clásico que está debajo** es la mitad del trabajo: casi todos tienen un algoritmo conocido.

Días de este tipo: 2023: 23, 25 · 2024: 23 · 2025: 8.

| Problema | Algoritmo | Días |
| --- | --- | --- |
| Grupos que se van uniendo | Union-find (conjuntos disjuntos) con compresión de caminos | 2025: 8 |
| Unir todo con el menor costo | Kruskal: aristas ordenadas por costo + union-find | 2025: 8 |
| Separar en dos grupos cortando k aristas | Corte mínimo = flujo máximo (Edmonds–Karp) | 2023: 25 |
| El grupo más grande donde todos se conocen | Clique máxima: Bron–Kerbosch con pivote | 2024: 23 |
| Tríos conectados entre sí | Triángulos con `a < b < c` para no contarlos tres veces | 2024: 23 |
| Dependencias (qué va antes de qué) | Orden topológico | 2024: 5 · 2023: 22 |
| Contar caminos en un grafo sin ciclos | Programación dinámica sobre el DAG | 2025: 11 |
| Camino más largo sin repetir nodos | Búsqueda exhaustiva sobre el grafo comprimido | 2023: 23 |

## Ideas clave

- Comprimir antes de buscar: en 2023 día 23 el laberinto de 141 × 141 tiene solo 36 cruces. Cada pasillo pasa a ser una arista con su largo, y la búsqueda exhaustiva se vuelve posible.
- Camino más largo: es NP-difícil en general, pero en un DAG es programación dinámica lineal. Si hay ciclos, se hace búsqueda en profundidad con los nodos visitados en una máscara de bits (`long`, hasta 64 nodos).
- Flujo máximo con capacidad 1 por arista: el flujo entre dos nodos es la menor cantidad de aristas a cortar para separarlos. Después, lo alcanzable por aristas no saturadas es uno de los dos grupos.
- Grafos no dirigidos: guardar cada arista en los dos sentidos, y al listar aristas normalizar el par (el nombre menor primero) para no duplicarlas.
- Reacciones en cadena (2023 día 22): si cada nodo depende solo de anteriores en un orden topológico, un recorrido en ese orden decide quién cae.

**En el repo y librerías**: `ConjuntosDisjuntos`, `BFS`, `Dijkstra`; JGraphT trae flujo, cortes (Stoer–Wagner), cliques, componentes y orden topológico ya probados.
