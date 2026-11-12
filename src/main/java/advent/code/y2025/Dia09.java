package advent.code.y2025;

import advent.code.BFS;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.Arrays;
import java.util.List;

/**
 * Año 2025 — Día 9: Sala de Cine
 *
 * <p>El input es la lista de posiciones {@code X,Y} de las baldosas rojas del
 * piso del cine. Se busca el rectángulo más grande que tenga baldosas rojas
 * en dos esquinas opuestas.</p>
 *
 * <p>En el input, cada baldosa comparte fila o columna con la siguiente (y la
 * última con la primera): en orden forman un polígono cerrado de lados
 * horizontales y verticales. Por eso se conserva el orden del input.</p>
 */
public class Dia09 implements Resolvedor {

    /**
     * Parte 1: área del rectángulo más grande con baldosas rojas en dos
     * esquinas opuestas.
     *
     * @param lineas posiciones {@code X,Y} de las baldosas rojas
     * @return el área máxima
     */
    @Override
    public String parte1(List<String> lineas) {
        List<Punto> rojas = leerBaldosas(lineas);
        long maxima = 0;
        for (int i = 0; i < rojas.size(); i++) {
            for (int j = i + 1; j < rojas.size(); j++) {
                maxima = Math.max(maxima, area(rojas.get(i), rojas.get(j)));
            }
        }
        return String.valueOf(maxima);
    }

    /**
     * Parte 2: área del rectángulo más grande con baldosas rojas en dos
     * esquinas opuestas que contenga <b>solo</b> baldosas rojas o verdes.
     *
     * <p>Las baldosas verdes son las de los lados del polígono que forman las
     * rojas en orden, y todas las de su interior. Es decir, el rectángulo
     * tiene que quedar completamente dentro del polígono (borde incluido).</p>
     *
     * @param lineas posiciones {@code X,Y} de las baldosas rojas, en orden
     * @return el área máxima
     */
    @Override
    public String parte2(List<String> lineas) {
        List<Punto> rojas = leerBaldosas(lineas);
        PisoComprimido piso = new PisoComprimido(rojas);
        long maxima = 0;
        for (int i = 0; i < rojas.size(); i++) {
            for (int j = i + 1; j < rojas.size(); j++) {
                long area = area(rojas.get(i), rojas.get(j));
                if (area > maxima && piso.rectanguloAdentro(rojas.get(i), rojas.get(j))) {
                    maxima = area;
                }
            }
        }
        return String.valueOf(maxima);
    }

    /**
     * Área del rectángulo con esquinas opuestas en dos baldosas, contando
     * baldosas: los dos extremos se incluyen, por eso se suma 1 a cada lado
     * (entre {@code 2,5} y {@code 9,7} hay {@code 8 · 3 = 24} baldosas).
     *
     * <p>Se calcula en {@code long}: con coordenadas de ~100 000 el área
     * llega a ~10¹⁰ y no cabe en un {@code int}.</p>
     *
     * @param a una esquina
     * @param b la esquina opuesta
     * @return cantidad de baldosas del rectángulo
     */
    static long area(Punto a, Punto b) {
        long ancho = Math.abs((long) a.x() - b.x()) + 1;
        long alto = Math.abs((long) a.y() - b.y()) + 1;
        return ancho * alto;
    }

    /**
     * Piso del cine con <b>coordenadas comprimidas</b>, para saber qué
     * baldosas están dentro del polígono sin construir una grilla de
     * 100 000 × 100 000.
     *
     * <p><b>Idea:</b> solo importan las coordenadas X e Y donde hay baldosas
     * rojas (unas 250 de cada una), porque los lados del polígono siempre
     * caen sobre ellas. Cada X distinta se convierte en una columna de la
     * grilla comprimida, y cada <em>hueco</em> entre dos X consecutivas en
     * otra columna (lo mismo con las filas). Una celda de hueco representa
     * muchas baldosas reales, pero todas están del mismo lado del polígono,
     * porque ningún lado pasa por dentro de un hueco.</p>
     *
     * <pre>
     * X reales:      2        7  8        11
     * columnas:   0  1  2     3  4  5     6   7
     *             ^  ^  ^     ^  ^  ^     ^   ^
     *        borde  x=2 hueco x=7 x=8 hueco x=11 borde (siempre afuera)
     * </pre>
     *
     * <p><b>Detalle importante:</b> entre dos coordenadas consecutivas (como
     * 7 y 8) <em>no</em> se crea hueco, porque no hay ninguna baldosa entre
     * ellas. Si se creara, una celda sin baldosas reales podría quedar
     * "afuera" (por ejemplo, en una muesca de ancho cero de un polígono con
     * forma de U) y rechazar por error un rectángulo que solo tiene baldosas
     * rojas y verdes.</p>
     *
     * <p>Sobre esa grilla se marcan los lados del polígono, se rellena el
     * exterior con una BFS desde una esquina (que siempre está afuera) y se
     * calculan sumas acumuladas 2D de las celdas exteriores. Así, saber si un
     * rectángulo toca alguna celda exterior cuesta O(1).</p>
     */
    static final class PisoComprimido {

        private static final char LADO = '#';
        private static final char AFUERA = 'o';

        /** X distintas de los vértices, ordenadas, y su columna en la grilla comprimida. */
        private final int[] columnasX;
        private final int[] indiceColumna;

        /** Y distintas de los vértices, ordenadas, y su fila en la grilla comprimida. */
        private final int[] filasY;
        private final int[] indiceFila;

