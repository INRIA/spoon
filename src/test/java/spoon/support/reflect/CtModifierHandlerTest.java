package spoon.support.reflect;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CtModifierHandlerTest {

    private final List<ModifierKind> inputModifierKinds = Arrays.asList(
            ModifierKind.STATIC,
            ModifierKind.PUBLIC,
            ModifierKind.ABSTRACT,
            ModifierKind.NATIVE
    );
    private final List<CtExtendedModifier> inputExtendedModifiers = inputModifierKinds
            .stream()
            .map(CtExtendedModifier::new)
            .collect(Collectors.toList());

    private void assertPass(CtModifierHandler handler) {
        Set<ModifierKind> outputModifierKinds = handler.getModifiers();
        Set<CtExtendedModifier> outputExtendedModifiers = handler.getExtendedModifiers();

        // We iterate over these collections
        Iterator<ModifierKind> inputIterator = inputModifierKinds.iterator();
        Iterator<CtExtendedModifier> inputIteratorExt = inputExtendedModifiers.iterator();
        Iterator<ModifierKind> outputIterator = outputModifierKinds.iterator();
        Iterator<CtExtendedModifier> outputIteratorExt = outputExtendedModifiers.iterator();

        Supplier<Boolean> hasNext = () -> inputIterator.hasNext()
                && inputIteratorExt.hasNext()
                && outputIterator.hasNext()
                && outputIteratorExt.hasNext();

        while (hasNext.get()) {
            assertEquals(inputIterator.next(), outputIterator.next());
            assertEquals(inputIteratorExt.next(), outputIteratorExt.next());
        }

        // None of these iterators should have any elements left.
        assertFalse(inputIterator.hasNext()
                || inputIteratorExt.hasNext()
                || outputIterator.hasNext()
                || outputIteratorExt.hasNext()
        );
    }

    @Test
    public void testAddModifier() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.createFactory();
        CtModifierHandler handler = new CtModifierHandler(factory.createClass("org.test.Test"));

        inputModifierKinds.forEach(handler::addModifier);
        assertPass(handler);
    }

    @Test
    public void testSetModifiers() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.createFactory();
        CtModifierHandler handler = new CtModifierHandler(factory.createClass("org.test.Test"));

        handler.setModifiers(new LinkedHashSet<>(inputModifierKinds));
        assertPass(handler);
    }

    @Test
    public void testSetExtendedModifiers() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.createFactory();
        CtModifierHandler handler = new CtModifierHandler(factory.createClass("org.test.Test"));

        handler.setExtendedModifiers(new LinkedHashSet<>(inputExtendedModifiers));
        assertPass(handler);
    }
}
