# Año 2023 — Día 11: Expansión Cósmica

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/11](https://adventofcode.com/2023/day/11). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Siguiendo los carteles hacia las aguas termales llegamos a un **observatorio**, donde un Elfo investigador estudia la **expansión cósmica**. Promete llevarnos a las aguas termales cuando termine su análisis del día; quizás podamos ayudarlo para que termine antes.

Su imagen del cielo (el input) tiene espacio vacío (`.`) y **galaxias** (`#`). Quiere la suma de las distancias más cortas entre **todos los pares** de galaxias, moviéndose de a una casilla hacia arriba, abajo, izquierda o derecha (se puede pasar por otras galaxias). Pero hay un detalle: mientras la luz de las galaxias viajaba, el universo se expandió, y toda **fila o columna sin galaxias** en realidad mide el **doble**.

#### Ejemplo

```
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
```

Hay dos filas y tres columnas vacías. Después de la expansión, la imagen queda de 12 × 13. Entre las 9 galaxias hay 36 pares; por ejemplo, la distancia entre la 5.ª y la 9.ª es 9, y entre la 1.ª y la 7.ª, 15. La suma de las 36 distancias es **374**.

#### Pregunta

Después de expandir el universo, ¿cuánto suman las distancias entre todos los pares de galaxias?

### Solución

#### El input real

Una imagen de 140 × 140 con 440 galaxias: 96 580 pares.

#### No hace falta agrandar la imagen

Moviéndose en horizontal y vertical, sin obstáculos, la distancia más corta entre dos galaxias es la **distancia Manhattan** entre sus posiciones después de la expansión. Y esas posiciones se pueden calcular sin dibujar la imagen expandida: cada fila ocupa 1 si tiene alguna galaxia y `factor` si está vacía, así que la coordenada expandida de una fila es la suma de lo que ocupan las anteriores. Lo mismo para las columnas:

```java
// Dia11.java — expandir()
boolean[] tieneGalaxia = new boolean[cantidad];
for (int indice : ocupadas) tieneGalaxia[indice] = true;
long[] expandida = new long[cantidad];
long posicion = 0;
for (int i = 0; i < cantidad; i++) {
    expandida[i] = posicion;
    posicion = Math.addExact(posicion, tieneGalaxia[i] ? 1 : factor);
}
return expandida;
```

Después se suman las distancias Manhattan de todos los pares. El **factor** de expansión es un parámetro (2 en esta parte), y las coordenadas son `long`, por si el universo se expandiera mucho más.

#### Verificación

- Prueba con el ejemplo (374).
- Una **expansión literal** (duplicar cada fila y cada columna vacías) da exactamente el dibujo expandido del enunciado.
- Las coordenadas expandidas del ejemplo saltan donde están las filas 3 y 7 y las columnas 2, 5 y 8, que son las vacías.
- En el ejemplo, coincide con expandir literalmente y medir cada uno de los 36 pares con una **BFS**.
- Con factor 1 no hay expansión; un factor menor que 1 falla.
- Con el input real, un script independiente en Python expande la imagen literalmente y suma las distancias **por eje** con los valores ordenados (en una lista ordenada, el valor de la posición `i` aparece sumado `i` veces y restado `n − 1 − i` veces), sin recorrer los pares. Obtiene el mismo resultado.

**Resultado con el input real: 9627977**

---

## Parte 2

### Enunciado

Las galaxias son mucho más antiguas (y están mucho más lejos) de lo que el investigador estimaba: cada fila o columna vacía en realidad vale por **un millón** de filas o columnas.

#### Ejemplo

Con la misma imagen del ejemplo, si cada fila o columna vacía valiera por 10, la suma de las distancias sería **1030**; si valiera por 100, **8410**.

#### Pregunta

Con cada fila y columna vacía un millón de veces más grande, ¿cuánto suman las distancias entre todos los pares de galaxias?

### Solución

#### Solo cambia el factor

La parte 1 nunca agrandó la imagen: calcula las coordenadas expandidas con un factor como parámetro, en `long`. La parte 2 es la misma cuenta con factor 1 000 000:

```java
// Dia11.java
public String parte2(List<String> lineas) {
    return String.valueOf(sumaDeDistancias(Grilla.desdeLineas(lineas), FACTOR_PARTE2));
}
```

Expandir la imagen literalmente sería imposible: tendría cientos de millones de filas y columnas.

#### Una recta en el factor

Cada par de galaxias cruza siempre la misma cantidad de filas y columnas vacías, así que su distancia es la distancia sin expandir más `(factor − 1)` por esa cantidad. Sumando sobre todos los pares, el resultado es una **recta** en el factor: `base + cruces · (factor − 1)`. En el ejemplo, la base es 292 y los cruces 82: con factor 2 da 374, con 10 da 1030 y con 100 da 8410, como dice el enunciado.

#### Verificación

- Prueba con los ejemplos del enunciado (factor 10: 1030; factor 100: 8410).
- La suma es una recta en el factor: con factor un millón da `base + cruces · 999 999`, con la base y los cruces calculados con factores 1 y 2.
- Con el input real, un script independiente en Python calcula la **base** (distancias sin expandir) y los **cruces** (filas y columnas vacías entre cada par) por eje, con los valores ordenados y sin coordenadas expandidas. Con factor 2 reproduce el resultado de la parte 1, y con factor un millón obtiene el mismo resultado.

**Resultado con el input real: 644248339497**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de distancias entre pares de galaxias, con expansión × 2 | Lo mismo, con expansión × 1 000 000 |
| **Enfoque** | Coordenadas expandidas y distancia Manhattan | El mismo cálculo con otro factor |
| **Complejidad** | O(galaxias² + filas + columnas) | O(galaxias² + filas + columnas) |
| **Resultado** | 9627977 | 644248339497 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia11.java`
