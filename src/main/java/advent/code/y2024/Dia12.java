package advent.code.y2024;

import advent.code.BFS;
import advent.code.Direccion;
import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * Año 2024 — Día 12: Grupos de Jardín
 *
 * <p>El input es un mapa de parcelas: cada letra es el tipo de planta de una
 * parcela. Las parcelas con la misma planta que se tocan horizontal o
 * verticalmente forman una <b>región</b>. Una misma letra puede formar varias
 * regiones separadas, y una región puede quedar dentro de otra.</p>
 */
public class Dia12 implements Resolvedor {

    /**
     * Parte 1: precio total de las cercas. El precio de una región es su
     * área multiplicada por su perímetro.
     *
     * @param lineas el mapa de parcelas
     * @return la suma de los precios de todas las regiones
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(precioTotal(Grilla.desdeLineas(lineas), Region::perimetro));
    }

    /**
     * Parte 2: precio total con descuento por volumen. El precio de una
     * región es su área multiplicada por su cantidad de lados (cada tramo
     * recto de cerca cuenta una vez, sin importar su largo).
     *
     * @param lineas el mapa de parcelas
     * @return la suma de los precios de todas las regiones
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(precioTotal(Grilla.desdeLineas(lineas), Region::lados));
    }

    /**
     * Suma de los precios de todas las regiones: área por la medida de la
     * cerca (perímetro en la parte 1, lados en la parte 2).
     *
     * @param mapa   el mapa de parcelas
     * @param medida medida de la cerca de una región
     * @return el precio total
     */
    static long precioTotal(Grilla mapa, ToIntFunction<Region> medida) {
        return regiones(mapa).stream()
                .mapToLong(region -> (long) region.area() * medida.applyAsInt(region))
                .sum();
    }

    /**
     * Una región: parcelas conectadas con la misma planta.
     *
     * @param planta   letra de la planta
     * @param parcelas posiciones de la región
     */
    record Region(char planta, Set<Punto> parcelas) {

        /** Cantidad de parcelas. */
        int area() {
            return parcelas.size();
        }

        /**
         * Lados de parcela que no tocan a otra parcela de la región: por cada
         * parcela, cuántos de sus 4 vecinos no son de la región. Un vecino
         * fuera del mapa tampoco es de la región, así que el borde del mapa
         * cuenta solo.
         *
         * @return el perímetro
         */
        int perimetro() {
            int perimetro = 0;
            for (Punto parcela : parcelas) {
                for (Punto vecino : parcela.vecinos()) {
                    if (!parcelas.contains(vecino)) perimetro++;
                }
            }
            return perimetro;
        }

        /**
         * Cantidad de lados: tramos rectos de cerca, sin importar su largo.
         *
         * <p>El borde de una región es un polígono (o varios, si tiene
         * huecos) con lados solo horizontales y verticales, y en un polígono
         * así hay tantos lados como esquinas. Las esquinas se cuentan
         * parcela por parcela: para cada par de direcciones perpendiculares
         * (arriba y derecha, derecha y abajo, …), la parcela tiene una
         * esquina en ese rincón si</p>
         * <ul>
         *   <li><b>convexa:</b> ninguno de los dos vecinos es de la región, o</li>
         *   <li><b>cóncava:</b> los dos son de la región pero el vecino en
         *       diagonal entre ellos no.</li>
         * </ul>
         *
         * <p>El caso cóncavo cuenta la esquina una sola vez (solo desde la
         * parcela que está en el rincón). Y dos regiones que se tocan en
         * diagonal no unen sus cercas: cada parcela ve su propia esquina
         * convexa.</p>
         *
         * @return la cantidad de lados
         */
        int lados() {
            int esquinas = 0;
            for (Punto parcela : parcelas) {
                for (Direccion direccion : Direccion.values()) {
                    Direccion perpendicular = direccion.giroDerecha();
                    boolean uno = parcelas.contains(parcela.mover(direccion));
                    boolean otro = parcelas.contains(parcela.mover(perpendicular));
                    boolean diagonal = parcelas.contains(parcela.mover(direccion).mover(perpendicular));
                    if ((!uno && !otro) || (uno && otro && !diagonal)) esquinas++;
                }
            }
            return esquinas;
        }
    }

    /**
     * Separa el mapa en regiones, en orden de lectura de su primera parcela.
     *
     * <p>Cada parcela que todavía no está en ninguna región inicia una nueva:
     * {@link BFS#explorar} desde ella, moviéndose solo a vecinos con la misma
     * planta, alcanza exactamente las parcelas de su región.</p>
     *
     * @param mapa el mapa de parcelas
     * @return las regiones
     */
    static List<Region> regiones(Grilla mapa) {
        List<Region> regiones = new ArrayList<>();
        Set<Punto> asignadas = new HashSet<>();
        for (Punto inicio : mapa.puntos()) {
            if (asignadas.contains(inicio)) continue;
            Set<Punto> parcelas = BFS.explorar(inicio, parcela -> mismaPlanta(mapa, parcela)).keySet();
            asignadas.addAll(parcelas);
            regiones.add(new Region(mapa.obtener(inicio), parcelas));
        }
        return regiones;
    }

    /**
     * Vecinos (sin diagonales) con la misma planta que la parcela.
     *
     * @param mapa    el mapa de parcelas
     * @param parcela posición
     * @return vecinos dentro del mapa con la misma letra
     */
    static List<Punto> mismaPlanta(Grilla mapa, Punto parcela) {
        char planta = mapa.obtener(parcela);
        return mapa.vecinos(parcela, otra -> otra == planta);
    }
}
