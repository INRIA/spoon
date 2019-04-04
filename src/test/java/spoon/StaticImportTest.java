package spoon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StaticImportTest {

        @Test
        public void testStaticImportISSUE2927() {

            String path = "./src/test/resources/import-static/ShuffleHandler.java";
            Launcher spoon;
            spoon = new Launcher();
            spoon.getEnvironment().setNoClasspath(true);
            spoon.getEnvironment().setSourceClasspath(new String[]{path});
            spoon.addInputResource( path );
            spoon.getEnvironment().setAutoImports(false);
            //spoon.setSourceOutputDirectory("./src/test/java/spoon/StaticImportTest.java");
            spoon.setBinaryOutputDirectory("./target/spoon/static-imports/bin");
            spoon.setSourceOutputDirectory("./target/spoon/static_imports/src");
            spoon.run();

            /*
            final List<CtClass> classes = Query.getElements(spoon.getFactory(), new NamedElementFilter<>(CtClass.class,"ClientClass"));

            final CtType<?> innerClass = classes.get(0).getNestedType("InnerClass");
            String expected = "spoon.test.imports.testclasses.ClientClass.InnerClass";
            assertEquals(expected, innerClass.getReference().toString());

            //test that acces path depends on the context
            //this checks the access path in context of innerClass. The context is defined by CtTypeReference.getParent(CtType.class).
            assertEquals("spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected", innerClass.getSuperclass().toString());
            //this checks the access path in context of SuperClass. The context is defined by CtTypeReference.getParent(CtType.class)
            assertEquals("spoon.test.imports.testclasses.internal.SuperClass.InnerClassProtected", innerClass.getSuperclass().getTypeDeclaration().getReference().toString());
            assertEquals("InnerClassProtected", innerClass.getSuperclass().getSimpleName());


            assertEquals("SuperClass", innerClass.getSuperclass().getDeclaringType().getSimpleName());
            assertEquals(spoon.getFactory().Class().get("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected"), innerClass.getSuperclass().getDeclaration());
            */

            assertEquals(true, false);
        }

}
