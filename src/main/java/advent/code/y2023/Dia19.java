package advent.code.y2023;

import advent.code.Rango;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2023 — Día 19: A Montones
 *
 * <p>El input tiene dos secciones: los flujos de trabajo, cada uno con un
 * nombre y una lista de reglas, y las piezas, cada una con cuatro
 * calificaciones ({@code x}, {@code m}, {@code a}, {@code s}). Cada pieza
 * empieza en el flujo {@code in} y va pasando de flujo en flujo hasta ser
 * aceptada ({@code A}) o rechazada ({@code R}).</p>
 */
public class Dia19 implements Resolvedor {

    /** Las categorías, en el orden en que se guardan las calificaciones. */
    static final String CATEGORIAS = "xmas";

    /** El flujo por el que empiezan todas las piezas. */
    static final String INICIO = "in";

    static final String ACEPTADA = "A";
    static final String RECHAZADA = "R";

    /** Los valores posibles de cada calificación en la parte 2. */
    static final Rango CALIFICACIONES = new Rango(1, 4000);

    private static final Pattern FLUJO = Pattern.compile("([a-z]+)\\{(.+)}");
    private static final Pattern CONDICION = Pattern.compile("([xmas])([<>])(\\d+):([a-zAR]+)");
    private static final Pattern DESTINO = Pattern.compile("[a-zAR]+");
    private static final Pattern PIEZA = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");

    /**
     * Una regla de un flujo. Sin condición ({@code categoria == -1}) se
     * aplica siempre; con condición, solo si la calificación de esa categoría
     * es menor ({@code <}) o mayor ({@code >}) que el valor.
     *
     * @param categoria índice en {@link #CATEGORIAS}, o {@code -1} si no tiene condición
     * @param operador  {@code '<'} o {@code '>'} (sin uso si no tiene condición)
     * @param valor     el valor con el que se compara
     * @param destino   a dónde va la pieza si la regla se aplica
     */
    record Regla(int categoria, char operador, int valor, String destino) {

        /** @return si la pieza cumple la condición (siempre, si no tiene) */
        boolean seAplica(int[] pieza) {
            if (categoria < 0) return true;
            return operador == '<' ? pieza[categoria] < valor : pieza[categoria] > valor;
        }

        /**
         * Los valores de {@code rango} que cumplen la condición: los menores
         * que el valor, o los mayores.
         *
         * @param rango los valores posibles de la categoría
         * @return los que cumplen, o vacío si ninguno
         */
        Optional<Rango> cumplen(Rango rango) {
            return rango.interseccion(operador == '<'
                    ? new Rango(Long.MIN_VALUE, valor - 1L)
                    : new Rango(valor + 1L, Long.MAX_VALUE));
        }

        /**
         * Los valores de {@code rango} que no cumplen la condición: el
         * complemento de {@link #cumplen} dentro del rango.
         *
         * @param rango los valores posibles de la categoría
         * @return los que no cumplen, o vacío si ninguno
         */
        Optional<Rango> noCumplen(Rango rango) {
            return rango.interseccion(operador == '<'
                    ? new Rango(valor, Long.MAX_VALUE)
                    : new Rango(Long.MIN_VALUE, valor));
        }
    }

    /**
     * El input leído.
     *
     * @param flujos las reglas de cada flujo, por nombre
     * @param piezas las calificaciones {@code x, m, a, s} de cada pieza
     */
    record Sistema(Map<String, List<Regla>> flujos, List<int[]> piezas) {
    }

