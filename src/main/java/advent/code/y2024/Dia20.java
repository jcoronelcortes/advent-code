package advent.code.y2024;

import advent.code.BFS;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Año 2024 — Día 20: Condición de Carrera
 *
 * <p>El input es el mapa de una pista ({@code .}, con la salida {@code S} y
 * la llegada {@code E}) rodeada de paredes ({@code #}). Cada paso cuesta un
 * picosegundo. Una sola vez por carrera se puede hacer <b>trampa</b>: durante
 * unos picosegundos se atraviesan paredes, y al terminar hay que estar de
 * nuevo sobre la pista. Una trampa se identifica por la casilla donde empieza
 * y la casilla donde termina. En la parte 1 la trampa dura hasta 2
 * picosegundos y en la parte 2 hasta 20.</p>
 */
public class Dia20 implements Resolvedor {

    static final char PARED = '#';
    static final char SALIDA = 'S';
    static final char LLEGADA = 'E';

    /** Duración máxima de la trampa en la parte 1, en picosegundos. */
    static final int DURACION_PARTE1 = 2;

    /** Duración máxima de la trampa en la parte 2, en picosegundos. */
    static final int DURACION_PARTE2 = 20;

    /** Ahorro mínimo que pide el enunciado, en picosegundos. */
    static final int AHORRO_MINIMO = 100;

    /**
     * Parte 1: cuántas trampas de hasta 2 picosegundos ahorran al menos 100.
     *
     * @param lineas el mapa de la pista
     * @return la cantidad de trampas
     */
    @Override
    public String parte1(List<String> lineas) {
        Pista pista = Pista.desde(Grilla.desdeLineas(lineas));
        return String.valueOf(pista.contarTrampas(DURACION_PARTE1, AHORRO_MINIMO));
    }

    /**
     * Parte 2: cuántas trampas de hasta 20 picosegundos ahorran al menos 100.
     *
     * @param lineas el mapa de la pista
     * @return la cantidad de trampas
     */
    @Override
    public String parte2(List<String> lineas) {
        Pista pista = Pista.desde(Grilla.desdeLineas(lineas));
        return String.valueOf(pista.contarTrampas(DURACION_PARTE2, AHORRO_MINIMO));
    }

    /**
     * La pista con la distancia de cada casilla a la salida y a la llegada.
     *
     * @param mapa           el mapa
     * @param desdeSalida    pasos desde la salida a cada casilla ({@code -1} si es pared o inalcanzable)
     * @param hastaLlegada   pasos de cada casilla a la llegada ({@code -1} si es pared o inalcanzable)
     * @param tiempoSinTrampa el tiempo de la carrera sin hacer trampa
     */
    record Pista(Grilla mapa, int[][] desdeSalida, int[][] hastaLlegada, int tiempoSinTrampa) {

        /**
         * Calcula las distancias con dos BFS: una desde la salida y otra desde
         * la llegada (los pasos se pueden dar en cualquier sentido, así que la
         * distancia de una casilla a la llegada es la de la llegada a ella).
         *
         * @param mapa el mapa
         * @return la pista
         * @throws IllegalArgumentException si la llegada es inalcanzable
         */
        static Pista desde(Grilla mapa) {
            int[][] desdeSalida = distancias(mapa, mapa.buscarUnico(SALIDA));
            Punto llegada = mapa.buscarUnico(LLEGADA);
            int tiempo = desdeSalida[llegada.y()][llegada.x()];
            if (tiempo < 0) throw new IllegalArgumentException("La llegada es inalcanzable");
            return new Pista(mapa, desdeSalida, distancias(mapa, llegada), tiempo);
        }

        /**
         * Cuántas trampas ahorran cada cantidad de picosegundos (solo las que
         * ahorran algo), ordenado por ahorro.
         *
         * <p>Una trampa que empieza en la casilla {@code a} y termina en la
         * {@code b} atraviesa lo que haya en el medio, así que su duración
         * mínima es la distancia Manhattan entre las dos. Si esa distancia es
         * a lo sumo {@code duracionMaxima}, la carrera con la trampa tarda
         * {@code desdeSalida[a] + distancia + hastaLlegada[b]}, y el ahorro es
         * lo que le falta a eso para llegar al tiempo sin trampa.</p>
         *
         * <p>Para cada casilla de la pista se recorren todos los
         * desplazamientos dentro del rombo de radio {@code duracionMaxima}:
         * O(casillas × duración²).</p>
         *
         * @param duracionMaxima picosegundos que puede durar la trampa
         * @return ahorro → cantidad de trampas
         */
        SortedMap<Integer, Long> ahorrosPorTrampa(int duracionMaxima) {
            SortedMap<Integer, Long> ahorros = new TreeMap<>();
            for (int y = 0; y < mapa.filas(); y++) {
                for (int x = 0; x < mapa.columnas(); x++) {
                    if (desdeSalida[y][x] < 0) continue;
                    for (int dy = -duracionMaxima; dy <= duracionMaxima; dy++) {
                        int resto = duracionMaxima - Math.abs(dy);
                        for (int dx = -resto; dx <= resto; dx++) {
                            int finY = y + dy;
                            int finX = x + dx;
                            if (finY < 0 || finY >= mapa.filas() || finX < 0 || finX >= mapa.columnas()) continue;
                            if (hastaLlegada[finY][finX] < 0) continue;
                            int tiempo = desdeSalida[y][x] + Math.abs(dx) + Math.abs(dy) + hastaLlegada[finY][finX];
                            int ahorro = tiempoSinTrampa - tiempo;
                            if (ahorro > 0) ahorros.merge(ahorro, 1L, Long::sum);
                        }
                    }
                }
            }
            return ahorros;
        }

        /**
         * Cuántas trampas de hasta {@code duracionMaxima} picosegundos ahorran
         * al menos {@code ahorroMinimo}.
         *
         * @param duracionMaxima picosegundos que puede durar la trampa
         * @param ahorroMinimo   ahorro mínimo, en picosegundos
         * @return la cantidad de trampas
         */
        long contarTrampas(int duracionMaxima, int ahorroMinimo) {
            return ahorrosPorTrampa(duracionMaxima).entrySet().stream()
                    .filter(entrada -> entrada.getKey() >= ahorroMinimo)
                    .mapToLong(Map.Entry::getValue)
                    .sum();
        }

        /** Pasos desde {@code inicio} a cada casilla de la pista, con {@code -1} donde no se llega. */
        private static int[][] distancias(Grilla mapa, Punto inicio) {
            int[][] distancias = new int[mapa.filas()][mapa.columnas()];
            for (int[] fila : distancias) Arrays.fill(fila, -1);
            BFS.explorar(inicio, posicion -> mapa.vecinos(posicion, c -> c != PARED))
                    .forEach((posicion, pasos) -> distancias[posicion.y()][posicion.x()] = Math.toIntExact(pasos));
            return distancias;
        }
    }
}
