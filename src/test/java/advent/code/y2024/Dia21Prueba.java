package advent.code.y2024;

import advent.code.BFS;
import advent.code.Direccion;
import advent.code.Punto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día 21 — Año 2024.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año 2024 — Día 21: Enigma de los Teclados")
class Dia21Prueba {

    private Dia21 dia21;

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of("029A", "980A", "179A", "456A", "379A");

    @BeforeEach
    void configurar() {
        dia21 = new Dia21();
    }

    // -------------------------------------------------------------------------
    // Herramientas independientes de la solución
    // -------------------------------------------------------------------------

    /** El botón al que apunta un brazo, o vacío si está en el hueco o fuera del teclado. */
    private static Optional<Character> botonEn(Dia21.Teclado teclado, Punto brazo) {
        return teclado.botones().entrySet().stream()
                .filter(boton -> boton.getValue().equals(brazo))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Simulador: aprieta las teclas en el teclado de más arriba y devuelve lo
     * que sale del de más abajo. Falla si algún brazo apunta al hueco o se sale.
     *
     * @param teclas   lo que tecleamos nosotros
     * @param teclados los teclados que manejan los robots, del más cercano a nosotros a la puerta
     */
    private static String simular(String teclas, Dia21.Teclado... teclados) {
        String entrada = teclas;
        for (Dia21.Teclado teclado : teclados) {
            StringBuilder salida = new StringBuilder();
            Punto brazo = teclado.posicion('A');
            for (char tecla : entrada.toCharArray()) {
                if (tecla == 'A') {
                    salida.append(botonEn(teclado, brazo).orElseThrow());
                } else {
                    brazo = brazo.mover(Direccion.desdeFlecha(tecla));
                    assertTrue(botonEn(teclado, brazo).isPresent(), "un brazo apuntó al hueco o se salió");
                }
            }
            entrada = salida.toString();
        }
        return entrada;
    }

    /**
     * Estado de la BFS: dónde apunta cada brazo (los de los robots en
     * teclados direccionales, del más cercano a nosotros al más lejano, y por
     * último el de la puerta) y cuántas teclas del código ya salieron.
     */
    private record Estado(List<Punto> brazos, int listos) {}

    /**
     * Fuerza bruta: BFS sobre el estado de todos los brazos, donde cada tecla
     * nuestra cuesta 1. No usa recorridos ni memoización: solo las reglas de
     * los teclados. Solo es viable con pocos robots (el estado crece ×5 por
     * cada uno).
     */
    private static long teclasPorBfs(String codigo, int robots) {
        List<Punto> brazos = new ArrayList<>(java.util.Collections.nCopies(robots, Dia21.DIRECCIONAL.posicion('A')));
        brazos.add(Dia21.NUMERICO.posicion('A'));
        return BFS.buscar(new Estado(List.copyOf(brazos), 0), estado -> estado.listos() == codigo.length(), estado -> {
            List<Estado> siguientes = new ArrayList<>();
            for (char tecla : "^v<>A".toCharArray()) {
                presionar(estado, tecla, codigo).ifPresent(siguientes::add);
            }
            return siguientes;
        }).orElseThrow();
    }

    /**
     * Aplica una tecla nuestra: baja por la cadena mientras cada robot apriete
     * {@code A}, y la primera flecha mueve el brazo correspondiente. Vacío si
     * un brazo cae en el hueco o la puerta recibe un botón equivocado.
     */
    private static Optional<Estado> presionar(Estado estado, char tecla, String codigo) {
        List<Punto> brazos = new ArrayList<>(estado.brazos());
        for (int i = 0; i < brazos.size(); i++) {
            Dia21.Teclado teclado = i < brazos.size() - 1 ? Dia21.DIRECCIONAL : Dia21.NUMERICO;
            if (tecla != 'A') {
                Punto brazo = brazos.get(i).mover(Direccion.desdeFlecha(tecla));
                if (botonEn(teclado, brazo).isEmpty()) return Optional.empty();
                brazos.set(i, brazo);
                return Optional.of(new Estado(List.copyOf(brazos), estado.listos()));
            }
            tecla = botonEn(teclado, brazos.get(i)).orElseThrow();
        }
        // la puerta apretó el botón `tecla`
        return tecla == codigo.charAt(estado.listos())
                ? Optional.of(new Estado(estado.brazos(), estado.listos() + 1))
                : Optional.empty();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("126384", dia21.parte1(EJEMPLO));
    }

    @ParameterizedTest(name = "{0}: {1} teclas, complejidad {2}")
    @CsvSource({
            "029A, 68, 1972",
            "980A, 60, 58800",
            "179A, 68, 12172",
            "456A, 64, 29184",
            "379A, 64, 24256"
    })
    @DisplayName("Parte 1: teclas mínimas y complejidad de cada código del ejemplo")
    void parte1_cadaCodigo(String codigo, long teclas, long complejidad) {
        Dia21.Cadena cadena = new Dia21.Cadena(Dia21.ROBOTS_PARTE1);
        assertEquals(teclas, cadena.teclasMinimas(codigo));
        assertEquals(complejidad, cadena.complejidad(codigo));
    }

    @Test
    @DisplayName("Parte 1: 029A con 0, 1 y 2 robots intermedios, como las secuencias del enunciado (12, 28 y 68)")
    void parte1_029AEnCadaNivel() {
        assertEquals(12, new Dia21.Cadena(0).teclasMinimas("029A"));   // <A^A>^^AvvvA
        assertEquals(28, new Dia21.Cadena(1).teclasMinimas("029A"));   // v<<A>>^A<A>AvA<^AA>A<vAAA>^A
        assertEquals(68, new Dia21.Cadena(2).teclasMinimas("029A"));
    }

    @Test
    @DisplayName("Parte 1: el simulador reproduce las secuencias del enunciado, nivel por nivel")
    void parte1_simuladorConLasSecuenciasDelEnunciado() {
        String nuestra = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A";
        assertEquals(68, nuestra.length());
        assertEquals("v<<A>>^A<A>AvA<^AA>A<vAAA>^A", simular(nuestra, Dia21.DIRECCIONAL));
        assertEquals("<A^A>^^AvvvA", simular(nuestra, Dia21.DIRECCIONAL, Dia21.DIRECCIONAL));
        assertEquals("029A", simular(nuestra, Dia21.DIRECCIONAL, Dia21.DIRECCIONAL, Dia21.NUMERICO));
    }

    @Test
    @DisplayName("Parte 1: los recorridos esquivan el hueco (de A a 7 no se puede empezar con <<)")
    void parte1_recorridosEsquivanElHueco() {
        List<String> recorridos = Dia21.NUMERICO.recorridos('A', '7');
        assertEquals(9, recorridos.size());   // los 10 órdenes de ^^^<< menos el que empieza con <<
        assertTrue(recorridos.stream().noneMatch(recorrido -> recorrido.startsWith("<<")));
        assertEquals(List.of(""), Dia21.NUMERICO.recorridos('5', '5'));
        assertEquals(List.of("v<"), Dia21.DIRECCIONAL.recorridos('^', '<'));   // <v pasaría por el hueco
    }

    @Test
    @DisplayName("Parte 1: la cantidad de teclas coincide con una BFS sobre las posiciones de todos los brazos")
    void parte1_coincideConBfs() {
        Dia21.Cadena cadena = new Dia21.Cadena(Dia21.ROBOTS_PARTE1);
        for (String codigo : EJEMPLO) {
            assertEquals(teclasPorBfs(codigo, Dia21.ROBOTS_PARTE1), cadena.teclasMinimas(codigo), codigo);
        }
    }

    @Test
    @DisplayName("Parte 1: un botón que no existe falla con un mensaje claro")
    void parte1_botonInexistente() {
        assertThrows(IllegalArgumentException.class, () -> new Dia21.Cadena(Dia21.ROBOTS_PARTE1).teclasMinimas("12B"));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Parte 2: con 0 a 3 robots, coincide con la BFS sobre todos los brazos")
    void parte2_coincideConBfsConPocosRobots() {
        for (int robots = 0; robots <= 3; robots++) {
            Dia21.Cadena cadena = new Dia21.Cadena(robots);
            for (String codigo : EJEMPLO) {
                assertEquals(teclasPorBfs(codigo, robots), cadena.teclasMinimas(codigo), codigo + " con " + robots);
            }
        }
    }

    @Test
    @DisplayName("Parte 2: con más robots nunca hacen falta menos teclas, y con 25 el resultado es enorme pero exacto")
    void parte2_crecimiento() {
        long anterior = 0;
        for (int robots = 0; robots <= Dia21.ROBOTS_PARTE2; robots++) {
            long teclas = new Dia21.Cadena(robots).teclasMinimas("029A");
            assertTrue(teclas > anterior, "con " + robots + " robots");
            anterior = teclas;
        }
        assertTrue(anterior > 10_000_000_000L);   // imposible armar la secuencia; solo se puede contar
    }

    @Test
    @DisplayName("Parte 2: la parte 2 es la cadena de 25 robots")
    void parte2_ejemplo() {
        assertEquals(String.valueOf(new Dia21.Cadena(Dia21.ROBOTS_PARTE2).sumaDeComplejidades(EJEMPLO)),
                dia21.parte2(EJEMPLO));
    }
}
