package advent.code;

import java.util.List;

/**
 * Punto de entrada principal. Recibe el año y día como argumentos,
 * carga la clase correspondiente y ejecuta ambas partes del desafío.
 */
public class Principal {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: Principal <año> <día>  (ej. 2025 1)");
            return;
        }

        int anio = Integer.parseInt(args[0]);
        int dia  = Integer.parseInt(args[1]);

        String nombreClase = String.format("advent.code.y%d.Dia%02d", anio, dia);
        Resolvedor resolvedor = (Resolvedor) Class.forName(nombreClase)
                                                  .getDeclaredConstructor()
                                                  .newInstance();

        List<String> lineas = EntradaSalida.leerEntrada(anio, dia);

        System.out.println("=== Año " + anio + " — Día " + dia + " ===");
        System.out.println("Parte 1: " + resolvedor.parte1(lineas));
        System.out.println("Parte 2: " + resolvedor.parte2(lineas));
    }
}