package advent.code.y2025;

import advent.code.ConjuntoRangos;
import advent.code.Rango;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;

/**
 * Año 2025 — Día 5: Cafetería
 *
 * <p>El input es una base de datos de ingredientes: primero una lista de
 * rangos inclusivos {@code a-b} de IDs frescos (pueden solaparse), luego una
 * línea en blanco y después la lista de IDs disponibles, uno por línea.
 * Un ID es fresco si cae en al menos un rango. Los IDs no caben en un
 * {@code int}, así que se trabaja con {@code long}.</p>
 */
public class Dia05 implements Resolvedor {

    /**
     * Parte 1: cantidad de IDs disponibles que son frescos.
     *
     * @param lineas rangos, línea en blanco e IDs disponibles
     * @return cantidad de IDs frescos
     */
    @Override
    public String parte1(List<String> lineas) {
        BaseDeDatos base = BaseDeDatos.parsear(lineas);
        ConjuntoRangos frescos = new ConjuntoRangos(base.rangos());
        long cantidad = base.disponibles().stream()
                .filter(frescos::contiene)
                .count();
        return String.valueOf(cantidad);
    }

    /**
     * Parte 2: cantidad total de IDs que los rangos consideran frescos,
     * contando una sola vez los que están en varios rangos. La lista de IDs
     * disponibles se ignora.
     *
     * <p>Los rangos son enormes (hasta ~10¹⁴ valores), así que no se pueden
     * enumerar los IDs: se unen los rangos solapados y se suman sus largos
     * ({@link ConjuntoRangos#cantidad}).</p>
     *
     * @param lineas rangos, línea en blanco e IDs disponibles
     * @return cantidad de IDs frescos distintos
     */
    @Override
    public String parte2(List<String> lineas) {
        BaseDeDatos base = BaseDeDatos.parsear(lineas);
        return String.valueOf(new ConjuntoRangos(base.rangos()).cantidad());
    }

    /**
     * Contenido del input: rangos de IDs frescos e IDs disponibles.
     *
     * @param rangos      rangos de IDs frescos (sin unir, en el orden del input)
     * @param disponibles IDs de los ingredientes disponibles
     */
    record BaseDeDatos(List<Rango> rangos, List<Long> disponibles) {

        /**
         * Separa el input en sus dos secciones usando la línea en blanco. La
         * sección de IDs puede faltar (la parte 2 no la usa).
         *
         * @param lineas líneas del input
         * @return la base de datos
         */
        static BaseDeDatos parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas);
            if (secciones.isEmpty()) return new BaseDeDatos(List.of(), List.of());
            List<Rango> rangos = secciones.getFirst().stream().map(Rango::parsear).toList();
            List<Long> disponibles = secciones.size() > 1
                    ? secciones.get(1).stream().map(Long::parseLong).toList()
                    : List.of();
            return new BaseDeDatos(rangos, disponibles);
        }
    }
}
