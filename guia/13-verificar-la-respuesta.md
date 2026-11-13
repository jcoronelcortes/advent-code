# Verificar la respuesta

[← Anterior: Librerías y herramientas](12-librerias-y-herramientas.md) · [Índice](README.md) · [Siguiente: Índice por día →](14-indice-por-dia.md)

AoC solo dice "correcto" o "incorrecto", así que **una respuesta se da por buena cuando un método independiente llega al mismo número**. Al resolver 2023 esa disciplina detectó errores reales, casi todos en las propias verificaciones.

## La rutina de cada día

1. Pruebas con el ejemplo del enunciado, incluidos los resultados intermedios que muestra (estados paso a paso, casillas marcadas, listas de valores).
2. Una fuerza bruta en Java para entradas chicas: simular casilla por casilla, probar todas las combinaciones, contar uno por uno.
3. Un script independiente con el input real, usando **otra idea**, no la misma traducida: por ejemplo, física literal con cubos contra un mapa de alturas (2023 día 22), o buscar la velocidad contra resolver un sistema (2023 día 24).
4. Una prueba de regresión con las respuestas aceptadas, para que una refactorización no rompa nada sin aviso.

## Pruebas basadas en propiedades

- En lugar de un caso, una propiedad que vale para todos: "Pick coincide con contar los puntos de cualquier triángulo", "con b = A·x, el sistema devuelve x".
- Encuentran casos que uno no imagina, como coordenadas negativas o rangos vacíos.

## Errores típicos que aparecieron

| Error | Dónde | Cómo se detectó |
| --- | --- | --- |
| Comparar por posición objetos que se reordenan | Fuerza bruta de 2023 día 22 | Repasar la prueba: `asentar` devuelve otro orden |
| Contar por posición: un ladrillo cae al lugar de otro idéntico | Verificación de 2023 día 22, parte 2 | Discrepancia con la solución; seguir cada ladrillo por su identidad |
| Suponer que el estado inicial vuelve a aparecer | Verificación de 2023 día 20 | El script no terminaba: el ciclo empieza en la presión 1 |
| Esperar un valor calculado de memoria | Pruebas de 2023 días 17 y 22 | La fuerza bruta ya coincidía con el código |
| Diferencia de coordenadas que desborda `int` | Primera versión de `Poligono` | Una prueba con coordenadas extremas |
| Reemplazo de texto que borró más de lo debido | Refactorización de 2023 día 24 | El compilador, antes de cualquier prueba |

## Buenas costumbres

- Ante una discrepancia, sospechar de los dos lados: la verificación también puede estar mal.
- Afirmar solo lo comprobado: un dato de la documentación también se verifica (como el contador de período 6 en 2023 día 20, que disparaba en 6, 13, 20… y no en 6 y 14).

**En el repo**: una clase `DiaNNPrueba` por día con los ejemplos y fuerzas brutas, `RespuestasPrueba` (regresión con las respuestas aceptadas) y `PruebaDocumentacion` (los fragmentos de código de `soluciones/` siguen coincidiendo con el código).
