# Rangos e intervalos

[← Anterior: Programación dinámica, backtracking y voraz](04-programacion-dinamica-backtracking-y-voraz.md) · [Índice](README.md) · [Siguiente: Matemática →](06-matematica.md)

Cuando aparecen rangos con miles de millones de valores, **se opera con los extremos, nunca con los valores**: un rango se parte, se recorta y se une, y el costo depende de cuántos rangos hay, no de su largo.

Días de este tipo: 2023: 5, 19 · 2025: 5.

## Operaciones que hay que dominar

- Intersección de `[a, b]` y `[c, d]`: `[max(a, c), min(b, d)]`, vacía si el inicio supera al fin.
- Resta: deja 0, 1 o 2 pedazos. Partir un rango contra los tramos de un mapa produce la parte que se transforma y la que sigue igual (2023 día 5).
- Unión de solapados: ordenar por inicio y fusionar mientras el siguiente empiece antes del fin actual (+1 si se unen rangos enteros contiguos).
- Pertenencia en muchos rangos: unirlos una vez y hacer búsqueda binaria (2025 día 5).
- Decidir y respetarlo siempre: rangos cerrados `[a, b]` o semiabiertos `[a, b)`. El largo de `[a, b]` es `b − a + 1`; mezclarlos da errores por uno.

## Cajas de varias dimensiones

- Un producto de rangos (uno por eje) se maneja igual: una condición sobre un eje parte la caja en dos (2023 día 19, con cuatro categorías de 1 a 4000).
- Si cada partición deja partes disjuntas, el total es la suma de los volúmenes, sin restar intersecciones.

## Compresión de coordenadas

- Cuando las coordenadas son enormes pero los puntos son pocos, se reemplazan por sus índices ordenados, se resuelve en una grilla chica y se pesa cada celda por su tamaño real (2025 día 9).
- Hay que agregar los huecos entre coordenadas consecutivas y un borde, o el relleno desde afuera se cuela.

**En el repo**: `Rango` (intersección, resta, unión), `ConjuntoRangos` (pertenencia y cantidad sin enumerar), `CompresionCoordenadas`; también `RangeSet` de Guava.
