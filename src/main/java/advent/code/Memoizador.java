package advent.code;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Función recursiva con caché (memoización), un recurso muy común en AoC
 * para programación dinámica: contar caminos, combinaciones, etc.
 *
 * <p><b>Por qué no usar {@code map.computeIfAbsent}:</b> si la función
 * se llama a sí misma dentro de {@code computeIfAbsent}, el mapa se modifica
 * mientras se está calculando y {@link HashMap} lanza
 * {@link java.util.ConcurrentModificationException}. Esta clase consulta y
 * guarda en pasos separados, así que la recursión es segura.</p>
 *
 * <p>La función recibe la clave y una referencia a sí misma ya memoizada,
 * para hacer las llamadas recursivas:</p>
 *
 * <pre>{@code
 * Memoizador<Integer, Long> fibonacci = new Memoizador<>((n, fib) ->
 *         n < 2 ? n : fib.apply(n - 1) + fib.apply(n - 2));
 * fibonacci.calcular(90);   // 2880067194370816120, al instante
 * }</pre>
 *
 * <p>Las claves compuestas se arman con un {@code record}. La recursión usa
 * la pila de Java: con profundidades de decenas de miles puede hacer falta
 * más pila ({@code -Xss}) o reescribir de forma iterativa.</p>
 *
 * @param <K> tipo de la clave (debe implementar {@code equals}/{@code hashCode})
 * @param <V> tipo del valor
 */
public final class Memoizador<K, V> implements Function<K, V> {

    private final Map<K, V> cache = new HashMap<>();
    private final BiFunction<K, Function<K, V>, V> funcion;

    /**
     * @param funcion cálculo para una clave; su segundo argumento es esta
     *                misma función memoizada, para las llamadas recursivas
     */
    public Memoizador(BiFunction<K, Function<K, V>, V> funcion) {
        this.funcion = funcion;
    }

    /**
     * Devuelve el valor de la clave, calculándolo solo la primera vez.
     *
     * @param clave clave a calcular
     * @return el valor (puede ser {@code null} si la función lo devuelve)
     */
    public V calcular(K clave) {
        if (cache.containsKey(clave)) return cache.get(clave);
        V valor = funcion.apply(clave, this);
        cache.put(clave, valor);
        return valor;
    }

    /** Igual que {@link #calcular}; permite usarlo como {@link Function}. */
    @Override
    public V apply(K clave) {
        return calcular(clave);
    }

    /** @return cantidad de claves ya calculadas */
    public int tamanio() {
        return cache.size();
    }

    /** Vacía la caché. */
    public void limpiar() {
        cache.clear();
    }
}
