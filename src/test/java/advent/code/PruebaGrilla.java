package advent.code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Grilla")
class PruebaGrilla {

    //   x: 0123
    // y=0  S.#.
    // y=1  .#..
    // y=2  ..#E
    private static final List<String> LINEAS = List.of(
            "S.#.",
            ".#..",
            "..#E"
    );

    private Grilla grilla;

    @BeforeEach
    void configurar() {
        grilla = Grilla.desdeLineas(LINEAS);
    }

    // -------------------------------------------------------------------------
    // Creación y dimensiones
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("desdeLineas: dimensiones y contenido")
    void desdeLineas() {
        assertEquals(3, grilla.filas());
        assertEquals(4, grilla.columnas());
        assertEquals(String.join("\n", LINEAS), grilla.toString());
    }

    @Test
    @DisplayName("desdeLineas: ignora líneas en blanco al final")
    void desdeLineas_lineasEnBlancoAlFinal() {
        Grilla conBlancos = Grilla.desdeLineas(List.of("ab", "cd", "", "  "));
        assertEquals(2, conBlancos.filas());
    }

    @Test
    @DisplayName("desdeLineas: rechaza filas de distinto largo")
    void desdeLineas_filasDesiguales() {
        assertThrows(IllegalArgumentException.class, () -> Grilla.desdeLineas(List.of("abc", "ab")));
    }

    @Test
    @DisplayName("desdeLineas: lista vacía da grilla 0x0")
    void desdeLineas_vacia() {
        Grilla vacia = Grilla.desdeLineas(List.of());
        assertEquals(0, vacia.filas());
        assertEquals(0, vacia.columnas());
    }

    @Test
    @DisplayName("constructor: grilla rellena con un carácter")
    void constructor_relleno() {
        Grilla nueva = new Grilla(2, 3, '.');
        assertEquals("...\n...", nueva.toString());
        assertEquals(6, nueva.contar('.'));
    }

    // -------------------------------------------------------------------------
    // Acceso
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("obtener: x es columna, y es fila")
    void obtener() {
        assertEquals('S', grilla.obtener(new Punto(0, 0)));
        assertEquals('#', grilla.obtener(new Punto(2, 0)));
        assertEquals('E', grilla.obtener(new Punto(3, 2)));
    }

    @Test
    @DisplayName("obtener: fuera de la grilla lanza error o devuelve el valor por defecto")
    void obtener_fuera() {
        assertThrows(IndexOutOfBoundsException.class, () -> grilla.obtener(new Punto(4, 0)));
        assertEquals('#', grilla.obtener(new Punto(-1, 0), '#'));
        assertEquals('S', grilla.obtener(new Punto(0, 0), '#'));
    }

    @Test
    @DisplayName("estaDentro: bordes y afuera")
    void estaDentro() {
        assertTrue(grilla.estaDentro(new Punto(0, 0)));
        assertTrue(grilla.estaDentro(new Punto(3, 2)));
        assertFalse(grilla.estaDentro(new Punto(4, 2)));
        assertFalse(grilla.estaDentro(new Punto(0, -1)));
    }

