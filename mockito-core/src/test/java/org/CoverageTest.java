/*
 * Copyright (c) 2026 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.mockitousage.matchers.MatchersTest.class
})

public class CoverageTest {

    @BeforeClass
    public static void beforeAll() {
        // CoverageRecorder.register(101, 102, 103, 104, 105, 106);
        CoverageRecorder.register(501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511);
    }

    @AfterClass
    public static void afterAll() throws Exception {
        CoverageRecorder.dump(Path.of("build/reports/manual-coverage.txt"));
    }
}
