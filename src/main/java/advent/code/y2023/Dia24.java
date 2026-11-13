package advent.code.y2023;

import advent.code.Resolvedor;
import advent.code.SistemaLineal;
import advent.code.Utiles;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Año 2023 — Día 24: Nunca Me Digas las Probabilidades
 *
 * <p>El input es la posición y la velocidad de cada granizo, como
 * {@code 19, 13, 30 @ -2, 1, -2}. Cada granizo se mueve en línea recta a
 * velocidad constante.</p>
 */
public class Dia24 implements Resolvedor {

    /** Los límites del área de prueba de la parte 1, en {@code x} y en {@code y}. */
    static final long MINIMO = 200_000_000_000_000L, MAXIMO = 400_000_000_000_000L;

    /**
     * Un granizo: su posición en el instante 0 y cuánto se mueve por
     * nanosegundo.
     */
    record Granizo(long px, long py, long pz, long vx, long vy, long vz) {
    }

    /**
     * Parte 1: cuántos pares de trayectorias se cruzan, a futuro y mirando
     * solo {@code x} e {@code y}, dentro del área de prueba.
     *
     * @param lineas los granizos
     * @return la cantidad de pares
     */
    @Override
    public String parte1(List<String> lineas) {
        return String.valueOf(crucesEnArea(leer(lineas), MINIMO, MAXIMO));
    }

    /**
     * Parte 2: la suma de las coordenadas de la posición inicial de una
     * piedra que, lanzada en el instante 0 con velocidad constante, choca con
     * todos los granizos.
     *
     * @param lineas los granizos
     * @return {@code x + y + z} de la posición inicial de la piedra
     */
    @Override
    public String parte2(List<String> lineas) {
        Granizo piedra = lanzamiento(leer(lineas));
        return String.valueOf(BigInteger.valueOf(piedra.px()).add(BigInteger.valueOf(piedra.py()))
                .add(BigInteger.valueOf(piedra.pz())));
    }

    /**
     * La posición y la velocidad de una piedra que choca con todos los
     * granizos.
     *
     * <p>La piedra ({@code P}, {@code V}) choca con el granizo {@code i} si
     * hay un instante {@code tᵢ} con {@code P + tᵢ·V = pᵢ + tᵢ·vᵢ}, es decir,
     * si {@code P − pᵢ} y {@code V − vᵢ} son paralelos:
     * {@code (P − pᵢ) × (V − vᵢ) = 0}. Al desarrollar aparece
     * {@code P × V}, que no es lineal, pero es el mismo para todos los
     * granizos: restando las ecuaciones de dos granizos se cancela y queda</p>
     *
     * <pre>
     *   P × (vⱼ − vᵢ) + (pⱼ − pᵢ) × V = pⱼ × vⱼ − pᵢ × vᵢ
     * </pre>
     *
     * <p>que son tres ecuaciones lineales en las seis incógnitas. Con dos
     * pares de granizos ({@code 0-j} y {@code 0-k}) hay seis ecuaciones, que
     * se resuelven exactamente con fracciones ({@link SistemaLineal}). Si el sistema queda singular
     * (por ejemplo, con velocidades paralelas), se prueba con otros granizos.
     * Al final se comprueba que la piedra tenga coordenadas enteras y choque
     * con <b>todos</b> los granizos en un instante no negativo.</p>
     *
     * @param granizos los granizos (al menos tres)
     * @return la piedra, como un granizo más
     * @throws IllegalStateException si no hay una piedra que choque con todos
     */
    static Granizo lanzamiento(List<Granizo> granizos) {
        for (int j = 1; j < granizos.size(); j++) {
            for (int k = j + 1; k < granizos.size(); k++) {
                BigFraction[][] sistema = new BigFraction[6][];
                System.arraycopy(ecuaciones(granizos.get(0), granizos.get(j)), 0, sistema, 0, 3);
                System.arraycopy(ecuaciones(granizos.get(0), granizos.get(k)), 0, sistema, 3, 3);
                Optional<BigFraction[]> solucion = SistemaLineal.resolverUnica(sistema);
                if (solucion.isEmpty()) continue;
                Granizo piedra = aEnteros(solucion.get());
                for (Granizo granizo : granizos) {
                    if (!choca(piedra, granizo)) {
                        throw new IllegalStateException("La piedra " + piedra + " no choca con " + granizo);
                    }
                }
                return piedra;
            }
        }
        throw new IllegalStateException("No se pudo determinar la piedra: ningún sistema tiene solución única");
    }

