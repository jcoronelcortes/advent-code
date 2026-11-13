package advent.code.y2023;

import advent.code.Ciclo;
import advent.code.Matematicas;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Año 2023 — Día 8: Páramo Embrujado
 *
 * <p>El input tiene una línea de instrucciones {@code L}/{@code R} y, después
 * de una línea en blanco, una red de nodos: {@code AAA = (BBB, CCC)} dice que
 * desde {@code AAA} se va a {@code BBB} por la izquierda y a {@code CCC} por
 * la derecha. Las instrucciones se repiten cuando se acaban. En la parte 2,
 * un fantasma parte de cada nodo terminado en {@code A} y todos avanzan a la
 * vez hasta estar todos en nodos terminados en {@code Z}.</p>
 */
public class Dia08 implements Resolvedor {

    static final String INICIO = "AAA";
    static final String FIN = "ZZZ";

    /**
     * Parte 1: cuántos pasos hacen falta para ir de {@code AAA} a
     * {@code ZZZ} siguiendo las instrucciones.
     *
     * @param lineas las instrucciones y la red
     * @return la cantidad de pasos
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(Red.parsear(lineas).pasos(INICIO, FIN::equals));
    }

    /**
     * Parte 2: cuántos pasos hacen falta para que los fantasmas que parten
     * de cada nodo terminado en {@code A} estén todos, a la vez, en nodos
     * terminados en {@code Z}.
     *
     * <p>Cada fantasma llega a nodos {@code Z} cada {@code g} pasos exactos
     * (se comprueba en {@link Red#periodoDeLlegada}), así que todos coinciden
     * por primera vez en el mínimo común múltiplo de sus períodos.</p>
     *
     * @param lineas las instrucciones y la red
     * @return la cantidad de pasos
     */
    @Override
    public String parte2(List<String> lineas) {
        Red red = Red.parsear(lineas);
        List<String> inicios = red.nodos().keySet().stream().filter(nodo -> nodo.endsWith("A")).sorted().toList();
        if (inicios.isEmpty()) throw new IllegalArgumentException("No hay nodos que terminen en A");
        List<Long> periodos = inicios.stream().map(inicio -> red.periodoDeLlegada(inicio, Dia08::esFinFantasma)).toList();
        return String.valueOf(Matematicas.mcm(periodos));
    }

    /** Un fantasma llega a destino en los nodos que terminan en {@code Z}. */
    static boolean esFinFantasma(String nodo) {
        return nodo.endsWith("Z");
    }

    /**
     * El estado de un recorrido: el nodo actual y qué instrucción toca.
     *
     * @param nodo        el nodo
     * @param instruccion la posición en las instrucciones
     */
    record Posicion(String nodo, int instruccion) {}

    /**
     * Los dos nodos a los que se llega desde un nodo.
     *
     * @param izquierda el nodo de la izquierda ({@code L})
     * @param derecha   el nodo de la derecha ({@code R})
     */
    record Salidas(String izquierda, String derecha) {}

    /**
     * Las instrucciones y la red.
     *
     * @param instrucciones la secuencia de {@code L} y {@code R}
     * @param nodos         cada nodo con sus dos salidas
     */
    record Red(String instrucciones, Map<String, Salidas> nodos) {

