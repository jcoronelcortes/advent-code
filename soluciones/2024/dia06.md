# Año 2024 — Día 6: Patrulla del Guardia

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/6](https://adventofcode.com/2024/day/6). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los Historiadores vuelven a usar su aparato y nos llevan al laboratorio de prototipos de trajes del Polo Norte... ¡en el año 1518! Hay que tener cuidado con las paradojas temporales y evitar a cualquiera de esa época mientras buscan al Jefe, pero un **guardia** patrulla esta parte del laboratorio. Si predecimos por dónde va a pasar, los Historiadores pueden buscar tranquilos.

Hacemos un mapa de la situación (el input):

```
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
```

- `^` es la posición actual del guardia, que mira **hacia arriba**.
- `#` es un **obstáculo** (cajas, escritorios, reactores alquímicos...).

Los guardias de 1518 siguen un protocolo de patrulla muy estricto, que repiten una y otra vez:

- si hay algo **justo enfrente**, giran **90° a la derecha**;
- si no, dan **un paso hacia adelante**.

Así siguen hasta salir del área del mapa.

#### Ejemplo

El guardia sube hasta chocar con un obstáculo, gira a la derecha, sigue derecho hasta el siguiente, gira otra vez, y así hasta que finalmente sale del mapa. Las posiciones por las que pasa, incluida la inicial, son las marcadas con `X`:

```
....#.....
....XXXXX#
....X...X.
..#.X...X.
..XXXXX#X.
..X.X.X.X.
.#XXXXXXX.
.XXXXXXX#.
#XXXXXXX..
......#X..
```

En el ejemplo, el guardia visita **41** posiciones distintas.

#### Pregunta

Prediciendo el recorrido del guardia, ¿cuántas posiciones distintas visita antes de salir del mapa?

### Solución

#### El input real

Es un mapa de 130 × 130 con 814 obstáculos y un solo guardia.

#### Simular el protocolo

Es una simulación directa, y es el primer día que usa la utilidad `Direccion`: el guardia empieza mirando `ARRIBA`, avanza con `Punto.mover(direccion)` y gira con `direccion.giroDerecha()`. Las posiciones visitadas se guardan en un conjunto, así que pasar dos veces por el mismo lugar cuenta una sola vez.

```java
// Dia06.java — patrullar()
while (true) {
    visitadas.add(posicion);
    if (!estados.add(new Estado(posicion, direccion))) return new Recorrido(visitadas, true);

    Punto adelante = posicion.mover(direccion);
    if (!mapa.estaDentro(adelante)) return new Recorrido(visitadas, false);
    if (mapa.obtener(adelante) == OBSTACULO || adelante.equals(obstaculoExtra)) {
        direccion = direccion.giroDerecha();
    } else {
        posicion = adelante;
    }
}
```

(En la parte 1 `obstaculoExtra` es `null`, así que solo cuentan los obstáculos del mapa; la parte 2 lo usa para probar un obstáculo nuevo.)

Girar y avanzar son pasos **separados**: después de girar, el guardia vuelve a mirar qué tiene enfrente. Si justo ahí también hay un obstáculo, gira otra vez sin moverse.

#### Detectar ciclos

El enunciado dice que el guardia termina saliendo del mapa, pero eso no se puede dar por sentado en código: con otro mapa podría quedar dando vueltas para siempre y el programa no terminaría. El guardia es **determinista**: su próximo movimiento depende solo de su posición y su dirección. Si vuelve a un mismo **estado** (misma posición *y* misma dirección), repetirá exactamente el mismo recorrido para siempre. Por eso la simulación guarda los estados y se detiene si alguno se repite. Pasar por la misma posición con otra dirección, en cambio, no es un ciclo (en el ejemplo pasa varias veces).

En la parte 1 un ciclo sería un input inválido, así que se lanza una excepción. En la parte 2, en cambio, provocar ciclos es justamente el objetivo.

#### Verificación

- Prueba con el ejemplo (41), y comparando el conjunto de posiciones visitadas con las `X` del diagrama del enunciado.
- Casos simples: sin obstáculos sale derecho; un obstáculo enfrente lo hace girar; rodeado por tres lados gira dos veces sin moverse y sale por el cuarto.
- Un guardia encerrado en un ciclo se detecta y se informa.
- Con el input real, un script independiente en Python con otra representación (posición y dirección como **números complejos**, donde girar 90° es multiplicar por `i`) obtiene el mismo resultado.

**Resultado con el input real: 5409**

---

## Parte 2

### Enunciado

Mientras los Historiadores trabajan esquivando la ruta del guardia, tomamos prestado su aparato y viajamos por los últimos meses para registrar el estado del puesto de guardia cada noche. Al volver, los Historiadores explican que el área de patrulla es demasiado grande para buscar sin que los descubran.

Por suerte, están bastante seguros de que agregar **un solo obstáculo nuevo** no causará una paradoja temporal. Quieren ponerlo de modo que el guardia quede **atrapado en un bucle**, dejando libre el resto del laboratorio. Para elegir la opción con menos riesgo de paradoja, quieren conocer **todas** las posiciones posibles para ese obstáculo. No puede ir en la posición inicial del guardia, porque él está ahí y lo notaría.

#### Ejemplo

En el mapa del ejemplo hay solo **6** posiciones donde un obstáculo nuevo deja al guardia en un bucle. Por ejemplo, una imprenta justo al lado de su posición inicial, o una pila de prototipos fallidos abajo a la derecha. En coordenadas (columna, fila): `(3,6)`, `(6,7)`, `(7,7)`, `(1,8)`, `(3,8)` y `(7,9)`.

#### Pregunta

Agregando un solo obstáculo nuevo para atrapar al guardia en un bucle, ¿cuántas posiciones distintas se podrían elegir para ese obstáculo?

### Solución

#### Probar cada posición candidata

La parte 1 ya detectaba ciclos (un estado de posición y dirección que se repite), así que basta con probar cada posición posible del obstáculo nuevo y contar en cuántas el guardia queda en un ciclo.

#### Clave: solo importan las celdas del recorrido original

Hay ~16 000 celdas libres, pero un obstáculo nuevo solo cambia algo si el guardia **choca** con él. Si se pone en una celda por la que el guardia nunca pasaba, el recorrido es exactamente el mismo y el guardia sale del mapa como antes. Así que alcanza con probar las ~5400 celdas del **recorrido original** (menos la posición inicial, que el enunciado excluye).

#### El obstáculo extra como parámetro

En vez de copiar la grilla y escribirle un `#` en cada una de las miles de pruebas, el obstáculo nuevo se pasa como parámetro y se trata igual que a los del mapa. Así lo hace `patrullar`, y también el simulador rápido de más abajo:

```java
// Dia06.java — patrullar()
if (mapa.obtener(adelante) == OBSTACULO || adelante.equals(obstaculoExtra)) {
```

#### Optimización: empezar donde cambia algo, y simular con arreglos

Con `patrullar` tal cual, son ~5400 simulaciones completas de miles de pasos cada una, con conjuntos de objetos `Estado`: tardaba ~1 s. Dos ideas lo bajan a ~11 ms:

**1. Empezar justo antes del obstáculo.** Hasta que el guardia intenta entrar por primera vez a la celda del obstáculo nuevo, su recorrido es **idéntico** al original. Así que, en una sola pasada del recorrido original, se guarda para cada celda el estado del guardia justo antes de entrar a ella por primera vez (`antesDeEntrar`), y cada prueba arranca desde ahí:

```java
// Dia06.java — parte2()
Grilla mapa = Grilla.desdeLineas(lineas);
DetectorDeCiclos detector = new DetectorDeCiclos(mapa);
long posiciones = antesDeEntrar(mapa).entrySet().stream()
        .filter(entrada -> detector.quedaAtrapado(entrada.getValue(), entrada.getKey()))
        .count();
```

**2. Un simulador con arreglos (`DetectorDeCiclos`).** Para la parte 2 no hace falta el conjunto de posiciones visitadas, solo saber si hay ciclo:

- los obstáculos se guardan en un `boolean[]` indexado por celda;
- solo se guardan los estados en los que el guardia **gira**. Todo ciclo incluye algún giro, porque en línea recta el guardia termina saliendo del mapa; así que si hay ciclo, se repite un estado de giro;
- los estados vistos se marcan con el número de prueba en una tabla de enteros que nunca se vacía: un estado "está visto" solo si su marca es la de la prueba actual.

```java
// Dia06.java — DetectorDeCiclos.quedaAtrapado()
if (obstaculo[siguiente] || siguiente == extra) {
    int estado = (y * ancho + x) * DIRECCIONES + direccion.ordinal();
    if (vistoEnGeneracion[estado] == generacion) return true;
    vistoEnGeneracion[estado] = generacion;
    direccion = direccion.giroDerecha();
} else {
```

`patrullar` sigue siendo la versión de referencia, más fácil de leer, y la usa la parte 1. Las pruebas comparan las dos versiones.

#### Verificación

- Prueba con el ejemplo (6), y comprobando que las 6 posiciones `O` de los diagramas del enunciado provocan un ciclo.
- El simulador rápido da lo mismo que `patrullar` en cada celda del recorrido del ejemplo, y un mismo detector reutilizado entre pruebas no arrastra estados de una prueba a la otra. `antesDeEntrar` cubre todo el recorrido salvo la posición inicial.
- Con el input real, un chequeo puntual comparó `patrullar` con el simulador rápido en las 5408 celdas candidatas: coinciden en todas (2022 con ciclo).
- En el ejemplo, probar **todas** las celdas libres da lo mismo que probar solo el recorrido original; un obstáculo fuera del recorrido no cambia nada.
- Con el input real, un script independiente en Python usa **otro criterio** para detectar el bucle: en vez de guardar estados, considera que el guardia está atrapado si da más pasos que `4 × celdas` (la cantidad de estados posibles). Obtiene el mismo resultado.

**Resultado con el input real: 2022**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Posiciones distintas que visita el guardia | Posiciones de un obstáculo nuevo que lo atrapan en un bucle |
| **Enfoque** | Simular el protocolo con `Direccion`, detectando ciclos por (posición, dirección) | Probar un obstáculo en cada celda del recorrido original, desde justo antes de entrar, con un simulador de arreglos |
| **Complejidad** | O(pasos del recorrido) | O(celdas del recorrido × pasos) |
| **Resultado** | 5409 | 2022 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia06.java`
