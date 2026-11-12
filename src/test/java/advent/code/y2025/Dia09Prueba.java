package advent.code.y2025;

import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 9 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 9: Sala de Cine")
class Dia09Prueba {

    private Dia09 dia09;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "7,1",
            "11,1",
            "11,7",
            "9,7",
            "9,5",
            "2,5",
            "2,3",
            "7,3"
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
        assertEquals("50", dia09.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las áreas de los rectángulos que muestra el enunciado")
    void parte1_areasDelEnunciado() {
        assertEquals(24, Dia09.area(new Punto(2, 5), new Punto(9, 7)));
        assertEquals(35, Dia09.area(new Punto(7, 1), new Punto(11, 7)));
        assertEquals(6, Dia09.area(new Punto(7, 3), new Punto(2, 3)));    // rectángulo de una fila
        assertEquals(50, Dia09.area(new Punto(2, 5), new Punto(11, 1)));
    }

    @Test
    @DisplayName("Parte 1: el área no depende del orden de las esquinas y no se desborda")
    void parte1_areaSimetricaYGrande() {
        Punto a = new Punto(1_000, 98_000);
        Punto b = new Punto(99_000, 2_000);
        assertEquals(Dia09.area(a, b), Dia09.area(b, a));
        assertEquals(98_001L * 96_001L, Dia09.area(a, b));   // > Integer.MAX_VALUE
        assertEquals(1, Dia09.area(a, a));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("24", dia09.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los rectángulos que muestra el enunciado están adentro; el de la parte 1 no")
    void parte2_rectangulosDelEnunciado() {
        Dia09.PisoComprimido piso = new Dia09.PisoComprimido(Dia09.leerBaldosas(EJEMPLO));
        assertTrue(piso.rectanguloAdentro(new Punto(7, 3), new Punto(11, 1)));    // área 15
        assertTrue(piso.rectanguloAdentro(new Punto(9, 7), new Punto(9, 5)));     // área 3
        assertTrue(piso.rectanguloAdentro(new Punto(9, 5), new Punto(2, 3)));     // área 24
        assertFalse(piso.rectanguloAdentro(new Punto(2, 5), new Punto(11, 1)));   // el de 50 de la parte 1
        assertFalse(piso.rectanguloAdentro(new Punto(7, 1), new Punto(2, 5)));
    }

    @Test
    @DisplayName("Parte 2: un polígono rectangular completo permite el rectángulo entero")
    void parte2_rectangulo() {
        assertEquals("30", dia09.parte2(List.of("1,1", "6,1", "6,5", "1,5")));    // 6 × 5
    }

    @Test
    @DisplayName("Parte 2: una muesca de ancho cero (lados a distancia 1) no deja baldosas afuera")
    void parte2_muescaAnchoCero() {
        // Forma de U cuya muesca entre x=3 y x=4 no tiene ninguna baldosa en medio:
        // todas las baldosas del rectángulo 0..7 × 0..8 son rojas o verdes
        List<String> u = List.of("0,0", "3,0", "3,5", "4,5", "4,0", "7,0", "7,8", "0,8");
        assertEquals("72", dia09.parte2(u));
    }

    @Test
    @DisplayName("Parte 2: una muesca con baldosas afuera sí limita el rectángulo")
    void parte2_muescaConBaldosas() {
        // Igual que la U anterior pero con la muesca entre x=3 y x=5 (la columna 4 queda afuera)
        List<String> u = List.of("0,0", "3,0", "3,5", "5,5", "5,0", "8,0", "8,8", "0,8");
        // El mayor rectángulo válido es 0..8 × 5..8 = 9 · 4 = 36
        assertEquals("36", dia09.parte2(u));
    }
}
