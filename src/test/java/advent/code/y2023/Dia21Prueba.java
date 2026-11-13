package advent.code.y2023;

import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 21 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 21: Contador de Pasos")
class Dia21Prueba {

    private Dia21 dia21;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "...........",
            ".....###.#.",
            ".###.##..#.",
            "..#.#...#..",
            "....#.#....",
            ".##..S####.",
            ".##..#...#.",
            ".......##..",
            ".##.#.####.",
            ".##..##.##.",
            "..........."
    );

    /** Las parcelas alcanzables en exactamente 6 pasos, como las muestra el enunciado. */
    private static final List<String> EJEMPLO_6_PASOS = List.of(
            "...........",
            ".....###.#.",
            ".###.##.O#.",
            ".O#O#O.O#..",
            "O.O.#.#.O..",
            ".##O.O####.",
            ".##.O#O..#.",
            ".O.O.O.##..",
            ".##.#.####.",
            ".##O.##.##.",
            "..........."
    );

    /** Fuerza bruta: simular el conjunto de posiciones paso por paso. */
    private static Set<Punto> pasoAPaso(Grilla mapa, int pasos) {
        Set<Punto> posiciones = Set.of(mapa.buscarUnico('S'));
        for (int i = 0; i < pasos; i++) {
            Set<Punto> siguientes = new HashSet<>();
            for (Punto p : posiciones) siguientes.addAll(mapa.vecinos(p, c -> c != '#'));
            posiciones = siguientes;
        }
        return posiciones;
    }

    @BeforeEach
    void configurar() {
        dia21 = new Dia21();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado: 16 parcelas en 6 pasos")
    void parte1_ejemploDelEnunciado() {
        assertEquals(16, Dia21.alcanzables(Grilla.desdeLineas(EJEMPLO), 6));
    }

    @Test
    @DisplayName("Parte 1: con 1, 2 y 3 pasos se alcanzan 2, 4 y 6 parcelas, como en los dibujos del enunciado")
    void parte1_primerosPasos() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        assertEquals(1, Dia21.alcanzables(mapa, 0));
        assertEquals(2, Dia21.alcanzables(mapa, 1));
        assertEquals(4, Dia21.alcanzables(mapa, 2));
        assertEquals(6, Dia21.alcanzables(mapa, 3));
    }

    @Test
    @DisplayName("Parte 1: las parcelas de 6 pasos son exactamente las O del enunciado")
    void parte1_parcelasDelEnunciado() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        assertEquals(Set.copyOf(Grilla.desdeLineas(EJEMPLO_6_PASOS).buscarTodos('O')), pasoAPaso(mapa, 6));
    }

    @Test
    @DisplayName("Parte 1: coincide con simular el conjunto de posiciones paso por paso, de 0 a 30 pasos")
    void parte1_fuerzaBruta() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        for (int pasos = 0; pasos <= 30; pasos++) {
            assertEquals(pasoAPaso(mapa, pasos).size(), Dia21.alcanzables(mapa, pasos), "pasos: " + pasos);
        }
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /**
     * El ejemplo con la fila y la columna de S despejadas, como en el input
     * real: así la cantidad de parcelas crece como una cuadrática.
     */
    private static final List<String> EJEMPLO_DESPEJADO = List.of(
            "...........",
            "......##.#.",
            ".###..#..#.",
            "..#.#...#..",
            "....#.#....",
            ".....S.....",
            ".##......#.",
            ".......##..",
            ".##.#.####.",
            ".##...#.##.",
            "..........."
    );

    @Test
    @DisplayName("Parte 2: en el mapa infinito del ejemplo, las cantidades del enunciado de 6 a 1000 pasos")
    void parte2_ejemploDelEnunciado() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        assertEquals(16, Dia21.alcanzablesEnMapaInfinito(mapa, 6));
        assertEquals(50, Dia21.alcanzablesEnMapaInfinito(mapa, 10));
        assertEquals(1594, Dia21.alcanzablesEnMapaInfinito(mapa, 50));
        assertEquals(6536, Dia21.alcanzablesEnMapaInfinito(mapa, 100));
        assertEquals(167004, Dia21.alcanzablesEnMapaInfinito(mapa, 500));
        assertEquals(668697, Dia21.alcanzablesEnMapaInfinito(mapa, 1000));
    }

    @Test
    @DisplayName("Parte 2: el mapa infinito coincide con simular paso por paso y con el mapa chico mientras no se salga")
    void parte2_mapaInfinitoFuerzaBruta() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        for (int pasos = 0; pasos <= 25; pasos++) {
            assertEquals(pasoAPasoInfinito(mapa, pasos), Dia21.alcanzablesEnMapaInfinito(mapa, pasos), "pasos: " + pasos);
        }
        // Con 5 pasos o menos desde el centro no se llega al borde: igual que en el mapa chico
        for (int pasos = 0; pasos <= 5; pasos++) {
            assertEquals(Dia21.alcanzables(mapa, pasos), Dia21.alcanzablesEnMapaInfinito(mapa, pasos));
        }
    }

    @Test
    @DisplayName("Parte 2: con la fila y la columna de S despejadas, extrapolar coincide con el BFS directo para todo resto")
    void parte2_extrapolarCoincide() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO_DESPEJADO);
        for (int resto = 0; resto < 11; resto++) {
            for (int k = 0; k <= 9; k++) {
                int pasos = resto + 11 * k;
                assertEquals(Dia21.alcanzablesEnMapaInfinito(mapa, pasos), Dia21.alcanzablesExtrapolando(mapa, pasos),
                        "pasos: " + pasos);
            }
        }
    }

    @Test
    @DisplayName("Parte 2: si las cantidades no crecen como una cuadrática o el mapa no es cuadrado, falla")
    void parte2_noCuadratica() {
        // En el ejemplo original la fila y la columna de S tienen rocas, y con resto 0 (26501365 mod 11) no da una cuadrática
        assertThrows(IllegalStateException.class,
                () -> Dia21.alcanzablesExtrapolando(Grilla.desdeLineas(EJEMPLO), 26501365));
        assertThrows(IllegalArgumentException.class,
                () -> Dia21.alcanzablesExtrapolando(Grilla.desdeLineas(List.of("...", ".S.")), 100));
    }

    /** Fuerza bruta: simular el conjunto de posiciones paso por paso en el mapa infinito. */
    private static int pasoAPasoInfinito(Grilla mapa, int pasos) {
        Set<Punto> posiciones = Set.of(mapa.buscarUnico('S'));
        for (int i = 0; i < pasos; i++) {
            Set<Punto> siguientes = new HashSet<>();
            for (Punto p : posiciones) {
                for (Punto v : p.vecinos()) {
                    Punto enMapa = new Punto(Math.floorMod(v.x(), mapa.columnas()), Math.floorMod(v.y(), mapa.filas()));
                    if (mapa.obtener(enMapa) != '#') siguientes.add(v);
                }
            }
            posiciones = siguientes;
        }
        return posiciones.size();
    }
}
