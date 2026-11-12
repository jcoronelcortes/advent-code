# Año 2025 — Día 9: Sala de Cine

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/9](https://adventofcode.com/2025/day/9). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Nos deslizamos por el poste de bomberos de una esquina del patio de juegos y caemos en la **sala de cine** de la base del Polo Norte. El piso es una gran grilla de baldosas cuadradas con un dibujo curioso, y los elfos están redecorando: van a cambiar algunas baldosas.

Algunas baldosas son **rojas**, y los elfos quieren encontrar el **rectángulo más grande** que tenga baldosas rojas en **dos esquinas opuestas**. Tienen la lista de las posiciones `X,Y` de las baldosas rojas (el input):

```
7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3
```

Con las rojas como `#` y las demás como `.`, el piso del ejemplo se ve así:

```
..............
.......#...#..
..............
..#....#......
..............
..#......#....
..............
.........#.#..
..............
```

Se pueden elegir dos baldosas rojas cualesquiera como esquinas opuestas. El área del rectángulo se mide en **baldosas**, incluidas las de los bordes.

#### Ejemplo

| Esquinas | Área |
|----------|------|
| `2,5` y `9,7` | 24 |
| `7,1` y `11,7` | 35 |
| `7,3` y `2,3` (un rectángulo de una sola fila) | 6 |
| `2,5` y `11,1` | **50** |

El rectángulo más grande del ejemplo tiene área **50**.

#### Pregunta

Usando dos baldosas rojas como esquinas opuestas, ¿cuál es el área del rectángulo más grande posible?

### Solución

#### El input real

Hay 496 baldosas con coordenadas entre ~1 500 y ~98 500.

**Observación sobre el input:** cada baldosa comparte fila o columna con la siguiente, y la última con la primera. En orden, las baldosas son los vértices de un **polígono cerrado** de lados horizontales y verticales. La parte 1 no lo usa, pero se conserva el orden del input por si la parte 2 lo necesita (y lo necesitó).

#### El área cuenta baldosas, no distancias

El rectángulo entre `2,5` y `9,7` ocupa las columnas 2 a 9 (8 baldosas) y las filas 5 a 7 (3 baldosas): su área es `8 · 3 = 24`, no `7 · 2 = 14`. Los dos extremos se incluyen, así que se suma 1 a cada lado:

```java
// Dia09.java — area()
long ancho = Math.abs((long) a.x() - b.x()) + 1;
long alto = Math.abs((long) a.y() - b.y()) + 1;
return ancho * alto;
```

Eso también hace que un rectángulo de una sola fila (como `7,3`–`2,3`) tenga área positiva.

El cálculo es en `long`: con coordenadas de ~100 000 el área llega a ~10¹⁰, que no cabe en un `int`.

#### Probar todos los pares

Con 496 baldosas hay `496 · 495 / 2 ≈ 123 000` pares: se prueban todos y se queda el área mayor. Tarda ~10 ms, así que no vale la pena algo más elaborado (como quedarse solo con las baldosas de la envolvente convexa).

#### Verificación

- Prueba con el ejemplo (50) y con las áreas de los 4 rectángulos que muestra el enunciado.
- El área no depende del orden de las esquinas, un rectángulo de una baldosa mide 1, y un área mayor que `Integer.MAX_VALUE` se calcula sin desbordar.
- Con el input real, un script independiente en Python obtiene el mismo resultado.

**Resultado con el input real: 4777824480**

---

## Parte 2

### Enunciado

Los elfos recuerdan algo importante: solo pueden cambiar baldosas **rojas o verdes**, así que el rectángulo solo puede contener baldosas de esos dos colores.

En la lista, cada baldosa roja está unida a la anterior y a la siguiente por una línea recta de baldosas **verdes**. La lista es circular: la primera roja también está unida a la última. Dos baldosas consecutivas de la lista siempre están en la misma fila o en la misma columna. Además, todas las baldosas **dentro** de ese circuito cerrado de rojas y verdes también son verdes. El resto de las baldosas no es ni roja ni verde.

#### Ejemplo

En el piso del ejemplo, las baldosas verdes (`X`) son:

```
..............
.......#XXX#..
.......XXXXX..
..#XXXX#XXXX..
..XXXXXXXXXX..
..#XXXXXX#XX..
.........XXX..
.........#X#..
..............
```

El rectángulo sigue necesitando baldosas rojas en dos esquinas opuestas, pero **todas** sus demás baldosas tienen que ser rojas o verdes, y eso reduce bastante las opciones:

| Esquinas | Área | ¿Sirve? |
|----------|------|---------|
| `7,3` y `11,1` | 15 | sí |
| `9,7` y `9,5` | 3 | sí |
| `9,5` y `2,3` | **24** | sí |
| `2,5` y `11,1` (el mayor de la parte 1) | 50 | no: incluye baldosas que no son rojas ni verdes |

El rectángulo más grande que solo tiene baldosas rojas y verdes tiene área **24**.

#### Pregunta

Usando dos baldosas rojas como esquinas opuestas, ¿cuál es el área del rectángulo más grande que contiene solo baldosas rojas y verdes?

### Solución

Dicho de otro modo: el rectángulo tiene que quedar **completamente dentro del polígono** (borde incluido). La parte 1 ya probaba todos los pares; ahora, además, hay que saber rápido si cada rectángulo está adentro.

#### El problema: la grilla es enorme

Con coordenadas de hasta ~100 000, una grilla real de baldosas tendría ~10¹⁰ celdas. No se puede construir.

#### Clave: compresión de coordenadas

Los lados del polígono siempre caen sobre las coordenadas de las baldosas rojas, que son solo ~250 X distintas y ~250 Y distintas. Se arma una grilla **comprimida** con la utilidad `CompresionCoordenadas` (una para las X y otra para las Y):

- cada X distinta es una columna, y cada **hueco** entre dos X consecutivas es otra columna (lo mismo con las Y y las filas);
- se agrega una columna y una fila vacías en cada borde, que siempre quedan afuera.

Una celda de hueco representa muchas baldosas reales, pero todas están del mismo lado del polígono, porque ningún lado pasa por dentro de un hueco. La grilla comprimida tiene ~500 × 500 celdas.

```
X reales:      2        7  8        11
columnas:   0  1  2     3  4  5     6   7
            ^  ^  ^     ^  ^  ^     ^   ^
       borde  x=2 hueco x=7 x=8 hueco x=11 borde
```

#### Detalle: no crear huecos sin baldosas

Entre dos coordenadas consecutivas (como 7 y 8) **no** se crea hueco: no hay ninguna baldosa entre ellas. Si se creara, esa celda vacía podría quedar "afuera" y rechazar por error un rectángulo válido. Ejemplo: un polígono con forma de U cuya muesca mide cero baldosas de ancho (lados en x=3 y x=4). Todas las baldosas del rectángulo completo son rojas o verdes (área 72), pero con un hueco artificial entre 3 y 4 la muesca parecería exterior y el resultado sería 36.

En el input real hay un par de coordenadas Y consecutivas, aunque no cambia la respuesta: se comprobó en Python que ambos modelos dan el mismo resultado. Igual se implementó el modelo exacto por baldosas, que es el correcto en general.

#### Rellenar el exterior y consultar en O(1)

1. Se marcan los lados del polígono en la grilla comprimida.
2. Se rellena el **exterior** con una BFS (`BFS.explorar`) desde la esquina `(0, 0)`, sin cruzar los lados.
3. Se calculan **sumas acumuladas 2D** de las celdas exteriores con la utilidad `SumaAcumulada2D`. Con ellas, la cantidad de celdas exteriores dentro de cualquier rectángulo se obtiene con 4 accesos a una tabla, en O(1):

```java
// Dia09.java — PisoComprimido
afuera = SumaAcumulada2D.contar(piso, celda -> celda == AFUERA);
...
boolean rectanguloAdentro(Punto a, Punto b) {
    return afuera.suma(comprimir(a), comprimir(b)) == 0;
}
```

Internamente, `suma` usa la fórmula de inclusión y exclusión: `S(y2, x2) − S(y1−1, x2) − S(y2, x1−1) + S(y1−1, x1−1)`, donde `S` es la suma de todo lo que está arriba y a la izquierda.

Para cada uno de los ~123 000 pares, se calcula el área y, solo si supera la mejor hasta el momento, se consulta si está adentro.

#### Verificación

- Prueba con el ejemplo (24), y con los rectángulos que muestra el enunciado (15, 3 y 24 adentro; el de 50 de la parte 1, afuera).
- Un polígono rectangular permite el rectángulo entero.
- Muescas: una de ancho cero no deja baldosas afuera (72); una con una columna de baldosas afuera sí limita el rectángulo (36).
- `CompresionCoordenadas` y `SumaAcumulada2D` tienen sus propias pruebas; la de sumas acumuladas incluye una prueba basada en propiedades contra la suma celda por celda.
- Con el input real, dos scripts independientes en Python dan el mismo resultado:
  - un método **geométrico** sin grilla: un rectángulo está adentro si ningún lado del polígono cruza su interior y su centro está adentro (*ray casting*); confirma las esquinas `5942,67632` y `94645,50248`;
  - una compresión **exacta por baldosas** escrita aparte, que además se usó para confirmar los resultados de las pruebas de las muescas.

**Resultado con el input real: 1542119040**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Rectángulo más grande con baldosas rojas en esquinas opuestas | Lo mismo, pero dentro del polígono |
| **Enfoque** | Probar todos los pares; área con extremos incluidos, en `long` | Compresión de coordenadas + BFS del exterior + sumas acumuladas 2D |
| **Complejidad** | O(n²), con n baldosas | O(n²) |
| **Resultado** | 4777824480 | 1542119040 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia09.java`
