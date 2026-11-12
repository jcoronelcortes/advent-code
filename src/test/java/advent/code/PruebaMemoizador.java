package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Memoizador")
class PruebaMemoizador {

    @Test
    @DisplayName("Fibonacci(90) recursivo termina al instante y es correcto")
    void fibonacci() {
        Memoizador<Integer, Long> fibonacci = new Memoizador<>((n, fib) ->
                n < 2 ? (long) n : fib.apply(n - 1) + fib.apply(n - 2));
        assertEquals(2880067194370816120L, fibonacci.calcular(90));
        assertEquals(91, fibonacci.tamanio());   // claves 0..90
    }

    @Test
    @DisplayName("cada clave se calcula una sola vez")
    void calculaUnaVez() {
        AtomicInteger llamadas = new AtomicInteger();
        Memoizador<Integer, Integer> doble = new Memoizador<>((n, propio) -> {
            llamadas.incrementAndGet();
            return 2 * n;
        });
        assertEquals(10, doble.calcular(5));
        assertEquals(10, doble.calcular(5));
        assertEquals(1, llamadas.get());

        doble.limpiar();
        assertEquals(10, doble.calcular(5));
        assertEquals(2, llamadas.get());
    }

    /** Clave compuesta, como en una programación dinámica sobre una grilla. */
    record Celda(int fila, int columna) {}

    @Test
    @DisplayName("claves compuestas con record: caminos en una grilla 16x16")
    void clavesCompuestas() {
        // Caminos monótonos (derecha/abajo) hasta (f, c) = C(f + c, f)
        Memoizador<Celda, Long> caminos = new Memoizador<>((celda, propio) ->
                celda.fila() == 0 || celda.columna() == 0 ? 1L
                        : propio.apply(new Celda(celda.fila() - 1, celda.columna()))
                        + propio.apply(new Celda(celda.fila(), celda.columna() - 1)));
        assertEquals(601080390L, caminos.calcular(new Celda(16, 16)));   // C(32, 16)
    }

    @Test
    @DisplayName("guarda también resultados null")
    void valoresNull() {
        AtomicInteger llamadas = new AtomicInteger();
        Memoizador<String, String> nulo = new Memoizador<>((clave, propio) -> {
            llamadas.incrementAndGet();
            return null;
        });
        assertNull(nulo.calcular("a"));
        assertNull(nulo.calcular("a"));
        assertEquals(1, llamadas.get());
    }
}
