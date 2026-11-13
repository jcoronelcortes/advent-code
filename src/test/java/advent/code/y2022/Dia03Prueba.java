package advent.code.y2022;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 3 — Año 2022.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2022 — Día 3: Reorganización de Mochilas")
class Dia03Prueba {

    private Dia03 dia03;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "vJrwpWtwJgWrhcsFMMfFFhFp",
            "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL",
            "PmmdzqPrVvPwwTWBwg",
            "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn",
            "ttgJtRGJQctTZtZT",
            "CrZsJsPPZsGzwwsLwLmpwMDw"
    );

    /** Fuerza bruta: los tipos que aparecen en todos los textos, comparando letra por letra con {@code indexOf}. */
    private static Set<Character> comunesPorLetras(List<String> grupos) {
        Set<Character> comunes = new TreeSet<>();
        for (char c : grupos.getFirst().toCharArray()) {
            if (grupos.stream().allMatch(g -> g.indexOf(c) >= 0)) comunes.add(c);
        }
        return comunes;
    }

    @BeforeEach
    void configurar() {
        dia03 = new Dia03();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("157", dia03.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los tipos comunes del ejemplo son p, L, P, v, t y s, con prioridades 16, 38, 42, 22, 20 y 19")
    void parte1_cadaMochila() {
        List<Integer> prioridades = new ArrayList<>();
        for (String mochila : EJEMPLO) {
            int mitad = mochila.length() / 2;
            prioridades.add(Dia03.unicoComun(List.of(mochila.substring(0, mitad), mochila.substring(mitad))));
        }
        assertEquals(List.of(16, 38, 42, 22, 20, 19), prioridades);
        assertEquals(List.of(16, 38, 42, 22, 20, 19), "pLPvts".chars().map(c -> Dia03.prioridad((char) c)).boxed().toList());
    }

    @Test
    @DisplayName("Parte 1: prioridades de los extremos, y un carácter que no es letra falla")
    void parte1_prioridades() {
        assertEquals(1, Dia03.prioridad('a'));
        assertEquals(26, Dia03.prioridad('z'));
        assertEquals(27, Dia03.prioridad('A'));
        assertEquals(52, Dia03.prioridad('Z'));
        assertThrows(IllegalArgumentException.class, () -> Dia03.prioridad('1'));
        assertThrows(IllegalArgumentException.class, () -> Dia03.prioridad('ñ'));
    }

    @Test
    @DisplayName("Parte 1: la máscara coincide con comparar letra por letra en cada mochila del ejemplo")
    void parte1_fuerzaBruta() {
        for (String mochila : EJEMPLO) {
            int mitad = mochila.length() / 2;
            List<String> partes = List.of(mochila.substring(0, mitad), mochila.substring(mitad));
            Set<Character> comunes = comunesPorLetras(partes);
            assertEquals(1, comunes.size(), mochila);
            assertEquals(Dia03.prioridad(comunes.iterator().next()), Dia03.unicoComun(partes));
        }
    }

    @Test
    @DisplayName("Parte 1: un largo impar, o ninguno o más de un tipo en común, falla con un mensaje claro")
    void parte1_inputInvalido() {
        assertThrows(IllegalArgumentException.class, () -> dia03.parte1(List.of("abcab")));
        assertThrows(IllegalArgumentException.class, () -> dia03.parte1(List.of("abcd")));     // ninguno
        assertThrows(IllegalArgumentException.class, () -> dia03.parte1(List.of("abab")));     // a y b
        assertEquals("1", dia03.parte1(List.of("aaba")));                                     // repetidos: solo a
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("70", dia03.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las insignias del ejemplo son r (18) y Z (52), como al comparar letra por letra")
    void parte2_insignias() {
        List<String> primero = EJEMPLO.subList(0, 3), segundo = EJEMPLO.subList(3, 6);
        assertEquals(18, Dia03.unicoComun(primero));
        assertEquals(52, Dia03.unicoComun(segundo));
        assertEquals(Set.of('r'), comunesPorLetras(primero));
        assertEquals(Set.of('Z'), comunesPorLetras(segundo));
    }

    @Test
    @DisplayName("Parte 2: una cantidad de mochilas que no es múltiplo de 3, o un grupo sin insignia única, falla")
    void parte2_inputInvalido() {
        assertThrows(IllegalArgumentException.class, () -> dia03.parte2(EJEMPLO.subList(0, 4)));
        assertThrows(IllegalArgumentException.class, () -> dia03.parte2(List.of("ab", "ab", "ab")));
        assertThrows(IllegalArgumentException.class, () -> dia03.parte2(List.of("ab", "cd", "ef")));
    }
}
