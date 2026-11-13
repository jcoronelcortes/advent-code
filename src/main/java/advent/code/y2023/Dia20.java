package advent.code.y2023;

import advent.code.Matematicas;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2023 — Día 20: Propagación de Pulsos
 *
 * <p>El input es una red de módulos conectados que se mandan pulsos altos o
 * bajos. Hay biestables ({@code %}), que cambian de estado con cada pulso
 * bajo; conjunciones ({@code &}), que recuerdan el último pulso de cada
 * entrada; y un difusor ({@code broadcaster}), que reenvía lo que recibe. Al
 * presionar el botón, el difusor recibe un pulso bajo y los pulsos se
 * procesan en el orden en que se mandaron.</p>
 */
public class Dia20 implements Resolvedor {

    /** Cuántas veces se presiona el botón en la parte 1. */
    static final int PRESIONES = 1000;

    /** El nombre del módulo que recibe el pulso del botón. */
    static final String DIFUSOR = "broadcaster";

    /** El origen del pulso que manda el botón. */
    static final String BOTON = "button";

    /** El módulo que enciende la máquina en la parte 2. */
    static final String MAQUINA = "rx";

    /** Cuántas veces se presiona el botón, como mucho, buscando los períodos. */
    static final int LIMITE_PRESIONES = 100_000;

    private static final Pattern LINEA = Pattern.compile("([%&]?)([a-z]+) -> ([a-z]+(?:, [a-z]+)*)");

    /** Los tipos de módulo. */
    enum Tipo {
        /** El {@code broadcaster}: reenvía el pulso que recibe a todos sus destinos. */
        DIFUSOR,
        /** {@code %}: ignora los pulsos altos; con uno bajo se prende o se apaga y avisa. */
        BIESTABLE,
        /** {@code &}: manda un pulso bajo si recuerda altos de todas sus entradas; si no, uno alto. */
        CONJUNCION,
        /** Un destino que no está definido en el input (como {@code output}): recibe y no hace nada. */
        SIN_TIPO
    }

    /**
     * Un pulso enviado.
     *
     * @param origen  quién lo manda
     * @param destino quién lo recibe
     * @param alto    si es alto ({@code false} si es bajo)
     */
    record Pulso(String origen, String destino, boolean alto) {
    }

    /**
     * Un módulo de la red.
     *
     * @param tipo     su tipo
     * @param destinos a quiénes manda sus pulsos, en orden
     */
    record Modulo(Tipo tipo, List<String> destinos) {
    }

    /**
     * Parte 1: el producto de la cantidad de pulsos bajos por la de pulsos
     * altos enviados (incluidos los del botón) tras presionar el botón mil
     * veces.
     *
     * @param lineas la configuración de los módulos
     * @return el producto
     */
    @Override
    public String parte1(List<String> lineas) {
        Red red = new Red(leer(lineas));
        long bajos = 0, altos = 0;
        for (int i = 0; i < PRESIONES; i++) {
            for (Pulso pulso : red.presionarBoton()) {
                if (pulso.alto()) altos++;
                else bajos++;
            }
        }
        return String.valueOf(bajos * altos);
    }

