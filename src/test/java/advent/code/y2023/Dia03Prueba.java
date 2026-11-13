package advent.code.y2023;

import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 3 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 3: Proporciones de Engranajes")
class Dia03Prueba {

    private Dia03 dia03;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "467..114..",
            "...*......",
            "..35..633.",
            "......#...",
            "617*......",
            ".....+.58.",
            "..592.....",
            "......755.",
            "...$.*....",
            ".664.598.."
    );

    @BeforeEach
    void configurar() {
        dia03 = new Dia03();
    }

    private static List<Integer> valores(List<String> lineas) {
        return Dia03.numeros(Grilla.desdeLineas(lineas)).stream().map(Dia03.Numero::valor).toList();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("4361", dia03.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los números del ejemplo, y solo 114 y 58 no son de pieza")
    void parte1_numerosDelEjemplo() {
        List<Dia03.Numero> numeros = Dia03.numeros(Grilla.desdeLineas(EJEMPLO));
        assertEquals(List.of(467, 114, 35, 633, 617, 58, 592, 755, 664, 598),
                numeros.stream().map(Dia03.Numero::valor).toList());
        assertEquals(List.of(114, 58), numeros.stream()
                .filter(numero -> numero.simbolos().isEmpty())
                .map(Dia03.Numero::valor)
                .toList());
    }

    @Test
    @DisplayName("Parte 1: un símbolo vecino de varias cifras cuenta una sola vez")
    void parte1_simboloSinRepetir() {
        Dia03.Numero numero = Dia03.numeros(Grilla.desdeLineas(List.of("467.", "..*."))).getFirst();
        assertEquals(Set.of(new Punto(2, 1)), numero.simbolos());   // vecino del 6 y del 7
    }

    @Test
    @DisplayName("Parte 1: los números en los bordes y pegados a un símbolo en la misma fila")
    void parte1_bordes() {
        assertEquals(List.of(12, 34), valores(List.of("12*34")));
        assertEquals("46", dia03.parte1(List.of("12*34")));
        assertEquals("0", dia03.parte1(List.of("12.34")));
        assertEquals("7", dia03.parte1(List.of("......", "....-7")));   // '-' es un símbolo, no un signo
    }

    @Test
    @DisplayName("Parte 1: el mismo valor en dos lugares cuenta dos veces si los dos son de pieza")
    void parte1_valoresRepetidos() {
        assertEquals("10", dia03.parte1(List.of("5*5")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("467835", dia03.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: en el ejemplo hay dos engranajes (467·35 y 755·598); el * junto a 617 no lo es")
    void parte2_engranajesDelEjemplo() {
        Map<Punto, List<Integer>> porSimbolo = Dia03.numerosPorSimbolo(Grilla.desdeLineas(EJEMPLO));
        assertEquals(List.of(467, 35), porSimbolo.get(new Punto(3, 1)));
        assertEquals(List.of(617), porSimbolo.get(new Punto(3, 4)));
        assertEquals(List.of(755, 598), porSimbolo.get(new Punto(5, 8)));
    }

    @Test
    @DisplayName("Parte 2: un * con tres números, o con dos que tocan varias cifras, se cuenta bien")
    void parte2_casosBorde() {
        assertEquals("0", dia03.parte2(List.of("1.2", ".*.", "3..")));    // tres números: no es engranaje
        assertEquals("200", dia03.parte2(List.of("10.", ".*.", "20.")));  // el * toca 1 y 0 de cada número
        assertEquals("0", dia03.parte2(List.of("10.", ".#.", "20.")));   // dos números, pero no es *
    }

    @Test
    @DisplayName("Parte 2: dos números iguales alrededor del mismo * cuentan como dos")
    void parte2_numerosIguales() {
        assertEquals("25", dia03.parte2(List.of("5*5")));
    }
}
