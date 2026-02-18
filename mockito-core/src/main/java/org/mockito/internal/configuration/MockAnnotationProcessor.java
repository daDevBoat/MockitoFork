/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.CoverageRecorder;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import static org.mockito.internal.util.StringUtil.join;
import org.mockito.internal.util.Supplier;
import org.mockito.quality.Strictness;

/**
 * Instantiates a mock on a field annotated by {@link Mock}
 */
public class MockAnnotationProcessor implements FieldAnnotationProcessor<Mock> {

    @Override
    public Object process(Mock annotation, Field field) {
        return processAnnotationForMock(
                annotation, field.getType(), field::getGenericType, field.getName());
    }

    @SuppressWarnings("deprecation")
    public static Object processAnnotationForMock(
            Mock annotation, Class<?> type, Supplier<Type> genericType, String name) {

        MockSettings mockSettings = Mockito.withSettings();
        if (annotation.extraInterfaces().length > 0) { // never null
            CoverageRecorder.mark(201);
            mockSettings.extraInterfaces(annotation.extraInterfaces());
        } else {
            CoverageRecorder.mark(202);
        }
        if ("".equals(annotation.name())) {
            CoverageRecorder.mark(203);
            mockSettings.name(name);
        } else {
            CoverageRecorder.mark(204);
            mockSettings.name(annotation.name());
        }
        if (annotation.serializable()) {
            CoverageRecorder.mark(205);
            mockSettings.serializable();
        } else {
            CoverageRecorder.mark(206);
        }
        if (annotation.stubOnly()) {
            CoverageRecorder.mark(207);
            mockSettings.stubOnly();
        } else {
            CoverageRecorder.mark(208);
        }
        if (annotation.lenient()) {
            CoverageRecorder.mark(209);
            mockSettings.lenient();
        } else {
            CoverageRecorder.mark(210);
        }
        if (annotation.strictness() != Mock.Strictness.TEST_LEVEL_DEFAULT) {
            CoverageRecorder.mark(211);
            mockSettings.strictness(Strictness.valueOf(annotation.strictness().toString()));
        } else {
            CoverageRecorder.mark(212);
        }
        if (!annotation.mockMaker().isEmpty()) {
            CoverageRecorder.mark(213);
            mockSettings.mockMaker(annotation.mockMaker());
        } else {
            CoverageRecorder.mark(214);
        }
        if (annotation.withoutAnnotations()) {
            CoverageRecorder.mark(215);
            mockSettings.withoutAnnotations();
        } else {
            CoverageRecorder.mark(216);
        }

        mockSettings.genericTypeToMock(genericType.get());

        // see @Mock answer default value
        mockSettings.defaultAnswer(annotation.answer());

        if (type == MockedStatic.class) {
            CoverageRecorder.mark(217);
            return Mockito.mockStatic(
                    inferParameterizedType(
                            genericType.get(), name, MockedStatic.class.getSimpleName()),
                    mockSettings);
        } else if (type == MockedConstruction.class) {
            CoverageRecorder.mark(218);
            return Mockito.mockConstruction(
                    inferParameterizedType(
                            genericType.get(), name, MockedConstruction.class.getSimpleName()),
                    mockSettings);
        } else {
            CoverageRecorder.mark(219);
            return Mockito.mock(type, mockSettings);
        }
    }

    static Class<?> inferParameterizedType(Type type, String name, String sort) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] arguments = parameterizedType.getActualTypeArguments();
            if (arguments.length != 1) {
                throw new IllegalArgumentException(
                        "Incorrect number of type arguments for "
                                + name
                                + " of type "
                                + sort
                                + ": expected 1 but received "
                                + arguments.length);
            }

            return (Class<?>)
                    (arguments[0] instanceof Class<?>
                            ? arguments[0]
                            : ((ParameterizedType) arguments[0]).getRawType());
        }
        throw new MockitoException(
                join(
                        "Mockito cannot infer a static mock from a raw type for " + name,
                        "",
                        "Instead of @Mock " + sort + " you need to specify a parameterized type",
                        "For example, if you would like to mock Sample.class, specify",
                        "",
                        "@Mock " + sort + "<Sample>",
                        "",
                        "as the type parameter."));
    }
}
