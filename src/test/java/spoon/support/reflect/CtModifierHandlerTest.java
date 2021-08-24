package spoon.support.reflect;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CtModifierHandlerTest {

    LinkedHashSet<ModifierKind> inputModifierKinds = new LinkedHashSet<>(Arrays.asList(
            ModifierKind.STATIC,
            ModifierKind.PUBLIC,
            ModifierKind.ABSTRACT,
            ModifierKind.NATIVE
    ));
    LinkedHashSet<CtExtendedModifier> inputExtendedModifiers = inputModifierKinds.stream().map(CtExtendedModifier::new).collect(Collectors.toCollection(LinkedHashSet::new));

    // these will be overwritten in each test.
    Set<ModifierKind> modifiers;
    Set<CtExtendedModifier> modifiersExt;
    CtModifierHandler handler;

    @Before
    public void setUp() {
        Launcher l = new Launcher();
        Factory f = l.createFactory();
        handler = new CtModifierHandler(f.createClass("org.test.Test"));
    }

    @Test
    public void testAddModifier() {
        inputModifierKinds.forEach(handler::addModifier);
        modifiersExt = handler.getExtendedModifiers();
        modifiers = handler.getModifiers();

        assertEquals("Order of modifiers was not preserved.", inputModifierKinds, modifiers);
        assertEquals("Order of extended modifiers was not preserved.", inputExtendedModifiers, modifiersExt);
    }

    @Test
    public void testSetModifiers() {
        handler.setModifiers(inputModifierKinds);
        modifiersExt = handler.getExtendedModifiers();
        modifiers = handler.getModifiers();

        assertEquals("Order of modifiers was not preserved.", inputModifierKinds, modifiers);
        assertEquals("Order of extended modifiers was not preserved.", inputExtendedModifiers, modifiersExt);
    }

    @Test
    public void testSetExtendedModifiers() {
        handler.setExtendedModifiers(inputExtendedModifiers);
        modifiersExt = handler.getExtendedModifiers();
        modifiers = handler.getModifiers();

        assertEquals("Order of modifiers was not preserved.", inputModifierKinds, modifiers);
        assertEquals("Order of extended modifiers was not preserved.", inputExtendedModifiers, modifiersExt);
    }
}
