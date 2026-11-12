package advent.code;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Búsqueda en anchura (BFS) genérica para problemas de grafos y grillas
 * donde todos los pasos cuestan lo mismo. Si los pasos tienen costos
 * distintos, usar {@link Dijkstra}, que tiene la misma forma de uso.
 *
 * <p>Funciona con cualquier tipo de nodo que implemente correctamente
 * {@code equals} y {@code hashCode} (como {@link Punto} o un {@code record}).</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Optional<Long> distancia = BFS.buscar(
 *     inicio,
 *     nodo -> nodo.equals(meta),
 *     nodo -> grilla.vecinos(nodo, c -> c != '#')
 * );
 * }</pre>
 */
public final class BFS {

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
    public static <N> Optional<Long> buscar(
            N inicio,
            Predicate<N> esMeta,
            Function<N, ? extends Iterable<N>> vecinos) {
        return buscarCamino(inicio, esMeta, vecinos).map(Camino::costo);
    }

    /**
     * Encuentra el camino más corto (con sus nodos) desde el nodo inicial
     * hasta el primer nodo que cumpla la condición de meta.
     *
     * @param inicio   nodo de partida
     * @param esMeta   condición que identifica el nodo destino
     * @param vecinos  función que retorna los nodos adyacentes
     * @param <N>      tipo del nodo (debe implementar equals/hashCode)
     * @return el camino, o {@code Optional.empty()} si no hay camino
     */
    public static <N> Optional<Camino<N>> buscarCamino(
            N inicio,
            Predicate<N> esMeta,
            Function<N, ? extends Iterable<N>> vecinos) {

        if (esMeta.test(inicio)) return Optional.of(new Camino<>(List.of(inicio), 0));

        Queue<N> cola = new ArrayDeque<>();
        Map<N, N> anterior = new HashMap<>();
        cola.add(inicio);
        anterior.put(inicio, null);

        while (!cola.isEmpty()) {
            N actual = cola.poll();

            for (N vecino : vecinos.apply(actual)) {
                if (anterior.containsKey(vecino)) continue;
                anterior.put(vecino, actual);

                if (esMeta.test(vecino)) {
                    List<N> nodos = reconstruir(vecino, anterior);
                    return Optional.of(new Camino<>(nodos, nodos.size() - 1));
                }
                cola.add(vecino);
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
    public static <N> Map<N, Long> explorar(
            N inicio,
            Function<N, ? extends Iterable<N>> vecinos) {

        Queue<N> cola = new ArrayDeque<>();
        Map<N, Long> distancias = new HashMap<>();
        cola.add(inicio);
        distancias.put(inicio, 0L);

        while (!cola.isEmpty()) {
            N actual = cola.poll();
            long distanciaActual = distancias.get(actual);

            for (N vecino : vecinos.apply(actual)) {
                if (!distancias.containsKey(vecino)) {
                    distancias.put(vecino, distanciaActual + 1);
                    cola.add(vecino);
                }
            }
        }

        return distancias;
    }

    /** Recorre los anteriores desde la meta hasta el inicio (cuyo anterior es {@code null}). */
    private static <N> List<N> reconstruir(N meta, Map<N, N> anterior) {
        List<N> camino = new ArrayList<>();
        for (N nodo = meta; nodo != null; nodo = anterior.get(nodo)) {
            camino.add(nodo);
        }
        Collections.reverse(camino);
        return camino;
    }
}
