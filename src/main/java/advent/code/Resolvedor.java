package advent.code;

import java.util.List;

/**
 * Interfaz que debe implementar cada día del desafío.
 * Cada implementación resuelve las dos partes del problema.
 */
public interface Resolvedor {

    /**
     * Resuelve la parte 1 del desafío.
     *
     * @param lineas líneas del archivo de entrada
     * @return resultado como texto
     */
    String parte1(List<String> lineas);

    /**
     * Resuelve la parte 2 del desafío.
     *
     * @param lineas líneas del archivo de entrada
     * @return resultado como texto
     */
    String parte2(List<String> lineas);
}