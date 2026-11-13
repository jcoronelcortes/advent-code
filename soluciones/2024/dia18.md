# Año 2024 — Día 18: Carrera en la RAM

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/18](https://adventofcode.com/2024/day/18). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Esta vez aparecemos **dentro de una computadora** del Polo Norte, todos pixelados. Un programa nos avisa que un algoritmo mal hecho está tirando bytes enteros sobre esta zona de memoria, uno por nanosegundo, y que hay que salir corriendo. Por suerte, leyendo el algoritmo armamos la lista de **dónde va a caer cada byte**, en orden: ese es el input.

El espacio de memoria es una grilla cuadrada con coordenadas de **0 a 70** en cada eje. Cada línea del input es una posición `X,Y`: `X` es la distancia al borde izquierdo e `Y` la distancia al borde de arriba.

Estamos en la esquina de arriba a la izquierda, `(0,0)`, y la salida está en la de abajo a la derecha, `(70,70)`. Donde cae un byte, la memoria queda **corrupta** y no se puede pisar. Tampoco se puede salir de la grilla. Nos movemos de a una casilla, hacia arriba, abajo, izquierda o derecha.

#### Ejemplo

En un espacio más chico, de 0 a 6, con estas posiciones (25 en total):

```
5,4  4,2  4,5  3,0  2,1  6,3  2,4  1,5  0,6  3,3  2,6  5,1  1,2
5,5  2,5  6,5  1,4  0,4  6,4  1,1  6,1  1,0  0,5  1,6  2,0
```

Después de que caen los primeros **12** bytes, la memoria queda así (`#` corrupta, `.` libre):

```
...#...
..#..#.
....#..
...#..#
..#..#.
.#..#..
#.#....
```

y el camino más corto hasta la salida tiene **22** pasos.

#### Pregunta

Después de que caen los primeros **1024** bytes, ¿cuál es la menor cantidad de pasos para llegar a la salida?

### Solución

#### El input real

3450 posiciones dentro del espacio de 71 × 71. Solo las primeras 1024 importan en esta parte.

#### Una BFS común

Con los 1024 bytes marcados en una `Grilla`, es el problema clásico de camino más corto en una grilla donde todos los pasos cuestan lo mismo: una **BFS**. La utilidad `BFS.buscar` devuelve la distancia, y `Grilla.vecinos` con un filtro da los vecinos libres sin salirse de la grilla:

```java
// Dia18.java — pasosMinimos()
Grilla memoria = memoria(bytes, lado, cantidad);
Punto salida = new Punto(lado, lado);
if (memoria.obtener(new Punto(0, 0)) == CORRUPTO) return Optional.empty();
return BFS.buscar(new Punto(0, 0), salida::equals, posicion -> memoria.vecinos(posicion, c -> c == LIBRE));
```

`X,Y` corresponde directamente a `Punto(x, y)` (`x` columna, `y` fila), así que no hace falta invertir nada.

El tamaño y la cantidad de bytes son **parámetros**, porque el ejemplo usa un espacio de 0 a 6 y 12 bytes. `parte1` usa los valores reales (70 y 1024). Si se piden más bytes de los que trae el input, falla con un mensaje claro en vez de dar una respuesta cualquiera.

#### Verificación

- Prueba con el ejemplo (22 pasos) y con el **dibujo** de la memoria después de 12 bytes, comparado con el del enunciado.
- `X` es la columna e `Y` la fila.
- Sin bytes, el camino mide 12 (= 2 × 6). Con una fila entera corrupta, no hay camino.
- Pedir 1024 bytes a un input de 25 falla.
- Con el input real, un script independiente en Python usa **Dijkstra con `heapq`** sobre un conjunto de tuplas, en vez de BFS sobre una grilla, y obtiene lo mismo.

**Resultado con el input real: 234**

---

## Parte 2

### Enunciado

Los Historiadores no se mueven tan rápido como nosotros en este universo pixelado, y tememos que el camino a la salida quede **completamente bloqueado** antes de que lleguen. Para saber qué tan rápido tienen que ir, hay que encontrar el **primer byte** que, al caer, deja la salida inalcanzable desde la entrada.

#### Ejemplo

En el ejemplo, después de que cae el byte de `1,1` (el número 20) todavía hay un camino hasta la salida. Pero al caer el siguiente, el de `6,1`, ya no queda ninguno:

```
...#...
.##..##
.#..#..
...#..#
###..##
.##.###
#.#....
```

Así que la respuesta del ejemplo es `6,1`.

#### Pregunta

¿Cuáles son las coordenadas del primer byte que deja la salida inalcanzable? Se responde como dos enteros separados por una coma, sin nada más.

### Solución

#### Búsqueda binaria sobre la cantidad de bytes

Probar byte por byte con una BFS cada vez funcionaría, pero son miles de BFS. Hay algo mejor: cada byte solo **agrega** un obstáculo, nunca saca uno. Entonces, si con k bytes la salida es inalcanzable, con más bytes también lo es. La respuesta "¿hay camino con k bytes?" es **sí, sí, …, sí, no, no, …, no**, y alcanza con encontrar dónde cambia: una **búsqueda binaria**.

La utilidad `Busqueda.primero` hace la búsqueda binaria: devuelve la menor cantidad de bytes, entre 0 y todos, con la que **no** hay camino:

```java
// Dia18.java — primerByteQueBloquea()
long bloqueado = Busqueda.primero(0, bytes.size(), cantidad -> pasosMinimos(bytes, lado, (int) cantidad).isEmpty())
        .orElseThrow(() -> new IllegalArgumentException("Ningún byte bloquea la salida"));
if (bloqueado == 0) {
    throw new IllegalArgumentException("La salida es inalcanzable antes de que caiga algún byte");
}
return bytes.get((int) bloqueado - 1);
```

Si ninguna cantidad bloquea la salida, la búsqueda devuelve vacío; si ya sin bytes no hay camino, devuelve 0. Los dos casos fallan con un mensaje claro. Si no, el byte que bloquea es el número `bloqueado`, que en la lista (indexada desde 0) está en `bloqueado - 1`.

Cada paso reutiliza `pasosMinimos` de la parte 1. Con 3450 bytes son unas **12 BFS** y termina en unos 20 ms.

#### Verificación

- Prueba con el ejemplo (`6,1`).
- Con 20 bytes (el último es `1,1`) hay camino y con 21 no, y la memoria con 21 bytes coincide con el **dibujo** del enunciado.
- En el ejemplo, la búsqueda binaria coincide con **agregar los bytes de a uno** hasta que no haya camino (fuerza bruta).
- Casos borde: el primer byte cae sobre la salida, o el último completa un cerco alrededor de la entrada.
- Si ningún byte bloquea la salida, falla con un mensaje claro.
- Con el input real, un script independiente en Python usa un método completamente distinto: **union-find al revés**. Empieza con todos los bytes caídos y los va sacando del último al primero, uniendo cada casilla liberada con sus vecinas libres. El primer byte cuya remoción conecta la entrada con la salida es la respuesta (el número 3037). Obtiene el mismo resultado.

**Resultado con el input real: 58,19**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Pasos mínimos hasta la salida tras 1024 bytes | Primer byte que deja la salida inalcanzable |
| **Enfoque** | BFS en la grilla | Búsqueda binaria sobre la cantidad de bytes, con una BFS por paso |
| **Complejidad** | O(lado²) | O(lado² · log bytes) |
| **Resultado** | 234 | 58,19 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia18.java`
