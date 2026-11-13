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
            "2024, 16, 160624,     692",
            "2024, 17, '7,4,2,5,1,4,6,0,4', 164278764924605",
            "2024, 18, 234,         '58,19'",
            "2024, 19, 206,         622121814629343",
            "2024, 20, 1360,        1005476",
            "2024, 21, 163086,      198466286401228",
            "2024, 22, 17163502021, 1938",
            "2024, 23, 1253,        'ag,bt,cq,da,hp,hs,mi,pa,qd,qe,qi,ri,uq'",
            "2024, 24, 49520947122770, 'gjc,gvm,qjj,qsb,wmp,z17,z26,z39'",
            "2024, 25, 3320,        estrella final",
            "2023, 1, 54667,        54203",
            "2023, 2, 2285,         77021",
            "2023, 3, 536202,       78272573",
            "2023, 4, 27059,        5744979",
            "2023, 5, 484023871,    46294175",
            "2023, 6, 4403592,      38017587",
            "2023, 7, 246163188,    245794069",
            "2023, 8, 13019,        13524038372771",
            "2023, 9, 2043183816,   1118",
            "2023, 10, 6690,        525",
            "2023, 11, 9627977,     644248339497",
            "2023, 12, 7204,        1672318386674",
            "2023, 13, 30802,       37876",
            "2023, 14, 107053,      88371",
            "2023, 15, 516657,      210906",
            "2023, 16, 8125,        8489",
            "2023, 17, 797,         914"
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
