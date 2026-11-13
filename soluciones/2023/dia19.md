# Año 2023 — Día 19: A Montones

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/19](https://adventofcode.com/2023/day/19). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los elfos de la Isla de los Engranajes nos prestan un ala delta para bajar a la Isla del Desierto. Al pie de la avalancha de piezas de máquinas hay una montaña de piezas, y un grupo de elfos las está clasificando con un sistema.

Cada pieza tiene una **calificación** en cuatro categorías: `x` (se ve extremadamente genial), `m` (musical: suena al golpearla), `a` (aerodinámica) y `s` (brillante). Después pasa por una serie de **flujos de trabajo** que terminan **aceptándola** (`A`) o **rechazándola** (`R`).

Cada flujo tiene un nombre y una lista de **reglas**. Una regla tiene una condición (una categoría, `<` o `>` y un número) y un destino; la **primera** regla cuya condición se cumple manda la pieza a su destino. La última regla de cada flujo no tiene condición y se aplica siempre si se llega a ella. Por ejemplo, `ex{x>10:one,m<20:two,a>30:R,A}` manda la pieza a `one` si su `x` es mayor que 10; si no, a `two` si su `m` es menor que 20; si no, la rechaza si su `a` es mayor que 30; y si no, la acepta.

Si el destino es otro flujo, la pieza empieza ese flujo desde el principio y no vuelve. Si es `A` o `R`, termina ahí.

El input tiene los flujos, una línea en blanco y las calificaciones de cada pieza. Todas las piezas empiezan en el flujo `in`.

#### Ejemplo

```
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
```

La primera pieza recorre `in → qqz → qs → lnx → A`, y la segunda `in → px → rfg → gd → R`. Al final se aceptan la primera, la tercera y la quinta, cuyas calificaciones suman 7540, 4623 y 6951. El total es **19114**.

#### Pregunta

¿Cuál es la suma de todas las calificaciones de todas las piezas aceptadas?

### Solución

#### El input real

580 flujos y 200 piezas.

#### Leer con cuidado

Cada flujo se lee con expresiones regulares a una lista de `Regla` (categoría, operador, valor y destino; la última sin condición), y cada pieza a un arreglo con sus cuatro calificaciones en el orden `x, m, a, s`. Al leer se comprueba que:

- exista el flujo `in` y ningún nombre esté repetido;
- todo destino sea `A`, `R` o un flujo que existe;
- solo la última regla de cada flujo vaya sin condición.

Así, un error en el input aparece al leerlo con un mensaje claro, en vez de un `NullPointerException` a mitad del recorrido.

#### Seguir cada pieza

Desde `in`, en cada flujo se busca la primera regla que se aplica y se salta a su destino, hasta llegar a `A` o `R`:

```java
// Dia19.java — esAceptada()
String actual = INICIO;
// Sin repetir flujos, a lo sumo se visitan todos una vez
for (int pasos = 0; pasos <= flujos.size(); pasos++) {
    if (actual.equals(ACEPTADA)) return true;
    if (actual.equals(RECHAZADA)) return false;
    actual = primeraQueSeAplica(flujos.get(actual), pieza).destino();
}
throw new IllegalStateException("La pieza entra en un ciclo de flujos");
```

Como el recorrido de una pieza es determinista, si visitara más flujos de los que existen tendría que haber repetido uno, y entonces daría vueltas para siempre. En ese caso falla en vez de quedarse colgada.

#### Verificación

- Prueba con el ejemplo (19114).
- Se aceptan exactamente la primera, la tercera y la quinta pieza del ejemplo.
- Con el flujo `ex` del enunciado, se aplica la **primera** regla que cumple aunque otras también cumplan, y `<` y `>` son estrictos.
- Un input mal formado (sin `in`, con un destino desconocido, una regla de más o de menos sin condición, un flujo repetido, una pieza incompleta o sin la sección de piezas) falla con un mensaje claro.
- Una pieza que vuelve a un flujo por el que ya pasó falla en vez de no terminar.
- Con el input real, un script independiente en Python **traduce cada flujo a una expresión condicional de Python** (`'qs' if s>2770 else ('hdj' if m<1801 else 'R')`), la compila y la evalúa con las calificaciones de cada pieza, y obtiene el mismo resultado.

**Resultado con el input real: 386787**

---

## Parte 2

### Enunciado

Aun con ayuda, clasificar las piezas de a una es demasiado lento. Un elfo propone calcular de antemano **qué combinaciones** de calificaciones se aceptan.

Cada una de las cuatro calificaciones puede ser un entero **de 1 a 4000**. Considerando solo los flujos (las piezas del input ya no importan), hay que contar cuántas combinaciones distintas de `x, m, a, s` terminan aceptadas.

#### Ejemplo

Con los flujos del ejemplo se aceptan **167409079868000** combinaciones.

#### Pregunta

¿Cuántas combinaciones distintas de calificaciones aceptan los flujos?

### Solución

#### Cajas en vez de piezas

Hay 4000⁴ ≈ 2,56 · 10¹⁴ combinaciones, así que no se pueden probar una por una. Pero las reglas solo comparan una categoría con un número, así que un conjunto de piezas de la forma "`x` entre tal y tal, `m` entre tal y tal…" (una **caja** de cuatro dimensiones, un `Rango` por categoría) se puede mandar entero por un flujo.

Cada regla con condición **parte la caja en dos** según la categoría que mira: la parte que cumple va al destino de la regla y la que no cumple sigue con la regla siguiente; la última regla se lleva lo que quede. Para partir, cada regla sabe qué valores de un rango la cumplen y cuáles no:

```java
// Dia19.java — cumplen()
return rango.interseccion(operador == '<'
        ? new Rango(Long.MIN_VALUE, valor - 1L)
        : new Rango(valor + 1L, Long.MAX_VALUE));
```

Se empieza con la caja de `[1, 4000]` en las cuatro categorías desde `in`. Las cajas que llegan a `R` se descartan, y las que llegan a `A` se cuentan multiplicando el largo de sus cuatro rangos:

```java
// Dia19.java — aceptadas()
for (Regla regla : flujos.get(destino)) {
    if (regla.categoria() < 0) {
        return total + aceptadas(flujos, regla.destino(), resto, profundidad + 1);
    }
    Optional<Rango> cumplen = regla.cumplen(resto[regla.categoria()]);
    if (cumplen.isPresent()) {
        Rango[] parte = resto.clone();
        parte[regla.categoria()] = cumplen.get();
        total += aceptadas(flujos, regla.destino(), parte, profundidad + 1);
    }
    Optional<Rango> noCumplen = regla.noCumplen(resto[regla.categoria()]);
    if (noCumplen.isEmpty()) return total;
    resto[regla.categoria()] = noCumplen.get();
}
```

Cada partición separa la caja en partes **disjuntas**, así que ninguna combinación se cuenta dos veces y no hace falta restar intersecciones. Si una parte queda vacía, deja de seguirse. Igual que en la parte 1, si una caja recorre más flujos de los que existen es porque hay un ciclo, y falla en vez de no terminar.

Con el input real llegan a `A` 602 cajas y tarda unos pocos milisegundos.

#### Verificación

- Prueba con el ejemplo (167409079868000).
- `cumplen` y `noCumplen` parten `[1, 4000]` en dos rangos que no se pisan ni dejan huecos, con `<` y `>` estrictos; en los bordes, una de las partes queda vacía.
- En el ejemplo, coincide con una **fuerza bruta comprimida**: los umbrales de las reglas parten cada categoría en intervalos donde ninguna condición cambia, se evalúa una pieza por celda con `esAceptada` de la parte 1 y se suma el tamaño de las celdas aceptadas.
- Con calificaciones de 1 a 12 y unos flujos inventados, coincide con probar **pieza por pieza** las 12⁴ = 20 736 combinaciones, y con la fuerza bruta comprimida.
- Un flujo que acepta todo da 4000⁴, uno que rechaza todo da 0, y un ciclo de flujos falla.
- Con el input real, un script independiente en Python recorre las cajas de otra forma (iterativo, con una pila y límites `lo`/`hi`) y obtiene el mismo resultado. Además, para 300 000 piezas al azar comprueba que "está dentro de alguna caja aceptada" coincide **siempre** con el evaluador de la parte 1 (que traduce los flujos a expresiones de Python), y que ninguna pieza cae en dos cajas.

**Resultado con el input real: 131029523269531**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de las calificaciones de las piezas aceptadas | Combinaciones de 1 a 4000 aceptadas |
| **Enfoque** | Seguir cada pieza aplicando la primera regla que cumple | Mandar cajas de rangos por los flujos, partiéndolas en cada regla |
| **Complejidad** | O(piezas × reglas recorridas) | O(cajas resultantes × reglas recorridas) |
| **Resultado** | 386787 | 131029523269531 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia19.java`
