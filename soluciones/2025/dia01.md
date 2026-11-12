# Año 2025 — Día 1: Entrada Secreta

## Descripción del problema

Llegamos a la entrada secreta del Polo Norte y la contraseña cambió. Un documento en la pared explica que la contraseña está guardada en una caja de seguridad con un dial circular que va del **0 al 99**. El dial comienza apuntando al **50**.

El input es una secuencia de instrucciones de rotación, una por línea. Cada instrucción tiene la forma `L68` o `R48`, donde la letra indica la dirección (`L` = izquierda, hacia números menores; `R` = derecha, hacia números mayores) y el número indica cuántos clicks girar.

El dial es circular: girar a la izquierda desde el 0 lleva al 99, y girar a la derecha desde el 99 lleva al 0.

---

## Parte 1

### Pregunta

¿Cuántas veces **termina** el dial apuntando al 0 después de completar cada rotación?

### Ejemplo

| Instrucción | Posición final | ¿Llega a 0? |
|-------------|---------------|-------------|
| Inicio      | 50            |             |
| L68         | 82            |             |
| L30         | 52            |             |
| R48         | **0**         | ✓           |
| L5          | 95            |             |
| R60         | 55            |             |
| L55         | **0**         | ✓           |
| L1          | 99            |             |
| L99         | **0**         | ✓           |
| R14         | 14            |             |
| L82         | 32            |             |

Resultado del ejemplo: **3**

### Desarrollo de la solución

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

### Pregunta

Ahora hay que contar cuántas veces el dial **pasa por 0 o termina en 0** durante cada rotación, incluyendo las posiciones intermedias de cada click.

### Ejemplo

Con las mismas instrucciones, ahora también cuentan los pasos intermedios:

| Instrucción | ¿Pasa por 0? | Conteo |
|-------------|-------------|--------|
| L68 desde 50 | Sí, 1 vez (al bajar de 1 a 0) | +1 |
| L30 desde 82 | No                             | +0 |
| R48 desde 52 | Sí, termina en 0              | +1 |
| L5  desde 0  | No                             | +0 |
| R60 desde 95 | Sí, 1 vez (al subir de 99 a 0) | +1 |
| L55 desde 55 | Sí, termina en 0              | +1 |
| L1  desde 0  | No                             | +0 |
| L99 desde 99 | Sí, termina en 0              | +1 |
| R14 desde 0  | No                             | +0 |
| L82 desde 14 | Sí, 1 vez (al bajar de 1 a 0) | +1 |

Total: **6** (3 finales + 3 intermedios)

> Caso especial del enunciado: `R1000` desde 50 pasa por 0 exactamente **10 veces**.

### Desarrollo de la solución

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