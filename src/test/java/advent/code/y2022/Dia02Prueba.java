package advent.code.y2022;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 2 — Año 2022.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2022 — Día 2: Piedra, Papel o Tijera")
class Dia02Prueba {

    private Dia02 dia02;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "A Y",
            "B X",
            "C Z"
    );

    @BeforeEach
    void configurar() {
        dia02 = new Dia02();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("15", dia02.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: las rondas del ejemplo valen 8, 1 y 6")
    void parte1_cadaRonda() {
        assertEquals(8, Dia02.puntaje(Dia02.Figura.PIEDRA, Dia02.Figura.PAPEL));
        assertEquals(1, Dia02.puntaje(Dia02.Figura.PAPEL, Dia02.Figura.PIEDRA));
        assertEquals(6, Dia02.puntaje(Dia02.Figura.TIJERA, Dia02.Figura.TIJERA));
    }

    @Test
    @DisplayName("Parte 1: las 9 combinaciones coinciden con las reglas escritas a mano")
    void parte1_reglas() {
        // Qué le gana a qué, escrito como en el enunciado, sin usar el módulo 3
        Map<Dia02.Figura, Dia02.Figura> leGanaA = Map.of(
                Dia02.Figura.PIEDRA, Dia02.Figura.TIJERA,
                Dia02.Figura.TIJERA, Dia02.Figura.PAPEL,
                Dia02.Figura.PAPEL, Dia02.Figura.PIEDRA);
        for (Dia02.Figura mia : Dia02.Figura.values()) {
            for (Dia02.Figura rival : Dia02.Figura.values()) {
                Dia02.Resultado esperado = mia == rival ? Dia02.Resultado.EMPATE
                        : leGanaA.get(mia) == rival ? Dia02.Resultado.VICTORIA : Dia02.Resultado.DERROTA;
                assertEquals(esperado, mia.contra(rival), mia + " contra " + rival);
            }
        }
    }

    @Test
    @DisplayName("Parte 1: una línea mal formada falla con un mensaje claro")
    void parte1_inputInvalido() {
        for (String linea : List.of("D X", "A W", "AX", "A  X", "a x")) {
            assertThrows(IllegalArgumentException.class, () -> Dia02.leer(List.of(linea)), linea);
        }
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("12", dia02.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: en el ejemplo se juega piedra las tres veces, con 4, 1 y 7 puntos")
    void parte2_cadaRonda() {
        assertEquals(Dia02.Figura.PIEDRA, Dia02.Figura.paraLograr(Dia02.Figura.PIEDRA, Dia02.Resultado.EMPATE));
        assertEquals(Dia02.Figura.PIEDRA, Dia02.Figura.paraLograr(Dia02.Figura.PAPEL, Dia02.Resultado.DERROTA));
        assertEquals(Dia02.Figura.PIEDRA, Dia02.Figura.paraLograr(Dia02.Figura.TIJERA, Dia02.Resultado.VICTORIA));
        assertEquals(4, Dia02.puntaje(Dia02.Figura.PIEDRA, Dia02.Figura.PIEDRA));
        assertEquals(1, Dia02.puntaje(Dia02.Figura.PAPEL, Dia02.Figura.PIEDRA));
        assertEquals(7, Dia02.puntaje(Dia02.Figura.TIJERA, Dia02.Figura.PIEDRA));
    }

    @Test
    @DisplayName("Parte 2: paraLograr coincide con probar las tres figuras y quedarse con la que da el resultado")
    void parte2_fuerzaBruta() {
        for (Dia02.Figura rival : Dia02.Figura.values()) {
            for (Dia02.Resultado buscado : Dia02.Resultado.values()) {
                List<Dia02.Figura> sirven = Arrays.stream(Dia02.Figura.values())
                        .filter(mia -> mia.contra(rival) == buscado).toList();
                assertEquals(List.of(Dia02.Figura.paraLograr(rival, buscado)), sirven, rival + " / " + buscado);
            }
        }
    }
}
