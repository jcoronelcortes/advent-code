package advent.code.y2025;

import advent.code.Rango;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 5 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 5: Cafetería")
class Dia05Prueba {

    private Dia05 dia05;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "3-5",
            "10-14",
            "16-20",
            "12-18",
            "",
            "1",
            "5",
            "8",
            "11",
            "17",
            "32"
    );

    @BeforeEach
    void configurar() {
        dia05 = new Dia05();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("3", dia05.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: cada ID del ejemplo se clasifica como dice el enunciado")
    void parte1_clasificacionDelEjemplo() {
        List<Rango> unidos = Rango.unir(Dia05.BaseDeDatos.parsear(EJEMPLO).rangos());
        assertFalse(Dia05.esFresco(unidos, 1));
        assertTrue(Dia05.esFresco(unidos, 5));
        assertFalse(Dia05.esFresco(unidos, 8));
        assertTrue(Dia05.esFresco(unidos, 11));
        assertTrue(Dia05.esFresco(unidos, 17));   // está en 16-20 y en 12-18
        assertFalse(Dia05.esFresco(unidos, 32));
    }

    @Test
    @DisplayName("Parte 1: los extremos de los rangos son inclusivos")
    void parte1_extremosInclusivos() {
        List<Rango> unidos = Rango.unir(List.of(new Rango(3, 5), new Rango(10, 14)));
        assertFalse(Dia05.esFresco(unidos, 2));
        assertTrue(Dia05.esFresco(unidos, 3));
        assertTrue(Dia05.esFresco(unidos, 5));
        assertFalse(Dia05.esFresco(unidos, 6));
        assertFalse(Dia05.esFresco(unidos, 9));
        assertTrue(Dia05.esFresco(unidos, 14));
        assertFalse(Dia05.esFresco(unidos, 15));
    }

    @Property
    @DisplayName("Parte 1: la búsqueda binaria coincide con revisar todos los rangos")
    void parte1_busquedaBinariaCoincideConFuerzaBruta(
            @ForAll @Size(max = 10) List<@IntRange(min = 0, max = 50) Integer> inicios,
            @ForAll @Size(max = 10) List<@IntRange(min = 0, max = 10) Integer> largos,
            @ForAll @IntRange(min = -5, max = 70) int id) {
        List<Rango> rangos = new ArrayList<>();
        for (int i = 0; i < Math.min(inicios.size(), largos.size()); i++) {
            rangos.add(new Rango(inicios.get(i), inicios.get(i) + largos.get(i)));
        }
        boolean esperado = rangos.stream().anyMatch(r -> r.contiene(id));
        assertEquals(esperado, Dia05.esFresco(Rango.unir(rangos), id));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        // 3, 4, 5 y del 10 al 20
        assertEquals("14", dia05.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: rangos contenidos, contiguos y repetidos se cuentan una sola vez")
    void parte2_solapamientos() {
        assertEquals("10", dia05.parte2(List.of("1-10", "3-4", "1-10", "")));     // contenido y repetido
        assertEquals("10", dia05.parte2(List.of("1-5", "6-10", "")));             // contiguos
        assertEquals("2", dia05.parte2(List.of("7-7", "9-9", "")));               // rangos de un solo ID
    }

    @Property
    @DisplayName("Parte 2: coincide con enumerar los IDs en un conjunto")
    void parte2_coincideConFuerzaBruta(
            @ForAll @Size(max = 10) List<@IntRange(min = 0, max = 50) Integer> inicios,
            @ForAll @Size(max = 10) List<@IntRange(min = 0, max = 10) Integer> largos) {
        List<String> lineas = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        for (int i = 0; i < Math.min(inicios.size(), largos.size()); i++) {
            int inicio = inicios.get(i);
            int fin = inicio + largos.get(i);
            lineas.add(inicio + "-" + fin);
            for (int id = inicio; id <= fin; id++) ids.add(id);
        }
        lineas.add("");
        assertEquals(String.valueOf(ids.size()), dia05.parte2(lineas));
    }
}
