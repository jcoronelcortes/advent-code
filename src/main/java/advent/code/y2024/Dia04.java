package advent.code.y2024;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.List;

/**
 * Año 2024 — Día 4: Búsqueda en Ceres
 *
 * <p>El input es una sopa de letras. Hay que encontrar todas las apariciones
 * de la palabra {@code XMAS}: en horizontal, vertical o diagonal, al derecho
 * o al revés, y pudiendo superponerse con otras.</p>
 */
public class Dia04 implements Resolvedor {

    private static final String PALABRA = "XMAS";

    /** Carácter que devuelve la grilla fuera de sus bordes: no coincide con ninguna letra. */
    private static final char AFUERA = '.';

    /**
     * Parte 1: cantidad de apariciones de {@code XMAS} en la sopa de letras.
     *
     * @param lineas filas de la sopa de letras
     * @return cantidad de apariciones
     */
    @Override
    public String parte1(List<String> lineas) {
        Grilla sopa = Grilla.desdeLineas(lineas);
        long apariciones = 0;
        for (Punto inicio : sopa.buscarTodos(PALABRA.charAt(0))) {
            for (Punto direccion : Punto.OCHO_DIRECCIONES) {
                if (estaLaPalabra(sopa, inicio, direccion, PALABRA)) apariciones++;
            }
        }
        return String.valueOf(apariciones);
    }

    /**
     * Parte 2: cantidad de <b>X-MAS</b>: dos {@code MAS} que se cruzan en
     * forma de X sobre la misma {@code A}, cada uno al derecho o al revés.
     *
     * @param lineas filas de la sopa de letras
     * @return cantidad de X-MAS
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla sopa = Grilla.desdeLineas(lineas);
        long cruces = sopa.buscarTodos('A').stream().filter(centro -> esCruz(sopa, centro)).count();
        return String.valueOf(cruces);
    }

    /**
     * Indica si hay una X-MAS centrada en {@code centro} (que debe ser una
     * {@code A}).
     *
     * <p>Cada diagonal que pasa por la {@code A} tiene que leerse {@code MAS}
     * en algún sentido, es decir, tener una {@code M} en un extremo y una
     * {@code S} en el otro. Fuera de la grilla se lee {@code .}, así que una
     * {@code A} en el borde nunca forma una cruz.</p>
     *
     * @param sopa   la sopa de letras
     * @param centro posición de la {@code A}
     * @return {@code true} si las dos diagonales forman {@code MAS}
     */
    static boolean esCruz(Grilla sopa, Punto centro) {
        return esMasEnDiagonal(sopa, centro, new Punto(-1, -1), new Punto(1, 1))       // ↖ ↘
                && esMasEnDiagonal(sopa, centro, new Punto(1, -1), new Punto(-1, 1));  // ↗ ↙
    }

    /** Una punta de la diagonal es M y la otra S (en cualquier orden). */
    private static boolean esMasEnDiagonal(Grilla sopa, Punto centro, Punto haciaUno, Punto haciaOtro) {
        char uno = sopa.obtener(centro.sumar(haciaUno), AFUERA);
        char otro = sopa.obtener(centro.sumar(haciaOtro), AFUERA);
        return (uno == 'M' && otro == 'S') || (uno == 'S' && otro == 'M');
    }

    /**
     * Indica si la palabra está escrita a partir de {@code inicio}, avanzando
     * en la {@code direccion} dada.
     *
     * <p>Leer al revés no necesita un caso aparte: {@code SAMX} hacia la
     * derecha es {@code XMAS} hacia la izquierda, y se encuentra al partir de
     * la {@code X} con la dirección opuesta. Por eso basta con empezar en cada
     * {@code X} y probar las 8 direcciones: cada aparición se cuenta una sola
     * vez.</p>
     *
     * @param sopa      la sopa de letras
     * @param inicio    posición de la primera letra
     * @param direccion paso entre una letra y la siguiente
     * @param palabra   palabra a buscar
     * @return {@code true} si la palabra está en esa posición y dirección
     */
    static boolean estaLaPalabra(Grilla sopa, Punto inicio, Punto direccion, String palabra) {
        Punto actual = inicio;
        for (int i = 0; i < palabra.length(); i++) {
            if (sopa.obtener(actual, AFUERA) != palabra.charAt(i)) return false;
            actual = actual.sumar(direccion);
        }
        return true;
    }
}
