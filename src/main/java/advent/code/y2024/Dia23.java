package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Año 2024 — Día 23: Fiesta LAN
 *
 * <p>El input es el mapa de una red: una conexión por línea, {@code a-b},
 * entre dos computadoras con nombres de dos letras. Las conexiones no tienen
 * sentido: {@code a-b} es lo mismo que {@code b-a}. La parte 1 cuenta grupos
 * de tres conectados entre sí y la parte 2 busca el grupo más grande.</p>
 */
public class Dia23 implements Resolvedor {

    /**
     * Parte 1: cuántos grupos de tres computadoras conectadas entre sí tienen
     * al menos una cuyo nombre empieza con {@code t}.
     *
     * @param lineas las conexiones
     * @return la cantidad de grupos
     */
    @Override
    public String parte1(List<String> lineas) {
        long conT = Red.parsear(lineas).triangulos().stream()
                .filter(grupo -> grupo.stream().anyMatch(nombre -> nombre.startsWith("t")))
                .count();
        return String.valueOf(conT);
    }

    /**
     * Parte 2: la contraseña de la fiesta: los nombres del grupo más grande
     * de computadoras conectadas todas entre sí, ordenados y separados por
     * comas.
     *
     * @param lineas las conexiones
     * @return la contraseña
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.join(",", Red.parsear(lineas).grupoMasGrande());
    }

    /**
     * La red: los vecinos de cada computadora, ordenados por nombre.
     *
     * @param vecinos computadora → computadoras conectadas a ella
     */
    record Red(Map<String, NavigableSet<String>> vecinos) {

        /**
         * Lee las conexiones {@code a-b}, en los dos sentidos.
         *
         * @param lineas una conexión por línea
         * @return la red
         * @throws IllegalArgumentException si una línea no tiene la forma {@code a-b}
         */
        static Red parsear(List<String> lineas) {
            Map<String, NavigableSet<String>> vecinos = new TreeMap<>();
            for (String linea : Utiles.lineasNoVacias(lineas)) {
                String[] extremos = linea.strip().split("-");
                if (extremos.length != 2 || extremos[0].equals(extremos[1])) {
                    throw new IllegalArgumentException("Conexión inválida: '" + linea + "'");
                }
                vecinos.computeIfAbsent(extremos[0], nombre -> new TreeSet<>()).add(extremos[1]);
                vecinos.computeIfAbsent(extremos[1], nombre -> new TreeSet<>()).add(extremos[0]);
            }
            return new Red(vecinos);
        }

        /**
         * Todos los grupos de tres computadoras conectadas entre sí, cada uno
         * con sus nombres ordenados.
         *
         * <p>Para cada conexión {@code a-b} con {@code a < b}, cada vecino
         * común {@code c} forma un triángulo. Exigir {@code a < b < c} hace
         * que cada triángulo se encuentre una sola vez (desde su conexión
         * entre los dos nombres menores).</p>
         *
         * @return los grupos, ordenados
         */
        List<List<String>> triangulos() {
            List<List<String>> triangulos = new ArrayList<>();
            vecinos.forEach((a, deA) -> {
                for (String b : deA.tailSet(a, false)) {
                    for (String c : deA.tailSet(b, false)) {
                        if (vecinos.get(b).contains(c)) triangulos.add(List.of(a, b, c));
                    }
                }
            });
            return Collections.unmodifiableList(triangulos);
        }

        /**
         * El grupo más grande de computadoras conectadas todas entre sí (una
         * <em>clique máxima</em> del grafo), con los nombres ordenados.
         *
         * <p>Usa el algoritmo de Bron–Kerbosch con pivote, que recorre las
         * cliques <em>maximales</em> (las que no se pueden agrandar) y se queda
         * con la más grande. Ver {@link #bronKerbosch}.</p>
         *
         * @return los nombres del grupo, ordenados
         * @throws IllegalStateException si hay dos grupos distintos con el
         *                               tamaño máximo (la contraseña sería ambigua)
         */
        List<String> grupoMasGrande() {
            List<Set<String>> mejores = new ArrayList<>();
            bronKerbosch(new TreeSet<>(), new TreeSet<>(vecinos.keySet()), new TreeSet<>(), mejores);
            if (mejores.size() != 1) {
                throw new IllegalStateException("Hay " + mejores.size() + " grupos con el tamaño máximo");
            }
            return List.copyOf(mejores.getFirst());
        }

        /**
         * Bron–Kerbosch con pivote.
         *
         * <ul>
         *   <li>{@code grupo}: las computadoras ya elegidas, todas conectadas entre sí.</li>
         *   <li>{@code candidatas}: las que están conectadas con todo el grupo y todavía se pueden agregar.</li>
         *   <li>{@code descartadas}: las que también están conectadas con todo el grupo, pero ya se
         *       exploraron; si queda alguna, el grupo se puede agrandar con ella, así que no es maximal.</li>
         * </ul>
         *
         * <p>Cuando no quedan candidatas ni descartadas, el grupo es maximal.
         * El <b>pivote</b> es la computadora con más vecinos entre las
         * candidatas: toda clique maximal incluye al pivote o a alguna
         * candidata que no es su vecina, así que basta ramificar por esas.</p>
         *
         * @param mejores los grupos más grandes encontrados hasta ahora (todos del mismo tamaño)
         */
        private void bronKerbosch(NavigableSet<String> grupo, NavigableSet<String> candidatas,
                                  NavigableSet<String> descartadas, List<Set<String>> mejores) {
            if (candidatas.isEmpty() && descartadas.isEmpty()) {
                int mejorTamanio = mejores.isEmpty() ? 0 : mejores.getFirst().size();
                if (grupo.size() > mejorTamanio) mejores.clear();
                if (grupo.size() >= mejorTamanio) mejores.add(new TreeSet<>(grupo));
                return;
            }
            String pivote = null;
            int vecinasDelPivote = -1;
            for (String opcion : unir(candidatas, descartadas)) {
                int vecinas = interseccion(candidatas, vecinos.get(opcion)).size();
                if (vecinas > vecinasDelPivote) {
                    pivote = opcion;
                    vecinasDelPivote = vecinas;
                }
            }
            NavigableSet<String> aProbar = new TreeSet<>(candidatas);
            aProbar.removeAll(vecinos.get(pivote));
            for (String computadora : aProbar) {
                NavigableSet<String> deEsta = vecinos.get(computadora);
                grupo.add(computadora);
                bronKerbosch(grupo, interseccion(candidatas, deEsta), interseccion(descartadas, deEsta), mejores);
                grupo.remove(computadora);
                candidatas.remove(computadora);
                descartadas.add(computadora);
            }
        }

        private static NavigableSet<String> interseccion(Set<String> a, Set<String> b) {
            NavigableSet<String> resultado = new TreeSet<>(a);
            resultado.retainAll(b);
            return resultado;
        }

        private static NavigableSet<String> unir(Set<String> a, Set<String> b) {
            NavigableSet<String> resultado = new TreeSet<>(a);
            resultado.addAll(b);
            return resultado;
        }
    }
}
