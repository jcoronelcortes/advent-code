package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Año 2023 — Día 23: Una Larga Caminata
 *
 * <p>El input es un mapa de senderos ({@code .}), bosque ({@code #}) y
 * pendientes heladas ({@code ^ > v <}). Hay que ir de la única casilla libre
 * de la fila de arriba a la única de la fila de abajo sin pisar dos veces la
 * misma casilla, y con el camino más largo posible. Desde una pendiente, el
 * paso siguiente tiene que ser cuesta abajo, en la dirección de la flecha.</p>
 */
public class Dia23 implements Resolvedor {

    static final char BOSQUE = '#';
    static final char SENDERO = '.';

    /** El camino más largo sobre un grafo general es exponencial: con más nodos la máscara no alcanza. */
    static final int MAXIMO_NODOS = Long.SIZE;

    /**
     * Un pasillo entre dos nodos.
     *
     * @param destino el índice del nodo al que lleva
     * @param largo   cuántos pasos tiene
     */
    record Arista(int destino, int largo) {
    }

    /**
     * El mapa comprimido: los nodos son la entrada (índice 0), la salida
     * (índice 1) y los cruces; las aristas, los pasillos entre ellos.
     *
     * @param nodos    la casilla de cada nodo
     * @param aristas  para cada nodo, los pasillos que salen de él
     */
    record Grafo(List<Punto> nodos, List<List<Arista>> aristas) {
    }

    /**
     * Parte 1: el largo de la caminata más larga, respetando las pendientes.
     *
     * @param lineas el mapa
     * @return la cantidad de pasos
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(caminoMasLargo(comprimir(Grilla.desdeLineas(lineas), true)));
    }

    /**
     * Parte 2: el largo de la caminata más larga si las pendientes se pueden
     * subir como cualquier sendero.
     *
     * @param lineas el mapa
     * @return la cantidad de pasos
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(caminoMasLargo(comprimir(Grilla.desdeLineas(lineas), false)));
    }

    /**
     * Comprime el mapa en un grafo de cruces.
     *
     * <p>Casi todo el mapa son pasillos de una sola vía: en un camino que no
     * repite casillas, una vez que se entra en un pasillo solo se puede
     * seguir hasta el otro extremo. Las únicas decisiones se toman en los
     * <b>cruces</b>, las casillas con tres o más vecinos transitables. Se
     * recorre cada pasillo desde cada cruce (y desde la entrada) hasta el
     * siguiente nodo, contando los pasos.</p>
     *
     * <p>Al recorrer se respetan las pendientes, si {@code pendientes} es
     * verdadero: desde una casilla con flecha solo se puede seguir en esa
     * dirección. Un pasillo que obliga a volver por donde se vino (una
     * pendiente en contra) o que termina en un callejón sin salida no genera
     * arista.</p>
     *
     * @param mapa       el mapa
     * @param pendientes si hay que respetar las pendientes
     * @return el grafo
     * @throws IllegalArgumentException si la fila de arriba o la de abajo no
     *                                  tienen exactamente una casilla libre, o
     *                                  hay demasiados cruces
     */
    static Grafo comprimir(Grilla mapa, boolean pendientes) {
        List<Punto> nodos = new ArrayList<>();
        nodos.add(unicaLibre(mapa, 0));
        nodos.add(unicaLibre(mapa, mapa.filas() - 1));
        for (Punto p : mapa.puntos()) {
            if (mapa.obtener(p) != BOSQUE && mapa.vecinos(p, c -> c != BOSQUE).size() >= 3) nodos.add(p);
        }
        if (nodos.size() > MAXIMO_NODOS) {
            throw new IllegalArgumentException("Demasiados cruces para buscar el camino más largo: " + nodos.size());
        }
        Map<Punto, Integer> indice = new HashMap<>();
        for (int i = 0; i < nodos.size(); i++) indice.put(nodos.get(i), i);

        List<List<Arista>> aristas = new ArrayList<>();
        for (Punto nodo : nodos) {
            List<Arista> salientes = new ArrayList<>();
            for (Direccion inicial : salidas(mapa, nodo, pendientes)) {
                Punto anterior = nodo, actual = nodo.mover(inicial);
                if (!transitable(mapa, actual)) continue;
                int largo = 1;
                while (actual != null && !indice.containsKey(actual)) {
                    Punto siguiente = null;
                    for (Direccion d : salidas(mapa, actual, pendientes)) {
                        Punto vecino = actual.mover(d);
                        if (!vecino.equals(anterior) && transitable(mapa, vecino)) siguiente = vecino;
                    }
                    anterior = actual;
                    actual = siguiente;
                    largo++;
                }
                // null: callejón sin salida o pendiente en contra; el mismo nodo: un pasillo que vuelve
                if (actual != null && !actual.equals(nodo)) salientes.add(new Arista(indice.get(actual), largo));
            }
            aristas.add(salientes);
        }
        return new Grafo(List.copyOf(nodos), aristas);
    }

    /**
     * El largo del camino más largo de la entrada a la salida sin repetir
     * nodos (y por lo tanto sin repetir casillas).
     *
     * <p>Es una búsqueda en profundidad que prueba todos los caminos,
     * guardando los nodos visitados en una máscara de bits. En el peor caso
     * es exponencial, pero el grafo comprimido tiene pocos nodos.</p>
     *
     * @param grafo el mapa comprimido
     * @return la cantidad de pasos del camino más largo
     * @throws IllegalStateException si no hay ningún camino
     */
    static long caminoMasLargo(Grafo grafo) {
        long mejor = buscar(grafo, 0, 1L, 0);
        if (mejor < 0) throw new IllegalStateException("No hay ningún camino de la entrada a la salida");
        return mejor;
    }

    /** El camino más largo desde {@code nodo} hasta la salida sin pisar los nodos de {@code visitados}, o −1. */
    private static long buscar(Grafo grafo, int nodo, long visitados, long recorrido) {
        if (nodo == 1) return recorrido;
        long mejor = -1;
        for (Arista arista : grafo.aristas().get(nodo)) {
            long bit = 1L << arista.destino();
            if ((visitados & bit) == 0) {
                mejor = Math.max(mejor, buscar(grafo, arista.destino(), visitados | bit, recorrido + arista.largo()));
            }
        }
        return mejor;
    }

    /** Hacia dónde se puede salir de una casilla: solo cuesta abajo desde una pendiente, si se respetan. */
    private static List<Direccion> salidas(Grilla mapa, Punto casilla, boolean pendientes) {
        char c = mapa.obtener(casilla);
        if (pendientes && c != SENDERO) return List.of(Direccion.desdeFlecha(c));
        return List.of(Direccion.values());
    }

    private static boolean transitable(Grilla mapa, Punto p) {
        return mapa.estaDentro(p) && mapa.obtener(p) != BOSQUE;
    }

    /** La única casilla libre de una fila (la entrada o la salida). */
    private static Punto unicaLibre(Grilla mapa, int fila) {
        List<Punto> libres = new ArrayList<>();
        for (int x = 0; x < mapa.columnas(); x++) {
            if (mapa.obtener(new Punto(x, fila)) != BOSQUE) libres.add(new Punto(x, fila));
        }
        if (libres.size() != 1) {
            throw new IllegalArgumentException("La fila " + fila + " tiene que tener una sola casilla libre: " + libres);
        }
        return libres.getFirst();
    }
}
