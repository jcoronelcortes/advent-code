package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BFS")
class PruebaBFS {

    // Grilla 5x5 con obstáculos en '#'
    // . . . . .
    // . # # # .
    // . # . # .
    // . # # # .
    // . . . . .
    private static final Set<Punto> OBSTACULOS = Set.of(
            new Punto(1,1), new Punto(2,1), new Punto(3,1),
            new Punto(1,2),                 new Punto(3,2),
            new Punto(1,3), new Punto(2,3), new Punto(3,3)
    );

    private Iterable<Punto> vecinosLibres(Punto p) {
        return p.vecinos().stream()
                .filter(v -> v.dentroDeGrilla(5, 5))
                .filter(v -> !OBSTACULOS.contains(v))
                .toList();
    }

    @Test
    @DisplayName("buscar encuentra la distancia mínima")
    void buscar_encuentraDistancia() {
        Punto inicio = new Punto(0, 0);
        Punto meta   = new Punto(4, 4);

        Optional<Long> resultado = BFS.buscar(inicio, meta::equals, this::vecinosLibres);

        assertTrue(resultado.isPresent());
        assertEquals(8L, resultado.get());
    }

    @Test
    @DisplayName("buscar retorna 0 si el inicio es la meta")
    void buscar_inicioEsMeta() {
        Punto inicio = new Punto(0, 0);
        Optional<Long> resultado = BFS.buscar(inicio, inicio::equals, this::vecinosLibres);
        assertEquals(Optional.of(0L), resultado);
    }

    @Test
    @DisplayName("buscar retorna vacío si no hay camino")
    void buscar_sinCamino() {
        // Nodo aislado que no tiene vecinos
        Optional<Long> resultado = BFS.buscar(
                new Punto(2, 2),
                p -> p.equals(new Punto(0, 0)),
                p -> p.vecinos().stream()
                        .filter(OBSTACULOS::contains)
                        .toList()
        );
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("explorar calcula distancias a todos los nodos alcanzables")
    void explorar_calculaDistancias() {
        Punto inicio = new Punto(0, 0);
        Map<Punto, Long> distancias = BFS.explorar(inicio, this::vecinosLibres);

        assertEquals(0L, distancias.get(new Punto(0, 0)));
        assertEquals(1L, distancias.get(new Punto(1, 0)));
        assertEquals(8L, distancias.get(new Punto(4, 4)));
        assertFalse(distancias.containsKey(new Punto(1, 1))); // obstáculo
    }

    @Test
    @DisplayName("buscarCamino devuelve los nodos del camino más corto")
    void buscarCamino_nodos() {
        Punto inicio = new Punto(0, 0);
        Punto meta   = new Punto(4, 4);

        Camino<Punto> camino = BFS.buscarCamino(inicio, meta::equals, this::vecinosLibres).orElseThrow();

        assertEquals(8, camino.costo());
        assertEquals(8, camino.pasos());
        assertEquals(inicio, camino.inicio());
        assertEquals(meta, camino.meta());
        for (int i = 1; i < camino.nodos().size(); i++) {
            assertEquals(1, camino.nodos().get(i - 1).distanciaManhattan(camino.nodos().get(i)),
                    "cada paso avanza una casilla");
            assertFalse(OBSTACULOS.contains(camino.nodos().get(i)));
        }
    }

    @Test
    @DisplayName("buscarCamino con inicio igual a meta: camino de un solo nodo")
    void buscarCamino_inicioEsMeta() {
        Punto inicio = new Punto(0, 0);
        Camino<Punto> camino = BFS.buscarCamino(inicio, inicio::equals, this::vecinosLibres).orElseThrow();
        assertEquals(List.of(inicio), camino.nodos());
        assertEquals(0, camino.pasos());
    }
}
