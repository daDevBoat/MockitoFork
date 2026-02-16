/*
 * Copyright (c) 2026 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class CoverageRecorder {

    private static final Set<Integer> ALL = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> HIT = ConcurrentHashMap.newKeySet();

    private CoverageRecorder() {}

    public static void register(int... ids) {
        for (int id : ids) {
            ALL.add(id);
        }
    }

    public static void mark(int id) {
        HIT.add(id);
    }

    public static void dump(Path output) throws IOException {
        TreeSet<Integer> allSorted = new TreeSet<>(ALL);
        TreeSet<Integer> hitSorted = new TreeSet<>(HIT);

        TreeSet<Integer> missing = new TreeSet<>(allSorted);
        missing.removeAll(hitSorted);

        double percent = allSorted.isEmpty() ? 0.0 : (100.0 * hitSorted.size() / allSorted.size());

        String report =
                "Manual branch coverage\n"
                        + "Covered: "
                        + hitSorted.size()
                        + "/"
                        + allSorted.size()
                        + String.format(" (%.2f%%)%n", percent)
                        + "ALL IDs: "
                        + allSorted
                        + "\n"
                        + "HIT IDs: "
                        + hitSorted
                        + "\n"
                        + "MISS IDs: "
                        + missing
                        + "\n";

        Files.createDirectories(output.getParent());
        Files.write(output, report.getBytes(StandardCharsets.UTF_8));
        System.out.println(report); // also print to console
    }
}
