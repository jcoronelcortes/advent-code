package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConjuntoRangos")
class PruebaConjuntoRangos {

    private static final ConjuntoRangos EJEMPLO = new ConjuntoRangos(List.of(
            new Rango(3, 5), new Rango(10, 14), new Rango(16, 20), new Rango(12, 18)));

    @Test
    @DisplayName("une los rangos solapados y los deja ordenados")
    void rangos_unidos() {
        assertEquals(List.of(new Rango(3, 5), new Rango(10, 20)), EJEMPLO.rangos());
    }

    @Test
    @DisplayName("contiene: los extremos son inclusivos y los huecos no pertenecen")
    void contiene_extremos() {
        assertFalse(EJEMPLO.contiene(2));
        assertTrue(EJEMPLO.contiene(3));
        assertTrue(EJEMPLO.contiene(5));
        assertFalse(EJEMPLO.contiene(6));
        assertFalse(EJEMPLO.contiene(9));
        assertTrue(EJEMPLO.contiene(10));
        assertTrue(EJEMPLO.contiene(20));
        assertFalse(EJEMPLO.contiene(21));
    }

    @Test
    @DisplayName("cantidad: cuenta una sola vez los valores solapados")
    void cantidad() {
        assertEquals(14, EJEMPLO.cantidad());
    }

    @Test
    @DisplayName("cantidad: IPs bloqueadas (AoC 2016 día 20)")
    void cantidad_ipsBloqueadas() {
        // IPs bloqueadas 5-8, 0-2, 4-7 en el espacio 0..9 → libres 3 y 9
        ConjuntoRangos bloqueadas = new ConjuntoRangos(List.of(new Rango(5, 8), new Rango(0, 2), new Rango(4, 7)));
        assertEquals(8, bloqueadas.cantidad());
        assertEquals(2, 10 - bloqueadas.cantidad());
        assertFalse(bloqueadas.contiene(3));
        assertFalse(bloqueadas.contiene(9));
    }

    @Test
    @DisplayName("conjunto vacío")
    void vacio() {
        ConjuntoRangos vacio = new ConjuntoRangos(List.of());
        assertTrue(vacio.estaVacio());
        assertFalse(vacio.contiene(0));
        assertEquals(0, vacio.cantidad());
    }

    @Test
    @DisplayName("valores grandes, cerca de Long.MAX_VALUE")
    void valoresGrandes() {
        ConjuntoRangos conjunto = new ConjuntoRangos(List.of(new Rango(Long.MAX_VALUE - 5, Long.MAX_VALUE)));
        assertTrue(conjunto.contiene(Long.MAX_VALUE));
        assertFalse(conjunto.contiene(Long.MAX_VALUE - 6));
        assertEquals(6, conjunto.cantidad());
    }

    @Test
    @DisplayName("equals: dos colecciones con la misma unión son iguales")
    void igualdad() {
        assertEquals(new ConjuntoRangos(List.of(new Rango(1, 5), new Rango(6, 10))),
                new ConjuntoRangos(List.of(new Rango(1, 10))));
    }

    @Property
    @Label("contiene y cantidad coinciden con enumerar los valores en un Set")
    void coincideConFuerzaBruta(
            @ForAll @Size(max = 10) List<@IntRange(min = -20, max = 50) Integer> inicios,
            @ForAll @Size(max = 10) List<@IntRange(min = 0, max = 10) Integer> largos,
            @ForAll @IntRange(min = -25, max = 65) int valor) {
        List<Rango> rangos = new ArrayList<>();
        Set<Long> valores = new HashSet<>();
        for (int i = 0; i < Math.min(inicios.size(), largos.size()); i++) {
            long inicio = inicios.get(i);
            long fin = inicio + largos.get(i);
            rangos.add(new Rango(inicio, fin));
            for (long v = inicio; v <= fin; v++) valores.add(v);
        }
        ConjuntoRangos conjunto = new ConjuntoRangos(rangos);
        assertEquals(valores.contains((long) valor), conjunto.contiene(valor));
        assertEquals(valores.size(), conjunto.cantidad());
    }
}
