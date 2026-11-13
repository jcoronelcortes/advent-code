# Año 2024 — Día 22: Mercado de Monos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/22](https://adventofcode.com/2024/day/22). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Nos teletransportamos a la selva y un mono **roba el aparato** de los Historiadores. Está dispuesto a devolverlo, pero a cambio de una cantidad absurda de bananas. La única forma de conseguirlas es en el **Mercado de Monos**, vendiendo buenos escondites a los compradores.

Los precios de los compradores parecen aleatorios, pero son **pseudoaleatorios**: cada comprador genera una secuencia de **números secretos**, cada uno a partir del anterior, con tres pasos:

1. Multiplicar el secreto por 64, **mezclar** el resultado con el secreto y **podar**.
2. Dividir el secreto por 32 (redondeando hacia abajo), mezclar y podar.
3. Multiplicar el secreto por 2048, mezclar y podar.

**Mezclar** un valor es hacer el XOR bit a bit con el secreto (mezclar 15 en 42 da 37). **Podar** es quedarse con el secreto módulo 16777216 (podar 100000000 da 16113920).

El input es el número secreto inicial de cada comprador. En un día, cada uno genera **2000** números secretos nuevos.

#### Ejemplo

Si un comprador empieza con 123, sus diez números siguientes son 15887950, 16495136, 527345, 704524, 1553684, 12683156, 11100544, 12249484, 7753432 y 5908254.

Con los compradores 1, 10, 100 y 2024, el número secreto 2000 de cada uno es 8685429, 4700978, 15273692 y 8667524, que suman **37327623**.

#### Pregunta

¿Cuánto suma el número secreto 2000 de todos los compradores?

### Solución

#### El input real

2020 compradores, cada uno con un número secreto inicial menor que 2<sup>24</sup>.

#### Simulación directa

No hay atajo que buscar: son 2020 × 2000 ≈ 4 millones de pasos, cada uno con unas pocas operaciones. `siguiente` escribe los tres pasos tal como los describe el enunciado:

```java
// Dia22.java — siguiente()
secreto = podar(mezclar(secreto, secreto * 64));
secreto = podar(mezclar(secreto, secreto / 32));
secreto = podar(mezclar(secreto, secreto * 2048));
return secreto;
```

Todo cabe en un `long`: el secreto es menor que 2<sup>24</sup> y se multiplica a lo sumo por 2<sup>11</sup>.

#### Son operaciones de bits

64, 32 y 2048 son potencias de 2 (2<sup>6</sup>, 2<sup>5</sup> y 2<sup>11</sup>), y 16777216 es 2<sup>24</sup>. Así que multiplicar y dividir son **corrimientos** de bits, y podar es quedarse con los **24 bits bajos** (`& 0xFFFFFF`). El generador es un *xorshift*, un generador pseudoaleatorio clásico. El código mantiene la forma del enunciado para poder compararlo línea por línea, y una prueba comprueba que la versión con corrimientos y máscara da lo mismo.

#### Verificación

- Prueba con el ejemplo (37327623), con el número 2000 de **cada comprador** y con los **diez números** que siguen a 123.
- Los ejemplos de mezclar (42 y 15 → 37) y podar (100000000 → 16113920).
- 10 000 pasos seguidos coinciden con la versión con corrimientos y máscara.
- Con el input real, un script independiente en Python con la versión de **corrimientos y máscara** obtiene el mismo resultado.

**Resultado con el input real: 17163502021**

---

## Parte 2

### Enunciado

Claro que los números secretos no son los precios: el **precio** que ofrece un comprador es la **última cifra** de cada número secreto. Si un comprador empieza con 123, sus primeros diez precios son 3, 0, 6, 5, 4, 4, 6, 4, 4 y 2.

No sabemos negociar con los monos, así que otro mono lo hace por nosotros. Pero solo sabe decidir cuándo vender mirando los **cambios de precio**: le damos una secuencia de **cuatro cambios consecutivos**, y a cada comprador le vende **la primera vez** que ve esa secuencia, al precio de ese momento. Después pasa al siguiente comprador. Si un comprador nunca muestra la secuencia, no le vende nada.

