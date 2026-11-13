# Año 2023 — Día 25: Sobrecarga de Nieve

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/25](https://adventofcode.com/2023/day/25). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Sigue sin nevar, así que vamos al último lugar que falta revisar: el centro de la Isla de la Nieve, justo debajo de la cascada. Ahí alguien conectó cientos de máquinas del clima, almanaques, módulos de comunicación, espejos, lentes y demás en un enorme aparato para hacer nieve, pero nada funciona. Una pantallita muestra "Error 2023", y en la línea de ayuda nos explican que es una **sobrecarga**: hay demasiados componentes conectados.

Hay que desconectar al menos la mitad del equipo, pero ya es Navidad y solo hay tiempo para cortar **tres cables**. Por suerte, alguien dejó un diagrama de conexiones (el input): cada línea tiene un componente, dos puntos y los componentes con los que está conectado. Los cables no tienen dirección (`abc: xyz` y `xyz: abc` son el mismo cable), y cada uno aparece una sola vez, así que algunos componentes solo aparecen a la derecha.

Hay que encontrar los tres cables que, al cortarlos, dividen los componentes en **dos grupos** separados, y multiplicar los tamaños de los dos grupos.

#### Ejemplo

```
jqt: rhn xhk nvd
rsh: frs pzl lsr
xhk: hfx
cmg: qnr nvd lhk bvb
rhn: xhk bvb hfx
bvb: xhk hfx
pzl: lsr hfx nvd
qnr: nvd
ntq: jqt hfx bvb xhk
nvd: lhk
lsr: lhk
rzs: qnr cmg lsr rsh
frs: qnr lhk lsr
```

Cortando los cables `hfx/pzl`, `bvb/cmg` y `nvd/jqt` quedan dos grupos: uno de 9 componentes (`cmg`, `frs`, `lhk`, `lsr`, `nvd`, `pzl`, `qnr`, `rsh` y `rzs`) y otro de 6 (`bvb`, `hfx`, `jqt`, `ntq`, `rhn` y `xhk`). El producto es **54**.

#### Pregunta

¿Cuál es el producto de los tamaños de los dos grupos que quedan al cortar los tres cables?

### Solución

#### El input real

1482 componentes y 3317 cables.

#### Flujo máximo, corte mínimo

Probar todos los tríos de cables sería imposible: son unos 6 · 10⁹. Lo que se busca es un **corte mínimo** de tamaño 3, y el teorema de **flujo máximo = corte mínimo** lo resuelve de forma exacta. Si cada cable tiene capacidad 1, el flujo máximo entre dos componentes es la menor cantidad de cables que hay que cortar para separarlos, y se calcula buscando con BFS **caminos de aumento**: caminos por cables que todavía tienen capacidad libre, por los que se manda una unidad más.

Se fija un componente `s` y se prueba con cada otro componente `t`:

- si `t` está **del otro lado** del corte buscado, el flujo da exactamente 3;
- si está **del mismo lado**, da más de 3 (si no, habría otro corte de 3 que los separa, y el enunciado dice que la red se divide en dos grupos cortando tres cables).

Así que alcanza con buscar a lo sumo 4 caminos de aumento por cada `t`, y en cuanto uno da exactamente 3 se tiene el corte:

```java
// Dia25.java — corteMinimo()
Map<Long, Integer> flujo = new HashMap<>();
int caminos = 0;
while (caminos <= cables && aumentar(adyacencia, flujo, 0, t)) caminos++;
if (caminos < cables) {
    throw new IllegalStateException("La red se separa cortando solo " + caminos + " cables");
}
if (caminos > cables) continue;
boolean[] alcanzable = alcanzables(adyacencia, flujo, 0);
```

Cuando el flujo da 3, los cables que lo llevan quedan saturados, y los componentes a los que todavía se llega desde `s` por cables con capacidad libre forman **uno de los dos grupos**. Los cables que salen de ese grupo son los cortados, y se comprueba que sean exactamente 3. Si la red se pudiera separar cortando menos cables, o no se pudiera con tres, falla con un mensaje claro.

Como los cables no tienen dirección, cada uno puede llevar flujo en cualquiera de los dos sentidos: el flujo se guarda con signo, `flujo(v, u) = −flujo(u, v)`, y de `u` a `v` queda capacidad libre mientras `flujo(u, v) < 1`. Con el input real todo tarda unos 20 ms.

#### Verificación

- Prueba con el ejemplo (54).
- El corte del ejemplo son exactamente los cables `hfx/pzl`, `bvb/cmg` y `nvd/jqt`, con los dos grupos de 9 y 6 componentes del enunciado.
- En el ejemplo, coincide con la **fuerza bruta** de probar los 5456 tríos de sus 33 cables: hay uno solo que divide la red en dos grupos, el mismo. Además, ningún par de cables alcanza para dividirla.
- Si la red se divide con un solo cable (dos triángulos unidos por un puente), buscar un corte de tres falla y buscar uno de un cable lo encuentra. En cinco componentes conectados todos entre sí, separar cualquiera exige cuatro cables, así que tampoco hay un corte de tres. Una línea mal formada o un cable de un componente a sí mismo fallan.
- Con el input real, un script independiente en Python usa una idea completamente distinta, la **intermediación de los cables**. Hace BFS desde uno de cada cinco componentes y cuenta cuántos caminos mínimos pasan por cada cable. Los tres cables del corte son los únicos puentes entre los dos grupos, así que los usan todos los caminos que cruzan de un lado al otro, y quedan primeros: `jxm/qns`, `dbt/tjd` y `mgb/plt`. Sin ellos la red queda en dos grupos de 752 y 730 componentes, con el mismo producto.

**Resultado con el input real: 548960**

---

## Parte 2

### Enunciado

El día 25 es el último del año y su segunda parte **no tiene un problema propio**: la segunda estrella se obtiene al completar todos los demás días.

### Solución

`parte2` devuelve el texto fijo `"estrella final"`, igual que el último día de 2024 y de 2025, para que la tabla anual y `RespuestasPrueba` traten este día como a los demás.

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Producto de los grupos al cortar tres cables | No tiene problema |
| **Enfoque** | Flujo máximo = corte mínimo, con a lo sumo 4 caminos de aumento | — |
| **Complejidad** | O(intentos × 4 × cables) | — |
| **Resultado** | 548960 | estrella final |

## Archivo de código

`src/main/java/advent/code/y2023/Dia25.java`
