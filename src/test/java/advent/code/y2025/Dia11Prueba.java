package advent.code.y2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 11 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 11: Reactor")
class Dia11Prueba {

    private Dia11 dia11;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "aaa: you hhh",
            "you: bbb ccc",
            "bbb: ddd eee",
            "ccc: ddd eee fff",
            "ddd: ggg",
            "eee: out",
            "fff: out",
            "ggg: out",
            "hhh: ccc fff iii",
            "iii: out"
    );

    @BeforeEach
    void configurar() {
        dia11 = new Dia11();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("5", dia11.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: caminos entre otros dispositivos del ejemplo")
    void parte1_otrosCaminos() {
        Map<String, List<String>> conexiones = Dia11.leerConexiones(EJEMPLO);
        assertEquals(1, Dia11.contarCaminos(conexiones, "out", "out"));    // el camino vacío
        assertEquals(2, Dia11.contarCaminos(conexiones, "bbb", "out"));    // ddd→ggg→out y eee→out
        assertEquals(3, Dia11.contarCaminos(conexiones, "ccc", "out"));
        assertEquals(0, Dia11.contarCaminos(conexiones, "you", "hhh"));    // hhh no es alcanzable desde you
        assertEquals(10, Dia11.contarCaminos(conexiones, "aaa", "out"));   // 5 por you + 5 por hhh (3 + 1 + 1)
    }

    @Test
    @DisplayName("Parte 1: interpreta las conexiones")
    void parte1_parseo() {
        Map<String, List<String>> conexiones = Dia11.leerConexiones(EJEMPLO);
        assertEquals(List.of("ddd", "eee", "fff"), conexiones.get("ccc"));
        assertEquals(10, conexiones.size());
        assertFalse(conexiones.containsKey("out"));
    }

    @Test
    @DisplayName("Parte 1: la cantidad de caminos crece exponencialmente y la memoización la cuenta igual")
    void parte1_caminosExponenciales() {
        // Cadena de 60 "diamantes": cada uno duplica los caminos → 2^60
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            lineas.add("n" + i + ": a" + i + " b" + i);
            lineas.add("a" + i + ": n" + (i + 1));
            lineas.add("b" + i + ": n" + (i + 1));
        }
        assertEquals(1L << 60, Dia11.contarCaminos(Dia11.leerConexiones(lineas), "n0", "n60"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2 (otra lista de dispositivos). */
    private static final List<String> EJEMPLO_PARTE_2 = List.of(
            "svr: aaa bbb",
            "aaa: fft",
            "fft: ccc",
            "bbb: tty",
            "tty: ccc",
            "ccc: ddd eee",
            "ddd: hub",
            "hub: fff",
            "eee: dac",
            "dac: fff",
            "fff: ggg hhh",
            "ggg: out",
            "hhh: out"
    );

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("2", dia11.parte2(EJEMPLO_PARTE_2));
    }

    @Test
    @DisplayName("Parte 2: de los 8 caminos del ejemplo, 2 pasan por ambos, y en orden fft → dac")
    void parte2_tramos() {
        Map<String, List<String>> conexiones = Dia11.leerConexiones(EJEMPLO_PARTE_2);
        assertEquals(8, Dia11.contarCaminos(conexiones, "svr", "out"));
        assertEquals(0, Dia11.contarCaminos(conexiones, "dac", "fft"));   // sin ciclos: un solo orden posible
        assertEquals(1, Dia11.contarCaminos(conexiones, "fft", "dac"));
        // svr→fft (1) · fft→dac (1) · dac→out (2) = 2
        assertEquals(2, Dia11.contarCaminosPor(conexiones, "svr", "out", "dac", "fft"));
        // El orden de los dispositivos obligatorios no importa
        assertEquals(2, Dia11.contarCaminosPor(conexiones, "svr", "out", "fft", "dac"));
    }

    @Test
    @DisplayName("Parte 2: coincide con enumerar todos los caminos en un grafo chico")
    void parte2_coincideConEnumerar() {
        // Grafo en capas: cada nodo de una capa se conecta con todos los de la siguiente
        List<String> lineas = new ArrayList<>();
        String[][] capas = {{"svr"}, {"a1", "a2", "dac"}, {"b1", "fft", "b2"}, {"c1", "c2"}, {"out"}};
        for (int capa = 0; capa < capas.length - 1; capa++) {
            for (String nodo : capas[capa]) lineas.add(nodo + ": " + String.join(" ", capas[capa + 1]));
        }
        Map<String, List<String>> conexiones = Dia11.leerConexiones(lineas);

        long esperado = enumerar(conexiones, "svr", "out", new ArrayList<>(List.of("svr")));
        assertEquals(2, esperado);   // svr→dac→fft→{c1,c2}→out
        assertEquals(esperado, Dia11.contarCaminosPor(conexiones, "svr", "out", "dac", "fft"));
    }

    /** Recorre explícitamente cada camino y cuenta los que pasan por dac y fft. */
    private static long enumerar(Map<String, List<String>> conexiones, String actual, String hasta, List<String> camino) {
        if (actual.equals(hasta)) return camino.contains("dac") && camino.contains("fft") ? 1 : 0;
        long total = 0;
        for (String salida : conexiones.getOrDefault(actual, List.of())) {
            camino.add(salida);
            total += enumerar(conexiones, salida, hasta, camino);
            camino.removeLast();
        }
        return total;
    }
}