        /**
         * Lee el input: las instrucciones, una línea en blanco y los nodos
         * como {@code AAA = (BBB, CCC)}.
         *
         * @param lineas el input
         * @return la red
         * @throws IllegalArgumentException si el formato no es el esperado
         */
        static Red parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas, 2);
            String instrucciones = String.join("", secciones.get(0));
            if (!instrucciones.matches("[LR]+")) {
                throw new IllegalArgumentException("Instrucciones inválidas: '" + instrucciones + "'");
            }
            Map<String, Salidas> nodos = new HashMap<>();
            for (String linea : secciones.get(1)) {
                String[] partes = linea.split("[\\s=(),]+");
                if (partes.length != 3) throw new IllegalArgumentException("Nodo inválido: '" + linea + "'");
                nodos.put(partes[0], new Salidas(partes[1], partes[2]));
            }
            return new Red(instrucciones, Map.copyOf(nodos));
        }

        /** El nodo al que se llega desde {@code nodo} siguiendo la instrucción número {@code paso}. */
        String siguiente(String nodo, long paso) {
            Salidas salidas = nodos.get(nodo);
            if (salidas == null) throw new IllegalArgumentException("El nodo " + nodo + " no está en la red");
            char instruccion = instrucciones.charAt((int) (paso % instrucciones.length()));
            return instruccion == 'L' ? salidas.izquierda() : salidas.derecha();
        }

        /**
         * Cuántos pasos hacen falta, desde {@code inicio}, para llegar a un
         * nodo que cumpla {@code esFin} (al menos un paso: si el inicio ya
         * cumple, se busca la próxima vez que se llega a uno).
         *
         * <p>El recorrido es determinista: depende solo del nodo actual y de
         * la posición en las instrucciones. Si ese par se repite sin haber
         * llegado, el recorrido es un ciclo que nunca llega, y en vez de
         * seguir para siempre se lanza una excepción. Hay a lo sumo
         * {@code nodos × instrucciones} pares distintos.</p>
         *
         * @param inicio el nodo de partida
         * @param esFin  la condición de llegada
         * @return la cantidad de pasos
         * @throws IllegalStateException si nunca se llega
         */
        long pasos(String inicio, Predicate<String> esFin) {
            long limite = (long) nodos.size() * instrucciones.length();
            String nodo = inicio;
            for (long paso = 0; paso <= limite; paso++) {
                nodo = siguiente(nodo, paso);
                if (esFin.test(nodo)) return paso + 1;
            }
            throw new IllegalStateException("Desde " + inicio + " nunca se llega al destino");
        }

        /**
         * El período con el que se llega a destino desde {@code inicio}:
         * el {@code g} tal que se llega en los pasos {@code g, 2g, 3g, …} y en
         * ningún otro.
         *
         * <p>Que exista no está garantizado, así que se <b>comprueba</b>. El
         * estado del recorrido es el par (nodo, posición en las
         * instrucciones), y {@link Ciclo#detectar} avanza hasta que un estado
         * se repite: a partir de ahí todo se repite con el mismo largo de
         * ciclo {@code λ}. Las llegadas hasta ese momento son entonces
         * <em>todas</em> las llegadas, repetidas cada {@code λ} pasos. Si son
         * exactamente los múltiplos de la primera, {@code g}, y {@code g}
         * divide a {@code λ}, las llegadas son los múltiplos de {@code g}
         * para siempre.</p>
         *
         * @param inicio el nodo de partida
         * @param esFin  la condición de llegada
         * @return el período {@code g}
         * @throws IllegalStateException si nunca se llega, o si las llegadas no
         *                               son los múltiplos de la primera
         */
        long periodoDeLlegada(String inicio, Predicate<String> esFin) {
            Ciclo<Posicion> recorrido = Ciclo.detectar(new Posicion(inicio, 0), posicion -> new Posicion(
                    siguiente(posicion.nodo(), posicion.instruccion()),
                    (posicion.instruccion() + 1) % instrucciones.length()));
            // los pasos 1 … inicio + λ: el último es el primero que repite un estado
            long paso = recorrido.estados().size();
            long ciclo = recorrido.largo();
            List<Long> llegadas = new ArrayList<>();
            for (long i = 1; i <= paso; i++) {
                if (esFin.test(recorrido.estadoEn(i).nodo())) llegadas.add(i);
            }
            if (llegadas.isEmpty()) throw new IllegalStateException("Desde " + inicio + " nunca se llega al destino");
            long periodo = llegadas.getFirst();
            boolean multiplos = ciclo % periodo == 0;
            for (int i = 0; i < llegadas.size() && multiplos; i++) multiplos = llegadas.get(i) == periodo * (i + 1);
            multiplos &= llegadas.size() == paso / periodo;   // ningún múltiplo de g se saltea antes de repetir
            if (!multiplos) {
                throw new IllegalStateException("Desde " + inicio + " las llegadas no son periódicas: "
                        + "el mínimo común múltiplo no sirve");
            }
            return periodo;
        }
    }
}
