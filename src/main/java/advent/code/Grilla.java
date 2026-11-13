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

    private static final Direccion[] DIRECCIONES = Direccion.values();

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
     * Obtiene el carácter de una posición como si la grilla se repitiera
     * infinitamente en todas las direcciones: {@code (x, y)} tiene lo mismo
     * que {@code (x mod columnas, y mod filas)}, con restos no negativos (ej.
     * 2023 día 21, un jardín infinito).
     *
     * @param punto cualquier posición, dentro o fuera de la grilla
     * @return el carácter de la copia de la grilla en la que cae
     */
    public char obtenerCiclico(Punto punto) {
        return celdas[Math.floorMod(punto.y(), filas())][Math.floorMod(punto.x(), columnas())];
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
     * Posición de un carácter que tiene que aparecer <b>exactamente una vez</b>,
     * como el punto de partida de un mapa ({@code S}, {@code ^}, {@code @}).
     *
     * <p>A diferencia de {@link #buscar}, falla con un mensaje claro si el
     * carácter no está o si está más de una vez: un input mal pegado se
     * detecta enseguida en vez de usar en silencio la primera aparición.</p>
     *
     * @param caracter carácter a buscar
     * @return su única posición
     * @throws IllegalArgumentException si no aparece o aparece más de una vez
     */
    public Punto buscarUnico(char caracter) {
        List<Punto> encontrados = buscarTodos(caracter);
        if (encontrados.size() != 1) {
            throw new IllegalArgumentException("Se esperaba exactamente un '" + caracter
                    + "' en la grilla y hay " + encontrados.size());
        }
        return encontrados.getFirst();
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
    public List<Punto> vecinosConDiagonales(Punto punto) {
        return punto.vecinosConDiagonales().stream().filter(this::estaDentro).toList();
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
        for (Punto vecino : punto.vecinosConDiagonales()) {
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
     * Rellena con un carácter la zona conectada (sin diagonales) que empieza
     * en un punto: todas las celdas transitables que se alcanzan desde ahí.
     * Es el "balde de pintura" de un editor de imágenes; sirve, por ejemplo,
     * para marcar el exterior de un polígono dibujado en la grilla.
     *
     * <p>Hace lo mismo que una BFS ({@link BFS#explorar}) seguida de
     * {@link #establecer} en cada celda alcanzada, pero con arreglos
     * indexados por celda en vez de {@code HashMap} y {@code Punto}: en
     * grillas de cientos de miles de celdas es muchas veces más rápido.</p>
     *
     * <p>Primero se recorre la zona y recién después se escribe el relleno,
     * así que {@code transitable} nunca ve el carácter de relleno.</p>
     *
     * @param inicio      punto de partida
     * @param transitable qué celdas forman parte de la zona
     * @param relleno     carácter que se escribe en la zona
     * @return cantidad de celdas rellenadas ({@code 0} si el inicio está
     *         fuera de la grilla o no es transitable)
     */
    public int rellenar(Punto inicio, Predicate<Character> transitable, char relleno) {
        if (!estaDentro(inicio) || !transitable.test(obtener(inicio))) return 0;
        int ancho = columnas();
        boolean[] alcanzada = new boolean[filas() * ancho];
        int[] cola = new int[filas() * ancho];
        int frente = 0;
        int fin = 0;
        int primera = inicio.y() * ancho + inicio.x();
        alcanzada[primera] = true;
        cola[fin++] = primera;
        while (frente < fin) {
            int actual = cola[frente++];
            int x = actual % ancho;
            int y = actual / ancho;
            for (Direccion direccion : DIRECCIONES) {
                int vecinoX = x + direccion.dx();
                int vecinoY = y + direccion.dy();
                if (vecinoX < 0 || vecinoY < 0 || vecinoX >= ancho || vecinoY >= filas()) continue;
                int vecino = vecinoY * ancho + vecinoX;
                if (!alcanzada[vecino] && transitable.test(celdas[vecinoY][vecinoX])) {
                    alcanzada[vecino] = true;
                    cola[fin++] = vecino;
                }
            }
        }
        for (int i = 0; i < fin; i++) {
            celdas[cola[i] / ancho][cola[i] % ancho] = relleno;
        }
        return fin;
    }

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
