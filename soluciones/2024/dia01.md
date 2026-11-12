# Año 2024 — Día 1: Histeria del Historiador

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/1](https://adventofcode.com/2024/day/1). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El **Historiador Jefe** nunca falta al gran lanzamiento del trineo de Navidad, pero hace meses que nadie lo ve. Lo último que se supo es que estaba visitando lugares de importancia histórica para el Polo Norte. Un grupo de Historiadores Mayores nos pide acompañarlos a revisar los lugares donde es más probable que haya ido. Cada lugar revisado se marca con una **estrella**, y creen que el Jefe está en uno de los primeros cincuenta: hay que ayudarlos a reunir **cincuenta estrellas** antes de que Papá Noel despegue el 25 de diciembre. Cada día del calendario hay dos problemas, y cada uno resuelto da una estrella.

Antes de salir surge el primer problema: la lista de lugares a revisar está vacía. Deciden empezar por la oficina del Historiador Jefe, donde no está, pero encuentran notas y listas de lugares históricos. En la oficina, cada lugar no aparece por su nombre sino por un número único, su **ID de ubicación**. Para no perderse nada, los Historiadores se dividen en dos grupos, y cada uno arma su propia lista completa de IDs.

Al poner las dos listas lado a lado (el input), queda claro que no se parecen mucho. Cada línea tiene un número de la lista **izquierda** y uno de la lista **derecha**:

```
3   4
4   3
2   5
1   3
3   9
3   3
```

Quizás las listas solo difieren un poco. Para averiguarlo hay que **emparejar** los números y medir cuánto se separan: el menor de la izquierda con el menor de la derecha, el segundo menor con el segundo menor, y así sucesivamente. La **distancia** de cada par es la diferencia entre sus dos números (por ejemplo, entre 3 y 7 hay 4, y entre 9 y 3 hay 6), y la **distancia total** es la suma de las distancias de todos los pares.

#### Ejemplo

| Par | Izquierda | Derecha | Distancia |
|-----|-----------|---------|-----------|
| el menor de cada lista | 1 | 3 | 2 |
| el segundo menor | 2 | 3 | 1 |
| el tercero | 3 | 3 | 0 |
| el cuarto | 3 | 4 | 1 |
| el quinto | 3 | 5 | 2 |
| el mayor | 4 | 9 | 5 |

La distancia total del ejemplo es `2 + 1 + 0 + 1 + 2 + 5 =` **11**.

#### Pregunta

¿Cuál es la distancia total entre las dos listas del input?

### Solución

#### El input real

Son 1000 líneas, cada una con dos IDs de hasta 5 dígitos.

#### Ordenar y emparejar por posición

Emparejar "el k-ésimo menor de la izquierda con el k-ésimo menor de la derecha" es exactamente lo que se obtiene al **ordenar las dos listas** y emparejar los elementos que quedan en la misma posición:

```java
// Dia01.java — parte1()
Listas listas = Listas.parsear(lineas);
List<Long> izquierda = listas.izquierda().stream().sorted().toList();
List<Long> derecha = listas.derecha().stream().sorted().toList();

long total = 0;
for (int i = 0; i < izquierda.size(); i++) {
    total += Math.abs(izquierda.get(i) - derecha.get(i));
}
```

El parseo (record `Listas`) separa cada línea en sus dos números con `Utiles.leerNumerosLargos`, ignora las líneas en blanco y lanza un error si una línea no tiene exactamente dos números. Las listas se guardan en el orden del input.

Ordenar cuesta O(n log n); con 1000 números tarda unos milisegundos.

#### Verificación

- Prueba con el ejemplo (11) y con el parseo de las dos columnas.
- La distancia no depende del orden de las líneas ni de cuál lista tiene el número mayor.
- Prueba basada en propiedades (jqwik): con listas aleatorias, el resultado coincide con emparejar **sacando repetidamente el mínimo** de cada lista.
- Con el input real, un script independiente en Python que empareja sacando los mínimos de dos **montículos** (`heapq`), sin ordenar las listas, obtiene el mismo resultado.

**Resultado con el input real: 2378066**

---

## Parte 2

### Enunciado

El análisis solo confirmó lo que todos temían: las dos listas son muy distintas... ¿o no? Los Historiadores no se ponen de acuerdo sobre qué grupo se equivocó ni sobre cómo leer la letra del Jefe, pero en medio del alboroto notamos un detalle: **muchos IDs aparecen en las dos listas**. Quizás los demás números ni siquiera son IDs, sino letra mal interpretada.

Esta vez hay que averiguar exactamente **cuántas veces aparece en la lista derecha cada número de la lista izquierda**. La **puntuación de similitud** se calcula sumando cada número de la izquierda multiplicado por la cantidad de veces que aparece en la derecha.

#### Ejemplo

Con las mismas listas:

| Número de la izquierda | Veces en la derecha | Suma |
|------------------------|---------------------|------|
| 3 | 3 | 9 |
| 4 | 1 | 4 |
| 2 | 0 | 0 |
| 1 | 0 | 0 |
| 3 | 3 | 9 |
| 3 | 3 | 9 |

La puntuación de similitud del ejemplo es `9 + 4 + 0 + 0 + 9 + 9 =` **31**. Un número repetido en la izquierda (como el 3) suma una vez por cada aparición.

#### Pregunta

¿Cuál es la puntuación de similitud de las dos listas del input?

### Solución

#### Contar las apariciones una sola vez

La forma directa es, para cada número de la izquierda, recorrer la lista derecha contando cuántas veces aparece: O(n²). Con 1000 números serían un millón de comparaciones, todavía rápido, pero se puede hacer mejor contando primero las apariciones de **cada** número de la derecha.

Para eso se usa un **`Multiset`** de Guava: un conjunto que recuerda cuántas veces está cada elemento. Se construye una vez con la lista derecha, y después `count(numero)` responde en O(1):

```java
// Dia01.java — parte2()
Listas listas = Listas.parsear(lineas);
Multiset<Long> apariciones = HashMultiset.create(listas.derecha());

long similitud = 0;
for (long numero : listas.izquierda()) {
    similitud = Math.addExact(similitud, Math.multiplyExact(numero, apariciones.count(numero)));
}
```

Si un número no está en la derecha, `count` devuelve 0 y no suma nada. Los números repetidos de la izquierda suman una vez por cada aparición, sin ningún caso especial, porque se recorre la lista izquierda completa. El costo total es O(n).

#### Verificación

- Prueba con el ejemplo (31).
- Repeticiones: un número que está 3 veces a la izquierda y 2 a la derecha suma `3 · (3 · 2)`; listas sin números en común dan 0.
- Prueba basada en propiedades (jqwik): con listas aleatorias de números chicos (para forzar muchas repeticiones), el resultado coincide con contar cada número **recorriendo** la lista derecha.
- Con el input real, un script independiente en Python que usa `list.count` para cada número de la izquierda (fuerza bruta O(n²)) obtiene el mismo resultado.

**Resultado con el input real: 18934359**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Distancia total entre las listas emparejadas por orden | Puntuación de similitud |
| **Enfoque** | Ordenar ambas listas y sumar las diferencias por posición | Contar apariciones con un `Multiset` y sumar número × veces |
| **Complejidad** | O(n log n) | O(n) |
| **Resultado** | 2378066 | 18934359 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia01.java`
