package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 7 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 7: Cartas de Camello")
class Dia07Prueba {

    private Dia07 dia07;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "32T3K 765",
            "T55J5 684",
            "KK677 28",
            "KTJJT 220",
            "QQQJA 483"
    );

    @BeforeEach
    void configurar() {
        dia07 = new Dia07();
    }

    private static int comparar(String a, String b) {
        return comparar(a, b, Dia07.PARTE1);
    }

    private static int comparar(String a, String b, Dia07.Reglas reglas) {
        return Integer.signum(Dia07.comparador(reglas).compare(new Dia07.Mano(a, 0), new Dia07.Mano(b, 0)));
    }

    /** Fuerza bruta: el mejor tipo reemplazando cada J por cada carta posible (sin comodines). */
    private static Dia07.Tipo mejorTipoPorFuerzaBruta(String cartas) {
        int i = cartas.indexOf(Dia07.COMODIN);
        if (i < 0) return new Dia07.Mano(cartas, 0).tipo(false);
        Dia07.Tipo mejor = Dia07.Tipo.CARTA_ALTA;
        for (char carta : "23456789TQKA".toCharArray()) {
            Dia07.Tipo tipo = mejorTipoPorFuerzaBruta(cartas.substring(0, i) + carta + cartas.substring(i + 1));
            if (tipo.compareTo(mejor) > 0) mejor = tipo;
        }
        return mejor;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("6440", dia07.parte1(EJEMPLO));
    }

    @ParameterizedTest(name = "{0} es {1}")
    @CsvSource({
            "AAAAA, REPOKER",
            "AA8AA, POKER",
            "23332, FULL",
            "TTT98, TRIO",
            "23432, DOBLE_PAR",
            "A23A4, PAR",
            "23456, CARTA_ALTA"
    })
    @DisplayName("Parte 1: los ejemplos de cada tipo del enunciado")
    void parte1_tipos(String cartas, Dia07.Tipo tipo) {
        assertEquals(tipo, new Dia07.Mano(cartas, 0).tipo(false));
    }

    @Test
    @DisplayName("Parte 1: a igual tipo, gana la primera carta distinta (33332 > 2AAAA, 77888 > 77788)")
    void parte1_desempate() {
        assertEquals(1, comparar("33332", "2AAAA"));
        assertEquals(1, comparar("77888", "77788"));
        assertEquals(1, comparar("23332", "AAA98"));   // el tipo pesa más que las cartas
        assertEquals(0, comparar("KK677", "KK677"));
    }

    @Test
    @DisplayName("Parte 1: el orden del ejemplo: 32T3K, KTJJT, KK677, T55J5, QQQJA")
    void parte1_orden() {
        List<String> ordenadas = Dia07.manos(EJEMPLO).stream()
                .sorted(Dia07.comparador(Dia07.PARTE1))
                .map(Dia07.Mano::cartas)
                .toList();
        assertEquals(List.of("32T3K", "KTJJT", "KK677", "T55J5", "QQQJA"), ordenadas);
    }

    @Test
    @DisplayName("Parte 1: una mano mal formada o una carta desconocida fallan con un mensaje claro")
    void parte1_errores() {
        assertThrows(IllegalArgumentException.class, () -> Dia07.Mano.parsear("32T3 765"));
        assertThrows(IllegalArgumentException.class, () -> comparar("32T3X", "32T3K"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("5905", dia07.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el orden del ejemplo con comodines: 32T3K, KK677, T55J5, QQQJA, KTJJT")
    void parte2_orden() {
        List<String> ordenadas = Dia07.manos(EJEMPLO).stream()
                .sorted(Dia07.comparador(Dia07.PARTE2))
                .map(Dia07.Mano::cartas)
                .toList();
        assertEquals(List.of("32T3K", "KK677", "T55J5", "QQQJA", "KTJJT"), ordenadas);
    }

    @ParameterizedTest(name = "{0} con comodines es {1}")
    @CsvSource({
            "QJJQ2, POKER",
            "T55J5, POKER",
            "KTJJT, POKER",
            "QQQJA, POKER",
            "JJJJJ, REPOKER",
            "JJJJ2, REPOKER",
            "2345J, PAR",
            "2233J, FULL",
            "32T3K, PAR"
    })
    @DisplayName("Parte 2: el tipo con comodines")
    void parte2_tipos(String cartas, Dia07.Tipo tipo) {
        assertEquals(tipo, new Dia07.Mano(cartas, 0).tipo(true));
    }

    @Test
    @DisplayName("Parte 2: para desempatar, la J es la carta más débil (JKKK2 < QQQQ2)")
    void parte2_desempate() {
        assertEquals(-1, comparar("JKKK2", "QQQQ2", Dia07.PARTE2));
        assertEquals(-1, comparar("J2222", "22222", Dia07.PARTE2));   // los dos son repóker; J < 2
    }

    @Test
    @DisplayName("Parte 2: sumar los comodines a la carta que más se repite da el mejor tipo posible")
    void parte2_coincideConFuerzaBruta() {
        // las cuatro primeras cartas entre J, 2, 3 y 4 (todas las formas de combinar comodines con
        // repeticiones) y la quinta cualquiera: cubre todos los patrones de repetición con y sin J
        String pocas = "J234";
        String todas = "23456789TJQKA";
        for (char a : pocas.toCharArray()) for (char b : pocas.toCharArray()) for (char c : pocas.toCharArray())
            for (char d : pocas.toCharArray()) for (char e : todas.toCharArray()) {
                String mano = "" + a + b + c + d + e;
                assertEquals(mejorTipoPorFuerzaBruta(mano), new Dia07.Mano(mano, 0).tipo(true), mano);
            }
    }
}
