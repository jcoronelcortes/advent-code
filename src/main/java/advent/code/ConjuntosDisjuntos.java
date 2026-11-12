package advent.code;

import java.util.ArrayList;
import java.util.List;

/**
 * Conjuntos disjuntos (<i>union-find</i>) sobre los elementos {@code 0..n-1}.
 *
 * <p>Mantiene una partición de los elementos en grupos y permite, en tiempo
 * casi constante, unir dos grupos y preguntar si dos elementos están en el
 * mismo grupo. Aparece en AoC al conectar cosas de a pares: circuitos,
 * constelaciones, regiones, árbol de expansión mínima (Kruskal).</p>
 *
 * <p>Usa compresión de caminos y unión por tamaño. Los elementos son
 * índices; para otros tipos, numerarlos primero (por ejemplo, su posición
 * en la lista del input).</p>
 *
 * <pre>{@code
 * ConjuntosDisjuntos grupos = new ConjuntosDisjuntos(5);
 * grupos.unir(0, 1);
 * grupos.unir(1, 2);
 * grupos.mismoGrupo(0, 2);   // true
 * grupos.tamanio(0);         // 3
 * grupos.cantidadGrupos();   // 3  ({0,1,2}, {3}, {4})
 * }</pre>
 */
public final class ConjuntosDisjuntos {

    /** Padre de cada elemento; la raíz de un grupo es su propio padre. */
    private final int[] padre;

    /** Tamaño del grupo, válido solo para las raíces. */
    private final int[] tamanio;

    private int cantidadGrupos;

    /**
     * Crea {@code n} grupos de un elemento cada uno.
     *
     * @param n cantidad de elementos
     */
    public ConjuntosDisjuntos(int n) {
        if (n < 0) throw new IllegalArgumentException("Cantidad negativa: " + n);
        padre = new int[n];
        tamanio = new int[n];
        for (int i = 0; i < n; i++) {
            padre[i] = i;
            tamanio[i] = 1;
        }
        cantidadGrupos = n;
    }

    /**
     * Representante (raíz) del grupo de un elemento. Dos elementos están en el
     * mismo grupo si y solo si tienen el mismo representante.
     *
     * @param elemento elemento entre {@code 0} y {@code n-1}
     * @return el representante de su grupo
     */
    public int buscar(int elemento) {
        int raiz = elemento;
        while (padre[raiz] != raiz) raiz = padre[raiz];
        // compresión de caminos: todos los recorridos apuntan directo a la raíz
        while (padre[elemento] != raiz) {
            int siguiente = padre[elemento];
            padre[elemento] = raiz;
            elemento = siguiente;
        }
        return raiz;
    }

    /**
     * Une los grupos de dos elementos.
     *
     * @param a un elemento
     * @param b otro elemento
     * @return {@code true} si estaban en grupos distintos y se unieron;
     *         {@code false} si ya estaban en el mismo grupo
     */
    public boolean unir(int a, int b) {
        int raizA = buscar(a);
        int raizB = buscar(b);
        if (raizA == raizB) return false;
        // unión por tamaño: el grupo chico cuelga del grande, para que los árboles sean bajos
        if (tamanio[raizA] < tamanio[raizB]) {
            int temporal = raizA;
            raizA = raizB;
            raizB = temporal;
        }
        padre[raizB] = raizA;
        tamanio[raizA] += tamanio[raizB];
        cantidadGrupos--;
        return true;
    }

    /**
     * @param a un elemento
     * @param b otro elemento
     * @return {@code true} si están en el mismo grupo
     */
    public boolean mismoGrupo(int a, int b) {
        return buscar(a) == buscar(b);
    }

    /**
     * @param elemento un elemento
     * @return cantidad de elementos de su grupo
     */
    public int tamanio(int elemento) {
        return tamanio[buscar(elemento)];
    }

    /** @return cantidad de grupos distintos */
    public int cantidadGrupos() {
        return cantidadGrupos;
    }

    /** @return cantidad total de elementos */
    public int cantidadElementos() {
        return padre.length;
    }

    /**
     * Tamaños de todos los grupos, de mayor a menor.
     *
     * @return un tamaño por grupo
     */
    public List<Integer> tamanios() {
        List<Integer> resultado = new ArrayList<>(cantidadGrupos);
        for (int i = 0; i < padre.length; i++) {
            if (buscar(i) == i) resultado.add(tamanio[i]);
        }
        resultado.sort((x, y) -> Integer.compare(y, x));
        return resultado;
    }
}
