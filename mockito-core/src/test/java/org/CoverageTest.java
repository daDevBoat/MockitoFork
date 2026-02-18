/*
 * Copyright (c) 2026 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.mockitousage.matchers.MatchersTest.class,
    org.mockito.internal.matchers.apachecommons.EqualsBuilderTest.class,
    org.concurrentmockito.ThreadVerifiesContinuouslyInteractingMockTest.class,
    org.mockitousage.annotation.AnnotationsTest.class,
    org.mockitousage.basicapi.MocksSerializationForAnnotationTest.class,
    org.mockitousage.strictness.LenientMockAnnotationTest.class,
    org.mockitousage.strictness.StrictnessMockAnnotationTest.class
})
public class CoverageTest {

    @BeforeClass
    public static void beforeAll() {
        CoverageRecorder.register(
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114);
        CoverageRecorder.register(
                201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217,
                218, 219);
        CoverageRecorder.register(
                301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317,
                318, 319);
        CoverageRecorder.register(501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511);
    }

    @AfterClass
    public static void afterAll() throws Exception {
        CoverageRecorder.dump(Path.of("build/reports/manual-coverage.txt"));
    }
}
