# Año 2023 — Día 22: Losas de Arena

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/22](https://adventofcode.com/2023/day/22). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Ya cayó suficiente arena para filtrar el agua de la Isla de la Nieve… casi. La arena cae en forma de **ladrillos** compactos que se van apilando, y para usarla hay que **desintegrar** algunos y devolverlos a arena suelta. Pero hay que elegir con cuidado: si se desintegra el ladrillo equivocado, parte de la pila se puede derrumbar.

Los elfos sacaron una **foto** de los ladrillos mientras todavía caían (el input). Cada línea da los dos extremos de un ladrillo como `x,y,z~x,y,z`. Cada ladrillo es una línea recta de cubos alineada a la grilla: `2,2,2~2,2,2` es un solo cubo, `0,0,10~1,0,10` son dos cubos en horizontal a lo largo de `x`, y `0,0,1~0,0,10` son diez cubos en vertical.

El suelo está en `z = 0`, así que la menor altura posible de un ladrillo es 1. Como la foto se sacó en plena caída, primero hay que ver dónde termina cada ladrillo. Los ladrillos caen derecho hacia abajo, **nunca rotan** (aunque un ladrillo largo quede apoyado en un solo extremo), y no pueden ocupar el mismo lugar, así que cada uno se detiene al tocar el primer ladrillo (o el suelo) que encuentra.

Una vez asentados, un ladrillo se puede **desintegrar sin peligro** si, al sacarlo, ningún otro ladrillo cae. Hay que considerar cada ladrillo por separado, como si fuera el único que se saca.

#### Ejemplo

```
1,0,1~1,2,1   ← A
0,0,2~2,0,2   ← B
0,2,3~2,2,3   ← C
0,0,4~0,2,4   ← D
2,0,5~2,2,5   ← E
0,1,6~2,1,6   ← F
1,1,8~1,1,9   ← G
```

Al asentarse, A sostiene a B y C; B y C sostienen, cada uno, a D y E; D y E sostienen a F; y F sostiene a G. No se puede sacar A (caerían B y C) ni F (caería G); los otros **5** sí se pueden sacar, porque todo lo que sostienen tiene otro apoyo, o porque no sostienen nada.

#### Pregunta

Una vez asentados los ladrillos, ¿cuántos se pueden elegir para desintegrar sin que caiga ningún otro?

### Solución

#### El input real

1393 ladrillos, todos rectos en un solo eje y de a lo sumo 5 cubos, con `x` e `y` entre 0 y 9 y alturas de hasta 336.

#### Dejar caer en orden de altura

Un ladrillo solo puede frenarse contra ladrillos que estén más abajo, así que se los deja caer **en orden de altura** (por su base): cuando le toca a uno, todos los que podrían frenarlo ya están en su lugar final. Para no mover cubos de a uno, se lleva un **mapa de alturas**: para cada casilla `(x, y)`, la altura del tope más alto y qué ladrillo lo ocupa.

Cada ladrillo cae hasta quedar justo encima del tope más alto bajo su **huella** (las casillas que ocupa visto desde arriba). Se apoya en **todos** los ladrillos que tienen ese tope; los que están más abajo, bajo otras partes de la huella, no lo tocan:

```java
// Dia22.java — asentar()
int base = 0;
for (Punto p : huella) base = Math.max(base, topes.getOrDefault(p, new int[]{0, -1})[0]);
Set<Integer> debajo = new LinkedHashSet<>();
for (Punto p : huella) {
    int[] tope = topes.get(p);
    if (tope != null && tope[0] == base) debajo.add(tope[1]);
}
```

Así queda un grafo de apoyos: para cada ladrillo, en cuáles se apoya y cuáles se apoyan en él. Si un ladrillo tuviera que "caer hacia arriba" para asentarse, es que se superponía con otro en la foto, y falla con un mensaje claro.

#### Quién se puede desintegrar

Al sacar un ladrillo, los únicos que podrían caer son los que se apoyan directamente en él (los demás tienen sus apoyos intactos). Cada uno de esos se sostiene si tiene **al menos otro apoyo**:

```java
// Dia22.java — sePuedeDesintegrar()
return pila.sostiene().get(i).stream().allMatch(arriba -> pila.apoyos().get(arriba).size() >= 2);
```

#### Verificación

- Prueba con el ejemplo (5).
- Los ladrillos del ejemplo quedan a las alturas del dibujo del enunciado, con exactamente los apoyos que describe, y los que se pueden sacar son B, C, D, E y G.
- En el ejemplo, coincide con la **fuerza bruta** de sacar cada ladrillo, volver a asentar el resto y ver si alguno se movió (siguiendo a cada ladrillo por su identidad; ver la parte 2). Además, una pila ya asentada no se mueve al volver a asentarla.
- Un ladrillo cuya huella tiene topes a distintas alturas se apoya solo en el más alto. Los extremos pueden venir en cualquier orden.
- Una línea mal formada, un ladrillo en el suelo o debajo, o dos ladrillos superpuestos en la foto, fallan con un mensaje claro.
- Con el input real, un script independiente en Python usa **física literal con cubos**: baja cada ladrillo de a un nivel mientras los cubos de abajo estén libres, repitiendo hasta que ninguno se mueva. Después, para cada ladrillo, lo saca y comprueba si algún otro podría bajar un nivel. Obtiene el mismo resultado.

**Resultado con el input real: 457**

---

## Parte 2

### Enunciado

Desintegrar ladrillos de a uno no alcanza: lo que hace falta es una **reacción en cadena**. Para cada ladrillo, hay que ver cuántos **otros** ladrillos caerían si se desintegrara solo ese, contando los que caen porque cayeron los que los sostenían, y así sucesivamente.

#### Ejemplo

Con la misma pila, desintegrar A hace caer los otros 6 ladrillos, y desintegrar F hace caer solo a G. Los demás no hacen caer a ninguno, así que la suma es **7**.

#### Pregunta

¿Cuál es la suma, sobre cada ladrillo, de la cantidad de otros ladrillos que caerían al desintegrarlo?

### Solución

#### Reacción en cadena en orden

Un ladrillo cae si **todos** sus apoyos cayeron; los que están en el suelo no tienen apoyos y nunca caen. La pila de la parte 1 guarda los ladrillos en el orden en que cayeron, y cada uno se apoya solo en ladrillos **anteriores**. Así que alcanza con recorrerlos una vez en ese orden, a partir del que se saca: cuando le toca a uno, ya se sabe si cayó cada uno de sus apoyos.

```java
// Dia22.java — caenAlDesintegrar()
boolean[] cayo = new boolean[pila.ladrillos().size()];
cayo[i] = true;
int caen = 0;
for (int j = i + 1; j < cayo.length; j++) {
    Set<Integer> apoyos = pila.apoyos().get(j);
    if (!apoyos.isEmpty() && apoyos.stream().allMatch(apoyo -> cayo[apoyo])) {
        cayo[j] = true;
        caen++;
    }
}
return caen;
```

Esto se repite para cada ladrillo. Con 1393 ladrillos son menos de un millón de pasos, y todo tarda unos 80 ms.

#### Un detalle al verificar

La primera fuerza bruta que escribí (sacar el ladrillo, volver a asentar el resto y contar cuántos cambiaron de posición) daba **menos** que la reacción en cadena con el input real. El error estaba en la fuerza bruta: comparaba conjuntos de posiciones. En una columna de cubos iguales, al sacar el de abajo, cada cubo cae exactamente al lugar que ocupaba el de abajo, así que el conjunto de posiciones parece casi igual aunque se movieron todos. Hay que seguir a cada ladrillo por su **identidad**. Con esa corrección, las dos formas coinciden.

#### Verificación

- Prueba con el ejemplo (7).
- En el ejemplo, sacar A hace caer 6 ladrillos, sacar F hace caer 1 y los demás no hacen caer ninguno.
- En el ejemplo y en una pila más enredada (ladrillos apoyados en dos, ramas que se juntan), coincide con la **fuerza bruta** de sacar cada ladrillo, volver a asentar el resto y contar cuántos se movieron, siguiendo a cada uno por su identidad.
- En una columna de tres cubos iguales, sacar el de abajo hace caer los otros dos (el caso que engañaba a la primera fuerza bruta).
- Un ladrillo con un apoyo que no cae se queda, aunque su otro apoyo caiga.
- Con el input real, un script independiente en Python hace esa misma fuerza bruta con su propio algoritmo de asentado, siguiendo a cada ladrillo por un identificador, y obtiene el mismo resultado.

**Resultado con el input real: 79122**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Ladrillos que se pueden desintegrar sin que caiga otro | Suma de los que caen al desintegrar cada uno |
| **Enfoque** | Caer en orden de altura con un mapa de alturas; grafo de apoyos | Reacción en cadena recorriendo los ladrillos en el orden en que cayeron |
| **Complejidad** | O(n log n + cubos) | O(n × apoyos) por ladrillo, O(n²) en total |
| **Resultado** | 457 | 79122 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia22.java`
