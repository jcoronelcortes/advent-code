package advent.code.y2024;

import advent.code.Camino;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 16 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 16: Laberinto de Renos")
class Dia16Prueba {

    private Dia16 dia16;

    /** Primer ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "###############",
            "#.......#....E#",
            "#.#.###.#.###.#",
            "#.....#.#...#.#",
            "#.###.#####.#.#",
            "#.#.#.......#.#",
            "#.#.#####.###.#",
            "#...........#.#",
            "###.#.#####.#.#",
            "#...#.....#.#.#",
            "#.#.#.###.#.#.#",
            "#.....#...#.#.#",
            "#.###.#.#.#.#.#",
            "#S..#.....#...#",
            "###############"
    );

    /** Segundo ejemplo del enunciado. */
    private static final List<String> EJEMPLO_2 = List.of(
            "#################",
            "#...#...#...#..E#",
            "#.#.#.#.#.#.#.#.#",
            "#.#.#.#...#...#.#",
            "#.#.#.#.###.#.#.#",
            "#...#.#.#.....#.#",
            "#.#.#.#.#.#####.#",
            "#.#...#.#.#.....#",
            "#.#.#####.#.###.#",
            "#.#.#.......#...#",
            "#.#.###.#####.###",
            "#.#.#...#.....#.#",
            "#.#.#.#####.###.#",
            "#.#.#.........#.#",
            "#.#.#.#########.#",
            "#S#.............#",
            "#################"
    );

    @BeforeEach
    void configurar() {
        dia16 = new Dia16();
    }

    /** Cantidad de giros de un camino: pasos en que el reno cambia de dirección. */
    private static long giros(Camino<Dia16.Reno> camino) {
        List<Dia16.Reno> renos = camino.nodos();
        long giros = 0;
        for (int i = 1; i < renos.size(); i++) {
            if (renos.get(i).direccion() != renos.get(i - 1).direccion()) giros++;
        }
        return giros;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el primer ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("7036", dia16.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el segundo ejemplo del enunciado")
    void parte1_segundoEjemplo() {
        assertEquals("11048", dia16.parte1(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 1: el mejor camino del primer ejemplo avanza 36 casillas y gira 7 veces")
    void parte1_pasosYGirosDelPrimerEjemplo() {
        Camino<Dia16.Reno> camino = Dia16.mejorCamino(Grilla.desdeLineas(EJEMPLO));
        long giros = giros(camino);
        assertEquals(7, giros);
        assertEquals(36, camino.pasos() - giros);
    }

    @Test
    @DisplayName("Parte 1: en el segundo ejemplo, el primer movimiento es girar hacia el norte")
    void parte1_primerMovimientoEsGirar() {
        Camino<Dia16.Reno> camino = Dia16.mejorCamino(Grilla.desdeLineas(EJEMPLO_2));
        Dia16.Reno inicio = camino.nodos().get(0);
        Dia16.Reno segundo = camino.nodos().get(1);
        assertEquals(Direccion.DERECHA, inicio.direccion());
        assertEquals(inicio.posicion(), segundo.posicion());
        assertEquals(Direccion.ARRIBA, segundo.direccion());
    }

    @Test
    @DisplayName("Parte 1: un pasillo recto hacia el este cuesta solo los pasos; hacia el oeste, dos giros más")
    void parte1_pasillos() {
        assertEquals("3", dia16.parte1(List.of("######", "#S..E#", "######")));
        assertEquals("2003", dia16.parte1(List.of("######", "#E..S#", "######")));
    }

    @Test
    @DisplayName("Parte 1: si la llegada es inalcanzable, falla con un mensaje claro")
    void parte1_llegadaInalcanzable() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> dia16.parte1(List.of("#####", "#S#E#", "#####")));
        assertTrue(error.getMessage().contains("inalcanzable"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** El laberinto con las casillas de los mejores caminos marcadas con {@code O}, como en el enunciado. */
    private static String marcarMejoresCaminos(List<String> ejemplo) {
        Grilla laberinto = Grilla.desdeLineas(ejemplo);
        for (Punto casilla : Dia16.casillasEnMejoresCaminos(laberinto)) laberinto.establecer(casilla, 'O');
        return laberinto.toString();
    }

    @Test
    @DisplayName("Parte 2: el primer ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("45", dia16.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el segundo ejemplo del enunciado")
    void parte2_segundoEjemplo() {
        assertEquals("64", dia16.parte2(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 2: las casillas de los mejores caminos del primer ejemplo, como en el dibujo del enunciado")
    void parte2_dibujoDelPrimerEjemplo() {
        assertEquals(String.join("\n", List.of(
                "###############",
                "#.......#....O#",
                "#.#.###.#.###O#",
                "#.....#.#...#O#",
                "#.###.#####.#O#",
                "#.#.#.......#O#",
                "#.#.#####.###O#",
                "#..OOOOOOOOO#O#",
                "###O#O#####O#O#",
                "#OOO#O....#O#O#",
                "#O#O#O###.#O#O#",
                "#OOOOO#...#O#O#",
                "#O###.#.#.#O#O#",
                "#O..#.....#OOO#",
                "###############")), marcarMejoresCaminos(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las casillas de los mejores caminos del segundo ejemplo, como en el dibujo del enunciado")
    void parte2_dibujoDelSegundoEjemplo() {
        assertEquals(String.join("\n", List.of(
                "#################",
                "#...#...#...#..O#",
                "#.#.#.#.#.#.#.#O#",
                "#.#.#.#...#...#O#",
                "#.#.#.#.###.#.#O#",
                "#OOO#.#.#.....#O#",
                "#O#O#.#.#.#####O#",
                "#O#O..#.#.#OOOOO#",
                "#O#O#####.#O###O#",
                "#O#O#..OOOOO#OOO#",
                "#O#O###O#####O###",
                "#O#O#OOO#..OOO#.#",
                "#O#O#O#####O###.#",
                "#O#O#OOOOOOO..#.#",
                "#O#O#O#########.#",
                "#O#OOO..........#",
                "#################")), marcarMejoresCaminos(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 2: dos caminos empatados que rodean una pared cuentan las casillas de los dos")
    void parte2_caminosEmpatados() {
        // por arriba o por abajo: 3 giros y 4 pasos cada uno (3004); comparten S, E y nada más
        assertEquals("8", dia16.parte2(List.of("#####", "#...#", "#S#E#", "#...#", "#####")));
    }

    @Test
    @DisplayName("Parte 2: si la llegada es inalcanzable, falla con un mensaje claro")
    void parte2_llegadaInalcanzable() {
        assertThrows(IllegalArgumentException.class, () -> dia16.parte2(List.of("#####", "#S#E#", "#####")));
    }
}
