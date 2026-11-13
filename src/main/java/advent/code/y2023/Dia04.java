package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Año 2023 — Día 4: Rasca y Gana
 *
 * <p>Cada línea del input es una tarjeta: {@code Card 1: 41 48 83 | 83 86 6}.
 * Antes de la barra están los números ganadores y después, los números que
 * tenemos. Lo que importa de cada tarjeta es cuántos de nuestros números
 * están entre los ganadores. En la parte 2, cada coincidencia gana una copia
 * de una de las tarjetas siguientes.</p>
 */
public class Dia04 implements Resolvedor {

    /**
     * Parte 1: la suma de los puntos de todas las tarjetas. Una tarjeta vale
     * 1 punto con la primera coincidencia y el doble con cada una de las
     * siguientes; sin coincidencias vale 0.
     *
     * @param lineas las tarjetas
     * @return los puntos
     */
    @Override
    public String parte1(List<String> lineas) {
        long puntos = tarjetas(lineas).stream().mapToLong(Tarjeta::puntos).sum();
        return String.valueOf(puntos);
    }

    /**
     * Parte 2: cuántas tarjetas se tienen al final, contando las originales
     * y todas las copias ganadas.
     *
     * @param lineas las tarjetas
     * @return la cantidad total de tarjetas
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(Arrays.stream(instancias(tarjetas(lineas))).sum());
    }

    /**
     * Cuántas instancias (original más copias) se terminan teniendo de cada
     * tarjeta.
     *
     * <p>Una tarjeta con {@code k} coincidencias gana una copia de cada una
     * de las {@code k} tarjetas siguientes, y cada copia gana lo mismo que la
     * original. Como las copias siempre son de tarjetas <em>posteriores</em>,
     * al llegar a la tarjeta {@code i} (en orden) ya se sabe cuántas
     * instancias tiene: todas las que podían darle copias ya pasaron. Así
     * que una sola pasada alcanza: sus {@code instancias[i]} ejemplares
     * suman {@code instancias[i]} copias a cada una de las {@code k}
     * siguientes.</p>
     *
     * @param tarjetas las tarjetas, en orden
     * @return instancias de cada tarjeta, en el mismo orden
     * @throws IllegalArgumentException si una tarjeta ganaría copias más allá de la última
     */
    static long[] instancias(List<Tarjeta> tarjetas) {
        long[] instancias = new long[tarjetas.size()];
        Arrays.fill(instancias, 1);
        for (int i = 0; i < tarjetas.size(); i++) {
            int coincidencias = tarjetas.get(i).coincidencias();
            if (i + coincidencias >= tarjetas.size()) {
                throw new IllegalArgumentException("La tarjeta " + tarjetas.get(i).numero()
                        + " ganaría copias más allá de la última");
            }
            for (int siguiente = i + 1; siguiente <= i + coincidencias; siguiente++) {
                instancias[siguiente] = Math.addExact(instancias[siguiente], instancias[i]);
            }
        }
        return instancias;
    }

    static List<Tarjeta> tarjetas(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Tarjeta::parsear).toList();
    }

    /**
     * Una tarjeta: su número y cuántos de nuestros números son ganadores.
     *
     * @param numero        el número de la tarjeta
     * @param coincidencias cuántos de nuestros números están entre los ganadores
     */
    record Tarjeta(int numero, int coincidencias) {

        /**
         * Lee una tarjeta como {@code Card 1: 41 48 83 | 83 86 6} (con
         * cualquier cantidad de espacios entre los números).
         *
         * @param linea la línea
         * @return la tarjeta
         * @throws IllegalArgumentException si la línea no tiene esa forma
         */
        static Tarjeta parsear(String linea) {
            String[] encabezadoYNumeros = linea.split(":");
            String[] ganadoresYPropios = encabezadoYNumeros.length == 2 ? encabezadoYNumeros[1].split("\\|") : new String[0];
            if (ganadoresYPropios.length != 2) throw new IllegalArgumentException("Tarjeta inválida: '" + linea + "'");
            int numero = Utiles.leerNumeros(encabezadoYNumeros[0], 1).getFirst();
            Set<Integer> ganadores = new HashSet<>(Utiles.leerNumeros(ganadoresYPropios[0]));
            int coincidencias = (int) Utiles.leerNumeros(ganadoresYPropios[1]).stream()
                    .filter(ganadores::contains)
                    .count();
            return new Tarjeta(numero, coincidencias);
        }

        /** Los puntos: 0 sin coincidencias; si no, 2^(coincidencias − 1). */
        long puntos() {
            return coincidencias == 0 ? 0 : 1L << (coincidencias - 1);
        }
    }
}
