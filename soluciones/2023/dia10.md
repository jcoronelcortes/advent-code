# Año 2023 — Día 10: Laberinto de Tuberías

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/10](https://adventofcode.com/2023/day/10). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con el ala delta subimos hasta la isla de metal, donde hace frío y todo, hasta las flores y el pasto, es de metal. Mientras buscamos las aguas termales, vemos algo metálico escabullirse y meterse en una **tubería** grande. Para adelantarnos hay que entender las tuberías: el campo entero está lleno de ellas, y dibujamos un mapa (el input).

Es una grilla de casillas:

| Carácter | Tubería |
|---|---|
| `\|` | vertical, conecta arriba y abajo |
| `-` | horizontal, conecta izquierda y derecha |
| `L` | codo arriba–derecha |
| `J` | codo arriba–izquierda |
| `7` | codo abajo–izquierda |
| `F` | codo abajo–derecha |
| `.` | suelo, sin tubería |
| `S` | donde está el animal: hay una tubería, pero no se ve su forma |

La tubería de `S` forma parte de un **lazo cerrado**: cada tubería del lazo conecta con sus dos vecinas, y `S` conecta exactamente con dos. Hay además muchas tuberías sueltas que no están conectadas al lazo.

Para adelantarnos al animal hay que encontrar la casilla del lazo **más lejana** de `S`, contando pasos **a lo largo del lazo** (sin importar en qué sentido lo recorra el animal).

#### Ejemplo

```
..F7.
.FJ|.
SJ.L7
|F--J
LJ...
```

Las distancias de cada casilla del lazo a `S` son:

```
..45.
.236.
01.78
14567
23...
```

y la más lejana está a **8** pasos. En un lazo cuadrado más simple, la más lejana está a 4.

#### Pregunta

¿Cuántos pasos a lo largo del lazo hay desde `S` hasta la casilla más lejana?

### Solución

#### El input real

Una grilla de 140 × 140; el lazo tiene 13 380 casillas.

#### La forma de `S`

Cada tubería conecta dos direcciones (`conexiones`). `S` conecta hacia una dirección si la tubería vecina de ese lado conecta **de vuelta** hacia `S`; tienen que ser exactamente dos, y con ellas se deduce qué tubería hay debajo de `S` (en los ejemplos, un codo `F`). Si `S` conecta con menos o con más de dos tuberías, falla con un mensaje claro.

#### Recorrer el lazo

Desde `S` se sale por una de sus dos conexiones. En cada casilla se sale por la conexión que **no** es por la que se entró, hasta volver a `S`:

```java
// Dia10.java — Lazo.desde()
while (!actual.equals(inicio)) {
    casillas.add(actual);
    Set<Direccion> salidas = conexiones(grilla.obtener(actual, '.'));
    Direccion entrada = direccion.opuesto();
    if (!salidas.contains(entrada)) {
        throw new IllegalArgumentException("El lazo se corta en " + actual);
    }
    direccion = salidas.stream().filter(salida -> salida != entrada).findFirst().orElseThrow();
    actual = actual.mover(direccion);
}
```

Si una tubería no conecta de vuelta (o el recorrido sale de la grilla, donde `obtener` devuelve suelo), el lazo está cortado y falla. Las tuberías sueltas nunca se visitan.

#### La casilla más lejana

El lazo queda como una lista de casillas en orden. La casilla número `i` está a `i` pasos yendo en un sentido y a `largo − i` en el otro, así que su distancia es el mínimo de los dos. La más lejana está justo a la **mitad**: `largo / 2`. Un lazo en una grilla siempre tiene largo par (para volver al punto de partida hay que dar tantos pasos hacia la derecha como hacia la izquierda, y tantos hacia arriba como hacia abajo), así que la mitad es exacta.

#### Verificación

- Prueba con el ejemplo complejo (8) y el cuadrado (4), los dos **con y sin tuberías sueltas** alrededor.
- `S` es un codo `F` en los dos ejemplos.
- Las distancias de cada casilla coinciden con los **dibujos** del enunciado.
- En los ejemplos, coincide con una **BFS** desde `S` que avanza por las tuberías conectadas en los dos sentidos.
- Si `S` no conecta con exactamente dos tuberías, falla.
- Con el input real, un script independiente en Python hace esa misma **BFS** (sin recorrer el lazo en orden) y obtiene el mismo resultado.

**Resultado con el input real: 6690**

---

## Parte 2

### Enunciado

Llegamos enseguida a la casilla más lejana, pero el animal no aparece. Quizás su nido esté **dentro del área encerrada por el lazo**. Para saber si vale la pena buscarlo, hay que contar cuántas casillas quedan **encerradas** por el lazo.

Cualquier casilla que no sea parte del lazo cuenta (suelo o tuberías sueltas). Y ojo: para estar **afuera** no hace falta un camino de casillas libres hasta el borde; también vale **colarse entre dos tuberías** que están pegadas.

#### Ejemplo

```
...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
...........
```

Solo quedan encerradas **4** casillas: los dos pares de abajo a la izquierda y abajo a la derecha. Las del centro están afuera, porque se puede salir por el hueco de abajo. Si las tuberías de abajo estuvieran pegadas (sin hueco), igual estarían afuera, colándose entre ellas. En dos ejemplos más grandes, con tuberías sueltas por todos lados, quedan encerradas 8 y 10 casillas.

#### Pregunta

¿Cuántas casillas quedan encerradas por el lazo?

### Solución

#### Un polígono con vértices enteros

Los centros de las casillas del lazo, recorridas en orden, forman un **polígono** con vértices en coordenadas enteras. Las casillas encerradas son exactamente los **puntos enteros del interior** de ese polígono, y para contarlos alcanzan dos fórmulas clásicas.

**Fórmula de Gauss** (o del "cordón de zapato"): el área de un polígono es la mitad del valor absoluto de la suma de `x[i]·y[i+1] − x[i+1]·y[i]` sobre todos sus lados. Se calcula el doble para quedarse con enteros.

**Teorema de Pick**: para un polígono con vértices enteros, `área = interiores + borde / 2 − 1`, donde `borde` es la cantidad de puntos enteros sobre el borde. Acá el borde son las casillas del lazo (cada paso avanza exactamente una casilla). Despejando:

```
interiores = área − borde / 2 + 1   →   (2·área − borde + 2) / 2
```

Las dos fórmulas están en la utilidad compartida `Poligono` (que también usa el día 18), así que contar las casillas encerradas es pasarle el lazo:

```java
// Dia10.java — Lazo.casillasEncerradas()
return Poligono.puntosInteriores(casillas);
```

Lo de "colarse entre tuberías" se resuelve solo: no hace falta recorrer los espacios de afuera, porque se cuentan directamente los puntos del interior del polígono. Por eso en la parte 1 convenía guardar el lazo **en orden**.

#### Verificación

- Prueba con los tres ejemplos (4, 8 y 10), y con el de 4 **sin hueco** entre las tuberías.
- El lazo cuadrado encierra 1 casilla, con área 4.
- Un **barrido por filas**, independiente de Pick: una casilla está adentro si a su izquierda hay una cantidad impar de casillas del lazo que conectan **hacia arriba** (`|`, `L`, `J`). Así un tramo `L--7` cuenta como un cruce y `L--J` como ninguno. En los seis ejemplos coincide con Pick, y en el de 10 encuentra **exactamente las casillas marcadas con `I`** en el enunciado.
- Con el input real, un script independiente en Python usa un **tercer método**: amplía la grilla al triple (cada casilla del lazo se dibuja como un bloque de 3×3 con su tubería), la rellena desde afuera y cuenta las casillas cuyo centro quedó sin alcanzar. Así modela literalmente el "colarse entre tuberías", y obtiene el mismo resultado.

**Resultado con el input real: 525**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Pasos hasta la casilla más lejana del lazo | Casillas encerradas por el lazo |
| **Enfoque** | Deducir la forma de S y recorrer el lazo; la mitad de su largo | Fórmula de Gauss y teorema de Pick sobre el lazo en orden |
| **Complejidad** | O(largo del lazo) | O(largo del lazo) |
| **Resultado** | 6690 | 525 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia10.java`
