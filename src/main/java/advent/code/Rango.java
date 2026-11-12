package advent.code;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Intervalo cerrado de enteros {@code [inicio, fin]} (ambos extremos incluidos).
 *
 * <p>Los rangos aparecen seguido en Advent of Code: IDs (2025 día 2),
 * semillas (2023 día 5), IPs bloqueadas (2016 día 20), cobertura de
 * sensores (2022 día 15), etc.</p>
 *
 * @param inicio primer valor (inclusivo)
 * @param fin    último valor (inclusivo), mayor o igual a {@code inicio}
 */
public record Rango(long inicio, long fin) implements Comparable<Rango> {

    /** Ordena por inicio y, en caso de empate, por fin. */
    private static final Comparator<Rango> ORDEN =
            Comparator.comparingLong(Rango::inicio).thenComparingLong(Rango::fin);

    public Rango {
        if (inicio > fin) {
            throw new IllegalArgumentException("Rango inválido: " + inicio + " > " + fin);
        }
    }

    /**
     * Crea un rango a partir de un texto {@code "inicio-fin"} (ej. {@code "95-115"}).
     * Admite espacios alrededor de los números.
     *
     * @param texto rango en formato {@code inicio-fin}
     * @return el rango correspondiente
     * @throws IllegalArgumentException si el texto no tiene el formato esperado
     */
    public static Rango parsear(String texto) {
        String[] extremos = texto.trim().split("\\s*-\\s*");
        if (extremos.length != 2 || !NumberUtils.isDigits(extremos[0]) || !NumberUtils.isDigits(extremos[1])) {
            throw new IllegalArgumentException("Formato de rango inválido: '" + texto + "'");
        }
        return new Rango(Long.parseLong(extremos[0]), Long.parseLong(extremos[1]));
    }

    /**
     * Crea un rango a partir de su inicio y su largo (cantidad de elementos).
     * Útil cuando el input viene como "inicio largo" (ej. 2023 día 5).
     *
     * @param inicio primer valor
     * @param largo  cantidad de elementos (positiva)
     * @return el rango {@code [inicio, inicio + largo - 1]}
     */
    public static Rango desdeLargo(long inicio, long largo) {
        if (largo <= 0) throw new IllegalArgumentException("El largo debe ser positivo: " + largo);
        return new Rango(inicio, inicio + largo - 1);
    }

    /**
     * Cantidad de enteros contenidos en el rango.
     *
     * @return {@code fin - inicio + 1}
     */
    public long largo() {
        return fin - inicio + 1;
    }

    /**
     * Indica si un valor pertenece al rango.
     *
     * @param valor valor a verificar
     * @return {@code true} si {@code inicio ≤ valor ≤ fin}
     */
    public boolean contiene(long valor) {
        return inicio <= valor && valor <= fin;
    }

    /**
     * Indica si otro rango está completamente contenido en este.
     *
     * @param otro rango a verificar
     * @return {@code true} si {@code otro} ⊆ {@code this}
     */
    public boolean contiene(Rango otro) {
        return inicio <= otro.inicio && otro.fin <= fin;
    }

    /**
     * Indica si dos rangos comparten al menos un valor.
     *
     * @param otro otro rango
     * @return {@code true} si se solapan
     */
    public boolean intersecta(Rango otro) {
        return inicio <= otro.fin && otro.inicio <= fin;
    }

    /**
     * Calcula la intersección con otro rango.
     *
     * @param otro otro rango
     * @return el rango común, o vacío si no se solapan
     */
    public Optional<Rango> interseccion(Rango otro) {
        if (!intersecta(otro)) return Optional.empty();
        return Optional.of(new Rango(Math.max(inicio, otro.inicio), Math.min(fin, otro.fin)));
    }

    /**
     * Calcula las partes de este rango que <em>no</em> están en {@code otro}.
     * Puede devolver 0, 1 o 2 rangos.
     *
     * <p>Ejemplo: {@code [1, 10] − [4, 6] = [[1, 3], [7, 10]]}.</p>
     *
     * @param otro rango a quitar
     * @return lista de rangos resultantes, ordenada
     */
    public List<Rango> restar(Rango otro) {
        if (!intersecta(otro)) return List.of(this);

        List<Rango> resultado = new ArrayList<>(2);
        if (inicio < otro.inicio) resultado.add(new Rango(inicio, otro.inicio - 1));
        if (otro.fin < fin) resultado.add(new Rango(otro.fin + 1, fin));
        return resultado;
    }

    /**
     * Desplaza el rango sumando {@code delta} a ambos extremos.
     *
     * @param delta cantidad a sumar (puede ser negativa)
     * @return el rango desplazado
     */
    public Rango desplazar(long delta) {
        return new Rango(inicio + delta, fin + delta);
    }

    /**
     * Une una colección de rangos fusionando los que se solapan o son
     * contiguos (ej. {@code [1, 3]} y {@code [4, 6]} se fusionan en {@code [1, 6]}).
     *
     * @param rangos rangos en cualquier orden
     * @return rangos disjuntos, no contiguos y ordenados por inicio
     */
    public static List<Rango> unir(Collection<Rango> rangos) {
        List<Rango> ordenados = new ArrayList<>(rangos);
        ordenados.sort(ORDEN);

        List<Rango> resultado = new ArrayList<>();
        for (Rango rango : ordenados) {
            if (!resultado.isEmpty()) {
                Rango ultimo = resultado.getLast();
                // contiguo o solapado (se evita fin + 1 para no desbordar con Long.MAX_VALUE)
                if (rango.inicio - 1 <= ultimo.fin) {
                    resultado.set(resultado.size() - 1, new Rango(ultimo.inicio, Math.max(ultimo.fin, rango.fin)));
                    continue;
                }
            }
            resultado.add(rango);
        }
        return resultado;
    }

    @Override
    public int compareTo(Rango otro) {
        return ORDEN.compare(this, otro);
    }

    @Override
    public String toString() {
        return inicio + "-" + fin;
    }
}
