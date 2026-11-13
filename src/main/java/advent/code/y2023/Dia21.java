package advent.code.y2023;

import advent.code.BFS;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Año 2023 — Día 21: Contador de Pasos
 *
 * <p>El input es el mapa de un jardín con parcelas ({@code .}), rocas
 * ({@code #}) y el punto de partida ({@code S}, que también es una parcela).
 * En cada paso el elfo se mueve a una parcela vecina en una de las cuatro
 * direcciones; la pregunta es en cuántas parcelas puede terminar después de
 * una cantidad exacta de pasos.</p>
 */
public class Dia21 implements Resolvedor {

    /** Cuántos pasos da el elfo en la parte 1. */
    static final int PASOS = 64;

    /** Cuántos pasos da el elfo en la parte 2, en el mapa infinito. */
    static final long PASOS_INFINITO = 26_501_365L;

    /**
     * Cuántas muestras se toman para extrapolar: tres alcanzan para fijar la
     * cuadrática y la cuarta sirve para comprobar que de verdad lo es.
     */
    static final int MUESTRAS = 4;

    static final char INICIO = 'S';
    static final char ROCA = '#';

    /**
     * Parte 1: en cuántas parcelas puede terminar el elfo con exactamente 64
     * pasos.
     *
     * @param lineas el mapa
     * @return la cantidad de parcelas
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(alcanzables(Grilla.desdeLineas(lineas), PASOS));
    }

    /**
     * Parte 2: en cuántas parcelas puede terminar el elfo con exactamente
     * 26501365 pasos, si el mapa se repite infinitamente en todas las
     * direcciones.
     *
     * @param lineas el mapa
     * @return la cantidad de parcelas
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(alcanzablesExtrapolando(Grilla.desdeLineas(lineas), PASOS_INFINITO));
    }

    /**
     * En cuántas parcelas del mapa infinito puede terminar el elfo con
     * exactamente {@code pasos} pasos, extrapolando con una cuadrática.
     *
     * <p>Con un mapa de lado {@code L}, se escribe {@code pasos = r + L·K}.
     * Si se miran solo las cantidades de pasos {@code r + L·k}, la zona
     * alcanzable es un rombo que crece exactamente un mapa por lado en cada
     * {@code k}, así que cubre una cantidad de mapas enteros que crece como
     * {@code k²}, más un borde que crece como {@code k}: la cantidad de
     * parcelas es un polinomio <b>cuadrático</b> en {@code k}. Eso vale si
     * el mapa deja avanzar sin trabas en línea recta (en el input real, la
     * fila y la columna de {@code S} y los bordes están libres de rocas).</p>
     *
     * <p>Se calculan con un BFS las cantidades para {@code k = 0, 1, 2, 3},
     * se comprueba que las segundas diferencias sean iguales (si no lo son,
     * no es una cuadrática y se falla en vez de extrapolar mal) y se evalúa
     * con diferencias hacia adelante:
     * {@code f(K) = f(0) + K·Δ + K(K−1)/2·Δ²}.</p>
     *
     * @param mapa  el mapa (cuadrado)
     * @param pasos la cantidad exacta de pasos
     * @return la cantidad de parcelas alcanzables
     * @throws IllegalArgumentException si el mapa no es cuadrado o no hay exactamente un {@code S}
     * @throws IllegalStateException    si las cantidades no crecen como una cuadrática
     */
    static long alcanzablesExtrapolando(Grilla mapa, long pasos) {
        if (mapa.filas() != mapa.columnas()) {
            throw new IllegalArgumentException("El mapa tiene que ser cuadrado: " + mapa.filas() + " × " + mapa.columnas());
        }
        int lado = mapa.filas();
        int resto = (int) (pasos % lado);
        long vueltas = pasos / lado;
        int maximo = resto + lado * (MUESTRAS - 1);
        Map<Punto, Long> distancias = distanciasEnMapaInfinito(mapa, (int) Math.min(pasos, maximo));
        if (pasos <= maximo) return contar(distancias, pasos);

        long[] muestras = new long[MUESTRAS];
        for (int k = 0; k < MUESTRAS; k++) muestras[k] = contar(distancias, resto + (long) lado * k);
        long primera = muestras[1] - muestras[0];
        long segunda = muestras[2] - 2 * muestras[1] + muestras[0];
        for (int k = 3; k < MUESTRAS; k++) {
            long otra = muestras[k] - 2 * muestras[k - 1] + muestras[k - 2];
            if (otra != segunda) {
                throw new IllegalStateException("Las cantidades no crecen como una cuadrática: "
                        + Arrays.toString(muestras));
            }
        }
        long combinaciones = Math.multiplyExact(vueltas, vueltas - 1) / 2;
        return Math.addExact(muestras[0],
                Math.addExact(Math.multiplyExact(vueltas, primera), Math.multiplyExact(combinaciones, segunda)));
    }

    /**
     * En cuántas parcelas del mapa infinito puede terminar el elfo con
     * exactamente {@code pasos} pasos, con un BFS directo (sirve para
     * cantidades moderadas de pasos).
     *
     * @param mapa  el mapa
     * @param pasos la cantidad exacta de pasos
     * @return la cantidad de parcelas alcanzables
     */
    static long alcanzablesEnMapaInfinito(Grilla mapa, int pasos) {
        return contar(distanciasEnMapaInfinito(mapa, pasos), pasos);
    }

    /**
     * Las distancias desde {@code S} en el mapa repetido infinitamente, hasta
     * {@code maximo} pasos.
     *
     * <p>Las coordenadas no se limitan al mapa: una posición {@code (x, y)}
     * tiene lo que tenga {@code (x mod columnas, y mod filas)}
     * ({@link Grilla#obtenerCiclico}). Para que el
     * BFS termine, solo se exploran las posiciones a distancia Manhattan de a
     * lo sumo {@code maximo} desde {@code S}; no se pierde nada, porque en un
     * camino mínimo de largo {@code d ≤ maximo} todas las posiciones están a
     * distancia Manhattan menor que {@code d}.</p>
     *
     * @param mapa   el mapa
     * @param maximo la mayor distancia que interesa
     * @return la distancia mínima a cada parcela alcanzable dentro del rombo
     */
    static Map<Punto, Long> distanciasEnMapaInfinito(Grilla mapa, int maximo) {
        Punto inicio = mapa.buscarUnico(INICIO);
        return BFS.explorar(inicio, p -> p.vecinos().stream()
                .filter(v -> v.distanciaManhattan(inicio) <= maximo)
                .filter(v -> mapa.obtenerCiclico(v) != ROCA)
                .toList());
    }

    /** Cuántas distancias son a lo sumo {@code pasos} y tienen su misma paridad. */
    private static long contar(Map<Punto, Long> distancias, long pasos) {
        return distancias.values().stream()
                .filter(d -> d <= pasos && d % 2 == pasos % 2)
                .count();
    }

    /**
     * En cuántas parcelas puede terminar el elfo con exactamente
     * {@code pasos} pasos.
     *
     * <p>No hace falta simular paso por paso el conjunto de posiciones. Una
     * parcela a distancia mínima {@code d} es alcanzable en exactamente
     * {@code pasos} pasos si {@code d ≤ pasos} y {@code d} tiene la misma
     * paridad que {@code pasos}: los pasos que sobran se gastan de a dos,
     * yendo a una parcela vecina y volviendo. Y si la paridad es distinta,
     * nunca se puede: el mapa es una grilla, así que cada paso cambia la
     * paridad de {@code x + y}. Las distancias salen de un solo BFS desde
     * {@code S}.</p>
     *
     * @param mapa  el mapa
     * @param pasos la cantidad exacta de pasos
     * @return la cantidad de parcelas alcanzables
     * @throws IllegalArgumentException si no hay exactamente un {@code S}
     */
    static long alcanzables(Grilla mapa, int pasos) {
        return contar(BFS.explorar(mapa.buscarUnico(INICIO), p -> mapa.vecinos(p, c -> c != ROCA)), pasos);
    }
}
