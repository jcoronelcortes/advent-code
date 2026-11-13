package advent.code;

import java.util.OptionalLong;
import java.util.function.LongPredicate;

/**
 * Búsqueda binaria sobre una condición monótona.
 *
 * <p>Sirve cuando hay que encontrar el <em>primer</em> valor que cumple algo,
 * y una vez que se cumple, se sigue cumpliendo para todos los valores
 * siguientes: la condición es {@code falso, …, falso, verdadero, …,
 * verdadero}. En vez de probar los valores uno por uno, se prueba el del
 * medio y se descarta la mitad que no puede contener la respuesta, así que
 * alcanza con unas {@code log₂(hasta − desde)} evaluaciones.</p>
 *
 * <p>Ejemplos: el primer byte que bloquea un camino (2024 día 18: con más
 * obstáculos nunca vuelve a haber camino), o el menor tiempo de carga con el
 * que un bote supera un récord (2023 día 6: en la mitad creciente de una
 * parábola).</p>
 */
public final class Busqueda {

    private Busqueda() {}

    /**
     * El menor valor de {@code [desde, hasta]} que cumple la condición.
     *
     * <p>La condición tiene que ser <b>monótona</b> en el intervalo: si se
     * cumple para un valor, se cumple para todos los mayores. Si no lo es, el
     * resultado es un valor cualquiera en el que la condición cambia de falso
     * a verdadero, no necesariamente el primero.</p>
     *
     * @param desde     primer valor del intervalo
     * @param hasta     último valor del intervalo (incluido)
     * @param condicion la condición, monótona en el intervalo
     * @return el menor valor que la cumple, o vacío si ninguno la cumple
     *         (o si el intervalo está vacío)
     */
    public static OptionalLong primero(long desde, long hasta, LongPredicate condicion) {
        if (desde > hasta || !condicion.test(hasta)) return OptionalLong.empty();
        if (condicion.test(desde)) return OptionalLong.of(desde);
        long noCumple = desde;   // invariante: condicion(noCumple) es falsa
        long cumple = hasta;     // invariante: condicion(cumple) es verdadera
        // cumple > noCumple siempre, así que su diferencia, leída sin signo, es
        // exacta aunque el intervalo abarque todos los long (y la resta común desborde)
        while (Long.compareUnsigned(cumple - noCumple, 1) > 0) {
            long medio = noCumple + ((cumple - noCumple) >>> 1);
            if (condicion.test(medio)) cumple = medio;
            else noCumple = medio;
        }
        return OptionalLong.of(cumple);
    }
}
