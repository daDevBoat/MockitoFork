/*
 * Copyright (c) 2026 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class CoverageTest {

    @BeforeAll
    static void beforeAll() {
        // CoverageRecorder.register(101, 102, 103, 104, 105, 106);
    }

    @AfterAll
    static void afterAll() throws Exception {
        CoverageRecorder.dump(Path.of("build/reports/manual-coverage.txt"));
    }
}
