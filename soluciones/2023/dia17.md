# Año 2023 — Día 17: Crisol Torpe

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/17](https://adventofcode.com/2023/day/17). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con la fábrica de lava funcionando, el reno nos da un paracaídas y bajamos a la **Isla de los Engranajes**, donde media isla es una enorme ciudad fábrica. Los elfos cargan la lava en **crisoles** con ruedas, empujados a mano. Como son pesados arriba, cuesta mucho mantenerlos en línea recta a velocidad.

Hay que llevar un crisol desde la pileta de lava hasta la fábrica de piezas perdiendo el **menor calor** posible. El input es un mapa de la ciudad donde cada manzana tiene un dígito: el calor que se pierde al **entrar** en ella. Se parte de la manzana de arriba a la izquierda (su calor no cuenta, salvo que se vuelva a entrar) y el destino es la de abajo a la derecha.

Reglas de movimiento:

- El crisol puede avanzar **a lo sumo tres manzanas** seguidas en la misma dirección; después tiene que girar 90° a la izquierda o a la derecha.
- **No puede retroceder**: en cada manzana solo puede girar a la izquierda, seguir derecho o girar a la derecha.

#### Ejemplo

```
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
```

El mejor recorrido zigzaguea por la ciudad sin pasar nunca de tres manzanas en línea recta, y pierde **102** de calor.

#### Pregunta

¿Cuál es el menor calor que puede perder el crisol entre la pileta de lava y la fábrica?

### Solución

#### El input real

Un mapa de 141 × 141 manzanas.

#### Dijkstra sobre tramos

El camino de costo mínimo se busca con el `Dijkstra` compartido, pero el nodo no puede ser solo la manzana: lo que el crisol puede hacer depende de cómo llegó. La forma habitual es guardar (manzana, dirección, cuántas manzanas lleva en línea recta) y moverse de a una manzana.

En cambio, aquí cada movimiento es un **tramo completo**: girar 90° y avanzar de 1 a 3 manzanas de una vez, sumando el calor de cada una. Seguir derecho después de un tramo es lo mismo que haber hecho un tramo más largo, así que alcanza con guardar (manzana, **dirección del último tramo**), y como siempre se gira, nunca se retrocede. Hay 4 estados por manzana en vez de los 12 (4 direcciones × 3 largos) del contador:

```java
// Dia17.java — tramos()
for (Direccion giro : List.of(estado.direccion().giroIzquierda(), estado.direccion().giroDerecha())) {
    Punto posicion = estado.posicion();
    long perdida = 0;
    for (int largo = 1; largo <= maximoRecto; largo++) {
        posicion = posicion.mover(giro);
        if (!mapa.estaDentro(posicion)) break;
        perdida += mapa.obtener(posicion) - '0';
        if (largo >= minimoRecto) tramos.add(new Dijkstra.Arista<>(new Estado(posicion, giro), perdida));
    }
}
```

(La condición `largo >= minimoRecto` es para la parte 2; aquí el mínimo es 1.)

Al principio el crisol todavía no se movió, así que puede salir hacia cualquier lado. Por eso se arranca desde **dos estados**: uno como si viniera de un tramo horizontal (sale hacia arriba o abajo) y otro como si viniera de uno vertical (sale hacia la izquierda o derecha). Los largos mínimo y máximo del tramo son parámetros (1 y 3 en esta parte), y un mapa con algo que no sea un dígito falla con un mensaje claro.

#### Verificación

- Prueba con el ejemplo (102).
- Desde la esquina, los tramos giran 90°, miden de 1 a 3 manzanas, no salen del mapa y su costo es el calor acumulado de las manzanas en las que entran.
- En el ejemplo y en un mapa donde conviene zigzaguear, coincide con la **fuerza bruta** de Dijkstra paso a paso sobre (manzana, dirección, manzanas en línea recta).
- Con tramos de una sola manzana, en una franja de 2 filas el crisol tiene que zigzaguear y entra en 6 manzanas en vez de 4.
- Un mapa con algo que no es un dígito falla.
- Con el input real, un script independiente en Python hace Dijkstra paso a paso con `heapq` y el contador de manzanas en línea recta, y obtiene el mismo resultado.

**Resultado con el input real: 797**

---

## Parte 2

### Enunciado

Los crisoles comunes son chicos, así que los elfos pasan a usar **ultra crisoles**, que son todavía más difíciles de manejar: además de costarles ir derecho, les cuesta girar.

Una vez que un ultra crisol empieza a moverse en una dirección, tiene que avanzar **al menos 4 manzanas** antes de poder girar, e incluso antes de poder detenerse en el destino. Y puede avanzar **a lo sumo 10** seguidas sin girar. El resto de las reglas no cambia.

#### Ejemplo

Con el mismo mapa, el ultra crisol pierde como mínimo **94** de calor.

Otro ejemplo:

```
111111111111
999999999991
999999999991
999999999991
999999999991
```

El camino barato (toda la fila de arriba y después bajar por la columna de la derecha) no sirve: la fila de arriba tiene 11 manzanas para avanzar, más que el máximo de 10, y al girar antes el ultra crisol tiene que bajar 4 por la zona cara. Lo mejor que puede hacer pierde **71**.

#### Pregunta

¿Cuál es el menor calor que puede perder un ultra crisol entre la pileta de lava y la fábrica?

### Solución

#### Tramos de 4 a 10

El modelo de tramos de la parte 1 lo resuelve agregando un **largo mínimo**: cada tramo gira 90° y avanza de 4 a 10 manzanas. Las manzanas que atraviesa antes de llegar a 4 suman su calor, pero no generan un estado donde se pueda girar (es la condición `largo >= minimoRecto` del fragmento de la parte 1).

La regla de que no puede **detenerse** antes de avanzar 4 sale gratis: como todo tramo mide al menos 4, el crisol solo llega al destino al final de un tramo válido. En el modelo con contador, en cambio, hay que acordarse de exigir el mínimo también en la condición de meta, que es un error típico. La parte 2 es entonces la misma búsqueda con otros límites:

```java
// Dia17.java — parte2()
return String.valueOf(menorPerdida(Grilla.desdeLineas(lineas), MINIMO_ULTRA, MAXIMO_ULTRA));
```

Si no hay ningún camino posible (por ejemplo, una grilla de 3 × 3, donde ningún tramo de 4 entra), falla con un mensaje claro, igual que con largos que no tienen sentido (mínimo menor que 1, o mayor que el máximo).

#### Verificación

- Pruebas con los dos ejemplos (94 y 71).
- En el segundo ejemplo, un crisol que puede avanzar de 1 a 11 sí toma el camino barato por el borde (pierde 15).
- Desde la esquina, hay 7 tramos hacia la derecha (de 4 a 10 manzanas), y el más corto suma el calor de las cuatro manzanas que atraviesa.
- En los dos ejemplos, coincide con la **fuerza bruta** de Dijkstra paso a paso con el contador, que solo deja girar o detenerse con al menos 4 manzanas en línea recta.
- Sin camino posible, o con largos inválidos, falla.
- Con el input real, el script independiente en Python de la parte 1 (paso a paso con `heapq`), ahora con mínimo y máximo, obtiene el mismo resultado en las dos partes.

**Resultado con el input real: 914**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Menor calor perdido con tramos de hasta 3 | Lo mismo con tramos de 4 a 10 |
| **Enfoque** | Dijkstra sobre (manzana, dirección del último tramo), girando siempre | El mismo Dijkstra con largo mínimo de tramo |
| **Complejidad** | O(V log V) con V = filas × columnas × 4 | O(V log V) con V = filas × columnas × 4 |
| **Resultado** | 797 | 914 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia17.java`
