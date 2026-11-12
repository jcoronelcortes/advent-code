package advent.code.y2025;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.CharRange;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 3 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 3: Vestíbulo")
class Dia03Prueba {

    private Dia03 dia03;

    /** Bancos de baterías del ejemplo del enunciado. */
    private static final List<String> BANCOS_EJEMPLO = List.of(
            "987654321111111",
            "811111111111119",
            "234234234234278",
            "818181911112111"
    );

    @BeforeEach
    void configurar() {
        dia03 = new Dia03();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado debe dar 357")
    void parte1_ejemploDelEnunciado() {
        assertEquals("357", dia03.parte1(BANCOS_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: lista vacía o con líneas en blanco")
    void parte1_lineasVacias() {
        assertEquals("0", dia03.parte1(List.of()));
        assertEquals("98", dia03.parte1(List.of("987654321111111", "", "  ")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado debe dar 3121910778619")
    void parte2_ejemploDelEnunciado() {
        assertEquals("3121910778619", dia03.parte2(BANCOS_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: lista vacía debe dar 0")
    void parte2_listaVacia() {
        assertEquals("0", dia03.parte2(List.of()));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @DisplayName("maximoVoltaje con 12 baterías: bancos del ejemplo")
    @CsvSource({
            "987654321111111, 987654321111",   // todas menos algunos 1 del final
            "811111111111119, 811111111119",   // todas menos algunos 1
            "234234234234278, 434234234278",   // sin un 2, un 3 y otro 2 del principio
            "818181911112111, 888911112111"    // sin algunos 1 del principio
    })
    void maximoVoltaje_ejemploDoce(String banco, long esperado) {
        assertEquals(esperado, Dia03.maximoVoltaje(banco, 12));
    }

    // -------------------------------------------------------------------------
    // maximoVoltaje
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → {1}")
    @DisplayName("maximoVoltaje con 2 baterías: bancos del ejemplo")
    @CsvSource({
            "987654321111111, 98",     // las dos primeras
            "811111111111119, 89",     // la primera y la última
            "234234234234278, 78",     // las dos últimas
            "818181911112111, 92"      // el 9 del medio y el 2 posterior
    })
    void maximoVoltaje_ejemplo(String banco, long esperado) {
        assertEquals(esperado, Dia03.maximoVoltaje(banco, 2));
    }

    @Test
    @DisplayName("maximoVoltaje: el dígito mayor al final no puede ir primero")
    void maximoVoltaje_mayorAlFinal() {
        // el 9 está al final: no puede ser el primer dígito, porque no queda nada después
        assertEquals(19, Dia03.maximoVoltaje("119", 2));
    }

    @Test
    @DisplayName("maximoVoltaje: ante empate toma la primera aparición")
    void maximoVoltaje_empate() {
        // tomar el primer 9 deja el segundo 9 disponible → 99
        assertEquals(99, Dia03.maximoVoltaje("1919", 2));
    }

    @Test
    @DisplayName("maximoVoltaje: con tantas baterías como el largo, usa todas")
    void maximoVoltaje_todas() {
        assertEquals(12345, Dia03.maximoVoltaje("12345", 5));
    }

    @Test
    @DisplayName("maximoVoltaje: con 1 batería es el dígito mayor")
    void maximoVoltaje_una() {
        assertEquals(9, Dia03.maximoVoltaje("31941", 1));
    }

    @Test
    @DisplayName("maximoVoltaje: cantidad inválida lanza error")
    void maximoVoltaje_cantidadInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Dia03.maximoVoltaje("12", 3));
        assertThrows(IllegalArgumentException.class, () -> Dia03.maximoVoltaje("12", 0));
    }

    // -------------------------------------------------------------------------
    // Propiedades (jqwik): el voraz contra métodos independientes en bancos aleatorios.
    // Si alguna falla, jqwik reduce el banco al caso mínimo que la rompe.
    // -------------------------------------------------------------------------

    @Property(tries = 500)
    @Label("maximoVoltaje: coincide con la fuerza bruta (1 a 4 baterías)")
    void maximoVoltaje_coincideConFuerzaBruta(
            @ForAll @StringLength(min = 4, max = 15) @CharRange(from = '1', to = '9') String banco,
            @ForAll @IntRange(min = 1, max = 4) int cantidad) {
        assertEquals(fuerzaBruta(banco, cantidad, 0), Dia03.maximoVoltaje(banco, cantidad));
    }

    @Property(tries = 300)
    @Label("maximoVoltaje: con 12 baterías coincide con programación dinámica en bancos de 100 dígitos")
    void maximoVoltaje_coincideConProgramacionDinamica(
            @ForAll @StringLength(min = 100, max = 100) @CharRange(from = '1', to = '9') String banco) {
        assertEquals(programacionDinamica(banco, 12), Dia03.maximoVoltaje(banco, 12));
    }

    /**
     * Método independiente del voraz: {@code mejor[j]} es el mayor número que se
     * puede formar con {@code j} dígitos de lo recorrido hasta el momento.
     */
    private static long programacionDinamica(String banco, int cantidad) {
        long[] mejor = new long[cantidad + 1];
        java.util.Arrays.fill(mejor, -1);
        mejor[0] = 0;
        for (char caracter : banco.toCharArray()) {
            int digito = caracter - '0';
            for (int j = cantidad; j >= 1; j--) {
                if (mejor[j - 1] >= 0) mejor[j] = Math.max(mejor[j], mejor[j - 1] * 10 + digito);
            }
        }
        return mejor[cantidad];
    }

    /** Prueba todas las combinaciones posibles de {@code cantidad} dígitos desde {@code desde}. */
    private static long fuerzaBruta(String banco, int cantidad, int desde) {
        if (cantidad == 0) return 0;
        long mejor = -1;
        for (int i = desde; i <= banco.length() - cantidad; i++) {
            long resto = fuerzaBruta(banco, cantidad - 1, i + 1);
            long candidato = (banco.charAt(i) - '0') * (long) Math.pow(10, cantidad - 1) + resto;
            mejor = Math.max(mejor, candidato);
        }
        return mejor;
    }
}
