package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Resolvedor;

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
        Direccion direccion = switch (partes.group(1)) {
            case "U" -> Direccion.ARRIBA;
            case "D" -> Direccion.ABAJO;
            case "L" -> Direccion.IZQUIERDA;
            default -> Direccion.DERECHA;
        };
        return new Tramo(direccion, Long.parseLong(partes.group(2)));
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
     * <p>Los centros de las casillas de la zanja forman un polígono con
     * vértices enteros en los extremos de cada tramo. Su área sale de la
     * fórmula de Gauss (del "cordón de zapato"), sumando
     * {@code x·y' − x'·y} por cada lado. El <b>teorema de Pick</b> relaciona
     * esa área con los puntos enteros: {@code área = interiores + borde/2 − 1},
     * donde {@code borde} son los puntos sobre el borde, es decir las casillas
     * de la zanja (la suma de los metros). Las casillas cavadas son
     * {@code interiores + borde = área + borde/2 + 1}.</p>
     *
     * <p>No hace falta dibujar la laguna: el cálculo depende solo de la
     * cantidad de tramos, no de lo grandes que sean. Los productos se hacen
     * con operaciones exactas, que fallan en vez de desbordar en silencio si
     * las coordenadas fueran demasiado grandes.</p>
     *
     * @param tramos los tramos de la zanja, en orden
     * @return la cantidad de casillas cavadas
     * @throws IllegalArgumentException si los tramos no cierran el lazo
     */
    static long volumen(List<Tramo> tramos) {
        long x = 0, y = 0, dobleArea = 0, borde = 0;
        for (Tramo tramo : tramos) {
            long nuevoX = x + tramo.direccion().dx() * tramo.metros();
            long nuevoY = y + tramo.direccion().dy() * tramo.metros();
            dobleArea = Math.addExact(dobleArea,
                    Math.subtractExact(Math.multiplyExact(x, nuevoY), Math.multiplyExact(nuevoX, y)));
            borde += tramo.metros();
            x = nuevoX;
            y = nuevoY;
        }
        if (x != 0 || y != 0) {
            throw new IllegalArgumentException("La zanja no vuelve al inicio: termina en (" + x + ", " + y + ")");
        }
        return (Math.abs(dobleArea) + borde) / 2 + 1;
    }
}
