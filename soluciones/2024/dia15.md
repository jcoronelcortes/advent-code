# Año 2024 — Día 15: Problemas en el Almacén

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/15](https://adventofcode.com/2024/day/15). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Volvemos a nuestro mini submarino y encontramos un cardumen de peces linterna muy nerviosos. Como se reproducen rápido, necesitan mucha comida guardada en almacenes manejados por robots, y perdieron el control del robot de uno de sus almacenes más importantes: anda empujando cajas por todos lados. Si podemos predecir cómo se mueve, quizás encuentren una forma segura de apagarlo.

El input tiene dos partes, separadas por una línea en blanco:

1. El **mapa** del almacén: paredes `#`, cajas `O`, espacio libre `.` y el robot `@`.
2. Los **movimientos** que intenta el robot, en orden: `^` arriba, `v` abajo, `<` izquierda y `>` derecha. Vienen partidos en varias líneas solo para que sean fáciles de copiar; forman **una sola secuencia**.

Si hay cajas en el camino, el robot intenta **empujarlas**. Pero si eso haría que el robot o alguna caja entrara en una pared, **no se mueve nada**, ni siquiera el robot.

Al final, cada caja tiene una **coordenada GPS**: 100 por su distancia al borde de arriba del mapa más su distancia al borde izquierdo, contando las paredes. Por ejemplo, esta caja está a 1 del borde de arriba y a 4 del izquierdo, así que su coordenada es 100 · 1 + 4 = 104:

```
#######
#...O..
#......
```

#### Ejemplo

Un almacén chico:

```
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
```

El primer `<` choca contra la pared y no pasa nada. Después, el robot sube, empuja hacia la derecha las dos cajas de la fila de arriba hasta que quedan contra la pared (los `>` siguientes ya no pueden moverlas), y baja empujando la columna de cajas de abajo. Al final las coordenadas GPS suman **2028**.

En el ejemplo más grande del enunciado (un almacén de 10 × 10 con 700 movimientos) la suma es **10092**.

#### Pregunta

Cuando el robot termina de moverse, ¿cuánto suman las coordenadas GPS de todas las cajas?

### Solución

#### El input real

Es un almacén de 50 × 50 rodeado de paredes, con 20 000 movimientos.

#### Juntar todo lo que se mueve, y moverlo junto

Las cajas que el robot tiene adelante forman una fila sin huecos. Para saber si se puede avanzar, se juntan **todas las casillas que tendrían que moverse**: el robot, la caja de adelante, la de adelante de esa, y así hasta llegar a una casilla libre. Si en el camino aparece una **pared**, no se mueve nada.

```java
// Dia15.java — Almacen.casillasQueSeMueven()
Set<Punto> casillas = new HashSet<>();
Queue<Punto> pendientes = new ArrayDeque<>(List.of(robot));
while (!pendientes.isEmpty()) {
    Punto casilla = pendientes.poll();
    if (!casillas.add(casilla)) continue;
    Punto adelante = casilla.mover(direccion);
    switch (mapa.obtener(adelante)) {
        case PARED -> {
            return Set.of();
        }
        case CAJA -> pendientes.add(adelante);
        case CAJA_IZQUIERDA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.DERECHA)));
        case CAJA_DERECHA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.IZQUIERDA)));
        default -> { }   // libre: ahí no hay nada que empujar
    }
}
return casillas;
```

En la parte 1 solo importan las cajas `O`, y esto es una fila. Está escrito como búsqueda en anchura porque la misma función resuelve la parte 2, donde lo que se empuja deja de ser una fila (los casos `[` y `]` se explican ahí).

Si se puede, todas esas casillas avanzan un paso. Primero se **borran todas** y después se **escriben** una casilla más adelante. Si se escribiera a medida que se borra, una casilla podría pisar a otra que todavía no se movió.

```java
// Dia15.java — Almacen.mover()
Set<Punto> casillas = casillasQueSeMueven(direccion);
if (casillas.isEmpty()) return;
Map<Punto, Character> contenido = new HashMap<>();
for (Punto casilla : casillas) {
    contenido.put(casilla, mapa.obtener(casilla));
    mapa.establecer(casilla, LIBRE);
}
contenido.forEach((casilla, caracter) -> mapa.establecer(casilla.mover(direccion), caracter));
robot = robot.mover(direccion);
```

Como el mapa está rodeado de paredes, la búsqueda nunca se sale de la grilla. El robot queda dibujado en el mapa (`@`), así que el estado se puede comparar directamente con los dibujos del enunciado.

#### Utilidad nueva: `Direccion.desdeFlecha`

Las flechas `^ v < >` aparecen en varios problemas de Advent of Code, así que su lectura se agregó a la utilidad `Direccion` en vez de quedar en este día. El input se separa con `Utiles.secciones(lineas, 2)`, que falla si no hay exactamente dos secciones (mapa y movimientos). Los movimientos se leen uniendo todas las líneas de la segunda sección y convirtiendo cada carácter.

#### Verificación

- Prueba con los dos ejemplos (2028 y 10092) y con la coordenada GPS de la caja suelta (104).
- Los **15 movimientos** del ejemplo chico, comparando el almacén con el dibujo del enunciado después de cada uno, incluidos los que chocan contra una pared.
- El dibujo final del ejemplo grande.
- Los movimientos repartidos en varias líneas dan lo mismo que en una sola.
- Con el input real, un script independiente en Python mueve las cajas **una por una**: primero comprueba recursivamente que haya lugar al final de la fila, y después mueve cada caja desde la más lejana. Obtiene el mismo resultado.

**Resultado con el input real: 1457740**

---

## Parte 2

### Enunciado

Los peces linterna logran apagar el robot, pero llegan noticias de que el robot de un **segundo almacén** también falla. Su mapa es igual al anterior, con una diferencia: **todo es el doble de ancho, menos el robot**. Los movimientos son los mismos.

El mapa ancho se obtiene reemplazando cada casilla del original:

| Original | Ancho |
|---|---|
| `#` | `##` |
| `O` | `[]` |
| `.` | `..` |
| `@` | `@.` |

Ahora cada caja ocupa **dos casillas** (`[]`), y el robot sigue ocupando una. Por eso las cajas pueden quedar desalineadas, y al empujar hacia arriba o hacia abajo una caja puede empujar a **dos** cajas a la vez.

La coordenada GPS de una caja ancha se mide hasta su **borde más cercano**: esta caja está a 1 del borde de arriba y a 5 del izquierdo, así que su coordenada es 105.

```
##########
##...[]...
##........
```

#### Ejemplo

Con este almacén y los movimientos `<vv<<^^<<^^`:

```
#######
#...#.#
#.....#
#..OO@#
#..O..#
#.....#
#######
```

ensanchado queda:

```
##############
##......##..##
##..........##
##....[][]@.##
##....[]....##
##..........##
##############
```

El robot empuja hacia la izquierda las dos cajas de su fila, baja, rodea, y después empuja hacia arriba la caja de abajo. Esa caja empuja a su vez a las **dos** de arriba, porque está a mitad de camino entre ellas. Al final, un empuje hacia arriba no se puede hacer porque **una sola mitad** de una caja choca con una pared, y entonces no se mueve nada.

En el ejemplo grande ensanchado, la suma de las coordenadas GPS al final es **9021**.

#### Pregunta

En el almacén ancho, ¿cuánto suman las coordenadas GPS de todas las cajas cuando el robot termina?

### Solución

#### Lo que se empuja ahora es un árbol

En la parte 1, lo que el robot empuja es siempre una fila recta. Con cajas anchas, en un empuje **vertical** cada caja empuja lo que tiene adelante de **cada una de sus dos mitades**: puede ser nada, una caja, o dos cajas desalineadas. Esas, a su vez, pueden empujar a otras. Lo que se mueve forma un **árbol** que se abre hacia adelante.

Por eso `casillasQueSeMueven` es una búsqueda en anchura. La única regla nueva es que **una caja ancha se mueve entera**: al encontrar `[` también se agrega la casilla de su derecha, y al encontrar `]` la de su izquierda.

```java
case CAJA_IZQUIERDA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.DERECHA)));
case CAJA_DERECHA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.IZQUIERDA)));
```

Si **cualquier** casilla del árbol choca con una pared, la búsqueda devuelve vacío y no se mueve nada. Aunque la otra mitad de la caja tenga lugar, la caja no puede avanzar a medias.

En un empuje **horizontal**, la otra mitad de la caja ya está en el camino, así que agregarla no cambia nada. Como las casillas se guardan en un conjunto, una casilla que se agrega dos veces se procesa una sola vez: cuando dos ramas del árbol llegan a la misma caja, se mueve una vez.

El movimiento en sí (`mover`) es el mismo de la parte 1: borrar todo y reescribir un paso más adelante. Borrar primero es lo que hace que funcione con el árbol, donde no hay un orden obvio para mover las casillas una por una.

#### Ensanchar y medir

`ensanchar` reemplaza cada casilla del mapa por sus dos casillas, y la parte 2 es la parte 1 con el mapa ensanchado:

```java
// Dia15.java
public String parte2(List<String> lineas) {
    Almacen almacen = Almacen.parsear(lineas, true);
    almacen.moverTodo();
    return String.valueOf(sumaGps(almacen.mapa()));
}
```

Para la coordenada GPS, el borde más cercano de una caja ancha al lado izquierdo es su mitad `[`, así que `sumaGps` cuenta las casillas `O` y `[`. Así la misma función sirve para las dos partes.

#### Verificación

- Prueba con el ejemplo grande (9021) y con la caja de coordenada 105.
- El mapa ensanchado del ejemplo grande y su dibujo final, comparados con el enunciado.
- Los **11 movimientos** del ejemplo ancho, comparando el almacén con el dibujo del enunciado después de cada uno. Incluyen la caja que empuja a dos y el empuje bloqueado porque una sola mitad de una caja choca con una pared.
- Un empuje horizontal de dos cajas anchas seguidas.
- Las pruebas de la parte 1, incluidos sus 15 pasos, siguen pasando con el método nuevo.
- Con el input real, un script independiente en Python usa el método **recursivo** clásico: una función que pregunta si una casilla puede moverse, mirando las dos mitades de cada caja en los empujes verticales, y otra que mueve las cajas desde las más lejanas. Obtiene el mismo resultado, con 596 cajas en un almacén de 50 × 100.

**Resultado con el input real: 1467145**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de las coordenadas GPS de las cajas al final | Lo mismo, con todo el doble de ancho salvo el robot |
| **Enfoque** | Simulación: juntar con BFS todo lo que se mueve, borrar y reescribir | La misma BFS; una caja ancha agrega su otra mitad |
| **Complejidad** | O(movimientos × casillas empujadas) | O(movimientos × casillas empujadas) |
| **Resultado** | 1457740 | 1467145 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia15.java`
