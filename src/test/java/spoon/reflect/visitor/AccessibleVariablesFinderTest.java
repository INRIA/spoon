/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.support.compiler.VirtualFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link AccessibleVariablesFinder}, specifically covering the
 * anonymous inner class method {@code VariableScanner.scanCtType} (line 77 of
 * AccessibleVariablesFinder.java).
 */
public class AccessibleVariablesFinderTest {

    /**
     * Contract: scanCtType adds public and protected fields of a type to the
     * accessible variables, but NOT private fields when the expression is outside
     * the declaring type.
     *
     * This test places an expression in Child (which extends Parent).
     * Public and protected fields of Parent must be accessible; private fields
     * of Parent must NOT be accessible from Child.
     */
    @Test
    public void testScanCtType_publicAndProtectedFieldsAccessibleFromSubclass() {
        // language=java
        String code = "package com.example;\n"
                + "public class Parent {\n"
                + "    public int publicField = 1;\n"
                + "    protected int protectedField = 2;\n"
                + "    private int privateField = 3;\n"
                + "    int packageField = 4;\n"
                + "}\n";

        String childCode = "package com.example;\n"
                + "public class Child extends Parent {\n"
                + "    public void method() {\n"
                + "        int x = 0;\n"
                + "    }\n"
                + "}\n";

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new VirtualFile(code, "Parent.java"));
        launcher.addInputResource(new VirtualFile(childCode, "Child.java"));
        launcher.buildModel();

        CtClass<?> childClass = (CtClass<?>) launcher.getFactory().Type().get("com.example.Child");
        // Use the method itself as the expression anchor so that method.getParent() == childClass (a CtType),
        // which causes AccessibleVariablesFinder to invoke scanCtType on the enclosing class and its parents.
        CtMethod<?> method = childClass.getMethodsByName("method").get(0);

        AccessibleVariablesFinder avf = new AccessibleVariablesFinder(method);
        List<CtVariable> found = avf.find();
        List<String> names = found.stream().map(CtVariable::getSimpleName).collect(Collectors.toList());

        // public and protected fields from Parent are accessible
        assertTrue(names.contains("publicField"),
                "publicField should be accessible from Child; found: " + names);
        assertTrue(names.contains("protectedField"),
                "protectedField should be accessible from Child; found: " + names);
        // package-private field from same package is accessible
        assertTrue(names.contains("packageField"),
                "packageField (default visibility, same package) should be accessible from Child; found: " + names);
        // private field from Parent is NOT accessible from Child
        assertFalse(names.contains("privateField"),
                "privateField should NOT be accessible from Child; found: " + names);
    }

    /**
     * Contract: scanCtType adds private fields when the expression IS inside
     * the declaring type (i.e., expression.hasParent(type) is true).
     */
    @Test
    public void testScanCtType_privateFieldAccessibleFromWithinDeclaringType() {
        String code = "package com.example;\n"
                + "public class Owner {\n"
                + "    public int publicField = 1;\n"
                + "    protected int protectedField = 2;\n"
                + "    private int privateField = 3;\n"
                + "    int packageField = 4;\n"
                + "    public void method() {\n"
                + "        int x = 0;\n"
                + "    }\n"
                + "}\n";

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new VirtualFile(code, "Owner.java"));
        launcher.buildModel();

        CtClass<?> ownerClass = (CtClass<?>) launcher.getFactory().Type().get("com.example.Owner");
        // Use the method itself as the expression anchor so that method.getParent() == ownerClass (a CtType),
        // which causes AccessibleVariablesFinder to invoke scanCtType on the enclosing class.
        CtMethod<?> method = ownerClass.getMethodsByName("method").get(0);

        AccessibleVariablesFinder avf = new AccessibleVariablesFinder(method);
        List<CtVariable> found = avf.find();
        List<String> names = found.stream().map(CtVariable::getSimpleName).collect(Collectors.toList());

        // all fields of Owner (including private) are accessible from within Owner
        assertTrue(names.contains("publicField"),
                "publicField should be accessible; found: " + names);
        assertTrue(names.contains("protectedField"),
                "protectedField should be accessible; found: " + names);
        assertTrue(names.contains("privateField"),
                "privateField should be accessible from within Owner; found: " + names);
        assertTrue(names.contains("packageField"),
                "packageField should be accessible; found: " + names);
    }

    /**
     * Contract: scanCtType recurses into super-interfaces, adding their public
     * and protected fields as accessible variables.
     */
    @Test
    public void testScanCtType_superInterfaceFieldsAccessible() {
        String ifaceCode = "package com.example;\n"
                + "public interface MyInterface {\n"
                + "    int IFACE_CONST = 42;\n"
                + "}\n";

        String implCode = "package com.example;\n"
                + "public class Impl implements MyInterface {\n"
                + "    public void method() {\n"
                + "        int x = 0;\n"
                + "    }\n"
                + "}\n";

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new VirtualFile(ifaceCode, "MyInterface.java"));
        launcher.addInputResource(new VirtualFile(implCode, "Impl.java"));
        launcher.buildModel();

        CtClass<?> implClass = (CtClass<?>) launcher.getFactory().Type().get("com.example.Impl");
        // Use the method itself as the expression anchor so that method.getParent() == implClass (a CtType).
        CtMethod<?> method = implClass.getMethodsByName("method").get(0);

        AccessibleVariablesFinder avf = new AccessibleVariablesFinder(method);
        List<CtVariable> found = avf.find();
        List<String> names = found.stream().map(CtVariable::getSimpleName).collect(Collectors.toList());

        // Interface constants (implicitly public static final) must be accessible
        assertTrue(names.contains("IFACE_CONST"),
                "IFACE_CONST from super-interface should be accessible; found: " + names);
    }

    /**
     * Contract: default-visibility fields are accessible only when the expression
     * is in the same package as the declaring type.
     */
    @Test
    public void testScanCtType_defaultVisibilityFieldNotAccessibleFromDifferentPackage() {
        String parentCode = "package com.parent;\n"
                + "public class Parent {\n"
                + "    public int publicField = 1;\n"
                + "    int packageField = 2;\n"
                + "}\n";

        String childCode = "package com.child;\n"
                + "public class Child extends com.parent.Parent {\n"
                + "    public void method() {\n"
                + "        int x = 0;\n"
                + "    }\n"
                + "}\n";

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new VirtualFile(parentCode, "Parent.java"));
        launcher.addInputResource(new VirtualFile(childCode, "Child.java"));
        launcher.buildModel();

        CtClass<?> childClass = (CtClass<?>) launcher.getFactory().Type().get("com.child.Child");
        // Use the method itself as the expression anchor so that method.getParent() == childClass (a CtType).
        CtMethod<?> method = childClass.getMethodsByName("method").get(0);

        AccessibleVariablesFinder avf = new AccessibleVariablesFinder(method);
        List<CtVariable> found = avf.find();
        List<String> names = found.stream().map(CtVariable::getSimpleName).collect(Collectors.toList());

        // public field from Parent is visible across packages
        assertTrue(names.contains("publicField"),
                "publicField should be accessible from a different package; found: " + names);
        // package-private field from a different package must NOT be accessible
        assertFalse(names.contains("packageField"),
                "packageField (default visibility) should NOT be accessible from a different package; found: " + names);
    }
}
