package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 4 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 4: Búsqueda en Ceres")
class Dia04Prueba {

    private Dia04 dia04;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "MMMSXXMASM",
            "MSAMXMSMSA",
            "AMXSXMAAMM",
            "MSAMASMSMX",
            "XMASAMXAMM",
            "XXAMMXXAMA",
            "SMSMSASXSS",
            "SAXAMASAAA",
            "MAMMMXMMMM",
            "MXMXAXMASX"
    );

    @BeforeEach
    void configurar() {
        dia04 = new Dia04();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("18", dia04.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el ejemplo chico del enunciado tiene 4 apariciones")
    void parte1_ejemploChico() {
        // Horizontal al revés (SAMX, ←), horizontal (→), vertical hacia arriba (↑) y diagonal (↘)
        assertEquals("4", dia04.parte1(List.of(
                "..X...",
                ".SAMX.",
                ".A..A.",
                "XMAS.S",
                ".X....")));
    }

    @Test
    @DisplayName("Parte 1: las 8 direcciones, de a una")
    void parte1_ochoDirecciones() {
        assertEquals("1", dia04.parte1(List.of("XMAS")));                 // →
        assertEquals("1", dia04.parte1(List.of("SAMX")));                 // ←
        assertEquals("1", dia04.parte1(List.of("X", "M", "A", "S")));     // ↓
        assertEquals("1", dia04.parte1(List.of("S", "A", "M", "X")));     // ↑
        assertEquals("1", dia04.parte1(List.of("X...", ".M..", "..A.", "...S")));   // ↘
        assertEquals("1", dia04.parte1(List.of("S...", ".A..", "..M.", "...X")));   // ↖
        assertEquals("1", dia04.parte1(List.of("...X", "..M.", ".A..", "S...")));   // ↙
        assertEquals("1", dia04.parte1(List.of("...S", "..A.", ".M..", "X...")));   // ↗
    }

    @Test
    @DisplayName("Parte 1: apariciones superpuestas y palabras cortadas por el borde")
    void parte1_superpuestasYBordes() {
        assertEquals("2", dia04.parte1(List.of("XMASAMX")));   // comparten la S
        assertEquals("0", dia04.parte1(List.of("XMA")));       // se sale de la grilla
        assertEquals("0", dia04.parte1(List.of("XMAX")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("9", dia04.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las 4 formas de cruzar dos MAS (cada uno al derecho o al revés)")
    void parte2_cuatroFormas() {
        assertEquals("1", dia04.parte2(List.of("M.S", ".A.", "M.S")));
        assertEquals("1", dia04.parte2(List.of("S.M", ".A.", "S.M")));
        assertEquals("1", dia04.parte2(List.of("M.M", ".A.", "S.S")));
        assertEquals("1", dia04.parte2(List.of("S.S", ".A.", "M.M")));
    }

    @Test
    @DisplayName("Parte 2: casos que no son X-MAS")
    void parte2_noCruces() {
        assertEquals("0", dia04.parte2(List.of("M.M", ".A.", "M.M")));   // ninguna diagonal tiene S
        assertEquals("0", dia04.parte2(List.of("M.S", ".A.", "S.M")));   // las diagonales leen MAM y SAS
        assertEquals("0", dia04.parte2(List.of(".M.", "MAS", ".S.")));   // un + no es una X
        assertEquals("0", dia04.parte2(List.of("A.S", ".A.", "M.S")));   // ↖ de la A central es otra A; la A del borde no tiene diagonales
    }

    @Test
    @DisplayName("Parte 2: dos cruces pueden compartir letras")
    void parte2_crucesCompartidas() {
        // Las dos A comparten la M y la S de la columna del medio
        assertEquals("2", dia04.parte2(List.of("M.M.M", ".A.A.", "S.S.S")));
    }
}
