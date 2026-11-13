package advent.code.y2022;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * Año 2022 — Día 3: Reorganización de Mochilas
 *
 * <p>El input tiene el contenido de cada mochila en una línea: cada letra es
 * un tipo de objeto ({@code a} y {@code A} son tipos distintos). La primera
 * mitad de la línea es el primer compartimiento y la segunda mitad, el
 * segundo.</p>
 */
public class Dia03 implements Resolvedor {

    /** Cuántos elfos tiene cada grupo en la parte 2. */
    static final int TAMANIO_GRUPO = 3;

    /**
     * Parte 1: la suma de las prioridades del tipo que aparece en los dos
     * compartimientos de cada mochila.
     *
     * @param lineas las mochilas
     * @return la suma de las prioridades
     * @throws IllegalArgumentException si una mochila tiene largo impar o sus
     *                                  compartimientos no comparten exactamente un tipo
     */
    @Override
    public String parte1(List<String> lineas) {
        long suma = 0;
        for (String mochila : leer(lineas)) {
            if (mochila.length() % 2 != 0) {
                throw new IllegalArgumentException("La mochila no se divide en dos mitades iguales: '" + mochila + "'");
            }
            int mitad = mochila.length() / 2;
            suma += unicoComun(List.of(mochila.substring(0, mitad), mochila.substring(mitad)));
        }
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la suma de las prioridades de las insignias: cada tres líneas
     * forman un grupo, y su insignia es el único tipo que llevan las tres
     * mochilas.
     *
     * @param lineas las mochilas
     * @return la suma de las prioridades
     * @throws IllegalArgumentException si la cantidad de mochilas no es múltiplo
     *                                  de 3 o un grupo no comparte exactamente un tipo
     */
    @Override
    public String parte2(List<String> lineas) {
        List<String> mochilas = leer(lineas);
        if (mochilas.size() % TAMANIO_GRUPO != 0) {
            throw new IllegalArgumentException("Hay " + mochilas.size() + " mochilas, que no se reparten en grupos de "
                    + TAMANIO_GRUPO);
        }
        return String.valueOf(mochilas.stream()
                .gather(Gatherers.windowFixed(TAMANIO_GRUPO))
                .mapToLong(Dia03::unicoComun)
                .sum());
    }

    /**
     * La prioridad de un tipo de objeto: de {@code a} a {@code z} valen 1 a 26
     * y de {@code A} a {@code Z}, 27 a 52.
     *
     * @param tipo una letra
     * @return su prioridad
     * @throws IllegalArgumentException si no es una letra de la A a la Z
     */
    static int prioridad(char tipo) {
        if (tipo >= 'a' && tipo <= 'z') return tipo - 'a' + 1;
        if (tipo >= 'A' && tipo <= 'Z') return tipo - 'A' + 27;
        throw new IllegalArgumentException("No es un tipo de objeto: '" + tipo + "'");
    }

    /**
     * Los tipos presentes en un texto, como máscara de bits: el bit
     * {@code p − 1} está prendido si aparece el tipo de prioridad {@code p}.
     * Con 52 tipos, entra en un {@code long}.
     *
     * @param objetos los objetos
     * @return la máscara de los tipos presentes
     */
    static long tipos(String objetos) {
        long mascara = 0;
        for (int i = 0; i < objetos.length(); i++) mascara |= 1L << (prioridad(objetos.charAt(i)) - 1);
        return mascara;
    }

    /**
     * La prioridad del único tipo que aparece en todos los textos: la
     * intersección de sus conjuntos de tipos es el AND de sus máscaras.
     *
     * @param grupos los textos que hay que comparar
     * @return la prioridad del tipo común
     * @throws IllegalArgumentException si no comparten exactamente un tipo
     */
    static int unicoComun(List<String> grupos) {
        long comunes = -1L;
        for (String objetos : grupos) comunes &= tipos(objetos);
        if (Long.bitCount(comunes) != 1) {
            throw new IllegalArgumentException("Se esperaba un único tipo en común y hay "
                    + Long.bitCount(comunes) + " en " + grupos);
        }
        return Long.numberOfTrailingZeros(comunes) + 1;
    }

    /** Las mochilas: las líneas no vacías. */
    private static List<String> leer(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas);
    }
}
