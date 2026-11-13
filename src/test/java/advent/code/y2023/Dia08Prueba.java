package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 8 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 8: Páramo Embrujado")
class Dia08Prueba {

    private Dia08 dia08;

    /** Primer ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "RL",
            "",
            "AAA = (BBB, CCC)",
            "BBB = (DDD, EEE)",
            "CCC = (ZZZ, GGG)",
            "DDD = (DDD, DDD)",
            "EEE = (EEE, EEE)",
            "GGG = (GGG, GGG)",
            "ZZZ = (ZZZ, ZZZ)"
    );

    /** Segundo ejemplo del enunciado: hay que repetir las instrucciones. */
    private static final List<String> EJEMPLO_2 = List.of(
            "LLR",
            "",
            "AAA = (BBB, BBB)",
            "BBB = (AAA, ZZZ)",
            "ZZZ = (ZZZ, ZZZ)"
    );

    @BeforeEach
    void configurar() {
        dia08 = new Dia08();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el primer ejemplo del enunciado (AAA → CCC → ZZZ)")
    void parte1_ejemploDelEnunciado() {
        assertEquals("2", dia08.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el segundo ejemplo del enunciado, repitiendo las instrucciones")
    void parte1_segundoEjemplo() {
        assertEquals("6", dia08.parte1(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 1: el recorrido del segundo ejemplo, paso a paso")
    void parte1_recorrido() {
        Dia08.Red red = Dia08.Red.parsear(EJEMPLO_2);
        List<String> esperado = List.of("BBB", "AAA", "BBB", "AAA", "BBB", "ZZZ");   // L L R L L R
        String nodo = Dia08.INICIO;
        for (int paso = 0; paso < esperado.size(); paso++) {
            nodo = red.siguiente(nodo, paso);
            assertEquals(esperado.get(paso), nodo, "paso " + (paso + 1));
        }
    }

    @Test
    @DisplayName("Parte 1: si nunca se llega, falla en vez de seguir para siempre")
    void parte1_nuncaLlega() {
        Dia08.Red red = Dia08.Red.parsear(List.of("L", "", "AAA = (BBB, BBB)", "BBB = (AAA, AAA)", "ZZZ = (ZZZ, ZZZ)"));
        assertThrows(IllegalStateException.class, () -> red.pasos(Dia08.INICIO, Dia08.FIN::equals));
    }

    @Test
    @DisplayName("Parte 1: instrucciones o nodos mal formados, o un nodo que falta, fallan con un mensaje claro")
    void parte1_errores() {
        assertThrows(IllegalArgumentException.class, () -> Dia08.Red.parsear(List.of("LX", "", "AAA = (AAA, AAA)")));
        assertThrows(IllegalArgumentException.class, () -> Dia08.Red.parsear(List.of("L", "", "AAA = (AAA)")));
        assertThrows(IllegalArgumentException.class, () -> dia08.parte1(List.of("L", "", "AAA = (BBB, BBB)")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2: dos fantasmas, desde 11A y 22A. */
    private static final List<String> EJEMPLO_FANTASMAS = List.of(
            "LR",
            "",
            "11A = (11B, XXX)",
            "11B = (XXX, 11Z)",
            "11Z = (11B, XXX)",
            "22A = (22B, XXX)",
            "22B = (22C, 22C)",
            "22C = (22Z, 22Z)",
            "22Z = (22B, 22B)",
            "XXX = (XXX, XXX)"
    );

    /** Fuerza bruta: mover a todos los fantasmas a la vez hasta que estén todos en nodos Z. */
    private static long pasosSimultaneos(List<String> lineas) {
        Dia08.Red red = Dia08.Red.parsear(lineas);
        List<String> fantasmas = red.nodos().keySet().stream().filter(nodo -> nodo.endsWith("A")).toList();
        for (long paso = 0; ; paso++) {
            final long actual = paso;
            fantasmas = fantasmas.stream().map(nodo -> red.siguiente(nodo, actual)).toList();
            if (fantasmas.stream().allMatch(Dia08::esFinFantasma)) return paso + 1;
        }
    }

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("6", dia08.parte2(EJEMPLO_FANTASMAS));
    }

    @Test
    @DisplayName("Parte 2: los períodos del ejemplo: 11A llega cada 2 pasos y 22A cada 3")
    void parte2_periodos() {
        Dia08.Red red = Dia08.Red.parsear(EJEMPLO_FANTASMAS);
        assertEquals(2, red.periodoDeLlegada("11A", Dia08::esFinFantasma));
        assertEquals(3, red.periodoDeLlegada("22A", Dia08::esFinFantasma));
    }

    @Test
    @DisplayName("Parte 2: coincide con mover a todos los fantasmas a la vez")
    void parte2_fuerzaBruta() {
        assertEquals(String.valueOf(pasosSimultaneos(EJEMPLO_FANTASMAS)), dia08.parte2(EJEMPLO_FANTASMAS));
        // tres fantasmas que llegan cada 2, 3 y 4 pasos: A → B → … → Z, y de Z vuelven a B
        List<String> tres = List.of("L", "",
                "1A = (1B, 1B)", "1B = (1Z, 1Z)", "1Z = (1B, 1B)",
                "2A = (2B, 2B)", "2B = (2C, 2C)", "2C = (2Z, 2Z)", "2Z = (2B, 2B)",
                "3A = (3B, 3B)", "3B = (3C, 3C)", "3C = (3D, 3D)", "3D = (3Z, 3Z)", "3Z = (3B, 3B)");
        assertEquals("12", dia08.parte2(tres));
        assertEquals(12, pasosSimultaneos(tres));
    }

    @Test
    @DisplayName("Parte 2: si las llegadas no son los múltiplos de la primera, falla en vez de dar un mcm equivocado")
    void parte2_noPeriodico() {
        // llega en los pasos 1, 4, 7, ...: la primera llegada (1) no es el período (3)
        List<String> red = List.of("L", "", "1A = (1Z, 1Z)", "1Z = (1B, 1B)", "1B = (1C, 1C)", "1C = (1Z, 1Z)");
        assertThrows(IllegalStateException.class, () -> dia08.parte2(red));
        // llega en los pasos impares (1, 3, 5, ...): junto con un fantasma de período 2, nunca coincidirían
        List<String> impares = List.of("L", "", "1A = (1Z, 1Z)", "1Z = (1B, 1B)", "1B = (1Z, 1Z)");
        assertThrows(IllegalStateException.class, () -> dia08.parte2(impares));
    }

    @Test
    @DisplayName("Parte 2: un fantasma que nunca llega falla con un mensaje claro")
    void parte2_nuncaLlega() {
        List<String> red = List.of("L", "", "1A = (1B, 1B)", "1B = (1A, 1A)", "1Z = (1Z, 1Z)");
        assertThrows(IllegalStateException.class, () -> dia08.parte2(red));
    }
}
