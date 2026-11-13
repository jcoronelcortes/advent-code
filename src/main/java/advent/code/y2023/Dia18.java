package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Poligono;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2023 — Día 18: Laguna del Lavaducto
 *
 * <p>El input es un plan de excavación: cada línea indica una dirección
 * ({@code U}, {@code D}, {@code L}, {@code R}), cuántos metros cavar y un
 * color. La excavadora va dejando una zanja que forma un lazo cerrado; después
 * se cava todo el interior. La pregunta es cuántos metros cúbicos (casillas
 * de 1 × 1) quedan cavados.</p>
 */
public class Dia18 implements Resolvedor {

    /** Una línea del plan: dirección, metros y color, como {@code R 6 (#70c710)}. */
    private static final Pattern LINEA = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9a-f]{6})\\)");

    /**
     * Un tramo de zanja.
     *
     * @param direccion hacia dónde se cava
     * @param metros    cuántos metros (al menos 1)
     */
    record Tramo(Direccion direccion, long metros) {
    }

    /**
     * Parte 1: cuántos metros cúbicos tiene la laguna siguiendo el plan.
     *
     * @param lineas el plan de excavación
     * @return el volumen de la laguna
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(volumen(leerPlan(lineas, Dia18::leerTramo)));
    }

    /**
     * Parte 2: lo mismo, pero las instrucciones verdaderas están escondidas
     * en los colores. La laguna es enorme (cientos de miles de millones de
     * casillas), pero el cálculo no depende de su tamaño.
     *
     * @param lineas el plan de excavación
     * @return el volumen de la laguna
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(volumen(leerPlan(lineas, Dia18::leerTramoDelColor)));
    }

    /**
     * Lee todas las líneas no vacías del plan con la interpretación dada.
     *
     * @param lineas el plan
     * @param lector cómo convertir cada línea en un tramo
     * @return los tramos, en orden
     */
    static List<Tramo> leerPlan(List<String> lineas, Function<String, Tramo> lector) {
        return lineas.stream().filter(linea -> !linea.isBlank()).map(lector).toList();
    }

    /**
     * Lee una línea del plan con la dirección y los metros que indica a la
     * vista (el color no se usa).
     *
     * @param linea una línea como {@code R 6 (#70c710)}
     * @return el tramo
     * @throws IllegalArgumentException si la línea no tiene el formato esperado
     */
    static Tramo leerTramo(String linea) {
        Matcher partes = partes(linea);
        return new Tramo(Direccion.desdeLetra(partes.group(1).charAt(0)), Long.parseLong(partes.group(2)));
    }

    /**
     * Lee la instrucción verdadera escondida en el color: los cinco primeros
     * dígitos hexadecimales son los metros y el último, la dirección
     * ({@code 0} derecha, {@code 1} abajo, {@code 2} izquierda,
     * {@code 3} arriba).
     *
     * @param linea una línea como {@code R 6 (#70c710)}
     * @return el tramo, por ejemplo derecha 461937
     * @throws IllegalArgumentException si la línea no tiene el formato esperado
     *                                  o el último dígito no es una dirección
     */
    static Tramo leerTramoDelColor(String linea) {
        String color = partes(linea).group(3);
        Direccion direccion = switch (color.charAt(5)) {
            case '0' -> Direccion.DERECHA;
            case '1' -> Direccion.ABAJO;
            case '2' -> Direccion.IZQUIERDA;
            case '3' -> Direccion.ARRIBA;
            default -> throw new IllegalArgumentException("Dirección inválida en el color: '" + linea + "'");
        };
        return new Tramo(direccion, Long.parseLong(color.substring(0, 5), 16));
    }

    /** Separa una línea del plan en dirección, metros y color, o falla si no tiene el formato esperado. */
    private static Matcher partes(String linea) {
        Matcher partes = LINEA.matcher(linea.strip());
        if (!partes.matches()) throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
        return partes;
    }

    /**
     * Cuántas casillas quedan cavadas: la zanja más todo su interior.
     *
     * <p>Los centros de las casillas de la zanja forman un polígono cuyos
     * vértices son los extremos de cada tramo, con coordenadas enteras. Las
     * casillas cavadas son los puntos enteros del interior más los del borde
     * (las casillas de la zanja), y se cuentan con la fórmula de Gauss y el
     * teorema de Pick ({@link Poligono#puntosCubiertos}).</p>
     *
     * <p>No hace falta dibujar la laguna: el cálculo depende solo de la
     * cantidad de tramos, no de lo grandes que sean. Las cuentas son exactas
     * y fallan en vez de desbordar en silencio si las coordenadas fueran
     * demasiado grandes.</p>
     *
     * @param tramos los tramos de la zanja, en orden
     * @return la cantidad de casillas cavadas
     * @throws IllegalArgumentException si los tramos no cierran el lazo
     */
    static long volumen(List<Tramo> tramos) {
        List<Punto> vertices = new ArrayList<>();
        Punto actual = new Punto(0, 0);
        for (Tramo tramo : tramos) {
            vertices.add(actual);
            actual = new Punto(Math.toIntExact(actual.x() + tramo.direccion().dx() * tramo.metros()),
                    Math.toIntExact(actual.y() + tramo.direccion().dy() * tramo.metros()));
        }
        if (!actual.equals(new Punto(0, 0))) {
            throw new IllegalArgumentException("La zanja no vuelve al inicio: termina en " + actual);
        }
        return Poligono.puntosCubiertos(vertices);
    }
}
