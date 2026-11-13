package advent.code.y2023;

import advent.code.Ciclo;
import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 14 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 14: Plato Reflector Parabólico")
class Dia14Prueba {

    private Dia14 dia14;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "O....#....",
            "O.OO#....#",
            ".....##...",
            "OO.#O....O",
            ".O.....O#.",
            "O.#..O.#.#",
            "..O..#O..O",
            ".......O..",
            "#....###..",
            "#OO..#...."
    );

    /** El ejemplo inclinado hacia el norte, como lo muestra el enunciado. */
    private static final List<String> EJEMPLO_AL_NORTE = List.of(
            "OOOO.#.O..",
            "OO..#....#",
            "OO..O##..O",
            "O..#.OO...",
            "........#.",
            "..#....#.#",
            "..O..#.O.O",
            "..O.......",
            "#....###..",
            "#....#...."
    );

    @BeforeEach
    void configurar() {
        dia14 = new Dia14();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("136", dia14.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: inclinar el ejemplo al norte da la plataforma del enunciado, sin modificar la original")
    void parte1_inclinarAlNorte() {
        Grilla plataforma = Grilla.desdeLineas(EJEMPLO);
        assertEquals(Grilla.desdeLineas(EJEMPLO_AL_NORTE), Dia14.inclinarAlNorte(plataforma));
        assertEquals(Grilla.desdeLineas(EJEMPLO), plataforma);
        assertEquals(136, Dia14.carga(Grilla.desdeLineas(EJEMPLO_AL_NORTE)));
    }

    @Test
    @DisplayName("Parte 1: coincide con mover las rocas una casilla por vez hasta que ninguna pueda moverse")
    void parte1_fuerzaBruta() {
        char[][] celdas = new char[EJEMPLO.size()][];
        for (int y = 0; y < celdas.length; y++) celdas[y] = EJEMPLO.get(y).toCharArray();
        boolean seMovio = true;
        while (seMovio) {
            seMovio = false;
            for (int y = 1; y < celdas.length; y++) {
                for (int x = 0; x < celdas[y].length; x++) {
                    if (celdas[y][x] == 'O' && celdas[y - 1][x] == '.') {
                        celdas[y - 1][x] = 'O';
                        celdas[y][x] = '.';
                        seMovio = true;
                    }
                }
            }
        }
        List<String> esperada = Arrays.stream(celdas).map(String::new).toList();
        assertEquals(Grilla.desdeLineas(esperada), Dia14.inclinarAlNorte(Grilla.desdeLineas(EJEMPLO)));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("64", dia14.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los tres primeros ciclos de giro dan las plataformas del enunciado")
    void parte2_primerosCiclos() {
        List<List<String>> esperadas = List.of(
                List.of(".....#....", "....#...O#", "...OO##...", ".OO#......", ".....OOO#.",
                        ".O#...O#.#", "....O#....", "......OOOO", "#...O###..", "#..OO#...."),
                List.of(".....#....", "....#...O#", ".....##...", "..O#......", ".....OOO#.",
                        ".O#...O#.#", "....O#...O", ".......OOO", "#..OO###..", "#.OOO#...O"),
                List.of(".....#....", "....#...O#", ".....##...", "..O#......", ".....OOO#.",
                        ".O#...O#.#", "....O#...O", ".......OOO", "#...O###.O", "#.OOO#...O"));
        Grilla plataforma = Grilla.desdeLineas(EJEMPLO);
        for (List<String> esperada : esperadas) {
            plataforma = Dia14.cicloDeGiro(plataforma);
            assertEquals(Grilla.desdeLineas(esperada), plataforma);
        }
    }

    @Test
    @DisplayName("Parte 2: el salto por el ciclo coincide con simular paso a paso los primeros 200 ciclos")
    void parte2_saltoCoincideConSimular() {
        Grilla inicial = Grilla.desdeLineas(EJEMPLO);
        Ciclo<Grilla> ciclo = Ciclo.detectar(inicial, Dia14::cicloDeGiro);
        Grilla plataforma = inicial;
        for (int paso = 0; paso <= 200; paso++) {
            assertEquals(plataforma, ciclo.estadoEn(paso), "paso " + paso);
            plataforma = Dia14.cicloDeGiro(plataforma);
        }
    }
}
