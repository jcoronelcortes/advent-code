package advent.code.y2024;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 7 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 7: Reparación del Puente")
class Dia07Prueba {

    private Dia07 dia07;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "190: 10 19",
            "3267: 81 40 27",
            "83: 17 5",
            "156: 15 6",
            "7290: 6 8 6 15",
            "161011: 16 10 13",
            "192: 17 8 14",
            "21037: 9 7 18 13",
            "292: 11 6 16 20"
    );

    @BeforeEach
    void configurar() {
        dia07 = new Dia07();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    private static final Set<Dia07.Operador> SUMA_Y_PRODUCTO = EnumSet.of(Dia07.Operador.SUMA, Dia07.Operador.PRODUCTO);

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("3749", dia07.parte1(EJEMPLO));   // 190 + 3267 + 292
    }

    @Test
    @DisplayName("Parte 1: solo las ecuaciones 190, 3267 y 292 del ejemplo son posibles")
    void parte1_ecuacionesDelEjemplo() {
        assertEquals(List.of(true, true, false, false, false, false, false, false, true),
                Dia07.leerEcuaciones(EJEMPLO).stream().map(e -> e.esPosible(SUMA_Y_PRODUCTO)).toList());
    }

    @Test
    @DisplayName("Parte 1: se evalúa de izquierda a derecha, sin precedencia")
    void parte1_izquierdaADerecha() {
        // 2 + 3 * 4 = (2 + 3) * 4 = 20, no 14
        assertTrue(new Dia07.Ecuacion(20, List.of(2L, 3L, 4L)).esPosible(SUMA_Y_PRODUCTO));
        assertFalse(new Dia07.Ecuacion(14, List.of(2L, 3L, 4L)).esPosible(SUMA_Y_PRODUCTO));
        // Un solo número: solo es posible si es igual al valor
        assertTrue(new Dia07.Ecuacion(7, List.of(7L)).esPosible(SUMA_Y_PRODUCTO));
        assertFalse(new Dia07.Ecuacion(8, List.of(7L)).esPosible(SUMA_Y_PRODUCTO));
    }

    @Test
    @DisplayName("Parte 1: deshacer cada operador")
    void parte1_deshacer() {
        assertEquals(OptionalLong.of(171), Dia07.Operador.SUMA.deshacer(190, 19));
        assertEquals(OptionalLong.empty(), Dia07.Operador.SUMA.deshacer(19, 19));     // lo anterior sería 0
        assertEquals(OptionalLong.of(10), Dia07.Operador.PRODUCTO.deshacer(190, 19));
        assertEquals(OptionalLong.empty(), Dia07.Operador.PRODUCTO.deshacer(191, 19)); // no es divisible
    }

    @Property(tries = 500)
    @Label("Parte 1: la búsqueda hacia atrás coincide con probar todas las combinaciones hacia adelante")
    void parte1_coincideConFuerzaBruta(
            @ForAll @Size(min = 1, max = 7) List<@IntRange(min = 1, max = 20) Integer> numeros,
            @ForAll @IntRange(min = 1, max = 3000) int valor) {
        List<Long> largos = numeros.stream().map(Integer::longValue).toList();
        boolean esperado = false;
        for (int combinacion = 0; combinacion < 1 << (largos.size() - 1); combinacion++) {
            long resultado = largos.getFirst();
            for (int i = 1; i < largos.size(); i++) {
                resultado = (combinacion >> (i - 1) & 1) == 0 ? resultado + largos.get(i) : resultado * largos.get(i);
            }
            if (resultado == valor) esperado = true;
        }
        assertEquals(esperado, new Dia07.Ecuacion(valor, largos).esPosible(SUMA_Y_PRODUCTO));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("11387", dia07.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: con || se suman las ecuaciones 156, 7290 y 192 del ejemplo")
    void parte2_ecuacionesDelEjemplo() {
        Set<Dia07.Operador> todos = EnumSet.allOf(Dia07.Operador.class);
        assertEquals(List.of(true, true, false, true, true, false, true, false, true),
                Dia07.leerEcuaciones(EJEMPLO).stream().map(e -> e.esPosible(todos)).toList());
    }

    @Test
    @DisplayName("Parte 2: deshacer la concatenación")
    void parte2_deshacerConcatenacion() {
        Dia07.Operador concatenar = Dia07.Operador.CONCATENACION;
        assertEquals(OptionalLong.of(15), concatenar.deshacer(156, 6));
        assertEquals(OptionalLong.of(12), concatenar.deshacer(12345, 345));
        assertEquals(OptionalLong.of(1), concatenar.deshacer(110, 10));      // operando con un cero al final
        assertEquals(OptionalLong.empty(), concatenar.deshacer(156, 7));     // no termina en 7
        assertEquals(OptionalLong.empty(), concatenar.deshacer(156, 156));   // igual al resultado: lo anterior sería 0
        assertEquals(OptionalLong.empty(), concatenar.deshacer(56, 456));    // el operando es más largo
    }

    @Test
    @DisplayName("Parte 2: la concatenación se evalúa en su turno, de izquierda a derecha")
    void parte2_izquierdaADerecha() {
        Set<Dia07.Operador> todos = EnumSet.allOf(Dia07.Operador.class);
        // 6 * 8 || 6 * 15 = ((6 * 8) || 6) * 15 = 486 * 15 = 7290
        assertTrue(new Dia07.Ecuacion(7290, List.of(6L, 8L, 6L, 15L)).esPosible(todos));
        // 17 || 8 + 14 = 178 + 14 = 192
        assertTrue(new Dia07.Ecuacion(192, List.of(17L, 8L, 14L)).esPosible(todos));
    }

    @Property(tries = 500)
    @Label("Parte 2: con los tres operadores coincide con probar todas las combinaciones hacia adelante")
    void parte2_coincideConFuerzaBruta(
            @ForAll @Size(min = 1, max = 6) List<@IntRange(min = 1, max = 30) Integer> numeros,
            @ForAll @IntRange(min = 1, max = 100000) int valor) {
        List<Long> largos = numeros.stream().map(Integer::longValue).toList();
        int huecos = largos.size() - 1;
        boolean esperado = false;
        for (int combinacion = 0; combinacion < Math.pow(3, huecos); combinacion++) {
            long resultado = largos.getFirst();
            int resto = combinacion;
            for (int i = 1; i < largos.size(); i++) {
                long n = largos.get(i);
                resultado = switch (resto % 3) {
                    case 0 -> resultado + n;
                    case 1 -> resultado * n;
                    default -> Long.parseLong(resultado + "" + n);
                };
                resto /= 3;
            }
            if (resultado == valor) esperado = true;
        }
        assertEquals(esperado, new Dia07.Ecuacion(valor, largos).esPosible(EnumSet.allOf(Dia07.Operador.class)));
    }
}
