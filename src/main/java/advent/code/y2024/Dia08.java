package advent.code.y2024;

import advent.code.Grilla;
import advent.code.Matematicas;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Año 2024 — Día 8: Colinealidad Resonante
 *
 * <p>El input es un mapa de antenas: cada antena es una letra (mayúscula o
 * minúscula) o un dígito que indica su frecuencia, y {@code .} es vacío. Dos
 * antenas de la misma frecuencia generan <b>antinodos</b> en los puntos
 * alineados con ambas donde una está al doble de distancia que la otra.</p>
 */
public class Dia08 implements Resolvedor {

    private static final char VACIO = '.';

    /**
     * Parte 1: cantidad de posiciones distintas, dentro del mapa, que tienen
     * un antinodo.
     *
     * @param lineas el mapa de antenas
     * @return cantidad de posiciones con antinodo
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(contarAntinodos(Grilla.desdeLineas(lineas), Dia08::antinodosDelPar));
    }

    /**
     * Parte 2: con armónicos resonantes, un antinodo es <b>cualquier</b>
     * posición alineada con al menos dos antenas de la misma frecuencia, sin
     * importar la distancia (incluidas las propias antenas).
     *
     * @param lineas el mapa de antenas
     * @return cantidad de posiciones con antinodo
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(contarAntinodos(Grilla.desdeLineas(lineas), Dia08::antinodosEnLaRecta));
    }

    /** Antinodos que genera un par de antenas de la misma frecuencia. */
    @FunctionalInterface
    interface ReglaDeAntinodos {
        List<Punto> antinodos(Grilla mapa, Punto a, Punto b);
    }

    /**
     * Cuenta las posiciones distintas con antinodo, aplicando la regla a cada
     * par de antenas de la misma frecuencia.
     *
     * @param mapa  el mapa de antenas
     * @param regla qué antinodos genera un par
     * @return cantidad de posiciones distintas con antinodo
     */
    static int contarAntinodos(Grilla mapa, ReglaDeAntinodos regla) {
        Set<Punto> antinodos = new HashSet<>();
        for (List<Punto> antenas : antenasPorFrecuencia(mapa).values()) {
            for (int i = 0; i < antenas.size(); i++) {
                for (int j = i + 1; j < antenas.size(); j++) {
                    antinodos.addAll(regla.antinodos(mapa, antenas.get(i), antenas.get(j)));
                }
            }
        }
        return antinodos.size();
    }

    /**
     * Todas las posiciones del mapa que están en la recta de dos antenas
     * (parte 2), incluidas las antenas y las posiciones entre ellas.
     *
     * <p>Se avanza desde {@code a} en ambos sentidos con el paso <b>más
     * chico</b> posible sobre la grilla: {@code b − a} dividido por el MCD de
     * sus componentes. Con antenas en {@code (0,0)} y {@code (2,2)}, el paso
     * es {@code (1,1)} y {@code (1,1)} también es un antinodo; avanzar de a
     * {@code (2,2)} se lo saltearía. (En el input real ningún par tiene MCD
     * mayor que 1, pero así es correcto en general.)</p>
     *
     * @param mapa el mapa (la recta se corta en sus bordes)
     * @param a    una antena
     * @param b    otra antena de la misma frecuencia
     * @return las posiciones de la recta dentro del mapa
     */
    static List<Punto> antinodosEnLaRecta(Grilla mapa, Punto a, Punto b) {
        Punto diferencia = b.restar(a);
        int divisor = (int) Matematicas.mcd(Math.abs(diferencia.x()), Math.abs(diferencia.y()));
        Punto paso = new Punto(diferencia.x() / divisor, diferencia.y() / divisor);

        List<Punto> recta = new ArrayList<>();
        for (Punto p = a; mapa.estaDentro(p); p = p.sumar(paso)) recta.add(p);
        for (Punto p = a.restar(paso); mapa.estaDentro(p); p = p.restar(paso)) recta.add(p);
        return recta;
    }

    /**
     * Los dos antinodos de un par de antenas, si están dentro del mapa.
     *
     * <p>Si {@code d = b − a} es el paso de una antena a la otra, los
     * antinodos están un paso más allá de cada una: {@code b + d} (que está a
     * distancia {@code d} de {@code b} y {@code 2d} de {@code a}) y
     * {@code a − d}.</p>
     *
     * @param mapa el mapa (para descartar los que caen afuera)
     * @param a    una antena
     * @param b    otra antena de la misma frecuencia
     * @return los antinodos dentro del mapa (0, 1 o 2)
     */
    static List<Punto> antinodosDelPar(Grilla mapa, Punto a, Punto b) {
        Punto paso = b.restar(a);
        return Stream.of(b.sumar(paso), a.restar(paso))
                .filter(mapa::estaDentro)
                .toList();
    }

    /**
     * Agrupa las antenas por frecuencia. Mayúsculas y minúsculas son
     * frecuencias distintas ({@code A} no es {@code a}).
     *
     * @param mapa el mapa de antenas
     * @return posiciones de las antenas de cada frecuencia
     */
    static Map<Character, List<Punto>> antenasPorFrecuencia(Grilla mapa) {
        return mapa.buscarTodos(celda -> celda != VACIO).stream()
                .collect(Collectors.groupingBy(mapa::obtener));
    }
}
