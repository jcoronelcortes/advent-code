package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherers;

/**
 * Año 2024 — Día 2: Reportes del Reno de Nariz Roja
 *
 * <p>Cada línea del input es un <b>reporte</b>: una lista de números
 * (<b>niveles</b>) separados por espacios. Un reporte es <b>seguro</b> si
 * sus niveles son todos crecientes o todos decrecientes, y dos niveles
 * vecinos difieren en al menos 1 y como mucho 3.</p>
 */
public class Dia02 implements Resolvedor {

    /** Diferencia mínima permitida entre dos niveles vecinos (en valor absoluto). */
    private static final int SALTO_MINIMO = 1;

    /** Diferencia máxima permitida entre dos niveles vecinos (en valor absoluto). */
    private static final int SALTO_MAXIMO = 3;

    /**
     * Parte 1: cantidad de reportes seguros.
     *
     * @param lineas un reporte por línea
     * @return cantidad de reportes seguros
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(leerReportes(lineas).stream().filter(Dia02::esSeguro).count());
    }

    /**
     * Parte 2: cantidad de reportes seguros con el <b>amortiguador de
     * problemas</b>, que tolera un solo nivel malo: un reporte inseguro cuenta
     * como seguro si al quitarle un nivel queda seguro.
     *
     * @param lineas un reporte por línea
     * @return cantidad de reportes seguros
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(leerReportes(lineas).stream().filter(Dia02::esSeguroConAmortiguador).count());
    }

    /**
     * Indica si un reporte es seguro, o lo sería quitándole un solo nivel.
     *
     * <p>Los reportes son cortos (hasta 8 niveles), así que basta con probar
     * quitar cada nivel: O(n²) por reporte, unas 64 comparaciones como
     * mucho.</p>
     *
     * @param niveles niveles del reporte
     * @return {@code true} si es seguro con a lo sumo un nivel quitado
     */
    static boolean esSeguroConAmortiguador(List<Integer> niveles) {
        if (esSeguro(niveles)) return true;
        for (int quitado = 0; quitado < niveles.size(); quitado++) {
            List<Integer> sinUno = new ArrayList<>(niveles);
            sinUno.remove(quitado);   // remove(int): por posición, no por valor
            if (esSeguro(sinUno)) return true;
        }
        return false;
    }

    /**
     * Indica si un reporte es seguro.
     *
     * <p>Se miran las <b>diferencias</b> entre niveles vecinos (con
     * {@link Gatherers#windowSliding}, que recorre la lista de a pares
     * consecutivos). Las dos reglas del enunciado equivalen a que todas las
     * diferencias estén entre 1 y 3, o todas entre −3 y −1: el signo común
     * asegura que el reporte sea todo creciente o todo decreciente, y una
     * diferencia 0 queda afuera de ambos intervalos.</p>
     *
     * @param niveles niveles del reporte
     * @return {@code true} si es seguro
     */
    static boolean esSeguro(List<Integer> niveles) {
        List<Integer> diferencias = diferencias(niveles);
        boolean creciente = diferencias.stream().allMatch(d -> d >= SALTO_MINIMO && d <= SALTO_MAXIMO);
        boolean decreciente = diferencias.stream().allMatch(d -> d <= -SALTO_MINIMO && d >= -SALTO_MAXIMO);
        return creciente || decreciente;
    }

    /**
     * Diferencias entre cada nivel y el anterior.
     *
     * @param niveles niveles del reporte
     * @return {@code niveles[i+1] − niveles[i]} para cada par vecino
     */
    static List<Integer> diferencias(List<Integer> niveles) {
        return niveles.stream()
                .gather(Gatherers.windowSliding(2))
                .filter(par -> par.size() == 2)      // con un solo nivel, la única ventana es incompleta
                .map(par -> par.get(1) - par.get(0))
                .toList();
    }

    /**
     * Convierte el input en reportes, ignorando las líneas en blanco.
     *
     * @param lineas un reporte por línea
     * @return niveles de cada reporte
     */
    static List<List<Integer>> leerReportes(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Utiles::leerNumeros).toList();
    }
}
