# Año 2024 — Día 14: Reducto del Baño

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/14](https://adventofcode.com/2024/day/14). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Uno de los Historiadores necesita ir al baño, y nos teletransportamos a la sede del Conejo de Pascua, donde hay uno. Pero la zona frente al baño está **llena de robots** de seguridad, y para llegar sin problemas hay que predecir dónde van a estar.

El input lista los robots, uno por línea, con su **posición** `p=x,y` y su **velocidad** `v=x,y`:

- `x` cuenta casillas desde la pared izquierda e `y` desde la pared de arriba: `p=0,0` es la esquina superior izquierda.
- La velocidad está en casillas por segundo: `x` positiva va a la derecha e `y` positiva va **hacia abajo**. Por ejemplo, `v=1,-2` avanza 1 a la derecha y 2 hacia arriba cada segundo.

El piso real mide **101 casillas de ancho por 103 de alto** (el ejemplo usa uno de 11 × 7). Los robots pueden **compartir casilla** sin molestarse, y cuando llegan a un borde **aparecen del otro lado**, como si el piso diera la vuelta.

Después de **100 segundos**, se cuenta cuántos robots hay en cada **cuadrante** del piso. Los que están justo en la fila o la columna del medio no cuentan para ningún cuadrante. El **factor de seguridad** es el producto de las cuatro cantidades.

#### Ejemplo

Con 12 robots en un piso de 11 × 7:

```
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
```

El robot `p=2,4 v=2,-3` pasa por (4, 1), (6, 5), (8, 2), (10, 6) y (1, 3) en sus primeros 5 segundos: al pasarse de arriba aparece abajo, y al pasarse de la derecha aparece a la izquierda.

Después de 100 segundos, la cantidad de robots en cada casilla es:

```
......2..1.
...........
1..........
.11........
.....1.....
...12......
.1....1....
```

Los cuadrantes tienen 1, 3, 4 y 1 robots, y el factor de seguridad es **12**.

#### Pregunta

En el piso de 101 × 103, ¿cuál es el factor de seguridad después de exactamente 100 segundos?

### Solución

#### El input real

Son 500 robots, con velocidades de hasta 99 casillas por segundo en cada eje.

#### No hace falta simular: aritmética modular

Cada robot se mueve en línea recta, siempre igual y sin interactuar con los demás. Y como los bordes se conectan con el lado opuesto, cada eje es un **círculo**: la posición después de `t` segundos sale directo con `(p + v·t) mod tamaño`.

```java
// Dia14.java — Robot.posicionEn()
long x = Matematicas.modulo(posicion.x() + velocidad.x() * segundos, ancho);
long y = Matematicas.modulo(posicion.y() + velocidad.y() * segundos, alto);
return new Punto((int) x, (int) y);
```

Hay que usar `Matematicas.modulo` y no el `%` de Java: con velocidades negativas, `%` puede dar un resto negativo (`-18 % 100` es `-18`), y `modulo` siempre lo deja en `[0, tamaño)`.

#### Cuadrantes

Como el ancho y el alto son impares, hay una columna y una fila exactamente en el medio (`ancho / 2` y `alto / 2`). Los robots que están ahí se saltan, y el resto se reparte según de qué lado de cada una esté:

```java
// Dia14.java — robotsPorCuadrante()
for (Robot robot : robots) {
    Punto p = robot.posicionEn(ancho, alto, segundos);
    if (p.x() == medioX || p.y() == medioY) continue;
    int cuadrante = (p.y() < medioY ? 0 : 2) + (p.x() < medioX ? 0 : 1);
    cuadrantes[cuadrante]++;
}
```

El tamaño del piso es un parámetro, así que las pruebas usan el de 11 × 7 del ejemplo y `parte1` usa el de 101 × 103.

#### Verificación

- Prueba con el ejemplo: factor de seguridad 12, con cuadrantes 1, 3, 4 y 1.
- Los dos dibujos del ejemplo (el inicial y el de los 100 segundos), casilla por casilla.
- El robot `p=2,4 v=2,-3` en cada uno de sus primeros 5 segundos, dando la vuelta por los bordes.
- Con el input real, un script independiente en Python **simula segundo a segundo**: mueve cada robot 100 veces y lo devuelve al piso cuando se pasa de un borde. Obtiene el mismo resultado, con cuadrantes de 137, 126, 114 y 111 robots.

**Resultado con el input real: 218433348**

---

## Parte 2

### Enunciado

Durante la pausa, alguien nota que estos robots se parecen mucho a los que se fabrican en el Polo Norte. Si son del mismo tipo, deberían tener una **sorpresa escondida**: muy de vez en cuando, la mayoría de los robots se acomodan formando el **dibujo de un árbol de Navidad**.

#### Pregunta

¿Cuál es la menor cantidad de segundos que tienen que pasar para que los robots muestren el árbol?

(No hay ejemplo, y el enunciado no dice cómo es el árbol ni cuántos robots lo forman.)

### Solución

#### Qué buscar si no se sabe cómo es el dibujo

El enunciado es deliberadamente vago, así que primero hubo que explorar el input. La observación que guía todo: casi todo el tiempo los robots están **desparramados** por el piso, y un dibujo hecho por la mayoría de ellos los **junta** en una zona chica. Se busca el momento de **menor dispersión**.

#### Los dos ejes, por separado

Revisar segundo a segundo funcionaría: las posiciones se repiten cada 101 × 103 = 10 403 segundos, así que el árbol tiene que aparecer dentro de ese ciclo. Pero hay algo mejor. La `x` de un robot solo depende de `t mod 101`, y la `y` solo de `t mod 103`. Entonces:

1. Se prueban los 101 segundos de 0 a 100 y se busca cuándo las `x` están más juntas: da `t mod 101 = 48`.
2. Se prueban los 103 segundos de 0 a 102 y se busca cuándo las `y` están más juntas: da `t mod 103 = 23`.
3. El **teorema chino del resto** (`Matematicas.restoChino`) da el primer segundo que cumple las dos condiciones: **6512**.

```java
// Dia14.java
static long segundoMasAgrupado(List<Robot> robots, int ancho, int alto) {
    int restoX = momentoMasAgrupado(robots, ancho, Punto::x, ancho, alto);
    int restoY = momentoMasAgrupado(robots, alto, Punto::y, ancho, alto);
    return Matematicas.restoChino(new long[]{restoX, restoY}, new long[]{ancho, alto});
}
```

Son 101 + 103 = 204 pruebas en vez de 10 403. Como 101 y 103 son primos, el teorema chino del resto da una **única** solución en el ciclo, que es la primera aparición del árbol.

La dispersión de cada eje es la varianza, pero sin dividir: `n·Σc² − (Σc)²` es `n²` veces la varianza, así que elige el mismo segundo y se calcula con enteros exactos. En el input real, el mínimo de las `x` es menos de la mitad de la mediana, y lo mismo pasa con las `y`: no hay dudas sobre cuál es.

#### El árbol

En el segundo 6512, 353 de los 500 robots forman este dibujo (recortado del piso completo; el resto sigue desparramado alrededor):

```
###############################
#.............................#
#.............................#
#.............................#
#.............................#
#..............#..............#
#.............###.............#
#............#####............#
#...........#######...........#
#..........#########..........#
#............#####............#
#...........#######...........#
#..........#########..........#
#.........###########.........#
#........#############........#
#..........#########..........#
#.........###########.........#
#........#############........#
#.......###############.......#
#......#################......#
#........#############........#
#.......###############.......#
#......#################......#
#.....###################.....#
#....#####################....#
#.............###.............#
#.............###.............#
#.............###.............#
#.............................#
#.............................#
#.............................#
#.............................#
###############################
```

#### Verificación

- El enunciado no trae ejemplo, así que las pruebas arman sus propios casos: 300 robots con velocidades pseudoaleatorias (semilla fija), con las posiciones iniciales calculadas hacia atrás para que caigan todos en un cuadrado de 5 × 5 en un segundo conocido. Se encuentra ese segundo, tanto para 7777 como para 3. También se encuentra el resto correcto de cada eje por separado.
- Con el input real, un script independiente en Python usa **otro criterio**: recorre los 10 403 segundos del ciclo completo y busca en cuáles ningún robot comparte casilla con otro. El único es el **6512**.
- El dibujo de ese segundo, mostrado arriba, es efectivamente un árbol de Navidad con marco.

**Resultado con el input real: 6512**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Producto de los robots por cuadrante a los 100 segundos | Primer segundo en que se forma un árbol de Navidad |
| **Enfoque** | Posición directa con `(p + v·t) mod tamaño`, sin simular | Menor dispersión de cada eje en su período + teorema chino del resto |
| **Complejidad** | O(robots) | O((ancho + alto) × robots) |
| **Resultado** | 218433348 | 6512 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia14.java`
