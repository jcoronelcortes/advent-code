package advent.code;

import java.util.List;

/**
 * Punto de entrada principal. Recibe el año y día como argumentos,
 * carga la clase correspondiente y ejecuta ambas partes del desafío,
 * mostrando el tiempo que tarda cada una.
 */
public class Principal {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: Principal <año> <día>  (ej. 2025 1)");
            return;
        }

        int anio = Integer.parseInt(args[0]);
        int dia  = Integer.parseInt(args[1]);

        Resolvedor resolvedor = cargarResolvedor(anio, dia);
        List<String> lineas = EntradaSalida.leerEntrada(anio, dia);

        System.out.println("=== Año " + anio + " — Día " + dia + " ===");
        ejecutar("Parte 1", () -> resolvedor.parte1(lineas));
        ejecutar("Parte 2", () -> resolvedor.parte2(lineas));
    }

    /**
     * Crea la instancia de la solución de un día: {@code advent.code.y{anio}.Dia{DD}}.
     *
     * @param anio año del desafío (ej. 2025)
     * @param dia  número de día (ej. 1)
     * @return la solución del día
     * @throws ReflectiveOperationException si la clase no existe o no se puede instanciar
     */
    public static Resolvedor cargarResolvedor(int anio, int dia) throws ReflectiveOperationException {
        String nombreClase = String.format("advent.code.y%d.Dia%02d", anio, dia);
        return (Resolvedor) Class.forName(nombreClase)
                                 .getDeclaredConstructor()
                                 .newInstance();
    }

    /** Ejecuta una parte e imprime su resultado junto con el tiempo que tardó. */
    private static void ejecutar(String nombre, java.util.function.Supplier<String> parte) {
        long inicio = System.nanoTime();
        String resultado = parte.get();
        double milisegundos = (System.nanoTime() - inicio) / 1_000_000.0;
        System.out.printf("%s: %s  (%.1f ms)%n", nombre, resultado, milisegundos);
    }
}
