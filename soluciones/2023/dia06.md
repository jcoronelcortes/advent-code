# Año 2023 — Día 6: Espera y Verás

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/6](https://adventofcode.com/2023/day/6). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El ferry nos cruza la Isla Isla, pero de la pila de arena que debería haber cerca no hay rastro. En un cartel del muelle vemos que hay **carreras de botes**, y el gran premio es un viaje a la **Isla del Desierto**: ¡de ahí debe venir la arena! Nos anotamos justo a tiempo.

No es una carrera común: cada carrera dura un tiempo fijo y gana el bote que llegue **más lejos**. Nos dan una hoja (el input) con la **duración** de cada carrera y la **distancia récord**; para ganar seguro hay que superar el récord en todas.

Los botes son de juguete y tienen un botón. Mientras se lo mantiene **apretado** al comienzo de la carrera, el bote se carga: cada milisegundo apretado le suma **1 mm/ms de velocidad**, pero el bote no se mueve hasta que se suelta, y ese tiempo cuenta dentro de la duración de la carrera.

#### Ejemplo

```
Time:      7  15   30
Distance:  9  40  200
```

En la carrera de 7 ms, apretando 0, 1, 2, …, 7 ms el bote recorre 0, 6, 10, 12, 12, 10, 6 y 0 mm. Para superar el récord de 9 hay **4** formas: apretar 2, 3, 4 o 5 ms. En la segunda carrera hay 8 formas (de 4 a 11 ms) y en la tercera, 9 (de 11 a 19 ms). El producto es 4 · 8 · 9 = **288**.

#### Pregunta

Multiplicando la cantidad de formas de superar el récord de cada carrera, ¿qué se obtiene?

### Solución

#### Una parábola simétrica

Si se aprieta el botón `h` ms en una carrera de `T`, el bote va a velocidad `h` durante `T − h` ms y recorre `h · (T − h)`. Esa distancia es una **parábola** simétrica alrededor de `T / 2`: crece desde `h = 0` hasta la mitad y después baja igual. Entonces los `h` que superan el récord forman un intervalo `[h₀, T − h₀]`, donde `h₀` es el menor que lo supera, y son exactamente **`T − 2·h₀ + 1`**.

#### Búsqueda binaria con enteros

En la mitad creciente `[0, T / 2]`, "supera el récord" es **monótono**: si un `h` lo supera, todos los mayores (hasta la mitad) también. Así que `h₀` se encuentra con una búsqueda binaria, usando la utilidad `Busqueda.primero`, que devuelve el menor valor de un intervalo que cumple una condición monótona (o vacío si ninguno la cumple):

```java
// Dia06.java — formasDeGanar()
if (tiempo < 0) throw new IllegalArgumentException("El tiempo no puede ser negativo: " + tiempo);
OptionalLong primero = Busqueda.primero(0, tiempo / 2, h -> distancia(h, tiempo) > record);
return primero.isPresent() ? tiempo - 2 * primero.getAsLong() + 1 : 0;
```

Si ni siquiera el mejor `h` (la mitad) supera el récord, no hay formas de ganar. Un récord negativo no necesita un caso aparte: todos los `h` lo superan, `h₀` es 0 y las formas son `T + 1`.

También se podría resolver la ecuación cuadrática `h² − T·h + récord = 0` con la fórmula de la raíz, pero en `double` los redondeos son delicados justo en el caso que más importa: cuando la distancia **empata** con el récord (en la tercera carrera del ejemplo, apretar 10 ms da exactamente 200, y empatar no alcanza). La búsqueda binaria usa solo enteros y compara exactamente. Con tiempos de dos cifras sería igual de fácil probar todos los `h`, pero así el método no depende del tamaño de los números.

#### Verificación

- Prueba con el ejemplo (288), con las **distancias** de la carrera de 7 ms y con las formas de ganar cada carrera (4, 8 y 9), incluido el empate en 200.
- La búsqueda binaria coincide con la **fuerza bruta** (probar cada `h` de 0 a `T`) en todas las carreras de hasta 60 ms, con todos los récords posibles (incluido −1, que gana con cualquier `h`).
- Un tiempo negativo, una cantidad de líneas distinta de dos o líneas que no coinciden fallan con un mensaje claro. Las dos líneas se leen con `Utiles.lineasNoVacias(lineas, 2)`, que exige la cantidad exacta.
- Con el input real, un script independiente en Python prueba todos los `h` de cada carrera y obtiene el mismo resultado.

**Resultado con el input real: 4403592**

---

## Parte 2

### Enunciado

Justo antes de la largada nos damos cuenta de que la hoja estaba mal espaciada: en realidad hay **una sola carrera**, y hay que ignorar los espacios entre los números de cada línea.

#### Ejemplo

```
Time:      7  15   30
Distance:  9  40  200
```

pasa a ser una carrera de **71530** ms con un récord de **940200** mm. Se puede ganar apretando el botón entre 14 y 71516 ms: **71503** formas.

#### Pregunta

¿De cuántas formas se puede superar el récord en esta carrera mucho más larga?

### Solución

#### Juntar las cifras y reusar la búsqueda

Cada línea se convierte en un solo número quitándole todo lo que no sea cifra (`replaceAll("\\D", "")`), y se cuentan las formas de ganar con el mismo `formasDeGanar` de la parte 1:

```java
// Dia06.java
public String parte2(List<String> lineas) {
    List<String> datos = Utiles.lineasNoVacias(lineas, 2);   // Time y Distance
    return String.valueOf(formasDeGanar(juntarCifras(datos.get(0)), juntarCifras(datos.get(1))));
}
```

En el input real la carrera dura casi **50 millones** de ms y el récord tiene 15 cifras. Probar cada `h` sería posible (son 50 millones de multiplicaciones), pero la búsqueda binaria de la parte 1 (`Busqueda.primero`) necesita unas 25 comparaciones y termina en menos de un milisegundo. Las distancias llegan a unos 6 × 10<sup>14</sup>: caben en un `long`, y `distancia` usa `Math.multiplyExact` por si no cupieran.

#### Verificación

- Prueba con el ejemplo (71503).
- Las cifras de cada línea del ejemplo se juntan en 71530 y 940200; una línea sin cifras falla.
- En el ejemplo, la búsqueda coincide con la **fuerza bruta**, y los bordes son los del enunciado: 13 y 71517 ms no alcanzan, 14 y 71516 sí.
- Con el input real, un script independiente en Python prueba **los casi 50 millones de valores** de `h` uno por uno y obtiene el mismo resultado.

**Resultado con el input real: 38017587**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Producto de las formas de superar cada récord | Formas de ganar una sola carrera, juntando las cifras |
| **Enfoque** | Parábola simétrica: búsqueda binaria del menor h que gana | La misma búsqueda binaria |
| **Complejidad** | O(carreras × log T) | O(log T) |
| **Resultado** | 4403592 | 38017587 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia06.java`
