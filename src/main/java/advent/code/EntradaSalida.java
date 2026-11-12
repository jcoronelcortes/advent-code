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
public class EntradaSalida {

    /**
     * Imprime un mensaje en la consola.
     *
     * @param mensaje texto a imprimir
     */
    public static void imprimirLinea(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Lee el archivo de entrada desde {@code src/main/resources/inputs/y{anio}/day{dia}.txt}.
     *
     * @param anio año del desafío (ej. 2025)
     * @param dia  número de día (ej. 1)
     * @return lista de líneas del archivo
     * @throws IOException si el archivo no existe
     */
    public static List<String> leerEntrada(int anio, int dia) throws IOException {
        String ruta = String.format("/inputs/y%d/day%02d.txt", anio, dia);
        InputStream flujo = EntradaSalida.class.getResourceAsStream(ruta);
        if (flujo == null) {
            throw new IOException("Archivo de entrada no encontrado: " + ruta);
        }
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(flujo))) {
            return lector.lines().collect(Collectors.toList());
        }
    }
}