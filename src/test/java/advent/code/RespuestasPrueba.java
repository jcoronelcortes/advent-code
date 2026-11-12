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
            "2025, 5, 635,         369761800782619"
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
