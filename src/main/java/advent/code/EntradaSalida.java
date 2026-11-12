package advent.code;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        String ruta = rutaEntrada(anio, dia);
        try (InputStream flujo = EntradaSalida.class.getResourceAsStream(ruta)) {
            if (flujo == null) {
                throw new IOException("Archivo de entrada no encontrado: " + ruta
                        + " (los inputs no se suben al repositorio: hay que pegarlos localmente)");
            }
            return IOUtils.readLines(flujo, StandardCharsets.UTF_8);
        }
    }

    /**
     * Indica si existe el archivo de entrada de un día. Los inputs están
     * excluidos del repositorio, así que pueden faltar (por ejemplo, en CI).
     *
     * @param anio año del desafío (ej. 2025)
     * @param dia  número de día (ej. 1)
     * @return {@code true} si el archivo existe
     */
    public static boolean existeEntrada(int anio, int dia) {
        return EntradaSalida.class.getResource(rutaEntrada(anio, dia)) != null;
    }

    private static String rutaEntrada(int anio, int dia) {
        return String.format("/inputs/y%d/day%02d.txt", anio, dia);
    }
}