    /**
     * Parte 2: la menor cantidad de presiones del botón hasta que el módulo
     * {@code rx} recibe un pulso bajo.
     *
     * @param lineas la configuración de los módulos
     * @return la cantidad de presiones
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(presionesHastaPulsoBajo(leer(lineas), MAQUINA));
    }

    /**
     * Cuántas presiones hacen falta para que {@code objetivo} reciba un pulso
     * bajo.
     *
     * <p>Simularlo directamente no termina: hacen falta cientos de billones
     * de presiones. Pero la red tiene una forma particular, que se comprueba
     * en vez de suponerla:</p>
     *
     * <ul>
     *   <li>El objetivo tiene una sola entrada, una <b>conjunción</b> (el
     *       concentrador). Esta manda un pulso bajo solo si recuerda pulsos
     *       altos de todas sus entradas, así que hace falta que cada entrada
     *       le mande un pulso alto en la misma presión.</li>
     *   <li>Cada entrada del concentrador manda pulsos altos
     *       <b>periódicamente</b>: en las presiones {@code k}, {@code 2k},
     *       {@code 3k}… Se simula hasta ver dos pulsos altos de cada entrada y
     *       se comprueba que el segundo llegue en la presión {@code 2k}.</li>
     * </ul>
     *
     * <p>Entonces la primera presión en la que todas coinciden es el mínimo
     * común múltiplo de los períodos. Si el objetivo recibe un pulso bajo
     * mientras se buscan los períodos, se devuelve esa presión directamente.</p>
     *
     * @param modulos  la red
     * @param objetivo el módulo que tiene que recibir el pulso bajo
     * @return la menor cantidad de presiones
     * @throws IllegalArgumentException si la red no tiene la forma esperada
     * @throws IllegalStateException    si alguna entrada no es periódica desde
     *                                  el inicio o no aparece a tiempo
     */
    static long presionesHastaPulsoBajo(Map<String, Modulo> modulos, String objetivo) {
        List<String> entradasObjetivo = entradas(modulos, objetivo);
        if (entradasObjetivo.size() != 1 || modulos.get(entradasObjetivo.getFirst()).tipo() != Tipo.CONJUNCION) {
            throw new IllegalArgumentException("'" + objetivo + "' tiene que tener una sola entrada, una conjunción: "
                    + entradasObjetivo);
        }
        String concentrador = entradasObjetivo.getFirst();
        // Para cada entrada del concentrador, las presiones en las que le mandó un pulso alto (las dos primeras)
        Map<String, List<Long>> altos = new LinkedHashMap<>();
        for (String entrada : entradas(modulos, concentrador)) altos.put(entrada, new ArrayList<>());

        Red red = new Red(modulos);
        for (long presion = 1; presion <= LIMITE_PRESIONES; presion++) {
            for (Pulso pulso : red.presionarBoton()) {
                if (pulso.destino().equals(objetivo) && !pulso.alto()) return presion;
                if (pulso.destino().equals(concentrador) && pulso.alto()) {
                    List<Long> presiones = altos.get(pulso.origen());
                    if (presiones.size() < 2 && (presiones.isEmpty() || presiones.getLast() != presion)) {
                        presiones.add(presion);
                    }
                }
            }
            if (altos.values().stream().allMatch(presiones -> presiones.size() == 2)) {
                return mcmDePeriodos(altos);
            }
        }
        throw new IllegalStateException("No se encontraron los períodos en " + LIMITE_PRESIONES + " presiones: " + altos);
    }

    /** El mcm de los períodos, comprobando que cada entrada repita en {@code 2k} lo que hizo en {@code k}. */
    private static long mcmDePeriodos(Map<String, List<Long>> altos) {
        List<Long> periodos = new ArrayList<>();
        for (Map.Entry<String, List<Long>> entrada : altos.entrySet()) {
            long primera = entrada.getValue().get(0), segunda = entrada.getValue().get(1);
            if (segunda != 2 * primera) {
                throw new IllegalStateException("'" + entrada.getKey() + "' no es periódica desde el inicio: manda "
                        + "pulsos altos en las presiones " + primera + " y " + segunda);
            }
            periodos.add(primera);
        }
        return Matematicas.mcm(periodos);
    }

