# Año 2025 — Día 2: Tienda de Regalos

## Descripción del problema

Tomamos el ascensor hasta la tienda de regalos del Polo Norte. Un elfo jugando con la computadora de la tienda agregó un montón de **IDs de producto inválidos** a la base de datos, y nos piden encontrarlos.

El input es una única línea con **rangos de IDs** separados por comas. Cada rango tiene la forma `inicio-fin` (ambos inclusivos):

```
11-22,95-115,998-1012,1188511880-1188511890,...
```

Ningún ID tiene ceros a la izquierda (`0101` no es un ID; `101` sí lo es y es válido).

---

## Parte 1

### Pregunta

Un ID es **inválido** si está formado únicamente por una secuencia de dígitos **repetida dos veces**: `55` (5 dos veces), `6464` (64 dos veces), `123123` (123 dos veces).

¿Cuánto da la **suma** de todos los IDs inválidos que aparecen en los rangos?

### Ejemplo

| Rango | IDs inválidos | Suma |
|-------|---------------|------|
| 11-22 | 11, 22 | 33 |
| 95-115 | 99 | 99 |
| 998-1012 | 1010 | 1010 |
| 1188511880-1188511890 | 1188511885 | 1188511885 |
| 222220-222224 | 222222 | 222222 |
| 1698522-1698528 | — | 0 |
| 446443-446449 | 446446 | 446446 |
| 38593856-38593862 | 38593859 | 38593859 |
| 565653-565659 | — | 0 |
| 824824821-824824827 | — | 0 |
| 2121212118-2121212124 | — | 0 |

Resultado del ejemplo: **1227775554**

### Desarrollo de la solución

#### Paso 1 — Parsear los rangos

El input real viene en una sola línea, pero el ejemplo del enunciado aparece "envuelto" en varias líneas terminadas en coma. Para soportar ambos casos, unimos todas las líneas antes de separar por comas, y validamos cada parte con una expresión regular:

```java
// Dia02.java — leerRangos()
for (String parte : String.join("", lineas).split(",")) {
    parte = parte.trim();
    if (!parte.matches("\\d+-\\d+")) continue;

    String[] extremos = parte.split("-");
    rangos.add(new Rango(NumberUtils.toLong(extremos[0]), NumberUtils.toLong(extremos[1])));
}
```

> Los IDs llegan hasta ~10 dígitos (`9898998927`), que **no caben en un `int`**, por eso usamos `long` en todo el problema.

#### Paso 2 — La idea obvia: recorrer cada rango

La forma directa sería recorrer cada ID del rango y verificar si su primera mitad es igual a la segunda:

```java
// Dia02.java — esInvalido()
String texto = String.valueOf(id);
int largo = texto.length();
if (largo % 2 != 0) return false;

int mitad = largo / 2;
return texto.substring(0, mitad).equals(texto.substring(mitad));
```

Con este input funcionaría (los rangos tienen a lo sumo unos cientos de miles de IDs), pero hay una forma mucho más elegante que no depende del tamaño de los rangos. `esInvalido` se conserva como verificación en las pruebas.

#### Paso 3 — Solución matemática: generar los inválidos en vez de buscarlos

**Clave:** un ID inválido de `2k` dígitos es una mitad `x` de `k` dígitos escrita dos veces, y eso equivale a una multiplicación:

```
6464     = 64  · 100  + 64  = 64  · 101
123123   = 123 · 1000 + 123 = 123 · 1001
x x      = x · 10^k + x     = x · (10^k + 1)
```

Entonces, para cada cantidad de dígitos `k`, los IDs inválidos son exactamente los múltiplos de `10^k + 1` cuyo cociente `x` tiene `k` dígitos, es decir `10^(k-1) ≤ x ≤ 10^k − 1`.

Para un rango `[inicio, fin]`, los `x` válidos forman un intervalo continuo:

```
xMinimo = max(10^(k-1), ⌈inicio / (10^k + 1)⌉)
xMaximo = min(10^k − 1, ⌊fin    / (10^k + 1)⌋)
```

Y la suma de los IDs es `(10^k + 1) · (xMinimo + ... + xMaximo)`, que se calcula con la fórmula de la **serie aritmética**.

