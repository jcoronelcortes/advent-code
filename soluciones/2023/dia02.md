# Año 2023 — Día 2: Enigma de los Cubos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/2](https://adventofcode.com/2023/day/2). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El trebuchet nos lanza hasta una isla que flota en el cielo, la **Isla de la Nieve**, aunque nieve no se ve mucha. Un Elfo nos recibe y, mientras caminamos, nos propone un juego.

Tiene una bolsa y cubos **rojos**, **verdes** y **azules**. En cada partida esconde en la bolsa una cantidad secreta de cubos de cada color. Después, varias veces, saca un **puñado** al azar, nos lo muestra y lo **vuelve a guardar**.

El input tiene una partida por línea: su número y los puñados que se mostraron, separados por `;`, como `Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green`.

#### Ejemplo

```
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
```

Si la bolsa tuviera **12 rojos, 13 verdes y 14 azules**, las partidas 1, 2 y 5 serían posibles. La 3 no, porque en un momento se mostraron 20 rojos; la 4 tampoco, por los 15 azules. La suma de los números de las partidas posibles es 1 + 2 + 5 = **8**.

#### Pregunta

Con 12 cubos rojos, 13 verdes y 14 azules en la bolsa, ¿cuánto suman los números de las partidas que serían posibles?

### Solución

#### El input real

100 partidas, cada una con varios puñados.

#### Solo importa el máximo de cada color

Como los cubos vuelven a la bolsa después de cada puñado, los puñados no se acumulan: una partida es posible si **ningún puñado** mostró más cubos de un color de los que hay en la bolsa. Eso es lo mismo que pedir que el **máximo** de cada color entre todos los puñados entre en la bolsa.

```java
// Dia02.java — Partida.minimoNecesario()
return punados.stream().reduce(new Cubos(0, 0, 0), Cubos::maximo);
```

Ese máximo es, además, lo **mínimo que tiene que haber** de cada color en la bolsa para que la partida sea posible. El record `Cubos` guarda una cantidad de cada color, y sirve tanto para un puñado como para la bolsa.

#### Verificación

- Prueba con el ejemplo (8), con los **tres puñados** de la partida 1 como los describe el enunciado, y con cada partida posible o imposible.
- El límite es inclusivo: 12 rojos entran en una bolsa con 12, y 13 no.
- Un color desconocido o una línea sin `:` fallan con un mensaje claro.
- Con el input real, un script independiente en Python busca con una expresión regular cada aparición `cantidad color` y descarta la partida si **alguna** supera el límite de su color, sin separar los puñados. Obtiene el mismo resultado.

**Resultado con el input real: 2285**

---

## Parte 2

### Enunciado

El Elfo cuenta que dejaron de producir nieve porque **no les llega agua**, y nos muestra el camino a la fuente para que la revisemos. Mientras tanto, hace una segunda pregunta: en cada partida, ¿cuál es la **menor cantidad** de cubos de cada color que podía haber en la bolsa para que la partida fuera posible?

La **potencia** de un conjunto de cubos es la cantidad de rojos por la de verdes por la de azules.

#### Ejemplo

Con las mismas partidas de la parte 1:

| Partida | Mínimo (rojos, verdes, azules) | Potencia |
|---|---|---|
| 1 | 4, 2, 6 | 48 |
| 2 | 1, 3, 4 | 12 |
| 3 | 20, 13, 6 | 1560 |
| 4 | 14, 3, 15 | 630 |
| 5 | 6, 3, 2 | 36 |

Las potencias suman **2286**.

#### Pregunta

¿Cuánto suman las potencias de los conjuntos mínimos de todas las partidas?

### Solución

#### Ya estaba calculado

En la parte 1, el máximo de cada color entre los puñados de una partida ya era, justamente, lo **mínimo** que tenía que haber en la bolsa: con un cubo menos de cualquier color, el puñado que mostró ese máximo sería imposible. Así que la parte 2 solo agrega la potencia y suma:

```java
// Dia02.java
public String parte2(List<String> lineas) {
    long suma = partidas(lineas).stream()
            .mapToLong(partida -> partida.minimoNecesario().potencia())
            .sum();
    return String.valueOf(suma);
}
```

La potencia se calcula en `long` por si las cantidades fueran grandes. Si un color no aparece nunca en una partida, su mínimo es 0, y la potencia también.

#### Verificación

- Prueba con el ejemplo (2286), y con el **mínimo y la potencia** de cada partida, como en la tabla del enunciado.
- Para cada partida del ejemplo, el mínimo hace posible la partida, y con **un cubo menos** de cualquier color deja de serlo.
- Un color que nunca aparece da potencia 0.
- Con el input real, un script independiente en Python toma el máximo de cada color con una expresión regular sobre toda la línea, sin separar puñados. Obtiene el mismo resultado.

**Resultado con el input real: 77021**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Partidas posibles con 12 rojos, 13 verdes y 14 azules | Suma de las potencias de los conjuntos mínimos |
| **Enfoque** | Máximo de cada color por partida | El mismo máximo, multiplicando los tres colores |
| **Complejidad** | O(largo del input) | O(largo del input) |
| **Resultado** | 2285 | 77021 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia02.java`
