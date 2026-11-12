package org.example;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: Main <año> <día>  (ej. 2024 1)");
            return;
        }

        int year = Integer.parseInt(args[0]);
        int day  = Integer.parseInt(args[1]);

        String className = String.format("org.example.y%d.Day%02d", year, day);
        Solver solver = (Solver) Class.forName(className)
                                      .getDeclaredConstructor()
                                      .newInstance();

        List<String> lines = IO.readInput(year, day);

        System.out.println("=== Año " + year + " — Día " + day + " ===");
        System.out.println("Parte 1: " + solver.part1(lines));
        System.out.println("Parte 2: " + solver.part2(lines));
    }
}
