# Año 2024 — Día 21: Enigma de los Teclados

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/21](https://adventofcode.com/2024/day/21). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos a la nave de Papá Noel y falta un Historiador: quedó encerrado en otra zona, detrás de una puerta que solo se abre tecleando unos **códigos** (el input) en su **teclado numérico**:

```
+---+---+---+
| 7 | 8 | 9 |
+---+---+---+
| 4 | 5 | 6 |
+---+---+---+
| 1 | 2 | 3 |
+---+---+---+
    | 0 | A |
    +---+---+
```

Nadie puede acercarse a la puerta, así que va un **robot**. Al robot no se le puede pedir que apriete un botón: tiene un brazo que se maneja a distancia con un **teclado direccional**:

```
    +---+---+
    | ^ | A |
+---+---+---+
| < | v | > |
+---+---+---+
```

Las flechas mueven el brazo un botón en esa dirección, y `A` hace que el robot apriete el botón al que apunta. Pero a ese teclado direccional tampoco se puede llegar, así que lo maneja **otro robot** con otro teclado direccional, y a ese, **un tercero**. Recién el teclado direccional del tercer robot lo tecleamos nosotros. En total:

- un teclado direccional que usamos nosotros;
- dos teclados direccionales que usan robots;
- el teclado numérico de la puerta, que usa un robot.

Todos los brazos empiezan apuntando a la `A` de su teclado. Si un brazo apunta al **hueco** sin botón, aunque sea un instante, el robot entra en pánico: eso no puede pasar nunca.

La **complejidad** de un código es la cantidad mínima de teclas que tenemos que apretar nosotros para que se teclee en la puerta, multiplicada por la parte numérica del código (sin ceros a la izquierda).

#### Ejemplo

Para teclear `029A` en la puerta, el robot de la puerta puede recibir `<A^A>^^AvvvA` (ir al 0 y apretarlo, subir al 2, ir al 9, bajar a la A). Hay tres secuencias de ese largo mínimo. Un nivel más arriba, una secuencia mínima es `v<<A>>^A<A>AvA<^AA>A<vAAA>^A`, y en nuestro teclado, una de **68** teclas. Así que la complejidad de `029A` es 68 · 29.

Con los códigos `029A`, `980A`, `179A`, `456A` y `379A` (68, 60, 68, 64 y 64 teclas), la suma de las complejidades es **126384**.

#### Pregunta

¿Cuánto suman las complejidades de los cinco códigos del input?

### Solución

#### El input real

Cinco códigos de tres cifras seguidas de `A`.

#### No hace falta armar las secuencias

Las secuencias crecen en cada nivel y hay muchas formas de mover un brazo, así que generar todas y quedarse con la más corta no escala. La clave es que **cada vez que un robot aprieta un botón, todos los brazos de arriba están en `A`**: para que el robot de abajo apriete, el de arriba tuvo que apretar `A`, y así hasta nosotros.

Eso hace que cada movimiento del brazo de abajo sea **independiente** de los demás: mover el brazo de `x` a `y` y apretar empieza y termina con todos los brazos de arriba en `A`. El costo total de una secuencia es la suma del costo de cada par de teclas consecutivas, empezando desde `A`, y el costo de cada par se puede **memoizar** por (desde, hasta, cuántos robots quedan arriba).

#### Todos los recorridos que esquivan el hueco

Para ir de un botón a otro por el camino más corto, el brazo da exactamente |dx| pasos horizontales y |dy| verticales, en algún orden. El orden importa (cambia lo que hay que teclear arriba), así que `Teclado.recorridos` genera **todos los órdenes** y descarta los que pasan por el hueco. Son a lo sumo C(5, 2) = 10, en el teclado numérico. Por ejemplo, de `A` a `7` hay 9: el único descartado empieza con `<<`, que pasaría por el hueco.

#### La recursión

`mejorRecorrido` prueba cada recorrido, le agrega la `A` para apretar y calcula cuánto cuesta teclear esa secuencia en el teclado direccional de arriba:

```java
// Dia21.java — Cadena.mejorRecorrido()
long mejor = Long.MAX_VALUE;
for (String recorrido : teclado.recorridos(desde, hasta)) {
    String secuencia = recorrido + "A";
    long teclas;
    if (capas == 0) {
        teclas = secuencia.length();
    } else {
        teclas = 0;
        char brazo = 'A';
        for (char tecla : secuencia.toCharArray()) {
            teclas = Math.addExact(teclas, costo.apply(new Movimiento(brazo, tecla, capas)));
            brazo = tecla;
        }
    }
    mejor = Math.min(mejor, teclas);
}
return mejor;
```

Si ese teclado direccional lo tecleamos nosotros (`capas == 0`), cada tecla cuesta 1. Si no, cada par de teclas es un `Movimiento` en un teclado direccional con un robot menos arriba, y su costo lo da el `Memoizador`, que vuelve a llamar a `mejorRecorrido`. El código se teclea igual, empezando con el brazo de la puerta en `A`.

La cantidad de robots es un **parámetro** de `Cadena`; en esta parte son 2. Como hay solo 5 × 5 movimientos posibles por nivel, la memoización hace que todo termine en unos milisegundos.

#### Verificación

- Prueba con el ejemplo (126384), y las teclas y la complejidad de **cada código** del ejemplo.
- `029A` con 0, 1 y 2 robots intermedios da 12, 28 y 68, los largos de las secuencias del enunciado.
- Un **simulador** independiente aprieta la secuencia de 68 teclas del enunciado y reproduce cada nivel, hasta `029A` en la puerta, comprobando que ningún brazo pase por el hueco.
- Los recorridos esquivan el hueco (de `A` a `7`, y de `^` a `<` en el direccional).
- En el ejemplo, la cantidad de teclas coincide con una **BFS sobre el estado de todos los brazos**, donde cada tecla nuestra cuesta 1. No usa recorridos ni memoización, solo las reglas de los teclados.
- Un botón que no existe falla con un mensaje claro.
- Con el input real, un script independiente en Python hace la misma BFS sobre el estado de los cuatro teclados y obtiene el mismo resultado.

**Resultado con el input real: 163086**

---

## Parte 2

### Enunciado

Apenas liberan al Historiador perdido, los demás se dan cuenta de que **falta otro**, encerrado en otra zona de la nave. Otra vez hay que mandar robots, pero ahora la cadena es mucho más larga:

- un teclado direccional que usamos nosotros;
- **25** teclados direccionales que usan robots;
- el teclado numérico de la puerta, que usa un robot.

Los códigos son los mismos; solo cambia la cantidad de robots.

#### Pregunta

Con 25 robots en teclados direccionales, ¿cuánto suman las complejidades de los cinco códigos?

### Solución

#### Ya estaba resuelto

La solución de la parte 1 nunca armó las secuencias: calcula el costo de cada movimiento y lo memoiza por (desde, hasta, robots que quedan arriba). La cantidad de robots es un parámetro de `Cadena`, así que la parte 2 es la misma cuenta con 25:

```java
// Dia21.java
public String parte2(List<String> lineas) {
    return String.valueOf(new Cadena(ROBOTS_PARTE2).sumaDeComplejidades(Utiles.lineasNoVacias(lineas)));
}
```

Armar las secuencias sería imposible: en cada nivel se multiplican aproximadamente por 2,5, y con 25 robots la de un solo código (`029A`) tiene unos 82 mil millones de teclas. Pero hay solo 5 × 5 = 25 movimientos posibles en un teclado direccional, así que la memoización calcula a lo sumo 25 × 25 = 625 valores. Termina en unos 4 ms.

#### Por qué hay que probar todos los recorridos

Con pocos robots, varios recorridos empatan (por ejemplo, `<^` y `^<` cuestan lo mismo con un solo nivel arriba), y elegir cualquiera da la respuesta correcta. Con más niveles, el empate se rompe: un orden que parecía igual de bueno termina costando más varios niveles arriba. Por eso `mejorRecorrido` no elige un orden por regla, sino que **prueba todos** en cada nivel y se queda con el mínimo.

Los números llegan a unos 2 × 10<sup>14</sup>: caben en un `long`, y las sumas y la multiplicación por la parte numérica usan `Math.addExact` y `Math.multiplyExact` por si no cupieran.

#### Verificación

El enunciado no da un resultado de ejemplo para 25 robots, y una BFS sobre el estado de 25 brazos es imposible. Por eso la verificación se apoya en pocos robots y en un método distinto:

- Con **0 a 3 robots**, para cada código del ejemplo, la solución coincide con una **BFS sobre el estado de todos los brazos** (generalizada para cualquier cantidad de robots), que solo usa las reglas de los teclados.
- Con más robots nunca hacen falta menos teclas, de 0 a 25; con 25, el código `029A` ya necesita más de diez mil millones de teclas.
- Con el input real, un script independiente en Python arma una **tabla de costos de abajo hacia arriba**: empieza con nuestro teclado (cada tecla cuesta 1) y en cada nivel calcula el costo de los 25 movimientos a partir de la tabla del nivel anterior, generando los recorridos como **permutaciones** de las flechas. Coincide con la BFS con 0 a 3 robots, da el resultado de la parte 1 con 2 y el mismo resultado con 25.

**Resultado con el input real: 198466286401228**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de complejidades con 2 robots intermedios | Lo mismo, con 25 robots intermedios |
| **Enfoque** | Costo de cada movimiento memoizado por nivel, probando todos los recorridos | El mismo código con 25 niveles |
| **Complejidad** | O(robots × 25 movimientos × recorridos) | O(robots × 25 movimientos × recorridos) |
| **Resultado** | 163086 | 198466286401228 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia21.java`
