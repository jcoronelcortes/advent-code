package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 1 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 1: Calibración del Trebuchet")
class Dia01Prueba {

    private Dia01 dia01;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "1abc2",
            "pqr3stu8vwx",
            "a1b2c3d4e5f",
            "treb7uchet"
    );

    @BeforeEach
    void configurar() {
        dia01 = new Dia01();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("142", dia01.parte1(EJEMPLO));
    }

    @ParameterizedTest(name = "{0} vale {1}")
    @CsvSource({
            "1abc2,       12",
            "pqr3stu8vwx, 38",
            "a1b2c3d4e5f, 15",
            "treb7uchet,  77"
    })
    @DisplayName("Parte 1: el valor de cada línea del ejemplo (con una sola cifra, se repite)")
    void parte1_cadaLinea(String linea, int valor) {
        assertEquals(valor, Dia01.valor(linea, Dia01.CIFRAS));
    }

    @Test
    @DisplayName("Parte 1: una línea sin cifras falla con un mensaje claro")
    void parte1_sinCifras() {
        assertThrows(IllegalArgumentException.class, () -> Dia01.valor("abc", Dia01.CIFRAS));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2, con cifras escritas en palabras. */
    private static final List<String> EJEMPLO_2 = List.of(
            "two1nine",
            "eightwothree",
            "abcone2threexyz",
            "xtwone3four",
            "4nineeightseven2",
            "zoneight234",
            "7pqrstsixteen"
    );

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("281", dia01.parte2(EJEMPLO_2));
    }

    @ParameterizedTest(name = "{0} vale {1}")
    @CsvSource({
            "two1nine,         29",
            "eightwothree,     83",
            "abcone2threexyz,  13",
            "xtwone3four,      24",
            "4nineeightseven2, 42",
            "zoneight234,      14",
            "7pqrstsixteen,    76"
    })
    @DisplayName("Parte 2: el valor de cada línea del ejemplo")
    void parte2_cadaLinea(String linea, int valor) {
        assertEquals(valor, Dia01.valor(linea, Dia01.CIFRAS_Y_PALABRAS));
    }

    @ParameterizedTest(name = "{0} vale {1}")
    @CsvSource({
            "oneight,   18",     // la última cifra comparte la 'e' con la primera
            "twone,     21",
            "eighthree, 83",
            "sevenine,  79"
    })
    @DisplayName("Parte 2: palabras que comparten letras cuentan las dos")
    void parte2_palabrasSolapadas(String linea, int valor) {
        assertEquals(valor, Dia01.valor(linea, Dia01.CIFRAS_Y_PALABRAS));
    }

    @Test
    @DisplayName("Parte 2: la parte 1 no cuenta las palabras")
    void parte2_parte1IgnoraPalabras() {
        assertEquals(11, Dia01.valor("one1two", Dia01.CIFRAS));
        assertEquals(12, Dia01.valor("one1two", Dia01.CIFRAS_Y_PALABRAS));
    }
}
