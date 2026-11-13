package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.LongBinaryOperator;

/**
 * Año 2024 — Día 24: Cables Cruzados
 *
 * <p>El input describe un circuito de compuertas lógicas: primero el valor
 * inicial de algunos cables ({@code x00: 1}) y, después de una línea en
 * blanco, las compuertas ({@code x00 AND y00 -> z00}). Cada compuerta
 * ({@code AND}, {@code OR} o {@code XOR}) tiene dos entradas y una salida, y
 * no hay ciclos. Los cables que empiezan con {@code z} forman un número en
 * binario, con {@code z00} como bit menos significativo. En la parte 2 el
 * circuito debería sumar los números de los cables {@code x} e {@code y},
 * pero a cuatro pares de compuertas les intercambiaron las salidas.</p>
 */
public class Dia24 implements Resolvedor {

    /**
     * Parte 1: el número que forman los cables {@code z} al simular el
     * circuito.
     *
     * @param lineas los valores iniciales y las compuertas
     * @return el número en decimal
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(Circuito.parsear(lineas).numero('z'));
    }

    /** Pares de compuertas con las salidas intercambiadas en el input real. */
    static final int PARES_CAMBIADOS = 4;

    /**
     * Parte 2: los ocho cables de salida intercambiados, ordenados y
     * separados por comas, para que el circuito sume {@code x + y}.
     *
     * @param lineas los valores iniciales y las compuertas
     * @return los cables
     */
    @Override
    public String parte2(List<String> lineas) {
        Reparacion reparacion = new Reparacion(Circuito.parsear(lineas), Long::sum);
        return String.join(",", reparacion.cablesIntercambiados(PARES_CAMBIADOS));
    }

    /** Las tres operaciones de las compuertas. */
    enum Operacion {
        AND, OR, XOR;

        boolean aplicar(boolean a, boolean b) {
            return switch (this) {
                case AND -> a && b;
                case OR -> a || b;
                case XOR -> a ^ b;
            };
        }
    }

    /**
     * Una compuerta: {@code entrada1 OPERACION entrada2 -> salida}.
     *
     * @param entrada1  un cable de entrada
     * @param operacion la operación
     * @param entrada2  el otro cable de entrada
     * @param salida    el cable de salida
     */
    record Compuerta(String entrada1, Operacion operacion, String entrada2, String salida) {

        /**
         * Lee una compuerta como {@code x00 AND y00 -> z00}.
         *
         * @param linea la línea
         * @return la compuerta
         * @throws IllegalArgumentException si la línea no tiene esa forma
         */
        static Compuerta parsear(String linea) {
            String[] partes = linea.strip().split("\\s+");
            if (partes.length != 5 || !partes[3].equals("->")) {
                throw new IllegalArgumentException("Compuerta inválida: '" + linea + "'");
            }
            return new Compuerta(partes[0], Operacion.valueOf(partes[1]), partes[2], partes[4]);
        }
    }

    /**
     * El circuito: los valores iniciales y la compuerta que escribe en cada cable.
     *
     * @param iniciales  cable → valor inicial
     * @param compuertas cable de salida → la compuerta que lo escribe
     */
    record Circuito(Map<String, Boolean> iniciales, Map<String, Compuerta> compuertas) {

