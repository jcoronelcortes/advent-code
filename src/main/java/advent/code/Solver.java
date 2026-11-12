package advent.code;

import java.util.List;

/**
 * Interfaz que debe implementar cada día del desafío.
 * Cada implementación resuelve las dos partes del problema.
 */
public interface Solver {

    /**
     * Resuelve la parte 1 del desafío.
     *
     * @param líneas líneas del archivo de input
     * @return resultado como texto
     */
    String part1(List<String> líneas);

    /**
     * Resuelve la parte 2 del desafío.
     *
     * @param líneas líneas del archivo de input
     * @return resultado como texto
     */
    String part2(List<String> líneas);
}
