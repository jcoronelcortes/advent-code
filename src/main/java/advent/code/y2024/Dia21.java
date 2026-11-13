package advent.code.y2024;

import advent.code.Memoizador;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Año 2024 — Día 21: Enigma de los Teclados
 *
 * <p>Hay que teclear unos códigos en un teclado numérico, pero a través de
 * una cadena de robots: un robot teclea en el numérico, otro robot le indica
 * los movimientos tecleando en un teclado direccional ({@code ^ v < > A}), y
 * así sucesivamente hasta el teclado direccional que usamos nosotros. Cada
 * brazo empieza apuntando a la {@code A} de su teclado y nunca puede pasar
 * por el hueco sin botón. En la parte 1 hay 2 robots en teclados
 * direccionales y en la parte 2, 25.</p>
 */
public class Dia21 implements Resolvedor {

    /** Robots que teclean en teclados direccionales en la parte 1. */
    static final int ROBOTS_PARTE1 = 2;

    /** Robots que teclean en teclados direccionales en la parte 2. */
    static final int ROBOTS_PARTE2 = 25;

    /** Teclado numérico de la puerta ({@code ' '} es el hueco). */
    static final Teclado NUMERICO = Teclado.desdeFilas("789", "456", "123", " 0A");

    /** Teclado direccional ({@code ' '} es el hueco). */
    static final Teclado DIRECCIONAL = Teclado.desdeFilas(" ^A", "<v>");

    /**
     * Parte 1: suma de las complejidades de los códigos, con dos robots en
     * teclados direccionales entre nosotros y el robot de la puerta.
     *
     * @param lineas un código por línea
     * @return la suma de las complejidades
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(new Cadena(ROBOTS_PARTE1).sumaDeComplejidades(Utiles.lineasNoVacias(lineas)));
    }

    /**
     * Parte 2: lo mismo, con 25 robots en teclados direccionales.
     *
     * @param lineas un código por línea
     * @return la suma de las complejidades
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(new Cadena(ROBOTS_PARTE2).sumaDeComplejidades(Utiles.lineasNoVacias(lineas)));
    }

    /**
     * Un teclado: la posición de cada botón y la del hueco.
     *
     * @param botones posición de cada botón ({@code x} columna, {@code y} fila)
     * @param hueco   la posición sin botón, por donde no puede pasar el brazo
     */
    record Teclado(Map<Character, Punto> botones, Punto hueco) {

        /** Arma el teclado a partir de sus filas, con {@code ' '} en el hueco. */
        static Teclado desdeFilas(String... filas) {
            Map<Character, Punto> botones = new HashMap<>();
            Punto hueco = null;
            for (int y = 0; y < filas.length; y++) {
                for (int x = 0; x < filas[y].length(); x++) {
                    char boton = filas[y].charAt(x);
                    if (boton == ' ') hueco = new Punto(x, y);
                    else botones.put(boton, new Punto(x, y));
                }
            }
            return new Teclado(Map.copyOf(botones), hueco);
        }

        Punto posicion(char boton) {
            Punto posicion = botones.get(boton);
            if (posicion == null) throw new IllegalArgumentException("El teclado no tiene el botón '" + boton + "'");
            return posicion;
        }

        /**
         * Todos los recorridos más cortos del brazo de un botón a otro que no
         * pasan por el hueco, como flechas del teclado direccional (sin la
         * {@code A} final).
         *
         * <p>Un recorrido más corto tiene exactamente |dx| flechas
         * horizontales y |dy| verticales, en cualquier orden. Se generan
         * todos los órdenes descartando los que pisan el hueco: a lo sumo
         * C(5, 2) = 10 en el teclado numérico.</p>
         *
         * @param desde botón donde está el brazo
         * @param hasta botón al que tiene que llegar
         * @return los recorridos (uno vacío si {@code desde == hasta})
         */
        List<String> recorridos(char desde, char hasta) {
            List<String> recorridos = new ArrayList<>();
            agregarRecorridos(posicion(desde), posicion(hasta), new StringBuilder(), recorridos);
            return recorridos;
        }

