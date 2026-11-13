package advent.code.y2023;

import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 13 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 13: Punto de Incidencia")
class Dia13Prueba {

    private Dia13 dia13;

    /** Primer patrón del ejemplo: reflejo vertical entre las columnas 5 y 6. */
    private static final List<String> VERTICAL = List.of(
            "#.##..##.",
            "..#.##.#.",
            "##......#",
            "##......#",
            "..#.##.#.",
            "..##..##.",
            "#.#.##.#."
    );

    /** Segundo patrón del ejemplo: reflejo horizontal entre las filas 4 y 5. */
    private static final List<String> HORIZONTAL = List.of(
            "#...##..#",
            "#....#..#",
            "..##..###",
            "#####.##.",
            "#####.##.",
            "..##..###",
            "#....#..#"
    );

    /** Input del ejemplo del enunciado: los dos patrones separados por una línea en blanco. */
    private static final List<String> EJEMPLO = new ArrayList<>();

    static {
        EJEMPLO.addAll(VERTICAL);
        EJEMPLO.add("");
        EJEMPLO.addAll(HORIZONTAL);
    }

    @BeforeEach
    void configurar() {
        dia13 = new Dia13();
    }

    /** Fuerza bruta: reflejar literalmente las filas de arriba y comparar con las de abajo. */
    private static boolean reflejaLiteralmente(List<String> filas, int k) {
        int alto = Math.min(k, filas.size() - k);
        List<String> arriba = new ArrayList<>(filas.subList(k - alto, k));
        Collections.reverse(arriba);
        return arriba.equals(filas.subList(k, k + alto));
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("405", dia13.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el primer patrón refleja entre las columnas 5 y 6; el segundo, entre las filas 4 y 5")
    void parte1_cadaPatron() {
        Grilla vertical = Grilla.desdeLineas(VERTICAL);
        assertEquals(List.of(), Dia13.lineasDeReflejo(vertical, 0));
        assertEquals(List.of(5), Dia13.lineasDeReflejo(vertical.transponer(), 0));
        assertEquals(5, Dia13.valor(vertical, 0));

        Grilla horizontal = Grilla.desdeLineas(HORIZONTAL);
        assertEquals(List.of(4), Dia13.lineasDeReflejo(horizontal, 0));
        assertEquals(List.of(), Dia13.lineasDeReflejo(horizontal.transponer(), 0));
        assertEquals(400, Dia13.valor(horizontal, 0));
    }

    @Test
    @DisplayName("Parte 1: coincide con reflejar literalmente las filas y las columnas")
    void parte1_fuerzaBruta() {
        for (List<String> lineas : List.of(VERTICAL, HORIZONTAL)) {
            Grilla patron = Grilla.desdeLineas(lineas);
            Grilla transpuesto = patron.transponer();
            List<String> columnas = List.of(transpuesto.toString().split("\n"));
            List<Integer> horizontales = new ArrayList<>(), verticales = new ArrayList<>();
            for (int k = 1; k < lineas.size(); k++) if (reflejaLiteralmente(lineas, k)) horizontales.add(k);
            for (int k = 1; k < columnas.size(); k++) if (reflejaLiteralmente(columnas, k)) verticales.add(k);
            assertEquals(horizontales, Dia13.lineasDeReflejo(patron, 0));
            assertEquals(verticales, Dia13.lineasDeReflejo(transpuesto, 0));
        }
    }

    @Test
    @DisplayName("Parte 1: un patrón sin reflejo, o con más de uno, falla con un mensaje claro")
    void parte1_sinReflejoUnico() {
        assertThrows(IllegalArgumentException.class, () -> Dia13.valor(Grilla.desdeLineas(List.of("#.", "..")), 0));
        assertThrows(IllegalArgumentException.class, () -> Dia13.valor(Grilla.desdeLineas(List.of("##", "##")), 0));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("400", dia13.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: con la mancha, el primer patrón refleja entre las filas 3 y 4; el segundo, entre las 1 y 2")
    void parte2_cadaPatron() {
        Grilla vertical = Grilla.desdeLineas(VERTICAL);
        assertEquals(List.of(3), Dia13.lineasDeReflejo(vertical, 1));
        assertEquals(List.of(), Dia13.lineasDeReflejo(vertical.transponer(), 1));
        assertEquals(300, Dia13.valor(vertical, 1));

        Grilla horizontal = Grilla.desdeLineas(HORIZONTAL);
        assertEquals(List.of(1), Dia13.lineasDeReflejo(horizontal, 1));
        assertEquals(List.of(), Dia13.lineasDeReflejo(horizontal.transponer(), 1));
        assertEquals(100, Dia13.valor(horizontal, 1));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar cada mancha posible y buscar una línea nueva")
    void parte2_fuerzaBruta() {
        for (List<String> lineas : List.of(VERTICAL, HORIZONTAL)) {
            Grilla original = Grilla.desdeLineas(lineas);
            long anterior = Dia13.valor(original, 0);
            Set<Long> nuevos = new TreeSet<>();
            for (int f = 0; f < lineas.size(); f++) {
                for (int c = 0; c < lineas.get(f).length(); c++) {
                    List<String> corregido = new ArrayList<>(lineas);
                    char[] fila = corregido.get(f).toCharArray();
                    fila[c] = fila[c] == '#' ? '.' : '#';
                    corregido.set(f, new String(fila));
                    Grilla patron = Grilla.desdeLineas(corregido);
                    for (int k : Dia13.lineasDeReflejo(patron, 0)) nuevos.add(100L * k);
                    for (int k : Dia13.lineasDeReflejo(patron.transponer(), 0)) nuevos.add((long) k);
                }
            }
            nuevos.remove(anterior);
            assertEquals(Set.of(Dia13.valor(original, 1)), nuevos);
        }
    }
}
