package advent.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que los fragmentos de código de la documentación
 * ({@code soluciones/{anio}/dia{DD}.md}) sigan coincidiendo con el código.
 *
 * <p>Un fragmento se revisa si su primera línea es un comentario con el
 * nombre del archivo, como {@code // Dia05.java — parte1()}. Cada una de sus
 * líneas tiene que existir en ese archivo. Se ignoran las líneas vacías, las
 * que son solo {@code }} o {@code ...}, las que contienen {@code ...} (código
 * abreviado a propósito) y los comentarios. Una línea de la documentación
 * puede tener un comentario extra al final que el código no tiene.</p>
 *
 * <p>El archivo se busca primero en el paquete del año de la página
 * ({@code advent.code.y2024}) y si no, entre las utilidades
 * ({@code advent.code}).</p>
 */
@DisplayName("Documentación")
class PruebaDocumentacion {

    private static final Path SOLUCIONES = Path.of("soluciones");
    private static final Path CODIGO = Path.of("src/main/java/advent/code");

    /** Bloque de código Java en Markdown. */
    private static final Pattern BLOQUE_JAVA = Pattern.compile("```java\\n(.*?)```", Pattern.DOTALL);

    /** Primera línea de un fragmento que indica de qué archivo es. */
    private static final Pattern CABECERA = Pattern.compile("^//\\s*([A-Za-z0-9]+\\.java)");

    // -------------------------------------------------------------------------
    // Documentación real
    // -------------------------------------------------------------------------

    /** Todas las páginas de soluciones, de todos los años. */
    static Stream<Path> paginas() throws IOException {
        try (Stream<Path> archivos = Files.walk(SOLUCIONES)) {
            return archivos.filter(p -> p.getFileName().toString().matches("dia\\d{2}\\.md"))
                    .sorted()
                    .toList()
                    .stream();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paginas")
    @DisplayName("los fragmentos de código coinciden con el código")
    void fragmentosActualizados(Path pagina) throws IOException {
        String anio = pagina.getParent().getFileName().toString();
        List<String> problemas = lineasDesactualizadas(Files.readString(pagina), archivo -> leerFuente(anio, archivo));
        assertTrue(problemas.isEmpty(), () -> "Fragmentos desactualizados en " + pagina + ":\n  "
                + String.join("\n  ", problemas));
    }

    @Test
    @DisplayName("hay páginas para revisar")
    void hayPaginas() throws IOException {
        assertTrue(paginas().count() > 0, "No se encontraron páginas en " + SOLUCIONES.toAbsolutePath());
    }

    /** Código de un archivo, buscándolo en el paquete del año y si no entre las utilidades. */
    private static Optional<String> leerFuente(String anio, String archivo) {
        for (Path candidato : List.of(CODIGO.resolve("y" + anio).resolve(archivo), CODIGO.resolve(archivo))) {
            if (Files.exists(candidato)) {
                try {
                    return Optional.of(Files.readString(candidato));
                } catch (IOException e) {
                    throw new IllegalStateException("No se pudo leer " + candidato, e);
                }
            }
        }
        return Optional.empty();
    }

    // -------------------------------------------------------------------------
    // Revisor
    // -------------------------------------------------------------------------

    /**
     * Revisa los fragmentos de un documento contra su código.
     *
     * @param documento texto Markdown
     * @param fuente    código de cada archivo por nombre (vacío si no existe)
     * @return descripción de cada línea que no coincide (vacía si todo está al día)
     */
    static List<String> lineasDesactualizadas(String documento, Function<String, Optional<String>> fuente) {
        List<String> problemas = new ArrayList<>();
        Matcher bloque = BLOQUE_JAVA.matcher(documento);
        while (bloque.find()) {
            List<String> lineas = bloque.group(1).lines().toList();
            if (lineas.isEmpty()) continue;
            Matcher cabecera = CABECERA.matcher(lineas.getFirst().strip());
            if (!cabecera.find()) continue;

            String archivo = cabecera.group(1);
            Optional<String> codigo = fuente.apply(archivo);
            if (codigo.isEmpty()) {
                problemas.add("[" + archivo + "] el archivo no existe");
                continue;
            }
            Set<String> lineasCodigo = codigo.get().lines().map(String::strip).collect(Collectors.toSet());
            for (String linea : lineas.subList(1, lineas.size())) {
                String texto = linea.strip();
                if (seIgnora(texto) || existe(texto, lineasCodigo)) continue;
                problemas.add("[" + archivo + "] " + texto);
            }
        }
        return problemas;
    }

    private static boolean seIgnora(String texto) {
        return texto.isEmpty() || texto.equals("}") || texto.startsWith("//") || texto.contains("...");
    }

    /** La línea está en el código, tal cual o sin el comentario que la documentación le agregó al final. */
    private static boolean existe(String texto, Set<String> lineasCodigo) {
        if (lineasCodigo.contains(texto)) return true;
        String sinComentario = texto.replaceFirst("\\s*//.*$", "");
        return !sinComentario.isEmpty() && lineasCodigo.stream().anyMatch(l -> l.startsWith(sinComentario));
    }

    // -------------------------------------------------------------------------
    // Pruebas del revisor (para que no pase siempre en verde)
    // -------------------------------------------------------------------------

    private static final Map<String, String> FUENTES = Map.of("Ejemplo.java", """
            class Ejemplo {
                int doble(int x) {
                    return x * 2;
                }
            }
            """);

    private static List<String> revisar(String documento) {
        return lineasDesactualizadas(documento, archivo -> Optional.ofNullable(FUENTES.get(archivo)));
    }

    @Test
    @DisplayName("revisor: un fragmento al día no reporta nada")
    void revisor_alDia() {
        assertEquals(List.of(), revisar("""
                ```java
                // Ejemplo.java — doble()
                int doble(int x) {
                    return x * 2;
                }
                ```
                """));
    }

    @Test
    @DisplayName("revisor: detecta una línea que ya no está en el código")
    void revisor_desactualizado() {
        assertEquals(List.of("[Ejemplo.java] return x + x;"), revisar("""
                ```java
                // Ejemplo.java
                return x + x;
                ```
                """));
    }

    @Test
    @DisplayName("revisor: ignora comentarios, '...' y bloques sin cabecera; acepta un comentario extra al final")
    void revisor_ignorados() {
        assertEquals(List.of(), revisar("""
                ```java
                // Ejemplo.java
                // un comentario cualquiera
                ...
                return x * 2;   // el doble
                long otraCosa = ...;
                }
                ```

                ```java
                codigoSinCabecera();
                ```
                """));
    }

    @Test
    @DisplayName("revisor: informa si el archivo del fragmento no existe")
    void revisor_archivoInexistente() {
        assertEquals(List.of("[NoExiste.java] el archivo no existe"), revisar("""
                ```java
                // NoExiste.java
                algo();
                ```
                """));
    }
}
