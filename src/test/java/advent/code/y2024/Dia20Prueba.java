package advent.code.y2024;

import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 20 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.
 * El ejemplo no tiene trampas que ahorren 100 picosegundos, así que se
 * comprueba la cantidad de trampas por cada ahorro.</p>
 */
@DisplayName("Año 2024 — Día 20: Condición de Carrera")
class Dia20Prueba {

    private Dia20 dia20;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "###############",
            "#...#...#.....#",
            "#.#.#.#.#.###.#",
            "#S#...#.#.#...#",
            "#######.#.#.###",
            "#######.#.#...#",
            "#######.#.###.#",
            "###..E#...#...#",
            "###.#######.###",
            "#...###...#...#",
            "#.#####.#.###.#",
            "#.#...#.#.#...#",
            "#.#.#.#.#.#.###",
            "#...#...#...###",
            "###############"
    );

    private static Dia20.Pista pistaDelEjemplo() {
        return Dia20.Pista.desde(Grilla.desdeLineas(EJEMPLO));
    }

    @BeforeEach
    void configurar() {
        dia20 = new Dia20();
    }

    /**
     * Fuerza bruta independiente: sacar cada pared (de a una) y volver a medir
     * la carrera con una BFS. Una trampa de 2 picosegundos que ahorra algo
     * atraviesa exactamente una pared, así que el mejor ahorro posible es el
     * de la mejor pared. (No sirve para contar trampas: una misma pared puede
     * usarse en varias.)
     */
    private static int mejorAhorroSacandoUnaPared(List<String> lineas) {
        Grilla mapa = Grilla.desdeLineas(lineas);
        int sinTrampa = Dia20.Pista.desde(mapa).tiempoSinTrampa();
        int mejor = 0;
        for (var pared : mapa.buscarTodos(Dia20.PARED)) {
            Grilla sinPared = mapa.copiar();
            sinPared.establecer(pared, '.');
            mejor = Math.max(mejor, sinTrampa - Dia20.Pista.desde(sinPared).tiempoSinTrampa());
        }
        return mejor;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: sin trampas, la carrera del ejemplo tarda 84 picosegundos")
    void parte1_tiempoSinTrampa() {
        assertEquals(84, pistaDelEjemplo().tiempoSinTrampa());
    }

    @Test
    @DisplayName("Parte 1: la cantidad de trampas por cada ahorro, como en la lista del enunciado")
    void parte1_ahorrosDelEjemplo() {
        Map<Integer, Long> esperado = new TreeMap<>(Map.ofEntries(
                Map.entry(2, 14L), Map.entry(4, 14L), Map.entry(6, 2L), Map.entry(8, 4L),
                Map.entry(10, 2L), Map.entry(12, 3L), Map.entry(20, 1L), Map.entry(36, 1L),
                Map.entry(38, 1L), Map.entry(40, 1L), Map.entry(64, 1L)));
        assertEquals(esperado, pistaDelEjemplo().ahorrosPorTrampa(Dia20.DURACION_PARTE1));
    }

    @Test
    @DisplayName("Parte 1: contar con un ahorro mínimo suma las cantidades de la lista")
    void parte1_contarTrampas() {
        Dia20.Pista pista = pistaDelEjemplo();
        assertEquals(4, pista.contarTrampas(Dia20.DURACION_PARTE1, 21));    // 36, 38, 40 y 64 (20 no)
        assertEquals(44, pista.contarTrampas(Dia20.DURACION_PARTE1, 1));    // todas
        assertEquals(0, pista.contarTrampas(Dia20.DURACION_PARTE1, 100));   // el ejemplo no tiene ninguna
    }

    @Test
    @DisplayName("Parte 1: el mejor ahorro coincide con sacar cada pared y volver a medir la carrera")
    void parte1_fuerzaBrutaSacandoParedes() {
        int mejor = pistaDelEjemplo().ahorrosPorTrampa(Dia20.DURACION_PARTE1).keySet().stream()
                .mapToInt(Integer::intValue).max().orElse(0);
        assertEquals(64, mejor);
        assertEquals(mejorAhorroSacandoUnaPared(EJEMPLO), mejor);
    }

    @Test
    @DisplayName("Parte 1: si la llegada es inalcanzable, falla con un mensaje claro")
    void parte1_llegadaInalcanzable() {
        assertThrows(IllegalArgumentException.class,
                () -> Dia20.Pista.desde(Grilla.desdeLineas(List.of("#####", "#S#E#", "#####"))));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: las trampas de hasta 20 ps que ahorran 50 o más, como en la lista del enunciado")
    void parte2_ahorrosDelEjemplo() {
        Map<Integer, Long> esperado = new TreeMap<>(Map.ofEntries(
                Map.entry(50, 32L), Map.entry(52, 31L), Map.entry(54, 29L), Map.entry(56, 39L),
                Map.entry(58, 25L), Map.entry(60, 23L), Map.entry(62, 20L), Map.entry(64, 19L),
                Map.entry(66, 12L), Map.entry(68, 14L), Map.entry(70, 12L), Map.entry(72, 22L),
                Map.entry(74, 4L), Map.entry(76, 3L)));
        assertEquals(esperado, pistaDelEjemplo().ahorrosPorTrampa(Dia20.DURACION_PARTE2).tailMap(50));
        assertEquals(285, pistaDelEjemplo().contarTrampas(Dia20.DURACION_PARTE2, 50));
    }

    @Test
    @DisplayName("Parte 2: la trampa de 6 ps del dibujo del enunciado, de S en (1,3) a (3,7), ahorra 76")
    void parte2_trampaDelDibujo() {
        Dia20.Pista pista = pistaDelEjemplo();
        // la trampa termina donde está el 6: dos columnas a la derecha y cuatro filas abajo de S
        int distancia = Math.abs(3 - 1) + Math.abs(7 - 3);
        assertEquals(6, distancia);
        int tiempo = pista.desdeSalida()[3][1] + distancia + pista.hastaLlegada()[7][3];
        assertEquals(76, pista.tiempoSinTrampa() - tiempo);
    }

    @Test
    @DisplayName("Parte 2: con duración 20 también se cuentan todas las trampas de la parte 1")
    void parte2_incluyeLasDeLaParte1() {
        Dia20.Pista pista = pistaDelEjemplo();
        for (int ahorroMinimo : new int[] {1, 10, 50}) {
            assertTrue(pista.contarTrampas(Dia20.DURACION_PARTE2, ahorroMinimo)
                    >= pista.contarTrampas(Dia20.DURACION_PARTE1, ahorroMinimo));
        }
    }
}
