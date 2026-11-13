package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 22 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 22: Losas de Arena")
class Dia22Prueba {

    private Dia22 dia22;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "1,0,1~1,2,1",
            "0,0,2~2,0,2",
            "0,2,3~2,2,3",
            "0,0,4~0,2,4",
            "2,0,5~2,2,5",
            "0,1,6~2,1,6",
            "1,1,8~1,1,9"
    );

    /** Fuerza bruta: cuántos ladrillos de la pila asentada se mueven al sacar el ladrillo {@code i} y volver a asentar. */
    private static long caenAlSacar(Dia22.Pila pila, int i) {
        List<Dia22.Ladrillo> resto = new ArrayList<>(pila.ladrillos());
        resto.remove(i);
        List<Dia22.Ladrillo> otraVez = Dia22.asentar(resto).ladrillos();
        // asentar los devuelve ordenados (de forma estable) por su base; se sigue a cada ladrillo por su
        // identidad, no por su posición: uno que cae justo al lugar de otro idéntico también se movió
        List<Integer> orden = new ArrayList<>();
        for (int j = 0; j < resto.size(); j++) orden.add(j);
        orden.sort(Comparator.comparingInt(j -> resto.get(j).z1()));
        long caen = 0;
        for (int k = 0; k < orden.size(); k++) if (!resto.get(orden.get(k)).equals(otraVez.get(k))) caen++;
        return caen;
    }

    @BeforeEach
    void configurar() {
        dia22 = new Dia22();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("5", dia22.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: los ladrillos del ejemplo quedan a las alturas del dibujo y se apoyan como dice el enunciado")
    void parte1_apoyosDelEjemplo() {
        // En el ejemplo el orden por altura coincide con el del input: A = 0, B = 1, …, G = 6
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(EJEMPLO));
        List<Integer> bases = pila.ladrillos().stream().map(Dia22.Ladrillo::z1).toList();
        assertEquals(List.of(1, 2, 2, 3, 3, 4, 5), bases);
        assertEquals(List.of(Set.of(), Set.of(0), Set.of(0), Set.of(1, 2), Set.of(1, 2), Set.of(3, 4), Set.of(5)),
                pila.apoyos());
        assertEquals(List.of(Set.of(1, 2), Set.of(3, 4), Set.of(3, 4), Set.of(5), Set.of(5), Set.of(6), Set.of()),
                pila.sostiene());
        List<Boolean> seguros = new ArrayList<>();
        for (int i = 0; i < 7; i++) seguros.add(Dia22.sePuedeDesintegrar(pila, i));
        assertEquals(List.of(false, true, true, true, true, false, true), seguros);
    }

    @Test
    @DisplayName("Parte 1: coincide con sacar cada ladrillo, volver a asentar y ver si se mueve alguno")
    void parte1_fuerzaBruta() {
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(EJEMPLO));
        for (int i = 0; i < pila.ladrillos().size(); i++) {
            assertEquals(caenAlSacar(pila, i) == 0, Dia22.sePuedeDesintegrar(pila, i), "ladrillo " + i);
        }
        // Una pila ya asentada no se mueve al volver a asentarla
        assertEquals(Set.copyOf(pila.ladrillos()), Set.copyOf(Dia22.asentar(pila.ladrillos()).ladrillos()));
    }

    @Test
    @DisplayName("Parte 1: un ladrillo apoyado en dos alturas distintas solo se apoya en el más alto")
    void parte1_apoyoSoloEnElTope() {
        // A en z=1 bajo x=0; B vertical de z=1 a z=2 bajo x=2; C horizontal arriba toca solo a B
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(List.of("0,0,1~0,0,1", "2,0,1~2,0,2", "0,0,5~2,0,5")));
        assertEquals(Set.of(1), pila.apoyos().get(2));
        assertEquals(3, pila.ladrillos().get(2).z1());
        // Los extremos pueden venir al revés
        assertEquals(Dia22.leer(List.of("1,2,3~4,2,3")), Dia22.leer(List.of("4,2,3~1,2,3")));
    }

    @Test
    @DisplayName("Parte 1: un input mal formado o con ladrillos superpuestos falla con un mensaje claro")
    void parte1_inputInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Dia22.leer(List.of("1,0,1~1,2")));
        assertThrows(IllegalArgumentException.class, () -> Dia22.leer(List.of("1,0,0~1,2,0")));
        assertThrows(IllegalArgumentException.class, () -> Dia22.leer(List.of("1,0,-1~1,2,1")));
        assertThrows(IllegalArgumentException.class,
                () -> Dia22.asentar(Dia22.leer(List.of("0,0,1~0,0,3", "0,0,2~1,0,2"))));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("7", dia22.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: sacar A hace caer los otros 6, sacar F hace caer a G y los demás no hacen caer nada")
    void parte2_cadaLadrillo() {
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(EJEMPLO));
        List<Integer> caen = new ArrayList<>();
        for (int i = 0; i < 7; i++) caen.add(Dia22.caenAlDesintegrar(pila, i));
        assertEquals(List.of(6, 0, 0, 0, 0, 1, 0), caen);
    }

    @Test
    @DisplayName("Parte 2: coincide con sacar cada ladrillo, volver a asentar y contar cuántos se mueven")
    void parte2_fuerzaBruta() {
        // El ejemplo y una pila más enredada, con ladrillos apoyados en dos y ramas que se juntan
        List<String> enredada = List.of(
                "0,0,1~2,0,1", "0,0,2~0,0,2", "2,0,2~2,0,2", "0,0,3~2,0,3", "1,0,4~1,0,6",
                "0,1,1~0,1,4", "0,0,7~0,1,7", "1,0,8~1,1,8", "3,0,1~3,0,9", "1,0,10~3,0,10");
        for (List<String> lineas : List.of(EJEMPLO, enredada)) {
            Dia22.Pila pila = Dia22.asentar(Dia22.leer(lineas));
            for (int i = 0; i < pila.ladrillos().size(); i++) {
                assertEquals(caenAlSacar(pila, i), Dia22.caenAlDesintegrar(pila, i), "ladrillo " + i);
            }
        }
    }

    @Test
    @DisplayName("Parte 2: en una columna de cubos iguales, sacar el de abajo hace caer a todos los de arriba")
    void parte2_columnaDeCubosIguales() {
        // Cada cubo cae justo al lugar del de abajo: contar por posición diría que no se movió ninguno
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(List.of("0,0,1~0,0,1", "0,0,2~0,0,2", "0,0,3~0,0,3")));
        assertEquals(2, Dia22.caenAlDesintegrar(pila, 0));
        assertEquals(2, caenAlSacar(pila, 0));
        assertEquals(1, caenAlSacar(pila, 1));
    }

    @Test
    @DisplayName("Parte 2: un ladrillo con un apoyo que no cae se queda, aunque su otro apoyo caiga")
    void parte2_apoyoQueResiste() {
        // A y B (vertical, de 2 de alto) en el suelo; C sobre A, a la misma altura que el tope de B;
        // D se apoya en C y en B. Sacar A hace caer a C, pero D sigue apoyado en B
        Dia22.Pila pila = Dia22.asentar(Dia22.leer(List.of(
                "0,0,1~0,0,1", "1,0,1~1,0,2", "0,0,2~0,0,2", "0,0,3~1,0,3")));
        assertEquals(Set.of(1, 2), pila.apoyos().get(3));
        assertEquals(1, Dia22.caenAlDesintegrar(pila, 0));
        assertEquals(caenAlSacar(pila, 0), Dia22.caenAlDesintegrar(pila, 0));
    }
}
