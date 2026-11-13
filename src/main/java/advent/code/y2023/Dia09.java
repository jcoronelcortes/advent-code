package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2023 — Día 9: Mantenimiento del Espejismo
 *
 * <p>Cada línea del input es el historial de un valor: una secuencia de
 * enteros (que pueden ser negativos). Para predecir el valor siguiente se
 * toman las diferencias entre valores consecutivos, después las diferencias
 * de esas diferencias, y así hasta llegar a una secuencia de ceros. En la
 * parte 2 se predice, de la misma forma, el valor anterior al primero.</p>
 */
public class Dia09 implements Resolvedor {

    /**
     * Parte 1: la suma de los valores siguientes predichos para cada
     * historial.
     *
     * @param lineas los historiales
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        long suma = Utiles.numerosLargosPorLinea(lineas).stream()
                .mapToLong(Dia09::siguiente)
                .reduce(0, Math::addExact);
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la suma de los valores anteriores predichos para cada
     * historial.
     *
     * @param lineas los historiales
     * @return la suma
     */
    @Override
    public String parte2(List<String> lineas) {
        long suma = Utiles.numerosLargosPorLinea(lineas).stream()
                .mapToLong(Dia09::anterior)
                .reduce(0, Math::addExact);
        return String.valueOf(suma);
    }

    /**
     * Las diferencias entre valores consecutivos: una menos que la secuencia.
     *
     * @param valores la secuencia
     * @return {@code valores[i + 1] − valores[i]} para cada {@code i}
     */
    static List<Long> diferencias(List<Long> valores) {
        List<Long> diferencias = new ArrayList<>(valores.size() - 1);
        for (int i = 1; i < valores.size(); i++) {
            diferencias.add(Math.subtractExact(valores.get(i), valores.get(i - 1)));
        }
        return diferencias;
    }

    /**
     * El valor que sigue a la secuencia.
     *
     * <p>Si la secuencia es toda de ceros, el siguiente también es 0. Si no,
     * el siguiente es el último valor más el siguiente de la secuencia de
     * diferencias (que se calcula igual). Es lo mismo que completar la
     * pirámide del enunciado de abajo hacia arriba.</p>
     *
     * @param valores la secuencia
     * @return el valor siguiente
     * @throws IllegalArgumentException si la secuencia está vacía (no llega a
     *                                  ceros antes de quedarse sin valores)
     */
    static long siguiente(List<Long> valores) {
        if (valores.isEmpty()) {
            throw new IllegalArgumentException("La secuencia no llega a ser toda de ceros");
        }
        if (valores.stream().allMatch(valor -> valor == 0)) return 0;
        return Math.addExact(valores.getLast(), siguiente(diferencias(valores)));
    }

    /**
     * El valor que va antes del primero de la secuencia.
     *
     * <p>Es la misma pirámide completada por la izquierda: si el anterior de
     * la secuencia es {@code x}, la primera diferencia pasa a ser
     * {@code primero − x}, que tiene que ser el anterior de la secuencia de
     * diferencias. Entonces {@code x = primero − anterior(diferencias)}.</p>
     *
     * @param valores la secuencia
     * @return el valor anterior
     * @throws IllegalArgumentException si la secuencia no llega a ser toda de ceros
     */
    static long anterior(List<Long> valores) {
        if (valores.isEmpty()) {
            throw new IllegalArgumentException("La secuencia no llega a ser toda de ceros");
        }
        if (valores.stream().allMatch(valor -> valor == 0)) return 0;
        return Math.subtractExact(valores.getFirst(), anterior(diferencias(valores)));
    }
}
