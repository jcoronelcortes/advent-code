package advent.code.y2024;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 2 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 2: Reportes del Reno de Nariz Roja")
class Dia02Prueba {

    private Dia02 dia02;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "7 6 4 2 1",
            "1 2 7 8 9",
            "9 7 6 2 1",
            "1 3 2 4 5",
            "8 6 4 4 1",
            "1 3 6 7 9"
    );

    @BeforeEach
    void configurar() {
        dia02 = new Dia02();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("2", dia02.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: cada reporte del ejemplo, con el motivo del enunciado")
    void parte1_reportesDelEjemplo() {
        assertEquals(List.of(true, false, false, false, false, true),
                Dia02.leerReportes(EJEMPLO).stream().map(Dia02::esSeguro).toList());
        // Motivos: 2→7 sube 5; 6→2 baja 4; sube y después baja; 4→4 no cambia
    }

    @Test
    @DisplayName("Parte 1: diferencias entre niveles vecinos")
    void parte1_diferencias() {
        assertEquals(List.of(-1, -2, -2, -1), Dia02.diferencias(List.of(7, 6, 4, 2, 1)));
        assertEquals(List.of(), Dia02.diferencias(List.of(5)));
        assertEquals(List.of(), Dia02.diferencias(List.of()));
    }

    @Test
    @DisplayName("Parte 1: límites de los saltos y reportes cortos")
    void parte1_limites() {
        assertTrue(Dia02.esSeguro(List.of(1, 4, 7)));      // saltos de exactamente 3
        assertFalse(Dia02.esSeguro(List.of(1, 5)));        // salto de 4
        assertFalse(Dia02.esSeguro(List.of(3, 3)));        // salto de 0
        assertTrue(Dia02.esSeguro(List.of(9, 8)));         // un solo salto, decreciente
        assertTrue(Dia02.esSeguro(List.of(5)));            // un solo nivel: no viola ninguna regla
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("4", dia02.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: cada reporte del ejemplo con el amortiguador")
    void parte2_reportesDelEjemplo() {
        // 1 3 2 4 5 se salva quitando el 3; 8 6 4 4 1 quitando un 4
        assertEquals(List.of(true, false, false, true, true, true),
                Dia02.leerReportes(EJEMPLO).stream().map(Dia02::esSeguroConAmortiguador).toList());
    }

    @Test
    @DisplayName("Parte 2: quitar el primero o el último nivel también vale; dos niveles malos no se salvan")
    void parte2_extremos() {
        assertTrue(Dia02.esSeguroConAmortiguador(List.of(9, 1, 2, 3)));    // quitando el primero
        assertTrue(Dia02.esSeguroConAmortiguador(List.of(1, 2, 3, 9)));    // quitando el último
        assertTrue(Dia02.esSeguroConAmortiguador(List.of(5, 1, 2, 3)));    // el primero rompe la dirección
        assertFalse(Dia02.esSeguroConAmortiguador(List.of(1, 9, 2, 9, 3)));
        // Tres niveles iguales: quitando cualquiera quedan dos iguales (salto 0)
        assertFalse(Dia02.esSeguroConAmortiguador(List.of(3, 3, 3)));
    }

    @Property(tries = 500)
    @Label("Parte 2: coincide con probar todas las formas de quitar uno o ningún nivel")
    void parte2_coincideConFuerzaBruta(
            @ForAll @Size(min = 1, max = 8) List<@IntRange(min = 1, max = 12) Integer> niveles) {
        boolean esperado = Dia02.esSeguro(niveles);
        for (int i = 0; i < niveles.size(); i++) {
            List<Integer> sinUno = new ArrayList<>();
            for (int j = 0; j < niveles.size(); j++) if (j != i) sinUno.add(niveles.get(j));
            esperado |= Dia02.esSeguro(sinUno);
        }
        assertEquals(esperado, Dia02.esSeguroConAmortiguador(niveles));
    }
}
