package advent.code.y2023;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Año 2023 — Día 16: El Suelo Será Lava
 *
 * <p>El input es un artefacto cuadrado con espacios vacíos ({@code .}),
 * espejos ({@code /} y {@code \}) y divisores ({@code |} y {@code -}). Un
 * haz de luz entra por la esquina superior izquierda yendo a la derecha y
 * rebota por la grilla; cada casilla por la que pasa al menos un haz queda
 * energizada.</p>
 */
public class Dia16 implements Resolvedor {

    /**
     * Un haz en una casilla, moviéndose en una dirección.
     *
     * @param posicion  la casilla
     * @param direccion hacia dónde va
     */
    record Haz(Punto posicion, Direccion direccion) {
    }

    /**
     * Parte 1: cuántas casillas quedan energizadas con el haz entrando por la
     * esquina superior izquierda hacia la derecha.
     *
     * @param lineas el artefacto
     * @return la cantidad de casillas energizadas
     */
    @Override
    public String parte1(List<String> lineas) {
        Grilla artefacto = Grilla.desdeLineas(lineas);
        return String.valueOf(energizadas(artefacto, new Haz(new Punto(0, 0), Direccion.DERECHA)));
    }

    /**
     * Parte 2: la mayor cantidad de casillas energizadas eligiendo por dónde
     * entra el haz: cualquier casilla del borde, alejándose de ese borde.
     * Se prueban todas, porque cada recorrido es lineal en el tamaño de la
     * grilla.
     *
     * @param lineas el artefacto
     * @return la mayor cantidad de casillas energizadas
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla artefacto = Grilla.desdeLineas(lineas);
        return String.valueOf(entradasDesdeLosBordes(artefacto.filas(), artefacto.columnas()).stream()
                .mapToInt(entrada -> energizadas(artefacto, entrada))
                .max()
                .orElseThrow());
    }

    /**
     * Todos los haces que pueden entrar desde un borde: por la fila de arriba
     * hacia abajo, por la de abajo hacia arriba, por la columna izquierda
     * hacia la derecha y por la derecha hacia la izquierda. Las esquinas
     * aparecen dos veces, una por cada dirección posible.
     *
     * @param filas    cantidad de filas
     * @param columnas cantidad de columnas
     * @return los {@code 2 × (filas + columnas)} haces de entrada
     */
    static List<Haz> entradasDesdeLosBordes(int filas, int columnas) {
        List<Haz> entradas = new ArrayList<>();
        for (int x = 0; x < columnas; x++) {
            entradas.add(new Haz(new Punto(x, 0), Direccion.ABAJO));
            entradas.add(new Haz(new Punto(x, filas - 1), Direccion.ARRIBA));
        }
        for (int y = 0; y < filas; y++) {
            entradas.add(new Haz(new Punto(0, y), Direccion.DERECHA));
            entradas.add(new Haz(new Punto(columnas - 1, y), Direccion.IZQUIERDA));
        }
        return entradas;
    }

    /**
     * Cuántas casillas quedan energizadas con un haz que entra a la grilla en
     * {@code inicial}.
     *
     * <p>Se recorren los estados (casilla, dirección) con una pila. Los haces
     * pueden entrar en bucles (por ejemplo, dos divisores que se devuelven la
     * luz), así que cada estado se procesa una sola vez: un haz que llega a
     * una casilla en una dirección por la que ya pasó otro haz haría
     * exactamente el mismo recorrido. Como hay a lo sumo
     * {@code filas × columnas × 4} estados, se marcan en un arreglo.</p>
     *
     * @param artefacto la grilla
     * @param inicial   el haz que entra (su casilla tiene que estar dentro)
     * @return la cantidad de casillas por las que pasa al menos un haz
     */
    static int energizadas(Grilla artefacto, Haz inicial) {
        int filas = artefacto.filas(), columnas = artefacto.columnas();
        int direcciones = Direccion.values().length;
        boolean[] visitado = new boolean[filas * columnas * direcciones];
        boolean[] energizada = new boolean[filas * columnas];
        int cantidad = 0;
        Deque<Haz> pendientes = new ArrayDeque<>();
        pendientes.push(inicial);
        while (!pendientes.isEmpty()) {
            Haz haz = pendientes.pop();
            Punto p = haz.posicion();
            if (!p.dentroDeGrilla(filas, columnas)) continue;
            int casilla = p.y() * columnas + p.x();
            int estado = casilla * direcciones + haz.direccion().ordinal();
            if (visitado[estado]) continue;
            visitado[estado] = true;
            if (!energizada[casilla]) {
                energizada[casilla] = true;
                cantidad++;
            }
            for (Direccion salida : salidas(artefacto.obtener(p), haz.direccion())) {
                pendientes.push(new Haz(p.mover(salida), salida));
            }
        }
        return cantidad;
    }

    /**
     * Hacia dónde sigue un haz que llega a una casilla yendo en {@code entrada}.
     *
     * <ul>
     *   <li>{@code .}: sigue derecho.</li>
     *   <li>{@code /}: derecha ↔ arriba, izquierda ↔ abajo.</li>
     *   <li>{@code \}: derecha ↔ abajo, izquierda ↔ arriba.</li>
     *   <li>{@code |} y {@code -}: si llega por la punta, sigue derecho; si
     *       llega por el costado plano, se divide hacia las dos puntas.</li>
     * </ul>
     *
     * @param casilla el contenido de la casilla
     * @param entrada la dirección del haz al llegar
     * @return las direcciones en que sale (una o dos)
     * @throws IllegalArgumentException si la casilla tiene un carácter desconocido
     */
    static List<Direccion> salidas(char casilla, Direccion entrada) {
        boolean horizontal = entrada == Direccion.IZQUIERDA || entrada == Direccion.DERECHA;
        return switch (casilla) {
            case '.' -> List.of(entrada);
            case '/' -> List.of(horizontal ? entrada.giroIzquierda() : entrada.giroDerecha());
            case '\\' -> List.of(horizontal ? entrada.giroDerecha() : entrada.giroIzquierda());
            case '|' -> horizontal ? List.of(Direccion.ARRIBA, Direccion.ABAJO) : List.of(entrada);
            case '-' -> horizontal ? List.of(entrada) : List.of(Direccion.IZQUIERDA, Direccion.DERECHA);
            default -> throw new IllegalArgumentException("Casilla desconocida: '" + casilla + "'");
        };
    }
}
