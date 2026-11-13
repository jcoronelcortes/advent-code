package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 24 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 24: Cables Cruzados")
class Dia24Prueba {

    private Dia24 dia24;

    /** Ejemplo chico del enunciado. */
    private static final List<String> CHICO = List.of(
            "x00: 1", "x01: 1", "x02: 1", "y00: 0", "y01: 1", "y02: 0",
            "",
            "x00 AND y00 -> z00", "x01 XOR y01 -> z01", "x02 OR y02 -> z02"
    );

    /** Ejemplo grande del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "x00: 1", "x01: 0", "x02: 1", "x03: 1", "x04: 0",
            "y00: 1", "y01: 1", "y02: 1", "y03: 1", "y04: 1",
            "",
            "ntg XOR fgs -> mjb", "y02 OR x01 -> tnw", "kwq OR kpj -> z05", "x00 OR x03 -> fst",
            "tgd XOR rvg -> z01", "vdt OR tnw -> bfw", "bfw AND frj -> z10", "ffh OR nrd -> bqk",
            "y00 AND y03 -> djm", "y03 OR y00 -> psh", "bqk OR frj -> z08", "tnw OR fst -> frj",
            "gnj AND tgd -> z11", "bfw XOR mjb -> z00", "x03 OR x00 -> vdt", "gnj AND wpb -> z02",
            "x04 AND y00 -> kjc", "djm OR pbm -> qhw", "nrd AND vdt -> hwm", "kjc AND fst -> rvg",
            "y04 OR y02 -> fgs", "y01 AND x02 -> pbm", "ntg OR kjc -> kwq", "psh XOR fgs -> tgd",
            "qhw XOR tgd -> z09", "pbm OR djm -> kpj", "x03 XOR y03 -> ffh", "x00 XOR y04 -> ntg",
            "bfw OR bqk -> z06", "nrd XOR fgs -> wpb", "frj XOR qhw -> z04", "bqk OR frj -> z07",
            "y03 OR x01 -> nrd", "hwm AND bqk -> z03", "tgd XOR rvg -> z12", "tnw OR pbm -> gnj"
    );

    @BeforeEach
    void configurar() {
        dia24 = new Dia24();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo chico da 4 (100 en binario)")
    void parte1_ejemploChico() {
        assertEquals("4", dia24.parte1(CHICO));
    }

    @Test
    @DisplayName("Parte 1: el ejemplo grande da 2024")
    void parte1_ejemploDelEnunciado() {
        assertEquals("2024", dia24.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el valor de cada cable del ejemplo grande, como en la lista del enunciado")
    void parte1_valoresDeCadaCable() {
        String lista = "bfw: 1 bqk: 1 djm: 1 ffh: 0 fgs: 1 frj: 1 fst: 1 gnj: 1 hwm: 1 kjc: 0 kpj: 1 kwq: 0 "
                + "mjb: 1 nrd: 1 ntg: 0 pbm: 1 psh: 1 qhw: 1 rvg: 0 tgd: 0 tnw: 1 vdt: 1 wpb: 0 "
                + "z00: 0 z01: 0 z02: 0 z03: 1 z04: 0 z05: 1 z06: 1 z07: 1 z08: 1 z09: 1 z10: 1 z11: 0 z12: 0";
        String[] partes = lista.split(" ");
        Map<String, Boolean> esperado = new TreeMap<>();
        for (int i = 0; i < partes.length; i += 2) {
            esperado.put(partes[i].replace(":", ""), partes[i + 1].equals("1"));
        }
        Dia24.Circuito circuito = Dia24.Circuito.parsear(EJEMPLO);
        Map<String, Boolean> calculado = circuito.simular().entrySet().stream()
                .filter(cable -> circuito.compuertas().containsKey(cable.getKey()))   // sin los iniciales
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
        assertEquals(esperado, calculado);
    }

    @Test
    @DisplayName("Parte 1: las tres operaciones")
    void parte1_operaciones() {
        for (boolean a : new boolean[] {false, true}) {
            for (boolean b : new boolean[] {false, true}) {
                assertEquals(a && b, Dia24.Operacion.AND.aplicar(a, b));
                assertEquals(a || b, Dia24.Operacion.OR.aplicar(a, b));
                assertEquals(a != b, Dia24.Operacion.XOR.aplicar(a, b));
            }
        }
    }

    @Test
    @DisplayName("Parte 1: el orden de las compuertas en el input no importa")
    void parte1_ordenDeLasCompuertas() {
        List<String> alReves = new ArrayList<>(EJEMPLO.subList(0, 11));
        List<String> compuertas = new ArrayList<>(EJEMPLO.subList(11, EJEMPLO.size()));
        Collections.reverse(compuertas);
        alReves.addAll(compuertas);
        assertEquals("2024", dia24.parte1(alReves));
    }

    @Test
    @DisplayName("Parte 1: solo los cables con letra y cifras son bits")
    void parte1_esBit() {
        assertTrue(Dia24.Circuito.esBit("z07", 'z'));
        assertFalse(Dia24.Circuito.esBit("zab", 'z'));
        assertFalse(Dia24.Circuito.esBit("x07", 'z'));
        assertFalse(Dia24.Circuito.esBit("z", 'z'));
    }

    @Test
    @DisplayName("Parte 1: un ciclo o un cable sin valor fallan con un mensaje claro")
    void parte1_errores() {
        assertThrows(IllegalStateException.class, () -> Dia24.Circuito.parsear(List.of(
                "x00: 1", "", "x00 AND aaa -> bbb", "x00 AND bbb -> aaa")).simular());
        assertThrows(IllegalArgumentException.class, () -> Dia24.Circuito.parsear(List.of(
                "x00: 1", "", "x00 AND nada -> z00")).simular());
        assertThrows(IllegalArgumentException.class, () -> Dia24.Circuito.parsear(List.of(
                "x00: 1", "", "x00 AND x00 -> z00", "x00 OR x00 -> z00")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    /** Ejemplo de la parte 2: un AND bit a bit de 6 bits con dos pares de salidas intercambiadas. */
    private static final List<String> EJEMPLO_AND = List.of(
            "x00: 0", "x01: 1", "x02: 0", "x03: 1", "x04: 0", "x05: 1",
            "y00: 0", "y01: 0", "y02: 1", "y03: 1", "y04: 0", "y05: 1",
            "",
            "x00 AND y00 -> z05", "x01 AND y01 -> z02", "x02 AND y02 -> z01",
            "x03 AND y03 -> z03", "x04 AND y04 -> z04", "x05 AND y05 -> z00"
    );

