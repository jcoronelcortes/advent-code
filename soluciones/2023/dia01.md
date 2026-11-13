# Año 2023 — Día 1: Calibración del Trebuchet

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/1](https://adventofcode.com/2023/day/1). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

La producción mundial de nieve tiene problemas y nos eligieron para revisarla. Los Elfos marcaron en un mapa los cincuenta lugares con más probabilidad de fallar, y hay que revisarlos todos antes del 25 de diciembre. Mientras preguntamos por qué no usan una máquina del clima y a dónde nos mandan ("al cielo"), nos damos cuenta de que nos están cargando en un **trebuchet**.

Al hacer los últimos ajustes descubren que su **documento de calibración** (el input) fue "mejorado" por una Elfa muy pequeña que quería lucir sus dotes artísticas, y ahora cuesta leer los valores.

Cada línea del documento tenía un **valor de calibración**: se obtiene juntando la **primera cifra** y la **última cifra** de la línea, en ese orden, para formar un número de dos cifras. Si la línea tiene una sola cifra, es a la vez la primera y la última.

#### Ejemplo

```
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
```

Los valores son 12, 38, 15 y 77 (la última línea solo tiene un 7), que suman **142**.

#### Pregunta

¿Cuánto suman los valores de calibración de todo el documento?

### Solución

#### El input real

1000 líneas de letras y cifras.

#### Buscar desde cada punta

La primera cifra se busca recorriendo la línea desde el comienzo, y la última, desde el final. En cada posición se mira si ahí empieza alguno de los textos de una tabla `texto → cifra`, que en esta parte son solo `"1"` a `"9"`:

```java
// Dia01.java — valor()
Integer primera = null;
for (int i = 0; i < linea.length() && primera == null; i++) primera = cifraEn(linea, i, cifras);
Integer ultima = null;
for (int i = linea.length() - 1; i >= 0 && ultima == null; i--) ultima = cifraEn(linea, i, cifras);
if (primera == null) throw new IllegalArgumentException("La línea no tiene cifras: '" + linea + "'");
return 10 * primera + ultima;
```

Mirar "qué texto empieza en esta posición" en vez de "qué carácter hay" deja la puerta abierta a cifras de más de un carácter, por si hicieran falta. Una línea sin ninguna cifra falla con un mensaje claro.

#### Verificación

- Prueba con el ejemplo (142) y con el valor de **cada línea** del ejemplo, incluida la que tiene una sola cifra.
- Una línea sin cifras falla.
- Con el input real, un script independiente en Python busca todas las cifras con una **expresión regular** (`\d`) y toma la primera y la última. Obtiene el mismo resultado.

**Resultado con el input real: 54667**

---

## Parte 2

### Enunciado

El cálculo no da bien: algunas cifras están **escritas con letras**. Las palabras `one`, `two`, `three`, `four`, `five`, `six`, `seven`, `eight` y `nine` también cuentan como cifras, y hay que volver a buscar la primera y la última de cada línea.

#### Ejemplo

| Línea | Valor |
|---|---|
| `two1nine` | 29 |
| `eightwothree` | 83 |
| `abcone2threexyz` | 13 |
| `xtwone3four` | 24 |
| `4nineeightseven2` | 42 |
| `zoneight234` | 14 |
| `7pqrstsixteen` | 76 |

Suman **281**.

#### Pregunta

Contando también las cifras escritas con letras, ¿cuánto suman los valores de calibración?

### Solución

#### Solo cambia la tabla

La búsqueda de la parte 1 ya mira qué **texto** empieza en cada posición, así que alcanza con agregar las nueve palabras a la tabla `texto → cifra`:

```java
// Dia01.java
public String parte2(List<String> lineas) {
    return String.valueOf(sumaDeValores(lineas, CIFRAS_Y_PALABRAS));
}
```

#### La trampa: palabras que comparten letras

En `eightwothree` la `t` es a la vez el final de `eight` y el comienzo de `two`, y en `oneight` la `e` es el final de `one` y el comienzo de `eight`. La forma tentadora de resolverlo, **reemplazar** las palabras por cifras en el texto, falla: al reemplazar `one` por `1` en `oneight` queda `1ight`, y se pierde el `8`.

Buscar desde cada punta no tiene ese problema: la primera cifra es la primera **posición** donde empieza alguna palabra o cifra, y la última, la última posición donde empieza alguna. En `oneight`, desde el comienzo aparece `one` en la posición 0, y desde el final, `eight` en la posición 2: el valor es 18. Las palabras nunca se "consumen".

#### Verificación

- Prueba con el ejemplo (281) y con el valor de **cada línea** del ejemplo.
- Palabras que comparten letras cuentan las dos: `oneight` vale 18, `twone` 21, `eighthree` 83 y `sevenine` 79.
- La tabla de la parte 1 no cuenta las palabras: `one1two` vale 11 en la parte 1 y 12 en la parte 2.
- Con el input real, un script independiente en Python busca con una **expresión regular con lookahead** (`(?=(one|two|…|1|2|…))`), que encuentra todas las coincidencias aunque se solapen, y toma la primera y la última. Obtiene el mismo resultado.

**Resultado con el input real: 54203**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de primera y última cifra de cada línea | Lo mismo, contando las cifras escritas con letras |
| **Enfoque** | Buscar la primera cifra desde cada punta | La misma búsqueda con las palabras en la tabla |
| **Complejidad** | O(largo total × cifras) | O(largo total × cifras) |
| **Resultado** | 54667 | 54203 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia01.java`
