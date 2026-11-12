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
}
