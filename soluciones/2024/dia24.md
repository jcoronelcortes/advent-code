# Año 2024 — Día 24: Cables Cruzados

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/24](https://adventofcode.com/2024/day/24). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos a una arboleda en la selva donde los Elfos instalaron un aparato que vigila la fruta, y que últimamente anda mal. Parece que intenta producir un número con **compuertas lógicas**.

Cada compuerta tiene dos entradas y una salida, y trabaja con valores 0 y 1:

- **AND** da 1 si las dos entradas son 1;
- **OR** da 1 si al menos una entrada es 1;
- **XOR** da 1 si las entradas son distintas.

Una compuerta espera a tener valor en sus dos entradas antes de escribir su salida. **No hay ciclos**, y cada cable recibe la salida de a lo sumo una compuerta, aunque puede ser entrada de muchas.

El input tiene dos secciones separadas por una línea en blanco: el **valor inicial** de algunos cables (`x00: 1`) y la lista de **compuertas** (`x00 AND y00 -> z00`: una AND con entradas `x00` e `y00` que escribe en `z00`).

Los cables cuyo nombre empieza con `z` forman un número en binario: `z00` es el bit menos significativo, `z01` el siguiente, y así.

#### Ejemplo

```
x00: 1
x01: 1
x02: 1
y00: 0
y01: 1
y02: 0

x00 AND y00 -> z00
x01 XOR y01 -> z01
x02 OR y02 -> z02
```

Al simularlo, `z00` = 0, `z01` = 0 y `z02` = 1: el número `100` en binario, que es **4**. En un ejemplo más grande, con 36 compuertas y bits de `z00` a `z12`, el resultado es `0011111101000`, que es **2024**.

#### Pregunta

Simulando el circuito, ¿qué número en decimal forman los cables que empiezan con `z`?

### Solución

#### El input real

90 cables con valor inicial (`x00` a `x44` e `y00` a `y44`), 222 compuertas y 46 bits de salida, de `z00` a `z45`.

#### Evaluar cada cable de forma recursiva

Se guarda, para cada cable, la compuerta que lo escribe. El valor de un cable es su valor inicial, si tiene; si no, el resultado de aplicar la operación de su compuerta a los valores de sus dos entradas, que se calculan de la misma forma:

```java
// Dia24.java — Circuito.valor()
Boolean conocido = valores.get(cable);
if (conocido != null) return conocido;
Compuerta compuerta = compuertas.get(cable);
if (compuerta == null) throw new IllegalArgumentException("El cable " + cable + " no tiene valor");
if (!enCurso.add(cable)) throw new IllegalStateException("El circuito tiene un ciclo en " + cable);
boolean resultado = compuerta.operacion().aplicar(
        valor(compuerta.entrada1(), valores, enCurso),
        valor(compuerta.entrada2(), valores, enCurso));
enCurso.remove(cable);
valores.put(cable, resultado);
return resultado;
```

Los valores calculados se guardan, así cada compuerta se evalúa una sola vez sin importar en qué orden aparezcan en el input. El enunciado garantiza que no hay ciclos, pero el conjunto `enCurso` los detecta igual: si al evaluar un cable se vuelve a pedir el mismo cable, falla con un mensaje claro en vez de desbordar la pila.

#### Armar el número

`numero('z')` toma los cables cuyo nombre es la letra seguida solo de cifras (así un cable intermedio que empezara con `z` no se confunde con un bit) y prende en el resultado el bit que indica el número de cada uno que vale 1. Con 46 bits cabe en un `long`; con 63 o más, falla.

#### Verificación

- Prueba con los dos ejemplos (4 y 2024) y con el **valor de cada cable** del ejemplo grande, como en la lista del enunciado.
- Las tablas de verdad de AND, OR y XOR.
- Dar vuelta el orden de las compuertas en el input da lo mismo.
- Solo los cables con letra y cifras son bits.
- Un ciclo, un cable sin valor y dos compuertas que escriben en el mismo cable fallan con un mensaje claro.
- Con el input real, un script independiente en Python hace **pasadas repetidas** sobre las compuertas, evaluando en cada una las que ya tienen sus dos entradas, hasta que no queda ninguna. Obtiene el mismo resultado.

**Resultado con el input real: 49520947122770**

---

## Parte 2

### Enunciado

Mirando el aparato con más cuidado, resulta que el circuito intenta **sumar**: los cables `x` forman un número, los `y` otro, y la suma debería salir en los cables `z` (en los tres, el bit 00 es el menos significativo). Los valores iniciales del input son solo un caso; el circuito debería sumar bien **cualquier** par de números.

Por las marcas en el aparato se sabe que hay exactamente **cuatro pares de compuertas** a las que les **intercambiaron los cables de salida**. Cada compuerta está en a lo sumo un par. Los intercambios pueden estar en cualquier lado, no solo en cables `z`.

La respuesta son los **ocho cables** involucrados, ordenados por nombre y separados por comas.

#### Ejemplo

El enunciado usa un circuito más chico que calcula el **AND bit a bit** de dos números de 6 bits (`x00 AND y00 -> z00`, y así), pero con dos pares de salidas intercambiadas: `z05` con `z00`, y `z02` con `z01`. La respuesta sería `z00,z01,z02,z05`.

#### Pregunta

¿Qué ocho cables hay que intercambiar (ordenados y separados por comas) para que el circuito sume bien?

### Solución

#### El circuito es un sumador en cascada

El circuito real es un **sumador con acarreo en cascada** de 45 bits: en cada bit, `z = x XOR y XOR acarreo`, y el acarreo que pasa al bit siguiente es `(x AND y) OR ((x XOR y) AND acarreo)`. Un intercambio rompe la suma a partir de algún bit. Pero en vez de depender de esa estructura, la solución **busca** los intercambios probando el circuito, así que sirve para cualquier operación (el ejemplo del enunciado es un AND).

#### Puntaje: el bit más bajo que falla

Se arma un lote fijo de pares `(x, y)` pseudoaleatorios (con semilla fija, siempre los mismos) y el resultado esperado de cada uno. El **puntaje** de un circuito es el **bit más bajo de `z` que da mal** en alguna prueba: 46 si todos dan bien, −1 si quedó un ciclo.

Un intercambio correcto **sube el puntaje**: el bit más bajo que fallaba pasa a estar bien. Un intercambio al azar casi nunca lo hace.

#### Evaluación bit-paralela

Para que probar muchos circuitos sea rápido, cada cable guarda un `long` cuyo bit `t` es su valor en la prueba número `t`. Una sola pasada por las compuertas evalúa **64 pruebas a la vez**, con AND, OR y XOR de `long`. Se usan 4 lotes (256 pruebas).

```java
// Dia24.java — Reparacion.evaluar()
long a = valores[entrada1[g]];
long b = valores[entrada2[g]];
valores[cable] = switch (operaciones[g]) {
    case AND -> a & b;
    case OR -> a | b;
    case XOR -> a ^ b;
};
```

Los cables se pasan a índices y las compuertas a arreglos. Intercambiar dos salidas es intercambiar dos posiciones de `compuertaDe` (qué compuerta escribe en cada cable), sin tocar nada más. La evaluación es recursiva, con un estado por cable (sin evaluar, en curso, listo) que detecta los ciclos que puede crear un intercambio.

#### Búsqueda con backtracking

En cada paso se prueban todos los intercambios de dos salidas que todavía no se usaron (unos 24 500 con 222 compuertas) y se guardan los que suben el puntaje. Se sigue por el que más lo sube y, si más adelante no se llega a arreglar el circuito, se vuelve atrás y se prueba el siguiente:

```java
// Dia24.java — Reparacion.buscar()
mejoras.sort(Comparator.comparingInt((int[] mejora) -> mejora[2]).reversed());
for (int[] mejora : mejoras) {
    intercambiar(mejora[0], mejora[1]);
    usados.add(mejora[0]);
    usados.add(mejora[1]);
    if (buscar(restantes - 1, usados)) return true;
    usados.removeLast();
    usados.removeLast();
    intercambiar(mejora[0], mejora[1]);
}
return false;
```

Termina cuando se usaron los cuatro pares y el circuito suma bien en las 256 pruebas. En total tarda unos 200 ms.

#### Verificación

- Prueba con el ejemplo del enunciado: el AND con dos pares intercambiados da `z00,z01,z02,z05`.
- Un **sumador generado** en la prueba (correcto por construcción) suma bien (345 + 678 = 1023) y tiene el puntaje máximo.
- Al sumador de 20 bits se le intercambian a propósito **cuatro pares** de salidas, de distintos tipos (una `z` con un acarreo parcial, dos cables internos de un mismo bit, una `z` con un acarreo), y la búsqueda encuentra exactamente esos ocho cables.
- Si no alcanza con la cantidad de pares pedida, falla con un mensaje claro.
- Con el input real, un script independiente en Python usa un método completamente distinto, **sin simular**: revisa la **estructura** del sumador en cascada (cada `z` salvo la última sale de un XOR, la última sale de un OR, cada XOR que no viene de `x` e `y` escribe una `z`, cada `x XOR y` alimenta otro XOR, cada AND alimenta un OR) y marca los cables que no cumplen. Marca exactamente los mismos ocho.
- Otro script prueba las **105 formas** de emparejar esos ocho cables y comprueba que exactamente una deja un sumador que da bien en 2000 sumas al azar: `gjc`↔`qjj`, `gvm`↔`z26`, `qsb`↔`z39` y `wmp`↔`z17`.

**Resultado con el input real: gjc,gvm,qjj,qsb,wmp,z17,z26,z39**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Número que forman los cables z | Los 8 cables intercambiados para que el circuito sume |
| **Enfoque** | Evaluación recursiva con memoria | Búsqueda de intercambios que suben el bit más bajo correcto, con evaluación bit-paralela |
| **Complejidad** | O(compuertas) | O(pares × compuertas³), sin contar el backtracking |
| **Resultado** | 49520947122770 | gjc,gvm,qjj,qsb,wmp,z17,z26,z39 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia24.java`
