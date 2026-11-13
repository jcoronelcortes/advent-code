package advent.code;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Poligono")
class PruebaPoligono {

    /** Un rectángulo de {@code ancho × alto} con una esquina en {@code (x, y)}, en sentido horario. */
    private static List<Punto> rectangulo(int x, int y, int ancho, int alto) {
        return List.of(new Punto(x, y), new Punto(x + ancho, y), new Punto(x + ancho, y + alto), new Punto(x, y + alto));
    }

    /**
     * Fuerza bruta: cuenta los puntos enteros de la caja que contiene al
     * triángulo según de qué lado de cada lado quedan (con productos cruzados).
     *
     * @return {interiores, del borde}
     */
    private static long[] contarEnTriangulo(Punto a, Punto b, Punto c) {
        int minX = Math.min(a.x(), Math.min(b.x(), c.x())), maxX = Math.max(a.x(), Math.max(b.x(), c.x()));
        int minY = Math.min(a.y(), Math.min(b.y(), c.y())), maxY = Math.max(a.y(), Math.max(b.y(), c.y()));
        long interiores = 0, borde = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Punto p = new Punto(x, y);
                long d1 = cruz(a, b, p), d2 = cruz(b, c, p), d3 = cruz(c, a, p);
                boolean negativo = d1 < 0 || d2 < 0 || d3 < 0, positivo = d1 > 0 || d2 > 0 || d3 > 0;
                if (negativo && positivo) continue;   // afuera
                if (d1 == 0 || d2 == 0 || d3 == 0) borde++;
                else interiores++;
            }
        }
        return new long[]{interiores, borde};
    }

    private static long cruz(Punto o, Punto a, Punto b) {
        return (long) (a.x() - o.x()) * (b.y() - o.y()) - (long) (a.y() - o.y()) * (b.x() - o.x());
    }

    @Test
    @DisplayName("un rectángulo de 3 × 2: área 6, 10 puntos en el borde, 2 adentro y 12 en total")
    void rectangulo_chico() {
        List<Punto> r = rectangulo(1, 1, 3, 2);
        assertEquals(12, Poligono.dobleArea(r));
        assertEquals(10, Poligono.puntosDelBorde(r));
        assertEquals(2, Poligono.puntosInteriores(r));
        assertEquals(12, Poligono.puntosCubiertos(r));
    }

    @Test
    @DisplayName("da lo mismo recorrer el polígono en un sentido o en el otro, y con puntos intermedios en los lados")
    void sentidoYPuntosIntermedios() {
        List<Punto> r = rectangulo(0, 0, 4, 3);
        List<Punto> alReves = new ArrayList<>(r);
        Collections.reverse(alReves);
        assertEquals(Poligono.dobleArea(r), Poligono.dobleArea(alReves));
        assertEquals(Poligono.puntosInteriores(r), Poligono.puntosInteriores(alReves));
        // El mismo rectángulo con todos los puntos del borde como vértices (como un lazo de casillas)
        List<Punto> casillas = new ArrayList<>();
        for (int x = 0; x < 4; x++) casillas.add(new Punto(x, 0));
        for (int y = 0; y < 3; y++) casillas.add(new Punto(4, y));
        for (int x = 4; x > 0; x--) casillas.add(new Punto(x, 3));
        for (int y = 3; y > 0; y--) casillas.add(new Punto(0, y));
        assertEquals(Poligono.dobleArea(r), Poligono.dobleArea(casillas));
        assertEquals(Poligono.puntosDelBorde(r), Poligono.puntosDelBorde(casillas));
        assertEquals(casillas.size(), Poligono.puntosDelBorde(casillas));
    }

    @Test
    @DisplayName("un polígono no convexo en forma de L")
    void formaDeL() {
        // L de 3 × 3 sin el cuadrado de 2 × 2 de arriba a la derecha: área 5
        List<Punto> l = List.of(new Punto(0, 0), new Punto(1, 0), new Punto(1, 2), new Punto(3, 2),
                new Punto(3, 3), new Punto(0, 3));
        assertEquals(10, Poligono.dobleArea(l));
        assertEquals(12, Poligono.puntosDelBorde(l));
        assertEquals(0, Poligono.puntosInteriores(l));
        assertEquals(12, Poligono.puntosCubiertos(l));
    }

    @Test
    @DisplayName("un lado diagonal tiene mcd(|dx|, |dy|) puntos enteros")
    void ladoDiagonal() {
        // Triángulo (0,0), (6,0), (0,4): la hipotenusa de (6,0) a (0,4) tiene mcd(6, 4) = 2 tramos
        List<Punto> t = List.of(new Punto(0, 0), new Punto(6, 0), new Punto(0, 4));
        assertEquals(24, Poligono.dobleArea(t));
        assertEquals(6 + 2 + 4, Poligono.puntosDelBorde(t));
        long[] bruto = contarEnTriangulo(t.get(0), t.get(1), t.get(2));
        assertEquals(bruto[0], Poligono.puntosInteriores(t));
        assertEquals(bruto[1], Poligono.puntosDelBorde(t));
    }

    @Test
    @DisplayName("con menos de tres vértices falla; con coordenadas enormes no desborda en silencio")
    void casosLimite() {
        assertThrows(IllegalArgumentException.class, () -> Poligono.dobleArea(List.of(new Punto(0, 0), new Punto(1, 1))));
        int grande = Integer.MAX_VALUE;
        // Un cuadrado de lado 2³¹ − 1: el doble del área (~ 9,2 · 10¹⁸) entra justo en un long
        List<Punto> enorme = rectangulo(0, 0, grande, grande);
        assertEquals(2L * grande * grande, Poligono.dobleArea(enorme));
        assertEquals(4L * grande, Poligono.puntosDelBorde(enorme));
        // De −(2³¹ − 1) a 2³¹ − 1: los lados no entran en un int ni el doble del área en un long,
        // así que el borde se calcula bien y el área falla en vez de dar cualquier cosa
        List<Punto> demasiadoGrande = List.of(new Punto(-grande, -grande), new Punto(grande, -grande),
                new Punto(grande, grande), new Punto(-grande, grande));
        assertEquals(8L * grande, Poligono.puntosDelBorde(demasiadoGrande));
        assertThrows(ArithmeticException.class, () -> Poligono.dobleArea(demasiadoGrande));
    }

    @Property
    @Label("rectángulo: interiores = (ancho − 1)(alto − 1) y cubiertos = (ancho + 1)(alto + 1)")
    void rectangulos(@ForAll @IntRange(min = -50, max = 50) int x, @ForAll @IntRange(min = -50, max = 50) int y,
                     @ForAll @IntRange(min = 1, max = 40) int ancho, @ForAll @IntRange(min = 1, max = 40) int alto) {
        List<Punto> r = rectangulo(x, y, ancho, alto);
        assertEquals(2L * ancho * alto, Poligono.dobleArea(r));
        assertEquals(2L * (ancho + alto), Poligono.puntosDelBorde(r));
        assertEquals((long) (ancho - 1) * (alto - 1), Poligono.puntosInteriores(r));
        assertEquals((long) (ancho + 1) * (alto + 1), Poligono.puntosCubiertos(r));
    }

    @Property
    @Label("triángulo cualquiera: Pick coincide con contar los puntos uno por uno")
    void triangulos(@ForAll @IntRange(min = -12, max = 12) int ax, @ForAll @IntRange(min = -12, max = 12) int ay,
                    @ForAll @IntRange(min = -12, max = 12) int bx, @ForAll @IntRange(min = -12, max = 12) int by,
                    @ForAll @IntRange(min = -12, max = 12) int cx, @ForAll @IntRange(min = -12, max = 12) int cy) {
        Punto a = new Punto(ax, ay), b = new Punto(bx, by), c = new Punto(cx, cy);
        if (cruz(a, b, c) == 0) return;   // degenerado: los tres en una recta
        long[] bruto = contarEnTriangulo(a, b, c);
        List<Punto> t = List.of(a, b, c);
        assertEquals(bruto[0], Poligono.puntosInteriores(t));
        assertEquals(bruto[1], Poligono.puntosDelBorde(t));
        assertEquals(bruto[0] + bruto[1], Poligono.puntosCubiertos(t));
        assertEquals(Math.abs(cruz(a, b, c)), Poligono.dobleArea(t));
    }
}
