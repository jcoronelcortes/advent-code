# Año 2025 — Día 1: Entrada Secreta

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2025/day/1](https://adventofcode.com/2025/day/1). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Llegamos a la entrada secreta de la base del Polo Norte, pero la contraseña cambió y no sirve. En la pared hay un documento de seguridad que explica dónde encontrarla: está guardada en una **caja fuerte con un dial circular** numerado del **0 al 99**, que al comenzar apunta al **50**.

El documento trae una lista de giros para aplicar al dial, uno por línea (es el input). Cada giro tiene la forma `L68` o `R48`:

- la letra es la dirección: `L` gira a la **izquierda** (hacia números menores) y `R` a la **derecha** (hacia números mayores);
- el número es la cantidad de **clics** que se gira.

El dial es circular: un clic a la izquierda desde el 0 lleva al 99, y un clic a la derecha desde el 99 lleva al 0.

Según el documento, la contraseña no es la posición final del dial, sino **cuántas veces queda apuntando al 0 al terminar un giro**.

#### Ejemplo

| Giro | Posición al terminar | ¿Termina en 0? |
|------|----------------------|----------------|
| inicio | 50 | |
| `L68` | 82 | |
| `L30` | 52 | |
| `R48` | **0** | ✓ |
| `L5` | 95 | |
| `R60` | 55 | |
| `L55` | **0** | ✓ |
| `L1` | 99 | |
| `L99` | **0** | ✓ |
| `R14` | 14 | |
| `L82` | 32 | |

El dial termina en 0 tres veces, así que la contraseña del ejemplo es **3**.

#### Pregunta

Aplicando todos los giros del input, ¿cuántas veces termina el dial apuntando al 0?

### Solución

#### Paso 1 — Parsear cada instrucción

Cada línea tiene la forma `[LR]\d+`. La convertimos una sola vez en un `record Rotacion`, que valida el formato con una expresión regular y separa la dirección del número. Ambas partes usan las mismas rotaciones:

```java
// Dia01.java — Rotacion.parsear()
static Rotacion parsear(String linea) {
    String texto = linea.trim();
    if (!texto.matches("[LR]\\d+")) {
        throw new IllegalArgumentException("Rotación inválida: '" + linea + "'");
    }
    return new Rotacion(texto.charAt(0), Integer.parseInt(texto.substring(1)));
}
```

> Una línea inválida lanza un error en vez de ignorarse en silencio: si el input viene mal pegado, es mejor enterarse. Las líneas en blanco sí se ignoran (`leerRotaciones`).

#### Paso 2 — Calcular la nueva posición

El dial es circular con 100 posiciones. Usamos aritmética modular para manejar los extremos (0 → 99 y 99 → 0). En Java el operador `%` puede devolver valores negativos (`-18 % 100 = -18`), así que usamos `Matematicas.modulo`, que siempre devuelve un valor en `[0, 100)`:

```java
// Dia01.java — Rotacion
int delta() {
    return direccion == 'R' ? distancia : -distancia;
}

int aplicar(int posicion) {
    return (int) Matematicas.modulo(posicion + delta(), TAMANIO_DIAL);
}
```

Ejemplo: desde 50 con L68 → `modulo(50 - 68, 100) = modulo(-18, 100) = 82` ✓

#### Paso 3 — Contar los ceros

Después de cada rotación, verificamos si el dial quedó en 0:

```java
// Dia01.java — parte1()
for (Rotacion rotacion : leerRotaciones(lineas)) {
    posicion = rotacion.aplicar(posicion);
    if (posicion == 0) contador++;
}
```

**Resultado con el input real: 1043**

---

## Parte 2

### Enunciado

Con esa contraseña la puerta no abre. Mientras esperamos armamos un muñeco de nieve, y al rodar las bolas aparece otro documento de seguridad tirado en la nieve: por nuevos protocolos, hay que usar otro **método de contraseña**.

Con el método nuevo hay que contar **cada clic** que deja al dial apuntando al 0, sin importar si ocurre **en medio de un giro** o al terminarlo. Los giros son los mismos de la parte 1.

#### Ejemplo

| Giro | Posición al terminar | Veces que pasa por 0 |
|------|----------------------|----------------------|
| `L68` desde 50 | 82 | 1 (en medio del giro) |
| `L30` | 52 | 0 |
| `R48` | 0 | 1 (al terminar) |
| `L5` | 95 | 0 |
| `R60` | 55 | 1 (en medio del giro) |
| `L55` | 0 | 1 (al terminar) |
| `L1` | 99 | 0 |
| `L99` | 0 | 1 (al terminar) |
| `R14` | 14 | 0 |
| `L82` | 32 | 1 (en medio del giro) |

Son 3 veces al terminar un giro más 3 en medio de un giro: la nueva contraseña es **6**.

Hay que tener cuidado con los giros largos: desde el 50, un solo giro `R1000` pasa por el 0 **diez veces** antes de volver al 50.

#### Pregunta

Con el método nuevo, ¿cuál es la contraseña que abre la puerta?

### Solución

#### El problema de simular click a click

La solución obvia sería simular cada click individual, pero con distancias de hasta 999 en miles de instrucciones, eso sería muy lento.

#### Solución matemática

Para una rotación de distancia `d` desde posición `p`, podemos calcular directamente cuántos clicks caen en 0 sin simularlos.

**Clave:** el dial toca el 0 en pasos específicos, no en todos:
- Girando a la **izquierda (L)**: el primer click en 0 ocurre en el paso `p` (cuando llegamos desde 1 hasta 0), luego cada 100 pasos.
- Girando a la **derecha (R)**: el primer click en 0 ocurre en el paso `100 - p` (cuando llegamos desde 99 hasta 0), luego cada 100 pasos.

```java
// Dia01.java — contarPasosPorCero()
static long contarPasosPorCero(int posicion, char direccion, int distancia) {
    if (direccion == 'L') {
        if (posicion == 0) return distancia / TAMANIO_DIAL;
        if (distancia >= posicion) return (distancia - posicion) / TAMANIO_DIAL + 1;
        return 0;
    } else {
        int pasoAlCero = (posicion == 0) ? TAMANIO_DIAL : (TAMANIO_DIAL - posicion);
        if (distancia >= pasoAlCero) return (distancia - pasoAlCero) / TAMANIO_DIAL + 1;
        return 0;
    }
}
```

**Verificación con el ejemplo `R1000` desde 50:**
- Primer paso al 0: `100 - 50 = 50`
- Pasos restantes: `(1000 - 50) / 100 = 9`
- Total: `9 + 1 = 10` ✓

**Verificación con `L68` desde 50:**
- Primer paso al 0: el paso número `50`
- Pasos restantes: `(68 - 50) / 100 = 0`
- Total: `0 + 1 = 1` ✓

#### Integración en parte2

```java
// Dia01.java — parte2()
for (Rotacion rotacion : leerRotaciones(lineas)) {
    contador += contarPasosPorCero(posicion, rotacion.direccion(), rotacion.distancia());
    posicion = rotacion.aplicar(posicion);
}
```

> Importante: primero contamos los pasos por cero con la posición **antes** de moverse, y luego actualizamos la posición.

**Resultado con el input real: 5963**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | ¿Cuántas veces termina en 0? | ¿Cuántas veces pasa o termina en 0? |
| **Enfoque** | Simulación directa | Cálculo matemático |
| **Complejidad** | O(n) | O(n) |
| **Resultado** | 1043 | 5963 |

## Archivo de código

`src/main/java/advent/code/y2025/Dia01.java`
