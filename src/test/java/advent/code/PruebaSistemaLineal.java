package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SistemaLineal")
class PruebaSistemaLineal {

    private static BigFraction[] fracciones(long... valores) {
        BigFraction[] resultado = new BigFraction[valores.length];
        for (int i = 0; i < valores.length; i++) resultado[i] = new BigFraction(valores[i]);
        return resultado;
    }

    @Test
    @DisplayName("resuelve un sistema de 2 × 2 con solución entera")
    void dosPorDos() {
        // x + y = 3, x − y = 1
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{1, 1}, {1, -1}}, new long[]{3, 1});
        assertArrayEquals(fracciones(2, 1), SistemaLineal.resolverUnica(sistema).orElseThrow());
    }

    @Test
    @DisplayName("da la solución fraccionaria exacta, sin redondeos")
    void solucionFraccionaria() {
        // 3x = 1, x + 3y = 0  →  x = 1/3, y = −1/9
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{3, 0}, {1, 3}}, new long[]{1, 0});
        assertArrayEquals(new BigFraction[]{new BigFraction(1, 3), new BigFraction(-1, 9)},
                SistemaLineal.resolverUnica(sistema).orElseThrow());
    }

    @Test
    @DisplayName("necesita cambiar filas cuando el primer coeficiente es cero")
    void cambioDeFilas() {
        // y = 2, x + y = 5
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{0, 1}, {1, 1}}, new long[]{2, 5});
        assertArrayEquals(fracciones(3, 2), SistemaLineal.resolverUnica(sistema).orElseThrow());
    }

    @Test
    @DisplayName("sin solución: la escalonada no es consistente y no hay solución única")
    void sinSolucion() {
        // x + y = 1, x + y = 2
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{1, 1}, {1, 1}}, new long[]{1, 2});
        SistemaLineal.Escalonada escalonada = SistemaLineal.escalonar(sistema);
        assertFalse(escalonada.consistente());
        assertEquals(Optional.empty(), SistemaLineal.resolverUnica(sistema));
    }

    @Test
    @DisplayName("infinitas soluciones: deja las variables libres y cada pivote en función de ellas")
    void variablesLibres() {
        // x + z = 4, y + 2z = 5 (z libre)  →  x = 4 − z, y = 5 − 2z
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{1, 0, 1}, {0, 1, 2}}, new long[]{4, 5});
        SistemaLineal.Escalonada escalonada = SistemaLineal.escalonar(sistema);
        assertTrue(escalonada.consistente());
        assertFalse(escalonada.tieneSolucionUnica());
        assertArrayEquals(new int[]{0, 1}, escalonada.pivotes());
        assertArrayEquals(new int[]{2}, escalonada.libres());
        assertArrayEquals(fracciones(1, 0, 1, 4), escalonada.matriz()[0]);
        assertArrayEquals(fracciones(0, 1, 2, 5), escalonada.matriz()[1]);
        assertEquals(Optional.empty(), SistemaLineal.resolverUnica(sistema));
    }

    @Test
    @DisplayName("más ecuaciones que incógnitas: si son compatibles hay solución única")
    void ecuacionesDeMas() {
        // x = 2, y = 3, x + y = 5
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{1, 0}, {0, 1}, {1, 1}}, new long[]{2, 3, 5});
        assertArrayEquals(fracciones(2, 3), SistemaLineal.resolverUnica(sistema).orElseThrow());
    }

    @Test
    @DisplayName("no modifica la matriz que recibe, y rechaza filas de distinto largo")
    void noModificaYValida() {
        BigFraction[][] sistema = SistemaLineal.aumentada(new long[][]{{2, 4}, {1, 3}}, new long[]{6, 5});
        BigFraction[][] copia = {sistema[0].clone(), sistema[1].clone()};
        SistemaLineal.resolverUnica(sistema);
        assertArrayEquals(copia, sistema);
        assertThrows(IllegalArgumentException.class, () -> SistemaLineal.aumentada(new long[][]{{1, 2}, {3}}, new long[]{1, 2}));
        assertThrows(IllegalArgumentException.class, () -> SistemaLineal.aumentada(new long[][]{{1, 2}}, new long[]{1, 2}));
    }

    @Test
    @DisplayName("coeficientes enormes (más allá de long) se resuelven exactos")
    void coeficientesEnormes() {
        BigInteger enorme = BigInteger.TEN.pow(30);
        // enorme·x + y = enorme·7 + 3, x − y = 4  →  x = 7, y = 3
        BigFraction[][] sistema = {
                {new BigFraction(enorme), BigFraction.ONE, new BigFraction(enorme.multiply(BigInteger.valueOf(7)).add(BigInteger.valueOf(3)))},
                {BigFraction.ONE, BigFraction.MINUS_ONE, new BigFraction(4)}};
        assertArrayEquals(fracciones(7, 3), SistemaLineal.resolverUnica(sistema).orElseThrow());
    }

    @Property
    @Label("con A invertible y b = A·x, devuelve exactamente x")
    void recuperaLaSolucion(@ForAll @Size(9) List<@IntRange(min = -9, max = 9) Integer> a,
                            @ForAll @Size(3) List<@IntRange(min = -50, max = 50) Integer> x) {
        long[][] coeficientes = new long[3][3];
        for (int i = 0; i < 9; i++) coeficientes[i / 3][i % 3] = a.get(i);
        long determinante = coeficientes[0][0] * (coeficientes[1][1] * coeficientes[2][2] - coeficientes[1][2] * coeficientes[2][1])
                - coeficientes[0][1] * (coeficientes[1][0] * coeficientes[2][2] - coeficientes[1][2] * coeficientes[2][0])
                + coeficientes[0][2] * (coeficientes[1][0] * coeficientes[2][1] - coeficientes[1][1] * coeficientes[2][0]);
        long[] b = new long[3];
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) b[i] += coeficientes[i][j] * x.get(j);
        Optional<BigFraction[]> solucion = SistemaLineal.resolverUnica(SistemaLineal.aumentada(coeficientes, b));
        if (determinante == 0) {
            assertTrue(solucion.isEmpty());
        } else {
            assertArrayEquals(fracciones(x.get(0), x.get(1), x.get(2)), solucion.orElseThrow());
        }
    }
}
