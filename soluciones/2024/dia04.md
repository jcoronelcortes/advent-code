# Año 2024 — Día 4: Búsqueda en Ceres

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/4](https://adventofcode.com/2024/day/4). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

"Parece que el Jefe no está aquí. ¡Siguiente!" Uno de los Historiadores aprieta el único botón de un aparato y, tras un destello, reconocemos el interior de la **estación de monitoreo de Ceres**. Mientras sigue la búsqueda, una pequeña elfa que vive en la estación nos tira de la camisa: quiere ayuda con su **sopa de letras** (el input). Solo tiene que encontrar una palabra: `XMAS`.

En esta sopa de letras las palabras pueden estar en **horizontal, vertical o diagonal**, escritas **al revés**, e incluso **superpuestas** con otras. Y no basta con encontrar una: hay que encontrarlas **todas**. Algunas formas en que puede aparecer `XMAS` (con las letras que no importan reemplazadas por `.`):

```
..X...
.SAMX.
.A..A.
XMAS.S
.X....
```

La sopa real está llena de letras:

```
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
```

#### Ejemplo

En esa sopa, `XMAS` aparece **18** veces. Las letras que forman parte de alguna aparición son:

```
....XXMAS.
.SAMXMS...
...S..A...
..A.A.MS.X
XMASAMX.MM
X.....XA.A
S.S.S.S.SS
.A.A.A.A.A
..M.M.M.MM
.X.X.XMASX
```

#### Pregunta

En la sopa de letras de la elfa, ¿cuántas veces aparece `XMAS`?

### Solución

#### El input real

Es una grilla de 140 × 140 letras, solo `X`, `M`, `A` y `S` (3736 de ellas son `X`).

#### Partir de cada X y probar las 8 direcciones

Toda aparición de `XMAS` empieza en una `X` y avanza en una de las **8 direcciones** (horizontal, vertical y las dos diagonales, en ambos sentidos). Así que basta con recorrer cada `X` y, para cada dirección, comprobar que las tres letras siguientes sean `M`, `A` y `S`:

```java
// Dia04.java — parte1()
Grilla sopa = Grilla.desdeLineas(lineas);
long apariciones = 0;
for (Punto inicio : sopa.buscarTodos(PALABRA.charAt(0))) {
    for (Punto direccion : OCHO_DIRECCIONES) {
        if (estaLaPalabra(sopa, inicio, direccion, PALABRA)) apariciones++;
    }
}
```

Tres casos del enunciado quedan cubiertos sin código extra:

- **Al revés:** `SAMX` leído hacia la derecha es `XMAS` leído hacia la izquierda, y se encuentra al partir de su `X` con la dirección opuesta. No hace falta buscar también `SAMX`.
- **Superpuestas:** cada combinación de `X` y dirección se revisa por separado, así que dos apariciones que comparten letras (como en `XMASAMX`) se cuentan las dos.
- **Bordes:** `Grilla.obtener(punto, defecto)` devuelve `.` fuera de la grilla, que no coincide con ninguna letra; una palabra cortada por el borde simplemente no cuenta.

```java
// Dia04.java — estaLaPalabra()
Punto actual = inicio;
for (int i = 0; i < palabra.length(); i++) {
    if (sopa.obtener(actual, AFUERA) != palabra.charAt(i)) return false;
    actual = actual.sumar(direccion);
}
return true;
```

Son unas 3700 `X` por 8 direcciones: ~30 000 comprobaciones, en pocos milisegundos.

#### Verificación

- Prueba con el ejemplo (18) y con el ejemplo chico del enunciado (4 apariciones: ←, →, ↑ y ↘).
- Las 8 direcciones, de a una.
- Apariciones superpuestas (`XMASAMX` tiene 2) y palabras cortadas por el borde.
- Con el input real, un script independiente en Python usa otro método: arma **todas las líneas** de la grilla como textos (filas, columnas y las diagonales en los dos sentidos) y cuenta en cada una `XMAS` y `SAMX`, admitiendo superposiciones. Obtiene el mismo resultado.

**Resultado con el input real: 2344**

---

## Parte 2

### Enunciado

La elfa nos mira extrañada: ¿entendimos mal la tarea? Buscando las instrucciones, damos vuelta la hoja y resulta que no es un juego de `XMAS` sino de **X-MAS**: hay que encontrar dos `MAS` que formen una **X**, cruzándose en la `A`. Por ejemplo:

```
M.S
.A.
M.S
```

(Otra vez, las letras que no importan están reemplazadas por `.`.) Dentro de la X, cada `MAS` puede estar escrito al derecho o al revés.

#### Ejemplo

En la misma sopa de letras de antes, conservando solo las letras de las X-MAS:

```
.M.S......
..A..MSMS.
.M.S.MAA..
..A.ASMSM.
.M.S.M....
..........
S.S.S.S.S.
.A.A.A.A..
M.M.M.M.M.
..........
```

hay **9** X-MAS.

#### Pregunta

Dando vuelta la hoja otra vez, ¿cuántas X-MAS hay en la sopa de letras?

### Solución

#### Partir de cada A y mirar sus dos diagonales

Toda X-MAS tiene una `A` en el centro, así que se recorren las `A` de la grilla. Para cada una, las dos diagonales que pasan por ella (↖↘ y ↗↙) tienen que leerse `MAS` en algún sentido: una punta `M` y la otra `S`, en cualquier orden.

```java
// Dia04.java
static boolean esCruz(Grilla sopa, Punto centro) {
    return esMasEnDiagonal(sopa, centro, new Punto(-1, -1), new Punto(1, 1))       // ↖ ↘
            && esMasEnDiagonal(sopa, centro, new Punto(1, -1), new Punto(-1, 1));  // ↗ ↙
}

private static boolean esMasEnDiagonal(Grilla sopa, Punto centro, Punto haciaUno, Punto haciaOtro) {
    char uno = sopa.obtener(centro.sumar(haciaUno), AFUERA);
    char otro = sopa.obtener(centro.sumar(haciaOtro), AFUERA);
    return (uno == 'M' && otro == 'S') || (uno == 'S' && otro == 'M');
}
```

Como cada diagonal admite dos sentidos, hay `2 · 2 = 4` formas de X-MAS, y la condición "una `M` y una `S` en cada diagonal" las cubre todas sin enumerarlas. Una `A` en el borde nunca forma cruz: fuera de la grilla se lee `.`. Y como cada cruz se cuenta desde su `A` central, dos cruces que comparten letras se cuentan las dos.

Detalle: lo que importa es la **X**, no el **+**. Un `MAS` horizontal cruzado con uno vertical no cuenta.

#### Verificación

- Prueba con el ejemplo (9).
- Las 4 formas de cruzar dos `MAS`, de a una.
- Casos que no son X-MAS: diagonales sin `S`, diagonales que leen `MAM` y `SAS`, un `+` en lugar de una X, y una `A` en el borde.
- Dos cruces que comparten letras se cuentan las dos.
- Con el input real, un script independiente en Python recorre **todas las ventanas de 3×3** de la grilla y compara sus esquinas con los 4 patrones posibles. Obtiene el mismo resultado.

**Resultado con el input real: 1815**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Apariciones de `XMAS` en la sopa de letras | Cruces de dos `MAS` en forma de X |
| **Enfoque** | Desde cada `X`, probar las 8 direcciones | Desde cada `A`, una `M` y una `S` en cada diagonal |
| **Complejidad** | O(filas · columnas) | O(filas · columnas) |
| **Resultado** | 2344 | 1815 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia04.java`
