package spoon.test.visibility;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class VisibilityTest {
    @Test
    public void testMethodeWithNonAccessibleTypeArgument() throws Exception {
        Factory f = build(spoon.test.visibility.MethodeWithNonAccessibleTypeArgument.class,
                spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf.class,
                Class.forName("spoon.test.visibility.packageprotected.NonAccessibleInterf")
                );
        CtClass<?> type = f.Class().get(spoon.test.visibility.MethodeWithNonAccessibleTypeArgument.class);
        assertEquals("MethodeWithNonAccessibleTypeArgument", type.getSimpleName());
        CtMethod<?> m = type.getMethodsByName("method").get(0);
        assertEquals(
                "new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf().method(new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf())",
                m.getBody().getStatement(0).toString()
        );
    }
}
