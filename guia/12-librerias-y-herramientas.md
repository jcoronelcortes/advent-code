# Librerías y herramientas

[← Anterior: Java para estos problemas](11-java.md) · [Índice](README.md) · [Siguiente: Verificar la respuesta →](13-verificar-la-respuesta.md)

La biblioteca estándar de Java cubre casi todo; **las librerías sirven para lo que es fácil de escribir mal**: fracciones exactas, primos, multiconjuntos y algoritmos de grafos ya probados.

| Librería (versión en el repo) | Para qué sirve en AoC | Uso en el repo |
| --- | --- | --- |
| Commons Math 3.6.1 | `BigFraction` para álgebra lineal exacta; también estadística y matrices | `SistemaLineal`, 2023 día 24, 2025 día 10 |
| Guava 33.4.0 | `Multiset` (conteos), `RangeSet` (unión de rangos), `Table`, `LongMath` (mcd, primos, potencia sin desborde) | 2024 día 1, `Matematicas` |
| Commons Lang 3.17.0 | `StringUtils`, `NumberUtils`, pares y tuplas | Validar los números de un rango `a-b` en `Rango.parsear` |
| Commons IO 2.17.0 | Leer archivos y recursos | `EntradaSalida` |
| JGraphT 1.5.2 | Flujo máximo, cortes mínimos (Stoer–Wagner), cliques, orden topológico, componentes | Disponible, no usado: las soluciones implementan los algoritmos |
| JUnit 5.11 | Pruebas con los ejemplos del enunciado | Una clase `DiaNNPrueba` por día |
| jqwik 1.9.2 | Pruebas basadas en propiedades: cientos de casos al azar contra una fórmula o una fuerza bruta | `PruebaPoligono`, `PruebaSistemaLineal`, `PruebaRango` |

## Herramientas

- Maven: `mvn test` corre todo; `mvn exec:java` con el año y el día ejecuta una solución y muestra el tiempo.
- Un script de Python aparte para verificar con otro método: `fractions.Fraction`, `heapq`, `collections.deque` y enteros sin límite hacen rápidas las comprobaciones.
- Con jqwik, usar `@Label` y no `@DisplayName`, que hace que omita la propiedad en silencio; y crear la instancia dentro de la propiedad, porque no ejecuta `@BeforeEach`.
