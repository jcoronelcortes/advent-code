# Año 2023 — Día 21: Contador de Pasos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/21](https://adventofcode.com/2023/day/21). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Alcanzamos el dirigible justo a tiempo y nos deja cerca del jardinero de la Isla del Desierto. Mientras esperamos que haya suficiente arena para filtrar el agua, un elfo que trabaja con él nos pide ayuda: necesita completar sus pasos del día y quiere saber a qué **parcelas del jardín** puede llegar dando **exactamente 64 pasos** más.

Nos da un mapa (el input) con su posición de partida (`S`), las parcelas (`.`) y las rocas (`#`). La posición de partida también es una parcela. En cada paso el elfo se mueve una casilla al norte, sur, este u oeste, pero solo a parcelas, nunca a rocas.

#### Ejemplo

```
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
```

Con un paso puede llegar a 2 parcelas, con dos a 4 (entre ellas la de partida, yendo y volviendo) y con tres a 6. Con **6 pasos** puede terminar en **16** parcelas distintas.

#### Pregunta

Partiendo de `S`, ¿en cuántas parcelas puede terminar el elfo con exactamente 64 pasos?

### Solución

#### El input real

Un mapa de 131 × 131 con `S` justo en el centro.

#### Distancia y paridad

En vez de simular el conjunto de posiciones paso por paso, alcanza con las **distancias mínimas** desde `S`, que salen de un solo BFS. Una parcela a distancia `d` se puede alcanzar en exactamente `n` pasos si y solo si:

- `d ≤ n`, porque hace falta llegar; y
- `d` tiene **la misma paridad** que `n`: los pasos que sobran se gastan de a dos, yendo a una parcela vecina y volviendo. Y si la paridad es distinta, no hay forma, porque cada paso cambia la paridad de `x + y`.

```java
// Dia21.java — alcanzables()
return contar(BFS.explorar(mapa.buscarUnico(INICIO), p -> mapa.vecinos(p, c -> c != ROCA)), pasos);
```

donde `contar` cuenta las distancias que son a lo sumo `pasos` y tienen su misma paridad.

La cantidad de pasos es un parámetro (64 en esta parte), y el punto de partida se busca con `Grilla.buscarUnico`, que falla si falta o está repetido.

#### Verificación

- Prueba con el ejemplo (16 parcelas en 6 pasos).
- Con 0, 1, 2 y 3 pasos se alcanzan 1, 2, 4 y 6 parcelas, como en los dibujos del enunciado.
- Simulando paso por paso, las parcelas alcanzables en 6 pasos son **exactamente** las `O` del dibujo del enunciado.
- En el ejemplo, coincide con la **fuerza bruta** de simular el conjunto de posiciones paso por paso para todas las cantidades de 0 a 30 pasos.
- Con el input real, un script independiente en Python simula el conjunto de posiciones durante 64 pasos y obtiene el mismo resultado.

**Resultado con el input real: 3542**

---

## Parte 2

### Enunciado

El elfo se había confundido: el número 64 venía de una lista de sus números favoritos. En realidad necesita dar exactamente **26501365 pasos**. Además, el jardín se **repite infinitamente** en todas las direcciones: más allá de cada borde del mapa vuelve a aparecer el mismo mapa, aunque la `S` de partida es solo la del centro (en las copias es una parcela común).

En el mapa infinito del ejemplo se alcanzan 16 parcelas en 6 pasos, 50 en 10, 1594 en 50, 6536 en 100, 167004 en 500, 668697 en 1000 y 16733044 en 5000.

#### Pregunta

En el mapa infinito, partiendo de `S`, ¿en cuántas parcelas puede terminar el elfo con exactamente 26501365 pasos?

### Solución

#### El mapa infinito, con un BFS acotado

El primer paso es poder contar en el mapa infinito para cantidades moderadas de pasos. Las coordenadas ya no se limitan al mapa: la posición `(x, y)` tiene lo que haya en `(x mod columnas, y mod filas)` (con `Math.floorMod`, que da restos no negativos). Para que el BFS termine, solo se exploran las posiciones a distancia Manhattan de a lo sumo el máximo que interesa. No se pierde nada: en un camino mínimo de largo `d`, todas las posiciones están a distancia Manhattan menor que `d`. El criterio de distancia y paridad de la parte 1 sigue valiendo.

