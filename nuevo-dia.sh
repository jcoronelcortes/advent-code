#!/bin/bash
# Uso: ./nuevo-dia.sh <anio> <dia>
# Ejemplo: ./nuevo-dia.sh 2025 2

ANIO=$1
DIA=$2

if [ -z "$ANIO" ] || [ -z "$DIA" ]; then
    echo "Uso: ./nuevo-dia.sh <anio> <dia>"
    exit 1
fi

DIA_FMT=$(printf "%02d" "$DIA")

CLASE="src/main/java/advent/code/y${ANIO}/Dia${DIA_FMT}.java"
PRUEBA="src/test/java/advent/code/y${ANIO}/Dia${DIA_FMT}Prueba.java"
INPUT="src/main/resources/inputs/y${ANIO}/day${DIA_FMT}.txt"

mkdir -p "$(dirname "$CLASE")" "$(dirname "$PRUEBA")" "$(dirname "$INPUT")"

# Crear clase principal
cat > "$CLASE" <<EOF
package advent.code.y${ANIO};

import advent.code.Resolvedor;

import java.util.List;

/**
 * Año ${ANIO} — Día ${DIA}:
 */
public class Dia${DIA_FMT} implements Resolvedor {

    @Override
    public String parte1(List<String> lineas) {
        // TODO: implementar parte 1
        return "sin implementar";
    }

    @Override
    public String parte2(List<String> lineas) {
        // TODO: implementar parte 2
        return "sin implementar";
    }
}
EOF

# Crear clase de prueba
cat > "$PRUEBA" <<EOF
package advent.code.y${ANIO};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Año ${ANIO} — Día ${DIA}")
class Dia${DIA_FMT}Prueba {

    private Dia${DIA_FMT} dia${DIA_FMT};

    @BeforeEach
    void configurar() {
        dia${DIA_FMT} = new Dia${DIA_FMT}();
    }

    @Test
    @DisplayName("Parte 1: ejemplo del enunciado")
    void parte1_ejemplo() {
        List<String> ejemplo = List.of(
            // TODO: agregar líneas del ejemplo
        );
        assertEquals("TODO", dia${DIA_FMT}.parte1(ejemplo));
    }

    @Test
    @DisplayName("Parte 2: ejemplo del enunciado")
    void parte2_ejemplo() {
        List<String> ejemplo = List.of(
            // TODO: agregar líneas del ejemplo
        );
        assertEquals("TODO", dia${DIA_FMT}.parte2(ejemplo));
    }
}
EOF

# Crear archivo de input vacío
touch "$INPUT"

echo "Archivos creados:"
echo "  $CLASE"
echo "  $PRUEBA"
echo "  $INPUT"