        private void agregarRecorridos(Punto actual, Punto destino, StringBuilder flechas, List<String> recorridos) {
            if (actual.equals(hueco)) return;
            if (actual.equals(destino)) {
                recorridos.add(flechas.toString());
                return;
            }
            if (destino.x() != actual.x()) {
                char flecha = destino.x() > actual.x() ? '>' : '<';
                avanzar(actual, destino, flechas, recorridos, flecha, Integer.signum(destino.x() - actual.x()), 0);
            }
            if (destino.y() != actual.y()) {
                char flecha = destino.y() > actual.y() ? 'v' : '^';
                avanzar(actual, destino, flechas, recorridos, flecha, 0, Integer.signum(destino.y() - actual.y()));
            }
        }

        private void avanzar(Punto actual, Punto destino, StringBuilder flechas, List<String> recorridos,
                             char flecha, int dx, int dy) {
            flechas.append(flecha);
            agregarRecorridos(new Punto(actual.x() + dx, actual.y() + dy), destino, flechas, recorridos);
            flechas.setLength(flechas.length() - 1);
        }
    }

    /**
     * La cadena de teclados: el numérico de la puerta, {@code robots}
     * teclados direccionales usados por robots, y el direccional que usamos
     * nosotros.
     */
    static final class Cadena {

        /** Un movimiento del brazo en un teclado direccional, a {@code capas} robots de nosotros. */
        private record Movimiento(char desde, char hasta, int capas) {}

        private final int robots;
        private final Memoizador<Movimiento, Long> costoDireccional;

        /**
         * @param robots cuántos robots teclean en teclados direccionales
         */
        Cadena(int robots) {
            this.robots = robots;
            this.costoDireccional = new Memoizador<>((movimiento, costo) -> mejorRecorrido(
                    DIRECCIONAL, movimiento.desde(), movimiento.hasta(), movimiento.capas() - 1, costo));
        }

        /**
         * Cuántas teclas tenemos que apretar nosotros para que el robot de la
         * puerta teclee el código.
         *
         * @param codigo el código del teclado numérico
         * @return la cantidad mínima de teclas
         */
        long teclasMinimas(String codigo) {
            long total = 0;
            char brazo = 'A';
            for (char boton : codigo.toCharArray()) {
                total = Math.addExact(total, mejorRecorrido(NUMERICO, brazo, boton, robots, costoDireccional));
                brazo = boton;
            }
            return total;
        }

        /**
         * Complejidad de un código: teclas mínimas por la parte numérica del
         * código (sin ceros a la izquierda).
         *
         * @param codigo el código
         * @return la complejidad
         */
        long complejidad(String codigo) {
            return Math.multiplyExact(teclasMinimas(codigo), Long.parseLong(codigo.replaceAll("\\D", "")));
        }

        /**
         * Suma de las complejidades de varios códigos.
         *
         * @param codigos los códigos
         * @return la suma
         */
        long sumaDeComplejidades(List<String> codigos) {
            return codigos.stream().mapToLong(this::complejidad).reduce(0, Math::addExact);
        }

        /**
         * Lo mínimo que cuesta (en teclas nuestras) mover el brazo de un
         * teclado de {@code desde} a {@code hasta} y apretar el botón, cuando
         * a ese brazo lo maneja un teclado direccional que está a
         * {@code capas} robots de nosotros ({@code capas = 0}: lo tecleamos
         * nosotros).
         *
         * <p>Cada recorrido posible se convierte en flechas más una
         * {@code A}, que hay que teclear en el direccional de arriba. El
         * brazo de ese direccional empieza en {@code A} (siempre vuelve a
         * {@code A} al apretar el botón de abajo), así que el costo de
         * teclear la secuencia es la suma del costo de cada par de teclas
         * consecutivas, empezando desde {@code A}. Por eso los movimientos se
         * pueden memoizar de forma independiente.</p>
         */
        private static long mejorRecorrido(Teclado teclado, char desde, char hasta, int capas,
                                           Function<Movimiento, Long> costo) {
            long mejor = Long.MAX_VALUE;
            for (String recorrido : teclado.recorridos(desde, hasta)) {
                String secuencia = recorrido + "A";
                long teclas;
                if (capas == 0) {
                    teclas = secuencia.length();
                } else {
                    teclas = 0;
                    char brazo = 'A';
                    for (char tecla : secuencia.toCharArray()) {
                        teclas = Math.addExact(teclas, costo.apply(new Movimiento(brazo, tecla, capas)));
                        brazo = tecla;
                    }
                }
                mejor = Math.min(mejor, teclas);
            }
            return mejor;
        }
    }
}
