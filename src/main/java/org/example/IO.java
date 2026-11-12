package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class IO {

    public static void println(String message) {
        System.out.println(message);
    }

    /**
     * Lee el archivo de input desde src/main/resources/inputs/y{year}/day{day}.txt
     * Ejemplo: inputs/y2024/day01.txt
     */
    public static List<String> readInput(int year, int day) throws IOException {
        String path = String.format("/inputs/y%d/day%02d.txt", year, day);
        InputStream is = IO.class.getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Input no encontrado: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
