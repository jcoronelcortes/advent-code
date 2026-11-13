# Año 2024 — Día 13: Artilugio de Garra

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/13](https://adventofcode.com/2024/day/13). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos al vestíbulo de un hotel en una isla tropical, que tiene una sala de juegos nueva con **máquinas de garra**. Pero son raras: en vez de una palanca, tienen dos botones, **A** y **B**. Pulsar A cuesta **3 fichas** y pulsar B cuesta **1 ficha**.

Cada botón mueve la garra una cantidad fija hacia la derecha (eje X) y hacia adelante (eje Y). Cada máquina tiene un premio, y para ganarlo la garra tiene que quedar **exactamente** sobre él en los dos ejes.

El input lista, para cada máquina, cuánto mueve cada botón y dónde está el premio:

```
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400
```

Calculamos que nunca hace falta pulsar un botón **más de 100 veces** para ganar un premio.

#### Ejemplo

Con cuatro máquinas:

```
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
```

- En la primera, lo más barato es pulsar A 80 veces y B 40 veces: 80·94 + 40·22 = 8400 y 80·34 + 40·67 = 5400. Cuesta 80·3 + 40 = **280** fichas.
- En la tercera, A 38 veces y B 86 veces: **200** fichas.
- La segunda y la cuarta **no se pueden ganar** con ninguna combinación.

Se ganan como mucho dos premios, por **480** fichas en total.

#### Pregunta

Para ganar todos los premios que se puedan, ¿cuál es la menor cantidad de fichas que hay que gastar?

### Solución

#### El input real

Son 320 máquinas. Los botones mueven entre 11 y 99 unidades por eje, y los premios están a menos de 20 000.

#### No hay nada que minimizar

Con `a` pulsaciones de A y `b` de B, la garra llega al premio si:

```
a·ax + b·bx = px
a·ay + b·by = py
```

Es un sistema de **2 ecuaciones lineales con 2 incógnitas**. Si los dos botones no se mueven en la misma dirección (si no son "paralelos"), el sistema tiene **una única solución**. Entonces no hay varias formas de ganar entre las que elegir la más barata: hay **una o ninguna**. La pregunta por "la menor cantidad de fichas" es una distracción.

En el input real ninguna máquina tiene botones paralelos. Si apareciera una, el código falla con un mensaje claro en vez de dar una respuesta equivocada.

#### Regla de Cramer

La solución única sale con la **regla de Cramer**: cada incógnita es un cociente de determinantes. Sirve solo si los dos cocientes son **exactos** (no se puede pulsar un botón media vez), **no negativos** y no pasan del límite de 100:

```java
// Dia13.java — Maquina.costoMinimo()
long determinante = Math.subtractExact(Math.multiplyExact(ax, by), Math.multiplyExact(ay, bx));
if (determinante == 0) {
    throw new IllegalStateException("Botones paralelos: el sistema no tiene solución única " + this);
}
long numeradorA = Math.subtractExact(Math.multiplyExact(px, by), Math.multiplyExact(py, bx));
long numeradorB = Math.subtractExact(Math.multiplyExact(ax, py), Math.multiplyExact(ay, px));
if (numeradorA % determinante != 0 || numeradorB % determinante != 0) return OptionalLong.empty();
long a = numeradorA / determinante;
long b = numeradorB / determinante;
if (a < 0 || b < 0 || a > pulsacionesMaximas || b > pulsacionesMaximas) return OptionalLong.empty();
return OptionalLong.of(Math.addExact(Math.multiplyExact(a, COSTO_A), Math.multiplyExact(b, COSTO_B)));
```

Todo es aritmética entera exacta (con `Math.*Exact` para detectar desborde). Así no hay errores de redondeo al decidir si una solución es entera, como pasaría con `double`.

Las máquinas se leen con `Utiles.secciones`, que separa los bloques por las líneas en blanco, y `Utiles.leerNumerosLargos`, que saca los 6 números de cada bloque.

#### Verificación

- Prueba con el ejemplo (480) y con el costo de cada una de las cuatro máquinas: 280, ninguna, 200, ninguna.
- Una solución con más de 100 pulsaciones de un botón, o con pulsaciones negativas, se descarta. Una máquina con botones paralelos falla con un error.
- Con el input real, un script independiente en Python hace la **fuerza bruta** que sugiere el enunciado: prueba las 101 × 101 combinaciones de pulsaciones de cada máquina y se queda con la más barata. Obtiene el mismo resultado, ganando 157 de los 320 premios.

**Resultado con el input real: 30973**

---

## Parte 2

### Enunciado

Al ir a buscar el primer premio, la garra no está ni cerca de donde esperábamos: por un error de conversión de unidades, cada premio está en realidad **10 000 000 000 000** (10¹³) más lejos, tanto en X como en Y.

Hay que sumar 10¹³ a las dos coordenadas de cada premio. Por ejemplo, el primero del ejemplo pasa a estar en `X=10000000008400, Y=10000000005400`. Ahora hacen falta muchas más de 100 pulsaciones, así que el límite ya no aplica.

#### Ejemplo

Con los premios corregidos, en el ejemplo **solo se pueden ganar la segunda y la cuarta máquina**, justo las que antes no se podían. El enunciado no da el total de fichas.

#### Pregunta

Con las coordenadas corregidas, ¿cuál es la menor cantidad de fichas para ganar todos los premios posibles?

### Solución

#### La misma cuenta, con números más grandes

La parte 2 es la trampa para quien hizo fuerza bruta en la parte 1: con premios a 10¹³, probar combinaciones es imposible. Pero la regla de Cramer no depende del tamaño de los números: sigue siendo una sola cuenta por máquina. Basta correr cada premio y quitar el límite de pulsaciones:

```java
// Dia13.java
public String parte2(List<String> lineas) {
    List<Maquina> corregidas = leerMaquinas(lineas).stream()
            .map(maquina -> maquina.conPremioCorrido(CORRECCION_PARTE2))
            .toList();
    return String.valueOf(fichasTotales(corregidas, Long.MAX_VALUE));
}
```

Cambia qué máquinas se pueden ganar: una solución entera para un premio no tiene por qué seguir siéndolo con el premio corrido, y al revés.

#### ¿Entra en un `long`?

Los numeradores de Cramer multiplican una coordenada del premio (~10¹³) por un movimiento de botón (< 100): ~10¹⁵, muy por debajo del máximo de un `long` (~9,2 · 10¹⁸). Igual, todas las operaciones usan `Math.*Exact`, que lanza un error en vez de desbordar en silencio.

#### Verificación

- En el ejemplo, con los premios corridos, solo se ganan la 2.ª y la 4.ª máquina, como dice el enunciado.
- El total del ejemplo, 875 318 608 908 (459 236 326 669 + 416 082 282 239). El enunciado no lo da: lo calcula el script independiente de abajo.
- Correr el premio suma lo mismo a X y a Y y no toca los botones.
- Con el input real, un script independiente en Python resuelve cada sistema con otro método: **eliminación gaussiana con fracciones exactas** (`fractions.Fraction`). Además comprueba cada solución sustituyéndola en las dos ecuaciones. Obtiene el mismo resultado en las dos partes, ganando 163 de los 320 premios en la parte 2.

**Resultado con el input real: 95688837203288**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Fichas mínimas para ganar todos los premios posibles (≤ 100 pulsaciones) | Lo mismo, con los premios corridos 10¹³ y sin límite |
| **Enfoque** | Sistema 2 × 2 con solución única: regla de Cramer en enteros | La misma regla de Cramer: no depende del tamaño de los números |
| **Complejidad** | O(1) por máquina | O(1) por máquina |
| **Resultado** | 30973 | 95688837203288 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia13.java`
