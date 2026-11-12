package advent.code.y2025;

import advent.code.ConjuntosDisjuntos;
import advent.code.Punto3D;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Año 2025 — Día 8: Patio de Juegos
 *
 * <p>El input es la lista de posiciones 3D de las cajas de conexión, una por
 * línea ({@code X,Y,Z}). Los elfos conectan pares de cajas con guirnaldas de
 * luces, empezando por los pares más cercanos (distancia en línea recta).
 * Las cajas conectadas, directa o indirectamente, forman un circuito.</p>
 */
public class Dia08 implements Resolvedor {

    /** Cantidad de conexiones de la parte 1 con el input real (en el ejemplo son 10). */
    static final int CONEXIONES_PARTE_1 = 1000;

    /** Cantidad de circuitos más grandes cuyos tamaños se multiplican. */
    private static final int CIRCUITOS_MAYORES = 3;

    /**
     * Parte 1: producto de los tamaños de los 3 circuitos más grandes tras
     * conectar los 1000 pares de cajas más cercanos.
     *
     * @param lineas posiciones de las cajas
     * @return el producto de los tres tamaños
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(productoMayores(lineas, CONEXIONES_PARTE_1));
    }

    /**
     * Parte 2: seguir conectando los pares más cercanos hasta que todas las
     * cajas formen un solo circuito, y multiplicar las coordenadas X de las
     * dos cajas de la última conexión necesaria.
     *
     * @param lineas posiciones de las cajas
     * @return producto de las X de la última conexión
     */
    @Override
    public String parte2(List<String> lineas) {
        List<Punto3D> cajas = leerCajas(lineas);
        Par ultima = ultimaConexion(cajas);
        return String.valueOf(Math.multiplyExact(cajas.get(ultima.a()).x(), cajas.get(ultima.b()).x()));
    }

    /**
     * Primera conexión que deja todas las cajas en un mismo circuito.
     *
     * <p>Es el <b>algoritmo de Kruskal</b> completo: se recorren los pares de
     * menor a mayor distancia uniendo circuitos hasta que queda uno solo. La
     * conexión que lo logra es la arista más larga del árbol de expansión
     * mínima.</p>
     *
     * @param cajas posiciones de las cajas (al menos 2)
     * @return el par cuya conexión deja un único circuito
     */
    static Par ultimaConexion(List<Punto3D> cajas) {
        if (cajas.size() < 2) throw new IllegalArgumentException("Se necesitan al menos 2 cajas");
        ConjuntosDisjuntos circuitos = new ConjuntosDisjuntos(cajas.size());
        for (Par par : paresOrdenados(cajas)) {
            if (circuitos.unir(par.a(), par.b()) && circuitos.cantidadGrupos() == 1) {
                return par;
            }
        }
        throw new IllegalStateException("Inalcanzable: con todos los pares el grafo es conexo");
    }

    /**
     * Conecta los {@code conexiones} pares más cercanos y multiplica los
     * tamaños de los 3 circuitos más grandes.
     *
     * <p>El ejemplo usa 10 conexiones y el input real 1000, por eso la
     * cantidad es un parámetro.</p>
     *
     * @param lineas      posiciones de las cajas
     * @param conexiones  cantidad de pares a conectar
     * @return el producto de los tres tamaños mayores
     */
    static long productoMayores(List<String> lineas, int conexiones) {
        List<Punto3D> cajas = leerCajas(lineas);
        List<Par> pares = paresOrdenados(cajas);

        ConjuntosDisjuntos circuitos = new ConjuntosDisjuntos(cajas.size());
        // Se cuentan los pares aunque ya estén en el mismo circuito ("no pasa nada")
        for (Par par : pares.subList(0, Math.min(conexiones, pares.size()))) {
            circuitos.unir(par.a(), par.b());
        }

        long producto = 1;
        for (int tamanio : circuitos.tamanios().subList(0, Math.min(CIRCUITOS_MAYORES, circuitos.cantidadGrupos()))) {
            producto *= tamanio;
        }
        return producto;
    }

    /**
     * Par de cajas (por su índice en el input) y su distancia al cuadrado.
     *
     * @param a                 índice de una caja
     * @param b                 índice de la otra caja
     * @param distanciaCuadrada distancia euclidiana al cuadrado entre ambas
     */
    record Par(int a, int b, long distanciaCuadrada) {}

    /**
     * Todos los pares de cajas, del más cercano al más lejano.
     *
     * <p>Se compara la distancia <b>al cuadrado</b>: ordena igual que la
     * distancia real y es exacta con enteros, sin raíces ni {@code double}.
     * Con 1000 cajas son ~500 000 pares.</p>
     *
     * @param cajas posiciones de las cajas
     * @return pares ordenados por distancia creciente
     */
    static List<Par> paresOrdenados(List<Punto3D> cajas) {
        List<Par> pares = new ArrayList<>(cajas.size() * (cajas.size() - 1) / 2);
        for (int i = 0; i < cajas.size(); i++) {
            for (int j = i + 1; j < cajas.size(); j++) {
                pares.add(new Par(i, j, cajas.get(i).distanciaCuadrada(cajas.get(j))));
            }
        }
        pares.sort(Comparator.comparingLong(Par::distanciaCuadrada));
        return pares;
    }

    /**
     * Convierte el input en posiciones, ignorando las líneas en blanco.
     *
     * @param lineas una posición {@code X,Y,Z} por línea
     * @return posiciones en el orden del input
     */
    static List<Punto3D> leerCajas(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(Punto3D::parsear).toList();
    }
}
