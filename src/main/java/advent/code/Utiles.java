package advent.code;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidades de parseo comunes para Advent of Code.
 *
 * <p>Para grillas de caracteres usar {@link Grilla}; para rangos
 * {@code inicio-fin}, {@link Rango}.</p>
 */
public final class Utiles {

    private Utiles() {}

    private static final Pattern PATRON_ENTERO = Pattern.compile("-?\\d+");

    /**
     * Extrae todos los números enteros (incluyendo negativos) de una cadena.
     * Útil para parsear líneas con múltiples números mezclados con texto.
     *
     * <p>Ejemplo: {@code "Posición: x=-5, y=10"} → {@code [-5, 10]}</p>
     *
     * @param texto cadena de entrada
     * @return lista de enteros encontrados en orden de aparición
     * @throws ArithmeticException si algún número no cabe en un {@code int}
     *                             (usar {@link #leerNumerosLargos(String)})
     */
    public static List<Integer> leerNumeros(String texto) {
        return leerNumerosLargos(texto).stream().map(Math::toIntExact).toList();
    }

    /**
     * Extrae todos los números enteros de múltiples líneas.
     *
     * @param lineas lista de líneas
     * @return lista de enteros de todas las líneas en orden
     */
    public static List<Integer> leerNumeros(List<String> lineas) {
        List<Integer> numeros = new ArrayList<>();
        for (String linea : lineas) {
            numeros.addAll(leerNumeros(linea));
        }
        return numeros;
    }

    /**
     * Igual que {@link #leerNumeros(String)}, pero con {@code long}: los inputs
     * de AoC suelen traer números de más de 10 dígitos (ej. 2025 días 2 y 3).
     *
     * @param texto cadena de entrada
     * @return lista de números encontrados en orden de aparición
     * @throws NumberFormatException si algún número no cabe en un {@code long}
     */
    public static List<Long> leerNumerosLargos(String texto) {
        List<Long> numeros = new ArrayList<>();
        Matcher matcher = PATRON_ENTERO.matcher(texto);
        while (matcher.find()) {
            numeros.add(Long.parseLong(matcher.group()));
        }
        return numeros;
    }

    /**
     * Extrae todos los números ({@code long}) de múltiples líneas.
     *
     * @param lineas lista de líneas
     * @return lista de números de todas las líneas en orden
     */
    public static List<Long> leerNumerosLargos(List<String> lineas) {
        List<Long> numeros = new ArrayList<>();
        for (String linea : lineas) {
            numeros.addAll(leerNumerosLargos(linea));
        }
        return numeros;
    }
}
