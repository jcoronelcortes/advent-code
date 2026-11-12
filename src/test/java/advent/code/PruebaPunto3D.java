package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Punto3D")
class PruebaPunto3D {

    @Test
    @DisplayName("parsear: separado por comas, con texto y negativos")
    void parsear() {
        assertEquals(new Punto3D(162, 817, 812), Punto3D.parsear("162,817,812"));
        assertEquals(new Punto3D(1, -2, 3), Punto3D.parsear("x=1, y=-2, z=3"));
    }

    @Test
    @DisplayName("parsear: rechaza textos sin exactamente 3 números")
    void parsear_invalido() {
        assertThrows(IllegalArgumentException.class, () -> Punto3D.parsear("1,2"));
        assertThrows(IllegalArgumentException.class, () -> Punto3D.parsear("1,2,3,4"));
    }

    @Test
    @DisplayName("distanciaCuadrada y distanciaManhattan")
    void distancias() {
        Punto3D a = new Punto3D(1, 2, 3);
        Punto3D b = new Punto3D(4, -2, 3);
        assertEquals(25, a.distanciaCuadrada(b));    // 3² + 4² + 0²
        assertEquals(7, a.distanciaManhattan(b));
        assertEquals(0, a.distanciaCuadrada(a));
    }

    @Test
    @DisplayName("distanciaCuadrada no se desborda con coordenadas de 6 dígitos")
    void distanciaCuadrada_grande() {
        Punto3D origen = new Punto3D(0, 0, 0);
        Punto3D lejos = new Punto3D(999_999, 999_999, 999_999);
        assertEquals(3L * 999_999 * 999_999, origen.distanciaCuadrada(lejos));
    }

    @Test
    @DisplayName("toString usa el formato del input")
    void texto() {
        assertEquals("162,817,812", new Punto3D(162, 817, 812).toString());
    }
}