    /** Los módulos que tienen a {@code modulo} entre sus destinos, en el orden del input. */
    static List<String> entradas(Map<String, Modulo> modulos, String modulo) {
        return modulos.entrySet().stream()
                .filter(origen -> origen.getValue().destinos().contains(modulo))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Lee la configuración: cada línea es un módulo con su tipo
     * ({@code %}, {@code &} o ninguno para el difusor), su nombre y sus
     * destinos. Los destinos que no aparecen definidos se agregan como
     * módulos sin tipo.
     *
     * @param lineas la configuración
     * @return los módulos por nombre, en el orden del input
     * @throws IllegalArgumentException si una línea no tiene el formato
     *                                  esperado, un módulo se repite, falta el
     *                                  difusor o un módulo sin prefijo no es
     *                                  el difusor
     */
    static Map<String, Modulo> leer(List<String> lineas) {
        Map<String, Modulo> modulos = new LinkedHashMap<>();
        for (String linea : Utiles.lineasNoVacias(lineas)) {
            Matcher partes = LINEA.matcher(linea.strip());
            if (!partes.matches()) throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
            String nombre = partes.group(2);
            Tipo tipo = switch (partes.group(1)) {
                case "%" -> Tipo.BIESTABLE;
                case "&" -> Tipo.CONJUNCION;
                default -> {
                    if (!nombre.equals(DIFUSOR)) {
                        throw new IllegalArgumentException("Módulo sin tipo que no es el difusor: '" + linea + "'");
                    }
                    yield Tipo.DIFUSOR;
                }
            };
            Modulo modulo = new Modulo(tipo, List.of(partes.group(3).split(", ")));
            if (modulos.put(nombre, modulo) != null) {
                throw new IllegalArgumentException("Módulo repetido: '" + nombre + "'");
            }
        }
        if (!modulos.containsKey(DIFUSOR)) throw new IllegalArgumentException("Falta el módulo '" + DIFUSOR + "'");
        for (Modulo modulo : List.copyOf(modulos.values())) {
            for (String destino : modulo.destinos()) {
                modulos.putIfAbsent(destino, new Modulo(Tipo.SIN_TIPO, List.of()));
            }
        }
        return modulos;
    }

    /**
     * La red de módulos con su estado: qué biestables están prendidos y qué
     * recuerda cada conjunción de cada una de sus entradas.
     */
    static final class Red {

        private final Map<String, Modulo> modulos;

        /** Los biestables prendidos (al principio, ninguno). */
        private final Set<String> prendidos = new HashSet<>();

        /** Para cada conjunción, el último pulso de cada entrada (al principio, todos bajos). */
        private final Map<String, Map<String, Boolean>> memoria = new HashMap<>();

        /**
         * Arma la red con todos los módulos apagados.
         *
         * @param modulos los módulos, como los devuelve {@link #leer}
         */
        Red(Map<String, Modulo> modulos) {
            this.modulos = modulos;
            for (Map.Entry<String, Modulo> origen : modulos.entrySet()) {
                for (String destino : origen.getValue().destinos()) {
                    if (modulos.get(destino).tipo() == Tipo.CONJUNCION) {
                        memoria.computeIfAbsent(destino, d -> new HashMap<>()).put(origen.getKey(), false);
                    }
                }
            }
        }

        /**
         * Presiona el botón una vez y procesa todos los pulsos hasta que no
         * quede ninguno.
         *
         * <p>Los pulsos se procesan con una <b>cola</b>: en el orden en que se
         * mandaron. Así, si un módulo manda pulsos a {@code a}, {@code b} y
         * {@code c}, los de {@code b} y {@code c} se procesan antes que los que
         * genere {@code a}.</p>
         *
         * @return todos los pulsos enviados, en orden, empezando por el del botón
         */
        List<Pulso> presionarBoton() {
            List<Pulso> enviados = new ArrayList<>();
            Queue<Pulso> pendientes = new ArrayDeque<>();
            pendientes.add(new Pulso(BOTON, DIFUSOR, false));
            while (!pendientes.isEmpty()) {
                Pulso pulso = pendientes.poll();
                enviados.add(pulso);
                Modulo modulo = modulos.get(pulso.destino());
                Boolean salida = switch (modulo.tipo()) {
                    case DIFUSOR -> pulso.alto();
                    case BIESTABLE -> {
                        if (pulso.alto()) yield null;
                        // Si estaba apagado se prende y manda alto; si estaba prendido se apaga y manda bajo
                        boolean estabaApagado = prendidos.add(pulso.destino());
                        if (!estabaApagado) prendidos.remove(pulso.destino());
                        yield estabaApagado;
                    }
                    case CONJUNCION -> {
                        Map<String, Boolean> recuerda = memoria.get(pulso.destino());
                        recuerda.put(pulso.origen(), pulso.alto());
                        yield !recuerda.values().stream().allMatch(alto -> alto);
                    }
                    case SIN_TIPO -> null;
                };
                if (salida == null) continue;
                for (String destino : modulo.destinos()) {
                    pendientes.add(new Pulso(pulso.destino(), destino, salida));
                }
            }
            return enviados;
        }

        /** @return los biestables prendidos, ordenados (útil para pruebas) */
        List<String> prendidos() {
            String[] nombres = prendidos.toArray(String[]::new);
            Arrays.sort(nombres);
            return List.of(nombres);
        }
    }
}
