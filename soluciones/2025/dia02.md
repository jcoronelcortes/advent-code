# Año 2025 — Día 2: Tienda de Regalos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/2](https://adventofcode.com/2025/day/2). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Entramos y tomamos el ascensor hasta su única otra parada: la **tienda de regalos** del Polo Norte. Desde ahí se llega al vestíbulo y al resto de la base. Uno de los vendedores nos reconoce y pide ayuda: un elfo pequeño estuvo jugando con una computadora de la tienda y agregó a la base de datos un montón de **IDs de producto inválidos**.

Los vendedores ya revisaron casi todo; solo faltan algunos **rangos de IDs**, que son el input. Vienen en una sola línea larga, separados por comas, y cada rango indica su primer y su último ID separados por un guion (ambos incluidos):

```
11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
1698522-1698528,446443-446449,38593856-38593862,565653-565659,
824824821-824824827,2121212118-2121212124
```

(Aquí el ejemplo está partido en varias líneas para que se lea mejor.)

Como el elfo solo hacía patrones tontos, un ID es **inválido** si está formado únicamente por una secuencia de dígitos **repetida dos veces**: `55` (el 5 dos veces), `6464` (el 64 dos veces) o `123123` (el 123 dos veces). Ningún ID empieza con cero: `0101` no es un ID, y `101` es un ID válido.

#### Ejemplo

| Rango | IDs inválidos |
|-------|---------------|
| `11-22` | 11 y 22 |
| `95-115` | 99 |
| `998-1012` | 1010 |
| `1188511880-1188511890` | 1188511885 |
| `222220-222224` | 222222 |
| `1698522-1698528` | ninguno |
| `446443-446449` | 446446 |
| `38593856-38593862` | 38593859 |
| los otros tres | ninguno |

La suma de todos los IDs inválidos del ejemplo es **1227775554**.

#### Pregunta

¿Cuánto suman todos los IDs inválidos que aparecen en los rangos del input?

### Solución

#### Paso 1 — Parsear los rangos

El input real viene en una sola línea, pero el ejemplo del enunciado aparece "envuelto" en varias líneas terminadas en coma. Para soportar ambos casos, unimos todas las líneas antes de separar por comas, y convertimos cada parte con la utilidad compartida `Rango.parsear` (`src/main/java/advent/code/Rango.java`):

```java
// Dia02.java — leerRangos()
for (String parte : String.join("", lineas).split(",")) {
    if (parte.isBlank()) continue;
    rangos.add(Rango.parsear(parte));
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

return Math.multiplyExact(sumaAritmetica(xMinimo, xMaximo), multiplicador);
```

> `potenciaDeDiez`, `dividirHaciaArriba`, `sumaAritmetica` y `cantidadDigitos` vienen de la clase compartida `Matematicas` (`src/main/java/advent/code/Matematicas.java`).

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

### Enunciado

El vendedor descubre enseguida que todavía quedan IDs inválidos en los rangos: parece que el elfo también hacía otros patrones.

Ahora un ID es inválido si está formado por una secuencia de dígitos repetida **al menos dos veces**: `12341234` (el 1234 dos veces), `123123123` (el 123 tres veces), `1212121212` (el 12 cinco veces) o `1111111` (el 1 siete veces). Los rangos son los mismos de la parte 1.

#### Ejemplo

| Rango | IDs inválidos | Cambio respecto de la parte 1 |
|-------|---------------|-------------------------------|
| `11-22` | 11 y 22 | igual |
| `95-115` | 99 y **111** | 111 = "1" tres veces |
| `998-1012` | **999** y 1010 | 999 = "9" tres veces |
| `1188511880-1188511890` | 1188511885 | igual |
| `222220-222224` | 222222 | igual |
| `1698522-1698528` | ninguno | igual |
| `446443-446449` | 446446 | igual |
| `38593856-38593862` | 38593859 | igual |
| `565653-565659` | **565656** | "56" tres veces |
| `824824821-824824827` | **824824824** | "824" tres veces |
| `2121212118-2121212124` | **2121212121** | "21" cinco veces |

Con la regla nueva, la suma del ejemplo es **4174379265**.

#### Pregunta

Con la regla nueva, ¿cuánto suman todos los IDs inválidos de los rangos del input?

### Solución

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

        int signo = mobius(repeticiones);    // Matematicas.mobius
        if (signo == 0) continue;

        suma -= signo * sumarConBloque(inicio, fin, largo, largo / repeticiones);
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
