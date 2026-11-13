package advent.code.y2024;

import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 18 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.
 * El ejemplo usa un espacio de 0 a 6 y solo 12 bytes, en vez de 0 a 70 y
 * 1024.</p>
 */
@DisplayName("Año 2024 — Día 18: Carrera en la RAM")
class Dia18Prueba {

    private Dia18 dia18;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "5,4", "4,2", "4,5", "3,0", "2,1", "6,3", "2,4", "1,5", "0,6", "3,3", "2,6", "5,1", "1,2",
            "5,5", "2,5", "6,5", "1,4", "0,4", "6,4", "1,1", "6,1", "1,0", "0,5", "1,6", "2,0"
    );

    private static final int LADO_EJEMPLO = 6;
    private static final int CANTIDAD_EJEMPLO = 12;

    @BeforeEach
    void configurar() {
        dia18 = new Dia18();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado (12 bytes en un espacio de 0 a 6) da 22 pasos")
    void parte1_ejemploDelEnunciado() {
        assertEquals(Optional.of(22L), Dia18.pasosMinimos(Dia18.parsear(EJEMPLO), LADO_EJEMPLO, CANTIDAD_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: la memoria después de 12 bytes, como en el dibujo del enunciado")
    void parte1_dibujoDeLaMemoria() {
        assertEquals(String.join("\n", List.of(
                "...#...",
                "..#..#.",
                "....#..",
                "...#..#",
                "..#..#.",
                ".#..#..",
                "#.#....")), Dia18.memoria(Dia18.parsear(EJEMPLO), LADO_EJEMPLO, CANTIDAD_EJEMPLO).toString());
    }

    @Test
    @DisplayName("Parte 1: X es la columna e Y la fila")
    void parte1_coordenadas() {
        assertEquals(List.of(new Punto(5, 4)), Dia18.parsear(List.of("5,4")));
    }

    @Test
    @DisplayName("Parte 1: sin bytes, el camino mide 2 × lado")
    void parte1_sinBytes() {
        assertEquals(Optional.of(12L), Dia18.pasosMinimos(Dia18.parsear(EJEMPLO), LADO_EJEMPLO, 0));
    }

    @Test
    @DisplayName("Parte 1: si los bytes cierran el paso, no hay camino")
    void parte1_salidaBloqueada() {
        List<Punto> pared = Dia18.parsear(List.of("0,2", "1,2", "2,2", "3,2", "4,2", "5,2", "6,2"));
        assertEquals(Optional.empty(), Dia18.pasosMinimos(pared, LADO_EJEMPLO, pared.size()));
    }

    @Test
    @DisplayName("Parte 1: si se piden más bytes de los que hay, falla con un mensaje claro")
    void parte1_faltanBytes() {
        assertThrows(IllegalArgumentException.class, () -> dia18.parte1(EJEMPLO));   // el ejemplo tiene 25, no 1024
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: en el ejemplo, el primer byte que bloquea la salida es el de 6,1")
    void parte2_ejemploDelEnunciado() {
        assertEquals(new Punto(6, 1), Dia18.primerByteQueBloquea(Dia18.parsear(EJEMPLO), LADO_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: después de 1,1 (el byte 20) todavía hay camino; después de 6,1 (el 21), no")
    void parte2_justoAntesYDespues() {
        List<Punto> bytes = Dia18.parsear(EJEMPLO);
        assertEquals(new Punto(1, 1), bytes.get(19));
        assertTrue(Dia18.pasosMinimos(bytes, LADO_EJEMPLO, 20).isPresent());
        assertTrue(Dia18.pasosMinimos(bytes, LADO_EJEMPLO, 21).isEmpty());
    }

    @Test
    @DisplayName("Parte 2: la memoria con 21 bytes, como en el dibujo del enunciado")
    void parte2_dibujoBloqueado() {
        assertEquals(String.join("\n", List.of(
                "...#...",
                ".##..##",
                ".#..#..",
                "...#..#",
                "###..##",
                ".##.###",
                "#.#....")), Dia18.memoria(Dia18.parsear(EJEMPLO), LADO_EJEMPLO, 21).toString());
    }

    @Test
    @DisplayName("Parte 2: coincide con agregar los bytes de a uno hasta que no haya camino")
    void parte2_fuerzaBrutaEnElEjemplo() {
        List<Punto> bytes = Dia18.parsear(EJEMPLO);
        int cantidad = 0;
        while (Dia18.pasosMinimos(bytes, LADO_EJEMPLO, cantidad).isPresent()) cantidad++;
        assertEquals(bytes.get(cantidad - 1), Dia18.primerByteQueBloquea(bytes, LADO_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: el primer o el último byte también se encuentran")
    void parte2_extremos() {
        List<Punto> cierraEnseguida = Dia18.parsear(List.of("6,6", "3,3"));   // cae sobre la salida
        assertEquals(new Punto(6, 6), Dia18.primerByteQueBloquea(cierraEnseguida, LADO_EJEMPLO));
        List<Punto> cierraAlFinal = Dia18.parsear(List.of("0,1", "3,3", "1,0"));   // rodea la entrada
        assertEquals(new Punto(1, 0), Dia18.primerByteQueBloquea(cierraAlFinal, LADO_EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: si ningún byte bloquea la salida, falla con un mensaje claro")
    void parte2_nuncaSeBloquea() {
        assertThrows(IllegalArgumentException.class,
                () -> Dia18.primerByteQueBloquea(Dia18.parsear(List.of("3,3")), LADO_EJEMPLO));
    }
}