    /**
     * Parte 1: la suma de todas las calificaciones de las piezas aceptadas.
     *
     * @param lineas los flujos y las piezas
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        Sistema sistema = leer(lineas);
        long suma = 0;
        for (int[] pieza : sistema.piezas()) {
            if (esAceptada(sistema.flujos(), pieza)) {
                for (int calificacion : pieza) suma += calificacion;
            }
        }
        return String.valueOf(suma);
    }

    /**
     * Parte 2: cuántas combinaciones de calificaciones (cada una de 1 a 4000)
     * terminan aceptadas. Las piezas del input ya no importan.
     *
     * @param lineas los flujos y las piezas
     * @return la cantidad de combinaciones aceptadas
     */
    @Override
    public String parte2(List<String> lineas) {
        Rango[] todas = {CALIFICACIONES, CALIFICACIONES, CALIFICACIONES, CALIFICACIONES};
        return String.valueOf(combinacionesAceptadas(leer(lineas).flujos(), todas));
    }

    /**
     * Cuántas piezas de la caja {@code caja} (un rango de valores por
     * categoría) terminan aceptadas si empiezan en {@code in}.
     *
     * @param flujos los flujos
     * @param caja   el rango de cada calificación, en el orden {@code x, m, a, s}
     * @return la cantidad de combinaciones aceptadas
     * @throws IllegalStateException si alguna pieza entra en un ciclo de flujos
     */
    static long combinacionesAceptadas(Map<String, List<Regla>> flujos, Rango[] caja) {
        return aceptadas(flujos, INICIO, caja, 0);
    }

    /**
     * Manda una caja entera de piezas por un flujo, en vez de una pieza por
     * vez.
     *
     * <p>Cada regla con condición parte la caja en dos según la categoría que
     * mira: la parte que cumple va al destino de la regla y la que no cumple
     * sigue con la regla siguiente. La última regla se lleva lo que quede.
     * Las cajas que llegan a {@code A} se cuentan multiplicando los largos de
     * sus cuatro rangos. Como cada partición separa la caja en partes
     * disjuntas, ninguna pieza se cuenta dos veces.</p>
     *
     * @param flujos      los flujos
     * @param destino     a dónde llega la caja
     * @param caja        el rango de cada calificación
     * @param profundidad cuántos flujos lleva recorridos la caja
     * @return la cantidad de piezas de la caja que terminan aceptadas
     */
    private static long aceptadas(Map<String, List<Regla>> flujos, String destino, Rango[] caja, int profundidad) {
        if (destino.equals(RECHAZADA)) return 0;
        if (destino.equals(ACEPTADA)) {
            long cantidad = 1;
            for (Rango rango : caja) cantidad *= rango.largo();
            return cantidad;
        }
        // Igual que en la parte 1: sin repetir flujos, a lo sumo se visitan todos una vez
        if (profundidad > flujos.size()) throw new IllegalStateException("Hay piezas que entran en un ciclo de flujos");
        long total = 0;
        Rango[] resto = caja.clone();
        for (Regla regla : flujos.get(destino)) {
            if (regla.categoria() < 0) {
                return total + aceptadas(flujos, regla.destino(), resto, profundidad + 1);
            }
            Optional<Rango> cumplen = regla.cumplen(resto[regla.categoria()]);
            if (cumplen.isPresent()) {
                Rango[] parte = resto.clone();
                parte[regla.categoria()] = cumplen.get();
                total += aceptadas(flujos, regla.destino(), parte, profundidad + 1);
            }
            Optional<Rango> noCumplen = regla.noCumplen(resto[regla.categoria()]);
            if (noCumplen.isEmpty()) return total;
            resto[regla.categoria()] = noCumplen.get();
        }
        throw new IllegalStateException("El flujo '" + destino + "' no termina en una regla sin condición");
    }

    /**
     * Sigue una pieza desde {@code in}: en cada flujo se aplica la primera
     * regla que cumple, hasta llegar a {@code A} o {@code R}.
     *
     * @param flujos los flujos
     * @param pieza  las calificaciones de la pieza
     * @return si la pieza termina aceptada
     * @throws IllegalStateException si la pieza vuelve a un flujo por el que ya pasó (nunca terminaría)
     */
    static boolean esAceptada(Map<String, List<Regla>> flujos, int[] pieza) {
        String actual = INICIO;
        // Sin repetir flujos, a lo sumo se visitan todos una vez
        for (int pasos = 0; pasos <= flujos.size(); pasos++) {
            if (actual.equals(ACEPTADA)) return true;
            if (actual.equals(RECHAZADA)) return false;
            actual = primeraQueSeAplica(flujos.get(actual), pieza).destino();
        }
        throw new IllegalStateException("La pieza entra en un ciclo de flujos");
    }

