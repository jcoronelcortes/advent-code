package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dijkstra")
class PruebaDijkstra {

    // -------------------------------------------------------------------------
    // Grafo con pesos explícitos
    //
    //        1        2
    //   A ------ B ------ D ---1--- F
    //   |        |
    //   4        1               G (aislado)
    //   |        |
    //   C ---1-- E
    //
    // A→C: A-B-E-C = 3 (mejor que A-C directo = 4)
    // -------------------------------------------------------------------------

    private static final Map<String, List<Dijkstra.Arista<String>>> GRAFO = Map.of(
            "A", List.of(new Dijkstra.Arista<>("B", 1), new Dijkstra.Arista<>("C", 4)),
            "B", List.of(new Dijkstra.Arista<>("A", 1), new Dijkstra.Arista<>("D", 2), new Dijkstra.Arista<>("E", 1)),
            "C", List.of(new Dijkstra.Arista<>("A", 4), new Dijkstra.Arista<>("E", 1)),
            "D", List.of(new Dijkstra.Arista<>("B", 2), new Dijkstra.Arista<>("F", 1)),
            "E", List.of(new Dijkstra.Arista<>("B", 1), new Dijkstra.Arista<>("C", 1)),
            "F", List.of(new Dijkstra.Arista<>("D", 1)),
            "G", List.of()
    );

    private static final Function<String, Iterable<Dijkstra.Arista<String>>> VECINOS_GRAFO =
            nodo -> GRAFO.getOrDefault(nodo, List.of());

    @Test
    @DisplayName("costoMinimo: prefiere el camino largo pero más barato")
    void costoMinimo_grafo() {
        assertEquals(Optional.of(3L), Dijkstra.costoMinimo("A", "C"::equals, VECINOS_GRAFO));
        assertEquals(Optional.of(4L), Dijkstra.costoMinimo("A", "F"::equals, VECINOS_GRAFO));
    }

    @Test
    @DisplayName("costoMinimo: 0 si el inicio es la meta, vacío si es inalcanzable")
    void costoMinimo_casosBorde() {
        assertEquals(Optional.of(0L), Dijkstra.costoMinimo("A", "A"::equals, VECINOS_GRAFO));
        assertEquals(Optional.empty(), Dijkstra.costoMinimo("A", "G"::equals, VECINOS_GRAFO));
    }

    @Test
    @DisplayName("buscarCamino: devuelve los nodos del camino en orden")
    void buscarCamino_nodos() {
        Camino<String> camino = Dijkstra.buscarCamino("A", "C"::equals, VECINOS_GRAFO).orElseThrow();
        assertEquals(List.of("A", "B", "E", "C"), camino.nodos());
        assertEquals(3, camino.costo());
        assertEquals("C", camino.meta());
    }

    @Test
    @DisplayName("buscarCamino: con varios inicios toma el más cercano")
    void buscarCamino_variosInicios() {
        Camino<String> camino = Dijkstra.buscarCamino(
                List.of("A", "F"), "D"::equals, VECINOS_GRAFO, nodo -> 0).orElseThrow();
        assertEquals(List.of("F", "D"), camino.nodos());
        assertEquals(1, camino.costo());
    }

    @Test
    @DisplayName("explorar: costo mínimo a todos los nodos alcanzables")
    void explorar_grafo() {
        Map<String, Long> costos = Dijkstra.explorar("A", VECINOS_GRAFO);
        assertEquals(Map.of("A", 0L, "B", 1L, "C", 3L, "D", 3L, "E", 2L, "F", 4L), costos);
    }

