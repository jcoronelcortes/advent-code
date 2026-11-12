# CLAUDE.md

Este archivo provee orientación a Claude Code (claude.ai/code) al trabajar con este repositorio.

## Proyecto

Proyecto Java 25 con Maven para resolver desafíos de Advent of Code. Paquete: `org.example`.

## Comandos

```bash
# Compilar
mvn clean compile

# Empaquetar
mvn clean package

# Ejecutar un día específico
mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="2025 1"
```

## Arquitectura

- `Solver.java` — interfaz que deben implementar todos los días (`part1` y `part2` reciben `List<String>` y devuelven `String`)
- `Main.java` — dispatcher: recibe año y día como args, carga la clase `org.example.y{year}.Day{DD}` via reflexión, lee el input y llama a ambas partes
- `IO.java` — utilidades: `println` y `readInput(year, day)` que lee desde resources
- `src/main/java/org/example/y{year}/Day{DD}.java` — una clase por día, implementa `Solver`
- `src/main/resources/inputs/y{year}/day{DD}.txt` — archivo de input de cada día (pegar el input del sitio aquí)

### Agregar un nuevo día

1. Crear `src/main/java/org/example/y{year}/Day{DD}.java` implementando `Solver`
2. Pegar el input en `src/main/resources/inputs/y{year}/day{DD}.txt`
3. Ejecutar con `mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="{year} {day}"`

## Convenciones

- Toda la documentación del repositorio debe estar en español