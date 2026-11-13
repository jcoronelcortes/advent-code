package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Prueba de regresión: verifica que cada día siga dando las respuestas ya
 * aceptadas con el input real. Sirve como red de seguridad al refactorizar.
 *
 * <p>Los inputs no están en el repositorio, así que si el archivo de un día
 * no existe (por ejemplo, en CI) esa prueba se <em>omite</em> en vez de fallar.</p>
 *
 * <p>Al resolver un día nuevo, agregar aquí una fila con sus respuestas.</p>
 */
@DisplayName("Respuestas con el input real")
class RespuestasPrueba {

    @ParameterizedTest(name = "{0} día {1}: parte 1 = {2}, parte 2 = {3}")
    @DisplayName("cada día sigue dando la respuesta aceptada")
    @CsvSource({
            "2025, 1, 1043,        5963",
            "2025, 2, 38310256125, 58961152806",
            "2025, 3, 17412,       172681562473501",
            "2025, 4, 1551,        9784",
            "2025, 5, 635,         369761800782619",
            "2025, 6, 6171290547579, 8811937976367",
            "2025, 7, 1635,        58097428661390",
            "2025, 8, 171503,      9069509600",
            "2025, 9, 4777824480,  1542119040",
            "2025, 10, 491,        20617",
            "2025, 11, 749,        420257875695750",
            "2025, 12, 538,        estrella final",
            "2024, 1, 2378066,     18934359",
            "2024, 2, 306,         366",
            "2024, 3, 178538786,   102467299",
            "2024, 4, 2344,        1815",
            "2024, 5, 5991,        5479",
            "2024, 6, 5409,        2022",
            "2024, 7, 3351424677624, 204976636995111",
            "2024, 8, 329,         1190",
            "2024, 9, 6385338159127, 6415163624282",
            "2024, 10, 694,        1497",
            "2024, 11, 183248,     218811774248729",
            "2024, 12, 1488414,    911750",
            "2024, 13, 30973,      95688837203288",
            "2024, 14, 218433348,  6512",
            "2024, 15, 1457740,    1467145",
            "2024, 16, 160624,     692"
    })
    void respuestas(int anio, int dia, String parte1, String parte2) throws Exception {
        assumeTrue(EntradaSalida.existeEntrada(anio, dia),
                () -> "No está el input de " + anio + " día " + dia + "; se omite la prueba");

        Resolvedor resolvedor = Principal.cargarResolvedor(anio, dia);
        List<String> lineas = EntradaSalida.leerEntrada(anio, dia);

        assertEquals(parte1, resolvedor.parte1(lineas), "parte 1");
        assertEquals(parte2, resolvedor.parte2(lineas), "parte 2");
    }
}
