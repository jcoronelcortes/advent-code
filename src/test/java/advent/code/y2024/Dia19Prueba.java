package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 19 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 19: Disposición de Toallas")
class Dia19Prueba {

    private Dia19 dia19;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "r, wr, b, g, bwu, rb, gb, br",
            "",
            "brwrr",
            "bggr",
            "gbbr",
            "rrbgbr",
            "ubwu",
            "bwurrg",
            "brgr",
            "bbrgwb"
    );

    @BeforeEach
    void configurar() {
        dia19 = new Dia19();
    }

    /** Cuenta de forma independiente: recursión sin memoria probando cada patrón como prefijo. */
    private static long manerasPorFuerzaBruta(Set<String> patrones, String diseno) {
        if (diseno.isEmpty()) return 1;
        long total = 0;
        for (String patron : patrones) {
            if (diseno.startsWith(patron)) total += manerasPorFuerzaBruta(patrones, diseno.substring(patron.length()));
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("6", dia19.parte1(EJEMPLO));
    }

    @ParameterizedTest(name = "{0} {1} se puede armar")
    @CsvSource({
            "brwrr,  true",
            "bggr,   true",
            "gbbr,   true",
            "rrbgbr, true",
            "ubwu,   false",
            "bwurrg, true",
            "brgr,   true",
            "bbrgwb, false"
    })
    @DisplayName("Parte 1: cada diseño del ejemplo, posible o imposible como dice el enunciado")
    void parte1_cadaDiseno(String diseno, boolean posible) {
        Dia19.Onsen onsen = Dia19.Onsen.parsear(EJEMPLO);
        assertEquals(posible, onsen.maneras(diseno) > 0);
    }

    @Test
    @DisplayName("Parte 1: se leen los 8 patrones y los 8 diseños, sin espacios")
    void parte1_parsear() {
        Dia19.Onsen onsen = Dia19.Onsen.parsear(EJEMPLO);
        assertEquals(Set.of("r", "wr", "b", "g", "bwu", "rb", "gb", "br"), onsen.patrones());
        assertEquals(8, onsen.disenos().size());
        assertEquals(3, onsen.largoMaximo());
    }

    @Test
    @DisplayName("Parte 1: rgrgr se arma de varias maneras con r, rg, gr, rgr y rgrgr")
    void parte1_variasManeras() {
        Dia19.Onsen onsen = new Dia19.Onsen(Set.of("r", "rg", "gr", "rgr", "rgrgr"), List.of(), 5);
        // rg+rg+r, rg+r+gr, r+gr+gr, rgr+gr, rg+rgr, rgrgr
        assertEquals(6, onsen.maneras("rgrgr"));
        assertEquals(manerasPorFuerzaBruta(onsen.patrones(), "rgrgr"), onsen.maneras("rgrgr"));
    }

    @Test
    @DisplayName("Parte 1: las toallas no se pueden dar vuelta")
    void parte1_sinDarVuelta() {
        Dia19.Onsen onsen = new Dia19.Onsen(Set.of("ggr"), List.of(), 3);
        assertEquals(1, onsen.maneras("ggr"));
        assertEquals(0, onsen.maneras("rgg"));
    }

    @Test
    @DisplayName("Parte 1: la programación dinámica coincide con la fuerza bruta en cada diseño del ejemplo")
    void parte1_coincideConFuerzaBruta() {
        Dia19.Onsen onsen = Dia19.Onsen.parsear(EJEMPLO);
        for (String diseno : onsen.disenos()) {
            assertEquals(manerasPorFuerzaBruta(onsen.patrones(), diseno), onsen.maneras(diseno), diseno);
        }
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("16", dia19.parte2(EJEMPLO));
    }

    @ParameterizedTest(name = "{0} se arma de {1} maneras")
    @CsvSource({
            "brwrr,  2",
            "bggr,   1",
            "gbbr,   4",
            "rrbgbr, 6",
            "ubwu,   0",
            "bwurrg, 1",
            "brgr,   2",
            "bbrgwb, 0"
    })
    @DisplayName("Parte 2: las maneras de cada diseño del ejemplo, como dice el enunciado")
    void parte2_manerasDeCadaDiseno(String diseno, long maneras) {
        assertEquals(maneras, Dia19.Onsen.parsear(EJEMPLO).maneras(diseno));
    }

    @Test
    @DisplayName("Parte 2: una cantidad de maneras que no cabe en un long falla en vez de desbordar")
    void parte2_desborde() {
        // con "a" y "aa", las maneras de armar n letras "a" son el número de Fibonacci F(n+1): F(93) ya no cabe en un long
        Dia19.Onsen onsen = new Dia19.Onsen(Set.of("a", "aa"), List.of(), 2);
        assertEquals(89, onsen.maneras("a".repeat(10)));   // F(11)
        assertThrows(ArithmeticException.class, () -> onsen.maneras("a".repeat(92)));
    }
}
