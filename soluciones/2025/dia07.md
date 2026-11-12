# Año 2025 — Día 7: Laboratorios

## Descripción del problema

Al salir del compactador llegamos a un ala de investigación del Polo Norte que estudia la **teletransportación**. Subimos al teletransportador y aparecemos en una sala sin puertas, con el teletransportador echando humo: el diagnóstico apunta a un problema en uno de los **colectores de taquiones**.

El input es el diagrama del colector:

- Un haz de taquiones entra por `S` y **siempre baja**.
- El espacio vacío (`.`) no lo afecta.
- Al llegar a un **divisor** (`^`) el haz se detiene, y salen dos haces nuevos: uno desde la posición inmediata a la **izquierda** del divisor y otro desde la inmediata a la **derecha**.
- Si dos haces llegan a la misma posición, se **funden** en uno.

```
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............
```

El input real es una grilla de 142 × 141 con 1754 divisores. Los divisores aparecen solo en filas alternas, nunca pegados entre sí ni en los bordes.

---

## Parte 1

### Pregunta

¿Cuántas veces se divide el haz?

### Ejemplo

El diagrama final con los haces (`|`):

```
.......S.......
.......|.......
......|^|......
......|.|......
.....|^|^|.....
.....|.|.|.....
....|^|^|^|....
....|.|.|.|....
...|^|^|||^|...
...|.|.|||.|...
..|^|^|||^|^|..
..|.|.|||.|.|..
.|^|||^||.||^|.
.|.|||.||.||.|.
|^|^|^|^|^|||^|
|.|.|.|.|.|||.|
```

En la fila 4, los dos divisores vierten haces en la misma columna central, así que entre los dos generan solo **3** haces, no 4. El ejemplo tiene 22 divisores, pero al de la fila 14, columna 9 no le llega ningún haz (en la fila 13 la columna 9 está vacía), así que no cuenta.

Resultado del ejemplo: **21**

### Desarrollo de la solución

#### Clave: los haces solo bajan

Como ningún haz sube, el problema se puede simular **fila por fila**: basta saber en qué columnas hay haz en la fila actual para calcular las de la siguiente. No hace falta una búsqueda en el grafo (BFS), ni recordar posiciones visitadas.

Por cada fila, para cada columna con haz:

- si la celda es un divisor, se cuenta una división y el haz pasa a las columnas vecinas (si están dentro de la grilla);
- si no, el haz sigue en la misma columna.

Los haces que coinciden en una columna se funden solos, porque la columna simplemente "tiene haz". Cada divisor se cuenta como máximo una vez: está en una sola fila y cada fila se procesa una vez.

#### Contar caminos en vez de marcar columnas

En lugar de un arreglo de booleanos se guarda, para cada columna, **cuántos caminos** distintos del haz llegan ahí (`long[]`). Para la parte 1 solo importa si es mayor que cero, pero es la misma simulación y el conteo queda disponible por si la parte 2 necesita distinguir los caminos (y lo necesitó).

```java
// Dia07.java — simular()
for (int fila = inicio.y() + 1; fila < grilla.filas(); fila++) {
    long[] siguientes = new long[columnas];
    for (int columna = 0; columna < columnas; columna++) {
        long caminos = haces[columna];
        if (caminos == 0) continue;

        if (grilla.obtener(new Punto(columna, fila)) == DIVISOR) {
            divisiones++;
            if (columna > 0) siguientes[columna - 1] += caminos;
            if (columna < columnas - 1) siguientes[columna + 1] += caminos;
        } else {
            siguientes[columna] += caminos;
        }
    }
    haces = siguientes;
}
```

Un haz nuevo "sale" junto al divisor en su misma fila y empieza a bajar desde ahí; como al lado de un divisor nunca hay otro divisor, equivale a ponerlo directamente en la fila siguiente.

#### Verificación

- Prueba con el ejemplo (21).
- Prueba de haces que se funden: en las primeras filas del ejemplo hay 3 divisiones y solo 3 columnas con haz.
- Un divisor al que no llega ningún haz no cuenta, y sin divisores el haz baja derecho (0 divisiones).
- Con el input real, un script independiente en Python que hace una **BFS** sobre las posiciones `(fila, columna)` de los haces, guardando las visitadas y los divisores alcanzados, obtiene el mismo resultado.

**Resultado con el input real: 1635**

---

## Parte 2

### Pregunta

Resulta que el colector es **cuántico**: se envía una sola partícula, y en cada divisor toma **los dos** caminos. Según la interpretación de muchos mundos, en cada divisor se divide el tiempo: en una línea temporal la partícula fue a la izquierda y en otra a la derecha. ¿En cuántas líneas temporales distintas termina la partícula?

### Ejemplo

El enunciado muestra algunas líneas temporales: la que siempre va a la izquierda, la que alterna izquierda y derecha, y otra que termina en el **mismo punto** que la alternada pero por un camino totalmente distinto. Esa última es la diferencia clave con la parte 1: dos caminos que llegan al mismo lugar **no se funden**, cuentan como líneas temporales distintas.

Resultado del ejemplo: **40**

### Desarrollo de la solución

#### Clave: es contar caminos, y la parte 1 ya lo hacía

Cada línea temporal es un camino distinto desde `S` hasta salir por abajo. La simulación de la parte 1 ya guarda **cuántos caminos** llegan a cada columna en cada fila; la parte 1 solo miraba si ese número era mayor que cero. Para la parte 2 basta con sumar los caminos que salen por la última fila:

```java
// Dia07.java — parte2()
long lineasTemporales = 0;
for (long caminos : simular(Grilla.desdeLineas(lineas)).haces()) {
    lineasTemporales = Math.addExact(lineasTemporales, caminos);
}
```

#### Por qué no enumerar los caminos

La cantidad de caminos crece de forma **exponencial** con el número de divisores: con el input real son ~5,8·10¹³. Recorrerlos uno por uno sería imposible. Sumar conteos por columna es **programación dinámica**: todos los caminos que llegan a la misma celda se tratan juntos, sumando sus cantidades, en una sola pasada.

Como los números son grandes, las sumas usan `Math.addExact`, que lanza una excepción si un conteo no cupiera en un `long` en vez de dar un resultado incorrecto en silencio.

#### Verificación

- Prueba con el ejemplo (40).
- Prueba de caminos que coinciden: en las primeras filas del ejemplo, la columna central recibe 2 caminos (no 1, como en la parte 1), para un total de 4.
- Casos borde: sin divisores hay una sola línea temporal, y un camino que sale por el costado de la grilla se pierde.
- Prueba contra un método independiente: en un diagrama sintético de 30 × 31 con 7231 caminos, la simulación coincide con contar los caminos **recursivamente** de arriba hacia abajo con `Memoizador`.
- Con el input real, un script independiente en Python con recursión memoizada (`lru_cache`) obtiene el mismo resultado.

**Resultado con el input real: 58097428661390**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Cantidad de veces que el haz se divide | Cantidad de líneas temporales (caminos distintos) |
| **Enfoque** | Simulación fila por fila con un arreglo de caminos por columna | La misma simulación: sumar los caminos que salen por abajo |
| **Complejidad** | O(filas · columnas) | O(filas · columnas) |
| **Resultado** | 1635 | 58097428661390 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia07.java`
