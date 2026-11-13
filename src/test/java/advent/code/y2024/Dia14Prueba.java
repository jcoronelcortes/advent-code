package advent.code.y2024;

import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 14 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 14: Reducto del Baño")
class Dia14Prueba {

    private Dia14 dia14;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "p=0,4 v=3,-3",
            "p=6,3 v=-1,-3",
            "p=10,3 v=-1,2",
            "p=2,0 v=2,-1",
            "p=0,0 v=1,3",
            "p=3,0 v=-2,-2",
            "p=7,6 v=-1,-3",
            "p=3,0 v=-1,-2",
            "p=9,3 v=2,3",
            "p=7,3 v=-1,2",
            "p=2,4 v=2,-3",
            "p=9,5 v=-3,-3"
    );

    /** El ejemplo usa un piso de 11 × 7, no el de 101 × 103 del input real. */
    private static final int ANCHO_EJEMPLO = 11;
    private static final int ALTO_EJEMPLO = 7;

    @BeforeEach
    void configurar() {
        dia14 = new Dia14();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado (piso de 11 × 7): factor de seguridad 12")
    void parte1_ejemploDelEnunciado() {
        assertEquals(12, Dia14.factorDeSeguridad(Dia14.leerRobots(EJEMPLO), ANCHO_EJEMPLO, ALTO_EJEMPLO, 100));
    }

    @Test
    @DisplayName("Parte 1: los cuadrantes del ejemplo tienen 1, 3, 4 y 1 robots")
    void parte1_cuadrantesDelEjemplo() {
        assertArrayEquals(new long[]{1, 3, 4, 1},
                Dia14.robotsPorCuadrante(Dia14.leerRobots(EJEMPLO), ANCHO_EJEMPLO, ALTO_EJEMPLO, 100));
    }

    @Test
    @DisplayName("Parte 1: el robot p=2,4 v=2,-3 en sus primeros 5 segundos, dando la vuelta por los bordes")
    void parte1_unRobotDaLaVuelta() {
        Dia14.Robot robot = Dia14.leerRobots(List.of("p=2,4 v=2,-3")).getFirst();
        List<Punto> esperadas = List.of(new Punto(2, 4), new Punto(4, 1), new Punto(6, 5),
                new Punto(8, 2), new Punto(10, 6), new Punto(1, 3));
        for (int segundos = 0; segundos <= 5; segundos++) {
            assertEquals(esperadas.get(segundos), robot.posicionEn(ANCHO_EJEMPLO, ALTO_EJEMPLO, segundos));
        }
    }

    @Test
    @DisplayName("Parte 1: el dibujo del ejemplo después de 100 segundos")
    void parte1_dibujoDespuesDe100Segundos() {
        List<String> esperado = List.of(
                "......2..1.",
                "...........",
                "1..........",
                ".11........",
                ".....1.....",
                "...12......",
                ".1....1....");
        assertEquals(esperado, dibujar(Dia14.leerRobots(EJEMPLO), 100));
    }

    @Test
    @DisplayName("Parte 1: el dibujo inicial del ejemplo")
    void parte1_dibujoInicial() {
        List<String> esperado = List.of(
                "1.12.......",
                "...........",
                "...........",
                "......11.11",
                "1.1........",
                ".........1.",
                ".......1...");
        assertEquals(esperado, dibujar(Dia14.leerRobots(EJEMPLO), 0));
    }

    /** Cantidad de robots por casilla, con '.' donde no hay ninguno, como en el enunciado. */
    private static List<String> dibujar(List<Dia14.Robot> robots, long segundos) {
        int[][] cantidad = new int[ALTO_EJEMPLO][ANCHO_EJEMPLO];
        for (Dia14.Robot robot : robots) {
            Punto p = robot.posicionEn(ANCHO_EJEMPLO, ALTO_EJEMPLO, segundos);
            cantidad[p.y()][p.x()]++;
        }
        List<String> filas = new ArrayList<>();
        for (int[] fila : cantidad) {
            StringBuilder texto = new StringBuilder();
            for (int n : fila) texto.append(n == 0 ? '.' : (char) ('0' + n));
            filas.add(texto.toString());
        }
        return filas;
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: robots que se juntan en un cuadrado de 5 × 5 en el segundo 7777 (el enunciado no trae ejemplo)")
    void parte2_robotsQueSeJuntan() {
        List<Dia14.Robot> robots = robotsQueSeJuntanEn(7777, 300);
        assertEquals(7777, Dia14.segundoMasAgrupado(robots, Dia14.ANCHO, Dia14.ALTO));
    }

    @Test
    @DisplayName("Parte 2: es el PRIMER segundo del ciclo de 101 × 103 en que se juntan")
    void parte2_primerSegundo() {
        List<Dia14.Robot> robots = robotsQueSeJuntanEn(3, 300);
        assertEquals(3, Dia14.segundoMasAgrupado(robots, Dia14.ANCHO, Dia14.ALTO));
    }

    @Test
    @DisplayName("Parte 2: cada eje se analiza por separado, dentro de su propio período")
    void parte2_cadaEjePorSeparado() {
        List<Dia14.Robot> robots = robotsQueSeJuntanEn(7777, 300);
        assertEquals(7777 % Dia14.ANCHO,
                Dia14.momentoMasAgrupado(robots, Dia14.ANCHO, Punto::x, Dia14.ANCHO, Dia14.ALTO));
        assertEquals(7777 % Dia14.ALTO,
                Dia14.momentoMasAgrupado(robots, Dia14.ALTO, Punto::y, Dia14.ANCHO, Dia14.ALTO));
    }

    /**
     * Robots con velocidades pseudoaleatorias (semilla fija) que en el
     * segundo indicado caen todos dentro de un cuadrado de 5 × 5 en el medio
     * del piso: su posición inicial se calcula retrocediendo desde ahí.
     */
    private static List<Dia14.Robot> robotsQueSeJuntanEn(long segundo, int cantidad) {
        Random azar = new Random(14);
        List<Dia14.Robot> robots = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Punto velocidad = new Punto(azar.nextInt(1, 100) * (azar.nextBoolean() ? 1 : -1),
                    azar.nextInt(1, 100) * (azar.nextBoolean() ? 1 : -1));
            Punto destino = new Punto(48 + azar.nextInt(5), 49 + azar.nextInt(5));
            Punto inicio = new Dia14.Robot(destino, velocidad).posicionEn(Dia14.ANCHO, Dia14.ALTO, -segundo);
            robots.add(new Dia14.Robot(inicio, velocidad));
        }
        return robots;
    }
}
