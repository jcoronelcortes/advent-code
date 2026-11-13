# Matemática

[← Anterior: Rangos e intervalos](05-rangos-e-intervalos.md) · [Índice](README.md) · [Siguiente: Geometría y álgebra lineal →](07-geometria-y-algebra-lineal.md)

Los números de AoC llegan a 10¹⁴ o más, así que **casi todo se hace en `long` y con cuentas exactas**. Muchas partes 2 que parecen de simulación se vuelven una fórmula al mirar la estructura.

Días de este tipo: 2023: 6 · 2024: 14, 22 · 2025: 1, 2.

## Divisibilidad y aritmética modular

- mcd y mcm: el mcm de los períodos da cuándo coinciden varios ciclos (2023 días 8 y 20). El mcd reduce un paso a su mínima expresión (2024 día 8).
- Módulo no negativo: en Java `-7 % 5` es `-2`. Para posiciones circulares, `Math.floorMod(a, m)`.
- Posición tras `t` pasos sin simular: `(p + v·t) mod tamaño` (2024 día 14).
- Teorema chino del resto: combina "pasa cada 101 pasos en x" y "cada 103 en y" en un solo instante (2024 día 14). Necesita inversos modulares, que salen del algoritmo de Euclides extendido.
- Operaciones de bits: `x ^ (x << 6)`, `& mask` para generadores tipo xorshift (2024 día 22); luces como bits y botones como XOR (2025 día 10).

## Fórmulas cerradas en vez de recorrer

- Desigualdades cuadráticas: en 2023 día 6, `h·(t − h) > r` es una parábola simétrica; alcanza con encontrar el primer `h` que gana, con búsqueda binaria o con la fórmula.
- Generar en vez de filtrar: los IDs repetidos de 2025 día 2 son `x·(10^k + 1)`, se generan directo y se suman con series aritméticas; los que se cuentan de más se corrigen con inclusión-exclusión (función de Möbius).
- Suma de `a` a `b`: `(a + b)·(b − a + 1) / 2`, dividiendo primero el factor par para no desbordar.

## Búsqueda binaria sobre la respuesta

- Si "funciona con `x`" es falso…falso, verdadero…verdadero, el primer verdadero sale en `log₂(intervalo)` pruebas.
- El punto medio como `lo + (hi − lo) / 2` para no desbordar cuando el intervalo abarca todos los `long`.

## Desbordes

- `Math.addExact`, `multiplyExact` y `toIntExact` fallan en vez de dar un número equivocado. Es mejor un error que una respuesta mala sin aviso.
- Si un producto puede pasar de 9,2·10¹⁸ (por ejemplo 10¹⁴ × 10⁶), usar `BigInteger`.

**En el repo**: `Matematicas` (mcd/mcm, módulo, potencia e inverso modular, resto chino, suma aritmética, dígitos, primos, Möbius, suma y producto exactos) y `Busqueda.primero`.
