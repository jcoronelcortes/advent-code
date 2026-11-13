package advent.code.y2023;

import advent.code.Rango;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 19 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 19: A Montones")
class Dia19Prueba {

    private Dia19 dia19;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "px{a<2006:qkq,m>2090:A,rfg}",
            "pv{a>1716:R,A}",
            "lnx{m>1548:A,A}",
            "rfg{s<537:gd,x>2440:R,A}",
            "qs{s>3448:A,lnx}",
            "qkq{x<1416:A,crn}",
            "crn{x>2662:A,R}",
            "in{s<1351:px,qqz}",
            "qqz{s>2770:qs,m<1801:hdj,R}",
            "gd{a>3333:R,R}",
            "hdj{m>838:A,pv}",
            "",
            "{x=787,m=2655,a=1222,s=2876}",
            "{x=1679,m=44,a=2067,s=496}",
            "{x=2036,m=264,a=79,s=2244}",
            "{x=2461,m=1339,a=466,s=291}",
            "{x=2127,m=1623,a=2188,s=1013}"
    );

    @BeforeEach
    void configurar() {
        dia19 = new Dia19();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("19114", dia19.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: se aceptan la primera, la tercera y la quinta pieza del ejemplo")
    void parte1_cadaPieza() {
        Dia19.Sistema sistema = Dia19.leer(EJEMPLO);
        assertEquals(5, sistema.piezas().size());
        assertArrayEquals(new int[]{787, 2655, 1222, 2876}, sistema.piezas().getFirst());
        List<Boolean> aceptadas = sistema.piezas().stream()
                .map(pieza -> Dia19.esAceptada(sistema.flujos(), pieza)).toList();
        assertEquals(List.of(true, false, true, false, true), aceptadas);
    }

    @Test
    @DisplayName("Parte 1: se aplica la primera regla que cumple, con < y > estrictos")
    void parte1_primeraRegla() {
        // ex{x>10:one,m<20:two,a>30:R,A} del enunciado, con one aceptando y two rechazando
        List<String> sistema = List.of("in{x>10:one,m<20:two,a>30:R,A}", "one{A}", "two{R}", "", "{x=0,m=0,a=0,s=0}");
        var flujos = Dia19.leer(sistema).flujos();
        assertTrue(Dia19.esAceptada(flujos, new int[]{11, 0, 0, 0}));     // x>10 gana aunque m<20
        assertFalse(Dia19.esAceptada(flujos, new int[]{10, 19, 0, 0}));   // x=10 no es >10; m<20
        assertTrue(Dia19.esAceptada(flujos, new int[]{10, 20, 30, 0}));   // nada se cumple: A
        assertFalse(Dia19.esAceptada(flujos, new int[]{10, 20, 31, 0}));  // a>30: R
    }

    @Test
    @DisplayName("Parte 1: un input mal formado falla con un mensaje claro")
    void parte1_inputInvalido() {
        for (List<String> invalido : List.of(
                List.of("px{a<2006:A,R}", "", "{x=1,m=1,a=1,s=1}"),                   // falta in
                List.of("in{a<2006:zz,R}", "", "{x=1,m=1,a=1,s=1}"),                  // destino desconocido
                List.of("in{a<2006:A}", "", "{x=1,m=1,a=1,s=1}"),                     // la última tiene condición
                List.of("in{A,R}", "", "{x=1,m=1,a=1,s=1}"),                          // una del medio no tiene
                List.of("in{A}", "in{R}", "", "{x=1,m=1,a=1,s=1}"),                   // flujo repetido
                List.of("in{A}", "", "{x=1,m=1,a=1}"),                                // pieza incompleta
                List.of("in{A}"))) {                                                  // falta la sección de piezas
            assertThrows(IllegalArgumentException.class, () -> Dia19.leer(invalido), invalido.toString());
        }
    }

    @Test
    @DisplayName("Parte 1: una pieza que vuelve a un flujo por el que ya pasó falla en vez de no terminar")
    void parte1_ciclo() {
        var flujos = Dia19.leer(List.of("in{x>5:aa,A}", "aa{in}", "", "{x=1,m=1,a=1,s=1}")).flujos();
        assertTrue(Dia19.esAceptada(flujos, new int[]{1, 1, 1, 1}));
        assertThrows(IllegalStateException.class, () -> Dia19.esAceptada(flujos, new int[]{9, 1, 1, 1}));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("167409079868000", dia19.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: cumplen y noCumplen parten el rango en dos sin perder ni repetir valores")
    void parte2_partirRango() {
        Rango rango = new Rango(1, 4000);
        Dia19.Regla menor = new Dia19.Regla(0, '<', 1416, "A");
        assertEquals(Optional.of(new Rango(1, 1415)), menor.cumplen(rango));
        assertEquals(Optional.of(new Rango(1416, 4000)), menor.noCumplen(rango));
        Dia19.Regla mayor = new Dia19.Regla(0, '>', 2662, "A");
        assertEquals(Optional.of(new Rango(2663, 4000)), mayor.cumplen(rango));
        assertEquals(Optional.of(new Rango(1, 2662)), mayor.noCumplen(rango));
        // En los bordes, una de las dos partes queda vacía
        assertEquals(Optional.empty(), new Dia19.Regla(0, '<', 1, "A").cumplen(rango));
        assertEquals(Optional.empty(), new Dia19.Regla(0, '>', 4000, "A").cumplen(rango));
        assertEquals(Optional.empty(), new Dia19.Regla(0, '<', 5000, "A").noCumplen(rango));
    }

    @Test
    @DisplayName("Parte 2: coincide con evaluar una pieza por celda de la grilla que forman los umbrales del ejemplo")
    void parte2_fuerzaBrutaComprimida() {
        Map<String, List<Dia19.Regla>> flujos = Dia19.leer(EJEMPLO).flujos();
        assertEquals(167409079868000L, porCeldas(flujos, 4000));
    }

    @Test
    @DisplayName("Parte 2: coincide con probar pieza por pieza todas las combinaciones con calificaciones de 1 a 12")
    void parte2_fuerzaBrutaChica() {
        List<String> sistema = List.of(
                "in{x<5:uno,m>8:dos,a<3:R,tres}",
                "uno{s>6:A,a>9:dos,x<2:R,A}",
                "dos{x>9:R,s<4:A,tres}",
                "tres{m<7:A,s>10:A,R}",
                "", "{x=1,m=1,a=1,s=1}");
        Map<String, List<Dia19.Regla>> flujos = Dia19.leer(sistema).flujos();
        long aceptadas = 0;
        for (int x = 1; x <= 12; x++)
            for (int m = 1; m <= 12; m++)
                for (int a = 1; a <= 12; a++)
                    for (int s = 1; s <= 12; s++)
                        if (Dia19.esAceptada(flujos, new int[]{x, m, a, s})) aceptadas++;
        Rango doce = new Rango(1, 12);
        assertEquals(aceptadas, Dia19.combinacionesAceptadas(flujos, new Rango[]{doce, doce, doce, doce}));
        assertEquals(aceptadas, porCeldas(flujos, 12));
    }

    @Test
    @DisplayName("Parte 2: sin reglas con condición se acepta o rechaza todo; un ciclo de flujos falla")
    void parte2_casosExtremos() {
        assertEquals("256000000000000", dia19.parte2(List.of("in{A}", "", "{x=1,m=1,a=1,s=1}")));
        assertEquals("0", dia19.parte2(List.of("in{R}", "", "{x=1,m=1,a=1,s=1}")));
        assertThrows(IllegalStateException.class,
                () -> dia19.parte2(List.of("in{x>5:aa,A}", "aa{in}", "", "{x=1,m=1,a=1,s=1}")));
    }

    /**
     * Fuerza bruta con compresión: los umbrales de las reglas parten cada
     * categoría en intervalos donde ninguna condición cambia de valor. Se
     * evalúa una pieza por cada celda (con {@code esAceptada}, de la parte 1)
     * y, si se acepta, se suma el tamaño de la celda.
     */
    private static long porCeldas(Map<String, List<Dia19.Regla>> flujos, int maximo) {
        List<List<Integer>> cortes = new ArrayList<>();
        for (int c = 0; c < 4; c++) {
            TreeSet<Integer> puntos = new TreeSet<>(List.of(1, maximo + 1));
            for (List<Dia19.Regla> reglas : flujos.values()) {
                for (Dia19.Regla r : reglas) {
                    if (r.categoria() != c) continue;
                    // "< v" cambia de valor entre v−1 y v; "> v", entre v y v+1
                    int corte = r.operador() == '<' ? r.valor() : r.valor() + 1;
                    if (corte > 1 && corte <= maximo) puntos.add(corte);
                }
            }
            cortes.add(new ArrayList<>(puntos));
        }
        long total = 0;
        int[] pieza = new int[4];
        List<Integer> cx = cortes.get(0), cm = cortes.get(1), ca = cortes.get(2), cs = cortes.get(3);
        for (int i = 0; i + 1 < cx.size(); i++) {
            pieza[0] = cx.get(i);
            for (int j = 0; j + 1 < cm.size(); j++) {
                pieza[1] = cm.get(j);
                for (int k = 0; k + 1 < ca.size(); k++) {
                    pieza[2] = ca.get(k);
                    for (int l = 0; l + 1 < cs.size(); l++) {
                        pieza[3] = cs.get(l);
                        if (Dia19.esAceptada(flujos, pieza)) {
                            total += (long) (cx.get(i + 1) - cx.get(i)) * (cm.get(j + 1) - cm.get(j))
                                    * (ca.get(k + 1) - ca.get(k)) * (cs.get(l + 1) - cs.get(l));
                        }
                    }
                }
            }
        }
        return total;
    }
}