    /**
     * Las tres ecuaciones lineales del par {@code (a, b)}, como filas
     * {@code [Px, Py, Pz, Vx, Vy, Vz | término independiente]}.
     */
    private static BigFraction[][] ecuaciones(Granizo a, Granizo b) {
        long ax = b.vx() - a.vx(), ay = b.vy() - a.vy(), az = b.vz() - a.vz();
        long bx = b.px() - a.px(), by = b.py() - a.py(), bz = b.pz() - a.pz();
        // c = p_b × v_b − p_a × v_a
        BigInteger[] cb = cruz3(b.px(), b.py(), b.pz(), b.vx(), b.vy(), b.vz());
        BigInteger[] ca = cruz3(a.px(), a.py(), a.pz(), a.vx(), a.vy(), a.vz());
        long[][] coeficientes = {
                {0, az, -ay, 0, -bz, by},
                {-az, 0, ax, bz, 0, -bx},
                {ay, -ax, 0, -by, bx, 0}};
        BigFraction[][] filas = new BigFraction[3][7];
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 6; c++) filas[f][c] = new BigFraction(coeficientes[f][c]);
            filas[f][6] = new BigFraction(cb[f].subtract(ca[f]));
        }
        return filas;
    }

    /** El producto vectorial en el espacio, exacto. */
    private static BigInteger[] cruz3(long ax, long ay, long az, long bx, long by, long bz) {
        BigInteger x1 = BigInteger.valueOf(ax), y1 = BigInteger.valueOf(ay), z1 = BigInteger.valueOf(az);
        BigInteger x2 = BigInteger.valueOf(bx), y2 = BigInteger.valueOf(by), z2 = BigInteger.valueOf(bz);
        return new BigInteger[]{
                y1.multiply(z2).subtract(z1.multiply(y2)),
                z1.multiply(x2).subtract(x1.multiply(z2)),
                x1.multiply(y2).subtract(y1.multiply(x2))};
    }

    /** La solución como piedra, si todas sus coordenadas son enteras. */
    private static Granizo aEnteros(BigFraction[] solucion) {
        long[] valores = new long[6];
        for (int i = 0; i < 6; i++) {
            if (!solucion[i].getDenominator().equals(BigInteger.ONE)) {
                throw new IllegalStateException("La piedra no tiene coordenadas enteras: " + List.of(solucion));
            }
            valores[i] = solucion[i].getNumerator().longValueExact();
        }
        return new Granizo(valores[0], valores[1], valores[2], valores[3], valores[4], valores[5]);
    }

    /**
     * Si la piedra choca con el granizo: hay un mismo instante {@code t ≥ 0}
     * en el que están en el mismo lugar en los tres ejes.
     */
    static boolean choca(Granizo piedra, Granizo granizo) {
        long[] dp = {granizo.px() - piedra.px(), granizo.py() - piedra.py(), granizo.pz() - piedra.pz()};
        long[] dv = {piedra.vx() - granizo.vx(), piedra.vy() - granizo.vy(), piedra.vz() - granizo.vz()};
        // En cada eje: dp = t·dv. Si dv = 0, dp tiene que ser 0; si no, t = dp / dv para todos los ejes
        BigFraction t = null;
        for (int eje = 0; eje < 3; eje++) {
            if (dv[eje] == 0) {
                if (dp[eje] != 0) return false;
                continue;
            }
            BigFraction este = new BigFraction(dp[eje], dv[eje]);
            if (t != null && !t.equals(este)) return false;
            t = este;
        }
        return t == null || t.compareTo(BigFraction.ZERO) >= 0;
    }

    /**
     * Lee los granizos: tres números, {@code @} y otros tres.
     *
     * @param lineas el input
     * @return los granizos, en orden
     * @throws IllegalArgumentException si una línea no tiene el formato esperado
     */
    static List<Granizo> leer(List<String> lineas) {
        return Utiles.lineasNoVacias(lineas).stream().map(linea -> {
            String[] partes = linea.split("@", -1);
            if (partes.length != 2) throw new IllegalArgumentException("Línea inválida: '" + linea + "'");
            List<Long> p = Utiles.leerNumerosLargos(partes[0], 3), v = Utiles.leerNumerosLargos(partes[1], 3);
            return new Granizo(p.get(0), p.get(1), p.get(2), v.get(0), v.get(1), v.get(2));
        }).toList();
    }

    /**
     * Cuántos pares de granizos tienen trayectorias que se cruzan en el plano
     * {@code x, y} dentro del cuadrado {@code [minimo, maximo]²}, en el futuro
     * de los dos.
     *
     * @param granizos los granizos
     * @param minimo   el menor valor de {@code x} y de {@code y} del área
     * @param maximo   el mayor valor de {@code x} y de {@code y} del área
     * @return la cantidad de pares
     */
    static long crucesEnArea(List<Granizo> granizos, long minimo, long maximo) {
        long cruces = 0;
        for (int i = 0; i < granizos.size(); i++) {
            for (int j = i + 1; j < granizos.size(); j++) {
                if (seCruzanEnArea(granizos.get(i), granizos.get(j), minimo, maximo)) cruces++;
            }
        }
        return cruces;
    }

    /**
     * Si las trayectorias de dos granizos se cruzan en el plano {@code x, y},
     * dentro del área y en el futuro de los dos.
     *
     * <p>Las trayectorias son {@code p₁ + t·v₁} y {@code p₂ + s·v₂}. Igualando y
     * resolviendo con productos cruzados ({@code a × b = aₓ·b_y − a_y·bₓ}):</p>
     *
     * <pre>
     *   D = v₁ × v₂,   t = ((p₂ − p₁) × v₂) / D,   s = ((p₂ − p₁) × v₁) / D
     * </pre>
     *
     * <p>Si {@code D = 0} las trayectorias son paralelas y no se cruzan. El
     * cruce está en el futuro si {@code t ≥ 0} y {@code s ≥ 0}, y su
     * coordenada {@code x} es {@code p₁ₓ + t·v₁ₓ = (p₁ₓ·D + tₙ·v₁ₓ) / D} (con
     * {@code tₙ} el numerador de {@code t}), y lo mismo para {@code y}.</p>
     *
     * <p>Todo se calcula <b>exacto</b>, con {@link BigInteger}: las posiciones
     * son del orden de 10¹⁴ y los productos pasan holgadamente el rango de un
     * {@code long}, y con {@code double} un cruce justo en el borde del área
     * podría caer del lado equivocado. Para no dividir, las fracciones se
     * comparan multiplicando por {@code D}, después de dejar {@code D}
     * positivo.</p>
     *
     * @param a      un granizo
     * @param b      otro granizo
     * @param minimo el menor valor de {@code x} y de {@code y} del área
     * @param maximo el mayor valor de {@code x} y de {@code y} del área
     * @return si las trayectorias se cruzan dentro del área, a futuro
     * @throws IllegalStateException si las trayectorias son la misma recta (se
     *                               cruzan en infinitos puntos)
     */
    static boolean seCruzanEnArea(Granizo a, Granizo b, long minimo, long maximo) {
        BigInteger dx = BigInteger.valueOf(b.px() - a.px()), dy = BigInteger.valueOf(b.py() - a.py());
        BigInteger d = cruz(BigInteger.valueOf(a.vx()), BigInteger.valueOf(a.vy()),
                BigInteger.valueOf(b.vx()), BigInteger.valueOf(b.vy()));
        BigInteger numeradorT = cruz(dx, dy, BigInteger.valueOf(b.vx()), BigInteger.valueOf(b.vy()));
        BigInteger numeradorS = cruz(dx, dy, BigInteger.valueOf(a.vx()), BigInteger.valueOf(a.vy()));
        if (d.signum() == 0) {
            if (numeradorS.signum() == 0) {
                throw new IllegalStateException("Las trayectorias de " + a + " y " + b + " están sobre la misma recta");
            }
            return false;
        }
        if (d.signum() < 0) {
            d = d.negate();
            numeradorT = numeradorT.negate();
            numeradorS = numeradorS.negate();
        }
        if (numeradorT.signum() < 0 || numeradorS.signum() < 0) return false;
        // x·D e y·D del cruce, para compararlos con minimo·D y maximo·D sin dividir
        BigInteger x = BigInteger.valueOf(a.px()).multiply(d).add(numeradorT.multiply(BigInteger.valueOf(a.vx())));
        BigInteger y = BigInteger.valueOf(a.py()).multiply(d).add(numeradorT.multiply(BigInteger.valueOf(a.vy())));
        BigInteger desde = BigInteger.valueOf(minimo).multiply(d), hasta = BigInteger.valueOf(maximo).multiply(d);
        return x.compareTo(desde) >= 0 && x.compareTo(hasta) <= 0 && y.compareTo(desde) >= 0 && y.compareTo(hasta) <= 0;
    }

    /** El producto cruzado en el plano: {@code aₓ·b_y − a_y·bₓ}. */
    private static BigInteger cruz(BigInteger ax, BigInteger ay, BigInteger bx, BigInteger by) {
        return ax.multiply(by).subtract(ay.multiply(bx));
    }
}
