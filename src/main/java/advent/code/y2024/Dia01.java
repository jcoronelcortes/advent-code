package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2024 — Día 1: Histeria del Historiador
 *
 * <p>El input son dos listas de IDs de ubicaciones puestas lado a lado: cada
 * línea tiene un número de la lista izquierda y uno de la derecha.</p>
 */
public class Dia01 implements Resolvedor {

    /**
     * Parte 1: distancia total entre las listas, emparejando el menor de la
     * izquierda con el menor de la derecha, el segundo menor con el segundo
     * menor, etc., y sumando la diferencia de cada par.
     *
     * @param lineas pares {@code izquierda derecha}
     * @return la distancia total
     */
    @Override
    public String parte1(List<String> lineas) {
        Listas listas = Listas.parsear(lineas);
        List<Long> izquierda = listas.izquierda().stream().sorted().toList();
        List<Long> derecha = listas.derecha().stream().sorted().toList();

        long total = 0;
        for (int i = 0; i < izquierda.size(); i++) {
            total += Math.abs(izquierda.get(i) - derecha.get(i));
        }
        return String.valueOf(total);
    }

    /**
     * Parte 2: puntuación de similitud. Cada número de la lista izquierda
     * suma su valor multiplicado por la cantidad de veces que aparece en la
     * lista derecha.
     *
     * <p>Las apariciones en la derecha se cuentan una sola vez con un
     * {@link Multiset} de Guava (un conjunto que recuerda cuántas veces está
     * cada elemento), así cada consulta cuesta O(1) en vez de recorrer la
     * lista entera.</p>
     *
     * @param lineas pares {@code izquierda derecha}
     * @return la puntuación de similitud
     */
    @Override
    public String parte2(List<String> lineas) {
        Listas listas = Listas.parsear(lineas);
        Multiset<Long> apariciones = HashMultiset.create(listas.derecha());

        long similitud = 0;
        for (long numero : listas.izquierda()) {
            similitud = Math.addExact(similitud, Math.multiplyExact(numero, apariciones.count(numero)));
        }
        return String.valueOf(similitud);
    }

    /**
     * Las dos listas del input, en el orden en que aparecen.
     *
     * @param izquierda números de la columna izquierda
     * @param derecha   números de la columna derecha
     */
    record Listas(List<Long> izquierda, List<Long> derecha) {

        /**
         * Separa cada línea en sus dos números, ignorando las líneas en blanco.
         *
         * @param lineas pares {@code izquierda derecha}
         * @return las dos listas
         * @throws IllegalArgumentException si una línea no tiene exactamente dos números
         */
        static Listas parsear(List<String> lineas) {
            List<Long> izquierda = new ArrayList<>();
            List<Long> derecha = new ArrayList<>();
            for (String linea : Utiles.lineasNoVacias(lineas)) {
                List<Long> numeros = Utiles.leerNumerosLargos(linea);
                if (numeros.size() != 2) {
                    throw new IllegalArgumentException("Se esperaban 2 números en '" + linea + "'");
                }
                izquierda.add(numeros.get(0));
                derecha.add(numeros.get(1));
            }
            return new Listas(izquierda, derecha);
        }
    }
}
