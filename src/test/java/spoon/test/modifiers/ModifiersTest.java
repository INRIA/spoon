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
package spoon.test.modifiers;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.modifiers.testclasses.AbstractClass;
import spoon.test.modifiers.testclasses.MethodVarArgs;
import spoon.test.modifiers.testclasses.StaticMethod;
import spoon.testing.utils.ModelUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ModifiersTest {

    @Test
    public void testMethodWithVarargsDoesNotBecomeTransient() {
        // contract: method with varsargs should not become transient
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/MethodVarArgs.java");
        spoon.buildModel();

        CtType<?> myClass = spoon.getFactory().Type().get(MethodVarArgs.class);
        CtMethod methodVarargs = myClass.getMethodsByName("getInitValues").get(0);

        Set<ModifierKind> expectedModifiers = Collections.singleton(ModifierKind.PROTECTED);

        assertEquals(expectedModifiers, methodVarargs.getModifiers());

        spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/MethodVarArgs.java");
        spoon.getEnvironment().setShouldCompile(true);
        spoon.run();
    }

    @Test
    public void testCtModifiableAddRemoveReturnCtModifiable() {
        // contract: CtModifiable#addModifier and CtModifiable#removeModifier should return CtModifiable

        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/MethodVarArgs.java");
        spoon.buildModel();

        CtType<?> myClass = spoon.getFactory().Type().get(MethodVarArgs.class);
        CtMethod methodVarargs = myClass.getMethodsByName("getInitValues").get(0);

        Object o = methodVarargs.addModifier(ModifierKind.FINAL);
        assertEquals(methodVarargs, o);

        o = methodVarargs.removeModifier(ModifierKind.FINAL);
        assertEquals(methodVarargs, o);
    }

    @Test
    public void testSetVisibility() {
        // contract: setVisibility should only work with public/private/protected modifiers

        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/StaticMethod.java");
        spoon.buildModel();

        CtType<?> myClass = spoon.getFactory().Type().get(StaticMethod.class);
        CtMethod methodPublicStatic = myClass.getMethodsByName("maMethod").get(0);

        assertEquals(ModifierKind.PUBLIC, methodPublicStatic.getVisibility());
        methodPublicStatic.setVisibility(ModifierKind.PROTECTED);
        assertEquals(ModifierKind.PROTECTED, methodPublicStatic.getVisibility());
        try {
            methodPublicStatic.setVisibility(ModifierKind.FINAL);
            fail();
        } catch (SpoonException e) {
        }

        assertEquals(ModifierKind.PROTECTED, methodPublicStatic.getVisibility());
    }

    @Test
    public void testGetModifiersHelpers() {
        // contract: the CtModifiable helpers like isPublic, isFinal etc returns right values

        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/AbstractClass.java");
        spoon.addInputResource("./src/test/java/spoon/test/modifiers/testclasses/ConcreteClass.java");
        spoon.getEnvironment().setShouldCompile(true);
        spoon.run();

        CtType<?> abstractClass = spoon.getFactory().Type().get(AbstractClass.class);

        checkCtModifiableHelpersAssertion(abstractClass, true, false, false, true, false, false, false, false, false, false, false);

        assertEquals(6, abstractClass.getFields().size());
        for (CtField field : abstractClass.getFields()) {
            switch (field.getSimpleName()) {
                case "privateField":
                    checkCtModifiableHelpersAssertion(field, false, false, true, false, false, false, false, false, false, false, false);
                    break;

                case "protectedField":
                    checkCtModifiableHelpersAssertion(field, false, true, false, false, false, false, false, false, false, false, false);
                    break;

                case "privateStaticField":
                    checkCtModifiableHelpersAssertion(field, false, false, true, false, false, true, false, false, false, false, false);
                    break;

                case "publicFinalField":
                    checkCtModifiableHelpersAssertion(field, true, false, false, false, true, false, false, false, false, false, false);
                    break;

                case "transientField":
                    checkCtModifiableHelpersAssertion(field, false, false, false, false, false, false, true, false, false, false, false);
                    break;

                case "volatileField":
                    checkCtModifiableHelpersAssertion(field, false, false, false, false, false, false, false, true, false, false, false);
                    break;

                default:
                    fail("The field "+field.getSimpleName()+" should be take into account.");
            }
        }

        assertEquals(7, abstractClass.getMethods().size());

        for (CtMethod method : abstractClass.getMethods()) {
            switch (method.getSimpleName()) {
                case "method":
                    checkCtModifiableHelpersAssertion(method, true, false, false, false, true, true, false, false, false, false, false);
                    break;

                case "onlyStatic":
                    checkCtModifiableHelpersAssertion(method, true, false, false, false, false, true, false, false, false, false, false);
                    break;

                case "otherMethod":
                    checkCtModifiableHelpersAssertion(method, false, true, false, true, false, false, false, false, false, false, false);
                    break;

                case "anotherOne":
                    checkCtModifiableHelpersAssertion(method, false, false, false, true, false, false, false, false, false, false, false);
                    break;

                case "synchronizedMethod":
                    checkCtModifiableHelpersAssertion(method, false, true, false, false, false, false, false, false, true, false, false);
                    break;

                case "nativeMethod":
                    checkCtModifiableHelpersAssertion(method, false, true, false, false, false, false, false, false, false, true, false);
                    break;

                case "strictfpMethod":
                    checkCtModifiableHelpersAssertion(method, false, true, false, false, false, false, false, false, false, false, true);
                    break;

                default:
                    fail("The method "+method.getSimpleName()+" should be taken into account.");
            }
        }

        CtType<?> concreteClass = spoon.getFactory().Type().get("spoon.test.modifiers.testclasses.ConcreteClass");
        checkCtModifiableHelpersAssertion(concreteClass, false, false, false, false, true, false, false, false, false, false, false);

        assertEquals(2, concreteClass.getFields().size());
        for (CtField field : concreteClass.getFields()) {
            switch (field.getSimpleName()) {
                case "className":
                    checkCtModifiableHelpersAssertion(field, true, false, false, false, true, true, false, false, false, false, false);
                    break;

                case "test":
                    checkCtModifiableHelpersAssertion(field, false, false, true, false, false, true, false, false, false, false, false);
                    break;

                default:
                    fail("The field "+field.getSimpleName()+" should be take into account.");
            }
        }

        assertEquals(2, concreteClass.getMethods().size());
        for (CtMethod method : concreteClass.getMethods()) {
            switch (method.getSimpleName()) {
                case "otherMethod":
                    checkCtModifiableHelpersAssertion(method, false, true, false, false, false, false, false, false, false, false, false);
                    break;

                case "anotherOne":
                    checkCtModifiableHelpersAssertion(method, false, false, false, false, true, false, false, false, false, false, false);
                    break;

                default:
                    fail("The method "+method.getSimpleName()+" should be taken into account.");
            }
        }
    }

    private void checkCtModifiableHelpersAssertion(CtModifiable element, boolean isPublic, boolean isProtected, boolean isPrivate, boolean isAbstract, boolean isFinal, boolean isStatic, boolean isTransient, boolean isVolatile, boolean isSynchronized, boolean isNative, boolean isStrictfp) {
        assertEquals(isPublic, element.isPublic(), "isPublic for " + element + " is wrong");
        assertEquals(isProtected, element.isProtected(), "isProtected for " + element + " is wrong");
        assertEquals(isPrivate, element.isPrivate(), "isPrivate for " + element + " is wrong");
        assertEquals(isAbstract, element.isAbstract(), "isAbstract for " + element + " is wrong");
        assertEquals(isFinal, element.isFinal(), "isFinal for " + element + " is wrong");
        assertEquals(isStatic, element.isStatic(), "isStatic for " + element + " is wrong");
        assertEquals(isTransient, element.isTransient(), "isTransient for " + element + " is wrong");
        assertEquals(isVolatile, element.isVolatile(), "isVolatile for " + element + " is wrong");
        assertEquals(isSynchronized, element.isSynchronized(), "isSynchronized for " + element + " is wrong");
        assertEquals(isNative, element.isNative(), "isNative for " + element + " is wrong");
        assertEquals(isStrictfp, element.isStrictfp(), "isStrictfp for " + element + " is wrong");
    }

    @Test
    public void testClearModifiersByEmptySet() throws Exception {
    	//contract: it is possible to remove modifiers by setModifiers(emptySet)
    	CtType<?> ctClass = ModelUtils.buildClass(StaticMethod.class);
    	assertTrue(ctClass.hasModifier(ModifierKind.PUBLIC));
    	assertEquals(1, ctClass.getModifiers().size());

    	ctClass.setModifiers(Collections.emptySet());
    	assertFalse(ctClass.hasModifier(ModifierKind.PUBLIC));
    	assertEquals(0, ctClass.getModifiers().size());
    }

    @Test
    public void testClearModifiersByNull() throws Exception {
    	//contract: it is possible to remove modifiers by setModifiers(null)
    	CtType<?> ctClass = ModelUtils.buildClass(StaticMethod.class);
    	assertTrue(ctClass.hasModifier(ModifierKind.PUBLIC));
    	assertEquals(1, ctClass.getModifiers().size());

    	// contract: one can get the modifiers through CtRole.EMODIFIER
        Collection<CtExtendedModifier> valueByRole = ctClass.getValueByRole(CtRole.EMODIFIER);
        assertEquals(1, valueByRole.size());

        ctClass.setModifiers(null);
    	assertFalse(ctClass.hasModifier(ModifierKind.PUBLIC));
    	assertEquals(0, ctClass.getModifiers().size());

    }
}
