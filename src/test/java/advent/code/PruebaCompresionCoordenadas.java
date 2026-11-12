package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompresionCoordenadas")
class PruebaCompresionCoordenadas {

    @Test
    @DisplayName("deja hueco solo si hay posiciones reales entre dos coordenadas")
    void huecos() {
        // 2 | hueco | 7 8 | hueco | 11, con borde en 0 y al final
        CompresionCoordenadas compresion = new CompresionCoordenadas(List.of(11, 2, 8, 7, 2));
        assertEquals(1, compresion.indice(2));
        assertEquals(3, compresion.indice(7));
        assertEquals(4, compresion.indice(8));    // consecutiva de 7: sin hueco
        assertEquals(6, compresion.indice(11));
        assertEquals(8, compresion.tamanio());    // 0..7
        assertEquals(4, compresion.cantidadCoordenadas());
    }

    @Test
    @DisplayName("coordenadas todas consecutivas no generan huecos")
    void consecutivas() {
        CompresionCoordenadas compresion = new CompresionCoordenadas(List.of(5, 6, 7));
        assertEquals(List.of(1, 2, 3), List.of(compresion.indice(5), compresion.indice(6), compresion.indice(7)));
        assertEquals(5, compresion.tamanio());
    }

    @Test
    @DisplayName("conserva el orden y admite coordenadas negativas y grandes")
    void ordenYRango() {
        CompresionCoordenadas compresion = new CompresionCoordenadas(List.of(100_000, -50, 0));
        assertTrue(compresion.indice(-50) < compresion.indice(0));
        assertTrue(compresion.indice(0) < compresion.indice(100_000));
        assertEquals(7, compresion.tamanio());    // borde, -50, hueco, 0, hueco, 100000, borde
    }

    @Test
    @DisplayName("rechaza coordenadas que no se comprimieron; sin coordenadas solo quedan los bordes")
    void casosBorde() {
        assertThrows(IllegalArgumentException.class, () -> new CompresionCoordenadas(List.of(1, 5)).indice(3));
        assertEquals(2, new CompresionCoordenadas(List.of()).tamanio());
    }
}
