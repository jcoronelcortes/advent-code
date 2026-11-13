# Año 2024 — Día 11: Piedras Plutonianas

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/11](https://adventofcode.com/2024/day/11). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los Historiadores recorren los pasillos infinitos de una antigua civilización de Plutón que sabía manipular el espacio-tiempo. Entre tanto, nos llama la atención una fila recta de **piedras**, cada una con un número grabado (el input). Lo raro es que **cada vez que parpadeamos, las piedras cambian**.

En cada parpadeo, todas las piedras cambian **al mismo tiempo**, según la **primera** de estas reglas que se cumpla:

1. Si tiene grabado un **0**, pasa a tener un **1**.
2. Si su número tiene una **cantidad par de dígitos**, se parte en **dos piedras**: la de la izquierda se queda con la primera mitad de los dígitos y la de la derecha con la segunda mitad. Los ceros a la izquierda se pierden: `1000` da `10` y `0`.
3. Si no, el número se **multiplica por 2024**.

Las piedras siempre conservan su orden en la fila.

#### Ejemplo

Con la fila `0 1 10 99 999`, un parpadeo aplica cada regla: el `0` pasa a `1`, el `1` a `2024`, el `10` se parte en `1` y `0`, el `99` en `9` y `9`, y el `999` pasa a `2021976`. Quedan siete piedras: `1 2024 1 0 9 9 2021976`.

Un ejemplo más largo, desde `125 17`:

```
Al inicio:        125 17
1 parpadeo:       253000 1 7
2 parpadeos:      253 0 2024 14168
3 parpadeos:      512072 1 20 24 28676032
4 parpadeos:      512 72 2024 2 0 2 4 2867 6032
5 parpadeos:      1036288 7 2 20 24 4048 1 4048 8096 28 67 60 32
6 parpadeos:      2097446912 14168 4048 2 0 2 4 40 48 2024 40 48 80 96 2 8 6 7 6 0 3 2
```

Después de 6 parpadeos hay 22 piedras, y después de 25 hay **55312**.

#### Pregunta

¿Cuántas piedras habrá después de parpadear 25 veces?

### Solución

#### El input real

Es una sola línea con 8 piedras. Después de 25 parpadeos la fila tiene casi 200 000 piedras, pero solo **596 números distintos**.

#### Las reglas de una piedra

La transformación de una piedra se escribe tal cual el enunciado, usando `Matematicas.cantidadDigitos` y `potenciaDeDiez` para partir el número sin pasar por texto:

```java
// Dia11.java
static List<Long> transformar(long piedra) {
    if (piedra == 0) return List.of(1L);
    int digitos = Matematicas.cantidadDigitos(piedra);
    if (digitos % 2 == 0) {
        long divisor = Matematicas.potenciaDeDiez(digitos / 2);
        return List.of(piedra / divisor, piedra % divisor);
    }
    return List.of(Math.multiplyExact(piedra, 2024L));
}
```

Partir con `/` y `%` resuelve solo lo de los ceros a la izquierda: `100007 / 1000 = 100` y `100007 % 1000 = 7`.

#### Contar por número, no simular la fila

El enunciado insiste en el orden, pero no importa para la respuesta: cada piedra evoluciona **sola**, sin mirar a sus vecinas, y solo se pregunta **cuántas** hay. Entonces, en vez de la fila, se guarda cuántas piedras hay de cada número. Todas las piedras con el mismo número se transforman juntas:

```java
// Dia11.java — contarPiedras
for (int i = 0; i < parpadeos; i++) {
    Map<Long, Long> siguientes = new HashMap<>();
    cantidades.forEach((piedra, cantidad) -> {
        for (long nueva : transformar(piedra)) {
            siguientes.merge(nueva, cantidad, Math::addExact);
        }
    });
    cantidades = siguientes;
}
```

La fila crece de forma exponencial (cerca de ×1,5 por parpadeo), pero los números distintos se repiten muchísimo (los dígitos sueltos, `2024`, `4048`, …), así que cada parpadeo cuesta lo mismo sin importar cuántas piedras haya. Las sumas usan `Math::addExact` para detectar desborde.

#### Verificación

- Prueba con el ejemplo (55312 después de 25 parpadeos y 22 después de 6).
- Un parpadeo sobre `0 1 10 99 999` aplica cada regla; `1000` se parte en `10` y `0`, y `100007` en `100` y `7`.
- Los 6 pasos intermedios del ejemplo, comparados uno por uno con `parpadear`, que simula la fila completa en orden.
- Contar por número coincide con simular la fila completa en cada parpadeo, del 0 al 25.
- Con el input real, un script independiente en Python simula la **fila completa** como texto (partiendo los dígitos como cadenas) y cuenta las piedras al final. Obtiene el mismo resultado.

**Resultado con el input real: 183248**

---

## Parte 2

### Enunciado

Los Historiadores siguen tardando: los pasillos infinitos son, después de todo, muy grandes.

#### Pregunta

¿Cuántas piedras habrá después de parpadear **75** veces en total?

(Esta vez el enunciado no trae un ejemplo con resultado.)

### Solución

#### La misma función, más parpadeos

Simular la fila ya no es posible: crece cerca de ×1,5 por parpadeo, y después de 75 tiene más de 200 billones de piedras. Pero `contarPiedras` nunca guarda la fila, solo cuántas piedras hay de cada número, así que la parte 2 es la misma llamada con 75 en vez de 25:

```java
// Dia11.java
public String parte2(List<String> lineas) {
    return String.valueOf(contarPiedras(Utiles.leerNumerosLargos(lineas), PARPADEOS_PARTE2));
}
```

Lo que mantiene el costo bajo es que los números distintos **dejan de crecer**: con el input real nunca pasan de 3795, aunque la cantidad de piedras siga creciendo de forma exponencial. Los números se parten apenas llegan a una cantidad par de dígitos, así que no crecen sin límite: el mayor que aparece es 409 526 509 568, muy lejos del máximo de un `long`. La respuesta (unos 2 × 10¹⁴) también entra holgada, y `Math::addExact` lo garantiza. Tarda ~24 ms.

#### Verificación

- El ejemplo `125 17` con 75 parpadeos da 65601038650482. El enunciado no da este valor: sale del script independiente de abajo.
- Con el input real, un script independiente en Python usa otro método: una **recursión memoizada** por (número, parpadeos restantes), que cuenta cuántas piedras salen de una sola piedra y parte los números como texto. Obtiene el mismo resultado en las dos partes.

**Resultado con el input real: 218811774248729**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Cantidad de piedras después de 25 parpadeos | Lo mismo, con 75 parpadeos |
| **Enfoque** | Contar cuántas piedras hay de cada número, sin simular la fila | La misma función: los números distintos no pasan de ~3800 |
| **Complejidad** | O(parpadeos × números distintos) | O(parpadeos × números distintos) |
| **Resultado** | 183248 | 218811774248729 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia11.java`
