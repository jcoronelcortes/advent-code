package advent.code;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Búsqueda en anchura (BFS) genérica para problemas de grafos y grillas.
 *
 * <p>Funciona con cualquier tipo de nodo que implemente correctamente
 * {@code equals} y {@code hashCode} (como {@link Punto}).</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Optional<Integer> distancia = BFS.buscar(
 *     inicio,
 *     nodo -> nodo.equals(meta),
 *     nodo -> nodo.vecinos()
 * );
 * }</pre>
 */
public class BFS {

    private BFS() {}

    /**
     * Encuentra la distancia mínima (en pasos) desde el nodo inicial
     * hasta el primer nodo que cumpla la condición de meta.
     *
     * @param inicio   nodo de partida
     * @param esMeta   condición que identifica el nodo destino
     * @param vecinos  función que retorna los nodos adyacentes
     * @param <N>      tipo del nodo (debe implementar equals/hashCode)
     * @return distancia mínima, o {@code Optional.empty()} si no hay camino
     */
    public static <N> Optional<Integer> buscar(
            N inicio,
            Predicate<N> esMeta,
            Function<N, Iterable<N>> vecinos) {

        if (esMeta.test(inicio)) return Optional.of(0);

        Queue<N> cola = new ArrayDeque<>();
        Map<N, Integer> distancias = new HashMap<>();
        cola.add(inicio);
        distancias.put(inicio, 0);

        while (!cola.isEmpty()) {
            N actual = cola.poll();
            int distanciaActual = distancias.get(actual);

            for (N vecino : vecinos.apply(actual)) {
                if (!distancias.containsKey(vecino)) {
                    int nuevaDistancia = distanciaActual + 1;
                    distancias.put(vecino, nuevaDistancia);
                    if (esMeta.test(vecino)) return Optional.of(nuevaDistancia);
                    cola.add(vecino);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Explora todos los nodos alcanzables desde el inicio y retorna
     * la distancia mínima a cada uno.
     *
     * @param inicio  nodo de partida
     * @param vecinos función que retorna los nodos adyacentes
     * @param <N>     tipo del nodo (debe implementar equals/hashCode)
     * @return mapa de nodo → distancia mínima desde el inicio
     */
    public static <N> Map<N, Integer> explorar(
            N inicio,
            Function<N, Iterable<N>> vecinos) {

        Queue<N> cola = new ArrayDeque<>();
        Map<N, Integer> distancias = new HashMap<>();
        cola.add(inicio);
        distancias.put(inicio, 0);

        while (!cola.isEmpty()) {
            N actual = cola.poll();
            int distanciaActual = distancias.get(actual);

            for (N vecino : vecinos.apply(actual)) {
                if (!distancias.containsKey(vecino)) {
                    distancias.put(vecino, distanciaActual + 1);
                    cola.add(vecino);
                }
            }
        }

        return distancias;
    }
}