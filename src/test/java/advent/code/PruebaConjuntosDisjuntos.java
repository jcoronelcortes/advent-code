package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConjuntosDisjuntos")
class PruebaConjuntosDisjuntos {

    @Test
    @DisplayName("al inicio cada elemento está solo en su grupo")
    void inicial() {
        ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(4);
        assertEquals(4, grupos.cantidadGrupos());
        assertEquals(4, grupos.cantidadElementos());
        assertFalse(grupos.mismoGrupo(0, 1));
        assertEquals(1, grupos.tamanio(3));
        assertEquals(List.of(1, 1, 1, 1), grupos.tamanios());
    }

    @Test
    @DisplayName("unir: junta grupos de forma transitiva y actualiza tamaños")
    void unir() {
        ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(5);
        assertTrue(grupos.unir(0, 1));
        assertTrue(grupos.unir(3, 4));
        assertTrue(grupos.unir(1, 4));
        assertTrue(grupos.mismoGrupo(0, 3));
        assertEquals(4, grupos.tamanio(0));
        assertEquals(2, grupos.cantidadGrupos());
        assertEquals(List.of(4, 1), grupos.tamanios());
    }

    @Test
    @DisplayName("unir: devuelve false si ya estaban en el mismo grupo")
    void unir_mismoGrupo() {
        ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(3);
        grupos.unir(0, 1);
        grupos.unir(1, 2);
        assertFalse(grupos.unir(0, 2));
        assertFalse(grupos.unir(2, 2));
        assertEquals(1, grupos.cantidadGrupos());
    }

    @Test
    @DisplayName("una cadena larga de uniones no es lenta (compresión de caminos)")
    void cadenaLarga() {
        int n = 1_000_000;
        ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(n);
        for (int i = 1; i < n; i++) grupos.unir(i - 1, i);
        assertEquals(1, grupos.cantidadGrupos());
        assertEquals(n, grupos.tamanio(0));
    }

    @Property
    @Label("coincide con recalcular los grupos fusionando conjuntos a mano")
    void coincideConFuerzaBruta(
            @ForAll @Size(max = 30) List<@IntRange(min = 0, max = 11) Integer> extremosA,
            @ForAll @Size(max = 30) List<@IntRange(min = 0, max = 11) Integer> extremosB) {
        int n = 12;
        ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(n);

        // Fuerza bruta: lista de conjuntos; unir = fusionar los dos conjuntos
        List<Set<Integer>> conjuntos = new ArrayList<>();
        for (int i = 0; i < n; i++) conjuntos.add(new HashSet<>(Set.of(i)));

        for (int k = 0; k < Math.min(extremosA.size(), extremosB.size()); k++) {
            int a = extremosA.get(k);
            int b = extremosB.get(k);
            Set<Integer> conjuntoA = conjuntos.stream().filter(c -> c.contains(a)).findFirst().orElseThrow();
            Set<Integer> conjuntoB = conjuntos.stream().filter(c -> c.contains(b)).findFirst().orElseThrow();
            boolean distintos = conjuntoA != conjuntoB;
            if (distintos) {
                conjuntoA.addAll(conjuntoB);
                conjuntos.remove(conjuntoB);
            }
            assertEquals(distintos, grupos.unir(a, b));
        }

        assertEquals(conjuntos.size(), grupos.cantidadGrupos());
        assertEquals(conjuntos.stream().map(Set::size).sorted((x, y) -> y - x).toList(), grupos.tamanios());
        for (int a = 0; a < n; a++) {
            for (int b = 0; b < n; b++) {
                int fa = a, fb = b;
                boolean esperado = conjuntos.stream().anyMatch(c -> c.contains(fa) && c.contains(fb));
                assertEquals(esperado, grupos.mismoGrupo(a, b));
            }
        }
    }
}
