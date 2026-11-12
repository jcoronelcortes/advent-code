# Año 2025 — Día 6: Compactador de Basura

## Descripción del problema

Por saltar con demasiado entusiasmo, caemos por el ducto de basura a un compactador cuya puerta está sellada magnéticamente. Una familia de cefalópodos promete abrirla, pero tardará un rato. Mientras tanto, nos piden ayuda con la tarea de matemáticas de la más pequeña.

El input es la **hoja de ejercicios**: varios problemas puestos uno **al lado del otro**. Cada problema tiene sus números en vertical (uno por fila) y, en la última fila, la operación a aplicar: suma (`+`) o multiplicación (`*`). Los problemas están separados por una **columna completa de espacios**.

```
123 328  51 64
 45 64  387 23
  6 98  215 314
*   +   *   +
```

El input real tiene 4 filas de números y una de operaciones, con 1000 problemas en una hoja de ~3750 columnas.

---

## Parte 1

### Pregunta

Resolver cada problema y sumar todos los resultados (el "gran total"). La alineación de los números dentro de cada problema (a la izquierda o a la derecha) no importa.

### Ejemplo

| Problema | Operación | Resultado |
|----------|-----------|-----------|
| 123, 45, 6 | `*` | 33210 |
| 328, 64, 98 | `+` | 490 |
| 51, 387, 215 | `*` | 4243455 |
| 64, 23, 314 | `+` | 401 |

Resultado del ejemplo: `33210 + 490 + 4243455 + 401 =` **4277556**

### Desarrollo de la solución

La dificultad está en el **parseo**: el cálculo es una suma o un producto.

#### Por qué no basta con separar por espacios cada línea

Dividir cada línea por espacios y emparejar los números por posición funcionaría en la parte 1, pero pierde la información de qué columnas ocupa cada problema. Se prefirió seguir la definición del enunciado (problemas separados por columnas de espacios) y guardar **el bloque de texto** de cada problema, con su alineación intacta, por si la parte 2 la necesita (y la necesitó).

#### Separar los problemas por columnas vacías

1. **Rellenar las líneas.** En el input las líneas no tienen todas el mismo largo (3749 y 3752 caracteres) porque los espacios del final se recortan. Se rellenan con espacios a la derecha hasta el ancho máximo; sin esto, revisar una columna cerca del final se saldría de las líneas cortas.
2. **Recorrer las columnas.** Una columna con solo espacios es un separador. Cada tramo de columnas no vacías es un problema: se recortan esas columnas de cada fila (record `Problema`) y la operación se toma de la última fila.

```java
// Dia06.java — leerProblemas()
int inicio = -1;                        // primera columna del problema actual
for (int columna = 0; columna <= ancho; columna++) {
    boolean separador = columna == ancho || esColumnaVacia(hoja, columna);
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

### Pregunta

Los cefalópodos adultos explican que su matemática se escribe **de derecha a izquierda y en columnas**: cada número ocupa su propia columna, con el dígito más significativo arriba. Los problemas siguen separados por columnas de espacios y la operación sigue abajo. ¿Cuál es el nuevo gran total?

### Ejemplo

Leyendo cada columna de arriba hacia abajo, de derecha a izquierda:

```
123 328  51 64
 45 64  387 23
  6 98  215 314
*   +   *   +
```

| Problema (de derecha a izquierda) | Columnas | Resultado |
|-----------------------------------|----------|-----------|
| `64 / 23 / 314`, `+` | `4`, `431`, `623` | 1058 |
| ` 51 / 387 / 215`, `*` | `175`, `581`, `32` | 3253600 |
| `328 / 64 / 98`, `+` | `8`, `248`, `369` | 625 |
| `123 / 45 / 6`, `*` | `356`, `24`, `1` | 8544 |

Resultado del ejemplo: `1058 + 3253600 + 625 + 8544 =` **3263827**

### Desarrollo de la solución

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
