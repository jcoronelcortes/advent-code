package advent.code;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Algoritmo de Dijkstra genérico: camino de costo mínimo en grafos con
 * aristas de costo no negativo. Opcionalmente funciona como A* si se le
 * da una heurística.
 *
 * <p>Igual que {@link BFS}, el grafo es <em>implícito</em>: no hace falta
 * construirlo, solo dar una función que, para cada nodo, devuelva sus
 * vecinos con el costo de llegar a ellos. El nodo puede ser cualquier tipo
 * con {@code equals}/{@code hashCode} correctos: un {@link Punto}, o un
 * {@code record} con más información (ej. posición + dirección + pasos).</p>
 *
 * <p>Ejemplo — laberinto donde avanzar cuesta 1 y girar cuesta 1000:</p>
 * <pre>{@code
 * record Estado(Punto posicion, Direccion direccion) {}
 *
 * Optional<Long> costo = Dijkstra.costoMinimo(
 *     new Estado(inicio, Direccion.DERECHA),
 *     estado -> estado.posicion().equals(meta),
 *     estado -> List.of(
 *         new Dijkstra.Arista<>(new Estado(estado.posicion().mover(estado.direccion()), estado.direccion()), 1),
 *         new Dijkstra.Arista<>(new Estado(estado.posicion(), estado.direccion().giroDerecha()), 1000),
 *         new Dijkstra.Arista<>(new Estado(estado.posicion(), estado.direccion().giroIzquierda()), 1000)));
 * }</pre>
 */
public final class Dijkstra {

    private Dijkstra() {}

    /**
     * Arista hacia un vecino con su costo.
     *
     * @param destino nodo vecino
     * @param costo   costo de moverse hasta él (no negativo)
     * @param <N>     tipo del nodo
     */
    public record Arista<N>(N destino, long costo) {}

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Costo mínimo desde el inicio hasta el primer nodo que cumpla la meta.
     *
     * @param inicio  nodo de partida
     * @param esMeta  condición que identifica el destino
     * @param vecinos función que devuelve los vecinos de un nodo con su costo
     * @param <N>     tipo del nodo
     * @return costo mínimo, o vacío si la meta es inalcanzable
     */
    public static <N> Optional<Long> costoMinimo(
            N inicio, Predicate<N> esMeta, Function<N, ? extends Iterable<Arista<N>>> vecinos) {
        return buscarCamino(List.of(inicio), esMeta, vecinos, nodo -> 0).map(Camino::costo);
    }

    /**
     * Camino de costo mínimo (con sus nodos) desde el inicio hasta el primer
     * nodo que cumpla la meta.
     *
     * @param inicio  nodo de partida
     * @param esMeta  condición que identifica el destino
     * @param vecinos función que devuelve los vecinos de un nodo con su costo
     * @param <N>     tipo del nodo
     * @return el camino, o vacío si la meta es inalcanzable
     */
    public static <N> Optional<Camino<N>> buscarCamino(
            N inicio, Predicate<N> esMeta, Function<N, ? extends Iterable<Arista<N>>> vecinos) {
        return buscarCamino(List.of(inicio), esMeta, vecinos, nodo -> 0);
    }

