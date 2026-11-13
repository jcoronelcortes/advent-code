package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Año 2023 — Día 1: Calibración del Trebuchet
 *
 * <p>Cada línea del input es un texto con cifras mezcladas entre letras. El
 * valor de calibración de una línea es el número de dos cifras formado por
 * su primera cifra y su última cifra (que pueden ser la misma). En la
 * parte 2 también cuentan como cifras las palabras en inglés de
 * {@code one} a {@code nine}.</p>
 */
public class Dia01 implements Resolvedor {

    /** Las cifras del 1 al 9 escritas como tales. */
    static final Map<String, Integer> CIFRAS = Map.of(
            "1", 1, "2", 2, "3", 3, "4", 4, "5", 5, "6", 6, "7", 7, "8", 8, "9", 9);

    /** Las cifras del 1 al 9 escritas como tales o con su nombre en inglés. */
    static final Map<String, Integer> CIFRAS_Y_PALABRAS = conPalabras();

    private static Map<String, Integer> conPalabras() {
        Map<String, Integer> cifras = new HashMap<>(CIFRAS);
        List<String> palabras = List.of("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
        for (int i = 0; i < palabras.size(); i++) cifras.put(palabras.get(i), i + 1);
        return Map.copyOf(cifras);
    }

    /**
     * Parte 1: la suma de los valores de calibración de todas las líneas.
     *
     * @param lineas el documento de calibración
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(sumaDeValores(lineas, CIFRAS));
    }

    /**
     * Parte 2: la misma suma, contando también como cifras las palabras
     * {@code one} a {@code nine}.
     *
     * @param lineas el documento de calibración
     * @return la suma
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(sumaDeValores(lineas, CIFRAS_Y_PALABRAS));
    }

    /**
     * La suma de los valores de calibración de todas las líneas.
     *
     * @param lineas  el documento
     * @param cifras  qué textos cuentan como cifra y cuánto valen
     * @return la suma
     */
    static long sumaDeValores(List<String> lineas, Map<String, Integer> cifras) {
        return Utiles.lineasNoVacias(lineas).stream().mapToLong(linea -> valor(linea, cifras)).sum();
    }

    /**
     * El valor de calibración de una línea: 10 × la primera cifra + la última.
     *
     * <p>La primera cifra se busca recorriendo la línea desde el comienzo y
     * mirando, en cada posición, si empieza ahí alguno de los textos de
     * {@code cifras}; la última, recorriéndola desde el final. Buscar desde
     * cada punta (en vez de reemplazar textos en la línea) hace que una cifra
     * pueda compartir letras con otra sin perderse.</p>
     *
     * @param linea  la línea
     * @param cifras qué textos cuentan como cifra y cuánto valen
     * @return el valor de calibración
     * @throws IllegalArgumentException si la línea no tiene ninguna cifra
     */
    static int valor(String linea, Map<String, Integer> cifras) {
        Integer primera = null;
        for (int i = 0; i < linea.length() && primera == null; i++) primera = cifraEn(linea, i, cifras);
        Integer ultima = null;
        for (int i = linea.length() - 1; i >= 0 && ultima == null; i--) ultima = cifraEn(linea, i, cifras);
        if (primera == null) throw new IllegalArgumentException("La línea no tiene cifras: '" + linea + "'");
        return 10 * primera + ultima;
    }

    /** La cifra cuyo texto empieza en la posición {@code i}, o {@code null} si no empieza ninguna. */
    private static Integer cifraEn(String linea, int i, Map<String, Integer> cifras) {
        for (Map.Entry<String, Integer> cifra : cifras.entrySet()) {
            if (linea.startsWith(cifra.getKey(), i)) return cifra.getValue();
        }
        return null;
    }
}
