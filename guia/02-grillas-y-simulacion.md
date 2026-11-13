# Grillas y simulación

[← Anterior: Texto, listas y secuencias](01-texto-listas-y-secuencias.md) · [Índice](README.md) · [Siguiente: Búsqueda de caminos →](03-busqueda-de-caminos.md)

Es el tipo más frecuente (13 días). La clave es **elegir bien el estado**: casi siempre es la casilla más algo (una dirección, un contador, una paridad), y con eso los bucles y las repeticiones se detectan solos.

Días de este tipo: 2023: 3, 11, 13, 14, 16, 22 · 2024: 4, 6, 12, 15, 25 · 2025: 4, 7.

## Fundamentos

- Convención fija de coordenadas: `x` = columna, `y` = fila, `y` crece hacia abajo. Mezclar `(fila, columna)` con `(x, y)` es el error más común.
- Un `record Punto(int x, int y)` tiene `equals` y `hashCode` gratis, así que sirve de clave en `HashMap` y `HashSet`.
- Direcciones como `enum` con giros a izquierda y derecha, opuesta y desplazamiento. Los espejos de 2023 día 16 son giros de 90°.
- Vecinos: 4 cardinales, 8 con diagonales, y filtrados por "transitable".
- Rotar y transponer: un problema "en las cuatro direcciones" se resuelve con una sola dirección y rotando (2023 día 14). Las líneas verticales son las horizontales de la grilla transpuesta (2023 día 13).

## Simular bien

- Procesar en el orden correcto: las rocas ruedan de arriba hacia abajo con un "tope" por columna (2023 día 14), los ladrillos caen en orden de altura (2023 día 22).
- Detectar bucles guardando estados vistos: (posición, dirección) en 2024 día 6 y 2023 día 16. Un haz que llega a un estado ya visitado no aporta nada.
- Mover grupos enteros: en 2024 día 15 se junta con BFS todo lo que se empuja, se borra y se reescribe, para no pisar casillas a mitad del movimiento.
- Una cola de trabajo en vez de barrer la grilla entera en cada vuelta (2025 día 4): al quitar algo solo cambian sus vecinos.
- Arreglos `boolean[]` o `int[]` indexados por `y * ancho + x` en lugar de `HashSet<Punto>` cuando la simulación se repite miles de veces.

## Contar sin recorrer

- Relleno (*flood fill*) desde afuera para separar interior y exterior, con un margen de una casilla alrededor.
- Regiones: perímetro = vecinos fuera de la región; lados = esquinas (convexas y cóncavas) en 2024 día 12.
- Expansión sin copiar la grilla: sumar el factor por cada fila y columna vacía antes de cada coordenada (2023 día 11).
- Mapas infinitos: la posición `(x, y)` vale lo que `(x mod ancho, y mod alto)`, con `Math.floorMod` para que los negativos no rompan (2023 día 21).

**En el repo**: `Grilla` (búsqueda, vecinos, `rellenar`, rotaciones, `obtenerCiclico`), `Punto`, `Direccion`, `Punto3D`, `SumaAcumulada2D`.
