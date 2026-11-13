package advent.code.y2023;

import advent.code.Punto;
import advent.code.Resolvedor;
import advent.code.Utiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Año 2023 — Día 22: Losas de Arena
 *
 * <p>El input es una foto de ladrillos de arena que todavía están cayendo:
 * cada línea da los dos extremos de un ladrillo recto, como
 * {@code 1,0,1~1,2,1}. Primero hay que dejarlos caer hasta que se apoyen en
 * el suelo ({@code z = 0}) o en otro ladrillo; después, ver cuáles se pueden
 * desintegrar sin que caiga ningún otro.</p>
 */
public class Dia22 implements Resolvedor {

    /**
     * Un ladrillo: el prisma entre dos esquinas, con {@code x1 ≤ x2},
     * {@code y1 ≤ y2} y {@code z1 ≤ z2} (todos los extremos incluidos).
     */
    record Ladrillo(int x1, int y1, int z1, int x2, int y2, int z2) {

        Ladrillo {
            if (x1 > x2 || y1 > y2 || z1 > z2) throw new IllegalArgumentException("Esquinas desordenadas");
            if (z1 < 1) throw new IllegalArgumentException("Un ladrillo no puede estar en el suelo o debajo: z = " + z1);
        }

        /** @return las casillas {@code (x, y)} que ocupa visto desde arriba */
        List<Punto> huella() {
            List<Punto> huella = new ArrayList<>();
            for (int x = x1; x <= x2; x++) for (int y = y1; y <= y2; y++) huella.add(new Punto(x, y));
            return huella;
        }

        /** @return el mismo ladrillo con su base a la altura {@code z} */
        Ladrillo bajarA(int z) {
            return new Ladrillo(x1, y1, z, x2, y2, z + (z2 - z1));
        }
    }

    /**
     * Los ladrillos ya asentados y quién se apoya en quién.
     *
     * @param ladrillos los ladrillos en su posición final, en el orden en que cayeron
     * @param apoyos    para cada ladrillo, los índices de los ladrillos sobre los que se apoya directamente
     *                  (vacío si está en el suelo)
     * @param sostiene  para cada ladrillo, los índices de los que se apoyan directamente en él
     */
    record Pila(List<Ladrillo> ladrillos, List<Set<Integer>> apoyos, List<Set<Integer>> sostiene) {
    }

    /**
     * Parte 1: cuántos ladrillos se pueden desintegrar sin que caiga ningún
     * otro.
     *
     * @param lineas la foto de los ladrillos
     * @return la cantidad
     */
    @Override
    public String parte1(List<String> lineas) {
        Pila pila = asentar(leer(lineas));
        long seguros = 0;
        for (int i = 0; i < pila.ladrillos().size(); i++) {
            if (sePuedeDesintegrar(pila, i)) seguros++;
        }
        return String.valueOf(seguros);
    }

    /**
     * Parte 2: la suma, sobre cada ladrillo, de cuántos otros caerían si se
     * desintegrara solo ese.
     *
     * @param lineas la foto de los ladrillos
     * @return la suma
     */
    @Override
    public String parte2(List<String> lineas) {
        Pila pila = asentar(leer(lineas));
        long suma = 0;
        for (int i = 0; i < pila.ladrillos().size(); i++) suma += caenAlDesintegrar(pila, i);
        return String.valueOf(suma);
    }

    /**
     * Cuántos otros ladrillos caen, en reacción en cadena, si se desintegra
     * el ladrillo {@code i}.
     *
     * <p>Un ladrillo cae si <b>todos</b> sus apoyos cayeron (los del suelo
     * nunca caen). Los ladrillos de la pila están en el orden en que cayeron,
     * y cada uno se apoya solo en ladrillos anteriores, así que alcanza con
     * recorrerlos una vez en ese orden a partir de {@code i}: cuando le toca a
     * uno, ya se sabe si cayó cada uno de sus apoyos.</p>
     *
     * @param pila la pila asentada
     * @param i    el índice del ladrillo que se desintegra
     * @return cuántos otros ladrillos caen
     */
    static int caenAlDesintegrar(Pila pila, int i) {
        boolean[] cayo = new boolean[pila.ladrillos().size()];
        cayo[i] = true;
        int caen = 0;
        for (int j = i + 1; j < cayo.length; j++) {
            Set<Integer> apoyos = pila.apoyos().get(j);
            if (!apoyos.isEmpty() && apoyos.stream().allMatch(apoyo -> cayo[apoyo])) {
                cayo[j] = true;
                caen++;
            }
        }
        return caen;
    }

