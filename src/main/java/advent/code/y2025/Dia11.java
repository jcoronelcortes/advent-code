package advent.code.y2025;

import advent.code.Memoizador;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Año 2025 — Día 11: Reactor
 *
 * <p>El input es la lista de dispositivos y sus salidas: {@code bbb: ddd eee}
 * significa que el dispositivo {@code bbb} tiene salidas hacia {@code ddd} y
 * {@code eee}. Los datos solo fluyen hacia las salidas, nunca hacia atrás:
 * es un grafo dirigido. En el input no hay ciclos (es un DAG), así que la
 * cantidad de caminos es finita.</p>
 */
public class Dia11 implements Resolvedor {

    private static final String INICIO = "you";
    private static final String SALIDA = "out";
    private static final String SERVIDOR = "svr";
    private static final String CONVERSOR = "dac";
    private static final String TRANSFORMADA = "fft";

    /**
     * Parte 1: cantidad de caminos distintos de {@code you} a {@code out}.
     *
     * @param lineas un dispositivo y sus salidas por línea
     * @return cantidad de caminos
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(contarCaminos(leerConexiones(lineas), INICIO, SALIDA));
    }

    /**
     * Parte 2: cantidad de caminos de {@code svr} a {@code out} que pasan
     * por {@code dac} y por {@code fft} (en cualquier orden).
     *
     * @param lineas un dispositivo y sus salidas por línea
     * @return cantidad de caminos
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(contarCaminosPor(leerConexiones(lineas), SERVIDOR, SALIDA, CONVERSOR, TRANSFORMADA));
    }

    /**
     * Cantidad de caminos de {@code desde} a {@code hasta} que pasan por
     * {@code a} y por {@code b}.
     *
     * <p>Un camino así visita {@code a} y {@code b} en algún orden, y se
     * divide en tres tramos independientes. Por cada orden, la cantidad de
     * caminos es el <b>producto</b> de los caminos de cada tramo:</p>
     * <pre>
     * desde → a → b → hasta   +   desde → b → a → hasta
     * </pre>
     *
     * <p>En un grafo sin ciclos, a lo sumo uno de los dos órdenes es posible
     * (si hubiera caminos de {@code a} a {@code b} y de {@code b} a
     * {@code a}, habría un ciclo), así que el otro término da 0.</p>
     *
     * @param conexiones salidas de cada dispositivo
     * @param desde      dispositivo de partida
     * @param hasta      dispositivo de llegada
     * @param a          un dispositivo obligatorio
     * @param b          el otro dispositivo obligatorio
     * @return cantidad de caminos que pasan por ambos
     */
    static long contarCaminosPor(Map<String, List<String>> conexiones, String desde, String hasta,
                                 String a, String b) {
        long primeroA = Math.multiplyExact(Math.multiplyExact(
                contarCaminos(conexiones, desde, a),
                contarCaminos(conexiones, a, b)),
                contarCaminos(conexiones, b, hasta));
        long primeroB = Math.multiplyExact(Math.multiplyExact(
                contarCaminos(conexiones, desde, b),
                contarCaminos(conexiones, b, a)),
                contarCaminos(conexiones, a, hasta));
        return Math.addExact(primeroA, primeroB);
    }

    /**
     * Cantidad de caminos distintos de {@code desde} a {@code hasta}.
     *
     * <p>Recursión con memoización: los caminos desde un dispositivo son la
     * suma de los caminos desde cada una de sus salidas. Como en un DAG
     * muchos caminos pasan por los mismos dispositivos, el {@link Memoizador}
     * calcula cada dispositivo una sola vez: el costo es lineal en la
     * cantidad de conexiones, aunque haya una cantidad enorme de caminos.</p>
     *
     * <p>Supone que no hay ciclos alcanzables desde {@code desde}; con un
     * ciclo habría infinitos caminos y la recursión no terminaría.</p>
     *
     * @param conexiones salidas de cada dispositivo
     * @param desde      dispositivo de partida
     * @param hasta      dispositivo de llegada
     * @return cantidad de caminos
     */
    static long contarCaminos(Map<String, List<String>> conexiones, String desde, String hasta) {
        Memoizador<String, Long> caminos = new Memoizador<>((dispositivo, propio) -> {
            if (dispositivo.equals(hasta)) return 1L;
            long total = 0;
            for (String salida : conexiones.getOrDefault(dispositivo, List.of())) {
                total = Math.addExact(total, propio.apply(salida));
            }
            return total;
        });
        return caminos.calcular(desde);
    }

    /**
     * Convierte el input en un mapa de cada dispositivo a sus salidas.
     *
     * @param lineas líneas {@code nombre: salida1 salida2 ...}
     * @return salidas de cada dispositivo (los que no tienen línea, como
     *         {@code out}, no aparecen)
     */
    static Map<String, List<String>> leerConexiones(List<String> lineas) {
        Map<String, List<String>> conexiones = new HashMap<>();
        for (String linea : Utiles.lineasNoVacias(lineas)) {
            String[] partes = linea.split(":", 2);
            if (partes.length != 2) throw new IllegalArgumentException("Falta ':' en '" + linea + "'");
            List<String> salidas = Arrays.stream(partes[1].trim().split("\\s+"))
                    .filter(salida -> !salida.isEmpty())
                    .toList();
            conexiones.put(partes[0].trim(), salidas);
        }
        return conexiones;
    }
}
