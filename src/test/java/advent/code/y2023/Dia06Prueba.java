package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 6 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 6: Espera y Verás")
class Dia06Prueba {

    private Dia06 dia06;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "Time:      7  15   30",
            "Distance:  9  40  200"
    );

    @BeforeEach
    void configurar() {
        dia06 = new Dia06();
    }

    /** Fuerza bruta: probar cada cantidad de milisegundos de 0 a T. */
    private static long formasPorFuerzaBruta(long tiempo, long record) {
        return LongStream.rangeClosed(0, tiempo).filter(h -> h * (tiempo - h) > record).count();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("288", dia06.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las distancias de la carrera de 7 ms, como en el enunciado")
    void parte1_distancias() {
        assertEquals(List.of(0L, 6L, 10L, 12L, 12L, 10L, 6L, 0L),
                LongStream.rangeClosed(0, 7).map(h -> Dia06.distancia(h, 7)).boxed().toList());
    }

    @ParameterizedTest(name = "carrera de {0} ms con récord {1}: {2} formas")
    @CsvSource({
            "7,  9,   4",
            "15, 40,  8",
            "30, 200, 9"     // con 10 ms empata en 200, y empatar no alcanza
    })
    @DisplayName("Parte 1: formas de ganar cada carrera del ejemplo")
    void parte1_cadaCarrera(long tiempo, long record, long formas) {
        assertEquals(formas, Dia06.formasDeGanar(tiempo, record));
    }

    @Test
    @DisplayName("Parte 1: coincide con la fuerza bruta en todas las carreras de hasta 60 ms y todos los récords posibles")
    void parte1_coincideConFuerzaBruta() {
        for (long tiempo = 0; tiempo <= 60; tiempo++) {
            for (long record = -1; record <= tiempo * tiempo / 4 + 1; record++) {   // con −1, todo h gana
                assertEquals(formasPorFuerzaBruta(tiempo, record), Dia06.formasDeGanar(tiempo, record),
                        "tiempo " + tiempo + ", récord " + record);
            }
        }
    }

    @Test
    @DisplayName("Parte 1: datos inválidos fallan con un mensaje claro")
    void parte1_datosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> Dia06.formasDeGanar(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> dia06.parte1(List.of("Time: 7 15")));
        assertThrows(IllegalArgumentException.class, () -> dia06.parte1(List.of("Time: 7 15", "Distance: 9")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado (de 14 a 71516 ms: 71503 formas)")
    void parte2_ejemploDelEnunciado() {
        assertEquals("71503", dia06.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las cifras de cada línea se juntan en un solo número")
    void parte2_juntarCifras() {
        assertEquals(71530, Dia06.juntarCifras(EJEMPLO.get(0)));
        assertEquals(940200, Dia06.juntarCifras(EJEMPLO.get(1)));
        assertThrows(IllegalArgumentException.class, () -> Dia06.juntarCifras("Time:"));
    }

    @Test
    @DisplayName("Parte 2: en el ejemplo coincide con la fuerza bruta, y 13 y 71517 ms no alcanzan")
    void parte2_fuerzaBruta() {
        assertEquals(formasPorFuerzaBruta(71530, 940200), Dia06.formasDeGanar(71530, 940200));
        assertTrue(Dia06.distancia(13, 71530) <= 940200);
        assertTrue(Dia06.distancia(14, 71530) > 940200);
        assertTrue(Dia06.distancia(71516, 71530) > 940200);
        assertTrue(Dia06.distancia(71517, 71530) <= 940200);
    }
}
