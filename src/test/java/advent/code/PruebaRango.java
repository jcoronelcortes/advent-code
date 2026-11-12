package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rango")
class PruebaRango {

    // -------------------------------------------------------------------------
    // Creación
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("constructor: rechaza inicio mayor que fin")
    void constructor_invalido() {
        assertThrows(IllegalArgumentException.class, () -> new Rango(5, 4));
    }

    @Test
    @DisplayName("parsear: formato inicio-fin, con y sin espacios")
    void parsear_valido() {
        assertEquals(new Rango(95, 115), Rango.parsear("95-115"));
        assertEquals(new Rango(1188511880L, 1188511890L), Rango.parsear(" 1188511880 - 1188511890 "));
    }

    @Test
    @DisplayName("parsear: rechaza formatos inválidos")
    void parsear_invalido() {
        assertThrows(IllegalArgumentException.class, () -> Rango.parsear("95"));
        assertThrows(IllegalArgumentException.class, () -> Rango.parsear("a-b"));
        assertThrows(IllegalArgumentException.class, () -> Rango.parsear("1-2-3"));
    }

    @Test
    @DisplayName("desdeLargo: inicio y cantidad de elementos")
    void desdeLargo() {
        assertEquals(new Rango(79, 92), Rango.desdeLargo(79, 14));
        assertThrows(IllegalArgumentException.class, () -> Rango.desdeLargo(1, 0));
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("largo: cantidad de enteros incluidos")
    void largo() {
        assertEquals(12, new Rango(11, 22).largo());
        assertEquals(1, new Rango(5, 5).largo());
    }

    @Test
    @DisplayName("contiene: valores en los bordes y fuera")
    void contiene_valor() {
        Rango rango = new Rango(10, 20);
        assertTrue(rango.contiene(10));
        assertTrue(rango.contiene(20));
        assertFalse(rango.contiene(9));
        assertFalse(rango.contiene(21));
    }

    @Test
    @DisplayName("contiene: otro rango completo")
    void contiene_rango() {
        Rango rango = new Rango(10, 20);
        assertTrue(rango.contiene(new Rango(12, 18)));
        assertTrue(rango.contiene(rango));
        assertFalse(rango.contiene(new Rango(15, 25)));
    }

    @Test
    @DisplayName("intersecta: solapados, tocándose y separados")
    void intersecta() {
        Rango rango = new Rango(10, 20);
        assertTrue(rango.intersecta(new Rango(15, 25)));
        assertTrue(rango.intersecta(new Rango(20, 30)));    // comparten el 20
        assertFalse(rango.intersecta(new Rango(21, 30)));
        assertFalse(rango.intersecta(new Rango(1, 9)));
    }

    @Test
    @DisplayName("interseccion: parte común o vacío")
    void interseccion() {
        Rango rango = new Rango(10, 20);
        assertEquals(Optional.of(new Rango(15, 20)), rango.interseccion(new Rango(15, 25)));
        assertEquals(Optional.of(new Rango(12, 14)), rango.interseccion(new Rango(12, 14)));
        assertEquals(Optional.empty(), rango.interseccion(new Rango(30, 40)));
    }

    // -------------------------------------------------------------------------
    // Operaciones
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("restar: quitar del medio deja dos partes")
    void restar_medio() {
        assertEquals(List.of(new Rango(1, 3), new Rango(7, 10)),
                new Rango(1, 10).restar(new Rango(4, 6)));
    }

    @Test
    @DisplayName("restar: quitar un borde, todo o nada")
    void restar_bordesYExtremos() {
        assertEquals(List.of(new Rango(6, 10)), new Rango(1, 10).restar(new Rango(0, 5)));
        assertEquals(List.of(new Rango(1, 4)), new Rango(1, 10).restar(new Rango(5, 15)));
        assertEquals(List.of(), new Rango(1, 10).restar(new Rango(0, 20)));
        assertEquals(List.of(new Rango(1, 10)), new Rango(1, 10).restar(new Rango(11, 20)));
    }

    @Test
    @DisplayName("desplazar: suma a ambos extremos")
    void desplazar() {
        assertEquals(new Rango(52, 99), new Rango(50, 97).desplazar(2));
        assertEquals(new Rango(-5, 0), new Rango(0, 5).desplazar(-5));
    }

    @Test
    @DisplayName("unir: fusiona solapados y contiguos, ordena el resultado")
    void unir() {
        List<Rango> rangos = List.of(
                new Rango(20, 25), new Rango(1, 3), new Rango(4, 6),   // 1-3 y 4-6 son contiguos
                new Rango(10, 15), new Rango(12, 18), new Rango(30, 30)
        );
        assertEquals(List.of(new Rango(1, 6), new Rango(10, 18), new Rango(20, 25), new Rango(30, 30)),
                Rango.unir(rangos));
    }

    @Test
    @DisplayName("unir: un rango que contiene a otros los absorbe")
    void unir_contenidos() {
        assertEquals(List.of(new Rango(1, 100)),
                Rango.unir(List.of(new Rango(1, 100), new Rango(5, 10), new Rango(50, 60))));
        assertEquals(List.of(), Rango.unir(List.of()));
    }

    @Test
    @DisplayName("unir: rango que llega a Long.MAX_VALUE no desborda")
    void unir_valoresExtremos() {
        assertEquals(List.of(new Rango(0, Long.MAX_VALUE)),
                Rango.unir(List.of(new Rango(0, Long.MAX_VALUE), new Rango(Long.MAX_VALUE, Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("contarCubiertos: cuenta una sola vez los solapamientos (AoC 2016 día 20)")
    void contarCubiertos() {
        // IPs bloqueadas 5-8, 0-2, 4-7 en el espacio 0..9 → libres 3 y 9
        List<Rango> bloqueadas = List.of(new Rango(5, 8), new Rango(0, 2), new Rango(4, 7));
        assertEquals(8, Rango.contarCubiertos(bloqueadas));
        assertEquals(2, 10 - Rango.contarCubiertos(bloqueadas));
    }

    @Test
    @DisplayName("compareTo: ordena por inicio y luego por fin")
    void ordenar() {
        List<Rango> rangos = new ArrayList<>(List.of(new Rango(5, 9), new Rango(1, 8), new Rango(5, 6)));
        rangos.sort(null);
        assertEquals(List.of(new Rango(1, 8), new Rango(5, 6), new Rango(5, 9)), rangos);
    }

    @Test
    @DisplayName("toString: formato inicio-fin")
    void textoDelRango() {
        assertEquals("95-115", new Rango(95, 115).toString());
    }
}
