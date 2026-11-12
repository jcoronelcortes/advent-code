# Año 2024 — Día 3: Dale Vueltas

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/3](https://adventofcode.com/2024/day/3). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

"Nuestras computadoras andan mal, así que no tengo idea de si nos queda algún Historiador Jefe en existencia. Pueden revisar el depósito, si quieren", dice el algo nervioso encargado del local de alquiler de trineos del Polo Norte. Los Historiadores van a mirar, y el encargado nos pregunta si podemos ver qué les pasa otra vez a las computadoras.

La computadora intenta correr un programa, pero su **memoria** (el input) está **corrupta**: todas las instrucciones están revueltas. Al parecer, el programa solo quiere multiplicar números, con instrucciones como `mul(X,Y)`, donde X e Y son números de **1 a 3 dígitos**. Por ejemplo, `mul(44,46)` multiplica 44 por 46 y da 2024, y `mul(123,4)` multiplica 123 por 4.

Como la memoria está corrupta, también hay muchos caracteres inválidos que hay que ignorar, aunque se parezcan a una instrucción `mul`. Secuencias como `mul(4*`, `mul(6,9!`, `?(12,34)` o `mul ( 2 , 4 )` no hacen nada.

#### Ejemplo

En este fragmento de memoria corrupta:

```
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
```

solo hay cuatro instrucciones `mul` reales: `mul(2,4)`, `mul(5,5)`, `mul(11,8)` y `mul(8,5)`. La suma de sus resultados es `2·4 + 5·5 + 11·8 + 8·5 =` **161**.

#### Pregunta

Buscando las instrucciones `mul` sin corromper en la memoria, ¿cuánto suman los resultados de todas las multiplicaciones?

### Solución

#### El input real

La memoria tiene ~20 000 caracteres repartidos en 6 líneas, con 792 apariciones de `mul(` (válidas o no).

#### Una expresión regular exacta

Una instrucción vale solo si está escrita **exactamente** así: `mul`, paréntesis, un número de 1 a 3 dígitos, coma, otro número de 1 a 3 dígitos y paréntesis de cierre, sin espacios. Eso es una expresión regular:

```java
// Dia03.java
private static final Pattern MULTIPLICACION = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
```

Cualquier variación (`mul[3,7]`, `mul(32,64]`, `mul ( 2 , 4 )`, un número de 4 dígitos) simplemente no coincide. Después basta con recorrer todas las coincidencias y sumar los productos. `Matcher.results()` (de la biblioteca estándar) las entrega como un stream, sin bucles:

```java
// Dia03.java — sumarMultiplicaciones()
return MULTIPLICACION.matcher(memoria).results()
        .mapToLong(Dia03::producto)
        .sum();
```

donde `producto` multiplica los grupos 1 y 2 de la coincidencia. Cada coincidencia se busca a partir del final de la anterior, así que también encuentra instrucciones pegadas entre sí (`mul(2,3)mul(4,5)`) o justo después de basura (`mulmul(2,3)`).

#### Una sola memoria, no líneas sueltas

El input ocupa varias líneas, pero es una sola memoria. Se unen las líneas conservando el salto de línea, que es un carácter más: una instrucción cortada por él (`mul(1` en una línea y `2,3)` en la siguiente) ya no está escrita exactamente como `mul(X,Y)` y no cuenta. Tratar la memoria como un solo texto también deja listo el terreno por si la parte 2 necesita llevar algún estado de una línea a la siguiente (y lo necesitó).

#### Verificación

- Prueba con el ejemplo (161).
- Las secuencias inválidas que menciona el enunciado (y las del ejemplo) no cuentan.
- Números de 1 a 3 dígitos sí; con 4 dígitos la instrucción no vale.
- Instrucciones pegadas, después de basura y repartidas en varias líneas; una instrucción cortada por un salto de línea no cuenta.
- Con el input real, un script independiente en Python **sin expresiones regulares**, que recorre la memoria carácter por carácter como un pequeño analizador, obtiene el mismo resultado.

**Resultado con el input real: 178538786**

---

## Parte 2

### Enunciado

Al revisar la memoria notamos que algunas instrucciones **condicionales** también siguieron intactas. Teniéndolas en cuenta, el resultado sería todavía más preciso. Hay dos instrucciones nuevas:

- `do()` **habilita** las instrucciones `mul` que vienen después;
- `don't()` las **deshabilita**.

Solo vale la `do()` o `don't()` más reciente. Al comienzo del programa, las instrucciones `mul` están habilitadas.

#### Ejemplo

```
xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
```

Es parecido al ejemplo anterior, pero `mul(5,5)` y `mul(11,8)` quedan deshabilitadas porque antes aparece un `don't()`. Las demás funcionan normalmente, incluida la del final, que vuelve a estar habilitada gracias al `do()` (dentro de `undo()`). La suma es `2·4 + 8·5 =` **48**.

#### Pregunta

Teniendo en cuenta las instrucciones nuevas, ¿cuánto suman los resultados de las multiplicaciones habilitadas?

### Solución

#### Recorrer las tres instrucciones en orden

Ahora importa el **orden** de las instrucciones. Una sola expresión regular reconoce las tres, y como `results()` recorre la memoria de izquierda a derecha, las coincidencias salen en el orden en que aparecen:

```java
// Dia03.java
private static final Pattern INSTRUCCION = Pattern.compile(
        "mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
```

Basta con llevar un estado, `habilitado`, que empieza en `true`:

```java
// Dia03.java — sumarMultiplicacionesHabilitadas()
for (MatchResult instruccion : INSTRUCCION.matcher(memoria).results().toList()) {
    switch (instruccion.group()) {
        case "do()" -> habilitado = true;
        case "don't()" -> habilitado = false;
        default -> {
            if (habilitado) total += producto(instruccion);
        }
    }
}
```

Dos detalles:

- `don't()` empieza con "do", pero la alternativa `do\(\)` exige el paréntesis justo después, así que nunca se confunde con un `do()`.
- `undo()` **contiene** un `do()` válido, y cuenta como tal: así lo indica el ejemplo del enunciado, donde rehabilita la última multiplicación.

#### El estado pasa de una línea a otra

Aquí se ve por qué la parte 1 trataba la memoria como un solo texto: un `don't()` al final de una línea sigue deshabilitando las multiplicaciones de la línea siguiente. Si se procesara cada línea por separado, el estado se reiniciaría en cada una y el resultado sería incorrecto.

#### Verificación

- Prueba con el ejemplo (48).
- Solo vale la última instrucción condicional; al comienzo está habilitado; `undo()` cuenta como `do()`; variantes mal escritas (`do( )`, `dont()`) no cambian nada.
- El estado sigue de una línea a la siguiente.
- Sin instrucciones condicionales, el resultado coincide con la parte 1.
- Con el input real, un script independiente en Python usa otro método: **corta el texto** en tramos, descarta todo lo que está entre un `don't()` y el siguiente `do()`, y aplica el analizador manual de la parte 1 al resto (separando los tramos para no unir por accidente los bordes de dos de ellos en un `mul(...)` falso). Obtiene el mismo resultado.

**Resultado con el input real: 102467299**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de las multiplicaciones válidas | Lo mismo, solo las habilitadas por `do()` / `don't()` |
| **Enfoque** | Expresión regular exacta `mul\((\d{1,3}),(\d{1,3})\)` | Una expresión para las tres instrucciones, recorridas en orden con un estado |
| **Complejidad** | O(largo de la memoria) | O(largo de la memoria) |
| **Resultado** | 178538786 | 102467299 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia03.java`
