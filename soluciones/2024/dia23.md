# Año 2024 — Día 23: Fiesta LAN

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/23](https://adventofcode.com/2024/day/23). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Recorriendo una zona segura del cuartel del Conejo de Pascua, vemos carteles de una **fiesta LAN** para hoy. Nos conectamos a un puerto de red y descargamos el **mapa de la red local**: el input.

Cada línea es una **conexión** entre dos computadoras, como `kh-tc`. Las conexiones no tienen sentido: `tc-kh` sería lo mismo.

Para encontrar la fiesta buscamos grupos de computadoras conectadas. Primero, los **grupos de tres** en los que cada computadora está conectada con las otras dos. Y como el nombre de la computadora del Historiador Jefe seguramente empieza con `t`, solo interesan los grupos con **al menos una computadora cuyo nombre empiece con `t`**.

#### Ejemplo

Con estas 32 conexiones:

```
kh-tc  qp-kh  de-cg  ka-co  yn-aq  qp-ub  cg-tb  vc-aq
tb-ka  wh-tc  yn-cg  kh-ub  ta-co  de-co  tc-td  tb-wq
wh-td  ta-ka  td-qp  aq-cg  wq-ub  ub-vc  de-ta  wq-aq
wq-vc  wh-yn  ka-de  kh-ta  co-tc  wh-qp  tb-vc  td-yn
```

hay 12 grupos de tres:

```
aq,cg,yn   aq,vc,wq   co,de,ka   co,de,ta   co,ka,ta   de,ka,ta
kh,qp,ub   qp,td,wh   tb,vc,wq   tc,td,wh   td,wh,yn   ub,vc,wq
```

y **7** tienen alguna computadora que empieza con `t`.

#### Pregunta

¿Cuántos grupos de tres computadoras conectadas entre sí tienen al menos una cuyo nombre empieza con `t`?

### Solución

#### El input real

3380 conexiones entre 520 computadoras, y **todas** tienen exactamente 13 vecinos.

#### Triángulos en un grafo

Las computadoras son los nodos de un grafo y las conexiones sus aristas; un grupo de tres conectadas entre sí es un **triángulo**. La red se guarda como un mapa de cada computadora a sus vecinos, todo **ordenado por nombre** (`TreeMap` y `TreeSet`).

Para no encontrar el mismo triángulo varias veces se exige `a < b < c`: para cada computadora `a`, se recorren los vecinos `b` mayores que `a` y, de los vecinos de `a`, los `c` mayores que `b`; si `b` y `c` también están conectados, es un triángulo.

```java
// Dia23.java — Red.triangulos()
vecinos.forEach((a, deA) -> {
    for (String b : deA.tailSet(a, false)) {
        for (String c : deA.tailSet(b, false)) {
            if (vecinos.get(b).contains(c)) triangulos.add(List.of(a, b, c));
        }
    }
});
```

`tailSet(x, false)` da los elementos mayores que `x` sin copiarlos, por eso los vecinos son un `NavigableSet`. Como se recorre en orden, los triángulos salen ordenados igual que en la lista del enunciado. Con 13 vecinos por computadora son a lo sumo 520 × 78 pares, y termina en unos 17 ms.

Después solo queda filtrar los triángulos con algún nombre que empiece con `t`.

#### Verificación

- Prueba con el ejemplo (7) y con la **lista completa** de los 12 grupos de tres del enunciado, en el mismo orden.
- En el ejemplo coincide con la fuerza bruta: probar **todos los tríos** de computadoras.
- `a-b` y `b-a` dan la misma red.
- Una línea sin la forma `a-b`, o una computadora conectada consigo misma, falla con un mensaje claro.
- Con el input real, un script independiente en Python parte **solo de las computadoras que empiezan con `t`**, prueba cada par de sus vecinos y junta los tríos en un conjunto (para no contar dos veces los que tienen dos o tres nombres con `t`). Obtiene el mismo resultado.

**Resultado con el input real: 1253**

---

## Parte 2

### Enunciado

Siguen siendo demasiados grupos para revisarlos uno por uno, así que hay que encontrar la fiesta de otra forma. Como no se ve a ningún empleado, deben estar todos en la fiesta: la fiesta LAN es el **grupo más grande de computadoras conectadas todas entre sí**, donde cada una tiene conexión con todas las demás del grupo.

La **contraseña** para entrar es el nombre de todas las computadoras de la fiesta, **ordenados alfabéticamente** y separados por comas.

#### Ejemplo

En la red del ejemplo, el grupo más grande es `co`, `de`, `ka` y `ta`: las seis conexiones entre ellas existen. La contraseña es `co,de,ka,ta`.

#### Pregunta

¿Cuál es la contraseña de la fiesta LAN?

### Solución

#### Clique máxima

Un grupo de nodos conectados todos entre sí es una **clique**, y el más grande es la **clique máxima**. En general es un problema difícil (NP-completo), pero con 520 computadoras de 13 vecinos cada una es chico.

Se usa el algoritmo de **Bron–Kerbosch con pivote**, el clásico para recorrer las cliques **maximales** (las que no se pueden agrandar con ninguna otra computadora). Mantiene tres conjuntos:

- **grupo**: las computadoras ya elegidas, todas conectadas entre sí;
- **candidatas**: las conectadas con todo el grupo, que todavía se pueden agregar;
- **descartadas**: las que también están conectadas con todo el grupo pero ya se exploraron. Si queda alguna, el grupo se podría agrandar con ella, así que todavía no es maximal.

Cuando no quedan candidatas ni descartadas, el grupo es maximal. En cada paso se prueba agregar cada candidata (achicando candidatas y descartadas a sus vecinas) y después se la pasa a descartadas, para no volver a encontrar las mismas cliques.

```java
// Dia23.java — Red.bronKerbosch()
NavigableSet<String> aProbar = new TreeSet<>(candidatas);
aProbar.removeAll(vecinos.get(pivote));
for (String computadora : aProbar) {
    NavigableSet<String> deEsta = vecinos.get(computadora);
    grupo.add(computadora);
    bronKerbosch(grupo, interseccion(candidatas, deEsta), interseccion(descartadas, deEsta), mejores);
    grupo.remove(computadora);
    candidatas.remove(computadora);
    descartadas.add(computadora);
}
```

El **pivote** es la computadora (de candidatas o descartadas) con más vecinas entre las candidatas. Toda clique maximal incluye al pivote o a alguna candidata que no es su vecina; si no, se podría agregar el pivote. Así que alcanza con ramificar por las candidatas que **no** son vecinas del pivote, y eso poda la mayor parte del recorrido. Termina en unos 70 ms.

#### Una sola respuesta

Se guardan todas las cliques del tamaño máximo. Si hubiera dos distintas, la contraseña sería ambigua, así que en ese caso falla en vez de elegir una al azar. Como los conjuntos son `TreeSet`, los nombres ya salen ordenados para la contraseña.

#### Verificación

- Prueba con el ejemplo (`co,de,ka,ta`).
- En el ejemplo, la solución coincide con el **buscador de cliques de JGraphT** (`BronKerboschCliqueFinder`), una implementación independiente que ya está entre las dependencias.
- El grupo encontrado está conectado todo entre sí.
- Dos grupos empatados en tamaño fallan; un grafo completo de 6 computadoras es un solo grupo de 6.
- Con el input real, un script independiente en Python usa otra idea: el grupo más grande que contiene a una computadora está formado por ella y algunos de sus 13 vecinos, así que prueba **todos los subconjuntos de vecinos**, del más grande al más chico. Encuentra un único grupo de 13 computadoras, el mismo.

**Resultado con el input real: ag,bt,cq,da,hp,hs,mi,pa,qd,qe,qi,ri,uq**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Grupos de tres conectados con algún nombre que empieza con t | Contraseña: el grupo más grande conectado todo entre sí |
| **Enfoque** | Triángulos del grafo con a < b < c | Clique máxima con Bron–Kerbosch con pivote |
| **Complejidad** | O(computadoras × grado²) | Exponencial en el peor caso; rápido con grado 13 |
| **Resultado** | 1253 | ag,bt,cq,da,hp,hs,mi,pa,qd,qe,qi,ri,uq |

## Archivo de código

`src/main/java/advent/code/y2024/Dia23.java`
