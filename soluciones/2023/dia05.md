# Año 2023 — Día 5: Si Le Das un Fertilizante a una Semilla

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/5](https://adventofcode.com/2023/day/5). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con el bote del Elfo llegamos al jardinero, que maneja un "jardín" que parece más bien una granja. Le contamos que a la Isla de la Nieve no le llega agua, y se da cuenta, horrorizado, de que hace semanas cortaron el agua porque se quedaron **sin arena para filtrarla**, y nadie revisó por qué. Nos pide que vayamos a ver en un ferry que sale pronto y, mientras esperamos, que lo ayudemos a entender el último **almanaque** de la isla.

El almanaque (el input) empieza con la lista de **semillas** a plantar. Después tiene **siete mapas** que convierten un número de una categoría al de la siguiente: semilla → suelo → fertilizante → agua → luz → temperatura → humedad → **ubicación**. Los números se reusan entre categorías: el suelo 123 y el fertilizante 123 no tienen nada que ver.

Cada línea de un mapa tiene tres números: el comienzo del rango de **destino**, el comienzo del rango de **origen** y el **largo**. Por ejemplo, `50 98 2` quiere decir que 98 y 99 pasan a 50 y 51. Los números de origen que no aparecen en ninguna línea pasan **al mismo número**.

#### Ejemplo

```
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

...

humidity-to-location map:
60 56 37
56 93 4
```

Con el mapa semilla → suelo, del 0 al 49 quedan iguales, del 50 al 97 pasan a 52–99, y 98 y 99 pasan a 50 y 51. Siguiendo los siete mapas, la semilla 79 termina en la ubicación 82, la 14 en la 43, la 55 en la 86 y la 13 en la **35**, que es la menor.

#### Pregunta

¿Cuál es la menor ubicación que corresponde a alguna de las semillas?

### Solución

#### El input real

20 semillas y 7 mapas con 178 líneas en total. Los números llegan a unos 4 mil millones, así que no entran en un `int`: todo es `long`.

#### Tramos y desplazamientos

Cada línea de un mapa se guarda como un **tramo**: un `Rango` de origen y un **desplazamiento** (destino − origen). Convertir un número es buscar el tramo que lo contiene y sumarle el desplazamiento; si ninguno lo contiene, queda igual:

```java
// Dia05.java — Mapa.convertir()
for (Tramo tramo : tramos) {
    if (tramo.origen().contiene(numero)) return numero + tramo.desplazamiento();
}
return numero;
```

Al crear un mapa, los tramos se ordenan por origen y se comprueba que **no se solapen**: si dos tramos contuvieran el mismo número, no estaría claro a cuál corresponde, así que falla con un mensaje claro en vez de elegir uno.

La ubicación de una semilla es el resultado de pasarla por los siete mapas en orden, y la respuesta es la menor de las 20.

#### Verificación

- Prueba con el ejemplo (35).
- El mapa semilla → suelo convierte cada número como en la **tabla** del enunciado (los bordes 49/50, 97/98 y 99), y las cuatro semillas.
- El **recorrido completo** de cada semilla por las ocho categorías, como en el enunciado.
- Los números grandes del input real no desbordan, y el primer número justo fuera de un tramo queda igual.
- Un mapa con tramos solapados falla.
- Con el input real, un script independiente en Python recorre las líneas **tal como vienen**, sin ordenarlas ni usar rangos, y obtiene el mismo resultado.

**Resultado con el input real: 484023871**

---

## Parte 2

### Enunciado

Con tan pocas semillas, todos se morirían de hambre. Releyendo el almanaque, la línea `seeds:` en realidad describe **rangos**: los números vienen de a pares, el **inicio** y el **largo** de cada rango.

#### Ejemplo

`seeds: 79 14 55 13` describe dos rangos: de 79 a 92 (14 semillas) y de 55 a 67 (13 semillas), 27 en total. La menor ubicación sale de la semilla 82, que pasa por suelo 84, fertilizante 84, agua 84, luz 77, temperatura 45, humedad 46 y ubicación **46**.

#### Pregunta

Considerando todos los rangos de semillas, ¿cuál es la menor ubicación?

### Solución

#### Demasiadas semillas para probarlas una por una

En el input real, los rangos suman **2 445 858 670 semillas**. Convertirlas una por una por los siete mapas es posible pero lento, y no hace falta.

#### Convertir rangos enteros

Dentro de un tramo, todos los números se desplazan lo mismo. Así que un rango de números, al pasar por un mapa, se parte en **pedazos**: la parte que cae en cada tramo (que se desplaza con ese tramo) y lo que no cae en ninguno (que queda igual). Cada pedazo sigue siendo un rango. La utilidad `Rango` ya tiene las operaciones necesarias:

```java
// Dia05.java — Mapa.convertir(Rango)
List<Rango> convertidos = new ArrayList<>();
List<Rango> sinTramo = List.of(rango);
for (Tramo tramo : tramos) {
    rango.interseccion(tramo.origen())
            .ifPresent(parte -> convertidos.add(parte.desplazar(tramo.desplazamiento())));
    sinTramo = sinTramo.stream().flatMap(parte -> parte.restar(tramo.origen()).stream()).toList();
}
convertidos.addAll(sinTramo);
return Rango.unir(convertidos);
```

- `interseccion` da la parte del rango que cae en el tramo, y `desplazar` la convierte.
- `restar` va sacando de lo que queda la parte de cada tramo; lo que sobrevive a todos los tramos no cae en ninguno y pasa igual.
- `Rango.unir` junta los pedazos que se solapan o se tocan, para que la cantidad de rangos no crezca sin necesidad.

Se pasan los rangos de semillas por los siete mapas y la respuesta es el menor **inicio** entre los rangos de ubicaciones. En vez de miles de millones de números, se manejan unos cien rangos, y termina en unos 10 ms.

#### Verificación

- Prueba con el ejemplo (46), y con los rangos 79–92 y 55–67 (27 semillas) y el recorrido de la semilla 82 hasta 46.
- En el ejemplo coincide con **convertir las 27 semillas una por una**.
- Para cada mapa del ejemplo, convertir el rango 0–120 da exactamente los mismos números que convertir cada número del rango.
- Un rango que cruza el borde de un tramo se parte en dos: la parte de afuera queda igual y la de adentro se desplaza.
- Una cantidad impar de números de semilla falla con un mensaje claro.
- Con el input real, un script independiente en Python parte los rangos de otra forma: junta los **puntos de corte** de cada mapa que caen dentro del rango, lo parte ahí y desplaza cada pedazo según dónde cae su inicio. Además, lleva la cuenta de qué **semilla** da origen a cada pedazo. Obtiene el mismo mínimo, y la semilla que lo produce (3439828590), convertida número por número con los siete mapas, llega exactamente a esa ubicación.

**Resultado con el input real: 46294175**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Menor ubicación de las semillas | Lo mismo, con rangos de semillas |
| **Enfoque** | Pasar cada semilla por los siete mapas | Convertir rangos enteros, partiéndolos en los bordes de los tramos |
| **Complejidad** | O(semillas × líneas de los mapas) | O(rangos × líneas de los mapas), sin depender de cuántas semillas haya |
| **Resultado** | 484023871 | 46294175 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia05.java`
