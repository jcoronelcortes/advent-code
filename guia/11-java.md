# Java para estos problemas

[← Anterior: Analizar el input](10-analizar-el-input.md) · [Índice](README.md) · [Siguiente: Librerías y herramientas →](12-librerias-y-herramientas.md)

Java moderno (17 a 25) alcanza para escribir soluciones cortas y rápidas. Lo que más rinde es **modelar con `record` y `enum`, elegir la colección correcta y no desbordar**.

## Lenguaje

- `record`: datos inmutables con `equals`, `hashCode` y `toString` automáticos. Es el tipo ideal para estados, puntos y claves de memoización. Un constructor compacto valida al crear.
- `enum` con campos y métodos: direcciones con su desplazamiento y sus giros, operadores con su función.
- `switch` con flechas y `yield`, y *pattern matching* (`o instanceof Grilla g`) para código sin casos olvidados.
- `var` para tipos largos, y bloques de texto `"""` para ejemplos en las pruebas.
- `sealed` + `record` para árboles de expresiones o instrucciones de una máquina.

## Colecciones

| Necesidad | Clase |
| --- | --- |
| Conjunto o mapa con búsqueda O(1) | `HashSet`, `HashMap` |
| Mantener el orden de inserción | `LinkedHashMap`, `LinkedHashSet` (y `SequencedMap` en Java 21+) |
| Ordenado, con vecinos (`floor`, `ceiling`) | `TreeMap`, `TreeSet` |
| Cola de BFS o pila de DFS | `ArrayDeque` (no `Stack` ni `LinkedList`) |
| Cola de prioridad de Dijkstra | `PriorityQueue` con `Comparator` |
| Contar apariciones | `Map.merge(k, 1, Integer::sum)` o `Multiset` de Guava |
| Muchos estados en un rango conocido | `boolean[]`, `int[]`, `long[]`, `BitSet` |

- `List.of`, `Set.of` y `Map.of` son inmutables: ideales para constantes, pero fallan con `null` y al modificarlos.
- No modificar un mapa mientras se lo recorre, ni dentro de su `computeIfAbsent`.

## Streams, sí, pero con criterio

- Para transformar y agregar: `mapToLong(…).sum()`, `Collectors.groupingBy`, `IntStream.range`.
- `Gatherers.windowSliding` y `windowFixed` (Java 24+) para ventanas.
- En bucles calientes que se ejecutan millones de veces, un `for` con arreglos suele ser bastante más rápido (conviene medirlo).

## Aritmética

- `long` por defecto. `int` desborda en 2·10⁹ sin aviso.
- `Math.floorMod` y `floorDiv` para negativos; `Math.addExact`, `multiplyExact` y `toIntExact` para detectar desbordes.
- `BigInteger` para productos de más de 9,2·10¹⁸; `BigFraction` (Commons Math) para fracciones exactas.
- Nunca `double` para respuestas exactas ni para comparar bordes.

## Rendimiento

- Primero el algoritmo correcto: ninguna optimización de Java salva un O(n³) que debía ser O(n log n).
- Codificar estados como enteros (`y * ancho + x`, o bits en un `long`) en vez de objetos cuando son millones.
- Medir con el tiempo que imprime el programa, no por intuición. En este repo los 25 días de 2023 tardan en total 1,5 segundos.
