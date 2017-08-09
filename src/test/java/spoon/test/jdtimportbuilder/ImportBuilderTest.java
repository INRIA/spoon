package spoon.test.jdtimportbuilder;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.imports.testclasses.A;
import spoon.test.imports.testclasses.ClassWithInvocation;
import spoon.test.imports.testclasses.Tacos;

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 09/08/2017.
 */
public class ImportBuilderTest {

    @Test
    public void testWithNoImport() {
        // contract: when the source code has no import, none is created when building model
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/A.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(A.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testWithSimpleImport() {
        // contract: when the source has one import, the same import is created as a reference in auto-import mode
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        Set<CtReference> imports = unitA.getImports();

        assertEquals(1, imports.size());

        CtReference ref = imports.iterator().next();
        assertEquals("TypeAnnotation", ref.getSimpleName());
        assertTrue(ref instanceof CtTypeReference);

        CtTypeReference refType = (CtTypeReference)ref;
        assertEquals("sun.reflect.annotation.TypeAnnotation", refType.getQualifiedName());
    }

    @Test
    public void testWithSimpleImportNoAutoimport() {
        // contract: when the source has one import, nothing is imported when not in autoimport
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
        spoon.getEnvironment().setAutoImports(false);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testInternalImportWhenNoClasspath() {
        // contract: in no-classpath anything which is not loaded cannot be imported, even if original source code has imports
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/resources/noclasspath/Attachment.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get("it.feio.android.omninotes.models.Attachment");
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testWithStaticStarredImportFromInterface() {
        // contract: when a starred static import is used with a target interface, all fields/methods should be imported
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/Tacos.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(Tacos.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        Set<CtReference> imports = unitA.getImports();

        assertEquals(2, imports.size());

        Iterator<CtReference> iterator = imports.iterator();
        CtReference firstRef = iterator.next();
        CtReference secondRef = iterator.next();

        assertTrue(firstRef instanceof CtFieldReference);
        assertTrue(secondRef instanceof CtExecutableReference);

        assertEquals("CONSTANT", firstRef.getSimpleName());
        assertEquals("spoon.test.imports.testclasses.internal4.Foo", ((CtFieldReference) firstRef).getFieldDeclaration().getType().getQualifiedName());

        assertEquals("mamethode", secondRef.getSimpleName());
    }
}
