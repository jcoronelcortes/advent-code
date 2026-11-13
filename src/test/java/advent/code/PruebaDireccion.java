package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static advent.code.Direccion.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Direccion")
class PruebaDireccion {

    @Test
    @DisplayName("giroIzquierda da vuelta en sentido antihorario")
    void giroIzquierda() {
        assertEquals(IZQUIERDA, ARRIBA.giroIzquierda());
        assertEquals(ABAJO,     IZQUIERDA.giroIzquierda());
        assertEquals(DERECHA,   ABAJO.giroIzquierda());
        assertEquals(ARRIBA,    DERECHA.giroIzquierda());
    }

    @Test
    @DisplayName("giroDerecha da vuelta en sentido horario")
    void giroDerecha() {
        assertEquals(DERECHA,   ARRIBA.giroDerecha());
        assertEquals(ABAJO,     DERECHA.giroDerecha());
        assertEquals(IZQUIERDA, ABAJO.giroDerecha());
        assertEquals(ARRIBA,    IZQUIERDA.giroDerecha());
    }

    @Test
    @DisplayName("opuesto retorna la dirección contraria")
    void opuesto() {
        assertEquals(ABAJO,     ARRIBA.opuesto());
        assertEquals(ARRIBA,    ABAJO.opuesto());
        assertEquals(DERECHA,   IZQUIERDA.opuesto());
        assertEquals(IZQUIERDA, DERECHA.opuesto());
    }

    @Test
    @DisplayName("giroIzquierda y giroDerecha son inversos")
    void giroDobleCancela() {
        for (Direccion d : Direccion.values()) {
            assertEquals(d, d.giroDerecha().giroIzquierda());
            assertEquals(d, d.giroIzquierda().giroDerecha());
        }
    }

    @Test
    @DisplayName("comoPunto retorna el delta correcto")
    void comoPunto() {
        assertEquals(new Punto( 0, -1), ARRIBA.comoPunto());
        assertEquals(new Punto( 0,  1), ABAJO.comoPunto());
        assertEquals(new Punto(-1,  0), IZQUIERDA.comoPunto());
        assertEquals(new Punto( 1,  0), DERECHA.comoPunto());
    }

    @Test
    @DisplayName("desdeFlecha: ^ v < > y un carácter inválido")
    void desdeFlecha() {
        assertEquals(ARRIBA, Direccion.desdeFlecha('^'));
        assertEquals(ABAJO, Direccion.desdeFlecha('v'));
        assertEquals(IZQUIERDA, Direccion.desdeFlecha('<'));
        assertEquals(DERECHA, Direccion.desdeFlecha('>'));
        assertThrows(IllegalArgumentException.class, () -> Direccion.desdeFlecha('x'));
    }
}
