package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 17 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 17: Computadora Cronoespacial")
class Dia17Prueba {

    private Dia17 dia17;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "Register A: 729",
            "Register B: 0",
            "Register C: 0",
            "",
            "Program: 0,1,5,4,3,0"
    );

    @BeforeEach
    void configurar() {
        dia17 = new Dia17();
    }

    private static Dia17.Computadora computadora(long a, long b, long c, Integer... programa) {
        return new Dia17.Computadora(a, b, c, List.of(programa));
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("4,6,3,5,6,3,5,2,1,0", dia17.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: con C = 9, el programa 2,6 deja B en 1")
    void parte1_bst() {
        Dia17.Computadora computadora = computadora(0, 0, 9, 2, 6);
        computadora.ejecutar();
        assertEquals(1, computadora.b());
    }

    @Test
    @DisplayName("Parte 1: con A = 10, el programa 5,0,5,1,5,4 emite 0,1,2")
    void parte1_out() {
        assertEquals("0,1,2", Dia17.unir(computadora(10, 0, 0, 5, 0, 5, 1, 5, 4).ejecutar()));
    }

    @Test
    @DisplayName("Parte 1: con A = 2024, el programa 0,1,5,4,3,0 emite 4,2,5,6,7,7,7,7,3,1,0 y deja A en 0")
    void parte1_advYSalto() {
        Dia17.Computadora computadora = computadora(2024, 0, 0, 0, 1, 5, 4, 3, 0);
        assertEquals("4,2,5,6,7,7,7,7,3,1,0", Dia17.unir(computadora.ejecutar()));
        assertEquals(0, computadora.a());
    }

    @Test
    @DisplayName("Parte 1: con B = 29, el programa 1,7 deja B en 26")
    void parte1_bxl() {
        Dia17.Computadora computadora = computadora(0, 29, 0, 1, 7);
        computadora.ejecutar();
        assertEquals(26, computadora.b());
    }

    @Test
    @DisplayName("Parte 1: con B = 2024 y C = 43690, el programa 4,0 deja B en 44354")
    void parte1_bxc() {
        Dia17.Computadora computadora = computadora(0, 2024, 43690, 4, 0);
        computadora.ejecutar();
        assertEquals(44354, computadora.b());
    }

    @Test
    @DisplayName("Parte 1: bdv y cdv dividen A pero guardan en B y C, sin tocar A")
    void parte1_bdvYCdv() {
        Dia17.Computadora computadora = computadora(100, 0, 0, 6, 2, 7, 3);   // B = 100 / 4, C = 100 / 8
        computadora.ejecutar();
        assertEquals(100, computadora.a());
        assertEquals(25, computadora.b());
        assertEquals(12, computadora.c());
    }

    @Test
    @DisplayName("Parte 1: dividir por 2 elevado a un exponente enorme da 0")
    void parte1_exponenteEnorme() {
        Dia17.Computadora computadora = computadora(1L << 62, 64, 0, 0, 5);   // A / 2^64
        computadora.ejecutar();
        assertEquals(0, computadora.a());
    }

    @Test
    @DisplayName("Parte 1: el operando combo 7 es inválido")
    void parte1_comboInvalido() {
        assertThrows(IllegalStateException.class, () -> computadora(0, 0, 0, 5, 7).ejecutar());
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2: un programa que se puede emitir a sí mismo. */
    private static final List<String> EJEMPLO_COPIA = List.of(
            "Register A: 2024",
            "Register B: 0",
            "Register C: 0",
            "",
            "Program: 0,3,5,4,3,0"
    );

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("117440", dia17.parte2(EJEMPLO_COPIA));
    }

    @Test
    @DisplayName("Parte 2: con A = 117440 el programa del ejemplo se emite a sí mismo")
    void parte2_laRespuestaSeCopia() {
        assertEquals("0,3,5,4,3,0", Dia17.unir(computadora(117440, 0, 0, 0, 3, 5, 4, 3, 0).ejecutar()));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar todos los valores de A desde 1 en el ejemplo")
    void parte2_fuerzaBrutaEnElEjemplo() {
        List<Integer> programa = List.of(0, 3, 5, 4, 3, 0);
        long a = 1;
        while (!computadora(a, 0, 0, 0, 3, 5, 4, 3, 0).ejecutar().equals(programa)) a++;
        assertEquals(String.valueOf(a), dia17.parte2(EJEMPLO_COPIA));
    }

    @Test
    @DisplayName("Parte 2: si ningún A sirve, falla con un mensaje claro")
    void parte2_sinSolucion() {
        // emite siempre 0 y termina: nunca puede emitirse a sí mismo (5,0,...)
        assertThrows(IllegalArgumentException.class, () -> dia17.parte2(List.of(
                "Register A: 0", "Register B: 0", "Register C: 0", "", "Program: 5,0,0,3,3,0")));
    }
}
