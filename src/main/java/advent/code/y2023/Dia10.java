package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Poligono;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Año 2023 — Día 10: Laberinto de Tuberías
 *
 * <p>El input es una grilla de tuberías: {@code |} y {@code -} son rectas,
 * {@code L J 7 F} son codos, {@code .} es suelo, y {@code S} es el punto de
 * partida, sobre una tubería cuya forma no se ve. La tubería de {@code S}
 * forma parte de un único lazo cerrado; las demás tuberías pueden no estar
 * conectadas a nada. En la parte 2 hay que contar las casillas encerradas
 * por el lazo.</p>
 */
public class Dia10 implements Resolvedor {

    static final char INICIO = 'S';

    /** Las tuberías posibles, para deducir la forma de {@code S}. */
    static final String TUBERIAS = "|-LJ7F";

    /**
     * Parte 1: cuántos pasos a lo largo del lazo hay hasta la casilla más
     * lejana de {@code S}.
     *
     * @param lineas la grilla
     * @return la cantidad de pasos
     */
    @Override
    public String parte1(List<String> lineas) {
        Lazo lazo = Lazo.desde(Grilla.desdeLineas(lineas));
        return String.valueOf(lazo.casillas().size() / 2);
    }

    /**
     * Parte 2: cuántas casillas quedan encerradas por el lazo.
     *
     * @param lineas la grilla
     * @return la cantidad de casillas encerradas
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(Lazo.desde(Grilla.desdeLineas(lineas)).casillasEncerradas());
    }

    /**
     * Las dos direcciones que conecta una tubería, o ninguna si no es una
     * tubería ({@code .} o cualquier otro carácter).
     *
     * @param tuberia el carácter de la casilla
     * @return las direcciones hacia las que sale
     */
    static Set<Direccion> conexiones(char tuberia) {
        return switch (tuberia) {
            case '|' -> EnumSet.of(Direccion.ARRIBA, Direccion.ABAJO);
            case '-' -> EnumSet.of(Direccion.IZQUIERDA, Direccion.DERECHA);
            case 'L' -> EnumSet.of(Direccion.ARRIBA, Direccion.DERECHA);
            case 'J' -> EnumSet.of(Direccion.ARRIBA, Direccion.IZQUIERDA);
            case '7' -> EnumSet.of(Direccion.ABAJO, Direccion.IZQUIERDA);
            case 'F' -> EnumSet.of(Direccion.ABAJO, Direccion.DERECHA);
            default -> EnumSet.noneOf(Direccion.class);
        };
    }

    /**
     * El lazo que pasa por {@code S}.
     *
     * @param casillas    las casillas del lazo en orden de recorrido, empezando por {@code S}
     * @param formaDeS    la tubería que hay debajo de {@code S}
     */
    record Lazo(List<Punto> casillas, char formaDeS) {

        /**
         * Encuentra el lazo de {@code S}.
         *
         * <p>La forma de {@code S} se deduce de sus vecinos: {@code S}
         * conecta hacia una dirección si la tubería de ese lado conecta de
         * vuelta hacia {@code S}. Tienen que ser exactamente dos. Después se
         * recorre el lazo: en cada casilla se sale por la conexión que no es
         * por la que se entró, hasta volver a {@code S}.</p>
         *
         * @param grilla la grilla
         * @return el lazo
         * @throws IllegalArgumentException si {@code S} no conecta con exactamente
         *                                  dos tuberías, o el lazo se corta
         */
        static Lazo desde(Grilla grilla) {
            Punto inicio = grilla.buscarUnico(INICIO);
            Set<Direccion> deS = EnumSet.noneOf(Direccion.class);
            for (Direccion direccion : Direccion.values()) {
                char vecino = grilla.obtener(inicio.mover(direccion), '.');
                if (conexiones(vecino).contains(direccion.opuesto())) deS.add(direccion);
            }
            if (deS.size() != 2) {
                throw new IllegalArgumentException("S conecta con " + deS.size() + " tuberías, no con 2");
            }
            char formaDeS = TUBERIAS.chars()
                    .mapToObj(tuberia -> (char) tuberia)
                    .filter(tuberia -> conexiones(tuberia).equals(deS))
                    .findFirst()
                    .orElseThrow();

            List<Punto> casillas = new ArrayList<>(List.of(inicio));
            Direccion direccion = deS.iterator().next();
            Punto actual = inicio.mover(direccion);
            while (!actual.equals(inicio)) {
                casillas.add(actual);
                Set<Direccion> salidas = conexiones(grilla.obtener(actual, '.'));
                Direccion entrada = direccion.opuesto();
                if (!salidas.contains(entrada)) {
                    throw new IllegalArgumentException("El lazo se corta en " + actual);
                }
                direccion = salidas.stream().filter(salida -> salida != entrada).findFirst().orElseThrow();
                actual = actual.mover(direccion);
            }
            return new Lazo(List.copyOf(casillas), formaDeS);
        }

        /**
         * Cuántos pasos a lo largo del lazo hay de {@code S} a la casilla
         * número {@code i} del recorrido: el camino más corto de los dos
         * sentidos.
         */
        int distancia(int i) {
            return Math.min(i, casillas.size() - i);
        }

        /**
         * Cuántas casillas quedan encerradas por el lazo.
         *
         * <p>Los centros de las casillas del lazo, en orden, son los vértices
         * de un polígono con coordenadas enteras, y las casillas encerradas
         * son los puntos enteros de su interior: se cuentan con la fórmula de
         * Gauss y el teorema de Pick ({@link Poligono#puntosInteriores}).
         * Así no hace falta preocuparse por "colarse entre tuberías": se
         * cuentan los puntos de adentro del polígono, sin recorrer los
         * espacios de afuera.</p>
         *
         * @return la cantidad de casillas encerradas
         */
        long casillasEncerradas() {
            return Poligono.puntosInteriores(casillas);
        }
    }
}
