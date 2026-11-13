# Año 2023 — Día 7: Cartas de Camello

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/7](https://adventofcode.com/2023/day/7). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El viaje con todo pago resulta ser un vuelo de cinco minutos, solo de ida, en dirigible, que nos deja al borde de un desierto: la **Isla del Desierto**. Una Elfa vestida de blanco y montada en un camello nos pregunta si trajimos "las piezas". Normalmente usan máquinas grandes para mover rocas y filtrar la arena, pero se rompieron porque dejaron de llegar las piezas para arreglarlas. El viaje hasta las máquinas dura días, así que nos enseña a jugar a las **Cartas de Camello**, un juego parecido al póker pero más fácil de jugar arriba de un camello.

Cada **mano** tiene cinco cartas: `A`, `K`, `Q`, `J`, `T`, `9`, …, `2`, de la más fuerte a la más débil. Cada mano es de un **tipo**, del más fuerte al más débil:

| Tipo | Ejemplo |
|---|---|
| Repóker (cinco iguales) | `AAAAA` |
| Póker (cuatro iguales) | `AA8AA` |
| Full (tres iguales y dos iguales) | `23332` |
| Trío (tres iguales, las otras dos distintas) | `TTT98` |
| Doble par | `23432` |
| Par | `A23A4` |
| Carta alta (todas distintas) | `23456` |

Las manos se ordenan primero por tipo. A igual tipo, se compara la **primera carta** de cada mano; si son iguales, la segunda, y así. Ojo: no se ordenan las cartas de la mano, se comparan en el orden en que aparecen. Por eso `33332` le gana a `2AAAA` (los dos son póker, y 3 le gana a 2), y `77888` le gana a `77788`.

El input tiene una mano por línea con su **apuesta**. La mano más débil queda en el puesto 1, la siguiente en el 2, y así. Cada mano gana su apuesta multiplicada por su puesto.

#### Ejemplo

```
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
```

El orden queda `32T3K` (par), `KTJJT` y `KK677` (doble par; la segunda carta desempata), `T55J5` y `QQQJA` (trío). Las ganancias son 765·1 + 220·2 + 28·3 + 684·4 + 483·5 = **6440**.

#### Pregunta

¿Cuáles son las ganancias totales de todas las manos?

### Solución

#### El input real

1000 manos.

#### El tipo sale de las repeticiones

Se cuenta cuántas veces aparece cada carta y se ordenan esas cantidades de mayor a menor. Cada tipo tiene un patrón distinto, y alcanza con mirar las **dos cantidades más grandes**:

```java
// Dia07.java — Mano.tipo()
return switch (mayor) {
    case 5 -> Tipo.REPOKER;
    case 4 -> Tipo.POKER;
    case 3 -> segunda == 2 ? Tipo.FULL : Tipo.TRIO;
    case 2 -> segunda == 2 ? Tipo.DOBLE_PAR : Tipo.PAR;
    default -> Tipo.CARTA_ALTA;
};
```

Los tipos son un `enum` declarado del más débil al más fuerte, así que el orden de declaración ya es el orden de fuerza y se pueden comparar directamente.

#### Ordenar y multiplicar

El comparador ordena por tipo y, a igual tipo, compara las cartas una por una según su posición en el **orden de cartas** de las reglas (`"23456789TJQKA"` en esta parte). Una carta que no está en ese orden falla con un mensaje claro. Después se ordenan las manos de la más débil a la más fuerte y se suma la apuesta de cada una por su puesto.

#### Verificación

- Prueba con el ejemplo (6440).
- Los **siete ejemplos de tipos** del enunciado.
- Los desempates del enunciado (`33332` > `2AAAA`, `77888` > `77788`), y que el tipo pesa más que las cartas.
- El **orden completo** de las cinco manos del ejemplo.
- Una mano mal formada o una carta desconocida fallan.
- Con el input real, un script independiente en Python ordena por otra clave: el **patrón de repeticiones** como lista ordenada de mayor a menor, seguido de las cartas como números. Como Python compara listas elemento por elemento, `[5] > [4, 1] > [3, 2] > [3, 1, 1] > …` da el orden de los tipos sin nombrarlos. Obtiene el mismo resultado.

**Resultado con el input real: 246163188**

---

## Parte 2

### Enunciado

Para hacerlo más interesante, la Elfa agrega una regla: las `J` ahora son **comodines**, que pueden hacerse pasar por la carta que haga más fuerte el **tipo** de la mano. Por ejemplo, `QJJQ2` pasa a ser póker.

Para compensar, la `J` pasa a ser la carta **más débil** al desempatar, incluso más que el 2: el orden queda `A K Q T 9 8 7 6 5 4 3 2 J`. Al desempatar, la `J` siempre vale como `J`, no como la carta que imita: `JKKK2` es más débil que `QQQQ2`.

#### Ejemplo

Con las mismas cinco manos: `32T3K` sigue siendo par (no tiene comodines), `KK677` es ahora el único doble par, y `T55J5`, `KTJJT` y `QQQJA` pasan a ser **póker**. El orden queda `32T3K`, `KK677`, `T55J5`, `QQQJA`, `KTJJT`, y las ganancias son **5905**.

#### Pregunta

Con la regla de los comodines, ¿cuáles son las nuevas ganancias totales?

### Solución

#### Reglas como parámetro

La parte 1 ya recibía el orden de las cartas. Ahora las dos diferencias entre partes (el orden y si la `J` es comodín) se juntan en un record `Reglas`, con una constante para cada parte:

```java
// Dia07.java
static final Reglas PARTE1 = new Reglas("23456789TJQKA", false);
```

En la parte 2 el orden empieza con la `J` y los comodines están activados. El desempate no cambia: compara las cartas según el orden, así que la `J` vale lo menos sin ningún caso especial.

#### Los comodines van a la carta que más se repite

Para el tipo, todos los comodines conviene que imiten a la carta que **más se repite** entre las demás: así crece la repetición más grande, que es la que más pesa en el tipo. Repartirlos o sumarlos a otra carta nunca da un tipo mejor (por ejemplo, `2233J` con el comodín como 2 o como 3 da full, y como cualquier otra carta, doble par). Por eso se sacan de la cuenta y se suman a la mayor:

```java
// Dia07.java — Mano.tipo()
int cantidadComodines = comodines ? repeticiones.getOrDefault(COMODIN, 0) : 0;
if (comodines) repeticiones.remove(COMODIN);
List<Integer> cantidades = repeticiones.values().stream().sorted(Comparator.reverseOrder()).toList();
int mayor = (cantidades.isEmpty() ? 0 : cantidades.get(0)) + cantidadComodines;
```

Si la mano es toda de comodines (`JJJJJ`), no queda ninguna otra carta: la mayor es 0 + 5, y es repóker.

#### Verificación

- Prueba con el ejemplo (5905) y con el **orden completo** de las cinco manos con comodines.
- El tipo con comodines de varias manos: las del ejemplo, `QJJQ2` (póker), `JJJJJ` y `JJJJ2` (repóker), `2345J` (par) y `2233J` (full).
- Al desempatar, la `J` es la más débil: `JKKK2` < `QQQQ2` y `J2222` < `22222`.
- **Fuerza bruta** sobre 3328 manos que cubren todos los patrones de repetición con y sin comodines: reemplazar cada `J` por cada carta posible y quedarse con el mejor tipo da lo mismo que sumar los comodines a la carta que más se repite.
- Con el input real, un script independiente en Python hace esa misma **fuerza bruta** para cada mano (todas las cartas posibles para cada `J`) y ordena por el mejor patrón de repeticiones. Obtiene el mismo resultado.

**Resultado con el input real: 245794069**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Ganancias totales de las manos | Lo mismo, con la J como comodín |
| **Enfoque** | Tipo por repeticiones; ordenar por tipo y cartas | Los comodines se suman a la carta que más se repite |
| **Complejidad** | O(manos × log manos) | O(manos × log manos) |
| **Resultado** | 246163188 | 245794069 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia07.java`
