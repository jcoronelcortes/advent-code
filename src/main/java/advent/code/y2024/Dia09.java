package advent.code.y2024;

import advent.code.Resolvedor;

import java.util.Arrays;
import java.util.List;

/**
 * Año 2024 — Día 9: Desfragmentador de Disco
 *
 * <p>El input es un <b>mapa del disco</b> en formato denso: una sola línea de
 * dígitos que alternan entre el largo de un archivo y el largo de un espacio
 * libre. {@code 12345} es un archivo de 1 bloque, 2 libres, un archivo de 3,
 * 4 libres y un archivo de 5. Cada archivo tiene un ID según su orden,
 * empezando en 0.</p>
 */
public class Dia09 implements Resolvedor {

    /** Valor de un bloque libre en el arreglo de bloques. */
    static final int LIBRE = -1;

    /**
     * Parte 1: checksum del disco después de mover los bloques de archivo,
     * uno por uno desde el final, al primer bloque libre de la izquierda.
     *
     * @param lineas el mapa del disco (una sola línea)
     * @return el checksum
     */
    @Override
    public String parte1(List<String> lineas) {
        int[] bloques = expandir(mapa(lineas));
        compactarPorBloques(bloques);
        return String.valueOf(checksum(bloques));
    }

    /**
     * Parte 2: checksum después de mover <b>archivos enteros</b>: cada archivo,
     * del ID más alto al más bajo y una sola vez, va al tramo libre de más a la
     * izquierda donde entra completo, si hay alguno a su izquierda.
     *
     * @param lineas el mapa del disco (una sola línea)
     * @return el checksum
     */
    @Override
    public String parte2(List<String> lineas) {
        Disco disco = Disco.desdeMapa(mapa(lineas));
        disco.compactarPorArchivos();
        return String.valueOf(disco.checksum());
    }

    /**
     * Disco representado por <b>tramos</b> (inicio y largo) en vez de bloques
     * sueltos: los archivos se mueven completos, así que alcanza con saber
     * dónde empieza cada uno y dónde están los huecos.
     *
     * @param inicioArchivo dónde empieza cada archivo (índice = ID)
     * @param largoArchivo  cuántos bloques ocupa cada archivo
     * @param inicioLibre   dónde empieza cada tramo libre, de izquierda a derecha
     * @param largoLibre    cuántos bloques tiene cada tramo libre
     */
    record Disco(int[] inicioArchivo, int[] largoArchivo, int[] inicioLibre, int[] largoLibre) {

        /**
         * Arma los tramos a partir del mapa denso.
         *
         * @param mapa dígitos del mapa del disco
         * @return el disco
         */
        static Disco desdeMapa(String mapa) {
            int archivos = (mapa.length() + 1) / 2;
            int[] inicioArchivo = new int[archivos];
            int[] largoArchivo = new int[archivos];
            int[] inicioLibre = new int[mapa.length() / 2];
            int[] largoLibre = new int[mapa.length() / 2];
            int posicion = 0;
            for (int i = 0; i < mapa.length(); i++) {
                int largo = mapa.charAt(i) - '0';
                if (i % 2 == 0) {
                    inicioArchivo[i / 2] = posicion;
                    largoArchivo[i / 2] = largo;
                } else {
                    inicioLibre[i / 2] = posicion;
                    largoLibre[i / 2] = largo;
                }
                posicion += largo;
            }
            return new Disco(inicioArchivo, largoArchivo, inicioLibre, largoLibre);
        }

