# Año 2025 — Día 6: Compactador de Basura

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/6](https://adventofcode.com/2025/day/6). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Después de ayudar en la cocina, nos ponemos a recrear con los elfos una escena de película y, con demasiado entusiasmo, saltamos al ducto de basura. Tras una breve caída aparecemos dentro de un **compactador de basura** con la puerta sellada magnéticamente.

Se nos acerca una familia de **cefalópodos**, que cree poder abrir la puerta, aunque les llevará un rato. Mientras esperamos, nos piden ayuda con la tarea de matemáticas de la más pequeña.

La matemática de los cefalópodos no es tan distinta de la nuestra. La hoja de ejercicios (el input) tiene una lista de problemas, y en cada uno hay que **sumar** (`+`) o **multiplicar** (`*`) un grupo de números. Lo raro es cómo están dispuestos: los problemas están **uno al lado del otro**, formando una lista horizontal muy larga.

```
123 328  51 64
 45 64  387 23
  6 98  215 314
*   +   *   +
```

- Los números de cada problema están **en vertical**, uno por fila.
- Debajo de cada problema está el símbolo de la operación.
- Los problemas están separados por una **columna formada solo por espacios**.
- La alineación de los números dentro de un problema (a la izquierda o a la derecha) no importa.

Para revisar su trabajo, los alumnos cefalópodos calculan el **gran total**: la suma de los resultados de todos los problemas. La hoja real es mucho más ancha, y hay que recorrerla entera.

#### Ejemplo

| Problema | Cuenta | Resultado |
|----------|--------|-----------|
| 1 | `123 * 45 * 6` | 33210 |
| 2 | `328 + 64 + 98` | 490 |
| 3 | `51 * 387 * 215` | 4243455 |
| 4 | `64 + 23 + 314` | 401 |

El gran total del ejemplo es `33210 + 490 + 4243455 + 401 =` **4277556**.

#### Pregunta

Resolviendo los problemas de la hoja, ¿cuál es el gran total?

### Solución

#### El input real

La hoja tiene 4 filas de números y una de operaciones, con 1000 problemas a lo ancho de ~3750 columnas.

La dificultad está en el **parseo**: el cálculo es una suma o un producto.

#### Por qué no basta con separar por espacios cada línea

Dividir cada línea por espacios y emparejar los números por posición funcionaría en la parte 1, pero pierde la información de qué columnas ocupa cada problema. Se prefirió seguir la definición del enunciado (problemas separados por columnas de espacios) y guardar **el bloque de texto** de cada problema, con su alineación intacta, por si la parte 2 la necesita (y la necesitó).

#### Separar los problemas por columnas vacías

1. **Rellenar las líneas.** En el input las líneas no tienen todas el mismo largo (3749 y 3752 caracteres) porque los espacios del final se recortan. `Grilla.desdeLineas(lineas, ' ')` arma una grilla rectangular completando con espacios a la derecha hasta el ancho máximo; sin esto, revisar una columna cerca del final se saldría de las líneas cortas.
2. **Recorrer las columnas.** Una columna con solo espacios es un separador. Cada tramo de columnas no vacías es un problema: se recortan esas columnas de cada fila (record `Problema`) y la operación se toma de la última fila.

```java
// Dia06.java — leerProblemas()
int inicio = -1;                        // primera columna del problema actual
for (int columna = 0; columna <= ancho; columna++) {
    boolean separador = columna == ancho || hoja.columna(columna).isBlank();
    if (!separador && inicio < 0) {
        inicio = columna;
    } else if (separador && inicio >= 0) {
        problemas.add(recortar(hoja, inicio, columna));
        inicio = -1;
    }
}
```

Tratar `columna == ancho` como separador cierra el último problema sin un caso especial.

#### Resolver

Cada fila del bloque, sin espacios, es un número. La operación se aplica con `Math.addExact` y `Math.multiplyExact`: si algún resultado no cupiera en un `long` se lanzaría una excepción en vez de dar un número incorrecto en silencio. En el input real el resultado más grande es ~6,4·10¹¹, muy lejos del límite.

#### Verificación

- Prueba con el ejemplo (4277556) y con el resultado de cada uno de los 4 problemas.
- Prueba con las líneas del ejemplo recortadas a distinto largo, como en el input real.
- Prueba de `aplicar`: suma, producto, operación desconocida y desborde.
- Con el input real, un script independiente en Python obtiene el mismo resultado.

**Resultado con el input real: 6171290547579**

---

## Parte 2

### Enunciado

Los cefalópodos grandes vuelven a ver cómo vamos y notan que nuestro gran total no coincide con el que espera la hoja: se olvidaron de explicar cómo se **lee** la matemática de los cefalópodos.

Se escribe **de derecha a izquierda y en columnas**: cada número ocupa su propia columna, con el dígito más significativo arriba y el menos significativo abajo. Los problemas siguen separados por columnas de espacios, y el símbolo de abajo sigue siendo la operación.

#### Ejemplo

Con la misma hoja:

```
123 328  51 64
 45 64  387 23
  6 98  215 314
*   +   *   +
```

leyendo cada problema columna por columna, de derecha a izquierda, los problemas cambian por completo:

| Problema (de derecha a izquierda) | Cuenta | Resultado |
|-----------------------------------|--------|-----------|
| el de más a la derecha | `4 + 431 + 623` | 1058 |
| el segundo | `175 * 581 * 32` | 3253600 |
| el tercero | `8 + 248 + 369` | 625 |
| el de más a la izquierda | `356 * 24 * 1` | 8544 |

Ahora el gran total es `1058 + 3253600 + 625 + 8544 =` **3263827**.

#### Pregunta

Resolviendo otra vez los problemas de la hoja, leídos a la manera de los cefalópodos, ¿cuál es el gran total?

### Solución

#### Por qué la parte 1 guardaba el bloque de texto

Aquí la alineación sí importa: en `64 / 23 / 314` el `4` final de `314` queda solo en su columna y forma el número `4`. Si la parte 1 hubiera guardado solo los números (`64`, `23`, `314`), esta información se habría perdido. Como el record `Problema` guarda el bloque tal cual, la parte 2 es solo **otra forma de leerlo**.

#### Leer por columnas

Para cada columna del bloque, de derecha a izquierda, se juntan sus caracteres de arriba hacia abajo saltando los espacios, y el resultado es un número:

```java
// Dia06.java — Problema.numerosPorColumna()
for (int columna = ancho - 1; columna >= 0; columna--) {
    StringBuilder digitos = new StringBuilder();
    for (String fila : filas) {
        char caracter = fila.charAt(columna);
        if (caracter != ' ') digitos.append(caracter);
    }
    if (!digitos.isEmpty()) numeros.add(Long.parseLong(digitos.toString()));
}
```

El orden de derecha a izquierda se respeta para coincidir con el enunciado, aunque no cambia el resultado: la suma y el producto son conmutativos.

#### Una sola función para las dos partes

Las dos partes solo difieren en cómo se leen los números, así que `granTotal` recibe esa lectura como función:

```java
// Dia06.java
public String parte1(List<String> lineas) {
    return String.valueOf(granTotal(lineas, Problema::numerosPorFila));
}

public String parte2(List<String> lineas) {
    return String.valueOf(granTotal(lineas, Problema::numerosPorColumna));
}
```

#### Verificación

- Prueba con el ejemplo (3263827) y con los números y el resultado de cada uno de los 4 problemas, en el mismo orden que el enunciado.
- Prueba con las líneas del ejemplo recortadas a distinto largo, como en el input real.
- Con el input real, un script independiente en Python que recorre **toda la hoja** de derecha a izquierda columna por columna (sin separar en problemas: va acumulando números y resuelve al encontrar un operador) obtiene el mismo resultado.

**Resultado con el input real: 8811937976367**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los resultados de todos los problemas | Lo mismo, leyendo cada número por columnas |
| **Enfoque** | Rellenar líneas, separar problemas por columnas de espacios, leer un número por fila | Mismo bloque de texto, leer un número por columna |
| **Complejidad** | O(filas · columnas) | O(filas · columnas) |
| **Resultado** | 6171290547579 | 8811937976367 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia06.java`
