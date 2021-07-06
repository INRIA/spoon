package spoon.test.template;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.template.*;
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
    public void testSubstituteMethodBodyWithTemplatedInitializer() {
        // contract: Given a block with a templated initializer, substituteMethodBody should return a
        // new block with the initializer replaced with the value bound to the template parameter

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtClass<?> targetClass = factory.Class().create("TargetClass");

        String templateExecutableName = "executable";
        String templateVariableName = "s";
        String initializerToSubstitute = "My chosen initializer";
        CtStatement expectedStatement = factory.createLocalVariable(
                factory.Type().stringType(), templateVariableName, factory.createLiteral(initializerToSubstitute)
        );
        final CtBlock<?> expectedMethodBody = factory.Code().createCtBlock(expectedStatement);

        StatementWithTemplatedInitializer template = new StatementWithTemplatedInitializer();
        template._initializer_ = factory.createLiteral(initializerToSubstitute);

        // act
        CtBlock<?> substitutedMethodBody = Substitution.substituteMethodBody(
                targetClass, template, templateExecutableName
        );

        // assert
        assertEquals(expectedMethodBody, substitutedMethodBody);
    }

    @Test
    public void testSubstituteStatementWithTemplatedInitializer() {
        // contract: Given a statement with a templated initializer, substituteStatement should
        // return a new statement with the initializer replaced with the value bound to the template
        // parameter

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtClass<?> targetClass = factory.Class().create("TargetClass");

        String templateExecutableName = "executable";
        int templateVariableIndex = 0;
        String templateVariableName = "s";
        String initializerToSubstitute = "My chosen initializer";
        CtStatement expectedStatement = factory.createLocalVariable(
                factory.Type().stringType(), templateVariableName, factory.createLiteral(initializerToSubstitute)
        );

        StatementWithTemplatedInitializer template = new StatementWithTemplatedInitializer();
        template._initializer_ = factory.createLiteral(initializerToSubstitute);

        // act
        CtStatement substitutedStatement = Substitution.substituteStatement(
                targetClass, template, templateVariableIndex, templateExecutableName
        );

        // assert
        assertEquals(expectedStatement, substitutedStatement);
    }

    private static class StatementWithTemplatedInitializer extends ExtensionTemplate {
        TemplateParameter<String> _initializer_;

        public void executable() {
            String s = _initializer_.S();
        }
    }

    @Test
    public void testSubstituteFieldDefaultExpressionWithTemplatedInitializer() {
        // contract: Give an expression with a templated initializer, substituteFieldDefaultExpression should
        // return a new expression with the initializer replaced with the value bound to the template parameter

        // arrange
        Factory factory = createFactoryWithTemplates();
        CtType<?> targetType = factory.Class().create("TargetClass");

        String initializerToSubstitute = "My chosen initializer";
        String templateFieldName = "s";
        CtExpression<String> expectedExpression =  factory.createLiteral(initializerToSubstitute);

        FieldWithTemplatedInitializer template = new FieldWithTemplatedInitializer();
        template._initializer_ = factory.createLiteral(initializerToSubstitute);

        // act
        CtExpression<?> substitutedExpression = Substitution.substituteFieldDefaultExpression(
                targetType, template, templateFieldName
        );

        // assert
        assertEquals(expectedExpression, substitutedExpression);
    }

    private static class FieldWithTemplatedInitializer extends ExtensionTemplate {
        TemplateParameter<String> _initializer_ = new CtLiteralImpl<>();
        String s = _initializer_.S();
    }

    private static Factory createFactoryWithTemplates() {
        Launcher spoon = new Launcher();
        spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/SubstitutionTest.java"));
        spoon.buildModel();
        return spoon.getFactory();
    }
}
