# Año 2023 — Día 8: Páramo Embrujado

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/8](https://adventofcode.com/2023/day/8). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Seguimos en camello por la Isla del Desierto cuando se acerca una tormenta de arena, y la Elfa **desaparece** ante nuestros ojos (justo nos había estado advirtiendo sobre fantasmas). En una alforja del camello hay **mapas** (el input): una lista de instrucciones de izquierda y derecha, y una red de nodos con nombre. Quizás siguiéndolas logremos salir del páramo embrujado.

La primera línea son las instrucciones (`L` izquierda, `R` derecha). Después de una línea en blanco viene la red: `AAA = (BBB, CCC)` dice que desde `AAA` se va a `BBB` por la izquierda y a `CCC` por la derecha. Estamos en `AAA` y hay que llegar a `ZZZ`. Si las instrucciones se acaban, se **repiten** desde el principio: `RL` en realidad es `RLRLRL…`.

#### Ejemplo

```
RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)
```

Desde `AAA`, la `R` lleva a `CCC` y la `L` a `ZZZ`: **2** pasos. En otro ejemplo, con instrucciones `LLR` y la red `AAA = (BBB, BBB)`, `BBB = (AAA, ZZZ)`, `ZZZ = (ZZZ, ZZZ)`, hacen falta **6** pasos, repitiendo las instrucciones.

#### Pregunta

¿Cuántos pasos hacen falta para llegar de `AAA` a `ZZZ`?

### Solución

#### El input real

277 instrucciones y 750 nodos.

#### Seguir las instrucciones

Se guarda la red en un mapa de cada nodo a sus dos salidas, y se avanza un paso por instrucción. La instrucción del paso número `p` es la de la posición `p mod largo`, así se repiten sin copiarlas:

```java
// Dia08.java — Red.siguiente()
Salidas salidas = nodos.get(nodo);
if (salidas == null) throw new IllegalArgumentException("El nodo " + nodo + " no está en la red");
char instruccion = instrucciones.charAt((int) (paso % instrucciones.length()));
return instruccion == 'L' ? salidas.izquierda() : salidas.derecha();
```

`pasos` recibe el nodo de partida y una **condición de llegada** (en esta parte, ser `ZZZ`), por si hiciera falta llegar a otros nodos.

#### Sin bucles infinitos

El recorrido depende solo del nodo actual y de la posición en las instrucciones. Hay a lo sumo `nodos × instrucciones` pares distintos (unos 200 000), así que si después de esa cantidad de pasos no se llegó, algún par se repitió y el recorrido quedó dando vueltas en un ciclo que no pasa por el destino. En ese caso se lanza una excepción en vez de seguir para siempre.

#### Verificación

- Prueba con los dos ejemplos (2 y 6), y el **recorrido paso a paso** del segundo, que repite las instrucciones.
- Una red donde nunca se llega a `ZZZ` falla en vez de colgarse.
- Instrucciones con otra letra, un nodo mal formado o un nodo que falta en la red fallan con un mensaje claro.
- Con el input real, un script independiente en Python recorre las instrucciones con `itertools.cycle` y obtiene el mismo resultado.

**Resultado con el input real: 13019**

---

## Parte 2

### Enunciado

La tormenta nos alcanza y casi no avanzamos. ¿Y si el mapa no es para personas, sino para **fantasmas**? Hay tantos nodos terminados en `A` como terminados en `Z`. Un fantasma probablemente empezaría a la vez en **todos los nodos terminados en `A`** y seguiría todos los caminos al mismo tiempo, con las mismas instrucciones, hasta que **todos a la vez** estén en nodos terminados en `Z`. Si solo algunos están en un `Z`, siguen como si nada.

#### Ejemplo

```
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
```

Desde `11A` y `22A`, después de los pasos 1 a 6 los fantasmas están en `11B`/`22B`, `11Z`/`22C`, `11B`/`22Z`, `11Z`/`22B`, `11B`/`22C` y `11Z`/`22Z`: recién en el paso **6** están los dos en un `Z`.

#### Pregunta

¿Cuántos pasos hacen falta para que todos los fantasmas estén a la vez en nodos terminados en `Z`?

### Solución

#### Demasiados pasos para simular

La respuesta del input real son más de 13 billones de pasos: mover a los seis fantasmas paso por paso no termina nunca. La idea conocida es mirar a cada fantasma por separado: si uno llega a un `Z` cada `g` pasos exactos (en los pasos `g`, `2g`, `3g`, …), todos coinciden por primera vez en el **mínimo común múltiplo** de sus `g`.

Pero eso solo vale si las llegadas de cada fantasma son **exactamente los múltiplos de la primera**, y el enunciado no lo garantiza. Un fantasma podría llegar en los pasos 1, 4, 7, … (cada 3 pasos, pero empezando en 1), o pasar por dos nodos `Z` distintos en su ciclo, y el mcm daría un número equivocado. Así que en vez de suponerlo, se **comprueba**.

#### Comprobar la periodicidad

El recorrido de un fantasma depende solo del **estado** (nodo, posición en las instrucciones). La utilidad `Ciclo.detectar` avanza ese estado hasta que uno se repite y devuelve los estados recorridos, dónde empieza el ciclo y cuánto mide (`λ`). A partir de ahí todo se repite cada `λ` pasos, así que las llegadas hasta ese momento son **todas** las llegadas:

```java
// Dia08.java — Red.periodoDeLlegada()
Ciclo<Posicion> recorrido = Ciclo.detectar(new Posicion(inicio, 0), posicion -> new Posicion(
        siguiente(posicion.nodo(), posicion.instruccion()),
        (posicion.instruccion() + 1) % instrucciones.length()));
// los pasos 1 … inicio + λ: el último es el primero que repite un estado
long paso = recorrido.estados().size();
long ciclo = recorrido.largo();
List<Long> llegadas = new ArrayList<>();
for (long i = 1; i <= paso; i++) {
    if (esFin.test(recorrido.estadoEn(i).nodo())) llegadas.add(i);
}
```

Si las llegadas son exactamente `g, 2g, 3g, …` (sin saltearse ningún múltiplo hasta ese punto) y `g` divide a `λ`, entonces las llegadas son los múltiplos de `g` para siempre: cada llegada posterior es una anterior más `λ`, que también es múltiplo de `g`. Si no, se lanza una excepción que dice que el mcm no sirve, en vez de devolver un resultado incorrecto.

En el ejemplo, el fantasma de `22A` repite estado en el paso 7, con un ciclo de 6 y llegadas en los pasos 3 y 6: su período es 3, aunque el ciclo de estados mida 6 (las dos instrucciones dan lo mismo en esa parte de la red). El de `11A` tiene período 2, y mcm(2, 3) = 6.

Con períodos garantizados, la respuesta es `Matematicas.mcm` de los seis. En el input real, los ciclos de los seis fantasmas miden entre 11 911 y 21 883 pasos, y todo termina en unos 30 ms.

#### Verificación

- Prueba con el ejemplo (6) y con los **períodos** de cada fantasma (2 y 3).
- Coincide con la **fuerza bruta** (mover a todos los fantasmas a la vez) en el ejemplo y en una red de tres fantasmas con períodos 2, 3 y 4 (12 pasos).
- Dos redes donde el mcm daría un resultado equivocado (llegadas en 1, 4, 7, … y en los impares) fallan en vez de devolver un número.
- Un fantasma que nunca llega falla con un mensaje claro.
- Con el input real, un script independiente en Python calcula el ciclo de estados de cada fantasma y, saltando con ese ciclo, **dónde está cada uno en el paso de la respuesta**: los seis están en un nodo `Z`. También comprueba que en `T/p`, para cada primo `p` que divide a la respuesta, no están todos en `Z`. Esto último por sí solo no prueba que sea el primer paso, pero junto con la periodicidad sí: si cada fantasma llega exactamente en los múltiplos de su período, los pasos en que coinciden todos son exactamente los múltiplos del mcm.

**Resultado con el input real: 13524038372771**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Pasos de AAA a ZZZ | Pasos hasta que todos los fantasmas estén en un Z |
| **Enfoque** | Simular, repitiendo las instrucciones | Período de cada fantasma (comprobado) y mínimo común múltiplo |
| **Complejidad** | O(pasos) | O(fantasmas × nodos × instrucciones) en el peor caso |
| **Resultado** | 13019 | 13524038372771 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia08.java`
