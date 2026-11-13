package advent.code.y2023;

import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2023 — Día 15: Biblioteca de Lentes
 *
 * <p>El input es la secuencia de inicialización: pasos separados por comas
 * en una sola línea larga (los saltos de línea se ignoran). El algoritmo
 * HASH convierte cualquier texto en un número de 0 a 255.</p>
 */
public class Dia15 implements Resolvedor {

    /** Por cuánto se multiplica el valor actual en cada carácter. */
    static final int MULTIPLICADOR = 17;

    /** El HASH es el resto de dividir por este valor (de 0 a 255). */
    static final int MODULO = 256;

    /** Un paso: una etiqueta de letras seguida de {@code -} o de {@code =} y una distancia focal de 1 a 9. */
    private static final Pattern PASO = Pattern.compile("([a-z]+)(?:(-)|=([1-9]))");

    /**
     * Parte 1: la suma del HASH de cada paso de la secuencia.
     *
     * @param lineas la secuencia de inicialización
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(pasos(lineas).stream().mapToLong(Dia15::hash).sum());
    }

    /**
     * Parte 2: el poder de enfoque de las lentes después de seguir todos los
     * pasos de la secuencia (el procedimiento HASHMAP).
     *
     * @param lineas la secuencia de inicialización
     * @return el poder de enfoque total
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(poderDeEnfoque(colocarLentes(pasos(lineas))));
    }

    /**
     * Sigue los pasos con 256 cajas. La caja de cada paso es el HASH de su
     * etiqueta. Con {@code -} se saca la lente con esa etiqueta, si está, y
     * las de atrás avanzan; con {@code =N} se reemplaza la lente con esa
     * etiqueta en su mismo lugar o, si no está, se agrega al fondo.
     *
     * <p>Cada caja es un {@link LinkedHashMap} de etiqueta a distancia focal:
     * guarda el orden de inserción, {@code put} de una etiqueta que ya está
     * cambia el valor sin moverla y {@code remove} mantiene el orden del
     * resto. Es exactamente el comportamiento de las cajas.</p>
     *
     * @param pasos los pasos de la secuencia
     * @return las 256 cajas, cada una con sus lentes de adelante hacia atrás
     * @throws IllegalArgumentException si un paso no tiene el formato esperado
     */
    static List<SequencedMap<String, Integer>> colocarLentes(List<String> pasos) {
        List<SequencedMap<String, Integer>> cajas = new ArrayList<>();
        for (int i = 0; i < MODULO; i++) cajas.add(new LinkedHashMap<>());
        for (String paso : pasos) {
            Matcher partes = PASO.matcher(paso);
            if (!partes.matches()) throw new IllegalArgumentException("Paso inválido: '" + paso + "'");
            String etiqueta = partes.group(1);
            SequencedMap<String, Integer> caja = cajas.get(hash(etiqueta));
            if (partes.group(2) != null) {
                caja.remove(etiqueta);
            } else {
                caja.put(etiqueta, Integer.parseInt(partes.group(3)));
            }
        }
        return cajas;
    }

    /**
     * El poder de enfoque total: por cada lente, (1 + número de caja) por
     * su posición en la caja (desde 1) por su distancia focal.
     *
     * @param cajas las cajas con sus lentes en orden
     * @return la suma
     */
    static long poderDeEnfoque(List<SequencedMap<String, Integer>> cajas) {
        long total = 0;
        for (int caja = 0; caja < cajas.size(); caja++) {
            int posicion = 1;
            for (int focal : cajas.get(caja).values()) {
                total += (long) (caja + 1) * posicion * focal;
                posicion++;
            }
        }
        return total;
    }

    /**
     * Los pasos de la secuencia: se unen las líneas ignorando los saltos de
     * línea y se separa por comas.
     *
     * @param lineas la secuencia, posiblemente partida en varias líneas
     * @return los pasos, en orden
     * @throws IllegalArgumentException si la secuencia está vacía o tiene un paso vacío
     */
    static List<String> pasos(List<String> lineas) {
        String secuencia = String.join("", lineas).strip();
        if (secuencia.isEmpty()) throw new IllegalArgumentException("La secuencia de inicialización está vacía");
        List<String> pasos = Arrays.asList(secuencia.split(",", -1));
        if (pasos.stream().anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("La secuencia tiene un paso vacío: " + secuencia);
        }
        return pasos;
    }

    /**
     * El algoritmo HASH: empezando en 0, por cada carácter se suma su código
     * ASCII, se multiplica por 17 y se toma el resto de dividir por 256.
     *
     * @param texto el texto
     * @return un número de 0 a 255
     */
    static int hash(String texto) {
        int valor = 0;
        for (int i = 0; i < texto.length(); i++) {
            valor = (valor + texto.charAt(i)) * MULTIPLICADOR % MODULO;
        }
        return valor;
    }
}
