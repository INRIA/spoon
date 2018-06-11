package spoon.test.variable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.FileReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InferredVariableTest {

    @Test
    public void testInferredVariableAreMarked() {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(10);
        launcher.addInputResource("./src/test/resources/spoon/test/var/Main.java");

        CtModel model = launcher.buildModel();
        List<CtLocalVariable> localVariables = model.getElements(new TypeFilter<>(CtLocalVariable.class));
        assertEquals(8, localVariables.size());

        TypeFactory typeFactory = launcher.getFactory().Type();

        assertTrue(localVariables.get(0).isInferred());
        assertEquals(typeFactory.STRING, localVariables.get(0).getType());

        assertFalse(localVariables.get(1).isInferred());
        assertEquals(typeFactory.STRING, localVariables.get(1).getType());

        assertTrue(localVariables.get(2).isInferred());
        assertEquals("java.io.FileReader", localVariables.get(2).getType().getQualifiedName());

        assertFalse(localVariables.get(3).isInferred());
        assertEquals("java.io.FileReader", localVariables.get(3).getType().getQualifiedName());

        assertTrue(localVariables.get(4).isInferred());
        assertEquals(typeFactory.BOOLEAN_PRIMITIVE, localVariables.get(4).getType());

        assertFalse(localVariables.get(5).isInferred());
        assertEquals(typeFactory.BOOLEAN_PRIMITIVE, localVariables.get(5).getType());

        assertTrue(localVariables.get(6).isInferred());
        assertEquals(typeFactory.INTEGER_PRIMITIVE, localVariables.get(6).getType());

        assertFalse(localVariables.get(7).isInferred());
        assertEquals(typeFactory.INTEGER_PRIMITIVE, localVariables.get(7).getType());
    }
}
