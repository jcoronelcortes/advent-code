package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Busqueda")
class PruebaBusqueda {

    @Test
    @DisplayName("primero: el menor valor que cumple la condición")
    void primero() {
        assertEquals(OptionalLong.of(37), Busqueda.primero(0, 100, x -> x >= 37));
        assertEquals(OptionalLong.of(0), Busqueda.primero(0, 100, x -> true));
        assertEquals(OptionalLong.of(100), Busqueda.primero(0, 100, x -> x == 100));
        assertEquals(OptionalLong.of(-5), Busqueda.primero(-10, 10, x -> x >= -5));
    }

    @Test
    @DisplayName("primero: vacío si ningún valor cumple o si el intervalo está vacío")
    void vacio() {
        assertEquals(OptionalLong.empty(), Busqueda.primero(0, 100, x -> false));
        assertEquals(OptionalLong.empty(), Busqueda.primero(5, 4, x -> true));
        assertEquals(OptionalLong.of(7), Busqueda.primero(7, 7, x -> true));
    }

    @Test
    @DisplayName("primero: con intervalos enormes no desborda y evalúa pocas veces")
    void intervalosEnormes() {
        AtomicInteger evaluaciones = new AtomicInteger();
        long umbral = 4_000_000_000_000_000_000L;
        OptionalLong resultado = Busqueda.primero(Long.MIN_VALUE, Long.MAX_VALUE, x -> {
            evaluaciones.incrementAndGet();
            return x >= umbral;
        });
        assertEquals(OptionalLong.of(umbral), resultado);
        assertTrue(evaluaciones.get() <= 66, "evaluó " + evaluaciones.get() + " veces");
    }

    @Property
    @Label("primero: coincide con recorrer el intervalo de a uno")
    void coincideConRecorrer(@ForAll @IntRange(min = -50, max = 50) int desde,
                             @ForAll @IntRange(min = 0, max = 100) int largo,
                             @ForAll @IntRange(min = -60, max = 160) int umbral) {
        long hasta = desde + largo - 1;
        OptionalLong esperado = OptionalLong.empty();
        for (long x = desde; x <= hasta; x++) {
            if (x >= umbral) {
                esperado = OptionalLong.of(x);
                break;
            }
        }
        assertEquals(esperado, Busqueda.primero(desde, hasta, x -> x >= umbral));
    }
}
