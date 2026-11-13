package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 4 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 4: Rasca y Gana")
class Dia04Prueba {

    private Dia04 dia04;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
            "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19",
            "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1",
            "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83",
            "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36",
            "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
    );

    @BeforeEach
    void configurar() {
        dia04 = new Dia04();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("13", dia04.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: coincidencias y puntos de cada tarjeta, como en el enunciado")
    void parte1_cadaTarjeta() {
        List<Dia04.Tarjeta> tarjetas = Dia04.tarjetas(EJEMPLO);
        assertEquals(List.of(4, 2, 2, 1, 0, 0), tarjetas.stream().map(Dia04.Tarjeta::coincidencias).toList());
        assertEquals(List.of(8L, 2L, 2L, 1L, 0L, 0L), tarjetas.stream().map(Dia04.Tarjeta::puntos).toList());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), tarjetas.stream().map(Dia04.Tarjeta::numero).toList());
    }

    @Test
    @DisplayName("Parte 1: con muchos espacios entre los números (como en el input real)")
    void parte1_espacios() {
        Dia04.Tarjeta tarjeta = Dia04.Tarjeta.parsear("Card   12:  9 39 |  9 80   39");
        assertEquals(12, tarjeta.numero());
        assertEquals(2, tarjeta.coincidencias());
    }

    @Test
    @DisplayName("Parte 1: una línea sin ':' o sin '|' falla con un mensaje claro")
    void parte1_lineaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Dia04.Tarjeta.parsear("Card 1: 1 2 3"));
        assertThrows(IllegalArgumentException.class, () -> Dia04.Tarjeta.parsear("Card 1 1 | 2"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("30", dia04.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: instancias de cada tarjeta del ejemplo (1, 2, 4, 8, 14 y 1), como en el enunciado")
    void parte2_instancias() {
        assertArrayEquals(new long[] {1, 2, 4, 8, 14, 1}, Dia04.instancias(Dia04.tarjetas(EJEMPLO)));
    }

    @Test
    @DisplayName("Parte 2: coincide con simular las copias una por una con una cola")
    void parte2_simulacion() {
        List<Dia04.Tarjeta> tarjetas = Dia04.tarjetas(EJEMPLO);
        Deque<Integer> pendientes = new ArrayDeque<>();
        for (int i = 0; i < tarjetas.size(); i++) pendientes.add(i);
        long total = 0;
        while (!pendientes.isEmpty()) {
            int i = pendientes.poll();
            total++;
            for (int j = 1; j <= tarjetas.get(i).coincidencias(); j++) pendientes.add(i + j);
        }
        assertEquals(String.valueOf(total), dia04.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: una tarjeta que ganaría copias más allá de la última falla con un mensaje claro")
    void parte2_masAllaDelFinal() {
        assertThrows(IllegalArgumentException.class, () -> dia04.parte2(List.of("Card 1: 1 2 | 1 2", "Card 2: 3 | 4")));
    }
}
