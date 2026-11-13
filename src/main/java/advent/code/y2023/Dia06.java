package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;

/**
 * Año 2023 — Día 6: Espera y Verás
 *
 * <p>El input tiene dos líneas: la duración de cada carrera ({@code Time:}) y
 * el récord de distancia de cada una ({@code Distance:}). Un bote de juguete
 * se carga manteniendo apretado un botón al comienzo: cada milisegundo
 * apretado le suma 1 mm/ms de velocidad, pero ese tiempo no lo usa para
 * moverse. Con {@code h} milisegundos apretado en una carrera de {@code T},
 * el bote recorre {@code h · (T − h)}. En la parte 2, los espacios entre
 * los números no cuentan: cada línea es un solo número.</p>
 */
public class Dia06 implements Resolvedor {

    /**
     * Parte 1: el producto, sobre todas las carreras, de la cantidad de
     * formas de superar el récord.
     *
     * @param lineas las duraciones y los récords
     * @return el producto
     */
    @Override
    public String parte1(List<String> lineas) {
        List<String> datos = Utiles.lineasNoVacias(lineas);
        if (datos.size() != 2) throw new IllegalArgumentException("Se esperaban dos líneas: Time y Distance");
        List<Long> tiempos = Utiles.leerNumerosLargos(datos.get(0));
        List<Long> records = Utiles.leerNumerosLargos(datos.get(1), tiempos.size());
        long producto = 1;
        for (int i = 0; i < tiempos.size(); i++) {
            producto = Math.multiplyExact(producto, formasDeGanar(tiempos.get(i), records.get(i)));
        }
        return String.valueOf(producto);
    }

    /**
     * Parte 2: la cantidad de formas de superar el récord en una sola
     * carrera, juntando las cifras de cada línea en un único número.
     *
     * @param lineas las duraciones y los récords
     * @return la cantidad de formas
     */
    @Override
    public String parte2(List<String> lineas) {
        List<String> datos = Utiles.lineasNoVacias(lineas);
        if (datos.size() != 2) throw new IllegalArgumentException("Se esperaban dos líneas: Time y Distance");
        return String.valueOf(formasDeGanar(juntarCifras(datos.get(0)), juntarCifras(datos.get(1))));
    }

    /**
     * El número que forman todas las cifras de una línea, sin los espacios
     * entre ellas: {@code "Time:  7  15   30"} → 71530.
     *
     * @param linea la línea
     * @return el número
     * @throws IllegalArgumentException si la línea no tiene cifras
     */
    static long juntarCifras(String linea) {
        String cifras = linea.replaceAll("\\D", "");
        if (cifras.isEmpty()) throw new IllegalArgumentException("La línea no tiene cifras: '" + linea + "'");
        return Long.parseLong(cifras);
    }

    /** La distancia que recorre el bote si se aprieta el botón {@code apretado} ms de los {@code tiempo}. */
    static long distancia(long apretado, long tiempo) {
        return Math.multiplyExact(apretado, tiempo - apretado);
    }

    /**
     * Cuántas cantidades enteras de milisegundos apretando el botón hacen
     * que el bote supere el récord.
     *
     * <p>La distancia {@code h · (T − h)} es una parábola simétrica
     * alrededor de {@code T / 2}: crece de {@code h = 0} hasta la mitad y
     * después decrece igual. Entonces los {@code h} que ganan forman un
     * intervalo {@code [h₀, T − h₀]}, donde {@code h₀} es el menor que supera
     * el récord, y son {@code T − 2·h₀ + 1}.</p>
     *
     * <p>{@code h₀} se busca con una <b>búsqueda binaria</b> en la mitad
     * creciente, {@code [0, T / 2]}, usando solo enteros: así no hay errores
     * de redondeo como con la fórmula de la raíz cuadrada en {@code double},
     * que importan justo cuando la distancia empata con el récord.</p>
     *
     * @param tiempo la duración de la carrera
     * @param record la distancia a superar (estrictamente)
     * @return la cantidad de formas de ganar (0 si no hay ninguna)
     * @throws IllegalArgumentException si el tiempo o el récord son negativos
     */
    static long formasDeGanar(long tiempo, long record) {
        if (tiempo < 0 || record < 0) throw new IllegalArgumentException("Tiempo y récord no pueden ser negativos");
        long mitad = tiempo / 2;
        if (distancia(mitad, tiempo) <= record) return 0;   // ni el mejor h alcanza
        long bajo = 0;        // no gana (h = 0 recorre 0, y el récord no es negativo)
        long alto = mitad;    // gana
        while (alto - bajo > 1) {
            long medio = bajo + (alto - bajo) / 2;
            if (distancia(medio, tiempo) > record) alto = medio;
            else bajo = medio;
        }
        return tiempo - 2 * alto + 1;
    }
}
