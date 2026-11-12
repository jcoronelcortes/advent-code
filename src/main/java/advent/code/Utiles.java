package advent.code;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidades comunes para resolver problemas de Advent of Code.
 */
public class Utiles {

    private static final Pattern PATRON_ENTERO = Pattern.compile("-?\\d+");

    /**
     * Extrae todos los números enteros (incluyendo negativos) de una cadena.
     * Útil para parsear líneas con múltiples números mezclados con texto.
     *
     * <p>Ejemplo: {@code "Posición: x=-5, y=10"} → {@code [-5, 10]}</p>
     *
     * @param texto cadena de entrada
     * @return lista de enteros encontrados en orden de aparición
     */
    public static List<Integer> leerNumeros(String texto) {
        List<Integer> numeros = new ArrayList<>();
        Matcher matcher = PATRON_ENTERO.matcher(texto);
        while (matcher.find()) {
            numeros.add(NumberUtils.toInt(matcher.group()));
        }
        return numeros;
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
     * Convierte una lista de líneas en una grilla de caracteres.
     *
     * @param lineas lista de líneas del input
     * @return matriz de caracteres {@code [fila][columna]}
     */
    public static char[][] leerGrilla(List<String> lineas) {
        int filas = lineas.size();
        int columnas = lineas.isEmpty() ? 0 : lineas.get(0).length();
        char[][] grilla = new char[filas][columnas];
        for (int fila = 0; fila < filas; fila++) {
            grilla[fila] = lineas.get(fila).toCharArray();
        }
        return grilla;
    }

    /**
     * Transpone una grilla de caracteres (filas ↔ columnas).
     *
     * @param grilla matriz original {@code [fila][columna]}
     * @return nueva matriz transpuesta {@code [columna][fila]}
     */
    public static char[][] transponerGrilla(char[][] grilla) {
        int filas = grilla.length;
        int columnas = grilla[0].length;
        char[][] transpuesta = new char[columnas][filas];
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                transpuesta[col][fila] = grilla[fila][col];
            }
        }
        return transpuesta;
    }

    /**
     * Obtiene el carácter en la posición indicada por un {@link Punto}.
     *
     * @param grilla matriz de caracteres
     * @param punto  posición {@code (x=columna, y=fila)}
     * @return carácter en esa posición
     */
    public static char obtenerCaracter(char[][] grilla, Punto punto) {
        return grilla[punto.y][punto.x];
    }
}