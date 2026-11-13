package advent.code.y2023;

import advent.code.Utiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 9 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 9: Mantenimiento del Espejismo")
class Dia09Prueba {

    private Dia09 dia09;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "0 3 6 9 12 15",
            "1 3 6 10 15 21",
            "10 13 16 21 30 45"
    );

    @BeforeEach
    void configurar() {
        dia09 = new Dia09();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("114", dia09.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el siguiente de cada historial del ejemplo (18, 28 y 68)")
    void parte1_cadaHistorial() {
        assertEquals(18, Dia09.siguiente(List.of(0L, 3L, 6L, 9L, 12L, 15L)));
        assertEquals(28, Dia09.siguiente(List.of(1L, 3L, 6L, 10L, 15L, 21L)));
        assertEquals(68, Dia09.siguiente(List.of(10L, 13L, 16L, 21L, 30L, 45L)));
    }

    @Test
    @DisplayName("Parte 1: la pirámide de diferencias del tercer historial, como en el enunciado")
    void parte1_piramide() {
        List<List<Long>> piramide = new ArrayList<>(List.of(List.of(10L, 13L, 16L, 21L, 30L, 45L)));
        while (!piramide.getLast().stream().allMatch(valor -> valor == 0)) {
            piramide.add(Dia09.diferencias(piramide.getLast()));
        }
        assertEquals(List.of(
                List.of(10L, 13L, 16L, 21L, 30L, 45L),
                List.of(3L, 3L, 5L, 9L, 15L),
                List.of(0L, 2L, 4L, 6L),
                List.of(2L, 2L, 2L),
                List.of(0L, 0L)), piramide);
    }

    @Test
    @DisplayName("Parte 1: un polinomio de grado 3 (con negativos) se predice exactamente")
    void parte1_polinomio() {
        // p(n) = n³ − 4n² − 7, con n = 0..7; el siguiente es p(8)
        List<Long> valores = new ArrayList<>();
        for (long n = 0; n <= 7; n++) valores.add(n * n * n - 4 * n * n - 7);
        assertEquals(8L * 8 * 8 - 4 * 8 * 8 - 7, Dia09.siguiente(valores));
    }

    @Test
    @DisplayName("Parte 1: una secuencia que no llega a ceros falla con un mensaje claro")
    void parte1_sinCeros() {
        assertThrows(IllegalArgumentException.class, () -> Dia09.siguiente(List.of(1L, 2L, 4L)));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("2", dia09.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el anterior de cada historial del ejemplo (−3, 0 y 5)")
    void parte2_cadaHistorial() {
        assertEquals(-3, Dia09.anterior(List.of(0L, 3L, 6L, 9L, 12L, 15L)));
        assertEquals(0, Dia09.anterior(List.of(1L, 3L, 6L, 10L, 15L, 21L)));
        assertEquals(5, Dia09.anterior(List.of(10L, 13L, 16L, 21L, 30L, 45L)));
    }

    @Test
    @DisplayName("Parte 2: el anterior es el siguiente de la secuencia dada vuelta")
    void parte2_secuenciaAlReves() {
        for (String linea : EJEMPLO) {
            List<Long> valores = Utiles.leerNumerosLargos(linea);
            assertEquals(Dia09.siguiente(valores.reversed()), Dia09.anterior(valores), linea);
        }
    }

    @Test
    @DisplayName("Parte 2: un polinomio de grado 3 se extrapola hacia atrás exactamente")
    void parte2_polinomio() {
        // p(n) = n³ − 4n² − 7, con n = 1..8; el anterior es p(0) = −7
        List<Long> valores = new ArrayList<>();
        for (long n = 1; n <= 8; n++) valores.add(n * n * n - 4 * n * n - 7);
        assertEquals(-7, Dia09.anterior(valores));
    }
}
