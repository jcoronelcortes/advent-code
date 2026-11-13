# Año 2023 — Día 14: Plato Reflector Parabólico

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/14](https://adventofcode.com/2023/day/14). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos al lugar al que apuntaban todos los espejos: un enorme **plato reflector parabólico** pegado a una montaña. Sus espejitos están mal orientados, y cada uno cuelga de cuerdas y poleas conectadas a una plataforma de metal llena de rocas. El peso de las rocas deforma la plataforma, y eso mueve los espejos: si movemos las rocas, enfocamos el plato.

La plataforma se puede **inclinar** hacia cualquiera de los cuatro puntos cardinales. Las rocas **redondas** (`O`) ruedan hasta chocar con el borde u otra roca; las **cúbicas** (`#`) no se mueven. El input es el mapa de la plataforma, con `.` en los espacios vacíos.

Las vigas del lado norte están dañadas, así que hay que calcular la **carga** sobre ellas: cada roca redonda aporta tantas unidades como filas haya desde la suya hasta el borde sur, contando la suya (en una plataforma de 10 filas, una roca en la primera fila aporta 10). Las cúbicas no aportan.

#### Ejemplo

```
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
```

Al inclinarla hacia el norte, cada roca redonda sube por su columna hasta tocar el borde, una cúbica u otra redonda:

```
OOOO.#.O..
OO..#....#
OO..O##..O
O..#.OO...
........#.
..#....#.#
..O..#.O.O
..O.......
#....###..
#....#....
```

La carga total es **136**.

#### Pregunta

¿Cuál es la carga sobre las vigas del norte después de inclinar la plataforma hacia el norte?

### Solución

#### El input real

Una plataforma de 100 × 100 con 1972 rocas redondas y 1539 cúbicas.

#### Inclinar con un tope por columna

Cada columna se recorre de arriba hacia abajo recordando el **tope**: la primera fila libre donde caería la próxima roca redonda. Una roca cúbica deja el tope justo debajo de ella; una redonda se mueve al tope y lo baja una fila. Así cada casilla se mira una sola vez, en vez de mover las rocas de a una casilla hasta que se queden quietas:

```java
// Dia14.java — inclinarAlNorte()
for (int x = 0; x < inclinada.columnas(); x++) {
    int tope = 0;
    for (int y = 0; y < inclinada.filas(); y++) {
        char casilla = inclinada.obtener(new Punto(x, y));
        if (casilla == CUBICA) {
            tope = y + 1;
        } else if (casilla == REDONDA) {
            inclinada.establecer(new Punto(x, y), VACIO);
            inclinada.establecer(new Punto(x, tope), REDONDA);
            tope++;
        }
    }
}
```

Se trabaja sobre una copia, así que la plataforma original no cambia. Después, la carga es la suma de `filas − y` por cada roca redonda.

#### Verificación

- Prueba con el ejemplo (136).
- Inclinar el ejemplo da exactamente la plataforma que muestra el enunciado, y la original no se modifica.
- Coincide con la **fuerza bruta** de mover cada roca una casilla hacia arriba, repitiendo hasta que ninguna pueda moverse.
- Con el input real, un script independiente en Python corta cada columna en tramos separados por `#` y suma la carga de apilar las `O` de cada tramo al principio, sin mover nada, y obtiene el mismo resultado.

**Resultado con el input real: 107053**

---

## Parte 2

### Enunciado

Inclinar al norte deforma el plato, pero no lo enfoca: para eso hay que llevar las rocas hacia los bordes. El panel tiene un botón de **ciclo de giro**: cada ciclo inclina la plataforma cuatro veces, hacia el **norte**, el **oeste**, el **sur** y el **este**, en ese orden, dejando que las rocas redondas rueden todo lo que puedan en cada inclinación.

Para asegurarse de que las vigas del norte aguanten, hay que calcular la carga sobre ellas después de **mil millones** de ciclos.

#### Ejemplo

Con la misma plataforma, después de un ciclo queda:

```
.....#....
....#...O#
...OO##...
.OO#......
.....OOO#.
.O#...O#.#
....O#....
......OOOO
#...O###..
#..OO#....
```

Tras mil millones de ciclos, la carga sobre el norte es **64**.

#### Pregunta

¿Cuál es la carga sobre las vigas del norte después de mil millones de ciclos de giro?

### Solución

#### Un ciclo: inclinar al norte y rotar

En vez de escribir cuatro inclinaciones distintas, se reutiliza `inclinarAlNorte` y se **rota** la plataforma 90° en sentido horario (`Grilla.rotarDerecha`) después de cada una. Al rotar, el lado oeste queda arriba, así que la siguiente inclinación "al norte" en realidad es hacia el oeste; después vienen el sur y el este. Tras las cuatro rotaciones la plataforma vuelve a su orientación original:

```java
// Dia14.java — cicloDeGiro()
Grilla resultado = plataforma;
for (int i = 0; i < 4; i++) resultado = inclinarAlNorte(resultado).rotarDerecha();
return resultado;
```

#### Saltar los mil millones con `Ciclo`

Simular mil millones de ciclos es imposible, pero las rocas solo pueden estar en una cantidad finita de posiciones y cada ciclo es determinista, así que tarde o temprano la plataforma se repite. La utilidad `Ciclo` detecta la primera repetición y da el estado en cualquier paso descontando vueltas enteras (`Grilla` tiene `equals` y `hashCode`, y cada ciclo devuelve una grilla nueva sin modificar la anterior):

```java
// Dia14.java — parte2()
Ciclo<Grilla> ciclo = Ciclo.detectar(Grilla.desdeLineas(lineas), Dia14::cicloDeGiro);
return String.valueOf(carga(ciclo.estadoEn(CICLOS)));
```

Con el input real, la plataforma entra en un ciclo en el paso 93 y se repite cada 72 ciclos, así que basta con simular 165.

#### Verificación

- Prueba con el ejemplo (64).
- Los tres primeros ciclos de giro dan exactamente las plataformas que muestra el enunciado.
- El salto con `Ciclo` coincide con simular paso a paso los primeros 200 ciclos del ejemplo.
- Con el input real, un script independiente en Python inclina en las cuatro direcciones **sin rotar**, moviendo cada roca de a una casilla hasta que ninguna pueda moverse, detecta la repetición con un diccionario y obtiene el mismo ciclo (empieza en 93 y mide 72) y el mismo resultado.

**Resultado con el input real: 88371**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Carga al norte tras inclinar al norte | Carga al norte tras mil millones de ciclos de giro |
| **Enfoque** | Un tope por columna: cada roca cae al primer lugar libre | Inclinar al norte y rotar 4 veces; saltar con `Ciclo` |
| **Complejidad** | O(filas × columnas) | O((inicio + largo del ciclo) × filas × columnas) |
| **Resultado** | 107053 | 88371 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia14.java`
