package spoon.test.imports;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

public class ImportAndExtendWithPackageNameTest {

    private static final String inputResource =
            "./src/test/resources/import-resources/ImportAndExtendWithPackageName.java";

    @Test
    public void testBuildModel() {
        final Launcher runLaunch = new Launcher();
        runLaunch.getEnvironment().setNoClasspath(true);
        runLaunch.addInputResource(inputResource);
        runLaunch.buildModel();

        final Collection<CtType<?>> types = runLaunch.getModel().getAllTypes();
        Assert.assertSame(1, types.size());

        final CtType type = types.iterator().next();
        Assert.assertEquals("ImportAndExtendWithPackageName", type.getSimpleName());

        final CtTypeReference superClass = type.getSuperclass();
        Assert.assertEquals("LLkParser", superClass.getSimpleName());
    }
}