        /**
         * {@code afueraAcumulado[f][c]} = cantidad de celdas exteriores en el
         * rectángulo de filas {@code [0, f)} y columnas {@code [0, c)}.
         */
        private final int[][] afueraAcumulado;

        /**
         * @param rojas vértices del polígono en orden (cada uno comparte fila
         *              o columna con el siguiente, y el último con el primero)
         */
        PisoComprimido(List<Punto> rojas) {
            columnasX = rojas.stream().mapToInt(Punto::x).distinct().sorted().toArray();
            filasY = rojas.stream().mapToInt(Punto::y).distinct().sorted().toArray();
            indiceColumna = comprimir(columnasX);
            indiceFila = comprimir(filasY);

            // +2: el borde exterior a la derecha (o abajo) de la última coordenada
            Grilla piso = new Grilla(indiceFila[filasY.length - 1] + 2, indiceColumna[columnasX.length - 1] + 2, '.');
            marcarLados(piso, rojas);
            rellenarExterior(piso);
            afueraAcumulado = sumasAcumuladas(piso);
        }

        /**
         * Indica si el rectángulo con esquinas opuestas en {@code a} y
         * {@code b} queda completamente dentro del polígono (borde incluido).
         *
         * @param a una esquina (baldosa roja)
         * @param b la esquina opuesta (baldosa roja)
         * @return {@code true} si no contiene ninguna baldosa exterior
         */
        boolean rectanguloAdentro(Punto a, Punto b) {
            int columna1 = columna(Math.min(a.x(), b.x()));
            int columna2 = columna(Math.max(a.x(), b.x()));
            int fila1 = fila(Math.min(a.y(), b.y()));
            int fila2 = fila(Math.max(a.y(), b.y()));
            int afuera = afueraAcumulado[fila2 + 1][columna2 + 1]
                    - afueraAcumulado[fila1][columna2 + 1]
                    - afueraAcumulado[fila2 + 1][columna1]
                    + afueraAcumulado[fila1][columna1];
            return afuera == 0;
        }

        /**
         * Índice comprimido de cada coordenada ordenada. El índice 0 es el
         * borde exterior; entre dos coordenadas se deja un índice de hueco
         * solo si hay baldosas entre ellas (diferencia mayor que 1).
         *
         * @param valores coordenadas distintas, ordenadas
         * @return índice comprimido de cada una
         */
        static int[] comprimir(int[] valores) {
            int[] indices = new int[valores.length];
            int siguiente = 1;
            for (int i = 0; i < valores.length; i++) {
                if (i > 0 && valores[i] - valores[i - 1] > 1) siguiente++;   // hueco con baldosas reales
                indices[i] = siguiente++;
            }
            return indices;
        }

        /** Columna comprimida de una X real que es vértice del polígono. */
        private int columna(int x) {
            return indiceColumna[Arrays.binarySearch(columnasX, x)];
        }

        /** Fila comprimida de una Y real que es vértice del polígono. */
        private int fila(int y) {
            return indiceFila[Arrays.binarySearch(filasY, y)];
        }

        /** Marca con {@link #LADO} las celdas por donde pasan los lados del polígono. */
        private void marcarLados(Grilla piso, List<Punto> rojas) {
            for (int i = 0; i < rojas.size(); i++) {
                Punto desde = rojas.get(i);
                Punto hasta = rojas.get((i + 1) % rojas.size());   // el último se une con el primero
                if (desde.x() != hasta.x() && desde.y() != hasta.y()) {
                    throw new IllegalArgumentException("Baldosas consecutivas sin fila ni columna en común: "
                            + desde + " y " + hasta);
                }
                int columna1 = columna(Math.min(desde.x(), hasta.x()));
                int columna2 = columna(Math.max(desde.x(), hasta.x()));
                int fila1 = fila(Math.min(desde.y(), hasta.y()));
                int fila2 = fila(Math.max(desde.y(), hasta.y()));
                for (int fila = fila1; fila <= fila2; fila++) {
                    for (int columna = columna1; columna <= columna2; columna++) {
                        piso.establecer(new Punto(columna, fila), LADO);
                    }
                }
            }
        }

        /**
         * Marca con {@link #AFUERA} todo lo alcanzable desde la esquina
         * {@code (0, 0)} sin cruzar un lado. Esa esquina siempre está afuera
         * porque la grilla tiene un borde vacío alrededor del polígono.
         */
        private void rellenarExterior(Grilla piso) {
            BFS.explorar(new Punto(0, 0), punto -> piso.vecinos(punto, celda -> celda != LADO))
                    .keySet()
                    .forEach(punto -> piso.establecer(punto, AFUERA));
        }

        /** Sumas acumuladas 2D de las celdas exteriores. */
        private static int[][] sumasAcumuladas(Grilla piso) {
            int[][] suma = new int[piso.filas() + 1][piso.columnas() + 1];
            for (int fila = 0; fila < piso.filas(); fila++) {
                for (int columna = 0; columna < piso.columnas(); columna++) {
                    int afuera = piso.obtener(new Punto(columna, fila)) == AFUERA ? 1 : 0;
                    suma[fila + 1][columna + 1] = afuera + suma[fila][columna + 1]
                            + suma[fila + 1][columna] - suma[fila][columna];
                }
            }
            return suma;
        }
    }

    /**
     * Convierte el input en posiciones, en el mismo orden.
     *
     * @param lineas una posición {@code X,Y} por línea
     * @return baldosas rojas en el orden del input
     */
    static List<Punto> leerBaldosas(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream()
                .map(Utiles::leerNumeros)
                .map(numeros -> new Punto(numeros.get(0), numeros.get(1)))
                .toList();
    }
}
