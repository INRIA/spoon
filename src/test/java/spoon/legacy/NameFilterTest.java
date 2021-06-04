/**
 * Copyright (C) 2006-2021 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.test.filters.testclasses.Foo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

class NameFilterTest {

    private Factory factory;

    @BeforeEach
    public void setup() throws Exception {
        factory = build(Foo.class);
    }

    @Test
    public void testNameFilter() throws Exception {
        // contract: legacy NameFilter is tested and works
        CtClass<?> foo = factory.Package().get("spoon.test.filters.testclasses").getType("Foo");
        assertEquals("Foo", foo.getSimpleName());
        List<CtNamedElement> elements = foo.getElements(new NameFilter<>("i"));
        assertEquals(1, elements.size());
    }
}