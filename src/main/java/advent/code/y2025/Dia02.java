package advent.code.y2025;

import advent.code.Resolvedor;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2025 — Día 2: Tienda de Regalos
 *
 * <p>El input es una lista de rangos de IDs de producto separados por comas
 * ({@code inicio-fin}). Se buscan los IDs <em>inválidos</em>, que son los
 * formados por una secuencia de dígitos repetida:</p>
 * <ul>
 *   <li>Parte 1: repetida exactamente dos veces (ej. {@code 6464}).</li>
 *   <li>Parte 2: repetida al menos dos veces (ej. {@code 6464}, {@code 111}, {@code 121212}).</li>
 * </ul>
 *
 * <p>En lugar de recorrer los rangos, los IDs inválidos se generan con
 * aritmética: un número de {@code L} dígitos formado por un bloque {@code x}
 * de {@code p} dígitos repetido {@code L/p} veces es igual a
 * {@code x · (10^L − 1) / (10^p − 1)}.</p>
 */
public class Dia02 implements Resolvedor {

    /**
     * Rango cerrado de IDs {@code [inicio, fin]}.
     *
     * @param inicio primer ID del rango (inclusivo)
     * @param fin    último ID del rango (inclusivo)
     */
    record Rango(long inicio, long fin) {}

    /** Máxima cantidad de dígitos soportada (los IDs deben caber en un {@code long}). */
    private static final int MAX_DIGITOS = 18;

    /**
     * Parte 1: suma todos los IDs formados por una secuencia repetida exactamente dos veces.
     *
     * @param lineas input con los rangos separados por comas (puede venir en una o varias líneas)
     * @return suma de todos los IDs inválidos
     */
    @Override
    public String parte1(List<String> lineas) {
        long suma = 0;
        for (Rango rango : leerRangos(lineas)) {
            suma += sumarInvalidosEnRango(rango.inicio(), rango.fin());
        }
        return String.valueOf(suma);
    }

    /**
     * Parte 2: suma todos los IDs formados por una secuencia repetida al menos dos veces.
     *
     * @param lineas input con los rangos separados por comas (puede venir en una o varias líneas)
     * @return suma de todos los IDs inválidos con la nueva regla
     */
    @Override
    public String parte2(List<String> lineas) {
        long suma = 0;
        for (Rango rango : leerRangos(lineas)) {
            suma += sumarRepetidosEnRango(rango.inicio(), rango.fin());
        }
        return String.valueOf(suma);
    }

    /**
     * Convierte el input en una lista de rangos. Une todas las líneas para
     * soportar el input "envuelto" (como en el ejemplo del enunciado) y
     * separa por comas, ignorando entradas vacías.
     *
     * @param lineas líneas del input
     * @return rangos en el orden en que aparecen
     */
    static List<Rango> leerRangos(List<String> lineas) {
        List<Rango> rangos = new ArrayList<>();
        for (String parte : String.join("", lineas).split(",")) {
            parte = parte.trim();
            if (!parte.matches("\\d+-\\d+")) continue;

            String[] extremos = parte.split("-");
            rangos.add(new Rango(NumberUtils.toLong(extremos[0]), NumberUtils.toLong(extremos[1])));
        }
        return rangos;
    }

    /**
     * Parte 1 — suma los IDs de {@code [inicio, fin]} formados por un bloque
     * repetido exactamente dos veces: para cada largo par {@code L}, son los
     * números con bloque de {@code L/2} dígitos.
     *
     * @param inicio primer ID del rango (inclusivo)
     * @param fin    último ID del rango (inclusivo)
     * @return suma de los IDs inválidos del rango
     */
    static long sumarInvalidosEnRango(long inicio, long fin) {
        long suma = 0;
        for (int largo = cantidadDigitos(inicio); largo <= cantidadDigitos(fin); largo++) {
            if (largo % 2 == 0) {
                suma += sumarConBloque(inicio, fin, largo, largo / 2);
            }
        }
        return suma;
    }

    /**
     * Parte 2 — suma los IDs de {@code [inicio, fin]} formados por un bloque
     * repetido al menos dos veces.
     *
     * <p>Un mismo número puede escribirse con varios bloques (ej. {@code 111111}
     * = "1"×6 = "11"×3 = "111"×2), así que para no contarlo varias veces se usa
     * inclusión-exclusión sobre las repeticiones {@code d} (divisores de
     * {@code L} mayores a 1):</p>
     *
     * <pre>  suma(L) = Σ  −μ(d) · S(L, L/d)</pre>
     *
     * <p>donde {@code μ} es la función de Möbius y {@code S(L, p)} es la suma
     * de los números de {@code L} dígitos con bloque de {@code p} dígitos.</p>
     *
     * @param inicio primer ID del rango (inclusivo)
     * @param fin    último ID del rango (inclusivo)
     * @return suma de los IDs inválidos del rango con la regla de la parte 2
     */
    static long sumarRepetidosEnRango(long inicio, long fin) {
        long suma = 0;
        for (int largo = cantidadDigitos(inicio); largo <= cantidadDigitos(fin); largo++) {
            for (int repeticiones = 2; repeticiones <= largo; repeticiones++) {
                if (largo % repeticiones != 0) continue;

                int mobius = mobius(repeticiones);
                if (mobius == 0) continue;

                suma -= mobius * sumarConBloque(inicio, fin, largo, largo / repeticiones);
            }
        }
        return suma;
    }

