package spoon.test.ctBlock;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 15/03/2017.
 */
public class TestCtBlock {

    @Test
    public void testRemoveStatement() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/ctBlock/testclasses/Toto.java");
        spoon.buildModel();

        List<CtMethod> methods = spoon.getModel().getElements(new NameFilter<CtMethod>("foo"));

        assertEquals(1, methods.size());

        CtMethod foo = methods.get(0);

        CtBlock block = foo.getBody();
        CtStatement lastStatement = block.getLastStatement();

        assertEquals("i++", lastStatement.toString());

        block.removeStatement(lastStatement);

        CtStatement newLastStatement = block.getLastStatement();

        assertTrue(newLastStatement != lastStatement);
        assertTrue(newLastStatement instanceof CtIf);
    }
}
