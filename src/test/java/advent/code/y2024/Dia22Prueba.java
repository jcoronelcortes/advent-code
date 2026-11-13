package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 22 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 22: Mercado de Monos")
class Dia22Prueba {

    private Dia22 dia22;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of("1", "10", "100", "2024");

    @BeforeEach
    void configurar() {
        dia22 = new Dia22();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("37327623", dia22.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: mezclar 15 en 42 da 37, y podar 100000000 da 16113920")
    void parte1_mezclarYPodar() {
        assertEquals(37, Dia22.mezclar(42, 15));
        assertEquals(16113920, Dia22.podar(100_000_000));
    }

    @Test
    @DisplayName("Parte 1: los diez números secretos que siguen a 123")
    void parte1_secuenciaDe123() {
        List<Long> secuencia = new ArrayList<>();
        long secreto = 123;
        for (int i = 0; i < 10; i++) {
            secreto = Dia22.siguiente(secreto);
            secuencia.add(secreto);
        }
        assertEquals(List.of(15887950L, 16495136L, 527345L, 704524L, 1553684L,
                12683156L, 11100544L, 12249484L, 7753432L, 5908254L), secuencia);
    }

    @ParameterizedTest(name = "el 2000.º secreto de {0} es {1}")
    @CsvSource({
            "1,    8685429",
            "10,   4700978",
            "100,  15273692",
            "2024, 8667524"
    })
    @DisplayName("Parte 1: el número secreto 2000 de cada comprador del ejemplo")
    void parte1_cadaComprador(long inicial, long esperado) {
        assertEquals(esperado, Dia22.despuesDe(inicial, Dia22.NUEVOS_POR_DIA));
    }

    @Test
    @DisplayName("Parte 1: con corrimientos y máscara de bits da lo mismo que con las operaciones del enunciado")
    void parte1_versionConBits() {
        long secreto = 123;
        for (int i = 0; i < 10_000; i++) {
            long esperado = secreto;
            esperado = (esperado ^ (esperado << 6)) & 0xFFFFFF;
            esperado = (esperado ^ (esperado >> 5)) & 0xFFFFFF;
            esperado = (esperado ^ (esperado << 11)) & 0xFFFFFF;
            secreto = Dia22.siguiente(secreto);
            assertEquals(esperado, secreto);
        }
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Compradores del ejemplo de la parte 2 (distinto al de la parte 1). */
    private static final List<String> EJEMPLO_2 = List.of("1", "2", "3", "2024");

    /**
     * Fuerza bruta independiente para una secuencia: simula los precios del
     * comprador y devuelve el precio la primera vez que los últimos cuatro
     * cambios son la secuencia, o −1 si nunca aparece.
     */
    private static int primeraVenta(long inicial, int nuevos, List<Integer> buscada) {
        List<Integer> precios = new ArrayList<>(List.of((int) (inicial % 10)));
        long secreto = inicial;
        for (int i = 0; i < nuevos; i++) {
            secreto = Dia22.siguiente(secreto);
            precios.add((int) (secreto % 10));
        }
        for (int i = 4; i < precios.size(); i++) {
            List<Integer> cambios = new ArrayList<>();
            for (int j = i - 3; j <= i; j++) cambios.add(precios.get(j) - precios.get(j - 1));
            if (cambios.equals(buscada)) return precios.get(i);
        }
        return -1;
    }

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("23", dia22.parte2(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 2: los diez primeros precios de 123 son la última cifra de cada secreto")
    void parte2_preciosDe123() {
        List<Integer> precios = new ArrayList<>();
        long secreto = 123;
        precios.add(Dia22.precio(secreto));
        for (int i = 0; i < 9; i++) {
            secreto = Dia22.siguiente(secreto);
            precios.add(Dia22.precio(secreto));
        }
        assertEquals(List.of(3, 0, 6, 5, 4, 4, 6, 4, 4, 2), precios);
    }

    @Test
    @DisplayName("Parte 2: con los primeros precios de 123, la secuencia -1,-1,0,2 vende a 6")
    void parte2_secuenciaDe123() {
        long[] bananas = Dia22.bananasPorSecuencia(List.of(123L), 9);   // 10 precios, 9 cambios
        assertEquals(6, bananas[Dia22.codificar(-1, -1, 0, 2)]);
        assertEquals(6, Arrays.stream(bananas).max().orElseThrow());
    }

    @Test
    @DisplayName("Parte 2: con -2,1,-1,3 se vende a 7, 7, nunca y 9, como dice el enunciado")
    void parte2_ventasDeCadaComprador() {
        List<Integer> buscada = List.of(-2, 1, -1, 3);
        assertEquals(7, primeraVenta(1, Dia22.NUEVOS_POR_DIA, buscada));
        assertEquals(7, primeraVenta(2, Dia22.NUEVOS_POR_DIA, buscada));
        assertEquals(-1, primeraVenta(3, Dia22.NUEVOS_POR_DIA, buscada));
        assertEquals(9, primeraVenta(2024, Dia22.NUEVOS_POR_DIA, buscada));
        long[] bananas = Dia22.bananasPorSecuencia(List.of(1L, 2L, 3L, 2024L), Dia22.NUEVOS_POR_DIA);
        assertEquals(23, bananas[Dia22.codificar(-2, 1, -1, 3)]);
    }

    @Test
    @DisplayName("Parte 2: el acumulador coincide con la fuerza bruta en las 130321 secuencias")
    void parte2_coincideConFuerzaBruta() {
        List<Long> compradores = List.of(1L, 2L, 3L, 2024L, 123L);
        // fuerza bruta: para cada comprador, un mapa (cuatro cambios → primer precio) armado con putIfAbsent
        Map<List<Integer>, Long> esperado = new HashMap<>();
        for (long inicial : compradores) {
            List<Integer> precios = new ArrayList<>(List.of((int) (inicial % 10)));
            long secreto = inicial;
            for (int i = 0; i < Dia22.NUEVOS_POR_DIA; i++) {
                secreto = Dia22.siguiente(secreto);
                precios.add((int) (secreto % 10));
            }
            Map<List<Integer>, Integer> primeras = new HashMap<>();
            for (int i = 4; i < precios.size(); i++) {
                List<Integer> cambios = new ArrayList<>();
                for (int j = i - 3; j <= i; j++) cambios.add(precios.get(j) - precios.get(j - 1));
                primeras.putIfAbsent(cambios, precios.get(i));
            }
            primeras.forEach((cambios, precio) -> esperado.merge(cambios, (long) precio, Long::sum));
        }
        long[] bananas = Dia22.bananasPorSecuencia(compradores, Dia22.NUEVOS_POR_DIA);
        for (int a = -9; a <= 9; a++) {
            for (int b = -9; b <= 9; b++) {
                for (int c = -9; c <= 9; c++) {
                    for (int d = -9; d <= 9; d++) {
                        assertEquals(esperado.getOrDefault(List.of(a, b, c, d), 0L),
                                bananas[Dia22.codificar(a, b, c, d)], a + "," + b + "," + c + "," + d);
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Parte 2: codificar rechaza cambios fuera de rango o en otra cantidad")
    void parte2_codificarInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Dia22.codificar(10, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Dia22.codificar(1, 2, 3));
        assertEquals(0, Dia22.codificar(-9, -9, -9, -9));
        assertEquals(Dia22.SECUENCIAS - 1, Dia22.codificar(9, 9, 9, 9));
    }
}
