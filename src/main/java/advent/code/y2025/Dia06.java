package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Matematicas;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Año 2025 — Día 6: Compactador de Basura
 *
 * <p>El input es una hoja de ejercicios de matemáticas de cefalópodos: varios
 * problemas puestos uno al lado del otro. Cada problema ocupa un bloque de
 * columnas con sus números en vertical (uno por fila) y, en la última fila,
 * la operación ({@code +} o {@code *}). Los problemas están separados por
 * una columna completa de espacios.</p>
 *
 * <pre>
 * 123 328  51 64
 *  45 64  387 23
 *   6 98  215 314
 * *   +   *   +
 * </pre>
 */
public class Dia06 implements Resolvedor {

    /**
     * Parte 1: suma de los resultados de todos los problemas, leyendo cada
     * fila del bloque como un número.
     *
     * @param lineas filas de la hoja de ejercicios
     * @return el gran total
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(granTotal(lineas, Problema::numerosPorFila));
    }

    /**
     * Parte 2: suma de los resultados leyendo los números "a lo cefalópodo":
     * cada <b>columna</b> del bloque es un número, con el dígito más
     * significativo arriba.
     *
     * <p>El enunciado dice que se leen de derecha a izquierda, pero como la
     * suma y el producto son conmutativos el orden no cambia el resultado.</p>
     *
     * @param lineas filas de la hoja de ejercicios
     * @return el gran total
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(granTotal(lineas, Problema::numerosPorColumna));
    }

    /**
     * Resuelve todos los problemas con la forma de leer los números indicada
     * y suma los resultados.
     *
     * @param lineas  filas de la hoja de ejercicios
     * @param lectura cómo obtener los números de un problema
     * @return el gran total
     */
    static long granTotal(List<String> lineas, Function<Problema, List<Long>> lectura) {
        long total = 0;
        for (Problema problema : leerProblemas(lineas)) {
            total = Math.addExact(total, problema.resolver(lectura));
        }
        return total;
    }

    /**
     * Un problema de la hoja: un bloque rectangular de texto.
     *
     * <p>Se guarda el texto del bloque tal cual (con sus espacios) y no solo
     * los números, porque en la parte 2 la alineación importa: los números
     * se leen por columnas.</p>
     *
     * @param filas     filas del bloque con números, todas del mismo ancho
     * @param operacion {@code '+'} o {@code '*'}
     */
    record Problema(List<String> filas, char operacion) {

        /**
         * Números del problema leyendo cada fila del bloque (ignorando la
         * alineación a izquierda o derecha).
         *
         * @return números de arriba hacia abajo
         */
        List<Long> numerosPorFila() {
            return filas.stream()
                    .map(String::strip)
                    .filter(texto -> !texto.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        }

        /**
         * Números del problema leyendo cada columna del bloque de arriba hacia
         * abajo (parte 2), de derecha a izquierda como indica el enunciado.
         * Los espacios de la columna se ignoran.
         *
         * <p>Ejemplo: el bloque {@code ["64 ", "23 ", "314"]} tiene las
         * columnas {@code "623"}, {@code "431"} y {@code "  4"}; de derecha a
         * izquierda: {@code [4, 431, 623]}.</p>
         *
         * @return números de derecha a izquierda
         */
        List<Long> numerosPorColumna() {
            List<Long> numeros = new ArrayList<>();
            int ancho = filas.getFirst().length();
            for (int columna = ancho - 1; columna >= 0; columna--) {
                StringBuilder digitos = new StringBuilder();
                for (String fila : filas) {
                    char caracter = fila.charAt(columna);
                    if (caracter != ' ') digitos.append(caracter);
                }
                if (!digitos.isEmpty()) numeros.add(Long.parseLong(digitos.toString()));
            }
            return numeros;
        }

        /**
         * Resuelve el problema con los números leídos por fila (parte 1).
         *
         * @return la suma o el producto de los números
         */
        long resolver() {
            return resolver(Problema::numerosPorFila);
        }

        /**
         * Resuelve el problema con la forma de leer los números indicada.
         *
         * @param lectura {@link #numerosPorFila} o {@link #numerosPorColumna}
         * @return la suma o el producto de los números
         */
        long resolver(Function<Problema, List<Long>> lectura) {
            return aplicar(operacion, lectura.apply(this));
        }
    }

    /**
     * Aplica la operación a todos los números. Usa aritmética exacta: si un
     * resultado no cabe en un {@code long} lanza una excepción en vez de
     * desbordarse en silencio.
     *
     * @param operacion {@code '+'} o {@code '*'}
     * @param numeros   números a operar
     * @return la suma o el producto
     */
    static long aplicar(char operacion, List<Long> numeros) {
        return switch (operacion) {
            case '+' -> Matematicas.sumaExacta(numeros);
            case '*' -> Matematicas.productoExacto(numeros);
            default -> throw new IllegalArgumentException("Operación desconocida: '" + operacion + "'");
        };
    }

    /**
     * Separa la hoja en problemas.
     *
     * <p>Las líneas del input no tienen todas el mismo largo (los espacios
     * del final se recortan), así que se arma una {@link Grilla} completando
     * con espacios hasta el ancho máximo. Después se recorren las columnas:
     * una columna formada solo por espacios separa dos problemas; cada tramo
     * de columnas no vacías es un problema.</p>
     *
     * @param lineas filas de la hoja (la última es la de operaciones)
     * @return problemas de izquierda a derecha
     */
    static List<Problema> leerProblemas(List<String> lineas) {
        Grilla hoja = Grilla.desdeLineas(lineas, ' ');
        int ancho = hoja.columnas();

        List<Problema> problemas = new ArrayList<>();
        int inicio = -1;                        // primera columna del problema actual
        for (int columna = 0; columna <= ancho; columna++) {
            boolean separador = columna == ancho || hoja.columna(columna).isBlank();
            if (!separador && inicio < 0) {
                inicio = columna;
            } else if (separador && inicio >= 0) {
                problemas.add(recortar(hoja, inicio, columna));
                inicio = -1;
            }
        }
        return problemas;
    }

    /** Extrae el bloque de columnas {@code [desde, hasta)} como un problema. */
    private static Problema recortar(Grilla hoja, int desde, int hasta) {
        int filaOperaciones = hoja.filas() - 1;
        List<String> filas = new ArrayList<>(filaOperaciones);
        for (int fila = 0; fila < filaOperaciones; fila++) {
            filas.add(hoja.fila(fila).substring(desde, hasta));
        }
        char operacion = hoja.fila(filaOperaciones).substring(desde, hasta).strip().charAt(0);
        return new Problema(filas, operacion);
    }
}
