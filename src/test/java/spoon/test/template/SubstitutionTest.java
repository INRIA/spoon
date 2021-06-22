package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.compiler.FileSystemFile;
import spoon.support.reflect.declaration.CtTypeImpl;
import spoon.template.StatementTemplate;
import spoon.template.Substitution;

import java.lang.annotation.Annotation;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstitutionTest {

    @Test
    public void testSubstitutionInsertAllFields() {
        // contract: Substitution.insertAllFields inserts the only field from a single-field template into the target class

        // arrange
        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));

        spoon.buildModel();
        Factory factory = spoon.getFactory();

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
        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));

        spoon.buildModel();
        Factory factory = spoon.getFactory();

        CtType<?> targetType = factory.Class().create("goodClass");
        StatementTemplate template = new SinglyNestedTemplate();

        // act
        Substitution.insertAllNestedTypes(targetType, template);

        // assert
        assertEquals(1, targetType.getNestedTypes().size());
    }

    private static class SinglyNestedTemplate extends StatementTemplate {

        class nestedClass {
        }

        @Override
        public void statement() {
        }
    }
}
