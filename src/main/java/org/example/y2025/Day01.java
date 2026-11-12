package org.example.y2025;

import org.example.Solver;

import java.util.List;

public class Day01 implements Solver {

    @Override
    public String part1(List<String> lines) {
        int position = 50;
        int count = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.matches("[LR]\\d+")) continue;

            char dir = line.charAt(0);
            int dist = Integer.parseInt(line.substring(1));

            int delta = (dir == 'R') ? dist : -dist;
            position = ((position + delta) % 100 + 100) % 100;

            if (position == 0) count++;
        }

        return String.valueOf(count);
    }

    @Override
    public String part2(List<String> lines) {
        int position = 50;
        long count = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.matches("[LR]\\d+")) continue;

            char dir = line.charAt(0);
            int dist = Integer.parseInt(line.substring(1));

            if (dir == 'L') {
                // Clicks que tocan 0: k ≡ position (mod 100), k en 1..dist
                if (position == 0) {
                    count += dist / 100;
                } else if (dist >= position) {
                    count += (dist - position) / 100 + 1;
                }
            } else {
                // Clicks que tocan 0: k ≡ (100 - position) (mod 100), k en 1..dist
                int toZero = (position == 0) ? 100 : (100 - position);
                if (dist >= toZero) {
                    count += (dist - toZero) / 100 + 1;
                }
            }

            int delta = (dir == 'R') ? dist : -dist;
            position = ((position + delta) % 100 + 100) % 100;
        }

        return String.valueOf(count);
    }
}