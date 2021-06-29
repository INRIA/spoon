package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.FileSystemFile;
import spoon.template.StatementTemplate;
import spoon.template.Substitution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static Factory createFactoryWithTemplates() {
        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));
        spoon.buildModel();
        return spoon.getFactory();
    }
}
