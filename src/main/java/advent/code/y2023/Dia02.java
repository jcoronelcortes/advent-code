package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2023 — Día 2: Enigma de los Cubos
 *
 * <p>Cada línea del input es una partida: {@code Game 1: 3 blue, 4 red; 1 red,
 * 2 green}. En cada partida, un Elfo saca varias veces un puñado de cubos
 * rojos, verdes y azules de una bolsa, los muestra y los vuelve a guardar;
 * cada puñado está separado por {@code ;}. En la parte 2 interesa el mínimo
 * de cubos de cada color que tenía que haber en la bolsa.</p>
 */
public class Dia02 implements Resolvedor {

    /** La bolsa de la parte 1: 12 rojos, 13 verdes y 14 azules. */
    static final Cubos BOLSA = new Cubos(12, 13, 14);

    /**
     * Parte 1: la suma de los números de las partidas que serían posibles
     * con 12 cubos rojos, 13 verdes y 14 azules en la bolsa.
     *
     * @param lineas las partidas
     * @return la suma de los números
     */
    @Override
    public String parte1(List<String> lineas) {
        int suma = partidas(lineas).stream()
                .filter(partida -> partida.posibleCon(BOLSA))
                .mapToInt(Partida::numero)
                .sum();
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la suma, sobre todas las partidas, de la potencia del mínimo
     * de cubos que tenía que haber en la bolsa.
     *
     * @param lineas las partidas
     * @return la suma de las potencias
     */
    @Override
    public String parte2(List<String> lineas) {
        long suma = partidas(lineas).stream()
                .mapToLong(partida -> partida.minimoNecesario().potencia())
                .sum();
        return String.valueOf(suma);
    }

    static List<Partida> partidas(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Partida::parsear).toList();
    }

    /**
     * Una cantidad de cubos de cada color.
     *
     * @param rojos  cubos rojos
     * @param verdes cubos verdes
     * @param azules cubos azules
     */
    record Cubos(int rojos, int verdes, int azules) {

        /**
         * Lee un puñado como {@code 3 blue, 4 red} (los colores que no
         * aparecen valen 0).
         *
         * @param texto el puñado
         * @return los cubos
         * @throws IllegalArgumentException si aparece un color desconocido
         */
        static Cubos parsear(String texto) {
            int rojos = 0, verdes = 0, azules = 0;
            for (String parte : texto.split(",")) {
                String[] cantidadYColor = parte.strip().split("\\s+");
                int cantidad = Integer.parseInt(cantidadYColor[0]);
                switch (cantidadYColor[1]) {
                    case "red" -> rojos += cantidad;
                    case "green" -> verdes += cantidad;
                    case "blue" -> azules += cantidad;
                    default -> throw new IllegalArgumentException("Color desconocido: '" + cantidadYColor[1] + "'");
                }
            }
            return new Cubos(rojos, verdes, azules);
        }

        /** El máximo de cada color entre estos cubos y otros. */
        Cubos maximo(Cubos otros) {
            return new Cubos(Math.max(rojos, otros.rojos), Math.max(verdes, otros.verdes),
                    Math.max(azules, otros.azules));
        }

        /** La potencia: la cantidad de rojos por la de verdes por la de azules. */
        long potencia() {
            return (long) rojos * verdes * azules;
        }

        /** Si de cada color hay a lo sumo lo que hay en {@code bolsa}. */
        boolean entranEn(Cubos bolsa) {
            return rojos <= bolsa.rojos && verdes <= bolsa.verdes && azules <= bolsa.azules;
        }
    }

    /**
     * Una partida: su número y los puñados que se mostraron.
     *
     * @param numero  el número de la partida
     * @param punados los puñados, en orden
     */
    record Partida(int numero, List<Cubos> punados) {

        /**
         * Lee una partida como {@code Game 1: 3 blue, 4 red; 1 red, 2 green}.
         *
         * @param linea la línea
         * @return la partida
         * @throws IllegalArgumentException si la línea no tiene esa forma
         */
        static Partida parsear(String linea) {
            String[] encabezadoYPunados = linea.split(":");
            if (encabezadoYPunados.length != 2) throw new IllegalArgumentException("Partida inválida: '" + linea + "'");
            int numero = Utiles.leerNumeros(encabezadoYPunados[0], 1).getFirst();
            List<Cubos> punados = new ArrayList<>();
            for (String punado : encabezadoYPunados[1].split(";")) punados.add(Cubos.parsear(punado));
            return new Partida(numero, List.copyOf(punados));
        }

        /**
         * Lo mínimo que tiene que haber de cada color en la bolsa para que
         * esta partida sea posible: el máximo que se mostró de cada uno.
         */
        Cubos minimoNecesario() {
            return punados.stream().reduce(new Cubos(0, 0, 0), Cubos::maximo);
        }

        /**
         * Si la partida podría haberse jugado con esa bolsa: ningún puñado
         * mostró más cubos de un color de los que hay. Alcanza con comparar
         * el máximo de cada color, porque los cubos se devuelven a la bolsa
         * después de cada puñado.
         */
        boolean posibleCon(Cubos bolsa) {
            return minimoNecesario().entranEn(bolsa);
        }
    }
}
