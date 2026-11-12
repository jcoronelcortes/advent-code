package advent.code.y2024;

import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 8 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 8: Colinealidad Resonante")
class Dia08Prueba {

    private Dia08 dia08;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "............",
            "........0...",
            ".....0......",
            ".......0....",
            "....0.......",
            "......A.....",
            "............",
            "............",
            "........A...",
            ".........A..",
            "............",
            "............"
    );

    @BeforeEach
    void configurar() {
        dia08 = new Dia08();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("14", dia08.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: dos antenas generan un antinodo a cada lado (ejemplo chico del enunciado)")
    void parte1_dosAntenas() {
        List<String> mapa = List.of(
                "..........",
                "..........",
                "..........",
                "....a.....",
                "..........",
                ".....a....",
                "..........",
                "..........",
                "..........",
                "..........");
        assertEquals("2", dia08.parte1(mapa));
        assertEquals(Set.of(new Punto(3, 1), new Punto(6, 7)),
                Set.copyOf(Dia08.antinodosDelPar(Grilla.desdeLineas(mapa), new Punto(4, 3), new Punto(5, 5))));
    }

    @Test
    @DisplayName("Parte 1: una tercera antena agrega antinodos, pero los que caen fuera del mapa no cuentan")
    void parte1_fueraDelMapa() {
        // Con tres antenas 'a' habría 6 antinodos, pero 2 caen a la derecha del mapa
        assertEquals("4", dia08.parte1(List.of(
                "..........",
                "..........",
                "..........",
                "....a.....",
                "........a.",
                ".....a....",
                "..........",
                "..........",
                "..........",
                "..........")));
    }

    @Test
    @DisplayName("Parte 1: A y a son frecuencias distintas; un antinodo puede caer sobre una antena")
    void parte1_frecuencias() {
        Map<Character, List<Punto>> porFrecuencia = Dia08.antenasPorFrecuencia(Grilla.desdeLineas(EJEMPLO));
        assertEquals(Set.of('0', 'A'), porFrecuencia.keySet());
        assertEquals(4, porFrecuencia.get('0').size());
        // La A sola no genera antinodos, pero tiene encima un antinodo de las 'a' (ejemplo del enunciado)
        assertEquals("4", dia08.parte1(List.of(
                "..........",
                "..........",
                "..........",
                "....a.....",
                "........a.",
                ".....a....",
                "..........",
                "......A...",
                "..........",
                "..........")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("34", dia08.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las tres antenas T del enunciado generan 9 antinodos, incluidas ellas mismas")
    void parte2_antenasT() {
        assertEquals("9", dia08.parte2(List.of(
                "T.........",
                "...T......",
                ".T........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........")));
    }

    @Test
    @DisplayName("Parte 2: el paso se reduce por el MCD, así cuentan las posiciones entre las antenas")
    void parte2_pasoReducido() {
        Grilla mapa = Grilla.desdeLineas(List.of("a....", ".....", "..a..", ".....", "....."));
        // Antenas en (0,0) y (2,2): la diagonal completa (0,0) a (4,4), incluida (1,1) entre ellas
        assertEquals(Set.of(new Punto(0, 0), new Punto(1, 1), new Punto(2, 2), new Punto(3, 3), new Punto(4, 4)),
                Set.copyOf(Dia08.antinodosEnLaRecta(mapa, new Punto(0, 0), new Punto(2, 2))));
    }

    @Test
    @DisplayName("Parte 2: una antena sola no genera antinodos, ni siquiera en su lugar")
    void parte2_antenaSola() {
        assertEquals("0", dia08.parte2(List.of("...", ".A.", "...")));
    }
}
