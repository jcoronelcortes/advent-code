package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 25 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 25: Sobrecarga de Nieve")
class Dia25Prueba {

    private Dia25 dia25;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "jqt: rhn xhk nvd",
            "rsh: frs pzl lsr",
            "xhk: hfx",
            "cmg: qnr nvd lhk bvb",
            "rhn: xhk bvb hfx",
            "bvb: xhk hfx",
            "pzl: lsr hfx nvd",
            "qnr: nvd",
            "ntq: jqt hfx bvb xhk",
            "nvd: lhk",
            "lsr: lhk",
            "rzs: qnr cmg lsr rsh",
            "frs: qnr lhk lsr"
    );

    /** Cuántos grupos separados quedan en la red sin los cables indicados (por BFS). */
    private static int grupos(Map<String, Set<String>> vecinos, Set<List<String>> sinCables) {
        Set<String> vistos = new HashSet<>();
        int grupos = 0;
        for (String inicio : vecinos.keySet()) {
            if (!vistos.add(inicio)) continue;
            grupos++;
            Deque<String> pendientes = new ArrayDeque<>(List.of(inicio));
            while (!pendientes.isEmpty()) {
                String u = pendientes.pop();
                for (String v : vecinos.get(u)) {
                    List<String> cable = u.compareTo(v) < 0 ? List.of(u, v) : List.of(v, u);
                    if (!sinCables.contains(cable) && vistos.add(v)) pendientes.push(v);
                }
            }
        }
        return grupos;
    }

    /** Todos los cables de la red, cada uno una vez y con sus componentes en orden alfabético. */
    private static List<List<String>> cables(Map<String, Set<String>> vecinos) {
        Set<List<String>> cables = new LinkedHashSet<>();
        vecinos.forEach((u, vs) -> vs.forEach(v -> cables.add(u.compareTo(v) < 0 ? List.of(u, v) : List.of(v, u))));
        return new ArrayList<>(cables);
    }

    @BeforeEach
    void configurar() {
        dia25 = new Dia25();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("54", dia25.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el corte del ejemplo son hfx/pzl, bvb/cmg y nvd/jqt, con grupos de 9 y 6 componentes")
    void parte1_corteDelEjemplo() {
        Dia25.Corte corte = Dia25.corteMinimo(Dia25.leer(EJEMPLO), 3);
        assertEquals(Set.of(List.of("hfx", "pzl"), List.of("bvb", "cmg"), List.of("jqt", "nvd")), corte.cables());
        Set<String> nueve = Set.of("cmg", "frs", "lhk", "lsr", "nvd", "pzl", "qnr", "rsh", "rzs");
        Set<String> seis = Set.of("bvb", "hfx", "jqt", "ntq", "rhn", "xhk");
        assertEquals(Set.of(nueve, seis), Set.of(Set.copyOf(corte.grupo()), Set.copyOf(corte.otro())));
    }

    @Test
    @DisplayName("Parte 1: coincide con probar todos los tríos de cables del ejemplo: hay uno solo que divide la red")
    void parte1_fuerzaBruta() {
        Map<String, Set<String>> vecinos = Dia25.leer(EJEMPLO);
        List<List<String>> todos = cables(vecinos);
        assertEquals(33, todos.size());
        assertEquals(1, grupos(vecinos, Set.of()));
        List<Set<List<String>>> cortes = new ArrayList<>();
        for (int i = 0; i < todos.size(); i++)
            for (int j = i + 1; j < todos.size(); j++)
                for (int k = j + 1; k < todos.size(); k++) {
                    Set<List<String>> trio = Set.of(todos.get(i), todos.get(j), todos.get(k));
                    if (grupos(vecinos, trio) == 2) cortes.add(trio);
                }
        assertEquals(List.of(Dia25.corteMinimo(vecinos, 3).cables()), cortes);
        // Y ningún par de cables alcanza para dividirla
        for (int i = 0; i < todos.size(); i++)
            for (int j = i + 1; j < todos.size(); j++)
                assertEquals(1, grupos(vecinos, Set.of(todos.get(i), todos.get(j))));
    }

    @Test
    @DisplayName("Parte 1: si la red se divide con menos cables, o no se puede con tres, falla con un mensaje claro")
    void parte1_sinCorteDeTres() {
        // Dos triángulos unidos por un solo cable
        Map<String, Set<String>> puente = Dia25.leer(List.of("aa: bb cc", "bb: cc", "cc: dd", "dd: ee ff", "ee: ff"));
        assertThrows(IllegalStateException.class, () -> Dia25.corteMinimo(puente, 3));
        assertEquals(1, Dia25.corteMinimo(puente, 1).cables().size());
        // Cinco componentes todos conectados entre sí: separar cualquiera exige cortar 4 cables
        Map<String, Set<String>> completo = Dia25.leer(List.of("aa: bb cc dd ee", "bb: cc dd ee", "cc: dd ee", "dd: ee"));
        assertThrows(IllegalStateException.class, () -> Dia25.corteMinimo(completo, 3));
        assertThrows(IllegalArgumentException.class, () -> Dia25.leer(List.of("aa bb cc")));
        assertThrows(IllegalArgumentException.class, () -> Dia25.leer(List.of("aa: aa")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @Disabled("TODO: completar el resultado esperado cuando se publique la parte 2")
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("TODO", dia25.parte2(EJEMPLO));
    }
}
