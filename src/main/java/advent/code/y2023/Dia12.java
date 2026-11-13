package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Collections;
import java.util.List;

/**
 * Año 2023 — Día 12: Aguas Termales
 *
 * <p>Cada línea del input es una fila de resortes: su estado ({@code .}
 * sano, {@code #} dañado, {@code ?} desconocido) y, después de un espacio, el
 * tamaño de cada grupo de resortes dañados seguidos, en orden
 * ({@code ???.### 1,1,3}). Hay que contar de cuántas formas se pueden
 * completar los {@code ?} para que la fila tenga exactamente esos grupos. En
 * la parte 2, cada fila se "desdobla": se repite cinco veces.</p>
 */
public class Dia12 implements Resolvedor {

    static final char SANO = '.';
    static final char DANADO = '#';
    static final char DESCONOCIDO = '?';

    /** Cuántas copias de cada fila hay al desdoblarla en la parte 2. */
    static final int COPIAS = 5;

    /**
     * Parte 1: la suma, sobre todas las filas, de la cantidad de arreglos
     * posibles.
     *
     * @param lineas las filas con sus grupos
     * @return la suma de las cantidades
     */
    @Override
    public String parte1(List<String> lineas) {
        long suma = Utiles.lineasNoVacias(lineas).stream()
                .map(Fila::parsear)
                .mapToLong(Fila::arreglos)
                .reduce(0, Math::addExact);
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la misma suma, con cada fila desdoblada en cinco copias.
     *
     * @param lineas las filas con sus grupos
     * @return la suma de las cantidades
     */
    @Override
    public String parte2(List<String> lineas) {
        long suma = Utiles.lineasNoVacias(lineas).stream()
                .map(linea -> Fila.parsear(linea).desdoblar(COPIAS))
                .mapToLong(Fila::arreglos)
                .reduce(0, Math::addExact);
        return String.valueOf(suma);
    }

    /**
     * Una fila: el estado de cada resorte y los tamaños de los grupos dañados.
     *
     * @param resortes los resortes ({@code .}, {@code #} o {@code ?})
     * @param grupos   el tamaño de cada grupo de dañados seguidos, en orden
     */
    record Fila(String resortes, int[] grupos) {

        /**
         * Lee una línea como {@code ???.### 1,1,3}.
         *
         * @param linea la línea
         * @return la fila
         * @throws IllegalArgumentException si la línea no tiene esa forma
         */
        static Fila parsear(String linea) {
            String[] partes = linea.strip().split("\\s+");
            if (partes.length != 2 || !partes[0].matches("[.#?]+")) {
                throw new IllegalArgumentException("Fila inválida: '" + linea + "'");
            }
            int[] grupos = Utiles.leerNumeros(partes[1]).stream().mapToInt(Integer::intValue).toArray();
            return new Fila(partes[0], grupos);
        }

        /**
         * La fila desdoblada: los resortes repetidos {@code copias} veces,
         * separados por {@code ?}, y los grupos repetidos {@code copias}
         * veces.
         *
         * @param copias cuántas copias
         * @return la fila desdoblada
         */
        Fila desdoblar(int copias) {
            String resortesDesdoblados = String.join(String.valueOf(DESCONOCIDO), Collections.nCopies(copias, resortes));
            int[] gruposDesdoblados = new int[grupos.length * copias];
            for (int copia = 0; copia < copias; copia++) {
                System.arraycopy(grupos, 0, gruposDesdoblados, copia * grupos.length, grupos.length);
            }
            return new Fila(resortesDesdoblados, gruposDesdoblados);
        }

        /**
         * De cuántas formas se pueden completar los {@code ?} para que la
         * fila tenga exactamente esos grupos.
         *
         * <p>Programación dinámica: {@code formas[i][j]} es la cantidad de
         * formas de completar los resortes desde la posición {@code i} con
         * los grupos desde el {@code j}. Se llena de atrás hacia adelante:</p>
         * <ul>
         *   <li>sin resortes, hay 1 forma si no quedan grupos y 0 si quedan;</li>
         *   <li>si el resorte {@code i} puede estar sano ({@code .} o
         *       {@code ?}), se sigue desde el siguiente con los mismos grupos;</li>
         *   <li>si puede empezar el grupo {@code j} ({@code #} o {@code ?}),
         *       el grupo entero tiene que caber sin pisar ningún {@code .}, y el
         *       resorte de después no puede ser {@code #} (si no, el grupo sería
         *       más largo): se sigue después del grupo y su separador.</li>
         * </ul>
         *
         * <p>Son {@code (resortes + 2) × (grupos + 1)} valores, cada uno en
         * O(1) gracias a una tabla de cuántos resortes seguidos pueden estar
         * dañados desde cada posición.</p>
         *
         * @return la cantidad de arreglos
         */
        long arreglos() {
            int n = resortes.length();
            int m = grupos.length;
            // posiblesDanados[i]: cuántos resortes seguidos desde i pueden estar dañados (no son '.')
            int[] posiblesDanados = new int[n + 1];
            for (int i = n - 1; i >= 0; i--) {
                posiblesDanados[i] = resortes.charAt(i) == SANO ? 0 : posiblesDanados[i + 1] + 1;
            }
            // formas[i][j]; la fila n + 1 es para "después del separador del último grupo"
            long[][] formas = new long[n + 2][m + 1];
            formas[n][m] = 1;
            formas[n + 1][m] = 1;
            for (int i = n - 1; i >= 0; i--) {
                char resorte = resortes.charAt(i);
                for (int j = m; j >= 0; j--) {
                    long total = 0;
                    if (resorte != DANADO) total = formas[i + 1][j];
                    if (resorte != SANO && j < m) {
                        int tamanio = grupos[j];
                        boolean cabe = posiblesDanados[i] >= tamanio
                                && (i + tamanio == n || resortes.charAt(i + tamanio) != DANADO);
                        if (cabe) total = Math.addExact(total, formas[Math.min(i + tamanio + 1, n + 1)][j + 1]);
                    }
                    formas[i][j] = total;
                }
            }
            return formas[0][0];
        }
    }
}
