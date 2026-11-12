package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Memoizador;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 7 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 7: Laboratorios")
class Dia07Prueba {

    private Dia07 dia07;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            ".......S.......",
            "...............",
            ".......^.......",
            "...............",
            "......^.^......",
            "...............",
            ".....^.^.^.....",
            "...............",
            "....^.^...^....",
            "...............",
            "...^.^...^.^...",
            "...............",
            "..^...^.....^..",
            "...............",
            ".^.^.^.^.^...^.",
            "..............."
    );

    @BeforeEach
    void configurar() {
        dia07 = new Dia07();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("21", dia07.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: dos divisores que vierten en la misma columna crean solo 3 haces")
    void parte1_hacesQueSeFunden() {
        // Las primeras 5 filas del ejemplo: 1 + 2 divisiones, y 3 columnas con haz
        Dia07.Resultado resultado = Dia07.simular(Grilla.desdeLineas(EJEMPLO.subList(0, 6)));
        assertEquals(3, resultado.divisiones());
        assertEquals(3, Arrays.stream(resultado.haces()).filter(caminos -> caminos > 0).count());
    }

    @Test
    @DisplayName("Parte 1: un divisor al que no llega ningún haz no cuenta")
    void parte1_divisorSinHaz() {
        assertEquals("1", dia07.parte1(List.of(
                "..S..",
                "..^..",
                "^...^",     // los haces caen en las columnas 1 y 3: estos divisores no se tocan
                ".....")));
    }

    @Test
    @DisplayName("Parte 1: sin divisores el haz baja derecho")
    void parte1_sinDivisores() {
        assertEquals("0", dia07.parte1(List.of(".S.", "...", "...")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("40", dia07.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los caminos que coinciden no se funden")
    void parte2_caminosQueCoinciden() {
        // Primeras filas del ejemplo: 4 líneas temporales (1 + 2 + 1 por columna)
        Dia07.Resultado resultado = Dia07.simular(Grilla.desdeLineas(EJEMPLO.subList(0, 6)));
        assertArrayEquals(new long[]{0, 0, 0, 0, 0, 1, 0, 2, 0, 1, 0, 0, 0, 0, 0}, resultado.haces());
        assertEquals("4", dia07.parte2(EJEMPLO.subList(0, 6)));
    }

    @Test
    @DisplayName("Parte 2: sin divisores hay una sola línea temporal; los haces que salen por el costado se pierden")
    void parte2_casosBorde() {
        assertEquals("1", dia07.parte2(List.of(".S.", "...", "...")));
        assertEquals("1", dia07.parte2(List.of("S..", "^..", "...")));   // el camino izquierdo sale de la grilla
    }

    @Test
    @DisplayName("Parte 2: coincide con contar caminos recursivamente con memoización")
    void parte2_coincideConRecursion() {
        // Diagrama de 30 x 31 con el patrón triangular del ejemplo (divisores en
        // filas impares, cada vez más anchos) y algunos divisores quitados para
        // romper la simetría: 7231 caminos
        int ancho = 31;
        int centro = 15;
        List<String> diagrama = new ArrayList<>();
        diagrama.add(".".repeat(centro) + "S" + ".".repeat(ancho - centro - 1));
        for (int fila = 1; fila < 30; fila++) {
            int nivel = (fila + 1) / 2;
            StringBuilder linea = new StringBuilder();
            for (int columna = 0; columna < ancho; columna++) {
                boolean divisor = fila % 2 == 1
                        && Math.abs(columna - centro) <= nivel - 1
                        && (columna - centro + nivel - 1) % 2 == 0
                        && (columna * 7 + fila * 3) % 11 != 0;
                linea.append(divisor ? '^' : '.');
            }
            diagrama.add(linea.toString());
        }
        Grilla grilla = Grilla.desdeLineas(diagrama);

        Memoizador<Punto, Long> caminos = new Memoizador<>((punto, propio) -> {
            if (!grilla.estaDentro(punto)) return 0L;
            if (punto.y() == grilla.filas() - 1) return 1L;
            Punto abajo = new Punto(punto.x(), punto.y() + 1);
            if (grilla.obtener(abajo) != '^') return propio.apply(abajo);
            return propio.apply(new Punto(punto.x() - 1, abajo.y()))
                    + propio.apply(new Punto(punto.x() + 1, abajo.y()));
        });
        long esperado = caminos.calcular(grilla.buscar('S').orElseThrow());

        assertEquals(7231, esperado, "el diagrama de prueba debería tener muchos caminos");
        assertEquals(String.valueOf(esperado), dia07.parte2(diagrama));
    }
}
