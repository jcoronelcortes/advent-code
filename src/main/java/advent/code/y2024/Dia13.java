package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;
import java.util.OptionalLong;

/**
 * Año 2024 — Día 13: Artilugio de Garra
 *
 * <p>El input describe máquinas de garra. Cada una tiene dos botones: A
 * cuesta 3 fichas y B cuesta 1, y cada pulsación mueve la garra una cantidad
 * fija en X y en Y. Para ganar el premio, la garra tiene que quedar
 * exactamente sobre él.</p>
 */
public class Dia13 implements Resolvedor {

    private static final long COSTO_A = 3;
    private static final long COSTO_B = 1;
    private static final long PULSACIONES_MAXIMAS_PARTE1 = 100;
    private static final long CORRECCION_PARTE2 = 10_000_000_000_000L;

    /**
     * Parte 1: fichas mínimas para ganar todos los premios posibles, con a lo
     * sumo 100 pulsaciones de cada botón.
     *
     * @param lineas las máquinas, separadas por líneas en blanco
     * @return la cantidad de fichas
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(fichasTotales(leerMaquinas(lineas), PULSACIONES_MAXIMAS_PARTE1));
    }

    /**
     * Parte 2: lo mismo, con cada premio corrido 10<sup>13</sup> en X y en Y,
     * y sin límite de pulsaciones.
     *
     * @param lineas las máquinas, separadas por líneas en blanco
     * @return la cantidad de fichas
     */
    @Override
    public String parte2(List<String> lineas) {
        List<Maquina> corregidas = leerMaquinas(lineas).stream()
                .map(maquina -> maquina.conPremioCorrido(CORRECCION_PARTE2))
                .toList();
        return String.valueOf(fichasTotales(corregidas, Long.MAX_VALUE));
    }

    /**
     * Suma de las fichas de las máquinas que se pueden ganar.
     *
     * @param maquinas           las máquinas
     * @param pulsacionesMaximas límite de pulsaciones de cada botón
     * @return la cantidad de fichas
     */
    static long fichasTotales(List<Maquina> maquinas, long pulsacionesMaximas) {
        long fichas = 0;
        for (Maquina maquina : maquinas) {
            OptionalLong costo = maquina.costoMinimo(pulsacionesMaximas);
            if (costo.isPresent()) fichas = Math.addExact(fichas, costo.getAsLong());
        }
        return fichas;
    }

    /**
     * Una máquina: cuánto mueve cada botón y dónde está el premio.
     *
     * @param ax movimiento en X del botón A
     * @param ay movimiento en Y del botón A
     * @param bx movimiento en X del botón B
     * @param by movimiento en Y del botón B
     * @param px posición X del premio
     * @param py posición Y del premio
     */
    record Maquina(long ax, long ay, long bx, long by, long px, long py) {

        /**
         * La misma máquina con el premio corrido la misma distancia en X y en Y.
         *
         * @param correccion cuánto se suma a cada coordenada del premio
         * @return la máquina corregida
         */
        Maquina conPremioCorrido(long correccion) {
            return new Maquina(ax, ay, bx, by, Math.addExact(px, correccion), Math.addExact(py, correccion));
        }

        /**
         * Fichas mínimas para ganar el premio, si se puede.
         *
         * <p>Con {@code a} pulsaciones de A y {@code b} de B, la garra llega
         * al premio si</p>
         * <pre>
         *   a·ax + b·bx = px
         *   a·ay + b·by = py
         * </pre>
         * <p>Es un sistema de 2 ecuaciones lineales con 2 incógnitas. Si los
         * botones no son paralelos (determinante distinto de 0), tiene una
         * <b>única</b> solución, que sale con la regla de Cramer: no hay nada
         * que minimizar. Sirve solo si es entera, no negativa y no pasa del
         * límite de pulsaciones.</p>
         *
         * @param pulsacionesMaximas límite de pulsaciones de cada botón
         * @return el costo en fichas, o vacío si no se puede ganar
         * @throws IllegalStateException si los botones son paralelos (no
         *         ocurre en el input: habría infinitas soluciones o ninguna)
         */
        OptionalLong costoMinimo(long pulsacionesMaximas) {
            long determinante = Math.subtractExact(Math.multiplyExact(ax, by), Math.multiplyExact(ay, bx));
            if (determinante == 0) {
                throw new IllegalStateException("Botones paralelos: el sistema no tiene solución única " + this);
            }
            long numeradorA = Math.subtractExact(Math.multiplyExact(px, by), Math.multiplyExact(py, bx));
            long numeradorB = Math.subtractExact(Math.multiplyExact(ax, py), Math.multiplyExact(ay, px));
            if (numeradorA % determinante != 0 || numeradorB % determinante != 0) return OptionalLong.empty();
            long a = numeradorA / determinante;
            long b = numeradorB / determinante;
            if (a < 0 || b < 0 || a > pulsacionesMaximas || b > pulsacionesMaximas) return OptionalLong.empty();
            return OptionalLong.of(Math.addExact(Math.multiplyExact(a, COSTO_A), Math.multiplyExact(b, COSTO_B)));
        }
    }

    /**
     * Lee las máquinas: cada bloque separado por línea en blanco tiene los
     * 6 números, en orden, de los botones A y B y del premio.
     *
     * @param lineas el input
     * @return las máquinas
     */
    static List<Maquina> leerMaquinas(List<String> lineas) {
        return Utiles.secciones(lineas).stream()
                .map(bloque -> {
                    List<Long> n = Utiles.leerNumerosLargos(bloque);
                    if (n.size() != 6) {
                        throw new IllegalArgumentException("Una máquina debe tener 6 números: " + bloque);
                    }
                    return new Maquina(n.get(0), n.get(1), n.get(2), n.get(3), n.get(4), n.get(5));
                })
                .toList();
    }
}
