package advent.code.y2025;

import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 12 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 12: Granja de Árboles de Navidad")
class Dia12Prueba {

    private Dia12 dia12;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "0:", "###", "##.", "##.", "",
            "1:", "###", "##.", ".##", "",
            "2:", ".##", "###", "##.", "",
            "3:", "##.", "###", "##.", "",
            "4:", "###", "#..", "###", "",
            "5:", "###", ".#.", "###", "",
            "4x4: 0 0 0 0 2 0",
            "12x5: 1 0 1 0 2 2",
            "12x5: 1 0 1 0 3 2"
    );

    @BeforeEach
    void configurar() {
        dia12 = new Dia12();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("2", dia12.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: cada región del ejemplo, y que las cotas no la deciden (hace falta buscar)")
    void parte1_regionesDelEjemplo() {
        Dia12.Granja granja = Dia12.Granja.parsear(EJEMPLO);
        List<Dia12.Region> regiones = granja.regiones();
        assertEquals(List.of(true, true, false),
                regiones.stream().map(region -> Dia12.caben(granja.formas(), region)).toList());
        // Las dos de 12x5 tienen más regalos que bloques de 3x3 y sus celdas caben en el área
        assertEquals(Dia12.Decision.DUDOSO, Dia12.decidirConCotas(granja.formas(), regiones.get(1)));
        assertEquals(Dia12.Decision.DUDOSO, Dia12.decidirConCotas(granja.formas(), regiones.get(2)));
    }

    @Test
    @DisplayName("Parte 1: interpreta formas y regiones")
    void parte1_parseo() {
        Dia12.Granja granja = Dia12.Granja.parsear(EJEMPLO);
        assertEquals(6, granja.formas().size());
        assertEquals(List.of(7, 7, 7, 7, 7, 7), granja.formas().stream().map(Dia12.Forma::celdas).toList());
        assertEquals(new Dia12.Region(12, 5, List.of(1, 0, 1, 0, 3, 2)), granja.regiones().get(2));
    }

    @Test
    @DisplayName("Parte 1: orientaciones distintas de cada forma")
    void parte1_orientaciones() {
        // Simétrica respecto de un eje: 4 orientaciones (forma 4 del ejemplo, una "C")
        assertEquals(4, Dia12.Forma.desdeDibujo(List.of("###", "#..", "###")).orientaciones().size());
        // Totalmente simétrica: una sola
        assertEquals(1, Dia12.Forma.desdeDibujo(List.of("###", "###", "###")).orientaciones().size());
        // Sin simetrías: 8 (forma 1 del ejemplo)
        assertEquals(8, Dia12.Forma.desdeDibujo(List.of("###", "##.", ".##")).orientaciones().size());
        // Cada orientación empieza en (0, 0)
        Dia12.Forma.desdeDibujo(List.of(".##", "###", "##.")).orientaciones()
                .forEach(orientacion -> assertEquals(new Punto(0, 0), orientacion.getFirst()));
    }

    @Test
    @DisplayName("Parte 1: lado del cuadrado contenedor, aunque la primera celda esté al medio")
    void parte1_lado() {
        assertEquals(3, Dia12.Forma.desdeDibujo(List.of(".#.", "###")).lado());   // ancho 3, alto 2
        assertEquals(2, Dia12.Forma.desdeDibujo(List.of("##", "#.")).lado());
        assertEquals(1, Dia12.Forma.desdeDibujo(List.of("#")).lado());
    }

    @Test
    @DisplayName("Parte 1: cotas: demasiadas celdas no caben; un bloque por regalo siempre cabe")
    void parte1_cotas() {
        List<Dia12.Forma> formas = Dia12.Granja.parsear(EJEMPLO).formas();
        // 3 regalos de 7 celdas = 21 > 4x5 = 20
        assertEquals(Dia12.Decision.NO_CABEN, Dia12.decidirConCotas(formas, new Dia12.Region(4, 5, List.of(3, 0, 0, 0, 0, 0))));
        // 6x6 tiene 4 bloques de 3x3: 4 regalos caben seguro
        assertEquals(Dia12.Decision.CABEN, Dia12.decidirConCotas(formas, new Dia12.Region(6, 6, List.of(1, 1, 1, 1, 0, 0))));
    }

    @Test
    @DisplayName("Parte 1: la búsqueda coincide con las cotas cuando estas deciden")
    void parte1_busquedaCoincideConCotas() {
        List<Dia12.Forma> formas = Dia12.Granja.parsear(EJEMPLO).formas();
        Dia12.Region caben = new Dia12.Region(6, 6, List.of(1, 1, 1, 1, 0, 0));
        Dia12.Region noCaben = new Dia12.Region(4, 5, List.of(3, 0, 0, 0, 0, 0));
        assertTrue(Dia12.buscarAcomodo(formas, caben));
        assertFalse(Dia12.buscarAcomodo(formas, noCaben));
    }

    @Test
    @DisplayName("Parte 1: piezas que encajan de forma ajustada")
    void parte1_encajeExacto() {
        // Dos "C" (forma 4) encajan en un 4x4 dejando 2 celdas vacías (el acomodo del enunciado)
        List<Dia12.Forma> formas = Dia12.Granja.parsear(EJEMPLO).formas();
        assertTrue(Dia12.buscarAcomodo(formas, new Dia12.Region(4, 4, List.of(0, 0, 0, 0, 2, 0))));
        // Un 3x4 tiene 12 celdas: dos "C" de 7 suman 14 y no caben
        assertFalse(Dia12.buscarAcomodo(formas, new Dia12.Region(3, 4, List.of(0, 0, 0, 0, 2, 0))));
        // Dominós de 2 celdas llenan exactamente un 2x3 (sin celdas vacías)
        List<Dia12.Forma> domino = List.of(Dia12.Forma.desdeDibujo(List.of("##")));
        assertTrue(Dia12.buscarAcomodo(domino, new Dia12.Region(2, 3, List.of(3))));
        // ...pero un 3x3 con 4 dominós (8 de 9 celdas) también, y con 5 no
        assertTrue(Dia12.buscarAcomodo(domino, new Dia12.Region(3, 3, List.of(4))));
        assertFalse(Dia12.buscarAcomodo(domino, new Dia12.Region(3, 3, List.of(5))));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el último día no tiene segundo problema")
    void parte2_sinProblema() {
        assertEquals(Dia12.SIN_PARTE_2, dia12.parte2(EJEMPLO));
    }
}
