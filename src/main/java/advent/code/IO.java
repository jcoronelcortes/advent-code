package advent.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilidades de entrada/salida para los desafíos de Advent of Code.
 */
public class IO {

    /**
     * Imprime un mensaje en la consola.
     *
     * @param mensaje texto a imprimir
     */
    public static void println(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Lee el archivo de input desde {@code src/main/resources/inputs/y{año}/day{día}.txt}.
     *
     * @param año  año del desafío (ej. 2025)
     * @param día  número de día (ej. 1)
     * @return lista de líneas del archivo
     * @throws IOException si el archivo no existe
     */
    public static List<String> readInput(int año, int día) throws IOException {
        String ruta = String.format("/inputs/y%d/day%02d.txt", año, día);
        InputStream is = IO.class.getResourceAsStream(ruta);
        if (is == null) {
            throw new IOException("Input no encontrado: " + ruta);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}