package advent.code.y2024;

import advent.code.Matematicas;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Año 2024 — Día 11: Piedras Plutonianas
 *
 * <p>El input es una fila de piedras con un número grabado. En cada parpadeo,
 * todas cambian a la vez según la primera regla que se cumpla: el 0 pasa a
 * ser 1; un número con cantidad par de dígitos se parte en dos piedras (mitad
 * izquierda y mitad derecha de los dígitos); cualquier otro se multiplica por
 * 2024.</p>
 */
public class Dia11 implements Resolvedor {

    private static final int PARPADEOS_PARTE1 = 25;
    private static final int PARPADEOS_PARTE2 = 75;

    /**
     * Parte 1: cantidad de piedras después de 25 parpadeos.
     *
     * @param lineas la fila de piedras inicial
     * @return la cantidad de piedras
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(contarPiedras(Utiles.leerNumerosLargos(lineas), PARPADEOS_PARTE1));
    }

    /**
     * Parte 2: cantidad de piedras después de 75 parpadeos.
     *
     * @param lineas la fila de piedras inicial
     * @return la cantidad de piedras
     */
    @Override
    public String parte2(List<String> lineas) {
        return String.valueOf(contarPiedras(Utiles.leerNumerosLargos(lineas), PARPADEOS_PARTE2));
    }

    /**
     * Cantidad de piedras después de una cantidad de parpadeos.
     *
     * <p>Cada piedra evoluciona sola, sin importar sus vecinas, y la pregunta
     * es solo cuántas hay: el orden no importa. Así que en vez de la fila se
     * guarda cuántas piedras hay de cada número. Las piedras con el mismo
     * número se transforman juntas, y los números distintos son pocos aunque
     * la fila crezca de forma exponencial.</p>
     *
     * @param piedras   números de la fila inicial
     * @param parpadeos cantidad de parpadeos
     * @return cantidad de piedras al final
     */
    static long contarPiedras(List<Long> piedras, int parpadeos) {
        Map<Long, Long> cantidades = new HashMap<>();
        for (long piedra : piedras) {
            cantidades.merge(piedra, 1L, Math::addExact);
        }
        for (int i = 0; i < parpadeos; i++) {
            Map<Long, Long> siguientes = new HashMap<>();
            cantidades.forEach((piedra, cantidad) -> {
                for (long nueva : transformar(piedra)) {
                    siguientes.merge(nueva, cantidad, Math::addExact);
                }
            });
            cantidades = siguientes;
        }
        return Matematicas.sumaExacta(cantidades.values());
    }

    /**
     * Un parpadeo sobre la fila completa, conservando el orden. Sirve para
     * comparar con los pasos intermedios del ejemplo.
     *
     * @param piedras la fila antes del parpadeo
     * @return la fila después del parpadeo
     */
    static List<Long> parpadear(List<Long> piedras) {
        List<Long> siguientes = new ArrayList<>();
        for (long piedra : piedras) {
            siguientes.addAll(transformar(piedra));
        }
        return siguientes;
    }

    /**
     * En qué se convierte una piedra al parpadear: se aplica la primera regla
     * que se cumpla.
     *
     * @param piedra número grabado
     * @return una o dos piedras, en orden
     */
    static List<Long> transformar(long piedra) {
        if (piedra == 0) return List.of(1L);
        int digitos = Matematicas.cantidadDigitos(piedra);
        if (digitos % 2 == 0) {
            long divisor = Matematicas.potenciaDeDiez(digitos / 2);
            return List.of(piedra / divisor, piedra % divisor);
        }
        return List.of(Math.multiplyExact(piedra, 2024L));
    }
}
