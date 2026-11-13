package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 23 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 23: Fiesta LAN")
class Dia23Prueba {

    private Dia23 dia23;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "kh-tc", "qp-kh", "de-cg", "ka-co", "yn-aq", "qp-ub", "cg-tb", "vc-aq",
            "tb-ka", "wh-tc", "yn-cg", "kh-ub", "ta-co", "de-co", "tc-td", "tb-wq",
            "wh-td", "ta-ka", "td-qp", "aq-cg", "wq-ub", "ub-vc", "de-ta", "wq-aq",
            "wq-vc", "wh-yn", "ka-de", "kh-ta", "co-tc", "wh-qp", "tb-vc", "td-yn"
    );

    @BeforeEach
    void configurar() {
        dia23 = new Dia23();
    }

    /** Los grupos escritos como en el enunciado: {@code aq,cg,yn}. */
    private static List<String> comoTexto(List<List<String>> grupos) {
        return grupos.stream().map(grupo -> String.join(",", grupo)).toList();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("7", dia23.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los 12 grupos de tres del ejemplo, como en la lista del enunciado")
    void parte1_gruposDeTres() {
        assertEquals(List.of(
                "aq,cg,yn", "aq,vc,wq", "co,de,ka", "co,de,ta", "co,ka,ta", "de,ka,ta",
                "kh,qp,ub", "qp,td,wh", "tb,vc,wq", "tc,td,wh", "td,wh,yn", "ub,vc,wq"),
                comoTexto(Dia23.Red.parsear(EJEMPLO).triangulos()));
    }

    @Test
    @DisplayName("Parte 1: coincide con probar todos los tríos de computadoras")
    void parte1_fuerzaBruta() {
        Dia23.Red red = Dia23.Red.parsear(EJEMPLO);
        List<String> nombres = new ArrayList<>(red.vecinos().keySet());   // ya ordenados
        List<List<String>> esperado = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            for (int j = i + 1; j < nombres.size(); j++) {
                for (int k = j + 1; k < nombres.size(); k++) {
                    String a = nombres.get(i), b = nombres.get(j), c = nombres.get(k);
                    if (red.vecinos().get(a).contains(b) && red.vecinos().get(a).contains(c)
                            && red.vecinos().get(b).contains(c)) {
                        esperado.add(List.of(a, b, c));
                    }
                }
            }
        }
        assertEquals(esperado, red.triangulos());
    }

    @Test
    @DisplayName("Parte 1: las conexiones no tienen sentido (a-b es lo mismo que b-a)")
    void parte1_sinSentido() {
        assertEquals(Dia23.Red.parsear(List.of("aa-bb", "bb-cc", "cc-aa")).triangulos(),
                Dia23.Red.parsear(List.of("bb-aa", "cc-bb", "aa-cc")).triangulos());
        assertEquals(1, Dia23.Red.parsear(List.of("aa-bb", "bb-cc", "cc-aa")).triangulos().size());
    }

    @Test
    @DisplayName("Parte 1: una línea sin la forma a-b falla con un mensaje claro")
    void parte1_lineaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Dia23.Red.parsear(List.of("aa-bb", "ccdd")));
        assertThrows(IllegalArgumentException.class, () -> Dia23.Red.parsear(List.of("aa-aa")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** La clique máxima según JGraphT, como referencia independiente. */
    private static List<String> grupoMasGrandeSegunJGraphT(Dia23.Red red) {
        Graph<String, DefaultEdge> grafo = new SimpleGraph<>(DefaultEdge.class);
        red.vecinos().forEach((a, deA) -> {
            grafo.addVertex(a);
            for (String b : deA) {
                grafo.addVertex(b);
                grafo.addEdge(a, b);
            }
        });
        List<Set<String>> maximas = new ArrayList<>();
        new BronKerboschCliqueFinder<>(grafo).maximumIterator().forEachRemaining(maximas::add);
        assertEquals(1, maximas.size(), "JGraphT encontró más de una clique máxima");
        return List.copyOf(new TreeSet<>(maximas.getFirst()));
    }

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("co,de,ka,ta", dia23.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: en el ejemplo coincide con el buscador de cliques de JGraphT")
    void parte2_coincideConJGraphT() {
        Dia23.Red red = Dia23.Red.parsear(EJEMPLO);
        assertEquals(grupoMasGrandeSegunJGraphT(red), red.grupoMasGrande());
    }

    @Test
    @DisplayName("Parte 2: el grupo más grande está conectado todo entre sí")
    void parte2_todoConectado() {
        Dia23.Red red = Dia23.Red.parsear(EJEMPLO);
        List<String> grupo = red.grupoMasGrande();
        for (String a : grupo) {
            for (String b : grupo) {
                if (!a.equals(b)) assertTrue(red.vecinos().get(a).contains(b), a + "-" + b);
            }
        }
    }

    @Test
    @DisplayName("Parte 2: dos grupos empatados en tamaño hacen ambigua la contraseña y fallan")
    void parte2_empate() {
        Dia23.Red red = Dia23.Red.parsear(List.of("aa-bb", "cc-dd"));
        assertThrows(IllegalStateException.class, red::grupoMasGrande);
    }

    @Test
    @DisplayName("Parte 2: un grafo completo de 6 computadoras es un solo grupo")
    void parte2_grafoCompleto() {
        List<String> nombres = List.of("aa", "bb", "cc", "dd", "ee", "ff");
        List<String> conexiones = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            for (int j = i + 1; j < nombres.size(); j++) conexiones.add(nombres.get(i) + "-" + nombres.get(j));
        }
        assertEquals(nombres, Dia23.Red.parsear(conexiones).grupoMasGrande());
    }
}
