package spoon.reflect.visitor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by nicolas on 25/02/2015.
 */
@RunWith(Parameterized.class)
public class CtInheritanceScannerTest<T extends CtVisitable> {

	private static Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ CtAnnotation.class, factory.Core().createAnnotation() },
				{ CtAnnotationFieldAccess.class, factory.Core().createAnnotationFieldAccess() },
				{ CtAnnotationType.class, factory.Core().createAnnotationType() },
				{ CtAnonymousExecutable.class, factory.Core().createAnonymousExecutable() },
				{ CtArrayAccess.class, factory.Core().createArrayAccess() },
				{ CtArrayTypeReference.class, factory.Core().createArrayTypeReference() },
				{ CtAssert.class, factory.Core().createAssert() },
				{ CtAssignment.class, factory.Core().createAssignment() },
				{ CtBinaryOperator.class, factory.Core().createBinaryOperator() },
				{ CtBlock.class, factory.Core().createBlock() },
				{ CtBreak.class, factory.Core().createBreak() },
				{ CtCase.class, factory.Core().createCase() },
				{ CtCatch.class, factory.Core().createCatch() },
				{ CtClass.class, factory.Core().createClass() },
				{ CtConditional.class, factory.Core().createConditional() },
				{ CtConstructor.class, factory.Core().createConstructor() },
				{ CtContinue.class, factory.Core().createContinue() },
				{ CtDo.class, factory.Core().createDo() },
				{ CtEnum.class, factory.Core().createEnum() },
				{ CtExecutableReference.class, factory.Core().createExecutableReference() },
				{ CtField.class, factory.Core().createField() },
				{ CtFieldAccess.class, factory.Core().createFieldAccess() },
				{ CtThisAccess.class, factory.Core().createThisAccess() },
				{ CtSuperAccess.class, factory.Core().createSuperAccess() },
				{ CtFieldReference.class, factory.Core().createFieldReference() },
				{ CtFor.class, factory.Core().createFor() },
				{ CtForEach.class, factory.Core().createForEach() },
				{ CtIf.class, factory.Core().createIf() },
				{ CtInterface.class, factory.Core().createInterface() },
				{ CtInvocation.class, factory.Core().createInvocation() },
				{ CtLiteral.class, factory.Core().createLiteral() },
				{ CtLocalVariable.class, factory.Core().createLocalVariable() },
				{ CtLocalVariableReference.class, factory.Core().createLocalVariableReference() },
				{ CtCatchVariable.class, factory.Core().createCatchVariable() },
				{ CtCatchVariableReference.class, factory.Core().createCatchVariableReference() },
				{ CtMethod.class, factory.Core().createMethod() },
				{ CtNewArray.class, factory.Core().createNewArray() },
				{ CtNewClass.class, factory.Core().createNewClass() },
				{ CtOperatorAssignment.class, factory.Core().createOperatorAssignment() },
				{ CtPackage.class, factory.Core().createPackage() },
				{ CtPackageReference.class, factory.Core().createPackageReference() },
				{ CtParameter.class, factory.Core().createParameter() },
				{ CtParameterReference.class, factory.Core().createParameterReference() },
				{ CtReturn.class, factory.Core().createReturn() },
				{ CtStatementList.class, factory.Core().createStatementList() },
				{ CtSwitch.class, factory.Core().createSwitch() },
				{ CtSynchronized.class, factory.Core().createSynchronized() },
				{ CtThrow.class, factory.Core().createThrow() },
				{ CtTry.class, factory.Core().createTry() },
				{ CtTryWithResource.class, factory.Core().createTryWithResource() },
				{ CtTypeParameter.class, factory.Core().createTypeParameter() },
				{ CtTypeParameterReference.class, factory.Core().createTypeParameterReference() },
				{ CtTypeReference.class, factory.Core().createTypeReference() },
				{ CtUnaryOperator.class, factory.Core().createUnaryOperator() },
				{ CtVariableAccess.class, factory.Core().createVariableAccess() },
				{ CtWhile.class, factory.Core().createWhile() },
				{ CtCodeSnippetExpression.class, factory.Core().createCodeSnippetExpression() },
				{ CtCodeSnippetStatement.class, factory.Core().createCodeSnippetStatement() },
				
				
				
		});
	}

	@Parameterized.Parameter(0)
	public Class<T> toTest;

	@Parameterized.Parameter(1)
	public T instance;

	private List<Method> getMethodToInvoke(Class<?> entry) throws Exception {
		Queue<Class> tocheck = new LinkedList<>();
		tocheck.add(entry);

		List<Method> toInvoke = new ArrayList<>();
		while (!tocheck.isEmpty()) {
			Class intf = tocheck.poll();

			Assert.assertTrue(intf.isInterface());
			if (!intf.getSimpleName().startsWith("Ct")) {
				continue;
			}
			Method mth;
			try {
				mth = CtInheritanceScanner.class.getDeclaredMethod("visit" + intf.getSimpleName(), intf);
			} catch (NoSuchMethodException ex) {
				mth = CtInheritanceScanner.class.getDeclaredMethod("scan" + intf.getSimpleName(), intf);
			}
			if (!toInvoke.contains(mth)) {
				toInvoke.add(mth);
			}
			for (Class<?> aClass : intf.getInterfaces()) {
				tocheck.add(aClass);
			}
		}
		return toInvoke;
	}

	/**
	 * A return element is a flow break and a statement
	 */
	@Test
	public void testCtInheritanceScanner() throws Throwable {
		CtInheritanceScanner mocked = mock(CtInheritanceScanner.class);
		List<Method> toInvoke = getMethodToInvoke(toTest);
		for (Method method : toInvoke) {
			method.invoke(Mockito.doCallRealMethod().when(mocked), instance);
		}
		instance.accept(mocked);

		//Collections.reverse(toInvoke);
		for (int i = 0; i < toInvoke.size(); i++) {
			try {
				toInvoke.get(i).invoke(verify(mocked), instance);
			} catch (InvocationTargetException ex) {
				for (Method method : toInvoke) {
					System.err.println(method.getName() + "(e);");
				}
				System.err.println("");
				//				System.err.println("error while verifying index:" + i + " method:" + toInvoke.get(i).toString());
				throw ex;

			}
		}
	}

}
