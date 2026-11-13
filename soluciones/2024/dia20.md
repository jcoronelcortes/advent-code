# Año 2024 — Día 20: Condición de Carrera

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/20](https://adventofcode.com/2024/day/20). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Otra vez dentro de la computadora, ahora frente a la CPU. Un programa nos desafía a una carrera: justo llegamos al festival de la **condición de carrera**, donde los programas compiten por terminar un recorrido en la menor cantidad de picosegundos.

El input es el mapa de la pista: casillas de pista `.` (incluidas la salida `S` y la llegada `E`) y paredes `#`. Se avanza de a una casilla hacia arriba, abajo, izquierda o derecha, y cada paso tarda **1 picosegundo**. Como hay **un solo camino** de la salida a la llegada y todos van a la misma velocidad, las carreras eran aburridas; por eso ahora se permite **hacer trampa**.

Exactamente **una vez** por carrera, un programa puede desactivar los choques durante **hasta 2 picosegundos** y atravesar paredes como si fueran pista. Al terminar la trampa tiene que estar de nuevo **sobre la pista**, o queda descalificado. Cada trampa se identifica por su casilla de **inicio** (donde está justo antes de atravesar paredes) y su casilla de **fin**.

#### Ejemplo

```
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
```

Sin trampas, la carrera tarda **84** picosegundos. Atravesando una pared en el lugar justo se puede ahorrar, por ejemplo, 12, 20, 38 o hasta 64 picosegundos (esta última lleva directo a la llegada). Agrupadas por lo que ahorran, las trampas del ejemplo son:

| Ahorro | 2 | 4 | 6 | 8 | 10 | 12 | 20 | 36 | 38 | 40 | 64 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| **Trampas** | 14 | 14 | 2 | 4 | 2 | 3 | 1 | 1 | 1 | 1 | 1 |

#### Pregunta

¿Cuántas trampas ahorran **al menos 100** picosegundos?

### Solución

#### El input real

Una pista de 141 × 141 que es un **único camino** de 9412 pasos, que serpentea por casi toda la grilla.

#### El tiempo de una carrera con trampa

Una trampa que empieza en la casilla `a` y termina en la `b` atraviesa lo que haya en el medio, así que dura, como mínimo, la **distancia Manhattan** entre las dos. La carrera con esa trampa tarda:

> pasos de la salida a `a` + distancia de `a` a `b` + pasos de `b` a la llegada

y lo que ahorra es la diferencia con el tiempo sin trampa. Con dos BFS (una desde la salida y otra desde la llegada) se tienen las distancias de todas las casillas, y cada trampa se evalúa en O(1).

Esta fórmula no depende de que la pista sea un solo camino: si hubiera atajos o ramas, seguiría siendo correcta, porque usa la distancia mínima a cada lado de la trampa.

#### Recorrer el rombo de cada casilla

Para cada casilla de la pista, los posibles finales de una trampa de hasta `duracionMaxima` picosegundos son las casillas a distancia Manhattan ≤ `duracionMaxima`: un **rombo** alrededor. Se recorren todos los desplazamientos del rombo y se cuenta cada final que esté sobre la pista y ahorre algo:

```java
// Dia20.java — Pista.ahorrosPorTrampa()
for (int dy = -duracionMaxima; dy <= duracionMaxima; dy++) {
    int resto = duracionMaxima - Math.abs(dy);
    for (int dx = -resto; dx <= resto; dx++) {
        int finY = y + dy;
        int finX = x + dx;
        if (finY < 0 || finY >= mapa.filas() || finX < 0 || finX >= mapa.columnas()) continue;
        if (hastaLlegada[finY][finX] < 0) continue;
        int tiempo = desdeSalida[y][x] + Math.abs(dx) + Math.abs(dy) + hastaLlegada[finY][finX];
        int ahorro = tiempoSinTrampa - tiempo;
        if (ahorro > 0) ahorros.merge(ahorro, 1L, Long::sum);
    }
}
```

Con duración 2 el rombo tiene 13 casillas. La duración es un parámetro, así que el mismo código sirve para trampas más largas.

Las distancias se pasan del mapa que devuelve `BFS.explorar` a **arreglos** (`-1` en paredes), para que cada consulta sea un acceso directo y no una búsqueda en un `HashMap`.

El resultado es la cantidad de trampas por cada ahorro, en un `SortedMap` ordenado por ahorro. Así se compara directamente con la lista del enunciado, y la respuesta es la suma de las cantidades con ahorro ≥ 100.

#### Verificación

- Sin trampas, el ejemplo tarda 84.
- La cantidad de trampas por **cada ahorro** coincide con la lista completa del enunciado (44 trampas en total).
- Contar con distintos ahorros mínimos suma las cantidades correctas; con 100, el ejemplo no tiene ninguna.
- Fuerza bruta en el ejemplo: sacar cada pared, de a una, y volver a medir la carrera con una BFS. El mejor ahorro (64) coincide.
- Con el input real, un script independiente en Python **recorre la pista** de la salida a la llegada (comprobando que sea un solo camino que cubre toda la pista) para numerar las casillas en orden. Una trampa entre las casillas número `i` y `j` ahorra `j − i − distancia`. Obtiene el mismo resultado.

**Resultado con el input real: 1360**

---

## Parte 2

### Enunciado

Los programas miran nuestra lista de trampas con desconcierto: la regla de los 2 picosegundos **quedó obsoleta** hace unos milisegundos. La versión nueva permite una sola trampa de **hasta 20 picosegundos**.

La trampa no tiene que usar los 20: puede durar cualquier tiempo hasta 20, pero igual tiene que terminar sobre la pista, y el tiempo que no se usa se pierde. Sigue valiendo que una trampa se identifica **solo por su inicio y su fin**: dos recorridos distintos por dentro de las paredes entre las mismas dos casillas son la **misma** trampa.

#### Ejemplo

En la pista de la parte 1, una trampa que sale de `S` y atraviesa seis casillas hasta quedar a dos de la llegada dura 6 picosegundos y ahorra **76**. Si atraviesa las paredes por otro lado pero empieza y termina en las mismas casillas, es la misma trampa.

Contando solo las que ahorran 50 o más:

| Ahorro | 50 | 52 | 54 | 56 | 58 | 60 | 62 | 64 | 66 | 68 | 70 | 72 | 74 | 76 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Trampas** | 32 | 31 | 29 | 39 | 25 | 23 | 20 | 19 | 12 | 14 | 12 | 22 | 4 | 3 |

#### Pregunta

Con trampas de hasta 20 picosegundos, ¿cuántas ahorran **al menos 100**?

### Solución

#### El mismo código, con un rombo más grande

La solución de la parte 1 ya trataba la duración como un parámetro, y su idea no cambia: una trampa de `a` a `b` dura como mínimo la distancia Manhattan entre las dos, sin importar qué paredes atraviese. Eso coincide con la regla nueva de que el recorrido por dentro de las paredes no importa, solo el inicio y el fin. Si esa distancia es a lo sumo 20, la trampa es posible.

```java
// Dia20.java
public String parte2(List<String> lineas) {
    Pista pista = Pista.desde(Grilla.desdeLineas(lineas));
    return String.valueOf(pista.contarTrampas(DURACION_PARTE2, AHORRO_MINIMO));
}
```

Lo único que cambia es el tamaño del **rombo** que se recorre alrededor de cada casilla: con radio 20 tiene 2 · 20 · 21 + 1 = **841** desplazamientos en vez de 13. Son unos 8 millones de consultas, todas a arreglos, y termina en unos 130 ms.

Una trampa "que dura menos de lo que podría" ya está incluida: el rombo de radio 20 contiene todos los finales a distancia 1, 2, …, 20.

#### Verificación

- La cantidad de trampas por cada ahorro de 50 en adelante coincide con la **lista completa** del enunciado (285 trampas).
- La trampa del dibujo del enunciado, de `S` en (1,3) a (3,7), dura 6 y ahorra 76.
- Con duración 20 hay al menos tantas trampas como con duración 2, para distintos ahorros mínimos.
- Con el input real, el script independiente en Python de la parte 1 (que numera las casillas recorriendo la pista y cuenta pares por su posición en el recorrido) con radio 20 obtiene el mismo resultado.

**Resultado con el input real: 1005476**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Trampas de hasta 2 ps que ahorran al menos 100 | Lo mismo, con trampas de hasta 20 ps |
| **Enfoque** | Dos BFS y recorrer el rombo de radio 2 de cada casilla | El mismo código, con rombo de radio 20 |
| **Complejidad** | O(casillas × duración²) | O(casillas × duración²) |
| **Resultado** | 1360 | 1005476 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia20.java`