    /**
     * Si al sacar el ladrillo {@code i} no cae ningún otro: todo ladrillo que
     * se apoya en él tiene que tener al menos otro apoyo.
     *
     * @param pila la pila asentada
     * @param i    el índice del ladrillo
     * @return si se puede desintegrar
     */
    static boolean sePuedeDesintegrar(Pila pila, int i) {
        return pila.sostiene().get(i).stream().allMatch(arriba -> pila.apoyos().get(arriba).size() >= 2);
    }

    /**
     * Lee los ladrillos: dos ternas {@code x,y,z} separadas por {@code ~}.
     * Los extremos pueden venir en cualquier orden.
     *
     * @param lineas la foto
     * @return los ladrillos, en el orden del input
     * @throws IllegalArgumentException si una línea no tiene seis números o un ladrillo toca el suelo
     */
    static List<Ladrillo> leer(List<String> lineas) {
        List<Ladrillo> ladrillos = new ArrayList<>();
        for (String linea : Utiles.lineasNoVacias(lineas)) {
            if (!linea.matches("\\d+,\\d+,\\d+~\\d+,\\d+,\\d+")) {
                throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
            }
            List<Integer> n = Utiles.leerNumeros(linea, 6);
            ladrillos.add(new Ladrillo(
                    Math.min(n.get(0), n.get(3)), Math.min(n.get(1), n.get(4)), Math.min(n.get(2), n.get(5)),
                    Math.max(n.get(0), n.get(3)), Math.max(n.get(1), n.get(4)), Math.max(n.get(2), n.get(5))));
        }
        return ladrillos;
    }

    /**
     * Deja caer todos los ladrillos hasta que se asienten.
     *
     * <p>Un ladrillo solo puede apoyarse en ladrillos que estén más abajo,
     * así que se los deja caer en orden de altura (por su base): cuando le
     * toca a uno, todos los que podrían frenarlo ya están en su lugar. Se
     * lleva un <b>mapa de alturas</b>: para cada casilla {@code (x, y)}, la
     * altura del tope más alto y qué ladrillo lo ocupa. Un ladrillo cae hasta
     * quedar justo encima del tope más alto bajo su huella, y se apoya en
     * todos los ladrillos que tienen ese tope (puede ser más de uno).</p>
     *
     * @param ladrillos los ladrillos de la foto
     * @return la pila asentada
     * @throws IllegalArgumentException si dos ladrillos de la foto se superponen
     */
    static Pila asentar(List<Ladrillo> ladrillos) {
        List<Ladrillo> porAltura = new ArrayList<>(ladrillos);
        porAltura.sort(Comparator.comparingInt(Ladrillo::z1));
        // Para cada casilla, {altura del tope, índice del ladrillo}; sin entrada es el suelo (altura 0)
        Map<Punto, int[]> topes = new HashMap<>();
        List<Ladrillo> asentados = new ArrayList<>();
        List<Set<Integer>> apoyos = new ArrayList<>();
        List<Set<Integer>> sostiene = new ArrayList<>();
        for (Ladrillo ladrillo : porAltura) {
            int i = asentados.size();
            List<Punto> huella = ladrillo.huella();
            int base = 0;
            for (Punto p : huella) base = Math.max(base, topes.getOrDefault(p, new int[]{0, -1})[0]);
            Set<Integer> debajo = new LinkedHashSet<>();
            for (Punto p : huella) {
                int[] tope = topes.get(p);
                if (tope != null && tope[0] == base) debajo.add(tope[1]);
            }
            Ladrillo asentado = ladrillo.bajarA(base + 1);
            if (asentado.z1() > ladrillo.z1()) {
                throw new IllegalArgumentException("El ladrillo " + ladrillo + " se superpone con otro de la foto");
            }
            for (Punto p : huella) topes.put(p, new int[]{asentado.z2(), i});
            asentados.add(asentado);
            apoyos.add(debajo);
            sostiene.add(new LinkedHashSet<>());
            for (int abajo : debajo) sostiene.get(abajo).add(i);
        }
        return new Pila(asentados, apoyos, sostiene);
    }
}
