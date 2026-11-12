# Año 2025 — Día 5: Cafetería

## Descripción del problema

Los montacargas derriban la pared y, efectivamente, al otro lado hay una cafetería. En la cocina los elfos están en problemas: justo antes de Navidad cambiaron a un nuevo sistema de inventario y ya no saben qué ingredientes están **frescos** y cuáles **descompuestos**.

El input es la base de datos del sistema y tiene dos secciones separadas por una línea en blanco:

1. **Rangos de IDs frescos**, inclusivos (`3-5` significa 3, 4 y 5). Los rangos pueden solaparse.
2. **IDs de ingredientes disponibles**, uno por línea.

```
3-5
10-14
16-20
12-18

1
5
8
11
17
32
```

En el input real los IDs llegan a ~5·10¹⁴, así que no caben en un `int`: todo se trabaja con `long`. Hay 183 rangos y 1000 IDs.

---

## Parte 1

### Pregunta

Un ID es fresco si cae en **al menos un** rango. ¿Cuántos de los IDs disponibles son frescos?

### Ejemplo

| ID | Rangos que lo contienen | ¿Fresco? |
|----|-------------------------|----------|
| 1 | ninguno | no |
| 5 | `3-5` | sí |
| 8 | ninguno | no |
| 11 | `10-14` | sí |
| 17 | `16-20` y `12-18` | sí |
| 32 | ninguno | no |

Resultado del ejemplo: **3**

### Desarrollo de la solución

#### La idea directa

Para cada ID, revisar si está en algún rango. Con 183 rangos y 1000 IDs son ~183 000 comparaciones, lo que es instantáneo. Pero hay una forma más ordenada, que además escala si hubiera muchos más IDs.

#### Unir los rangos y buscar con búsqueda binaria

1. **Unir los rangos solapados.** La utilidad `ConjuntoRangos` lo hace al construirse (con `Rango.unir`): ordena los rangos por inicio y fusiona los que se solapan o son contiguos. En el ejemplo, `10-14`, `12-18` y `16-20` quedan como un único `10-20`:

   ```
   3-5, 10-14, 16-20, 12-18   →   3-5, 10-20
   ```

   El resultado son rangos **disjuntos y ordenados**, y "estar en algún rango" sigue significando exactamente lo mismo.

2. **Búsqueda binaria.** Como los rangos unidos están ordenados y no se solapan, el único que puede contener a un ID es el **último rango que empieza antes o en ese ID**. Se encuentra con búsqueda binaria y luego basta revisar si el ID no se pasa de su fin.

```java
// ConjuntoRangos.java — contiene()
int bajo = 0;
int alto = unidos.size() - 1;
int candidato = -1;             // último rango con inicio <= valor
while (bajo <= alto) {
    int medio = (bajo + alto) >>> 1;
    if (rangos.get(medio).inicio() <= valor) {
        candidato = medio;
        bajo = medio + 1;
    } else {
        alto = medio - 1;
    }
}
return candidato >= 0 && rangos.get(candidato).contiene(valor);
```

Con eso la parte 1 queda en pocas líneas:

```java
// Dia05.java — parte1()
BaseDeDatos base = BaseDeDatos.parsear(lineas);
ConjuntoRangos frescos = new ConjuntoRangos(base.rangos());
long cantidad = base.disponibles().stream()
        .filter(frescos::contiene)
        .count();
```

El parseo separa las dos secciones con `Utiles.secciones` (record `BaseDeDatos`) y reutiliza `Rango.parsear` para las líneas `a-b`.

#### Verificación

- Prueba con el ejemplo (3) y con la clasificación de cada ID según la tabla del enunciado.
- Prueba del parseo de las dos secciones.
- En `PruebaConjuntoRangos`: extremos inclusivos y una prueba basada en propiedades (jqwik) que, con rangos y valores aleatorios, compara `contiene` y `cantidad` con enumerar todos los valores en un `Set`.
- Con el input real, un script independiente en Python que revisa cada ID contra todos los rangos obtiene el mismo resultado.

**Resultado con el input real: 635**

---

## Parte 2

### Pregunta

Ahora la lista de IDs disponibles no importa. ¿Cuántos IDs **distintos** consideran frescos los rangos? Un ID que está en varios rangos se cuenta una sola vez.

### Ejemplo

Los rangos `3-5`, `10-14`, `16-20` y `12-18` consideran frescos los IDs 3, 4, 5 y del 10 al 20.

Resultado del ejemplo: `3 + 11 =` **14**

### Desarrollo de la solución

#### Por qué no se pueden enumerar los IDs

La idea más directa sería meter todos los IDs de todos los rangos en un conjunto y contarlos. Con el ejemplo funciona, pero en el input real los rangos tienen del orden de 10¹² a 10¹⁴ valores cada uno: no hay memoria ni tiempo para enumerarlos.

#### Clave: unir y sumar largos

Si los rangos no se solaparan, la respuesta sería simplemente la suma de sus largos (`fin − inicio + 1`). El problema son los solapamientos, que contarían dos veces los mismos IDs. La solución es la misma unión de la parte 1: después de `Rango.unir` los rangos son disjuntos, así que ya se pueden sumar sus largos sin contar nada dos veces.

```
3-5, 10-14, 16-20, 12-18   →   3-5, 10-20   →   3 + 11 = 14
```

`ConjuntoRangos.cantidad()` hace exactamente eso, así que la parte 2 queda en una línea:

```java
// Dia05.java — parte2()
BaseDeDatos base = BaseDeDatos.parsear(lineas);
return String.valueOf(new ConjuntoRangos(base.rangos()).cantidad());
```

La respuesta (~3,7·10¹⁴) cabe de sobra en un `long`.

#### Verificación

- Prueba con el ejemplo (14).
- Casos de solapamiento: un rango contenido en otro, un rango repetido, rangos contiguos (`1-5` y `6-10`) y rangos de un solo ID.
- Prueba basada en propiedades (jqwik): con rangos pequeños aleatorios, el resultado coincide con enumerar todos los IDs en un `Set` y contarlos.
- Con el input real, un script independiente en Python que hace un **barrido de eventos** (+1 al inicio de cada rango, −1 después de su fin, sumando las zonas donde hay al menos un rango activo) obtiene el mismo resultado.

**Resultado con el input real: 369761800782619**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | IDs disponibles que caen en algún rango | IDs distintos cubiertos por los rangos |
| **Enfoque** | `ConjuntoRangos`: unir rangos + búsqueda binaria por ID | `ConjuntoRangos.cantidad()`: unir rangos y sumar sus largos |
| **Complejidad** | O(R log R + N log R), con R rangos y N IDs | O(R log R) |
| **Resultado** | 635 | 369761800782619 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia05.java`
