package spoon.test.variable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.filter.TypeFilter;

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
        assertEquals(4, localVariables.size());

        assertFalse(localVariables.get(0).isInferred());
        assertTrue(localVariables.get(1).isInferred());
        assertTrue(localVariables.get(2).isInferred());
        assertTrue(localVariables.get(3).isInferred());
    }
}
