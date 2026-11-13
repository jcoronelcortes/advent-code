package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 16 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 16: El Suelo Será Lava")
class Dia16Prueba {

    private Dia16 dia16;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            ".|...\\....",
            "|.-.\\.....",
            ".....|-...",
            "........|.",
            "..........",
            ".........\\",
            "..../.\\\\..",
            ".-.-/..|..",
            ".|....-|.\\",
            "..//.|...."
    );

    /** Las casillas energizadas del ejemplo, como las muestra el enunciado. */
    private static final List<String> EJEMPLO_ENERGIZADO = List.of(
            "######....",
            ".#...#....",
            ".#...#####",
            ".#...##...",
            ".#...##...",
            ".#...##...",
            ".#..####..",
            "########..",
            ".#######..",
            ".#...#.#.."
    );

    @BeforeEach
    void configurar() {
        dia16 = new Dia16();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("46", dia16.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: espejos y divisores desvían el haz según el enunciado")
    void parte1_salidas() {
        assertEquals(List.of(Direccion.ARRIBA), Dia16.salidas('/', Direccion.DERECHA));
        assertEquals(List.of(Direccion.ABAJO), Dia16.salidas('/', Direccion.IZQUIERDA));
        assertEquals(List.of(Direccion.DERECHA), Dia16.salidas('/', Direccion.ARRIBA));
        assertEquals(List.of(Direccion.IZQUIERDA), Dia16.salidas('/', Direccion.ABAJO));
        assertEquals(List.of(Direccion.ABAJO), Dia16.salidas('\\', Direccion.DERECHA));
        assertEquals(List.of(Direccion.ARRIBA), Dia16.salidas('\\', Direccion.IZQUIERDA));
        assertEquals(List.of(Direccion.IZQUIERDA), Dia16.salidas('\\', Direccion.ARRIBA));
        assertEquals(List.of(Direccion.DERECHA), Dia16.salidas('\\', Direccion.ABAJO));
        assertEquals(List.of(Direccion.DERECHA), Dia16.salidas('-', Direccion.DERECHA));
        assertEquals(List.of(Direccion.IZQUIERDA, Direccion.DERECHA), Dia16.salidas('-', Direccion.ABAJO));
        assertEquals(List.of(Direccion.ARRIBA), Dia16.salidas('|', Direccion.ARRIBA));
        assertEquals(List.of(Direccion.ARRIBA, Direccion.ABAJO), Dia16.salidas('|', Direccion.IZQUIERDA));
        assertEquals(List.of(Direccion.ABAJO), Dia16.salidas('.', Direccion.ABAJO));
        assertThrows(IllegalArgumentException.class, () -> Dia16.salidas('x', Direccion.ABAJO));
    }

    @Test
    @DisplayName("Parte 1: coincide con simular todos los haces a la vez, paso a paso, hasta que no aparezca nada nuevo")
    void parte1_fuerzaBruta() {
        Grilla artefacto = Grilla.desdeLineas(EJEMPLO);
        Set<Dia16.Haz> vistos = new HashSet<>();
        Set<Dia16.Haz> frente = Set.of(new Dia16.Haz(new Punto(0, 0), Direccion.DERECHA));
        while (!frente.isEmpty()) {
            vistos.addAll(frente);
            Set<Dia16.Haz> siguiente = new HashSet<>();
            for (Dia16.Haz haz : frente) {
                for (Direccion d : Dia16.salidas(artefacto.obtener(haz.posicion()), haz.direccion())) {
                    Dia16.Haz nuevo = new Dia16.Haz(haz.posicion().mover(d), d);
                    if (artefacto.estaDentro(nuevo.posicion()) && !vistos.contains(nuevo)) siguiente.add(nuevo);
                }
            }
            frente = siguiente;
        }
        Grilla esperada = Grilla.desdeLineas(EJEMPLO_ENERGIZADO);
        Set<Punto> energizadas = new HashSet<>();
        for (Dia16.Haz haz : vistos) energizadas.add(haz.posicion());
        assertEquals(Set.copyOf(esperada.buscarTodos('#')), energizadas);
        assertEquals(energizadas.size(), Dia16.energizadas(artefacto, new Dia16.Haz(new Punto(0, 0), Direccion.DERECHA)));
    }

    @Test
    @DisplayName("Parte 1: un haz atrapado en un bucle termina igual")
    void parte1_bucle() {
        // El haz baja por el |, rebota en los espejos de las esquinas y vuelve al | por el costado
        Grilla artefacto = Grilla.desdeLineas(List.of(
                "/-\\",
                "|.|",
                "\\-/"));
        assertEquals(8, Dia16.energizadas(artefacto, new Dia16.Haz(new Punto(1, 0), Direccion.DERECHA)));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("51", dia16.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: entrando por la cuarta casilla de arriba hacia abajo se energizan las 51 del enunciado")
    void parte2_mejorEntrada() {
        assertEquals(51, Dia16.energizadas(Grilla.desdeLineas(EJEMPLO), new Dia16.Haz(new Punto(3, 0), Direccion.ABAJO)));
    }

    @Test
    @DisplayName("Parte 2: las entradas son cada casilla del borde alejándose de él; las esquinas, dos veces")
    void parte2_entradas() {
        List<Dia16.Haz> entradas = Dia16.entradasDesdeLosBordes(3, 4);
        assertEquals(2 * (3 + 4), entradas.size());
        assertEquals(entradas.size(), Set.copyOf(entradas).size());
        for (Dia16.Haz haz : entradas) {
            Punto p = haz.posicion();
            assertTrue(p.dentroDeGrilla(3, 4), haz.toString());
            // Un paso hacia atrás tiene que caer fuera de la grilla: el haz viene de afuera
            assertFalse(p.mover(haz.direccion().opuesto()).dentroDeGrilla(3, 4), haz.toString());
        }
        // La esquina inferior derecha puede entrar hacia la izquierda o hacia arriba
        assertTrue(entradas.contains(new Dia16.Haz(new Punto(3, 2), Direccion.IZQUIERDA)));
        assertTrue(entradas.contains(new Dia16.Haz(new Punto(3, 2), Direccion.ARRIBA)));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar cada casilla y dirección cuyo paso anterior quede fuera de la grilla")
    void parte2_fuerzaBruta() {
        Grilla artefacto = Grilla.desdeLineas(EJEMPLO);
        int mejor = 0;
        for (Punto p : artefacto.puntos()) {
            for (Direccion d : Direccion.values()) {
                if (!artefacto.estaDentro(p.mover(d.opuesto()))) {
                    mejor = Math.max(mejor, Dia16.energizadas(artefacto, new Dia16.Haz(p, d)));
                }
            }
        }
        assertEquals(String.valueOf(mejor), dia16.parte2(EJEMPLO));
    }
}
