package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 4 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 4: Departamento de Impresión")
class Dia04Prueba {

    private Dia04 dia04;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "..@@.@@@@.",
            "@@@.@.@.@@",
            "@@@@@.@.@@",
            "@.@@@@..@.",
            "@@.@@@@.@@",
            ".@@@@@@@.@",
            ".@.@.@.@@@",
            "@.@@@.@@@@",
            ".@@@@@@@@.",
            "@.@.@@@.@."
    );

    /** Ejemplo con los rollos accesibles marcados con {@code x}, según el enunciado. */
    private static final List<String> EJEMPLO_MARCADO = List.of(
            "..xx.xx@x.",
            "x@@.@.@.@@",
            "@@@@@.x.@@",
            "@.@@@@..@.",
            "x@.@@@@.@x",
            ".@@@@@@@.@",
            ".@.@.@.@@@",
            "x.@@@.@@@@",
            ".@@@@@@@@.",
            "x.x.@@@.x."
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
    @DisplayName("Parte 1: los rollos accesibles son exactamente los marcados en el enunciado")
    void parte1_posicionesAccesibles() {
        Set<Punto> esperados = new HashSet<>(Grilla.desdeLineas(EJEMPLO_MARCADO).buscarTodos('x'));
        Set<Punto> obtenidos = new HashSet<>(Dia04.accesibles(Grilla.desdeLineas(EJEMPLO)));
        assertEquals(esperados, obtenidos);
    }

    @Test
    @DisplayName("Parte 1: un rollo aislado y las esquinas son accesibles")
    void parte1_casosBorde() {
        assertEquals("1", dia04.parte1(List.of("@")));
        // Bloque 3x3 lleno: las 4 esquinas tienen 3 vecinos; bordes y centro tienen 5 u 8
        assertEquals("4", dia04.parte1(List.of("@@@", "@@@", "@@@")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("43", dia04.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: retira por rondas 13, 12, 7, 5, 2, 1, 1, 1, 1 como el enunciado")
    void parte2_rondasDelEnunciado() {
        Grilla grilla = Grilla.desdeLineas(EJEMPLO);
        List<Integer> rondas = new ArrayList<>();
        List<Punto> accesibles;
        while (!(accesibles = Dia04.accesibles(grilla)).isEmpty()) {
            rondas.add(accesibles.size());
            accesibles.forEach(rollo -> grilla.establecer(rollo, '.'));
        }
        assertEquals(List.of(13, 12, 7, 5, 2, 1, 1, 1, 1), rondas);
    }

    @Test
    @DisplayName("Parte 2: en un bloque 3x3 lleno se retiran todos los rollos")
    void parte2_bloqueLleno() {
        // Tras sacar las esquinas, los bordes quedan con 3 vecinos y el centro con 4
        assertEquals("9", dia04.parte2(List.of("@@@", "@@@", "@@@")));
    }
}
