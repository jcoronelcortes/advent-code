package advent.code.y2023;

import advent.code.Rango;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Año 2023 — Día 5: Si Le Das un Fertilizante a una Semilla
 *
 * <p>El input es un almanaque: una lista de semillas y siete mapas que
 * convierten un número de una categoría al de la siguiente (semilla →
 * suelo → fertilizante → agua → luz → temperatura → humedad → ubicación).
 * Cada línea de un mapa ({@code destino origen largo}) dice que los números
 * de {@code origen} a {@code origen + largo − 1} pasan a
 * {@code destino}, {@code destino + 1}, ...; los números que no caen en
 * ninguna línea quedan iguales. En la parte 2, los números de semilla vienen
 * de a pares {@code inicio largo} que describen rangos enteros.</p>
 */
public class Dia05 implements Resolvedor {

    /**
     * Parte 1: la menor ubicación que corresponde a alguna de las semillas.
     *
     * @param lineas el almanaque
     * @return la menor ubicación
     */
    @Override
    public String parte1(List<String> lineas) {
        Almanaque almanaque = Almanaque.parsear(lineas);
        long menor = almanaque.semillas().stream()
                .mapToLong(almanaque::ubicacion)
                .min()
                .orElseThrow(() -> new IllegalArgumentException("No hay semillas"));
        return String.valueOf(menor);
    }

    /**
     * Parte 2: la menor ubicación, si los números de la línea
     * {@code seeds:} son pares {@code inicio largo} que describen rangos de
     * semillas.
     *
     * @param lineas el almanaque
     * @return la menor ubicación
     */
    @Override
    public String parte2(List<String> lineas) {
        Almanaque almanaque = Almanaque.parsear(lineas);
        long menor = almanaque.ubicaciones(almanaque.rangosDeSemillas()).stream()
                .mapToLong(Rango::inicio)
                .min()
                .orElseThrow(() -> new IllegalArgumentException("No hay semillas"));
        return String.valueOf(menor);
    }

    /**
     * Un tramo de un mapa: los números de {@code origen} se convierten
     * sumándoles {@code desplazamiento}.
     *
     * @param origen         los números que convierte
     * @param desplazamiento cuánto hay que sumarles (destino − origen)
     */
    record Tramo(Rango origen, long desplazamiento) {

        /** Lee una línea {@code destino origen largo}. */
        static Tramo parsear(String linea) {
            List<Long> numeros = Utiles.leerNumerosLargos(linea, 3);
            long destino = numeros.get(0), origen = numeros.get(1), largo = numeros.get(2);
            return new Tramo(Rango.desdeLargo(origen, largo), destino - origen);
        }
    }

    /**
     * Un mapa de una categoría a la siguiente.
     *
     * @param nombre  el nombre, como {@code seed-to-soil}
     * @param tramos  los tramos, ordenados por origen y sin solaparse
     */
    record Mapa(String nombre, List<Tramo> tramos) {

        Mapa {
            tramos = tramos.stream().sorted(Comparator.comparing(Tramo::origen)).toList();
            for (int i = 1; i < tramos.size(); i++) {
                if (tramos.get(i - 1).origen().intersecta(tramos.get(i).origen())) {
                    throw new IllegalArgumentException("El mapa " + nombre + " tiene tramos que se solapan");
                }
            }
        }

        /**
         * Convierte un número: si cae en un tramo, se le suma su
         * desplazamiento; si no, queda igual.
         *
         * @param numero el número de la categoría de origen
         * @return el de la categoría de destino
         */
        long convertir(long numero) {
            for (Tramo tramo : tramos) {
                if (tramo.origen().contiene(numero)) return numero + tramo.desplazamiento();
            }
            return numero;
        }

        /**
         * Convierte un rango entero de números.
         *
         * <p>El rango se parte en pedazos: la parte que cae dentro de cada
         * tramo se desplaza con ese tramo, y lo que no cae en ningún tramo
         * queda igual. Como dentro de un mismo pedazo todos los números se
         * desplazan lo mismo, cada pedazo sigue siendo un rango.</p>
         *
         * @param rango los números de la categoría de origen
         * @return los de la categoría de destino, como rangos que no se solapan
         */
        List<Rango> convertir(Rango rango) {
            List<Rango> convertidos = new ArrayList<>();
            List<Rango> sinTramo = List.of(rango);
            for (Tramo tramo : tramos) {
                rango.interseccion(tramo.origen())
                        .ifPresent(parte -> convertidos.add(parte.desplazar(tramo.desplazamiento())));
                sinTramo = sinTramo.stream().flatMap(parte -> parte.restar(tramo.origen()).stream()).toList();
            }
            convertidos.addAll(sinTramo);
            return Rango.unir(convertidos);
        }
    }

    /**
     * El almanaque: las semillas y los mapas, en orden.
     *
     * @param semillas los números de la línea {@code seeds:}
     * @param mapas    los mapas, de semilla a ubicación
     */
    record Almanaque(List<Long> semillas, List<Mapa> mapas) {

        /**
         * Lee el almanaque: la línea {@code seeds:} y, separados por líneas en
         * blanco, los mapas (un encabezado {@code a-to-b map:} y sus líneas).
         *
         * @param lineas el input
         * @return el almanaque
         */
        static Almanaque parsear(List<String> lineas) {
            List<List<String>> secciones = Utiles.secciones(lineas);
            List<Long> semillas = Utiles.leerNumerosLargos(secciones.getFirst().getFirst());
            List<Mapa> mapas = new ArrayList<>();
            for (List<String> seccion : secciones.subList(1, secciones.size())) {
                String nombre = seccion.getFirst().replace(" map:", "").strip();
                List<Tramo> tramos = seccion.subList(1, seccion.size()).stream().map(Tramo::parsear).toList();
                mapas.add(new Mapa(nombre, tramos));
            }
            return new Almanaque(semillas, List.copyOf(mapas));
        }

        /**
         * La ubicación de una semilla: el resultado de pasarla por todos los
         * mapas, en orden.
         *
         * @param semilla el número de semilla
         * @return el número de ubicación
         */
        long ubicacion(long semilla) {
            long numero = semilla;
            for (Mapa mapa : mapas) numero = mapa.convertir(numero);
            return numero;
        }

        /**
         * Las semillas leídas como pares {@code inicio largo}.
         *
         * @return los rangos de semillas
         * @throws IllegalArgumentException si la cantidad de números es impar
         */
        List<Rango> rangosDeSemillas() {
            if (semillas.size() % 2 != 0) throw new IllegalArgumentException("Las semillas no vienen de a pares");
            List<Rango> rangos = new ArrayList<>();
            for (int i = 0; i < semillas.size(); i += 2) rangos.add(Rango.desdeLargo(semillas.get(i), semillas.get(i + 1)));
            return rangos;
        }

        /**
         * Las ubicaciones de rangos enteros de semillas: se pasan todos por
         * cada mapa en orden. Después de cada mapa se unen los rangos que se
         * solapan o se tocan, para que no se multipliquen.
         *
         * @param semillas los rangos de semillas
         * @return los rangos de ubicaciones, sin solaparse
         */
        List<Rango> ubicaciones(List<Rango> semillas) {
            List<Rango> rangos = Rango.unir(semillas);
            for (Mapa mapa : mapas) {
                rangos = Rango.unir(rangos.stream().flatMap(rango -> mapa.convertir(rango).stream()).toList());
            }
            return rangos;
        }
    }
}
