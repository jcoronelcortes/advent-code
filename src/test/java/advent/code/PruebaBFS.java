package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        Optional<Integer> resultado = BFS.buscar(inicio, meta::equals, this::vecinosLibres);

        assertTrue(resultado.isPresent());
        assertEquals(8, resultado.get());
    }

    @Test
    @DisplayName("buscar retorna 0 si el inicio es la meta")
    void buscar_inicioEsMeta() {
        Punto inicio = new Punto(0, 0);
        Optional<Integer> resultado = BFS.buscar(inicio, inicio::equals, this::vecinosLibres);
        assertEquals(Optional.of(0), resultado);
    }

    @Test
    @DisplayName("buscar retorna vacío si no hay camino")
    void buscar_sinCamino() {
        // Nodo aislado que no tiene vecinos
        Optional<Integer> resultado = BFS.buscar(
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
        Map<Punto, Integer> distancias = BFS.explorar(inicio, this::vecinosLibres);

        assertEquals(0, distancias.get(new Punto(0, 0)));
        assertEquals(1, distancias.get(new Punto(1, 0)));
        assertEquals(8, distancias.get(new Punto(4, 4)));
        assertFalse(distancias.containsKey(new Punto(1, 1))); // obstáculo
    }
}