package advent.code.y2025;

import advent.code.Solver;

import java.util.List;

/**
 * Año 2025 — Día 1: Entrada Secreta
 *
 * <p>Un dial circular con números del 0 al 99 comienza apuntando al 50.
 * Se sigue una secuencia de rotaciones (L/R + distancia) y se cuentan
 * cuántas veces el dial queda o pasa por el 0.</p>
 */
public class Day01 implements Solver {

    /** Posición inicial del dial. */
    private static final int POSICION_INICIAL = 50;

    /** Tamaño del dial (números del 0 al 99). */
    private static final int TAMANIO_DIAL = 100;

    /**
     * Parte 1: cuenta cuántas veces el dial <em>termina</em> en 0
     * después de cada rotación.
     *
     * @param líneas instrucciones de rotación (ej. "L68", "R48")
     * @return cantidad de veces que el dial queda en 0
     */
    @Override
    public String part1(List<String> líneas) {
        int posicion = POSICION_INICIAL;
        int contador = 0;

        for (String línea : líneas) {
            línea = línea.trim();
            if (!línea.matches("[LR]\\d+")) continue;

            char dirección = línea.charAt(0);
            int distancia = Integer.parseInt(línea.substring(1));

            int delta = (dirección == 'R') ? distancia : -distancia;
            posicion = ((posicion + delta) % TAMANIO_DIAL + TAMANIO_DIAL) % TAMANIO_DIAL;

            if (posicion == 0) contador++;
        }

        return String.valueOf(contador);
    }

    /**
     * Parte 2: cuenta cuántas veces el dial <em>pasa o termina</em> en 0
     * durante cada rotación, incluyendo posiciones intermedias.
     *
     * <p>Para una rotación de distancia {@code d} desde posición {@code p},
     * se calcula matemáticamente cuántos clicks caen exactamente en 0
     * sin simular click por click.</p>
     *
     * @param líneas instrucciones de rotación (ej. "L68", "R48")
     * @return cantidad total de clicks que apuntan al 0
     */
    @Override
    public String part2(List<String> líneas) {
        int posicion = POSICION_INICIAL;
        long contador = 0;

        for (String línea : líneas) {
            línea = línea.trim();
            if (!línea.matches("[LR]\\d+")) continue;

            char dirección = línea.charAt(0);
            int distancia = Integer.parseInt(línea.substring(1));

            contador += contarPasosPorCero(posicion, dirección, distancia);

            int delta = (dirección == 'R') ? distancia : -distancia;
            posicion = ((posicion + delta) % TAMANIO_DIAL + TAMANIO_DIAL) % TAMANIO_DIAL;
        }

        return String.valueOf(contador);
    }

    /**
     * Calcula cuántos clicks caen en 0 durante una rotación.
     *
     * <p>Para izquierda (L): el primer click en 0 ocurre en el paso {@code p},
     * luego cada 100 pasos. Para derecha (R): el primer click en 0 ocurre
     * en el paso {@code 100 - p}, luego cada 100 pasos.</p>
     *
     * @param posicion  posición actual del dial
     * @param dirección 'L' (izquierda) o 'R' (derecha)
     * @param distancia cantidad de clicks de la rotación
     * @return cantidad de clicks que tocan el 0
     */
    static long contarPasosPorCero(int posicion, char dirección, int distancia) {
        if (dirección == 'L') {
            if (posicion == 0) return distancia / TAMANIO_DIAL;
            if (distancia >= posicion) return (distancia - posicion) / TAMANIO_DIAL + 1;
            return 0;
        } else {
            int pasoAlCero = (posicion == 0) ? TAMANIO_DIAL : (TAMANIO_DIAL - posicion);
            if (distancia >= pasoAlCero) return (distancia - pasoAlCero) / TAMANIO_DIAL + 1;
            return 0;
        }
    }
}