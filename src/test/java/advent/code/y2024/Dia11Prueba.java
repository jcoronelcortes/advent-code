package advent.code.y2024;

import advent.code.Utiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 11 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 11: Piedras Plutonianas")
class Dia11Prueba {

    private Dia11 dia11;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of("125 17");

    @BeforeEach
    void configurar() {
        dia11 = new Dia11();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("55312", dia11.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: un parpadeo sobre 0 1 10 99 999 aplica cada regla")
    void parte1_unParpadeoAplicaCadaRegla() {
        assertEquals(List.of(1L, 2024L, 1L, 0L, 9L, 9L, 2021976L),
                Dia11.parpadear(List.of(0L, 1L, 10L, 99L, 999L)));
    }

    @Test
    @DisplayName("Parte 1: al partir no se conservan ceros a la izquierda (1000 → 10 y 0)")
    void parte1_partirSinCerosALaIzquierda() {
        assertEquals(List.of(10L, 0L), Dia11.transformar(1000));
        assertEquals(List.of(100L, 7L), Dia11.transformar(100007));
    }

    @Test
    @DisplayName("Parte 1: los 6 parpadeos del ejemplo, paso a paso")
    void parte1_pasosDelEjemplo() {
        List<String> pasos = List.of(
                "253000 1 7",
                "253 0 2024 14168",
                "512072 1 20 24 28676032",
                "512 72 2024 2 0 2 4 2867 6032",
                "1036288 7 2 20 24 4048 1 4048 8096 28 67 60 32",
                "2097446912 14168 4048 2 0 2 4 40 48 2024 40 48 80 96 2 8 6 7 6 0 3 2");
        List<Long> fila = List.of(125L, 17L);
        for (String paso : pasos) {
            fila = Dia11.parpadear(fila);
            assertEquals(Utiles.leerNumerosLargos(paso), fila);
        }
    }

    @Test
    @DisplayName("Parte 1: contar por número coincide con simular la fila completa")
    void parte1_contarCoincideConSimular() {
        List<Long> fila = List.of(125L, 17L);
        for (int parpadeos = 0; parpadeos <= 25; parpadeos++) {
            assertEquals(fila.size(), Dia11.contarPiedras(List.of(125L, 17L), parpadeos));
            fila = Dia11.parpadear(fila);
        }
        assertEquals(22, Dia11.contarPiedras(List.of(125L, 17L), 6));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo con 75 parpadeos (el enunciado no da el valor; calculado con un script independiente)")
    void parte2_ejemplo() {
        assertEquals("65601038650482", dia11.parte2(EJEMPLO));
    }
}
