package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Punto")
class PruebaPunto {

    @Test
    @DisplayName("mover se desplaza en la dirección correcta")
    void mover() {
        Punto origen = new Punto(3, 3);
        assertEquals(new Punto(3, 2), origen.mover(Direccion.ARRIBA));
        assertEquals(new Punto(3, 4), origen.mover(Direccion.ABAJO));
        assertEquals(new Punto(2, 3), origen.mover(Direccion.IZQUIERDA));
        assertEquals(new Punto(4, 3), origen.mover(Direccion.DERECHA));
    }

    @Test
    @DisplayName("sumar y restar son operaciones inversas")
    void sumarRestar() {
        Punto a = new Punto(1, 2);
        Punto b = new Punto(3, 4);
        assertEquals(new Punto(4, 6), a.sumar(b));
        assertEquals(new Punto(-2, -2), a.restar(b));
    }

    @Test
    @DisplayName("escalar multiplica ambas coordenadas")
    void escalar() {
        assertEquals(new Punto(6, 10), new Punto(3, 5).escalar(2));
        assertEquals(new Punto(0, 0),  new Punto(3, 5).escalar(0));
    }

    @Test
    @DisplayName("distanciaManhattan calcula correctamente")
    void distanciaManhattan() {
        assertEquals(0, new Punto(0, 0).distanciaManhattan(new Punto(0, 0)));
        assertEquals(5, new Punto(0, 0).distanciaManhattan(new Punto(3, 2)));
        assertEquals(5, new Punto(3, 2).distanciaManhattan(new Punto(0, 0)));
    }

    @Test
    @DisplayName("vecinos retorna exactamente 4 vecinos cardinales")
    void vecinos() {
        List<Punto> vecinos = new Punto(0, 0).vecinos();
        assertEquals(4, vecinos.size());
        assertTrue(vecinos.contains(new Punto( 0, -1)));
        assertTrue(vecinos.contains(new Punto( 0,  1)));
        assertTrue(vecinos.contains(new Punto(-1,  0)));
        assertTrue(vecinos.contains(new Punto( 1,  0)));
    }

    @Test
    @DisplayName("vecinosConDiagonales retorna los 8 vecinos distintos, en orden de lectura")
    void vecinosConDiagonales() {
        assertEquals(List.of(
                new Punto(4, 6), new Punto(5, 6), new Punto(6, 6),
                new Punto(4, 7),                  new Punto(6, 7),
                new Punto(4, 8), new Punto(5, 8), new Punto(6, 8)),
                new Punto(5, 7).vecinosConDiagonales());
    }

    @Test
    @DisplayName("dentroDeGrilla detecta límites correctamente")
    void dentroDeGrilla() {
        assertTrue( new Punto(0, 0).dentroDeGrilla(5, 5));
        assertTrue( new Punto(4, 4).dentroDeGrilla(5, 5));
        assertFalse(new Punto(5, 0).dentroDeGrilla(5, 5));
        assertFalse(new Punto(0, 5).dentroDeGrilla(5, 5));
        assertFalse(new Punto(-1, 0).dentroDeGrilla(5, 5));
    }

    @Test
    @DisplayName("equals y hashCode son consistentes")
    void equalsHashCode() {
        Punto a = new Punto(1, 2);
        Punto b = new Punto(1, 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Punto(2, 1));
    }
}