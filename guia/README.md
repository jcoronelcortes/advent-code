# Guía para resolver Advent of Code (2023–2025)

Lo que un programador necesita saber para resolver problemas como los de Advent of Code, sacado del análisis de los 62 días resueltos en este repositorio: 25 de 2023, 25 de 2024 y 12 de 2025. Para cada tipo de problema, qué algoritmos y qué partes de Java hay que dominar, con ejemplos concretos de días reales.

## Índice

1. [Texto, listas y secuencias](01-texto-listas-y-secuencias.md)
2. [Grillas y simulación](02-grillas-y-simulacion.md)
3. [Búsqueda de caminos](03-busqueda-de-caminos.md)
4. [Programación dinámica, backtracking y voraz](04-programacion-dinamica-backtracking-y-voraz.md)
5. [Rangos e intervalos](05-rangos-e-intervalos.md)
6. [Matemática](06-matematica.md)
7. [Geometría y álgebra lineal](07-geometria-y-algebra-lineal.md)
8. [Ciclos, periodicidad y extrapolación](08-ciclos-periodicidad-y-extrapolacion.md)
9. [Grafos avanzados](09-grafos-avanzados.md)
10. [Analizar el input](10-analizar-el-input.md)
11. [Java para estos problemas](11-java.md)
12. [Librerías y herramientas](12-librerias-y-herramientas.md)
13. [Verificar la respuesta](13-verificar-la-respuesta.md)
14. [Índice por día](14-indice-por-dia.md)

## Panorama

Los 62 días caen en diez tipos de problema. Las grillas (13 días) y el texto o las secuencias (11) son los más comunes, y junto con la programación dinámica (9) suman 33 días, más de la mitad. Lo que más pesa, sin embargo, es que **la parte 2 casi siempre cambia la escala**: lo que en la parte 1 se simula, en la parte 2 hay que contarlo, comprimirlo o deducirlo.

| Tipo | Días | Cuáles | Cómo reconocerlo | Técnica clave |
| --- | --: | --- | --- | --- |
| [Grillas y simulación](02-grillas-y-simulacion.md) | 13 | 2023: 3, 11, 13, 14, 16, 22 · 2024: 4, 6, 12, 15, 25 · 2025: 4, 7 | Un mapa de caracteres; algo se mueve, cae, rebota o se repite | `Grilla`, `Punto`, `Direccion`; estados (casilla, dirección) |
| [Texto, listas y secuencias](01-texto-listas-y-secuencias.md) | 11 | 2023: 1, 2, 4, 7, 9, 15 · 2024: 1, 2, 3, 5 · 2025: 6 | Una línea por registro; cifras, palabras, órdenes, diferencias | Expresiones regulares, comparadores, conteos |
| [DP, backtracking y voraz](04-programacion-dinamica-backtracking-y-voraz.md) | 9 | 2023: 12 · 2024: 7, 9, 11, 19, 21 · 2025: 3, 11, 12 | "¿De cuántas formas…?" o un árbol de opciones que explota | Memoización sobre un estado chico |
| [Geometría y álgebra lineal](07-geometria-y-algebra-lineal.md) | 7 | 2023: 10, 18, 24 · 2024: 8, 13 · 2025: 9, 10 | Coordenadas enormes, áreas, rectas, "exactamente una solución" | Gauss y Pick, productos cruzados, eliminación gaussiana exacta |
| [Matemática](06-matematica.md) | 5 | 2023: 6 · 2024: 14, 22 · 2025: 1, 2 | Un número gigante, un módulo, una fórmula cerrada | Aritmética modular, resto chino, búsqueda binaria |
| [Búsqueda de caminos](03-busqueda-de-caminos.md) | 5 | 2023: 17 · 2024: 10, 16, 18, 20 | "El camino más corto", "el menor costo" | BFS, Dijkstra sobre estados enriquecidos |
| [Grafos avanzados](09-grafos-avanzados.md) | 4 | 2023: 23, 25 · 2024: 23 · 2025: 8 | Conexiones entre nombres; cortar, agrupar o el camino más largo | Union-find, flujo máximo, cliques, compresión |
| [Rangos e intervalos](05-rangos-e-intervalos.md) | 3 | 2023: 5, 19 · 2025: 5 | Rangos `a-b` con miles de millones de valores | Partir y unir intervalos sin enumerarlos |
| [Ciclos y periodicidad](08-ciclos-periodicidad-y-extrapolacion.md) | 3 | 2023: 8, 20, 21 | Mil millones de pasos, o "cuándo coinciden todos" | Detectar el ciclo, mcm, extrapolar |
| [Máquinas e ingeniería inversa](10-analizar-el-input.md) | 2 | 2024: 17, 24 | Un programa o un circuito que hay que entender, no solo ejecutar | Intérprete, luego leer lo que calcula |

Cada día se cuenta en un solo tipo, el de su técnica principal; muchos combinan varios. El [índice por día](14-indice-por-dia.md) da el tipo y la técnica de cada uno, con un enlace a su documentación completa en `soluciones/`.

Las secciones de [Java](11-java.md), [librerías](12-librerias-y-herramientas.md) y [verificación](13-verificar-la-respuesta.md) valen para todos los tipos.
