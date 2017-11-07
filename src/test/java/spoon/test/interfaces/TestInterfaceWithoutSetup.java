package spoon.test.interfaces;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestInterfaceWithoutSetup {
    @Test
    public void testModifierFromInterfaceFieldAndMethod() {
        // contract: methods defined in interface are all public and fields are all public and static
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/resources/spoon/test/itf/DumbItf.java");
        spoon.getEnvironment().setNoClasspath(false);
        spoon.buildModel();

        CtType dumbType = spoon.getFactory().Type().get("toto.DumbItf");

        assertEquals(2, dumbType.getFields().size());

        CtField fieldImplicit = dumbType.getField("CONSTANT_INT");

        Set<CtExtendedModifier> extendedModifierSet = fieldImplicit.getExtendedModifiers();
        assertEquals(3, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.FINAL, true)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, true)));

        assertEquals(ModifierKind.PUBLIC, fieldImplicit.getVisibility());
        assertTrue(fieldImplicit.hasModifier(ModifierKind.STATIC));
        assertTrue(fieldImplicit.hasModifier(ModifierKind.PUBLIC));
        assertTrue(fieldImplicit.hasModifier(ModifierKind.FINAL));

        CtField fieldExplicit = dumbType.getField("ANOTHER_INT");

        extendedModifierSet = fieldExplicit.getExtendedModifiers();
        assertEquals(3, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.FINAL, true)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, false)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, false)));

        assertEquals(ModifierKind.PUBLIC, fieldExplicit.getVisibility());
        assertTrue(fieldExplicit.hasModifier(ModifierKind.STATIC));
        assertTrue(fieldExplicit.hasModifier(ModifierKind.PUBLIC));
        assertTrue(fieldExplicit.hasModifier(ModifierKind.FINAL));

        assertEquals(4, dumbType.getMethods().size());

        CtMethod staticMethod = (CtMethod) dumbType.getMethodsByName("foo").get(0);
        assertTrue(staticMethod.hasModifier(ModifierKind.PUBLIC));
        assertTrue(staticMethod.hasModifier(ModifierKind.STATIC));

        extendedModifierSet = staticMethod.getExtendedModifiers();
        assertEquals(2, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, false)));

        CtMethod publicMethod = (CtMethod) dumbType.getMethodsByName("machin").get(0);
        assertTrue(publicMethod.hasModifier(ModifierKind.PUBLIC));
        assertFalse(publicMethod.hasModifier(ModifierKind.STATIC));

        extendedModifierSet = publicMethod.getExtendedModifiers();
        assertEquals(2, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.ABSTRACT, true)));

        CtMethod defaultMethod = (CtMethod) dumbType.getMethodsByName("bla").get(0);
        assertTrue(defaultMethod.hasModifier(ModifierKind.PUBLIC));
        assertTrue(defaultMethod.isDefaultMethod());
        assertFalse(defaultMethod.hasModifier(ModifierKind.STATIC));

        extendedModifierSet = defaultMethod.getExtendedModifiers();
        assertEquals(1, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));

        CtMethod explicitDefaultMethod = (CtMethod) dumbType.getMethodsByName("anotherOne").get(0);
        assertTrue(explicitDefaultMethod.hasModifier(ModifierKind.PUBLIC));

        extendedModifierSet = explicitDefaultMethod.getExtendedModifiers();
        assertEquals(2, extendedModifierSet.size());
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, false)));
        assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.ABSTRACT, true)));
    }
}
