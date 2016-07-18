package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.printer.sniper.SniperJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.AClass;

import java.util.Arrays;

public class SniperTest {

	public Launcher createSpoon() throws Exception {
	    Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/");
		spoon.getEnvironment().setBuildStackChanges(true);
		spoon.buildModel();
		return spoon;
	}

	private CtFieldRead readEnumValue(Factory factory, Class enumClass, String field) {
		CtFieldRead<Object> fieldRead = factory.createFieldRead();
		CtTypeReference<Object> ctTypeReference = factory.createCtTypeReference(enumClass);
		fieldRead.setTarget(factory.createTypeAccess(ctTypeReference));
		CtFieldReference<Object> fieldReference = factory.createFieldReference();
		fieldReference.setDeclaringType(ctTypeReference);
		fieldReference.setSimpleName(field);
		fieldReference.setStatic(true);
		fieldReference.setFinal(true);
		fieldRead.setVariable(fieldReference);
		return fieldRead;
	}

	@Test
	public void testAddAnnotation() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		factory.Annotation().annotate(aClass.getMethodsByName("param").get(0), Deprecated.class);

		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		String output = sniper.getResult();
		Assert.assertEquals("   @java.lang.Deprecated", output.substring(271, 295));
	}

	@Test
	public void testAddComment() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		aClass.getMethodsByName("param").get(0).addComment(factory.createInlineComment("blabla"));

		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		String output = sniper.getResult();
		Assert.assertEquals("   // blabla", output.substring(271, 283));
	}

	@Test
	public void testPrettyPrinter() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		CtMethod method = factory.Core().createMethod();
		method.setSimpleName("m");
		method.setType(factory.Type().VOID_PRIMITIVE);
		method.addModifier(ModifierKind.PUBLIC);
		method.setBody(factory.Core().createBlock());

		aClass.addMethod(method);

		aClass.setSimpleName("Blabla");
		aClass.removeModifier(ModifierKind.PUBLIC);
		aClass.addModifier(ModifierKind.PRIVATE);
		aClass.addComment(factory.createInlineComment("blabla"));

		CtLocalVariable param = aClass.getMethodsByName("param").get(0).getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);
		Refactoring.changeLocalVariableName(param, "g");

		aClass.getMethod("aMethod").getBody().addStatement(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));

		CtStatement statement = aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0);
		statement.replace(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));
		CtFieldRead ctFieldRead = readEnumValue(factory, CtRole.class, CtRole.LABEL.name());
		aClass.getMethod("aMethodWithGeneric").getBody().addStatement(factory.createLocalVariable(factory.createCtTypeReference(CtRole.class), "d", ctFieldRead));

		aClass.getMethod("aMethodWithGeneric").getBody().addStatement(statement);

		//aClass.getMethod("aMethodWithGeneric").getBody().removeStatement(aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0));
		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		System.out.println(sniper.getResult());
	}
}
