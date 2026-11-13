package advent.code.y2024;

import advent.code.Camino;
import advent.code.Dijkstra;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Año 2024 — Día 16: Laberinto de Renos
 *
 * <p>El input es un laberinto con paredes {@code #}, una salida {@code S} y
 * una llegada {@code E}. El reno sale mirando al este; avanzar una casilla
 * cuesta 1 punto y girar 90° en el lugar cuesta 1000. Nunca puede entrar en
 * una pared. En la parte 2 interesan todas las casillas que están en
 * <em>algún</em> camino de puntaje mínimo.</p>
 */
public class Dia16 implements Resolvedor {

    static final char SALIDA = 'S';
    static final char LLEGADA = 'E';
    static final char PARED = '#';

    static final long COSTO_AVANZAR = 1;
    static final long COSTO_GIRAR = 1000;

    /** Dónde está el reno y hacia dónde mira: el nodo del grafo. */
    record Reno(Punto posicion, Direccion direccion) {}

    /**
     * Parte 1: el puntaje mínimo para ir de la salida a la llegada.
     *
     * @param lineas el laberinto
     * @return el puntaje mínimo
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(mejorCamino(Grilla.desdeLineas(lineas)).costo());
    }

    /**
     * Parte 2: cuántas casillas están en al menos uno de los caminos de
     * puntaje mínimo, contando la salida y la llegada.
     *
     * @param lineas el laberinto
     * @return la cantidad de casillas
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(casillasEnMejoresCaminos(Grilla.desdeLineas(lineas)).size());
    }

    /**
     * Un camino de puntaje mínimo desde la salida (mirando al este) hasta la
     * llegada, mirando hacia cualquier lado.
     *
     * @param laberinto el laberinto
     * @return el camino, con su puntaje
     * @throws IllegalArgumentException si la llegada es inalcanzable
     */
    static Camino<Reno> mejorCamino(Grilla laberinto) {
        Punto llegada = laberinto.buscarUnico(LLEGADA);
        return Dijkstra.buscarCamino(
                        new Reno(laberinto.buscarUnico(SALIDA), Direccion.DERECHA),
                        reno -> reno.posicion().equals(llegada),
                        reno -> movimientos(laberinto, reno))
                .orElseThrow(() -> new IllegalArgumentException("La llegada es inalcanzable"));
    }

    /**
     * Las casillas que pertenecen a algún camino de puntaje mínimo.
     *
     * <p>{@link Dijkstra#nodosEnCaminosMinimos} devuelve los <em>estados</em>
     * (posición y dirección) de todos los caminos empatados; una casilla
     * puede aparecer con varias direcciones (por ejemplo, donde el reno
     * gira), así que se quedan solo las posiciones distintas.</p>
     *
     * @param laberinto el laberinto
     * @return las casillas, incluidas la salida y la llegada
     * @throws IllegalArgumentException si la llegada es inalcanzable
     */
    static Set<Punto> casillasEnMejoresCaminos(Grilla laberinto) {
        Punto llegada = laberinto.buscarUnico(LLEGADA);
        Set<Reno> renos = Dijkstra.nodosEnCaminosMinimos(
                new Reno(laberinto.buscarUnico(SALIDA), Direccion.DERECHA),
                reno -> reno.posicion().equals(llegada),
                reno -> movimientos(laberinto, reno));
        if (renos.isEmpty()) throw new IllegalArgumentException("La llegada es inalcanzable");
        return renos.stream().map(Reno::posicion).collect(Collectors.toSet());
    }

    /**
     * Lo que puede hacer el reno desde un estado: avanzar (si adelante no hay
     * pared) o girar 90° hacia cualquiera de los dos lados.
     *
     * @param laberinto el laberinto
     * @param reno      el estado actual
     * @return los estados siguientes con su costo
     */
    static List<Dijkstra.Arista<Reno>> movimientos(Grilla laberinto, Reno reno) {
        List<Dijkstra.Arista<Reno>> aristas = new ArrayList<>(3);
        Punto adelante = reno.posicion().mover(reno.direccion());
        if (laberinto.obtener(adelante, PARED) != PARED) {
            aristas.add(new Dijkstra.Arista<>(new Reno(adelante, reno.direccion()), COSTO_AVANZAR));
        }
        aristas.add(new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroDerecha()), COSTO_GIRAR));
        aristas.add(new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroIzquierda()), COSTO_GIRAR));
        return aristas;
    }
}
