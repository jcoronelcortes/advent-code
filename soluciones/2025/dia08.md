# Año 2025 — Día 8: Patio de Juegos

## Descripción del problema

El teletransportador nos deja en un enorme espacio subterráneo con un patio de juegos gigante. Los elfos están colgando **cajas de conexión** eléctricas y quieren unirlas con guirnaldas de luces: cuando dos cajas se conectan, la electricidad pasa entre ellas, y todas las cajas conectadas directa o indirectamente forman un **circuito**.

El input es la lista de posiciones 3D de las cajas, una por línea (`X,Y,Z`):

```
162,817,812
57,618,57
906,360,560
...
425,690,689
```

Para ahorrar luces, los elfos conectan primero los pares de cajas **más cercanos** según la distancia en línea recta (euclidiana).

El input real tiene 1000 cajas con coordenadas de hasta 5 dígitos.

---

## Parte 1

### Pregunta

Conectar los **1000 pares** de cajas más cercanos. Después, ¿cuál es el producto de los tamaños de los **3 circuitos más grandes**?

Un detalle importante: si el siguiente par más cercano ya está en el mismo circuito, "no pasa nada", pero el par **igual cuenta** como una de las conexiones.

### Ejemplo

Con las 20 cajas del ejemplo y solo **10** conexiones:

| Paso | Par conectado | Efecto |
|------|---------------|--------|
| 1 | `162,817,812` – `425,690,689` | circuito de 2 |
| 2 | `162,817,812` – `431,825,988` | ese circuito pasa a 3 |
| 3 | `906,360,560` – `805,96,715` | nuevo circuito de 2 |
| 4 | `431,825,988` – `425,690,689` | ya estaban juntas: no pasa nada |
| … | … | … |

Tras las 10 conexiones quedan 11 circuitos de tamaños 5, 4, 2, 2 y siete de 1.

Resultado del ejemplo: `5 · 4 · 2 =` **40**

### Desarrollo de la solución

Es el comienzo del **algoritmo de Kruskal** (árbol de expansión mínima): recorrer las aristas de menor a mayor peso y unir sus extremos.

#### 1. Ordenar todos los pares por distancia

Con 1000 cajas hay `1000 · 999 / 2 ≈ 500 000` pares: se calculan todos y se ordenan. Se usa la distancia **al cuadrado**:

- ordena exactamente igual que la distancia real (la raíz es creciente);
- es un entero exacto, sin raíces ni `double` que puedan empatar o redondear mal;
- cabe en un `long`: como mucho `3 · 100 000² = 3·10¹⁰`.

Se verificó con el input real que no hay distancias empatadas en el corte de las 1000 conexiones, así que el orden de los empates no afecta la respuesta.

#### 2. Unir con conjuntos disjuntos (union-find)

Para saber qué cajas están en el mismo circuito se agregó la utilidad **`ConjuntosDisjuntos`**: mantiene una partición de los elementos `0..n-1` y permite unir dos grupos y consultar su tamaño en tiempo casi constante (con compresión de caminos y unión por tamaño). Las cajas se identifican por su posición en el input.

```java
// Dia08.java — productoMayores()
ConjuntosDisjuntos circuitos = new ConjuntosDisjuntos(cajas.size());
// Se cuentan los pares aunque ya estén en el mismo circuito ("no pasa nada")
for (Par par : pares.subList(0, Math.min(conexiones, pares.size()))) {
    circuitos.unir(par.a(), par.b());
}
```

`ConjuntosDisjuntos.tamanios()` devuelve los tamaños de los circuitos de mayor a menor, y se multiplican los 3 primeros.

Como el ejemplo usa 10 conexiones y el input real 1000, la cantidad es un parámetro de `productoMayores`. También se agregó la utilidad **`Punto3D`** (record con coordenadas `long`, `parsear` y `distanciaCuadrada`).

#### Verificación

- Prueba con el ejemplo (10 conexiones → 40).
- Los 4 primeros pares más cercanos son exactamente los que describe el enunciado, incluido el que no cambia nada.
- Tras 10 conexiones quedan 11 circuitos de tamaños `5, 4, 2, 2, 1, 1, 1, 1, 1, 1, 1`.
- `ConjuntosDisjuntos` tiene su propia prueba basada en propiedades (jqwik) contra una versión de fuerza bruta que fusiona conjuntos a mano, y una prueba de rendimiento con una cadena de un millón de uniones.
- Con el input real, un script independiente en Python que arma el grafo con las 1000 aristas y cuenta componentes conexas con **DFS** (sin union-find) obtiene el mismo resultado: los circuitos más grandes miden 89, 47 y 41.

**Resultado con el input real: 171503**

---

## Parte 2

### Pregunta

Seguir conectando los pares más cercanos hasta que **todas** las cajas queden en un solo circuito. ¿Cuál es el producto de las coordenadas X de las dos cajas de la **última conexión** necesaria?

### Ejemplo

La primera conexión que deja las 20 cajas en un único circuito es la de `216,146,977` y `117,168,530`.

Resultado del ejemplo: `216 · 117 =` **25272**

### Desarrollo de la solución

#### Clave: es Kruskal completo

La parte 1 eran las primeras 1000 iteraciones del algoritmo de Kruskal; la parte 2 es el algoritmo **completo**: recorrer los pares de menor a mayor distancia, uniendo circuitos, hasta que quede uno solo. La conexión que lo logra es la **arista más larga del árbol de expansión mínima**.

`ConjuntosDisjuntos` ya lleva la cuenta de cuántos grupos quedan, así que basta con detenerse cuando `unir` junta dos circuitos distintos y la cantidad llega a 1:

```java
// Dia08.java — ultimaConexion()
ConjuntosDisjuntos circuitos = new ConjuntosDisjuntos(cajas.size());
for (Par par : paresOrdenados(cajas)) {
    if (circuitos.unir(par.a(), par.b()) && circuitos.cantidadGrupos() == 1) {
        return par;
    }
}
```

Los pares que ya estaban en el mismo circuito (`unir` devuelve `false`) se saltan sin efecto. El producto de las X usa `Math.multiplyExact` por seguridad, aunque con coordenadas de 5 dígitos el resultado (~9·10⁹) cabe de sobra en un `long`.

#### Verificación

- Prueba con el ejemplo (25272) y con el par exacto de la última conexión que indica el enunciado.
- Casos borde: con 2 cajas la única conexión es la última; con 3 cajas en línea, el par más lejano nunca hace falta porque sus extremos ya quedaron conectados a través de la del medio.
- Con el input real, un script independiente en Python calcula el árbol de expansión mínima con el **algoritmo de Prim** (sin ordenar pares ni usar union-find) y toma su arista más larga: obtiene el mismo resultado, con la conexión entre `90968,13304,86478` y `99700,4569,92874`.

**Resultado con el input real: 9069509600**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Producto de los 3 circuitos más grandes tras 1000 conexiones | Producto de las X de la conexión que deja un solo circuito |
| **Enfoque** | Ordenar pares por distancia² + `ConjuntosDisjuntos` (inicio de Kruskal) | Kruskal completo hasta que quede un circuito |
| **Complejidad** | O(n² log n), con n cajas | O(n² log n) |
| **Resultado** | 171503 | 9069509600 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia08.java`
