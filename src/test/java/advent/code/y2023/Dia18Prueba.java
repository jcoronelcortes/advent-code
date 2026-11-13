package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 18 — Año 2023.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2023 — Día 18: Laguna del Lavaducto")
class Dia18Prueba {

    private Dia18 dia18;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            "R 6 (#70c710)",
            "D 5 (#0dc571)",
            "L 2 (#5713f0)",
            "D 2 (#d2c081)",
            "R 2 (#59c680)",
            "D 2 (#411b91)",
            "L 5 (#8ceee2)",
            "U 2 (#caa173)",
            "L 1 (#1b58a2)",
            "U 2 (#caa171)",
            "R 2 (#7807d2)",
            "U 3 (#a77fa3)",
            "L 2 (#015232)",
            "U 2 (#7a21e3)"
    );

    @BeforeEach
    void configurar() {
        dia18 = new Dia18();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("62", dia18.parte1(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 1: lee la dirección y los metros de cada línea")
    void parte1_leerTramo() {
        assertEquals(new Dia18.Tramo(Direccion.DERECHA, 6), Dia18.leerTramo("R 6 (#70c710)"));
        assertEquals(new Dia18.Tramo(Direccion.ARRIBA, 12), Dia18.leerTramo("U 12 (#7a21e3)"));
        assertEquals(new Dia18.Tramo(Direccion.IZQUIERDA, 1), Dia18.leerTramo("L 1 (#1b58a2)"));
        assertEquals(new Dia18.Tramo(Direccion.ABAJO, 2), Dia18.leerTramo("D 2 (#d2c081)"));
        for (String invalida : List.of("X 6 (#70c710)", "R (#70c710)", "R 6", "R 6 (#70c71)")) {
            assertThrows(IllegalArgumentException.class, () -> Dia18.leerTramo(invalida), invalida);
        }
    }

    @Test
    @DisplayName("Parte 1: un cuadrado de lado 3 deja 4 × 4 casillas, y una zanja abierta falla")
    void parte1_casosChicos() {
        List<Dia18.Tramo> cuadrado = List.of(new Dia18.Tramo(Direccion.DERECHA, 3), new Dia18.Tramo(Direccion.ABAJO, 3),
                new Dia18.Tramo(Direccion.IZQUIERDA, 3), new Dia18.Tramo(Direccion.ARRIBA, 3));
        assertEquals(16, Dia18.volumen(cuadrado));
        // El mismo cuadrado recorrido al revés (sentido antihorario) da lo mismo
        assertEquals(16, Dia18.volumen(cuadrado.reversed().stream()
                .map(t -> new Dia18.Tramo(t.direccion().opuesto(), t.metros())).toList()));
        assertThrows(IllegalArgumentException.class, () -> Dia18.volumen(cuadrado.subList(0, 3)));
    }

    @Test
    @DisplayName("Parte 1: coincide con dibujar la zanja y rellenar desde afuera")
    void parte1_fuerzaBruta() {
        assertEquals(62, dibujarYRellenar(EJEMPLO.stream().map(Dia18::leerTramo).toList()));
        // Una forma en U, con una entrada que deja casillas de afuera rodeadas por tres lados
        List<String> forma = List.of("R 4 (#000000)", "D 4 (#000000)", "L 1 (#000000)", "U 3 (#000000)",
                "L 2 (#000000)", "D 3 (#000000)", "L 1 (#000000)", "U 4 (#000000)");
        List<Dia18.Tramo> tramos = forma.stream().map(Dia18::leerTramo).toList();
        assertEquals(dibujarYRellenar(tramos), Dia18.volumen(tramos));
    }

    /**
     * Fuerza bruta: dibuja la zanja en una grilla con un margen de una casilla,
     * rellena el exterior desde una esquina y cuenta lo que no quedó afuera.
     */
    private static long dibujarYRellenar(List<Dia18.Tramo> tramos) {
        List<Punto> zanja = new ArrayList<>();
        Punto actual = new Punto(0, 0);
        zanja.add(actual);
        for (Dia18.Tramo tramo : tramos) {
            for (int i = 0; i < tramo.metros(); i++) {
                actual = actual.mover(tramo.direccion());
                zanja.add(actual);
            }
        }
        int minX = zanja.stream().mapToInt(Punto::x).min().orElseThrow() - 1;
        int minY = zanja.stream().mapToInt(Punto::y).min().orElseThrow() - 1;
        int maxX = zanja.stream().mapToInt(Punto::x).max().orElseThrow() + 1;
        int maxY = zanja.stream().mapToInt(Punto::y).max().orElseThrow() + 1;
        Grilla terreno = new Grilla(maxY - minY + 1, maxX - minX + 1, '.');
        for (Punto p : zanja) terreno.establecer(new Punto(p.x() - minX, p.y() - minY), '#');
        int afuera = terreno.rellenar(new Punto(0, 0), c -> c == '.', 'o');
        return (long) terreno.filas() * terreno.columnas() - afuera;
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("952408144115", dia18.parte2(EJEMPLO));
    }

    @Test
    @DisplayName("Parte 2: los colores del ejemplo dan las instrucciones del enunciado")
    void parte2_leerTramoDelColor() {
        List<Dia18.Tramo> esperados = List.of(
                new Dia18.Tramo(Direccion.DERECHA, 461937), new Dia18.Tramo(Direccion.ABAJO, 56407),
                new Dia18.Tramo(Direccion.DERECHA, 356671), new Dia18.Tramo(Direccion.ABAJO, 863240),
                new Dia18.Tramo(Direccion.DERECHA, 367720), new Dia18.Tramo(Direccion.ABAJO, 266681),
                new Dia18.Tramo(Direccion.IZQUIERDA, 577262), new Dia18.Tramo(Direccion.ARRIBA, 829975),
                new Dia18.Tramo(Direccion.IZQUIERDA, 112010), new Dia18.Tramo(Direccion.ABAJO, 829975),
                new Dia18.Tramo(Direccion.IZQUIERDA, 491645), new Dia18.Tramo(Direccion.ARRIBA, 686074),
                new Dia18.Tramo(Direccion.IZQUIERDA, 5411), new Dia18.Tramo(Direccion.ARRIBA, 500254));
        assertEquals(esperados, Dia18.leerPlan(EJEMPLO, Dia18::leerTramoDelColor));
        assertThrows(IllegalArgumentException.class, () -> Dia18.leerTramoDelColor("R 6 (#70c714)"));
    }

    @Test
    @DisplayName("Parte 2: coincide con dibujar y rellenar un plan de colores con metros chicos")
    void parte2_fuerzaBruta() {
        // Los colores codifican la misma forma en U de la parte 1 (metros 4, 4, 1, 3, 2, 3, 1, 4)
        List<String> plan = List.of("R 1 (#000040)", "R 1 (#000041)", "R 1 (#000012)", "R 1 (#000033)",
                "R 1 (#000022)", "R 1 (#000031)", "R 1 (#000012)", "R 1 (#000043)");
        List<Dia18.Tramo> tramos = Dia18.leerPlan(plan, Dia18::leerTramoDelColor);
        assertEquals(dibujarYRellenar(tramos), Long.parseLong(dia18.parte2(plan)));
    }

    @Test
    @DisplayName("Parte 2: un cuadrado enorme da el resultado exacto, sin desbordar")
    void parte2_cuadradoEnorme() {
        long lado = 0xFFFFF;
        List<Dia18.Tramo> cuadrado = List.of(new Dia18.Tramo(Direccion.DERECHA, lado),
                new Dia18.Tramo(Direccion.ABAJO, lado), new Dia18.Tramo(Direccion.IZQUIERDA, lado),
                new Dia18.Tramo(Direccion.ARRIBA, lado));
        assertEquals((lado + 1) * (lado + 1), Dia18.volumen(cuadrado));
    }
}
