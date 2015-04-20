package spoon.reflect.declaration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.declaration.testclasses.ExtendsObject;
import spoon.reflect.declaration.testclasses.Subclass;
import spoon.reflect.declaration.testclasses.Subinterface;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.io.File;
import java.util.Set;

public class CtTypeInformationTest
{
    private Factory factory;

    @Before
    public void setUp() throws Exception {
        final File testDirectory = new File("./src/test/java/spoon/reflect/declaration/testclasses/");

        Launcher launcher = new Launcher();
        factory = launcher.getFactory();
        SpoonCompiler compiler = launcher.createCompiler();
        compiler.setDestinationDirectory(new File("./target/spooned/"));
        factory.getEnvironment().setComplianceLevel(8);
        compiler.addInputSource(testDirectory);
        compiler.build();
        compiler.compileInputSources();
    }

    @Test
    public void testGetSuperclass() throws Exception {
        // test superclass of class
        CtType<?> type = this.factory.Type().get(Subclass.class);

        CtTypeReference<?> superclass = type.getSuperclass();
        Assert.assertEquals(ExtendsObject.class.getName(), superclass.getQualifiedName());

//        superclass = superclass.getSuperclass();
//        Assert.assertEquals(Object.class.getName(), superclass.getQualifiedName());

        Assert.assertNull(superclass.getSuperclass());

        // test superclass of interface type reference
        Set<CtTypeReference<?>> superInterfaces = type.getSuperInterfaces();
        Assert.assertEquals(1, superInterfaces.size());
        CtTypeReference<?> superinterface = superInterfaces.iterator().next();
        Assert.assertEquals(Subinterface.class.getName(), superinterface.getQualifiedName());
        Assert.assertNull(superinterface.getSuperclass());

        // test superclass of interface
        type = this.factory.Type().get(Subinterface.class);
        Assert.assertNull(type.getSuperclass());
    }
}