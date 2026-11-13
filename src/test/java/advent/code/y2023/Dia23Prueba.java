package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 23 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 23: Una Larga Caminata")
class Dia23Prueba {

    private Dia23 dia23;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "#.#####################",
            "#.......#########...###",
            "#######.#########.#.###",
            "###.....#.>.>.###.#.###",
            "###v#####.#v#.###.#.###",
            "###.>...#.#.#.....#...#",
            "###v###.#.#.#########.#",
            "###...#.#.#.......#...#",
            "#####.#.#.#######.#.###",
            "#.....#.#.#.......#...#",
            "#.#####.#.#.#########v#",
            "#.#...#...#...###...>.#",
            "#.#.#v#######v###.###v#",
            "#...#.>.#...>.>.#.###.#",
            "#####v#.#.###v#.#.###.#",
            "#.....#...#...#.#.#...#",
            "#.#########.###.#.#.###",
            "#...###...#...#...#.###",
            "###.###.#.###v#####v###",
            "#...#...#.#.>.>.#.>.###",
            "#.###.###.#.###.#.#v###",
            "#.....###...###...#...#",
            "#####################.#"
    );

    /**
     * Fuerza bruta: todas las caminatas casilla por casilla, sin comprimir,
     * con una búsqueda en profundidad que no repite casillas.
     *
     * @return el largo de cada caminata que llega a la salida
     */
    private static List<Integer> caminatas(Grilla mapa, boolean pendientes) {
        Punto inicio = mapa.buscarTodos('.').stream().filter(p -> p.y() == 0).findFirst().orElseThrow();
        List<Integer> largos = new ArrayList<>();
        recorrer(mapa, pendientes, inicio, new HashSet<>(Set.of(inicio)), largos);
        return largos;
    }

    private static void recorrer(Grilla mapa, boolean pendientes, Punto actual, Set<Punto> pisadas, List<Integer> largos) {
        if (actual.y() == mapa.filas() - 1) {
            largos.add(pisadas.size() - 1);
            return;
        }
        char c = mapa.obtener(actual);
        List<Direccion> direcciones = pendientes && c != '.' ? List.of(Direccion.desdeFlecha(c)) : List.of(Direccion.values());
        for (Direccion d : direcciones) {
            Punto siguiente = actual.mover(d);
            if (mapa.estaDentro(siguiente) && mapa.obtener(siguiente) != '#' && pisadas.add(siguiente)) {
                recorrer(mapa, pendientes, siguiente, pisadas, largos);
                pisadas.remove(siguiente);
            }
        }
    }

    @BeforeEach
    void configurar() {
        dia23 = new Dia23();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("94", dia23.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las caminatas del ejemplo miden 94, 90, 86, 82, 82 y 74, como dice el enunciado")
    void parte1_todasLasCaminatas() {
        List<Integer> largos = new ArrayList<>(caminatas(Grilla.desdeLineas(EJEMPLO), true));
        largos.sort(Comparator.reverseOrder());
        assertEquals(List.of(94, 90, 86, 82, 82, 74), largos);
    }

    @Test
    @DisplayName("Parte 1: el grafo comprimido del ejemplo tiene la entrada, la salida y 7 cruces, y pasillos de un solo sentido")
    void parte1_grafoComprimido() {
        Dia23.Grafo grafo = Dia23.comprimir(Grilla.desdeLineas(EJEMPLO), true);
        assertEquals(2 + 7, grafo.nodos().size());
        assertEquals(new Punto(1, 0), grafo.nodos().get(0));
        assertEquals(new Punto(21, 22), grafo.nodos().get(1));
        // De la entrada sale un solo pasillo, al primer cruce; a la entrada no vuelve ninguno (por las pendientes)
        assertEquals(1, grafo.aristas().get(0).size());
        assertTrue(grafo.aristas().stream().flatMap(List::stream).noneMatch(a -> a.destino() == 0));
    }

    @Test
    @DisplayName("Parte 1: un pasillo con una pendiente en contra no se puede recorrer")
    void parte1_pendienteEnContra() {
        // El único camino baja y después tiene que subir por una pendiente que apunta hacia arriba
        Grilla mapa = Grilla.desdeLineas(List.of(
                "#.###",
                "#.###",
                "#.^.#",
                "###.#"));
        assertThrows(IllegalStateException.class, () -> Dia23.caminoMasLargo(Dia23.comprimir(mapa, true)));
        assertEquals(List.of(), caminatas(mapa, true));
    }

    @Test
    @DisplayName("Parte 1: un mapa sin una única entrada o salida falla con un mensaje claro")
    void parte1_mapaInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> Dia23.comprimir(Grilla.desdeLineas(List.of("#..#", "#..#", "#.##")), true));
        assertThrows(IllegalArgumentException.class,
                () -> Dia23.comprimir(Grilla.desdeLineas(List.of("#.##", "#..#", "####")), true));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("154", dia23.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar todas las caminatas casilla por casilla, subiendo pendientes")
    void parte2_fuerzaBruta() {
        List<Integer> largos = caminatas(Grilla.desdeLineas(EJEMPLO), false);
        assertEquals(154, largos.stream().mapToInt(Integer::intValue).max().orElseThrow());
        // Sin pendientes hay muchas más caminatas que con ellas
        assertTrue(largos.size() > caminatas(Grilla.desdeLineas(EJEMPLO), true).size());
    }

    @Test
    @DisplayName("Parte 2: sin pendientes los pasillos van en los dos sentidos, y la pendiente en contra ya no corta")
    void parte2_dobleSentido() {
        Dia23.Grafo grafo = Dia23.comprimir(Grilla.desdeLineas(EJEMPLO), false);
        for (int i = 0; i < grafo.nodos().size(); i++) {
            for (Dia23.Arista arista : grafo.aristas().get(i)) {
                int desde = i;
                assertTrue(grafo.aristas().get(arista.destino()).stream()
                                .anyMatch(vuelta -> vuelta.destino() == desde && vuelta.largo() == arista.largo()),
                        "falta la vuelta de " + i + " → " + arista.destino());
            }
        }
        Grilla mapa = Grilla.desdeLineas(List.of(
                "#.###",
                "#.###",
                "#.^.#",
                "###.#"));
        assertEquals(5, Dia23.caminoMasLargo(Dia23.comprimir(mapa, false)));
        assertEquals(List.of(5), caminatas(mapa, false));
    }

    @Test
    @DisplayName("Parte 2: en un mapa con un ciclo, la caminata más larga da la vuelta larga sin repetir casillas")
    void parte2_ciclo() {
        // Dos caminos entre los cruces: uno corto por el medio y uno largo por el borde
        Grilla mapa = Grilla.desdeLineas(List.of(
                "#.#####",
                "#.....#",
                "#.#.#.#",
                "#.....#",
                "#####.#"));
        long esperado = caminatas(mapa, false).stream().mapToInt(Integer::intValue).max().orElseThrow();
        assertEquals(esperado, Dia23.caminoMasLargo(Dia23.comprimir(mapa, false)));
    }
}
