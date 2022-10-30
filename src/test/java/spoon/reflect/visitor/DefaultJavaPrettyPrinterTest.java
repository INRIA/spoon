package spoon.reflect.visitor;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.test.GitHubIssue;
import spoon.test.SpoonTestHelpers;
import spoon.testing.utils.ModelTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static spoon.test.SpoonTestHelpers.containsRegexMatch;

public class DefaultJavaPrettyPrinterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1 + 2 + 3",
            "1 + (2 + 3)",
            "1 + 2 + -3",
            "1 + 2 + -(2 + 3)",
            "\"Sum: \" + (1 + 2)",
            "\"Sum: \" + 1 + 2",
            "-(1 + 2 + 3)",
            "true || true && false",
            "(true || false) && false",
            "1 | 2 | 3",
            "1 | (2 | 3)",
            "1 | 2 & 3",
            "(1 | 2) & 3",
            "1 | 2 ^ 3",
            "(1 | 2) ^ 3"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForExpressions(String rawExpression) {
        // contract: When input expressions are minimally parenthesized, pretty-printed output
        // should match the input
        CtExpression<?> expr = createLauncherWithOptimizeParenthesesPrinter()
                .getFactory().createCodeSnippetExpression(rawExpression).compile();
        assertThat(expr.toString(), equalTo(rawExpression));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int sum = 1 + 2 + 3",
            "java.lang.String s = \"Sum: \" + (1 + 2)",
            "java.lang.String s = \"Sum: \" + 1 + 2",
            "java.lang.System.out.println(\"1\" + \"2\" + \"3\" + \"4\")"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForStatements(String rawStatement) {
        // contract: When input expressions as part of statements are minimally parenthesized,
        // pretty-printed output should match the input
        CtStatement statement = createLauncherWithOptimizeParenthesesPrinter()
                .getFactory().createCodeSnippetStatement(rawStatement).compile();
        assertThat(statement.toString(), equalTo(rawStatement));
    }

    private static Launcher createLauncherWithOptimizeParenthesesPrinter() {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setPrettyPrinterCreator(() -> {
            DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(launcher.getEnvironment());
            printer.setMinimizeRoundBrackets(true);
            return printer;
        });
        return launcher;
    }


    @Test
    void testAutoImportPrinterDoesNotImportFunctionalInterfaceTargetedInLambda() {
        // contract: The auto-import printer should not import functional interfaces that are
        // targeted in lambdas, but are not explicitly referenced anywhere
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/target-functional-interface-in-lambda");
        launcher.buildModel();
        CtCompilationUnit cu = launcher.getFactory().Type().get("TargetsFunctionalInterface")
                .getPosition().getCompilationUnit();

        PrettyPrinter autoImportPrettyPrinter = launcher.getEnvironment().createPrettyPrinterAutoImport();
        String output = autoImportPrettyPrinter.prettyprint(cu);

        assertThat(output, not(containsString("import java.util.function.IntFunction;")));
    }

    @Test
    void testLocalTypesPrintedWithoutLeadingDigits() {
        // contract: leading digits should be stripped from simple names when printing
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(16);
        launcher.addInputResource("src/test/resources/localtypes");
        launcher.buildModel();

        CtCompilationUnit cu = launcher.getFactory().Type().get("LocalTypesHolder")
                .getPosition().getCompilationUnit();

        String output = cu.prettyprint();
        // local classes will always have a leading space, without leading digits
        assertThat(output, containsString(" MyClass"));
        assertThat(output, containsString(" MyEnum"));
        assertThat(output, containsString(" MyInterface"));
        // the code does not contain a 1, which would be the prefix of the local types' binary names
        // in the given code snippet
        assertThat(output, not(containsString("1")));
    }

    @Test
    void testEmptyIfBlocksArePrintedWithoutError() {
        // contract: empty if blocks don't crash the DJJP
        Launcher launcher = new Launcher();
        var ctIf = launcher.getFactory().createIf()
                .setThenStatement(launcher.getFactory().createBlock())
                .setElseStatement(launcher.getFactory().createBlock());
        assertDoesNotThrow(() -> ctIf.toString());
    }
    @Test
    void testEmptyIfBlocksWithCommentsArePrintedWithoutError() {
        // contract: empty if blocks don't crash the DJJP
        Launcher launcher = new Launcher();
        CtBlock<Object> thenBlock = launcher.getFactory().createBlock();
        CtBlock<Object> elseBlock = launcher.getFactory().createBlock();
        thenBlock.addComment(launcher.getFactory().createComment().setContent("then"));
        elseBlock.addComment(launcher.getFactory().createComment().setContent("else"));
        var ctIf = launcher.getFactory().createIf()
                .setThenStatement(thenBlock)
                .setElseStatement(elseBlock);
        assertDoesNotThrow(() -> ctIf.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int a = (new int[10])[1];",
            "java.lang.Object b = (new java.lang.Object[6])[4];",
            "double c = (new double[1][1][1])[0][0][0];",
            "boolean d = new boolean[]{ true, false }[0];",
            "float e = (new float[3][])[0][0];", // compiles, but throws a NPE on execution
            "(new byte[8])[3] = 64;",
            "(new byte[8][])[3] = new byte[16];",
    })
    void testPrintNewArrayWithImmediateAccess(String line) {
        // contract: immediate access to a new array must be surrounded by parentheses if
        // no elements are passed directly
        String code = SpoonTestHelpers.wrapLocal(line);
        CtModel model = SpoonTestHelpers.createModelFromString(code, 8);
        CtType<?> first = model.getAllTypes().iterator().next();
        assertThat(first.toString(), containsString(line));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testPrintMethodReferenceTypeParameters() {
        // contract: type parameters of method references are printed
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        CtTypeReference<?> typeReference = factory.Type().createReference("Arrays");
        CtExecutableReferenceExpression exeExpression = factory.createExecutableReferenceExpression();
        exeExpression.setExecutable(factory.createExecutableReference());
        exeExpression.getExecutable().setSimpleName("binarySearch");
        exeExpression.setTarget(factory.createTypeAccess(typeReference));
        // if no type parameters are given, no < and no > should be printed
        assertThat(exeExpression.toString(), not(anyOf(containsString("<"), containsString(">"))));

        exeExpression.getExecutable().addActualTypeArgument(factory.Type().integerType());
        // with type parameters, the < and > should be printed
        assertThat(exeExpression.toString(), allOf(containsString("<"), containsString(">")));
        // the type parameter should appear
        assertThat(exeExpression.toString(), containsString("Integer"));

        exeExpression.getExecutable().addActualTypeArgument(factory.Type().integerType());
        // more than one parameter type should be separated (with .* to allow imports)
        assertThat(exeExpression.toString(), containsRegexMatch("<(.*)Integer, (.*)Integer>"));

        // remove type arguments again, we want to try something else
        exeExpression.getExecutable().setActualTypeArguments(null);
        // we want to have a bit more complex type and construct a fake Comparable<Integer, Comhyperbola<Integer>>
        CtTypeReference<?> complexTypeReference = factory.Type().integerType().getSuperInterfaces().stream()
                .filter(t -> t.getSimpleName().equals("Comparable"))
                .findAny()
                .orElseThrow();
        complexTypeReference.addActualTypeArgument(complexTypeReference.clone().setSimpleName("Comhyperbola"));
        exeExpression.getExecutable().addActualTypeArgument(complexTypeReference);
        assertThat(exeExpression.toString(), containsRegexMatch("<(.*)Comparable<(.*)Integer, (.*)Comhyperbola<(.*)Integer>>>"));
    }

    @ParameterizedTest
    @ArgumentsSource(SealedTypesProvider.class)
    void testPrintSealedTypes(CtType<?> sealedType, List<String> explicitPermitted) {
        // contract: sealed types are printed correctly
        String printed = sealedType.toString();
        // the sealed keyword is always required
        assertThat(printed, containsString("sealed"));
        if (explicitPermitted.isEmpty()) {
            // the permits keyword should only exist if explicit permitted types are printed
            assertThat(printed, not(containsString("permits")));
        } else {
            assertThat(printed, containsString("permits"));
            for (String permitted : explicitPermitted) {
                assertThat(printed, containsRegexMatch("\\s" + permitted));
            }
        }
    }

    @Test
    void testPrintNonSealedTypes() {
        // contract: the non-sealed modifier is printed
        Launcher launcher = new Launcher();
        CtClass<?> ctClass = launcher.getFactory().Class().create("MyClass");
        ctClass.addModifier(ModifierKind.NON_SEALED);
        assertThat(ctClass.toString(), containsString("non-sealed "));
    }

    static class SealedTypesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            Launcher launcher = new Launcher();
            launcher.getEnvironment().setComplianceLevel(17);
            launcher.addInputResource("src/test/resources/sealedclasses");
            launcher.buildModel();
            Factory factory = launcher.getFactory();
            return Stream.of(
                Arguments.of(factory.Type().get("SealedClassWithPermits"), List.of("ExtendingClass", "OtherExtendingClass")),
                Arguments.of(factory.Type().get("SealedInterfaceWithPermits"), List.of("ExtendingClass", "OtherExtendingClass")),
                Arguments.of(factory.Type().get("SealedClassWithNestedSubclasses"), List.of()), // implicit
                Arguments.of(factory.Type().get("SealedInterfaceWithNestedSubclasses"), List.of()) // implicit
            );
        }
    }

    @Nested
    class SquareBracketsForArrayInitialization_ArrayIsBuiltUsingFactoryMethods {
        @GitHubIssue(issueNumber = 4887, fixed = true)
        void bracketsShouldBeAttachedToTypeByDefault() {
            // contract: the square brackets should be attached to type by default when array is built using factory methods
            // arrange
            Launcher launcher = new Launcher();
            Factory factory = launcher.getFactory();

            CtArrayTypeReference<Integer> arrayTypeReference = factory.createArrayTypeReference();
            arrayTypeReference.setComponentType(factory.Type().INTEGER_PRIMITIVE);
            CtNewArray<Integer> newArray = factory.createNewArray();
            newArray.setValueByRole(CtRole.TYPE, arrayTypeReference);
            List<CtLiteral<Integer>> elements = new ArrayList<>(List.of(factory.createLiteral(3)));
            newArray.setValueByRole(CtRole.EXPRESSION, elements);
            CtLocalVariable<Integer> localVariable = factory.createLocalVariable(arrayTypeReference, "intArray", newArray);

            // act
            String actualStringRepresentation = localVariable.toString();

            // assert
            assertThat(actualStringRepresentation, equalTo("int[] intArray = new int[]{ 3 }"));
        }

        @Test
        void bracketsShouldBeAttachedToIdentifierIfSpecified() {
            // contract: the square brackets should be attached to identifier if specified explicitly
            // arrange
            Launcher launcher = new Launcher();
            Factory factory = launcher.getFactory();

            CtArrayTypeReference<CtElement> arrayTypeReference = factory.createArrayTypeReference();
            CtTypeReference<CtElement> arrayType = factory.Type().createReference(CtElement.class);
            arrayTypeReference.setComponentType(arrayType);
            CtNewArray<CtElement> newArray = factory.createNewArray();
            newArray.setValueByRole(CtRole.TYPE, arrayTypeReference);
            List<CtElement> elements = new ArrayList<>(List.of(factory.createLiteral(1.0f)));
            newArray.setValueByRole(CtRole.EXPRESSION, elements);
            CtLocalVariable<CtElement> localVariable = factory.createLocalVariable(arrayTypeReference, "spoonElements", newArray);


            // act
            ((CtArrayTypeReferenceImpl<?>) arrayTypeReference).setDeclarationKind(CtArrayTypeReferenceImpl.DeclarationKind.IDENTIFIER);
            String actualStringRepresentation = localVariable.toString();

            // assert
            assertThat(actualStringRepresentation,
                    equalTo("spoon.reflect.declaration.CtElement spoonElements[] = new spoon.reflect.declaration.CtElement[]{ 1.0F }"));
        }
    }

    @ModelTest(value = "src/test/resources/patternmatching/InstanceofGenerics.java", complianceLevel = 16)
    void testKeepGenericType(Factory factory) {
        // contract: generic type parameters can appear in instanceof expressions if they are only carried over
        CtType<?> x = factory.Type().get("InstanceofGenerics");
        String printed = x.toString();
        assertThat(printed, containsString("Set<T>"));
        assertThat(printed, containsString("List<T> list"));
        assertThat(printed, containsRegexMatch("Collection<.*String>"));
        assertThat(printed, containsRegexMatch("List<.*List<T>>"));
        assertThat(printed, containsRegexMatch("List<.*List<\\? extends T>>"));
        assertThat(printed, containsRegexMatch("List<.*List<\\? super T>>"));
    }
}
