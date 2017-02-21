package spoon.test.refactoring;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.refactoring.ChangeLocalVariableName;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.refactoring.testclasses.TryRename;
import spoon.test.refactoring.testclasses.VariableRename;
import spoon.testing.utils.ModelUtils;

public class ChangeVariableNameTest
{
	Launcher launcher;
	Factory factory;
	CtClass<VariableRename> varRenameClass;
	CtTypeReference<TryRename> tryRename;
	CtLocalVariable<?> local1Var;

	@Before
	public void setup() throws Exception {
		varRenameClass = (CtClass<VariableRename>)ModelUtils.buildClass(VariableRename.class);
		launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/refactoring/testclasses/VariableRename.java");
		File outputBinDirectory = new File("./target/spooned-refactoring-test");
		if (!outputBinDirectory.exists()) {
			outputBinDirectory.mkdirs();
		}
		launcher.setBinaryOutputDirectory(outputBinDirectory);
		launcher.setSourceOutputDirectory(outputBinDirectory);
		launcher.buildModel();
		factory = launcher.getFactory();
		varRenameClass = factory.Class().get(VariableRename.class);
		tryRename = factory.createCtTypeReference(TryRename.class);
		local1Var = varRenameClass.filterChildren((CtLocalVariable<?> var)->var.getSimpleName().equals("local1")).first();
	}

	@Test
	public void testModelConsistency() throws Throwable {
		new VariableRename();
	}
	
	String[] DEBUG = new String[]{/*"nestedClassMethodWithoutRefs", "var3", "var1"*/};

	@Test
	public void testRenameLocalVariableToUsedName() throws Exception {
		
		varRenameClass.getMethods().forEach(method->{
			//debugging support
			if(DEBUG.length==3 && DEBUG[0].equals(method.getSimpleName())==false) return;
			method.filterChildren((CtVariable var)->true)
				.map((CtVariable var)->var.getAnnotation(tryRename))
				.forEach((CtAnnotation<TryRename> annotation)->{
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
							//TODO rename of other variables
						}
					}
				});
		});
	}
	
	protected void checkLocalVariableRename(CtLocalVariable<?> targetVariable, String newName, boolean renameShouldPass) {
		
		String originName = targetVariable.getSimpleName();
		ChangeLocalVariableName refactor = new ChangeLocalVariableName();
		refactor.setTarget(targetVariable);
		refactor.setNewName(newName);
		if(renameShouldPass) {
			try {
				refactor.refactor();
			} catch(SpoonException e) {
				throw new AssertionError(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" should NOT fail when trying rename to \""+newName+"\"\n"+targetVariable.toString(), e);
			}
			assertEquals(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\" passed, but the name of variable was not changed", newName, targetVariable.getSimpleName());
			printModelAndTestConsistency(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\"");
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
		ChangeLocalVariableName refactor = new ChangeLocalVariableName();
		refactor.setTarget(targetVariable);
		//rollback changes
		refactor.setNewName(originName);
		try {
			refactor.refactor();
		} catch(SpoonException e) {
			throw new AssertionError(getParentMethodName(targetVariable)+" Rename of \""+originName+"\" to \""+newName+"\" passed, but rename back to \""+originName+"\" failed", e);
		}
	}

	private void printModelAndTestConsistency(String refactoringDescription) {
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
			TestClassloader classLoader = new TestClassloader();
			Class testModelClass = classLoader.loadClass(VariableRename.class.getName());
			testModelClass.newInstance();
		} catch (Throwable e) {
			throw new AssertionError("The model validation of code in "+launcher.getEnvironment().getBinaryOutputDirectory()+" failed after: "+refactoringDescription, e);
		}
	}
	
	private class TestClassloader extends URLClassLoader {
		TestClassloader() throws MalformedURLException {
			super(new URL[] { new File(launcher.getEnvironment().getBinaryOutputDirectory()).toURL()}, ChangeVariableNameTest.class.getClassLoader());
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
	public void testRefactorWithoutTarget() throws Exception {
		
		ChangeLocalVariableName refactor = new ChangeLocalVariableName();
		refactor.setNewName("local1");
		try {
			refactor.refactor();
			fail();
		} catch(SpoonException e) {
			
		}
	}

	@Test
	public void testRenameLocalVariableToSameName() throws Exception {
		
		ChangeLocalVariableName refactor = new ChangeLocalVariableName();
		refactor.setTarget(local1Var);
		refactor.setNewName("local1");
		refactor.refactor();
		assertEquals("local1", local1Var.getSimpleName());
	}
	
	@Test
	public void testRenameLocalVariableToInvalidName() throws Exception {
		
		ChangeLocalVariableName refactor = new ChangeLocalVariableName();
		refactor.setTarget(local1Var);
		try {
			refactor.setNewName("");
			fail();
		} catch(SpoonException e) {
		}
		
		try {
			refactor.setNewName("x ");
			fail();
		} catch(SpoonException e) {
		}
		
		try {
			refactor.setNewName("x y");
			fail();
		} catch(SpoonException e) {
		}
		
		try {
			refactor.setNewName("x(");
			fail();
		} catch(SpoonException e) {
		}
	}		
}