    /**
     * Suma los números de {@code [inicio, fin]} que tienen exactamente
     * {@code largo} dígitos y están formados por un bloque de {@code bloque}
     * dígitos repetido ({@code bloque} debe dividir a {@code largo}).
     *
     * <p>Esos números son {@code x · multiplicador}, con
     * {@code multiplicador = (10^largo − 1) / (10^bloque − 1)} (ej. {@code 1001}
     * para 6 dígitos y bloque 3; {@code 10101} para 6 dígitos y bloque 2) y
     * {@code x} un número de exactamente {@code bloque} dígitos. Los {@code x}
     * válidos forman un intervalo continuo, que se suma con la serie aritmética.</p>
     *
     * @param inicio primer ID del rango (inclusivo)
     * @param fin    último ID del rango (inclusivo)
     * @param largo  cantidad total de dígitos del ID
     * @param bloque cantidad de dígitos del bloque que se repite
     * @return suma de los números que cumplen la condición
     */
    static long sumarConBloque(long inicio, long fin, int largo, int bloque) {
        long potenciaBloque = potenciaDeDiez(bloque);
        long multiplicador = (potenciaDeDiez(largo) - 1) / (potenciaBloque - 1);

        // x debe tener exactamente "bloque" dígitos (sin ceros a la izquierda)
        long xMinimo = Math.max(potenciaBloque / 10, dividirHaciaArriba(inicio, multiplicador));
        long xMaximo = Math.min(potenciaBloque - 1, fin / multiplicador);

        if (xMinimo > xMaximo) return 0;

        long cantidad = xMaximo - xMinimo + 1;
        long sumaX = (xMinimo + xMaximo) * cantidad / 2;
        return Math.multiplyExact(sumaX, multiplicador);
    }

    /**
     * Parte 1 — indica si un ID está formado por una secuencia repetida exactamente dos veces.
     *
     * @param id ID de producto
     * @return {@code true} si el ID es inválido
     */
    static boolean esInvalido(long id) {
        String texto = String.valueOf(id);
        int largo = texto.length();
        if (largo % 2 != 0) return false;

        int mitad = largo / 2;
        return texto.substring(0, mitad).equals(texto.substring(mitad));
    }

    /**
     * Parte 2 — indica si un ID está formado por una secuencia repetida al menos dos veces.
     *
     * <p>Truco clásico: un texto {@code s} es una repetición de un bloque si y
     * solo si aparece dentro de {@code s + s} en una posición distinta de 0 y
     * del final.</p>
     *
     * @param id ID de producto
     * @return {@code true} si el ID es inválido con la regla de la parte 2
     */
    static boolean esRepetido(long id) {
        String texto = String.valueOf(id);
        return (texto + texto).indexOf(texto, 1) < texto.length();
    }

    /**
     * Función de Möbius: 0 si {@code n} tiene un factor primo repetido;
     * si no, {@code (−1)^k} con {@code k} la cantidad de factores primos.
     *
     * @param n entero positivo
     * @return μ(n)
     */
    static int mobius(int n) {
        int resultado = 1;
        for (int primo = 2; primo * primo <= n; primo++) {
            if (n % primo != 0) continue;
            n /= primo;
            if (n % primo == 0) return 0;
            resultado = -resultado;
        }
        return (n > 1) ? -resultado : resultado;
    }

    /** Cantidad de dígitos de un número no negativo. */
    private static int cantidadDigitos(long numero) {
        return String.valueOf(numero).length();
    }

    /** Calcula 10^exponente. */
    private static long potenciaDeDiez(int exponente) {
        if (exponente > MAX_DIGITOS) {
            throw new IllegalArgumentException("Demasiados dígitos: " + exponente);
        }
        long resultado = 1;
        for (int i = 0; i < exponente; i++) resultado *= 10;
        return resultado;
    }

    /** División entera redondeando hacia arriba (para valores no negativos). */
    private static long dividirHaciaArriba(long dividendo, long divisor) {
        return (dividendo + divisor - 1) / divisor;
    }
}
