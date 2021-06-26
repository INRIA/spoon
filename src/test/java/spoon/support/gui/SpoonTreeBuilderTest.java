package spoon.support.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.declaration.CtClassImpl;

import javax.swing.tree.DefaultMutableTreeNode;

import static org.junit.jupiter.api.Assertions.*;

class SpoonTreeBuilderTest {
    Launcher spoon;
    Factory factory;
    SpoonTreeBuilder spoonTreeBuilder;

    @BeforeEach
    public void setUp() {
        spoon = new Launcher();
        spoon.buildModel();
        factory = spoon.getFactory();

        spoonTreeBuilder = new SpoonTreeBuilder();
    }

    @Test
    public void testEnter() {
        // contract : SpoonTreeBuilder.enter creates a node for the entered element while entering the scanner

        CtClass<?> testClass = factory.Class().create("testClass");

        spoonTreeBuilder.enter(testClass);
        DefaultMutableTreeNode node = spoonTreeBuilder.nodes.peek();

        assertEquals("testClass", ((CtClassImpl<?>) node.getUserObject()).getSimpleName());
    }

    @Test
    public void testExit() {
        // contract : SpoonTreeBuilder.exit removes the node for the current element while exiting the scanner

        CtClass<?> testClass = factory.Class().create("testClass");

        spoonTreeBuilder.enter(testClass);
        DefaultMutableTreeNode node = spoonTreeBuilder.nodes.peek();
        spoonTreeBuilder.exit(testClass);

        assertNotEquals(node, spoonTreeBuilder.nodes.peek());
    }
}