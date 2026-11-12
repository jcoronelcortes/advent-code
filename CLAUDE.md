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
- `Principal.java` — dispatcher: recibe año y día como args, carga la clase `advent.code.y{anio}.Dia{DD}` via reflexión, lee el input y ejecuta ambas partes
- `EntradaSalida.java` — utilidades: `imprimirLinea` y `leerEntrada(anio, dia)` que lee desde resources
- `src/main/java/advent/code/y{anio}/Dia{DD}.java` — una clase por día, implementa `Resolvedor`
- `src/main/resources/inputs/y{anio}/day{DD}.txt` — archivo de input de cada día (pegar el input del sitio aquí)
- `src/test/java/advent/code/y{anio}/Dia{DD}Prueba.java` — pruebas JUnit 5 por día

### Agregar un nuevo día

1. Crear `src/main/java/advent/code/y{anio}/Dia{DD}.java` implementando `Resolvedor`
2. Pegar el input en `src/main/resources/inputs/y{anio}/day{DD}.txt`
3. Crear `src/test/java/advent/code/y{anio}/Dia{DD}Prueba.java` con los ejemplos del enunciado
4. Ejecutar con `mvn exec:java -Dexec.mainClass="advent.code.Principal" -Dexec.args="{anio} {dia}"`

## Convenciones

- Todo el código debe estar escrito en español (clases, métodos, variables)
- Toda la documentación del repositorio debe estar en español
- Las clases de prueba deben terminar en `Prueba` (ej. `Dia01Prueba`)