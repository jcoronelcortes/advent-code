# Año 2023 — Día 18: Laguna del Lavaducto

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/18](https://adventofcode.com/2023/day/18). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

La fábrica de piezas ya funciona, pero para ponerse al día va a necesitar mucha lava, así que los elfos están cavando una **laguna** al lado. No están seguros de que alcance, y nos piden revisar el **plan de excavación** (el input).

Cada línea del plan tiene una dirección vista desde arriba (`U` arriba, `D` abajo, `L` izquierda, `R` derecha), cuántos metros cavar y un color en hexadecimal para pintar ese borde. La excavadora empieza en un pozo de 1 metro cúbico y va cavando cubos de 1 metro en la dirección indicada, dejando una **zanja** que forma un lazo cerrado. Después se cava todo el **interior** del lazo, también a 1 metro de profundidad.

#### Ejemplo

```
R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)
```

La zanja (`#`) queda así, con 38 cubos:

```
#######
#.....#
###...#
..#...#
..#...#
###.###
#...#..
##..###
.#....#
.######
```

Al cavar el interior, la laguna tiene **62** metros cúbicos.

#### Pregunta

Siguiendo el plan, ¿cuántos metros cúbicos de lava puede contener la laguna?

### Solución

#### El input real

602 instrucciones; la zanja mide unos 3 500 metros.

#### Gauss y Pick, sin dibujar

En vez de dibujar la zanja y rellenar el interior, se usa la misma idea que en el día 10, pero con vértices en lugar de casillas. Los centros de las casillas de la zanja forman un **polígono** cuyos vértices son los extremos de cada tramo, todos con coordenadas enteras:

- La **fórmula de Gauss** (del "cordón de zapato") da su área: la mitad del valor absoluto de la suma de `x·y' − x'·y` sobre cada lado.
- El **teorema de Pick** relaciona esa área con los puntos enteros: `área = interiores + borde/2 − 1`, donde `borde` es la cantidad de puntos enteros sobre el borde, o sea las casillas de la zanja (la suma de los metros).

Lo que se cava es la zanja más el interior: `interiores + borde = área + borde/2 + 1`. Las dos fórmulas están en la utilidad compartida `Poligono` (la misma que usa el día 10), así que alcanza con armar la lista de vértices, comprobar que la zanja vuelva al inicio y pedir los puntos cubiertos:

```java
// Dia18.java — volumen()
List<Punto> vertices = new ArrayList<>();
Punto actual = new Punto(0, 0);
for (Tramo tramo : tramos) {
    vertices.add(actual);
    actual = new Punto(Math.toIntExact(actual.x() + tramo.direccion().dx() * tramo.metros()),
            Math.toIntExact(actual.y() + tramo.direccion().dy() * tramo.metros()));
}
if (!actual.equals(new Punto(0, 0))) {
    throw new IllegalArgumentException("La zanja no vuelve al inicio: termina en " + actual);
}
return Poligono.puntosCubiertos(vertices);
```

`Poligono` calcula todo con el doble del área para no usar fracciones, y con operaciones exactas (`Math.addExact` y compañía), que fallan en vez de desbordar en silencio; en esta parte sobra, pero en la parte 2 las coordenadas llegan a millones. Por lo mismo, cada coordenada se pasa a `int` con `Math.toIntExact`. El cálculo solo depende de la cantidad de tramos, no de su largo, y no importa si la zanja se recorre en sentido horario o antihorario (por el valor absoluto). Si la zanja no vuelve al inicio falla con un mensaje claro, igual que una línea que no tiene el formato esperado.

#### Verificación

- Prueba con el ejemplo (62).
- Lee bien la dirección y los metros de cada línea, y rechaza líneas mal formadas.
- Un cuadrado de lado 3 deja 4 × 4 = 16 casillas, recorrido en cualquiera de los dos sentidos; una zanja abierta falla.
- Coincide con la **fuerza bruta** de dibujar la zanja en una `Grilla`, rellenar el exterior desde una esquina con `Grilla.rellenar` y contar lo que no quedó afuera: en el ejemplo, y en una forma de U cuya entrada deja casillas de afuera rodeadas por tres lados.
- Con el input real, un script independiente en Python dibuja la zanja casilla por casilla, rellena el exterior con BFS y obtiene el mismo resultado.

**Resultado con el input real: 62573**

---

## Parte 2

### Enunciado

Los elfos tenían razón en preocuparse: la laguna planeada queda chica. Resulta que alguien **intercambió** los colores con las instrucciones al armar el plan, y las instrucciones verdaderas están escondidas en los códigos hexadecimales.

Cada código tiene seis dígitos hexadecimales: los **cinco primeros** son la distancia en metros (un número hexadecimal de cinco cifras), y el **último** es la dirección: `0` derecha, `1` abajo, `2` izquierda y `3` arriba. La dirección y los metros que se veían a simple vista se ignoran.

#### Ejemplo

En el plan de la parte 1, `#70c710` significa derecha 461937, `#0dc571` abajo 56407, y así con el resto. Siguiendo esas instrucciones, la laguna tiene **952408144115** metros cúbicos.

#### Pregunta

Con las instrucciones sacadas de los colores, ¿cuántos metros cúbicos de lava puede contener la laguna?

### Solución

#### Solo cambia la lectura

La laguna ahora tiene cientos de miles de millones de casillas, así que dibujarla sería imposible, pero el cálculo de la parte 1 con Gauss y Pick no depende del tamaño: solo de la cantidad de tramos. Lo único nuevo es leer cada tramo desde el color:

```java
// Dia18.java — leerTramoDelColor()
String color = partes(linea).group(3);
Direccion direccion = switch (color.charAt(5)) {
    case '0' -> Direccion.DERECHA;
    case '1' -> Direccion.ABAJO;
    case '2' -> Direccion.IZQUIERDA;
    case '3' -> Direccion.ARRIBA;
    default -> throw new IllegalArgumentException("Dirección inválida en el color: '" + linea + "'");
};
return new Tramo(direccion, Long.parseLong(color.substring(0, 5), 16));
```

Las dos partes comparten el formato de la línea y la forma de leer el plan (`leerPlan` recibe la interpretación de cada línea). Con el input real las coordenadas llegan a unos 10 millones, así que caben en un `int` y los productos de la fórmula de Gauss rondan 10¹⁴, holgados en un `long`; de todos modos se calculan con operaciones exactas.

#### Verificación

- Prueba con el ejemplo (952408144115).
- Los catorce colores del ejemplo dan exactamente las instrucciones del enunciado, y un último dígito que no es una dirección (`4`) falla.
- Un plan de colores que codifica la forma en U de la parte 1, con metros chicos, coincide con la **fuerza bruta** de dibujar y rellenar.
- Un cuadrado de lado 0xFFFFF (el mayor que se puede escribir en cinco dígitos) da exactamente (lado + 1)².
- Con el input real, un script independiente en Python usa un método completamente distinto: **compresión de coordenadas** (los bordes de las casillas de los vértices dividen el plano en una grilla de unas 527 × 515 celdas), relleno del exterior con BFS sobre la grilla comprimida y suma del área real de cada celda que no quedó afuera. Obtiene el mismo resultado.

**Resultado con el input real: 54662804037719**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Metros cúbicos de la laguna | Lo mismo con las instrucciones de los colores |
| **Enfoque** | Área con Gauss y puntos enteros con Pick | El mismo cálculo; solo cambia la lectura |
| **Complejidad** | O(instrucciones) | O(instrucciones) |
| **Resultado** | 62573 | 54662804037719 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia18.java`
