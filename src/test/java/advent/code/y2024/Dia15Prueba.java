package advent.code.y2024;

import advent.code.Direccion;
import advent.code.Grilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 15 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 15: Problemas en el Almacén")
class Dia15Prueba {

    private Dia15 dia15;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "##########",
            "#..O..O.O#",
            "#......O.#",
            "#.OO..O.O#",
            "#..O@..O.#",
            "#O#..O...#",
            "#O..O..O.#",
            "#.OO.O.OO#",
            "#....O...#",
            "##########",
            "",
            "<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^",
            "vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v",
            "><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<",
            "<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^",
            "^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><",
            "^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^",
            ">^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^",
            "<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>",
            "^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>",
            "v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^"
    );

    /** Ejemplo chico del enunciado. */
    private static final List<String> CHICO = List.of(
            "########",
            "#..O.O.#",
            "##@.O..#",
            "#...O..#",
            "#.#.O..#",
            "#...O..#",
            "#......#",
            "########",
            "",
            "<^^>>>vv<v>>v<<"
    );

    @BeforeEach
    void configurar() {
        dia15 = new Dia15();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo grande del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("10092", dia15.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el ejemplo chico del enunciado")
    void parte1_ejemploChico() {
        assertEquals("2028", dia15.parte1(CHICO));
    }

    @Test
    @DisplayName("Parte 1: el almacén grande al terminar, como en el dibujo del enunciado")
    void parte1_dibujoFinalDelEjemploGrande() {
        Dia15.Almacen almacen = Dia15.Almacen.parsear(EJEMPLO, false);
        almacen.moverTodo();
        assertEquals(String.join("\n", List.of(
                "##########",
                "#.O.O.OOO#",
                "#........#",
                "#OO......#",
                "#OO@.....#",
                "#O#.....O#",
                "#O.....OO#",
                "#O.....OO#",
                "#OO....OO#",
                "##########")), almacen.mapa().toString());
    }

    private record Paso(char flecha, List<String> dibujo) {}

    @Test
    @DisplayName("Parte 1: los 15 movimientos del ejemplo chico, paso a paso (incluye empujes bloqueados por paredes)")
    void parte1_pasosDelEjemploChico() {
        List<Paso> pasos = List.of(
                new Paso('<', List.of("########", "#..O.O.#", "##@.O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('^', List.of("########", "#.@O.O.#", "##..O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('^', List.of("########", "#.@O.O.#", "##..O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('>', List.of("########", "#..@OO.#", "##..O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('>', List.of("########", "#...@OO#", "##..O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('>', List.of("########", "#...@OO#", "##..O..#", "#...O..#", "#.#.O..#", "#...O..#", "#......#", "########")),
                new Paso('v', List.of("########", "#....OO#", "##..@..#", "#...O..#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('v', List.of("########", "#....OO#", "##..@..#", "#...O..#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('<', List.of("########", "#....OO#", "##.@...#", "#...O..#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('v', List.of("########", "#....OO#", "##.....#", "#..@O..#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('>', List.of("########", "#....OO#", "##.....#", "#...@O.#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('>', List.of("########", "#....OO#", "##.....#", "#....@O#", "#.#.O..#", "#...O..#", "#...O..#", "########")),
                new Paso('v', List.of("########", "#....OO#", "##.....#", "#.....O#", "#.#.O@.#", "#...O..#", "#...O..#", "########")),
                new Paso('<', List.of("########", "#....OO#", "##.....#", "#.....O#", "#.#O@..#", "#...O..#", "#...O..#", "########")),
                new Paso('<', List.of("########", "#....OO#", "##.....#", "#.....O#", "#.#O@..#", "#...O..#", "#...O..#", "########")));
        Dia15.Almacen almacen = Dia15.Almacen.parsear(CHICO, false);
        for (Paso paso : pasos) {
            almacen.mover(Direccion.desdeFlecha(paso.flecha()));
            assertEquals(String.join("\n", paso.dibujo()), almacen.mapa().toString(), "después de " + paso.flecha());
        }
    }

    @Test
    @DisplayName("Parte 1: la coordenada GPS de una caja es 100 · fila + columna (el ejemplo da 104)")
    void parte1_coordenadaGps() {
        assertEquals(104, Dia15.sumaGps(Grilla.desdeLineas(List.of("#######", "#...O..", "#......"))));
    }

    @Test
    @DisplayName("Parte 1: los movimientos en varias líneas forman una sola secuencia")
    void parte1_movimientosEnVariasLineas() {
        Dia15.Almacen enUnaLinea = Dia15.Almacen.parsear(List.of("#####", "#@O.#", "#####", "", ">><"), false);
        Dia15.Almacen enVarias = Dia15.Almacen.parsear(List.of("#####", "#@O.#", "#####", "", ">", "><"), false);
        enUnaLinea.moverTodo();
        enVarias.moverTodo();
        assertEquals("#####\n#@.O#\n#####", enUnaLinea.mapa().toString());   // el 2.º '>' choca: la caja ya está contra la pared
        assertEquals(enUnaLinea.mapa(), enVarias.mapa());
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo grande del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("9021", dia15.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el mapa ensanchado del ejemplo grande, como en el enunciado")
    void parte2_ensanchar() {
        List<String> esperado = List.of(
                "####################",
                "##....[]....[]..[]##",
                "##............[]..##",
                "##..[][]....[]..[]##",
                "##....[]@.....[]..##",
                "##[]##....[]......##",
                "##[]....[]....[]..##",
                "##..[][]..[]..[][]##",
                "##........[]......##",
                "####################");
        assertEquals(esperado, Dia15.ensanchar(EJEMPLO.subList(0, 10)));
    }

    @Test
    @DisplayName("Parte 2: el almacén ancho del ejemplo grande al terminar")
    void parte2_dibujoFinalDelEjemploGrande() {
        Dia15.Almacen almacen = Dia15.Almacen.parsear(EJEMPLO, true);
        almacen.moverTodo();
        assertEquals(String.join("\n", List.of(
                "####################",
                "##[].......[].[][]##",
                "##[]...........[].##",
                "##[]........[][][]##",
                "##[]......[]....[]##",
                "##..##......[]....##",
                "##..[]............##",
                "##..@......[].[][]##",
                "##......[][]..[]..##",
                "####################")), almacen.mapa().toString());
    }

    @Test
    @DisplayName("Parte 2: los 11 movimientos del ejemplo ancho, paso a paso (una caja empuja a dos; un empuje bloqueado por una sola mitad)")
    void parte2_pasosDelEjemploAncho() {
        List<String> chico = List.of(
                "#######",
                "#...#.#",
                "#.....#",
                "#..OO@#",
                "#..O..#",
                "#.....#",
                "#######",
                "",
                "<vv<<^^<<^^");
        Dia15.Almacen almacen = Dia15.Almacen.parsear(chico, true);
        assertEquals(String.join("\n", List.of(
                "##############",
                "##......##..##",
                "##..........##",
                "##....[][]@.##",
                "##....[]....##",
                "##..........##",
                "##############")), almacen.mapa().toString(), "estado inicial");
        List<Paso> pasos = List.of(
                new Paso('<', List.of("##############", "##......##..##", "##..........##", "##...[][]@..##", "##....[]....##", "##..........##", "##############")),
                new Paso('v', List.of("##############", "##......##..##", "##..........##", "##...[][]...##", "##....[].@..##", "##..........##", "##############")),
                new Paso('v', List.of("##############", "##......##..##", "##..........##", "##...[][]...##", "##....[]....##", "##.......@..##", "##############")),
                new Paso('<', List.of("##############", "##......##..##", "##..........##", "##...[][]...##", "##....[]....##", "##......@...##", "##############")),
                new Paso('<', List.of("##############", "##......##..##", "##..........##", "##...[][]...##", "##....[]....##", "##.....@....##", "##############")),
                new Paso('^', List.of("##############", "##......##..##", "##...[][]...##", "##....[]....##", "##.....@....##", "##..........##", "##############")),
                new Paso('^', List.of("##############", "##......##..##", "##...[][]...##", "##....[]....##", "##.....@....##", "##..........##", "##############")),
                new Paso('<', List.of("##############", "##......##..##", "##...[][]...##", "##....[]....##", "##....@.....##", "##..........##", "##############")),
                new Paso('<', List.of("##############", "##......##..##", "##...[][]...##", "##....[]....##", "##...@......##", "##..........##", "##############")),
                new Paso('^', List.of("##############", "##......##..##", "##...[][]...##", "##...@[]....##", "##..........##", "##..........##", "##############")),
                new Paso('^', List.of("##############", "##...[].##..##", "##...@.[]...##", "##....[]....##", "##..........##", "##..........##", "##############")));
        for (Paso paso : pasos) {
            almacen.mover(Direccion.desdeFlecha(paso.flecha()));
            assertEquals(String.join("\n", paso.dibujo()), almacen.mapa().toString(), "después de " + paso.flecha());
        }
    }

    @Test
    @DisplayName("Parte 2: la coordenada GPS de una caja ancha se mide a su mitad izquierda (el ejemplo da 105)")
    void parte2_coordenadaGps() {
        assertEquals(105, Dia15.sumaGps(Grilla.desdeLineas(List.of("##########", "##...[]...", "##........"))));
    }

    @Test
    @DisplayName("Parte 2: empujar en horizontal una fila de cajas anchas las corre a todas")
    void parte2_empujeHorizontal() {
        Dia15.Almacen almacen = Dia15.Almacen.parsear(List.of("#######", "#@OO..#", "#######", "", ">>"), true);
        almacen.moverTodo();   // el 1.º '>' solo avanza al hueco; el 2.º empuja las dos cajas
        assertEquals("##############\n##..@[][]...##\n##############", almacen.mapa().toString());
    }
}
