package advent.code.y2023;

import advent.code.Grilla;
import advent.code.Punto;
import advent.code.Resolvedor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Año 2023 — Día 3: Proporciones de Engranajes
 *
 * <p>El input es el esquema de un motor: una grilla con números, símbolos y
 * puntos ({@code .}, que no son símbolos). Un número es un <b>número de
 * pieza</b> si tiene algún símbolo en alguna de las casillas que lo rodean,
 * incluidas las diagonales. Un <b>engranaje</b> es un {@code *} con
 * exactamente dos números de pieza alrededor.</p>
 */
public class Dia03 implements Resolvedor {

    static final char VACIO = '.';
    static final char ENGRANAJE = '*';

    /**
     * Parte 1: la suma de todos los números de pieza.
     *
     * @param lineas el esquema del motor
     * @return la suma
     */
    @Override
    public String parte1(List<String> lineas) {
        Grilla esquema = Grilla.desdeLineas(lineas);
        long suma = numeros(esquema).stream()
                .filter(numero -> !numero.simbolos().isEmpty())
                .mapToLong(Numero::valor)
                .sum();
        return String.valueOf(suma);
    }

    /**
     * Parte 2: la suma de las proporciones de todos los engranajes: los
     * {@code *} con exactamente dos números de pieza vecinos, cuya proporción
     * es el producto de esos dos números.
     *
     * @param lineas el esquema del motor
     * @return la suma de las proporciones
     */
    @Override
    public String parte2(List<String> lineas) {
        Grilla esquema = Grilla.desdeLineas(lineas);
        long suma = 0;
        for (Map.Entry<Punto, List<Integer>> simbolo : numerosPorSimbolo(esquema).entrySet()) {
            List<Integer> vecinos = simbolo.getValue();
            if (esquema.obtener(simbolo.getKey()) == ENGRANAJE && vecinos.size() == 2) {
                suma = Math.addExact(suma, (long) vecinos.get(0) * vecinos.get(1));
            }
        }
        return String.valueOf(suma);
    }

    /**
     * La relación al revés: para cada símbolo, los números que lo tienen de
     * vecino. Como cada número guarda sus símbolos sin repetir, un número
     * aparece una sola vez en la lista de cada símbolo aunque lo toquen
     * varias de sus cifras.
     *
     * @param esquema el esquema
     * @return posición del símbolo → valores de sus números vecinos
     */
    static Map<Punto, List<Integer>> numerosPorSimbolo(Grilla esquema) {
        Map<Punto, List<Integer>> porSimbolo = new HashMap<>();
        for (Numero numero : numeros(esquema)) {
            for (Punto simbolo : numero.simbolos()) {
                porSimbolo.computeIfAbsent(simbolo, posicion -> new ArrayList<>()).add(numero.valor());
            }
        }
        return porSimbolo;
    }

    /** Si un carácter es un símbolo: ni cifra ni punto. */
    static boolean esSimbolo(char caracter) {
        return caracter != VACIO && !Character.isDigit(caracter);
    }

    /**
     * Un número del esquema y los símbolos que lo rodean.
     *
     * @param valor    el número
     * @param simbolos las posiciones de los símbolos vecinos (incluidas las
     *                 diagonales), sin repetir
     */
    record Numero(int valor, Set<Punto> simbolos) {}

    /**
     * Todos los números del esquema, de arriba a abajo y de izquierda a
     * derecha.
     *
     * <p>Un número es una racha de cifras seguidas en una fila; termina en
     * cualquier carácter que no sea cifra o en el borde. Sus vecinos son las
     * casillas que rodean a cualquiera de sus cifras; se guardan en un
     * conjunto para que un símbolo vecino de dos cifras cuente una sola
     * vez.</p>
     *
     * @param esquema el esquema
     * @return los números con sus símbolos vecinos
     */
    static List<Numero> numeros(Grilla esquema) {
        List<Numero> numeros = new ArrayList<>();
        for (int fila = 0; fila < esquema.filas(); fila++) {
            int columna = 0;
            while (columna < esquema.columnas()) {
                if (!Character.isDigit(esquema.obtener(new Punto(columna, fila)))) {
                    columna++;
                    continue;
                }
                int valor = 0;
                Set<Punto> simbolos = new LinkedHashSet<>();
                while (columna < esquema.columnas() && Character.isDigit(esquema.obtener(new Punto(columna, fila)))) {
                    Punto cifra = new Punto(columna, fila);
                    valor = Math.addExact(Math.multiplyExact(valor, 10), esquema.obtener(cifra) - '0');
                    for (Punto vecino : esquema.vecinosConDiagonales(cifra)) {
                        if (esSimbolo(esquema.obtener(vecino))) simbolos.add(vecino);
                    }
                    columna++;
                }
                numeros.add(new Numero(valor, simbolos));
            }
        }
        return numeros;
    }
}
