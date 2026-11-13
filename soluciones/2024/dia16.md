# Año 2024 — Día 16: Laberinto de Renos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/16](https://adventofcode.com/2024/day/16). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegan las Olimpíadas de Renos, y este año la prueba principal es un **laberinto**: gana el reno que obtenga el **menor puntaje**. Mientras buscamos al Historiador Jefe, nos quedamos a mirar un rato.

El input es el mapa del laberinto: paredes `#`, espacio libre `.`, la casilla de **salida** `S` y la de **llegada** `E`. El reno parte de `S` **mirando al este** y tiene que llegar a `E`. Puede:

- **avanzar** una casilla en la dirección en que mira, sumando **1 punto** (nunca hacia una pared);
- **girar** 90° en el lugar, en sentido horario o antihorario, sumando **1000 puntos**.

#### Ejemplo

```
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
```

Hay muchos caminos, pero los mejores cuestan **7036**: 36 pasos y 7 giros. En un segundo ejemplo, de 17 × 17, el mejor puntaje es **11048**, y el mejor camino empieza **girando** hacia el norte antes de dar el primer paso.

#### Pregunta

¿Cuál es el menor puntaje posible para ir de la salida a la llegada?

### Solución

#### El input real

Un laberinto de 141 × 141 rodeado de paredes, con unas 10 000 casillas libres. La salida está abajo a la izquierda y la llegada arriba a la derecha.

#### El estado incluye hacia dónde mira el reno

Con solo la posición no alcanza: llegar a una casilla mirando al norte no es lo mismo que llegar mirando al este, porque lo que cuesta seguir depende de la dirección. Así que cada nodo del grafo es el **par posición + dirección**:

```java
// Dia16.java
record Reno(Punto posicion, Direccion direccion) {}
```

Hay a lo sumo 4 nodos por casilla libre (unos 40 000 en el input real). Desde cada uno salen tres aristas: avanzar (costo 1, si adelante no hay pared) y girar a cada lado (costo 1000). Girar dos veces para dar media vuelta sale solo, como dos aristas seguidas.

```java
// Dia16.java — movimientos()
List<Dijkstra.Arista<Reno>> aristas = new ArrayList<>(3);
Punto adelante = reno.posicion().mover(reno.direccion());
if (laberinto.obtener(adelante, PARED) != PARED) {
    aristas.add(new Dijkstra.Arista<>(new Reno(adelante, reno.direccion()), COSTO_AVANZAR));
}
aristas.add(new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroDerecha()), COSTO_GIRAR));
aristas.add(new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroIzquierda()), COSTO_GIRAR));
return aristas;
```

#### Dijkstra

Como los costos son distintos (1 y 1000), una BFS no sirve: hace falta **Dijkstra**. La utilidad `Dijkstra.buscarCamino` termina en el primer estado que está en la llegada, **mirando hacia cualquier lado**; como Dijkstra saca los estados en orden de costo, ese es el mínimo.

```java
// Dia16.java — mejorCamino()
Punto llegada = laberinto.buscarUnico(LLEGADA);
return Dijkstra.buscarCamino(
                new Reno(laberinto.buscarUnico(SALIDA), Direccion.DERECHA),
                reno -> reno.posicion().equals(llegada),
                reno -> movimientos(laberinto, reno))
        .orElseThrow(() -> new IllegalArgumentException("La llegada es inalcanzable"));
```

Se usa `buscarCamino` (y no solo `costoMinimo`) para poder revisar en las pruebas el camino encontrado, no solo su costo.

#### Verificación

- Prueba con los dos ejemplos (7036 y 11048).
- El camino del primer ejemplo tiene **7 giros y 36 pasos**, como dice el enunciado. Como cada paso cuesta menos que un giro, el puntaje 7036 solo se puede formar así, aunque haya varios caminos empatados.
- En el segundo ejemplo, el primer movimiento es girar hacia el norte sin moverse.
- Pasillos rectos: hacia el este cuesta solo los pasos; hacia el oeste, dos giros más (media vuelta).
- Si la llegada es inalcanzable, falla con un mensaje claro.
- Con el input real, un script independiente en Python con `heapq` usa otra formulación: cada arista **gira 0, 1 o 2 veces y avanza** un paso en un solo movimiento (costos 1, 1001 y 2001). Obtiene el mismo resultado.

