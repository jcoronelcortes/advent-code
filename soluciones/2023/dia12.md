# Año 2023 — Día 12: Aguas Termales

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/12](https://adventofcode.com/2023/day/12). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Por fin llegamos a las **aguas termales**, aunque no están muy calientes: falta **lava**. Tampoco se fabrica nada en la Isla Engranaje hasta que vuelva a llegar lava para las forjas. Nos piden subir a ver por qué dejó de fluir, usando uno de los **resortes** del campo para impulsarnos. El problema: muchos resortes están rotos, y los registros de cuáles están dañados (el input) también están dañados.

Cada línea es una fila de resortes: `.` sano, `#` dañado y `?` desconocido. Después de un espacio viene, en orden, el **tamaño de cada grupo** de resortes dañados seguidos de la fila. Los grupos siempre están separados por al menos un resorte sano: `####` es un grupo de 4, nunca 2,2.

Hay que contar de **cuántas formas** se pueden completar los `?` para que la fila tenga exactamente esos grupos.

#### Ejemplo

```
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
```

La primera fila tiene un solo arreglo (`#.#.###`). La segunda tiene 4: el último `?` tiene que ser `#`, y cada `??` esconde exactamente un dañado. La última tiene 10. En total: 1 + 4 + 1 + 1 + 4 + 10 = **21**.

#### Pregunta

¿Cuánto suman las cantidades de arreglos de todas las filas?

### Solución

#### El input real

1000 filas de hasta 20 resortes, con hasta 18 `?` por fila.

#### Programación dinámica

Probar todas las formas de completar los `?` funciona con filas cortas (2<sup>18</sup> son unas 260 000 por fila), pero crece al doble con cada `?`. En cambio, lo que importa en cada momento es solo **por dónde va la fila** y **qué grupo toca colocar**: `formas[i][j]` es la cantidad de formas de completar los resortes desde la posición `i` con los grupos desde el `j`. Se llena de atrás hacia adelante:

```java
// Dia12.java — Fila.arreglos()
for (int i = n - 1; i >= 0; i--) {
    char resorte = resortes.charAt(i);
    for (int j = m; j >= 0; j--) {
        long total = 0;
        if (resorte != DANADO) total = formas[i + 1][j];
        if (resorte != SANO && j < m) {
            int tamanio = grupos[j];
            boolean cabe = posiblesDanados[i] >= tamanio
                    && (i + tamanio == n || resortes.charAt(i + tamanio) != DANADO);
            if (cabe) total = Math.addExact(total, formas[Math.min(i + tamanio + 1, n + 1)][j + 1]);
        }
        formas[i][j] = total;
    }
}
```

- Si el resorte `i` puede estar **sano** (`.` o `?`), se sigue con el siguiente y los mismos grupos.
- Si puede **empezar el grupo** `j` (`#` o `?`), el grupo entero tiene que caber sin pisar ningún `.`, y el resorte de después no puede ser `#` (si no, el grupo sería más largo). Si cabe, se salta el grupo y su separador, que queda como sano.
- Al final de la fila hay 1 forma si no quedan grupos, y 0 si quedan. La fila extra `n + 1` representa "después del separador del último grupo".

Para saber en O(1) si un grupo cabe, se precalcula `posiblesDanados[i]`: cuántos resortes seguidos desde `i` no son `.`. Así cada fila se resuelve en O(resortes × grupos).

#### Verificación

- Prueba con el ejemplo (21) y con los arreglos de **cada fila** (1, 4, 1, 1, 4 y 10).
- Las filas sin `?` del enunciado tienen exactamente 1 arreglo, y `#### 2,2` tiene 0.
- En el ejemplo coincide con la **fuerza bruta**: probar todas las formas de completar los `?`.
- Con jqwik, en **500 filas al azar** de hasta 14 resortes, coincide con la fuerza bruta.
- Una fila mal formada falla con un mensaje claro.
- Con el input real, un script independiente en Python usa **otra formulación**: recursión con memoria que recorre carácter por carácter llevando el largo de la racha de `#` actual, en vez de saltar grupos enteros. Obtiene el mismo resultado.

**Resultado con el input real: 7204**

---

## Parte 2

### Enunciado

Mirando el campo, parece haber muchos más resortes de los que dicen los registros: estaban **doblados**. Para desdoblar una fila, los resortes se reemplazan por **cinco copias** de sí mismos separadas por `?`, y los grupos, por cinco copias separadas por comas. Por ejemplo, `.# 1` pasa a ser `.#?.#?.#?.#?.# 1,1,1,1,1`.

#### Ejemplo

Desdobladas, las filas del ejemplo tienen 1, 16384, 1, 16, 2500 y 506250 arreglos: en total, **525152**.

#### Pregunta

Con las filas desdobladas, ¿cuánto suman las cantidades de arreglos?

### Solución

#### Desdoblar y reusar la programación dinámica

Desdoblar es armar la fila nueva: los resortes repetidos cinco veces con `?` en el medio, y los grupos repetidos cinco veces.

```java
// Dia12.java — Fila.desdoblar()
String resortesDesdoblados = String.join(String.valueOf(DESCONOCIDO), Collections.nCopies(copias, resortes));
int[] gruposDesdoblados = new int[grupos.length * copias];
for (int copia = 0; copia < copias; copia++) {
    System.arraycopy(grupos, 0, gruposDesdoblados, copia * grupos.length, grupos.length);
}
return new Fila(resortesDesdoblados, gruposDesdoblados);
```

Las filas pasan a tener hasta 104 resortes y hasta 94 `?` (contando los separadores): probar todas las combinaciones (2<sup>94</sup>) es imposible. Pero la programación dinámica de la parte 1 no depende de cuántos `?` haya, solo del largo de la fila y de la cantidad de grupos, así que se usa sin cambios y termina en unos 20 ms. Los resultados llegan a billones por fila, y se suman con `Math.addExact` por si no cupieran en un `long`.

#### Verificación

- Prueba con el ejemplo (525152) y con los arreglos de **cada fila desdoblada** (1, 16384, 1, 16, 2500 y 506250).
- Desdoblar `.# 1` y la primera fila del ejemplo da exactamente lo del enunciado.
- Con **dos** copias (donde todavía es posible), coincide con la fuerza bruta en todas las filas del ejemplo.
- Con el input real, el script independiente en Python de la parte 1 (la otra formulación, con la racha de `#` actual) desdobla las filas cinco veces y obtiene el mismo resultado.

**Resultado con el input real: 1672318386674**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los arreglos posibles de cada fila | Lo mismo, con las filas desdobladas × 5 |
| **Enfoque** | Programación dinámica sobre (posición, grupo) | La misma programación dinámica |
| **Complejidad** | O(resortes × grupos) por fila | O(resortes × grupos) por fila |
| **Resultado** | 7204 | 1672318386674 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia12.java`
