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

# Ejecutar todos los días de un año (tabla con respuestas y tiempos)
mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="2025"
```

## Arquitectura

- `Resolvedor.java` — interfaz que deben implementar todos los días (`parte1` y `parte2` reciben `List<String>` y devuelven `String`)
- `Principal.java` — dispatcher: recibe año y día como args, carga la clase `advent.code.y{anio}.Dia{DD}` vía reflexión (`cargarResolvedor`), lee el input y ejecuta ambas partes mostrando el tiempo; con solo el año ejecuta todos los días resueltos en una tabla
- `EntradaSalida.java` — `leerEntrada(anio, dia)` lee desde resources; `existeEntrada(anio, dia)` indica si el input está
- `src/main/java/advent/code/y{anio}/Dia{DD}.java` — una clase por día, implementa `Resolvedor`
- `src/main/resources/inputs/y{anio}/dia{DD}.txt` — input de cada día (**ignorado por git**)
- `enunciados/{anio}/dia{DD}_parte{N}.txt` — enunciados copiados del sitio (**ignorados por git**)
- `src/test/java/advent/code/y{anio}/Dia{DD}Prueba.java` — pruebas JUnit 5 por día con los ejemplos del enunciado
- `src/test/java/advent/code/RespuestasPrueba.java` — regresión con las respuestas aceptadas; se omite si falta el input
- `src/test/java/advent/code/PruebaDocumentacion.java` — verifica que los fragmentos de código de `soluciones/` (bloques que empiezan con `// Archivo.java`) sigan coincidiendo con el código; si se cambia código documentado, hay que actualizar el fragmento
- `soluciones/{anio}/dia{DD}.md` — documentación de cada día: por cada parte, el **enunciado** (redactado en español con palabras propias, nunca traducción literal) y debajo su **solución** (idea, implementación, verificación y resultado)

### Utilidades compartidas (`advent.code`)

Revisar antes de escribir código nuevo:

- `Utiles` — `leerNumeros` (int) y `leerNumerosLargos` (long) desde texto; `numerosPorLinea` / `numerosLargosPorLinea` (una lista por línea); `lineasNoVacias` y `secciones` (bloques separados por línea en blanco); `lineasNoVacias(lineas, n)`, `secciones(lineas, n)`, `leerNumeros(texto, n)` y `leerNumerosLargos(texto o lineas, n)` exigen una cantidad exacta y fallan con un mensaje claro
- `Matematicas` — mcd/mcm, `modulo` no negativo, potencia e inverso modular, resto chino, `sumaAritmetica`, dígitos, primos, Möbius; `sumaExacta` y `productoExacto` (detectan desborde)
- `Rango` — intervalo cerrado `[inicio, fin]`: parseo `"a-b"`, intersección, resta, unión de solapados
- `ConjuntoRangos` — unión de rangos: `contiene` (búsqueda binaria) y `cantidad`, sin enumerar valores
- `Punto` (record, `x` = columna, `y` = fila, `parsear("x,y")`) y `Direccion` (enum con giros y `desdeFlecha` para `^ v < >`); `Punto3D` (record `long`, `distanciaCuadrada`, `parsear`)
- `Grilla` — grilla de caracteres: búsqueda (`buscarUnico` para el punto de partida: falla si falta o se repite), vecinos con filtro, `contarVecinos`, `rellenar` (balde de pintura con arreglos: mucho más rápido que `BFS.explorar` en grillas grandes), rotaciones, copia; `desdeLineas` ignora líneas en blanco al final y `desdeLineas(lineas, ' ')` admite líneas de distinto largo
- `Busqueda.primero(desde, hasta, condicion)` — búsqueda binaria: el menor valor que cumple una condición monótona (falso…falso, verdadero…verdadero), o vacío; no desborda aunque el intervalo abarque todos los `long`
- `BFS` y `Dijkstra` — búsquedas genéricas con la misma forma de uso; ambas devuelven costos `long` y `Camino<N>`. `Dijkstra` admite heurística (A*) y `nodosEnCaminosMinimos`
- `ConjuntosDisjuntos` — union-find sobre índices `0..n-1`: `unir`, `mismoGrupo`, `tamanio`, `tamanios`, `cantidadGrupos`
- `SumaAcumulada2D` — suma de cualquier rectángulo en O(1); `contar(grilla, condicion)` cuenta celdas
- `CompresionCoordenadas` — coordenadas enormes → índices chicos con borde y huecos solo donde hay posiciones reales
- `Memoizador` — función recursiva con caché para programación dinámica (evita el `ConcurrentModificationException` de `computeIfAbsent` recursivo)
- Librerías: Commons Lang3/IO/Math3, Guava (`RangeSet`, `Multiset`, `Table`), JGraphT; jqwik para pruebas basadas en propiedades
- Java 25: `Gatherers.windowSliding`/`windowFixed` para recorrer ventanas consecutivas en streams

### Agregar un nuevo día

1. Leer primero el enunciado (para traducir el título) y ejecutar `./nuevo-dia.sh {anio} {dia} "Título"`: genera clase, prueba, documentación, fila del README y enunciados vacíos `dia{DD}_parte{N}.txt` (renombra los `_part{N}` en inglés, avisa si el input quedó en `inputs.y{anio}` y lo descarga si está definida `AOC_SESSION`). Si el enunciado ya está pegado muestra el título original, y si falta el título en español se detiene sin crear nada
2. Pegar el input en `src/main/resources/inputs/y{anio}/dia{DD}.txt` (y opcionalmente el enunciado en `enunciados/{anio}/`)
3. Completar el ejemplo del enunciado en `Dia{DD}Prueba.java` y quitar `@Disabled`
4. Resolver, verificar la respuesta con un método independiente (fuerza bruta) y documentar en `soluciones/{anio}/dia{DD}.md`: enunciado redactado con palabras propias y debajo la solución, por cada parte
5. Agregar las respuestas a `RespuestasPrueba` y a la tabla del README
6. Ejecutar con `mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="{anio} {dia}"`

## Convenciones

- Todo el código debe estar escrito en español (clases, métodos, variables)
- Toda la documentación del repositorio debe estar en español
- Las clases de prueba deben terminar en `Prueba` (ej. `Dia01Prueba`); las de utilidades empiezan con `Prueba` (ej. `PruebaGrilla`)
- En pruebas jqwik (`@Property`) usar `@Label`, no `@DisplayName`: jqwik omite en silencio las propiedades con `@DisplayName`. Tampoco ejecuta `@BeforeEach` de JUnit, así que la instancia se crea dentro de la propiedad
- No subir inputs ni enunciados de Advent of Code al repositorio. En `soluciones/` el enunciado va **redactado con palabras propias** (no traducido literalmente), por la misma razón
