# Año 2025 — Día 12: Granja de Árboles de Navidad

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/12](https://adventofcode.com/2025/day/12). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Queda poco tiempo y casi todo está decorado. No hay más escaleras, ascensores ni teletransportadores para seguir bajando, pero sí un ducto de ventilación. Saltamos adentro y, después de unos minutos de golpes, salimos a una caverna grande y bien iluminada, llena de **árboles de Navidad**.

Los elfos que decoran a toda prisa están preocupados por una sola cosa: los **regalos** para los elfos pequeños. Es tradición ponerlos debajo de los árboles, y temen que no quepan. Los regalos vienen en unas pocas **formas** estándar pero muy raras, y tanto las formas como las regiones bajo los árboles se miden en unidades de una grilla.

El input tiene dos secciones:

1. **Las formas de los regalos.** Cada una empieza con su índice y dos puntos, seguidos de un dibujo, donde `#` es parte del regalo y `.` no.
2. **Las regiones bajo los árboles.** Cada línea da el ancho y el largo de la región y cuántos regalos de cada forma hay que acomodar en ella: `12x5: 1 0 1 0 3 2` es una región de 12 de ancho por 5 de largo, donde va 1 regalo de forma 0, ninguno de forma 1, 1 de forma 2, ninguno de forma 3, 3 de forma 4 y 2 de forma 5.

```
0:      1:      2:      3:      4:      5:
###     ###     .##     ##.     ###     ###
##.     ##.     ###     ###     #..     .#.
##.     .##     ##.     ##.     ###     ###

4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2
```

(En el input, cada forma va en su propio bloque, una debajo de la otra; aquí se muestran en fila para ahorrar espacio.)

Las reglas para acomodar los regalos:

- Se pueden **rotar y voltear**, pero siempre alineados con la grilla.
- **No se pueden apilar ni superponer**: dos regalos no pueden tener un `#` en la misma posición.
- Sí pueden **encajar** entre sí: el `.` de una forma no ocupa lugar, así que otro regalo puede usar esa posición.

#### Ejemplo

| Región | Regalos | ¿Caben? |
|--------|---------|---------|
| `4x4` | dos de forma 4 | sí |
| `12x5` | uno de forma 0, uno de forma 2, dos de forma 4 y dos de forma 5 | sí |
| `12x5` | lo mismo, pero con **tres** de forma 4 | no, de ninguna manera |

Una forma de acomodar los regalos de las dos primeras regiones (cada letra es un regalo):

```
AAA.        ....AAAFFE.E
ABAB        .BBBAAFFFEEE
ABAB        DDDBAAFFCECE
.BBB        DBBB....CCC.
            DDD.....C.C.
```

En el ejemplo, los regalos caben en **2** regiones.

#### Pregunta

¿En cuántas de las regiones bajo los árboles caben todos los regalos indicados?

### Solución

#### El input real

Hay 6 formas (de 5 a 7 celdas cada una) y **1000 regiones** de entre ~35 y ~50 de lado, con cientos de regalos cada una.

#### El problema general es muy difícil

Acomodar piezas en una región (*packing*) es un problema NP-completo: en general no se conoce nada mucho mejor que probar combinaciones. Con cientos de regalos por región, una búsqueda exhaustiva sería imposible. Antes de buscar, conviene mirar el input.

#### Clave: dos cotas exactas que deciden todo el input

1. **No caben** si las celdas `#` de todos los regalos suman **más que el área** de la región: los regalos no se pueden superponer, así que ningún acomodo existe.
2. **Caben** si la región tiene **al menos un bloque de 3×3 por regalo** (`⌊ancho/3⌋ · ⌊largo/3⌋ ≥ regalos`): cada regalo entra en su propio bloque, sin tocar a los demás. Ni siquiera hace falta encajarlos.

Las dos cotas son **exactas** (no son heurísticas): cuando deciden, la respuesta es segura. En el input real, **las 1000 regiones quedan decididas**: 462 tienen más celdas que área y 538 tienen un bloque por regalo. No hay ninguna región intermedia, así que la respuesta es exacta sin buscar nada.

El lado del bloque es el del menor cuadrado que contiene la forma (3 en el input); se calcula con el rango real de las celdas en cada eje.

#### Búsqueda exhaustiva para los casos dudosos

El ejemplo sí tiene casos intermedios: las dos regiones de 12×5 tienen más regalos (6 y 7) que bloques de 3×3 (4), y sus celdas caben en el área (42 y 49 de 60). Para esos casos hay una búsqueda con **backtracking por celdas**:

- Se recorren las celdas en orden de lectura. En cada celda libre hay dos opciones: **dejarla vacía**, o **colocar un regalo** cuya primera celda (en orden de lectura) caiga justo ahí. Como las celdas anteriores ya están decididas, cualquier regalo que cubra esta celda tiene que empezar en ella: la búsqueda es completa.
- **Poda:** la cantidad de celdas que pueden quedar vacías es fija (`área − celdas de los regalos`). Al agotarse, cada celda libre tiene que llenarse.
- Los regalos de la misma forma son intercambiables: se cuentan por forma, para no probar las mismas combinaciones en otro orden.

Las orientaciones de cada forma (hasta 8: 4 rotaciones, con y sin volteo) se calculan con `Grilla.rotarDerecha` y `Grilla.transponer`, normalizadas para descartar las repetidas: una forma con simetría tiene menos orientaciones distintas.

#### Verificación

- Prueba con el ejemplo (2), región por región (sí, sí, no), y comprobando que las cotas **no** deciden las dos de 12×5, así que la búsqueda realmente se ejercita.
- Parseo de formas y regiones; cantidad de orientaciones distintas (1 para un cuadrado lleno, 4 para la "C", 8 para una forma sin simetrías); lado del cuadrado contenedor, incluida una forma cuya primera celda está al medio.
- Las cotas, y que la búsqueda coincide con ellas cuando estas deciden.
- Encajes ajustados: dos "C" en un 4×4, dominós llenando un 2×3 sin celdas vacías, y 4 dominós sí pero 5 no en un 3×3.
- En Python, una búsqueda independiente distinta (coloca cada regalo en cualquier posición libre, sin recorrer por celdas) confirma el ejemplo: sí, sí, no. Tarda 2,5 minutos en demostrar que la tercera región es imposible, contra menos de un segundo de la búsqueda por celdas.
- Con el input real, un script en Python clasifica las 1000 regiones con las mismas cotas: 462 imposibles, 538 posibles, 0 dudosas.

**Resultado con el input real: 538**

---

## Parte 2

### Enunciado

El día 12 es el último del año y su segunda parte **no tiene un problema propio**: la segunda estrella se obtiene al completar todos los demás días.

### Solución

El último día no tiene un segundo problema: su segunda estrella se obtiene al completar los otros 11 días. `parte2` devuelve el texto fijo `"estrella final"`, para que la tabla anual y `RespuestasPrueba` traten este día igual que los demás.

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Regiones donde caben todos los regalos | No tiene problema |
| **Enfoque** | Dos cotas exactas (área y bloques de 3×3); backtracking por celdas si no deciden | — |
| **Complejidad** | O(regiones) con las cotas; exponencial en la búsqueda (solo para el ejemplo) | — |
| **Resultado** | 538 | estrella final |

## Archivo de código

`src/main/java/advent/code/y2025/Dia12.java`
