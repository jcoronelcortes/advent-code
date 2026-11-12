# Año 2024 — Día 5: Cola de Impresión

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/5](https://adventofcode.com/2024/day/5). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Satisfechos con la búsqueda en Ceres, los Historiadores proponen revisar las pilas de papelería del subsuelo 17. El **departamento de impresión** del Polo Norte está más ocupado que nunca, y un elfo que maneja una impresora conocida nos llama: las actualizaciones del manual de seguridad para el lanzamiento del trineo no se imprimen bien.

Según los protocolos, las páginas nuevas del manual tienen que imprimirse en un **orden** muy específico. La notación `X|Y` significa que, si una actualización incluye las páginas X e Y, la página X tiene que imprimirse **en algún momento antes** que la Y (no necesariamente justo antes).

El elfo tiene las reglas de orden y las páginas de cada actualización (el input), separadas por una línea en blanco:

```
47|53
97|13
97|61
...
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
```

(El ejemplo completo tiene 21 reglas.) Cada actualización es una lista de páginas separadas por comas. Las reglas que mencionan páginas que no están en una actualización **no se aplican** a ella.

Además, los elfos necesitan la **página del medio** de cada actualización.

#### Ejemplo

| Actualización | ¿En orden? | Motivo |
|---------------|------------|--------|
| `75,47,61,53,29` | sí | cada página cumple las reglas con todas las demás |
| `97,61,53,29,13` | sí | |
| `75,29,13` | sí | |
| `75,97,47,61,53` | no | pone el 75 antes que el 97, y la regla `97\|75` lo prohíbe |
| `61,13,29` | no | rompe la regla `29\|13` |
| `97,13,75,29,47` | no | rompe varias reglas |

Las actualizaciones en orden tienen como página del medio 61, 53 y 29, que suman **143**.

#### Pregunta

Buscando las actualizaciones que ya están en el orden correcto, ¿cuánto suman sus páginas del medio?

### Solución

#### El input real

Hay 1176 reglas y 200 actualizaciones, de 5 a 23 páginas. Todas tienen una cantidad **impar** de páginas, así que la del medio siempre está bien definida.

#### Lectura con las utilidades

Las dos secciones se separan con `Utiles.secciones`, y cada línea se convierte en números con `Utiles.numerosPorLinea`, que sirve igual para `47|53` y para `75,47,61`: extrae los números sin importar el separador. Las reglas se guardan en un **conjunto** (`Set<Regla>`), para consultar si existe una regla en O(1).

#### Clave: buscar la regla contraria

Una actualización está en orden si ningún par de sus páginas contradice una regla. Para cada par de páginas en las posiciones `i < j`, el orden es incorrecto solo si existe la regla **contraria**, `página_j|página_i`:

```java
// Dia05.java — Manual.estaEnOrden()
for (int i = 0; i < paginas.size(); i++) {
    for (int j = i + 1; j < paginas.size(); j++) {
        if (reglas.contains(new Regla(paginas.get(j), paginas.get(i)))) return false;
    }
}
return true;
```

Así, las reglas que mencionan páginas ausentes nunca se consultan y se ignoran solas, como pide el enunciado. Con actualizaciones de hasta 23 páginas son como mucho 253 pares por actualización.

#### Verificación

- Prueba con el ejemplo (143) y con el resultado de cada una de sus 6 actualizaciones.
- Las reglas que mencionan páginas ausentes no se aplican, y una regla no necesita que las páginas estén contiguas para contar.
- Página del medio, y error si una actualización tiene una cantidad par de páginas.
- Con el input real, un script independiente en Python usa el enfoque inverso: recorre **las reglas** y, para cada una cuyas dos páginas están en la actualización, compara sus posiciones. Obtiene el mismo resultado.

**Resultado con el input real: 5991**

---

## Parte 2

### Enunciado

Mientras los elfos imprimen las actualizaciones que ya estaban en orden, nos queda un rato para arreglar las demás. Hay que tomar **solo las actualizaciones desordenadas** y, usando las reglas, ponerles las páginas en el orden correcto.

#### Ejemplo

| Actualización desordenada | En el orden correcto | Página del medio |
|---------------------------|----------------------|------------------|
| `75,97,47,61,53` | `97,75,47,61,53` | 47 |
| `61,13,29` | `61,29,13` | 29 |
| `97,13,75,29,47` | `97,75,47,29,13` | 47 |

La suma de las páginas del medio, ya ordenadas, es `47 + 29 + 47 =` **123**.

#### Pregunta

Después de ordenar correctamente solo las actualizaciones que estaban desordenadas, ¿cuánto suman sus páginas del medio?

### Solución

#### Las reglas como comparador

La forma natural de ordenar es usar las reglas como **comparador**: `a` va antes que `b` si existe la regla `a|b`.

```java
// Dia05.java — Manual.ordenar()
List<Integer> ordenadas = new ArrayList<>(paginas);
ordenadas.sort((a, b) -> {
    if (a.equals(b)) return 0;
    if (reglas.contains(new Regla(a, b))) return -1;
    if (reglas.contains(new Regla(b, a))) return 1;
    throw new IllegalStateException("No hay regla entre las páginas " + a + " y " + b);
});
```

#### Por qué esto es correcto (y cuándo no lo sería)

Un comparador solo sirve para ordenar si define un **orden total y transitivo**. Las reglas por sí solas no lo garantizan, así que antes de confiar en este enfoque se revisó el input:

- **Dentro de cada actualización, todo par de páginas tiene una regla.** Entonces el comparador nunca queda sin respuesta.
- **El orden correcto es único:** un orden topológico (algoritmo de Kahn) de las reglas restringidas a cada actualización nunca tuvo dos candidatos a la vez.
- **Pero el conjunto completo de reglas tiene ciclos** entre sus 49 páginas: no es un orden global. Solo es un orden total restringido a las páginas de cada actualización, que es lo único que necesita el comparador.

Si en otro input dos páginas no tuvieran regla, el comparador lanza una excepción en vez de devolver un orden arbitrario.

#### Verificación

- Prueba con el ejemplo (123), y con los tres órdenes correctos que muestra el enunciado.
- Ordenar una actualización que ya estaba en orden no la cambia, y el resultado de ordenar cualquier actualización del ejemplo siempre está en orden.
- Si dos páginas no tienen regla entre ellas, se lanza una excepción.
- Con el input real, un script independiente en Python obtiene el mismo resultado con un **orden topológico** (algoritmo de Kahn) de las reglas restringidas a las páginas de cada actualización, que no depende de que todo par tenga una regla.

**Resultado con el input real: 5479**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma de las páginas del medio de las actualizaciones en orden | Lo mismo, después de ordenar las desordenadas |
| **Enfoque** | Conjunto de reglas; buscar la regla contraria para cada par de páginas | Ordenar usando las reglas como comparador |
| **Complejidad** | O(páginas²) por actualización | O(páginas · log páginas) por actualización |
| **Resultado** | 5991 | 5479 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia05.java`
