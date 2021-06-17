package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.template.StatementTemplate;
import spoon.template.Substitution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstitutionTest {

    public class SingleFieldTemplate extends StatementTemplate {
        String testString = "goodName";

        @Override
        public void statement() { }
    }

    @Test
    public void testSubstitutionInsertAllFields() {
        // contract: Substitution.insertAllFields inserts the only field from a single-field template into the target class

        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));

        spoon.buildModel();
        Factory factory = spoon.getFactory();

        CtType<?> targetType = factory.Class().create("someClass");
        StatementTemplate template = new SingleFieldTemplate();

        Substitution.insertAllFields(targetType, template);

        List<String> expectedFieldNames = Collections.singletonList("testString");
        List<String> actualFieldNames = targetType.getFields().stream()
                .map(CtField::getSimpleName)
                .collect(Collectors.toList());
        // verifies that the field has correct name
        assertEquals(expectedFieldNames, actualFieldNames);

        List<String> expectedFieldAssignments = Collections.singletonList("\"goodName\"");
        List<String> actualFieldAssignments = new ArrayList<>();
        for (CtField<?> ctField : targetType.getFields()) {
            CtExpression<?> assignment = ctField.getAssignment();
            actualFieldAssignments.add(String.valueOf(assignment));
        }
        // verifies that the field has correct assignment
        assertEquals(expectedFieldAssignments, actualFieldAssignments);
    }
}