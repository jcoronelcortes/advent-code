package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 20 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 20: Propagación de Pulsos")
class Dia20Prueba {

    private Dia20 dia20;

    /** Primer ejemplo del enunciado: tres biestables en cadena y un inversor. */
    private static final List<String> EJEMPLO = List.of(
            "broadcaster -> a, b, c",
            "%a -> b",
            "%b -> c",
            "%c -> inv",
            "&inv -> a"
    );

    /** Segundo ejemplo del enunciado: una conjunción con dos entradas y un módulo de salida sin tipo. */
    private static final List<String> EJEMPLO_2 = List.of(
            "broadcaster -> a",
            "%a -> inv, con",
            "&inv -> b",
            "%b -> con",
            "&con -> output"
    );

    /** Los pulsos como los escribe el enunciado: {@code origen -low-> destino}. */
    private static List<String> texto(List<Dia20.Pulso> pulsos) {
        return pulsos.stream()
                .map(p -> p.origen() + " -" + (p.alto() ? "high" : "low") + "-> " + p.destino())
                .toList();
    }

    @BeforeEach
    void configurar() {
        dia20 = new Dia20();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: los dos ejemplos del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("32000000", dia20.parte1(EJEMPLO));
        assertEquals("11687500", dia20.parte1(EJEMPLO_2));
    }

    @Test
    @DisplayName("Parte 1: el primer ejemplo manda los 12 pulsos del enunciado, en orden, en cada presión")
    void parte1_pulsosDelPrimerEjemplo() {
        List<String> esperados = List.of(
                "button -low-> broadcaster",
                "broadcaster -low-> a", "broadcaster -low-> b", "broadcaster -low-> c",
                "a -high-> b", "b -high-> c", "c -high-> inv", "inv -low-> a",
                "a -low-> b", "b -low-> c", "c -low-> inv", "inv -high-> a");
        Dia20.Red red = new Dia20.Red(Dia20.leer(EJEMPLO));
        assertEquals(esperados, texto(red.presionarBoton()));
        assertEquals(List.of(), red.prendidos());
        assertEquals(esperados, texto(red.presionarBoton()));
    }

    @Test
    @DisplayName("Parte 1: el segundo ejemplo da las cuatro secuencias del enunciado y vuelve al estado inicial")
    void parte1_pulsosDelSegundoEjemplo() {
        Dia20.Red red = new Dia20.Red(Dia20.leer(EJEMPLO_2));
        assertEquals(List.of(
                "button -low-> broadcaster", "broadcaster -low-> a", "a -high-> inv", "a -high-> con",
                "inv -low-> b", "con -high-> output", "b -high-> con", "con -low-> output"),
                texto(red.presionarBoton()));
        assertEquals(List.of("a", "b"), red.prendidos());
        assertEquals(List.of(
                "button -low-> broadcaster", "broadcaster -low-> a", "a -low-> inv", "a -low-> con",
                "inv -high-> b", "con -high-> output"),
                texto(red.presionarBoton()));
        assertEquals(List.of("b"), red.prendidos());
        assertEquals(List.of(
                "button -low-> broadcaster", "broadcaster -low-> a", "a -high-> inv", "a -high-> con",
                "inv -low-> b", "con -low-> output", "b -low-> con", "con -high-> output"),
                texto(red.presionarBoton()));
        assertEquals(List.of("a"), red.prendidos());
        assertEquals(List.of(
                "button -low-> broadcaster", "broadcaster -low-> a", "a -low-> inv", "a -low-> con",
                "inv -high-> b", "con -high-> output"),
                texto(red.presionarBoton()));
        assertEquals(List.of(), red.prendidos());
    }