En el código esto está en una función general, `sumarConBloque(inicio, fin, largo, bloque)`, que también se usa en la Parte 2. Para la Parte 1 se la llama con cada largo par `L = 2k` y bloque `k`:

```java
// Dia02.java — sumarInvalidosEnRango()
for (int largo = cantidadDigitos(inicio); largo <= cantidadDigitos(fin); largo++) {
    if (largo % 2 == 0) {
        suma += sumarConBloque(inicio, fin, largo, largo / 2);
    }
}
```

```java
// Dia02.java — sumarConBloque()
long potenciaBloque = potenciaDeDiez(bloque);
long multiplicador = (potenciaDeDiez(largo) - 1) / (potenciaBloque - 1);   // 10^k + 1 cuando largo = 2k

long xMinimo = Math.max(potenciaBloque / 10, dividirHaciaArriba(inicio, multiplicador));
long xMaximo = Math.min(potenciaBloque - 1, fin / multiplicador);

if (xMinimo > xMaximo) return 0;

long cantidad = xMaximo - xMinimo + 1;
long sumaX = (xMinimo + xMaximo) * cantidad / 2;
return Math.multiplyExact(sumaX, multiplicador);
```

> Solo se prueban los largos entre los dígitos de `inicio` y los de `fin`, así cada rango hace muy pocas iteraciones.

**Verificación con `11-22`:**
- `k = 1` → multiplicador `11`, `x ∈ [max(1, ⌈11/11⌉), min(9, ⌊22/11⌋)] = [1, 2]`
- Suma: `11 · (1 + 2) = 33` ✓ (los IDs `11` y `22`)
- `k ≥ 2` → `xMinimo ≥ 10 > xMaximo = 0`, no aporta nada.

**Verificación con `998-1012`:**
- `k = 1` → multiplicador `11`, `x ∈ [max(1, 91), min(9, 92)]` → vacío.
- `k = 2` → multiplicador `101`, `x ∈ [max(10, ⌈998/101⌉ = 10), min(99, ⌊1012/101⌋ = 10)] = [10, 10]`
- Suma: `101 · 10 = 1010` ✓

#### Paso 4 — Sumar todos los rangos

```java
// Dia02.java — parte1()
for (Rango rango : leerRangos(lineas)) {
    suma += sumarInvalidosEnRango(rango.inicio(), rango.fin());
}
```

**Resultado con el input real: 38310256125**

> El resultado se verificó además con una fuerza bruta independiente que recorre todos los IDs de todos los rangos.

---

## Parte 2

### Pregunta

El elfo hizo más patrones. Ahora un ID es inválido si está formado por una secuencia de dígitos repetida **al menos** dos veces: `12341234` (1234 dos veces), `123123123` (123 tres veces), `1212121212` (12 cinco veces), `1111111` (1 siete veces).

El input es el mismo de la Parte 1.

### Ejemplo

| Rango | IDs inválidos | Cambio respecto a la Parte 1 |
|-------|---------------|------------------------------|
| 11-22 | 11, 22 | igual |
| 95-115 | 99, **111** | `111` = "1" × 3 |
| 998-1012 | **999**, 1010 | `999` = "9" × 3 |
| 1188511880-1188511890 | 1188511885 | igual |
| 222220-222224 | 222222 | igual |
| 1698522-1698528 | — | igual |
| 446443-446449 | 446446 | igual |
| 38593856-38593862 | 38593859 | igual |
| 565653-565659 | **565656** | "56" × 3 |
| 824824821-824824827 | **824824824** | "824" × 3 |
| 2121212118-2121212124 | **2121212121** | "21" × 5 |

Resultado del ejemplo: **4174379265**

### Desarrollo de la solución

#### Paso 1 — Generalizar la fórmula

Un número de `L` dígitos formado por un bloque `x` de `p` dígitos repetido `L/p` veces sigue siendo `x` por un multiplicador fijo:

```
565656     = 56  · 10101          (bloque 2, repetido 3 veces)
824824824  = 824 · 1001001        (bloque 3, repetido 3 veces)
1111111    = 1   · 1111111        (bloque 1, repetido 7 veces)

multiplicador = 1 + 10^p + 10^(2p) + ... = (10^L − 1) / (10^p − 1)
```

