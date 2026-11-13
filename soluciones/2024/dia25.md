# Año 2024 — Día 25: Crónica del Código

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/25](https://adventofcode.com/2024/day/25). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Sin más ideas ni tiempo, los Historiadores deciden volver una última vez a la oficina del Historiador Jefe, por si volvió sin que lo notáramos. La puerta está **cerrada con llave**, y se escucha a alguien adentro que no responde. Las cerraduras del piso son versiones virtuales de **cerraduras de cinco pernos**, y seguridad del Polo Norte no sabe qué llave va con cada cerradura: solo puede mandar los **esquemas** de todas las cerraduras y llaves del piso (el input).

Cada esquema es un dibujo de 7 filas por 5 columnas, separado del siguiente por una línea en blanco:

- una **cerradura** tiene la fila de arriba llena (`#####`) y la de abajo vacía; sus **pernos** son columnas que bajan desde arriba;
- una **llave** tiene la fila de arriba vacía y la de abajo llena; sus **dientes** son columnas que suben desde abajo.

Cada esquema se puede convertir en una lista de **alturas**, una por columna, sin contar la fila llena. Una llave **puede** entrar en una cerradura si en ninguna columna el perno y el diente se superponen, es decir, si en cada columna la suma de las dos alturas no pasa del espacio disponible.

#### Ejemplo

Con dos cerraduras y tres llaves, las alturas son:

| Cerraduras | Llaves |
|---|---|
| `0,5,3,4,3` | `5,0,2,1,3` |
| `1,2,0,5,3` | `4,3,4,0,2` |
| | `3,0,2,0,1` |

La primera cerradura choca con la primera llave en la última columna (3 + 3 > 5) y con la segunda en la segunda columna, pero encaja con la tercera. La segunda cerradura choca con la primera llave en la primera columna y encaja con las otras dos. En total, **3** pares encajan.

#### Pregunta

¿Cuántos pares distintos de cerradura y llave encajan sin superponerse en ninguna columna?

### Solución

#### El input real

500 esquemas de 7 × 5: 250 cerraduras y 250 llaves.

#### Alturas y comparación de todos los pares

Cada esquema se clasifica por sus filas de arriba y de abajo, y se convierte en alturas contando los `#` de cada columna y restando uno (la fila llena es la base, no un perno ni un diente). Contar sirve para los dos tipos porque pernos y dientes son columnas sin huecos.

Después se prueba cada cerradura con cada llave: 250 × 250 = 62 500 pares de 5 columnas, nada que optimizar.

```java
// Dia25.java — encajan()
for (int columna = 0; columna < cerradura.length; columna++) {
    if (cerradura[columna] + llave[columna] > espacio) return false;
}
return true;
```

El espacio disponible es el alto del esquema sin las dos filas de los extremos (5), y se calcula a partir del input en vez de fijarlo. Un esquema que no sea ni cerradura ni llave, o esquemas de distinto alto, fallan con un mensaje claro.

#### Verificación

- Prueba con el ejemplo (3), y las **alturas** de las dos cerraduras y las tres llaves, como en el enunciado.
- Cada uno de los seis pares del ejemplo encaja o no, como dice el enunciado.
- En el ejemplo, comparar alturas da lo mismo que **superponer los dibujos** casilla por casilla.
- Un esquema que no es ni cerradura ni llave falla.
- Con el input real, un script independiente en Python superpone los dibujos como **conjuntos de casillas `#`**: un par encaja si los conjuntos no tienen ninguna casilla en común. Obtiene el mismo resultado.

**Resultado con el input real: 3320**

---

## Parte 2

### Enunciado

El día 25 es el último del año y su segunda parte **no tiene un problema propio**: la segunda estrella se obtiene al completar todos los demás días.

### Solución

El último día no tiene un segundo problema: su segunda estrella se obtiene al completar los otros 24 días. `parte2` devuelve el texto fijo `"estrella final"`, para que la tabla anual y `RespuestasPrueba` traten este día igual que los demás.

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Pares de cerradura y llave que encajan | No tiene problema |
| **Enfoque** | Alturas por columna y comparar todos los pares | — |
| **Complejidad** | O(cerraduras × llaves × columnas) | — |
| **Resultado** | 3320 | estrella final |

## Archivo de código

`src/main/java/advent/code/y2024/Dia25.java`
