package advent.code.y2023;

import advent.code.Grilla;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2023 — Día 13: Punto de Incidencia
 *
 * <p>El input son patrones de ceniza ({@code .}) y rocas ({@code #})
 * separados por líneas en blanco. Cada patrón tiene una línea de reflejo,
 * horizontal (entre dos filas) o vertical (entre dos columnas): las filas (o
 * columnas) a cada lado coinciden como en un espejo, hasta que uno de los
 * lados se termina.</p>
 */
public class Dia13 implements Resolvedor {

    /** Cuánto vale cada fila por encima de una línea horizontal en el resumen. */
    static final int PESO_FILAS = 100;

    /**
     * Parte 1: el resumen de las notas: las columnas a la izquierda de cada
     * línea vertical más 100 por las filas encima de cada línea horizontal.
     *
     * @param lineas los patrones
     * @return el resumen
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(resumen(lineas, 0));
    }

    /**
     * Parte 2: el resumen con la mancha de cada espejo corregida. Arreglar
     * una sola casilla deja un reflejo perfecto justo en la línea donde los
     * dos lados difieren en exactamente una casilla, que nunca es la línea
     * original (esa tiene cero).
     *
     * @param lineas los patrones
     * @return el resumen con las nuevas líneas de reflejo
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(resumen(lineas, 1));
    }

    /**
     * El resumen de todos los patrones, buscando en cada uno la línea de
     * reflejo con exactamente {@code diferencias} casillas que no coinciden.
     *
     * @param lineas       los patrones
     * @param diferencias  cuántas casillas distintas tiene que haber entre los dos lados
     * @return la suma de los valores de cada patrón
     */
    static long resumen(List<String> lineas, int diferencias) {
        return Utiles.secciones(lineas).stream()
                .map(Grilla::desdeLineas)
                .mapToLong(patron -> valor(patron, diferencias))
                .sum();
    }

    /**
     * El valor de un patrón: las columnas a la izquierda de su línea
     * vertical, o 100 por las filas encima de su línea horizontal.
     *
     * @param patron      el patrón
     * @param diferencias cuántas casillas distintas tiene que haber
     * @return el valor
     * @throws IllegalArgumentException si no hay exactamente una línea de reflejo
     */
    static long valor(Grilla patron, int diferencias) {
        List<Long> valores = new ArrayList<>();
        for (int filas : lineasDeReflejo(patron, diferencias)) valores.add((long) PESO_FILAS * filas);
        for (int columnas : lineasDeReflejo(patron.transponer(), diferencias)) valores.add((long) columnas);
        if (valores.size() != 1) {
            throw new IllegalArgumentException("Se esperaba una línea de reflejo y hay " + valores.size()
                    + " en:\n" + patron);
        }
        return valores.getFirst();
    }

    /**
     * Las líneas de reflejo horizontales con exactamente {@code diferencias}
     * casillas distintas, como cantidad de filas encima de cada una.
     *
     * <p>Para la línea que deja {@code k} filas arriba, se comparan las
     * filas que quedan enfrentadas, {@code k − 1} con {@code k},
     * {@code k − 2} con {@code k + 1}, y así hasta que uno de los dos lados
     * se termina, contando las casillas distintas. Las líneas verticales son
     * las horizontales del patrón transpuesto.</p>
     *
     * @param patron      el patrón
     * @param diferencias cuántas casillas distintas tiene que haber
     * @return las cantidades de filas encima de cada línea que cumple
     */
    static List<Integer> lineasDeReflejo(Grilla patron, int diferencias) {
        List<Integer> lineas = new ArrayList<>();
        for (int k = 1; k < patron.filas(); k++) {
            int distintas = 0;
            for (int arriba = k - 1, abajo = k; arriba >= 0 && abajo < patron.filas(); arriba--, abajo++) {
                distintas += distintas(patron.fila(arriba), patron.fila(abajo));
                if (distintas > diferencias) break;
            }
            if (distintas == diferencias) lineas.add(k);
        }
        return lineas;
    }

    /** Cuántas posiciones distintas hay entre dos filas del mismo largo. */
    static int distintas(String a, String b) {
        int distintas = 0;
        for (int i = 0; i < a.length(); i++) if (a.charAt(i) != b.charAt(i)) distintas++;
        return distintas;
    }
}
