package advent.code.y2023;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Chars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 12 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 12: Aguas Termales")
class Dia12Prueba {

    private Dia12 dia12;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "???.### 1,1,3",
            ".??..??...?##. 1,1,3",
            "?#?#?#?#?#?#?#? 1,3,1,6",
            "????.#...#... 4,1,1",
            "????.######..#####. 1,6,5",
            "?###???????? 3,2,1"
    );

    @BeforeEach
    void configurar() {
        dia12 = new Dia12();
    }

    /** Los tamaños de los grupos de '#' seguidos de una fila sin '?'. */
    private static List<Integer> grupos(String fila) {
        return Arrays.stream(fila.split("\\.+")).filter(grupo -> !grupo.isEmpty()).map(String::length).toList();
    }

    /** Fuerza bruta: prueba las 2^k formas de completar los '?' y cuenta las que dan esos grupos. */
    private static long arreglosPorFuerzaBruta(String resortes, int[] grupos) {
        List<Integer> esperados = Arrays.stream(grupos).boxed().toList();
        List<Integer> desconocidos = new ArrayList<>();
        for (int i = 0; i < resortes.length(); i++) if (resortes.charAt(i) == '?') desconocidos.add(i);
        long total = 0;
        for (int mascara = 0; mascara < (1 << desconocidos.size()); mascara++) {
            char[] fila = resortes.toCharArray();
            for (int k = 0; k < desconocidos.size(); k++) fila[desconocidos.get(k)] = (mascara >> k & 1) == 1 ? '#' : '.';
            if (grupos(new String(fila)).equals(esperados)) total++;
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("21", dia12.parte1(EJEMPLO));
    }

    @ParameterizedTest(name = "{0} {1}: {2} arreglos")
    @CsvSource(delimiter = ';', value = {
            "???.###;            1,1,3;   1",
            ".??..??...?##.;     1,1,3;   4",
            "?#?#?#?#?#?#?#?;    1,3,1,6; 1",
            "????.#...#...;      4,1,1;   1",
            "????.######..#####.; 1,6,5;  4",
            "?###????????;       3,2,1;   10"
    })
    @DisplayName("Parte 1: los arreglos de cada fila del ejemplo")
    void parte1_cadaFila(String resortes, String grupos, long arreglos) {
        assertEquals(arreglos, Dia12.Fila.parsear(resortes + " " + grupos).arreglos());
    }

    @Test
    @DisplayName("Parte 1: las filas sin '?' del enunciado tienen exactamente 1 arreglo")
    void parte1_filasCompletas() {
        for (String linea : List.of("#.#.### 1,1,3", ".#...#....###. 1,1,3", ".#.###.#.###### 1,3,1,6",
                "####.#...#... 4,1,1", "#....######..#####. 1,6,5", ".###.##....# 3,2,1")) {
            assertEquals(1, Dia12.Fila.parsear(linea).arreglos(), linea);
        }
        assertEquals(0, Dia12.Fila.parsear("#### 2,2").arreglos());   // #### es un grupo de 4, nunca 2,2
    }

    @Test
    @DisplayName("Parte 1: en el ejemplo coincide con probar todas las formas de completar los '?'")
    void parte1_fuerzaBruta() {
        for (String linea : EJEMPLO) {
            Dia12.Fila fila = Dia12.Fila.parsear(linea);
            assertEquals(arreglosPorFuerzaBruta(fila.resortes(), fila.grupos()), fila.arreglos(), linea);
        }
    }

    @Property(tries = 500)
    @Label("Parte 1: en filas al azar coincide con probar todas las formas de completar los '?'")
    void parte1_fuerzaBrutaAlAzar(@ForAll @StringLength(min = 1, max = 14) @Chars({'.', '#', '?'}) String resortes,
                                  @ForAll @IntRange(min = 0, max = 1_000_000) int semilla) {
        // grupos de una forma al azar de completar la fila, para que haya arreglos casi siempre
        Random azar = new Random(semilla);
        StringBuilder completa = new StringBuilder();
        for (char c : resortes.toCharArray()) completa.append(c == '?' ? (azar.nextBoolean() ? '#' : '.') : c);
        int[] grupos = grupos(completa.toString()).stream().mapToInt(Integer::intValue).toArray();
        Dia12.Fila fila = new Dia12.Fila(resortes, grupos);
        assertEquals(arreglosPorFuerzaBruta(resortes, grupos), fila.arreglos());
    }

    @Test
    @DisplayName("Parte 1: una fila mal formada falla con un mensaje claro")
    void parte1_filaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Dia12.Fila.parsear("??x 1"));
        assertThrows(IllegalArgumentException.class, () -> Dia12.Fila.parsear("???"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("525152", dia12.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: desdoblar '.# 1' y la primera fila del ejemplo, como en el enunciado")
    void parte2_desdoblar() {
        Dia12.Fila corta = Dia12.Fila.parsear(".# 1").desdoblar(Dia12.COPIAS);
        assertEquals(".#?.#?.#?.#?.#", corta.resortes());
        assertArrayEquals(new int[] {1, 1, 1, 1, 1}, corta.grupos());
        Dia12.Fila primera = Dia12.Fila.parsear(EJEMPLO.getFirst()).desdoblar(Dia12.COPIAS);
        assertEquals("???.###????.###????.###????.###????.###", primera.resortes());
        assertArrayEquals(new int[] {1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3}, primera.grupos());
    }

    @ParameterizedTest(name = "{0} {1} desdoblada: {2} arreglos")
    @CsvSource(delimiter = ';', value = {
            "???.###;            1,1,3;   1",
            ".??..??...?##.;     1,1,3;   16384",
            "?#?#?#?#?#?#?#?;    1,3,1,6; 1",
            "????.#...#...;      4,1,1;   16",
            "????.######..#####.; 1,6,5;  2500",
            "?###????????;       3,2,1;   506250"
    })
    @DisplayName("Parte 2: los arreglos de cada fila del ejemplo desdoblada")
    void parte2_cadaFila(String resortes, String grupos, long arreglos) {
        assertEquals(arreglos, Dia12.Fila.parsear(resortes + " " + grupos).desdoblar(Dia12.COPIAS).arreglos());
    }

    @Test
    @DisplayName("Parte 2: con dos copias, coincide con la fuerza bruta en el ejemplo")
    void parte2_fuerzaBrutaConDosCopias() {
        for (String linea : EJEMPLO) {
            Dia12.Fila doble = Dia12.Fila.parsear(linea).desdoblar(2);
            assertEquals(arreglosPorFuerzaBruta(doble.resortes(), doble.grupos()), doble.arreglos(), linea);
        }
    }
}
