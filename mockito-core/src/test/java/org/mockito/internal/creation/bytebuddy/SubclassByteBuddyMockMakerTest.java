/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.Test;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.plugins.MockMaker;
import org.mockito.invocation.MockHandler;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;

import static org.assertj.core.api.Assertions.assertThat;

public class SubclassByteBuddyMockMakerTest
        extends AbstractByteBuddyMockMakerTest<SubclassByteBuddyMockMaker> {

    public SubclassByteBuddyMockMakerTest() {
        super(new SubclassByteBuddyMockMaker());
    }

    @Test
    public void is_type_mockable_excludes_primitive_wrapper_classes() {
        MockMaker.TypeMockability mockable = mockMaker.isTypeMockable(Integer.class);
        assertThat(mockable.mockable()).isFalse();
        assertThat(mockable.nonMockableReason()).contains("final");
    }

    @Test
    public void is_type_mockable_excludes_sealed_classes() {
        if (ClassFileVersion.ofThisVm().isAtMost(ClassFileVersion.JAVA_V16)) {
            return;
        }
        DynamicType.Builder<?> base = new ByteBuddy().subclass(Object.class);
        DynamicType.Builder<?> subclass =
                new ByteBuddy().subclass(base.toTypeDescription()).merge(TypeManifestation.FINAL);
        Class<?> type =
                base.permittedSubclass(subclass.toTypeDescription())
                        .make()
                        .include(subclass.make())
                        .load(null)
                        .getLoaded();
        MockMaker.TypeMockability mockable = mockMaker.isTypeMockable(type);
        assertThat(mockable.mockable()).isFalse();
        assertThat(mockable.nonMockableReason()).contains("sealed");
    }

    @Test
    public void is_type_mockable_excludes_primitive_classes() {
        MockMaker.TypeMockability mockable = mockMaker.isTypeMockable(int.class);
        assertThat(mockable.mockable()).isFalse();
        assertThat(mockable.nonMockableReason()).contains("primitive");
    }

    @Test
    public void is_type_mockable_allow_anonymous() {
        Observer anonymous =
                new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {}
                };
        MockMaker.TypeMockability mockable = mockMaker.isTypeMockable(anonymous.getClass());
        assertThat(mockable.mockable()).isTrue();
        assertThat(mockable.nonMockableReason()).contains("");
    }

    @Test
    public void is_type_mockable_give_empty_reason_if_type_is_mockable() {
        MockMaker.TypeMockability mockable = mockMaker.isTypeMockable(SomeClass.class);
        assertThat(mockable.mockable()).isTrue();
        assertThat(mockable.nonMockableReason()).isEqualTo("");
    }

    @Test
    public void mock_class_with_annotations() throws Exception {
        MockSettingsImpl<ClassWithAnnotation> mockSettings = new MockSettingsImpl<>();
        mockSettings.setTypeToMock(ClassWithAnnotation.class);

        ClassWithAnnotation proxy = mockMaker.createMock(mockSettings, dummyHandler());

        assertThat(proxy.getClass().isAnnotationPresent(SampleAnnotation.class)).isTrue();
        assertThat(proxy.getClass().getAnnotation(SampleAnnotation.class).value()).isEqualTo("foo");

        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .isAnnotationPresent(SampleAnnotation.class))
                .isTrue();
        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .getAnnotation(SampleAnnotation.class)
                                .value())
                .isEqualTo("bar");
    }

    @Test
    public void mock_class_with_annotations_with_additional_interface() throws Exception {
        MockSettingsImpl<ClassWithAnnotation> mockSettings = new MockSettingsImpl<>();
        mockSettings.setTypeToMock(ClassWithAnnotation.class);
        mockSettings.extraInterfaces(Serializable.class);

        ClassWithAnnotation proxy = mockMaker.createMock(mockSettings, dummyHandler());

        assertThat(proxy.getClass().isAnnotationPresent(SampleAnnotation.class)).isTrue();
        assertThat(proxy.getClass().getAnnotation(SampleAnnotation.class).value()).isEqualTo("foo");

        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .isAnnotationPresent(SampleAnnotation.class))
                .isTrue();
        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .getAnnotation(SampleAnnotation.class)
                                .value())
                .isEqualTo("bar");
    }

    @Test
    public void mock_interface_with_annotations() throws Exception {
        MockSettingsImpl<InterfaceWithAnnotation> mockSettings = new MockSettingsImpl<>();
        mockSettings.setTypeToMock(InterfaceWithAnnotation.class);

        InterfaceWithAnnotation proxy = mockMaker.createMock(mockSettings, dummyHandler());

        assertThat(proxy.getClass().isAnnotationPresent(SampleAnnotation.class)).isTrue();
        assertThat(proxy.getClass().getAnnotation(SampleAnnotation.class).value()).isEqualTo("foo");

        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .isAnnotationPresent(SampleAnnotation.class))
                .isTrue();
        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .getAnnotation(SampleAnnotation.class)
                                .value())
                .isEqualTo("bar");
    }

    @Test
    public void mock_interface_with_annotations_with_additional_interface() throws Exception {
        MockSettingsImpl<InterfaceWithAnnotation> mockSettings = new MockSettingsImpl<>();
        mockSettings.setTypeToMock(InterfaceWithAnnotation.class);
        mockSettings.extraInterfaces(Serializable.class);

        InterfaceWithAnnotation proxy = mockMaker.createMock(mockSettings, dummyHandler());

        assertThat(proxy.getClass().isAnnotationPresent(SampleAnnotation.class)).isFalse();

        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .isAnnotationPresent(SampleAnnotation.class))
                .isTrue();
        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .getAnnotation(SampleAnnotation.class)
                                .value())
                .isEqualTo("bar");
    }

    @Test
    public void mock_type_without_annotations() throws Exception {
        MockSettingsImpl<ClassWithAnnotation> mockSettings = new MockSettingsImpl<>();
        mockSettings.setTypeToMock(ClassWithAnnotation.class);
        mockSettings.withoutAnnotations();

        ClassWithAnnotation proxy = mockMaker.createMock(mockSettings, dummyHandler());

        assertThat(proxy.getClass().isAnnotationPresent(SampleAnnotation.class)).isFalse();
        assertThat(
                        proxy.getClass()
                                .getMethod("sampleMethod")
                                .isAnnotationPresent(SampleAnnotation.class))
                .isFalse();
    }

    @Test
        public void describe_class_for_instance_and_null() throws Exception {
        //Get the private describeClass(Object method) via reflection
        Method method =
            SubclassByteBuddyMockMaker.class.getDeclaredMethod("describeClass", Object.class);
        method.setAccessible(true); //Make the private method accessible

        Object instance = new Object();
        //Invoke the method with the instance
        String description = (String) method.invoke(null, instance);

        //Create the expected string
        Class<?> type = Object.class;
        String expected =
            "'"
                + type.getCanonicalName()
                + "', loaded by classloader : '"
                + type.getClassLoader()
                + "'";

        //Verifies the description matches the expected format
        assertThat(description).isEqualTo(expected);
        //Verifies that the string "null" is returned for null instance
        assertThat(method.invoke(null, new Object[] {null})).isEqualTo("null");
        }

    @Test
    public void describe_class_for_class_type_and_null() throws Exception {
        Method method =
                SubclassByteBuddyMockMaker.class.getDeclaredMethod("describeClass", Class.class);
        method.setAccessible(true);

        //Uses String.class as the test class (instead of object)
        Class<?> type = String.class;
        String description = (String) method.invoke(null, type);

        String expected =
                "'"
                        + type.getCanonicalName()
                        + "', loaded by classloader : '"
                        + type.getClassLoader()
                        + "'";

        assertThat(description).isEqualTo(expected);
        
        assertThat(method.invoke(null, new Object[] {null})).isEqualTo("null");
    }

    @Test
    public void get_handler_returns_null_when_mock_is_not_mock_access() {
        //Creates an object that is not a MockAccess instance
        Object notMockAccess = new Object();

        //Calls getHandler with the object
        MockHandler handler = mockMaker.getHandler(notMockAccess);
        
        //Verifies that null is returned
        assertThat(handler).isNull();
    }


    @Override
    protected Class<?> mockTypeOf(Class<?> type) {
        return type.getSuperclass();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SampleAnnotation {

        String value();
    }

    @SampleAnnotation("foo")
    public static class ClassWithAnnotation {

        @SampleAnnotation("bar")
        public void sampleMethod() {
            throw new UnsupportedOperationException();
        }
    }

    @SampleAnnotation("foo")
    public interface InterfaceWithAnnotation {

        @SampleAnnotation("bar")
        void sampleMethod();
    }
}
