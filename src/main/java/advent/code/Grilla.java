package advent.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Grilla rectangular de caracteres, el formato de input más común en Advent of Code.
 *
 * <p>Las posiciones se indican con {@link Punto}, donde {@code x} es la
 * <b>columna</b> e {@code y} es la <b>fila</b> (el origen está arriba a la
 * izquierda, igual que {@link Direccion}: {@code ARRIBA} resta a {@code y}).</p>
 *
 * <p>La grilla es mutable ({@link #establecer}) para simular cambios;
 * {@link #copiar()} crea una copia independiente. Implementa
 * {@code equals}/{@code hashCode} según su contenido, así que puede usarse
 * como clave en un {@code Map} para detectar ciclos.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Grilla grilla = Grilla.desdeLineas(lineas);
 * Punto inicio = grilla.buscar('S').orElseThrow();
 * Optional<Long> pasos = BFS.buscar(inicio,
 *         p -> grilla.obtener(p) == 'E',
 *         p -> grilla.vecinos(p, c -> c != '#'));
 * }</pre>
 */
public final class Grilla {

    private final char[][] celdas;

    /**
     * Crea una grilla vacía rellena con un carácter.
     *
     * @param filas    cantidad de filas
     * @param columnas cantidad de columnas
     * @param relleno  carácter inicial de todas las celdas
     */
    public Grilla(int filas, int columnas, char relleno) {
        if (filas < 0 || columnas < 0) {
            throw new IllegalArgumentException("Dimensiones inválidas: " + filas + "x" + columnas);
        }
        celdas = new char[filas][columnas];
        for (char[] fila : celdas) Arrays.fill(fila, relleno);
    }

    private Grilla(char[][] celdas) {
        this.celdas = celdas;
    }

    /**
     * Crea una grilla a partir de las líneas del input. Ignora las líneas en
     * blanco al final (es común que el archivo termine con una).
     *
     * @param lineas líneas del input, todas del mismo largo
     * @return la grilla
     * @throws IllegalArgumentException si las líneas no tienen el mismo largo
     */
    public static Grilla desdeLineas(List<String> lineas) {
        int cantidad = lineas.size();
        while (cantidad > 0 && lineas.get(cantidad - 1).isBlank()) cantidad--;

        char[][] celdas = new char[cantidad][];
        for (int fila = 0; fila < cantidad; fila++) {
            celdas[fila] = lineas.get(fila).toCharArray();
            if (celdas[fila].length != celdas[0].length) {
                throw new IllegalArgumentException("La fila " + fila + " mide " + celdas[fila].length
                        + " y la fila 0 mide " + celdas[0].length);
            }
        }
        return new Grilla(celdas);
    }

    /**
     * Crea una grilla a partir de líneas que pueden tener distinto largo,
     * completando las cortas con {@code relleno} a la derecha hasta el ancho
     * de la más larga. Es común en AoC que el input no traiga los espacios
     * del final de cada línea (ej. 2025 día 6). Igual que
     * {@link #desdeLineas(List)}, ignora las líneas en blanco al final.
     *
     * @param lineas  líneas del input
     * @param relleno carácter con que se completan las líneas cortas (normalmente {@code ' '})
     * @return la grilla rectangular
     */
    public static Grilla desdeLineas(List<String> lineas, char relleno) {
        int cantidad = lineas.size();
        while (cantidad > 0 && lineas.get(cantidad - 1).isBlank()) cantidad--;
        List<String> utiles = lineas.subList(0, cantidad);

        int ancho = utiles.stream().mapToInt(String::length).max().orElse(0);
        String completar = String.valueOf(relleno);
        return desdeLineas(utiles.stream()
                .map(linea -> linea + completar.repeat(ancho - linea.length()))
                .toList());
    }

    // -------------------------------------------------------------------------
    // Dimensiones y acceso
    // -------------------------------------------------------------------------

    /** @return cantidad de filas (alto) */
    public int filas() {
        return celdas.length;
    }

    /** @return cantidad de columnas (ancho) */
    public int columnas() {
        return celdas.length == 0 ? 0 : celdas[0].length;
    }

    /**
     * Indica si una posición está dentro de la grilla.
     *
     * @param punto posición {@code (x=columna, y=fila)}
     * @return {@code true} si está dentro de los límites
     */
    public boolean estaDentro(Punto punto) {
        return punto.dentroDeGrilla(filas(), columnas());
    }

    /**
     * Obtiene el carácter de una posición.
     *
     * @param punto posición {@code (x=columna, y=fila)}
     * @return el carácter
     * @throws IndexOutOfBoundsException si la posición está fuera de la grilla
     */
    public char obtener(Punto punto) {
        if (!estaDentro(punto)) throw new IndexOutOfBoundsException("Fuera de la grilla: " + punto);
        return celdas[punto.y()][punto.x()];
    }

    /**
     * Obtiene el carácter de una posición, o un valor por defecto si está fuera.
     * Evita chequear los bordes al mirar vecinos.
     *
     * @param punto   posición {@code (x=columna, y=fila)}
     * @param defecto carácter a devolver si la posición está fuera
     * @return el carácter o {@code defecto}
     */
    public char obtener(Punto punto, char defecto) {
        return estaDentro(punto) ? celdas[punto.y()][punto.x()] : defecto;
    }

    /**
     * Cambia el carácter de una posición.
     *
     * @param punto posición {@code (x=columna, y=fila)}
     * @param valor nuevo carácter
     * @throws IndexOutOfBoundsException si la posición está fuera de la grilla
     */
    public void establecer(Punto punto, char valor) {
        if (!estaDentro(punto)) throw new IndexOutOfBoundsException("Fuera de la grilla: " + punto);
        celdas[punto.y()][punto.x()] = valor;
    }

    /**
     * Devuelve una fila como texto.
     *
     * @param fila índice de la fila
     * @return la fila completa
     */
    public String fila(int fila) {
        return new String(celdas[fila]);
    }

    /**
     * Devuelve una columna como texto (de arriba hacia abajo).
     *
     * @param columna índice de la columna
     * @return la columna completa
     */
    public String columna(int columna) {
        StringBuilder texto = new StringBuilder(filas());
        for (char[] fila : celdas) texto.append(fila[columna]);
        return texto.toString();
    }

    // -------------------------------------------------------------------------
    // Recorridos y búsquedas
    // -------------------------------------------------------------------------

    /**
     * Todas las posiciones de la grilla, recorriendo fila por fila.
     *
     * @return lista de posiciones
     */
    public List<Punto> puntos() {
        List<Punto> puntos = new ArrayList<>(filas() * columnas());
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                puntos.add(new Punto(x, y));
            }
        }
        return puntos;
    }

    /**
     * Primera posición (fila por fila) que contiene el carácter.
     * Típico para ubicar el inicio {@code 'S'} o la meta {@code 'E'}.
     *
     * @param caracter carácter a buscar
     * @return la posición, o vacío si no aparece
     */
    public Optional<Punto> buscar(char caracter) {
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                if (celdas[y][x] == caracter) return Optional.of(new Punto(x, y));
            }
        }
        return Optional.empty();
    }

    /**
     * Todas las posiciones que contienen el carácter.
     *
     * @param caracter carácter a buscar
     * @return lista de posiciones, fila por fila
     */
    public List<Punto> buscarTodos(char caracter) {
        return buscarTodos(c -> c == caracter);
    }

    /**
     * Todas las posiciones cuyo carácter cumple la condición.
     *
     * @param condicion condición sobre el carácter (ej. {@code Character::isDigit})
     * @return lista de posiciones, fila por fila
     */
    public List<Punto> buscarTodos(Predicate<Character> condicion) {
        List<Punto> encontrados = new ArrayList<>();
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                if (condicion.test(celdas[y][x])) encontrados.add(new Punto(x, y));
            }
        }
        return encontrados;
    }

    /**
     * Cuenta las celdas que contienen el carácter.
     *
     * @param caracter carácter a contar
     * @return cantidad de apariciones
     */
    public long contar(char caracter) {
        long total = 0;
        for (char[] fila : celdas) {
            for (char celda : fila) {
                if (celda == caracter) total++;
            }
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Vecinos
    // -------------------------------------------------------------------------

    /**
     * Vecinos en las 4 direcciones cardinales que están dentro de la grilla.
     *
     * @param punto posición central
     * @return vecinos dentro de los límites
     */
    public List<Punto> vecinos(Punto punto) {
        return punto.vecinos().stream().filter(this::estaDentro).toList();
    }

    /**
     * Vecinos en las 4 direcciones cardinales, dentro de la grilla, cuyo
     * carácter cumple la condición. Listo para usar con {@link BFS} o {@link Dijkstra}.
     *
     * @param punto         posición central
     * @param esTransitable condición sobre el carácter del vecino (ej. {@code c -> c != '#'})
     * @return vecinos que cumplen la condición
     */
    public List<Punto> vecinos(Punto punto, Predicate<Character> esTransitable) {
        return punto.vecinos().stream()
                .filter(this::estaDentro)
                .filter(vecino -> esTransitable.test(obtener(vecino)))
                .toList();
    }

    /**
     * Vecinos en las 8 direcciones (incluye diagonales) que están dentro de la grilla.
     *
     * @param punto posición central
     * @return vecinos dentro de los límites
     */
    public List<Punto> vecinosDiagonales(Punto punto) {
        return punto.vecinosDiagonales().stream().filter(this::estaDentro).toList();
    }

    /**
     * Cuenta cuántos de los 8 vecinos (incluye diagonales) tienen cierto
     * carácter. Las posiciones fuera de la grilla no cuentan. Típico de
     * autómatas celulares (ej. 2025 día 4: rollos con menos de 4 rollos vecinos).
     *
     * @param punto    posición central
     * @param caracter carácter a contar
     * @return cantidad de vecinos con ese carácter (0 a 8)
     */
    public int contarVecinos(Punto punto, char caracter) {
        int cantidad = 0;
        for (Punto vecino : punto.vecinosDiagonales()) {
            if (estaDentro(vecino) && obtener(vecino) == caracter) cantidad++;
        }
        return cantidad;
    }

    /**
     * Cuenta cuántos de los 4 vecinos cardinales tienen cierto carácter.
     * Las posiciones fuera de la grilla no cuentan.
     *
     * @param punto    posición central
     * @param caracter carácter a contar
     * @return cantidad de vecinos con ese carácter (0 a 4)
     */
    public int contarVecinosCardinales(Punto punto, char caracter) {
        int cantidad = 0;
        for (Punto vecino : punto.vecinos()) {
            if (estaDentro(vecino) && obtener(vecino) == caracter) cantidad++;
        }
        return cantidad;
    }

    // -------------------------------------------------------------------------
    // Transformaciones (devuelven una grilla nueva)
    // -------------------------------------------------------------------------

    /**
     * Crea una copia independiente de la grilla.
     *
     * @return copia con el mismo contenido
     */
    public Grilla copiar() {
        char[][] copia = new char[filas()][];
        for (int fila = 0; fila < filas(); fila++) copia[fila] = celdas[fila].clone();
        return new Grilla(copia);
    }

    /**
     * Intercambia filas por columnas.
     *
     * @return grilla transpuesta
     */
    public Grilla transponer() {
        char[][] resultado = new char[columnas()][filas()];
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                resultado[x][y] = celdas[y][x];
            }
        }
        return new Grilla(resultado);
    }

    /**
     * Rota la grilla 90° en sentido horario.
     *
     * @return grilla rotada
     */
    public Grilla rotarDerecha() {
        char[][] resultado = new char[columnas()][filas()];
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                resultado[x][filas() - 1 - y] = celdas[y][x];
            }
        }
        return new Grilla(resultado);
    }

    /**
     * Rota la grilla 90° en sentido antihorario.
     *
     * @return grilla rotada
     */
    public Grilla rotarIzquierda() {
        char[][] resultado = new char[columnas()][filas()];
        for (int y = 0; y < filas(); y++) {
            for (int x = 0; x < columnas(); x++) {
                resultado[columnas() - 1 - x][y] = celdas[y][x];
            }
        }
        return new Grilla(resultado);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof Grilla otra && Arrays.deepEquals(celdas, otra.celdas);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(celdas);
    }

    /** @return la grilla como texto, una fila por línea (útil para depurar) */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int fila = 0; fila < filas(); fila++) {
            if (fila > 0) texto.append('\n');
            texto.append(celdas[fila]);
        }
        return texto.toString();
    }
}
