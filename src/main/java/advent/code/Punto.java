package advent.code;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Punto inmutable en un plano 2D con operaciones comunes para AoC.
 */
public final class Punto {

    public final int x;
    public final int y;

    public Punto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Mueve el punto una posición en la dirección indicada. */
    public Punto mover(Direccion direccion) {
        return new Punto(x + direccion.getDx(), y + direccion.getDy());
    }

    /** Suma otro punto (útil para aplicar deltas). */
    public Punto sumar(Punto otro) {
        return new Punto(x + otro.x, y + otro.y);
    }

    /** Resta otro punto. */
    public Punto restar(Punto otro) {
        return new Punto(x - otro.x, y - otro.y);
    }

    /** Escala el punto por un factor. */
    public Punto escalar(int factor) {
        return new Punto(x * factor, y * factor);
    }

    /** Distancia Manhattan hacia otro punto. */
    public int distanciaManhattan(Punto otro) {
        return Math.abs(x - otro.x) + Math.abs(y - otro.y);
    }

    /**
     * Vecinos en las 4 direcciones cardinales (arriba, abajo, izquierda, derecha).
     */
    public List<Punto> vecinos() {
        return Arrays.stream(Direccion.values())
                .map(this::mover)
                .toList();
    }

    /**
     * Vecinos en las 8 direcciones (cardinales + diagonales).
     */
    public List<Punto> vecinosDiagonales() {
        return List.of(
                new Punto(x - 1, y - 1), new Punto(x, y - 1), new Punto(x + 1, y - 1),
                new Punto(x - 1, y),                           new Punto(x + 1, y),
                new Punto(x - 1, y + 1), new Punto(x, y + 1), new Punto(x + 1, y + 1)
        );
    }

    /** Verifica si el punto está dentro de los límites de una grilla. */
    public boolean dentroDeGrilla(int filas, int columnas) {
        return x >= 0 && x < columnas && y >= 0 && y < filas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Punto punto)) return false;
        return x == punto.x && y == punto.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}