    @Test
    @DisplayName("Parte 1: un input mal formado falla con un mensaje claro")
    void parte1_inputInvalido() {
        for (List<String> invalido : List.of(
                List.of("%a -> b"),                                  // falta el difusor
                List.of("broadcaster -> a", "%a -> b", "%a -> c"),   // módulo repetido
                List.of("broadcaster -> a", "a -> b"),               // sin tipo y no es el difusor
                List.of("broadcaster -> a", "%a ->"),                // sin destinos
                List.of("broadcaster -> a", "#a -> b"))) {           // tipo desconocido
            assertThrows(IllegalArgumentException.class, () -> Dia20.leer(invalido), invalido.toString());
        }
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /**
     * Una red con la misma forma que el input real: el difusor alimenta
     * varios contadores binarios independientes. El contador {@code k} es una
     * cadena de biestables (los bits) y una conjunción que mira los bits que
     * valen 1 en {@code k}; al llegar a {@code k}, la conjunción manda un
     * pulso bajo que reinicia el contador a 0 y, a través de un inversor, un
     * pulso alto a {@code kc}, que es la única entrada de {@code rx}.
     */
    private static List<String> redDeContadores(int bits, int... periodos) {
        List<String> lineas = new ArrayList<>();
        List<String> inicios = new ArrayList<>();
        for (int j = 0; j < periodos.length; j++) {
            // Nombres sin choques: bits "axa", "axb"…, conjunción "cona", inversor "inva" (para el contador a)
            String p = String.valueOf((char) ('a' + j));
            int k = periodos[j];
            inicios.add(bit(p, 0));
            for (int i = 0; i < bits; i++) {
                List<String> destinos = new ArrayList<>();
                if (i + 1 < bits) destinos.add(bit(p, i + 1));
                if ((k >> i & 1) == 1) destinos.add("con" + p);
                if (!destinos.isEmpty()) lineas.add("%" + bit(p, i) + " -> " + String.join(", ", destinos));
            }
            Set<String> reinicio = new LinkedHashSet<>();
            for (int i = 0; i < bits; i++) if ((k >> i & 1) == 0) reinicio.add(bit(p, i));
            reinicio.add(bit(p, 0));
            reinicio.add("inv" + p);
            lineas.add("&con" + p + " -> " + String.join(", ", reinicio));
            lineas.add("&inv" + p + " -> kc");
        }
        lineas.addFirst("broadcaster -> " + String.join(", ", inicios));
        lineas.add("&kc -> rx");
        return lineas;
    }

    /** El nombre del bit {@code i} del contador {@code p}. */
    private static String bit(String p, int i) {
        return p + "x" + (char) ('a' + i);
    }

    /** Fuerza bruta: presiona el botón hasta que {@code rx} reciba un pulso bajo. */
    private static long simularHastaRx(List<String> lineas) {
        Dia20.Red red = new Dia20.Red(Dia20.leer(lineas));
        for (long presion = 1; ; presion++) {
            for (Dia20.Pulso pulso : red.presionarBoton()) {
                if (pulso.destino().equals("rx") && !pulso.alto()) return presion;
            }
        }
    }

    @Test
    @DisplayName("Parte 2: con contadores de período 5 y 7, rx recibe un pulso bajo en la presión 35, como al simular")
    void parte2_dosContadores() {
        List<String> red = redDeContadores(3, 5, 7);
        assertEquals(35, simularHastaRx(red));
        assertEquals("35", dia20.parte2(red));
    }

    @Test
    @DisplayName("Parte 2: con tres contadores de 4 bits coincide con simular hasta que rx reciba un pulso bajo")
    void parte2_tresContadores() {
        List<String> red = redDeContadores(4, 11, 13, 9);
        assertEquals(simularHastaRx(red), Long.parseLong(dia20.parte2(red)));
        assertEquals(11 * 13 * 9, Long.parseLong(dia20.parte2(red)));
    }

    @Test
    @DisplayName("Parte 2: si una entrada no es periódica desde el inicio, falla en vez de dar un mcm equivocado")
    void parte2_noPeriodica() {
        // Un contador par no se reinicia bien: manda pulsos altos en 6, 13, 20… (cada 7 después de la primera), y la respuesta real es 90, no mcm = 30
        List<String> red = redDeContadores(4, 3, 5, 6);
        assertEquals(90, simularHastaRx(red));
        assertThrows(IllegalStateException.class, () -> dia20.parte2(red));
    }

    @Test
    @DisplayName("Parte 2: si rx no existe o su entrada no es una única conjunción, falla con un mensaje claro")
    void parte2_formaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> dia20.parte2(EJEMPLO));
        assertThrows(IllegalArgumentException.class, () -> dia20.parte2(List.of("broadcaster -> a", "%a -> rx")));
        assertThrows(IllegalArgumentException.class,
                () -> dia20.parte2(List.of("broadcaster -> a, b", "%a -> rx", "&b -> rx")));
    }
}
