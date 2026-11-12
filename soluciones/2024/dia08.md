# Año 2024 — Día 8: Colinealidad Resonante

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/8](https://adventofcode.com/2024/day/8). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Aparecemos en la azotea de una instalación ultrasecreta del Conejo de Pascua. Mientras los Historiadores buscan, revisamos la enorme antena conocida y descubrimos que fue reconfigurada para emitir una señal que hace a la gente un 0,1 % más propensa a regalar en Navidad Chocolate Mediocre de Imitación marca Conejo de Pascua. ¡Inaceptable!

Recorriendo la ciudad, encontramos que hay muchas antenas así. Cada una está sintonizada en una **frecuencia**, indicada por una letra minúscula, una mayúscula o un dígito. Armamos un mapa de las antenas (el input):

```
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
```

La señal solo hace efecto en ciertos **antinodos**. Un antinodo aparece en cualquier punto **perfectamente alineado** con dos antenas de la **misma frecuencia**, pero solo cuando una de las antenas está al **doble de distancia** que la otra. Así, cada par de antenas de la misma frecuencia genera dos antinodos, uno a cada lado.

- Las antenas de frecuencias distintas no generan antinodos (`A` y `a` son frecuencias distintas).
- Un antinodo puede caer en un lugar donde hay una antena.
- Solo cuentan los antinodos **dentro** del mapa.

#### Ejemplo

Dos antenas `a` generan los dos antinodos marcados con `#`:

```
..........
...#......
..........
....a.....
..........
.....a....
..........
......#...
..........
..........
```

Con una tercera antena `a` habría cuatro antinodos más, pero dos caen fuera del mapa por la derecha, así que solo se agregan dos.

En el ejemplo grande, las antenas `0` y `A` generan estos antinodos (uno de ellos coincide con la `A` de más arriba):

```
......#....#
...#....0...
....#0....#.
..#....0....
....0....#..
.#....A.....
...#........
#......#....
........A...
.........A..
..........#.
..........#.
```

En total hay **14** posiciones distintas con un antinodo dentro del mapa.

#### Pregunta

¿Cuántas posiciones distintas, dentro del mapa, tienen un antinodo?

### Solución

#### El input real

Es un mapa de 50 × 50 con antenas de 47 frecuencias distintas (unas 4 por frecuencia).

#### Agrupar por frecuencia y recorrer los pares

Las antenas se agrupan por frecuencia con `Collectors.groupingBy`, usando el carácter de la celda como clave (así `A` y `a` quedan en grupos distintos):

```java
// Dia08.java — antenasPorFrecuencia()
return mapa.buscarTodos(celda -> celda != VACIO).stream()
        .collect(Collectors.groupingBy(mapa::obtener));
```

#### Los antinodos de un par

"Alineado con las dos antenas y una al doble de distancia que la otra" significa: un paso más allá de cada antena, en la recta que las une. Si `paso = b − a` es el desplazamiento de una antena a la otra, los antinodos son `b + paso` y `a − paso`:

```java
// Dia08.java — antinodosDelPar()
Punto paso = b.restar(a);
return Stream.of(b.sumar(paso), a.restar(paso))
        .filter(mapa::estaDentro)
        .toList();
```

Por ejemplo, con antenas en `(4, 3)` y `(5, 5)`, el paso es `(1, 2)` y los antinodos quedan en `(6, 7)` y `(3, 1)`. `b + paso` está a distancia `paso` de `b` y `2·paso` de `a`, justo lo que pide el enunciado.

Los antinodos se guardan en un **conjunto**: si varios pares generan el mismo lugar, o si cae sobre una antena, se cuenta una sola vez.

#### Verificación

- Prueba con el ejemplo (14).
- Los tres ejemplos chicos del enunciado: dos antenas (2 antinodos, en las posiciones exactas del dibujo), tres antenas con dos antinodos fuera del mapa (4), y la `A` sola, que no genera antinodos pero tiene uno de las `a` encima (sigue habiendo 4).
- `A` y `a` se agrupan por separado.
- Con el input real, un script independiente en Python recorre el problema al revés: para **cada celda** del mapa revisa si algún par de antenas de la misma frecuencia cumple que la celda está a una distancia de una que es el doble de la distancia a la otra, en la misma dirección. Obtiene el mismo resultado.

**Resultado con el input real: 329**

---

## Parte 2

### Enunciado

Mirando por encima de nuestro hombro, uno de los Historiadores pregunta si tuvimos en cuenta los efectos de los **armónicos resonantes**. ¡Ups!

Con el modelo actualizado, un antinodo aparece en **cualquier** posición de la grilla que esté exactamente alineada con **al menos dos** antenas de la misma frecuencia, **sin importar la distancia**. Eso significa que algunos de los nuevos antinodos caen sobre las propias antenas (salvo que la antena sea la única de su frecuencia).

#### Ejemplo

Estas tres antenas de frecuencia `T` generan muchos antinodos:

```
T....#....
...T......
.T....#...
.........#
..#.......
..........
...#......
..........
....#.....
..........
```

Además, las tres antenas `T` están alineadas con otras dos antenas, así que también son antinodos: en total hay **9**.

El ejemplo original ahora tiene **34** antinodos, incluidos los que caen sobre cada antena.

#### Pregunta

Con el modelo actualizado, ¿cuántas posiciones distintas, dentro del mapa, tienen un antinodo?

### Solución

#### Misma estructura, otra regla

Lo único que cambia es qué antinodos genera un par de antenas. El recorrido de los pares quedó en `contarAntinodos`, que recibe esa regla como función, y cada parte pasa la suya:

```java
// Dia08.java
public String parte1(List<String> lineas) {
    return String.valueOf(contarAntinodos(Grilla.desdeLineas(lineas), Dia08::antinodosDelPar));
}

public String parte2(List<String> lineas) {
    return String.valueOf(contarAntinodos(Grilla.desdeLineas(lineas), Dia08::antinodosEnLaRecta));
}
```

#### Toda la recta, con el paso más chico

Ahora los antinodos son todas las posiciones de la grilla sobre la recta que une las dos antenas. Se recorren desde una antena en ambos sentidos hasta salir del mapa. Así quedan incluidas las dos antenas, cumpliendo que caen sobre ellas.

La sutileza es el **paso**. "Cualquier posición exactamente alineada" incluye las que están **entre** las antenas. Con antenas en `(0,0)` y `(2,2)`, la posición `(1,1)` también está en la recta, pero avanzando de a `b − a = (2,2)` se la saltearía. Por eso el paso se divide por el **MCD** de sus componentes, y queda el desplazamiento más chico sobre la grilla:

```java
// Dia08.java — antinodosEnLaRecta()
Punto diferencia = b.restar(a);
int divisor = (int) Matematicas.mcd(Math.abs(diferencia.x()), Math.abs(diferencia.y()));
Punto paso = new Punto(diferencia.x() / divisor, diferencia.y() / divisor);

List<Punto> recta = new ArrayList<>();
for (Punto p = a; mapa.estaDentro(p); p = p.sumar(paso)) recta.add(p);
for (Punto p = a.restar(paso); mapa.estaDentro(p); p = p.restar(paso)) recta.add(p);
return recta;
```

En el input real ningún par de antenas tiene MCD mayor que 1 (se comprobó en Python), así que el resultado sería el mismo sin reducir el paso. Pero reducido es lo correcto en general, y una prueba lo cubre.

Una antena sola no genera antinodos, ni siquiera en su lugar: no forma ningún par.

#### Verificación

- Prueba con el ejemplo (34) y con el de las tres antenas `T` (9, incluidas ellas mismas).
- Con antenas en `(0,0)` y `(2,2)`, la recta incluye `(1,1)`, entre ellas.
- Una antena sola no genera antinodos.
- Con el input real, un script independiente en Python usa otro método: para **cada celda** del mapa, comprueba con el **producto vectorial** (`(b − a) × (p − a) = 0`) si está alineada con algún par de antenas de la misma frecuencia. Así cubre también las posiciones entre antenas sin ningún cálculo de pasos. Obtiene el mismo resultado.

**Resultado con el input real: 1190**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Posiciones con antinodo dentro del mapa | Lo mismo, con antinodos en toda la recta |
| **Enfoque** | Agrupar por frecuencia; para cada par, `b + paso` y `a − paso` | Recorrer toda la recta con el paso reducido por el MCD |
| **Complejidad** | O(pares de antenas de la misma frecuencia) | O(pares × largo de la recta) |
| **Resultado** | 329 | 1190 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia08.java`
