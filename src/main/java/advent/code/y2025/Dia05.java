package advent.code.y2025;

import advent.code.Rango;
import advent.code.Resolvedor;

import java.util.ArrayList;
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
        List<Rango> frescos = Rango.unir(base.rangos());
        long cantidad = base.disponibles().stream()
                .filter(id -> esFresco(frescos, id))
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
     * ({@link Rango#contarCubiertos}).</p>
     *
     * @param lineas rangos, línea en blanco e IDs disponibles
     * @return cantidad de IDs frescos distintos
     */
    @Override
    public String parte2(List<String> lineas) {
        BaseDeDatos base = BaseDeDatos.parsear(lineas);
        return String.valueOf(Rango.contarCubiertos(base.rangos()));
    }

    /**
     * Indica si un ID cae en alguno de los rangos.
     *
     * <p>Como los rangos vienen de {@link Rango#unir} (disjuntos y ordenados
     * por inicio), basta una búsqueda binaria: se busca el último rango que
     * empieza en o antes del ID y se revisa si el ID no se pasa de su fin.</p>
     *
     * @param unidos rangos disjuntos ordenados por inicio
     * @param id     ID del ingrediente
     * @return {@code true} si el ingrediente es fresco
     */
    static boolean esFresco(List<Rango> unidos, long id) {
        int bajo = 0;
        int alto = unidos.size() - 1;
        int candidato = -1;             // último rango con inicio <= id
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            if (unidos.get(medio).inicio() <= id) {
                candidato = medio;
                bajo = medio + 1;
            } else {
                alto = medio - 1;
            }
        }
        return candidato >= 0 && unidos.get(candidato).contiene(id);
    }

    /**
     * Contenido del input: rangos de IDs frescos e IDs disponibles.
     *
     * @param rangos      rangos de IDs frescos (sin unir, en el orden del input)
     * @param disponibles IDs de los ingredientes disponibles
     */
    record BaseDeDatos(List<Rango> rangos, List<Long> disponibles) {

        /**
         * Separa el input en sus dos secciones usando la primera línea en blanco.
         *
         * @param lineas líneas del input
         * @return la base de datos
         */
        static BaseDeDatos parsear(List<String> lineas) {
            List<Rango> rangos = new ArrayList<>();
            List<Long> disponibles = new ArrayList<>();
            boolean enRangos = true;
            for (String linea : lineas) {
                String texto = linea.trim();
                if (texto.isEmpty()) {
                    if (!rangos.isEmpty()) enRangos = false;
                    continue;
                }
                if (enRangos) {
                    rangos.add(Rango.parsear(texto));
                } else {
                    disponibles.add(Long.parseLong(texto));
                }
            }
            return new BaseDeDatos(rangos, disponibles);
        }
    }
}
