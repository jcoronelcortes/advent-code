package advent.code;

import java.util.function.Predicate;

/**
 * Sumas acumuladas 2D (<i>prefix sums</i>): después de una preparación
 * O(filas · columnas), la suma de cualquier rectángulo se obtiene en O(1).
 *
 * <p>Sirve para preguntar muchas veces "¿cuántas celdas de cierto tipo hay
 * en este rectángulo?" (ej. 2025 día 9: si un rectángulo toca alguna celda
 * exterior). Usa la misma convención que {@link Grilla}: {@code x} es la
 * columna e {@code y} la fila.</p>
 *
 * <pre>{@code
 * SumaAcumulada2D muros = SumaAcumulada2D.contar(grilla, c -> c == '#');
 * long cantidad = muros.suma(new Punto(2, 1), new Punto(5, 4));   // muros en ese rectángulo
 * }</pre>
 */
public final class SumaAcumulada2D {

    /** {@code acumulada[y][x]} = suma del rectángulo de filas {@code [0, y)} y columnas {@code [0, x)}. */
    private final long[][] acumulada;

    /**
     * Prepara las sumas acumuladas de una matriz.
     *
     * @param valores matriz rectangular de valores, indexada {@code [fila][columna]}
     */
    public SumaAcumulada2D(long[][] valores) {
        int filas = valores.length;
        int columnas = filas == 0 ? 0 : valores[0].length;
        acumulada = new long[filas + 1][columnas + 1];
        for (int y = 0; y < filas; y++) {
            if (valores[y].length != columnas) {
                throw new IllegalArgumentException("La fila " + y + " mide " + valores[y].length + " y no " + columnas);
            }
            for (int x = 0; x < columnas; x++) {
                acumulada[y + 1][x + 1] = valores[y][x] + acumulada[y][x + 1] + acumulada[y + 1][x] - acumulada[y][x];
            }
        }
    }

    /**
     * Prepara sumas acumuladas que cuentan las celdas de una grilla que
     * cumplen una condición (cada una vale 1, las demás 0).
     *
     * @param grilla    grilla de caracteres
     * @param condicion qué celdas cuentan
     * @return las sumas acumuladas
     */
    public static SumaAcumulada2D contar(Grilla grilla, Predicate<Character> condicion) {
        long[][] valores = new long[grilla.filas()][grilla.columnas()];
        for (Punto punto : grilla.puntos()) {
            if (condicion.test(grilla.obtener(punto))) valores[punto.y()][punto.x()] = 1;
        }
        return new SumaAcumulada2D(valores);
    }

    /**
     * Suma del rectángulo con esquinas opuestas {@code a} y {@code b}, ambas
     * incluidas y en cualquier orden.
     *
     * @param a una esquina (dentro de la matriz)
     * @param b la esquina opuesta (dentro de la matriz)
     * @return la suma de los valores del rectángulo
     */
    public long suma(Punto a, Punto b) {
        int x1 = Math.min(a.x(), b.x());
        int x2 = Math.max(a.x(), b.x());
        int y1 = Math.min(a.y(), b.y());
        int y2 = Math.max(a.y(), b.y());
        if (x1 < 0 || y1 < 0 || y2 >= filas() || x2 >= columnas()) {
            throw new IndexOutOfBoundsException("Rectángulo fuera de la matriz: " + a + " – " + b);
        }
        return acumulada[y2 + 1][x2 + 1] - acumulada[y1][x2 + 1] - acumulada[y2 + 1][x1] + acumulada[y1][x1];
    }

    /** @return cantidad de filas de la matriz original */
    public int filas() {
        return acumulada.length - 1;
    }

    /** @return cantidad de columnas de la matriz original */
    public int columnas() {
        return acumulada[0].length - 1;
    }
}
