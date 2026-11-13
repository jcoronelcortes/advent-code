package advent.code;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidades de parseo comunes para Advent of Code.
 *
 * <p>Para grillas de caracteres usar {@link Grilla}; para rangos
 * {@code inicio-fin}, {@link Rango} y {@link ConjuntoRangos}.</p>
 */
public final class Utiles {

    private Utiles() {}

    private static final Pattern PATRON_ENTERO = Pattern.compile("-?\\d+");

    // -------------------------------------------------------------------------
    // Líneas y secciones
    // -------------------------------------------------------------------------

    /**
     * Quita los espacios de los extremos de cada línea y descarta las que
     * quedan vacías (es común que el input termine con una línea en blanco).
     *
     * @param lineas líneas del input
     * @return líneas no vacías, sin espacios en los extremos, en el mismo orden
     */
    public static List<String> lineasNoVacias(List<String> lineas) {
        return lineas.stream()
                .map(String::strip)
                .filter(linea -> !linea.isEmpty())
                .toList();
    }

    /**
     * Separa el input en secciones delimitadas por líneas en blanco, un
     * formato muy común en AoC (ej. 2025 día 5: rangos, línea en blanco, IDs).
     *
     * <p>Varias líneas en blanco seguidas, o al principio o al final, no
     * generan secciones vacías. Las líneas de cada sección se devuelven sin
     * espacios en los extremos.</p>
     *
     * <p>Ejemplo: {@code ["3-5", "10-14", "", "1", "5"]} →
     * {@code [["3-5", "10-14"], ["1", "5"]]}</p>
     *
     * @param lineas líneas del input
     * @return secciones no vacías, en orden
     */
    public static List<List<String>> secciones(List<String> lineas) {
        List<List<String>> secciones = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        for (String linea : lineas) {
            String texto = linea.strip();
            if (texto.isEmpty()) {
                if (!actual.isEmpty()) secciones.add(List.copyOf(actual));
                actual.clear();
            } else {
                actual.add(texto);
            }
        }
        if (!actual.isEmpty()) secciones.add(List.copyOf(actual));
        return secciones;
    }

    /**
     * Igual que {@link #secciones(List)}, pero exige una cantidad exacta de
     * secciones: falla con un mensaje claro si el input no tiene el formato
     * esperado (ej. mapa y movimientos).
     *
     * @param lineas   líneas del input
     * @param cantidad cantidad de secciones esperada
     * @return las secciones
     * @throws IllegalArgumentException si no hay exactamente esa cantidad
     */
    public static List<List<String>> secciones(List<String> lineas, int cantidad) {
        return exigirCantidad(secciones(lineas), cantidad, "secciones separadas por línea en blanco", lineas);
    }

    // -------------------------------------------------------------------------
    // Números
    // -------------------------------------------------------------------------

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
     * Los números de cada línea, por separado: una lista por línea no vacía.
     * Es el formato de input más común después de la grilla (ej. 2024 días 1
     * y 2: una fila de números por línea).
     *
     * <p>Ejemplo: {@code ["7 6 4", "", "1 2"]} → {@code [[7, 6, 4], [1, 2]]}</p>
     *
     * @param lineas líneas del input
     * @return los números de cada línea no vacía, en orden
     * @throws ArithmeticException si algún número no cabe en un {@code int}
     *                             (usar {@link #numerosLargosPorLinea(List)})
     */
    public static List<List<Integer>> numerosPorLinea(List<String> lineas) {
        return lineasNoVacias(lineas).stream().map(Utiles::leerNumeros).toList();
    }

    /**
     * Igual que {@link #numerosPorLinea(List)}, pero con {@code long}.
     *
     * @param lineas líneas del input
     * @return los números de cada línea no vacía, en orden
     */
    public static List<List<Long>> numerosLargosPorLinea(List<String> lineas) {
        return lineasNoVacias(lineas).stream().map(Utiles::leerNumerosLargos).toList();
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

    // -------------------------------------------------------------------------
    // Números, con cantidad exacta
    // -------------------------------------------------------------------------

    /**
     * Igual que {@link #leerNumeros(String)}, pero exige una cantidad exacta
     * de números (ej. {@code p=0,4 v=3,-3} tiene 4).
     *
     * @param texto    cadena de entrada
     * @param cantidad cantidad de números esperada
     * @return los números, en orden
     * @throws IllegalArgumentException si no hay exactamente esa cantidad
     */
    public static List<Integer> leerNumeros(String texto, int cantidad) {
        return exigirCantidad(leerNumeros(texto), cantidad, "números", texto);
    }

    /**
     * Igual que {@link #leerNumerosLargos(String)}, pero exige una cantidad
     * exacta de números.
     *
     * @param texto    cadena de entrada
     * @param cantidad cantidad de números esperada
     * @return los números, en orden
     * @throws IllegalArgumentException si no hay exactamente esa cantidad
     */
    public static List<Long> leerNumerosLargos(String texto, int cantidad) {
        return exigirCantidad(leerNumerosLargos(texto), cantidad, "números", texto);
    }

    /**
     * Igual que {@link #leerNumerosLargos(List)}, pero exige una cantidad
     * exacta de números entre todas las líneas (ej. un bloque de 3 líneas
     * que describe una máquina con 6 números).
     *
     * @param lineas   lista de líneas
     * @param cantidad cantidad de números esperada
     * @return los números, en orden
     * @throws IllegalArgumentException si no hay exactamente esa cantidad
     */
    public static List<Long> leerNumerosLargos(List<String> lineas, int cantidad) {
        return exigirCantidad(leerNumerosLargos(lineas), cantidad, "números", lineas);
    }

    private static <T> List<T> exigirCantidad(List<T> lista, int cantidad, String que, Object origen) {
        if (lista.size() != cantidad) {
            throw new IllegalArgumentException(
                    "Se esperaban " + cantidad + " " + que + " y hay " + lista.size() + " en: " + origen);
        }
        return lista;
    }
}
