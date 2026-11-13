package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 13 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 13: Artilugio de Garra")
class Dia13Prueba {

    private Dia13 dia13;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "Button A: X+94, Y+34",
            "Button B: X+22, Y+67",
            "Prize: X=8400, Y=5400",
            "",
            "Button A: X+26, Y+66",
            "Button B: X+67, Y+21",
            "Prize: X=12748, Y=12176",
            "",
            "Button A: X+17, Y+86",
            "Button B: X+84, Y+37",
            "Prize: X=7870, Y=6450",
            "",
            "Button A: X+69, Y+23",
            "Button B: X+27, Y+71",
            "Prize: X=18641, Y=10279"
    );

    @BeforeEach
    void configurar() {
        dia13 = new Dia13();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("480", dia13.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: la 1.ª máquina cuesta 280 (80 A y 40 B), la 3.ª 200 (38 A y 86 B), la 2.ª y la 4.ª no se pueden ganar")
    void parte1_costoDeCadaMaquina() {
        assertEquals(List.of(OptionalLong.of(280), OptionalLong.empty(), OptionalLong.of(200), OptionalLong.empty()),
                Dia13.leerMaquinas(EJEMPLO).stream().map(maquina -> maquina.costoMinimo(100)).toList());
    }

    @Test
    @DisplayName("Parte 1: una solución con más pulsaciones que el límite no sirve")
    void parte1_limiteDePulsaciones() {
        Dia13.Maquina maquina = new Dia13.Maquina(1, 0, 0, 1, 101, 5);   // 101 A y 5 B
        assertEquals(OptionalLong.empty(), maquina.costoMinimo(100));
        assertEquals(OptionalLong.of(308), maquina.costoMinimo(101));
    }

    @Test
    @DisplayName("Parte 1: una solución con pulsaciones negativas no sirve")
    void parte1_sinPulsacionesNegativas() {
        Dia13.Maquina maquina = new Dia13.Maquina(1, 0, 0, 1, -2, 5);
        assertEquals(OptionalLong.empty(), maquina.costoMinimo(100));
    }

    @Test
    @DisplayName("Parte 1: botones paralelos fallan con un mensaje claro")
    void parte1_botonesParalelos() {
        Dia13.Maquina maquina = new Dia13.Maquina(1, 2, 2, 4, 10, 20);
        assertThrows(IllegalStateException.class, () -> maquina.costoMinimo(100));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: con los premios corridos, solo se ganan la 2.ª y la 4.ª máquina")
    void parte2_soloSeGananLaSegundaYLaCuarta() {
        assertEquals(List.of(false, true, false, true),
                Dia13.leerMaquinas(EJEMPLO).stream()
                        .map(maquina -> maquina.conPremioCorrido(10_000_000_000_000L).costoMinimo(Long.MAX_VALUE))
                        .map(OptionalLong::isPresent)
                        .toList());
    }

    @Test
    @DisplayName("Parte 2: el ejemplo (el enunciado no da el total; calculado con un script independiente)")
    void parte2_ejemplo() {
        assertEquals(String.valueOf(459236326669L + 416082282239L), dia13.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: correr el premio suma lo mismo en X y en Y, y no cambia los botones")
    void parte2_conPremioCorrido() {
        assertEquals(new Dia13.Maquina(94, 34, 22, 67, 10_000_000_008_400L, 10_000_000_005_400L),
                Dia13.leerMaquinas(EJEMPLO).getFirst().conPremioCorrido(10_000_000_000_000L));
    }
}
