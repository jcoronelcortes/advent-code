# Año 2023 — Día 16: El Suelo Será Lava

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/16](https://adventofcode.com/2023/day/16). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con el haz de luz ya enfocado, el reno nos lleva hasta el corazón de la montaña, que en realidad es una cueva gigante. Ahí el haz sale de la pared y entra en un **artefacto**: una grilla cuadrada con espacios vacíos (`.`), **espejos** (`/` y `\`) y **divisores** (`|` y `-`). Cada casilla por la que pasa la luz convierte parte de ella en calor para derretir la roca.

El haz entra por la **esquina superior izquierda**, yendo **hacia la derecha**, y se comporta así según lo que encuentra:

- **`.`**: sigue en la misma dirección.
- **Espejo** (`/` o `\`): gira 90° según la inclinación del espejo. Por ejemplo, yendo a la derecha, un `/` lo manda hacia arriba y un `\` hacia abajo.
- **Divisor por la punta** (yendo a la izquierda o derecha contra un `-`, o arriba o abajo contra un `|`): lo atraviesa como si fuera vacío.
- **Divisor por el costado plano**: el haz se divide en dos, uno hacia cada punta. Por ejemplo, yendo a la derecha contra un `|`, salen un haz hacia arriba y otro hacia abajo.

Los haces no interactúan entre sí. Una casilla queda **energizada** si al menos un haz pasa por ella, se refleja o se divide en ella.

#### Ejemplo

```
.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....
```

Las casillas energizadas (`#`) quedan así:

```
######....
.#...#....
.#...#####
.#...##...
.#...##...
.#...##...
.#..####..
########..
.#######..
.#...#.#..
```

Son **46**.

#### Pregunta

Con el haz entrando arriba a la izquierda hacia la derecha, ¿cuántas casillas quedan energizadas?

### Solución

#### El input real

Un artefacto de 110 × 110.

#### Hacia dónde sale el haz

Cada casilla decide, según la dirección de entrada, hacia dónde sigue el haz (una o dos direcciones). Los espejos son giros de 90°: un `/` gira a la izquierda los haces horizontales (derecha → arriba) y a la derecha los verticales (arriba → derecha); el `\` hace lo contrario:

```java
// Dia16.java — salidas()
boolean horizontal = entrada.esHorizontal();
return switch (casilla) {
    case '.' -> List.of(entrada);
    case '/' -> List.of(horizontal ? entrada.giroIzquierda() : entrada.giroDerecha());
    case '\\' -> List.of(horizontal ? entrada.giroDerecha() : entrada.giroIzquierda());
    case '|' -> horizontal ? List.of(Direccion.ARRIBA, Direccion.ABAJO) : List.of(entrada);
    case '-' -> horizontal ? List.of(entrada) : List.of(Direccion.IZQUIERDA, Direccion.DERECHA);
    default -> throw new IllegalArgumentException("Casilla desconocida: '" + casilla + "'");
};
```

#### Recorrer estados, no haces

Los haces pueden quedar dando vueltas para siempre (dos divisores que se devuelven la luz, o un circuito de espejos), así que no se puede simular cada haz hasta que salga de la grilla. La clave es que un haz queda determinado por su **estado**: casilla y dirección. Si un haz llega a un estado por el que ya pasó otro, hará exactamente el mismo recorrido, así que no aporta nada nuevo.

Se recorren los estados con una pila, procesando cada uno una sola vez. Hay a lo sumo `filas × columnas × 4` estados, así que se marcan en un arreglo de `boolean` en vez de un `HashSet`; las casillas energizadas se cuentan aparte, la primera vez que se pisa cada una.

#### Verificación

- Prueba con el ejemplo (46).
- Cada espejo y divisor manda el haz en las direcciones que describe el enunciado, en los cuatro sentidos de entrada.
- En el ejemplo, coincide con una **simulación por frentes**: mover todos los haces a la vez un paso por turno, guardando los estados en un `HashSet`, hasta que no aparezca ninguno nuevo. Las casillas que pisa son exactamente las `#` del diagrama del enunciado.
- Un haz atrapado en un circuito de espejos y divisores termina igual.
- Con el input real, un script independiente en Python representa posiciones y direcciones con **números complejos**, donde reflejarse en un espejo es intercambiar las componentes de la dirección, y obtiene el mismo resultado.

**Resultado con el input real: 8125**

---

## Parte 2

### Enunciado

La luz no alcanza para producir lava. El reno nos lleva a un panel de control que permite **alinear el artefacto** para que el haz entre por **cualquier casilla del borde**, alejándose de ese borde: por la fila de arriba hacia abajo, por la de abajo hacia arriba, por la columna izquierda hacia la derecha o por la derecha hacia la izquierda. En una esquina se puede elegir cualquiera de las dos direcciones que se alejan del borde.

Hay que encontrar la configuración que energiza **la mayor cantidad** de casillas.

#### Ejemplo

Con el mismo artefacto, lo mejor es entrar por la cuarta casilla de la fila de arriba, yendo hacia abajo: así quedan energizadas **51** casillas.

#### Pregunta

¿Cuántas casillas quedan energizadas con la mejor configuración de entrada?

### Solución

#### Probar todas las entradas

Hay `2 × (filas + columnas)` entradas posibles: 440 en el input real, contando dos veces cada esquina (una por cada dirección). Cada recorrido de la parte 1 es lineal en el tamaño de la grilla, así que alcanza con probarlas todas y quedarse con el máximo, reutilizando `energizadas` sin cambios:

```java
// Dia16.java — entradasDesdeLosBordes()
List<Haz> entradas = new ArrayList<>();
for (int x = 0; x < columnas; x++) {
    entradas.add(new Haz(new Punto(x, 0), Direccion.ABAJO));
    entradas.add(new Haz(new Punto(x, filas - 1), Direccion.ARRIBA));
}
for (int y = 0; y < filas; y++) {
    entradas.add(new Haz(new Punto(0, y), Direccion.DERECHA));
    entradas.add(new Haz(new Punto(columnas - 1, y), Direccion.IZQUIERDA));
}
return entradas;
```

Con el input real tarda menos de 0,1 segundos.

#### Verificación

- Prueba con el ejemplo (51).
- Entrando por la cuarta casilla de arriba hacia abajo se energizan las 51 casillas del enunciado.
- Las entradas son `2 × (filas + columnas)`, todas distintas y dentro de la grilla, cada una con el paso anterior **fuera** de la grilla (o sea, el haz viene de afuera); la esquina inferior derecha aparece hacia la izquierda y hacia arriba.
- En el ejemplo, coincide con elegir las entradas de otra forma: recorrer **todas las casillas y direcciones** y quedarse con las que tienen el paso anterior fuera de la grilla.
- Con el input real, el script independiente en Python de la parte 1 (con números complejos) elige las entradas de esa misma otra forma y obtiene el mismo resultado.

**Resultado con el input real: 8489**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Casillas energizadas desde arriba a la izquierda | Máximo de casillas energizadas entrando desde un borde |
| **Enfoque** | Recorrer estados (casilla, dirección) una sola vez cada uno | Probar las 2 × (filas + columnas) entradas |
| **Complejidad** | O(filas × columnas) | O((filas + columnas) × filas × columnas) |
| **Resultado** | 8125 | 8489 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia16.java`
