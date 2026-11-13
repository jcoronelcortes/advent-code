package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 15 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 15: Biblioteca de Lentes")
class Dia15Prueba {

    private Dia15 dia15;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"
    );

    @BeforeEach
    void configurar() {
        dia15 = new Dia15();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("1320", dia15.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: el HASH de \"HASH\" es 52, y el de cada paso del ejemplo da lo del enunciado")
    void parte1_hash() {
        assertEquals(52, Dia15.hash("HASH"));
        assertEquals(0, Dia15.hash(""));
        List<Integer> esperados = List.of(30, 253, 97, 47, 14, 180, 9, 197, 48, 214, 231);
        assertEquals(esperados, Dia15.pasos(EJEMPLO).stream().map(Dia15::hash).toList());
    }

    @Test
    @DisplayName("Parte 1: los saltos de línea se ignoran al separar los pasos")
    void parte1_saltosDeLinea() {
        assertEquals(List.of("rn=1", "cm-", "qp=3"), Dia15.pasos(List.of("rn=1,c", "m-,qp", "=3")));
        assertEquals("1320", dia15.parte1(List.of("rn=1,cm-,qp=3,cm=2,qp-,pc", "=4,ot=9,ab=5,pc-,pc=6,ot=7", "")));
    }

    @Test
    @DisplayName("Parte 1: una secuencia vacía o con un paso vacío falla con un mensaje claro")
    void parte1_secuenciaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> Dia15.pasos(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Dia15.pasos(List.of("rn=1,,cm-")));
        assertThrows(IllegalArgumentException.class, () -> Dia15.pasos(List.of("rn=1,")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("145", dia15.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: al final del ejemplo solo las cajas 0 y 3 tienen lentes, en el orden del enunciado")
    void parte2_cajasDelEjemplo() {
        List<SequencedMap<String, Integer>> cajas = Dia15.colocarLentes(Dia15.pasos(EJEMPLO));
        assertEquals(256, cajas.size());
        assertEquals(List.of("rn", "cm"), List.copyOf(cajas.get(0).sequencedKeySet()));
        assertEquals(List.of(1, 2), List.copyOf(cajas.get(0).sequencedValues()));
        assertEquals(List.of("ot", "ab", "pc"), List.copyOf(cajas.get(3).sequencedKeySet()));
        assertEquals(List.of(7, 5, 6), List.copyOf(cajas.get(3).sequencedValues()));
        for (int caja = 0; caja < 256; caja++) {
            if (caja != 0 && caja != 3) assertTrue(cajas.get(caja).isEmpty(), "caja " + caja);
        }
    }

    @Test
    @DisplayName("Parte 2: reemplazar no mueve la lente; sacarla y volver a ponerla la manda al fondo")
    void parte2_reemplazarYSacar() {
        // "rn", "cm" caen en la caja 0
        List<SequencedMap<String, Integer>> reemplazo = Dia15.colocarLentes(List.of("rn=1", "cm=2", "rn=5"));
        assertEquals(List.of("rn", "cm"), List.copyOf(reemplazo.get(0).sequencedKeySet()));
        assertEquals(1 * 1 * 5 + 1 * 2 * 2, Dia15.poderDeEnfoque(reemplazo));

        List<SequencedMap<String, Integer>> sacar = Dia15.colocarLentes(List.of("rn=1", "cm=2", "rn-", "rn=5"));
        assertEquals(List.of("cm", "rn"), List.copyOf(sacar.get(0).sequencedKeySet()));

        // Sacar una etiqueta que no está no hace nada
        assertEquals(Dia15.colocarLentes(List.of("rn=1")), Dia15.colocarLentes(List.of("cm-", "rn=1", "qp-")));
    }

    @Test
    @DisplayName("Parte 2: coincide con simular las cajas con listas de pares")
    void parte2_fuerzaBruta() {
        List<List<String[]>> cajas = new ArrayList<>();
        for (int i = 0; i < 256; i++) cajas.add(new ArrayList<>());
        for (String paso : Dia15.pasos(EJEMPLO)) {
            boolean sacar = paso.endsWith("-");
            String etiqueta = paso.substring(0, sacar ? paso.length() - 1 : paso.indexOf('='));
            List<String[]> caja = cajas.get(Dia15.hash(etiqueta));
            int indice = -1;
            for (int i = 0; i < caja.size(); i++) if (caja.get(i)[0].equals(etiqueta)) indice = i;
            if (sacar) {
                if (indice >= 0) caja.remove(indice);
            } else {
                String[] lente = {etiqueta, paso.substring(paso.indexOf('=') + 1)};
                if (indice >= 0) caja.set(indice, lente); else caja.add(lente);
            }
        }
        long total = 0;
        for (int c = 0; c < 256; c++) {
            for (int i = 0; i < cajas.get(c).size(); i++) {
                total += (c + 1L) * (i + 1) * Integer.parseInt(cajas.get(c).get(i)[1]);
            }
        }
        assertEquals(String.valueOf(total), dia15.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: un paso con formato inválido falla con un mensaje claro")
    void parte2_pasoInvalido() {
        for (String paso : List.of("rn", "rn=", "rn=0", "rn=12", "=1", "-", "rn-1", "RN=1")) {
            assertThrows(IllegalArgumentException.class, () -> Dia15.colocarLentes(List.of(paso)), paso);
        }
    }
}
