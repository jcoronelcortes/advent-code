package advent.code;

/**
 * Direcciones cardinales con sus vectores de movimiento.
 * Útil para problemas de grillas y navegación.
 */
public enum Direccion {

    ARRIBA   ( 0, -1),
    ABAJO    ( 0,  1),
    IZQUIERDA(-1,  0),
    DERECHA  ( 1,  0);

    /** Desplazamiento en el eje X. */
    private final int dx;

    /** Desplazamiento en el eje Y. */
    private final int dy;

    Direccion(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** @return desplazamiento en el eje X (columna) */
    public int dx() { return dx; }

    /** @return desplazamiento en el eje Y (fila) */
    public int dy() { return dy; }

    /** Retorna el vector de esta dirección como un {@link Punto}. */
    public Punto comoPunto() { return new Punto(dx, dy); }

    /** Rota 90° hacia la izquierda. */
    public Direccion giroIzquierda() {
        return switch (this) {
            case ARRIBA    -> IZQUIERDA;
            case IZQUIERDA -> ABAJO;
            case ABAJO     -> DERECHA;
            case DERECHA   -> ARRIBA;
        };
    }

    /** Rota 90° hacia la derecha. */
    public Direccion giroDerecha() {
        return switch (this) {
            case ARRIBA    -> DERECHA;
            case DERECHA   -> ABAJO;
            case ABAJO     -> IZQUIERDA;
            case IZQUIERDA -> ARRIBA;
        };
    }

    /** Retorna la dirección opuesta. */
    public Direccion opuesto() {
        return switch (this) {
            case ARRIBA    -> ABAJO;
            case ABAJO     -> ARRIBA;
            case IZQUIERDA -> DERECHA;
            case DERECHA   -> IZQUIERDA;
        };
    }
}