    /**
     * Un sumador de {@code bits} bits, correcto por construcción (sumador
     * con acarreo en cascada, como el del input real): en cada bit,
     * {@code z = x XOR y XOR acarreo} y el acarreo siguiente es
     * {@code (x AND y) OR ((x XOR y) AND acarreo)}.
     */
    private static List<String> sumador(int bits) {
        List<String> lineas = new ArrayList<>();
        for (int b = 0; b < bits; b++) lineas.add(String.format("x%02d: 0", b));
        for (int b = 0; b < bits; b++) lineas.add(String.format("y%02d: 0", b));
        lineas.add("");
        lineas.add("x00 XOR y00 -> z00");
        lineas.add("x00 AND y00 -> c00");
        for (int b = 1; b < bits; b++) {
            String x = String.format("x%02d", b), y = String.format("y%02d", b);
            String acarreo = String.format("c%02d", b - 1);
            lineas.add(x + " XOR " + y + " -> s" + b);
            lineas.add("s" + b + " XOR " + acarreo + String.format(" -> z%02d", b));
            lineas.add(x + " AND " + y + " -> a" + b);
            lineas.add("s" + b + " AND " + acarreo + " -> p" + b);
            lineas.add("a" + b + " OR p" + b + String.format(" -> c%02d", b));
        }
        // el último bit de z es el acarreo final (un OR consigo mismo, para que sea una compuerta)
        lineas.add(String.format("c%02d OR c%02d -> z%02d", bits - 1, bits - 1, bits));
        return lineas;
    }

    /** Intercambia los cables de salida de dos compuertas en las líneas del input. */
    private static List<String> intercambiar(List<String> lineas, String a, String b) {
        return lineas.stream().map(linea -> {
            if (linea.endsWith("-> " + a)) return linea.substring(0, linea.length() - a.length()) + b;
            if (linea.endsWith("-> " + b)) return linea.substring(0, linea.length() - b.length()) + a;
            return linea;
        }).toList();
    }

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado (un AND con dos pares intercambiados) da z00,z01,z02,z05")
    void parte2_ejemploDelEnunciado() {
        Dia24.Reparacion reparacion = new Dia24.Reparacion(Dia24.Circuito.parsear(EJEMPLO_AND), (x, y) -> x & y);
        assertEquals(List.of("z00", "z01", "z02", "z05"), reparacion.cablesIntercambiados(2));
    }

    @Test
    @DisplayName("Parte 2: el sumador generado suma bien y tiene el puntaje máximo")
    void parte2_sumadorCorrecto() {
        List<String> lineas = new ArrayList<>(sumador(10));
        // 345 + 678 = 1023, escrito en los cables iniciales
        for (int b = 0; b < 10; b++) {
            lineas.set(b, String.format("x%02d: %d", b, (345 >> b) & 1));
            lineas.set(10 + b, String.format("y%02d: %d", b, (678 >> b) & 1));
        }
        assertEquals(1023, Dia24.Circuito.parsear(lineas).numero('z'));
        assertEquals(11, new Dia24.Reparacion(Dia24.Circuito.parsear(sumador(10)), Long::sum).puntaje());
    }

    @Test
    @DisplayName("Parte 2: en un sumador de 20 bits con 4 pares intercambiados, encuentra exactamente esos 8 cables")
    void parte2_sumadorConIntercambios() {
        List<String> lineas = sumador(20);
        lineas = intercambiar(lineas, "z05", "a5");       // la suma con el acarreo parcial
        lineas = intercambiar(lineas, "s9", "a9");        // dentro de un bit
        lineas = intercambiar(lineas, "z13", "c13");      // una salida con un acarreo
        lineas = intercambiar(lineas, "p17", "z17");
        Dia24.Reparacion reparacion = new Dia24.Reparacion(Dia24.Circuito.parsear(lineas), Long::sum);
        assertTrue(reparacion.puntaje() < 20);
        assertEquals(List.of("a5", "a9", "c13", "p17", "s9", "z05", "z13", "z17"),
                reparacion.cablesIntercambiados(4));
    }

    @Test
    @DisplayName("Parte 2: si no alcanza con esa cantidad de pares, falla con un mensaje claro")
    void parte2_noAlcanza() {
        Dia24.Reparacion reparacion = new Dia24.Reparacion(Dia24.Circuito.parsear(EJEMPLO_AND), (x, y) -> x & y);
        assertThrows(IllegalStateException.class, () -> reparacion.cablesIntercambiados(1));
    }
}
