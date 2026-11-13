package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ciclo")
class PruebaCiclo {

    @Test
    @DisplayName("detectar: una cola antes del ciclo (1 → 2 → 3 → 4 → 5 → 3 …)")
    void colaYCiclo() {
        Ciclo<Integer> ciclo = Ciclo.detectar(1, x -> x == 5 ? 3 : x + 1);
        assertEquals(List.of(1, 2, 3, 4, 5), ciclo.estados());
        assertEquals(2, ciclo.inicio());
        assertEquals(3, ciclo.largo());
    }

    @Test
    @DisplayName("detectar: un ciclo puro (empieza en el paso 0) y un punto fijo (largo 1)")
    void cicloPuroYPuntoFijo() {
        Ciclo<Integer> puro = Ciclo.detectar(0, x -> (x + 1) % 4);
        assertEquals(0, puro.inicio());
        assertEquals(4, puro.largo());

        Ciclo<Integer> fijo = Ciclo.detectar(10, x -> Math.max(x - 3, 1));
        assertEquals(List.of(10, 7, 4, 1), fijo.estados());
        assertEquals(3, fijo.inicio());
        assertEquals(1, fijo.largo());
    }

    @Test
    @DisplayName("estadoEn: antes del ciclo, dentro y en pasos enormes")
    void estadoEn() {
        Ciclo<Integer> ciclo = Ciclo.detectar(1, x -> x == 5 ? 3 : x + 1);   // 1 2 3 4 5 3 4 5 3 …
        assertEquals(1, ciclo.estadoEn(0));
        assertEquals(2, ciclo.estadoEn(1));
        assertEquals(3, ciclo.estadoEn(5));
        assertEquals(5, ciclo.estadoEn(7));
        assertEquals(3 + (int) ((1_000_000_000_000L - 2) % 3), ciclo.estadoEn(1_000_000_000_000L));
        assertThrows(IllegalArgumentException.class, () -> ciclo.estadoEn(-1));
    }

    @Test
    @DisplayName("detectar: sirve con records como estado")
    void conRecords() {
        record Estado(String nodo, int posicion) {}
        Ciclo<Estado> ciclo = Ciclo.detectar(new Estado("A", 0),
                e -> new Estado(e.nodo().equals("A") ? "B" : "A", (e.posicion() + 1) % 3));
        assertEquals(0, ciclo.inicio());
        assertEquals(6, ciclo.largo());   // mcm de 2 (A/B) y 3 (posiciones)
    }

    @Property
    @Label("estadoEn coincide con avanzar paso a paso")
    void coincideConSimular(@ForAll @IntRange(min = 1, max = 60) int modulo,
                            @ForAll @IntRange(min = 0, max = 59) int a,
                            @ForAll @IntRange(min = 0, max = 59) int b,
                            @ForAll @IntRange(min = 0, max = 59) int inicial,
                            @ForAll @IntRange(min = 0, max = 500) int paso) {
        UnaryOperator<Integer> siguiente = x -> (x * x * a + b) % modulo;
        int inicio = inicial % modulo;
        Ciclo<Integer> ciclo = Ciclo.detectar(inicio, siguiente);
        int esperado = inicio;
        for (int i = 0; i < paso; i++) esperado = siguiente.apply(esperado);
        assertEquals(esperado, ciclo.estadoEn(paso));
        assertTrue(ciclo.largo() >= 1);
        assertEquals(ciclo.estados().size(), ciclo.estados().stream().distinct().count(), "estados distintos");
    }
}
