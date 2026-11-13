package advent.code.y2025;

import advent.code.BFS;
import advent.code.Resolvedor;
import advent.code.SistemaLineal;
import advent.code.Utiles;

import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2025 — Día 10: Fábrica
 *
 * <p>Cada línea del input describe una máquina:</p>
 * <pre>
 * [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
 * </pre>
 * <ul>
 *   <li>{@code [.##.]}: diagrama de luces indicadoras ({@code #} encendida);
 *       todas empiezan apagadas.</li>
 *   <li>{@code (1,3)}: un botón y las luces que alterna (índices desde 0).</li>
 *   <li>{@code {3,5,4,7}}: requisitos de voltaje (no se usan en la parte 1).</li>
 * </ul>
 */
public class Dia10 implements Resolvedor {

    /**
     * Parte 1: suma, sobre todas las máquinas, de la menor cantidad de
     * pulsaciones que deja las luces como en el diagrama.
     *
     * @param lineas una máquina por línea
     * @return total de pulsaciones
     */
    @Override
    public String parte1(List<String> lineas) {
        long total = 0;
        for (Maquina maquina : leerMaquinas(lineas)) {
            total += maquina.minimasPulsacionesLuces();
        }
        return String.valueOf(total);
    }

    /**
     * Parte 2: suma, sobre todas las máquinas, de la menor cantidad de
     * pulsaciones que deja los contadores de voltaje exactamente en los
     * requisitos. Cada pulsación suma 1 a los contadores de su botón.
     *
     * @param lineas una máquina por línea
     * @return total de pulsaciones
     */
    @Override
    public String parte2(List<String> lineas) {
        long total = 0;
        for (Maquina maquina : leerMaquinas(lineas)) {
            total = Math.addExact(total, maquina.minimasPulsacionesVoltaje());
        }
        return String.valueOf(total);
    }

    /**
     * Una máquina del manual.
     *
     * <p>Las luces y los botones se representan como <b>máscaras de bits</b>:
     * el bit {@code i} corresponde a la luz {@code i}. Apretar un botón
     * alterna sus luces, que es un XOR del estado con la máscara del botón.</p>
     *
     * @param cantidadLuces cantidad de luces indicadoras
     * @param objetivo      máscara de las luces que deben quedar encendidas
     * @param botones       luces que alterna cada botón (índices desde 0)
     * @param voltajes      requisitos de voltaje de cada contador
     */
    record Maquina(int cantidadLuces, int objetivo, List<List<Integer>> botones, List<Integer> voltajes) {

        /**
         * Máscara de bits de cada botón.
         *
         * @return una máscara por botón
         */
        List<Integer> mascaras() {
            return botones.stream()
                    .map(luces -> luces.stream().mapToInt(luz -> 1 << luz).reduce(0, (a, b) -> a | b))
                    .toList();
        }

        /**
         * Menor cantidad de pulsaciones para pasar de todas las luces apagadas
         * al diagrama.
         *
         * <p>BFS sobre los estados de las luces: cada estado es una máscara
         * (como mucho {@code 2^luces} estados) y sus vecinos son los estados que
         * se obtienen apretando un botón. La BFS encuentra el camino con menos
         * pasos, es decir, la menor cantidad de pulsaciones.</p>
         *
         * @return la menor cantidad de pulsaciones
         * @throws IllegalStateException si ninguna combinación logra el diagrama
         */
        long minimasPulsacionesLuces() {
            List<Integer> mascaras = mascaras();
            return BFS.buscar(0,
                            estado -> estado == objetivo,
                            estado -> mascaras.stream().map(boton -> estado ^ boton).toList())
                    .orElseThrow(() -> new IllegalStateException("Ninguna combinación de botones logra " + this));
        }

        /**
         * Menor cantidad de pulsaciones para que los contadores lleguen
         * exactamente a los voltajes requeridos.
         *
         * <p>Es un problema de <b>programación lineal entera</b>: si
         * {@code x_j} es la cantidad de veces que se aprieta el botón
         * {@code j}, cada contador {@code i} da una ecuación
         * {@code Σ x_j (botones j que tocan i) = voltaje_i}, con
         * {@code x_j ≥ 0} entero, y se minimiza {@code Σ x_j}. Se resuelve
         * con {@link SistemaBotones}.</p>
         *
         * @return la menor cantidad de pulsaciones
         * @throws IllegalStateException si no hay ninguna solución
         */
        long minimasPulsacionesVoltaje() {
            return new SistemaBotones(this).minimo()
                    .orElseThrow(() -> new IllegalStateException("Ninguna combinación de botones logra " + voltajes));
        }
    }

    /**
     * Sistema {@code A·x = b} de una máquina (una fila por contador, una
     * columna por botón) resuelto por <b>eliminación gaussiana</b> más
     * enumeración de las variables libres.
     *
     * <p><b>Idea:</b> las ecuaciones son pocas (hasta 10) y los botones
     * también (hasta 13). La eliminación gaussiana deja cada variable
     * <em>pivote</em> en función de las <em>libres</em> (las que sobran
     * cuando hay más botones que ecuaciones independientes; en el input
     * real son como mucho 3). Basta con probar todos los valores posibles de
     * las libres: cada pivote queda determinado, y la combinación sirve si
     * todos los pivotes son enteros y no negativos.</p>
     *
     * <p>Cada variable libre está acotada: un botón no se puede apretar más
     * veces que el menor voltaje de los contadores que toca, porque los
     * contadores nunca bajan.</p>
     *
     * <p>La eliminación usa fracciones exactas ({@link SistemaLineal}, con
     * {@link BigFraction}) para no
     * perder precisión; después cada pivote se reescribe con enteros como
     * {@code x_p = (R − Σ C_f·x_f) / D}, para que la enumeración, que es la
     * parte costosa, use solo aritmética {@code long}.</p>
     */
    static final class SistemaBotones {

        private final int botones;
        private final int[] cotas;            // máximo de pulsaciones posible de cada botón
        private final int[] libres;           // índices de los botones libres
        private final int[] pivotes;          // índice del botón pivote de cada fila reducida
        private final long[] constante;       // R de cada pivote
        private final long[][] coeficiente;   // C_f de cada pivote, por variable libre
        private final long[] denominador;     // D de cada pivote (positivo)
        private final boolean inconsistente;

        SistemaBotones(Maquina maquina) {
            List<Integer> voltajes = maquina.voltajes();
            int contadores = voltajes.size();
            botones = maquina.botones().size();

            // Matriz aumentada [A | b]
            BigFraction[][] matriz = new BigFraction[contadores][botones + 1];
            for (int i = 0; i < contadores; i++) {
                for (int j = 0; j < botones; j++) {
                    matriz[i][j] = maquina.botones().get(j).contains(i) ? BigFraction.ONE : BigFraction.ZERO;
                }
                matriz[i][botones] = new BigFraction(voltajes.get(i));
            }

            cotas = new int[botones];
            for (int j = 0; j < botones; j++) {
                cotas[j] = maquina.botones().get(j).stream().mapToInt(voltajes::get).min().orElse(0);
            }

            // Forma escalonada reducida
            SistemaLineal.Escalonada escalonada = SistemaLineal.escalonar(matriz);
            inconsistente = !escalonada.consistente();
            pivotes = escalonada.pivotes();
            libres = escalonada.libres();
            matriz = escalonada.matriz();

            // x_p = b'_p − Σ a'_pf · x_f  →  multiplicar por el mcm de los denominadores
            constante = new long[pivotes.length];
            coeficiente = new long[pivotes.length][libres.length];
            denominador = new long[pivotes.length];
            for (int p = 0; p < pivotes.length; p++) {
                BigInteger mcm = matriz[p][botones].getDenominator();
                for (int libre : libres) mcm = mcm(mcm, matriz[p][libre].getDenominator());
                BigFraction factor = new BigFraction(mcm);
                denominador[p] = mcm.longValueExact();
                constante[p] = matriz[p][botones].multiply(factor).getNumerator().longValueExact();
                for (int f = 0; f < libres.length; f++) {
                    coeficiente[p][f] = matriz[p][libres[f]].multiply(factor).getNumerator().longValueExact();
                }
            }
        }

        private static BigInteger mcm(BigInteger a, BigInteger b) {
            return a.divide(a.gcd(b)).multiply(b);
        }

        /** @return cantidad de variables libres del sistema */
        int cantidadLibres() {
            return libres.length;
        }

        /**
         * Menor {@code Σ x} entre todas las soluciones enteras no negativas.
         *
         * @return el mínimo, o vacío si no hay solución
         */
        OptionalLong minimo() {
            if (inconsistente) return OptionalLong.empty();
            long[] mejor = {Long.MAX_VALUE};
            enumerar(0, new long[libres.length], 0, mejor);
            return mejor[0] == Long.MAX_VALUE ? OptionalLong.empty() : OptionalLong.of(mejor[0]);
        }

        /**
         * Prueba recursivamente todos los valores de las variables libres
         * (dentro de sus cotas). Poda: si la suma de las libres ya alcanza al
         * mejor total encontrado, valores mayores no pueden mejorarlo.
         */
        private void enumerar(int indice, long[] valores, long sumaLibres, long[] mejor) {
            if (sumaLibres >= mejor[0]) return;
            if (indice < libres.length) {
                for (long valor = 0; valor <= cotas[libres[indice]]; valor++) {
                    valores[indice] = valor;
                    enumerar(indice + 1, valores, sumaLibres + valor, mejor);
                }
                return;
            }
            long total = sumaLibres;
            for (int p = 0; p < pivotes.length; p++) {
                long numerador = constante[p];
                for (int f = 0; f < libres.length; f++) numerador -= coeficiente[p][f] * valores[f];
                if (numerador < 0 || numerador % denominador[p] != 0) return;   // negativo o no entero
                total += numerador / denominador[p];
                if (total >= mejor[0]) return;
            }
            mejor[0] = total;
        }
    }

    private static final Pattern DIAGRAMA = Pattern.compile("\\[([.#]*)]");
    private static final Pattern BOTON = Pattern.compile("\\(([\\d,\\s]*)\\)");
    private static final Pattern VOLTAJES = Pattern.compile("\\{([\\d,\\s]*)}");

    /**
     * Convierte el input en máquinas, ignorando las líneas en blanco.
     *
     * @param lineas una máquina por línea
     * @return máquinas en el orden del input
     */
    static List<Maquina> leerMaquinas(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Dia10::leerMaquina).toList();
    }

    /**
     * Interpreta una línea {@code [diagrama] (botón) ... {voltajes}}.
     *
     * @param linea descripción de una máquina
     * @return la máquina
     */
    static Maquina leerMaquina(String linea) {
        Matcher diagrama = DIAGRAMA.matcher(linea);
        if (!diagrama.find()) throw new IllegalArgumentException("Falta el diagrama de luces: " + linea);
        String luces = diagrama.group(1);
        int objetivo = 0;
        for (int i = 0; i < luces.length(); i++) {
            if (luces.charAt(i) == '#') objetivo |= 1 << i;
        }

        List<List<Integer>> botones = new ArrayList<>();
        Matcher boton = BOTON.matcher(linea);
        while (boton.find()) botones.add(Utiles.leerNumeros(boton.group(1)));

        Matcher voltajes = VOLTAJES.matcher(linea);
        List<Integer> requisitos = voltajes.find() ? Utiles.leerNumeros(voltajes.group(1)) : List.of();

        return new Maquina(luces.length(), objetivo, botones, requisitos);
    }
}