        /**
         * Mueve cada archivo, del ID más alto al más bajo, al primer tramo libre
         * de la izquierda donde entra completo.
         *
         * <p>El hueco que deja un archivo al moverse no hace falta registrarlo:
         * los archivos que faltan mover tienen IDs menores, están más a la
         * izquierda y solo se mueven hacia la izquierda, así que ese hueco
         * siempre les queda a la derecha y nunca lo usan.</p>
         *
         * <p>Para cada archivo se recorren los tramos libres de izquierda a
         * derecha, solo los que empiezan antes que el archivo: unas 10 000
         * búsquedas sobre unos 10 000 tramos en el peor caso, rápido en la
         * práctica porque la búsqueda se corta en cuanto encuentra lugar.</p>
         */
        void compactarPorArchivos() {
            for (int id = inicioArchivo.length - 1; id >= 0; id--) {
                int largo = largoArchivo[id];
                for (int tramo = 0; tramo < inicioLibre.length && inicioLibre[tramo] < inicioArchivo[id]; tramo++) {
                    if (largoLibre[tramo] >= largo) {
                        inicioArchivo[id] = inicioLibre[tramo];
                        inicioLibre[tramo] += largo;     // lo que sobra del tramo sigue libre
                        largoLibre[tramo] -= largo;
                        break;
                    }
                }
            }
        }

        /** @return el checksum: suma de posición × ID de cada bloque de archivo */
        long checksum() {
            long suma = 0;
            for (int id = 0; id < inicioArchivo.length; id++) {
                for (int bloque = 0; bloque < largoArchivo[id]; bloque++) {
                    suma += (long) (inicioArchivo[id] + bloque) * id;
                }
            }
            return suma;
        }
    }

    /**
     * Convierte el mapa denso en un bloque por posición: el ID del archivo, o
     * {@link #LIBRE}.
     *
     * <p>Ejemplo: {@code 12345} → {@code 0..111....22222} →
     * {@code [0, -1, -1, 1, 1, 1, -1, -1, -1, -1, 2, 2, 2, 2, 2]}.</p>
     *
     * @param mapa dígitos del mapa del disco
     * @return los bloques del disco
     */
    static int[] expandir(String mapa) {
        int total = mapa.chars().map(c -> c - '0').sum();
        int[] bloques = new int[total];
        int posicion = 0;
        for (int i = 0; i < mapa.length(); i++) {
            int largo = mapa.charAt(i) - '0';
            int valor = i % 2 == 0 ? i / 2 : LIBRE;     // posiciones pares: archivo con ID i/2
            Arrays.fill(bloques, posicion, posicion + largo, valor);
            posicion += largo;
        }
        return bloques;
    }

    /**
     * Mueve los bloques de archivo de a uno, del final al primer hueco libre
     * de la izquierda, hasta que no queden huecos entre archivos.
     *
     * <p>Dos punteros: {@code libre} avanza hasta el próximo hueco desde la
     * izquierda y {@code archivo} retrocede hasta el próximo bloque de archivo
     * desde la derecha. Mientras no se crucen, se intercambian. Es O(n) en
     * vez de buscar el hueco desde el principio en cada movimiento.</p>
     *
     * @param bloques los bloques del disco (se modifican)
     */
    static void compactarPorBloques(int[] bloques) {
        int libre = 0;
        int archivo = bloques.length - 1;
        while (true) {
            while (libre < bloques.length && bloques[libre] != LIBRE) libre++;
            while (archivo >= 0 && bloques[archivo] == LIBRE) archivo--;
            if (libre >= archivo) return;
            bloques[libre] = bloques[archivo];
            bloques[archivo] = LIBRE;
        }
    }

    /**
     * Suma de posición × ID de archivo de cada bloque, salteando los libres.
     * Se calcula en {@code long}: con ~95 000 bloques e IDs de hasta 9999
     * supera el rango de un {@code int}.
     *
     * @param bloques los bloques del disco
     * @return el checksum
     */
    static long checksum(int[] bloques) {
        long suma = 0;
        for (int posicion = 0; posicion < bloques.length; posicion++) {
            if (bloques[posicion] != LIBRE) suma += (long) posicion * bloques[posicion];
        }
        return suma;
    }

    /**
     * El mapa del disco: el input es una sola línea larga. Se ignoran los
     * espacios de los extremos y las líneas en blanco, y se valida que sean
     * solo dígitos (el enunciado advierte que es fácil pegarlo mal).
     *
     * @param lineas líneas del input
     * @return los dígitos del mapa
     */
    static String mapa(List<String> lineas) {
        String mapa = String.join("", lineas).strip();
        if (!mapa.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("El mapa del disco debe tener solo dígitos");
        }
        return mapa;
    }
}
