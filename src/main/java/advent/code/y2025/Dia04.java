package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Año 2025 — Día 4: Departamento de Impresión
 *
 * <p>El input es una grilla donde {@code @} es un rollo de papel y {@code .}
 * un espacio vacío. Un montacargas puede acceder a un rollo si en sus
 * 8 posiciones vecinas (incluidas las diagonales) hay menos de 4 rollos.</p>
 */
public class Dia04 implements Resolvedor {

    /** Carácter que representa un rollo de papel. */
    private static final char ROLLO = '@';

    /** Carácter que queda en el lugar de un rollo retirado. */
    private static final char VACIO = '.';

    /** Un rollo es accesible si tiene estrictamente menos de esta cantidad de rollos vecinos. */
    private static final int LIMITE_VECINOS = 4;

    /**
     * Parte 1: cantidad de rollos accesibles por un montacargas.
     *
     * @param lineas filas de la grilla
     * @return cantidad de rollos accesibles
     */
    @Override
    public String parte1(List<String> lineas) {
        Grilla grilla = Grilla.desdeLineas(lineas);
        return String.valueOf(accesibles(grilla).size());
    }

    /**
     * Parte 2: cantidad total de rollos que se pueden retirar si se repite el
     * proceso de sacar los accesibles hasta que no quede ninguno accesible.
     *
     * @param lineas filas de la grilla
     * @return cantidad total de rollos retirados
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla grilla = Grilla.desdeLineas(lineas);
        return String.valueOf(retirarTodos(grilla));
    }

    /**
     * Retira rollos accesibles hasta que no quede ninguno, modificando la grilla.
     *
     * <p>Retirar un rollo solo puede <em>disminuir</em> la cantidad de vecinos de
     * los demás, así que un rollo accesible sigue siéndolo hasta que se retira.
     * Por eso el orden no importa y el total es siempre el mismo. En vez de
     * recorrer la grilla completa en cada ronda, se usa una cola de trabajo:
     * al retirar un rollo solo se revisan sus 8 vecinos, que son los únicos
     * cuyo conteo cambió.</p>
     *
     * @param grilla grilla de rollos (se modifica: los retirados quedan como {@code .})
     * @return cantidad de rollos retirados
     */
    static long retirarTodos(Grilla grilla) {
        Deque<Punto> pendientes = new ArrayDeque<>(accesibles(grilla));
        long retirados = 0;
        while (!pendientes.isEmpty()) {
            Punto rollo = pendientes.poll();
            if (grilla.obtener(rollo) != ROLLO) continue;   // ya se retiró
            grilla.establecer(rollo, VACIO);
            retirados++;
            for (Punto vecino : grilla.vecinosConDiagonales(rollo)) {
                if (grilla.obtener(vecino) == ROLLO && rollosVecinos(grilla, vecino) < LIMITE_VECINOS) {
                    pendientes.add(vecino);
                }
            }
        }
        return retirados;
    }

    /**
     * Rollos de la grilla que tienen menos de {@link #LIMITE_VECINOS} rollos
     * entre sus 8 vecinos.
     *
     * @param grilla grilla de rollos
     * @return posiciones de los rollos accesibles
     */
    static List<Punto> accesibles(Grilla grilla) {
        return grilla.buscarTodos(ROLLO).stream()
                .filter(rollo -> rollosVecinos(grilla, rollo) < LIMITE_VECINOS)
                .toList();
    }

    /**
     * Cuenta los rollos entre los 8 vecinos de una posición (los que caen
     * fuera de la grilla no cuentan).
     *
     * @param grilla grilla de rollos
     * @param punto  posición central
     * @return cantidad de rollos vecinos (0 a 8)
     */
    static int rollosVecinos(Grilla grilla, Punto punto) {
        return grilla.contarVecinos(punto, ROLLO);
    }
}
