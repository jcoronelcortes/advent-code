# Año 2024 — Día 19: Disposición de Toallas

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/19](https://adventofcode.com/2024/day/19). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los Historiadores nos llevan a las aguas termales de la Isla Engranaje, y aprovechamos para intentar entrar al **onsen** de al lado. No tenemos el dinero adecuado para la entrada, pero el personal nos ofrece un trato: si los ayudamos a **acomodar sus toallas**, entramos gratis.

Cada toalla tiene un **patrón** de franjas de colores: blanco (`w`), azul (`u`), negro (`b`), rojo (`r`) o verde (`g`). Por ejemplo, una toalla `ggr` tiene dos franjas verdes y después una roja, en ese orden. Hay pocos patrones distintos, pero de cada uno hay **todas las toallas que haga falta**. Las toallas **no se pueden dar vuelta**: `ggr` no sirve como `rgg`.

El experto en imagen del onsen armó una lista de **diseños**, cada uno una secuencia larga de colores. Para mostrar un diseño hay que poner toallas una al lado de la otra de modo que sus franjas formen **exactamente** el diseño. Por ejemplo, `rgrgr` se puede formar con dos toallas `rg` y una `r`, con una `rgr` y una `gr`, o con una sola `rgrgr` (si existiera ese patrón).

El input tiene en la primera línea los patrones disponibles, separados por comas, y, después de una línea en blanco, un diseño por línea.

#### Ejemplo

```
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
```

- `brwrr` se arma con `br`, `wr` y `r`.
- `bggr` con `b`, `g`, `g` y `r`.
- `gbbr` con `gb` y `br`.
- `rrbgbr` con `r`, `rb`, `g` y `br`.
- `ubwu` **no se puede**.
- `bwurrg` con `bwu`, `r`, `r` y `g`.
- `brgr` con `br`, `g` y `r`.
- `bbrgwb` **no se puede**.

Así que **6** de los 8 diseños son posibles.

#### Pregunta

¿Cuántos de los diseños se pueden armar con los patrones disponibles?

### Solución

#### El input real

447 patrones de hasta 8 franjas, y 400 diseños de hasta 60.

#### Por qué no alcanza probar a lo bruto

La forma directa es recursiva: probar cada patrón que coincida con el comienzo del diseño y seguir con lo que queda. Funciona con los diseños posibles (encuentra uno enseguida), pero con los **imposibles** tiene que descartar todas las combinaciones, y la cantidad de formas de partir un diseño de 60 letras crece exponencialmente. Lo mismo le pasa a una expresión regular como `(r|wr|b|…)+`: en la verificación, Python no terminó en dos minutos.

Pero lo que importa de cada intento es solo **desde qué posición** queda por armar el diseño, y hay a lo sumo 61 posiciones. Eso es programación dinámica.

#### Programación dinámica desde el final

`maneras[i]` es la cantidad de formas de armar lo que queda del diseño a partir de la posición `i`. El final vacío se arma de una sola forma (sin toallas): `maneras[largo] = 1`. Desde `i`, cada patrón que coincide con el diseño en esa posición deja por armar lo que sigue después de él, así que se suman esas formas:

```java
// Dia19.java — Onsen.maneras()
int largo = diseno.length();
long[] maneras = new long[largo + 1];
maneras[largo] = 1;
for (int i = largo - 1; i >= 0; i--) {
    for (int fin = i + 1; fin <= Math.min(largo, i + largoMaximo); fin++) {
        if (maneras[fin] > 0 && patrones.contains(diseno.substring(i, fin))) {
            maneras[i] = Math.addExact(maneras[i], maneras[fin]);
        }
    }
}
return maneras[0];
```

En vez de recorrer los 447 patrones en cada posición, se prueban solo los **prefijos** de hasta 8 letras (el largo del patrón más largo), cada uno con una búsqueda en un conjunto. Son a lo sumo 60 × 8 búsquedas por diseño.

Un diseño se puede armar si `maneras[0] > 0`. Contar las formas en vez de responder solo sí o no cuesta lo mismo y deja la cuenta lista por si hace falta. `Math.addExact` falla si la cuenta no cabe en un `long`, en vez de dar un número equivocado.

#### Verificación

- Prueba con el ejemplo (6), y con **cada diseño** del ejemplo, posible o imposible como dice el enunciado.
- `rgrgr`, con los patrones `r`, `rg`, `gr`, `rgr` y `rgrgr`, se arma de 6 formas, contadas a mano.
- Las toallas no se dan vuelta: con `ggr` se arma `ggr` pero no `rgg`.
- En el ejemplo, la programación dinámica coincide con una **recursión sin memoria** que cuenta todas las formas.
- Con el input real, un script independiente en Python usa una recursión **de arriba hacia abajo con memoria** (`functools.cache`) que prueba cada patrón con `startswith`, en vez de cortar prefijos y buscarlos en un conjunto. Obtiene el mismo resultado.

**Resultado con el input real: 206**

---

## Parte 2

### Enunciado

Al personal no le gustan algunas de las disposiciones que propusimos. Para no entrar en un ciclo sin fin de reacomodar toallas, decidimos darles **todas las opciones posibles**: para cada diseño, todas las formas distintas de armarlo.

#### Ejemplo

Con los mismos patrones y diseños de la parte 1:

| Diseño | Formas | |
|---|---|---|
| `brwrr` | 2 | `b r wr r`, `br wr r` |
| `bggr` | 1 | `b g g r` |
| `gbbr` | 4 | `g b b r`, `g b br`, `gb b r`, `gb br` |
| `rrbgbr` | 6 | `r r b g b r`, `r r b g br`, `r r b gb r`, `r rb g b r`, `r rb g br`, `r rb gb r` |
| `ubwu` | 0 | imposible |
| `bwurrg` | 1 | `bwu r r g` |
| `brgr` | 2 | `b r g r`, `br g r` |
| `bbrgwb` | 0 | imposible |

En total, 2 + 1 + 4 + 6 + 1 + 2 = **16**.

#### Pregunta

Sumando, para cada diseño, la cantidad de formas distintas de armarlo, ¿cuánto da?

### Solución

#### Ya estaba contado

La programación dinámica de la parte 1 no responde solo si un diseño se puede armar: **cuenta de cuántas formas**. La parte 1 se quedaba con los diseños con `maneras > 0`; la parte 2 suma las maneras:

```java
// Dia19.java
public String parte2(List<String> lineas) {
    Onsen onsen = Onsen.parsear(lineas);
    long total = onsen.disenos().stream().mapToLong(onsen::maneras).reduce(0, Math::addExact);
    return String.valueOf(total);
}
```

La recursión explica por qué la suma es correcta: dos formas de armar un diseño que empiezan con patrones distintos son distintas, y las que empiezan con el mismo patrón se distinguen por cómo arman el resto. Así que las formas desde una posición son la suma, sobre cada patrón que encaja ahí, de las formas de armar lo que queda después.

#### Números grandes

Las cuentas crecen muy rápido: un solo diseño del input se arma de unos **92 billones** de formas, y el total ronda los 622 billones. Todavía caben de sobra en un `long` (hasta unos 9 × 10<sup>18</sup>), pero tanto `maneras` como la suma usan `Math.addExact`, que **falla** si se pasaran en vez de dar un número equivocado. Hay una prueba que lo confirma: con los patrones `a` y `aa`, las formas de armar n letras `a` son números de Fibonacci, y con 92 letras ya no caben.

#### Verificación

- Prueba con el ejemplo (16) y con la cantidad de formas de **cada diseño**, como en la tabla del enunciado.
- La prueba de desborde: 10 letras `a` se arman de 89 formas (Fibonacci), y 92 letras lanzan `ArithmeticException`.
- Las pruebas de la parte 1 comparan la programación dinámica con una recursión sin memoria que cuenta todas las formas.
- Con el input real, el script independiente en Python de la parte 1 (recursión de arriba hacia abajo con memoria) cuenta las formas con **enteros sin límite de tamaño**, así que tampoco puede desbordar. Obtiene el mismo total.

**Resultado con el input real: 622121814629343**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Cuántos diseños se pueden armar | Suma de las formas de armar cada diseño |
| **Enfoque** | Programación dinámica sobre la posición en el diseño | La misma programación dinámica, sumando las formas |
| **Complejidad** | O(diseños × largo × largo máximo de patrón) | O(diseños × largo × largo máximo de patrón) |
| **Resultado** | 206 | 622121814629343 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia19.java`
