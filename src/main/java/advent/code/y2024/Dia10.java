package advent.code.y2024;

import advent.code.BFS;
import advent.code.Grilla;
import advent.code.Memoizador;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.List;

/**
 * Año 2024 — Día 10: A Pie
 *
 * <p>El input es un mapa topográfico: cada posición tiene una altura de 0 a 9.
 * Un <b>sendero</b> empieza en altura 0, termina en altura 9 y en cada paso
 * (arriba, abajo, izquierda o derecha, nunca en diagonal) sube exactamente 1.
 * Un <b>inicio de sendero</b> es una posición de altura 0.</p>
 */
public class Dia10 implements Resolvedor {

    private static final char INICIO = '0';
    private static final char CIMA = '9';

    /**
     * Parte 1: suma de los puntajes de todos los inicios de sendero. El
     * puntaje de un inicio es cuántas posiciones de altura 9 distintas
     * alcanza por algún sendero.
     *
     * @param lineas el mapa topográfico
     * @return la suma de los puntajes
     */
    @Override
    public String parte1(List<String> lineas) {
        Grilla mapa = Grilla.desdeLineas(lineas);
        long suma = mapa.buscarTodos(INICIO).stream().mapToLong(inicio -> puntaje(mapa, inicio)).sum();
        return String.valueOf(suma);
    }

    /**
     * Parte 2: suma de las calificaciones de todos los inicios de sendero. La
     * calificación de un inicio es cuántos senderos distintos empiezan en él.
     *
     * @param lineas el mapa topográfico
     * @return la suma de las calificaciones
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla mapa = Grilla.desdeLineas(lineas);
        Memoizador<Punto, Long> senderos = senderosHastaCima(mapa);
        long suma = mapa.buscarTodos(INICIO).stream().mapToLong(senderos::calcular).sum();
        return String.valueOf(suma);
    }

    /**
     * Cantidad de cimas (altura 9) distintas que se alcanzan desde un inicio.
     *
     * <p>Una BFS ({@link BFS#explorar}) desde el inicio, donde los vecinos
     * transitables de una posición son los que tienen exactamente una altura
     * más. Todo lo que la BFS alcanza está en algún sendero, y cada posición
     * aparece una sola vez, así que basta contar las cimas alcanzadas.</p>
     *
     * @param mapa   el mapa topográfico
     * @param inicio posición de altura 0
     * @return cantidad de cimas alcanzables
     */
    static long puntaje(Grilla mapa, Punto inicio) {
        return BFS.explorar(inicio, posicion -> subidas(mapa, posicion)).keySet().stream()
                .filter(posicion -> mapa.obtener(posicion) == CIMA)
                .count();
    }

    /**
     * Cantidad de senderos distintos desde cada posición hasta alguna cima.
     *
     * <p>Desde una cima hay un solo sendero (quedarse ahí); desde cualquier
     * otra posición, la suma de los senderos desde cada vecino una altura más
     * arriba. Como la altura siempre sube no hay ciclos, y el
     * {@link Memoizador} calcula cada posición una sola vez, compartida entre
     * todos los inicios.</p>
     *
     * @param mapa el mapa topográfico
     * @return función memoizada posición → cantidad de senderos
     */
    static Memoizador<Punto, Long> senderosHastaCima(Grilla mapa) {
        return new Memoizador<>((posicion, propio) -> {
            if (mapa.obtener(posicion) == CIMA) return 1L;
            long total = 0;
            for (Punto vecino : subidas(mapa, posicion)) {
                total += propio.apply(vecino);
            }
            return total;
        });
    }

    /**
     * Vecinos a los que se puede subir desde una posición: los que tienen
     * exactamente una altura más. Una celda sin altura (como los {@code .}
     * de los ejemplos del enunciado) nunca cumple la condición.
     *
     * @param mapa     el mapa topográfico
     * @param posicion posición actual
     * @return los vecinos con altura + 1
     */
    static List<Punto> subidas(Grilla mapa, Punto posicion) {
        char siguiente = (char) (mapa.obtener(posicion) + 1);
        return mapa.vecinos(posicion, altura -> altura == siguiente);
    }
}
