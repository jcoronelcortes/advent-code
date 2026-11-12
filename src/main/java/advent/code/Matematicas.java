package advent.code;

import com.google.common.math.LongMath;

import java.math.BigInteger;
import java.util.Collection;

/**
 * Funciones matemáticas recurrentes en Advent of Code: divisibilidad,
 * aritmética modular, series, dígitos y teoría de números.
 *
 * <p>Todas trabajan con {@code long}, ya que los inputs de AoC suelen
 * exceder el rango de {@code int}.</p>
 */
public final class Matematicas {

    private Matematicas() {}

    // -------------------------------------------------------------------------
    // Divisibilidad
    // -------------------------------------------------------------------------

    /**
     * Máximo común divisor de dos números (acepta negativos; el resultado es no negativo).
     *
     * @param a primer número
     * @param b segundo número
     * @return mcd(a, b)
     */
    public static long mcd(long a, long b) {
        return LongMath.gcd(Math.abs(a), Math.abs(b));
    }

    /**
     * Mínimo común múltiplo de dos números. Lanza {@link ArithmeticException}
     * si el resultado no cabe en un {@code long}.
     *
     * @param a primer número
     * @param b segundo número
     * @return mcm(a, b), o 0 si alguno es 0
     */
    public static long mcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(Math.multiplyExact(a / mcd(a, b), b));
    }

    /**
     * Mínimo común múltiplo de una colección de números. Típico en problemas
     * donde varios ciclos deben coincidir (ej. AoC 2023 día 8).
     *
     * @param numeros números a combinar
     * @return mcm de todos los números (1 si la colección está vacía)
     */
    public static long mcm(Collection<Long> numeros) {
        long resultado = 1;
        for (long numero : numeros) {
            resultado = mcm(resultado, numero);
        }
        return resultado;
    }

    /**
     * División entera redondeando hacia arriba, correcta también con negativos.
     *
     * @param dividendo dividendo
     * @param divisor   divisor (distinto de 0)
     * @return ⌈dividendo / divisor⌉
     */
    public static long dividirHaciaArriba(long dividendo, long divisor) {
        return -Math.floorDiv(-dividendo, divisor);
    }

    // -------------------------------------------------------------------------
    // Aritmética modular
    // -------------------------------------------------------------------------

    /**
     * Módulo siempre no negativo (a diferencia de {@code %}, que en Java
     * puede devolver negativos). Útil para posiciones en estructuras circulares.
     *
     * <p>Ejemplo: {@code modulo(-18, 100) = 82}.</p>
     *
     * @param a       número
     * @param modulo  módulo (positivo)
     * @return valor en {@code [0, modulo)}
     */
    public static long modulo(long a, long modulo) {
        return Math.floorMod(a, modulo);
    }

    /**
     * Calcula {@code (base ^ exponente) mod modulo} por exponenciación rápida.
     *
     * @param base      base
     * @param exponente exponente (no negativo)
     * @param modulo    módulo (positivo; los productos intermedios no desbordan)
     * @return resultado en {@code [0, modulo)}
     */
    public static long potenciaModular(long base, long exponente, long modulo) {
        if (exponente < 0) throw new IllegalArgumentException("Exponente negativo: " + exponente);

        long resultado = 1 % modulo;
        long actual = modulo(base, modulo);
        while (exponente > 0) {
            if ((exponente & 1) == 1) resultado = multiplicarModular(resultado, actual, modulo);
            actual = multiplicarModular(actual, actual, modulo);
            exponente >>= 1;
        }
        return resultado;
    }

    /**
     * Inverso multiplicativo de {@code a} módulo {@code modulo}, usando el
     * algoritmo extendido de Euclides.
     *
     * @param a      número
     * @param modulo módulo (positivo)
     * @return {@code x} en {@code [0, modulo)} tal que {@code a·x ≡ 1 (mod modulo)}
     * @throws ArithmeticException si {@code a} y {@code modulo} no son coprimos
     */
    public static long inversoModular(long a, long modulo) {
        long[] resultado = euclidesExtendido(modulo(a, modulo), modulo);
        if (resultado[0] != 1) {
            throw new ArithmeticException(a + " no tiene inverso módulo " + modulo);
        }
        return modulo(resultado[1], modulo);
    }

    /**
     * Teorema Chino del Resto: encuentra el menor {@code x ≥ 0} tal que
     * {@code x ≡ restos[i] (mod modulos[i])} para todo {@code i}. Los módulos
     * no necesitan ser coprimos.
     *
     * <p>Típico en problemas de "cuándo se alinean todos los ciclos"
     * (ej. AoC 2020 día 13).</p>
     *
     * @param restos  restos buscados
     * @param modulos módulos (positivos), mismo largo que {@code restos}
     * @return menor solución no negativa
     * @throws ArithmeticException si el sistema no tiene solución
     */
    public static long restoChino(long[] restos, long[] modulos) {
        if (restos.length != modulos.length) {
            throw new IllegalArgumentException("restos y modulos deben tener el mismo largo");
        }

        long solucion = 0;
        long moduloAcumulado = 1;
        for (int i = 0; i < restos.length; i++) {
            long resto = modulo(restos[i], modulos[i]);
            long[] egcd = euclidesExtendido(moduloAcumulado, modulos[i]);
            long divisor = egcd[0];

            long diferencia = resto - solucion;
            if (diferencia % divisor != 0) {
                throw new ArithmeticException("El sistema de congruencias no tiene solución");
            }

            long moduloReducido = modulos[i] / divisor;
            long paso = multiplicarModular(modulo(diferencia / divisor, moduloReducido),
                    modulo(egcd[1], moduloReducido), moduloReducido);

            long nuevoModulo = Math.multiplyExact(moduloAcumulado, moduloReducido);
            solucion = modulo(solucion + multiplicarModular(moduloAcumulado, paso, nuevoModulo), nuevoModulo);
            moduloAcumulado = nuevoModulo;
        }
        return solucion;
    }

    // -------------------------------------------------------------------------
    // Aritmética exacta
    // -------------------------------------------------------------------------

    /**
     * Suma con detección de desborde: si el resultado no cabe en un
     * {@code long}, lanza {@link ArithmeticException} en vez de dar un número
     * incorrecto en silencio. En AoC las respuestas suelen rondar 10¹⁵.
     *
     * @param valores valores a sumar
     * @return la suma (0 si no hay valores)
     * @throws ArithmeticException si se desborda
     */
    public static long sumaExacta(long... valores) {
        long total = 0;
        for (long valor : valores) total = Math.addExact(total, valor);
        return total;
    }

    /**
     * Igual que {@link #sumaExacta(long...)}, para una colección.
     *
     * @param valores valores a sumar
     * @return la suma (0 si está vacía)
     * @throws ArithmeticException si se desborda
     */
    public static long sumaExacta(Collection<Long> valores) {
        long total = 0;
        for (long valor : valores) total = Math.addExact(total, valor);
        return total;
    }

    /**
     * Producto con detección de desborde.
     *
     * @param valores valores a multiplicar
     * @return el producto (1 si no hay valores)
     * @throws ArithmeticException si se desborda
     */
    public static long productoExacto(long... valores) {
        long total = 1;
        for (long valor : valores) total = Math.multiplyExact(total, valor);
        return total;
    }

    /**
     * Igual que {@link #productoExacto(long...)}, para una colección.
     *
     * @param valores valores a multiplicar
     * @return el producto (1 si está vacía)
     * @throws ArithmeticException si se desborda
     */
    public static long productoExacto(Collection<Long> valores) {
        long total = 1;
        for (long valor : valores) total = Math.multiplyExact(total, valor);
        return total;
    }

    // -------------------------------------------------------------------------
    // Series y dígitos
    // -------------------------------------------------------------------------

    /**
     * Suma de todos los enteros entre {@code desde} y {@code hasta} (inclusivos).
     *
     * @param desde primer número
     * @param hasta último número
     * @return {@code desde + (desde+1) + ... + hasta}, o 0 si {@code desde > hasta}
     */
    public static long sumaAritmetica(long desde, long hasta) {
        if (desde > hasta) return 0;
        long cantidad = hasta - desde + 1;
        long suma = desde + hasta;
        // dividir antes de multiplicar el factor par para reducir el riesgo de desborde
        return (cantidad % 2 == 0)
                ? Math.multiplyExact(cantidad / 2, suma)
                : Math.multiplyExact(cantidad, suma / 2);
    }

    /**
     * Calcula {@code 10^exponente}, lanzando error si no cabe en un {@code long}.
     *
     * @param exponente exponente entre 0 y 18
     * @return 10 elevado al exponente
     */
    public static long potenciaDeDiez(int exponente) {
        return LongMath.checkedPow(10, exponente);
    }

    /**
     * Cantidad de dígitos decimales de un número (sin contar el signo).
     *
     * @param numero número
     * @return cantidad de dígitos ({@code 1} para el 0)
     */
    public static int cantidadDigitos(long numero) {
        if (numero == Long.MIN_VALUE) return 19;
        return String.valueOf(Math.abs(numero)).length();
    }

    // -------------------------------------------------------------------------
    // Teoría de números
    // -------------------------------------------------------------------------

    /**
     * Indica si un número es primo.
     *
     * @param numero número
     * @return {@code true} si es primo
     */
    public static boolean esPrimo(long numero) {
        return numero >= 2 && LongMath.isPrime(numero);
    }

    /**
     * Función de Möbius: 0 si {@code n} tiene un factor primo repetido;
     * si no, {@code (−1)^k} con {@code k} la cantidad de factores primos distintos.
     *
     * <p>Se usa en inclusión-exclusión sobre divisores (ver Día 2 de 2025).</p>
     *
     * @param n entero positivo
     * @return μ(n)
     */
    public static int mobius(long n) {
        if (n <= 0) throw new IllegalArgumentException("n debe ser positivo: " + n);

        int resultado = 1;
        for (long primo = 2; primo * primo <= n; primo++) {
            if (n % primo != 0) continue;
            n /= primo;
            if (n % primo == 0) return 0;
            resultado = -resultado;
        }
        return (n > 1) ? -resultado : resultado;
    }

    // -------------------------------------------------------------------------
    // Auxiliares internos
    // -------------------------------------------------------------------------

    /** Devuelve {@code [mcd, x, y]} tales que {@code a·x + b·y = mcd(a, b)}. */
    private static long[] euclidesExtendido(long a, long b) {
        long rAnterior = a, r = b;
        long xAnterior = 1, x = 0;
        long yAnterior = 0, y = 1;
        while (r != 0) {
            long cociente = rAnterior / r;
            long temporal;
            temporal = r; r = rAnterior - cociente * r; rAnterior = temporal;
            temporal = x; x = xAnterior - cociente * x; xAnterior = temporal;
            temporal = y; y = yAnterior - cociente * y; yAnterior = temporal;
        }
        return new long[]{rAnterior, xAnterior, yAnterior};
    }

    /** Calcula {@code (a · b) mod modulo} sin desbordar, con {@code a, b} en {@code [0, modulo)}. */
    private static long multiplicarModular(long a, long b, long modulo) {
        return BigInteger.valueOf(a)
                .multiply(BigInteger.valueOf(b))
                .mod(BigInteger.valueOf(modulo))
                .longValueExact();
    }
}
