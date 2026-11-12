package advent.code;

import java.util.Collection;
import java.util.List;

/**
 * Conjunto de enteros representado como una unión de {@link Rango}s.
 *
 * <p>Al construirlo se unen los rangos solapados o contiguos
 * ({@link Rango#unir}), así que internamente quedan disjuntos y ordenados.
 * Eso permite responder si un valor pertenece al conjunto con búsqueda
 * binaria, y contar sus elementos sumando largos, aunque los rangos
 * tengan billones de valores (ej. 2025 día 5).</p>
 *
 * <p>Es inmutable.</p>
 *
 * <pre>{@code
 * ConjuntoRangos frescos = new ConjuntoRangos(List.of(new Rango(3, 5), new Rango(10, 14), new Rango(12, 18)));
 * frescos.contiene(17);   // true
 * frescos.cantidad();     // 12  (3..5 y 10..18)
 * frescos.rangos();       // [3-5, 10-18]
 * }</pre>
 */
public final class ConjuntoRangos {

    /** Rangos disjuntos, no contiguos y ordenados por inicio. */
    private final List<Rango> rangos;

    /**
     * Crea el conjunto con la unión de los rangos dados.
     *
     * @param rangos rangos en cualquier orden, pueden solaparse
     */
    public ConjuntoRangos(Collection<Rango> rangos) {
        this.rangos = List.copyOf(Rango.unir(rangos));
    }

    /**
     * Indica si un valor pertenece a alguno de los rangos.
     *
     * <p>Búsqueda binaria del último rango que empieza en o antes de
     * {@code valor}: como los rangos son disjuntos y ordenados, es el único
     * que puede contenerlo.</p>
     *
     * @param valor valor a buscar
     * @return {@code true} si el valor está en el conjunto
     */
    public boolean contiene(long valor) {
        int bajo = 0;
        int alto = rangos.size() - 1;
        int candidato = -1;             // último rango con inicio <= valor
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            if (rangos.get(medio).inicio() <= valor) {
                candidato = medio;
                bajo = medio + 1;
            } else {
                alto = medio - 1;
            }
        }
        return candidato >= 0 && rangos.get(candidato).contiene(valor);
    }

    /**
     * Cantidad de enteros distintos del conjunto.
     *
     * @return suma de los largos de los rangos unidos
     */
    public long cantidad() {
        long total = 0;
        for (Rango rango : rangos) total += rango.largo();
        return total;
    }

    /**
     * Rangos del conjunto, ya unidos.
     *
     * @return rangos disjuntos, no contiguos y ordenados por inicio
     */
    public List<Rango> rangos() {
        return rangos;
    }

    /** @return {@code true} si el conjunto no tiene elementos */
    public boolean estaVacio() {
        return rangos.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConjuntoRangos otro && rangos.equals(otro.rangos);
    }

    @Override
    public int hashCode() {
        return rangos.hashCode();
    }

    @Override
    public String toString() {
        return rangos.toString();
    }
}
