# Año 2022 — Día 1: Contando Calorías

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2022/day/1](https://adventofcode.com/2022/day/1). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los renos de Santa necesitan mucha energía mágica para repartir los regalos, y su bocado favorito es una fruta estrella que solo crece en lo profundo de la selva. Acompañamos a los elfos en su expedición anual, que necesita juntar al menos cincuenta estrellas antes del 25 de diciembre.

Al desembarcar, los elfos hacen el inventario de sus provisiones, en particular de las **calorías** que lleva cada uno (el input). Cada elfo anota las calorías de cada alimento que lleva, uno por línea, y separa su inventario del del elfo anterior con una **línea en blanco**.

Por si alguien tiene hambre, quieren saber cuántas calorías lleva el elfo que **más** lleva.

#### Ejemplo

```
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
```

Son cinco elfos, con 6000, 4000, 11000, 24000 y 10000 calorías. El que más lleva es el cuarto, con **24000**.

#### Pregunta

¿Cuántas calorías lleva en total el elfo que más lleva?

### Solución

#### El input real

2265 líneas: el inventario de 260 elfos.

#### Bloques y sumas

Los elfos son los bloques separados por líneas en blanco, que es justo lo que devuelve `Utiles.secciones` (que además ignora líneas en blanco repetidas o al final). El total de cada elfo es la suma de sus líneas, y la respuesta, el mayor de esos totales (`sumaDeLosMayores(totales, 1)`, que también usa la parte 2). Los totales se calculan así:

```java
// Dia01.java — totales()
return Utiles.secciones(lineas).stream()
        .map(elfo -> elfo.stream().mapToLong(Dia01::calorias).sum())
        .toList();
```

Cada línea tiene que ser un único número: si no, falla con un mensaje claro, igual que un inventario vacío.

#### Verificación

- Prueba con el ejemplo (24000).
- Los totales de los cinco elfos del ejemplo son exactamente los del enunciado.
- Varias líneas en blanco seguidas, o al principio o al final, no crean elfos vacíos.
- Un inventario vacío, o una línea que no es un único número, falla con un mensaje claro.
- Con el input real, un script independiente en Python separa el texto por líneas en blanco dobles, suma y ordena los totales, y obtiene el mismo resultado.

**Resultado con el input real: 69912**

---

## Parte 2

### Enunciado

Los elfos se dan cuenta de que el que más calorías lleva podría quedarse sin comida, así que prefieren saber cuántas calorías llevan entre los **tres** elfos que más llevan: si a uno se le acaba, quedan dos de reserva.

#### Ejemplo

Los tres que más llevan son el cuarto (24000), el tercero (11000) y el quinto (10000), que suman **45000**.

#### Pregunta

¿Cuántas calorías llevan en total los tres elfos que más llevan?

### Solución

#### La suma de los k mayores

Las dos partes son el mismo cálculo: ordenar los totales de mayor a menor y sumar los primeros `k`. La parte 1 es `k = 1` y la parte 2, `k = 3`:

```java
// Dia01.java — sumaDeLosMayores()
return totales.stream()
        .sorted(Comparator.reverseOrder())
        .limit(k)
        .mapToLong(Long::longValue)
        .sum();
```

Con 260 elfos, ordenar es lo más simple y tarda nada. Si hay menos de `k` elfos, falla con un mensaje claro en vez de sumar menos.

#### Verificación

- Prueba con el ejemplo (45000).
- Coincide con la **fuerza bruta** de probar todos los tríos de elfos del ejemplo y quedarse con la mayor suma.
- Con totales repetidos se cuentan todos (tres elfos con 5 suman 15), y con menos de tres elfos falla.
- Con el input real, un script independiente en Python usa `heapq.nlargest` (un montículo en vez de ordenar todo) y obtiene el mismo resultado.

**Resultado con el input real: 208180**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Calorías del elfo que más lleva | Calorías de los tres que más llevan |
| **Enfoque** | Bloques con `Utiles.secciones`, sumar y tomar el máximo | Ordenar los totales y sumar los tres primeros |
| **Complejidad** | O(líneas) | O(líneas + elfos log elfos) |
| **Resultado** | 69912 | 208180 |

## Archivo de código

`src/main/java/advent/code/y2022/Dia01.java`
