package advent.code.y2025;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Año 2025 — Día 2")
class Dia02Prueba {

    private Dia02 dia02;

    @BeforeEach
    void configurar() {
        dia02 = new Dia02();
    }

    @Test
    @DisplayName("Parte 1: ejemplo del enunciado")
    void parte1_ejemplo() {
        List<String> ejemplo = List.of(
            // TODO: agregar líneas del ejemplo
        );
        assertEquals("TODO", dia02.parte1(ejemplo));
    }

    @Test
    @DisplayName("Parte 2: ejemplo del enunciado")
    void parte2_ejemplo() {
        List<String> ejemplo = List.of(
            // TODO: agregar líneas del ejemplo
        );
        assertEquals("TODO", dia02.parte2(ejemplo));
    }
}
