/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockMakers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.exceptions.base.MockitoException;

@RunWith(Enclosed.class)
public class MockAnnotationProcessorTest {

    @SuppressWarnings("unused")
    private MockedStatic<Void> nonGeneric;

    @SuppressWarnings("unused")
    private MockedStatic<List<?>> generic;

    @SuppressWarnings({"rawtypes", "unused"})
    private MockedStatic raw;

    @SuppressWarnings("unused")
    private MockedConstruction<Void> nonGenericConstruction;

    @SuppressWarnings("unused")
    private MockedConstruction<List<?>> genericConstruction;

    @SuppressWarnings({"rawtypes", "unused"})
    private MockedConstruction rawConstruction;

    @RunWith(Parameterized.class)
    public static class NonGenericTest {

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {{"nonGeneric"}, {"nonGenericConstruction"}});
        }

        @Parameter public String fieldName;

        @Test
        public void ensure_non_generic_fields_can_be_inferred() throws Exception {
            Class<?> type =
                    MockAnnotationProcessor.inferParameterizedType(
                            MockAnnotationProcessorTest.class
                                    .getDeclaredField(fieldName)
                                    .getGenericType(),
                            fieldName,
                            "Sample");
            assertThat(type).isEqualTo(Void.class);
        }
    }

    @RunWith(Parameterized.class)
    public static class GenericTest {

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {{"generic"}, {"genericConstruction"}});
        }

        @Parameter public String fieldName;

        @Test
        public void ensure_generic_fields_can_be_inferred() throws Exception {
            Class<?> type =
                    MockAnnotationProcessor.inferParameterizedType(
                            MockAnnotationProcessorTest.class
                                    .getDeclaredField(fieldName)
                                    .getGenericType(),
                            fieldName,
                            "Sample");
            assertThat(type).isEqualTo(List.class);
        }
    }

    @RunWith(Parameterized.class)
    public static class RawTest {

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {{"raw"}, {"rawConstruction"}});
        }

        @Parameter public String fieldName;

        @Test
        public void ensure_raw_fields_cannot_be_inferred() {
            assertThatThrownBy(
                            () ->
                                    MockAnnotationProcessor.inferParameterizedType(
                                            MockAnnotationProcessorTest.class
                                                    .getDeclaredField(fieldName)
                                                    .getGenericType(),
                                            fieldName,
                                            "Sample"))
                    .isInstanceOf(MockitoException.class)
                    .hasMessageContaining(
                            "Mockito cannot infer a static mock from a raw type for " + fieldName);
        }
    }

    @RunWith(Parameterized.class)
    public static class WrongNumberOfArgsTest {

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {{"raw"}, {"rawConstruction"}});
        }

        @Parameter public String fieldName;

        @Test
        public void ensure_parameterized_types_with_more_than_one_arg_cannot_be_inferred() {
            final ParameterizedType parameterizedType = mock();
            when(parameterizedType.getActualTypeArguments())
                    .thenReturn(new Type[] {String.class, String.class});

            assertThatThrownBy(
                            () ->
                                    MockAnnotationProcessor.inferParameterizedType(
                                            parameterizedType, fieldName, "Sample"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(
                            "Incorrect number of type arguments for "
                                    + fieldName
                                    + " of type Sample: expected 1 but received 2");
        }
    }

    @RunWith(Parameterized.class)
    public static class ProcessAnnotationForMockTests {

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {{"raw"}, {"rawConstruction"}});
        }

        @Parameter public String fieldName;

        @Mock(mockMaker = MockMakers.SUBCLASS)
        private List<String> testMock;

        @Test
        public void mock_maker_non_empty() throws Exception {
            /**
             * Contract: When the mockMaker setting is non-empty, it should be set to the
             * MockMaker that is passed. This test mocks a List of strings, then checks that
             * the mock is successful by asserting its type and that Mockito returns the
             * default mock for the size of the List (0 is default mock for ints).
             * Java reflection is used to extract the parameters that processAnnotationForMock
             * needs to create a Mock (annotations, type, generics, name).
             */
            Field reflectTestMock =
                    ProcessAnnotationForMockTests.class.getDeclaredField("testMock");
            Mock annotation = reflectTestMock.getAnnotation(Mock.class);

            Object returnedObject =
                    MockAnnotationProcessor.processAnnotationForMock(
                            annotation,
                            reflectTestMock.getType(),
                            reflectTestMock::getGenericType,
                            reflectTestMock.getName());
            assertThat(
                            Mockito.mockingDetails(returnedObject)
                                    .getMockCreationSettings()
                                    .getMockMaker())
                    .isEqualTo(MockMakers.SUBCLASS);
            assertThat(returnedObject).isInstanceOf(List.class);
            assertEquals(((List<?>) returnedObject).size(), 0);
        }

        @Mock private List<String> testMock2;

        @Test
        public void mock_maker_empty() throws Exception {
            /**
             * Contract: When the mockMaker setting is empty, it should not set any mockMaker but
             * still create a mock. This test uses reflection to extract the parameters
             * that processAnnotationForMock needs to create a Mock
             * (annotations, type, generics, name).
             */
            Field reflectTestMock =
                    ProcessAnnotationForMockTests.class.getDeclaredField("testMock2");
            Mock annotation = reflectTestMock.getAnnotation(Mock.class);

            Object returnedObject =
                    MockAnnotationProcessor.processAnnotationForMock(
                            annotation,
                            reflectTestMock.getType(),
                            reflectTestMock::getGenericType,
                            reflectTestMock.getName());
            assertThat(returnedObject).isNotNull();
            assertThat(returnedObject).isInstanceOf(List.class);
        }

        static class DummyStaticForMocking {

            static String functionality() {
                return "functionality";
            }
        }

        @Mock private MockedStatic<DummyStaticForMocking> testStaticMock;

        @Test
        public void mock_static_class() throws Exception {
            /**
             * Contract: Mocking a static class with @Mock should return a static mock when
             * calling processAnnotationForMock. Furthermore, basic mock functionality is
             * tested by intercepting the functionality function of DummyStaticForMocking
             * and asserting that it was intercepted.
             * Java reflection is used to extract the parameters that processAnnotationForMock
             * needs to create a MockedStatic (annotations, generics, name).
             */
            Field reflectTestMock =
                    ProcessAnnotationForMockTests.class.getDeclaredField("testStaticMock");
            Mock annotation = reflectTestMock.getAnnotation(Mock.class);

            Object returnedObject =
                    MockAnnotationProcessor.processAnnotationForMock(
                            annotation,
                            MockedStatic.class,
                            reflectTestMock::getGenericType,
                            reflectTestMock.getName());

            try (MockedStatic<DummyStaticForMocking> ms =
                    (MockedStatic<DummyStaticForMocking>) returnedObject) {
                ms.when(DummyStaticForMocking::functionality).thenReturn("intercepted");
                assertThat(returnedObject).isInstanceOf(MockedStatic.class);
                assertThat(DummyStaticForMocking.functionality()).isEqualTo("intercepted");
            }
        }

        static class DummyStaticForConstruction {

            public DummyStaticForConstruction() {}
        }

        @Mock private MockedConstruction<DummyStaticForConstruction> testConstructionMock;

        @Test
        public void mock_construction_class() throws Exception {
            /**
             * Contract: Mocking a construction of a class should return a
             * MockedConstruction type. Furthermore, basic mock functionality is
             * tested by asserting that the constructor was intercepted.
             * This test uses reflection to extract the parameters that processAnnotationForMock
             * needs to create a MockedConstruction (annotations, generics, name).
             */
            Field reflectTestMock =
                    ProcessAnnotationForMockTests.class.getDeclaredField("testConstructionMock");
            Mock annotation = reflectTestMock.getAnnotation(Mock.class);
            Object returnedObject =
                    MockAnnotationProcessor.processAnnotationForMock(
                            annotation,
                            MockedConstruction.class,
                            reflectTestMock::getGenericType,
                            reflectTestMock.getName());

            try (MockedConstruction<DummyStaticForConstruction> mc =
                    (MockedConstruction<DummyStaticForConstruction>) returnedObject) {
                assertThat(returnedObject).isInstanceOf(MockedConstruction.class);
                new DummyStaticForConstruction();
                assertThat(mc.constructed()).hasSize(1);
            }
        }
    }
}
