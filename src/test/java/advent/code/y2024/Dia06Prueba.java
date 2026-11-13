package advent.code.y2024;

import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 6 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 6: Patrulla del Guardia")
class Dia06Prueba {

    private Dia06 dia06;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "....#.....",
            ".........#",
            "..........",
            "..#.......",
            ".......#..",
            "..........",
            ".#..^.....",
            "........#.",
            "#.........",
            "......#..."
    );

    @BeforeEach
    void configurar() {
        dia06 = new Dia06();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("41", dia06.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el recorrido del ejemplo pasa por las posiciones marcadas con X en el enunciado")
    void parte1_recorridoDelEjemplo() {
        List<String> marcado = List.of(
                "....#.....",
                "....XXXXX#",
                "....X...X.",
                "..#.X...X.",
                "..XXXXX#X.",
                "..X.X.X.X.",
                ".#XXXXXXX.",
                ".XXXXXXX#.",
                "#XXXXXXX..",
                "......#X..");
        Set<Punto> esperadas = new HashSet<>(Grilla.desdeLineas(marcado).buscarTodos('X'));
        assertEquals(esperadas, Dia06.patrullar(Grilla.desdeLineas(EJEMPLO)).visitadas());
    }

    @Test
    @DisplayName("Parte 1: sin obstáculos sale derecho; un obstáculo enfrente lo hace girar")
    void parte1_casosSimples() {
        assertEquals("3", dia06.parte1(List.of(".", ".", "^")));              // sube 2 y sale
        assertEquals("3", dia06.parte1(List.of("#..", "^..")));               // gira a la derecha y sale
        assertEquals("2", dia06.parte1(List.of(".#.", "#^#", "...")));         // gira dos veces, baja y sale
    }

    @Test
    @DisplayName("Parte 1: detecta cuando el guardia queda en un ciclo")
    void parte1_ciclo() {
        List<String> encerrado = List.of(
                ".#..",
                "...#",
                "#^..",
                "..#.");
        assertTrue(Dia06.patrullar(Grilla.desdeLineas(encerrado)).enCiclo());
        assertThrows(IllegalStateException.class, () -> dia06.parte1(encerrado));
        assertFalse(Dia06.patrullar(Grilla.desdeLineas(EJEMPLO)).enCiclo());
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("6", dia06.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las 6 posiciones del ejemplo son las que muestra el enunciado")
    void parte2_posicionesDelEjemplo() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        // Las O de las seis opciones del enunciado (x = columna, y = fila)
        Set<Punto> esperadas = Set.of(new Punto(3, 6), new Punto(6, 7), new Punto(7, 7),
                new Punto(1, 8), new Punto(3, 8), new Punto(7, 9));
        for (Punto candidata : esperadas) {
            assertTrue(Dia06.patrullar(mapa, candidata).enCiclo(), "debería haber ciclo con " + candidata);
        }
    }

    @Test
    @DisplayName("Parte 2: probar solo el recorrido original da lo mismo que probar todas las celdas libres")
    void parte2_soloElRecorridoAlcanza() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        Punto inicio = mapa.buscar('^').orElseThrow();
        long probandoTodas = mapa.buscarTodos('.').stream()
                .filter(candidata -> Dia06.patrullar(mapa, candidata).enCiclo())
                .count();
        assertEquals(String.valueOf(probandoTodas), dia06.parte2(EJEMPLO));
        // Un obstáculo fuera del recorrido no cambia nada
        assertEquals(Dia06.patrullar(mapa).visitadas(), Dia06.patrullar(mapa, new Punto(0, 0)).visitadas());
        assertFalse(Dia06.patrullar(mapa).visitadas().contains(new Punto(0, 0)));
        assertFalse(mapa.buscarTodos('.').contains(inicio));
    }

    @Test
    @DisplayName("Parte 2: el detector rápido coincide con patrullar para cada celda del recorrido del ejemplo")
    void parte2_detectorCoincideConPatrullar() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        Dia06.DetectorDeCiclos detector = new Dia06.DetectorDeCiclos(mapa);
        Map<Punto, Dia06.Estado> antes = Dia06.antesDeEntrar(mapa);
        assertFalse(antes.isEmpty());
        antes.forEach((candidata, estado) -> assertEquals(
                Dia06.patrullar(mapa, candidata).enCiclo(), detector.quedaAtrapado(estado, candidata),
                "obstáculo en " + candidata));
    }

    @Test
    @DisplayName("Parte 2: antesDeEntrar cubre todo el recorrido original salvo la posición inicial")
    void parte2_antesDeEntrarCubreElRecorrido() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        Set<Punto> esperadas = new HashSet<>(Dia06.patrullar(mapa).visitadas());
        esperadas.remove(mapa.buscarUnico('^'));
        assertEquals(esperadas, Dia06.antesDeEntrar(mapa).keySet());
    }

    @Test
    @DisplayName("Parte 2: el detector reutilizado entre pruebas no arrastra estados de la anterior")
    void parte2_detectorReutilizable() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        Dia06.DetectorDeCiclos detector = new Dia06.DetectorDeCiclos(mapa);
        Map<Punto, Dia06.Estado> antes = Dia06.antesDeEntrar(mapa);
        Punto conCiclo = new Punto(3, 6);   // primera 'O' de los diagramas del enunciado
        Punto sinCiclo = antes.keySet().stream().filter(c -> !Dia06.patrullar(mapa, c).enCiclo()).findFirst().orElseThrow();
        for (int i = 0; i < 3; i++) {
            assertTrue(detector.quedaAtrapado(antes.get(conCiclo), conCiclo));
            assertFalse(detector.quedaAtrapado(antes.get(sinCiclo), sinCiclo));
        }
    }
}
