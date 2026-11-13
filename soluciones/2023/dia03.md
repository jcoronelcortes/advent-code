# Año 2023 — Día 3: Proporciones de Engranajes

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/3](https://adventofcode.com/2023/day/3). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con el Elfo llegamos a una estación de teleférico que lleva hasta la fuente de agua, pero las góndolas **no se mueven**. Un Elfo ingeniero, algo engrasado, explica que al motor le falta una pieza y nadie sabe cuál. Si sumamos todos los **números de pieza** del esquema del motor, debería ser fácil deducir cuál falta.

El **esquema** (el input) es una grilla con números, símbolos y puntos. Un número es un número de pieza si tiene un **símbolo** en alguna de las casillas que lo rodean, **incluidas las diagonales**. Los puntos (`.`) no son símbolos.

#### Ejemplo

```
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
```

Todos los números son de pieza salvo **114** (arriba a la derecha) y **58** (en el medio a la derecha), que no tienen ningún símbolo alrededor. Los demás suman **4361**.

#### Pregunta

¿Cuánto suman todos los números de pieza del esquema?

### Solución

#### El input real

Un esquema de 140 × 140 con 10 símbolos distintos (`# $ % & * + - / = @`), entre ellos 364 asteriscos.

#### Números y sus vecinos

Cada número es una **racha de cifras seguidas** en una fila. Se recorre cada fila y, al encontrar una cifra, se sigue hasta que termina la racha (en cualquier carácter que no sea cifra, o en el borde), armando el valor y revisando los 8 vecinos de **cada cifra**:

```java
// Dia03.java — numeros()
while (columna < esquema.columnas() && Character.isDigit(esquema.obtener(new Punto(columna, fila)))) {
    Punto cifra = new Punto(columna, fila);
    valor = Math.addExact(Math.multiplyExact(valor, 10), esquema.obtener(cifra) - '0');
    for (Punto vecino : esquema.vecinosConDiagonales(cifra)) {
        if (esSimbolo(esquema.obtener(vecino))) simbolos.add(vecino);
    }
    columna++;
}
```

Los vecinos de todas las cifras juntos son exactamente las casillas que rodean al número. `Grilla.vecinosConDiagonales` ya descarta las que caen fuera de la grilla, así que los números en los bordes no necesitan un caso aparte.

En vez de responder solo "¿tiene algún símbolo?", cada `Numero` guarda **el conjunto de posiciones** de sus símbolos vecinos. Al ser un conjunto, un símbolo vecino de varias cifras (por ejemplo, en diagonal a una y debajo de otra) aparece una sola vez. La parte 1 se queda con los números cuyo conjunto no está vacío.

Un símbolo es cualquier carácter que no sea cifra ni punto. En particular, `-` es un símbolo y no un signo menos: en `-7`, el 7 es un número de pieza.

#### Verificación

- Prueba con el ejemplo (4361), con la **lista de números** del ejemplo, y con que solo 114 y 58 no son de pieza.
- Un símbolo vecino de dos cifras cuenta una sola vez.
- Números en los bordes, pegados a un símbolo en la misma fila, y con `-` antes.
- El mismo valor en dos lugares cuenta dos veces.
- Con el input real, un script independiente en Python primero **marca todas las casillas vecinas de algún símbolo** y después busca los números con una expresión regular, sumando los que tienen alguna cifra en una casilla marcada. Obtiene el mismo resultado.

**Resultado con el input real: 536202**

---

## Parte 2

### Enunciado

La ingeniera instala la pieza que faltaba y el motor arranca, pero la góndola va tan despacio que ni siquiera sale de la estación. Además de la pieza, hay un **engranaje** equivocado.

Un **engranaje** es un símbolo `*` que tiene **exactamente dos** números de pieza alrededor. Su **proporción** es el producto de esos dos números. Hay que sumar las proporciones de todos los engranajes.

#### Ejemplo

En el mismo esquema de la parte 1 hay dos engranajes: el de arriba a la izquierda, entre 467 y 35 (proporción 16345), y el de abajo a la derecha, entre 755 y 598 (proporción 451490). El `*` junto a 617 no es un engranaje porque tiene un solo número alrededor. La suma es **467835**.

#### Pregunta

¿Cuánto suman las proporciones de todos los engranajes del esquema?

### Solución

#### Dar vuelta la relación

En la parte 1, cada número guarda las posiciones de sus símbolos vecinos. Para los engranajes hace falta lo contrario: para cada símbolo, **qué números lo tocan**. Se arma recorriendo los números una vez:

```java
// Dia03.java — numerosPorSimbolo()
Map<Punto, List<Integer>> porSimbolo = new HashMap<>();
for (Numero numero : numeros(esquema)) {
    for (Punto simbolo : numero.simbolos()) {
        porSimbolo.computeIfAbsent(simbolo, posicion -> new ArrayList<>()).add(numero.valor());
    }
}
return porSimbolo;
```

Después se suman los productos de los `*` que tienen exactamente dos números en su lista.

Que cada número guarde sus símbolos **sin repetir** es justo lo que hace falta acá: si un `*` toca dos cifras del mismo número (por ejemplo, una arriba y otra en diagonal), ese número aparece una sola vez en su lista, y no se confunde con dos números distintos. En cambio, dos números distintos con el **mismo valor** (como en `5*5`) son dos entradas, y el `*` es un engranaje.

#### Verificación

- Prueba con el ejemplo (467835), y con los números de cada `*` del ejemplo: 467 y 35, solo 617, y 755 y 598.
- Un `*` con tres números no es engranaje; un `*` que toca dos cifras de cada uno de sus dos números sí lo es (10 · 20 = 200); dos números junto a un símbolo que no es `*` no cuentan.
- Dos números iguales alrededor del mismo `*` cuentan como dos (`5*5` da 25).
- Con el input real, un script independiente en Python usa otro método: para cada `*`, busca los números cuya **caja ampliada** (una fila arriba y abajo, una columna a cada lado) lo contiene. Obtiene el mismo resultado.

**Resultado con el input real: 78272573**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los números con un símbolo vecino | Suma de las proporciones de los engranajes |
| **Enfoque** | Recorrer rachas de cifras y sus 8 vecinos | La relación al revés: números por símbolo |
| **Complejidad** | O(filas × columnas) | O(filas × columnas) |
| **Resultado** | 536202 | 78272573 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia03.java`
