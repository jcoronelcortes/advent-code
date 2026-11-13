package advent.code.y2023;

import advent.code.Dijkstra;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 17 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 17: Crisol Torpe")
class Dia17Prueba {

    private Dia17 dia17;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "2413432311323",
            "3215453535623",
            "3255245654254",
            "3446585845452",
            "4546657867536",
            "1438598798454",
            "4457876987766",
            "3637877979653",
            "4654967986887",
            "4564679986453",
            "1224686865563",
            "2546548887735",
            "4322674655533"
    );

    @BeforeEach
    void configurar() {
        dia17 = new Dia17();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("102", dia17.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los tramos giran 90°, miden de 1 a 3 manzanas, no salen del mapa y suman el calor de cada una")
    void parte1_tramos() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        // Desde la esquina, viniendo de un tramo vertical: solo puede ir a la derecha (la izquierda está fuera)
        List<Dijkstra.Arista<Dia17.Estado>> tramos =
                Dia17.tramos(mapa, new Dia17.Estado(new Punto(0, 0), Direccion.ABAJO), 1, 3);
        assertEquals(List.of(
                new Dijkstra.Arista<>(new Dia17.Estado(new Punto(1, 0), Direccion.DERECHA), 4),
                new Dijkstra.Arista<>(new Dia17.Estado(new Punto(2, 0), Direccion.DERECHA), 4 + 1),
                new Dijkstra.Arista<>(new Dia17.Estado(new Punto(3, 0), Direccion.DERECHA), 4 + 1 + 3)
        ), tramos);
    }

    @Test
    @DisplayName("Parte 1: coincide con Dijkstra paso a paso sobre (manzana, dirección, manzanas en línea recta)")
    void parte1_fuerzaBruta() {
        assertEquals(102, pasoAPaso(Grilla.desdeLineas(EJEMPLO), 1, 3));
        assertEquals(Dia17.menorPerdida(Grilla.desdeLineas(EJEMPLO), 1, 3), pasoAPaso(Grilla.desdeLineas(EJEMPLO), 1, 3));
        // Un mapa donde conviene zigzaguear porque ir derecho por el borde cuesta más
        List<String> zigzag = List.of("11999", "91111", "99991");
        assertEquals(pasoAPaso(Grilla.desdeLineas(zigzag), 1, 3), Dia17.menorPerdida(Grilla.desdeLineas(zigzag), 1, 3));
    }

    @Test
    @DisplayName("Parte 1: con tramos de una sola manzana el crisol tiene que zigzaguear")
    void parte1_tramosDeUno() {
        // En una franja de 2 filas, con tramos de 1 no puede ir derecho: alterna fila de arriba y de abajo,
        // derecha, abajo, derecha, arriba, derecha, abajo: 6 manzanas (con tramos de 3 bastan 4)
        Grilla mapa = Grilla.desdeLineas(List.of("1111", "1111"));
        assertEquals(pasoAPaso(mapa, 1, 1), Dia17.menorPerdida(mapa, 1, 1));
        assertEquals(6, Dia17.menorPerdida(mapa, 1, 1));
        assertEquals(4, Dia17.menorPerdida(mapa, 1, 3));
    }

    @Test
    @DisplayName("Parte 1: un mapa con algo que no es un dígito falla con un mensaje claro")
    void parte1_mapaInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Dia17.menorPerdida(Grilla.desdeLineas(List.of("12", "3x")), 1, 3));
    }

    /**
     * Fuerza bruta: Dijkstra moviéndose de a una manzana y contando cuántas
     * lleva en línea recta. Solo puede girar, o detenerse en el destino, si
     * lleva al menos {@code minimoRecto}.
     */
    private static long pasoAPaso(Grilla mapa, int minimoRecto, int maximoRecto) {
        record Paso(Punto posicion, Direccion direccion, int recto) {
        }
        Punto destino = new Punto(mapa.columnas() - 1, mapa.filas() - 1);
        List<Paso> inicios = List.of(new Paso(new Punto(0, 0), Direccion.DERECHA, 0),
                new Paso(new Punto(0, 0), Direccion.ABAJO, 0));
        return Dijkstra.buscarCamino(inicios,
                paso -> paso.posicion().equals(destino) && paso.recto() >= minimoRecto, paso -> {
            List<Dijkstra.Arista<Paso>> vecinos = new ArrayList<>();
            for (Direccion d : Direccion.values()) {
                if (d == paso.direccion().opuesto()) continue;
                // Al principio (recto 0) puede salir hacia cualquier lado; después, girar exige el mínimo
                if (d != paso.direccion() && paso.recto() > 0 && paso.recto() < minimoRecto) continue;
                int recto = d == paso.direccion() ? paso.recto() + 1 : 1;
                Punto siguiente = paso.posicion().mover(d);
                if (recto <= maximoRecto && mapa.estaDentro(siguiente)) {
                    vecinos.add(new Dijkstra.Arista<>(new Paso(siguiente, d, recto), mapa.obtener(siguiente) - '0'));
                }
            }
            return vecinos;
        }, paso -> 0).orElseThrow().costo();
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Segundo ejemplo de la parte 2: el camino barato por el borde no sirve al ultra crisol. */
    private static final List<String> EJEMPLO_ULTRA = List.of(
            "111111111111",
            "999999999991",
            "999999999991",
            "999999999991",
            "999999999991"
    );

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("94", dia17.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el segundo ejemplo da 71, no 16, porque no puede girar ni detenerse sin avanzar 4")
    void parte2_segundoEjemplo() {
        assertEquals("71", dia17.parte2(EJEMPLO_ULTRA));
        // Un crisol común sí puede bajar por la columna de la derecha
        assertEquals(11 + 4, Dia17.menorPerdida(Grilla.desdeLineas(EJEMPLO_ULTRA), 1, 11));
    }

    @Test
    @DisplayName("Parte 2: los tramos miden de 4 a 10 manzanas pero suman el calor de todas las que atraviesan")
    void parte2_tramos() {
        Grilla mapa = Grilla.desdeLineas(EJEMPLO);
        List<Dijkstra.Arista<Dia17.Estado>> tramos =
                Dia17.tramos(mapa, new Dia17.Estado(new Punto(0, 0), Direccion.ABAJO), 4, 10);
        // Hacia la derecha: largos 4 a 10 (la fila tiene 13); hacia la izquierda, nada
        assertEquals(7, tramos.size());
        assertEquals(new Dijkstra.Arista<>(new Dia17.Estado(new Punto(4, 0), Direccion.DERECHA), 4 + 1 + 3 + 4),
                tramos.getFirst());
    }

    @Test
    @DisplayName("Parte 2: coincide con Dijkstra paso a paso con mínimo y máximo de manzanas en línea recta")
    void parte2_fuerzaBruta() {
        for (List<String> lineas : List.of(EJEMPLO, EJEMPLO_ULTRA)) {
            Grilla mapa = Grilla.desdeLineas(lineas);
            assertEquals(pasoAPaso(mapa, 4, 10), Dia17.menorPerdida(mapa, 4, 10));
        }
    }

    @Test
    @DisplayName("Parte 2: sin camino posible o con largos inválidos falla con un mensaje claro")
    void parte2_invalido() {
        // En una grilla de 3 × 3 ningún tramo de 4 a 10 entra
        Grilla chica = Grilla.desdeLineas(List.of("111", "111", "111"));
        assertThrows(IllegalStateException.class, () -> Dia17.menorPerdida(chica, 4, 10));
        assertThrows(IllegalArgumentException.class, () -> Dia17.menorPerdida(chica, 0, 3));
        assertThrows(IllegalArgumentException.class, () -> Dia17.menorPerdida(chica, 4, 3));
    }
}
