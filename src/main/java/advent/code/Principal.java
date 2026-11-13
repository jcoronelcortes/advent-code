package advent.code;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Punto de entrada principal.
 *
 * <ul>
 *   <li>{@code Principal <año> <día>}: ejecuta ambas partes de un día y
 *       muestra el resultado y el tiempo de cada una.</li>
 *   <li>{@code Principal <año>}: ejecuta todos los días resueltos de ese año
 *       (los que tienen clase e input) y muestra una tabla con las respuestas,
 *       los tiempos y el total. Sirve para ver de un vistazo si algún día se
 *       volvió lento después de un refactor.</li>
 * </ul>
 */
public class Principal {

    /** Último día de Advent of Code (25 hasta 2024; desde 2025 son 12, pero no hace daño probar hasta 25). */
    private static final int ULTIMO_DIA = 25;

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            ejecutarAnio(Integer.parseInt(args[0]));
        } else if (args.length >= 2) {
            ejecutarDia(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            System.out.println("Uso: Principal <año> [<día>]  (ej. \"2025 1\" para un día, \"2025\" para todo el año)");
        }
    }

    /**
     * Crea la instancia de la solución de un día: {@code advent.code.y{anio}.Dia{DD}}.
     *
     * @param anio año del desafío (ej. 2025)
     * @param dia  número de día (ej. 1)
     * @return la solución del día
     * @throws ReflectiveOperationException si la clase no existe o no se puede instanciar
     */
    public static Resolvedor cargarResolvedor(int anio, int dia) throws ReflectiveOperationException {
        String nombreClase = String.format("advent.code.y%d.Dia%02d", anio, dia);
        return (Resolvedor) Class.forName(nombreClase)
                                 .getDeclaredConstructor()
                                 .newInstance();
    }

    /** Ejecuta ambas partes de un día e imprime cada resultado con su tiempo. */
    private static void ejecutarDia(int anio, int dia) throws Exception {
        Resolvedor resolvedor = cargarResolvedor(anio, dia);
        List<String> lineas = EntradaSalida.leerEntrada(anio, dia);

        System.out.println("=== Año " + anio + " — Día " + dia + " ===");
        Medicion parte1 = medir(() -> resolvedor.parte1(lineas));
        System.out.printf("Parte 1: %s  (%.1f ms)%n", parte1.resultado(), parte1.milisegundos());
        Medicion parte2 = medir(() -> resolvedor.parte2(lineas));
        System.out.printf("Parte 2: %s  (%.1f ms)%n", parte2.resultado(), parte2.milisegundos());
    }

    /**
     * Ejecuta todos los días del año que tengan clase e input, y muestra una
     * tabla con las respuestas y los tiempos. Los días sin clase o sin input
     * se omiten en silencio (todavía no se resolvieron o falta pegar el input).
     */
    private static void ejecutarAnio(int anio) throws Exception {
        System.out.println("=== Año " + anio + " ===");

        List<List<String>> filas = new ArrayList<>();
        double total = 0;
        for (int dia = 1; dia <= ULTIMO_DIA; dia++) {
            if (!EntradaSalida.existeEntrada(anio, dia)) continue;
            Resolvedor resolvedor;
            try {
                resolvedor = cargarResolvedor(anio, dia);
            } catch (ClassNotFoundException noResuelto) {
                continue;
            }

            List<String> lineas = EntradaSalida.leerEntrada(anio, dia);
            Medicion parte1 = medir(() -> resolvedor.parte1(lineas));
            Medicion parte2 = medir(() -> resolvedor.parte2(lineas));
            filas.add(List.of(String.format("%02d", dia),
                    parte1.resultado(), String.format("%.1f", parte1.milisegundos()),
                    parte2.resultado(), String.format("%.1f", parte2.milisegundos())));
            total += parte1.milisegundos() + parte2.milisegundos();
        }

        tabla(List.of("Día", "Parte 1", "ms", "Parte 2", "ms"), filas).forEach(System.out::println);
        System.out.printf("%d días en %.1f ms%n", filas.size(), total);
    }

    /**
     * Arma las líneas de la tabla anual con cada columna tan ancha como su
     * texto más largo (encabezado incluido), para que las respuestas largas
     * no desalineen las demás columnas. Las columnas de tiempo ({@code ms})
     * se alinean a la derecha; las demás, a la izquierda.
     *
     * @param encabezados los títulos de las columnas
     * @param filas       los valores de cada fila, en el mismo orden
     * @return las líneas, empezando por la de encabezados, sin espacios al final
     */
    static List<String> tabla(List<String> encabezados, List<List<String>> filas) {
        int[] anchos = new int[encabezados.size()];
        for (int columna = 0; columna < anchos.length; columna++) {
            anchos[columna] = encabezados.get(columna).length();
            for (List<String> fila : filas) anchos[columna] = Math.max(anchos[columna], fila.get(columna).length());
        }
        List<String> lineas = new ArrayList<>();
        for (List<String> fila : concatenar(encabezados, filas)) {
            StringBuilder linea = new StringBuilder();
            for (int columna = 0; columna < anchos.length; columna++) {
                if (columna > 0) linea.append("  ");
                String valor = fila.get(columna);
                String relleno = " ".repeat(anchos[columna] - valor.length());
                boolean derecha = encabezados.get(columna).equals("ms");
                linea.append(derecha ? relleno + valor : valor + relleno);
            }
            lineas.add(linea.toString().stripTrailing());
        }
        return lineas;
    }

    private static List<List<String>> concatenar(List<String> encabezados, List<List<String>> filas) {
        List<List<String>> todas = new ArrayList<>();
        todas.add(encabezados);
        todas.addAll(filas);
        return todas;
    }

    /** Resultado de una parte y el tiempo que tardó. */
    private record Medicion(String resultado, double milisegundos) {}

    /** Ejecuta una parte midiendo su tiempo. */
    private static Medicion medir(Supplier<String> parte) {
        long inicio = System.nanoTime();
        String resultado = parte.get();
        return new Medicion(resultado, (System.nanoTime() - inicio) / 1_000_000.0);
    }
}
