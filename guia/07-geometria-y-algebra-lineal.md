# Geometría y álgebra lineal

[← Anterior: Matemática](06-matematica.md) · [Índice](README.md) · [Siguiente: Ciclos, periodicidad y extrapolación →](08-ciclos-periodicidad-y-extrapolacion.md)

Siete días se resuelven con fórmulas clásicas de geometría y álgebra en vez de recorrer. **La regla de oro es calcular exacto**: con `double`, un cruce justo en el borde o una solución entera puede salir como 2,9999999.

Días de este tipo: 2023: 10, 18, 24 · 2024: 8, 13 · 2025: 9, 10.

## Áreas y puntos enteros

- Fórmula de Gauss (del cordón de zapato): el doble del área de un polígono es `|Σ (xᵢ·yᵢ₊₁ − xᵢ₊₁·yᵢ)|`.
- Teorema de Pick, para vértices enteros: `área = interiores + borde/2 − 1`. Así se cuentan las casillas encerradas por un lazo (2023 día 10) o cavadas por una zanja de millones de metros (2023 día 18), sin dibujar nada.
- Puntos enteros de un lado `(dx, dy)`: `mcd(|dx|, |dy|)`.

## Rectas y productos cruzados

- Producto cruzado en 2D: `a × b = aₓ·b_y − a_y·bₓ`. Su signo dice de qué lado está un punto; si vale 0, son paralelos.
- Cruce de `p₁ + t·v₁` con `p₂ + s·v₂`: `t = ((p₂ − p₁) × v₂) / (v₁ × v₂)`. Para comparar sin dividir, se multiplica por el denominador dejado positivo (2023 día 24).
- Puntos sobre una recta con paso entero: el paso reducido por el mcd (2024 día 8).
- Rectángulos dentro de un polígono enorme: compresión de coordenadas, relleno del exterior y sumas acumuladas 2D para preguntar "¿está todo adentro?" en O(1) (2025 día 9).

## Sistemas de ecuaciones

- 2 × 2 con solución única: regla de Cramer con enteros y comprobar que la división sea exacta (2024 día 13).
- Linealizar: en 2023 día 24, "la piedra choca con cada granizo" es `(P − pᵢ) × (V − vᵢ) = 0`. El término no lineal `P × V` es igual para todos y se cancela al restar dos ecuaciones: queda un sistema lineal de 6 × 6.
- Eliminación gaussiana con fracciones exactas (`BigFraction`), hasta la forma escalonada reducida. Si sobran incógnitas, las libres se enumeran dentro de sus cotas (2025 día 10).
- Siempre verificar la solución contra todos los datos, no solo contra las ecuaciones usadas.

**En el repo**: `Poligono` (Gauss y Pick), `SistemaLineal` (eliminación exacta, solución única o forma escalonada con variables libres), `SumaAcumulada2D`, `Punto3D`.
