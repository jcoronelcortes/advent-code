package advent.code.y2023;

import advent.code.BFS;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 11 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 11: Expansión Cósmica")
class Dia11Prueba {

    private Dia11 dia11;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "...#......",
            ".......#..",
            "#.........",
            "..........",
            "......#...",
            ".#........",
            ".........#",
            "..........",
            ".......#..",
            "#...#....."
    );

    @BeforeEach
    void configurar() {
        dia11 = new Dia11();
    }

    /** Fuerza bruta: duplica literalmente cada fila y cada columna sin galaxias. */
    private static List<String> expandirLiteralmente(List<String> lineas) {
        List<String> filas = new ArrayList<>();
        for (String fila : lineas) {
            filas.add(fila);
            if (!fila.contains("#")) filas.add(fila);
        }
        List<Integer> columnasVacias = new ArrayList<>();
        for (int x = 0; x < lineas.getFirst().length(); x++) {
            final int columna = x;
            if (lineas.stream().noneMatch(fila -> fila.charAt(columna) == '#')) columnasVacias.add(columna);
        }
        List<String> resultado = new ArrayList<>();
        for (String fila : filas) {
            StringBuilder nueva = new StringBuilder();
            for (int x = 0; x < fila.length(); x++) {
                nueva.append(fila.charAt(x));
                if (columnasVacias.contains(x)) nueva.append(fila.charAt(x));
            }
            resultado.add(nueva.toString());
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("374", dia11.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el universo expandido, como en el dibujo del enunciado")
    void parte1_dibujoExpandido() {
        assertEquals(List.of(
                "....#........",
                ".........#...",
                "#............",
                ".............",
                ".............",
                "........#....",
                ".#...........",
                "............#",
                ".............",
                ".............",
                ".........#...",
                "#....#......."), expandirLiteralmente(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las coordenadas expandidas: las filas 3 y 7 y las columnas 2, 5 y 8 están vacías")
    void parte1_coordenadasExpandidas() {
        int[] filasOcupadas = {0, 1, 2, 4, 5, 6, 8, 9};
        assertArrayEquals(new long[] {0, 1, 2, 3, 5, 6, 7, 8, 10, 11}, Dia11.expandir(10, filasOcupadas, 2));
        int[] columnasOcupadas = {0, 1, 3, 4, 6, 7, 9};
        assertArrayEquals(new long[] {0, 1, 2, 4, 5, 6, 8, 9, 10, 12}, Dia11.expandir(10, columnasOcupadas, 2));
    }

    @Test
    @DisplayName("Parte 1: coincide con expandir literalmente y medir cada par con una BFS (36 pares)")
    void parte1_fuerzaBruta() {
        Grilla expandida = Grilla.desdeLineas(expandirLiteralmente(EJEMPLO));
        List<Punto> galaxias = expandida.buscarTodos('#');
        assertEquals(9, galaxias.size());
        long suma = 0;
        for (int i = 0; i < galaxias.size(); i++) {
            for (int j = i + 1; j < galaxias.size(); j++) {
                Punto destino = galaxias.get(j);
                suma += BFS.buscar(galaxias.get(i), destino::equals, expandida::vecinos).orElseThrow();
            }
        }
        assertEquals(suma, Dia11.sumaDeDistancias(Grilla.desdeLineas(EJEMPLO), 2));
    }

    @Test
    @DisplayName("Parte 1: con factor 1 no hay expansión, y un factor menor que 1 falla")
    void parte1_factores() {
        Grilla imagen = Grilla.desdeLineas(List.of("#..", "...", "..#"));
        assertEquals(4, Dia11.sumaDeDistancias(imagen, 1));
        assertEquals(6, Dia11.sumaDeDistancias(imagen, 2));   // una fila y una columna vacías entre las dos
        assertThrows(IllegalArgumentException.class, () -> Dia11.sumaDeDistancias(imagen, 0));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: los ejemplos del enunciado con factor 10 (1030) y 100 (8410)")
    void parte2_ejemploDelEnunciado() {
        Grilla imagen = Grilla.desdeLineas(EJEMPLO);
        assertEquals(1030, Dia11.sumaDeDistancias(imagen, 10));
        assertEquals(8410, Dia11.sumaDeDistancias(imagen, 100));
    }

    @Test
    @DisplayName("Parte 2: la suma crece en línea recta con el factor")
    void parte2_lineal() {
        // cada par cruza una cantidad fija de filas y columnas vacías, así que su distancia es
        // (distancia sin expandir) + (vacías cruzadas) · (factor − 1): una recta en el factor
        Grilla imagen = Grilla.desdeLineas(EJEMPLO);
        long base = Dia11.sumaDeDistancias(imagen, 1);
        long pendiente = Dia11.sumaDeDistancias(imagen, 2) - base;
        assertEquals(292, base);
        assertEquals(82, pendiente);
        assertEquals(base + pendiente * 999_999, Dia11.sumaDeDistancias(imagen, 1_000_000));
        assertEquals(String.valueOf(base + pendiente * 999_999), dia11.parte2(EJEMPLO));
    }
}
