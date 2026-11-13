package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * Año 2024 — Día 17: Computadora Cronoespacial
 *
 * <p>El input describe una computadora de 3 bits: los valores iniciales de
 * sus tres registros ({@code A}, {@code B} y {@code C}, que pueden guardar
 * cualquier entero) y su programa, una lista de números del 0 al 7. El
 * programa se lee de a pares: código de instrucción y operando. Hay ocho
 * instrucciones (divisiones por potencias de 2, XOR, módulo 8, un salto
 * condicional y una que emite un valor a la salida). En la parte 2 hay que
 * encontrar el valor inicial de {@code A} con el que el programa se emite a
 * sí mismo.</p>
 */
public class Dia17 implements Resolvedor {

    /**
     * Parte 1: lo que emite el programa con los registros del input, separado
     * por comas.
     *
     * @param lineas los registros y el programa
     * @return la salida unida con comas
     */
    @Override
    public String parte1(List<String> lineas) {
        Computadora computadora = Computadora.parsear(lineas);
        return unir(computadora.ejecutar());
    }

    /**
     * Parte 2: el menor valor inicial positivo de {@code A} con el que el
     * programa emite una copia exacta de sí mismo.
     *
     * @param lineas los registros y el programa ({@code A} se ignora)
     * @return el valor de {@code A}
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(menorAQueSeCopia(Computadora.parsear(lineas)));
    }

    /**
     * El menor {@code A} positivo con el que el programa se emite a sí mismo.
     *
     * <p>Supone la forma de los programas de este problema: un solo ciclo que
     * en cada vuelta emite un valor, divide {@code A} por 8 ({@code adv 3}) y
     * vuelve al inicio mientras {@code A} no sea 0. Entonces lo que emite
     * {@code A} es el valor de la primera vuelta seguido de lo que emite
     * {@code A / 8}. Por eso, si {@code A} emite los últimos k números del
     * programa, {@code A / 8} emite los últimos k − 1.</p>
     *
     * <p>Se arma {@code A} de a 3 bits, desde los más altos: a cada valor que
     * ya emite el final del programa se le agrega un dígito en base 8 (del
     * menor al mayor) y se ejecuta el programa para ver si ahora emite un
     * número más del final. Cada candidato se comprueba ejecutando el programa
     * completo, así que la forma supuesta solo afecta qué se prueba, nunca da
     * una respuesta falsa. Como los dígitos se prueban en orden y todos los
     * valores completos tienen la misma cantidad de dígitos, el primero que
     * emite el programa entero es el menor.</p>
     *
     * @param computadora la computadora con su programa
     * @return el menor {@code A} positivo
     * @throws IllegalArgumentException si ningún {@code A} de esa forma sirve
     */
    static long menorAQueSeCopia(Computadora computadora) {
        List<Integer> programa = computadora.programa();
        return buscar(computadora, programa, 0, programa.size() - 1)
                .orElseThrow(() -> new IllegalArgumentException("Ningún valor de A hace que el programa se copie"));
    }

    /**
     * Búsqueda en profundidad: {@code prefijo} ya emite {@code programa}
     * desde la posición {@code desde + 1}; se le agrega un dígito en base 8
     * para emitir también la posición {@code desde}.
     */
    private static OptionalLong buscar(Computadora computadora, List<Integer> programa, long prefijo, int desde) {
        if (desde < 0) return prefijo > 0 ? OptionalLong.of(prefijo) : OptionalLong.empty();
        List<Integer> esperado = programa.subList(desde, programa.size());
        for (int digito = 0; digito < 8; digito++) {
            long candidato = prefijo * 8 + digito;
            if (computadora.conA(candidato).ejecutar().equals(esperado)) {
                OptionalLong encontrado = buscar(computadora, programa, candidato, desde - 1);
                if (encontrado.isPresent()) return encontrado;
            }
        }
        return OptionalLong.empty();
    }

    /** Une valores con comas, como pide el enunciado. */
    static String unir(List<Integer> valores) {
        return valores.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * La computadora: tres registros y un programa.
     *
     * <p>Los registros son {@code long} y no se limitan a 3 bits. El estado
     * queda accesible después de {@link #ejecutar()} para poder comprobar los
     * ejemplos del enunciado que hablan de cómo terminan los registros.</p>
     */
    static final class Computadora {

        static final int ADV = 0;
        static final int BXL = 1;
        static final int BST = 2;
        static final int JNZ = 3;
        static final int BXC = 4;
        static final int OUT = 5;
        static final int BDV = 6;
        static final int CDV = 7;

        private long a;
        private long b;
        private long c;
        private final int[] programa;

        Computadora(long a, long b, long c, List<Integer> programa) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.programa = programa.stream().mapToInt(Integer::intValue).toArray();
        }

        /**
         * Lee el input: tres líneas de registros, una línea en blanco y el programa.
         *
         * @param lineas el input
         * @return la computadora lista para ejecutar
         */
        static Computadora parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas, 2);
            List<Long> registros = Utiles.leerNumerosLargos(secciones.get(0), 3);
            List<Integer> programa = Utiles.leerNumeros(String.join(",", secciones.get(1)));
            return new Computadora(registros.get(0), registros.get(1), registros.get(2), programa);
        }

        long a() { return a; }

        long b() { return b; }

        long c() { return c; }

        List<Integer> programa() { return Arrays.stream(programa).boxed().toList(); }

        /** Una computadora nueva con el mismo programa y {@code B}, {@code C}, pero otro {@code A}. */
        Computadora conA(long nuevoA) {
            return new Computadora(nuevoA, b, c, programa());
        }

        /**
         * Ejecuta el programa hasta que el puntero de instrucción se sale del
         * programa.
         *
         * @return los valores emitidos por {@code out}, en orden
         */
        List<Integer> ejecutar() {
            List<Integer> salida = new ArrayList<>();
            int puntero = 0;
            while (puntero + 1 < programa.length) {
                int codigo = programa[puntero];
                int operando = programa[puntero + 1];
                puntero += 2;
                switch (codigo) {
                    case ADV -> a = dividir(combo(operando));
                    case BXL -> b ^= operando;
                    case BST -> b = combo(operando) & 7;
                    case JNZ -> {
                        if (a != 0) puntero = operando;
                    }
                    case BXC -> b ^= c;
                    case OUT -> salida.add((int) (combo(operando) & 7));
                    case BDV -> b = dividir(combo(operando));
                    case CDV -> c = dividir(combo(operando));
                    default -> throw new IllegalStateException("Instrucción inválida: " + codigo);
                }
            }
            return salida;
        }

        /**
         * {@code A / 2^exponente}, truncado. Como {@code A} no es negativo, es
         * un corrimiento a la derecha; con un exponente de 63 o más el
         * resultado es 0 (en Java, {@code >>} solo usa los 6 bits bajos del
         * corrimiento, así que hay que cubrir ese caso aparte).
         */
        private long dividir(long exponente) {
            if (a < 0) throw new IllegalStateException("El registro A es negativo: " + a);
            return exponente >= Long.SIZE - 1 ? 0 : a >> exponente;
        }

        /** Valor de un operando combo: 0–3 literales, 4–6 los registros A, B y C. */
        private long combo(int operando) {
            return switch (operando) {
                case 0, 1, 2, 3 -> operando;
                case 4 -> a;
                case 5 -> b;
                case 6 -> c;
                default -> throw new IllegalStateException("Operando combo inválido: " + operando);
            };
        }
    }
}
