package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2024 — Día 25: Crónica del Código
 *
 * <p>El input son esquemas de cerraduras y llaves de cinco pernos, separados
 * por líneas en blanco. Una cerradura tiene la fila de arriba llena
 * ({@code #####}) y sus pernos bajan; una llave tiene la fila de abajo llena
 * y sus dientes suben. Una llave puede entrar en una cerradura si en ninguna
 * columna se superponen.</p>
 */
public class Dia25 implements Resolvedor {

    /** Texto de la parte 2: el último día no tiene un segundo problema. */
    static final String SIN_PARTE_2 = "estrella final";

    /**
     * Parte 1: cuántos pares distintos de cerradura y llave encajan sin
     * superponerse en ninguna columna.
     *
     * @param lineas los esquemas
     * @return la cantidad de pares
     */
    @Override
    public String parte1(List<String> lineas) {
        Esquemas esquemas = Esquemas.parsear(lineas);
        long pares = 0;
        for (int[] cerradura : esquemas.cerraduras()) {
            for (int[] llave : esquemas.llaves()) {
                if (encajan(cerradura, llave, esquemas.espacio())) pares++;
            }
        }
        return String.valueOf(pares);
    }

    /**
     * Parte 2: el último día no tiene un segundo problema; la segunda
     * estrella se obtiene al completar todos los demás días.
     *
     * @param lineas líneas del input (no se usan)
     * @return {@link #SIN_PARTE_2}
     */
    @Override
    public String parte2(List<String> lineas) {
        return SIN_PARTE_2;
    }

    /**
     * Si una llave encaja en una cerradura: en cada columna, la altura del
     * perno más la del diente no pasa del espacio disponible.
     *
     * @param cerradura alturas de los pernos
     * @param llave     alturas de los dientes
     * @param espacio   altura disponible en cada columna
     * @return {@code true} si no se superponen en ninguna columna
     */
    static boolean encajan(int[] cerradura, int[] llave, int espacio) {
        for (int columna = 0; columna < cerradura.length; columna++) {
            if (cerradura[columna] + llave[columna] > espacio) return false;
        }
        return true;
    }

    /**
     * Las alturas de las cerraduras y las llaves.
     *
     * @param cerraduras alturas de los pernos de cada cerradura
     * @param llaves     alturas de los dientes de cada llave
     * @param espacio    altura disponible: las filas del esquema sin la de arriba ni la de abajo
     */
    record Esquemas(List<int[]> cerraduras, List<int[]> llaves, int espacio) {

        /**
         * Lee los esquemas y los convierte en alturas.
         *
         * <p>La altura de una columna es la cantidad de {@code #} menos uno
         * (la fila llena, que es la base de la cerradura o de la llave, no
         * cuenta). Contar los {@code #} sirve para los dos tipos, porque los
         * pernos y los dientes son columnas sin huecos.</p>
         *
         * @param lineas los esquemas, separados por líneas en blanco
         * @return las alturas
         * @throws IllegalArgumentException si un esquema no es ni cerradura ni
         *                                  llave, o no todos tienen el mismo tamaño
         */
        static Esquemas parsear(List<String> lineas) {
            List<int[]> cerraduras = new ArrayList<>();
            List<int[]> llaves = new ArrayList<>();
            int filas = -1;
            for (List<String> esquema : Utiles.secciones(lineas)) {
                if (filas < 0) filas = esquema.size();
                if (esquema.size() != filas) throw new IllegalArgumentException("Los esquemas no tienen el mismo alto");
                String arriba = esquema.getFirst();
                String abajo = esquema.getLast();
                boolean esCerradura = arriba.chars().allMatch(c -> c == '#') && abajo.chars().allMatch(c -> c == '.');
                boolean esLlave = arriba.chars().allMatch(c -> c == '.') && abajo.chars().allMatch(c -> c == '#');
                if (!esCerradura && !esLlave) {
                    throw new IllegalArgumentException("Un esquema no es ni cerradura ni llave:\n" + String.join("\n", esquema));
                }
                (esCerradura ? cerraduras : llaves).add(alturas(esquema));
            }
            return new Esquemas(cerraduras, llaves, filas - 2);
        }

        private static int[] alturas(List<String> esquema) {
            int[] alturas = new int[esquema.getFirst().length()];
            for (String fila : esquema) {
                for (int columna = 0; columna < alturas.length; columna++) {
                    if (fila.charAt(columna) == '#') alturas[columna]++;
                }
            }
            for (int columna = 0; columna < alturas.length; columna++) alturas[columna]--;
            return alturas;
        }
    }
}
