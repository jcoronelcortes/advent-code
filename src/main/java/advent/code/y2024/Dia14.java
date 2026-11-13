package advent.code.y2024;

import advent.code.Matematicas;
import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Año 2024 — Día 14: Reducto del Baño
 *
 * <p>El input lista robots, cada uno con su posición {@code p=x,y} y su
 * velocidad {@code v=x,y} (casillas por segundo). Se mueven en línea recta
 * sobre un piso de 101 × 103 casillas y, al llegar a un borde, aparecen del
 * otro lado. Pueden compartir casilla.</p>
 */
public class Dia14 implements Resolvedor {

    static final int ANCHO = 101;
    static final int ALTO = 103;
    private static final int SEGUNDOS_PARTE1 = 100;

    /**
     * Parte 1: factor de seguridad después de 100 segundos.
     *
     * @param lineas los robots, uno por línea
     * @return el producto de la cantidad de robots en cada cuadrante
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(factorDeSeguridad(leerRobots(lineas), ANCHO, ALTO, SEGUNDOS_PARTE1));
    }

    /**
     * Parte 2: primer segundo en que la mayoría de los robots forman el
     * dibujo de un árbol de Navidad.
     *
     * @param lineas los robots, uno por línea
     * @return el segundo
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(segundoMasAgrupado(leerRobots(lineas), ANCHO, ALTO));
    }

    /**
     * Primer segundo en que los robots están más agrupados en los dos ejes.
     *
     * <p>El enunciado no dice cómo es el árbol, pero un dibujo hecho por la
     * mayoría de los robots los junta en una zona chica, mientras que el
     * resto del tiempo están desparramados por todo el piso. Así que se busca
     * el momento de <b>menor dispersión</b>.</p>
     *
     * <p>Los ejes se pueden separar: la {@code x} de cada robot se repite
     * cada {@code ancho} segundos y la {@code y} cada {@code alto} segundos.
     * Basta probar {@code ancho} segundos para encontrar el resto
     * {@code t mod ancho} con las {@code x} más juntas, y {@code alto}
     * segundos para {@code t mod alto}. El teorema chino del resto
     * ({@link Matematicas#restoChino}) combina los dos restos en el primer
     * segundo que cumple ambos. Son {@code ancho + alto} pruebas en vez de
     * {@code ancho × alto}.</p>
     *
     * @param robots los robots
     * @param ancho  ancho del piso
     * @param alto   alto del piso
     * @return el primer segundo de menor dispersión en ambos ejes
     */
    static long segundoMasAgrupado(List<Robot> robots, int ancho, int alto) {
        int restoX = momentoMasAgrupado(robots, ancho, Punto::x, ancho, alto);
        int restoY = momentoMasAgrupado(robots, alto, Punto::y, ancho, alto);
        return Matematicas.restoChino(new long[]{restoX, restoY}, new long[]{ancho, alto});
    }

    /**
     * Segundo, dentro de un período, en que una coordenada de los robots
     * tiene la menor dispersión.
     *
     * <p>La dispersión es la varianza, pero sin dividir: {@code n·Σc² − (Σc)²}
     * es {@code n²} veces la varianza, así que ordena igual y se calcula en
     * enteros exactos.</p>
     *
     * @param robots     los robots
     * @param periodo    cada cuántos segundos se repite esa coordenada
     * @param coordenada qué coordenada mirar
     * @param ancho      ancho del piso
     * @param alto       alto del piso
     * @return el segundo en {@code [0, periodo)} con menor dispersión
     */
    static int momentoMasAgrupado(List<Robot> robots, int periodo, ToIntFunction<Punto> coordenada,
                                  int ancho, int alto) {
        int mejor = 0;
        long menorDispersion = Long.MAX_VALUE;
        for (int segundos = 0; segundos < periodo; segundos++) {
            long suma = 0;
            long sumaCuadrados = 0;
            for (Robot robot : robots) {
                long c = coordenada.applyAsInt(robot.posicionEn(ancho, alto, segundos));
                suma += c;
                sumaCuadrados += c * c;
            }
            long dispersion = robots.size() * sumaCuadrados - suma * suma;
            if (dispersion < menorDispersion) {
                menorDispersion = dispersion;
                mejor = segundos;
            }
        }
        return mejor;
    }

    /**
     * Un robot: posición inicial y velocidad, en casillas por segundo.
     *
     * @param posicion  posición inicial ({@code x} desde la pared izquierda, {@code y} desde la de arriba)
     * @param velocidad desplazamiento por segundo ({@code y} positiva hacia abajo)
     */
    record Robot(Punto posicion, Punto velocidad) {

        /**
         * Dónde está después de una cantidad de segundos.
         *
         * <p>Como los bordes se conectan con el lado opuesto, cada eje es un
         * círculo: la posición es {@code (p + v·t) mod tamaño}, sin simular
         * segundo a segundo. {@link Matematicas#modulo} da siempre un valor no
         * negativo, aunque la velocidad sea negativa.</p>
         *
         * @param ancho    ancho del piso
         * @param alto     alto del piso
         * @param segundos tiempo transcurrido
         * @return la posición en ese momento
         */
        Punto posicionEn(int ancho, int alto, long segundos) {
            long x = Matematicas.modulo(posicion.x() + velocidad.x() * segundos, ancho);
            long y = Matematicas.modulo(posicion.y() + velocidad.y() * segundos, alto);
            return new Punto((int) x, (int) y);
        }
    }

    /**
     * Factor de seguridad: cuántos robots hay en cada cuadrante del piso,
     * multiplicados entre sí. Los robots justo en la fila o la columna del
     * medio no están en ningún cuadrante.
     *
     * @param robots   los robots
     * @param ancho    ancho del piso (impar)
     * @param alto     alto del piso (impar)
     * @param segundos tiempo transcurrido
     * @return el producto de los cuatro cuadrantes
     */
    static long factorDeSeguridad(List<Robot> robots, int ancho, int alto, long segundos) {
        long[] cuadrantes = robotsPorCuadrante(robots, ancho, alto, segundos);
        return Matematicas.productoExacto(cuadrantes);
    }

    /**
     * Cantidad de robots en cada cuadrante, en orden: arriba a la izquierda,
     * arriba a la derecha, abajo a la izquierda, abajo a la derecha.
     *
     * @param robots   los robots
     * @param ancho    ancho del piso (impar)
     * @param alto     alto del piso (impar)
     * @param segundos tiempo transcurrido
     * @return los cuatro conteos
     */
    static long[] robotsPorCuadrante(List<Robot> robots, int ancho, int alto, long segundos) {
        if (ancho % 2 == 0 || alto % 2 == 0) {
            throw new IllegalArgumentException("El piso debe tener ancho y alto impares para tener una fila y una columna del medio");
        }
        int medioX = ancho / 2;
        int medioY = alto / 2;
        long[] cuadrantes = new long[4];
        for (Robot robot : robots) {
            Punto p = robot.posicionEn(ancho, alto, segundos);
            if (p.x() == medioX || p.y() == medioY) continue;
            int cuadrante = (p.y() < medioY ? 0 : 2) + (p.x() < medioX ? 0 : 1);
            cuadrantes[cuadrante]++;
        }
        return cuadrantes;
    }

    /**
     * Lee los robots: cada línea {@code p=x,y v=x,y} tiene 4 números.
     *
     * @param lineas el input
     * @return los robots
     */
    static List<Robot> leerRobots(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream()
                .map(linea -> Utiles.leerNumeros(linea, 4))
                .map(n -> new Robot(new Punto(n.get(0), n.get(1)), new Punto(n.get(2), n.get(3))))
                .toList();
    }
}
