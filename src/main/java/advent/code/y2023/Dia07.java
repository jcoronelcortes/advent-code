package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Año 2023 — Día 7: Cartas de Camello
 *
 * <p>Cada línea del input es una mano de cinco cartas y su apuesta:
 * {@code 32T3K 765}. Las manos se ordenan de la más débil a la más fuerte:
 * primero por tipo (repóker, póker, full, trío, doble par, par, carta alta)
 * y, a igual tipo, comparando las cartas una por una en el orden en que
 * aparecen. Cada mano gana su apuesta por su puesto en ese orden. En la
 * parte 2, la {@code J} es un comodín para el tipo y la carta más débil
 * para desempatar.</p>
 */
public class Dia07 implements Resolvedor {

    /** El comodín de la parte 2. */
    static final char COMODIN = 'J';

    /**
     * Las reglas del juego.
     *
     * @param orden     las cartas de la más débil a la más fuerte, para desempatar
     * @param comodines si la {@link #COMODIN} cuenta como cualquier carta para el tipo
     */
    record Reglas(String orden, boolean comodines) {}

    /** Parte 1: sin comodines. */
    static final Reglas PARTE1 = new Reglas("23456789TJQKA", false);

    /** Parte 2: la J es comodín y la carta más débil. */
    static final Reglas PARTE2 = new Reglas("J23456789TQKA", true);

    /**
     * Parte 1: las ganancias totales: la suma de la apuesta de cada mano por
     * su puesto (1 para la más débil).
     *
     * @param lineas las manos con sus apuestas
     * @return las ganancias totales
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(ganancias(manos(lineas), PARTE1));
    }

    /**
     * Parte 2: las ganancias totales con la {@code J} como comodín (para el
     * tipo) y como la carta más débil (para desempatar).
     *
     * @param lineas las manos con sus apuestas
     * @return las ganancias totales
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(ganancias(manos(lineas), PARTE2));
    }

    /** Los tipos de mano, del más débil al más fuerte (el orden del enum es el de fuerza). */
    enum Tipo { CARTA_ALTA, PAR, DOBLE_PAR, TRIO, FULL, POKER, REPOKER }

    /**
     * Una mano y su apuesta.
     *
     * @param cartas  las cinco cartas, en el orden del input
     * @param apuesta la apuesta
     */
    record Mano(String cartas, long apuesta) {

        /**
         * Lee una línea {@code cartas apuesta}.
         *
         * @param linea la línea
         * @return la mano
         * @throws IllegalArgumentException si la línea no tiene esa forma
         */
        static Mano parsear(String linea) {
            String[] partes = linea.strip().split("\\s+");
            if (partes.length != 2 || partes[0].length() != 5) {
                throw new IllegalArgumentException("Mano inválida: '" + linea + "'");
            }
            return new Mano(partes[0], Long.parseLong(partes[1]));
        }

        /**
         * El tipo de la mano, según cuántas veces se repite cada carta.
         *
         * <p>Las repeticiones, ordenadas de mayor a menor, determinan el
         * tipo: {@code [5]} es repóker, {@code [4, 1]} póker,
         * {@code [3, 2]} full, {@code [3, 1, 1]} trío, {@code [2, 2, 1]}
         * doble par, {@code [2, 1, 1, 1]} par, y cinco cartas distintas,
         * carta alta. Alcanza con mirar las dos repeticiones más grandes.</p>
         *
         * <p>Con comodines, conviene que todos imiten a la carta que más se
         * repite: así crece la repetición más grande, que es la que más pesa
         * en el tipo (sumarlos a otra carta, o repartirlos, nunca da un tipo
         * mejor). Por eso los comodines se sacan de la cuenta y se suman a la
         * mayor; si la mano es toda de comodines, es repóker.</p>
         *
         * @param comodines si la {@link #COMODIN} cuenta como cualquier carta
         * @return el tipo
         */
        Tipo tipo(boolean comodines) {
            Map<Character, Integer> repeticiones = new HashMap<>();
            for (char carta : cartas.toCharArray()) repeticiones.merge(carta, 1, Integer::sum);
            int cantidadComodines = comodines ? repeticiones.getOrDefault(COMODIN, 0) : 0;
            if (comodines) repeticiones.remove(COMODIN);
            List<Integer> cantidades = repeticiones.values().stream().sorted(Comparator.reverseOrder()).toList();
            int mayor = (cantidades.isEmpty() ? 0 : cantidades.get(0)) + cantidadComodines;
            int segunda = cantidades.size() > 1 ? cantidades.get(1) : 0;
            return switch (mayor) {
                case 5 -> Tipo.REPOKER;
                case 4 -> Tipo.POKER;
                case 3 -> segunda == 2 ? Tipo.FULL : Tipo.TRIO;
                case 2 -> segunda == 2 ? Tipo.DOBLE_PAR : Tipo.PAR;
                default -> Tipo.CARTA_ALTA;
            };
        }
    }

    static List<Mano> manos(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Mano::parsear).toList();
    }

    /**
     * Compara manos por tipo y, a igual tipo, carta por carta según el orden
     * de las reglas. Los comodines solo cambian el tipo: para desempatar, la
     * {@code J} vale lo que dice el orden.
     *
     * @param reglas las reglas
     * @return el comparador, de la mano más débil a la más fuerte
     */
    static Comparator<Mano> comparador(Reglas reglas) {
        String orden = reglas.orden();
        Comparator<Mano> porTipo = Comparator.comparing(mano -> mano.tipo(reglas.comodines()));
        return porTipo.thenComparing((a, b) -> {
            for (int i = 0; i < a.cartas().length(); i++) {
                int diferencia = Integer.compare(valor(a.cartas().charAt(i), orden), valor(b.cartas().charAt(i), orden));
                if (diferencia != 0) return diferencia;
            }
            return 0;
        });
    }

    private static int valor(char carta, String orden) {
        int valor = orden.indexOf(carta);
        if (valor < 0) throw new IllegalArgumentException("Carta desconocida: '" + carta + "'");
        return valor;
    }

    /**
     * Las ganancias: se ordenan las manos de la más débil a la más fuerte y
     * cada una gana su apuesta por su puesto (1, 2, 3, ...).
     *
     * @param manos  las manos
     * @param reglas las reglas
     * @return la suma de las ganancias
     */
    static long ganancias(List<Mano> manos, Reglas reglas) {
        List<Mano> ordenadas = manos.stream().sorted(comparador(reglas)).toList();
        long total = 0;
        for (int puesto = 1; puesto <= ordenadas.size(); puesto++) {
            total = Math.addExact(total, Math.multiplyExact(ordenadas.get(puesto - 1).apuesta(), puesto));
        }
        return total;
    }
}
