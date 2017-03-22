package spoon.test.refactoring;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.refactoring.testclasses.TestTryRename;
import spoon.test.refactoring.testclasses.CtRenameLocalVariableRefactoringTestSubject;
import spoon.testing.utils.ModelUtils;

public class CtRenameLocalVariableRefactoringTest
{
	@Test
	public void testModelConsistency() throws Throwable {
		//contract: check that all assertions in all methods of the RenameLocalVariableRefactorTestSubject are correct
		new CtRenameLocalVariableRefactoringTestSubject().checkModelConsistency();
	}
	
	/**
	 * If you need to debug behavior of refactoring on the exact method and variable in the {@link CtRenameLocalVariableRefactoringTestSubject} model,
	 * then provide
	 * 1) name of method of {@link CtRenameLocalVariableRefactoringTestSubject}
	 * 2) original name variable in the method
	 * 3) new name of variable in the method
	 * then put breakpoint on the line `this.getClass();` below and the debugger stops just before 
	 * the to be inspected refactoring starts
	 */
	private String[] DEBUG = new String[]{/*"nestedClassMethodWithoutRefs", "var3", "var1"*/};

	/**
	 * The {@link CtRenameLocalVariableRefactoringTestSubject} class is loaded as spoon model. Then:
	 * - It looks for each CtVariable and it's CtAnnotation and tries to rename that variable to the name defined by annotation.
	 * - If the annotation name is prefixed with "-", then that refactoring should fail.
	 * - If the annotation name is not prefixed, then that refactoring should pass.
	 * If it behaves different then expected, then this test fails
	 */
	@Test
	public void testRenameAllLocalVariablesOfRenameTestSubject() throws Exception {
		CtClass<?> varRenameClass = (CtClass<?>)ModelUtils.buildClass(CtRenameLocalVariableRefactoringTestSubject.class);
		CtTypeReference<TestTryRename> tryRename = varRenameClass.getFactory().createCtTypeReference(TestTryRename.class);
		
		varRenameClass.getMethods().forEach(method->{
			//debugging support
			if(DEBUG.length==3 && DEBUG[0].equals(method.getSimpleName())==false) return;
			method.filterChildren((CtVariable var)->true)
				.map((CtVariable var)->var.getAnnotation(tryRename))
				.forEach((CtAnnotation<TestTryRename> annotation)->{
					String[] newNames = annotation.getActualAnnotation().value();
					CtVariable<?> targetVariable = (CtVariable<?>)annotation.getAnnotatedElement();
					for (String newName : newNames) {
						boolean renameShouldPass = newName.startsWith("-")==false;
						if (!renameShouldPass) {
							newName = newName.substring(1);
						}
						if (targetVariable instanceof CtLocalVariable<?>) {
							//debugging support
							if(DEBUG.length==3 && DEBUG[1].equals(targetVariable.getSimpleName()) && DEBUG[2].equals(newName)) {
								//put breakpoint here and continue debugging of the buggy case
								this.getClass();
							}
							checkLocalVariableRename((CtLocalVariable<?>) targetVariable, newName, renameShouldPass);
						} else {
							//TODO test rename of other variables, e.g. parameters and catch... later
						}
					}
				});
		});
	}
	
	protected void checkLocalVariableRename(CtLocalVariable<?> targetVariable, String newName, boolean renameShouldPass) {
		
		String originName = targetVariable.getSimpleName();
		CtRenameLocalVariableRefactoring refactor = new CtRenameLocalVariableRefactoring();
		refactor.setTarget(targetVariable);
		refactor.setNewName(newName);
		if(renameShouldPass) {
			try {
				refactor.refactor();
			} catch(SpoonException e) {
				throw new AssertionError(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" should NOT fail when trying rename to \""+newName+"\"\n"+targetVariable.toString(), e);
			}
			assertEquals(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\" passed, but the name of variable was not changed", newName, targetVariable.getSimpleName());
			assertCorrectModel(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\"");
		} else {
			try {
				refactor.refactor();
				fail(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" should fail when trying rename to \""+newName+"\"");
			} catch(SpoonException e) {
			}
			assertEquals(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" failed when trying rename to \""+newName+"\" but the name of variable should not be changed", originName, targetVariable.getSimpleName());
		}
		if(renameShouldPass) {
			rollback(targetVariable, originName);
		}
		assertEquals(originName, targetVariable.getSimpleName());
	}
	
	private void rollback(CtLocalVariable<?> targetVariable, String originName) {
		String newName = targetVariable.getSimpleName();
		CtRenameLocalVariableRefactoring refactor = new CtRenameLocalVariableRefactoring();
		refactor.setTarget(targetVariable);
		//rollback changes
		refactor.setNewName(originName);
		try {
			refactor.refactor();
		} catch(SpoonException e) {
			throw new AssertionError(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\" passed, but rename back to \""+originName+"\" failed", e);
		}
	}

