package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 25 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 25: Crónica del Código")
class Dia25Prueba {

    private Dia25 dia25;

    /** Input del ejemplo del enunciado: dos cerraduras y tres llaves. */
    private static final List<String> EJEMPLO = List.of(
            "#####", ".####", ".####", ".####", ".#.#.", ".#...", ".....",
            "",
            "#####", "##.##", ".#.##", "...##", "...#.", "...#.", ".....",
            "",
            ".....", "#....", "#....", "#...#", "#.#.#", "#.###", "#####",
            "",
            ".....", ".....", "#.#..", "###..", "###.#", "###.#", "#####",
            "",
            ".....", ".....", ".....", "#....", "#.#..", "#.#.#", "#####"
    );

    @BeforeEach
    void configurar() {
        dia25 = new Dia25();
    }

    private static List<List<Integer>> comoListas(List<int[]> alturas) {
        return alturas.stream().map(a -> Arrays.stream(a).boxed().toList()).toList();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("3", dia25.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las alturas de las cerraduras y las llaves, como en el enunciado")
    void parte1_alturas() {
        Dia25.Esquemas esquemas = Dia25.Esquemas.parsear(EJEMPLO);
        assertEquals(List.of(List.of(0, 5, 3, 4, 3), List.of(1, 2, 0, 5, 3)), comoListas(esquemas.cerraduras()));
        assertEquals(List.of(List.of(5, 0, 2, 1, 3), List.of(4, 3, 4, 0, 2), List.of(3, 0, 2, 0, 1)),
                comoListas(esquemas.llaves()));
        assertEquals(5, esquemas.espacio());
    }

    @Test
    @DisplayName("Parte 1: cada par del ejemplo encaja o no, como dice el enunciado")
    void parte1_cadaPar() {
        Dia25.Esquemas esquemas = Dia25.Esquemas.parsear(EJEMPLO);
        boolean[][] esperado = {{false, false, true}, {false, true, true}};
        for (int c = 0; c < 2; c++) {
            for (int l = 0; l < 3; l++) {
                assertEquals(esperado[c][l],
                        Dia25.encajan(esquemas.cerraduras().get(c), esquemas.llaves().get(l), esquemas.espacio()),
                        "cerradura " + c + ", llave " + l);
            }
        }
    }

    @Test
    @DisplayName("Parte 1: con alturas da lo mismo que superponer los dibujos casilla por casilla")
    void parte1_superponerDibujos() {
        List<List<String>> cerraduras = new ArrayList<>();
        List<List<String>> llaves = new ArrayList<>();
        for (int inicio = 0; inicio < EJEMPLO.size(); inicio += 8) {
            List<String> esquema = EJEMPLO.subList(inicio, inicio + 7);
            (esquema.getFirst().equals("#####") ? cerraduras : llaves).add(esquema);
        }
        long pares = 0;
        for (List<String> cerradura : cerraduras) {
            for (List<String> llave : llaves) {
                boolean choca = false;
                for (int fila = 0; fila < 7; fila++) {
                    for (int columna = 0; columna < 5; columna++) {
                        choca |= cerradura.get(fila).charAt(columna) == '#' && llave.get(fila).charAt(columna) == '#';
                    }
                }
                if (!choca) pares++;
            }
        }
        assertEquals(String.valueOf(pares), dia25.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: un esquema que no es ni cerradura ni llave falla con un mensaje claro")
    void parte1_esquemaInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Dia25.Esquemas.parsear(List.of(
                "#####", ".....", "#####")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el último día no tiene segundo problema")
    void parte2_sinProblema() {
        assertEquals(Dia25.SIN_PARTE_2, dia25.parte2(EJEMPLO));
    }
}
