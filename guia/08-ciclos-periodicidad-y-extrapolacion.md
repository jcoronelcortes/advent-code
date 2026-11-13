# Ciclos, periodicidad y extrapolación

[← Anterior: Geometría y álgebra lineal](07-geometria-y-algebra-lineal.md) · [Índice](README.md) · [Siguiente: Grafos avanzados →](09-grafos-avanzados.md)

"Mil millones de pasos" o "¿cuándo coinciden todos?" significan que **algo se repite y hay que encontrar el patrón**, no simular. Pero el patrón hay que comprobarlo: suponer un período que no lo es da una respuesta equivocada sin aviso.

Días de este tipo: 2023: 8, 20, 21 (y la parte 2 de 2023 día 14).

## Detectar un ciclo

- Si el estado es determinista y finito, tarde o temprano se repite. Guardar cada estado con el paso en que apareció; la primera repetición da el inicio y el largo del ciclo.
- El estado en el paso `N` sin simularlo: `estados[inicio + (N − inicio) mod largo]` (2023 día 14: mil millones de ciclos de giro, con un ciclo de largo 72).
- El estado tiene que ser inmutable y tener `equals`/`hashCode`. Una grilla mutable guardada en un mapa y después modificada rompe todo.

## Varios ciclos que tienen que coincidir

- Si cada parte se repite con período `kᵢ` desde el principio, coinciden por primera vez en `mcm(kᵢ)` (2023 días 8 y 20).
- Comprobar que el ciclo empiece en 0: que el segundo evento llegue en `2k`. Si no, el mcm no es la respuesta. Con ciclos desfasados se necesita el teorema chino del resto.
- En 2023 día 20, las cuatro subredes se simularon por separado y se demostró que cada una vuelve a su estado y solo dispara en los múltiplos de su período.

## Extrapolar

- Si una cantidad crece como un polinomio en `k`, con tres muestras se fija una cuadrática: `f(K) = f(0) + K·Δ + K(K−1)/2·Δ²`.
- En 2023 día 21 (26501365 = 65 + 131·202300 pasos), el jardín infinito crece un mapa por lado en cada `k`. Una cuarta muestra confirmó que las segundas diferencias son constantes antes de extrapolar.
- Sin muestra de control, una extrapolación es una apuesta.

**En el repo**: `Ciclo.detectar(inicial, siguiente)` y `estadoEn(paso)`; `Matematicas.mcm` y `restoChino`.
