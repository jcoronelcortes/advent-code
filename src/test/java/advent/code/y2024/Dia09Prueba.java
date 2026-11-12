package advent.code.y2024;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 9 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 9: Desfragmentador de Disco")
class Dia09Prueba {

    private Dia09 dia09;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "2333133121414131402"
    );

    @BeforeEach
    void configurar() {
        dia09 = new Dia09();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("1928", dia09.parte1(EJEMPLO));
    }

    /** Muestra los bloques como en el enunciado: el ID de cada archivo, o '.' si está libre. */
    private static String dibujar(int[] bloques) {
        StringBuilder texto = new StringBuilder();
        for (int bloque : bloques) texto.append(bloque == Dia09.LIBRE ? "." : String.valueOf(bloque));
        return texto.toString();
    }

    @Test
    @DisplayName("Parte 1: expandir el mapa denso a bloques, como los dibujos del enunciado")
    void parte1_expandir() {
        assertEquals("0..111....22222", dibujar(Dia09.expandir("12345")));
        assertEquals("00...111...2...333.44.5555.6666.777.888899", dibujar(Dia09.expandir("2333133121414131402")));
        assertEquals("000000000111111111222222222", dibujar(Dia09.expandir("90909")));   // sin espacio libre
    }

    @Test
    @DisplayName("Parte 1: compactar deja el disco como el último paso del enunciado")
    void parte1_compactar() {
        int[] chico = Dia09.expandir("12345");
        Dia09.compactarPorBloques(chico);
        assertEquals("022111222......", dibujar(chico));

        int[] ejemplo = Dia09.expandir("2333133121414131402");
        Dia09.compactarPorBloques(ejemplo);
        assertEquals("0099811188827773336446555566..............", dibujar(ejemplo));
    }

    @Test
    @DisplayName("Parte 1: checksum saltea los bloques libres; el mapa debe tener solo dígitos")
    void parte1_checksumYValidacion() {
        assertEquals(0 * 0 + 1 * 2 + 2 * 2 + 3 * 1, Dia09.checksum(new int[]{0, 2, 2, 1, Dia09.LIBRE}));
        assertThrows(IllegalArgumentException.class, () -> Dia09.mapa(List.of("123a5")));
        assertEquals("12345", Dia09.mapa(List.of(" 12345 ", "")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("2858", dia09.parte2(EJEMPLO));
    }

    /** Dibuja el disco por tramos, igual que {@link #dibujar(int[])}. */
    private static String dibujar(Dia09.Disco disco, int largoTotal) {
        int[] bloques = new int[largoTotal];
        java.util.Arrays.fill(bloques, Dia09.LIBRE);
        for (int id = 0; id < disco.inicioArchivo().length; id++) {
            for (int k = 0; k < disco.largoArchivo()[id]; k++) bloques[disco.inicioArchivo()[id] + k] = id;
        }
        return dibujar(bloques);
    }

    @Test
    @DisplayName("Parte 2: compactar por archivos deja el disco como el último paso del enunciado")
    void parte2_compactar() {
        Dia09.Disco disco = Dia09.Disco.desdeMapa("2333133121414131402");
        assertEquals("00...111...2...333.44.5555.6666.777.888899", dibujar(disco, 42));
        disco.compactarPorArchivos();
        assertEquals("00992111777.44.333....5555.6666.....8888..", dibujar(disco, 42));
    }

    @Test
    @DisplayName("Parte 2: un archivo que no entra en ningún hueco de su izquierda no se mueve")
    void parte2_noEntra() {
        // 0..111: el archivo 1 (3 bloques) no entra en el hueco de 2
        Dia09.Disco disco = Dia09.Disco.desdeMapa("123");
        disco.compactarPorArchivos();
        assertEquals("0..111", dibujar(disco, 6));
        // Un archivo nunca se mueve hacia la derecha, aunque haya lugar
        Dia09.Disco sinHuecoIzquierdo = Dia09.Disco.desdeMapa("1013");   // 0 seguido de 1 y 3 libres
        sinHuecoIzquierdo.compactarPorArchivos();
        assertEquals("01...", dibujar(sinHuecoIzquierdo, 5));
    }

    @Test
    @DisplayName("Parte 2: cada archivo se intenta mover una sola vez, del ID más alto al más bajo")
    void parte2_unaSolaVez() {
        // 0.1.22 → el 2 (largo 2) no entra en ningún hueco de 1; después el 1 se mueve al primer hueco
        // y deja libre un tramo de 2 antes del 2, pero el 2 ya tuvo su turno y no se reintenta
        Dia09.Disco disco = Dia09.Disco.desdeMapa("11112");
        assertEquals("0.1.22", dibujar(disco, 6));
        disco.compactarPorArchivos();
        assertEquals("01..22", dibujar(disco, 6));
    }
}
