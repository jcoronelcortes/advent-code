#!/bin/bash
# Genera los archivos para un día nuevo de Advent of Code.
#
# Uso:     ./nuevo-dia.sh <anio> <dia> ["Título del día"]
# Ejemplo: ./nuevo-dia.sh 2025 4 "Departamento de Impresión"
#
# Crea (sin sobrescribir los que ya existan):
#   - src/main/java/advent/code/y<anio>/DiaNN.java         clase de la solución
#   - src/test/java/advent/code/y<anio>/DiaNNPrueba.java   pruebas con el ejemplo del enunciado
#   - soluciones/<anio>/diaNN.md                            documentación de la solución
#   - una fila en la tabla de soluciones del README
#   - las carpetas para el input y el enunciado (ignoradas por git)

set -euo pipefail

ANIO=${1:-}
DIA=${2:-}
TITULO=${3:-"Título pendiente"}

if [ -z "$ANIO" ] || [ -z "$DIA" ]; then
    echo "Uso: ./nuevo-dia.sh <anio> <dia> [\"Título del día\"]"
    exit 1
fi

DIA_FMT=$(printf "%02d" "$((10#$DIA))")
DIA_NUM=$((10#$DIA))

CLASE="src/main/java/advent/code/y${ANIO}/Dia${DIA_FMT}.java"
PRUEBA="src/test/java/advent/code/y${ANIO}/Dia${DIA_FMT}Prueba.java"
DOC="soluciones/${ANIO}/dia${DIA_FMT}.md"
INPUT="src/main/resources/inputs/y${ANIO}/dia${DIA_FMT}.txt"
ENUNCIADOS="enunciados/${ANIO}"

mkdir -p "$(dirname "$CLASE")" "$(dirname "$PRUEBA")" "$(dirname "$DOC")" "$(dirname "$INPUT")" "$ENUNCIADOS"

# Escribe stdin en el archivo solo si todavía no existe
crear() {
    local archivo=$1
    if [ -e "$archivo" ]; then
        echo "  (ya existe, no se modifica) $archivo"
        cat > /dev/null
    else
        cat > "$archivo"
        echo "  creado: $archivo"
    fi
}

echo "Año ${ANIO} — Día ${DIA_NUM}: ${TITULO}"

# -----------------------------------------------------------------------------
# Clase de la solución
# -----------------------------------------------------------------------------
crear "$CLASE" <<EOF
package advent.code.y${ANIO};

import advent.code.Resolvedor;

import java.util.List;

/**
 * Año ${ANIO} — Día ${DIA_NUM}: ${TITULO}
 *
 * <p>TODO: resumen del problema.</p>
 */
public class Dia${DIA_FMT} implements Resolvedor {

    /**
     * Parte 1: TODO.
     *
     * @param lineas líneas del input
     * @return la respuesta
     */
    @Override
    public String parte1(List<String> lineas) {
        // TODO: implementar parte 1
        return "sin implementar";
    }

    /**
     * Parte 2: TODO.
     *
     * @param lineas líneas del input
     * @return la respuesta
     */
    @Override
    public String parte2(List<String> lineas) {
        // TODO: implementar parte 2
        return "sin implementar";
    }
}
EOF

# -----------------------------------------------------------------------------
# Pruebas
# -----------------------------------------------------------------------------
crear "$PRUEBA" <<EOF
package advent.code.y${ANIO};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la solución del Día ${DIA_NUM} — Año ${ANIO}.
 *
 * <p>Los casos de prueba están basados en los ejemplos del enunciado oficial.</p>
 */
@DisplayName("Año ${ANIO} — Día ${DIA_NUM}: ${TITULO}")
class Dia${DIA_FMT}Prueba {

    private Dia${DIA_FMT} dia${DIA_FMT};

    /** Input del ejemplo del enunciado. */
    private static final List<String> EJEMPLO = List.of(
            // TODO: pegar las líneas del ejemplo
    );

    @BeforeEach
    void configurar() {
        dia${DIA_FMT} = new Dia${DIA_FMT}();
    }

    // -------------------------------------------------------------------------
    // Parte 1
    // -------------------------------------------------------------------------

    @Test
    @Disabled("TODO: completar el ejemplo y el resultado esperado")
    @DisplayName("Parte 1: el ejemplo del enunciado")
    void parte1_ejemploDelEnunciado() {
        assertEquals("TODO", dia${DIA_FMT}.parte1(EJEMPLO));
    }

    // -------------------------------------------------------------------------
    // Parte 2
    // -------------------------------------------------------------------------

    @Test
    @Disabled("TODO: completar el resultado esperado cuando se publique la parte 2")
    @DisplayName("Parte 2: el ejemplo del enunciado")
    void parte2_ejemploDelEnunciado() {
        assertEquals("TODO", dia${DIA_FMT}.parte2(EJEMPLO));
    }
}
EOF

# -----------------------------------------------------------------------------
# Documentación
# -----------------------------------------------------------------------------
crear "$DOC" <<EOF
# Año ${ANIO} — Día ${DIA_NUM}: ${TITULO}

## Descripción del problema

TODO: contexto del problema y formato del input.

---

## Parte 1

### Pregunta

TODO

### Ejemplo

TODO: tabla con el ejemplo del enunciado.

Resultado del ejemplo: **TODO**

### Desarrollo de la solución

TODO: idea clave, fragmentos de código y verificación con el ejemplo.

**Resultado con el input real: TODO**

---

## Parte 2

_Pendiente: aún no se ha publicado el enunciado de la segunda parte en el repositorio._

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | — | — |
| **Enfoque** | — | — |
| **Complejidad** | — | — |
| **Resultado** | — | — |

## Archivo de código

\`src/main/java/advent/code/y${ANIO}/Dia${DIA_FMT}.java\`
EOF

# -----------------------------------------------------------------------------
# Fila en la tabla de soluciones del README
# -----------------------------------------------------------------------------
FILA="| [${DIA_FMT}](src/main/java/advent/code/y${ANIO}/Dia${DIA_FMT}.java) | ${TITULO} | — | — | [ver](soluciones/${ANIO}/dia${DIA_FMT}.md) |"
PATRON_ANIO="^\| \[[0-9][0-9]\]\(src/main/java/advent/code/y${ANIO}/"

if grep -qF "src/main/java/advent/code/y${ANIO}/Dia${DIA_FMT}.java)" README.md; then
    echo "  (ya existe, no se modifica) fila del día en README.md"
elif grep -qE "$PATRON_ANIO" README.md; then
    # insertar después de la última fila de ese año
    ULTIMA=$(grep -nE "$PATRON_ANIO" README.md | tail -1 | cut -d: -f1)
    awk -v linea="$ULTIMA" -v fila="$FILA" '{ print } NR == linea { print fila }' README.md > README.md.tmp
    mv README.md.tmp README.md
    echo "  agregada fila del día en README.md"
else
    # primer día del año: agregar una sección nueva al final
    printf '\n### %s\n\n| Día | Título | Parte 1 | Parte 2 | Documentación |\n|-----|--------|---------|---------|---------------|\n%s\n' \
        "$ANIO" "$FILA" >> README.md
    echo "  agregada sección ${ANIO} en README.md"
fi

# -----------------------------------------------------------------------------
# Input y enunciado (no se suben al repositorio)
# -----------------------------------------------------------------------------
touch "$INPUT"

echo
echo "Siguientes pasos:"
echo "  1. Pegar el input en             $INPUT"
echo "  2. (Opcional) pegar el enunciado en ${ENUNCIADOS}/dia${DIA_FMT}_parte1.txt"
echo "  3. Completar el ejemplo en       $PRUEBA (y quitar @Disabled)"
echo "  4. Al tener las respuestas, agregarlas a src/test/java/advent/code/RespuestasPrueba.java"
echo "  5. Ejecutar: mvn exec:java -Dexec.mainClass=\"advent.code.Principal\" -Dexec.args=\"${ANIO} ${DIA_NUM}\""
