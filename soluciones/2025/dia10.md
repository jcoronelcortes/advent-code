# Año 2025 — Día 10: Fábrica

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/10](https://adventofcode.com/2025/day/10). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Al otro lado del pasillo hay una gran **fábrica**. Los elfos tienen todo el tiempo del mundo para decorar, pero porque las máquinas están apagadas y nadie sabe el procedimiento de inicio: un perro Shiba Inu se comió esa parte del manual. Solo quedan, para cada máquina, un diagrama de **luces indicadoras**, los esquemas de cableado de sus **botones** y unos **requisitos de voltaje**.

El manual describe una máquina por línea (el input):

```
[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
```

- Entre corchetes está el **diagrama de luces**: `.` es apagada y `#` encendida. La máquina tiene tantas luces como indica el diagrama, y **todas empiezan apagadas**. Para arrancar la máquina, las luces tienen que quedar exactamente como en el diagrama: `[.##.]` pide la primera apagada, la segunda y la tercera encendidas y la cuarta apagada.
- Entre paréntesis están los **botones**. Cada uno indica qué luces **alterna** (enciende si estaba apagada, apaga si estaba encendida), contando desde 0. Por ejemplo, `(0,3,4)` alterna la primera, la cuarta y la quinta luz: si las luces estaban `[#.....]`, quedan `[...##.]`.
- Entre llaves están los requisitos de voltaje, que **por ahora no importan**.

Cada botón se puede apretar las veces que se quiera, siempre una cantidad entera (no existe apretar "medio botón", ni una cantidad negativa). Para ahorrar tiempo, hay que encontrar la **menor cantidad de pulsaciones** que deja las luces de cada máquina como en su diagrama.

#### Ejemplo

| Máquina | Mínimo | Una forma de lograrlo |
|---------|--------|-----------------------|
| `[.##.]` | 2 | apretar `(0,2)` y `(0,1)` una vez cada uno |
| `[...#.]` | 3 | apretar `(0,4)`, `(0,1,2)` y `(1,2,3,4)` una vez cada uno |
| `[.###.#]` | 2 | apretar `(0,3,4)` y `(0,1,2,4,5)` una vez cada uno |

En la primera máquina también funciona apretar los tres primeros botones (3 pulsaciones), o `(1,3)`, `(2,3)` y dos veces `(0,1)` (4 pulsaciones), pero el mínimo es 2. En total, el ejemplo necesita `2 + 3 + 2 =` **7** pulsaciones.

#### Pregunta

¿Cuál es la menor cantidad total de pulsaciones para dejar las luces de todas las máquinas como en sus diagramas?

### Solución

#### El input real

Hay 187 máquinas, con hasta 10 luces y 13 botones cada una.

#### Luces y botones como bits

Cada luz es un bit: el estado de las luces es un número donde el bit `i` indica si la luz `i` está encendida. Un botón también es una máscara de bits, y apretarlo **alterna** sus luces, que es exactamente un **XOR**:

```
estado  0000      [....]
botón   0101      (0,2)
XOR     0101      [#.#.]
```

En el ejemplo, `[.##.]` es la máscara `0110` (luces 1 y 2).

#### BFS sobre los estados

Con 10 luces hay como mucho `2¹⁰ = 1024` estados posibles. Se busca el camino más corto desde "todo apagado" (`0`) hasta el diagrama, donde cada paso es apretar un botón. Es una **BFS** con la utilidad `BFS`:

```java
// Dia10.java — Maquina.minimasPulsacionesLuces()
return BFS.buscar(0,
                estado -> estado == objetivo,
                estado -> mascaras.stream().map(boton -> estado ^ boton).toList())
        .orElseThrow(...);
```

La BFS explora primero todos los estados a 1 pulsación, luego a 2, etc., así que el primero que coincide con el diagrama da el mínimo.

#### Por qué nunca conviene apretar un botón dos veces

El XOR cumple `x ^ b ^ b = x`: apretar un botón dos veces lo deja todo igual. Por eso la solución óptima aprieta cada botón **0 o 1 vez**, y además el orden no importa. La BFS no necesita saberlo (lo descubre sola, porque volver a un estado ya visitado no se explora), pero es lo que permite verificarlo de otra forma.

El parseo usa expresiones regulares para el diagrama `[...]`, cada botón `(...)` y los voltajes `{...}`; los voltajes se guardan en el record `Maquina` aunque esta parte no los use (la parte 2 sí).

#### Verificación

- Prueba con el ejemplo (7) y con el mínimo de cada máquina (2, 3 y 2).
- Prueba del parseo: diagrama, botones, máscaras y voltajes.
- Si las luces ya coinciden con el diagrama, el mínimo es 0; si ninguna combinación sirve, se lanza una excepción en vez de devolver un número sin sentido.
- Con el input real, un script independiente en Python que prueba **subconjuntos** de botones de menor a mayor tamaño (aprovechando que cada botón se aprieta 0 o 1 vez) obtiene el mismo resultado.

**Resultado con el input real: 491**

---

## Parte 2

### Enunciado

Las máquinas empiezan a encenderse, y ahora toca ocuparse del voltaje. Debajo de los botones de cada máquina hay una palanca que los cambia de modo: en vez de configurar las luces, pasan a **aumentar contadores de voltaje** (el diagrama de luces ya no importa).

Cada máquina tiene un contador por cada número de los requisitos de voltaje, y **todos empiezan en 0**. Por ejemplo, `{3,5,4,7}` indica cuatro contadores que tienen que terminar en 3, 5, 4 y 7, respectivamente.

Los esquemas de los botones siguen valiendo: ahora cada botón indica qué contadores **aumenta en 1** cada vez que se aprieta. Por ejemplo, `(1,3)` suma 1 al segundo y al cuarto contador: si estaban en `{0,1,2,3}`, quedan en `{0,2,2,4}`.

Cada máquina tiene que quedar con sus contadores **exactamente** en los requisitos, y al dedo ya le duele tanto apretar botones que hay que hacerlo con la **menor cantidad de pulsaciones** posible.

#### Ejemplo

| Máquina | Requisitos | Mínimo | Una forma de lograrlo |
|---------|------------|--------|-----------------------|
| 1 | `{3,5,4,7}` | 10 | `(3)` una vez, `(1,3)` tres veces, `(2,3)` tres veces, `(0,2)` una vez y `(0,1)` dos veces |
| 2 | `{7,5,12,7,2}` | 12 | `(0,2,3,4)` dos veces, `(2,3)` cinco veces y `(0,1,2)` cinco veces |
| 3 | `{10,11,11,5,10,5}` | 11 | `(0,1,2,3,4)` cinco veces, `(0,1,2,4,5)` cinco veces y `(1,2)` una vez |

En total, el ejemplo necesita `10 + 12 + 11 =` **33** pulsaciones.

#### Pregunta

¿Cuál es la menor cantidad total de pulsaciones para dejar los contadores de voltaje de todas las máquinas exactamente en sus requisitos?

### Solución

#### Es programación lineal entera

Si `x_j` es la cantidad de veces que se aprieta el botón `j`, cada contador `i` da una ecuación:

```
Σ x_j  (sobre los botones j que tocan el contador i)  =  voltaje_i
```

Con `x_j` **entero** y **no negativo**, y minimizando `Σ x_j`. Es un sistema `A·x = b` donde `A` tiene una fila por contador y una columna por botón (con un 1 si el botón toca ese contador).

A diferencia de la parte 1, aquí apretar un botón dos veces sí importa (suma 2), y los voltajes llegan a 285, así que una BFS sobre los estados de los contadores sería inviable (285¹⁰ estados).

#### Eliminación gaussiana + enumerar las variables libres

1. **Eliminación gaussiana** con fracciones exactas (`BigFraction` de Commons Math) hasta la forma escalonada reducida. Cada botón queda como variable **pivote** (determinada por las demás) o **libre** (sobra porque hay más botones que ecuaciones independientes).
2. Cada pivote queda en función de las libres. Para enumerar rápido se reescribe con enteros: `x_p = (R − Σ C_f·x_f) / D`, con `long`.
3. Se **prueban todos los valores** de las variables libres. Cada una está acotada: un botón no se puede apretar más veces que el menor voltaje de los contadores que toca, porque los contadores nunca bajan. Para cada combinación, la solución es válida si todos los pivotes son **enteros y no negativos**; se queda la de menor suma.

```java
// Dia10.java — SistemaBotones.enumerar() (al llegar a una combinación completa)
long total = sumaLibres;
for (int p = 0; p < pivotes.length; p++) {
    long numerador = constante[p];
    for (int f = 0; f < libres.length; f++) numerador -= coeficiente[p][f] * valores[f];
    if (numerador < 0 || numerador % denominador[p] != 0) return;   // negativo o no entero
    total += numerador / denominador[p];
    if (total >= mejor[0]) return;
}
mejor[0] = total;
```

Además se poda: si la suma de las libres ya alcanza al mejor total encontrado, no se sigue (los pivotes solo pueden sumar).

#### Por qué es rápido

En el input real, de las 187 máquinas, 100 no tienen variables libres (solución única), 39 tienen 1, 41 tienen 2 y solo 7 tienen 3. Con cotas de como mucho 285, el peor caso son unos pocos millones de combinaciones, cada una con aritmética `long`. Las 187 máquinas se resuelven en ~50 ms.

#### Verificación

- Prueba con el ejemplo (33) y con el mínimo de cada máquina (10, 12 y 11).
- Sistemas sin variables libres y con una libre (un botón `(0,1)` que reemplaza a `(0)` + `(1)` con menos pulsaciones).
- Casos sin solución: voltajes inalcanzables, una solución que exigiría pulsaciones negativas, y una cuya única solución real es fraccionaria (½ de cada botón).
- Prueba basada en propiedades (jqwik, 300 casos): en máquinas pequeñas al azar, alcanzables o no, el resultado coincide con probar **todas** las combinaciones de pulsaciones.
- Con el input real, un script independiente en Python obtiene el mismo resultado con un método completamente distinto, por **paridad**: los botones apretados una cantidad impar de veces tienen que dar la paridad de cada voltaje (como en la parte 1); después de apretarlos, lo que falta es par en todos los contadores, así que el problema que queda es el mismo con los voltajes a la mitad: `f(b) = min |S| + 2·f((b − A·S) / 2)`, con memoización.

**Resultado con el input real: 20617**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Mínimo de pulsaciones para encender las luces del diagrama | Mínimo de pulsaciones para llegar exacto a los voltajes |
| **Enfoque** | Luces como bits, botones como XOR, BFS sobre los estados | Eliminación gaussiana exacta + enumerar variables libres acotadas |
| **Complejidad** | O(2^L · B) por máquina, con L luces y B botones | O(V^F · L) por máquina, con F ≤ 3 variables libres y V ≤ 285 |
| **Resultado** | 491 | 20617 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia10.java`
