package advent.code.y2024;

import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
     * el guardia sale del mapa igual que antes. Y cada prueba arranca justo
     * antes de que el guardia entre por primera vez a esa celda: hasta ahí
     * el recorrido es idéntico al original.</p>
     *
     * @param lineas el mapa
     * @return cantidad de posiciones que provocan un ciclo
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla mapa = Grilla.desdeLineas(lineas);
        DetectorDeCiclos detector = new DetectorDeCiclos(mapa);
        long posiciones = antesDeEntrar(mapa).entrySet().stream()
                .filter(entrada -> detector.quedaAtrapado(entrada.getValue(), entrada.getKey()))
                .count();
        return String.valueOf(posiciones);
    }

    /**
     * Para cada celda del recorrido original (salvo la inicial), el estado
     * del guardia justo antes de entrar a ella por primera vez: dónde estaba
     * y hacia dónde miraba.
     *
     * <p>Si se pone un obstáculo en esa celda, todo lo anterior pasa igual,
     * porque el guardia todavía no intentó entrar ahí. La diferencia empieza
     * en ese estado: en vez de entrar, choca y gira.</p>
     *
     * @param mapa el mapa (el guardia sale de él sin ciclos)
     * @return celda → estado previo a la primera entrada, en orden de recorrido
     */
    static Map<Punto, Estado> antesDeEntrar(Grilla mapa) {
        Punto inicio = buscarGuardia(mapa);
        Punto posicion = inicio;
        Direccion direccion = Direccion.ARRIBA;
        Map<Punto, Estado> antes = new LinkedHashMap<>();
        while (true) {
            Punto adelante = posicion.mover(direccion);
            if (!mapa.estaDentro(adelante)) return antes;
            if (mapa.obtener(adelante) == OBSTACULO) {
                direccion = direccion.giroDerecha();
            } else {
                if (!adelante.equals(inicio)) antes.putIfAbsent(adelante, new Estado(posicion, direccion));
                posicion = adelante;
            }
        }
    }

    /**
     * Simulación rápida para la parte 2: dice si el guardia queda atrapado
     * con un obstáculo extra, sin construir el recorrido.
     *
     * <p>Es el mismo protocolo que {@link #patrullar}, pero con arreglos en
     * vez de conjuntos de objetos, porque se repite miles de veces:</p>
     * <ul>
     *   <li>Los obstáculos están en un {@code boolean[]} indexado por celda.</li>
     *   <li>Solo se guardan los estados en los que el guardia <b>gira</b>:
     *       un ciclo siempre incluye algún giro (en línea recta, el guardia
     *       termina saliendo del mapa), así que si hay ciclo, un estado de
     *       giro se repite.</li>
     *   <li>Los estados vistos se marcan con el número de prueba
     *       ({@code generacion}) en una tabla que no hace falta vaciar entre
     *       una prueba y otra.</li>
     * </ul>
     */
    static final class DetectorDeCiclos {

        private static final int DIRECCIONES = Direccion.values().length;

        private final int ancho;
        private final int alto;
        private final boolean[] obstaculo;
        private final int[] vistoEnGeneracion;
        private int generacion;

        DetectorDeCiclos(Grilla mapa) {
            ancho = mapa.columnas();
            alto = mapa.filas();
            obstaculo = new boolean[ancho * alto];
            for (Punto celda : mapa.buscarTodos(OBSTACULO)) obstaculo[celda.y() * ancho + celda.x()] = true;
            vistoEnGeneracion = new int[ancho * alto * DIRECCIONES];
        }

        /**
         * Si el guardia queda en un ciclo partiendo de un estado, con un
         * obstáculo extra.
         *
         * @param desde          estado de partida
         * @param obstaculoExtra celda del obstáculo nuevo
         * @return {@code true} si queda atrapado; {@code false} si sale del mapa
         */
        boolean quedaAtrapado(Estado desde, Punto obstaculoExtra) {
            generacion++;
            int extra = obstaculoExtra.y() * ancho + obstaculoExtra.x();
            int x = desde.posicion().x();
            int y = desde.posicion().y();
            Direccion direccion = desde.direccion();
            while (true) {
                int siguienteX = x + direccion.dx();
                int siguienteY = y + direccion.dy();
                if (siguienteX < 0 || siguienteY < 0 || siguienteX >= ancho || siguienteY >= alto) return false;
                int siguiente = siguienteY * ancho + siguienteX;
                if (obstaculo[siguiente] || siguiente == extra) {
                    int estado = (y * ancho + x) * DIRECCIONES + direccion.ordinal();
                    if (vistoEnGeneracion[estado] == generacion) return true;
                    vistoEnGeneracion[estado] = generacion;
                    direccion = direccion.giroDerecha();
                } else {
                    x = siguienteX;
                    y = siguienteY;
                }
            }
        }
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
    record Estado(Punto posicion, Direccion direccion) {}

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
