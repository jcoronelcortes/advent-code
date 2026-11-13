package advent.code.y2022;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 1 — Año 2022.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2022 — Día 1: Contando Calorías")
class Dia01Prueba {

    private Dia01 dia01;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "1000", "2000", "3000",
            "",
            "4000",
            "",
            "5000", "6000",
            "",
            "7000", "8000", "9000",
            "",
            "10000"
    );

    @BeforeEach
    void configurar() {
        dia01 = new Dia01();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("24000", dia01.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los totales de los cinco elfos son los del enunciado")
    void parte1_totales() {
        assertEquals(List.of(6000L, 4000L, 11000L, 24000L, 10000L), Dia01.totales(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: varias líneas en blanco seguidas o al final no crean elfos vacíos")
    void parte1_lineasEnBlanco() {
        assertEquals(List.of(3L, 4L), Dia01.totales(List.of("", "1", "2", "", "", "4", "", "")));
    }

    @Test
    @DisplayName("Parte 1: un inventario vacío o con algo que no es un número falla con un mensaje claro")
    void parte1_inputInvalido() {
        assertThrows(IllegalArgumentException.class, () -> dia01.parte1(List.of()));
        assertThrows(IllegalArgumentException.class, () -> dia01.parte1(List.of("100", "cien")));
        assertThrows(IllegalArgumentException.class, () -> dia01.parte1(List.of("100 200")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("45000", dia01.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar todos los tríos de elfos y quedarse con la mayor suma")
    void parte2_fuerzaBruta() {
        List<Long> totales = Dia01.totales(EJEMPLO);
        long mejor = 0;
        for (int a = 0; a < totales.size(); a++)
            for (int b = a + 1; b < totales.size(); b++)
                for (int c = b + 1; c < totales.size(); c++)
                    mejor = Math.max(mejor, totales.get(a) + totales.get(b) + totales.get(c));
        assertEquals(mejor, Dia01.sumaDeLosMayores(totales, 3));
        assertEquals(24000 + 11000 + 10000, mejor);
    }

    @Test
    @DisplayName("Parte 2: con totales repetidos se cuentan todos, y con menos de tres elfos falla")
    void parte2_casosLimite() {
        assertEquals(15, Dia01.sumaDeLosMayores(List.of(5L, 5L, 5L, 1L), 3));
        assertEquals(24000, Dia01.sumaDeLosMayores(Dia01.totales(EJEMPLO), 1));
        assertThrows(IllegalArgumentException.class, () -> dia01.parte2(List.of("1", "", "2")));
        assertThrows(IllegalArgumentException.class, () -> Dia01.sumaDeLosMayores(List.of(1L), 0));
    }
}