	private void assertCorrectModel(String refactoringDescription) {
		Launcher launcher = new Launcher();
		File outputBinDirectory = new File("./target/spooned-refactoring-test");
		if (!outputBinDirectory.exists()) {
			outputBinDirectory.mkdirs();
		}
		launcher.setBinaryOutputDirectory(outputBinDirectory);
		launcher.setSourceOutputDirectory(outputBinDirectory);
		
//		 1) print modified model,
		try {
			launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		} catch (Throwable e) {
			new AssertionError("The printing of java sources failed after: "+refactoringDescription, e);
		}
		
//		 2) build it
		try {
//			launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
			launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.CTTYPES);
		} catch (Throwable e) {
			new AssertionError("The compilation of java sources in "+launcher.getEnvironment().getBinaryOutputDirectory()+" failed after: "+refactoringDescription, e);
		}
//		 3) create instance using that new model and test consistency
		try {
//			varRenameClass.newInstance();
			TestClassloader classLoader = new TestClassloader(launcher);
			Class testModelClass = classLoader.loadClass(CtRenameLocalVariableRefactoringTestSubject.class.getName());
			testModelClass.getMethod("checkModelConsistency").invoke(testModelClass.newInstance());
		} catch (InvocationTargetException e) {
			throw new AssertionError("The model validation of code in "+launcher.getEnvironment().getBinaryOutputDirectory()+" failed after: "+refactoringDescription, e.getTargetException());
		} catch (Throwable e) {
			throw new AssertionError("The model validation of code in "+launcher.getEnvironment().getBinaryOutputDirectory()+" failed after: "+refactoringDescription, e);
		}
	}
	
	private class TestClassloader extends URLClassLoader {
		TestClassloader(Launcher launcher) throws MalformedURLException {
			super(new URL[] { new File(launcher.getEnvironment().getBinaryOutputDirectory()).toURL()}, CtRenameLocalVariableRefactoringTest.class.getClassLoader());
		}

		@Override
		public Class<?> loadClass(String s) throws ClassNotFoundException {
			try {
				return findClass(s);
			} catch (Exception e) {
				return super.loadClass(s);
			}
		}
	}
	
	
	private String getParentMethodName(CtElement ele) {
		CtMethod parentMethod = ele.getParent(CtMethod.class);
		CtMethod m;
		while(parentMethod!=null && (m=parentMethod.getParent(CtMethod.class))!=null) {
			parentMethod = m;
		}
		if(parentMethod!=null) {
			return parentMethod.getParent(CtType.class).getSimpleName()+"#"+parentMethod.getSimpleName();
		} else {
			return ele.getParent(CtType.class).getSimpleName()+"#annonymous block";
		}
	}
	

	@Test
	public void testRefactorWrongUsage() throws Exception {
		CtType varRenameClass = ModelUtils.buildClass(CtRenameLocalVariableRefactoringTestSubject.class);
		CtLocalVariable<?> local1Var = varRenameClass.filterChildren((CtLocalVariable<?> var)->var.getSimpleName().equals("local1")).first();
		
		//contract: a target variable is not defined. Throw SpoonException
		CtRenameLocalVariableRefactoring refactor = new CtRenameLocalVariableRefactoring();
		refactor.setNewName("local1");
		try {
			refactor.refactor();
			fail();
		} catch(SpoonException e) {
			//should fail - OK
		}
		//contract: invalid rename request to empty string. Throw SpoonException
		refactor.setTarget(local1Var);
		try {
			refactor.setNewName("");
			fail();
		} catch(SpoonException e) {
			//should fail - OK
		}
		
		//contract: invalid rename request to variable name which contains space. Throw SpoonException
		try {
			refactor.setNewName("x ");
			fail();
		} catch(SpoonException e) {
			//should fail - OK
		}
		
		//contract: invalid rename request to variable name which contains space. Throw SpoonException
		try {
			refactor.setNewName("x y");
			fail();
		} catch(SpoonException e) {
			//should fail - OK
		}
		
		//contract: invalid rename request to variable name which contains character which is not allowed in variable name. Throw SpoonException
		try {
			refactor.setNewName("x(");
			fail();
		} catch(SpoonException e) {
			//should fail - OK
		}
	}

	@Test
	public void testRenameLocalVariableToSameName() throws Exception {
		CtType varRenameClass = ModelUtils.buildClass(CtRenameLocalVariableRefactoringTestSubject.class);
		CtLocalVariable<?> local1Var = varRenameClass.filterChildren((CtLocalVariable<?> var)->var.getSimpleName().equals("local1")).first();
		
		CtRenameLocalVariableRefactoring refactor = new CtRenameLocalVariableRefactoring();
		refactor.setTarget(local1Var);
		refactor.setNewName("local1");
		refactor.refactor();
		assertEquals("local1", local1Var.getSimpleName());
	}
}
