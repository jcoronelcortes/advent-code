package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 5 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 5: Cola de Impresión")
class Dia05Prueba {

    private Dia05 dia05;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "47|53",
            "97|13",
            "97|61",
            "97|47",
            "75|29",
            "61|13",
            "75|53",
            "29|13",
            "97|29",
            "53|29",
            "61|53",
            "97|53",
            "61|29",
            "47|13",
            "75|47",
            "97|75",
            "47|61",
            "75|61",
            "47|29",
            "75|13",
            "53|13",
            "",
            "75,47,61,53,29",
            "97,61,53,29,13",
            "75,29,13",
            "75,97,47,61,53",
            "61,13,29",
            "97,13,75,29,47"
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
        assertEquals("143", dia05.parte1(EJEMPLO));   // 61 + 53 + 29
    }

    @Test
    @DisplayName("Parte 1: las tres primeras actualizaciones del ejemplo están en orden; las otras tres no")
    void parte1_actualizacionesDelEjemplo() {
        Dia05.Manual manual = Dia05.Manual.parsear(EJEMPLO);
        assertEquals(21, manual.reglas().size());
        assertEquals(List.of(true, true, true, false, false, false),
                manual.actualizaciones().stream().map(manual::estaEnOrden).toList());
    }

    @Test
    @DisplayName("Parte 1: solo cuentan las reglas cuyas dos páginas están en la actualización")
    void parte1_reglasQueNoAplican() {
        Dia05.Manual manual = new Dia05.Manual(Set.of(new Dia05.Regla(1, 2), new Dia05.Regla(3, 4)), List.of());
        assertTrue(manual.estaEnOrden(List.of(4, 1, 2)));    // 3|4 no aplica: falta el 3
        assertFalse(manual.estaEnOrden(List.of(2, 5, 1)));   // 1|2 no tiene que ser contigua para contar
        assertTrue(manual.estaEnOrden(List.of(7)));
    }

    @Test
    @DisplayName("Parte 1: página del medio")
    void parte1_paginaDelMedio() {
        assertEquals(61, Dia05.paginaDelMedio(List.of(75, 47, 61, 53, 29)));
        assertEquals(29, Dia05.paginaDelMedio(List.of(75, 29, 13)));
        assertThrows(IllegalArgumentException.class, () -> Dia05.paginaDelMedio(List.of(1, 2)));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("123", dia05.parte2(EJEMPLO));   // 47 + 29 + 47
    }

    @Test
    @DisplayName("Parte 2: las tres actualizaciones desordenadas del ejemplo quedan como dice el enunciado")
    void parte2_ordenesDelEjemplo() {
        Dia05.Manual manual = Dia05.Manual.parsear(EJEMPLO);
        assertEquals(List.of(97, 75, 47, 61, 53), manual.ordenar(List.of(75, 97, 47, 61, 53)));
        assertEquals(List.of(61, 29, 13), manual.ordenar(List.of(61, 13, 29)));
        assertEquals(List.of(97, 75, 47, 29, 13), manual.ordenar(List.of(97, 13, 75, 29, 47)));
        // Ordenar una actualización que ya estaba en orden no la cambia
        assertEquals(List.of(75, 47, 61, 53, 29), manual.ordenar(List.of(75, 47, 61, 53, 29)));
    }

    @Test
    @DisplayName("Parte 2: el resultado de ordenar siempre está en orden")
    void parte2_ordenadasQuedanEnOrden() {
        Dia05.Manual manual = Dia05.Manual.parsear(EJEMPLO);
        manual.actualizaciones().forEach(paginas -> assertTrue(manual.estaEnOrden(manual.ordenar(paginas))));
    }

    @Test
    @DisplayName("Parte 2: si dos páginas no tienen regla, lo informa en vez de inventar un orden")
    void parte2_sinRegla() {
        Dia05.Manual manual = new Dia05.Manual(Set.of(new Dia05.Regla(1, 2)), List.of());
        assertThrows(IllegalStateException.class, () -> manual.ordenar(List.of(3, 1)));
    }
}
