package advent.code;

import java.util.List;

/**
 * Punto de entrada principal. Recibe el año y día como argumentos,
 * carga la clase correspondiente y ejecuta ambas partes del desafío.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: Main <año> <día>  (ej. 2025 1)");
            return;
        }

        int año = Integer.parseInt(args[0]);
        int día = Integer.parseInt(args[1]);

        String nombreClase = String.format("advent.code.y%d.Day%02d", año, día);
        Solver solver = (Solver) Class.forName(nombreClase)
                                      .getDeclaredConstructor()
                                      .newInstance();

        List<String> líneas = IO.readInput(año, día);

        System.out.println("=== Año " + año + " — Día " + día + " ===");
        System.out.println("Parte 1: " + solver.part1(líneas));
        System.out.println("Parte 2: " + solver.part2(líneas));
    }
}