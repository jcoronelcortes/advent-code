# Año 2023 — Día 9: Mantenimiento del Espejismo

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/9](https://adventofcode.com/2023/day/9). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Siguiendo los mapas del fantasma, el camello nos lleva a través de la tormenta hasta un **oasis**. Mirando hacia arriba descubrimos otra isla flotante, esta vez de metal: de ahí deben venir las piezas para las máquinas de arena. Hay un ala delta medio enterrada; cuando salga el sol y caliente la arena, quizás podamos subir con el aire caliente. Mientras esperamos, tomamos lecturas del ecosistema del oasis con nuestro **sensor de inestabilidad de oasis y arena**.

El informe del sensor (el input) tiene en cada línea el **historial** de un valor: una secuencia de enteros. Hay que **predecir el valor siguiente** de cada historial:

1. Se arma una secuencia con las **diferencias** entre valores consecutivos (tiene un valor menos).
2. Si no es toda de ceros, se repite con esa secuencia, hasta llegar a una de ceros.
3. Se agrega un cero al final de la última y se completa la pirámide de abajo hacia arriba: cada valor nuevo es el anterior de su fila más el que tiene abajo.

#### Ejemplo

```
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
```

Para el tercer historial, la pirámide queda:

```
10  13  16  21  30  45  68
   3   3   5   9  15  23
     0   2   4   6   8
       2   2   2   2
         0   0   0
```

y su siguiente valor es 68. Los de los otros dos son 18 y 28, y la suma es **114**.

#### Pregunta

¿Cuánto suman los valores siguientes predichos para todos los historiales?

### Solución

#### El input real

200 historiales de 21 valores cada uno, con números negativos en varios de ellos.

#### Recursión sobre las diferencias

Completar la pirámide de abajo hacia arriba equivale a una recursión: el siguiente de una secuencia es su **último valor más el siguiente de su secuencia de diferencias**, y el siguiente de una secuencia de ceros es 0:

```java
// Dia09.java — siguiente()
if (valores.isEmpty()) {
    throw new IllegalArgumentException("La secuencia no llega a ser toda de ceros");
}
if (valores.stream().allMatch(valor -> valor == 0)) return 0;
return Math.addExact(valores.getLast(), siguiente(diferencias(valores)));
```

Si la secuencia se queda sin valores antes de llegar a ceros, el método no tiene suficiente información para predecir, y falla con un mensaje claro.

#### Por qué funciona: polinomios

Si los valores salen de un polinomio de grado `d`, sus diferencias salen de uno de grado `d − 1`, y después de `d + 1` niveles quedan todas en cero. Así que este método predice el valor siguiente del **polinomio de menor grado que pasa por todos los puntos** del historial. Eso permite verificarlo con un método completamente distinto.

#### Verificación

- Prueba con el ejemplo (114), con el siguiente de **cada historial** (18, 28 y 68) y con la **pirámide completa** del tercero, como en el enunciado.
- Un polinomio de grado 3 con valores negativos se predice exactamente.
- Una secuencia que no llega a ceros falla.
- Con el input real, un script independiente en Python usa **interpolación de Lagrange** con fracciones exactas: evalúa en el punto siguiente el polinomio que pasa por los 21 valores de cada historial, sin calcular diferencias. Obtiene el mismo resultado.

**Resultado con el input real: 2043183816**

---

## Parte 2

### Enunciado

Estaría bueno tener todavía más historia en el informe: hay que **extrapolar hacia atrás**. Se arma la misma pirámide de diferencias hasta llegar a ceros, pero ahora se agrega un cero al **principio** de la fila de ceros y se completa cada fila por la **izquierda**, de abajo hacia arriba.

#### Ejemplo

Para el tercer historial del ejemplo:

```
5  10  13  16  21  30  45
  5   3   3   5   9  15
   -2   0   2   4   6
      2   2   2   2
        0   0   0
```

El valor anterior es 5. Para los otros dos historiales es −3 y 0, y la suma es **2**.

#### Pregunta

¿Cuánto suman los valores anteriores predichos para todos los historiales?

### Solución

#### La misma recursión, por la izquierda

Si el valor anterior de una secuencia es `x`, la nueva primera diferencia es `primero − x`, y eso tiene que ser el valor anterior de la secuencia de diferencias. Despejando, **`x = primero − anterior(diferencias)`**:

```java
// Dia09.java — anterior()
if (valores.isEmpty()) {
    throw new IllegalArgumentException("La secuencia no llega a ser toda de ceros");
}
if (valores.stream().allMatch(valor -> valor == 0)) return 0;
return Math.subtractExact(valores.getFirst(), anterior(diferencias(valores)));
```

Es la recursión de la parte 1 con el primer valor en vez del último y una resta en vez de una suma.

Otra forma de verlo: el anterior de una secuencia es el **siguiente de la secuencia dada vuelta**. Al darla vuelta, las diferencias cambian de signo pero la pirámide sigue llegando a ceros, y el "siguiente" de la secuencia invertida es el valor que va antes del primero. Las pruebas comprueban que las dos formas coinciden.

#### Verificación

- Prueba con el ejemplo (2) y con el anterior de **cada historial** (−3, 0 y 5).
- En el ejemplo, el anterior coincide con el **siguiente de la secuencia dada vuelta**.
- Un polinomio de grado 3 se extrapola hacia atrás exactamente (`p(0)` a partir de `p(1)` … `p(8)`).
- Con el input real, el script de **interpolación de Lagrange** de la parte 1, evaluado en el punto anterior al primero en vez del siguiente al último, obtiene el mismo resultado.

**Resultado con el input real: 1118**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los valores siguientes predichos | Suma de los valores anteriores predichos |
| **Enfoque** | Recursión sobre las diferencias | La misma recursión por la izquierda: primero − anterior(diferencias) |
| **Complejidad** | O(historiales × valores²) | O(historiales × valores²) |
| **Resultado** | 2043183816 | 1118 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia09.java`
