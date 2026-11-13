package advent.code.y2024;

import advent.code.BFS;
import advent.code.Busqueda;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;
import java.util.Optional;

/**
 * Año 2024 — Día 18: Carrera en la RAM
 *
 * <p>El input es la lista ordenada de las posiciones {@code X,Y} donde van
 * cayendo bytes en un espacio de memoria cuadrado (de 0 a 70 en cada eje).
 * Una posición donde cayó un byte queda corrupta y no se puede pisar. Hay que
 * ir de la esquina de arriba a la izquierda a la de abajo a la derecha, con
 * pasos en las 4 direcciones. En la parte 2 hay que encontrar el primer
 * byte que deja la salida inalcanzable.</p>
 */
public class Dia18 implements Resolvedor {

    static final char LIBRE = '.';
    static final char CORRUPTO = '#';

    /** Coordenada máxima del espacio de memoria real (va de 0 a 70). */
    static final int LADO = 70;

    /** Cantidad de bytes que caen en la parte 1 (un kilobyte). */
    static final int KILOBYTE = 1024;

    /**
     * Parte 1: la menor cantidad de pasos hasta la salida después de que
     * caen los primeros 1024 bytes.
     *
     * @param lineas las posiciones de los bytes, en el orden en que caen
     * @return la cantidad mínima de pasos
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(pasosMinimos(parsear(lineas), LADO, KILOBYTE)
                .orElseThrow(() -> new IllegalArgumentException("La salida es inalcanzable")));
    }

    /**
     * Parte 2: la posición del primer byte que deja la salida inalcanzable,
     * como {@code X,Y}.
     *
     * @param lineas las posiciones de los bytes, en el orden en que caen
     * @return la posición del byte
     */
    @Override
    public String parte2(List<String> lineas) {
        Punto bloqueo = primerByteQueBloquea(parsear(lineas), LADO);
        return bloqueo.x() + "," + bloqueo.y();
    }

    /**
     * Lee las posiciones {@code X,Y} de los bytes ({@code x} es la columna e
     * {@code y} la fila, igual que en {@link Punto}).
     *
     * @param lineas el input
     * @return las posiciones, en orden
     */
    static List<Punto> parsear(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Punto::parsear).toList();
    }

    /**
     * El espacio de memoria después de que caen los primeros bytes.
     *
     * @param bytes    las posiciones de los bytes, en orden
     * @param lado     coordenada máxima (el espacio va de 0 a {@code lado})
     * @param cantidad cuántos bytes caen
     * @return la grilla con {@code #} donde la memoria está corrupta
     */
    static Grilla memoria(List<Punto> bytes, int lado, int cantidad) {
        if (cantidad > bytes.size()) {
            throw new IllegalArgumentException("Solo hay " + bytes.size() + " bytes, no " + cantidad);
        }
        Grilla memoria = new Grilla(lado + 1, lado + 1, LIBRE);
        for (Punto posicion : bytes.subList(0, cantidad)) memoria.establecer(posicion, CORRUPTO);
        return memoria;
    }

    /**
     * El primer byte que, al caer, deja la salida inalcanzable.
     *
     * <p>Cada byte solo agrega un obstáculo, así que si la salida es
     * inalcanzable después de k bytes, también lo es después de más. Eso
     * permite una <b>búsqueda binaria</b> ({@link Busqueda#primero}) sobre la
     * cantidad de bytes caídos, con una BFS en cada paso: unas 12 BFS en vez
     * de una por byte. Si la primera cantidad sin camino es {@code k}, el
     * byte que bloquea es el número {@code k} (contando desde 1).</p>
     *
     * @param bytes las posiciones de los bytes, en orden
     * @param lado  coordenada máxima
     * @return la posición del byte
     * @throws IllegalArgumentException si la salida nunca queda bloqueada, o
     *                                  si ya lo está antes de que caiga nada
     */
    static Punto primerByteQueBloquea(List<Punto> bytes, int lado) {
        long bloqueado = Busqueda.primero(0, bytes.size(), cantidad -> pasosMinimos(bytes, lado, (int) cantidad).isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Ningún byte bloquea la salida"));
        if (bloqueado == 0) {
            throw new IllegalArgumentException("La salida es inalcanzable antes de que caiga algún byte");
        }
        return bytes.get((int) bloqueado - 1);
    }

    /**
     * La menor cantidad de pasos de {@code (0,0)} a {@code (lado,lado)}
     * después de que caen los primeros bytes: una BFS por las posiciones
     * libres.
     *
     * @param bytes    las posiciones de los bytes, en orden
     * @param lado     coordenada máxima
     * @param cantidad cuántos bytes caen
     * @return los pasos, o vacío si la salida quedó bloqueada
     */
    static Optional<Long> pasosMinimos(List<Punto> bytes, int lado, int cantidad) {
        Grilla memoria = memoria(bytes, lado, cantidad);
        Punto salida = new Punto(lado, lado);
        if (memoria.obtener(new Punto(0, 0)) == CORRUPTO) return Optional.empty();
        return BFS.buscar(new Punto(0, 0), salida::equals, posicion -> memoria.vecinos(posicion, c -> c == LIBRE));
    }
}
