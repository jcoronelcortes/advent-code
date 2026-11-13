# Año 2023 — Día 24: Nunca Me Digas las Probabilidades

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/24](https://adventofcode.com/2023/day/24). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Algo falla en la fabricación de nieve: en vez de nieve, el agua del aire está formando **granizo**. Quizás se pueda hacer algo para romper los granizos.

Por unos vientos fuertes (seguramente mágicos), cada granizo vuela en **línea recta** a velocidad constante. El input tiene la posición de cada granizo en el instante 0 y su velocidad, cuánto se mueve por nanosegundo, con el formato `px, py, pz @ vx, vy, vz`. Por ejemplo, `20, 19, 15 @ 1, -5, -3` está en (20, 19, 15) y un nanosegundo después está en (21, 14, 12).

Para estimar si los granizos chocarán entre sí, primero se mira solo el plano `x, y`, ignorando `z`. Hay que contar cuántos **pares de trayectorias** se cruzan dentro de un **área de prueba**, mirando solo hacia el futuro. Los granizos no tienen que coincidir en el mismo instante: alcanza con que las líneas que van a recorrer se crucen.

#### Ejemplo

```
19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3
```

Con un área de prueba de 7 a 27 en `x` y en `y`, de los 10 pares hay 2 cuyas trayectorias se cruzan adentro, por ejemplo el primero con el segundo en (14,333…, 15,333…). Otros tres pares se cruzan fuera del área, uno es de trayectorias paralelas y cuatro se cruzaron en el pasado de alguno de los dos granizos. La respuesta es **2**.

En el input real el área va de 200000000000000 a 400000000000000.

#### Pregunta

Mirando solo `x` e `y`, ¿cuántos pares de trayectorias se cruzan dentro del área de prueba?

### Solución

#### El input real

300 granizos (44 850 pares), con posiciones del orden de 10¹⁴ y velocidades de hasta 930 en valor absoluto. Hay 3 pares de trayectorias paralelas y ninguno sobre la misma recta.

#### El cruce de dos rectas

Las trayectorias son `p₁ + t·v₁` y `p₂ + s·v₂`. Igualándolas y aplicando el producto cruzado del plano (`a × b = aₓ·b_y − a_y·bₓ`) para despejar cada incógnita:

```
D = v₁ × v₂,    t = ((p₂ − p₁) × v₂) / D,    s = ((p₂ − p₁) × v₁) / D
```

- Si `D = 0`, las trayectorias son **paralelas** y no se cruzan. Si además están sobre la misma recta se cruzarían en infinitos puntos; el enunciado no dice qué hacer en ese caso (y en el input no pasa), así que falla con un mensaje claro.
- El cruce está en el **futuro de los dos** si `t ≥ 0` y `s ≥ 0`.
- Su posición es `p₁ + t·v₁`, y tiene que caer dentro del área en `x` y en `y`.

#### Exacto, sin `double`

Con posiciones de 10¹⁴, productos como `p·D` llegan a unos 10²⁰ y no entran en un `long`. Con `double` se perdería precisión, y un cruce muy cerca del borde del área podría caer del lado equivocado. Así que todo se calcula con `BigInteger` y **sin dividir**: se deja `D` positivo y se comparan `x·D` e `y·D` con `minimo·D` y `maximo·D`.

```java
// Dia24.java — seCruzanEnArea()
if (numeradorT.signum() < 0 || numeradorS.signum() < 0) return false;
// x·D e y·D del cruce, para compararlos con minimo·D y maximo·D sin dividir
BigInteger x = BigInteger.valueOf(a.px()).multiply(d).add(numeradorT.multiply(BigInteger.valueOf(a.vx())));
BigInteger y = BigInteger.valueOf(a.py()).multiply(d).add(numeradorT.multiply(BigInteger.valueOf(a.vy())));
BigInteger desde = BigInteger.valueOf(minimo).multiply(d), hasta = BigInteger.valueOf(maximo).multiply(d);
return x.compareTo(desde) >= 0 && x.compareTo(hasta) <= 0 && y.compareTo(desde) >= 0 && y.compareTo(hasta) <= 0;
```

Se prueban los 44 850 pares, lo que tarda unos 30 ms.

#### Verificación

- Prueba con el ejemplo (2 con el área de 7 a 27).
- Solo los pares primero-segundo y primero-tercero del ejemplo se cruzan dentro del área. Con un área enorme entran también los tres que se cruzaban afuera, pero no los del pasado ni los paralelos.
- Un cruce justo en el borde del área cuenta y uno apenas afuera no, también con números del tamaño del input y un cruce en un instante fraccionario (`t = 1/3`).
- Dos trayectorias sobre la misma recta fallan, igual que una línea mal formada.
- Con el input real, un script independiente en Python resuelve cada sistema 2×2 con la **regla de Cramer** sobre fracciones exactas (`fractions.Fraction`). Comprueba que los dos granizos lleguen al mismo punto y obtiene el mismo resultado.

**Resultado con el input real: 23760**

---

## Parte 2

### Enunciado

Resulta que ningún granizo va a chocar con otro por sí solo, así que hay que provocarlo. Encontramos una **piedra**, y con los vientos mágicos podemos ubicarnos en cualquier posición entera y lanzarla con cualquier velocidad entera. La piedra no se frena ni cambia de dirección al chocar. Ahora, **contando el eje `z`**, hay que lanzarla en el instante 0 de forma que choque con **todos** los granizos: en algún instante tiene que estar exactamente en el mismo lugar que cada uno.

#### Ejemplo

Con los granizos del ejemplo, hay que lanzar la piedra desde (24, 13, 10) con velocidad (−3, 1, 2). Así choca con el quinto granizo en el instante 1, con el segundo en el 3, con el tercero en el 4, con el primero en el 5 y con el cuarto en el 6. La suma de las coordenadas de la posición inicial es 24 + 13 + 10 = **47**.

#### Pregunta

¿Cuánto da la suma de las coordenadas `x`, `y` y `z` de la posición desde la que hay que lanzar la piedra?

### Solución

#### De paralelismo a ecuaciones lineales

La piedra (`P`, `V`) choca con el granizo `i` si hay un instante `tᵢ` en que están en el mismo lugar: `P + tᵢ·V = pᵢ + tᵢ·vᵢ`, o sea `P − pᵢ = tᵢ·(vᵢ − V)`. Eso dice que los vectores `P − pᵢ` y `V − vᵢ` son **paralelos**, y dos vectores son paralelos si su producto vectorial es cero:

```
(P − pᵢ) × (V − vᵢ) = 0
```

Al desarrollar aparece `P × V`, que no es lineal, pero es **el mismo para todos los granizos**. Restando las ecuaciones de dos granizos `i` y `j` se cancela y queda un sistema lineal:

```
P × (vⱼ − vᵢ) + (pⱼ − pᵢ) × V = pⱼ × vⱼ − pᵢ × vᵢ
```

Son tres ecuaciones (una por coordenada) en las seis incógnitas `Px, Py, Pz, Vx, Vy, Vz`. Con dos pares, el granizo 0 con el 1 y el 0 con el 2, hay seis ecuaciones.

#### Resolver exacto y comprobar

Los coeficientes llegan a unos 10¹⁷, así que el sistema se resuelve con **eliminación gaussiana sobre fracciones exactas**, con la utilidad compartida `SistemaLineal` (que trabaja con `BigFraction` de Commons Math y también usa 2025 día 10). Si el sistema queda singular (por ejemplo, con velocidades paralelas) se prueba con otros granizos. Al final no se confía en el álgebra: se exige que la solución tenga coordenadas enteras y se comprueba que la piedra choque con **cada uno** de los granizos, en un mismo instante no negativo en los tres ejes.

```java
// Dia24.java — lanzamiento()
Optional<BigFraction[]> solucion = SistemaLineal.resolverUnica(sistema);
if (solucion.isEmpty()) continue;
Granizo piedra = aEnteros(solucion.get());
for (Granizo granizo : granizos) {
    if (!choca(piedra, granizo)) {
        throw new IllegalStateException("La piedra " + piedra + " no choca con " + granizo);
    }
}
return piedra;
```

Con el input real, la piedra sale de (363206674204110, 368909610239045, 156592420220258) con velocidad (−164, −127, 223), choca con los 300 granizos, y todo tarda menos de 10 ms.

#### Verificación

- Prueba con el ejemplo (47).
- La piedra del ejemplo es exactamente `24, 13, 10 @ -3, 1, 2`, y choca con cada granizo en el instante que da el enunciado (5, 3, 4, 6 y 1).
- `choca` exige el mismo instante en los tres ejes, no negativo, y maneja los ejes en los que la piedra y el granizo van a la misma velocidad.
- En el ejemplo, coincide con una **fuerza bruta** que prueba todos los instantes de choque chicos con los dos primeros granizos, deduce la piedra de esos dos choques y se queda con las que chocan con todos: sale una sola, la misma.
- Si se agrega un granizo con el que ninguna piedra puede chocar, falla con un mensaje claro.
- Con el input real, un script independiente en Python usa un método completamente distinto: **busca la velocidad**. Si dos granizos tienen la misma velocidad en un eje, la distancia entre ellos en ese eje no cambia, y la piedra los alcanza a los dos, así que esa distancia tiene que ser múltiplo de la diferencia de velocidades entre la piedra y ellos. Eso deja **una sola velocidad candidata** por eje entre −1000 y 1000. Con ella saca la posición de los dos primeros granizos, comprueba que choque con todos, y obtiene la misma piedra.

**Resultado con el input real: 888708704663413**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Pares de trayectorias que se cruzan en el área, a futuro | Posición de una piedra que choque con todos |
| **Enfoque** | Cruce de rectas con productos cruzados, exacto con `BigInteger` | Sistema lineal de 6 × 6 restando productos vectoriales, con fracciones exactas |
| **Complejidad** | O(n²) pares | O(1) para el sistema, O(n) para comprobar |
| **Resultado** | 23760 | 888708704663413 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia24.java`
