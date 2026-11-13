package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Año 2023 — Día 25: Sobrecarga de Nieve
 *
 * <p>El input es un diagrama de conexiones: cada línea tiene un componente,
 * dos puntos y los componentes con los que está conectado por un cable. Los
 * cables no tienen dirección. Cortando exactamente tres cables, los
 * componentes quedan divididos en dos grupos separados.</p>
 */
public class Dia25 implements Resolvedor {

    /** Cuántos cables se pueden cortar. */
    static final int CABLES = 3;

    /**
     * Un corte de la red.
     *
     * @param grupo  los componentes de uno de los dos lados
     * @param otro   los del otro lado
     * @param cables los cables cortados, cada uno como sus dos componentes en orden alfabético
     */
    record Corte(Set<String> grupo, Set<String> otro, Set<List<String>> cables) {
    }

    /**
     * Parte 1: el producto de los tamaños de los dos grupos que quedan al
     * cortar los tres cables.
     *
     * @param lineas el diagrama de conexiones
     * @return el producto
     */
    @Override
    public String parte1(List<String> lineas) {
        Corte corte = corteMinimo(leer(lineas), CABLES);
        return String.valueOf((long) corte.grupo().size() * corte.otro().size());
    }

    /**
     * Parte 2: pendiente.
     *
     * @param lineas el diagrama de conexiones
     * @return la respuesta
     */
    @Override
    public String parte2(List<String> lineas) {
        return "sin implementar";
    }

    /**
     * Lee el diagrama: los vecinos de cada componente, en los dos sentidos.
     *
     * @param lineas el diagrama
     * @return los vecinos de cada componente, en el orden en que aparecen
     * @throws IllegalArgumentException si una línea no tiene el formato esperado
     *                                  o un componente se conecta consigo mismo
     */
    static Map<String, Set<String>> leer(List<String> lineas) {
        Map<String, Set<String>> vecinos = new LinkedHashMap<>();
        for (String linea : Utiles.lineasNoVacias(lineas)) {
            if (!linea.strip().matches("[a-z]+: [a-z]+( [a-z]+)*")) {
                throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
            }
            String[] partes = linea.strip().split(": ");
            for (String otro : partes[1].split(" ")) {
                if (otro.equals(partes[0])) throw new IllegalArgumentException("Cable de un componente a sí mismo: " + linea);
                vecinos.computeIfAbsent(partes[0], c -> new LinkedHashSet<>()).add(otro);
                vecinos.computeIfAbsent(otro, c -> new LinkedHashSet<>()).add(partes[0]);
            }
        }
        return vecinos;
    }

