# Año 2025 — Día 4: Departamento de Impresión

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/4](https://adventofcode.com/2025/day/4). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

La escalera mecánica nos deja en el **departamento de impresión**, que ya se prepara para Navidad: hay grandes **rollos de papel** por todas partes y una impresora gigante en un rincón. Lo que necesitamos es seguir bajando, y los elfos creen que detrás de la pared del fondo hay una cafetería. Podrían derribar la pared, pero sus **montacargas** están demasiado ocupados moviendo rollos. Si optimizamos ese trabajo, quizás les quede tiempo libre.

Los rollos (`@`) están sobre una gran grilla, y los elfos tienen un diagrama de dónde está cada uno (el input):

```
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
```

Un montacargas solo puede **acceder** a un rollo si entre sus **8 posiciones vecinas** (incluidas las diagonales) hay **menos de 4** rollos.

#### Ejemplo

Los rollos accesibles del ejemplo, marcados con `x`, son 13:

```
..xx.xx@x.
x@@.@.@.@@
@@@@@.x.@@
@.@@@@..@.
x@.@@@@.@x
.@@@@@@@.@
.@.@.@.@@@
x.@@@.@@@@
.@@@@@@@@.
x.x.@@@.x.
```

Por ejemplo, el rollo de la esquina inferior izquierda tiene solo 3 posiciones vecinas dentro de la grilla, y en una sola hay un rollo: es accesible. En cambio, el rollo de la fila 0, columna 7 tiene exactamente 4 rollos alrededor, y no lo es: la condición es estrictamente *menos* de 4.

#### Pregunta

En el diagrama completo, ¿cuántos rollos de papel son accesibles para un montacargas?

### Solución

Es una simulación directa: para cada `@` se cuentan los `@` entre sus 8 vecinos y se cuenta el rollo si son menos de 4. Se reutiliza la utilidad `Grilla`:

- `buscarTodos('@')` entrega las posiciones de todos los rollos.
- `contarVecinos(punto, '@')` cuenta los rollos entre los 8 vecinos que están **dentro** de la grilla, así que los bordes y esquinas no requieren casos especiales: una posición fuera de la grilla simplemente no cuenta como rollo.

```java
// Dia04.java
static List<Punto> accesibles(Grilla grilla) {
    return grilla.buscarTodos(ROLLO).stream()
            .filter(rollo -> rollosVecinos(grilla, rollo) < LIMITE_VECINOS)
            .toList();
}

static int rollosVecinos(Grilla grilla, Punto punto) {
    return grilla.contarVecinos(punto, ROLLO);
}
```

`accesibles` devuelve las posiciones (no solo la cantidad) para poder probar que coinciden **exactamente** con las `x` del enunciado, y porque la parte 2 las usa como punto de partida.

#### Verificación

- Las pruebas comparan el conjunto de posiciones accesibles con las `x` del diagrama marcado del enunciado, además del total 13.
- Casos borde: un rollo aislado es accesible; en un bloque 3×3 lleno solo las 4 esquinas lo son (3 vecinos), mientras que los bordes tienen 5 y el centro 8.
- Con el input real, un script independiente en Python que recorre la grilla con índices obtiene el mismo resultado.

**Resultado con el input real: 1551**

---

## Parte 2

### Enunciado

Ahora los elfos quieren acceder a la mayor cantidad de papel posible. Un rollo accesible se puede **retirar**, y al retirarlo es posible que otros rollos queden accesibles y también se puedan retirar. El proceso se repite hasta que no quede ningún rollo accesible.

#### Ejemplo

Con el mismo diagrama, una forma de retirar la mayor cantidad posible es hacerlo por rondas, sacando en cada una todos los rollos accesibles en ese momento:

| Ronda | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|-------|---|---|---|---|---|---|---|---|---|
| Rollos retirados | 13 | 12 | 7 | 5 | 2 | 1 | 1 | 1 | 1 |

Después de la novena ronda no queda ningún rollo accesible. En total se retiran **43** rollos.

#### Pregunta

Partiendo del diagrama original, ¿cuántos rollos de papel en total pueden retirar los elfos con sus montacargas?

### Solución

#### Clave: el orden no importa

Retirar un rollo solo puede **disminuir** la cantidad de vecinos de los demás. Entonces, un rollo que ya es accesible sigue siéndolo hasta que se retira, y el conjunto final de rollos retirados es el mismo sin importar en qué orden se saquen. No hace falta simular rondas.

#### Cola de trabajo en lugar de rondas

La simulación por rondas recorre la grilla completa en cada ronda. En vez de eso se usa una **cola de trabajo**:

1. Se parte con los rollos accesibles de la parte 1.
2. Se saca un rollo de la cola; si todavía está, se retira (queda `.`).
3. Solo sus 8 vecinos cambiaron de conteo, así que se revisan solo esos: los que son rollos y ahora tienen menos de 4 vecinos se agregan a la cola.

Un rollo puede quedar en la cola más de una vez; la comprobación del paso 2 evita contarlo dos veces.

```java
// Dia04.java — retirarTodos()
Deque<Punto> pendientes = new ArrayDeque<>(accesibles(grilla));
long retirados = 0;
while (!pendientes.isEmpty()) {
    Punto rollo = pendientes.poll();
    if (grilla.obtener(rollo) != ROLLO) continue;   // ya se retiró
    grilla.establecer(rollo, VACIO);
    retirados++;
    for (Punto vecino : grilla.vecinosConDiagonales(rollo)) {
        if (grilla.obtener(vecino) == ROLLO && rollosVecinos(grilla, vecino) < LIMITE_VECINOS) {
            pendientes.add(vecino);
        }
    }
}
```

Cada rollo se retira una sola vez y provoca a lo sumo 8 revisiones de 8 vecinos, así que el costo es lineal en el tamaño de la grilla.

#### Verificación

- Una prueba simula el proceso **por rondas**, igual que el enunciado, y verifica la secuencia `13, 12, 7, 5, 2, 1, 1, 1, 1`; otra verifica el total 43 con la cola de trabajo.
- Caso borde: en un bloque 3×3 lleno se retiran los 9 rollos (tras las esquinas, los bordes quedan con 3 vecinos y el centro con 4; tras los bordes, el centro queda con 0).
- Con el input real, un script independiente en Python que simula por rondas obtiene el mismo resultado.

**Resultado con el input real: 9784**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Rollos con menos de 4 rollos vecinos (8 direcciones) | Total de rollos retirados repitiendo el proceso |
| **Enfoque** | Recorrer la grilla contando vecinos con `Grilla.contarVecinos` | Cola de trabajo: al retirar un rollo solo se revisan sus vecinos |
| **Complejidad** | O(filas · columnas) | O(filas · columnas) |
| **Resultado** | 1551 | 9784 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia04.java`
