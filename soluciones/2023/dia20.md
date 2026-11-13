# Año 2023 — Día 20: Propagación de Pulsos

> El enunciado de cada parte está redactado en español con palabras propias a partir del original en [adventofcode.com/2023/day/20](https://adventofcode.com/2023/day/20). No es una traducción literal: el creador de Advent of Code pide no republicar el texto de sus problemas.

## Parte 1

### Enunciado

Con las piezas correctas, los elfos arreglaron todas las máquinas; solo falta mandarles la orden de arranque para que vuelva a fluir la arena. Las máquinas están conectadas por cables a **módulos de comunicación** que se mandan **pulsos**, que pueden ser **altos** o **bajos**. Cuando un módulo manda un pulso, lo manda a cada uno de sus destinos.

Hay tres tipos de módulos:

- **Biestable** (prefijo `%`): está prendido o apagado, y empieza apagado. Ignora los pulsos altos. Con un pulso bajo cambia de estado: si estaba apagado se prende y manda un pulso **alto**; si estaba prendido se apaga y manda uno **bajo**.
- **Conjunción** (prefijo `&`): recuerda el último pulso que recibió de **cada una** de sus entradas (al principio, bajo para todas). Al recibir un pulso actualiza el recuerdo de esa entrada y, si recuerda altos de todas, manda un pulso **bajo**; si no, uno **alto**.
- **Difusor** (`broadcaster`, hay uno solo): reenvía el pulso que recibe a todos sus destinos.

Hay además un **botón** que, al presionarlo, manda un pulso bajo al difusor. Antes de volver a presionarlo hay que esperar a que se procesen todos los pulsos. Los pulsos se procesan **en el orden en que se mandaron**: si un módulo manda pulsos a `a`, `b` y `c`, los de `b` y `c` se procesan antes que los que genere `a`.

El input lista cada módulo con su prefijo de tipo (si tiene), su nombre, una flecha y sus destinos. Un destino puede no estar definido (como `output`): recibe pulsos y no hace nada.

#### Ejemplo

```
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
```

Al presionar el botón se mandan 12 pulsos: el del botón, tres bajos del difusor a `a`, `b` y `c`, y después una cadena en la que los biestables se prenden y vuelven a apagarse gracias a `inv`, que con una sola entrada funciona como inversor. Todo queda como al principio, así que cada presión manda 8 pulsos bajos y 4 altos. Tras mil presiones son 8000 bajos y 4000 altos, y su producto es **32000000**.

En un segundo ejemplo, con una conjunción de dos entradas, el estado vuelve al inicial cada cuatro presiones, y tras mil presiones el producto es **11687500**.

#### Pregunta

Tras presionar el botón mil veces, ¿cuál es el producto de la cantidad de pulsos bajos por la de pulsos altos enviados (contando los del botón)?

### Solución

#### El input real

58 módulos.

#### Leer la red

Cada línea se lee con una expresión regular a un `Modulo` (tipo y destinos). Los destinos que no están definidos se agregan como módulos `SIN_TIPO`. Al leer se comprueba que esté el difusor, que ningún módulo se repita y que el único sin prefijo sea el difusor.

La `Red` guarda el estado: el conjunto de biestables prendidos y, para cada conjunción, un mapa con el último pulso de cada entrada. Ese mapa se arma al principio recorriendo todas las conexiones, porque una conjunción tiene que conocer **todas** sus entradas antes de recibir el primer pulso: si solo recordara las que ya le mandaron algo, podría creer que "todas" son altas cuando todavía falta alguna.

#### Una cola, no recursión

Presionar el botón procesa los pulsos con una **cola**, que da justo el orden que pide el enunciado. Cada pulso procesado se agrega a la lista de enviados, y cada tipo de módulo decide si responde y con qué:

```java
// Dia20.java — presionarBoton()
Boolean salida = switch (modulo.tipo()) {
    case DIFUSOR -> pulso.alto();
    case BIESTABLE -> {
        if (pulso.alto()) yield null;
        // Si estaba apagado se prende y manda alto; si estaba prendido se apaga y manda bajo
        boolean estabaApagado = prendidos.add(pulso.destino());
        if (!estabaApagado) prendidos.remove(pulso.destino());
        yield estabaApagado;
    }
    case CONJUNCION -> {
        Map<String, Boolean> recuerda = memoria.get(pulso.destino());
        recuerda.put(pulso.origen(), pulso.alto());
        yield !recuerda.values().stream().allMatch(alto -> alto);
    }
    case SIN_TIPO -> null;
};
```

Con recursión (procesar en profundidad), el orden cambiaría y el resultado también: en la tercera presión del segundo ejemplo, la conjunción `con` vería a `b` apagarse antes de enterarse de que `a` se prendió, y no mandaría el pulso bajo a `output` que describe el enunciado (lo comprobé simulando con una pila en vez de una cola). La parte 1 cuenta los pulsos bajos y altos de mil presiones y los multiplica.

#### Verificación

- Pruebas con los dos ejemplos (32000000 y 11687500).
- En el primer ejemplo, cada presión manda exactamente los 12 pulsos del enunciado, en el mismo orden, y todos los biestables quedan apagados.
- En el segundo ejemplo, las cuatro primeras presiones mandan exactamente las cuatro secuencias de pulsos del enunciado, los biestables prendidos después de cada una son los que describe, y tras la cuarta todo vuelve al estado inicial.
- Un input mal formado (sin difusor, con un módulo repetido, sin prefijo y que no es el difusor, sin destinos o con un tipo desconocido) falla con un mensaje claro.
- Con el input real, una implementación independiente en Python, con diccionarios simples y `deque`, obtiene los mismos 17 521 pulsos bajos y 42 456 altos.

**Resultado con el input real: 743871576**

---

## Parte 2

### Enunciado

La última máquina, la que manda la arena hacia la Isla Isla, tiene conectado un módulo llamado `rx`, y se enciende cuando `rx` recibe **un pulso bajo**.

Partiendo de nuevo con todos los módulos en su estado inicial, y esperando que se procesen todos los pulsos tras cada presión, hay que averiguar la **menor cantidad de presiones** del botón para que `rx` reciba un pulso bajo.

#### Pregunta

¿Cuántas presiones del botón hacen falta, como mínimo, para que `rx` reciba un pulso bajo?

### Solución

#### Simular no alcanza

La respuesta está en los cientos de billones de presiones, así que no se puede simular presión por presión. Pero la red del input tiene una forma muy particular:

```
broadcaster ─┬─▶ subred 1 (12 biestables + &nh) ─▶ &ph ─┐
             ├─▶ subred 2 (12 biestables + &mf) ─▶ &vn ─┤
             ├─▶ subred 3 (12 biestables + &fd) ─▶ &kt ─┼─▶ &kc ─▶ rx
             └─▶ subred 4 (12 biestables + &kb) ─▶ &hn ─┘
```

- `rx` tiene **una sola entrada**, la conjunción `kc` (el *concentrador*). Esta manda un pulso bajo solo si recuerda pulsos altos de **todas** sus entradas, así que hace falta que las cuatro le manden un pulso alto en la misma presión.
- Cada destino del difusor arranca una **subred independiente** de 14 módulos: 12 biestables encadenados que forman un **contador binario**, una conjunción que detecta cuándo el contador llega a cierto valor `k` (y lo reinicia a 0) y un inversor que en ese momento manda un pulso alto a `kc`.

Si cada entrada de `kc` manda su pulso alto exactamente en las presiones `k`, `2k`, `3k`…, la primera presión en la que coinciden todas es el **mínimo común múltiplo** de los `k`.

#### Comprobar la forma, no suponerla

La solución no da por sentada esa forma. Primero exige que el objetivo tenga una única entrada y que sea una conjunción. Después simula presiones, anotando en qué presiones cada entrada del concentrador le manda un pulso alto, hasta tener **dos** de cada una, y comprueba que la segunda llegue en la presión `2k`:

```java
// Dia20.java — mcmDePeriodos()
List<Long> periodos = new ArrayList<>();
for (Map.Entry<String, List<Long>> entrada : altos.entrySet()) {
    long primera = entrada.getValue().get(0), segunda = entrada.getValue().get(1);
    if (segunda != 2 * primera) {
        throw new IllegalStateException("'" + entrada.getKey() + "' no es periódica desde el inicio: manda "
                + "pulsos altos en las presiones " + primera + " y " + segunda);
    }
    periodos.add(primera);
}
return Matematicas.mcm(periodos);
```

Si alguna entrada no cumple, falla en vez de devolver un mcm equivocado. Y si `rx` recibe un pulso bajo mientras se buscan los períodos (en una red chica), se devuelve esa presión directamente. Con el input real hacen falta unas 8 200 presiones, que tardan unos 50 ms.

Los períodos son 3797, 3907, 4021 y 4093, cuatro primos, así que el mcm es su producto.

#### Verificación

- Con una red de juguete con la misma forma (contadores binarios de período 5 y 7 que alimentan `kc` → `rx`), `rx` recibe el pulso bajo en la presión 35, igual que al **simular presión por presión** hasta que ocurra.
- Con tres contadores de 4 bits (períodos 11, 13 y 9) coincide con simular, y da 1287.
- Con un contador par (período 6), la entrada no es periódica desde el inicio: manda pulsos altos en las presiones 6, 13, 20… (cada 7 después de la primera), y la respuesta real (simulando) es 90, no mcm(3, 5, 6) = 30. La solución falla con un mensaje claro en vez de devolver 30.
- Si `rx` no existe, o su entrada no es una única conjunción, falla con un mensaje claro.
- Con el input real, un script independiente en Python **demuestra** las hipótesis en vez de suponerlas:
  1. Simula cada subred **sola** y detecta cuándo su estado (biestables, memorias de sus conjunciones y lo que `kc` recuerda de su inversor) se repite: las cuatro entran en un ciclo desde la presión 1, de largo 3797, 4021, 4093 y 3907.
  2. Comprueba que, durante el primer ciclo y uno más, cada subred manda pulsos altos a `kc` **exactamente** en los múltiplos de su largo. Por ser deterministas, eso vale para siempre, así que la primera presión con los cuatro pulsos altos a la vez es el mcm.
  3. Arma el estado de toda la red justo antes de la presión N = mcm, poniendo cada subred en el estado que le corresponde por su ciclo, simula **una sola presión** y comprueba que `rx` recibe un pulso bajo.

**Resultado con el input real: 244151741342687**

---

## Resumen

| | Parte 1 | Parte 2 |
|---|---|---|
| **Pregunta** | Bajos × altos tras mil presiones | Presiones hasta que `rx` reciba un pulso bajo |
| **Enfoque** | Simular con una cola, en el orden de envío | Período de cada entrada del concentrador y mcm, comprobando la periodicidad |
| **Complejidad** | O(presiones × pulsos por presión) | O(2 · máx(período) × pulsos por presión) |
| **Resultado** | 743871576 | 244151741342687 |

## Archivo de código

`src/main/java/advent/code/y2023/Dia20.java`
