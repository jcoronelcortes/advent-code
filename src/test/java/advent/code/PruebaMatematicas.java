package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Matematicas")
class PruebaMatematicas {

    // -------------------------------------------------------------------------
    // Divisibilidad
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "mcd({0}, {1}) = {2}")
    @DisplayName("mcd: valores conocidos")
    @CsvSource({"12, 18, 6", "17, 5, 1", "0, 7, 7", "-12, 18, 6", "100, 100, 100"})
    void mcd_valoresConocidos(long a, long b, long esperado) {
        assertEquals(esperado, Matematicas.mcd(a, b));
    }

    @ParameterizedTest(name = "mcm({0}, {1}) = {2}")
    @DisplayName("mcm: valores conocidos")
    @CsvSource({"4, 6, 12", "7, 5, 35", "0, 5, 0", "-4, 6, 12"})
    void mcm_valoresConocidos(long a, long b, long esperado) {
        assertEquals(esperado, Matematicas.mcm(a, b));
    }

    @Test
    @DisplayName("mcm de una colección: ciclos que coinciden")
    void mcm_coleccion() {
        assertEquals(60, Matematicas.mcm(List.of(3L, 4L, 5L, 6L)));
        assertEquals(1, Matematicas.mcm(List.of()));
    }

    @Test
    @DisplayName("mcm: lanza error si desborda")
    void mcm_desborde() {
        assertThrows(ArithmeticException.class, () -> Matematicas.mcm(Long.MAX_VALUE, Long.MAX_VALUE - 1));
    }

    @ParameterizedTest(name = "⌈{0} / {1}⌉ = {2}")
    @DisplayName("dividirHaciaArriba: positivos y negativos")
    @CsvSource({"7, 2, 4", "6, 2, 3", "0, 5, 0", "-7, 2, -3", "998, 101, 10"})
    void dividirHaciaArriba_valores(long dividendo, long divisor, long esperado) {
        assertEquals(esperado, Matematicas.dividirHaciaArriba(dividendo, divisor));
    }

    // -------------------------------------------------------------------------
    // Aritmética modular
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} mod {1} = {2}")
    @DisplayName("modulo: siempre no negativo")
    @CsvSource({"-18, 100, 82", "250, 100, 50", "-1, 7, 6", "0, 3, 0"})
    void modulo_noNegativo(long a, long m, long esperado) {
        assertEquals(esperado, Matematicas.modulo(a, m));
    }

    @ParameterizedTest(name = "{0}^{1} mod {2} = {3}")
    @DisplayName("potenciaModular: valores conocidos")
    @CsvSource({
            "2, 10, 1000, 24",
            "3, 0, 7, 1",
            "5, 3, 1, 0",
            "-2, 3, 5, 2",                                   // -8 mod 5
            "7, 1000000, 1000000007, 880007888",                              // verificado con pow() de Python
            "20201227, 1000000000, 1000000000000000003, 233689064226526592"   // módulo grande sin desborde
    })
    void potenciaModular_valores(long base, long exponente, long modulo, long esperado) {
        assertEquals(esperado, Matematicas.potenciaModular(base, exponente, modulo));
    }

    @Test
    @DisplayName("inversoModular: a·x ≡ 1")
    void inversoModular_valido() {
        assertEquals(4, Matematicas.inversoModular(3, 11));   // 3·4 = 12 ≡ 1
        long inverso = Matematicas.inversoModular(123456789, 1_000_000_007);
        assertEquals(1, Math.floorMod(123456789L * inverso, 1_000_000_007L));
    }

    @Test
    @DisplayName("inversoModular: lanza error si no son coprimos")
    void inversoModular_noCoprimos() {
        assertThrows(ArithmeticException.class, () -> Matematicas.inversoModular(4, 8));
    }

    @Test
    @DisplayName("restoChino: módulos coprimos")
    void restoChino_coprimos() {
        // x ≡ 2 (mod 3), x ≡ 3 (mod 5), x ≡ 2 (mod 7) → x = 23
        assertEquals(23, Matematicas.restoChino(new long[]{2, 3, 2}, new long[]{3, 5, 7}));
    }

    @Test
    @DisplayName("restoChino: ejemplo de AoC 2020 día 13 (buses 7,13,x,x,59,x,31,19)")
    void restoChino_buses() {
        // t + i ≡ 0 (mod bus_i)  →  t ≡ -i (mod bus_i)
        long[] restos  = {0, -1, -4, -6, -7};
        long[] modulos = {7, 13, 59, 31, 19};
        assertEquals(1068781, Matematicas.restoChino(restos, modulos));
    }

    @Test
    @DisplayName("restoChino: módulos no coprimos con y sin solución")
    void restoChino_noCoprimos() {
        // x ≡ 2 (mod 4), x ≡ 4 (mod 6) → x = 10
        assertEquals(10, Matematicas.restoChino(new long[]{2, 4}, new long[]{4, 6}));
        // x ≡ 1 (mod 4), x ≡ 2 (mod 6) → sin solución (paridad incompatible)
        assertThrows(ArithmeticException.class,
                () -> Matematicas.restoChino(new long[]{1, 2}, new long[]{4, 6}));
    }

    // -------------------------------------------------------------------------
    // Series y dígitos
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} + ... + {1} = {2}")
    @DisplayName("sumaAritmetica: valores conocidos")
    @CsvSource({"1, 100, 5050", "5, 5, 5", "10, 1, 0", "-3, 3, 0", "1, 9, 45", "10, 99, 4905"})
    void sumaAritmetica_valores(long desde, long hasta, long esperado) {
        assertEquals(esperado, Matematicas.sumaAritmetica(desde, hasta));
    }

    @Test
    @DisplayName("potenciaDeDiez: valores y desborde")
    void potenciaDeDiez_valores() {
        assertEquals(1, Matematicas.potenciaDeDiez(0));
        assertEquals(1_000_000_000_000_000_000L, Matematicas.potenciaDeDiez(18));
        assertThrows(ArithmeticException.class, () -> Matematicas.potenciaDeDiez(19));
    }

    @ParameterizedTest(name = "{0} tiene {1} dígitos")
    @DisplayName("cantidadDigitos: valores conocidos")
    @CsvSource({"0, 1", "7, 1", "10, 2", "-123, 3", "9898998927, 10", "9223372036854775807, 19"})
    void cantidadDigitos_valores(long numero, int esperado) {
        assertEquals(esperado, Matematicas.cantidadDigitos(numero));
    }

    // -------------------------------------------------------------------------
    // Teoría de números
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("esPrimo: primos y no primos")
    void esPrimo_valores() {
        assertTrue(Matematicas.esPrimo(2));
        assertTrue(Matematicas.esPrimo(1_000_000_007));
        assertFalse(Matematicas.esPrimo(1));
        assertFalse(Matematicas.esPrimo(-7));
        assertFalse(Matematicas.esPrimo(91));   // 7 · 13
    }

    @ParameterizedTest(name = "μ({0}) = {1}")
    @DisplayName("mobius: valores conocidos")
    @CsvSource({"1, 1", "2, -1", "3, -1", "4, 0", "5, -1", "6, 1", "8, 0", "10, 1", "30, -1"})
    void mobius_valoresConocidos(long n, int esperado) {
        assertEquals(esperado, Matematicas.mobius(n));
    }

    @Test
    @DisplayName("mobius: lanza error con n no positivo")
    void mobius_noPositivo() {
        assertThrows(IllegalArgumentException.class, () -> Matematicas.mobius(0));
    }
}
