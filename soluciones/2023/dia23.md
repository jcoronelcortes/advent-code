# Año 2023 — Día 23: Una Larga Caminata

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/23](https://adventofcode.com/2023/day/23). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los elfos retoman el filtrado y el agua limpia empieza a caer por el borde de la Isla Isla. Nos bajan con una soga hasta la **Isla de la Nieve**, donde el agua se va absorbiendo en el aire hasta que haya humedad suficiente para nevar. Mientras tanto, ¿por qué no salir a caminar?

Tenemos un mapa de los senderos (el input) con caminos (`.`), bosque (`#`) y **pendientes** heladas (`^`, `>`, `v` y `<`). Empezamos en la única casilla de camino de la fila de arriba y queremos llegar a la única de la fila de abajo. Por el hielo, si pisamos una pendiente, el paso siguiente tiene que ser **cuesta abajo**, en la dirección de la flecha. Además, para que el paseo sea lo más pintoresco posible, nunca pisamos dos veces la misma casilla.

Hay que encontrar la caminata **más larga** posible.

#### Ejemplo

```
#.#####################
#.......#########...###
#######.#########.#.###
###.....#.>.>.###.#.###
###v#####.#v#.###.#.###
###.>...#.#.#.....#...#
###v###.#.#.#########.#
###...#.#.#.......#...#
#####.#.#.#######.#.###
#.....#.#.#.......#...#
#.#####.#.#.#########v#
#.#...#...#...###...>.#
#.#.#v#######v###.###v#
#...#.>.#...>.>.#.###.#
#####v#.#.###v#.#.###.#
#.....#...#...#.#.#...#
#.#########.###.#.#.###
#...###...#...#...#.###
###.###.#.###v#####v###
#...#...#.#.>.>.#.>.###
#.###.###.#.###.#.#v###
#.....###...###...#...#
#####################.#
```

Hay seis caminatas posibles, de 94, 90, 86, 82, 82 y 74 pasos. La más larga tiene **94**.

#### Pregunta

¿Cuántos pasos tiene la caminata más larga?

### Solución

#### El input real

Un mapa de 141 × 141, con 58 pendientes `>` y 60 `v` (no hay `^` ni `<`).

#### Comprimir en un grafo de cruces

El camino más largo sin repetir casillas es un problema difícil en general (no hay un algoritmo eficiente conocido), así que no se puede atacar casilla por casilla en una grilla de casi 20 000 casillas. Pero el mapa es casi todo **pasillos de una sola vía**: una vez que se entra en uno, solo se puede seguir hasta el otro extremo. Las decisiones se toman solo en los **cruces**, las casillas con tres o más vecinos transitables.

Así que se arma un grafo cuyos nodos son la entrada (índice 0), la salida (índice 1) y los cruces, y cuyas aristas son los pasillos entre ellos, con su largo. Desde cada nodo se recorre cada pasillo hasta llegar a otro nodo:

```java
// Dia23.java — comprimir()
Punto anterior = nodo, actual = nodo.mover(inicial);
if (!transitable(mapa, actual)) continue;
int largo = 1;
while (actual != null && !indice.containsKey(actual)) {
    Punto siguiente = null;
    for (Direccion d : salidas(mapa, actual, pendientes)) {
        Punto vecino = actual.mover(d);
        if (!vecino.equals(anterior) && transitable(mapa, vecino)) siguiente = vecino;
    }
    anterior = actual;
    actual = siguiente;
    largo++;
}
```

Las pendientes se respetan al elegir hacia dónde seguir: desde una casilla con flecha, la única salida es cuesta abajo. Si un pasillo obliga a volver por donde se vino (porque una pendiente apunta en contra) o termina en un callejón sin salida, no genera arista. Así, los pasillos con pendientes quedan como aristas **de un solo sentido**. Si la fila de arriba o la de abajo no tienen exactamente una casilla libre, falla con un mensaje claro.

#### Buscar el camino más largo

El grafo comprimido del input real tiene 36 nodos. Sobre él, una búsqueda en profundidad prueba todos los caminos de la entrada a la salida, guardando los nodos visitados en una **máscara de bits** (un `long`, así que admite hasta 64 nodos), y se queda con el más largo. Tarda unos 25 ms.

#### Verificación

- Prueba con el ejemplo (94).
- En el ejemplo, una **fuerza bruta** casilla por casilla, sin comprimir, encuentra exactamente las seis caminatas del enunciado: 94, 90, 86, 82, 82 y 74 pasos.
- El grafo comprimido del ejemplo tiene la entrada, la salida y 7 cruces. De la entrada sale un solo pasillo, y por las pendientes ninguno vuelve a ella.
- Un pasillo con una pendiente en contra no se puede recorrer: si es el único camino, no hay caminata posible y falla.
- Un mapa sin una única entrada o salida falla con un mensaje claro.
- Con el input real, un script independiente en Python comprime el mapa por su cuenta y comprueba que, con las pendientes, el grafo **no tiene ciclos**. En un grafo sin ciclos el camino más largo se calcula con programación dinámica (el más largo desde cada nodo es el máximo, entre sus aristas, de la arista más el más largo desde su destino), sin búsqueda exhaustiva. Obtiene el mismo resultado.

**Resultado con el input real: 2282**

---

## Parte 2

### Enunciado

Al llegar al comienzo del sendero resulta que el suelo no está tan resbaloso como parecía: las pendientes se pueden subir sin problema. Ahora hay que tratar todas las pendientes como **senderos comunes** (`.`), sin dejar de cumplir que nunca se pisa dos veces la misma casilla.

#### Ejemplo

Con el mismo mapa, la caminata más larga pasa a tener **154** pasos.

#### Pregunta

Si las pendientes se pueden subir, ¿cuántos pasos tiene la caminata más larga?

### Solución

#### El mismo grafo, en los dos sentidos

La compresión de la parte 1 ya tenía un parámetro para respetar o no las pendientes; ahora se llama con `false`, y cada pasillo genera una arista en cada sentido:

```java
// Dia23.java — parte2()
return String.valueOf(caminoMasLargo(comprimir(Grilla.desdeLineas(lineas), false)));
```

La diferencia está en la búsqueda: con las pendientes el grafo no tenía ciclos y los caminos posibles eran pocos, pero sin ellas cada cruce se conecta en los dos sentidos y la búsqueda en profundidad tiene que probar muchísimos más caminos. Aun así, con 36 nodos y la máscara de bits tarda unos 0,3 segundos, así que no hizo falta optimizarla.

#### Verificación

- Prueba con el ejemplo (154).
- En el ejemplo, coincide con la **fuerza bruta** de probar todas las caminatas casilla por casilla, subiendo pendientes; y hay más caminatas que con pendientes.
- Sin pendientes, cada pasillo del grafo del ejemplo tiene su vuelta con el mismo largo, y un pasillo con una pendiente en contra ya no corta el camino.
- En un mapa chico con un ciclo, la caminata más larga da la vuelta larga sin repetir casillas, igual que en la fuerza bruta.
- Con el input real, un script independiente en Python comprime el mapa por su cuenta y busca el camino más largo de otra forma: iterativamente, con una pila, y con una **poda**. La salida tiene un solo cruce vecino, así que al llegar a ese cruce hay que ir directo a la salida (cualquier otro camino ya no podría volver a pasar por él). Obtiene el mismo resultado.

**Resultado con el input real: 6646**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Caminata más larga respetando las pendientes | Lo mismo si las pendientes se pueden subir |
| **Enfoque** | Comprimir en un grafo de cruces y probar todos los caminos | El mismo grafo con pasillos de doble sentido |
| **Complejidad** | Exponencial en los cruces (36), lineal en el mapa | Exponencial en los cruces (36) |
| **Resultado** | 2282 | 6646 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia23.java`
