package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Año 2024 — Día 19: Disposición de Toallas
 *
 * <p>La primera línea del input lista los patrones de toalla disponibles
 * (secuencias de colores {@code w u b r g}, separadas por comas); después de
 * una línea en blanco viene un diseño por línea. Un diseño se puede armar si
 * es la concatenación de patrones disponibles, que se pueden repetir todas
 * las veces que haga falta y no se pueden dar vuelta. La parte 1 cuenta los
 * diseños posibles y la parte 2 suma de cuántas maneras se arma cada uno.</p>
 */
public class Dia19 implements Resolvedor {

    /**
     * Parte 1: cuántos diseños se pueden armar con las toallas disponibles.
     *
     * @param lineas los patrones y los diseños
     * @return la cantidad de diseños posibles
     */
    @Override
    public String parte1(List<String> lineas) {
        Onsen onsen = Onsen.parsear(lineas);
        long posibles = onsen.disenos().stream().filter(diseno -> onsen.maneras(diseno) > 0).count();
        return String.valueOf(posibles);
    }

    /**
     * Parte 2: la suma, sobre todos los diseños, de la cantidad de maneras
     * distintas de armar cada uno.
     *
     * @param lineas los patrones y los diseños
     * @return la suma de las maneras
     */
    @Override
    public String parte2(List<String> lineas) {
        Onsen onsen = Onsen.parsear(lineas);
        long total = onsen.disenos().stream().mapToLong(onsen::maneras).reduce(0, Math::addExact);
        return String.valueOf(total);
    }

    /**
     * Los patrones de toalla disponibles y los diseños pedidos.
     *
     * @param patrones   los patrones disponibles
     * @param disenos    los diseños, en orden
     * @param largoMaximo el largo del patrón más largo
     */
    record Onsen(Set<String> patrones, List<String> disenos, int largoMaximo) {

        /**
         * Lee el input: una línea de patrones separados por comas, una línea
         * en blanco y los diseños.
         *
         * @param lineas el input
         * @return el onsen
         */
        static Onsen parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas, 2);
            Set<String> patrones = Arrays.stream(String.join(",", secciones.get(0)).split(","))
                    .map(String::strip)
                    .filter(patron -> !patron.isEmpty())
                    .collect(Collectors.toSet());
            int largoMaximo = patrones.stream().mapToInt(String::length).max().orElse(0);
            return new Onsen(patrones, List.copyOf(secciones.get(1)), largoMaximo);
        }

        /**
         * De cuántas maneras distintas se puede armar un diseño con los
         * patrones disponibles (0 si no se puede).
         *
         * <p>Programación dinámica desde el final: {@code maneras[i]} es la
         * cantidad de maneras de armar lo que queda del diseño desde la
         * posición {@code i}. El final vacío se arma de una sola manera, sin
         * toallas. Desde {@code i}, cada patrón que coincide con el diseño en
         * esa posición deja por armar lo que sigue después de él. Solo hace
         * falta probar los prefijos de hasta {@code largoMaximo} letras, con
         * una búsqueda en el conjunto cada uno.</p>
         *
         * @param diseno el diseño
         * @return la cantidad de maneras
         * @throws ArithmeticException si la cantidad no cabe en un {@code long}
         */
        long maneras(String diseno) {
            int largo = diseno.length();
            long[] maneras = new long[largo + 1];
            maneras[largo] = 1;
            for (int i = largo - 1; i >= 0; i--) {
                for (int fin = i + 1; fin <= Math.min(largo, i + largoMaximo); fin++) {
                    if (maneras[fin] > 0 && patrones.contains(diseno.substring(i, fin))) {
                        maneras[i] = Math.addExact(maneras[i], maneras[fin]);
                    }
                }
            }
            return maneras[0];
        }
    }
}
