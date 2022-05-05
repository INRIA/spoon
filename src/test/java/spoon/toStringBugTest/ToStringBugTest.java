package spoon.toStringBugTest;


import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtType;

import static org.junit.jupiter.api.Assertions.fail;

public class ToStringBugTest {
    
    @Test
    public void testIssue3382() {
        // contract: the cleanComment should not crash
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        // folder with both classes
        launcher.addInputResource("./src/test/resources/toStringBugTest");
        launcher.buildModel();
        List<CtType<?>> all = launcher.getFactory().Class().getAll();

        // first class of resources
        CtType<?> ctType0 = all.get(0);
        try {
            String s = ctType0.toString();
        } catch (SpoonException e) {
            // spoon.SpoonException: Unexpected next line after last line
            fail(e.toString());
        }

        // second class of resources
        CtType<?> ctType1 = all.get(1);
        try {
            String s = ctType1.toString();
        } catch (SpoonException e) {
            fail(e.toString());
        }
    }
}
