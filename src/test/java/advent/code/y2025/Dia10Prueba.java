package advent.code.y2025;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 10 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 10: Fábrica")
class Dia10Prueba {

    private Dia10 dia10;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
            "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
            "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"
    );

    @BeforeEach
    void configurar() {
        dia10 = new Dia10();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("7", dia10.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: mínimo de cada máquina del ejemplo (2, 3 y 2)")
    void parte1_minimoPorMaquina() {
        assertEquals(List.of(2L, 3L, 2L), Dia10.leerMaquinas(EJEMPLO).stream()
                .map(Dia10.Maquina::minimasPulsacionesLuces)
                .toList());
    }

    @Test
    @DisplayName("Parte 1: interpreta diagrama, botones y voltajes")
    void parte1_parseo() {
        Dia10.Maquina maquina = Dia10.leerMaquina(EJEMPLO.getFirst());
        assertEquals(4, maquina.cantidadLuces());
        assertEquals(0b0110, maquina.objetivo());            // luces 1 y 2 encendidas
        assertEquals(List.of(List.of(3), List.of(1, 3), List.of(2), List.of(2, 3), List.of(0, 2), List.of(0, 1)),
                maquina.botones());
        assertEquals(List.of(0b1000, 0b1010, 0b0100, 0b1100, 0b0101, 0b0011), maquina.mascaras());
        assertEquals(List.of(3, 5, 4, 7), maquina.voltajes());
    }

    @Test
    @DisplayName("Parte 1: si las luces ya están como el diagrama, no hace falta apretar nada")
    void parte1_sinPulsaciones() {
        assertEquals("0", dia10.parte1(List.of("[...] (0) (1,2) {1,1,1}")));
    }

    @Test
    @DisplayName("Parte 1: si ninguna combinación sirve, lo informa en vez de devolver un número")
    void parte1_sinSolucion() {
        assertThrows(IllegalStateException.class, () -> dia10.parte1(List.of("[#.] (1) {1,1}")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("33", dia10.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: mínimo de cada máquina del ejemplo (10, 12 y 11)")
    void parte2_minimoPorMaquina() {
        assertEquals(List.of(10L, 12L, 11L), Dia10.leerMaquinas(EJEMPLO).stream()
                .map(Dia10.Maquina::minimasPulsacionesVoltaje)
                .toList());
    }

    @Test
    @DisplayName("Parte 2: sistemas con 0 y con varias variables libres")
    void parte2_variablesLibres() {
        // 2 botones independientes: sin variables libres, solución única
        Dia10.Maquina unica = Dia10.leerMaquina("[..] (0) (1) {3,4}");
        assertEquals(0, new Dia10.SistemaBotones(unica).cantidadLibres());
        assertEquals(7, unica.minimasPulsacionesVoltaje());

        // (0,1) equivale a (0) + (1) pero cuesta una sola pulsación: conviene usarlo
        Dia10.Maquina conLibre = Dia10.leerMaquina("[..] (0) (1) (0,1) {3,4}");
        assertEquals(1, new Dia10.SistemaBotones(conLibre).cantidadLibres());
        assertEquals(4, conLibre.minimasPulsacionesVoltaje());   // (0,1)×3 + (1)×1
    }

    @Test
    @DisplayName("Parte 2: sin solución entera no negativa, lo informa")
    void parte2_sinSolucion() {
        // Un solo botón que suma a ambos contadores: no puede dejar 1 y 2
        assertThrows(IllegalStateException.class,
                () -> Dia10.leerMaquina("[..] (0,1) {1,2}").minimasPulsacionesVoltaje());
        // Con (0,1) y (0,1,2), el contador 2 solo lo sube el segundo botón:
        // {2,2,1} se logra con uno de cada uno...
        assertEquals(2, Dia10.leerMaquina("[...] (0,1) (0,1,2) {2,2,1}").minimasPulsacionesVoltaje());
        // ...pero {1,1,2} exige apretar (0,1,2) dos veces, y eso ya pasa de 1 en los contadores 0 y 1
        assertThrows(IllegalStateException.class,
                () -> Dia10.leerMaquina("[...] (0,1) (0,1,2) {1,1,2}").minimasPulsacionesVoltaje());
    }

    @Test
    @DisplayName("Parte 2: las soluciones fraccionarias no sirven")
    void parte2_soloEnteros() {
        // (0,1), (1,2), (0,2) con {1,1,1}: la única solución real es 1/2 de cada uno
        assertThrows(IllegalStateException.class,
                () -> Dia10.leerMaquina("[...] (0,1) (1,2) (0,2) {1,1,1}").minimasPulsacionesVoltaje());
        assertEquals(3, Dia10.leerMaquina("[...] (0,1) (1,2) (0,2) {2,2,2}").minimasPulsacionesVoltaje());
    }

    @Property(tries = 300)
    @Label("Parte 2: coincide con probar todas las combinaciones en máquinas pequeñas")
    void parte2_coincideConFuerzaBruta(
            @ForAll @Size(min = 1, max = 5) List<@IntRange(min = 1, max = 15) Integer> botonesComoBits,
            @ForAll @Size(min = 5, max = 5) List<@IntRange(min = 0, max = 4) Integer> pulsaciones,
            @ForAll boolean alterarVoltajes) {
        // 4 contadores; cada botón es una máscara de bits no vacía de esos contadores
        int contadores = 4;
        List<List<Integer>> botones = botonesComoBits.stream()
                .map(bits -> java.util.stream.IntStream.range(0, contadores).filter(i -> (bits >> i & 1) == 1).boxed().toList())
                .toList();

        // Voltajes alcanzables (salvo que se alteren a propósito para probar casos sin solución)
        int[] voltajes = new int[contadores];
        for (int j = 0; j < botones.size(); j++) {
            for (int contador : botones.get(j)) voltajes[contador] += pulsaciones.get(j);
        }
        if (alterarVoltajes) voltajes[0] += 1;
        List<Integer> requisitos = java.util.Arrays.stream(voltajes).boxed().toList();
        Dia10.Maquina maquina = new Dia10.Maquina(contadores, 0, botones, requisitos);

        OptionalLong esperado = fuerzaBruta(botones, voltajes, 0, new int[contadores], 0);
        assertEquals(esperado, new Dia10.SistemaBotones(maquina).minimo());
    }

    /** Prueba todas las cantidades de pulsaciones (0..máximo voltaje) de cada botón. */
    private static OptionalLong fuerzaBruta(List<List<Integer>> botones, int[] objetivo, int indice,
                                            int[] actual, long pulsaciones) {
        if (indice == botones.size()) {
            return java.util.Arrays.equals(actual, objetivo) ? OptionalLong.of(pulsaciones) : OptionalLong.empty();
        }
        int maximo = java.util.Arrays.stream(objetivo).max().orElse(0);
        OptionalLong mejor = OptionalLong.empty();
        for (int veces = 0; veces <= maximo; veces++) {
            int[] siguiente = actual.clone();
            for (int contador : botones.get(indice)) siguiente[contador] += veces;
            OptionalLong resultado = fuerzaBruta(botones, objetivo, indice + 1, siguiente, pulsaciones + veces);
            if (resultado.isPresent() && (mejor.isEmpty() || resultado.getAsLong() < mejor.getAsLong())) {
                mejor = resultado;
            }
        }
        return mejor;
    }
}