    @Test
    @DisplayName("lanza error con costos negativos")
    void costoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> Dijkstra.costoMinimo(
                "A", "B"::equals, nodo -> List.of(new Dijkstra.Arista<>("B", -1))));
    }

    // -------------------------------------------------------------------------
    // Grillas
    // -------------------------------------------------------------------------

    private static final Grilla GRILLA_SIMPLE = Grilla.desdeLineas(List.of(
            "S....",
            ".###.",
            ".#...",
            ".#.#.",
            "...#E"
    ));

    @Test
    @DisplayName("vecinosEnGrilla: con costo 1 coincide con BFS")
    void grilla_coincideConBFS() {
        Punto inicio = GRILLA_SIMPLE.buscar('S').orElseThrow();
        Punto meta = GRILLA_SIMPLE.buscar('E').orElseThrow();

        Optional<Long> costo = Dijkstra.costoMinimo(inicio, meta::equals,
                Dijkstra.vecinosEnGrilla(GRILLA_SIMPLE, c -> c != '#'));
        Optional<Long> pasos = BFS.buscar(inicio, meta::equals,
                p -> GRILLA_SIMPLE.vecinos(p, c -> c != '#'));

        assertEquals(Optional.of(8L), costo);
        assertEquals(Optional.of(8L), pasos);
    }

    @Test
    @DisplayName("A*: con heurística Manhattan da el mismo costo que Dijkstra")
    void aEstrella_mismoCosto() {
        Punto inicio = GRILLA_SIMPLE.buscar('S').orElseThrow();
        Punto meta = GRILLA_SIMPLE.buscar('E').orElseThrow();

        Camino<Punto> camino = Dijkstra.buscarCamino(
                List.of(inicio), meta::equals,
                Dijkstra.vecinosEnGrilla(GRILLA_SIMPLE, c -> c != '#'),
                p -> p.distanciaManhattan(meta)).orElseThrow();

        assertEquals(8, camino.costo());
        assertEquals(9, camino.nodos().size());   // 8 pasos → 9 casillas
        assertEquals(inicio, camino.nodos().getFirst());
        assertEquals(meta, camino.meta());
        camino.nodos().forEach(p -> assertNotEquals('#', GRILLA_SIMPLE.obtener(p)));
    }

    @Test
    @DisplayName("grilla con pesos: cada dígito es el costo de entrar a esa casilla")
    void grilla_conPesos() {
        Grilla pesos = Grilla.desdeLineas(List.of(
                "1911",
                "1919",
                "1119"
        ));
        // bajar por la izquierda, cruzar abajo, subir por la columna 2 y doblar al final
        Optional<Long> costo = Dijkstra.costoMinimo(new Punto(0, 0), new Punto(3, 0)::equals,
                p -> pesos.vecinos(p).stream()
                        .map(v -> new Dijkstra.Arista<>(v, pesos.obtener(v) - '0'))
                        .toList());
        assertEquals(Optional.of(7L), costo);
    }

    // -------------------------------------------------------------------------
    // Estados compuestos y caminos empatados — laberinto del ejemplo de AoC 2024 día 16
    // (avanzar cuesta 1, girar 90° cuesta 1000, se empieza mirando al este)
    // -------------------------------------------------------------------------

    private record Reno(Punto posicion, Direccion direccion) {}

    private static final Grilla LABERINTO = Grilla.desdeLineas(List.of(
            "###############",
            "#.......#....E#",
            "#.#.###.#.###.#",
            "#.....#.#...#.#",
            "#.###.#####.#.#",
            "#.#.#.......#.#",
            "#.#.#####.###.#",
            "#...........#.#",
            "###.#.#####.#.#",
            "#...#.....#.#.#",
            "#.#.#.###.#.#.#",
            "#.....#...#.#.#",
            "#.###.#.#.#.#.#",
            "#S..#.....#...#",
            "###############"
    ));

    private static Iterable<Dijkstra.Arista<Reno>> movimientosReno(Reno reno) {
        List<Dijkstra.Arista<Reno>> aristas = new ArrayList<>(List.of(
                new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroDerecha()), 1000),
                new Dijkstra.Arista<>(new Reno(reno.posicion(), reno.direccion().giroIzquierda()), 1000)
        ));
        Punto adelante = reno.posicion().mover(reno.direccion());
        if (LABERINTO.obtener(adelante, '#') != '#') {
            aristas.add(new Dijkstra.Arista<>(new Reno(adelante, reno.direccion()), 1));
        }
        return aristas;
    }

    @Test
    @DisplayName("estado compuesto (posición + dirección): costo mínimo 7036")
    void laberinto_costoMinimo() {
        Reno inicio = new Reno(LABERINTO.buscar('S').orElseThrow(), Direccion.DERECHA);
        Punto meta = LABERINTO.buscar('E').orElseThrow();

        assertEquals(Optional.of(7036L),
                Dijkstra.costoMinimo(inicio, reno -> reno.posicion().equals(meta), PruebaDijkstra::movimientosReno));
    }

    @Test
    @DisplayName("nodosEnCaminosMinimos: 45 casillas están en algún camino óptimo")
    void laberinto_casillasEnCaminosMinimos() {
        Reno inicio = new Reno(LABERINTO.buscar('S').orElseThrow(), Direccion.DERECHA);
        Punto meta = LABERINTO.buscar('E').orElseThrow();

        Set<Reno> estados = Dijkstra.nodosEnCaminosMinimos(
                inicio, reno -> reno.posicion().equals(meta), PruebaDijkstra::movimientosReno);
        Set<Punto> casillas = estados.stream().map(Reno::posicion).collect(Collectors.toSet());

        assertEquals(45, casillas.size());
        assertTrue(casillas.contains(inicio.posicion()));
        assertTrue(casillas.contains(meta));
    }

    @Test
    @DisplayName("nodosEnCaminosMinimos: vacío si la meta es inalcanzable")
    void nodosEnCaminosMinimos_inalcanzable() {
        assertEquals(Set.of(), Dijkstra.nodosEnCaminosMinimos("A", "G"::equals, VECINOS_GRAFO));
    }

    @Test
    @DisplayName("nodosEnCaminosMinimos: incluye las dos ramas de un empate")
    void nodosEnCaminosMinimos_empate() {
        // A→B→D y A→C→D cuestan 2; A→E→D cuesta 3
        Map<String, List<Dijkstra.Arista<String>>> rombo = Map.of(
                "A", List.of(new Dijkstra.Arista<>("B", 1), new Dijkstra.Arista<>("C", 1), new Dijkstra.Arista<>("E", 1)),
                "B", List.of(new Dijkstra.Arista<>("D", 1)),
                "C", List.of(new Dijkstra.Arista<>("D", 1)),
                "E", List.of(new Dijkstra.Arista<>("D", 2))
        );
        assertEquals(Set.of("A", "B", "C", "D"),
                Dijkstra.nodosEnCaminosMinimos("A", "D"::equals, n -> rombo.getOrDefault(n, List.of())));
    }
}