    /**
     * Versión general: varios nodos de inicio (todos con costo 0) y heurística
     * opcional (A*).
     *
     * <p>La heurística debe ser <em>admisible</em> (nunca sobreestimar el costo
     * real hasta la meta) y <em>consistente</em>; por ejemplo, la distancia
     * Manhattan cuando cada paso cuesta al menos 1. Con {@code nodo -> 0} es
     * Dijkstra común.</p>
     *
     * @param inicios    nodos de partida
     * @param esMeta     condición que identifica el destino
     * @param vecinos    función que devuelve los vecinos de un nodo con su costo
     * @param heuristica estimación del costo restante hasta la meta
     * @param <N>        tipo del nodo
     * @return el camino, o vacío si la meta es inalcanzable
     */
    public static <N> Optional<Camino<N>> buscarCamino(
            Collection<N> inicios,
            Predicate<N> esMeta,
            Function<N, ? extends Iterable<Arista<N>>> vecinos,
            ToLongFunction<N> heuristica) {

        Map<N, Long> costos = new HashMap<>();
        Map<N, N> anterior = new HashMap<>();
        PriorityQueue<Entrada<N>> cola = new PriorityQueue<>();

        for (N inicio : inicios) {
            costos.put(inicio, 0L);
            cola.add(new Entrada<>(inicio, 0, heuristica.applyAsLong(inicio)));
        }

        while (!cola.isEmpty()) {
            Entrada<N> actual = cola.poll();
            if (actual.costo() > costos.get(actual.nodo())) continue;   // entrada obsoleta

            if (esMeta.test(actual.nodo())) {
                return Optional.of(new Camino<>(reconstruir(actual.nodo(), anterior), actual.costo()));
            }

            for (Arista<N> arista : vecinos.apply(actual.nodo())) {
                long nuevoCosto = actual.costo() + validarCosto(arista);
                Long costoConocido = costos.get(arista.destino());
                if (costoConocido == null || nuevoCosto < costoConocido) {
                    costos.put(arista.destino(), nuevoCosto);
                    anterior.put(arista.destino(), actual.nodo());
                    cola.add(new Entrada<>(arista.destino(), nuevoCosto,
                            nuevoCosto + heuristica.applyAsLong(arista.destino())));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Explora todo lo alcanzable desde el inicio y devuelve el costo mínimo a cada nodo.
     *
     * @param inicio  nodo de partida
     * @param vecinos función que devuelve los vecinos de un nodo con su costo
     * @param <N>     tipo del nodo
     * @return mapa nodo → costo mínimo desde el inicio
     */
    public static <N> Map<N, Long> explorar(N inicio, Function<N, ? extends Iterable<Arista<N>>> vecinos) {
        Map<N, Long> costos = new HashMap<>();
        PriorityQueue<Entrada<N>> cola = new PriorityQueue<>();
        costos.put(inicio, 0L);
        cola.add(new Entrada<>(inicio, 0, 0));

        while (!cola.isEmpty()) {
            Entrada<N> actual = cola.poll();
            if (actual.costo() > costos.get(actual.nodo())) continue;

            for (Arista<N> arista : vecinos.apply(actual.nodo())) {
                long nuevoCosto = actual.costo() + validarCosto(arista);
                Long costoConocido = costos.get(arista.destino());
                if (costoConocido == null || nuevoCosto < costoConocido) {
                    costos.put(arista.destino(), nuevoCosto);
                    cola.add(new Entrada<>(arista.destino(), nuevoCosto, nuevoCosto));
                }
            }
        }

        return costos;
    }

    /**
     * Todos los nodos que pertenecen a <em>algún</em> camino de costo mínimo
     * entre el inicio y la meta (puede haber varios caminos empatados).
     *
     * <p>Típico en la parte 2 de problemas de laberintos: "¿cuántas casillas
     * forman parte de al menos un camino óptimo?" (ej. AoC 2024 día 16).</p>
     *
     * @param inicio  nodo de partida
     * @param esMeta  condición que identifica el destino
     * @param vecinos función que devuelve los vecinos de un nodo con su costo
     * @param <N>     tipo del nodo
     * @return nodos en caminos mínimos (incluye inicio y metas), o vacío si la meta es inalcanzable
     */
    public static <N> Set<N> nodosEnCaminosMinimos(
            N inicio, Predicate<N> esMeta, Function<N, ? extends Iterable<Arista<N>>> vecinos) {

        Map<N, Long> costos = new HashMap<>();
        Map<N, List<N>> anteriores = new HashMap<>();
        PriorityQueue<Entrada<N>> cola = new PriorityQueue<>();
        List<N> metas = new ArrayList<>();
        long costoMeta = Long.MAX_VALUE;

        costos.put(inicio, 0L);
        cola.add(new Entrada<>(inicio, 0, 0));

        while (!cola.isEmpty()) {
            Entrada<N> actual = cola.poll();
            if (actual.costo() > costos.get(actual.nodo())) continue;
            if (actual.costo() > costoMeta) break;   // ya no hay caminos empatados

            if (esMeta.test(actual.nodo())) {
                costoMeta = actual.costo();
                metas.add(actual.nodo());
                continue;
            }

            for (Arista<N> arista : vecinos.apply(actual.nodo())) {
                long nuevoCosto = actual.costo() + validarCosto(arista);
                Long costoConocido = costos.get(arista.destino());
                if (costoConocido == null || nuevoCosto < costoConocido) {
                    costos.put(arista.destino(), nuevoCosto);
                    anteriores.put(arista.destino(), new ArrayList<>(List.of(actual.nodo())));
                    cola.add(new Entrada<>(arista.destino(), nuevoCosto, nuevoCosto));
                } else if (nuevoCosto == costoConocido) {
                    anteriores.computeIfAbsent(arista.destino(), nodo -> new ArrayList<>())
                            .add(actual.nodo());   // camino empatado
                }
            }
        }

        // recorrer hacia atrás desde las metas por todos los anteriores empatados
        Set<N> enCamino = new HashSet<>(metas);
        Deque<N> pendientes = new ArrayDeque<>(metas);
        while (!pendientes.isEmpty()) {
            for (N previo : anteriores.getOrDefault(pendientes.poll(), List.of())) {
                if (enCamino.add(previo)) pendientes.add(previo);
            }
        }
        return enCamino;
    }

    /**
     * Atajo para grillas: vecinos en 4 direcciones que cumplen la condición,
     * todos con costo 1.
     *
     * @param grilla        grilla a recorrer
     * @param esTransitable condición sobre el carácter (ej. {@code c -> c != '#'})
     * @return función de vecinos lista para usar en los métodos de esta clase
     */
    public static Function<Punto, List<Arista<Punto>>> vecinosEnGrilla(
            Grilla grilla, Predicate<Character> esTransitable) {
        return punto -> grilla.vecinos(punto, esTransitable).stream()
                .map(vecino -> new Arista<>(vecino, 1))
                .toList();
    }

    // -------------------------------------------------------------------------
    // Auxiliares internos
    // -------------------------------------------------------------------------

    /** Elemento de la cola de prioridad; se ordena por {@code prioridad} (costo + heurística). */
    private record Entrada<N>(N nodo, long costo, long prioridad) implements Comparable<Entrada<N>> {
        @Override
        public int compareTo(Entrada<N> otra) {
            return Long.compare(prioridad, otra.prioridad);
        }
    }

    private static long validarCosto(Arista<?> arista) {
        if (arista.costo() < 0) {
            throw new IllegalArgumentException("Dijkstra no admite costos negativos: " + arista);
        }
        return arista.costo();
    }

    private static <N> List<N> reconstruir(N meta, Map<N, N> anterior) {
        List<N> camino = new ArrayList<>();
        for (N nodo = meta; nodo != null; nodo = anterior.get(nodo)) {
            camino.add(nodo);
        }
        Collections.reverse(camino);
        return camino;
    }
}