Así que `sumarConBloque(inicio, fin, L, p)` (la misma función de la Parte 1) suma todos los números de `L` dígitos con bloque de `p` dígitos dentro del rango. Llamemos a esa suma `S(L, p)`.

#### Paso 2 — El problema: contar dos veces

La idea ingenua sería sumar `S(L, p)` para cada divisor `p` de `L`. Pero un mismo número puede escribirse con varios bloques:

```
111111 = "1" × 6 = "11" × 3 = "111" × 2
```

Con la suma ingenua, `111111` se contaría **tres veces**. Hay que usar **inclusión-exclusión**.

#### Paso 3 — Inclusión-exclusión con la función de Möbius

Ordenamos por **cantidad de repeticiones** `d = L/p`. Si un número se repite `d` veces, también se repite `d'` veces para cualquier divisor `d'` de `d` (ej. "1"×6 también es "11"×3 y "111"×2). Por eso alcanza con mirar las repeticiones por un **número primo** (`d = 2, 3, 5, 7...`), y corregir los solapamientos:

- se **suman** los casos `d` primo (2, 3, 5, 7, …),
- se **restan** los casos `d` producto de dos primos distintos (6, 10, 15, …), que quedaron contados dos veces,
- se **suman** los de tres primos (30, …), y así sucesivamente,
- los `d` con un primo repetido (4, 8, 9, …) no aportan: ya están cubiertos por sus primos.

Ese signo es exactamente `−μ(d)`, donde `μ` es la **función de Möbius**:

```
suma(L) = Σ  −μ(d) · S(L, L/d)      para cada d > 1 que divide a L
```

| d | μ(d) | Aporte |
|---|------|--------|
| 2, 3, 5, 7 | −1 | se suma |
| 4, 8, 9 | 0 | no aporta |
| 6, 10 | +1 | se resta |

```java
// Dia02.java — sumarRepetidosEnRango()
for (int largo = cantidadDigitos(inicio); largo <= cantidadDigitos(fin); largo++) {
    for (int repeticiones = 2; repeticiones <= largo; repeticiones++) {
        if (largo % repeticiones != 0) continue;

        int mobius = mobius(repeticiones);
        if (mobius == 0) continue;

        suma -= mobius * sumarConBloque(inicio, fin, largo, largo / repeticiones);
    }
}
```

**Verificación con `111111` (L = 6):** los divisores `d > 1` de 6 son 2, 3 y 6.
- `d = 2` (μ = −1) → bloque 3: `111·1001` → **+111111**
- `d = 3` (μ = −1) → bloque 2: `11·10101` → **+111111**
- `d = 6` (μ = +1) → bloque 1: `1·111111` → **−111111**
- Total: `111111` contado **una sola vez** ✓

**Verificación con `95-115`:**
- `L = 2`: `d = 2` → `99` (`9·11`) → +99
- `L = 3`: `d = 3` → `111` (`1·111`) → +111
- Total: `210` ✓

#### Paso 4 — Comprobación por fuerza bruta

Para las pruebas se escribió además `esRepetido`, que usa un truco clásico: un texto `s` es la repetición de un bloque si y solo si aparece dentro de `s + s` en una posición que no sea la 0:

```java
// Dia02.java — esRepetido()
String texto = String.valueOf(id);
return (texto + texto).indexOf(texto, 1) < texto.length();
```

Las pruebas comparan la fórmula contra esta verificación para todos los IDs entre 1 y 2.000.000.

**Resultado con el input real: 58961152806**

> El resultado se verificó además con una fuerza bruta independiente en Python sobre todos los IDs de todos los rangos.

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de IDs formados por una secuencia repetida dos veces | Suma de IDs formados por una secuencia repetida al menos dos veces |
| **Enfoque** | Generación matemática: `ID = x · (10^k + 1)` + serie aritmética | Misma fórmula generalizada + inclusión-exclusión (Möbius) |
| **Complejidad** | O(r · d), con `r` rangos y `d` dígitos (independiente del tamaño de cada rango) | O(r · d²) |
| **Resultado** | 38310256125 | 58961152806 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia02.java`
