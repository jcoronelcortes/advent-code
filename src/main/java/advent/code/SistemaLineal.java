package advent.code;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Sistemas de ecuaciones lineales resueltos de forma <b>exacta</b>, con
 * eliminación gaussiana sobre fracciones ({@link BigFraction}).
 *
 * <p>Con {@code double} los errores de redondeo pueden convertir una solución
 * entera en {@code 2.9999999}, o un sistema sin solución en uno que parece
 * tenerla; con fracciones no hay redondeo. Sirve para sistemas chicos con
 * coeficientes grandes (ej. 2023 día 24: 6 incógnitas y coeficientes de
 * 10¹⁷) o con variables libres (ej. 2025 día 10).</p>
 *
 * <p>El sistema se da como matriz <em>aumentada</em> {@code [A | b]}: cada
 * fila es una ecuación, con los coeficientes de las incógnitas y, en la
 * última columna, el término independiente.</p>
 *
 * <pre>{@code
 * // x + y = 3, x − y = 1
 * BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{1, 1}, {1, -1}}, new long[]{3, 1});
 * SistemaLineal.resolverUnica(sistema);   // Optional[[2, 1]]
 * }</pre>
 */
public final class SistemaLineal {

    private SistemaLineal() {}

    /**
     * La forma escalonada reducida de un sistema.
     *
     * <p>Las primeras {@code pivotes.length} filas de {@code matriz}
     * corresponden, en orden, a las columnas de {@code pivotes}: en la fila
     * {@code p} la incógnita {@code pivotes[p]} tiene coeficiente 1 y no
     * aparece en ninguna otra fila. Así, cada incógnita pivote queda
     * despejada en función de las libres:
     * {@code x[pivotes[p]] = matriz[p][n] − Σ matriz[p][f]·x[f]} sobre las
     * libres {@code f}.</p>
     *
     * @param matriz      la matriz aumentada reducida
     * @param pivotes     las columnas con pivote, en orden
     * @param libres      las columnas sin pivote (variables libres), en orden
     * @param consistente si el sistema tiene solución (ninguna fila queda como {@code 0 = c} con {@code c ≠ 0})
     */
    public record Escalonada(BigFraction[][] matriz, int[] pivotes, int[] libres, boolean consistente) {

        /** @return si la solución es única: tiene solución y no hay variables libres */
        public boolean tieneSolucionUnica() {
            return consistente && libres.length == 0;
        }
    }

    /**
     * Arma la matriz aumentada de un sistema con coeficientes enteros.
     *
     * @param coeficientes una fila por ecuación, con un coeficiente por incógnita
     * @param terminos     el término independiente de cada ecuación
     * @return la matriz {@code [A | b]}
     * @throws IllegalArgumentException si las filas no tienen todas el mismo largo
     *                                  o no hay un término por ecuación
     */
    public static BigFraction[][] aumentada(long[][] coeficientes, long[] terminos) {
        if (coeficientes.length != terminos.length) {
            throw new IllegalArgumentException("Hay " + coeficientes.length + " ecuaciones y "
                    + terminos.length + " términos independientes");
        }
        int incognitas = coeficientes.length == 0 ? 0 : coeficientes[0].length;
        BigFraction[][] matriz = new BigFraction[coeficientes.length][incognitas + 1];
        for (int i = 0; i < coeficientes.length; i++) {
            if (coeficientes[i].length != incognitas) {
                throw new IllegalArgumentException("Las ecuaciones tienen distinta cantidad de coeficientes");
            }
            for (int j = 0; j < incognitas; j++) matriz[i][j] = new BigFraction(coeficientes[i][j]);
            matriz[i][incognitas] = new BigFraction(terminos[i]);
        }
        return matriz;
    }

    /**
     * Lleva el sistema a su forma escalonada reducida (Gauss–Jordan).
     *
     * <p>Columna por columna, se busca una fila con coeficiente distinto de
     * cero que todavía no tenga pivote, se la divide por ese coeficiente para
     * dejar un 1 y se anula esa columna en todas las demás filas. Las columnas
     * en las que no hay pivote son variables libres.</p>
     *
     * @param aumentada la matriz {@code [A | b]} (no se modifica)
     * @return la forma escalonada reducida
     * @throws IllegalArgumentException si las filas no tienen todas el mismo largo
     */
    public static Escalonada escalonar(BigFraction[][] aumentada) {
        int filas = aumentada.length;
        int columnas = filas == 0 ? 1 : aumentada[0].length;
        int incognitas = columnas - 1;
        BigFraction[][] m = new BigFraction[filas][];
        for (int i = 0; i < filas; i++) {
            if (aumentada[i].length != columnas) {
                throw new IllegalArgumentException("Las filas de la matriz tienen distinto largo");
            }
            m[i] = aumentada[i].clone();
        }

        List<Integer> pivotes = new ArrayList<>();
        int fila = 0;
        for (int columna = 0; columna < incognitas && fila < filas; columna++) {
            int elegida = fila;
            while (elegida < filas && m[elegida][columna].equals(BigFraction.ZERO)) elegida++;
            if (elegida == filas) continue;
            BigFraction[] temporal = m[fila];
            m[fila] = m[elegida];
            m[elegida] = temporal;

            BigFraction pivote = m[fila][columna];
            for (int j = columna; j < columnas; j++) m[fila][j] = m[fila][j].divide(pivote);
            for (int i = 0; i < filas; i++) {
                if (i == fila || m[i][columna].equals(BigFraction.ZERO)) continue;
                BigFraction factor = m[i][columna];
                for (int j = columna; j < columnas; j++) m[i][j] = m[i][j].subtract(factor.multiply(m[fila][j]));
            }
            pivotes.add(columna);
            fila++;
        }

        boolean consistente = true;
        for (int i = fila; i < filas; i++) {
            if (!m[i][incognitas].equals(BigFraction.ZERO)) consistente = false;
        }
        int[] columnasPivote = pivotes.stream().mapToInt(Integer::intValue).toArray();
        int[] libres = IntStream.range(0, incognitas).filter(j -> !pivotes.contains(j)).toArray();
        return new Escalonada(m, columnasPivote, libres, consistente);
    }

    /**
     * La solución del sistema, si es única.
     *
     * @param aumentada la matriz {@code [A | b]} (no se modifica)
     * @return el valor de cada incógnita, o vacío si el sistema no tiene
     *         solución o tiene infinitas
     */
    public static Optional<BigFraction[]> resolverUnica(BigFraction[][] aumentada) {
        Escalonada escalonada = escalonar(aumentada);
        if (!escalonada.tieneSolucionUnica()) return Optional.empty();
        int incognitas = escalonada.pivotes().length;
        BigFraction[] solucion = new BigFraction[incognitas];
        for (int p = 0; p < incognitas; p++) solucion[escalonada.pivotes()[p]] = escalonada.matriz()[p][incognitas];
        return Optional.of(solucion);
    }
}