    /**
     * Busca un corte de exactamente {@code cables} cables que divida la red
     * en dos grupos.
     *
     * <p>Usa el teorema de <b>flujo máximo = corte mínimo</b>: si cada cable
     * tiene capacidad 1, el flujo máximo entre dos componentes es la menor
     * cantidad de cables que hay que cortar para separarlos. Se fija un
     * componente {@code s} y se prueba con cada otro componente {@code t}: si
     * {@code t} está del otro lado del corte buscado, el flujo da
     * exactamente {@code cables}; si está del mismo lado, da más (o el corte
     * no sería el mínimo). Por eso se buscan como mucho {@code cables + 1}
     * caminos de aumento, que basta para distinguir los dos casos.</p>
     *
     * <p>Cuando el flujo da {@code cables}, los componentes a los que todavía
     * se llega desde {@code s} por cables no saturados forman uno de los dos
     * grupos, y los cables que salen de ese grupo son los cortados. Se
     * comprueba que sean exactamente {@code cables}.</p>
     *
     * @param vecinos la red
     * @param cables  cuántos cables tiene el corte
     * @return el corte
     * @throws IllegalStateException si la red se puede separar cortando menos
     *                               cables, o si no se puede con esa cantidad
     */
    static Corte corteMinimo(Map<String, Set<String>> vecinos, int cables) {
        List<String> nombres = new ArrayList<>(vecinos.keySet());
        Map<String, Integer> indice = new HashMap<>();
        for (int i = 0; i < nombres.size(); i++) indice.put(nombres.get(i), i);
        int[][] adyacencia = new int[nombres.size()][];
        for (int i = 0; i < nombres.size(); i++) {
            adyacencia[i] = vecinos.get(nombres.get(i)).stream().mapToInt(indice::get).toArray();
        }

        for (int t = 1; t < nombres.size(); t++) {
            Map<Long, Integer> flujo = new HashMap<>();
            int caminos = 0;
            while (caminos <= cables && aumentar(adyacencia, flujo, 0, t)) caminos++;
            if (caminos < cables) {
                throw new IllegalStateException("La red se separa cortando solo " + caminos + " cables");
            }
            if (caminos > cables) continue;
            boolean[] alcanzable = alcanzables(adyacencia, flujo, 0);
            Set<String> grupo = new TreeSet<>(), otro = new TreeSet<>();
            Set<List<String>> cortados = new LinkedHashSet<>();
            for (int u = 0; u < nombres.size(); u++) {
                (alcanzable[u] ? grupo : otro).add(nombres.get(u));
                for (int v : adyacencia[u]) {
                    if (alcanzable[u] && !alcanzable[v]) {
                        String a = nombres.get(u), b = nombres.get(v);
                        cortados.add(a.compareTo(b) < 0 ? List.of(a, b) : List.of(b, a));
                    }
                }
            }
            if (cortados.size() != cables) {
                throw new IllegalStateException("El corte encontrado tiene " + cortados.size() + " cables");
            }
            return new Corte(grupo, otro, cortados);
        }
        throw new IllegalStateException("No hay un corte de " + cables + " cables que divida la red");
    }

    /**
     * Busca con BFS un camino de {@code s} a {@code t} por cables con
     * capacidad libre y, si lo encuentra, manda una unidad de flujo por él.
     *
     * <p>Cada cable puede llevar una unidad en cualquiera de los dos
     * sentidos. El flujo de {@code u} a {@code v} se guarda con signo
     * ({@code flujo(v, u) = −flujo(u, v)}); queda capacidad libre de
     * {@code u} a {@code v} mientras {@code flujo(u, v) < 1}.</p>
     *
     * @return si encontró un camino
     */
    private static boolean aumentar(int[][] adyacencia, Map<Long, Integer> flujo, int s, int t) {
        int[] anterior = new int[adyacencia.length];
        Arrays.fill(anterior, -1);
        anterior[s] = s;
        Queue<Integer> cola = new ArrayDeque<>(List.of(s));
        while (!cola.isEmpty() && anterior[t] < 0) {
            int u = cola.poll();
            for (int v : adyacencia[u]) {
                if (anterior[v] < 0 && flujo.getOrDefault(arco(u, v, adyacencia.length), 0) < 1) {
                    anterior[v] = u;
                    cola.add(v);
                }
            }
        }
        if (anterior[t] < 0) return false;
        for (int v = t; v != s; v = anterior[v]) {
            int u = anterior[v];
            flujo.merge(arco(u, v, adyacencia.length), 1, Integer::sum);
            flujo.merge(arco(v, u, adyacencia.length), -1, Integer::sum);
        }
        return true;
    }

    /** Los componentes a los que se llega desde {@code s} por cables con capacidad libre. */
    private static boolean[] alcanzables(int[][] adyacencia, Map<Long, Integer> flujo, int s) {
        boolean[] alcanzable = new boolean[adyacencia.length];
        alcanzable[s] = true;
        Queue<Integer> cola = new ArrayDeque<>(List.of(s));
        while (!cola.isEmpty()) {
            int u = cola.poll();
            for (int v : adyacencia[u]) {
                if (!alcanzable[v] && flujo.getOrDefault(arco(u, v, adyacencia.length), 0) < 1) {
                    alcanzable[v] = true;
                    cola.add(v);
                }
            }
        }
        return alcanzable;
    }

    private static long arco(int u, int v, int n) {
        return (long) u * n + v;
    }
}
