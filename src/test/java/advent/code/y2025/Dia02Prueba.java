package advent.code.y2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 2 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 2: Tienda de Regalos")
class Dia02Prueba {

    private Dia02 dia02;

    /** Rangos del ejemplo del enunciado, envueltos en varias líneas como aparecen en el texto. */
    private static final List<String> RANGOS_EJEMPLO = List.of(
            "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,",
            "1698522-1698528,446443-446449,38593856-38593862,565653-565659,",
            "824824821-824824827,2121212118-2121212124"
    );

    @BeforeEach
    void configurar() {
        dia02 = new Dia02();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado debe dar 1227775554")
    void parte1_ejemploDelEnunciado() {
        assertEquals("1227775554", dia02.parte1(RANGOS_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el ejemplo en una sola línea da el mismo resultado")
    void parte1_ejemploEnUnaLinea() {
        assertEquals("1227775554", dia02.parte1(List.of(String.join("", RANGOS_EJEMPLO))));
    }

    @Test
    @DisplayName("Parte 1: lista vacía debe dar 0")
    void parte1_listaVacia() {
        assertEquals("0", dia02.parte1(List.of()));
    }

    // -------------------------------------------------------------------------
    // sumarInvalidosEnRango — casos del enunciado rango por rango
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0}-{1} suma {2}")
    @DisplayName("sumarInvalidosEnRango: rangos del ejemplo")
    @CsvSource({
            "11, 22, 33",                              // 11 + 22
            "95, 115, 99",
            "998, 1012, 1010",
            "1188511880, 1188511890, 1188511885",
            "222220, 222224, 222222",
            "1698522, 1698528, 0",
            "446443, 446449, 446446",
            "38593856, 38593862, 38593859",
            "565653, 565659, 0",
            "824824821, 824824827, 0",
            "2121212118, 2121212124, 0"
    })
    void sumarInvalidos_rangosDelEjemplo(long inicio, long fin, long esperado) {
        assertEquals(esperado, Dia02.sumarInvalidosEnRango(inicio, fin));
    }

    @Test
    @DisplayName("sumarInvalidosEnRango: coincide con la fuerza bruta en 1..200000")
    void sumarInvalidos_coincideConFuerzaBruta() {
        long esperado = 0;
        for (long id = 1; id <= 200_000; id++) {
            if (Dia02.esInvalido(id)) esperado += id;
        }
        assertEquals(esperado, Dia02.sumarInvalidosEnRango(1, 200_000));
    }

    // -------------------------------------------------------------------------
    // esInvalido (método auxiliar)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} es inválido")
    @DisplayName("esInvalido: secuencias repetidas dos veces")
    @CsvSource({"55", "6464", "123123", "1010", "1188511885"})
    void esInvalido_verdadero(long id) {
        assertTrue(Dia02.esInvalido(id));
    }

    @ParameterizedTest(name = "{0} es válido")
    @DisplayName("esInvalido: IDs válidos")
    @CsvSource({"101", "5", "111", "1234", "565656", "12341235"})
    void esInvalido_falso(long id) {
        assertFalse(Dia02.esInvalido(id));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado debe dar 4174379265")
    void parte2_ejemploDelEnunciado() {
        assertEquals("4174379265", dia02.parte2(RANGOS_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: lista vacía debe dar 0")
    void parte2_listaVacia() {
        assertEquals("0", dia02.parte2(List.of()));
    }

    @ParameterizedTest(name = "{0}-{1} suma {2}")
    @DisplayName("sumarRepetidosEnRango: rangos del ejemplo")
    @CsvSource({
            "11, 22, 33",                              // 11 + 22
            "95, 115, 210",                            // 99 + 111
            "998, 1012, 2009",                         // 999 + 1010
            "1188511880, 1188511890, 1188511885",
            "222220, 222224, 222222",
            "1698522, 1698528, 0",
            "446443, 446449, 446446",
            "38593856, 38593862, 38593859",
            "565653, 565659, 565656",
            "824824821, 824824827, 824824824",
            "2121212118, 2121212124, 2121212121"
    })
    void sumarRepetidos_rangosDelEjemplo(long inicio, long fin, long esperado) {
        assertEquals(esperado, Dia02.sumarRepetidosEnRango(inicio, fin));
    }

    @Test
    @DisplayName("sumarRepetidosEnRango: 111111 se cuenta una sola vez")
    void sumarRepetidos_sinDobleConteo() {
        // 111111 = "1"×6 = "11"×3 = "111"×2, pero es un único ID
        assertEquals(111111, Dia02.sumarRepetidosEnRango(111111, 111111));
    }

    @Test
    @DisplayName("sumarRepetidosEnRango: coincide con la fuerza bruta en 1..2000000")
    void sumarRepetidos_coincideConFuerzaBruta() {
        long esperado = 0;
        for (long id = 1; id <= 2_000_000; id++) {
            if (Dia02.esRepetido(id)) esperado += id;
        }
        assertEquals(esperado, Dia02.sumarRepetidosEnRango(1, 2_000_000));
    }

    @ParameterizedTest(name = "{0} es inválido")
    @DisplayName("esRepetido: secuencias repetidas al menos dos veces")
    @CsvSource({"12341234", "123123123", "1212121212", "1111111", "55", "565656"})
    void esRepetido_verdadero(long id) {
        assertTrue(Dia02.esRepetido(id));
    }

    @ParameterizedTest(name = "{0} es válido")
    @DisplayName("esRepetido: IDs válidos")
    @CsvSource({"101", "5", "1234", "12121", "1698525"})
    void esRepetido_falso(long id) {
        assertFalse(Dia02.esRepetido(id));
    }

    @ParameterizedTest(name = "μ({0}) = {1}")
    @DisplayName("mobius: valores conocidos")
    @CsvSource({"1, 1", "2, -1", "3, -1", "4, 0", "5, -1", "6, 1", "8, 0", "10, 1", "30, -1"})
    void mobius_valoresConocidos(int n, int esperado) {
        assertEquals(esperado, Dia02.mobius(n));
    }

    // -------------------------------------------------------------------------
    // leerRangos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("leerRangos: el ejemplo tiene 11 rangos")
    void leerRangos_ejemplo() {
        List<Dia02.Rango> rangos = Dia02.leerRangos(RANGOS_EJEMPLO);
        assertEquals(11, rangos.size());
        assertEquals(new Dia02.Rango(11, 22), rangos.getFirst());
        assertEquals(new Dia02.Rango(2121212118L, 2121212124L), rangos.getLast());
    }
}
