# Advent of Code — Soluciones en Java

## ¿Qué es Advent of Code?

[Advent of Code](https://adventofcode.com) es una competencia anual de programación que se realiza cada diciembre. Consiste en 25 días de puzzles, donde cada día se publican dos problemas de dificultad creciente. Cada problema tiene un input personalizado y dos partes, siendo la segunda más compleja que la primera.

## Requisitos

- Java 25
- Maven 3.9+

## Estructura del proyecto

```
src/
  main/
    java/advent/code/
      Principal.java          # Punto de entrada — recibe año y día, muestra resultado y tiempo
      Resolvedor.java         # Interfaz base para todas las soluciones
      EntradaSalida.java      # Lectura de archivos de input
      Punto.java              # Coordenadas 2D (record)
      Punto3D.java            # Coordenadas 3D (record, long): distancia al cuadrado y Manhattan
      Direccion.java          # Enum de direcciones cardinales
      Utiles.java             # Números (int y long) de texto o por línea, líneas no vacías, secciones
      Matematicas.java        # MCD/MCM, aritmética modular, resto chino, Möbius, dígitos, suma/producto exactos
      Rango.java              # Intervalos [inicio, fin]: intersección, resta, unión
      ConjuntoRangos.java     # Unión de rangos: pertenencia (búsqueda binaria) y cantidad
      Grilla.java             # Grilla de caracteres (admite líneas de distinto largo): búsqueda, vecinos, rotaciones
      BFS.java                # Búsqueda en anchura genérica
      Dijkstra.java           # Camino de costo mínimo genérico (y A*)
      Camino.java             # Resultado de BFS y Dijkstra: nodos + costo
      Memoizador.java         # Función recursiva con caché (programación dinámica)
      ConjuntosDisjuntos.java # Union-find: unir grupos, mismo grupo, tamaños
      SumaAcumulada2D.java    # Suma de cualquier rectángulo en O(1)
      CompresionCoordenadas.java # Coordenadas enormes → índices chicos, con huecos exactos
      y{anio}/
        Dia01.java ...        # Una clase por día (y2025: Dia01 ... Dia12)
    resources/
      inputs/y{anio}/         # Inputs personales (NO se suben al repositorio)
        dia01.txt ...
  test/
    java/advent/code/
      RespuestasPrueba.java   # Regresión: cada día sigue dando la respuesta aceptada
      PruebaPunto.java, PruebaDireccion.java, PruebaUtiles.java, PruebaMatematicas.java,
      PruebaRango.java, PruebaConjuntoRangos.java, PruebaGrilla.java, PruebaBFS.java,
      PruebaDijkstra.java, PruebaMemoizador.java, PruebaConjuntosDisjuntos.java,
      PruebaPunto3D.java, PruebaSumaAcumulada2D.java, PruebaCompresionCoordenadas.java
      y{anio}/
        Dia01Prueba.java ...
soluciones/
  {anio}/
    dia01.md ...              # Documentación detallada de cada día
enunciados/                   # Enunciados copiados del sitio (NO se suben al repositorio)
nuevo-dia.sh                  # Genera los archivos de un día nuevo
```

### Inputs y enunciados

El creador de Advent of Code [pide no publicar](https://adventofcode.com/about) los inputs ni los enunciados, así que `src/main/resources/inputs/` y `enunciados/` están en el `.gitignore`. Al clonar el repositorio hay que pegar los inputs propios en `src/main/resources/inputs/y{anio}/dia{DD}.txt`.

Las pruebas no dependen de los inputs: usan los ejemplos de los enunciados. La única excepción es `RespuestasPrueba`, que se **omite** automáticamente cuando falta el input de un día (por ejemplo, en CI).

## Librerías incluidas

| Librería | Uso principal |
|----------|---------------|
| [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/) | Strings, números (`NumberUtils`), pares y ternas |
| [Apache Commons IO](https://commons.apache.org/proper/commons-io/) | Lectura de archivos |
| [Apache Commons Math3](https://commons.apache.org/proper/commons-math/) | Combinatoria, primos, álgebra lineal |
| [Guava](https://github.com/google/guava) | `RangeSet`, `Multiset`, `Table`, `LongMath`, permutaciones y combinaciones |
| [JGraphT](https://jgrapht.org/) | Grafos: Dijkstra, A*, orden topológico, componentes conexas, flujo máximo, cliques |
| [JUnit 5](https://junit.org/junit5/) | Pruebas unitarias (solo en pruebas) |
| [jqwik](https://jqwik.net/) | Pruebas basadas en propiedades: casos aleatorios contra fuerza bruta (solo en pruebas) |

## Cómo usar

### Ejecutar un día

```bash
mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="2025 1"
```

Muestra el resultado de cada parte y el tiempo que tardó.

### Ejecutar todo un año

```bash
mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="2025"
```

Ejecuta todos los días que tienen solución e input, y muestra una tabla con las respuestas y el tiempo de cada parte. Sirve para detectar si algún día se volvió lento después de un refactor.

### Correr los tests

```bash
mvn test
```

### Crear los archivos para un nuevo día

```bash
./nuevo-dia.sh 2025 4 "Título del día"
```

Genera la clase de solución, la clase de prueba (con los tests de ejemplo marcados `@Disabled` hasta completarlos), la documentación en `soluciones/`, la fila en la tabla de este README, el archivo del input y los archivos de enunciado vacíos (`diaNN_parte1.txt` y `diaNN_parte2.txt`, para solo pegar el texto con el nombre correcto). No sobrescribe archivos que ya existan.

**Descarga automática del input (opcional):** si está definida la variable de entorno `AOC_SESSION` con el valor de la cookie `session` de adventofcode.com, el script descarga el input una sola vez (si el archivo ya tiene contenido no lo vuelve a pedir). La cookie se define en el shell, nunca en el repositorio:

```bash
export AOC_SESSION=...   # por ejemplo en ~/.zshrc
```

Se puede personalizar el User-Agent con `AOC_USER_AGENT`.

Después de resolver un día, agregar sus respuestas a `RespuestasPrueba` para protegerlas en futuros refactors.

---

## Soluciones

### 2025

| Día | Título | Parte 1 | Parte 2 | Documentación |
|-----|--------|---------|---------|---------------|
| [01](src/main/java/advent/code/y2025/Dia01.java) | Entrada Secreta | 1043 | 5963 | [ver](soluciones/2025/dia01.md) |
| [02](src/main/java/advent/code/y2025/Dia02.java) | Tienda de Regalos | 38310256125 | 58961152806 | [ver](soluciones/2025/dia02.md) |
| [03](src/main/java/advent/code/y2025/Dia03.java) | Vestíbulo | 17412 | 172681562473501 | [ver](soluciones/2025/dia03.md) |
| [04](src/main/java/advent/code/y2025/Dia04.java) | Departamento de Impresión | 1551 | 9784 | [ver](soluciones/2025/dia04.md) |
| [05](src/main/java/advent/code/y2025/Dia05.java) | Cafetería | 635 | 369761800782619 | [ver](soluciones/2025/dia05.md) |
| [06](src/main/java/advent/code/y2025/Dia06.java) | Compactador de Basura | 6171290547579 | 8811937976367 | [ver](soluciones/2025/dia06.md) |
| [07](src/main/java/advent/code/y2025/Dia07.java) | Laboratorios | 1635 | 58097428661390 | [ver](soluciones/2025/dia07.md) |
| [08](src/main/java/advent/code/y2025/Dia08.java) | Patio de Juegos | 171503 | 9069509600 | [ver](soluciones/2025/dia08.md) |
| [09](src/main/java/advent/code/y2025/Dia09.java) | Sala de Cine | 4777824480 | 1542119040 | [ver](soluciones/2025/dia09.md) |
| [10](src/main/java/advent/code/y2025/Dia10.java) | Fábrica | 491 | 20617 | [ver](soluciones/2025/dia10.md) |
| [11](src/main/java/advent/code/y2025/Dia11.java) | Reactor | 749 | 420257875695750 | [ver](soluciones/2025/dia11.md) |
| [12](src/main/java/advent/code/y2025/Dia12.java) | Granja de Árboles de Navidad | 538 | estrella final | [ver](soluciones/2025/dia12.md) |

### 2024

| Día | Título | Parte 1 | Parte 2 | Documentación |
|-----|--------|---------|---------|---------------|
| [01](src/main/java/advent/code/y2024/Dia01.java) | Histeria del Historiador | 2378066 | 18934359 | [ver](soluciones/2024/dia01.md) |
| [02](src/main/java/advent/code/y2024/Dia02.java) | Reportes del Reno de Nariz Roja | 306 | 366 | [ver](soluciones/2024/dia02.md) |
| [03](src/main/java/advent/code/y2024/Dia03.java) | Dale Vueltas | 178538786 | 102467299 | [ver](soluciones/2024/dia03.md) |
| [04](src/main/java/advent/code/y2024/Dia04.java) | Búsqueda en Ceres | 2344 | 1815 | [ver](soluciones/2024/dia04.md) |
| [05](src/main/java/advent/code/y2024/Dia05.java) | Cola de Impresión | 5991 | 5479 | [ver](soluciones/2024/dia05.md) |
| [06](src/main/java/advent/code/y2024/Dia06.java) | Patrulla del Guardia | 5409 | 2022 | [ver](soluciones/2024/dia06.md) |
