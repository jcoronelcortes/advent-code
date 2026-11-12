package advent.code.y2025;

import advent.code.BFS;
import advent.code.CompresionCoordenadas;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.SumaAcumulada2D;
import advent.code.Utiles;

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
     * caen sobre ellas. {@link CompresionCoordenadas} convierte cada X e Y en
     * un índice chico, dejando un hueco entre dos coordenadas solo si hay
     * baldosas entre ellas. Una celda de hueco representa muchas baldosas
     * reales, pero todas están del mismo lado del polígono, porque ningún
     * lado pasa por dentro de un hueco.</p>
     *
     * <p>Sobre esa grilla se marcan los lados del polígono, se rellena el
     * exterior con una BFS desde una esquina (que siempre está afuera, porque
     * la compresión deja un borde vacío) y se preparan sumas acumuladas
     * ({@link SumaAcumulada2D}) de las celdas exteriores. Así, saber si un
     * rectángulo toca alguna celda exterior cuesta O(1).</p>
     */
    static final class PisoComprimido {

        private static final char LADO = '#';
        private static final char AFUERA = 'o';

        private final CompresionCoordenadas columnas;
        private final CompresionCoordenadas filas;
        private final SumaAcumulada2D afuera;

        /**
         * @param rojas vértices del polígono en orden (cada uno comparte fila
         *              o columna con el siguiente, y el último con el primero)
         */
        PisoComprimido(List<Punto> rojas) {
            columnas = new CompresionCoordenadas(rojas.stream().map(Punto::x).toList());
            filas = new CompresionCoordenadas(rojas.stream().map(Punto::y).toList());

            Grilla piso = new Grilla(filas.tamanio(), columnas.tamanio(), '.');
            marcarLados(piso, rojas);
            BFS.explorar(new Punto(0, 0), punto -> piso.vecinos(punto, celda -> celda != LADO))
                    .keySet()
                    .forEach(punto -> piso.establecer(punto, AFUERA));
            afuera = SumaAcumulada2D.contar(piso, celda -> celda == AFUERA);
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
            return afuera.suma(comprimir(a), comprimir(b)) == 0;
        }

        /** Posición en la grilla comprimida de una baldosa roja. */
        private Punto comprimir(Punto baldosa) {
            return new Punto(columnas.indice(baldosa.x()), filas.indice(baldosa.y()));
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
                Punto a = comprimir(desde);
                Punto b = comprimir(hasta);
                for (int fila = Math.min(a.y(), b.y()); fila <= Math.max(a.y(), b.y()); fila++) {
                    for (int columna = Math.min(a.x(), b.x()); columna <= Math.max(a.x(), b.x()); columna++) {
                        piso.establecer(new Punto(columna, fila), LADO);
                    }
                }
            }
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