    /** La primera regla del flujo que se aplica a la pieza (la última siempre se aplica). */
    private static Regla primeraQueSeAplica(List<Regla> reglas, int[] pieza) {
        for (Regla regla : reglas) {
            if (regla.seAplica(pieza)) return regla;
        }
        throw new IllegalStateException("Ninguna regla se aplica: " + reglas);
    }

    /**
     * Lee los flujos y las piezas, y comprueba que todo destino exista, que
     * esté el flujo {@code in} y que cada flujo termine en una regla sin
     * condición.
     *
     * @param lineas las dos secciones del input
     * @return el sistema
     * @throws IllegalArgumentException si algo no tiene el formato esperado
     */
    static Sistema leer(List<String> lineas) {
        List<List<String>> secciones = Utiles.secciones(lineas, 2);
        Map<String, List<Regla>> flujos = new HashMap<>();
        for (String linea : secciones.get(0)) {
            Matcher flujo = FLUJO.matcher(linea.strip());
            if (!flujo.matches()) throw new IllegalArgumentException("Flujo inválido: '" + linea + "'");
            List<Regla> reglas = new ArrayList<>();
            String[] textos = flujo.group(2).split(",", -1);
            for (int i = 0; i < textos.length; i++) {
                reglas.add(leerRegla(textos[i], i == textos.length - 1, linea));
            }
            if (flujos.put(flujo.group(1), List.copyOf(reglas)) != null) {
                throw new IllegalArgumentException("Flujo repetido: '" + flujo.group(1) + "'");
            }
        }
        if (!flujos.containsKey(INICIO)) throw new IllegalArgumentException("Falta el flujo '" + INICIO + "'");
        for (List<Regla> reglas : flujos.values()) {
            for (Regla regla : reglas) {
                String destino = regla.destino();
                if (!destino.equals(ACEPTADA) && !destino.equals(RECHAZADA) && !flujos.containsKey(destino)) {
                    throw new IllegalArgumentException("Destino desconocido: '" + destino + "'");
                }
            }
        }
        List<int[]> piezas = new ArrayList<>();
        for (String linea : secciones.get(1)) {
            Matcher pieza = PIEZA.matcher(linea.strip());
            if (!pieza.matches()) throw new IllegalArgumentException("Pieza inválida: '" + linea + "'");
            int[] calificaciones = new int[CATEGORIAS.length()];
            for (int i = 0; i < calificaciones.length; i++) calificaciones[i] = Integer.parseInt(pieza.group(i + 1));
            piezas.add(calificaciones);
        }
        return new Sistema(Map.copyOf(flujos), List.copyOf(piezas));
    }

    /**
     * Lee una regla: {@code x>10:one} o, si es la última, solo un destino.
     *
     * @param texto  la regla
     * @param ultima si es la última del flujo (la única que va sin condición)
     * @param linea  la línea completa, para el mensaje de error
     * @return la regla
     * @throws IllegalArgumentException si no tiene el formato esperado, o le
     *                                  falta o le sobra la condición
     */
    private static Regla leerRegla(String texto, boolean ultima, String linea) {
        Matcher condicion = CONDICION.matcher(texto);
        if (!ultima && condicion.matches()) {
            return new Regla(CATEGORIAS.indexOf(condicion.group(1).charAt(0)), condicion.group(2).charAt(0),
                    Integer.parseInt(condicion.group(3)), condicion.group(4));
        }
        if (ultima && DESTINO.matcher(texto).matches()) return new Regla(-1, ' ', 0, texto);
        throw new IllegalArgumentException("Regla inválida '" + texto + "' en: '" + linea + "'");
    }
}
