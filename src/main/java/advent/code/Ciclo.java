package advent.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Detección de ciclos en una secuencia de estados determinista.
 *
 * <p>Muchos problemas avanzan un estado paso a paso con una regla fija
 * ({@code estado → siguiente(estado)}) y piden el estado después de una
 * cantidad enorme de pasos (ej. mil millones). Si los estados posibles son
 * finitos, tarde o temprano uno se repite, y desde ahí la secuencia da vueltas
 * para siempre:</p>
 *
 * <pre>
 *   e₀ → e₁ → … → e_inicio → … → e_(inicio+largo−1) ─┐
 *                    ↑                                │
 *                    └────────────────────────────────┘
 * </pre>
 *
 * <p>{@link #detectar} avanza hasta encontrar la primera repetición y guarda
 * los estados recorridos; con eso, {@link #estadoEn} da el estado en cualquier
 * paso sin simularlo (ej. 2023 día 8: dónde está cada fantasma en el paso
 * 13 billones).</p>
 *
 * <p>Los estados tienen que implementar {@code equals} y {@code hashCode}
 * (un {@code record} sirve) y ser inmutables, porque se guardan.</p>
 *
 * @param estados los estados de los pasos {@code 0} a {@code inicio + largo − 1}, todos distintos
 * @param inicio  el primer paso que forma parte del ciclo
 * @param largo   cada cuántos pasos se repite el ciclo (al menos 1)
 * @param <E>     el tipo del estado
 */
public record Ciclo<E>(List<E> estados, int inicio, int largo) {

    /**
     * Avanza desde {@code inicial} hasta que un estado se repite.
     *
     * <p>Guarda cada estado con el paso en que apareció; el primer estado
     * que ya estaba es el comienzo del ciclo. Usa memoria proporcional a la
     * cantidad de pasos hasta la repetición, que puede ser tan grande como
     * la cantidad de estados posibles: si son infinitos y nunca se repiten,
     * no termina.</p>
     *
     * @param inicial   el estado del paso 0
     * @param siguiente la regla para avanzar un paso
     * @param <E>       el tipo del estado
     * @return el ciclo encontrado
     */
    public static <E> Ciclo<E> detectar(E inicial, UnaryOperator<E> siguiente) {
        Map<E, Integer> pasoDe = new HashMap<>();
        List<E> estados = new ArrayList<>();
        E estado = inicial;
        while (!pasoDe.containsKey(estado)) {
            pasoDe.put(estado, estados.size());
            estados.add(estado);
            estado = siguiente.apply(estado);
        }
        int inicio = pasoDe.get(estado);
        return new Ciclo<>(List.copyOf(estados), inicio, estados.size() - inicio);
    }

    /**
     * El estado después de {@code paso} pasos, sin simularlos: antes del
     * ciclo se busca directamente; después, se descuentan vueltas enteras.
     *
     * @param paso la cantidad de pasos desde el estado inicial (puede ser enorme)
     * @return el estado en ese paso
     * @throws IllegalArgumentException si el paso es negativo
     */
    public E estadoEn(long paso) {
        if (paso < 0) throw new IllegalArgumentException("El paso no puede ser negativo: " + paso);
        if (paso < estados.size()) return estados.get((int) paso);
        return estados.get(inicio + (int) ((paso - inicio) % largo));
    }
}
