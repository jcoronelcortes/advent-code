package advent.code.y2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas para la solución del Día 1 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 1: Entrada Secreta")
class Day01Test {

    private Day01 día01;

    /** Rotaciones del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = Arrays.asList(
            "L68", "L30", "R48", "L5", "R60", "L55", "L1", "L99", "R14", "L82"
    );

    @BeforeEach
    void setUp() {
        día01 = new Day01();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado debe dar 3")
    void parte1_ejemplo() {
        assertEquals("3", día01.part1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: lista vacía debe dar 0")
    void parte1_listaVacía() {
        assertEquals("0", día01.part1(List.of()));
    }

    @Test
    @DisplayName("Parte 1: rotación exacta a 0 cuenta")
    void parte1_rotaciónExactaACero() {
        assertEquals("1", día01.part1(List.of("R50")));
    }

    @Test
    @DisplayName("Parte 1: rotación que no llega a 0 no cuenta")
    void parte1_sinPasosPorCero() {
        assertEquals("0", día01.part1(List.of("R10")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado debe dar 6")
    void parte2_ejemplo() {
        assertEquals("6", día01.part2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: R1000 desde 50 pasa por 0 exactamente 10 veces")
    void parte2_casoEspecialR1000() {
        assertEquals("10", día01.part2(List.of("R1000")));
    }

    @Test
    @DisplayName("Parte 2: lista vacía debe dar 0")
    void parte2_listaVacía() {
        assertEquals("0", día01.part2(List.of()));
    }

    // -------------------------------------------------------------------------
    // contarPasosPorCero (método auxiliar)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("contarPasosPorCero: L68 desde 50 pasa por 0 una vez")
    void contarPasos_L68_desde50() {
        assertEquals(1, Day01.contarPasosPorCero(50, 'L', 68));
    }

    @Test
    @DisplayName("contarPasosPorCero: L30 desde 82 no pasa por 0")
    void contarPasos_L30_desde82() {
        assertEquals(0, Day01.contarPasosPorCero(82, 'L', 30));
    }

    @Test
    @DisplayName("contarPasosPorCero: R60 desde 95 pasa por 0 una vez")
    void contarPasos_R60_desde95() {
        assertEquals(1, Day01.contarPasosPorCero(95, 'R', 60));
    }

    @Test
    @DisplayName("contarPasosPorCero: desde 0 girando L, cuenta cada 100 pasos")
    void contarPasos_desde0_L() {
        assertEquals(2, Day01.contarPasosPorCero(0, 'L', 250));
    }

    @Test
    @DisplayName("contarPasosPorCero: desde 0 girando R, cuenta cada 100 pasos")
    void contarPasos_desde0_R() {
        assertEquals(3, Day01.contarPasosPorCero(0, 'R', 300));
    }
}