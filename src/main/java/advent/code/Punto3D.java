package advent.code;

import java.util.List;

/**
 * Punto en el espacio 3D con coordenadas enteras.
 *
 * <p>Las coordenadas son {@code long} porque en AoC suelen ser grandes y sus
 * productos (por ejemplo, la distancia al cuadrado) no caben en un {@code int}.</p>
 *
 * @param x coordenada X
 * @param y coordenada Y
 * @param z coordenada Z
 */
public record Punto3D(long x, long y, long z) {

    /**
     * Crea un punto a partir de un texto con tres números, por ejemplo
     * {@code "162,817,812"} o {@code "x=1, y=-2, z=3"}.
     *
     * @param texto texto con exactamente tres números enteros
     * @return el punto
     * @throws IllegalArgumentException si el texto no tiene tres números
     */
    public static Punto3D parsear(String texto) {
        List<Long> numeros = Utiles.leerNumerosLargos(texto);
        if (numeros.size() != 3) {
            throw new IllegalArgumentException("Se esperaban 3 coordenadas en '" + texto + "'");
        }
        return new Punto3D(numeros.get(0), numeros.get(1), numeros.get(2));
    }

    /**
     * Distancia euclidiana al cuadrado. Sirve para comparar distancias sin
     * usar {@code double} ni raíces: si {@code d1² < d2²} entonces
     * {@code d1 < d2}, y el resultado es exacto.
     *
     * @param otro otro punto
     * @return {@code dx² + dy² + dz²}
     */
    public long distanciaCuadrada(Punto3D otro) {
        long dx = x - otro.x;
        long dy = y - otro.y;
        long dz = z - otro.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Distancia de Manhattan.
     *
     * @param otro otro punto
     * @return {@code |dx| + |dy| + |dz|}
     */
    public long distanciaManhattan(Punto3D otro) {
        return Math.abs(x - otro.x) + Math.abs(y - otro.y) + Math.abs(z - otro.z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
