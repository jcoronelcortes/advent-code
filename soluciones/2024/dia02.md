# Año 2024 — Día 2: Reportes del Reno de Nariz Roja

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/2](https://adventofcode.com/2024/day/2). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Por suerte, el primer lugar que los Historiadores quieren revisar queda cerca de la oficina del Historiador Jefe: la planta de fusión y fisión nuclear del **Reno de Nariz Roja**. Del Jefe no hay rastro, pero los ingenieros corren a recibirnos (todavía recuerdan la vez que se salvó a Rudolph sintetizándolo a partir de un solo electrón) y, ya que estamos ahí, nos piden ayuda para analizar unos datos raros del reactor. Los Historiadores ya se repartieron para buscar por toda la instalación, así que aceptamos.

Los datos (el input) son muchos **reportes**, uno por línea. Cada reporte es una lista de números, llamados **niveles**, separados por espacios:

```
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
```

Los sistemas de seguridad del reactor solo toleran niveles que suben o bajan **gradualmente**. Un reporte es **seguro** solo si se cumplen las dos condiciones:

- los niveles son **todos crecientes** o **todos decrecientes**;
- dos niveles vecinos difieren en **al menos 1** y **como mucho 3**.

#### Ejemplo

| Reporte | ¿Seguro? | Motivo |
|---------|----------|--------|
| `7 6 4 2 1` | sí | siempre baja, de a 1 o 2 |
| `1 2 7 8 9` | no | de 2 a 7 sube 5 |
| `9 7 6 2 1` | no | de 6 a 2 baja 4 |
| `1 3 2 4 5` | no | de 1 a 3 sube, pero de 3 a 2 baja |
| `8 6 4 4 1` | no | de 4 a 4 no sube ni baja |
| `1 3 6 7 9` | sí | siempre sube, de a 1, 2 o 3 |

En el ejemplo hay **2** reportes seguros.

#### Pregunta

¿Cuántos reportes son seguros?

### Solución

#### El input real

Son 1000 reportes, de 5 a 8 niveles cada uno.

#### Clave: mirar las diferencias entre vecinos

Las dos reglas se pueden expresar con las **diferencias** entre cada nivel y el anterior. En `7 6 4 2 1` las diferencias son `−1 −2 −2 −1`. Un reporte es seguro si y solo si:

- todas las diferencias están entre **1 y 3** (siempre sube gradualmente), o
- todas están entre **−3 y −1** (siempre baja gradualmente).

El signo común garantiza que sea todo creciente o todo decreciente, y una diferencia 0 (dos niveles iguales) queda fuera de ambos intervalos. Así las dos reglas se comprueban juntas.

#### Pares consecutivos con `Gatherers.windowSliding`

Para obtener las diferencias se usa `Gatherers.windowSliding(2)` de Java 25, que recorre una lista de a **pares consecutivos** (`[7,6]`, `[6,4]`, `[4,2]`, `[2,1]`) sin bucles con índices:

```java
// Dia02.java — diferencias()
return niveles.stream()
        .gather(Gatherers.windowSliding(2))
        .filter(par -> par.size() == 2)      // con un solo nivel, la única ventana es incompleta
        .map(par -> par.get(1) - par.get(0))
        .toList();
```

El filtro cubre un detalle de `windowSliding`: si la lista tiene menos elementos que el tamaño de la ventana, devuelve una sola ventana incompleta. Con él, un reporte de un solo nivel no tiene diferencias y se considera seguro (no viola ninguna regla).

```java
// Dia02.java — esSeguro()
List<Integer> diferencias = diferencias(niveles);
boolean creciente = diferencias.stream().allMatch(d -> d >= SALTO_MINIMO && d <= SALTO_MAXIMO);
boolean decreciente = diferencias.stream().allMatch(d -> d <= -SALTO_MINIMO && d >= -SALTO_MAXIMO);
return creciente || decreciente;
```

#### Verificación

- Prueba con el ejemplo (2) y con el resultado de cada uno de sus 6 reportes.
- Diferencias de un reporte, de un solo nivel y vacío.
- Límites: saltos de exactamente 3 sirven, de 4 o de 0 no; un reporte de dos niveles decreciente es seguro.
- Con el input real, un script independiente en Python con otro criterio (el reporte coincide con su versión **ordenada**, creciente o decreciente, y después revisa los saltos) obtiene el mismo resultado.

**Resultado con el input real: 306**

---

## Parte 2

### Enunciado

A los ingenieros les sorprende que haya tan pocos reportes seguros, hasta que se dan cuenta de que olvidaron mencionar el **amortiguador de problemas**: un módulo montado en el reactor que permite que los sistemas de seguridad toleren **un solo nivel malo** en un reporte que, sin él, sería seguro. Es como si ese nivel nunca hubiera existido.

Las reglas son las mismas que antes, pero ahora, si al **quitar un solo nivel** de un reporte inseguro este queda seguro, el reporte cuenta como seguro.

#### Ejemplo

| Reporte | ¿Seguro? | Motivo |
|---------|----------|--------|
| `7 6 4 2 1` | sí | ya era seguro, sin quitar nada |
| `1 2 7 8 9` | no | no se salva quitando ningún nivel |
| `9 7 6 2 1` | no | no se salva quitando ningún nivel |
| `1 3 2 4 5` | sí | quitando el segundo nivel, el 3 |
| `8 6 4 4 1` | sí | quitando el tercer nivel, un 4 |
| `1 3 6 7 9` | sí | ya era seguro, sin quitar nada |

Gracias al amortiguador, en el ejemplo hay **4** reportes seguros.

#### Pregunta

Teniendo en cuenta que el amortiguador puede quitar un solo nivel de los reportes inseguros, ¿cuántos reportes son seguros?

### Solución

#### Probar quitar cada nivel

Un reporte es seguro con amortiguador si ya es seguro, o si lo es quitándole **algún** nivel. Como los reportes tienen como mucho 8 niveles, basta con probar quitar cada uno y reutilizar `esSeguro` de la parte 1:

```java
// Dia02.java — esSeguroConAmortiguador()
if (esSeguro(niveles)) return true;
for (int quitado = 0; quitado < niveles.size(); quitado++) {
    List<Integer> sinUno = new ArrayList<>(niveles);
    sinUno.remove(quitado);   // remove(int): por posición, no por valor
    if (esSeguro(sinUno)) return true;
}
return false;
```

Es O(n²) por reporte: unas 64 comparaciones como mucho, con 1000 reportes, en pocos milisegundos.

Un detalle de Java: `quitado` es un `int`, así que `remove` quita el elemento **en esa posición**. Si fuera un `Integer`, se llamaría a `remove(Object)` y quitaría el primer nivel con **ese valor**, que es otra cosa.

#### Por qué no hace falta algo más ingenioso

Existe una solución O(n) que recorre el reporte una sola vez, pero con reportes tan cortos la diferencia es imperceptible y el código sería bastante más difícil de verificar. Probar todas las opciones es exactamente lo que dice el enunciado, así que es fácil convencerse de que es correcto.

#### Verificación

- Prueba con el ejemplo (4) y con el resultado de cada uno de sus 6 reportes.
- Quitar el primer o el último nivel también vale (incluido un primer nivel que rompe la dirección); dos niveles malos no se salvan; tres niveles iguales tampoco.
- Prueba basada en propiedades (jqwik, 500 casos): con reportes aleatorios de 1 a 8 niveles, el resultado coincide con armar explícitamente cada reporte con un nivel menos.
- Con el input real, un script independiente en Python obtiene el mismo resultado con **otro algoritmo**: una programación dinámica de una sola pasada que, para cada dirección, calcula si se puede terminar en cada nivel habiendo descartado 0 o 1 niveles, sin armar listas sin un elemento.

**Resultado con el input real: 366**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Cantidad de reportes seguros | Lo mismo, tolerando un nivel malo |
| **Enfoque** | Diferencias entre vecinos (`Gatherers.windowSliding`), todas en [1, 3] o en [−3, −1] | Probar quitar cada nivel y reutilizar `esSeguro` |
| **Complejidad** | O(niveles) | O(n²) por reporte, con n ≤ 8 |
| **Resultado** | 306 | 366 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia02.java`
