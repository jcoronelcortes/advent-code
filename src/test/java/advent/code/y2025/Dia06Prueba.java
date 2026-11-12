package advent.code.y2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 6 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 6: Compactador de Basura")
class Dia06Prueba {

    private Dia06 dia06;

    /**
     * Input del ejemplo del enunciado. Las líneas tienen distinto largo, igual
     * que en el input real (los espacios del final no vienen).
     */
    private static final List<String> EJEMPLO = List.of(
            "123 328  51 64 ",
            " 45 64  387 23 ",
            "  6 98  215 314",
            "*   +   *   +  "
    );

    @BeforeEach
    void configurar() {
        dia06 = new Dia06();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("4277556", dia06.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: cada problema del ejemplo da el resultado del enunciado")
    void parte1_problemasDelEjemplo() {
        List<Dia06.Problema> problemas = Dia06.leerProblemas(EJEMPLO);
        assertEquals(4, problemas.size());
        assertEquals(List.of(123L, 45L, 6L), problemas.get(0).numerosPorFila());
        assertEquals('*', problemas.get(0).operacion());
        assertEquals(List.of(33210L, 490L, 4243455L, 401L),
                problemas.stream().map(Dia06.Problema::resolver).toList());
    }

    @Test
    @DisplayName("Parte 1: funciona aunque las líneas tengan distinto largo")
    void parte1_lineasDeDistintoLargo() {
        List<String> recortadas = EJEMPLO.stream().map(String::stripTrailing).toList();
        assertEquals("4277556", dia06.parte1(recortadas));
    }

    @Test
    @DisplayName("aplicar: suma, producto, operación desconocida y desborde")
    void aplicar() {
        assertEquals(6, Dia06.aplicar('+', List.of(1L, 2L, 3L)));
        assertEquals(24, Dia06.aplicar('*', List.of(2L, 3L, 4L)));
        assertThrows(IllegalArgumentException.class, () -> Dia06.aplicar('-', List.of(1L)));
        assertThrows(ArithmeticException.class, () -> Dia06.aplicar('*', List.of(Long.MAX_VALUE, 2L)));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("3263827", dia06.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: cada problema leído por columnas, de derecha a izquierda, como el enunciado")
    void parte2_problemasDelEjemplo() {
        List<Dia06.Problema> problemas = Dia06.leerProblemas(EJEMPLO);
        // Mismo orden que el enunciado: "356 * 24 * 1", "8 + 248 + 369", ...
        assertEquals(List.of(356L, 24L, 1L), problemas.get(0).numerosPorColumna());
        assertEquals(List.of(8L, 248L, 369L), problemas.get(1).numerosPorColumna());
        assertEquals(List.of(175L, 581L, 32L), problemas.get(2).numerosPorColumna());
        assertEquals(List.of(4L, 431L, 623L), problemas.get(3).numerosPorColumna());
        assertEquals(List.of(8544L, 625L, 3253600L, 1058L),
                problemas.stream().map(p -> p.resolver(Dia06.Problema::numerosPorColumna)).toList());
    }

    @Test
    @DisplayName("Parte 2: funciona aunque las líneas tengan distinto largo")
    void parte2_lineasDeDistintoLargo() {
        List<String> recortadas = EJEMPLO.stream().map(String::stripTrailing).toList();
        assertEquals("3263827", dia06.parte2(recortadas));
    }
}
