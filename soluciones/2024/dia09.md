# Año 2024 — Día 9: Desfragmentador de Disco

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/9](https://adventofcode.com/2024/day/9). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Otro botonazo y aparecemos en los pasillos conocidos de unos anfípodos amigables, cada uno con su minisubmarino personal. Mientras los Historiadores salen disparados a buscar al Jefe (sobre todo chocando contra las paredes), vemos a un anfípodo en un rincón peleando con su computadora: quiere **compactar** todos los archivos del disco para tener más espacio libre contiguo, pero su programa no funciona.

Nos muestra el **mapa del disco** (el input), una sola línea larga de dígitos en formato denso:

```
2333133121414131402
```

Los dígitos **alternan** entre el largo de un **archivo** y el largo de un **espacio libre**. Por ejemplo, `12345` es un archivo de 1 bloque, 2 bloques libres, un archivo de 3 bloques, 4 libres y un archivo de 5; y `90909` son tres archivos de 9 bloques seguidos, sin espacio entre ellos.

Cada archivo tiene un **ID** según el orden en que aparece, empezando en 0. Dibujando cada bloque con el ID de su archivo, o `.` si está libre, `12345` es:

```
0..111....22222
```

y el ejemplo grande es:

```
00...111...2...333.44.5555.6666.777.888899
```

El anfípodo quiere mover los bloques de archivo **de a uno**, desde el **final** del disco al bloque libre **de más a la izquierda**, hasta que no queden huecos entre los bloques de archivo:

```
0..111....22222
02.111....2222.
022111....222..
0221112...22...
02211122..2....
022111222......
```

Al final hay que calcular el **checksum** del sistema de archivos: la suma, para cada bloque, de su **posición** multiplicada por el **ID** del archivo que contiene (la primera posición es 0; los bloques libres no suman).

#### Ejemplo

El ejemplo grande, compactado, queda:

```
0099811188827773336446555566..............
```

Su checksum empieza con `0·0 + 1·0 + 2·9 + 3·9 + 4·8 + ...` y da **1928**.

#### Pregunta

Compactando el disco del anfípodo con el proceso que pidió, ¿cuál es el checksum resultante?

### Solución

#### El input real

Es una sola línea de 19 999 dígitos: 10 000 archivos (IDs 0 a 9999) y 95 177 bloques en total. El enunciado advierte que es fácil pegarla mal, así que se verificó que llegó completa (solo dígitos, sin saltos de línea, y una cantidad impar, es decir, que termina en un archivo), y el código valida que sean solo dígitos.

#### Expandir el mapa a bloques

Primero se pasa del formato denso a un arreglo con un valor por bloque: el ID del archivo, o `-1` (`LIBRE`). Las posiciones pares del mapa son archivos (con ID `i / 2`) y las impares, espacio libre:

```java
// Dia09.java — expandir()
int largo = mapa.charAt(i) - '0';
int valor = i % 2 == 0 ? i / 2 : LIBRE;     // posiciones pares: archivo con ID i/2
Arrays.fill(bloques, posicion, posicion + largo, valor);
posicion += largo;
```

Con 95 177 bloques, el arreglo es chico.

#### Compactar con dos punteros

Buscar el primer hueco desde el principio en cada movimiento sería cuadrático. En cambio, dos punteros recorren el disco una sola vez: `libre` avanza desde la izquierda hasta el próximo hueco, `archivo` retrocede desde la derecha hasta el próximo bloque de archivo, y se intercambian mientras no se crucen:

```java
// Dia09.java — compactarPorBloques()
while (true) {
    while (libre < bloques.length && bloques[libre] != LIBRE) libre++;
    while (archivo >= 0 && bloques[archivo] == LIBRE) archivo--;
    if (libre >= archivo) return;
    bloques[libre] = bloques[archivo];
    bloques[archivo] = LIBRE;
}
```

El checksum se calcula en `long`: posiciones de hasta ~95 000 por IDs de hasta 9999 superan el rango de un `int`.

#### Verificación

- Prueba con el ejemplo (1928).
- Expandir `12345`, el ejemplo grande y `90909` da exactamente los dibujos del enunciado.
- Compactar `12345` y el ejemplo grande da exactamente el último paso de los dibujos del enunciado.
- Checksum con bloques libres, y validación de que el mapa tenga solo dígitos.
- Con el input real, un script independiente en Python usa otra observación: al terminar, quedan llenas exactamente las primeras `K` posiciones (`K` = total de bloques de archivo); en cada una queda el bloque original si era de archivo, o el siguiente bloque tomado desde el final si era un hueco. Calcula el checksum sin simular los movimientos y obtiene el mismo resultado.

**Resultado con el input real: 6385338159127**

---

## Parte 2

### Enunciado

Al terminar quedan claras dos cosas: el disco tiene mucho más espacio libre contiguo, como quería el anfípodo, pero la computadora anda mucho más lenta. Quizás fragmentar todos esos archivos no fue buena idea.

El anfípodo ya tiene un plan nuevo: en vez de mover bloques sueltos, mover **archivos enteros**.

- Cada archivo se intenta mover **una sola vez**, en orden de **ID decreciente** (empezando por el de ID más alto).
- Va al tramo de bloques libres **de más a la izquierda** donde entre **completo**.
- Si no hay ningún tramo libre **a su izquierda** donde entre, el archivo **no se mueve**.

El checksum se calcula igual que antes.

#### Ejemplo

El ejemplo grande ahora avanza distinto:

```
00...111...2...333.44.5555.6666.777.888899
0099.111...2...333.44.5555.6666.777.8888..
0099.1117772...333.44.5555.6666.....8888..
0099.111777244.333....5555.6666.....8888..
00992111777.44.333....5555.6666.....8888..
```

y su checksum es **2858**.

#### Pregunta

Empezando de nuevo y compactando el disco con este método, ¿cuál es el checksum resultante?

### Solución

#### Tramos en vez de bloques

Como ahora los archivos se mueven completos, el disco se representa por **tramos**: para cada archivo, dónde empieza y cuánto mide; para cada hueco, lo mismo (record `Disco`).

#### Mover cada archivo una vez

Para cada archivo, del ID más alto al más bajo, se recorren los tramos libres de izquierda a derecha (solo los que empiezan antes que el archivo) hasta encontrar el primero donde entra. El archivo se muda al inicio de ese tramo, y lo que sobra del tramo sigue libre:

```java
// Dia09.java — Disco.compactarPorArchivos()
for (int id = inicioArchivo.length - 1; id >= 0; id--) {
    int largo = largoArchivo[id];
    for (int tramo = 0; tramo < inicioLibre.length && inicioLibre[tramo] < inicioArchivo[id]; tramo++) {
        if (largoLibre[tramo] >= largo) {
            inicioArchivo[id] = inicioLibre[tramo];
            inicioLibre[tramo] += largo;     // lo que sobra del tramo sigue libre
            largoLibre[tramo] -= largo;
            break;
        }
    }
}
```

#### Clave: el hueco que deja un archivo nunca se usa

Al mudarse, un archivo deja un hueco donde estaba, pero no hace falta registrarlo. Los archivos que faltan mover tienen IDs **menores**, están **más a la izquierda** y solo pueden moverse **hacia la izquierda**: ese hueco siempre les queda a la derecha.

Hay un caso sutil que muestra que cada archivo se intenta **una sola vez**: en `0.1.22`, el archivo 2 no entra en ningún hueco de 1 bloque y se queda. Después el archivo 1 se muda al primer hueco y deja libre un tramo de 2 justo antes del 2, pero el 2 ya tuvo su turno: el resultado es `01..22`, no `0122..`.

Son ~10 000 archivos y ~10 000 tramos libres, pero cada búsqueda se corta en cuanto encuentra lugar o llega a la posición del archivo: tarda ~18 ms.

#### Verificación

- Prueba con el ejemplo (2858), y comparando el disco compactado con el último paso del dibujo del enunciado.
- Un archivo que no entra en ningún hueco de su izquierda no se mueve, y nunca se mueve hacia la derecha.
- Cada archivo se intenta una sola vez (`0.1.22` → `01..22`), confirmado también con una simulación literal del enunciado, bloque por bloque.
- Con el input real, un script independiente en Python usa otro algoritmo: **10 montículos**, uno por cada largo de tramo libre (0 a 9), ordenados por inicio. Para un archivo de largo `L` mira el primer tramo de los montículos de largo `L` a 9 y elige el que empieza más a la izquierda; lo que sobra va al montículo de su nuevo largo. Obtiene el mismo resultado.

**Resultado con el input real: 6415163624282**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Checksum después de compactar moviendo bloques sueltos | Lo mismo, moviendo archivos enteros |
| **Enfoque** | Expandir a un arreglo de bloques y compactar con dos punteros | Tramos; cada archivo al primer tramo libre donde entra |
| **Complejidad** | O(bloques) | O(archivos × tramos) en el peor caso |
| **Resultado** | 6385338159127 | 6415163624282 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia09.java`
