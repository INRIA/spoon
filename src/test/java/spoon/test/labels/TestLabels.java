package spoon.test.labels;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 19/06/2017.
 */
public class TestLabels {
    @Test
    public void testLabelsAreDetected() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/labels/testclasses/ManyLabels.java");
        launcher.buildModel();

        CtMethod mainMethod = launcher.getFactory().getModel().getElements(new NameFilter<CtMethod>("main")).get(0);

        CtBlock body = mainMethod.getBody();
        assertEquals(2, body.getStatements().size());

        assertEquals("labelBlock", body.getStatement(0).getLabel());
        assertEquals("sw", body.getStatement(1).getLabel());

        CtBlock block = (CtBlock) body.getStatement(0);
        CtSwitch ctSwitch = (CtSwitch) body.getStatement(1);

        assertTrue(block.getStatement(1) instanceof CtIf);

        CtIf firstIf = (CtIf) block.getStatement(1);

        CtBlock then = firstIf.getThenStatement();
        CtBreak firstBreak = (CtBreak) then.getStatement(1);

        assertEquals("labelBlock", firstBreak.getTargetLabel());
        assertEquals(block, firstBreak.getLabelledStatement());
    }
}
