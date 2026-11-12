# Advent of Code — Soluciones en Java

## ¿Qué es Advent of Code?

[Advent of Code](https://adventofcode.com) es una competencia anual de programación que se realiza cada diciembre. Consiste en 25 días de puzzles, donde cada día se publican dos problemas de dificultad creciente. Cada problema tiene un input personalizado y dos partes, siendo la segunda más compleja que la primera.

## Estructura del proyecto

```
src/
  main/
    java/advent/code/
      Principal.java          # Punto de entrada — recibe año y día como args
      Resolvedor.java         # Interfaz base para todas las soluciones
      EntradaSalida.java      # Lectura de archivos de input
      Punto.java              # Coordenadas 2D reutilizables
      Direccion.java          # Enum de direcciones cardinales
      Utiles.java             # Parseo de números, grillas y matrices
      Matematicas.java        # MCD/MCM, aritmética modular, resto chino, Möbius, dígitos
      Rango.java              # Intervalos [inicio, fin]: intersección, resta, unión
      Grilla.java             # Grilla de caracteres: búsqueda, vecinos, rotaciones
      BFS.java                # Búsqueda en anchura genérica
      Dijkstra.java           # Camino de costo mínimo genérico (y A*)
      y2025/
        Dia01.java            # Solución día 1
        Dia02.java            # Solución día 2
        Dia03.java            # Solución día 3
    resources/
      inputs/y2025/
        day01.txt             # Input del día 1
        day02.txt             # Input del día 2
        day03.txt             # Input del día 3
  test/
    java/advent/code/
      PruebaPunto.java
      PruebaDireccion.java
      PruebaUtiles.java
      PruebaMatematicas.java
      PruebaRango.java
      PruebaGrilla.java
      PruebaBFS.java
      PruebaDijkstra.java
      y2025/
        Dia01Prueba.java
        Dia02Prueba.java
        Dia03Prueba.java
soluciones/
  2025/
    dia01.md                  # Documentación detallada del día 1
    dia02.md                  # Documentación detallada del día 2
    dia03.md                  # Documentación detallada del día 3
```

## Librerías incluidas

| Librería | Uso principal |
|----------|---------------|
| [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/) | Strings, números (`NumberUtils`), pares y ternas |
| [Apache Commons IO](https://commons.apache.org/proper/commons-io/) | Lectura de archivos |
| [Apache Commons Math3](https://commons.apache.org/proper/commons-math/) | Combinatoria, primos, álgebra lineal |
| [Guava](https://github.com/google/guava) | `RangeSet`, `Multiset`, `Table`, `LongMath`, permutaciones y combinaciones |
| [JGraphT](https://jgrapht.org/) | Grafos: Dijkstra, A*, orden topológico, componentes conexas, flujo máximo, cliques |

## Cómo usar

### Ejecutar un día

```bash
mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="2025 1"
```

### Correr los tests

```bash
mvn test
```

### Crear los archivos para un nuevo día

```bash
./nuevo-dia.sh 2025 2
```

Esto genera automáticamente la clase de solución, la clase de prueba y el archivo de input vacío.

---

## Soluciones

### 2025

| Día | Título | Parte 1 | Parte 2 | Documentación |
|-----|--------|---------|---------|---------------|
| [01](src/main/java/advent/code/y2025/Dia01.java) | Entrada Secreta | 1043 | 5963 | [ver](soluciones/2025/dia01.md) |
| [02](src/main/java/advent/code/y2025/Dia02.java) | Tienda de Regalos | 38310256125 | 58961152806 | [ver](soluciones/2025/dia02.md) |
| [03](src/main/java/advent/code/y2025/Dia03.java) | Vestíbulo | 17412 | 172681562473501 | [ver](soluciones/2025/dia03.md) |