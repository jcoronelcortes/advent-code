package advent.code.y2024;

import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Arrays;
import java.util.List;

/**
 * Año 2024 — Día 22: Mercado de Monos
 *
 * <p>Cada comprador del mercado genera una secuencia pseudoaleatoria de
 * números secretos: cada uno se obtiene del anterior con tres pasos de
 * multiplicar o dividir por una potencia de 2, mezclar (XOR) y podar (módulo
 * 2<sup>24</sup>). El input tiene el número secreto inicial de cada
 * comprador, uno por línea. En la parte 2, el precio de cada comprador es la
 * última cifra de su número secreto, y un mono vende la primera vez que ve
 * una secuencia elegida de cuatro cambios de precio.</p>
 */
public class Dia22 implements Resolvedor {

    /** Cuántos números secretos nuevos genera cada comprador en un día. */
    static final int NUEVOS_POR_DIA = 2000;

    /** Módulo de la poda: 16777216 = 2^24. */
    static final long PODA = 16_777_216;

    /** Cuántos cambios de precio consecutivos mira el mono. */
    static final int LARGO_SECUENCIA = 4;

    /** Cantidad de valores posibles de un cambio de precio: de −9 a 9. */
    static final int VALORES_DE_CAMBIO = 19;

    /** Cantidad de secuencias de cuatro cambios distintas: 19^4. */
    static final int SECUENCIAS = VALORES_DE_CAMBIO * VALORES_DE_CAMBIO * VALORES_DE_CAMBIO * VALORES_DE_CAMBIO;

    /**
     * Parte 1: la suma, sobre todos los compradores, del número secreto
     * número 2000 que genera cada uno.
     *
     * @param lineas el número secreto inicial de cada comprador
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        long suma = Utiles.leerNumerosLargos(lineas).stream()
                .mapToLong(secreto -> despuesDe(secreto, NUEVOS_POR_DIA))
                .sum();
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la mayor cantidad de bananas que se puede conseguir eligiendo
     * una sola secuencia de cuatro cambios de precio para todos los
     * compradores.
     *
     * @param lineas el número secreto inicial de cada comprador
     * @return la mayor cantidad de bananas
     */
    @Override
    public String parte2(List<String> lineas) {
        long[] bananas = bananasPorSecuencia(Utiles.leerNumerosLargos(lineas), NUEVOS_POR_DIA);
        return String.valueOf(Arrays.stream(bananas).max().orElse(0));
    }

    /**
     * Cuántas bananas consigue el mono con cada secuencia posible de cuatro
     * cambios, sumando sobre todos los compradores.
     *
     * <p>En vez de probar las 130321 secuencias una por una (cada una
     * recorriendo a todos los compradores), se recorre cada comprador una sola
     * vez: en cada precio, los últimos cuatro cambios forman una secuencia, y
     * si es la <b>primera</b> vez que ese comprador la muestra, ese es el
     * precio al que el mono le vendería con esa secuencia. Se suma a la
     * secuencia en un acumulador común.</p>
     *
     * <p>Cada secuencia se codifica como un número en base 19 (cada cambio
     * corrido a 0–18), que se actualiza en cada paso descartando el cambio
     * más viejo. Para saber si un comprador ya mostró una secuencia, se
     * guarda en {@code ultimoComprador} el último comprador que la mostró:
     * así no hace falta limpiar un conjunto por cada comprador.</p>
     *
     * @param iniciales los números secretos iniciales
     * @param nuevos    cuántos números nuevos genera cada comprador
     * @return bananas por secuencia, indexado por {@link #codificar}
     */
    static long[] bananasPorSecuencia(List<Long> iniciales, int nuevos) {
        long[] bananas = new long[SECUENCIAS];
        int[] ultimoComprador = new int[SECUENCIAS];
        Arrays.fill(ultimoComprador, -1);
        for (int comprador = 0; comprador < iniciales.size(); comprador++) {
            long secreto = iniciales.get(comprador);
            int precio = precio(secreto);
            int secuencia = 0;
            for (int i = 1; i <= nuevos; i++) {
                secreto = siguiente(secreto);
                int nuevoPrecio = precio(secreto);
                secuencia = (secuencia * VALORES_DE_CAMBIO + (nuevoPrecio - precio + 9)) % SECUENCIAS;
                precio = nuevoPrecio;
                if (i >= LARGO_SECUENCIA && ultimoComprador[secuencia] != comprador) {
                    ultimoComprador[secuencia] = comprador;
                    bananas[secuencia] += precio;
                }
            }
        }
        return bananas;
    }

    /**
     * El número de una secuencia de cuatro cambios en el acumulador.
     *
     * @param cambios cuatro cambios de precio, entre −9 y 9
     * @return el índice, entre 0 y 19^4 − 1
     */
    static int codificar(int... cambios) {
        if (cambios.length != LARGO_SECUENCIA) {
            throw new IllegalArgumentException("Se esperaban " + LARGO_SECUENCIA + " cambios");
        }
        int codigo = 0;
        for (int cambio : cambios) {
            if (cambio < -9 || cambio > 9) throw new IllegalArgumentException("Cambio fuera de rango: " + cambio);
            codigo = codigo * VALORES_DE_CAMBIO + cambio + 9;
        }
        return codigo;
    }

    /** El precio que ofrece un comprador: la última cifra de su número secreto. */
    static int precio(long secreto) {
        return (int) (secreto % 10);
    }

    /**
     * El siguiente número secreto, con los tres pasos del enunciado. Cada uno
     * calcula un valor, lo mezcla (XOR) con el secreto y poda el resultado.
     *
     * <p>Como 64, 32 y 2048 son potencias de 2, multiplicar y dividir son
     * corrimientos de bits, y como 16777216 es 2<sup>24</sup>, podar es
     * quedarse con los 24 bits bajos. Se escriben como en el enunciado para
     * poder compararlos línea por línea; una prueba comprueba que dan lo
     * mismo que la versión con corrimientos y máscara.</p>
     *
     * @param secreto el número secreto actual (entre 0 y 2^24 − 1)
     * @return el siguiente
     */
    static long siguiente(long secreto) {
        secreto = podar(mezclar(secreto, secreto * 64));
        secreto = podar(mezclar(secreto, secreto / 32));
        secreto = podar(mezclar(secreto, secreto * 2048));
        return secreto;
    }

    /**
     * El número secreto después de generar {@code cantidad} nuevos.
     *
     * @param secreto  el número secreto inicial
     * @param cantidad cuántos números nuevos generar
     * @return el último generado
     */
    static long despuesDe(long secreto, int cantidad) {
        for (int i = 0; i < cantidad; i++) secreto = siguiente(secreto);
        return secreto;
    }

    /** Mezclar un valor en el secreto: XOR bit a bit. */
    static long mezclar(long secreto, long valor) {
        return secreto ^ valor;
    }

    /** Podar el secreto: módulo 16777216. */
    static long podar(long secreto) {
        return secreto % PODA;
    }
}
