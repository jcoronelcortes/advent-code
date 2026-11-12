# Año 2025 — Día 7: Laboratorios

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/7](https://adventofcode.com/2025/day/7). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Nos despedimos de los cefalópodos y salimos a los pasillos de un ala de investigación del Polo Norte. Un cartel grande dice "centro de teletransportación", y no resistimos la tentación de subirnos a la gran plataforma amarilla. De pronto aparecemos en una sala sin puertas, cuya única salida es el teletransportador... que está echando humo mágico.

En la sala hay repuestos, manuales y equipos de diagnóstico. Al conectar uno, muestra el código de error **0H-N0**: hay un problema en uno de los **colectores de taquiones**. Encontramos un diagrama del colector (el input):

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

- Un **haz de taquiones** entra al colector por la posición `S` y **siempre baja**.
- El haz atraviesa libremente el espacio vacío (`.`).
- Si el haz llega a un **divisor** (`^`), se detiene, y en su lugar salen **dos haces nuevos**: uno desde la posición inmediatamente a la **izquierda** del divisor y otro desde la inmediatamente a la **derecha**.
- Si dos haces caen en la misma posición, se juntan en uno solo.

#### Ejemplo

El haz baja desde `S` hasta el primer divisor, que lo parte en dos; esos dos bajan hasta los dos divisores siguientes, que en total generan solo **tres** haces, porque ambos vierten en la columna que está entre ellos. El proceso sigue hasta que cada haz llega a un divisor o sale del colector. Con los haces dibujados como `|`, queda así:

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

En este ejemplo, el haz se divide **21** veces en total.

#### Pregunta

Para reparar el teletransportador, primero hay que entender cómo se dividen los haces. En el diagrama del colector, ¿cuántas veces se divide el haz?

### Solución

#### El input real

Es una grilla de 142 × 141 con 1754 divisores. Los divisores aparecen solo en filas alternas, nunca pegados entre sí ni en los bordes.

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
            if (columna > 0) siguientes[columna - 1] = Math.addExact(siguientes[columna - 1], caminos);
            if (columna < columnas - 1) siguientes[columna + 1] = Math.addExact(siguientes[columna + 1], caminos);
        } else {
            siguientes[columna] = Math.addExact(siguientes[columna], caminos);
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

### Enunciado

Terminado el análisis, empezamos a reparar el teletransportador. Al abrir el costado para cambiar el colector roto, descubrimos que no es un colector clásico: es un colector **cuántico**.

En un colector cuántico se envía **una sola partícula**, que en cada divisor toma **los dos** caminos, izquierda y derecha. Como eso es imposible, el manual recomienda la interpretación de **muchos mundos**: cada vez que la partícula llega a un divisor, lo que se divide es el tiempo. En una **línea temporal** la partícula fue a la izquierda, y en otra fue a la derecha.

Lo que hay que saber es cuántas líneas temporales quedan activas después de que la partícula completa todos sus recorridos posibles por el colector.

#### Ejemplo

Con el mismo diagrama hay muchas líneas temporales: por ejemplo, aquella en la que la partícula siempre fue a la izquierda, o aquella en la que alternó izquierda y derecha en cada divisor. Incluso hay líneas temporales que terminan en el **mismo punto** que otra, pero por un camino totalmente distinto: siguen siendo líneas temporales diferentes.

En total, la partícula termina en **40** líneas temporales distintas.

#### Pregunta

Aplicando la interpretación de muchos mundos al diagrama del colector, ¿en cuántas líneas temporales distintas termina una sola partícula de taquiones?

### Solución

#### Clave: es contar caminos, y la parte 1 ya lo hacía

Cada línea temporal es un camino distinto desde `S` hasta salir por abajo. La simulación de la parte 1 ya guarda **cuántos caminos** llegan a cada columna en cada fila; la parte 1 solo miraba si ese número era mayor que cero. Para la parte 2 basta con sumar los caminos que salen por la última fila:

```java
// Dia07.java — parte2()
return String.valueOf(Matematicas.sumaExacta(simular(Grilla.desdeLineas(lineas)).haces()));
```

#### Por qué no enumerar los caminos

La cantidad de caminos crece de forma **exponencial** con el número de divisores: con el input real son ~5,8·10¹³. Recorrerlos uno por uno sería imposible. Sumar conteos por columna es **programación dinámica**: todos los caminos que llegan a la misma celda se tratan juntos, sumando sus cantidades, en una sola pasada.

Como los números son grandes, las sumas usan `Math.addExact` (en la simulación) y `Matematicas.sumaExacta` (al final), que lanzan una excepción si un conteo no cupiera en un `long` en vez de dar un resultado incorrecto en silencio.

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
