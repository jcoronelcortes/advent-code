# Año 2022 — Día 2: Piedra, Papel o Tijera

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2022/day/2](https://adventofcode.com/2022/day/2). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los elfos arman el campamento en la playa, y para decidir qué carpa queda más cerca de la comida se juega un torneo gigante de **piedra, papel o tijera**. En cada ronda, los dos jugadores eligen a la vez una figura: la piedra le gana a la tijera, la tijera al papel y el papel a la piedra; si eligen lo mismo, es empate.

Un elfo nos da una **guía de estrategia cifrada** (el input): una ronda por línea, con lo que va a jugar el rival en la primera columna (`A` piedra, `B` papel, `C` tijera). Antes de que explique la segunda columna se lo llevan, así que suponemos que es lo que hay que jugar: `X` piedra, `Y` papel y `Z` tijera.

El puntaje de una ronda es el de la figura elegida (1 por piedra, 2 por papel y 3 por tijera) más el del resultado (0 si se pierde, 3 si se empata y 6 si se gana). El puntaje total es la suma de todas las rondas.

#### Ejemplo

```
A Y
B X
C Z
```

En la primera ronda el rival juega piedra y nosotros papel: ganamos, 2 + 6 = 8. En la segunda perdemos con piedra contra papel: 1 + 0 = 1. La tercera es empate con tijera: 3 + 3 = 6. El total es **15**.

#### Pregunta

¿Qué puntaje total se obtiene siguiendo la guía al pie de la letra?

### Solución

#### El input real

2500 rondas, con solo las 9 combinaciones posibles de las dos columnas.

#### Ganar es estar un lugar adelante

Las figuras van en un `enum` en el orden piedra, papel, tijera. En ese orden **cada figura le gana a la anterior**, en círculo: el papel a la piedra, la tijera al papel y la piedra a la tijera. Entonces el resultado sale de la diferencia de posiciones módulo 3:

```java
// Dia02.java — Figura.contra()
return switch (Math.floorMod(ordinal() - rival.ordinal(), 3)) {
    case 0 -> Resultado.EMPATE;
    case 1 -> Resultado.VICTORIA;
    default -> Resultado.DERROTA;
};
```

`Math.floorMod` da siempre un resto entre 0 y 2, aunque la diferencia sea negativa (con `%`, piedra menos tijera daría −2). Los puntos de cada figura son su posición más 1, y los de cada resultado los guarda el `enum Resultado`.

La segunda columna se lee como un número de 0 a 2 sin interpretarlo, y la parte 1 lo convierte en la figura que hay que jugar. Una línea que no sea una letra de `A` a `C`, un espacio y una de `X` a `Z` falla con un mensaje claro.

#### Verificación

- Prueba con el ejemplo (15).
- Las tres rondas del ejemplo valen 8, 1 y 6.
- Las 9 combinaciones de figuras dan el mismo resultado que las reglas escritas a mano (qué le gana a qué), sin usar el módulo.
- Una línea mal formada falla con un mensaje claro.
- Con el input real, un script independiente en Python usa una **tabla** escrita a mano con el puntaje de cada una de las 9 combinaciones, y obtiene el mismo resultado.

**Resultado con el input real: 14531**

---

## Parte 2

### Enunciado

El elfo vuelve y aclara que la segunda columna en realidad dice **cómo tiene que terminar la ronda**: `X` perder, `Y` empatar y `Z` ganar. El puntaje se calcula igual, pero ahora hay que elegir la figura que logra ese resultado.

#### Ejemplo

Con la misma guía: contra piedra hay que empatar, así que se juega piedra (1 + 3 = 4); contra papel hay que perder, y se juega piedra (1 + 0 = 1); contra tijera hay que ganar, y se juega piedra (1 + 6 = 7). El total es **12**.

#### Pregunta

Interpretando la segunda columna como el resultado buscado, ¿qué puntaje total se obtiene?

### Solución

#### La inversa de `contra`

Como en la parte 1 la segunda columna se guardó como un número de 0 a 2 sin interpretar, ahora se lee como un `Resultado`: el `enum` ya estaba en el orden derrota, empate, victoria, que es justo el de `X`, `Y`, `Z`.

La figura que hay que jugar sale del mismo círculo: para ganar, la que está un lugar **adelante** del rival; para empatar, la misma; para perder, la de **atrás**:

```java
// Dia02.java — Figura.paraLograr()
int desplazamiento = switch (resultado) {
    case VICTORIA -> 1;
    case EMPATE -> 0;
    case DERROTA -> -1;
};
return values()[Math.floorMod(rival.ordinal() + desplazamiento, 3)];
```

El puntaje de cada ronda se calcula con la misma función `puntaje` de la parte 1.

#### Verificación

- Prueba con el ejemplo (12).
- En las tres rondas del ejemplo se juega piedra, con 4, 1 y 7 puntos.
- Para cada rival y cada resultado buscado, `paraLograr` coincide con la **fuerza bruta** de probar las tres figuras con `contra` y quedarse con la única que da ese resultado.
- Con el input real, un script independiente en Python usa una **tabla** escrita a mano con el puntaje de las 9 combinaciones según esta nueva lectura, y obtiene el mismo resultado.

**Resultado con el input real: 11258**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Puntaje si la segunda columna es la figura a jugar | Puntaje si la segunda columna es el resultado buscado |
| **Enfoque** | Figuras en círculo: el resultado es la diferencia módulo 3 | La figura es el rival desplazado −1, 0 o +1 en el círculo |
| **Complejidad** | O(rondas) | O(rondas) |
| **Resultado** | 14531 | 11258 |

## Archivo de código

`src/main/java/advent/code/y2022/Dia02.java`
