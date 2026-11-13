package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 2 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 2: Enigma de los Cubos")
class Dia02Prueba {

    private Dia02 dia02;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green",
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue",
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red",
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red",
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
    );

    @BeforeEach
    void configurar() {
        dia02 = new Dia02();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("8", dia02.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: la partida 1 tiene tres puñados, como describe el enunciado")
    void parte1_parsear() {
        Dia02.Partida partida = Dia02.Partida.parsear(EJEMPLO.getFirst());
        assertEquals(1, partida.numero());
        assertEquals(List.of(new Dia02.Cubos(4, 0, 3), new Dia02.Cubos(1, 2, 6), new Dia02.Cubos(0, 2, 0)),
                partida.punados());
    }

    @Test
    @DisplayName("Parte 1: las partidas 1, 2 y 5 son posibles; la 3 (20 rojos) y la 4 (15 azules), no")
    void parte1_cadaPartida() {
        List<Boolean> posibles = Dia02.partidas(EJEMPLO).stream()
                .map(partida -> partida.posibleCon(Dia02.BOLSA))
                .toList();
        assertEquals(List.of(true, true, false, false, true), posibles);
    }

    @Test
    @DisplayName("Parte 1: el límite es inclusivo (12 rojos entran en una bolsa con 12)")
    void parte1_limiteInclusivo() {
        assertTrue(Dia02.Partida.parsear("Game 7: 12 red, 13 green, 14 blue").posibleCon(Dia02.BOLSA));
        assertFalse(Dia02.Partida.parsear("Game 7: 13 red").posibleCon(Dia02.BOLSA));
    }

    @Test
    @DisplayName("Parte 1: un color desconocido o una línea sin ':' fallan con un mensaje claro")
    void parte1_errores() {
        assertThrows(IllegalArgumentException.class, () -> Dia02.Partida.parsear("Game 1: 3 yellow"));
        assertThrows(IllegalArgumentException.class, () -> Dia02.Partida.parsear("Game 1 3 red"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("2286", dia02.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el mínimo de cada partida y su potencia, como en el enunciado")
    void parte2_minimosYPotencias() {
        List<Dia02.Partida> partidas = Dia02.partidas(EJEMPLO);
        assertEquals(List.of(new Dia02.Cubos(4, 2, 6), new Dia02.Cubos(1, 3, 4), new Dia02.Cubos(20, 13, 6),
                        new Dia02.Cubos(14, 3, 15), new Dia02.Cubos(6, 3, 2)),
                partidas.stream().map(Dia02.Partida::minimoNecesario).toList());
        assertEquals(List.of(48L, 12L, 1560L, 630L, 36L),
                partidas.stream().map(partida -> partida.minimoNecesario().potencia()).toList());
    }

    @Test
    @DisplayName("Parte 2: con un cubo menos de cualquier color, la partida deja de ser posible")
    void parte2_esElMinimo() {
        for (Dia02.Partida partida : Dia02.partidas(EJEMPLO)) {
            Dia02.Cubos minimo = partida.minimoNecesario();
            assertTrue(partida.posibleCon(minimo));
            assertFalse(partida.posibleCon(new Dia02.Cubos(minimo.rojos() - 1, minimo.verdes(), minimo.azules())));
            assertFalse(partida.posibleCon(new Dia02.Cubos(minimo.rojos(), minimo.verdes() - 1, minimo.azules())));
            assertFalse(partida.posibleCon(new Dia02.Cubos(minimo.rojos(), minimo.verdes(), minimo.azules() - 1)));
        }
    }

    @Test
    @DisplayName("Parte 2: un color que nunca aparece vale 0, y la potencia también")
    void parte2_colorAusente() {
        assertEquals(0, Dia02.Partida.parsear("Game 9: 3 red; 2 blue").minimoNecesario().potencia());
    }
}
