# Año 2024 — Día 17: Computadora Cronoespacial

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2024/day/17](https://adventofcode.com/2024/day/17). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Los Historiadores aprietan el botón de su extraño aparato y esta vez sentimos que caemos. El aparato anuncia que falló el arranque y se despliega en una computadora entera, que termina en nuestras manos para depurarla.

Es una computadora de **3 bits**: su programa es una lista de números del 0 al 7. Tiene tres **registros**, `A`, `B` y `C`, que no se limitan a 3 bits: guardan cualquier entero.

El programa se lee **de a pares**: un código de instrucción y su **operando**. Un **puntero de instrucción** empieza en 0 y, salvo en los saltos, avanza 2 después de cada instrucción. Cuando intenta leer más allá del final del programa, la computadora se **detiene**.

Hay dos tipos de operando:

- **Literal**: vale el número mismo.
- **Combo**: 0 a 3 valen el número mismo; 4, 5 y 6 valen lo que tengan los registros `A`, `B` y `C`; el 7 está reservado y no aparece en programas válidos.

Las ocho instrucciones:

| Código | Nombre | Qué hace |
|---|---|---|
| 0 | `adv` | `A` = `A` dividido por 2 elevado al operando combo, truncado |
| 1 | `bxl` | `B` = `B` XOR operando literal |
| 2 | `bst` | `B` = operando combo módulo 8 |
| 3 | `jnz` | si `A` no es 0, salta a la posición del operando literal (y el puntero no avanza 2) |
| 4 | `bxc` | `B` = `B` XOR `C` (lee un operando pero lo ignora) |
| 5 | `out` | emite el operando combo módulo 8 |
| 6 | `bdv` | como `adv`, pero guarda en `B` |
| 7 | `cdv` | como `adv`, pero guarda en `C` |

#### Ejemplo

Algunos programas chicos:

- Con `C` = 9, el programa `2,6` deja `B` en 1.
- Con `A` = 10, el programa `5,0,5,1,5,4` emite `0,1,2`.
- Con `A` = 2024, el programa `0,1,5,4,3,0` emite `4,2,5,6,7,7,7,7,3,1,0` y deja `A` en 0.
- Con `B` = 29, el programa `1,7` deja `B` en 26.
- Con `B` = 2024 y `C` = 43690, el programa `4,0` deja `B` en 44354.

El input da los registros iniciales y el programa:

```
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
```

Al terminar, lo emitido es `4,6,3,5,6,3,5,2,1,0`.

#### Pregunta

Ejecutando el programa con los registros del input, ¿qué emite, unido con comas?

### Solución

#### El input real

Un programa de 16 números (8 instrucciones), con `B` y `C` en 0 y un `A` de 8 dígitos.

#### Un intérprete directo

La clase `Computadora` guarda los registros (`long`) y el programa, y `ejecutar` es el ciclo clásico de un intérprete: leer código y operando, avanzar el puntero y aplicar la instrucción.

```java
// Dia17.java — Computadora.ejecutar()
List<Integer> salida = new ArrayList<>();
int puntero = 0;
while (puntero + 1 < programa.length) {
    int codigo = programa[puntero];
    int operando = programa[puntero + 1];
    puntero += 2;
    switch (codigo) {
        case ADV -> a = dividir(combo(operando));
        case BXL -> b ^= operando;
        case BST -> b = combo(operando) & 7;
        case JNZ -> {
            if (a != 0) puntero = operando;
        }
        case BXC -> b ^= c;
        case OUT -> salida.add((int) (combo(operando) & 7));
        case BDV -> b = dividir(combo(operando));
        case CDV -> c = dividir(combo(operando));
        default -> throw new IllegalStateException("Instrucción inválida: " + codigo);
    }
}
return salida;
```

El puntero avanza 2 **antes** de aplicar la instrucción; así, el salto simplemente lo pisa y no hace falta un caso especial para "no avanzar". Módulo 8 es quedarse con los 3 bits bajos (`& 7`).

#### Dividir por una potencia de 2

Dividir `A` por 2<sup>n</sup> y truncar es correr `A` n bits a la derecha, porque `A` nunca es negativo. Hay una trampa: en Java, `a >> n` usa solo los 6 bits bajos de `n`, así que `a >> 64` da `a` y no 0. Como el exponente puede venir de un registro (operandos 5 y 6), se cubre aparte:

```java
// Dia17.java — Computadora.dividir()
if (a < 0) throw new IllegalStateException("El registro A es negativo: " + a);
return exponente >= Long.SIZE - 1 ? 0 : a >> exponente;
```

El operando combo 7 lanza una excepción en vez de devolver cualquier cosa, por si un programa inválido lo usara.

#### Verificación

- Prueba con el ejemplo del input (`4,6,3,5,6,3,5,2,1,0`) y con los **cinco** programas chicos del enunciado, comprobando la salida y los registros finales.
- `bdv` y `cdv` guardan en `B` y `C` sin tocar `A`.
- Dividir por 2<sup>64</sup> da 0, y el operando combo 7 falla.
- Con el input real, un script independiente en Python **traduce a mano** el programa a un ciclo de unas pocas líneas (sin intérprete) y emite lo mismo.

**Resultado con el input real: 7,4,2,5,1,4,6,0,4**

---

## Parte 2

### Enunciado

Leyendo el manual del aparato descubrimos el problema: el programa debería emitir **una copia de sí mismo**, pero el valor del registro `A` se corrompió. Hay que encontrar un nuevo valor inicial para `A` con el que lo emitido sea **exactamente** el programa.

#### Ejemplo

```
Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0
```

Si `A` empieza en **117440** (en vez de 2024, que se ignora), el programa emite `0,3,5,4,3,0`: una copia de sí mismo.

#### Pregunta

¿Cuál es el menor valor inicial **positivo** de `A` con el que el programa se emite a sí mismo?

### Solución

#### Probar todos no alcanza

El programa tiene 16 números, y cada vuelta de su ciclo emite uno y divide `A` por 8. Para emitir 16 valores, `A` tiene que tener 16 dígitos en base 8: está entre 8<sup>15</sup> y 8<sup>16</sup>, unos **35 billones** de valores. Imposible probarlos uno por uno.

#### La forma del programa

Tanto el ejemplo como el input son **un solo ciclo** que en cada vuelta:

1. calcula algo a partir de `A` y lo emite;
2. divide `A` por 8 (`adv 3`), es decir, descarta sus 3 bits bajos;
3. vuelve al inicio si `A` no es 0 (`jnz 0`).

`B` y `C` se recalculan desde `A` en cada vuelta, así que lo único que pasa de una vuelta a la siguiente es `A`. Entonces:

> lo que emite `A` = el valor de la primera vuelta, seguido de lo que emite `A / 8`.

Y al revés: si `A` emite los **últimos k** números del programa, `A / 8` emite los últimos k − 1. Los 3 bits más altos de `A` deciden el **último** valor emitido, los siguientes 3 el anteúltimo, y así.

#### Armar A de a 3 bits, desde los más altos

Se empieza con `A` = 0 y se le van agregando dígitos en base 8: a un valor que ya emite el final del programa, se le prueba cada dígito del 0 al 7 (`prefijo * 8 + digito`) y se **ejecuta el programa completo** para ver si ahora emite un número más del final. Es una búsqueda en profundidad:

```java
// Dia17.java — buscar()
if (desde < 0) return prefijo > 0 ? OptionalLong.of(prefijo) : OptionalLong.empty();
List<Integer> esperado = programa.subList(desde, programa.size());
for (int digito = 0; digito < 8; digito++) {
    long candidato = prefijo * 8 + digito;
    if (computadora.conA(candidato).ejecutar().equals(esperado)) {
        OptionalLong encontrado = buscar(computadora, programa, candidato, desde - 1);
        if (encontrado.isPresent()) return encontrado;
    }
}
return OptionalLong.empty();
```

Algunas observaciones:

- **Hace falta poder volver atrás.** En la primera vuelta de cada valor, `C` se calcula corriendo `A` a la derecha, así que el valor emitido depende también de bits más altos que los 3 nuevos. Un dígito puede servir ahora y no dejar ninguna opción para los siguientes; por eso es una búsqueda en profundidad y no una elección voraz.
- **El primero que encuentra es el menor.** Los dígitos se prueban del 0 al 7, empezando por los más significativos, y todas las respuestas tienen la misma cantidad de dígitos (16). Así que el orden en que se prueban es el orden numérico.
- **Nunca da una respuesta falsa.** Cada candidato se comprueba ejecutando el programa real; la forma supuesta solo decide qué valores se prueban. Si un programa no tuviera esa forma, la búsqueda no encontraría nada y fallaría con un mensaje claro.

En la práctica hay muy pocos candidatos por nivel (a lo sumo un centenar) y termina en unos 2 ms.

#### Verificación

- Prueba con el ejemplo (117440), y que con ese valor el programa del ejemplo se emite a sí mismo.
- En el ejemplo, la respuesta coincide con **probar todos los valores de `A` desde 1** hasta encontrar uno que sirva (fuerza bruta).
- Un programa que nunca puede copiarse falla con un mensaje claro.
- Con el input real, un script independiente en Python, con el programa traducido a mano, guarda **todos** los candidatos de cada nivel en vez de buscar en profundidad. Al final hay 16 valores de `A` que hacen que el programa se copie, y el menor coincide con la respuesta.

**Resultado con el input real: 164278764924605**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Qué emite el programa | Menor `A` con el que el programa se emite a sí mismo |
| **Enfoque** | Intérprete de las 8 instrucciones | Armar `A` de a 3 bits desde los más altos, con búsqueda en profundidad |
| **Complejidad** | O(instrucciones ejecutadas) | O(largo del programa × candidatos × 8 ejecuciones) |
| **Resultado** | 7,4,2,5,1,4,6,0,4 | 164278764924605 |

## Archivo de código

`src/main/java/advent/code/y2024/Dia17.java`
