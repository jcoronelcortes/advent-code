package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Año 2024 — Día 5: Cola de Impresión
 *
 * <p>El input tiene dos secciones separadas por una línea en blanco:</p>
 * <ol>
 *   <li><b>reglas de orden</b> {@code X|Y}: si una actualización incluye las
 *       páginas X e Y, X tiene que imprimirse antes que Y (no necesariamente
 *       justo antes);</li>
 *   <li><b>actualizaciones</b>: listas de páginas separadas por comas.</li>
 * </ol>
 */
public class Dia05 implements Resolvedor {

    /**
     * Parte 1: suma de la página del medio de cada actualización que ya está
     * en el orden correcto.
     *
     * @param lineas reglas, línea en blanco y actualizaciones
     * @return la suma de las páginas del medio
     */
    @Override
    public String parte1(List<String> lineas) {
        Manual manual = Manual.parsear(lineas);
        long suma = manual.actualizaciones().stream()
                .filter(manual::estaEnOrden)
                .mapToLong(Dia05::paginaDelMedio)
                .sum();
        return String.valueOf(suma);
    }

    /**
     * Parte 2: ordenar correctamente las actualizaciones que estaban
     * desordenadas y sumar sus páginas del medio.
     *
     * @param lineas reglas, línea en blanco y actualizaciones
     * @return la suma de las páginas del medio de las actualizaciones corregidas
     */
    @Override
    public String parte2(List<String> lineas) {
        Manual manual = Manual.parsear(lineas);
        long suma = manual.actualizaciones().stream()
                .filter(paginas -> !manual.estaEnOrden(paginas))
                .map(manual::ordenar)
                .mapToLong(Dia05::paginaDelMedio)
                .sum();
        return String.valueOf(suma);
    }

    /**
     * Regla {@code antes|despues}.
     *
     * @param antes   página que debe ir primero
     * @param despues página que debe ir después
     */
    record Regla(int antes, int despues) {}

    /**
     * Reglas de orden y actualizaciones del manual de seguridad.
     *
     * @param reglas           reglas de orden (en un conjunto: consultar una cuesta O(1))
     * @param actualizaciones  páginas de cada actualización, en el orden propuesto
     */
    record Manual(Set<Regla> reglas, List<List<Integer>> actualizaciones) {

        /**
         * Indica si una actualización respeta todas las reglas que la afectan.
         *
         * <p>Para cada par de páginas en las posiciones {@code i < j}, el
         * orden es incorrecto solo si existe la regla <em>contraria</em>
         * {@code página_j|página_i}. Las reglas que mencionan páginas que no
         * están en la actualización nunca se consultan, así que se ignoran
         * solas, como pide el enunciado.</p>
         *
         * @param paginas páginas de la actualización
         * @return {@code true} si ningún par contradice una regla
         */
        boolean estaEnOrden(List<Integer> paginas) {
            for (int i = 0; i < paginas.size(); i++) {
                for (int j = i + 1; j < paginas.size(); j++) {
                    if (reglas.contains(new Regla(paginas.get(j), paginas.get(i)))) return false;
                }
            }
            return true;
        }

        /**
         * Ordena las páginas de una actualización según las reglas.
         *
         * <p>Usa las reglas como comparador: {@code a} va antes que {@code b}
         * si existe la regla {@code a|b}. Esto es correcto porque, dentro de
         * cada actualización, <b>todo par de páginas tiene una regla</b> (se
         * verificó en el input), así que las reglas definen un orden total
         * entre esas páginas.</p>
         *
         * <p>Ojo: el conjunto completo de reglas tiene ciclos, así que no es un
         * orden global entre todas las páginas; solo lo es restringido a las
         * páginas de cada actualización. Si un par no tuviera regla, el
         * comparador no sería un orden válido y se lanza una excepción en vez
         * de devolver un orden arbitrario.</p>
         *
         * @param paginas páginas de la actualización
         * @return las páginas en el orden correcto
         * @throws IllegalStateException si dos páginas no tienen ninguna regla entre ellas
         */
        List<Integer> ordenar(List<Integer> paginas) {
            List<Integer> ordenadas = new ArrayList<>(paginas);
            ordenadas.sort((a, b) -> {
                if (a.equals(b)) return 0;
                if (reglas.contains(new Regla(a, b))) return -1;
                if (reglas.contains(new Regla(b, a))) return 1;
                throw new IllegalStateException("No hay regla entre las páginas " + a + " y " + b);
            });
            return ordenadas;
        }

        /**
         * Separa el input en sus dos secciones.
         *
         * @param lineas reglas, línea en blanco y actualizaciones
         * @return el manual
         */
        static Manual parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas, 2);
            Set<Regla> reglas = new HashSet<>();
            for (List<Integer> regla : Utiles.numerosPorLinea(secciones.get(0))) {
                reglas.add(new Regla(regla.get(0), regla.get(1)));
            }
            return new Manual(reglas, Utiles.numerosPorLinea(secciones.get(1)));
        }
    }

    /**
     * Página del medio de una actualización (todas tienen una cantidad impar
     * de páginas).
     *
     * @param paginas páginas de la actualización
     * @return la página central
     */
    static int paginaDelMedio(List<Integer> paginas) {
        if (paginas.size() % 2 == 0) {
            throw new IllegalArgumentException("Una actualización con cantidad par de páginas no tiene medio: " + paginas);
        }
        return paginas.get(paginas.size() / 2);
    }
}
