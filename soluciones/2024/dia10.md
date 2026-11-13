# Año 2024 — Día 10: A Pie

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/10](https://adventofcode.com/2024/day/10). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos a una planta de producción de lava en una isla flotante en el cielo. Mientras los demás buscan, sentimos un empujoncito en la pierna: un reno con casco nos trae una "Guía de senderismo de la Isla de Lava", casi toda quemada por la lava, y un **mapa topográfico** en blanco de la zona (el input). Quizás podamos completar los senderos que faltan.

El mapa indica la **altura** de cada posición, de 0 (la más baja) a 9 (la más alta):

```
0123
1234
8765
9876
```

Según lo que se salvó de la guía, un buen **sendero** es lo más largo posible y sube de forma pareja y gradual. En la práctica, un sendero es cualquier camino que:

- empieza en altura **0** y termina en altura **9**;
- en cada paso sube **exactamente 1**;
- solo da pasos hacia arriba, abajo, izquierda o derecha, **nunca en diagonal**.

Un **inicio de sendero** es una posición donde empieza al menos un sendero (siempre de altura 0). Su **puntaje** es la cantidad de posiciones de altura **9** que se alcanzan desde ahí por algún sendero.

#### Ejemplo

En el mapa de arriba hay un solo inicio, en la esquina superior izquierda, con puntaje 1: alcanza un solo 9 (el de abajo a la izquierda).

En este otro (los `.` son intransitables, para simplificar; no aparecen en el mapa real), el inicio tiene puntaje 2:

```
...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9
```

En un ejemplo más grande:

```
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
```

hay 9 inicios que, en orden de lectura, tienen puntajes 5, 6, 5, 3, 1, 3, 5, 3 y 5. La suma es **36**.

#### Pregunta

¿Cuánto suman los puntajes de todos los inicios de sendero del mapa?

### Solución

#### El input real

Es un mapa de 56 × 56, con 296 posiciones de altura 0 y 247 de altura 9.

#### Una BFS desde cada inicio

El puntaje de un inicio es cuántas cimas (altura 9) distintas alcanza. Eso es exactamente una **búsqueda en anchura** desde el inicio, con la utilidad `BFS.explorar`, donde los vecinos transitables de una posición son los que tienen **una altura más**:

```java
// Dia10.java
static long puntaje(Grilla mapa, Punto inicio) {
    return BFS.explorar(inicio, posicion -> subidas(mapa, posicion)).keySet().stream()
            .filter(posicion -> mapa.obtener(posicion) == CIMA)
            .count();
}

static List<Punto> subidas(Grilla mapa, Punto posicion) {
    char siguiente = (char) (mapa.obtener(posicion) + 1);
    return mapa.vecinos(posicion, altura -> altura == siguiente);
}
```

Todo lo que la BFS alcanza está en algún sendero, y cada posición aparece **una sola vez** en el resultado, así que una cima a la que se llega por varios caminos cuenta una vez, como pide el puntaje.

Las alturas se comparan como caracteres: `'3' + 1` es `'4'`. Las celdas `.` de los ejemplos chicos nunca son "una altura más" que un dígito, así que quedan excluidas sin código extra.

#### Verificación

- Prueba con el ejemplo grande (36) y con el puntaje de cada uno de sus 9 inicios, en orden.
- Los cuatro ejemplos chicos del enunciado, con celdas `.` intransitables (puntajes 1, 2, 4 y 1 + 2), copiados del texto del enunciado.
- Con el input real, un script independiente en Python usa otro método: una programación dinámica **desde las cimas**, altura por altura de 9 a 0, que calcula para cada posición el conjunto de cimas alcanzables como la unión de los conjuntos de sus vecinos una altura más arriba. Obtiene el mismo resultado.

**Resultado con el input real: 694**

---

## Parte 2

### Enunciado

El reno vuelve con más páginas recuperadas de la guía. Hay otra forma de medir un inicio de sendero: su **calificación**, que es la cantidad de **senderos distintos** que empiezan en él. Dos senderos son distintos si pasan por alguna posición diferente, aunque terminen en la misma cima.

#### Ejemplo

Este inicio tiene calificación 3: los tres senderos llegan al mismo 9, pero por caminos distintos.

```
.....0.
..4321.
..5..2.
..6543.
..7..4.
..8765.
..9....
```

En el ejemplo grande de la parte 1, los 9 inicios tienen, en orden de lectura, calificaciones 20, 24, 10, 4, 1, 4, 5, 8 y 5, que suman **81**. Un mapa de 6 × 6 lleno de subidas, como

```
012345
123456
234567
345678
4.6789
56789.
```

tiene un solo inicio, pero con calificación 227: 121 senderos llegan al 9 del borde derecho y 106 al del borde inferior.

Y el mapa `..90..9` de la parte 1, cuyo inicio tenía puntaje 4, tiene calificación 13.

#### Pregunta

¿Cuánto suman las calificaciones de todos los inicios de sendero del mapa?

### Solución

#### Contar caminos en vez de cimas

Ahora importan los caminos, no solo a dónde llegan. Los senderos desde una posición se cuentan con una recursión:

- desde una **cima** hay exactamente **1** sendero (ya terminó);
- desde cualquier otra posición, la **suma** de los senderos desde cada vecino una altura más arriba (los mismos `subidas` de la parte 1).

Como la altura sube en cada paso, el grafo no tiene ciclos y la recursión siempre termina. Muchos senderos comparten tramos, así que se memoiza con la utilidad `Memoizador`:

```java
// Dia10.java
static Memoizador<Punto, Long> senderosHastaCima(Grilla mapa) {
    return new Memoizador<>((posicion, propio) -> {
        if (mapa.obtener(posicion) == CIMA) return 1L;
        long total = 0;
        for (Punto vecino : subidas(mapa, posicion)) {
            total += propio.apply(vecino);
        }
        return total;
    });
}
```

El mismo memoizador se usa para **todos** los inicios: el valor de una posición no depende de dónde se empezó, así que cada celda se calcula una sola vez y el costo total es O(celdas), menos que la BFS por inicio de la parte 1.

No hay riesgo de desborde: cada posición tiene a lo sumo 4 subidas y un sendero da 9 pasos, así que una calificación no pasa de 4⁹ = 262 144.

#### Verificación

- Prueba con el ejemplo grande (81) y con la calificación de cada uno de sus 9 inicios, en orden.
- Los ejemplos chicos del enunciado: calificaciones 3, 13 y 227.
- Con el input real, un script independiente en Python recorre con un DFS **sin memoización** y cuenta cada sendero uno por uno. Obtiene el mismo resultado.

**Resultado con el input real: 1497**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los puntajes (cimas distintas alcanzables) | Suma de las calificaciones (senderos distintos) |
| **Enfoque** | `BFS.explorar` desde cada inicio, subiendo de a 1 | Conteo de caminos con `Memoizador`, compartido entre inicios |
| **Complejidad** | O(inicios × celdas) | O(celdas) |
| **Resultado** | 694 | 1497 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia10.java`
