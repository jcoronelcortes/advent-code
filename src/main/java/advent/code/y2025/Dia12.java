package advent.code.y2025;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Año 2025 — Día 12: Granja de Árboles de Navidad
 *
 * <p>El input tiene dos secciones: primero las formas de los regalos
 * ({@code 0:} seguido de un dibujo de 3×3 con {@code #}), y después las
 * regiones bajo los árboles ({@code 12x5: 1 0 1 0 2 2}: ancho × largo y
 * cuántos regalos de cada forma hay que acomodar). Los regalos se pueden
 * rotar y voltear, siempre sobre la grilla y sin superponerse.</p>
 */
public class Dia12 implements Resolvedor {

    /**
     * Parte 1: cantidad de regiones donde caben todos sus regalos.
     *
     * @param lineas formas, línea en blanco y regiones
     * @return cantidad de regiones donde caben
     */
    @Override
    public String parte1(List<String> lineas) {
        Granja granja = Granja.parsear(lineas);
        long cantidad = granja.regiones().stream().filter(region -> caben(granja.formas(), region)).count();
        return String.valueOf(cantidad);
    }

    /** Texto de la parte 2: el último día no tiene un segundo problema. */
    static final String SIN_PARTE_2 = "estrella final";

    /**
     * Parte 2: el último día no tiene un segundo problema; su segunda
     * estrella se obtiene al completar todos los demás días.
     *
     * @param lineas líneas del input (no se usan)
     * @return {@link #SIN_PARTE_2}
     */
    @Override
    public String parte2(List<String> lineas) {
        return SIN_PARTE_2;
    }

    // -------------------------------------------------------------------------
    // Modelo
    // -------------------------------------------------------------------------

    /**
     * Forma de un regalo con todas sus orientaciones distintas (hasta 8:
     * 4 rotaciones, con y sin volteo).
     *
     * @param celdas        cantidad de celdas {@code #}
     * @param orientaciones cada orientación como lista de desplazamientos,
     *                      ordenados por fila y columna; el primero es
     *                      {@code (0, 0)} (la primera celda en orden de lectura)
     */
    record Forma(int celdas, List<List<Punto>> orientaciones) {

        /**
         * Crea la forma a partir de su dibujo, calculando sus orientaciones
         * con las rotaciones y la transposición de {@link Grilla}.
         *
         * @param dibujo filas del dibujo ({@code #} = parte del regalo)
         * @return la forma
         */
        static Forma desdeDibujo(List<String> dibujo) {
            Grilla base = Grilla.desdeLineas(dibujo, '.');
            Set<List<Punto>> distintas = new LinkedHashSet<>();
            for (Grilla volteo : List.of(base, base.transponer())) {   // transponer = rotar + voltear
                Grilla actual = volteo;
                for (int giro = 0; giro < 4; giro++) {
                    distintas.add(normalizar(actual.buscarTodos('#')));
                    actual = actual.rotarDerecha();
                }
            }
            return new Forma(base.buscarTodos('#').size(), List.copyOf(distintas));
        }

        /**
         * Ordena las celdas en orden de lectura y las desplaza para que la
         * primera quede en {@code (0, 0)}. Dos orientaciones iguales quedan
         * idénticas, y el desplazamiento permite anclar el regalo en la
         * primera celda libre durante la búsqueda.
         */
        private static List<Punto> normalizar(List<Punto> celdas) {
            List<Punto> ordenadas = new ArrayList<>(celdas);
            ordenadas.sort(Comparator.comparingInt(Punto::y).thenComparingInt(Punto::x));
            Punto primera = ordenadas.getFirst();
            return ordenadas.stream().map(celda -> celda.restar(primera)).toList();
        }

        /**
         * Lado del menor cuadrado que contiene la forma (3 en el input):
         * el mayor entre su ancho y su alto. Rotar o voltear no lo cambia.
         *
         * @return el lado del cuadrado contenedor
         */
        int lado() {
            List<Punto> celdas = orientaciones.getFirst();
            int ancho = celdas.stream().mapToInt(Punto::x).max().orElse(0)
                    - celdas.stream().mapToInt(Punto::x).min().orElse(0) + 1;
            int alto = celdas.stream().mapToInt(Punto::y).max().orElse(0)
                    - celdas.stream().mapToInt(Punto::y).min().orElse(0) + 1;
            return Math.max(ancho, alto);
        }
    }

    /**
     * Región bajo un árbol.
     *
     * @param ancho      columnas
     * @param largo      filas
     * @param cantidades cuántos regalos de cada forma hay que acomodar
     */
    record Region(int ancho, int largo, List<Integer> cantidades) {

        /** @return cantidad total de regalos */
        int regalos() {
            return cantidades.stream().mapToInt(Integer::intValue).sum();
        }
    }

    /**
     * Contenido del input.
     *
     * @param formas   formas de los regalos, por índice
     * @param regiones regiones a revisar
     */
    record Granja(List<Forma> formas, List<Region> regiones) {

        /**
         * Separa el input en bloques por líneas en blanco: los que empiezan
         * con {@code N:} son formas; las líneas {@code AxB: ...}, regiones.
         *
         * @param lineas líneas del input
         * @return la granja
         */
        static Granja parsear(List<String> lineas) {
            List<Forma> formas = new ArrayList<>();
            List<Region> regiones = new ArrayList<>();
            for (List<String> seccion : Utiles.secciones(lineas)) {
                if (seccion.getFirst().matches("\\d+:")) {
                    formas.add(Forma.desdeDibujo(seccion.subList(1, seccion.size())));
                    continue;
                }
                for (String linea : seccion) {
                    List<Integer> numeros = Utiles.leerNumeros(linea);
                    regiones.add(new Region(numeros.get(0), numeros.get(1), numeros.subList(2, numeros.size())));
                }
            }
            return new Granja(formas, regiones);
        }
    }

    // -------------------------------------------------------------------------
    // Decisión
    // -------------------------------------------------------------------------

    /**
     * Indica si todos los regalos de la región caben.
     *
     * <p>Primero dos cotas que deciden al instante y son exactas:</p>
     * <ol>
     *   <li><b>No caben</b> si las celdas de todos los regalos suman más que el
     *       área: los regalos no se pueden superponer.</li>
     *   <li><b>Caben</b> si hay al menos un bloque de {@code lado × lado} por
     *       regalo: cada uno entra en su propio bloque sin tocar a los demás.</li>
     * </ol>
     * <p>Solo si ninguna decide se hace la búsqueda exhaustiva
     * ({@link #buscarAcomodo}). En el input real las 1000 regiones se
     * deciden con las cotas; la búsqueda es necesaria para el ejemplo.</p>
     *
     * @param formas formas de los regalos
     * @param region región a revisar
     * @return {@code true} si caben todos los regalos
     */
    static boolean caben(List<Forma> formas, Region region) {
        return switch (decidirConCotas(formas, region)) {
            case CABEN -> true;
            case NO_CABEN -> false;
            case DUDOSO -> buscarAcomodo(formas, region);
        };
    }

    /** Resultado de aplicar las cotas. */
    enum Decision { CABEN, NO_CABEN, DUDOSO }

    /**
     * Aplica las dos cotas exactas.
     *
     * @param formas formas de los regalos
     * @param region región a revisar
     * @return la decisión, o {@link Decision#DUDOSO} si hace falta buscar
     */
    static Decision decidirConCotas(List<Forma> formas, Region region) {
        long celdasRegalos = 0;
        int lado = 1;
        for (int i = 0; i < region.cantidades().size(); i++) {
            celdasRegalos += (long) region.cantidades().get(i) * formas.get(i).celdas();
            if (region.cantidades().get(i) > 0) lado = Math.max(lado, formas.get(i).lado());
        }
        if (celdasRegalos > (long) region.ancho() * region.largo()) return Decision.NO_CABEN;

        long bloques = (long) (region.ancho() / lado) * (region.largo() / lado);
        if (region.regalos() <= bloques) return Decision.CABEN;
        return Decision.DUDOSO;
    }

    /**
     * Búsqueda exhaustiva (backtracking) de un acomodo de todos los regalos.
     *
     * <p>Recorre las celdas en orden de lectura. En cada celda libre hay dos
     * opciones: dejarla vacía, o colocar ahí un regalo cuya
     * <em>primera</em> celda (en orden de lectura) caiga justo en ella. Como
     * todas las celdas anteriores ya están decididas, cualquier regalo que
     * cubra esta celda tiene que empezar en ella, así que la búsqueda es
     * completa.</p>
     *
     * <p>Poda: la cantidad de celdas que pueden quedar vacías es fija
     * ({@code área − celdas de los regalos}); al agotarse, cada celda libre
     * tiene que llenarse. Los regalos de la misma forma son
     * intercambiables, así que se cuentan por forma en vez de uno por uno.</p>
     *
     * @param formas formas de los regalos
     * @param region región a revisar
     * @return {@code true} si existe un acomodo
     */
    static boolean buscarAcomodo(List<Forma> formas, Region region) {
        int area = region.ancho() * region.largo();
        int[] restantes = region.cantidades().stream().mapToInt(Integer::intValue).toArray();
        long celdasRegalos = 0;
        for (int i = 0; i < restantes.length; i++) celdasRegalos += (long) restantes[i] * formas.get(i).celdas();
        if (celdasRegalos > area) return false;
        return new Busqueda(formas, region.ancho(), region.largo(), restantes)
                .buscar(0, (int) (area - celdasRegalos), region.regalos());
    }

    /** Estado de la búsqueda exhaustiva. */
    private static final class Busqueda {
        private final List<Forma> formas;
        private final int ancho;
        private final int largo;
        private final int[] restantes;
        private final boolean[] ocupada;

        Busqueda(List<Forma> formas, int ancho, int largo, int[] restantes) {
            this.formas = formas;
            this.ancho = ancho;
            this.largo = largo;
            this.restantes = restantes;
            this.ocupada = new boolean[ancho * largo];
        }

        /**
         * @param celda        próxima celda a decidir (en orden de lectura)
         * @param vaciasLibres cuántas celdas más pueden quedar vacías
         * @param faltan       regalos que faltan colocar
         */
        boolean buscar(int celda, int vaciasLibres, int faltan) {
            if (faltan == 0) return true;
            while (celda < ocupada.length && ocupada[celda]) celda++;
            if (celda == ocupada.length) return false;

            int x = celda % ancho;
            int y = celda / ancho;
            for (int forma = 0; forma < restantes.length; forma++) {
                if (restantes[forma] == 0) continue;
                for (List<Punto> orientacion : formas.get(forma).orientaciones()) {
                    if (!entra(orientacion, x, y)) continue;
                    marcar(orientacion, x, y, true);
                    restantes[forma]--;
                    boolean exito = buscar(celda + 1, vaciasLibres, faltan - 1);
                    restantes[forma]++;
                    marcar(orientacion, x, y, false);
                    if (exito) return true;
                }
            }
            // Dejar la celda vacía, si todavía se puede
            if (vaciasLibres > 0) {
                ocupada[celda] = true;
                boolean exito = buscar(celda + 1, vaciasLibres - 1, faltan);
                ocupada[celda] = false;
                return exito;
            }
            return false;
        }

        private boolean entra(List<Punto> orientacion, int x, int y) {
            for (Punto desplazamiento : orientacion) {
                int cx = x + desplazamiento.x();
                int cy = y + desplazamiento.y();
                if (cx < 0 || cx >= ancho || cy >= largo || ocupada[cy * ancho + cx]) return false;
            }
            return true;
        }

        private void marcar(List<Punto> orientacion, int x, int y, boolean valor) {
            for (Punto desplazamiento : orientacion) {
                ocupada[(y + desplazamiento.y()) * ancho + x + desplazamiento.x()] = valor;
            }
        }
    }
}
