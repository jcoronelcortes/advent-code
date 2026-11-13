package advent.code.y2024;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Año 2024 — Día 15: Problemas en el Almacén
 *
 * <p>El input tiene dos partes separadas por una línea en blanco: el mapa de
 * un almacén (paredes {@code #}, cajas {@code O}, el robot {@code @}) y la
 * lista de movimientos que intenta el robot ({@code ^ v < >}). Al moverse, el
 * robot empuja todas las cajas que tenga adelante; si alguna chocaría con una
 * pared, no se mueve nada. En la parte 2 todo es el doble de ancho salvo el
 * robot, y las cajas ocupan dos casillas ({@code []}).</p>
 */
public class Dia15 implements Resolvedor {

    static final char ROBOT = '@';
    static final char CAJA = 'O';
    static final char CAJA_IZQUIERDA = '[';
    static final char CAJA_DERECHA = ']';
    static final char PARED = '#';
    static final char LIBRE = '.';

    /**
     * Parte 1: suma de las coordenadas GPS de las cajas cuando el robot
     * termina de moverse.
     *
     * @param lineas el mapa, una línea en blanco y los movimientos
     * @return la suma de las coordenadas GPS
     */
    @Override
    public String parte1(List<String> lineas) {
        Almacen almacen = Almacen.parsear(lineas, false);
        almacen.moverTodo();
        return String.valueOf(sumaGps(almacen.mapa()));
    }

    /**
     * Parte 2: lo mismo en el almacén ensanchado, con cajas de dos casillas.
     *
     * @param lineas el mapa, una línea en blanco y los movimientos
     * @return la suma de las coordenadas GPS
     */
    @Override
    public String parte2(List<String> lineas) {
        Almacen almacen = Almacen.parsear(lineas, true);
        almacen.moverTodo();
        return String.valueOf(sumaGps(almacen.mapa()));
    }

    /**
     * El almacén: el mapa (que se va modificando) y los movimientos.
     */
    static final class Almacen {

        private final Grilla mapa;
        private final List<Direccion> movimientos;
        private Punto robot;

        Almacen(Grilla mapa, List<Direccion> movimientos) {
            this.mapa = mapa;
            this.movimientos = movimientos;
            this.robot = mapa.buscarUnico(ROBOT);
        }

        Grilla mapa() {
            return mapa;
        }

        /** Hace todos los movimientos, en orden. */
        void moverTodo() {
            movimientos.forEach(this::mover);
        }

        /**
         * Intenta mover el robot un paso, empujando las cajas que tenga
         * adelante.
         *
         * <p>Primero se juntan todas las casillas que tendrían que moverse
         * ({@link #casillasQueSeMueven}); si alguna choca con una pared, no
         * se mueve nada. Si no, se borran todas y se escriben un paso más
         * adelante. Borrar primero y escribir después evita pisar una casilla
         * que todavía no se movió.</p>
         *
         * @param direccion hacia dónde intenta moverse
         */
        void mover(Direccion direccion) {
            Set<Punto> casillas = casillasQueSeMueven(direccion);
            if (casillas.isEmpty()) return;
            Map<Punto, Character> contenido = new HashMap<>();
            for (Punto casilla : casillas) {
                contenido.put(casilla, mapa.obtener(casilla));
                mapa.establecer(casilla, LIBRE);
            }
            contenido.forEach((casilla, caracter) -> mapa.establecer(casilla.mover(direccion), caracter));
            robot = robot.mover(direccion);
        }

        /**
         * Casillas que se mueven si el robot avanza: él mismo y todas las
         * cajas que empuja, directa o indirectamente.
         *
         * <p>Una búsqueda en anchura desde el robot: cada casilla que se
         * mueve empuja lo que tiene adelante. Una caja ancha se mueve
         * entera, así que al encontrar una mitad se agrega también la otra.
         * Así, en un empuje vertical, una caja puede empujar a dos, y cada
         * una de esas a otras, como un árbol. En un empuje horizontal la otra
         * mitad ya está en el camino, y agregarla de nuevo no cambia nada.</p>
         *
         * <p>El mapa siempre está rodeado de paredes, así que la búsqueda
         * nunca se sale de la grilla.</p>
         *
         * @param direccion hacia dónde intenta moverse el robot
         * @return las casillas que se mueven, o vacío si alguna choca con una pared
         */
        Set<Punto> casillasQueSeMueven(Direccion direccion) {
            Set<Punto> casillas = new HashSet<>();
            Queue<Punto> pendientes = new ArrayDeque<>(List.of(robot));
            while (!pendientes.isEmpty()) {
                Punto casilla = pendientes.poll();
                if (!casillas.add(casilla)) continue;
                Punto adelante = casilla.mover(direccion);
                switch (mapa.obtener(adelante)) {
                    case PARED -> {
                        return Set.of();
                    }
                    case CAJA -> pendientes.add(adelante);
                    case CAJA_IZQUIERDA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.DERECHA)));
                    case CAJA_DERECHA -> pendientes.addAll(List.of(adelante, adelante.mover(Direccion.IZQUIERDA)));
                    default -> { }   // libre: ahí no hay nada que empujar
                }
            }
            return casillas;
        }

        /**
         * Lee el mapa y los movimientos. Los movimientos pueden venir en
         * varias líneas: forman una sola secuencia.
         *
         * @param lineas  el input
         * @param ancho   si el mapa se ensancha al doble (parte 2)
         * @return el almacén al inicio
         */
        static Almacen parsear(List<String> lineas, boolean ancho) {
            List<List<String>> secciones = Utiles.secciones(lineas);
            if (secciones.size() != 2) {
                throw new IllegalArgumentException("Se esperaban 2 secciones (mapa y movimientos) y hay " + secciones.size());
            }
            List<Direccion> movimientos = String.join("", secciones.get(1)).chars()
                    .mapToObj(flecha -> Direccion.desdeFlecha((char) flecha))
                    .toList();
            List<String> mapa = ancho ? ensanchar(secciones.get(0)) : secciones.get(0);
            return new Almacen(Grilla.desdeLineas(mapa), movimientos);
        }
    }

    /**
     * El mapa al doble de ancho: {@code #} → {@code ##}, {@code O} →
     * {@code []}, {@code .} → {@code ..} y {@code @} → {@code @.} (el robot
     * no cambia de tamaño).
     *
     * @param mapa las líneas del mapa original
     * @return las líneas del mapa ensanchado
     */
    static List<String> ensanchar(List<String> mapa) {
        List<String> ancho = new ArrayList<>();
        for (String linea : mapa) {
            StringBuilder nueva = new StringBuilder();
            for (char casilla : linea.toCharArray()) {
                nueva.append(switch (casilla) {
                    case PARED -> "##";
                    case CAJA -> "[]";
                    case LIBRE -> "..";
                    case ROBOT -> "@.";
                    default -> throw new IllegalArgumentException("Casilla desconocida en el mapa: '" + casilla + "'");
                });
            }
            ancho.add(nueva.toString());
        }
        return ancho;
    }

    /**
     * Suma de las coordenadas GPS de las cajas: 100 por la distancia al
     * borde de arriba más la distancia al borde izquierdo (contando las
     * paredes). En una caja ancha se mide hasta su borde más cercano, que es
     * su mitad izquierda.
     *
     * @param mapa el almacén
     * @return la suma
     */
    static long sumaGps(Grilla mapa) {
        return mapa.buscarTodos(casilla -> casilla == CAJA || casilla == CAJA_IZQUIERDA).stream()
                .mapToLong(caja -> 100L * caja.y() + caja.x())
                .sum();
    }
}
