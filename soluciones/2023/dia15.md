# Año 2023 — Día 15: Biblioteca de Lentes

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/15](https://adventofcode.com/2023/day/15). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

El plato reflector, ya enfocado, manda toda la luz a la pared de una instalación dentro de la montaña más grande de la Isla de la Lava: la **Fábrica de Producción de Lava**. Un reno nervioso nos equipa con antiparras y casco y nos lleva a la sala que recoge la luz, llena de lentes. Ahí nos entrega un **manual de inicialización** para poner en marcha la fábrica.

Antes de empezar, el manual pide usar el algoritmo **HASH**, que convierte cualquier texto en un número de 0 a 255. Se parte de un valor 0 y, por cada carácter del texto, en orden:

1. se le suma el código ASCII del carácter;
2. se multiplica por 17;
3. se reemplaza por el resto de dividirlo por 256.

El valor final es el HASH. Por ejemplo, el HASH de `HASH` es **52**.

El input es la **secuencia de inicialización**: una lista de pasos separados por comas, en una sola línea larga (los saltos de línea se ignoran). Para comprobar que el HASH está bien, el manual propone sumar el HASH de cada paso.

#### Ejemplo

```
rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
```

Son 11 pasos, cuyos HASH son 30, 253, 97, 47, 14, 180, 9, 197, 48, 214 y 231. La suma es **1320**.

#### Pregunta

¿Cuál es la suma del HASH de cada paso de la secuencia de inicialización?

### Solución

#### El input real

Una sola línea de unos 23 000 caracteres con 4000 pasos.

#### Separar los pasos

Se unen las líneas (por si la secuencia viene partida) y se separa por comas. Una secuencia vacía o con un paso vacío (dos comas seguidas, o una coma al final) falla con un mensaje claro en vez de sumar un HASH de 0 que no corresponde.

#### El HASH

Es una traducción directa de las tres reglas. El valor nunca pasa de (255 + 127) · 17, así que cabe holgado en un `int`:

```java
// Dia15.java — hash()
int valor = 0;
for (int i = 0; i < texto.length(); i++) {
    valor = (valor + texto.charAt(i)) * MULTIPLICADOR % MODULO;
}
return valor;
```

#### Verificación

- Prueba con el ejemplo (1320).
- El HASH de `HASH` es 52, y el de cada paso del ejemplo coincide con los once valores del enunciado.
- Los saltos de línea se ignoran aunque partan un paso por la mitad.
- Una secuencia vacía o con un paso vacío falla.
- Con el input real, un script independiente en Python calcula el HASH con enteros sin límite y toma el resto por 256 **una sola vez al final** (da lo mismo porque sumar y multiplicar respetan los restos), y obtiene el mismo resultado.

**Resultado con el input real: 516657**

---

## Parte 2

### Enunciado

El reno nos devuelve la página robada y confirma que el HASH está bien. El manual sigue: hay **256 cajas** numeradas de 0 a 255, una detrás de otra, por las que pasa la luz. Cada caja tiene ranuras para lentes, y junto a la pared hay una biblioteca de lentes con **distancias focales** del 1 al 9.

Cada paso de la secuencia empieza con una **etiqueta** de letras; el HASH de la etiqueta indica en qué caja se trabaja. Después viene la operación:

- **`-`**: si en esa caja hay una lente con esa etiqueta, se saca, y las lentes que estaban detrás avanzan para cerrar el hueco sin cambiar de orden. Si no hay, no pasa nada.
- **`=` seguido de un número**: hay que poner una lente con esa distancia focal, marcada con la etiqueta. Si en la caja ya hay una lente con esa etiqueta, se **reemplaza en su mismo lugar**; si no, la nueva va **al fondo**, detrás de las que ya están.

Al terminar, se calcula el **poder de enfoque** de cada lente: (1 + número de su caja) × su posición dentro de la caja (1 la primera) × su distancia focal. La respuesta es la suma de todos.

#### Ejemplo

Con la misma secuencia, al final solo hay lentes en dos cajas:

```
Caja 0: [rn 1] [cm 2]
Caja 3: [ot 7] [ab 5] [pc 6]
```

En la caja 3, `pc` se sacó y se volvió a poner, así que quedó al fondo, y `ot` se reemplazó (de 9 a 7) sin moverse. Los poderes son 1·1·1 + 1·2·2 + 4·1·7 + 4·2·5 + 4·3·6 = **145**.

#### Pregunta

¿Cuál es el poder de enfoque total después de seguir la secuencia de inicialización?

### Solución

#### Cada caja es un `LinkedHashMap`

Una caja necesita buscar por etiqueta, reemplazar sin mover, sacar cerrando el hueco y agregar al fondo. Un `LinkedHashMap` de etiqueta a distancia focal hace las cuatro cosas: recuerda el orden de inserción, `put` de una clave que ya está cambia el valor **sin moverla**, y `remove` deja al resto en el mismo orden. Además, como la clave sacada se olvida, si se vuelve a poner entra al fondo, igual que la lente `pc` del ejemplo:

```java
// Dia15.java — colocarLentes()
for (String paso : pasos) {
    Matcher partes = PASO.matcher(paso);
    if (!partes.matches()) throw new IllegalArgumentException("Paso inválido: '" + paso + "'");
    String etiqueta = partes.group(1);
    SequencedMap<String, Integer> caja = cajas.get(hash(etiqueta));
    if (partes.group(2) != null) {
        caja.remove(etiqueta);
    } else {
        caja.put(etiqueta, Integer.parseInt(partes.group(3)));
    }
}
```

Cada paso se valida con una expresión regular (letras, y después `-` o `=` con un dígito del 1 al 9); uno que no la cumple falla con un mensaje claro. Al final se recorre cada caja en orden sumando `(caja + 1) · posición · focal`.

#### Verificación

- Prueba con el ejemplo (145).
- Al final del ejemplo, solo las cajas 0 y 3 tienen lentes, con las etiquetas y distancias focales en el orden del enunciado.
- Reemplazar una lente no la mueve; sacarla y volver a ponerla la manda al fondo; sacar una etiqueta que no está no hace nada.
- Coincide con una **simulación con listas de pares** que busca la etiqueta a mano.
- Un paso con formato inválido falla.
- Con el input real, un script independiente en Python no simula las cajas: guarda en qué paso entró cada etiqueta que sigue puesta (sacarla borra el registro, reemplazarla lo conserva), y ordena cada caja por ese momento. Obtiene el mismo resultado.

**Resultado con el input real: 210906**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Suma del HASH de cada paso | Poder de enfoque tras colocar las lentes |
| **Enfoque** | Aplicar el HASH carácter por carácter | Cada caja es un `LinkedHashMap` de etiqueta a focal |
| **Complejidad** | O(largo de la secuencia) | O(largo de la secuencia) |
| **Resultado** | 516657 | 210906 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia15.java`
