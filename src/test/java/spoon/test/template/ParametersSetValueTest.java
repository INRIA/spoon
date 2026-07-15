/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtType;
import spoon.support.template.Parameters;
import spoon.template.Parameter;
import spoon.template.Template;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link Parameters#setValue(Template, String, Integer, Object)}.
 */
public class ParametersSetValueTest {

    /**
     * Minimal template implementation with a mutable {@link Parameter}-annotated field.
     */
    static class SimpleParamTemplate implements Template<CtType<?>> {
        @Parameter
        String myParam;

        @Override
        public CtType<?> apply(CtType<?> targetType) {
            return targetType;
        }

        @Override
        public boolean withPartialEvaluation() {
            return false;
        }
    }

    /**
     * Template with a {@code final} {@link Parameter}-annotated field.
     * The field itself cannot be mutated at runtime, so {@link Parameters} stores
     * the value in its internal {@code finals} map.
     */
    static class FinalParamTemplate implements Template<CtType<?>> {
        @Parameter
        final String finalParam = null;

        @Override
        public CtType<?> apply(CtType<?> targetType) {
            return targetType;
        }

        @Override
        public boolean withPartialEvaluation() {
            return false;
        }
    }

    @Test
    void testSetValueMutableField() {
        SimpleParamTemplate template = new SimpleParamTemplate();
        assertNull(template.myParam, "field should start as null");

        Parameters.setValue(template, "myParam", null, "hello");

        assertEquals("hello", template.myParam, "setValue should have updated the field");
    }

    @Test
    void testSetValueOverwritesMutableField() {
        SimpleParamTemplate template = new SimpleParamTemplate();
        Parameters.setValue(template, "myParam", null, "first");
        Parameters.setValue(template, "myParam", null, "second");

        assertEquals("second", template.myParam, "setValue should overwrite the previous value");
    }

    @Test
    void testSetValueUnknownParameterNameDoesNotThrow() {
        SimpleParamTemplate template = new SimpleParamTemplate();
        // When the parameter name does not match any field the method should silently return.
        Parameters.setValue(template, "doesNotExist", null, "irrelevant");
        // No exception should have been thrown; the existing field is untouched.
        assertNull(template.myParam);
    }

    @Test
    void testSetValueFinalField() throws Exception {
        FinalParamTemplate template = new FinalParamTemplate();
        Parameters.setValue(template, "finalParam", null, "storedValue");

        // For final fields the value is kept in the internal finals map;
        // use Parameters.getValue to verify the round-trip.
        Object retrieved = Parameters.getValue(template, "finalParam", null);
        assertEquals("storedValue", retrieved,
                "getValue should return the value stored for a final parameter");
    }
}
