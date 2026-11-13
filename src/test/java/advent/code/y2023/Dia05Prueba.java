package advent.code.y2023;

import advent.code.Rango;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 5 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 5: Si Le Das un Fertilizante a una Semilla")
class Dia05Prueba {

    private Dia05 dia05;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "seeds: 79 14 55 13",
            "",
            "seed-to-soil map:", "50 98 2", "52 50 48",
            "",
            "soil-to-fertilizer map:", "0 15 37", "37 52 2", "39 0 15",
            "",
            "fertilizer-to-water map:", "49 53 8", "0 11 42", "42 0 7", "57 7 4",
            "",
            "water-to-light map:", "88 18 7", "18 25 70",
            "",
            "light-to-temperature map:", "45 77 23", "81 45 19", "68 64 13",
            "",
            "temperature-to-humidity map:", "0 69 1", "1 0 69",
            "",
            "humidity-to-location map:", "60 56 37", "56 93 4"
    );

    @BeforeEach
    void configurar() {
        dia05 = new Dia05();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("35", dia05.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el mapa semilla → suelo, como en la tabla del enunciado")
    void parte1_mapaSemillaSuelo() {
        Dia05.Mapa mapa = Dia05.Almanaque.parsear(EJEMPLO).mapas().getFirst();
        assertEquals("seed-to-soil", mapa.nombre());
        assertEquals(List.of(0L, 49L, 52L, 53L, 98L, 99L, 50L, 51L, 81L, 14L, 57L, 13L),
                List.of(0L, 49L, 50L, 51L, 96L, 97L, 98L, 99L, 79L, 14L, 55L, 13L).stream().map(mapa::convertir).toList());
    }

    @Test
    @DisplayName("Parte 1: el recorrido completo de cada semilla, como en el enunciado")
    void parte1_recorridoDeCadaSemilla() {
        Dia05.Almanaque almanaque = Dia05.Almanaque.parsear(EJEMPLO);
        List<List<Long>> esperado = List.of(
                List.of(79L, 81L, 81L, 81L, 74L, 78L, 78L, 82L),
                List.of(14L, 14L, 53L, 49L, 42L, 42L, 43L, 43L),
                List.of(55L, 57L, 57L, 53L, 46L, 82L, 82L, 86L),
                List.of(13L, 13L, 52L, 41L, 34L, 34L, 35L, 35L));
        for (List<Long> recorrido : esperado) {
            List<Long> calculado = new ArrayList<>(List.of(recorrido.getFirst()));
            long numero = recorrido.getFirst();
            for (Dia05.Mapa mapa : almanaque.mapas()) {
                numero = mapa.convertir(numero);
                calculado.add(numero);
            }
            assertEquals(recorrido, calculado);
            assertEquals(recorrido.getLast(), almanaque.ubicacion(recorrido.getFirst()));
        }
    }

    @Test
    @DisplayName("Parte 1: los números grandes del input real no desbordan")
    void parte1_numerosGrandes() {
        Dia05.Mapa mapa = new Dia05.Mapa("prueba", List.of(Dia05.Tramo.parsear("4170452318 3837406401 124514978")));
        assertEquals(4170452318L, mapa.convertir(3837406401L));
        assertEquals(4170452318L + 124514977, mapa.convertir(3837406401L + 124514977));
        assertEquals(3837406401L + 124514978, mapa.convertir(3837406401L + 124514978));   // justo afuera
    }

    @Test
    @DisplayName("Parte 1: un mapa con tramos que se solapan falla con un mensaje claro")
    void parte1_tramosSolapados() {
        assertThrows(IllegalArgumentException.class, () -> new Dia05.Mapa("prueba",
                List.of(Dia05.Tramo.parsear("0 10 5"), Dia05.Tramo.parsear("100 12 5"))));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("46", dia05.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: las semillas son los rangos 79–92 y 55–67 (27 semillas), y la 82 llega a 46")
    void parte2_rangosDeSemillas() {
        Dia05.Almanaque almanaque = Dia05.Almanaque.parsear(EJEMPLO);
        assertEquals(List.of(new Rango(79, 92), new Rango(55, 67)), almanaque.rangosDeSemillas());
        assertEquals(27, almanaque.rangosDeSemillas().stream().mapToLong(Rango::largo).sum());
        assertEquals(46, almanaque.ubicacion(82));
    }

    @Test
    @DisplayName("Parte 2: coincide con convertir las 27 semillas del ejemplo una por una")
    void parte2_fuerzaBruta() {
        Dia05.Almanaque almanaque = Dia05.Almanaque.parsear(EJEMPLO);
        long menor = Long.MAX_VALUE;
        for (Rango rango : almanaque.rangosDeSemillas()) {
            for (long semilla = rango.inicio(); semilla <= rango.fin(); semilla++) {
                menor = Math.min(menor, almanaque.ubicacion(semilla));
            }
        }
        assertEquals(String.valueOf(menor), dia05.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: convertir un rango da los mismos números que convertir cada número")
    void parte2_convertirRango() {
        Dia05.Almanaque almanaque = Dia05.Almanaque.parsear(EJEMPLO);
        for (Dia05.Mapa mapa : almanaque.mapas()) {
            Rango rango = new Rango(0, 120);
            List<Long> esperado = new ArrayList<>();
            for (long numero = rango.inicio(); numero <= rango.fin(); numero++) esperado.add(mapa.convertir(numero));
            List<Long> calculado = new ArrayList<>();
            for (Rango parte : mapa.convertir(rango)) {
                for (long numero = parte.inicio(); numero <= parte.fin(); numero++) calculado.add(numero);
            }
            assertEquals(esperado.stream().sorted().distinct().toList(), calculado, mapa.nombre());
        }
    }

    @Test
    @DisplayName("Parte 2: un rango que cruza el borde de un tramo se parte en dos")
    void parte2_rangoPartido() {
        Dia05.Mapa mapa = new Dia05.Mapa("prueba", List.of(Dia05.Tramo.parsear("100 10 5")));   // 10–14 → 100–104
        assertEquals(List.of(new Rango(8, 9), new Rango(100, 102)), mapa.convertir(new Rango(8, 12)));
    }

    @Test
    @DisplayName("Parte 2: una cantidad impar de números de semilla falla con un mensaje claro")
    void parte2_semillasImpares() {
        List<String> impar = new ArrayList<>(EJEMPLO);
        impar.set(0, "seeds: 79 14 55");
        assertThrows(IllegalArgumentException.class, () -> dia05.parte2(impar));
    }
}
