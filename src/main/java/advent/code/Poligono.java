package advent.code;

import java.util.List;

/**
 * Polígonos simples con vértices de coordenadas enteras: área con la fórmula
 * de Gauss y cantidad de puntos enteros con el teorema de Pick.
 *
 * <p>Sirve cuando un recorrido cerrado por una grilla encierra una zona y
 * hay que contar casillas sin rellenarlas (ej. 2023 día 10, un lazo de
 * tuberías; 2023 día 18, una zanja con tramos de millones de metros). Los
 * vértices son los centros de las casillas donde el recorrido cambia de
 * dirección (o todas las casillas del recorrido: los puntos intermedios de un
 * lado recto no cambian nada).</p>
 *
 * <ul>
 *   <li>{@link #dobleArea}: el doble del área, entero (la fórmula de Gauss
 *       da múltiplos de 1/2).</li>
 *   <li>{@link #puntosDelBorde}: los puntos enteros sobre los lados.</li>
 *   <li>{@link #puntosInteriores}: los puntos enteros estrictamente adentro
 *       (por ejemplo, las casillas encerradas por un lazo).</li>
 *   <li>{@link #puntosCubiertos}: los de adentro más los del borde (por
 *       ejemplo, las casillas de una zanja más su interior).</li>
 * </ul>
 *
 * <p>El polígono tiene que ser <b>simple</b> (sus lados no se cruzan) y
 * tener al menos tres vértices; puede recorrerse en cualquier sentido. Los
 * vértices se dan en orden, sin repetir el primero al final. Los cálculos son
 * exactos: fallan con {@link ArithmeticException} en vez de desbordar.</p>
 */
public final class Poligono {

    private Poligono() {}

    /**
     * El doble del área, con la fórmula de Gauss (del "cordón de zapato"):
     * el valor absoluto de la suma de {@code x[i]·y[i+1] − x[i+1]·y[i]} sobre
     * todos los lados.
     *
     * @param vertices los vértices en orden
     * @return el doble del área (no negativo)
     * @throws IllegalArgumentException si hay menos de tres vértices
     */
    public static long dobleArea(List<Punto> vertices) {
        exigirPoligono(vertices);
        long suma = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Punto actual = vertices.get(i);
            Punto siguiente = vertices.get((i + 1) % vertices.size());
            suma = Math.addExact(suma, Math.subtractExact(
                    (long) actual.x() * siguiente.y(), (long) siguiente.x() * actual.y()));
        }
        return Math.abs(suma);
    }

    /**
     * La cantidad de puntos enteros sobre el borde: un lado de {@code (dx, dy)}
     * tiene {@code mcd(|dx|, |dy|)} puntos enteros sin contar uno de sus
     * extremos. En un recorrido por casillas vecinas, es la cantidad de
     * casillas; con lados horizontales y verticales, la suma de sus largos.
     *
     * @param vertices los vértices en orden
     * @return la cantidad de puntos enteros del borde
     * @throws IllegalArgumentException si hay menos de tres vértices
     */
    public static long puntosDelBorde(List<Punto> vertices) {
        exigirPoligono(vertices);
        long borde = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Punto desde = vertices.get(i), hasta = vertices.get((i + 1) % vertices.size());
            // En long: con coordenadas cerca de los extremos de int, la diferencia no entra en un int
            borde = Math.addExact(borde, Matematicas.mcd((long) hasta.x() - desde.x(), (long) hasta.y() - desde.y()));
        }
        return borde;
    }

    /**
     * La cantidad de puntos enteros estrictamente dentro del polígono.
     *
     * <p>El <b>teorema de Pick</b> dice que
     * {@code área = interiores + borde / 2 − 1}. Despejando, y multiplicando
     * por 2 para no usar fracciones:
     * {@code interiores = (2·área − borde + 2) / 2}.</p>
     *
     * @param vertices los vértices en orden
     * @return la cantidad de puntos interiores
     * @throws IllegalArgumentException si hay menos de tres vértices
     */
    public static long puntosInteriores(List<Punto> vertices) {
        return (dobleArea(vertices) - puntosDelBorde(vertices) + 2) / 2;
    }

    /**
     * La cantidad de puntos enteros dentro del polígono o sobre su borde:
     * {@code interiores + borde = (2·área + borde) / 2 + 1}.
     *
     * @param vertices los vértices en orden
     * @return la cantidad de puntos cubiertos
     * @throws IllegalArgumentException si hay menos de tres vértices
     */
    public static long puntosCubiertos(List<Punto> vertices) {
        return Math.addExact(dobleArea(vertices), puntosDelBorde(vertices)) / 2 + 1;
    }

    private static void exigirPoligono(List<Punto> vertices) {
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Un polígono necesita al menos tres vértices: " + vertices);
        }
    }
}
