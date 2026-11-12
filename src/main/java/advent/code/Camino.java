package advent.code;

import java.util.List;

/**
 * Camino encontrado por una búsqueda ({@link BFS} o {@link Dijkstra}).
 *
 * @param nodos nodos del camino, desde el inicio hasta la meta (ambos incluidos)
 * @param costo costo total (en {@link BFS}, la cantidad de pasos)
 * @param <N>   tipo del nodo
 */
public record Camino<N>(List<N> nodos, long costo) {

    public Camino {
        if (nodos.isEmpty()) throw new IllegalArgumentException("Un camino tiene al menos un nodo");
        nodos = List.copyOf(nodos);
    }

    /** @return el nodo de partida (primero del camino) */
    public N inicio() {
        return nodos.getFirst();
    }

    /** @return el nodo meta (último del camino) */
    public N meta() {
        return nodos.getLast();
    }

    /** @return cantidad de pasos (aristas) del camino */
    public int pasos() {
        return nodos.size() - 1;
    }
}
