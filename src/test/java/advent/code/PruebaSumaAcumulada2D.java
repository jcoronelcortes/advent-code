package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SumaAcumulada2D")
class PruebaSumaAcumulada2D {

    private static final long[][] MATRIZ = {
            {1, 2, 3},
            {4, 5, 6},
    };

    @Test
    @DisplayName("suma de rectángulos, con esquinas en cualquier orden")
    void suma() {
        SumaAcumulada2D sumas = new SumaAcumulada2D(MATRIZ);
        assertEquals(21, sumas.suma(new Punto(0, 0), new Punto(2, 1)));   // todo
        assertEquals(5, sumas.suma(new Punto(1, 1), new Punto(1, 1)));    // una celda
        assertEquals(16, sumas.suma(new Punto(2, 1), new Punto(1, 0)));   // 2 + 3 + 5 + 6, esquinas invertidas
        assertEquals(2, sumas.filas());
        assertEquals(3, sumas.columnas());
    }

    @Test
    @DisplayName("contar: cuenta las celdas de una grilla que cumplen la condición")
    void contar() {
        Grilla grilla = Grilla.desdeLineas(List.of("#.#", "##.", "..#"));
        SumaAcumulada2D muros = SumaAcumulada2D.contar(grilla, c -> c == '#');
        assertEquals(5, muros.suma(new Punto(0, 0), new Punto(2, 2)));
        assertEquals(3, muros.suma(new Punto(0, 0), new Punto(1, 1)));
        assertEquals(0, muros.suma(new Punto(1, 2), new Punto(1, 2)));
    }

    @Test
    @DisplayName("rechaza rectángulos fuera de la matriz y matrices no rectangulares")
    void invalidos() {
        SumaAcumulada2D sumas = new SumaAcumulada2D(MATRIZ);
        assertThrows(IndexOutOfBoundsException.class, () -> sumas.suma(new Punto(0, 0), new Punto(3, 1)));
        assertThrows(IllegalArgumentException.class, () -> new SumaAcumulada2D(new long[][]{{1, 2}, {3}}));
    }

    @Property(tries = 300)
    @Label("coincide con sumar celda por celda")
    void coincideConFuerzaBruta(
            @ForAll @IntRange(min = 0, max = 1000) int semilla,
            @ForAll @IntRange(min = 0, max = 5) int x1, @ForAll @IntRange(min = 0, max = 5) int x2,
            @ForAll @IntRange(min = 0, max = 4) int y1, @ForAll @IntRange(min = 0, max = 4) int y2) {
        long[][] matriz = new long[5][6];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 6; x++) matriz[y][x] = (semilla * 31L + y * 7L + x * 13L) % 19 - 9;   // incluye negativos
        }
        long esperado = 0;
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) esperado += matriz[y][x];
        }
        assertEquals(esperado, new SumaAcumulada2D(matriz).suma(new Punto(x1, y1), new Punto(x2, y2)));
    }
}
