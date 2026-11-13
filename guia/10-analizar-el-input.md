# Analizar el input

[← Anterior: Grafos avanzados](09-grafos-avanzados.md) · [Índice](README.md) · [Siguiente: Java para estos problemas →](11-java.md)

Algunas partes 2 no tienen solución general eficiente: **el input real tiene una estructura especial** que el enunciado no menciona y que hay que descubrir mirándolo. Lo profesional es usar esa estructura sin suponerla: comprobarla en el código y fallar si no se cumple.

Días donde fue decisivo: 2023: 20, 21 · 2024: 17, 24 (máquinas e ingeniería inversa).

## Cómo descubrir la estructura

- Imprimir estadísticas antes de programar: tamaño, rangos de valores, cuántos nodos tienen grado 3, si hay filas o columnas vacías.
- Dibujar el grafo o mirar los nodos alrededor del objetivo. En 2023 día 20, `rx` tiene una sola entrada, una conjunción con cuatro subredes independientes; cada una es un contador binario de 12 bits.
- Buscar regularidades numéricas: en 2023 día 21 la fila y la columna de `S` están libres y 26501365 = 65 + 131·202300.
- Comparar el ejemplo con el input: el ejemplo a veces **no** tiene la propiedad (en 2023 día 21 su fila central tiene rocas), y el atajo solo vale para el input real.

## Máquinas virtuales y circuitos

- Primero, un intérprete fiel de las instrucciones (2024 día 17: ocho instrucciones con operandos literales y "combo"). Con él se prueban hipótesis.
- Después, leer lo que el programa calcula. En 2024 día 17, cada vuelta usa los 3 bits bajos de `A` y lo divide por 8, así que `A` se arma de a 3 bits desde los más altos, con búsqueda en profundidad.
- Circuitos: en 2024 día 24 las compuertas forman un sumador de 45 bits con cuatro pares de compuertas con las salidas intercambiadas. Se buscan intercambios que hagan correcto el bit más bajo que falla, probando sumas con todos los bits a la vez.

## Comprobar, no suponer

- Validar la estructura en el código: "`rx` tiene una sola entrada y es una conjunción", "el segundo evento llega en `2k`", "las segundas diferencias son iguales".
- Probar el atajo contra una fuerza bruta en un input chico que sí tenga la estructura, y comprobar que falle con uno que no la tenga.
