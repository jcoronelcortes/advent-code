package advent.code.y2022;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;

/**
 * Año 2022 — Día 2: Piedra, Papel o Tijera
 *
 * <p>El input es una guía de estrategia: una ronda por línea, con lo que
 * juega el rival ({@code A}, {@code B} o {@code C}) y una segunda columna
 * ({@code X}, {@code Y} o {@code Z}) cuyo significado hay que interpretar.</p>
 */
public class Dia02 implements Resolvedor {

    /**
     * Las figuras, en un orden en el que cada una le gana a la anterior (en
     * círculo: la piedra le gana a la tijera). Así el resultado de una ronda
     * sale de la diferencia de posiciones módulo 3.
     */
    enum Figura {
        PIEDRA, PAPEL, TIJERA;

        /** @return los puntos por elegir esta figura: 1, 2 o 3 */
        int puntos() {
            return ordinal() + 1;
        }

        /**
         * El resultado de jugar esta figura contra la del rival.
         *
         * @param rival la figura del rival
         * @return el resultado, desde el punto de vista de esta figura
         */
        Resultado contra(Figura rival) {
            return switch (Math.floorMod(ordinal() - rival.ordinal(), 3)) {
                case 0 -> Resultado.EMPATE;
                case 1 -> Resultado.VICTORIA;
                default -> Resultado.DERROTA;
            };
        }

        /**
         * La figura que hay que jugar contra {@code rival} para lograr
         * {@code resultado}: la inversa de {@link #contra}. Para ganar, la que
         * está un lugar adelante en el círculo; para empatar, la misma; para
         * perder, la de atrás.
         *
         * @param rival     la figura del rival
         * @param resultado el resultado buscado
         * @return la figura que lo logra
         */
        static Figura paraLograr(Figura rival, Resultado resultado) {
            int desplazamiento = switch (resultado) {
                case VICTORIA -> 1;
                case EMPATE -> 0;
                case DERROTA -> -1;
            };
            return values()[Math.floorMod(rival.ordinal() + desplazamiento, 3)];
        }
    }

    /** El resultado de una ronda, con sus puntos (en el orden de las letras {@code X}, {@code Y}, {@code Z} de la parte 2). */
    enum Resultado {
        DERROTA(0), EMPATE(3), VICTORIA(6);

        private final int puntos;

        Resultado(int puntos) {
            this.puntos = puntos;
        }

        int puntos() {
            return puntos;
        }
    }

    /**
     * Una ronda de la guía.
     *
     * @param rival   lo que juega el rival
     * @param columna la segunda columna, de 0 ({@code X}) a 2 ({@code Z}), sin interpretar
     */
    record Ronda(Figura rival, int columna) {
    }

    /**
     * Parte 1: el puntaje total si la segunda columna es la figura que hay que
     * jugar ({@code X} piedra, {@code Y} papel, {@code Z} tijera).
     *
     * @param lineas la guía de estrategia
     * @return el puntaje total
     */
    @Override
    public String parte1(List<String> lineas) {
        long total = 0;
        for (Ronda ronda : leer(lineas)) {
            total += puntaje(ronda.rival(), Figura.values()[ronda.columna()]);
        }
        return String.valueOf(total);
    }

    /**
     * Parte 2: el puntaje total si la segunda columna es cómo tiene que
     * terminar la ronda ({@code X} perder, {@code Y} empatar, {@code Z} ganar),
     * eligiendo la figura que lo logra.
     *
     * @param lineas la guía de estrategia
     * @return el puntaje total
     */
    @Override
    public String parte2(List<String> lineas) {
        long total = 0;
        for (Ronda ronda : leer(lineas)) {
            Resultado buscado = Resultado.values()[ronda.columna()];
            total += puntaje(ronda.rival(), Figura.paraLograr(ronda.rival(), buscado));
        }
        return String.valueOf(total);
    }

    /**
     * Los puntos de una ronda: los de la figura elegida más los del resultado.
     *
     * @param rival lo que juega el rival
     * @param mia   lo que juego yo
     * @return el puntaje de la ronda
     */
    static int puntaje(Figura rival, Figura mia) {
        return mia.puntos() + mia.contra(rival).puntos();
    }

    /**
     * Lee la guía: cada línea es una letra de {@code A} a {@code C}, un espacio
     * y una letra de {@code X} a {@code Z}.
     *
     * @param lineas la guía
     * @return las rondas, en orden
     * @throws IllegalArgumentException si una línea no tiene ese formato
     */
    static List<Ronda> leer(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(linea -> {
            if (!linea.matches("[ABC] [XYZ]")) throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
            return new Ronda(Figura.values()[linea.charAt(0) - 'A'], linea.charAt(2) - 'X');
        }).toList();
    }
}
