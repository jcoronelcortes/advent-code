package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Utiles")
class PruebaUtiles {

    @Test
    @DisplayName("leerNumeros extrae enteros positivos de una cadena")
    void leerNumeros_positivos() {
        assertEquals(List.of(1, 2, 3), Utiles.leerNumeros("1 2 3"));
    }

    @Test
    @DisplayName("leerNumeros extrae enteros negativos y mixtos")
    void leerNumeros_negativos() {
        assertEquals(List.of(-5, 10, -3), Utiles.leerNumeros("-5 10 -3"));
    }

    @Test
    @DisplayName("leerNumeros ignora el texto no numérico")
    void leerNumeros_conTexto() {
        assertEquals(List.of(-1, 5, 10), Utiles.leerNumeros("x=-1, y=5, z=10"));
        assertEquals(List.of(12, 3),     Utiles.leerNumeros("Día 12: grupo de 3 elfos"));
    }

    @Test
    @DisplayName("leerNumeros lanza error si un número no cabe en int")
    void leerNumeros_desborde() {
        assertThrows(ArithmeticException.class, () -> Utiles.leerNumeros("9898998927"));
    }

    @Test
    @DisplayName("leerNumerosLargos soporta números de más de 10 dígitos")
    void leerNumerosLargos() {
        assertEquals(List.of(6868562486L, -6868811237L),
                Utiles.leerNumerosLargos("6868562486 a -6868811237"));
    }

    @Test
    @DisplayName("leerNumerosLargos une los números de varias líneas")
    void leerNumerosLargos_lineas() {
        assertEquals(List.of(1L, 2L, 30000000000L),
                Utiles.leerNumerosLargos(List.of("1 2", "", "x=30000000000")));
    }

    @Test
    @DisplayName("lineasNoVacias: quita espacios de los extremos y descarta líneas en blanco")
    void lineasNoVacias() {
        assertEquals(List.of("a", "b c"), Utiles.lineasNoVacias(List.of("", "  a ", "   ", "b c", "")));
        assertEquals(List.of(), Utiles.lineasNoVacias(List.of("", " ")));
    }

    @Test
    @DisplayName("secciones: corta en las líneas en blanco")
    void secciones() {
        assertEquals(List.of(List.of("3-5", "10-14"), List.of("1", "5")),
                Utiles.secciones(List.of("3-5", "10-14", "", "1", "5")));
    }

    @Test
    @DisplayName("secciones: varias líneas en blanco seguidas, al inicio o al final no crean secciones vacías")
    void secciones_lineasEnBlancoExtra() {
        assertEquals(List.of(List.of("a"), List.of("b", "c"), List.of("d")),
                Utiles.secciones(List.of("", "a", "", "", " ", "b", " c ", "", "d", "", "")));
        assertEquals(List.of(), Utiles.secciones(List.of("", "")));
        assertEquals(List.of(List.of("solo")), Utiles.secciones(List.of("solo")));
    }

    @Test
    @DisplayName("numerosPorLinea: una lista por línea, ignorando las vacías")
    void numerosPorLinea() {
        assertEquals(List.of(List.of(7, 6, 4), List.of(1, -2)),
                Utiles.numerosPorLinea(List.of("7 6 4", "", "1,-2", " ")));
        assertEquals(List.of(List.of(30000000000L, 1L)),
                Utiles.numerosLargosPorLinea(List.of("30000000000   1")));
        assertEquals(List.of(List.of()), Utiles.numerosPorLinea(List.of("sin números")));
    }

    @Test
    @DisplayName("secciones con cantidad: devuelve las secciones si coinciden y falla si no")
    void seccionesConCantidad() {
        List<String> lineas = List.of("a", "", "b");
        assertEquals(List.of(List.of("a"), List.of("b")), Utiles.secciones(lineas, 2));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> Utiles.secciones(lineas, 3));
        assertTrue(error.getMessage().contains("Se esperaban 3 secciones"), error.getMessage());
    }

    @Test
    @DisplayName("leerNumeros con cantidad: devuelve los números si coinciden y falla si no")
    void leerNumerosConCantidad() {
        assertEquals(List.of(0, 4, 3, -3), Utiles.leerNumeros("p=0,4 v=3,-3", 4));
        assertEquals(List.of(30000000000L, 1L), Utiles.leerNumerosLargos("30000000000 1", 2));
        assertEquals(List.of(94L, 34L, 22L, 67L, 8400L, 5400L), Utiles.leerNumerosLargos(
                List.of("Button A: X+94, Y+34", "Button B: X+22, Y+67", "Prize: X=8400, Y=5400"), 6));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> Utiles.leerNumeros("1 2 3", 2));
        assertTrue(error.getMessage().contains("Se esperaban 2 números y hay 3 en: 1 2 3"), error.getMessage());
        assertThrows(IllegalArgumentException.class, () -> Utiles.leerNumerosLargos("1", 2));
        assertThrows(IllegalArgumentException.class, () -> Utiles.leerNumerosLargos(List.of("1", "2"), 3));
    }
}
