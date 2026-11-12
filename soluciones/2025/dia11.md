# Año 2025 — Día 11: Reactor

## Descripción del problema

Bajo la fábrica hay un **reactor** toroidal, y los elfos no logran que se comunique con un rack de servidores nuevo. Entre ambos hay una maraña de cables y dispositivos. Creen que el problema aparece cuando los datos siguen cierto **camino** por los dispositivos.

El input es la lista de dispositivos y sus salidas:

```
aaa: you hhh
you: bbb ccc
bbb: ddd eee
ccc: ddd eee fff
ddd: ggg
eee: out
fff: out
ggg: out
hhh: ccc fff iii
iii: out
```

`bbb: ddd eee` significa que `bbb` tiene salidas hacia `ddd` y `eee`. Los datos solo fluyen hacia las salidas, nunca hacia atrás: es un **grafo dirigido**.

El input real tiene 607 dispositivos y 1581 conexiones, y **no tiene ciclos** (es un DAG): se verificó con un orden topológico que incluye todos los dispositivos. Esto es esencial, porque con un ciclo habría infinitos caminos.

---

## Parte 1

### Pregunta

¿Cuántos caminos distintos hay desde el dispositivo `you` hasta la salida `out`?

### Ejemplo

| Camino |
|--------|
| `you → bbb → ddd → ggg → out` |
| `you → bbb → eee → out` |
| `you → ccc → ddd → ggg → out` |
| `you → ccc → eee → out` |
| `you → ccc → fff → out` |

Resultado del ejemplo: **5**

### Desarrollo de la solución

#### Clave: los caminos desde un nodo son la suma de los caminos desde sus salidas

```
caminos(out) = 1
caminos(d)   = Σ caminos(s)   para cada salida s de d
```

En el ejemplo: `caminos(ccc) = caminos(ddd) + caminos(eee) + caminos(fff) = 1 + 1 + 1 = 3`, y `caminos(you) = caminos(bbb) + caminos(ccc) = 2 + 3 = 5`.

Recorrer los caminos uno por uno puede ser exponencial: en un DAG la cantidad de caminos crece muy rápido (una cadena de 60 "diamantes" ya tiene 2⁶⁰). Pero muchos caminos comparten dispositivos, así que basta con calcular cada dispositivo **una sola vez** y reutilizarlo: **memoización**. Se usa la utilidad `Memoizador`:

```java
// Dia11.java — contarCaminos()
Memoizador<String, Long> caminos = new Memoizador<>((dispositivo, propio) -> {
    if (dispositivo.equals(hasta)) return 1L;
    long total = 0;
    for (String salida : conexiones.getOrDefault(dispositivo, List.of())) {
        total = Math.addExact(total, propio.apply(salida));
    }
    return total;
});
return caminos.calcular(desde);
```

El costo es lineal en la cantidad de conexiones. Los dispositivos sin línea en el input (como `out`) no tienen salidas: si no son el destino, aportan 0 caminos. `contarCaminos` recibe el origen y el destino como parámetros, por si la parte 2 pide otros (y los pidió).

#### Verificación

- Prueba con el ejemplo (5).
- Caminos entre otros dispositivos del ejemplo: `bbb` (2), `ccc` (3), `aaa` (10, sumando los que pasan por `you` y por `hhh`), un destino inalcanzable (0) y origen igual al destino (1, el camino vacío).
- Una cadena de 60 diamantes con 2⁶⁰ caminos se cuenta al instante.
- Con el input real, un script independiente en Python calcula un **orden topológico** (algoritmo de Kahn), que además confirma que no hay ciclos, y propaga la cantidad de caminos **hacia adelante** en ese orden, sin recursión. Obtiene el mismo resultado.

**Resultado con el input real: 749**

---

## Parte 2

### Pregunta

Los elfos descubrieron que el camino problemático pasa por **`dac`** (un conversor digital-analógico) y por **`fft`** (un dispositivo que hace una transformada rápida de Fourier). ¿Cuántos caminos de **`svr`** (el rack de servidores) a `out` pasan por **ambos**, en cualquier orden?

### Ejemplo

Con otra lista de dispositivos:

```
svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out
```

Hay 8 caminos de `svr` a `out`, pero solo 2 pasan por `dac` y `fft`: `svr,aaa,fft,ccc,eee,dac,fff,ggg,out` y el mismo terminando por `hhh`.

Resultado del ejemplo: **2**

### Desarrollo de la solución

#### Clave: dividir el camino en tramos

Un camino que pasa por `dac` y `fft` las visita en algún orden, y queda dividido en **tres tramos independientes**. Cualquier forma de recorrer el primer tramo se puede combinar con cualquier forma de recorrer los otros, así que la cantidad de caminos es el **producto**:

```
caminos = svr→dac · dac→fft · fft→out   +   svr→fft · fft→dac · dac→out
```

En un grafo sin ciclos, a lo sumo uno de los dos órdenes es posible: si hubiera caminos de `dac` a `fft` y de `fft` a `dac`, juntos formarían un ciclo. El otro término da 0 solo.

En el ejemplo: `dac→fft = 0` y `svr→fft · fft→dac · dac→out = 1 · 1 · 2 = 2`.

Como `contarCaminos` ya recibía origen y destino como parámetros, la parte 2 son 6 llamadas:

```java
// Dia11.java — contarCaminosPor()
long primeroA = Math.multiplyExact(Math.multiplyExact(
        contarCaminos(conexiones, desde, a),
        contarCaminos(conexiones, a, b)),
        contarCaminos(conexiones, b, hasta));
long primeroB = ...;   // lo mismo con a y b intercambiados
return Math.addExact(primeroA, primeroB);
```

Los números son grandes: en total hay ~9·10¹⁶ caminos de `svr` a `out` (todavía caben en un `long`), y las multiplicaciones y sumas usan `Math.multiplyExact` y `Math.addExact` para detectar un desborde en vez de dar un resultado incorrecto.

#### Verificación

- Prueba con el ejemplo (2).
- Los tramos del ejemplo: 8 caminos en total, `dac→fft = 0`, `fft→dac = 1`, y el resultado no depende del orden en que se nombran `dac` y `fft`.
- En un grafo chico en capas, el resultado coincide con **enumerar explícitamente** cada camino y contar los que pasan por ambos.
- Con el input real, un script independiente en Python usa otro método: programación dinámica **hacia adelante** en orden topológico, donde el estado es el dispositivo junto con cuáles de `dac` y `fft` ya se visitaron (4 combinaciones). Obtiene el mismo resultado, y además confirma el total: de los ~9,2·10¹⁶ caminos, ~7,6·10¹⁶ no pasan por ninguno de los dos.

**Resultado con el input real: 420257875695750**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Caminos de `you` a `out` | Caminos de `svr` a `out` que pasan por `dac` y `fft` |
| **Enfoque** | Recursión con memoización (`Memoizador`) sobre el DAG | Producto de los caminos de cada tramo, en ambos órdenes |
| **Complejidad** | O(dispositivos + conexiones) | O(dispositivos + conexiones) |
| **Resultado** | 749 | 420257875695750 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia11.java`
