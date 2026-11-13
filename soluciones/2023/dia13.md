# Año 2023 — Día 13: Punto de Incidencia

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/13](https://adventofcode.com/2023/day/13). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El resorte nos lanza hasta el borde de la **Isla de la Lava**, pero de lava no hay rastro: solo ceniza y roca. Entre unas montañas hay un valle lleno de **espejos** enormes, muchos caídos y clavados en la ceniza en ángulos raros. Como todo es del mismo color, cuesta saber dónde se puede caminar sin chocar con un espejo, así que anotamos los patrones de ceniza (`.`) y rocas (`#`) que vemos (el input), separados por líneas en blanco.

Cada patrón tiene un **reflejo perfecto**: una línea horizontal entre dos filas, o una vertical entre dos columnas, tal que las filas (o columnas) a cada lado coinciden como en un espejo. Si la línea no está justo en el medio, las filas que no tienen con quién reflejarse se ignoran.

Para **resumir** las notas se suman las columnas a la izquierda de cada línea vertical, más **100** por las filas encima de cada línea horizontal.

#### Ejemplo

```
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
```

El primer patrón se refleja en una línea vertical entre las columnas 5 y 6 (la columna 1 no tiene con quién reflejarse). El segundo, en una horizontal entre las filas 4 y 5. El resumen es 5 + 100 · 4 = **405**.

#### Pregunta

¿Cuál es el resumen de todas las notas?

### Solución

#### El input real

100 patrones, cada uno con exactamente una línea de reflejo.

#### Contar diferencias en vez de comparar

Para la línea horizontal que deja `k` filas arriba, se comparan las filas que quedan enfrentadas: `k − 1` con `k`, `k − 2` con `k + 1`, y así hasta que uno de los lados se termina. En vez de preguntar "¿son iguales?", se **cuentan las casillas distintas**; un reflejo perfecto tiene 0:

```java
// Dia13.java — lineasDeReflejo()
for (int k = 1; k < patron.filas(); k++) {
    int distintas = 0;
    for (int arriba = k - 1, abajo = k; arriba >= 0 && abajo < patron.filas(); arriba--, abajo++) {
        distintas += distintas(patron.fila(arriba), patron.fila(abajo));
        if (distintas > diferencias) break;
    }
    if (distintas == diferencias) lineas.add(k);
}
```

La cantidad de diferencias es un parámetro (0 en esta parte). Si se pasa, se deja de comparar esa línea.

#### Las verticales, transponiendo

Una línea vertical entre columnas es una línea horizontal del patrón **transpuesto** (filas y columnas intercambiadas, con `Grilla.transponer`), así que el mismo código encuentra las dos. Cada patrón tiene que tener exactamente una línea de reflejo; si no tiene ninguna o tiene más de una, falla con un mensaje claro en vez de elegir una.

#### Verificación

- Prueba con el ejemplo (405).
- El primer patrón refleja solo entre las columnas 5 y 6, y el segundo solo entre las filas 4 y 5.
- En los dos patrones, coincide con **reflejar literalmente** las filas (y las columnas) a cada lado de cada línea posible y compararlas.
- Un patrón sin reflejo, o con más de uno, falla.
- Con el input real, un script independiente en Python refleja las filas con *slicing* de listas, y las columnas armándolas como texto. Comprueba que cada patrón tiene exactamente una línea, y obtiene el mismo resultado.

**Resultado con el input real: 30802**

---

## Parte 2

### Enunciado

Al seguir caminando por el valle nos damos de frente contra un espejo. Mirando de cerca descubrimos que cada espejo tiene exactamente **una mancha**: una sola casilla que en realidad debería ser del tipo contrario (un `.` que es `#`, o al revés).

En cada patrón hay que encontrar la mancha cuya corrección hace válida una línea de reflejo **distinta** de la anterior (la línea vieja puede dejar de valer o no; da igual), y resumir las notas igual que antes pero con las líneas nuevas.

#### Ejemplo

Con los mismos dos patrones: en el primero, la mancha está en la esquina superior izquierda; al corregirla aparece una línea horizontal entre las filas 3 y 4. En el segundo, la mancha es la quinta casilla de la fila 2, y al corregirla aparece una línea horizontal entre las filas 1 y 2. El resumen es 100 · 3 + 100 · 1 = **400**.

#### Pregunta

¿Cuál es el resumen de las notas usando la nueva línea de reflejo de cada patrón?

### Solución

#### Una sola diferencia

No hace falta buscar la mancha. Si corregir una casilla deja un reflejo perfecto en cierta línea, antes de corregirla esa línea tenía **exactamente una** casilla distinta entre los dos lados: la mancha (que tiene que estar en la parte que se refleja; si estuviera en una fila sobrante no cambiaría nada). Y al revés: una línea con una única diferencia queda perfecta al corregir esa casilla.

Además, la línea nueva nunca puede ser la original, porque esa tiene cero diferencias. Así que la parte 2 es el mismo cálculo que la parte 1 con `diferencias = 1`:

```java
// Dia13.java — parte2()
return String.valueOf(resumen(lineas, 1));
```

El conteo corta en cuanto pasa de una diferencia, y `valor` sigue exigiendo que haya exactamente una línea que cumpla.

#### Verificación

- Prueba con el ejemplo (400).
- Con una diferencia, el primer patrón refleja solo entre las filas 3 y 4, y el segundo solo entre las filas 1 y 2.
- En los dos patrones, coincide con **probar cada mancha posible**: invertir cada casilla, buscar todas las líneas perfectas y quedarse con las que no son la original.
- Con el input real, un script independiente en Python hace esa misma fuerza bruta (invertir cada casilla de cada patrón y reflejar con *slicing*), comprueba que cada patrón tiene exactamente una línea nueva, y obtiene el mismo resultado.

**Resultado con el input real: 37876**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Resumen de las líneas de reflejo | Resumen con la mancha corregida |
| **Enfoque** | Contar casillas distintas en cada línea; verticales transponiendo | La misma búsqueda con exactamente 1 casilla distinta |
| **Complejidad** | O(filas² × columnas) por patrón | O(filas² × columnas) por patrón |
| **Resultado** | 30802 | 37876 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia13.java`
