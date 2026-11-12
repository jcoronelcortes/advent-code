package advent.code.y2024;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Año 2024 — Día 6: Patrulla del Guardia
 *
 * <p>El input es el mapa de un laboratorio: {@code #} es un obstáculo y
 * {@code ^} el guardia, mirando hacia arriba. El guardia repite: si tiene
 * algo justo enfrente, gira 90° a la derecha; si no, avanza un paso. Así
 * sigue hasta salir del mapa.</p>
 */
public class Dia06 implements Resolvedor {

    private static final char GUARDIA = '^';
    private static final char OBSTACULO = '#';

    /**
     * Parte 1: cantidad de posiciones distintas que visita el guardia antes
     * de salir del mapa (incluida la inicial).
     *
     * @param lineas el mapa
     * @return cantidad de posiciones visitadas
     */
    @Override
    public String parte1(List<String> lineas) {
        Recorrido recorrido = patrullar(Grilla.desdeLineas(lineas));
        if (recorrido.enCiclo()) throw new IllegalStateException("El guardia nunca sale del mapa");
        return String.valueOf(recorrido.visitadas().size());
    }

    /**
     * Parte 2: cantidad de posiciones donde poner <b>un</b> obstáculo nuevo
     * deja al guardia atrapado en un ciclo. No se puede poner donde está el
     * guardia al comienzo.
     *
     * <p>Solo hace falta probar las posiciones del recorrido original: un
     * obstáculo en una celda por la que el guardia nunca pasaba no lo toca, y
     * el guardia sale del mapa igual que antes.</p>
     *
     * @param lineas el mapa
     * @return cantidad de posiciones que provocan un ciclo
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla mapa = Grilla.desdeLineas(lineas);
        Punto inicio = buscarGuardia(mapa);
        long posiciones = patrullar(mapa).visitadas().stream()
                .filter(candidata -> !candidata.equals(inicio))
                .filter(candidata -> patrullar(mapa, candidata).enCiclo())
                .count();
        return String.valueOf(posiciones);
    }

    /**
     * Resultado de una patrulla.
     *
     * @param visitadas posiciones por las que pasó el guardia
     * @param enCiclo   {@code true} si quedó dando vueltas para siempre en vez de salir
     */
    record Recorrido(Set<Punto> visitadas, boolean enCiclo) {}

    /** Posición inicial del guardia. */
    private static Punto buscarGuardia(Grilla mapa) {
        return mapa.buscarUnico(GUARDIA);
    }

    /** Estado completo del guardia: dónde está y hacia dónde mira. */
    private record Estado(Punto posicion, Direccion direccion) {}

    /**
     * Simula la patrulla hasta que el guardia sale del mapa o entra en un
     * ciclo.
     *
     * <p>El guardia es determinista: su próximo movimiento depende solo de su
     * posición y su dirección. Si vuelve a un mismo {@link Estado} (misma
     * posición <em>y</em> misma dirección), repetirá exactamente lo mismo para
     * siempre, así que la simulación se detiene. Pasar por la misma posición
     * con otra dirección no es un ciclo.</p>
     *
     * @param mapa el laboratorio
     * @return las posiciones visitadas y si quedó en un ciclo
     */
    static Recorrido patrullar(Grilla mapa) {
        return patrullar(mapa, null);
    }

    /**
     * Igual que {@link #patrullar(Grilla)}, pero con un obstáculo extra. Se
     * pasa aparte en vez de modificar el mapa para no copiar la grilla en
     * cada una de las miles de pruebas de la parte 2.
     *
     * @param mapa           el laboratorio
     * @param obstaculoExtra posición de un obstáculo nuevo, o {@code null} si no hay
     * @return las posiciones visitadas y si quedó en un ciclo
     */
    static Recorrido patrullar(Grilla mapa, Punto obstaculoExtra) {
        Punto posicion = buscarGuardia(mapa);
        Direccion direccion = Direccion.ARRIBA;

        Set<Punto> visitadas = new HashSet<>();
        Set<Estado> estados = new HashSet<>();
        while (true) {
            visitadas.add(posicion);
            if (!estados.add(new Estado(posicion, direccion))) return new Recorrido(visitadas, true);

            Punto adelante = posicion.mover(direccion);
            if (!mapa.estaDentro(adelante)) return new Recorrido(visitadas, false);
            if (mapa.obtener(adelante) == OBSTACULO || adelante.equals(obstaculoExtra)) {
                direccion = direccion.giroDerecha();
            } else {
                posicion = adelante;
            }
        }
    }
}
