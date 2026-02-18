/*
 * Copyright (c) 2026 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

        HashMap<Character, List<Integer>> prefixCounter = new HashMap<>();
        HashMap<Character, Integer> totalCounter = new HashMap<>();
        for (int p : allSorted) {
            char firstDigit = String.valueOf(p).charAt(0);
            totalCounter.put(firstDigit, totalCounter.getOrDefault(firstDigit, 0) + 1);
            if (hitSorted.contains(p)) {
                prefixCounter.computeIfAbsent(firstDigit, k -> new ArrayList<>()).add(p);
            }
        }

        double totalPercent =
                allSorted.isEmpty() ? 0.0 : (100.0 * hitSorted.size() / allSorted.size());

        String totalReport =
                "Total branch coverage\n"
                        + "Covered: "
                        + hitSorted.size()
                        + "/"
                        + allSorted.size()
                        + String.format(" (%.2f%%)%n", totalPercent)
                        + "ALL IDs: "
                        + allSorted
                        + "\n"
                        + "HIT IDs: "
                        + hitSorted
                        + "\n"
                        + "MISS IDs: "
                        + missing
                        + "\n";
        System.out.println(totalReport);

        List<String> listOfReports = new ArrayList<>();
        listOfReports.add(totalReport);

        for (char prefix : new TreeSet<>(totalCounter.keySet())) {
            System.out.println("------------------------------------");
            System.out.println("HITS FOR PREFIX: " + prefix);

            List<Integer> hitsIDS = prefixCounter.getOrDefault(prefix, Collections.emptyList());
            int covered = hitsIDS.size();
            int total = totalCounter.get(prefix);

            String hits = hitsIDS.stream().map(String::valueOf).collect(Collectors.joining(","));
            double percent = (covered * 100.0 / total);
            String report =
                    "HITS FOR PREFIX: "
                            + prefix
                            + "\n"
                            + "Manual branch coverage\n"
                            + "Covered: "
                            + covered
                            + "/"
                            + total
                            + String.format(" (%.2f%%)%n", percent)
                            + "HIT IDs: "
                            + hits
                            + "\n";
            System.out.println(report);
            listOfReports.add(report);
        }

        Files.createDirectories(output.getParent());
        String allReports = String.join("\n-------------------------------\n", listOfReports);
        Files.write(output, allReports.getBytes(StandardCharsets.UTF_8));
    }
}
