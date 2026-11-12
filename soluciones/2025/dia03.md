# Año 2025 — Día 3: Vestíbulo

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/3](https://adventofcode.com/2025/day/3). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Bajamos una escalera corta hasta el enorme **vestíbulo** y pasamos el control de seguridad. Pero los ascensores principales tienen una luz roja: una subida de tensión los quemó. La elfa que intenta repararlos sugiere bajar por la **escalera mecánica** hasta el departamento de impresión, aunque también está apagada; por suerte no se quemó, solo le falta energía.

Cerca hay **baterías** de emergencia para alimentar la escalera. Cada una tiene un **voltaje** del 1 al 9. Las baterías están organizadas en **bancos**: cada línea del input es un banco, y cada dígito es una batería.

```
987654321111111
811111111111119
234234234234278
818181911112111
```

En cada banco hay que encender **exactamente dos** baterías. El voltaje que produce el banco es el número formado por los dígitos de las baterías encendidas, **en el orden en que están**: las baterías no se pueden reordenar. Por ejemplo, en el banco `12345`, encender la batería 2 y la 4 produce `24`.

El voltaje total es la suma del **máximo** voltaje que puede producir cada banco.

#### Ejemplo

| Banco | Baterías encendidas | Máximo |
|-------|---------------------|--------|
| `987654321111111` | las dos primeras | 98 |
| `811111111111119` | el 8 y el 9 | 89 |
| `234234234234278` | las dos últimas | 78 |
| `818181911112111` | el 9 y el 2 | 92 |

El voltaje total del ejemplo es `98 + 89 + 78 + 92 =` **357**.

#### Pregunta

Buscando el máximo voltaje de cada banco del input, ¿cuál es el voltaje total?

### Solución

#### La idea obvia: probar todas las parejas

Con bancos de 100 dígitos hay `100 · 99 / 2 = 4950` parejas por banco, así que probarlas todas funcionaría. Pero existe una solución más simple y que además sirve para encender **cualquier cantidad** de baterías, no solo dos.

#### Clave: el primer dígito manda

Entre dos números con la misma cantidad de dígitos, gana siempre el que tiene el **primer dígito más grande**, sin importar los demás (`90 > 89`). Así que conviene elegir los dígitos **de izquierda a derecha, cada uno lo más grande posible**: es un **algoritmo voraz**.

Hay dos detalles:

1. **Dejar lugar para los dígitos que faltan.** El primer dígito no puede ser la última batería, porque no quedaría ninguna para el segundo. En general, si hay que elegir `k` dígitos de un banco de largo `n`, el dígito número `i` (contando desde 0) solo puede buscarse hasta la posición `n − k + i`.
2. **Ante un empate, quedarse con la primera aparición.** Así quedan más baterías disponibles a la derecha. En `1919`, tomar el primer 9 permite que el segundo dígito también sea 9 (`99`); tomar el último 9 no dejaría nada.

```java
// Dia03.java — maximoVoltaje()
long voltaje = 0;
int desde = 0;
for (int elegidos = 0; elegidos < cantidad; elegidos++) {
    int hasta = largo - cantidad + elegidos;   // deja lugar para los dígitos que faltan

    int posicionMayor = desde;
    for (int i = desde + 1; i <= hasta; i++) {
        if (banco.charAt(i) > banco.charAt(posicionMayor)) posicionMayor = i;
    }

    voltaje = voltaje * 10 + (banco.charAt(posicionMayor) - '0');
    desde = posicionMayor + 1;
}
```

> El `>` estricto es lo que asegura quedarse con la primera aparición ante empates.
>
> Comparar `char` directamente funciona porque los dígitos `'1'..'9'` están en orden en la tabla de caracteres.

#### Verificación con `818181911112111` (n = 15, k = 2)

| Dígito | Ventana de búsqueda | Mayor encontrado | Voltaje |
|--------|--------------------|------------------|---------|
| 1° | posiciones 0..13 → `81818191111211` | `9` en la posición 6 | 9 |
| 2° | posiciones 7..14 → `11112111` | `2` en la posición 11 | 92 |

#### Verificación con `811111111111119`

| Dígito | Ventana de búsqueda | Mayor encontrado | Voltaje |
|--------|--------------------|------------------|---------|
| 1° | posiciones 0..13 → `81111111111111` (el 9 queda afuera) | `8` en la posición 0 | 8 |
| 2° | posiciones 1..14 → `11111111111119` | `9` en la posición 14 | 89 |

Este caso muestra por qué importa la ventana: el 9 es el mayor dígito, pero si fuera el primero no quedaría batería para el segundo.

#### Sumar todos los bancos

```java
// Dia03.java — sumarMaximos()
for (String banco : Utiles.lineasNoVacias(lineas)) {
    total += maximoVoltaje(banco, cantidad);
}
```

La parte 1 llama a `sumarMaximos(lineas, 2)`.

**Resultado con el input real: 17412**

> El resultado se verificó además con una fuerza bruta independiente en Python que prueba todas las parejas de cada banco. Las pruebas unitarias comparan el algoritmo voraz contra una fuerza bruta en 500 bancos aleatorios, con 1 a 4 baterías.

---

## Parte 2

### Enunciado

La escalera mecánica no se mueve: necesita más voltaje para vencer la fricción estática. La elfa aprieta el gran botón rojo que desactiva el límite de seguridad (y tiene que confirmar "sí, estoy seguro" tantas veces que perdemos la cuenta).

Ahora hay que encender **exactamente doce** baterías en cada banco. El voltaje sigue siendo el número formado por los dígitos encendidos en su orden; la única diferencia es que ahora tiene 12 cifras en vez de 2. Los bancos son los mismos de la parte 1.

#### Ejemplo

| Banco | Baterías que quedan apagadas | Máximo |
|-------|------------------------------|--------|
| `987654321111111` | algunos 1 del final | 987654321111 |
| `811111111111119` | algunos 1 | 811111111119 |
| `234234234234278` | un 2, un 3 y otro 2 cerca del principio | 434234234278 |
| `818181911112111` | algunos 1 cerca del principio | 888911112111 |

El voltaje total del ejemplo es **3121910778619**.

#### Pregunta

Encendiendo doce baterías por banco, ¿cuál es el nuevo voltaje total?

### Solución

#### Mismo algoritmo, otro número

El algoritmo voraz de la Parte 1 ya fue escrito para encender **cualquier cantidad** de baterías, así que la Parte 2 es una sola línea:

```java
// Dia03.java — parte2()
return String.valueOf(sumarMaximos(lineas, BATERIAS_PARTE_2));   // BATERIAS_PARTE_2 = 12
```

> Un voltaje de 12 dígitos es como máximo `999999999999`, que no cabe en un `int` (~2·10⁹) pero sí en un `long` (~9·10¹⁸).

#### ¿Por qué el voraz sigue siendo correcto con 12 dígitos?

Con 2 baterías se podían probar todas las parejas, pero con 12 baterías en un banco de 100 hay `C(100, 12) ≈ 10¹⁵` combinaciones: la fuerza bruta ya no es posible. El voraz sigue funcionando por la misma razón que antes: entre dos números de 12 cifras, el que tiene el primer dígito mayor gana **siempre**, sin importar los otros 11. Por eso nunca conviene sacrificar el primer dígito para mejorar los siguientes, y lo mismo vale para el segundo, el tercero, etc.

#### Verificación con `234234234234278` (n = 15, k = 12)

Como hay que encender 12 de 15, en cada paso la ventana tiene solo `15 − 12 + 1 = 4` posiciones:

| Dígito | Ventana | Mayor | Voltaje parcial |
|--------|---------|-------|-----------------|
| 1° | posiciones 0..3 → `2342` | `4` (pos. 2) | 4 |
| 2° | posiciones 3..4 → `23` | `3` (pos. 4) | 43 |
| 3° | posiciones 5..5 → `4` | `4` (pos. 5) | 434 |
| ... | a partir de acá la ventana tiene un solo dígito: se toman todos los restantes | | 434234234278 |

Se saltearon exactamente las posiciones 0, 1 y 3 (`2`, `3` y `2`), como dice el enunciado.

#### Comprobación

Para verificar el voraz sin fuerza bruta, las pruebas lo comparan con un método completamente distinto: **programación dinámica**, donde `mejor[j]` guarda el mayor número de `j` dígitos que se puede formar con lo recorrido hasta el momento. Coinciden en 300 bancos aleatorios de 100 dígitos.

**Resultado con el input real: 172681562473501**

> El resultado se verificó además con la misma programación dinámica escrita en Python.

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma del máximo voltaje encendiendo 2 baterías por banco | Lo mismo, encendiendo 12 baterías |
| **Enfoque** | Voraz: elegir de izquierda a derecha el mayor dígito que deje lugar para los restantes | El mismo voraz con `k = 12` |
| **Complejidad** | O(b · n · k), con `b` bancos de largo `n` y `k` baterías | O(b · n · k) |
| **Resultado** | 17412 | 172681562473501 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia03.java`