        /**
         * Lee el input: valores iniciales, una línea en blanco y las compuertas.
         *
         * @param lineas el input
         * @return el circuito
         * @throws IllegalArgumentException si dos compuertas escriben en el mismo cable
         */
        static Circuito parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas, 2);
            Map<String, Boolean> iniciales = new HashMap<>();
            for (String linea : secciones.get(0)) {
                String[] partes = linea.split(":");
                iniciales.put(partes[0].strip(), partes[1].strip().equals("1"));
            }
            Map<String, Compuerta> compuertas = new HashMap<>();
            for (String linea : secciones.get(1)) {
                Compuerta compuerta = Compuerta.parsear(linea);
                if (compuertas.put(compuerta.salida(), compuerta) != null) {
                    throw new IllegalArgumentException("Dos compuertas escriben en " + compuerta.salida());
                }
            }
            return new Circuito(Map.copyOf(iniciales), Map.copyOf(compuertas));
        }

        /**
         * Los valores de todos los cables una vez que el circuito se estabiliza.
         *
         * <p>Cada cable se evalúa de forma recursiva: si tiene valor inicial,
         * es ese; si no, se evalúan las dos entradas de la compuerta que lo
         * escribe. Los valores calculados se guardan, así cada compuerta se
         * evalúa una sola vez. Como el circuito no tiene ciclos, la
         * recursión termina; igual se detectan, por si el input los tuviera.</p>
         *
         * @return cable → valor
         */
        Map<String, Boolean> simular() {
            Map<String, Boolean> valores = new HashMap<>(iniciales);
            Set<String> enCurso = new HashSet<>();
            for (String cable : compuertas.keySet()) valor(cable, valores, enCurso);
            return valores;
        }

        private boolean valor(String cable, Map<String, Boolean> valores, Set<String> enCurso) {
            Boolean conocido = valores.get(cable);
            if (conocido != null) return conocido;
            Compuerta compuerta = compuertas.get(cable);
            if (compuerta == null) throw new IllegalArgumentException("El cable " + cable + " no tiene valor");
            if (!enCurso.add(cable)) throw new IllegalStateException("El circuito tiene un ciclo en " + cable);
            boolean resultado = compuerta.operacion().aplicar(
                    valor(compuerta.entrada1(), valores, enCurso),
                    valor(compuerta.entrada2(), valores, enCurso));
            enCurso.remove(cable);
            valores.put(cable, resultado);
            return resultado;
        }

        /**
         * El número que forman los cables que empiezan con {@code letra}: el
         * cable {@code letra00} es el bit menos significativo, {@code letra01}
         * el siguiente, y así.
         *
         * @param letra la letra de los cables (por ejemplo {@code 'z'})
         * @return el número
         * @throws ArithmeticException si hay más de 63 bits
         */
        long numero(char letra) {
            Map<String, Boolean> valores = simular();
            long numero = 0;
            for (Map.Entry<String, Boolean> cable : valores.entrySet()) {
                String nombre = cable.getKey();
                if (!esBit(nombre, letra) || !cable.getValue()) continue;
                int bit = Integer.parseInt(nombre.substring(1));
                if (bit >= Long.SIZE - 1) throw new ArithmeticException("Demasiados bits: " + nombre);
                numero |= 1L << bit;
            }
            return numero;
        }

        /** Si el cable es un bit de un número: la letra seguida solo de cifras (como {@code z07}). */
        static boolean esBit(String cable, char letra) {
            return cable.length() > 1 && cable.charAt(0) == letra
                    && cable.substring(1).chars().allMatch(Character::isDigit);
        }
    }

    /**
     * Busca qué pares de compuertas tienen las salidas intercambiadas, para
     * que el circuito calcule {@code z = operacion(x, y)}.
     *
     * <p><b>Puntaje.</b> Se prueba el circuito con un lote fijo de pares
     * {@code (x, y)} pseudoaleatorios y el puntaje es el <em>bit más bajo</em>
     * de {@code z} que da mal en alguna prueba (o la cantidad de bits de
     * {@code z} si todos dan bien; −1 si el circuito quedó con un ciclo).
     * Arreglar un intercambio tiene que subir el puntaje: el bit más bajo que
     * fallaba pasa a estar bien.</p>
     *
     * <p><b>Evaluación bit-paralela.</b> Cada cable guarda un {@code long}
     * cuyo bit {@code t} es su valor en la prueba {@code t}. Así una pasada
     * por las compuertas evalúa 64 pruebas a la vez con AND, OR y XOR de
     * {@code long}.</p>
     *
     * <p><b>Búsqueda.</b> Se prueban todos los intercambios de dos salidas
     * que no se hayan usado y se sigue, con backtracking, por los que suben
     * el puntaje (primero los que más lo suben), hasta usar la cantidad de
     * pares pedida con el circuito completo bien.</p>
     */
    static final class Reparacion {

        /** Lotes de 64 pruebas cada uno. */
        private static final int LOTES = 4;

        /** Semilla fija, para que las pruebas (y el resultado) sean siempre las mismas. */
        private static final long SEMILLA = 24;

        private final String[] nombres;
        private final Operacion[] operaciones;   // de cada compuerta
        private final int[] entrada1;             // de cada compuerta (índice de cable)
        private final int[] entrada2;
        private final int[] compuertaDe;          // cable → compuerta que lo escribe (−1 si es de entrada)
        private final int[] salidas;              // cables que son salida de alguna compuerta
        private final int[] bitsX;
        private final int[] bitsY;
        private final int[] bitsZ;
        private final long[][] valoresX;          // [lote][bit]: el bit de x en cada prueba
        private final long[][] valoresY;
        private final long[][] esperado;          // [lote][bit de z]

        /**
         * @param circuito  el circuito, con las salidas intercambiadas
         * @param operacion lo que debería calcular, por ejemplo {@code Long::sum}
         */
        Reparacion(Circuito circuito, LongBinaryOperator operacion) {
            Set<String> todos = new TreeSet<>(circuito.iniciales().keySet());
            circuito.compuertas().values().forEach(c -> {
                todos.add(c.entrada1());
                todos.add(c.entrada2());
                todos.add(c.salida());
            });
            nombres = todos.toArray(String[]::new);
            Map<String, Integer> indice = new HashMap<>();
            for (int i = 0; i < nombres.length; i++) indice.put(nombres[i], i);

            List<Compuerta> lista = new ArrayList<>(circuito.compuertas().values());
            lista.sort(Comparator.comparing(Compuerta::salida));
            operaciones = new Operacion[lista.size()];
            entrada1 = new int[lista.size()];
            entrada2 = new int[lista.size()];
            compuertaDe = new int[nombres.length];
            Arrays.fill(compuertaDe, -1);
            salidas = new int[lista.size()];
            for (int g = 0; g < lista.size(); g++) {
                Compuerta compuerta = lista.get(g);
                operaciones[g] = compuerta.operacion();
                entrada1[g] = indice.get(compuerta.entrada1());
                entrada2[g] = indice.get(compuerta.entrada2());
                salidas[g] = indice.get(compuerta.salida());
                compuertaDe[salidas[g]] = g;
            }
            bitsX = bits(indice, 'x');
            bitsY = bits(indice, 'y');
            bitsZ = bits(indice, 'z');
            if (bitsX.length != bitsY.length || bitsX.length >= Long.SIZE - 1 || bitsZ.length >= Long.SIZE) {
                throw new IllegalArgumentException("Cantidad de bits no soportada");
            }

            valoresX = new long[LOTES][bitsX.length];
            valoresY = new long[LOTES][bitsY.length];
            esperado = new long[LOTES][bitsZ.length];
            Random azar = new Random(SEMILLA);
            long mascaraEntrada = (1L << bitsX.length) - 1;
            long mascaraSalida = bitsZ.length == Long.SIZE - 1 ? Long.MAX_VALUE : (1L << bitsZ.length) - 1;
            for (int lote = 0; lote < LOTES; lote++) {
                for (int prueba = 0; prueba < Long.SIZE; prueba++) {
                    long x = azar.nextLong() & mascaraEntrada;
                    long y = azar.nextLong() & mascaraEntrada;
                    long z = operacion.applyAsLong(x, y) & mascaraSalida;
                    for (int b = 0; b < bitsX.length; b++) {
                        valoresX[lote][b] |= ((x >>> b) & 1) << prueba;
                        valoresY[lote][b] |= ((y >>> b) & 1) << prueba;
                    }
                    for (int b = 0; b < bitsZ.length; b++) esperado[lote][b] |= ((z >>> b) & 1) << prueba;
                }
            }
        }

        /** Índices de los cables {@code letra00}, {@code letra01}, ... en orden, sin huecos. */
        private int[] bits(Map<String, Integer> indice, char letra) {
            int cantidad = (int) indice.keySet().stream().filter(nombre -> Circuito.esBit(nombre, letra)).count();
            int[] bits = new int[cantidad];
            for (int b = 0; b < cantidad; b++) {
                Integer cable = indice.get(String.format("%c%02d", letra, b));
                if (cable == null) throw new IllegalArgumentException("Falta el cable " + letra + b);
                bits[b] = cable;
            }
            return bits;
        }

        /**
         * Los cables de salida de los {@code pares} intercambios que arreglan
         * el circuito, ordenados.
         *
         * @param pares cuántos pares de compuertas tienen las salidas intercambiadas
         * @return los {@code 2 × pares} cables, ordenados por nombre
         * @throws IllegalStateException si no hay forma de arreglarlo con esa cantidad de pares
         */
        List<String> cablesIntercambiados(int pares) {
            List<Integer> usados = new ArrayList<>();
            if (!buscar(pares, usados)) {
                throw new IllegalStateException("No se encontraron " + pares + " intercambios que arreglen el circuito");
            }
            return usados.stream().map(cable -> nombres[cable]).sorted().toList();
        }

        private boolean buscar(int restantes, List<Integer> usados) {
            int actual = puntaje();
            if (restantes == 0) return actual == bitsZ.length;
            List<int[]> mejoras = new ArrayList<>();   // {cable a, cable b, puntaje}
            for (int i = 0; i < salidas.length; i++) {
                if (usados.contains(salidas[i])) continue;
                for (int j = i + 1; j < salidas.length; j++) {
                    if (usados.contains(salidas[j])) continue;
                    intercambiar(salidas[i], salidas[j]);
                    int nuevo = puntaje();
                    intercambiar(salidas[i], salidas[j]);
                    if (nuevo > actual) mejoras.add(new int[] {salidas[i], salidas[j], nuevo});
                }
            }
            mejoras.sort(Comparator.comparingInt((int[] mejora) -> mejora[2]).reversed());
            for (int[] mejora : mejoras) {
                intercambiar(mejora[0], mejora[1]);
                usados.add(mejora[0]);
                usados.add(mejora[1]);
                if (buscar(restantes - 1, usados)) return true;
                usados.removeLast();
                usados.removeLast();
                intercambiar(mejora[0], mejora[1]);
            }
            return false;
        }

        /** Intercambia las compuertas que escriben en dos cables. */
        private void intercambiar(int a, int b) {
            int compuerta = compuertaDe[a];
            compuertaDe[a] = compuertaDe[b];
            compuertaDe[b] = compuerta;
        }

        /**
         * El bit más bajo de {@code z} que da mal en alguna prueba; la
         * cantidad de bits de {@code z} si todos dan bien; −1 si hay un ciclo.
         */
        int puntaje() {
            int puntaje = bitsZ.length;
            long[] valores = new long[nombres.length];
            byte[] estado = new byte[nombres.length];   // 0 sin evaluar, 1 en curso, 2 listo
            for (int lote = 0; lote < LOTES; lote++) {
                Arrays.fill(estado, (byte) 0);
                for (int b = 0; b < bitsX.length; b++) {
                    valores[bitsX[b]] = valoresX[lote][b];
                    valores[bitsY[b]] = valoresY[lote][b];
                    estado[bitsX[b]] = 2;
                    estado[bitsY[b]] = 2;
                }
                for (int b = 0; b < puntaje; b++) {
                    if (!evaluar(bitsZ[b], valores, estado)) return -1;
                    if (valores[bitsZ[b]] != esperado[lote][b]) {
                        puntaje = b;
                        break;
                    }
                }
            }
            return puntaje;
        }

        /** Evalúa un cable de forma recursiva; {@code false} si encuentra un ciclo. */
        private boolean evaluar(int cable, long[] valores, byte[] estado) {
            if (estado[cable] == 2) return true;
            if (estado[cable] == 1) return false;
            int g = compuertaDe[cable];
            if (g < 0) throw new IllegalArgumentException("El cable " + nombres[cable] + " no tiene valor");
            estado[cable] = 1;
            if (!evaluar(entrada1[g], valores, estado) || !evaluar(entrada2[g], valores, estado)) return false;
            long a = valores[entrada1[g]];
            long b = valores[entrada2[g]];
            valores[cable] = switch (operaciones[g]) {
                case AND -> a & b;
                case OR -> a | b;
                case XOR -> a ^ b;
            };
            estado[cable] = 2;
            return true;
        }
    }
}
