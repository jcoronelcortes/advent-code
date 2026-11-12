package advent.code.y2025;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;

/**
 * Año 2025 — Día 3: Vestíbulo
 *
 * <p>Cada línea del input es un banco de baterías, una por dígito (1 a 9).
 * En cada banco se encienden exactamente {@code k} baterías, sin reordenarlas,
 * y el banco produce el número formado por esos dígitos. Se busca el máximo
 * voltaje de cada banco y se suman.</p>
 */
public class Dia03 implements Resolvedor {

    /** Cantidad de baterías a encender por banco en la parte 1. */
    private static final int BATERIAS_PARTE_1 = 2;

    /** Cantidad de baterías a encender por banco en la parte 2 (12 dígitos caben en un {@code long}). */
    private static final int BATERIAS_PARTE_2 = 12;

    /**
     * Parte 1: suma del máximo voltaje de cada banco encendiendo 2 baterías.
     *
     * @param lineas un banco de baterías por línea
     * @return voltaje total
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(sumarMaximos(lineas, BATERIAS_PARTE_1));
    }

    /**
     * Parte 2: suma del máximo voltaje de cada banco encendiendo 12 baterías.
     * Usa el mismo algoritmo voraz que la parte 1; solo cambia la cantidad.
     *
     * @param lineas un banco de baterías por línea
     * @return voltaje total
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(sumarMaximos(lineas, BATERIAS_PARTE_2));
    }

    /**
     * Suma el máximo voltaje de cada banco, ignorando las líneas en blanco.
     *
     * @param lineas   un banco de baterías por línea
     * @param cantidad baterías a encender en cada banco
     * @return suma de los máximos
     */
    static long sumarMaximos(List<String> lineas, int cantidad) {
        long total = 0;
        for (String banco : Utiles.lineasNoVacias(lineas)) {
            total += maximoVoltaje(banco, cantidad);
        }
        return total;
    }

    /**
     * Máximo número que se puede formar eligiendo {@code cantidad} dígitos del
     * banco, respetando su orden.
     *
     * <p><b>Algoritmo voraz:</b> entre números con la misma cantidad de dígitos,
     * gana el que tiene el primer dígito más grande, así que conviene elegir
     * los dígitos de izquierda a derecha, cada uno lo más grande posible.
     * La única restricción es dejar suficientes baterías a la derecha para
     * completar los dígitos que faltan: el dígito {@code i} (de {@code 0} a
     * {@code cantidad-1}) se busca entre la posición siguiente al dígito
     * anterior y la posición {@code n - cantidad + i}. Ante empates se toma
     * la primera aparición, para dejar más opciones a la derecha.</p>
     *
     * <p>Ejemplo con {@code 818181911112111} y 2 baterías: el primer dígito se
     * busca en las posiciones 0..13 → el 9 (posición 6); el segundo en
     * 7..14 → el 2 (posición 11). Resultado: 92.</p>
     *
     * @param banco    dígitos del banco
     * @param cantidad baterías a encender (entre 1 y el largo del banco)
     * @return el máximo voltaje
     */
    static long maximoVoltaje(String banco, int cantidad) {
        int largo = banco.length();
        if (cantidad < 1 || cantidad > largo) {
            throw new IllegalArgumentException("No se pueden encender " + cantidad
                    + " baterías en un banco de " + largo);
        }

        long voltaje = 0;
        int desde = 0;
        for (int elegidos = 0; elegidos < cantidad; elegidos++) {
            int hasta = largo - cantidad + elegidos;   // deja lugar para los dígitos que faltan

            int posicionMayor = desde;
            for (int i = desde + 1; i <= hasta; i++) {
                if (banco.charAt(i) > banco.charAt(posicionMayor)) posicionMayor = i;
            }

            voltaje = voltaje * 10 + (banco.charAt(posicionMayor) - '0');
            desde = posicionMayor + 1;
        }
        return voltaje;
    }
}