**Resultado con el input real: 160624**

---

## Parte 2

### Enunciado

Ya sabemos cómo son los mejores caminos; ahora hay que elegir dónde sentarse a mirar. Cada casilla que no es pared (`S`, `.` o `E`) tiene asientos en sus bordes, y lo que más importa es que la casilla esté en **alguno de los mejores caminos**: si no, nos perdemos la acción.

Puede haber **varios caminos empatados** con el puntaje mínimo. Hay que contar las casillas que están en **al menos uno** de ellos, incluidas `S` y `E`.

#### Ejemplo

En el primer ejemplo, **45** casillas (marcadas con `O`) están en algún mejor camino:

```
###############
#.......#....O#
#.#.###.#.###O#
#.....#.#...#O#
#.###.#####.#O#
#.#.#.......#O#
#.#.#####.###O#
#..OOOOOOOOO#O#
###O#O#####O#O#
#OOO#O....#O#O#
#O#O#O###.#O#O#
#OOOOO#...#O#O#
#O###.#.#.#O#O#
#O..#.....#OOO#
###############
```

En el segundo ejemplo son **64**.

#### Pregunta

¿Cuántas casillas están en al menos uno de los mejores caminos del laberinto?

### Solución

#### Todos los caminos empatados con Dijkstra

La utilidad `Dijkstra.nodosEnCaminosMinimos` ya resuelve esto sobre cualquier grafo. Es el Dijkstra de la parte 1 con dos cambios:

- Para cada estado guarda **todos** los anteriores con los que se llega al costo mínimo, no solo uno. Si se encuentra otro camino con el mismo costo, se agrega a la lista.
- No se detiene en el primer estado de la llegada: sigue mientras el costo no supere al de la llegada, para juntar también los empates que llegan mirando hacia otro lado.

Al final recorre los anteriores **hacia atrás** desde la llegada, y todo lo que alcanza está en algún camino mínimo.

Lo que devuelve son **estados** (posición y dirección), y una misma casilla puede aparecer varias veces: por ejemplo, donde el reno gira está mirando hacia dos lados. Por eso al final se cuentan solo las posiciones distintas:

```java
// Dia16.java — casillasEnMejoresCaminos()
Punto llegada = laberinto.buscarUnico(LLEGADA);
Set<Reno> renos = Dijkstra.nodosEnCaminosMinimos(
        new Reno(laberinto.buscarUnico(SALIDA), Direccion.DERECHA),
        reno -> reno.posicion().equals(llegada),
        reno -> movimientos(laberinto, reno));
if (renos.isEmpty()) throw new IllegalArgumentException("La llegada es inalcanzable");
return renos.stream().map(Reno::posicion).collect(Collectors.toSet());
```

Los movimientos son los mismos de la parte 1.

#### Verificación

- Prueba con los dos ejemplos (45 y 64).
- Los **dos dibujos** del enunciado: se marcan con `O` las casillas encontradas y se compara el laberinto completo, no solo la cantidad.
- Un laberinto chico con dos caminos empatados que rodean una pared, por arriba y por abajo (3 giros y 4 pasos cada uno): cuenta las 8 casillas de los dos.
- Si la llegada es inalcanzable, falla.
- Con el input real, un script independiente en Python usa otro método: un Dijkstra **desde la salida** y otro **hacia atrás desde la llegada** (sobre el grafo invertido). Un estado está en un mejor camino si la suma de sus dos distancias es igual al puntaje mínimo. Obtiene el mismo resultado.

**Resultado con el input real: 692**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Puntaje mínimo de la salida a la llegada | Casillas en algún camino de puntaje mínimo |
| **Enfoque** | Dijkstra sobre estados (posición, dirección) | Dijkstra guardando todos los anteriores empatados y recorrido hacia atrás |
| **Complejidad** | O(V log V), con V = 4 × casillas libres | O(V log V) |
| **Resultado** | 160624 | 692 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia16.java`
