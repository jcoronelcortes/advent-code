package advent.code;

import java.util.Arrays;
import java.util.Collection;

/**
 * Compresión de coordenadas: convierte un conjunto de coordenadas enormes
 * (ej. hasta 100 000) en índices pequeños consecutivos, para armar una
 * {@link Grilla} chica que conserve la geometría (ej. 2025 día 9).
 *
 * <p>Cada coordenada distinta recibe un índice, y entre dos coordenadas se
 * deja un índice de <b>hueco</b> que representa todas las posiciones reales
 * entre ellas. El hueco se crea solo si de verdad hay posiciones entre medio
 * (diferencia mayor que 1): si se creara entre coordenadas consecutivas,
 * aparecería una celda que no corresponde a ninguna posición real y podría
 * cambiar el resultado (por ejemplo, simular un pasillo de ancho cero).</p>
 *
 * <p>El índice 0 y el último son un borde vacío, por fuera de todas las
 * coordenadas: útil para rellenar el exterior de una figura desde una esquina.</p>
 *
 * <pre>
 * coordenadas:     2        7  8        11
 * índices:      0  1  2     3  4  5     6   7
 *               ^     ^              ^      ^
 *             borde  hueco (3..6)  hueco   borde
 * </pre>
 */
public final class CompresionCoordenadas {

    /** Coordenadas distintas, ordenadas. */
    private final int[] valores;

    /** Índice comprimido de cada coordenada de {@link #valores}. */
    private final int[] indices;

    /**
     * @param coordenadas coordenadas a comprimir (pueden repetirse y venir en cualquier orden)
     */
    public CompresionCoordenadas(Collection<Integer> coordenadas) {
        valores = coordenadas.stream().mapToInt(Integer::intValue).distinct().sorted().toArray();
        indices = new int[valores.length];
        int siguiente = 1;                       // 0 es el borde
        for (int i = 0; i < valores.length; i++) {
            if (i > 0 && valores[i] - valores[i - 1] > 1) siguiente++;   // hueco con posiciones reales
            indices[i] = siguiente++;
        }
    }

    /**
     * Índice comprimido de una coordenada.
     *
     * @param coordenada una de las coordenadas con que se creó la compresión
     * @return su índice
     * @throws IllegalArgumentException si no es una de esas coordenadas
     */
    public int indice(int coordenada) {
        int posicion = Arrays.binarySearch(valores, coordenada);
        if (posicion < 0) throw new IllegalArgumentException("Coordenada no comprimida: " + coordenada);
        return indices[posicion];
    }

    /**
     * Cantidad total de índices, incluidos los dos bordes. Es el tamaño que
     * debe tener la grilla comprimida en este eje.
     *
     * @return cantidad de índices
     */
    public int tamanio() {
        return valores.length == 0 ? 2 : indices[indices.length - 1] + 2;
    }

    /** @return cantidad de coordenadas distintas */
    public int cantidadCoordenadas() {
        return valores.length;
    }
}
