package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 3 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 3: Dale Vueltas")
class Dia03Prueba {

    private Dia03 dia03;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
    );

    @BeforeEach
    void configurar() {
        dia03 = new Dia03();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("161", dia03.parte1(EJEMPLO));   // 2*4 + 5*5 + 11*8 + 8*5
    }

    @Test
    @DisplayName("Parte 1: las instrucciones mal escritas del enunciado no cuentan")
    void parte1_instruccionesInvalidas() {
        assertEquals(0, Dia03.sumarMultiplicaciones("mul(4*"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul(6,9!"));
        assertEquals(0, Dia03.sumarMultiplicaciones("?(12,34)"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul ( 2 , 4 )"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul[3,7]"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul(32,64]"));
    }

    @Test
    @DisplayName("Parte 1: números de 1 a 3 dígitos; con 4 dígitos la instrucción no vale")
    void parte1_digitos() {
        assertEquals(2024, Dia03.sumarMultiplicaciones("mul(44,46)"));
        assertEquals(492, Dia03.sumarMultiplicaciones("mul(123,4)"));
        assertEquals(998001, Dia03.sumarMultiplicaciones("mul(999,999)"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul(1234,5)"));
        assertEquals(0, Dia03.sumarMultiplicaciones("mul(5,1234)"));
    }

    @Test
    @DisplayName("Parte 1: instrucciones pegadas, superpuestas con basura y en varias líneas")
    void parte1_variasLineas() {
        assertEquals(6 + 20, Dia03.sumarMultiplicaciones("mul(2,3)mul(4,5)"));
        assertEquals(6, Dia03.sumarMultiplicaciones("mulmul(2,3)"));   // basura justo antes
        assertEquals("16", dia03.parte1(List.of("mul(2,3)x", "ymul(5,2)")));
        // Una instrucción cortada por el salto de línea no cuenta
        assertEquals("0", dia03.parte1(List.of("mul(1", "2,3)")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2 (agrega don't() y do()). */
    private static final List<String> EJEMPLO_PARTE_2 = List.of(
            "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
    );

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("48", dia03.parte2(EJEMPLO_PARTE_2));   // 2*4 + 8*5
    }

    @Test
    @DisplayName("Parte 2: solo vale la última instrucción condicional; al comienzo está habilitado")
    void parte2_ultimaInstruccion() {
        assertEquals(6, Dia03.sumarMultiplicacionesHabilitadas("mul(2,3)"));
        assertEquals(0, Dia03.sumarMultiplicacionesHabilitadas("don't()don't()do()don't()mul(2,3)"));
        assertEquals(6, Dia03.sumarMultiplicacionesHabilitadas("don't()do()do()mul(2,3)"));
        // "undo()" contiene "do()": cuenta como do(), como en el ejemplo del enunciado
        assertEquals(6, Dia03.sumarMultiplicacionesHabilitadas("don't()undo()mul(2,3)"));
        // Variantes mal escritas no cambian el estado
        assertEquals(0, Dia03.sumarMultiplicacionesHabilitadas("don't()do( )dont()mul(2,3)"));
    }

    @Test
    @DisplayName("Parte 2: el estado sigue de una línea a la siguiente")
    void parte2_estadoEntreLineas() {
        assertEquals("6", dia03.parte2(List.of("mul(2,3)don't()", "mul(4,5)")));
        assertEquals("20", dia03.parte2(List.of("don't()mul(2,3)", "do()mul(4,5)")));
    }

    @Test
    @DisplayName("Parte 2: sin instrucciones condicionales coincide con la parte 1")
    void parte2_sinCondicionales() {
        assertEquals(dia03.parte1(EJEMPLO), dia03.parte2(EJEMPLO));
    }
}
