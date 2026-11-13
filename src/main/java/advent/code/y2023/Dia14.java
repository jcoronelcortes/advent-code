package advent.code.y2023;

import advent.code.Ciclo;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.List;

/**
 * Año 2023 — Día 14: Plato Reflector Parabólico
 *
 * <p>El input es una plataforma con rocas redondas ({@code O}), que ruedan
 * al inclinarla, rocas cúbicas ({@code #}), que no se mueven, y espacios
 * vacíos ({@code .}). La carga sobre el borde norte es la suma, por cada
 * roca redonda, de las filas desde ella hasta el borde sur (contando la
 * suya).</p>
 */
public class Dia14 implements Resolvedor {

    static final char REDONDA = 'O';
    static final char CUBICA = '#';
    static final char VACIO = '.';

    /** Cuántos ciclos de giro pide la parte 2. */
    static final long CICLOS = 1_000_000_000L;

    /**
     * Parte 1: la carga sobre el borde norte después de inclinar la
     * plataforma hacia el norte.
     *
     * @param lineas la plataforma
     * @return la carga
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(carga(inclinarAlNorte(Grilla.desdeLineas(lineas))));
    }

    /**
     * Parte 2: la carga sobre el borde norte después de mil millones de
     * ciclos de giro. Las posiciones posibles son finitas, así que la
     * plataforma termina repitiéndose: se detecta el ciclo y se salta
     * directo al estado final sin simularlo.
     *
     * @param lineas la plataforma
     * @return la carga
     */
    @Override
    public String parte2(List<String> lineas) {
        Ciclo<Grilla> ciclo = Ciclo.detectar(Grilla.desdeLineas(lineas), Dia14::cicloDeGiro);
        return String.valueOf(carga(ciclo.estadoEn(CICLOS)));
    }

    /**
     * Un ciclo de giro: inclinar hacia el norte, el oeste, el sur y el este,
     * en ese orden.
     *
     * <p>En vez de escribir cuatro inclinaciones, se inclina siempre al norte
     * y se rota la plataforma 90° en sentido horario: así el lado oeste pasa a
     * ser el norte, después el sur y después el este. Tras las cuatro
     * rotaciones la plataforma vuelve a su orientación original.</p>
     *
     * @param plataforma la plataforma (no se modifica)
     * @return la plataforma después del ciclo
     */
    static Grilla cicloDeGiro(Grilla plataforma) {
        Grilla resultado = plataforma;
        for (int i = 0; i < 4; i++) resultado = inclinarAlNorte(resultado).rotarDerecha();
        return resultado;
    }

    /**
     * La plataforma inclinada hacia el norte: cada roca redonda rueda hacia
     * arriba hasta chocar con el borde, una roca cúbica u otra redonda.
     *
     * <p>Cada columna se recorre de arriba hacia abajo recordando el
     * {@code tope}: la primera fila libre donde caería la próxima roca. Una
     * roca cúbica lo pone justo debajo de ella; una redonda se mueve al tope
     * y lo baja una fila. Así cada casilla se mira una sola vez.</p>
     *
     * @param plataforma la plataforma (no se modifica)
     * @return una plataforma nueva con las rocas redondas movidas
     */
    static Grilla inclinarAlNorte(Grilla plataforma) {
        Grilla inclinada = plataforma.copiar();
        for (int x = 0; x < inclinada.columnas(); x++) {
            int tope = 0;
            for (int y = 0; y < inclinada.filas(); y++) {
                char casilla = inclinada.obtener(new Punto(x, y));
                if (casilla == CUBICA) {
                    tope = y + 1;
                } else if (casilla == REDONDA) {
                    inclinada.establecer(new Punto(x, y), VACIO);
                    inclinada.establecer(new Punto(x, tope), REDONDA);
                    tope++;
                }
            }
        }
        return inclinada;
    }

    /**
     * La carga sobre el borde norte: cada roca redonda pesa tantas filas
     * como haya desde la suya hasta el borde sur, incluida.
     *
     * @param plataforma la plataforma
     * @return la carga total
     */
    static long carga(Grilla plataforma) {
        return plataforma.buscarTodos(REDONDA).stream()
                .mapToLong(roca -> plataforma.filas() - roca.y())
                .sum();
    }
}
