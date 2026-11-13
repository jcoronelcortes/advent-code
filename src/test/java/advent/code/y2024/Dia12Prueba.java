package advent.code.y2024;

import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 12 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 12: Grupos de Jardín")
class Dia12Prueba {

    private Dia12 dia12;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "RRRRIICCFF",
            "RRRRIICCCF",
            "VVRRRCCFFF",
            "VVRCCCJFFF",
            "VVVVCJJCFE",
            "VVIVCCJJEE",
            "VVIIICJJEE",
            "MIIIIIJJEE",
            "MIIISIJEEE",
            "MMMISSJEEE"
    );

    /** Primer ejemplo chico: cinco plantas, cada una en una sola región. */
    private static final List<String> CHICO = List.of("AAAA", "BBCD", "BBCC", "EEEC");

    /** Segundo ejemplo chico: cuatro X sueltas dentro de una región de O. */
    private static final List<String> REGIONES_DENTRO = List.of("OOOOO", "OXOXO", "OOOOO", "OXOXO", "OOOOO");

    /** Ejemplo de la parte 2: una región con forma de E. */
    private static final List<String> FORMA_DE_E = List.of("EEEEE", "EXXXX", "EEEEE", "EXXXX", "EEEEE");

    /** Ejemplo de la parte 2: dos regiones de B que se tocan en diagonal dentro de una de A. */
    private static final List<String> TOQUE_DIAGONAL = List.of(
            "AAAAAA", "AAABBA", "AAABBA", "ABBAAA", "ABBAAA", "AAAAAA");

    @BeforeEach
    void configurar() {
        dia12 = new Dia12();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo grande del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("1930", dia12.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los ejemplos chicos del enunciado (140 y 772)")
    void parte1_ejemplosChicos() {
        assertEquals("140", dia12.parte1(CHICO));
        assertEquals("772", dia12.parte1(REGIONES_DENTRO));
    }

    @Test
    @DisplayName("Parte 1: área y perímetro de cada región del primer ejemplo chico")
    void parte1_areaYPerimetroDelEjemploChico() {
        Grilla mapa = Grilla.desdeLineas(CHICO);
        assertEquals(List.of("A 4 10", "B 4 8", "C 4 10", "D 1 4", "E 3 8"), describir(mapa));
    }

    @Test
    @DisplayName("Parte 1: la región de O rodea a las X (área 21, perímetro 36) y cada X es su propia región")
    void parte1_regionesDentroDeOtra() {
        Grilla mapa = Grilla.desdeLineas(REGIONES_DENTRO);
        assertEquals(List.of("O 21 36", "X 1 4", "X 1 4", "X 1 4", "X 1 4"), describir(mapa));
    }

    @Test
    @DisplayName("Parte 1: en el ejemplo grande, las dos regiones de C y las dos de I se separan")
    void parte1_mismaPlantaEnVariasRegiones() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        assertEquals(List.of("R 12 18", "I 4 8", "C 14 28", "F 10 18", "V 13 20", "J 11 20",
                        "C 1 4", "E 13 18", "I 14 22", "M 5 12", "S 3 8"),
                describir(mapa));
    }

    /** Cada región como {@code "planta área perímetro"}, en orden de lectura. */
    private static List<String> describir(Grilla mapa) {
        return Dia12.regiones(mapa).stream()
                .map(region -> region.planta() + " " + region.area() + " " + region.perimetro(mapa))
                .toList();
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo grande del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("1206", dia12.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los ejemplos chicos del enunciado (80, 436, 236 y 368)")
    void parte2_ejemplosChicos() {
        assertEquals("80", dia12.parte2(CHICO));
        assertEquals("436", dia12.parte2(REGIONES_DENTRO));
        assertEquals("236", dia12.parte2(FORMA_DE_E));
        assertEquals("368", dia12.parte2(TOQUE_DIAGONAL));
    }

    @Test
    @DisplayName("Parte 2: lados de cada región en los ejemplos del enunciado")
    void parte2_ladosDeCadaRegion() {
        assertEquals(List.of("A 4", "B 4", "C 8", "D 4", "E 4"), lados(CHICO));
        assertEquals(List.of("E 12", "X 4", "X 4"), lados(FORMA_DE_E));
        assertEquals(List.of("R 10", "I 4", "C 22", "F 12", "V 10", "J 12", "C 4", "E 8", "I 16", "M 6", "S 6"),
                lados(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: dos regiones que se tocan en diagonal no unen sus cercas (la A rodea a dos B: 12 lados)")
    void parte2_toqueDiagonal() {
        assertEquals(List.of("A 12", "B 4", "B 4"), lados(TOQUE_DIAGONAL));
    }

    @Test
    @DisplayName("Parte 2: la O con cuatro X adentro tiene 4 lados afuera y 4 por cada hueco")
    void parte2_huecos() {
        assertEquals(List.of("O 20", "X 4", "X 4", "X 4", "X 4"), lados(REGIONES_DENTRO));
    }

    /** Cada región como {@code "planta lados"}, en orden de lectura. */
    private static List<String> lados(List<String> lineas) {
        return Dia12.regiones(Grilla.desdeLineas(lineas)).stream()
                .map(region -> region.planta() + " " + region.lados())
                .toList();
    }
}
