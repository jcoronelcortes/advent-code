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
    @DisplayName("leerGrilla convierte líneas a matriz de caracteres")
    void leerGrilla() {
        char[][] grilla = Utiles.leerGrilla(List.of("ABC", "DEF"));
        assertEquals(2, grilla.length);
        assertEquals(3, grilla[0].length);
        assertEquals('A', grilla[0][0]);
        assertEquals('F', grilla[1][2]);
    }

    @Test
    @DisplayName("transponerGrilla intercambia filas y columnas")
    void transponerGrilla() {
        char[][] original = Utiles.leerGrilla(List.of("ABC", "DEF"));
        char[][] transpuesta = Utiles.transponerGrilla(original);
        assertEquals(3, transpuesta.length);
        assertEquals(2, transpuesta[0].length);
        assertEquals('A', transpuesta[0][0]);
        assertEquals('D', transpuesta[0][1]);
        assertEquals('B', transpuesta[1][0]);
    }

    @Test
    @DisplayName("obtenerCaracter lee correctamente por Punto")
    void obtenerCaracter() {
        char[][] grilla = Utiles.leerGrilla(List.of("ABC", "DEF"));
        assertEquals('A', Utiles.obtenerCaracter(grilla, new Punto(0, 0)));
        assertEquals('C', Utiles.obtenerCaracter(grilla, new Punto(2, 0)));
        assertEquals('D', Utiles.obtenerCaracter(grilla, new Punto(0, 1)));
    }
}