Con los precios de 123 de arriba, los cambios son −3, 6, −1, −1, 0, 2, −2, 0, −2. El primer 6 llega después de solo dos cambios, así que no se puede pedir; pero el segundo 6 llega después de −1, −1, 0, 2: con esa secuencia, el mono vendería a 6.

Solo se puede dar **una secuencia**, la misma para todos los compradores, y cada uno genera 2000 números nuevos (2000 cambios).

#### Ejemplo

Con los compradores 1, 2, 3 y 2024, la mejor secuencia es −2, 1, −1, 3: el mono vende a 7, a 7, nunca (el comprador 3 no la muestra) y a 9. En total, **23** bananas.

#### Pregunta

Eligiendo la mejor secuencia de cuatro cambios, ¿cuántas bananas se consiguen como máximo?

### Solución

#### Recorrer a cada comprador una sola vez

Cada cambio va de −9 a 9, así que hay 19<sup>4</sup> = **130321** secuencias posibles. Probar cada una recorriendo a los 2020 compradores serían unos 500 mil millones de pasos.

Pero se puede dar vuelta: recorrer a cada comprador **una sola vez**. En cada precio, los últimos cuatro cambios forman una secuencia; si es la **primera vez** que ese comprador la muestra, ese es el precio al que el mono le vendería con esa secuencia. Se suma a esa secuencia en un acumulador común a todos los compradores. Al final, el acumulador dice cuántas bananas da cada secuencia, y la respuesta es el máximo.

#### Acumulador en un arreglo

Cada secuencia se **codifica** como un número en base 19 (cada cambio corrido a 0–18), así que el acumulador es un arreglo de 130321 posiciones. El código se actualiza en cada paso descartando el cambio más viejo: multiplicar por 19, sumar el nuevo y quedarse con el resto módulo 19<sup>4</sup>.

Para saber si un comprador ya mostró una secuencia no hace falta un conjunto por comprador: un segundo arreglo guarda el **último comprador** que mostró cada secuencia. Si es el actual, ya se contó.

```java
// Dia22.java — bananasPorSecuencia()
for (int i = 1; i <= nuevos; i++) {
    secreto = siguiente(secreto);
    int nuevoPrecio = precio(secreto);
    secuencia = (secuencia * VALORES_DE_CAMBIO + (nuevoPrecio - precio + 9)) % SECUENCIAS;
    precio = nuevoPrecio;
    if (i >= LARGO_SECUENCIA && ultimoComprador[secuencia] != comprador) {
        ultimoComprador[secuencia] = comprador;
        bananas[secuencia] += precio;
    }
}
```

La condición `i >= LARGO_SECUENCIA` evita contar las primeras ventanas, que todavía no tienen cuatro cambios. Son unos 4 millones de pasos en total y termina en unos 50 ms.

#### Verificación

- Prueba con el ejemplo (23), y con los diez primeros **precios** de 123.
- Con los primeros precios de 123, la secuencia −1, −1, 0, 2 vende a 6, y es la mejor.
- Con −2, 1, −1, 3, una búsqueda directa en los precios de cada comprador vende a 7, a 7, nunca y a 9, como dice el enunciado, y el acumulador da 23.
- Fuerza bruta: con cinco compradores, un mapa por comprador (lista de cuatro cambios → primer precio, armado con `putIfAbsent`) coincide con el acumulador en **las 130321 secuencias**, incluidas las que valen 0.
- `codificar` rechaza cambios fuera de rango y cantidades distintas de cuatro.
- Con el input real, un script independiente en Python usa un diccionario con **tuplas** de cambios y un conjunto de vistas por comprador. Obtiene el mismo resultado, con la secuencia 1, 0, −2, 2.

**Resultado con el input real: 1938**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma del número secreto 2000 de cada comprador | Máximo de bananas con una sola secuencia de cuatro cambios |
| **Enfoque** | Simulación directa del generador (xorshift) | Acumulador por secuencia (base 19) recorriendo cada comprador una vez |
| **Complejidad** | O(compradores × 2000) | O(compradores × 2000 + 19⁴) |
| **Resultado** | 17163502021 | 1938 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia22.java`
