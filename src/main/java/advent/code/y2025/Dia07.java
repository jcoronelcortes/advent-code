package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.List;

/**
 * Año 2025 — Día 7: Laboratorios
 *
 * <p>El input es el diagrama de un colector de taquiones. Un haz entra por
 * {@code S} y siempre baja. Atraviesa el espacio vacío ({@code .}); al llegar
 * a un divisor ({@code ^}) el haz se detiene y salen dos haces nuevos,
 * desde la posición inmediata a la izquierda y a la derecha del divisor.
 * Dos haces que llegan a la misma posición se funden en uno.</p>
 */
public class Dia07 implements Resolvedor {

    private static final char INICIO = 'S';
    private static final char DIVISOR = '^';

    /**
     * Parte 1: cantidad de veces que el haz se divide, es decir, cantidad de
     * divisores a los que llega al menos un haz.
     *
     * @param lineas filas del diagrama
     * @return cantidad de divisiones
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(simular(Grilla.desdeLineas(lineas)).divisiones());
    }

    /**
     * Parte 2: cantidad de líneas temporales en un colector cuántico.
     *
     * <p>Una sola partícula toma los dos caminos en cada divisor, y cada
     * división crea una línea temporal nueva. Aquí los caminos que llegan a la
     * misma posición <b>no</b> se funden: son líneas temporales distintas. La
     * respuesta es la cantidad total de caminos distintos de {@code S} hasta
     * el borde inferior, que es la suma de los caminos que la simulación
     * acumula en cada columna de la última fila.</p>
     *
     * @param lineas filas del diagrama
     * @return cantidad de líneas temporales
     */
    @Override
    public String parte2(List<String> lineas) {
        long lineasTemporales = 0;
        for (long caminos : simular(Grilla.desdeLineas(lineas)).haces()) {
            lineasTemporales = Math.addExact(lineasTemporales, caminos);
        }
        return String.valueOf(lineasTemporales);
    }

    /**
     * Resultado de la simulación.
     *
     * @param divisiones cantidad de divisores alcanzados por algún haz
     * @param haces      por cada columna, cuántos haces (caminos) salen por
     *                   el borde inferior en esa columna
     */
    record Resultado(int divisiones, long[] haces) {}

    /**
     * Simula los haces fila por fila, de arriba hacia abajo.
     *
     * <p>Como los haces solo bajan, basta con recordar en qué columnas hay
     * haz en la fila actual. En vez de un booleano se guarda <b>cuántos
     * caminos</b> llegan a cada columna: para la parte 1 solo importa si es
     * mayor que cero (los haces que coinciden se funden) y la parte 2 usa el
     * conteo (cada camino es una línea temporal distinta).</p>
     *
     * <p>En cada fila, un haz que cae sobre un divisor cuenta una división
     * (solo la primera vez que se alcanza ese divisor, que es la única,
     * porque cada fila se procesa una vez) y pasa a las columnas vecinas;
     * en cualquier otra celda sigue en la misma columna. Los haces que se
     * salen por los costados se pierden.</p>
     *
     * @param grilla diagrama del colector
     * @return divisiones y haces que salen por abajo
     */
    static Resultado simular(Grilla grilla) {
        int columnas = grilla.columnas();
        Punto inicio = grilla.buscar(INICIO)
                .orElseThrow(() -> new IllegalArgumentException("El diagrama no tiene '" + INICIO + "'"));

        long[] haces = new long[columnas];
        haces[inicio.x()] = 1;
        int divisiones = 0;

        for (int fila = inicio.y() + 1; fila < grilla.filas(); fila++) {
            long[] siguientes = new long[columnas];
            for (int columna = 0; columna < columnas; columna++) {
                long caminos = haces[columna];
                if (caminos == 0) continue;

                if (grilla.obtener(new Punto(columna, fila)) == DIVISOR) {
                    divisiones++;
                    if (columna > 0) siguientes[columna - 1] = Math.addExact(siguientes[columna - 1], caminos);
                    if (columna < columnas - 1) siguientes[columna + 1] = Math.addExact(siguientes[columna + 1], caminos);
                } else {
                    siguientes[columna] = Math.addExact(siguientes[columna], caminos);
                }
            }
            haces = siguientes;
        }
        return new Resultado(divisiones, haces);
    }
}
