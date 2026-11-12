package advent.code.y2024;

import advent.code.Matematicas;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.EnumSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

/**
 * Año 2024 — Día 7: Reparación del Puente
 *
 * <p>Cada línea es una ecuación de calibración a la que le faltan los
 * operadores: {@code 3267: 81 40 27} tiene un valor de prueba (3267) y los
 * números en orden. Los operadores se evalúan siempre de izquierda a derecha,
 * sin precedencia, y los números no se pueden reordenar.</p>
 */
public class Dia07 implements Resolvedor {

    /**
     * Parte 1: suma de los valores de prueba de las ecuaciones que se pueden
     * cumplir con {@code +} y {@code *}.
     *
     * @param lineas una ecuación por línea
     * @return el resultado total de calibración
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(resultadoDeCalibracion(lineas, EnumSet.of(Operador.SUMA, Operador.PRODUCTO)));
    }

    /**
     * Parte 2: igual que la parte 1, pero con un tercer operador, la
     * concatenación ({@code ||}).
     *
     * @param lineas una ecuación por línea
     * @return el resultado total de calibración
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(resultadoDeCalibracion(lineas, EnumSet.allOf(Operador.class)));
    }

    /**
     * Operador que se puede poner entre dos números.
     *
     * <p>Cada operador sabe <b>deshacerse</b>: dado el resultado de
     * {@code anterior op operando} y el {@code operando}, calcula qué valor
     * tenía que tener {@code anterior}, si es que alguno sirve. Así la búsqueda
     * puede ir de atrás hacia adelante.</p>
     */
    enum Operador {
        SUMA {
            @Override
            OptionalLong deshacer(long resultado, long operando) {
                // Los números son positivos: lo anterior también tiene que serlo
                return resultado > operando ? OptionalLong.of(resultado - operando) : OptionalLong.empty();
            }
        },
        PRODUCTO {
            @Override
            OptionalLong deshacer(long resultado, long operando) {
                return resultado % operando == 0 ? OptionalLong.of(resultado / operando) : OptionalLong.empty();
            }
        },
        /**
         * Concatenación: pega los dígitos ({@code 12 || 345 = 12345}). Para
         * deshacerla, el resultado tiene que terminar en los dígitos del
         * operando, y lo anterior es lo que queda al quitarlos
         * ({@code 156 = 15 || 6}, así que lo anterior es 15).
         */
        CONCATENACION {
            @Override
            OptionalLong deshacer(long resultado, long operando) {
                long potencia = Matematicas.potenciaDeDiez(Matematicas.cantidadDigitos(operando));
                boolean terminaEnOperando = resultado % potencia == operando;
                // resultado > operando: si fueran iguales, lo anterior sería 0 (y los números son positivos)
                return terminaEnOperando && resultado > operando
                        ? OptionalLong.of(resultado / potencia)
                        : OptionalLong.empty();
            }
        };

        /**
         * @param resultado valor de {@code anterior op operando}
         * @param operando  el número de la derecha
         * @return el valor que tenía que tener {@code anterior}, o vacío si ninguno sirve
         */
        abstract OptionalLong deshacer(long resultado, long operando);
    }

    /**
     * Una ecuación de calibración.
     *
     * @param valor   valor de prueba (antes de los dos puntos)
     * @param numeros números en orden (positivos)
     */
    record Ecuacion(long valor, List<Long> numeros) {

        /**
         * Indica si poniendo operadores entre los números se obtiene el valor.
         *
         * @param operadores operadores disponibles
         * @return {@code true} si alguna combinación da el valor de prueba
         */
        boolean esPosible(Set<Operador> operadores) {
            return sePuedeObtener(valor, numeros.size() - 1, operadores);
        }

        /**
         * ¿Se puede obtener {@code objetivo} con los números {@code 0..ultimo}?
         *
         * <p>Como se evalúa de izquierda a derecha, el último número es el
         * último operando: {@code objetivo = (lo anterior) op numeros[ultimo]}.
         * Para cada operador se calcula qué tenía que valer "lo anterior" y se
         * sigue con un número menos. Muchas ramas se cortan enseguida (una
         * resta que no da positivo, una división que no es exacta), por eso
         * es mucho más rápido que probar todas las combinaciones hacia
         * adelante.</p>
         */
        private boolean sePuedeObtener(long objetivo, int ultimo, Set<Operador> operadores) {
            if (ultimo == 0) return objetivo == numeros.getFirst();
            long operando = numeros.get(ultimo);
            for (Operador operador : operadores) {
                OptionalLong anterior = operador.deshacer(objetivo, operando);
                if (anterior.isPresent() && sePuedeObtener(anterior.getAsLong(), ultimo - 1, operadores)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Suma los valores de prueba de las ecuaciones posibles.
     *
     * @param lineas     una ecuación por línea
     * @param operadores operadores disponibles
     * @return el resultado total de calibración
     */
    static long resultadoDeCalibracion(List<String> lineas, Set<Operador> operadores) {
        return leerEcuaciones(lineas).stream()
                .filter(ecuacion -> ecuacion.esPosible(operadores))
                .mapToLong(Ecuacion::valor)
                .sum();
    }

    /**
     * Convierte el input en ecuaciones: el primer número de cada línea es el
     * valor de prueba y el resto son los números.
     *
     * @param lineas una ecuación por línea
     * @return las ecuaciones
     */
    static List<Ecuacion> leerEcuaciones(List<String> lineas) {
        return Utiles.numerosLargosPorLinea(lineas).stream()
                .map(numeros -> new Ecuacion(numeros.getFirst(), numeros.subList(1, numeros.size())))
                .toList();
    }
}
