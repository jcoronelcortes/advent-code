# Año 2022 — Día 3: Reorganización de Mochilas

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2022/day/3](https://adventofcode.com/2022/day/3). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Un elfo cargó las mochilas para la expedición, pero no siguió del todo las instrucciones. Cada mochila tiene **dos compartimientos**, y todos los objetos de un mismo tipo deberían ir en uno solo; el elfo se equivocó con **exactamente un tipo** por mochila.

El input tiene el contenido de cada mochila en una línea. Cada tipo de objeto es una letra, y las mayúsculas y minúsculas son tipos distintos. Los dos compartimientos tienen siempre la misma cantidad de objetos: la primera mitad de la línea es el primero y la segunda mitad, el segundo.

Cada tipo tiene una **prioridad**: de `a` a `z` valen 1 a 26, y de `A` a `Z`, 27 a 52. Hay que encontrar el tipo que aparece en los dos compartimientos de cada mochila y sumar sus prioridades.

#### Ejemplo

```
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
```

La primera mochila se divide en `vJrwpWtwJgWr` y `hcsFMMfFFhFp`, que solo comparten la `p`. En las otras, los tipos repetidos son `L`, `P`, `v`, `t` y `s`. Sus prioridades son 16, 38, 42, 22, 20 y 19, que suman **157**.

#### Pregunta

¿Cuánto suman las prioridades de los tipos que aparecen en los dos compartimientos de cada mochila?

### Solución

#### El input real

300 mochilas, todas de largo par.

#### Conjuntos como bits

Hay solo 52 tipos, así que el conjunto de tipos de un texto entra en un `long`: el bit `p − 1` se prende si aparece el tipo de prioridad `p`. Con eso, los tipos comunes a varios textos son el **AND** de sus máscaras, y la prioridad del único común es la posición de su bit más 1:

```java
// Dia03.java — unicoComun()
long comunes = -1L;
for (String objetos : grupos) comunes &= tipos(objetos);
if (Long.bitCount(comunes) != 1) {
    throw new IllegalArgumentException("Se esperaba un único tipo en común y hay "
            + Long.bitCount(comunes) + " en " + grupos);
}
return Long.numberOfTrailingZeros(comunes) + 1;
```

Se empieza con todos los bits prendidos (`-1L`) para que el primer AND deje la máscara del primer texto. `Long.bitCount` cuenta los bits prendidos, y así se comprueba lo que asegura el enunciado: que haya **exactamente un** tipo en común. Si no, falla con un mensaje claro, igual que una mochila de largo impar o un carácter que no es una letra.

La función recibe una lista de textos y no solo dos, porque comparar compartimientos es un caso particular de buscar lo común entre varios.

#### Verificación

- Prueba con el ejemplo (157).
- Los tipos comunes del ejemplo tienen exactamente las prioridades del enunciado (16, 38, 42, 22, 20 y 19).
- Las prioridades de los extremos (`a` 1, `z` 26, `A` 27, `Z` 52), y un carácter que no es letra falla.
- En cada mochila del ejemplo, la máscara coincide con la **fuerza bruta** de comparar letra por letra con `indexOf`.
- Un largo impar, ningún tipo en común o más de uno fallan; un tipo repetido dentro de un compartimiento cuenta una sola vez.
- Con el input real, un script independiente en Python intersecta conjuntos (`set`) y busca la prioridad en un diccionario; comprueba que siempre haya un único tipo común y obtiene el mismo resultado.

**Resultado con el input real: 7878**

---

## Parte 2

### Enunciado

Por seguridad, los elfos están divididos en **grupos de tres**, y cada uno lleva una **insignia** que identifica a su grupo. Dentro de un grupo, la insignia es el **único** tipo de objeto que llevan los tres elfos; cualquier otro tipo lo llevan a lo sumo dos. Nadie anotó qué tipo es la insignia de cada grupo, así que hay que deducirlo.

Cada tres líneas del input forman un grupo (las tres primeras, las tres siguientes, y así).

#### Ejemplo

En el primer grupo (las tres primeras mochilas del ejemplo), el único tipo que aparece en las tres es la `r`; en el segundo, la `Z`. Sus prioridades son 18 y 52, que suman **70**.

#### Pregunta

¿Cuánto suman las prioridades de las insignias de todos los grupos?

### Solución

#### Lo común entre tres

La función `unicoComun` de la parte 1 ya recibe una lista de textos, así que la insignia de un grupo es lo común entre sus tres mochilas: el AND de tres máscaras en vez de dos. Solo falta agrupar las líneas de a tres, que es lo que hace `Gatherers.windowFixed` (Java 24+):

```java
// Dia03.java — parte2()
return String.valueOf(mochilas.stream()
        .gather(Gatherers.windowFixed(TAMANIO_GRUPO))
        .mapToLong(Dia03::unicoComun)
        .sum());
```

`windowFixed` deja una ventana más corta al final si sobran líneas, así que antes se exige que la cantidad de mochilas sea múltiplo de 3; si no, falla con un mensaje claro en vez de buscar la insignia de un grupo incompleto.

#### Verificación

- Prueba con el ejemplo (70).
- Las insignias del ejemplo son `r` (18) y `Z` (52), y coinciden con la **fuerza bruta** de comparar letra por letra con `indexOf`.
- Una cantidad de mochilas que no es múltiplo de 3, o un grupo con ninguno o más de un tipo en común, fallan.
- Con el input real, un script independiente en Python intersecta los conjuntos de las tres mochilas de cada uno de los 100 grupos, comprueba que la insignia sea única y obtiene el mismo resultado.

**Resultado con el input real: 2760**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de prioridades del tipo repetido en cada mochila | Suma de prioridades de las insignias de cada grupo de tres |
| **Enfoque** | Conjuntos de tipos como máscaras de 52 bits; intersección con AND | El mismo AND sobre grupos de tres (`Gatherers.windowFixed`) |
| **Complejidad** | O(largo del input) | O(largo del input) |
| **Resultado** | 7878 | 2760 |

## Archivo de código

`src/main/java/advent/code/y2022/Dia03.java`
