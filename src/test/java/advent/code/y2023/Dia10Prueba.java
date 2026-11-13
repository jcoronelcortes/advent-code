package advent.code.y2023;

import advent.code.BFS;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 10 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 10: Laberinto de Tuberías")
class Dia10Prueba {

    private Dia10 dia10;

    /** El lazo cuadrado del enunciado, con S en la esquina de arriba a la izquierda. */
    private static final List<String> CUADRADO = List.of(".....", ".S-7.", ".|.|.", ".L-J.", ".....");

    /** El mismo lazo, rodeado de tuberías que no están conectadas a él. */
    private static final List<String> CUADRADO_CON_RUIDO = List.of("-L|F7", "7S-7|", "L|7||", "-L-J|", "L|-JF");

    /** El lazo más complejo del enunciado. */
    private static final List<String> EJEMPLO = List.of("..F7.", ".FJ|.", "SJ.L7", "|F--J", "LJ...");

    /** El mismo lazo complejo, con tuberías sueltas alrededor. */
    private static final List<String> EJEMPLO_CON_RUIDO = List.of("7-F7-", ".FJ|7", "SJLL7", "|F--J", "LJ.LJ");

    @BeforeEach
    void configurar() {
        dia10 = new Dia10();
    }

    /** Dibuja la distancia de cada casilla del lazo a S, con '.' en las demás (como en el enunciado). */
    private static List<String> dibujoDeDistancias(List<String> lineas) {
        Grilla grilla = Grilla.desdeLineas(lineas);
        Dia10.Lazo lazo = Dia10.Lazo.desde(grilla);
        Grilla dibujo = new Grilla(grilla.filas(), grilla.columnas(), '.');
        for (int i = 0; i < lazo.casillas().size(); i++) {
            dibujo.establecer(lazo.casillas().get(i), Character.forDigit(lazo.distancia(i), 10));
        }
        return List.of(dibujo.toString().split("\n"));
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado (lazo complejo)")
    void parte1_ejemploDelEnunciado() {
        assertEquals("8", dia10.parte1(EJEMPLO));
        assertEquals("8", dia10.parte1(EJEMPLO_CON_RUIDO));
    }

    @Test
    @DisplayName("Parte 1: el lazo cuadrado, con y sin tuberías sueltas alrededor")
    void parte1_cuadrado() {
        assertEquals("4", dia10.parte1(CUADRADO));
        assertEquals("4", dia10.parte1(CUADRADO_CON_RUIDO));
    }

    @Test
    @DisplayName("Parte 1: S es un codo F en el cuadrado y en el ejemplo complejo")
    void parte1_formaDeS() {
        assertEquals('F', Dia10.Lazo.desde(Grilla.desdeLineas(CUADRADO)).formaDeS());
        assertEquals('F', Dia10.Lazo.desde(Grilla.desdeLineas(EJEMPLO_CON_RUIDO)).formaDeS());
    }

    @Test
    @DisplayName("Parte 1: las distancias de cada casilla del lazo, como en los dibujos del enunciado")
    void parte1_dibujos() {
        assertEquals(List.of(".....", ".012.", ".1.3.", ".234.", "....."), dibujoDeDistancias(CUADRADO));
        assertEquals(List.of("..45.", ".236.", "01.78", "14567", "23..."), dibujoDeDistancias(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: coincide con una BFS desde S por las tuberías conectadas en los dos sentidos")
    void parte1_bfs() {
        for (List<String> lineas : List.of(CUADRADO_CON_RUIDO, EJEMPLO_CON_RUIDO)) {
            Grilla grilla = Grilla.desdeLineas(lineas);
            Punto inicio = grilla.buscarUnico(Dia10.INICIO);
            char formaDeS = Dia10.Lazo.desde(grilla).formaDeS();
            Map<Punto, Long> distancias = BFS.explorar(inicio, punto -> {
                char tuberia = punto.equals(inicio) ? formaDeS : grilla.obtener(punto);
                List<Punto> vecinos = new ArrayList<>();
                for (Direccion direccion : Dia10.conexiones(tuberia)) {
                    Punto vecino = punto.mover(direccion);
                    char otra = vecino.equals(inicio) ? formaDeS : grilla.obtener(vecino, '.');
                    if (Dia10.conexiones(otra).contains(direccion.opuesto())) vecinos.add(vecino);
                }
                return vecinos;
            });
            long maxima = distancias.values().stream().mapToLong(Long::longValue).max().orElseThrow();
            assertEquals(String.valueOf(maxima), dia10.parte1(lineas));
        }
    }

    @Test
    @DisplayName("Parte 1: si S no conecta con exactamente dos tuberías, falla con un mensaje claro")
    void parte1_sMalConectada() {
        assertThrows(IllegalArgumentException.class, () -> dia10.parte1(List.of("...", ".S.", "...")));
        assertThrows(IllegalArgumentException.class, () -> dia10.parte1(List.of(".|.", "-S-", ".|.")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Primer ejemplo de la parte 2: dos zonas encerradas, y el centro afuera. */
    private static final List<String> ENCIERRA_4 = List.of(
            "...........", ".S-------7.", ".|F-----7|.", ".||.....||.", ".||.....||.",
            ".|L-7.F-J|.", ".|..|.|..|.", ".L--J.L--J.", "...........");

    /** El mismo, pero sin hueco entre las tuberías: igual se puede "colar" entre ellas. */
    private static final List<String> ENCIERRA_4_SIN_HUECO = List.of(
            "..........", ".S------7.", ".|F----7|.", ".||OOOO||.", ".||OOOO||.",
            ".|L-7F-J|.", ".|II||II|.", ".L--JL--J.", "..........");

    /** Ejemplo más grande, con 8 casillas encerradas. */
    private static final List<String> ENCIERRA_8 = List.of(
            ".F----7F7F7F7F-7....", ".|F--7||||||||FJ....", ".||.FJ||||||||L7....", "FJL7L7LJLJ||LJ.L-7..",
            "L--J.L7...LJS7F-7L7.", "....F-J..F7FJ|L7L7L7", "....L7.F7||L7|.L7L7|", ".....|FJLJ|FJ|F7|.LJ",
            "....FJL-7.||.||||...", "....L---J.LJ.LJLJ...");

    /** Ejemplo con muchas tuberías sueltas, con 10 casillas encerradas. */
    private static final List<String> ENCIERRA_10 = List.of(
            "FF7FSF7F7F7F7F7F---7", "L|LJ||||||||||||F--J", "FL-7LJLJ||||||LJL-77", "F--JF--7||LJLJ7F7FJ-",
            "L---JF-JLJ.||-FJLJJ7", "|F|F-JF---7F7-L7L|7|", "|FFJF7L7F-JF7|JL---7", "7-L-JL7||F7|L7F-7F7|",
            "L.L7LFJ|||||FJL7||LJ", "L7JLJL-JLJLJL--JLJ.L");

    /**
     * Barrido por filas, independiente de Pick: una casilla que no es del lazo
     * está adentro si a su izquierda hay una cantidad impar de casillas del
     * lazo que conectan hacia arriba ({@code |}, {@code L}, {@code J}). Mirar
     * solo las que conectan hacia arriba resuelve los tramos horizontales:
     * {@code L--7} cruza una vez y {@code L--J} no cruza.
     */
    private static List<Punto> encerradasPorBarrido(List<String> lineas) {
        Grilla grilla = Grilla.desdeLineas(lineas);
        Dia10.Lazo lazo = Dia10.Lazo.desde(grilla);
        Set<Punto> delLazo = new HashSet<>(lazo.casillas());
        List<Punto> encerradas = new ArrayList<>();
        for (int y = 0; y < grilla.filas(); y++) {
            boolean adentro = false;
            for (int x = 0; x < grilla.columnas(); x++) {
                Punto punto = new Punto(x, y);
                if (delLazo.contains(punto)) {
                    char tuberia = punto.equals(lazo.casillas().getFirst()) ? lazo.formaDeS() : grilla.obtener(punto);
                    if (Dia10.conexiones(tuberia).contains(Direccion.ARRIBA)) adentro = !adentro;
                } else if (adentro) {
                    encerradas.add(punto);
                }
            }
        }
        return encerradas;
    }

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("4", dia10.parte2(ENCIERRA_4));
        assertEquals("8", dia10.parte2(ENCIERRA_8));
        assertEquals("10", dia10.parte2(ENCIERRA_10));
    }

    @Test
    @DisplayName("Parte 2: colarse entre tuberías también es estar afuera")
    void parte2_sinHueco() {
        assertEquals("4", dia10.parte2(ENCIERRA_4_SIN_HUECO));
    }

    @Test
    @DisplayName("Parte 2: el lazo cuadrado encierra 1 casilla, y su área es 4")
    void parte2_cuadrado() {
        Dia10.Lazo lazo = Dia10.Lazo.desde(Grilla.desdeLineas(CUADRADO));
        assertEquals(8, lazo.dobleArea());   // centros de (1,1) a (3,3): un cuadrado de 2 × 2
        assertEquals(1, lazo.casillasEncerradas());
    }

    @Test
    @DisplayName("Parte 2: Pick coincide con el barrido por filas, y las casillas son las marcadas con I en el enunciado")
    void parte2_barrido() {
        for (List<String> lineas : List.of(ENCIERRA_4, ENCIERRA_4_SIN_HUECO, ENCIERRA_8, ENCIERRA_10, CUADRADO, EJEMPLO)) {
            assertEquals(String.valueOf(encerradasPorBarrido(lineas).size()), dia10.parte2(lineas));
        }
        // las marcas I del ejemplo de 10
        List<String> marcado = List.of(
                "FF7FSF7F7F7F7F7F---7", "L|LJ||||||||||||F--J", "FL-7LJLJ||||||LJL-77", "F--JF--7||LJLJIF7FJ-",
                "L---JF-JLJIIIIFJLJJ7", "|F|F-JF---7IIIL7L|7|", "|FFJF7L7F-JF7IIL---7", "7-L-JL7||F7|L7F-7F7|",
                "L.L7LFJ|||||FJL7||LJ", "L7JLJL-JLJLJL--JLJ.L");
        Grilla conMarcas = Grilla.desdeLineas(marcado);
        assertEquals(new HashSet<>(conMarcas.buscarTodos('I')),
                new HashSet<>(encerradasPorBarrido(ENCIERRA_10)));
    }
}
