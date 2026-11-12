package advent.code.y2024;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 1 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 1: Histeria del Historiador")
class Dia01Prueba {

    private Dia01 dia01;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "3   4",
            "4   3",
            "2   5",
            "1   3",
            "3   9",
            "3   3"
    );

    @BeforeEach
    void configurar() {
        dia01 = new Dia01();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("11", dia01.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: separa las dos columnas en el orden del input")
    void parte1_parseo() {
        Dia01.Listas listas = Dia01.Listas.parsear(EJEMPLO);
        assertEquals(List.of(3L, 4L, 2L, 1L, 3L, 3L), listas.izquierda());
        assertEquals(List.of(4L, 3L, 5L, 3L, 9L, 3L), listas.derecha());
        assertThrows(IllegalArgumentException.class, () -> Dia01.Listas.parsear(List.of("1 2 3")));
    }

    @Test
    @DisplayName("Parte 1: la distancia no depende del orden de las líneas ni de qué lista es mayor")
    void parte1_simetria() {
        assertEquals("0", dia01.parte1(List.of("5   1", "1   5")));       // ordenadas son iguales
        assertEquals("6", dia01.parte1(List.of("10   4")));
        assertEquals("6", dia01.parte1(List.of("4   10")));
    }

    @Property
    @Label("Parte 1: coincide con emparejar sacando siempre el mínimo de cada lista")
    void parte1_coincideConEmparejarMinimos(
            @ForAll @Size(max = 20) List<@IntRange(min = 1, max = 99) Integer> izquierda,
            @ForAll @Size(max = 20) List<@IntRange(min = 1, max = 99) Integer> derecha) {
        int n = Math.min(izquierda.size(), derecha.size());
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < n; i++) lineas.add(izquierda.get(i) + "   " + derecha.get(i));

        // Fuerza bruta: sacar repetidamente el mínimo de cada lista y sumar la diferencia
        List<Integer> restanIzquierda = new ArrayList<>(izquierda.subList(0, n));
        List<Integer> restanDerecha = new ArrayList<>(derecha.subList(0, n));
        long esperado = 0;
        while (!restanIzquierda.isEmpty()) {
            Integer menorIzquierda = Collections.min(restanIzquierda);
            Integer menorDerecha = Collections.min(restanDerecha);
            restanIzquierda.remove(menorIzquierda);
            restanDerecha.remove(menorDerecha);
            esperado += Math.abs(menorIzquierda - menorDerecha);
        }
        // jqwik no ejecuta el @BeforeEach de JUnit: se crea la instancia aquí
        assertEquals(String.valueOf(esperado), new Dia01().parte1(lineas));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("31", dia01.parte2(EJEMPLO));   // 9 + 4 + 0 + 0 + 9 + 9
    }

    @Test
    @DisplayName("Parte 2: cada repetición de la izquierda suma de nuevo; los que no aparecen suman 0")
    void parte2_repeticiones() {
        // El 3 aparece 2 veces a la derecha; está 3 veces a la izquierda: 3 · (3·2) = 18
        assertEquals("18", dia01.parte2(List.of("3   3", "3   3", "3   7")));
        assertEquals("0", dia01.parte2(List.of("1   2", "3   4")));
    }

    @Property
    @Label("Parte 2: coincide con contar cada número recorriendo la lista derecha")
    void parte2_coincideConContar(
            @ForAll @Size(max = 20) List<@IntRange(min = 1, max = 9) Integer> izquierda,
            @ForAll @Size(max = 20) List<@IntRange(min = 1, max = 9) Integer> derecha) {
        int n = Math.min(izquierda.size(), derecha.size());
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < n; i++) lineas.add(izquierda.get(i) + "   " + derecha.get(i));

        long esperado = 0;
        for (int i = 0; i < n; i++) {
            int numero = izquierda.get(i);
            long veces = derecha.subList(0, n).stream().filter(d -> d == numero).count();
            esperado += numero * veces;
        }
        assertEquals(String.valueOf(esperado), new Dia01().parte2(lineas));
    }
}
