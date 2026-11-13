package advent.code.y2023;

import advent.code.Dijkstra;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.List;

/**
 * Año 2023 — Día 17: Crisol Torpe
 *
 * <p>El input es un mapa de manzanas: cada dígito es el calor que pierde el
 * crisol al entrar en esa manzana. Hay que llevarlo de la esquina superior
 * izquierda a la inferior derecha perdiendo el menor calor posible, sin
 * retroceder y sin avanzar más de unas cuantas manzanas seguidas en la misma
 * dirección.</p>
 */
public class Dia17 implements Resolvedor {

    /** El crisol de la parte 1 avanza de 1 a 3 manzanas antes de girar. */
    static final int MINIMO_CRISOL = 1, MAXIMO_CRISOL = 3;

    /** El ultra crisol de la parte 2 avanza de 4 a 10 manzanas antes de girar (o de detenerse). */
    static final int MINIMO_ULTRA = 4, MAXIMO_ULTRA = 10;

    /**
     * Dónde está el crisol y en qué dirección hizo su último tramo recto.
     *
     * @param posicion  la manzana
     * @param direccion la dirección del último tramo (el siguiente tiene que girar)
     */
    record Estado(Punto posicion, Direccion direccion) {
    }

    /**
     * Parte 1: el menor calor perdido llevando el crisol de la esquina
     * superior izquierda a la inferior derecha, con tramos rectos de a lo sumo
     * tres manzanas.
     *
     * @param lineas el mapa
     * @return el menor calor perdido
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(menorPerdida(Grilla.desdeLineas(lineas), MINIMO_CRISOL, MAXIMO_CRISOL));
    }

    /**
     * Parte 2: lo mismo con un ultra crisol, que tiene que avanzar al menos
     * cuatro manzanas antes de girar o detenerse, y a lo sumo diez.
     *
     * @param lineas el mapa
     * @return el menor calor perdido
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(menorPerdida(Grilla.desdeLineas(lineas), MINIMO_ULTRA, MAXIMO_ULTRA));
    }

    /**
     * El menor calor perdido entre las esquinas, con tramos rectos de
     * {@code minimoRecto} a {@code maximoRecto} manzanas.
     *
     * <p>En vez de mover el crisol de a una manzana llevando la cuenta de
     * cuántas lleva en línea recta, cada movimiento es un <b>tramo
     * completo</b>: girar 90° y avanzar entre {@code minimoRecto} y
     * {@code maximoRecto} manzanas de una vez, sumando el calor de cada una.
     * Seguir derecho después de un tramo equivale a haber hecho un tramo más
     * largo, así que alcanza con guardar la dirección del último tramo; y
     * como siempre se gira, nunca se retrocede. Como todo tramo mide al menos
     * {@code minimoRecto}, el crisol tampoco puede detenerse en el destino
     * antes de tiempo.</p>
     *
     * <p>Al principio el crisol todavía no se movió y puede salir hacia
     * cualquier lado: se arranca desde dos estados, uno como si viniera de un
     * tramo horizontal (y así sale hacia arriba o abajo) y otro vertical.</p>
     *
     * @param mapa        el calor que se pierde en cada manzana (dígitos)
     * @param minimoRecto el tramo recto más corto permitido (al menos 1)
     * @param maximoRecto el tramo recto más largo permitido
     * @return el menor calor perdido
     * @throws IllegalArgumentException si el mapa tiene algo que no es un dígito o los largos no tienen sentido
     * @throws IllegalStateException    si no se puede llegar al destino
     */
    static long menorPerdida(Grilla mapa, int minimoRecto, int maximoRecto) {
        if (minimoRecto < 1 || maximoRecto < minimoRecto) {
            throw new IllegalArgumentException("Largos de tramo inválidos: " + minimoRecto + " a " + maximoRecto);
        }
        for (Punto p : mapa.puntos()) {
            if (!Character.isDigit(mapa.obtener(p))) {
                throw new IllegalArgumentException("Manzana sin dígito en " + p + ": '" + mapa.obtener(p) + "'");
            }
        }
        Punto inicio = new Punto(0, 0);
        Punto destino = new Punto(mapa.columnas() - 1, mapa.filas() - 1);
        return Dijkstra.buscarCamino(
                        List.of(new Estado(inicio, Direccion.DERECHA), new Estado(inicio, Direccion.ABAJO)),
                        estado -> estado.posicion().equals(destino),
                        estado -> tramos(mapa, estado, minimoRecto, maximoRecto),
                        estado -> 0)
                .orElseThrow(() -> new IllegalStateException("No se puede llegar al destino"))
                .costo();
    }

    /**
     * Los tramos posibles desde un estado: girar a la izquierda o a la
     * derecha y avanzar de {@code minimoRecto} a {@code maximoRecto} manzanas
     * sin salir del mapa. El costo de cada tramo es el calor de todas las
     * manzanas en las que entra (no el de la manzana de partida), incluidas
     * las que atraviesa antes de llegar al largo mínimo.
     *
     * @param mapa        el mapa
     * @param estado      dónde está el crisol y cómo llegó
     * @param minimoRecto el tramo más corto permitido
     * @param maximoRecto el tramo más largo permitido
     * @return los estados alcanzables con su costo
     */
    static List<Dijkstra.Arista<Estado>> tramos(Grilla mapa, Estado estado, int minimoRecto, int maximoRecto) {
        List<Dijkstra.Arista<Estado>> tramos = new ArrayList<>();
        for (Direccion giro : List.of(estado.direccion().giroIzquierda(), estado.direccion().giroDerecha())) {
            Punto posicion = estado.posicion();
            long perdida = 0;
            for (int largo = 1; largo <= maximoRecto; largo++) {
                posicion = posicion.mover(giro);
                if (!mapa.estaDentro(posicion)) break;
                perdida += mapa.obtener(posicion) - '0';
                if (largo >= minimoRecto) tramos.add(new Dijkstra.Arista<>(new Estado(posicion, giro), perdida));
            }
        }
        return tramos;
    }
}
