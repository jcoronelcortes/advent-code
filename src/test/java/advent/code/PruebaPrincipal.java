package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Principal: tabla anual")
class PruebaPrincipal {

    private static final List<String> ENCABEZADOS = List.of("Día", "Parte 1", "ms", "Parte 2", "ms");

    @Test
    @DisplayName("cada columna toma el ancho de su texto más largo y los tiempos se alinean a la derecha")
    void anchos() {
        List<String> lineas = Principal.tabla(ENCABEZADOS, List.of(
                List.of("01", "123", "4,2", "7", "10,5"),
                List.of("23", "1253", "11,1", "ag,bt,cq,da,hp", "61,2")));
        assertEquals(List.of(
                "Día  Parte 1    ms  Parte 2           ms",
                "01   123       4,2  7               10,5",
                "23   1253     11,1  ag,bt,cq,da,hp  61,2"), lineas);
    }

    @Test
    @DisplayName("una respuesta larga no desalinea las columnas siguientes")
    void respuestaLargaNoDesalinea() {
        List<String> lineas = Principal.tabla(ENCABEZADOS, List.of(
                List.of("23", "1253", "11,1", "ag,bt,cq,da,hp,hs,mi,pa,qd,qe,qi,ri,uq", "61,2"),
                List.of("24", "49520947122770", "2,1", "gjc,gvm,qjj,qsb,wmp,z17,z26,z39", "182,0"),
                List.of("25", "3320", "6,7", "estrella final", "0,0")));
        int largo = lineas.get(1).length();
        assertTrue(lineas.stream().skip(1).allMatch(linea -> linea.length() == largo), "todas las filas del mismo largo");
    }

    @Test
    @DisplayName("sin filas, solo queda el encabezado")
    void sinFilas() {
        assertEquals(List.of("Día  Parte 1  ms  Parte 2  ms"), Principal.tabla(ENCABEZADOS, List.of()));
    }
}
