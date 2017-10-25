package spoon.test.interfaces;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InterfaceTest {
    @Test
    public void testModifierFromInterfaceFieldAndMethod() {
        // contract: methods defined in interface are all public and fields are all public and static
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/resources/spoon/test/itf/DumbItf.java");
        spoon.getEnvironment().setNoClasspath(false);
        spoon.buildModel();

        CtType dumbType = spoon.getFactory().Type().get("toto.DumbItf");

        assertEquals(1, dumbType.getFields().size());

        CtField field = (CtField)dumbType.getFields().get(0);

        assertEquals( ModifierKind.PUBLIC, field.getVisibility());
        assertTrue(field.hasModifier(ModifierKind.STATIC));
        assertTrue(field.hasModifier(ModifierKind.PUBLIC));
        assertTrue(field.hasModifier(ModifierKind.FINAL));

        assertEquals(3, dumbType.getMethods().size());

        CtMethod staticMethod = (CtMethod) dumbType.getMethodsByName("foo").get(0);
        assertTrue(staticMethod.hasModifier(ModifierKind.PUBLIC));
        assertTrue(staticMethod.hasModifier(ModifierKind.STATIC));

        CtMethod publicMethod = (CtMethod) dumbType.getMethodsByName("machin").get(0);
        assertTrue(publicMethod.hasModifier(ModifierKind.PUBLIC));
        assertFalse(publicMethod.hasModifier(ModifierKind.STATIC));

        CtMethod defaultMethod = (CtMethod) dumbType.getMethodsByName("bla").get(0);
        assertTrue(defaultMethod.hasModifier(ModifierKind.PUBLIC));
        assertTrue(defaultMethod.isDefaultMethod());
        assertFalse(defaultMethod.hasModifier(ModifierKind.STATIC));
    }
}
