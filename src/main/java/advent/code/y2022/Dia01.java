package advent.code.y2022;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Comparator;
import java.util.List;

/**
 * Año 2022 — Día 1: Contando Calorías
 *
 * <p>El input es el inventario de los elfos: las calorías de cada alimento,
 * uno por línea, con una línea en blanco entre el inventario de un elfo y el
 * del siguiente.</p>
 */
public class Dia01 implements Resolvedor {

    /**
     * Parte 1: cuántas calorías lleva el elfo que más lleva.
     *
     * @param lineas el inventario
     * @return el mayor total de calorías
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(sumaDeLosMayores(totales(lineas), 1));
    }

    /**
     * Parte 2: cuántas calorías llevan entre los tres elfos que más llevan.
     *
     * @param lineas el inventario
     * @return la suma de los tres mayores totales
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(sumaDeLosMayores(totales(lineas), 3));
    }

    /**
     * La suma de los {@code k} mayores totales: se ordenan de mayor a menor y
     * se suman los primeros {@code k}.
     *
     * @param totales el total de cada elfo
     * @param k       cuántos elfos sumar (al menos 1)
     * @return la suma de los {@code k} mayores
     * @throws IllegalArgumentException si {@code k} no es positivo o hay menos de {@code k} elfos
     */
    static long sumaDeLosMayores(List<Long> totales, int k) {
        if (k < 1 || totales.size() < k) {
            throw new IllegalArgumentException("Se piden los " + k + " mayores y hay " + totales.size() + " elfos");
        }
        return totales.stream()
                .sorted(Comparator.reverseOrder())
                .limit(k)
                .mapToLong(Long::longValue)
                .sum();
    }

    /**
     * El total de calorías de cada elfo, en el orden del input: cada bloque
     * separado por líneas en blanco es un elfo, y su total es la suma de sus
     * líneas.
     *
     * @param lineas el inventario
     * @return el total de cada elfo
     * @throws IllegalArgumentException si una línea no es un único número
     */
    static List<Long> totales(List<String> lineas) {
        return Utiles.secciones(lineas).stream()
                .map(elfo -> elfo.stream().mapToLong(Dia01::calorias).sum())
                .toList();
    }

    /** Las calorías de un alimento: la línea tiene que ser un único número no negativo. */
    private static long calorias(String linea) {
        if (!linea.matches("\\d+")) throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
        return Long.parseLong(linea);
    }
}
