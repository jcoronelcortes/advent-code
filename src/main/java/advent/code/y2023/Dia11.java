package advent.code.y2023;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.List;

/**
 * Año 2023 — Día 11: Expansión Cósmica
 *
 * <p>El input es una imagen del cielo: espacio vacío ({@code .}) y galaxias
 * ({@code #}). Por la expansión del universo, cada fila y cada columna sin
 * galaxias es en realidad más grande. Hay que sumar las distancias (en pasos
 * horizontales y verticales) entre todos los pares de galaxias: en la
 * parte 1 cada fila o columna vacía vale por 2, y en la parte 2, por un
 * millón.</p>
 */
public class Dia11 implements Resolvedor {

    static final char GALAXIA = '#';

    /** En la parte 1, cada fila o columna vacía vale por 2. */
    static final long FACTOR_PARTE1 = 2;

    /** En la parte 2, cada fila o columna vacía vale por un millón. */
    static final long FACTOR_PARTE2 = 1_000_000;

    /**
     * Parte 1: la suma de las distancias entre todos los pares de galaxias,
     * con cada fila y columna vacía del doble de tamaño.
     *
     * @param lineas la imagen
     * @return la suma de las distancias
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(sumaDeDistancias(Grilla.desdeLineas(lineas), FACTOR_PARTE1));
    }

    /**
     * Parte 2: la misma suma, con cada fila y columna vacía un millón de
     * veces más grande.
     *
     * @param lineas la imagen
     * @return la suma de las distancias
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(sumaDeDistancias(Grilla.desdeLineas(lineas), FACTOR_PARTE2));
    }

    /**
     * La suma de las distancias entre todos los pares de galaxias, si cada
     * fila y cada columna vacía vale por {@code factor}.
     *
     * <p>No hace falta agrandar la grilla: la coordenada expandida de una
     * galaxia es la original más {@code (factor − 1)} por cada fila (o
     * columna) vacía que tiene antes. La distancia más corta entre dos
     * galaxias, moviéndose en horizontal y vertical, es la distancia
     * Manhattan entre sus coordenadas expandidas (pasar por otra galaxia no
     * molesta).</p>
     *
     * @param imagen la imagen
     * @param factor cuánto vale cada fila o columna vacía (al menos 1)
     * @return la suma de las distancias
     */
    static long sumaDeDistancias(Grilla imagen, long factor) {
        if (factor < 1) throw new IllegalArgumentException("El factor debe ser al menos 1: " + factor);
        List<Punto> galaxias = imagen.buscarTodos(GALAXIA);
        long[] filaExpandida = expandir(imagen.filas(), galaxias.stream().mapToInt(Punto::y).toArray(), factor);
        long[] columnaExpandida = expandir(imagen.columnas(), galaxias.stream().mapToInt(Punto::x).toArray(), factor);
        long suma = 0;
        for (int i = 0; i < galaxias.size(); i++) {
            for (int j = i + 1; j < galaxias.size(); j++) {
                Punto a = galaxias.get(i), b = galaxias.get(j);
                long distancia = Math.abs(filaExpandida[a.y()] - filaExpandida[b.y()])
                        + Math.abs(columnaExpandida[a.x()] - columnaExpandida[b.x()]);
                suma = Math.addExact(suma, distancia);
            }
        }
        return suma;
    }

    /**
     * La coordenada expandida de cada fila (o columna): cada una ocupa 1 si
     * tiene alguna galaxia y {@code factor} si está vacía, así que su
     * coordenada es la suma de lo que ocupan las anteriores.
     *
     * @param cantidad   cuántas filas (o columnas) hay
     * @param ocupadas   la fila (o columna) de cada galaxia
     * @param factor     cuánto vale cada una vacía
     * @return la coordenada expandida de cada índice
     */
    static long[] expandir(int cantidad, int[] ocupadas, long factor) {
        boolean[] tieneGalaxia = new boolean[cantidad];
        for (int indice : ocupadas) tieneGalaxia[indice] = true;
        long[] expandida = new long[cantidad];
        long posicion = 0;
        for (int i = 0; i < cantidad; i++) {
            expandida[i] = posicion;
            posicion = Math.addExact(posicion, tieneGalaxia[i] ? 1 : factor);
        }
        return expandida;
    }
}