#### Una cuadrática

Con 26501365 pasos el rombo alcanzable tiene unos 1,4 · 10¹⁵ casillas, así que hace falta otra idea. El input tiene una forma especial: la fila y la columna de `S`, y los cuatro bordes del mapa, están **libres de rocas**. Además, `S` está en el centro de un mapa de lado 131, y

```
26501365 = 65 + 131 · 202300
```

Es decir, el elfo llega justo hasta el borde de un mapa repetido, 202300 mapas más allá. Si se miran solo las cantidades de pasos `65 + 131 · k`, el rombo alcanzable crece exactamente un mapa por lado en cada `k`: cubre una cantidad de mapas enteros que crece como `k²`, más un borde de mapas a medio cubrir que crece como `k`. La cantidad de parcelas resulta un **polinomio cuadrático** en `k`.

No se da por sentado: se calculan con un solo BFS las cantidades para `k = 0, 1, 2, 3`, se comprueba que las **segundas diferencias** sean iguales (con tres muestras se fija la cuadrática y la cuarta la confirma) y se evalúa en `K = 202300` con diferencias hacia adelante, `f(K) = f(0) + K·Δ + K(K−1)/2·Δ²`:

```java
// Dia21.java — alcanzablesExtrapolando()
long[] muestras = new long[MUESTRAS];
for (int k = 0; k < MUESTRAS; k++) muestras[k] = contar(distancias, resto + (long) lado * k);
long primera = muestras[1] - muestras[0];
long segunda = muestras[2] - 2 * muestras[1] + muestras[0];
for (int k = 3; k < MUESTRAS; k++) {
    long otra = muestras[k] - 2 * muestras[k - 1] + muestras[k - 2];
    if (otra != segunda) {
        throw new IllegalStateException("Las cantidades no crecen como una cuadrática: "
                + Arrays.toString(muestras));
    }
}
long combinaciones = Math.multiplyExact(vueltas, vueltas - 1) / 2;
return Math.addExact(muestras[0],
        Math.addExact(Math.multiplyExact(vueltas, primera), Math.multiplyExact(combinaciones, segunda)));
```

Con el input real las muestras son 3725, 32896, 91055 y 178202, y las segundas diferencias valen 28988 las dos veces. La mayor muestra necesita 458 pasos, unas 420 000 posiciones en el BFS, y todo tarda unos 0,35 segundos. Si las cantidades no crecieran como una cuadrática (por ejemplo, en un mapa con rocas en la fila de `S`), falla en vez de extrapolar mal.

#### Verificación

- En el mapa infinito del ejemplo, el BFS acotado da las cantidades del enunciado para 6, 10, 50, 100, 500 y 1000 pasos.
- El BFS acotado coincide con **simular paso por paso** el conjunto de posiciones en el mapa infinito, de 0 a 25 pasos, y con la parte 1 mientras no se llega al borde del mapa.
- En el ejemplo con la fila y la columna de `S` despejadas (como el input real), extrapolar coincide con el BFS directo para **todos los restos** módulo 11 y `k` de 0 a 9.
- En el ejemplo original, que tiene rocas en la fila y la columna de `S`, las cantidades no son una cuadrática y la extrapolación falla; un mapa que no es cuadrado también.
- Con el input real, un script independiente en Python:
  - calcula seis muestras (`k` de 0 a 5) con su propio BFS y comprueba que las **cuatro** segundas diferencias son iguales;
  - obtiene el mismo resultado con un **método completamente distinto**: contar mapas enteros. Con un BFS dentro de un solo mapa, cuenta las parcelas de distancia par e impar, y las de las "esquinas" (distancia mayor que 65). Después combina `(K+1)²` mapas de una paridad, `K²` de la otra y las esquinas que sobran o faltan en el borde del rombo.

**Resultado con el input real: 593174122420825**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Parcelas alcanzables en exactamente 64 pasos | Lo mismo con 26501365 pasos en el mapa infinito |
| **Enfoque** | BFS: distancia ≤ pasos y con la misma paridad | Cuadrática en k para 65 + 131·k, comprobada y extrapolada |
| **Complejidad** | O(filas × columnas) | O((4 · lado)²): un BFS hasta la cuarta muestra |
| **Resultado** | 3542 | 593174122420825 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia21.java`
