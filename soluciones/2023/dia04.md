# Año 2023 — Día 4: Rasca y Gana

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/4](https://adventofcode.com/2023/day/4). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

La góndola nos sube hasta otra isla flotante, más cálida y húmeda: la **Isla Isla**. Ahí, un Elfo sentado sobre una pila de tarjetas de colores nos ofrece su bote para ir a ver al jardinero (que quizás sepa de la fuente de agua), si lo ayudamos primero con algo: le regalaron un montón de **tarjetas de rasca y gana**, ya raspadas, y no entiende qué ganó.

Cada tarjeta tiene dos listas de números separadas por una barra (`|`): primero los **números ganadores** y después **los que tenemos**. Hay que ver cuáles de los nuestros están entre los ganadores. La primera coincidencia hace que la tarjeta valga **1 punto**, y cada coincidencia siguiente **duplica** el valor.

#### Ejemplo

```
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
```

La tarjeta 1 tiene cuatro coincidencias (48, 83, 17 y 86), así que vale 8 puntos (1, duplicado tres veces). Las demás valen 2, 2, 1, 0 y 0. En total, **13** puntos.

#### Pregunta

¿Cuántos puntos valen en total todas las tarjetas?

### Solución

#### El input real

204 tarjetas, cada una con 10 números ganadores y 25 propios. Ningún número se repite dentro de una misma lista.

#### Coincidencias y potencias de 2

De cada tarjeta solo importa **cuántas coincidencias** tiene. Los ganadores se guardan en un conjunto y se cuentan los números propios que están en él:

```java
// Dia04.java — Tarjeta.parsear()
int numero = Utiles.leerNumeros(encabezadoYNumeros[0], 1).getFirst();
Set<Integer> ganadores = new HashSet<>(Utiles.leerNumeros(ganadoresYPropios[0]));
int coincidencias = (int) Utiles.leerNumeros(ganadoresYPropios[1]).stream()
        .filter(ganadores::contains)
        .count();
```

`Utiles.leerNumeros` toma todos los números del texto sin importar cuántos espacios haya entre ellos (el input los alinea con espacios de más).

Con `k` coincidencias, la tarjeta vale 1 duplicado `k − 1` veces, o sea, 2<sup>k − 1</sup>; con 0 coincidencias, 0 puntos. Se calcula con un corrimiento de bits (`1L << (k - 1)`).

#### Verificación

- Prueba con el ejemplo (13), y con las **coincidencias y los puntos** de cada tarjeta (8, 2, 2, 1, 0, 0), como en el enunciado.
- Números separados por varios espacios, como en el input real.
- Una línea sin `:` o sin `|` falla con un mensaje claro.
- Con el input real, un script independiente en Python calcula las coincidencias como la **intersección de dos conjuntos**, y confirma de paso que no hay números repetidos (si los hubiera, intersecar y contar podrían dar distinto). Obtiene el mismo resultado.

**Resultado con el input real: 27059**

---

## Parte 2

### Enunciado

Justo antes de darle la respuesta al Elfo, nos damos cuenta de que las reglas estaban impresas al dorso de las tarjetas: **no hay puntos**. Cada tarjeta hace ganar **copias de las tarjetas siguientes**, tantas como coincidencias tenga: si la tarjeta 10 tiene 5 coincidencias, gana una copia de cada una de las tarjetas 11 a 15.

Las copias funcionan igual que las originales: una copia de la tarjeta 10 también gana copias de las tarjetas 11 a 15. Esto se repite hasta que ninguna tarjeta gane más. Ninguna tarjeta hace ganar copias más allá de la última.

#### Ejemplo

Con las mismas seis tarjetas de la parte 1:

- La tarjeta 1 (4 coincidencias) gana una copia de las tarjetas 2, 3, 4 y 5.
- Las 2 instancias de la tarjeta 2 (2 coincidencias) ganan, cada una, una copia de las 3 y 4.
- Las 4 instancias de la tarjeta 3 ganan, cada una, una copia de las 4 y 5.
- Las 8 instancias de la tarjeta 4 ganan, cada una, una copia de la 5.
- Las 14 instancias de la tarjeta 5 y la de la tarjeta 6 no ganan nada.

Al final hay 1, 2, 4, 8, 14 y 1 instancias de cada tarjeta: **30** en total.

#### Pregunta

Contando las originales, ¿cuántas tarjetas se tienen al final?

### Solución

#### Una pasada en orden

Simular cada copia por separado funciona en el ejemplo, pero en el input real se terminan teniendo casi 6 millones de tarjetas. No hace falta: todas las instancias de una tarjeta ganan lo mismo, así que alcanza con contar **cuántas instancias** hay de cada una.

Como las copias siempre son de tarjetas **posteriores**, al llegar a la tarjeta `i` (recorriéndolas en orden) ya se sabe cuántas instancias tiene: todas las tarjetas que podían darle copias ya se procesaron. Entonces sus `instancias[i]` ejemplares suman `instancias[i]` copias a cada una de las siguientes:

```java
// Dia04.java — instancias()
for (int siguiente = i + 1; siguiente <= i + coincidencias; siguiente++) {
    instancias[siguiente] = Math.addExact(instancias[siguiente], instancias[i]);
}
```

Cada tarjeta empieza con 1 instancia (la original). La respuesta es la suma del arreglo. Son a lo sumo 204 × 10 sumas.

Aunque el enunciado asegura que ninguna tarjeta gana copias más allá de la última, si pasara falla con un mensaje claro en vez de ignorarlo en silencio.

#### Verificación

- Prueba con el ejemplo (30) y con las **instancias de cada tarjeta** (1, 2, 4, 8, 14 y 1), como en el enunciado.
- En el ejemplo coincide con **simular las copias una por una** con una cola: cada tarjeta que sale de la cola agrega a la cola las que gana.
- Una tarjeta que ganaría copias más allá de la última falla.
- Con el input real, un script independiente en Python usa una **recursión con memoria**: las tarjetas que termina aportando la tarjeta `i` son ella misma más las que aportan las que gana. Sumando sobre todas las originales obtiene el mismo resultado.

**Resultado con el input real: 5744979**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Puntos de todas las tarjetas | Total de tarjetas con las copias ganadas |
| **Enfoque** | Contar coincidencias con un conjunto; 2^(k−1) puntos | Instancias de cada tarjeta en una pasada en orden |
| **Complejidad** | O(largo del input) | O(tarjetas × coincidencias) |
| **Resultado** | 27059 | 5744979 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia04.java`