    @Test
    @DisplayName("establecer: modifica la celda")
    void establecer() {
        grilla.establecer(new Punto(1, 0), 'O');
        assertEquals('O', grilla.obtener(new Punto(1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> grilla.establecer(new Punto(9, 9), 'O'));
    }

    @Test
    @DisplayName("fila y columna como texto")
    void filaYColumna() {
        assertEquals(".#..", grilla.fila(1));
        assertEquals("#.#", grilla.columna(2));
    }

    // -------------------------------------------------------------------------
    // Búsquedas
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buscar: primera aparición o vacío")
    void buscar() {
        assertEquals(Optional.of(new Punto(0, 0)), grilla.buscar('S'));
        assertEquals(Optional.of(new Punto(3, 2)), grilla.buscar('E'));
        assertEquals(Optional.empty(), grilla.buscar('X'));
    }

    @Test
    @DisplayName("buscarTodos: por carácter y por condición, fila por fila")
    void buscarTodos() {
        assertEquals(List.of(new Punto(2, 0), new Punto(1, 1), new Punto(2, 2)), grilla.buscarTodos('#'));
        assertEquals(List.of(new Punto(0, 0), new Punto(3, 2)), grilla.buscarTodos(Character::isLetter));
    }

    @Test
    @DisplayName("contar y puntos")
    void contarYPuntos() {
        assertEquals(3, grilla.contar('#'));
        assertEquals(7, grilla.contar('.'));
        assertEquals(12, grilla.puntos().size());
        assertEquals(new Punto(0, 0), grilla.puntos().getFirst());
        assertEquals(new Punto(3, 2), grilla.puntos().getLast());
    }

    // -------------------------------------------------------------------------
    // Vecinos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("vecinos: en la esquina solo hay 2")
    void vecinos_esquina() {
        assertEquals(Set.of(new Punto(1, 0), new Punto(0, 1)), new HashSet<>(grilla.vecinos(new Punto(0, 0))));
    }

    @Test
    @DisplayName("vecinos: filtrando paredes")
    void vecinos_transitables() {
        // (2,1) tiene arriba '#', abajo '#', izquierda '#', derecha '.'
        assertEquals(List.of(new Punto(3, 1)), grilla.vecinos(new Punto(2, 1), c -> c != '#'));
    }

    @Test
    @DisplayName("vecinosDiagonales: 8 en el centro, 3 en la esquina")
    void vecinosDiagonales() {
        assertEquals(8, grilla.vecinosDiagonales(new Punto(1, 1)).size());
        assertEquals(3, grilla.vecinosDiagonales(new Punto(3, 2)).size());
    }

    @Test
    @DisplayName("vecinos: integración con BFS")
    void vecinos_conBFS() {
        Punto inicio = grilla.buscar('S').orElseThrow();
        Punto meta = grilla.buscar('E').orElseThrow();
        // Desde S solo se llega a (1,0), (0,1), (0,2) y (1,2): las paredes cortan el paso hacia E
        assertEquals(Optional.empty(), BFS.buscar(inicio, meta::equals, p -> grilla.vecinos(p, c -> c != '#')));

        // Abriendo (2,0): S → (1,0) → (2,0) → (3,0) → (3,1) → E = 5 pasos
        grilla.establecer(new Punto(2, 0), '.');
        assertEquals(Optional.of(5L), BFS.buscar(inicio, meta::equals, p -> grilla.vecinos(p, c -> c != '#')));
    }

    // -------------------------------------------------------------------------
    // Transformaciones
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("copiar: la copia es independiente")
    void copiar() {
        Grilla copia = grilla.copiar();
        assertEquals(grilla, copia);

        copia.establecer(new Punto(0, 0), 'X');
        assertEquals('S', grilla.obtener(new Punto(0, 0)));
        assertNotEquals(grilla, copia);
    }

    @Test
    @DisplayName("transponer: filas pasan a ser columnas")
    void transponer() {
        Grilla transpuesta = Grilla.desdeLineas(List.of("abc", "def")).transponer();
        assertEquals("ad\nbe\ncf", transpuesta.toString());
    }

    @Test
    @DisplayName("rotarDerecha y rotarIzquierda")
    void rotar() {
        Grilla original = Grilla.desdeLineas(List.of("abc", "def"));
        assertEquals("da\neb\nfc", original.rotarDerecha().toString());
        assertEquals("cf\nbe\nad", original.rotarIzquierda().toString());
        assertEquals(original, original.rotarDerecha().rotarIzquierda());
        assertEquals(original, original.rotarDerecha().rotarDerecha().rotarDerecha().rotarDerecha());
    }

    @Test
    @DisplayName("equals/hashCode: sirve como clave para detectar ciclos")
    void equalsYHashCode() {
        Set<Grilla> vistas = new HashSet<>();
        assertTrue(vistas.add(grilla.copiar()));
        assertFalse(vistas.add(Grilla.desdeLineas(LINEAS)));
    }
}
