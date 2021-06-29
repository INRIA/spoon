package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.template.StatementTemplate;
import spoon.template.Substitution;
import spoon.test.template.testclasses.types.AClassModel;
import spoon.test.template.testclasses.types.AnEnumModel;
import spoon.test.template.testclasses.types.AnIfaceModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SubstitutionTest {

    @Test
    public void testSubstitutionInsertAllFields() {
        // contract: Substitution.insertAllFields inserts the only field from a single-field template into the target class

        // arrange
        Factory factory = createFactoryWithTemplates();

        CtField<String> expectedField = factory.createField();
        expectedField.setSimpleName("testString");
        expectedField.setAssignment(factory.createLiteral("goodName"));
        expectedField.setType(factory.Type().stringType());

        CtType<?> targetType = factory.Class().create("someClass");
        StatementTemplate template = new SingleFieldTemplate();

        // act
        Substitution.insertAllFields(targetType, template);

        // assert
        assertEquals(Collections.singletonList(expectedField), targetType.getFields());
    }

    private static class SingleFieldTemplate extends StatementTemplate {
        String testString = "goodName";

        @Override
        public void statement() { }
    }

    @Test
    public void testInsertAllNestedTypes() {
        // contract: Substitution.insertAllNestedTypes inserts the only nested class from a singly nested template into the target class

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtType<?> targetType = factory.Class().create("goodClass");
        StatementTemplate template = new SinglyNestedTemplate();

        // act
        Substitution.insertAllNestedTypes(targetType, template);

        // assert
        assertEquals(1, targetType.getNestedTypes().size());
        assertEquals("nestedClass", targetType.getNestedType("nestedClass").getSimpleName());
    }

    private static class SinglyNestedTemplate extends StatementTemplate {

        class nestedClass {
        }

        @Override
        public void statement() {
        }
    }

    @Test
    public void testInsertAllConstructor() {
        // contract: Substitution.insertAllConstructor inserts the only constructor from a single constructor template into the target class

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtType<?> targetType = factory.Class().create("goodClass");
        StatementTemplate template = new SingleConstructorTemplate();

        // act
        Substitution.insertAllConstructors(targetType, template);
        List<?> typeMembers = targetType.getTypeMembers();

        // assert
        assertEquals(1, typeMembers.size());
        assertTrue(typeMembers.get(0) instanceof CtConstructor);
    }

    private static class SingleConstructorTemplate extends StatementTemplate {

        public SingleConstructorTemplate() { }

        @Override
        public void statement() {
        }
    }

    @Test
    public void testInsertAllSuperInterfaces() {
        // contract: Substitution.insertAllSuperInterfaces inserts the only superInterface from a single interface implementing template into the target class

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtType<?> targetType = factory.Class().create("goodClass");
        StatementTemplate template = new SingleInterfaceImplementingTemplate();

        // act
        Substitution.insertAllSuperInterfaces(targetType, template);
        List<CtTypeReference> superInterfaces = new ArrayList<>(targetType.getSuperInterfaces());

        // assert
        assertEquals(1, superInterfaces.size());
        assertTrue(superInterfaces.get(0).isInterface());
        assertEquals("A", superInterfaces.get(0).getSimpleName());
    }

    private static class SingleInterfaceImplementingTemplate extends StatementTemplate implements A {

        @Override
        public void statement() { }
    }

    private interface A { }

    @Test
    public void testCreateTypeFromTemplate() {
        //contract: the Substitution API provides a method createTypeFromTemplate
        final Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput" });
        launcher.addTemplateResource(new FileSystemFolder("./src/test/java/spoon/test/template/testclasses/types"));

        launcher.buildModel();
        Factory factory = launcher.getFactory();

        Map<String, Object> parameters = new HashMap<>();
        //replace someMethod with genMethod
        parameters.put("someMethod", "genMethod");

        //contract: we can generate interface with createTypeFromTemplate
        final CtType<?> aIfaceModel = launcher.getFactory().Templates().Interface().get(AnIfaceModel.class);
        CtType<?> genIface = Substitution.createTypeFromTemplate("generated.GenIface", aIfaceModel, parameters);
        assertNotNull(genIface);
        CtMethod<?> generatedIfaceMethod = genIface.getMethod("genMethod");
        assertNotNull(generatedIfaceMethod);
        assertNull(genIface.getMethod("someMethod"));

        //add new substitution request - replace AnIfaceModel by GenIface
        parameters.put("AnIfaceModel", genIface.getReference());
        //contract: we can generate class
        final CtType<?> aClassModel = launcher.getFactory().Class().get(AClassModel.class);
        CtType<?> genClass = Substitution.createTypeFromTemplate("generated.GenClass", aClassModel, parameters);
        assertNotNull(genClass);
        assertSame(genClass, factory.Type().get("generated.GenClass"));
        CtMethod<?> generatedClassMethod = genClass.getMethod("genMethod");
        assertNotNull(generatedClassMethod);
        assertNull(genClass.getMethod("someMethod"));
        assertNotSame(generatedIfaceMethod, generatedClassMethod);
        assertTrue(generatedClassMethod.isOverriding(generatedIfaceMethod));

        //contract: we can generate enum
        parameters.put("case1", "GOOD");
        parameters.put("case2", "BETTER");
        final CtType<?> aEnumModel = launcher.getFactory().Type().get(AnEnumModel.class);
        CtEnum<?> genEnum = (CtEnum<?>) Substitution.createTypeFromTemplate("generated.GenEnum", aEnumModel, parameters);
        assertNotNull(genEnum);
        assertSame(genEnum, factory.Type().get("generated.GenEnum"));
        assertEquals(2, genEnum.getEnumValues().size());
        assertEquals("GOOD", genEnum.getEnumValues().get(0).getSimpleName());
        assertEquals("BETTER", genEnum.getEnumValues().get(1).getSimpleName());
    }

    @Test
    public void testInsertAllMethods() {
        Factory factory = createFactoryWithTemplates();

        CtClass<?> testSimpleTpl = factory.Class().create("TestSimpleTpl");
        //whitespace seems wrong here
        new SimpleTemplate().apply(testSimpleTpl);

        Set<CtMethod<?>> listMethods = testSimpleTpl.getMethods();
        assertEquals(0, testSimpleTpl.getMethodsByName("apply").size());
        assertEquals(1, listMethods.size());
    }

    /**
     * Created by urli on 31/05/2017.
     */
    private static class SimpleTemplate extends StatementTemplate {

        @Override
        public CtClass apply(CtType targetType) {
            Substitution.insertAll(targetType, this);

            return (CtClass) targetType;
        }

        @Override
        public void statement()  { }
    }

    private static Factory createFactoryWithTemplates() {
        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));
        spoon.buildModel();
        return spoon.getFactory();
    }
}
