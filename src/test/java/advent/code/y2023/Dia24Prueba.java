package advent.code.y2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 24 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 24: Nunca Me Digas las Probabilidades")
class Dia24Prueba {

    private Dia24 dia24;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "19, 13, 30 @ -2,  1, -2",
            "18, 19, 22 @ -1, -1, -2",
            "20, 25, 34 @ -2, -2, -4",
            "12, 31, 28 @ -1, -2, -1",
            "20, 19, 15 @  1, -5, -3"
    );

    @BeforeEach
    void configurar() {
        dia24 = new Dia24();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado, con el área de 7 a 27")
    void parte1_ejemploDelEnunciado() {
        assertEquals(2, Dia24.crucesEnArea(Dia24.leer(EJEMPLO), 7, 27));
    }

    @Test
    @DisplayName("Parte 1: solo los pares A-B y A-C del ejemplo se cruzan dentro del área, a futuro")
    void parte1_cadaPar() {
        List<Dia24.Granizo> g = Dia24.leer(EJEMPLO);
        assertEquals(new Dia24.Granizo(20, 19, 15, 1, -5, -3), g.get(4));
        List<String> dentro = new ArrayList<>();
        for (int i = 0; i < g.size(); i++) {
            for (int j = i + 1; j < g.size(); j++) {
                if (Dia24.seCruzanEnArea(g.get(i), g.get(j), 7, 27)) dentro.add(i + "-" + j);
            }
        }
        assertEquals(List.of("0-1", "0-2"), dentro);
        // Con un área enorme entran también los que se cruzan afuera (0-3, 1-3, 2-3), pero no los del pasado
        // (0-4, 1-4, 2-4, 3-4) ni los paralelos (1-2)
        assertEquals(5, Dia24.crucesEnArea(g, -1_000, 1_000));
    }

    @Test
    @DisplayName("Parte 1: un cruce justo en el borde del área cuenta, y uno apenas afuera no")
    void parte1_bordes() {
        // Se cruzan en (10, 10) en t = s = 10
        Dia24.Granizo a = new Dia24.Granizo(0, 0, 0, 1, 1, 0);
        Dia24.Granizo b = new Dia24.Granizo(20, 0, 0, -1, 1, 0);
        assertTrue(Dia24.seCruzanEnArea(a, b, 10, 20));
        assertTrue(Dia24.seCruzanEnArea(a, b, 0, 10));
        assertFalse(Dia24.seCruzanEnArea(a, b, 11, 20));
        // Con números del tamaño del input y un cruce en un instante fraccionario (t = s = 1/3), en el borde
        long base = 300_000_000_000_000L;
        Dia24.Granizo c = new Dia24.Granizo(base, base, 0, 3, 0, 0);
        Dia24.Granizo d = new Dia24.Granizo(base + 1, base - 1, 0, 0, 3, 0);
        // c llega a x = base + 1 en t = 1/3; d llega a y = base en s = 1/3: se cruzan en (base + 1, base)
        assertTrue(Dia24.seCruzanEnArea(c, d, base, base + 1));
        assertFalse(Dia24.seCruzanEnArea(c, d, base + 1, base + 2));
    }

    @Test
    @DisplayName("Parte 1: trayectorias sobre la misma recta fallan; una línea mal formada también")
    void parte1_casosInvalidos() {
        Dia24.Granizo a = new Dia24.Granizo(0, 0, 0, 1, 1, 0);
        Dia24.Granizo b = new Dia24.Granizo(5, 5, 0, 2, 2, 0);
        assertThrows(IllegalStateException.class, () -> Dia24.seCruzanEnArea(a, b, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> Dia24.leer(List.of("19, 13, 30, -2, 1, -2")));
        assertThrows(IllegalArgumentException.class, () -> Dia24.leer(List.of("19, 13 @ -2, 1, -2")));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("47", dia24.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: la piedra del ejemplo es 24, 13, 10 @ -3, 1, 2 y choca con cada granizo en el instante del enunciado")
    void parte2_piedraDelEjemplo() {
        List<Dia24.Granizo> granizos = Dia24.leer(EJEMPLO);
        Dia24.Granizo piedra = Dia24.lanzamiento(granizos);
        assertEquals(new Dia24.Granizo(24, 13, 10, -3, 1, 2), piedra);
        // Instantes de choque del enunciado: 5, 3, 4, 6 y 1; en ese instante coinciden en los tres ejes
        long[] instantes = {5, 3, 4, 6, 1};
        for (int i = 0; i < granizos.size(); i++) {
            Dia24.Granizo g = granizos.get(i);
            long t = instantes[i];
            assertEquals(piedra.px() + t * piedra.vx(), g.px() + t * g.vx());
            assertEquals(piedra.py() + t * piedra.vy(), g.py() + t * g.vy());
            assertEquals(piedra.pz() + t * piedra.vz(), g.pz() + t * g.vz());
            assertTrue(Dia24.choca(piedra, g));
        }
    }

    @Test
    @DisplayName("Parte 2: choca exige el mismo instante en los tres ejes, y no negativo")
    void parte2_choca() {
        Dia24.Granizo piedra = new Dia24.Granizo(0, 0, 0, 1, 1, 1);
        assertTrue(Dia24.choca(piedra, new Dia24.Granizo(4, 4, 4, -1, -1, -1)));   // t = 2
        assertFalse(Dia24.choca(piedra, new Dia24.Granizo(4, 4, 5, -1, -1, -1)));  // en z no coincide
        assertFalse(Dia24.choca(piedra, new Dia24.Granizo(-4, -4, -4, -1, -1, -1))); // t = −2: en el pasado
        assertTrue(Dia24.choca(piedra, new Dia24.Granizo(0, 3, 6, 1, 0, -1)));     // t = 3, con dv = 0 en x
        assertFalse(Dia24.choca(piedra, new Dia24.Granizo(1, 3, 6, 1, 0, -1)));    // dv = 0 en x pero distinto lugar
    }

    @Test
    @DisplayName("Parte 2: coincide con buscar la velocidad por fuerza bruta en un rango chico y probar todos los granizos")
    void parte2_fuerzaBruta() {
        List<Dia24.Granizo> granizos = Dia24.leer(EJEMPLO);
        List<Dia24.Granizo> encontradas = new ArrayList<>();
        // Si la piedra tiene velocidad V, choca con el granizo 0 en algún instante t0 y con el 1 en t1:
        // se prueban instantes chicos y se deduce la piedra de esos dos choques
        for (long t0 = 0; t0 <= 10; t0++) {
            for (long t1 = 0; t1 <= 10; t1++) {
                if (t0 == t1) continue;
                Dia24.Granizo a = granizos.get(0), b = granizos.get(1);
                long ax = a.px() + t0 * a.vx(), ay = a.py() + t0 * a.vy(), az = a.pz() + t0 * a.vz();
                long bx = b.px() + t1 * b.vx(), by = b.py() + t1 * b.vy(), bz = b.pz() + t1 * b.vz();
                long dt = t1 - t0;
                if ((bx - ax) % dt != 0 || (by - ay) % dt != 0 || (bz - az) % dt != 0) continue;
                long vx = (bx - ax) / dt, vy = (by - ay) / dt, vz = (bz - az) / dt;
                Dia24.Granizo piedra = new Dia24.Granizo(ax - t0 * vx, ay - t0 * vy, az - t0 * vz, vx, vy, vz);
                if (granizos.stream().allMatch(g -> Dia24.choca(piedra, g))) encontradas.add(piedra);
            }
        }
        assertEquals(List.of(Dia24.lanzamiento(granizos)), encontradas);
    }

    @Test
    @DisplayName("Parte 2: si ninguna piedra choca con todos, falla con un mensaje claro")
    void parte2_sinSolucion() {
        List<String> conUnoDeMas = new ArrayList<>(EJEMPLO);
        conUnoDeMas.add("100, 100, 100 @ 1, 1, 1");
        assertThrows(IllegalStateException.class, () -> Dia24.lanzamiento(Dia24.leer(conUnoDeMas)));
    }
}
