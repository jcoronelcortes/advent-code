# Año 2024 — Día 7: Reparación del Puente

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/7](https://adventofcode.com/2024/day/7). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los Historiadores nos llevan a un puente colgante conocido, sobre un río en medio de una selva. El Jefe no está de este lado; quizás esté del otro. Pero el puente está en reparación (al parecer se rompe seguido) y no se puede cruzar hasta que lo arreglen.

Los ingenieros dicen que solo faltan las calibraciones finales, pero unos elefantes jóvenes que jugaban cerca **se robaron todos los operadores** de sus ecuaciones de calibración. Podrían terminar si alguien determina qué **valores de prueba** se pueden obtener poniendo operadores en esas ecuaciones (el input):

```
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
```

Cada línea es una ecuación: el valor de prueba está antes de los dos puntos, y hay que decidir si los números restantes se pueden combinar con operadores para obtenerlo.

- Los operadores se evalúan siempre **de izquierda a derecha**, sin reglas de precedencia.
- Los números **no se pueden reordenar**.
- Los elefantes tienen dos tipos de operadores: **suma** (`+`) y **multiplicación** (`*`).

#### Ejemplo

Solo tres de las ecuaciones se pueden cumplir:

| Ecuación | Cómo |
|----------|------|
| `190: 10 19` | `10 * 19` (con `+` daría 29) |
| `3267: 81 40 27` | `81 + 40 * 27` y también `81 * 40 + 27` (de izquierda a derecha) |
| `292: 11 6 16 20` | `11 + 6 * 16 + 20`, de una sola forma |

El **resultado total de calibración** es la suma de los valores de prueba de las ecuaciones que se pueden cumplir: `190 + 3267 + 292 =` **3749**.

#### Pregunta

¿Cuál es el resultado total de calibración de las ecuaciones que se pueden cumplir?

### Solución

#### El input real

Hay 850 ecuaciones de 3 a 12 números. Los valores de prueba llegan a ~1,2·10¹⁴, así que todo es `long`. No hay ceros, algo que la búsqueda necesita (ver más abajo).

#### Probar hacia adelante sería caro

Con 12 números hay 11 huecos y `2¹¹ = 2048` combinaciones de operadores por ecuación. Para la parte 1 alcanzaría, pero si hubiera más operadores crecería muy rápido (con 3 operadores serían `3¹¹ ≈ 177 000`).

#### Clave: buscar de atrás hacia adelante

Como se evalúa de izquierda a derecha, el **último número es el último operando**: `valor = (todo lo anterior) op último`. Entonces, para cada operador, se puede calcular qué tenía que valer "todo lo anterior" y seguir con un número menos:

- con `+`, lo anterior tenía que valer `valor − último`, y solo es posible si da **positivo** (todos los números lo son);
- con `*`, lo anterior tenía que valer `valor / último`, y solo es posible si la división es **exacta**.

Casi todas las ramas se cortan enseguida, porque la mayoría de las divisiones no son exactas. Al llegar al primer número, la ecuación es posible si coincide con lo que quedó.

Cada operador se modela como una constante de un `enum` que sabe **deshacerse**:

```java
// Dia07.java — Operador
SUMA {
    @Override
    OptionalLong deshacer(long resultado, long operando) {
        // Los números son positivos: lo anterior también tiene que serlo
        return resultado > operando ? OptionalLong.of(resultado - operando) : OptionalLong.empty();
    }
},
PRODUCTO {
    @Override
    OptionalLong deshacer(long resultado, long operando) {
        return resultado % operando == 0 ? OptionalLong.of(resultado / operando) : OptionalLong.empty();
    }
};
```

```java
// Dia07.java — Ecuacion.sePuedeObtener()
if (ultimo == 0) return objetivo == numeros.getFirst();
long operando = numeros.get(ultimo);
for (Operador operador : operadores) {
    OptionalLong anterior = operador.deshacer(objetivo, operando);
    if (anterior.isPresent() && sePuedeObtener(anterior.getAsLong(), ultimo - 1, operadores)) {
        return true;
    }
}
return false;
```

La ausencia de ceros importa para el producto: con un 0 como operando, `valor / 0` no está definido, y `0 * x = 0` para cualquier `x`, así que deshacerlo no daría un único valor anterior.

Los operadores disponibles se pasan como un conjunto (`EnumSet`), por si la parte 2 agrega alguno (y lo agregó).

#### Verificación

- Prueba con el ejemplo (3749) y con cuáles de sus ecuaciones son posibles.
- Evaluación de izquierda a derecha: `2 3 4` puede dar 20 (`(2 + 3) * 4`) pero no 14; con un solo número, solo vale si es igual al valor.
- Deshacer cada operador, incluidos los casos que no sirven (resta que da 0, división no exacta).
- Prueba basada en propiedades (jqwik, 500 casos): la búsqueda hacia atrás coincide con probar **todas** las combinaciones hacia adelante.
- Con el input real, un script independiente en Python que prueba todas las combinaciones de operadores hacia adelante (`itertools.product`) obtiene el mismo resultado.

**Resultado con el input real: 3351424677624**

---

## Parte 2

### Enunciado

Los ingenieros se preocupan: el resultado total de calibración no está ni cerca de las tolerancias de seguridad. Justo entonces descubrimos el error: unos elefantes bien escondidos tienen un **tercer tipo de operador**.

El operador de **concatenación** (`||`) junta los dígitos de su izquierda y su derecha en un solo número: `12 || 345` da `12345`. Todos los operadores se siguen evaluando de izquierda a derecha.

#### Ejemplo

Además de las tres ecuaciones que ya se podían cumplir con suma y multiplicación, ahora se pueden cumplir otras tres:

| Ecuación | Cómo |
|----------|------|
| `156: 15 6` | `15 \|\| 6` |
| `7290: 6 8 6 15` | `6 * 8 \|\| 6 * 15` |
| `192: 17 8 14` | `17 \|\| 8 + 14` |

Sumando los seis valores de prueba, el nuevo resultado total de calibración es **11387**.

#### Pregunta

Con los tres operadores, ¿cuál es el resultado total de calibración de las ecuaciones que se pueden cumplir?

### Solución

#### Un operador más en el `enum`

Como la búsqueda de la parte 1 recibe los operadores como parámetro, la parte 2 consiste en agregar la concatenación al `enum` y usar todos los operadores (`EnumSet.allOf`).

Para **deshacer** la concatenación, el resultado tiene que **terminar** en los dígitos del operando, y lo anterior es lo que queda al quitarlos: en `156 = 15 || 6`, el 156 termina en 6, y lo anterior es 15. Con `Matematicas.cantidadDigitos` y `potenciaDeDiez` se hace con aritmética, sin pasar a texto:

```java
// Dia07.java — Operador.CONCATENACION
long potencia = Matematicas.potenciaDeDiez(Matematicas.cantidadDigitos(operando));
boolean terminaEnOperando = resultado % potencia == operando;
// resultado > operando: si fueran iguales, lo anterior sería 0 (y los números son positivos)
return terminaEnOperando && resultado > operando
        ? OptionalLong.of(resultado / potencia)
        : OptionalLong.empty();
```

#### Aquí se nota la ventaja de buscar hacia atrás

Con 3 operadores y hasta 11 huecos, probar hacia adelante serían hasta `3¹¹ ≈ 177 000` combinaciones por ecuación. Hacia atrás, la concatenación poda todavía más que el producto: casi ningún resultado termina justo en los dígitos del último número. Las 850 ecuaciones se resuelven en ~5 ms.

#### Verificación

- Prueba con el ejemplo (11387) y con cuáles de sus ecuaciones son posibles ahora.
- Deshacer la concatenación: casos válidos, un operando con un cero al final (`110 = 1 || 10`), un resultado que no termina en el operando, un operando igual al resultado y uno más largo que el resultado.
- La concatenación se evalúa en su turno, de izquierda a derecha (`6 * 8 || 6 * 15 = 486 * 15`).
- Prueba basada en propiedades (jqwik, 500 casos): con los tres operadores, la búsqueda coincide con probar **todas** las combinaciones hacia adelante (concatenando como texto).
- Con el input real, un script independiente en Python usa otro método: lleva el **conjunto de valores alcanzables** número por número, hacia adelante, descartando los que superan el valor de prueba (ningún operador achica el valor). Obtiene el mismo resultado.

**Resultado con el input real: 204976636995111**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de los valores de las ecuaciones posibles con `+` y `*` | Lo mismo, agregando la concatenación `\|\|` |
| **Enfoque** | Búsqueda hacia atrás deshaciendo cada operador | El mismo, con un operador más en el `enum` |
| **Complejidad** | O(2ⁿ) en el peor caso, con muchas ramas podadas | O(3ⁿ) en el peor caso, con todavía más poda |
| **Resultado** | 3351424677624 | 204976636995111 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia07.java`
