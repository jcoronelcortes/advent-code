package advent.code.y2024;

import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 10 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 10: A Pie")
class Dia10Prueba {

    private Dia10 dia10;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "89010123",
            "78121874",
            "87430965",
            "96549874",
            "45678903",
            "32019012",
            "01329801",
            "10456732"
    );

    @BeforeEach
    void configurar() {
        dia10 = new Dia10();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("36", dia10.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los 9 inicios del ejemplo, en orden de lectura, tienen puntajes 5, 6, 5, 3, 1, 3, 5, 3 y 5")
    void parte1_puntajesDelEjemplo() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        assertEquals(List.of(5L, 6L, 5L, 3L, 1L, 3L, 5L, 3L, 5L),
                mapa.buscarTodos('0').stream().map(inicio -> Dia10.puntaje(mapa, inicio)).toList());
    }

    @Test
    @DisplayName("Parte 1: los ejemplos chicos del enunciado (con '.' intransitables)")
    void parte1_ejemplosChicos() {
        assertEquals("1", dia10.parte1(List.of("0123", "1234", "8765", "9876")));
        assertEquals("2", dia10.parte1(List.of(
                "...0...", "...1...", "...2...", "6543456", "7.....7", "8.....8", "9.....9")));
        assertEquals("4", dia10.parte1(List.of(
                "..90..9", "...1.98", "...2..7", "6543456", "765.987", "876....", "987....")));
        assertEquals("3", dia10.parte1(List.of(   // dos inicios: puntajes 1 y 2
                "10..9..", "2...8..", "3...7..", "4567654", "...8..3", "...9..2", ".....01")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("81", dia10.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los 9 inicios del ejemplo, en orden de lectura, tienen calificaciones 20, 24, 10, 4, 1, 4, 5, 8 y 5")
    void parte2_calificacionesDelEjemplo() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        var senderos = Dia10.senderosHastaCima(mapa);
        assertEquals(List.of(20L, 24L, 10L, 4L, 1L, 4L, 5L, 8L, 5L),
                mapa.buscarTodos('0').stream().map(senderos::calcular).toList());
    }

    @Test
    @DisplayName("Parte 2: los ejemplos chicos del enunciado (calificaciones 3, 13 y 227)")
    void parte2_ejemplosChicos() {
        assertEquals("3", dia10.parte2(List.of(
                ".....0.", "..4321.", "..5..2.", "..6543.", "..7..4.", "..8765.", "..9....")));
        assertEquals("13", dia10.parte2(List.of(
                "..90..9", "...1.98", "...2..7", "6543456", "765.987", "876....", "987....")));
        assertEquals("227", dia10.parte2(List.of(
                "012345", "123456", "234567", "345678", "4.6789", "56789.")));
    }
}
