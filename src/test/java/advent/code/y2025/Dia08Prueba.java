package advent.code.y2025;

import advent.code.ConjuntosDisjuntos;
import advent.code.Punto3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 8 — Año 2025.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2025 — Día 8: Patio de Juegos")
class Dia08Prueba {

    private Dia08 dia08;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "162,817,812",
            "57,618,57",
            "906,360,560",
            "592,479,940",
            "352,342,300",
            "466,668,158",
            "542,29,236",
            "431,825,988",
            "739,650,466",
            "52,470,668",
            "216,146,977",
            "819,987,18",
            "117,168,530",
            "805,96,715",
            "346,949,466",
            "970,615,88",
            "941,993,340",
            "862,61,35",
            "984,92,344",
            "425,690,689"
    );

    @BeforeEach
    void configurar() {
        dia08 = new Dia08();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado (10 conexiones)")
    void parte1_ejemploDelEnunciado() {
        assertEquals(40, Dia08.productoMayores(EJEMPLO, 10));
    }

    @Test
    @DisplayName("Parte 1: los primeros pares más cercanos son los que indica el enunciado")
    void parte1_paresMasCercanos() {
        List<Punto3D> cajas = Dia08.leerCajas(EJEMPLO);
        List<Dia08.Par> pares = Dia08.paresOrdenados(cajas);
        assertEquals(List.of(
                        Set.of(Punto3D.parsear("162,817,812"), Punto3D.parsear("425,690,689")),
                        Set.of(Punto3D.parsear("162,817,812"), Punto3D.parsear("431,825,988")),
                        Set.of(Punto3D.parsear("906,360,560"), Punto3D.parsear("805,96,715")),
                        Set.of(Punto3D.parsear("431,825,988"), Punto3D.parsear("425,690,689"))),
                pares.subList(0, 4).stream()
                        .map(par -> Set.of(cajas.get(par.a()), cajas.get(par.b())))
                        .toList());
    }

    @Test
    @DisplayName("Parte 1: tras 10 conexiones quedan 11 circuitos de tamaños 5, 4, 2, 2 y siete de 1")
    void parte1_circuitosTrasDiezConexiones() {
        List<Punto3D> cajas = Dia08.leerCajas(EJEMPLO);
        ConjuntosDisjuntos circuitos = new ConjuntosDisjuntos(cajas.size());
        Dia08.paresOrdenados(cajas).subList(0, 10).forEach(par -> circuitos.unir(par.a(), par.b()));
        assertEquals(11, circuitos.cantidadGrupos());
        assertEquals(List.of(5, 4, 2, 2, 1, 1, 1, 1, 1, 1, 1), circuitos.tamanios());
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("25272", dia08.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: la última conexión es la que indica el enunciado")
    void parte2_ultimaConexion() {
        List<Punto3D> cajas = Dia08.leerCajas(EJEMPLO);
        Dia08.Par ultima = Dia08.ultimaConexion(cajas);
        assertEquals(Set.of(Punto3D.parsear("216,146,977"), Punto3D.parsear("117,168,530")),
                Set.of(cajas.get(ultima.a()), cajas.get(ultima.b())));
    }

    @Test
    @DisplayName("Parte 2: con 2 cajas la única conexión es la última; los pares ya conectados no cuentan")
    void parte2_casosBorde() {
        assertEquals("15", dia08.parte2(List.of("3,0,0", "5,0,0")));
        // Tres cajas en línea: se conectan 1-2 (dist 1) y 2-3 (dist 2); la 1-3 (dist 3) nunca hace falta
        assertEquals(String.valueOf(2 * 4), dia08.parte2(List.of("1,0,0", "2,0,0", "4,0,0")));
    }
}
