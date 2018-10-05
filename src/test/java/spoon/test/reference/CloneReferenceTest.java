/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class CloneReferenceTest {

    @Test
    public void testGetDeclarationAfterClone() {
        // contract: all variable references of the clone (but fields) should point to the variable of the clone
        Launcher spoon = new Launcher();

        List<String> names = Arrays.asList("f1", "f2", "a", "b", "x", "param", "e");
        spoon.addInputResource("./src/test/resources/noclasspath/A2.java");
        spoon.getEnvironment().setComplianceLevel(8);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();


        final CtClass<Object> a = spoon.getFactory().Class().get("A2");
        // test before clone
        for (String name : names) {
            CtVariable var1 = findVariable(a, name);
            CtVariable var2 = findReference(a, name).getDeclaration();
            assertSame(var1, var2);
        }

        CtClass b = a.clone();

        // test after clone
        for (String name : names) {
            CtVariable var1 = findVariable(b, name);
            CtVariableReference refVar1 = findReference(b, name);
            CtVariable var2 = refVar1.getDeclaration();
            assertSame("Var1 and var2 are not the same element", var1, var2);
        }
    }

    @Test
    public void testGetDeclarationOfFieldAfterClone() {
        // contract: all field references of the clone point to the old class
        // behaviour changed on https://github.com/INRIA/spoon/pull/1215
        Launcher spoon = new Launcher();

        String name = "field";
        spoon.addInputResource("./src/test/resources/noclasspath/A2.java");
        spoon.getEnvironment().setComplianceLevel(8);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();


        final CtClass<Object> a = spoon.getFactory().Class().get("A2");
        // test before clone
        CtField oldVar1 = (CtField)findVariable(a, name);
        CtField oldVar2 = (CtField)findReference(a, name).getDeclaration();
        assertSame(oldVar1, oldVar2);

        CtClass b = a.clone();

        // test after clone
        CtField var1 = (CtField)findVariable(b, name);
        CtVariableReference refVar1 = findReference(b, name);
        CtField var2 = (CtField)refVar1.getDeclaration();
        assertNotSame(var1, var2);
        assertSame(var2, oldVar1);
        assertSame(var1.getParent(CtClass.class), b);
    }

    class Finder<T> extends CtScanner {

        private final Class<T> c;
        private final Predicate<T> filter;
        private T result;

        public Finder(Class<T> c, Predicate<T> filter) {
            this.c = c;
            this.filter = filter;
        }

        @Override
        public void scan(CtElement element) {
            if (element != null && c.isAssignableFrom(element.getClass()) && filter.test((T) element)) {
                result = (T) element;
            } else {
                super.scan(element);
            }
        }

        public T find(CtElement root) {
            scan(root);
            return result;
        }
    }

    public <T extends CtElement> T find(CtElement root, Class<T> c, Predicate<T> filter) {
        return new Finder<>(c, filter).find(root);
    }

    public CtVariable findVariable(CtElement root, String name) {
        return find(root, CtVariable.class, var -> name.equals(var.getSimpleName()));
    }

    public CtVariableReference findReference(CtElement root, String name) {
        return find(root, CtVariableReference.class, ref -> name.equals(ref.getSimpleName()));
    }
}