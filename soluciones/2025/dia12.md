# Año 2025 — Día 12: Granja de Árboles de Navidad

## Descripción del problema

Saltamos por un ducto de ventilación y aparecemos en una caverna llena de árboles de Navidad. Los elfos están preocupados por los **regalos**: es tradición ponerlos debajo de los árboles, pero tienen formas muy raras y temen que no quepan.

El input tiene dos secciones:

1. **Formas de los regalos**: un índice y un dibujo de 3×3 (`#` es parte del regalo).
2. **Regiones bajo los árboles**: `12x5: 1 0 1 0 2 2` significa una región de 12 de ancho por 5 de largo, donde hay que acomodar 1 regalo de forma 0, ninguno de forma 1, 1 de forma 2, etc.

```
0:          4:
###         ###
##.         #..
##.         ###
...
4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2
```

Los regalos se pueden **rotar y voltear**, pero siempre sobre la grilla y **sin superponerse**: los `.` de una forma no ocupan lugar, así que otro regalo puede encajar ahí.

El input real tiene 6 formas (de 5 a 7 celdas cada una) y **1000 regiones** de entre ~35 y ~50 de lado, con cientos de regalos cada una.

---

## Parte 1

### Pregunta

¿En cuántas regiones caben todos sus regalos?

### Ejemplo

| Región | Regalos | ¿Caben? |
|--------|---------|---------|
| `4x4` | dos de forma 4 | sí: `AAA.` / `ABAB` / `ABAB` / `.BBB` |
| `12x5` | 1, 0, 1, 0, 2, 2 | sí |
| `12x5` | 1, 0, 1, 0, **3**, 2 | no, de ninguna forma |

Resultado del ejemplo: **2**

### Desarrollo de la solución

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
