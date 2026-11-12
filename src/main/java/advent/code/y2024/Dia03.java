package advent.code.y2024;

import advent.code.Resolvedor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Año 2024 — Día 3: Dale Vueltas
 *
 * <p>El input es la memoria corrupta de un programa: texto con instrucciones
 * {@code mul(X,Y)} válidas mezcladas con basura. Solo cuentan las
 * instrucciones escritas exactamente así, con X e Y de 1 a 3 dígitos.</p>
 */
public class Dia03 implements Resolvedor {

    /**
     * Instrucción {@code mul(X,Y)} exacta: sin espacios, con dos números de 1
     * a 3 dígitos. Cualquier variación ({@code mul(4*}, {@code mul ( 2 , 4 )},
     * {@code mul[3,7]}) no coincide y se ignora.
     */
    private static final Pattern MULTIPLICACION = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");

    /**
     * Cualquiera de las tres instrucciones de la parte 2: {@code mul(X,Y)},
     * {@code do()} o {@code don't()}. Al buscarlas con una sola expresión, las
     * coincidencias salen en el orden en que aparecen en la memoria.
     */
    private static final Pattern INSTRUCCION = Pattern.compile(
            "mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");

    /**
     * Parte 1: suma de los resultados de todas las instrucciones
     * {@code mul} válidas de la memoria.
     *
     * @param lineas la memoria corrupta (puede ocupar varias líneas)
     * @return la suma de las multiplicaciones
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(sumarMultiplicaciones(memoria(lineas)));
    }

    /**
     * Parte 2: igual que la parte 1, pero respetando las instrucciones
     * condicionales: {@code do()} habilita las multiplicaciones siguientes y
     * {@code don't()} las deshabilita. Al comienzo están habilitadas.
     *
     * @param lineas la memoria corrupta (puede ocupar varias líneas)
     * @return la suma de las multiplicaciones habilitadas
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(sumarMultiplicacionesHabilitadas(memoria(lineas)));
    }

    /**
     * Recorre las instrucciones en orden y suma los productos de las
     * {@code mul} que aparecen mientras las multiplicaciones están
     * habilitadas. Solo importa la última {@code do()} o {@code don't()}
     * vista, y el estado sigue de una línea a la siguiente porque la memoria
     * se trata como un solo texto.
     *
     * @param memoria texto de la memoria
     * @return la suma de los productos habilitados
     */
    static long sumarMultiplicacionesHabilitadas(String memoria) {
        long total = 0;
        boolean habilitado = true;
        Matcher instruccion = INSTRUCCION.matcher(memoria);
        while (instruccion.find()) {
            switch (instruccion.group()) {
                case "do()" -> habilitado = true;
                case "don't()" -> habilitado = false;
                default -> {
                    if (habilitado) {
                        total += Long.parseLong(instruccion.group(1)) * Long.parseLong(instruccion.group(2));
                    }
                }
            }
        }
        return total;
    }

    /**
     * Suma los productos de todas las instrucciones {@code mul(X,Y)} válidas.
     *
     * @param memoria texto de la memoria
     * @return la suma de los productos
     */
    static long sumarMultiplicaciones(String memoria) {
        long total = 0;
        Matcher instruccion = MULTIPLICACION.matcher(memoria);
        while (instruccion.find()) {
            total += Long.parseLong(instruccion.group(1)) * Long.parseLong(instruccion.group(2));
        }
        return total;
    }

    /**
     * Une las líneas del input en un solo texto, conservando los saltos de
     * línea. La memoria es una sola secuencia y el salto de línea es un
     * carácter más: una instrucción cortada por él (como
     * {@code mul(1\n2,3)}) ya no está escrita exactamente {@code mul(X,Y)} y
     * no cuenta. Tratarla como un solo texto también evita procesar cada
     * línea por separado, por si algún estado debe pasar de una línea a la
     * siguiente.
     *
     * @param lineas líneas del input
     * @return la memoria completa
     */
    static String memoria(List<String> lineas) {
        return String.join("\n", lineas);
    }
}
