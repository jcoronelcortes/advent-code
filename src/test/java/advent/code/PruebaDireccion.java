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
    @DisplayName("toPunto retorna el delta correcto")
    void toPunto() {
        assertEquals(new Punto( 0, -1), ARRIBA.toPunto());
        assertEquals(new Punto( 0,  1), ABAJO.toPunto());
        assertEquals(new Punto(-1,  0), IZQUIERDA.toPunto());
        assertEquals(new Punto( 1,  0), DERECHA.toPunto());
    }
}