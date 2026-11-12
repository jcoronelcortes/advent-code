# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Maven-based Java 25 project for solving Advent of Code challenges. Package: `org.example`.

## Commands

```bash
# Compile
mvn clean compile

# Package
mvn clean package

# Run main class
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## Running a specific day

```bash
mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="2024 1"
```

## Architecture

- `Solver.java` — interfaz que deben implementar todos los días (`part1` y `part2` reciben `List<String>` y devuelven `String`)
- `Main.java` — dispatcher: recibe año y día como args, carga la clase `org.example.y{year}.Day{DD}` via reflexión, lee el input y llama a ambas partes
- `IO.java` — utilidades: `println` y `readInput(year, day)` que lee desde resources
- `src/main/java/org/example/y{year}/Day{DD}.java` — una clase por día, implementa `Solver`
- `src/main/resources/inputs/y{year}/day{DD}.txt` — archivo de input de cada día (pegar el input del sitio aquí)

### Agregar un nuevo día

1. Crear `src/main/java/org/example/y{year}/Day{DD}.java` implementando `Solver`
2. Pegar el input en `src/main/resources/inputs/y{year}/day{DD}.txt`
3. Ejecutar con `mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="{year} {day}"`