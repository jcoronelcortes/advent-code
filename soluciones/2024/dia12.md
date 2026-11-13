# Año 2024 — Día 12: Grupos de Jardín

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/12](https://adventofcode.com/2024/day/12). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Buscamos al Historiador Jefe cerca de un jardinero y su enorme granja. Unos elfos nos piden ayuda: quieren cercar cada **región** de parcelas, pero no saben cuánta cerca necesitan ni cuánto les va a costar. Nos dan un mapa de las parcelas (el input).

Cada parcela tiene un solo tipo de planta, indicado con una letra. Las parcelas con la **misma planta** que se tocan **horizontal o verticalmente** forman una región:

```
AAAA
BBCD
BBCC
EEEC
```

Aquí hay cinco plantas (A, B, C, D y E), cada una en una sola región.

Para cada región importan dos medidas:

- **Área:** cuántas parcelas tiene. A, B y C tienen área 4, E tiene 3 y D tiene 1.
- **Perímetro:** cada parcela es un cuadrado de 4 lados, y el perímetro cuenta los lados de sus parcelas que **no tocan** a otra parcela de la misma región. A y C tienen perímetro 10, B y E tienen 8, y la D suelta tiene 4.

Una misma planta puede formar **varias regiones separadas**, y una región puede quedar **dentro de otra**:

```
OOOOO
OXOXO
OOOOO
OXOXO
OOOOO
```

Hay cinco regiones: todas las O juntas, y cada X sola. Cada X tiene área 1 y perímetro 4. La región de O tiene área 21, y su perímetro es 20 por el borde de afuera más 4 por cada X que rodea: **36**.

El **precio** de cercar una región es su **área por su perímetro**, y el precio total es la suma de todas las regiones.

#### Ejemplo

El primer mapa cuesta 4·10 + 4·8 + 4·10 + 1·4 + 3·8 = **140**, y el de las X, 21·36 + 4·(1·4) = **772**.

En un mapa más grande:

```
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
```

hay 11 regiones (dos de C y dos de I), y el precio total es **1930**.

#### Pregunta

¿Cuánto cuesta cercar todas las regiones del mapa?

### Solución

#### El input real

Es un mapa de 140 × 140 con las 26 letras, que forman **619 regiones**: hay muchas regiones distintas con la misma planta.

#### Separar las regiones con una BFS

Se recorren las parcelas en orden de lectura. Cada una que todavía no está en ninguna región inicia una nueva: una BFS (`BFS.explorar`) que solo avanza a vecinos con la misma planta alcanza exactamente las parcelas de esa región.

```java
// Dia12.java
static List<Region> regiones(Grilla mapa) {
    List<Region> regiones = new ArrayList<>();
    Set<Punto> asignadas = new HashSet<>();
    for (Punto inicio : mapa.puntos()) {
        if (asignadas.contains(inicio)) continue;
        Set<Punto> parcelas = BFS.explorar(inicio, parcela -> mismaPlanta(mapa, parcela)).keySet();
        asignadas.addAll(parcelas);
        regiones.add(new Region(mapa.obtener(inicio), parcelas));
    }
    return regiones;
}
```

Así, las dos regiones de C del ejemplo grande quedan separadas solas: la BFS desde la primera C nunca llega a la otra.

#### El perímetro, parcela por parcela

Cada lado de una parcela es parte del perímetro si del otro lado **no hay una parcela de la misma región**. Así que, por cada parcela, se cuentan los vecinos que no están en el conjunto de parcelas de la región:

```java
// Dia12.java — Region.perimetro()
int perimetro = 0;
for (Punto parcela : parcelas) {
    for (Punto vecino : parcela.vecinos()) {
        if (!parcelas.contains(vecino)) perimetro++;
    }
}
return perimetro;
```

No hace falta el mapa: un vecino fuera del mapa tampoco está en el conjunto, así que los lados contra el borde cuentan solos. Esto también cubre las regiones dentro de otras: el lado entre una O y una X cuenta para el perímetro de las dos, porque cada una tiene a la otra afuera de su conjunto.

#### Verificación

- Prueba con los tres ejemplos del enunciado (140, 772 y 1930).
- El área y el perímetro de **cada región** de los tres ejemplos, en el orden en que los da el enunciado, que coincide con el orden de lectura. Incluye las dos regiones de C y las dos de I del ejemplo grande, y la O que rodea a las X.
- Con el input real, un script independiente en Python usa otro método: une las regiones con **union-find** y calcula el perímetro **recorriendo los bordes** del mapa. Cada borde entre dos regiones distintas suma 1 a cada lado, y cada borde del contorno suma 1. Obtiene el mismo resultado.

**Resultado con el input real: 1488414**

---

## Parte 2

### Enunciado

Por suerte, los elfos piden tanta cerca que les toca un **descuento por volumen**: el precio de una región ya no usa el perímetro, sino su **cantidad de lados**. Cada tramo **recto** de cerca cuenta como un lado, sin importar lo largo que sea.

El precio de una región pasa a ser su **área por su cantidad de lados**.

#### Ejemplo

En el primer mapa (`AAAA`, `BBCD`, …), las regiones A, B, D y E tienen 4 lados cada una, pero la C, con su forma escalonada, tiene **8**. Los precios son 16, 16, 32, 4 y 12, que suman **80**. El mapa de las O y las X cuesta **436**.

Una región con forma de E:

```
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
```

tiene área 17 y 12 lados (precio 204), y con las dos regiones de X el total es **236**.

Un caso delicado:

```
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
```

Las dos regiones de B tienen 4 lados cada una. La A tiene 4 lados por fuera y **8 por dentro**: 12 en total, y el mapa cuesta **368**. Hay que tener cuidado donde las dos B se tocan **en diagonal**: cada tramo de cerca tiene un lado de adentro y uno de afuera, así que la cerca **no** sigue derecho a través de ese punto.

En el ejemplo grande, el nuevo precio total es **1206**.

#### Pregunta

Con el descuento, ¿cuánto cuesta cercar todas las regiones del mapa?

### Solución

#### Contar esquinas en vez de lados

Seguir el borde de una región y contar dónde dobla es engorroso, sobre todo con huecos y con regiones que se tocan en diagonal. Pero el borde de una región es un polígono (o varios, uno por cada hueco) con lados solo horizontales y verticales, y en un polígono así **hay tantos lados como esquinas**: cada lado empieza en una esquina y termina en la siguiente.

Las esquinas, en cambio, se cuentan parcela por parcela. Cada parcela tiene 4 rincones, uno por cada par de direcciones perpendiculares (arriba y derecha, derecha y abajo, …). En un rincón hay una esquina si:

- **convexa:** ninguno de los dos vecinos es de la región (la punta de un saliente), o
- **cóncava:** los dos vecinos son de la región, pero la parcela en diagonal entre ellos no (la parte de adentro de un codo).

```java
// Dia12.java — Region.lados()
for (Punto parcela : parcelas) {
    for (Direccion direccion : Direccion.values()) {
        Direccion perpendicular = direccion.giroDerecha();
        boolean uno = parcelas.contains(parcela.mover(direccion));
        boolean otro = parcelas.contains(parcela.mover(perpendicular));
        boolean diagonal = parcelas.contains(parcela.mover(direccion).mover(perpendicular));
        if ((!uno && !otro) || (uno && otro && !diagonal)) esquinas++;
    }
}
```

Cada esquina se cuenta **una sola vez**:

- Una esquina convexa solo la ve la parcela de la punta.
- En una cóncava, las tres parcelas de la región que rodean el rincón lo ven distinto, y solo la del medio (la que tiene a los dos vecinos dentro) cumple la condición.

El toque en diagonal también sale bien. Donde dos B se tocan por una punta, las cuatro parcelas alrededor de ese punto (dos B y dos A) tienen **cada una** su esquina convexa: cada parcela tiene como vecinos en ese rincón a dos de la otra región. Son 2 esquinas para las B (una cada una) y 2 para la A, así que la cerca dobla ahí en vez de seguir derecho, como pide el enunciado.

La pertenencia se pregunta al conjunto de parcelas de la región y no a la letra del mapa. Así, una parcela de la misma planta que está en otra región nunca se confunde con una de esta.

#### Precio de las dos partes

Las dos partes solo cambian la medida de la cerca, así que comparten `precioTotal`:

```java
// Dia12.java
static long precioTotal(Grilla mapa, ToIntFunction<Region> medida) {
    return regiones(mapa).stream()
            .mapToLong(region -> (long) region.area() * medida.applyAsInt(region))
            .sum();
}
```

con `Region::perimetro` en la parte 1 y `Region::lados` en la parte 2. Las dos medidas preguntan lo mismo, si una casilla es de la región, al conjunto de parcelas.

#### Verificación

- Prueba con los cinco ejemplos del enunciado (80, 436, 236, 368 y 1206).
- La cantidad de lados de **cada región** de los ejemplos, comparada con la que da el enunciado, incluidas la C escalonada (8), la E (12) y las 11 regiones del ejemplo grande.
- El toque en diagonal: la A que rodea a las dos B tiene 12 lados y cada B tiene 4.
- Huecos: la O con cuatro X adentro tiene 4 lados por fuera y 4 por cada hueco, 20 en total.
- Con el input real, un script independiente en Python usa otro método: **junta los bordes en tramos rectos**. Agrupa los bordes de cada región por orientación, por el lado hacia el que mira la cerca y por la fila o columna en que están, y cuenta los tramos de posiciones consecutivas. Separar por el lado hacia el que mira es lo que resuelve el toque en diagonal. Obtiene el mismo resultado.

**Resultado con el input real: 911750**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de área × perímetro de cada región | Suma de área × cantidad de lados |
| **Enfoque** | Una BFS por región; perímetro = vecinos fuera de la región, por parcela | Lados = esquinas, contadas por parcela (convexas y cóncavas) |
| **Complejidad** | O(parcelas) | O(parcelas) |
| **Resultado** | 1488414 | 911750 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia12.java`
