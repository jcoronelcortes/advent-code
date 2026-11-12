# CLAUDE.md

Este archivo provee orientación a Claude Code (claude.ai/code) al trabajar con este repositorio.

## Proyecto

Proyecto Java 25 con Maven para resolver desafíos de Advent of Code. Paquete: `advent.code`.

## Comandos

```bash
# Compilar
mvn clean compile

# Correr tests
mvn test

# Ejecutar un día específico
mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="2025 1"
```

## Arquitectura

- `Resolvedor.java` — interfaz que deben implementar todos los días (`parte1` y `parte2` reciben `List<String>` y devuelven `String`)
- `Principal.java` — dispatcher: recibe año y día como args, carga la clase `advent.code.y{anio}.Dia{DD}` vía reflexión (`cargarResolvedor`), lee el input y ejecuta ambas partes mostrando el tiempo
- `EntradaSalida.java` — `leerEntrada(anio, dia)` lee desde resources; `existeEntrada(anio, dia)` indica si el input está
- `src/main/java/advent/code/y{anio}/Dia{DD}.java` — una clase por día, implementa `Resolvedor`
- `src/main/resources/inputs/y{anio}/day{DD}.txt` — input de cada día (**ignorado por git**)
- `enunciados/{anio}/dia{DD}_parte{N}.txt` — enunciados copiados del sitio (**ignorados por git**)
- `src/test/java/advent/code/y{anio}/Dia{DD}Prueba.java` — pruebas JUnit 5 por día con los ejemplos del enunciado
- `src/test/java/advent/code/RespuestasPrueba.java` — regresión con las respuestas aceptadas; se omite si falta el input
- `soluciones/{anio}/dia{DD}.md` — documentación detallada de cada día

### Utilidades compartidas (`advent.code`)

Revisar antes de escribir código nuevo:

- `Utiles` — `leerNumeros` (int) y `leerNumerosLargos` (long) desde texto
- `Matematicas` — mcd/mcm, `modulo` no negativo, potencia e inverso modular, resto chino, `sumaAritmetica`, dígitos, primos, Möbius
- `Rango` — intervalo cerrado `[inicio, fin]`: parseo `"a-b"`, intersección, resta, unión de solapados
- `Punto` (record, `x` = columna, `y` = fila) y `Direccion` (enum con giros)
- `Grilla` — grilla de caracteres: búsqueda, vecinos con filtro, rotaciones, copia
- `BFS` y `Dijkstra` — búsquedas genéricas con la misma forma de uso; ambas devuelven costos `long` y `Camino<N>`. `Dijkstra` admite heurística (A*) y `nodosEnCaminosMinimos`
- Librerías: Commons Lang3/IO/Math3, Guava, JGraphT; jqwik para pruebas basadas en propiedades

### Agregar un nuevo día

1. Ejecutar `./nuevo-dia.sh {anio} {dia} "Título"`: genera clase, prueba, documentación y fila del README
2. Pegar el input en `src/main/resources/inputs/y{anio}/day{DD}.txt` (y opcionalmente el enunciado en `enunciados/{anio}/`)
3. Completar el ejemplo del enunciado en `Dia{DD}Prueba.java` y quitar `@Disabled`
4. Resolver, verificar la respuesta con un método independiente (fuerza bruta) y documentar en `soluciones/{anio}/dia{DD}.md`
5. Agregar las respuestas a `RespuestasPrueba` y a la tabla del README
6. Ejecutar con `mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="{anio} {dia}"`

## Convenciones

- Todo el código debe estar escrito en español (clases, métodos, variables)
- Toda la documentación del repositorio debe estar en español
- Las clases de prueba deben terminar en `Prueba` (ej. `Dia01Prueba`); las de utilidades empiezan con `Prueba` (ej. `PruebaGrilla`)
- No subir inputs ni enunciados de Advent of Code al repositorio
