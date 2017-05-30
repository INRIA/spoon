package spoon.test.refactoring;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.refactoring.CtParameterRemoveRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AllMethodsSameSignatureFunction;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;
import spoon.reflect.visitor.filter.SubInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.refactoring.parameter.testclasses.IFaceB;
import spoon.test.refactoring.parameter.testclasses.IFaceK;
import spoon.test.refactoring.parameter.testclasses.IFaceL;
import spoon.test.refactoring.parameter.testclasses.TestHierarchy;
import spoon.test.refactoring.parameter.testclasses.TypeA;
import spoon.test.refactoring.parameter.testclasses.TypeB;
import spoon.test.refactoring.parameter.testclasses.TypeC;
import spoon.test.refactoring.parameter.testclasses.TypeR;
import spoon.testing.utils.ModelUtils;

public class MethodsRefactoringTest {

	@Test
	public void testSubInheritanceHierarchyFunction() {
		Factory factory = ModelUtils.build(new File("./src/test/java/spoon/test/refactoring/parameter/testclasses"));
		
		List<String> allSubtypes = factory.Class().get(TypeA.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		checkContainsOnly(allSubtypes, 
				"spoon.test.refactoring.parameter.testclasses.TypeB",
				"spoon.test.refactoring.parameter.testclasses.TypeB$1",
				"spoon.test.refactoring.parameter.testclasses.TypeC");

		allSubtypes = factory.Class().get(TypeB.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		checkContainsOnly(allSubtypes, 
				"spoon.test.refactoring.parameter.testclasses.TypeB$1",
				"spoon.test.refactoring.parameter.testclasses.TypeC");
		
		allSubtypes = factory.Class().get(TypeC.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		assertEquals(0, allSubtypes.size());

		allSubtypes = factory.Interface().get(IFaceB.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		checkContainsOnly(allSubtypes, 
				"spoon.test.refactoring.parameter.testclasses.TypeB",
				"spoon.test.refactoring.parameter.testclasses.TypeB$1",
				"spoon.test.refactoring.parameter.testclasses.TypeB$1Local",
				"spoon.test.refactoring.parameter.testclasses.TypeB$2",
				"spoon.test.refactoring.parameter.testclasses.TypeC",
				"spoon.test.refactoring.parameter.testclasses.IFaceL",
				"spoon.test.refactoring.parameter.testclasses.TypeL",
				"spoon.test.refactoring.parameter.testclasses.TypeM"
				);
		
		allSubtypes = factory.Interface().get(IFaceL.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		checkContainsOnly(allSubtypes, 
				"spoon.test.refactoring.parameter.testclasses.TypeB$1Local",
				"spoon.test.refactoring.parameter.testclasses.TypeL",
				"spoon.test.refactoring.parameter.testclasses.TypeM"
				);
		
		allSubtypes = factory.Interface().get(IFaceK.class).map(new SubInheritanceHierarchyFunction()).map((CtType type)->type.getQualifiedName()).list();
		checkContainsOnly(allSubtypes, 
				"spoon.test.refactoring.parameter.testclasses.TypeB$1Local",
				"spoon.test.refactoring.parameter.testclasses.TypeL",
				"spoon.test.refactoring.parameter.testclasses.TypeM",
				"spoon.test.refactoring.parameter.testclasses.TypeK",
				"spoon.test.refactoring.parameter.testclasses.TypeR",
				"spoon.test.refactoring.parameter.testclasses.TypeS"
				);
	}

	private void checkContainsOnly(List<String> foundNames, String... expectedNames) {
		for (String name : expectedNames) {
			assertTrue("The "+name+" not found", foundNames.remove(name));
		}
		assertTrue("Unexpected names found: "+foundNames, foundNames.isEmpty());
	}

	@Test
	public void testAllMethodsSameSignatureFunction() {
		Factory factory = ModelUtils.build(new File("./src/test/java/spoon/test/refactoring/parameter/testclasses"));
		
		//each executable in test classes is marked with a annotation TestHierarchy,
		//which defines the name of the hierarchy where this executable belongs to. 

		//collect all executables which are marked that they belong to hierarchy A_method1
		List<CtExecutable<?>> executablesOfHierarchyA = getExecutablesOfHierarchy(factory, "A_method1");
		//check executables of this hierarchy
		checkMethodHierarchies(executablesOfHierarchyA);

		//collect all executables which are marked that they belong to hierarchy R_method1
		List<CtExecutable<?>> executablesOfHierarchyR = getExecutablesOfHierarchy(factory, "R_method1");
		//check executables of this hierarchy
		checkMethodHierarchies(executablesOfHierarchyR);
		
		//contract: CtConstructor has no other same signature
		CtConstructor<?> constructorTypeA = factory.Class().get(TypeA.class).getConstructors().iterator().next();
		CtExecutable<?> exec = constructorTypeA.map(new AllMethodsSameSignatureFunction()).first();
		assertNull("Unexpected executable found by Constructor of TypeA "+exec, exec);
		CtConstructor<?> constructorTypeB = factory.Class().get(TypeB.class).getConstructors().iterator().next();
		exec = constructorTypeA.map(new AllMethodsSameSignatureFunction()).first();
		assertNull("Unexpected executable found by Constructor of TypeA "+exec, exec);
		//contract: constructor is returned if includingSelf == true
		assertSame(constructorTypeA, constructorTypeA.map(new AllMethodsSameSignatureFunction().includingSelf(true)).first());
	}

	private void checkMethodHierarchies(List<CtExecutable<?>> expectedExecutables) {
		//contract: check that found methods does not depend on the starting point. 
		//The same set of executables has to be found if we start on any of them
		int countOfTestedLambdas = 0;
		int countOfTestedMethods = 0;
		for (CtExecutable<?> ctExecutable : expectedExecutables) {
			if (ctExecutable instanceof CtLambda) {
				countOfTestedLambdas++;
			} else {
				assertTrue(ctExecutable instanceof CtMethod);
				countOfTestedMethods++;
			}
			//start checking of method hierarchy from each expected executable. It must always return same results
			checkMethodHierarchy(expectedExecutables, ctExecutable);
		}
		assertTrue(countOfTestedLambdas>0);
		assertTrue(countOfTestedMethods>0);
	}
	
	private void checkMethodHierarchy(List<CtExecutable<?>> expectedExecutables, CtExecutable startExecutable) {
		//contract: check that by default it does not includes self
		//contract: check that by default it returns lambdas
		{
			final List<CtExecutable<?>> executables = startExecutable.map(new AllMethodsSameSignatureFunction()).list();
			assertFalse("Unexpected start executable "+startExecutable, containsSame(executables, startExecutable));
			//check that some method was found
			assertTrue(executables.size()>0);
			//check that expected methods were found and remove them 
			expectedExecutables.forEach(m->{
				boolean found = removeSame(executables, m);
				if(startExecutable==m) {
					//it is start method. It should not be there
					assertFalse("The signature "+getQSignature(m)+" was returned too", found);
				} else {
					assertTrue("The signature "+getQSignature(m)+" not found", found);
				}
			});
			//check that there is no unexpected executable
			assertTrue("Unexpected executables: "+executables, executables.isEmpty());
		}
		
		//contract: check that includingSelf(true) returns startMethod too
		//contract: check that by default it still returns lambdas
		{
			final List<CtExecutable<?>> executables = startExecutable.map(new AllMethodsSameSignatureFunction().includingSelf(true)).list();
			assertTrue("Missing start executable "+startExecutable, containsSame(executables, startExecutable));
			//check that some method was found
			assertTrue(executables.size()>0);
			//check that expected methods were found and remove them 
			expectedExecutables.forEach(m->{
				assertTrue("The signature "+getQSignature(m)+" not found", removeSame(executables, m));
			});
			//check that there is no unexpected executable
			assertTrue("Unexpected executables: "+executables, executables.isEmpty());
		}
		
		//contract: check that includingLambdas(false) returns no lambda expressions
		{
			final List<CtExecutable<?>> executables = startExecutable.map(new AllMethodsSameSignatureFunction().includingSelf(true).includingLambdas(false)).list();
			if (startExecutable instanceof CtLambda) {
				//lambda must not be returned even if it is first 
				assertFalse("Unexpected start executable "+startExecutable, containsSame(executables, startExecutable));
			} else {
				assertTrue("Missing start executable "+startExecutable, containsSame(executables, startExecutable));
			}
			
			//check that some method was found
			assertTrue(executables.size()>0);
			//check that expected methods were found and remove them 
			expectedExecutables.forEach(m->{
				if(m instanceof CtLambda) {
					//the lambdas are not expected. Do not ask for them
					return;
				}
				assertTrue("The signature "+getQSignature(m)+" not found", removeSame(executables, m));
			});
			//check that there is no unexpected executable or lambda
			assertTrue("Unexepcted executables "+executables, executables.isEmpty());
		}
		//contract: check early termination
		//contract: check that first returned element is the startExecutable itself if includingSelf == true
		CtExecutable<?> exec = startExecutable.map(new AllMethodsSameSignatureFunction().includingSelf(true)).first();
		assertSame(startExecutable, exec);
		//contract: check that first returned element is not the startExecutable itself if includingSelf == false, but some other executable from the expected
		exec = startExecutable.map(new AllMethodsSameSignatureFunction().includingSelf(false)).first();
		assertNotSame(startExecutable, exec);
		assertTrue(containsSame(expectedExecutables, exec));
	}
	
	private String getQSignature(CtExecutable e) {
		if (e instanceof CtMethod<?>) {
			CtMethod<?> m = (CtMethod<?>) e;
			return m.getDeclaringType().getQualifiedName()+"#"+m.getSignature();
		}
		return e.getShortRepresentation();
	}

	private List<CtExecutable<?>> getExecutablesOfHierarchy(Factory factory, String hierarchyName) {
		return factory.getModel().getRootPackage().filterChildren(new TypeFilter(CtExecutable.class)).select((CtExecutable<?> exec)->{
			//detect if found executable belongs to hierarchy 'hierarchyName'
			CtElement ele = exec;
			if (exec instanceof CtLambda) {
				//lambda is marked by annotation on the first statement of the lambda body.
				List<CtStatement> stats = exec.getBody().getStatements();
				if(stats.size()>0) {
					ele = stats.get(0);
				}
			}
			TestHierarchy th = ele.getAnnotation(TestHierarchy.class);
			if (th!=null) {
				return Arrays.asList(th.value()).indexOf(hierarchyName)>=0;
			}
			return false;
		}).list();
	}

	@Test
	public void testExecutableReferenceFilter() {
		Factory factory = ModelUtils.build(new File("./src/test/java/spoon/test/refactoring/parameter/testclasses"));
		
		List<CtExecutable<?>> executables = factory.getModel().getRootPackage().filterChildren((CtExecutable<?> e)->true).list();
		int nrExecRefsTotal = 0;
		//contract check that ExecutableReferenceFilter found CtExecutableReferences of each executable individually 
		for (CtExecutable<?> executable : executables) {
			nrExecRefsTotal += checkExecutableReferenceFilter(factory, Collections.singletonList(executable));
		}
		//contract check that ExecutableReferenceFilter found CtExecutableReferences of all executables together 
		int nrExecRefsTotal2 = checkExecutableReferenceFilter(factory, executables);
		
		assertSame(nrExecRefsTotal, nrExecRefsTotal2);

		//contract check that it found lambdas too
		CtLambda lambda = factory.getModel().getRootPackage().filterChildren((CtLambda<?> e)->true).first();
		assertNotNull(lambda);
		//this test case is quite wild, because there is normally lambda reference in spoon model. So make one lambda reference here:
		CtExecutableReference<?> lambdaRef = lambda.getReference();
		List<CtExecutableReference<?>> refs = lambdaRef.filterChildren(null).select(new ExecutableReferenceFilter(lambda)).list();
		assertEquals(1, refs.size());
		assertSame(lambdaRef, refs.get(0));
	}

	private int checkExecutableReferenceFilter(Factory factory, List<CtExecutable<?>> executables) {
		assertTrue(executables.size()>0);
		ExecutableReferenceFilter execRefFilter = new ExecutableReferenceFilter();
		executables.forEach((CtExecutable<?> e)->execRefFilter.addExecutable(e));
		final List<CtExecutableReference<?>> refs = new ArrayList<>(factory.getModel().getRootPackage().filterChildren(execRefFilter).list());
		int nrExecRefs = refs.size();
		//use different (slower, but straight forward) algorithm to search for all executable references to check if ExecutableReferenceFilter returns correct results
		factory.getModel().getRootPackage().filterChildren((CtExecutableReference er)->{
			return containsSame(executables, er.getDeclaration());
		}).forEach((CtExecutableReference er)->{
			//check that each expected reference was found by ExecutableReferenceFilter and remove it from that list
			assertTrue("Executable reference: "+er+" not found.", refs.remove(er));
		});
		//check that no other reference was found by ExecutableReferenceFilter
		assertSame(0, refs.size());
		return nrExecRefs;
	}
	
	private boolean containsSame(Collection list, Object item) {
		for (Object object : list) {
			if(object==item) {
				return true;
			}
		}
		return false;
	}
	private boolean removeSame(Collection list, Object item) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object object = (Object) iter.next();
			if(object==item) {
				iter.remove();
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void testCtParameterRemoveRefactoring() throws FileNotFoundException {
		String testPackagePath = "spoon/test/refactoring/parameter/testclasses";
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSource(SpoonResourceHelper.createResource(new File("./src/test/java/"+testPackagePath)));
		comp.build();
		Factory factory = comp.getFactory();
		
		CtType<?> typeA = factory.Class().get(TypeA.class);
		
		CtMethod<?> methodTypeA_method1 = typeA.getMethodsByName("method1").get(0);
		CtParameterRemoveRefactoring refactor = new CtParameterRemoveRefactoring();
		refactor.setTarget(methodTypeA_method1.getParameters().get(0));
		//check that expected methods are targets of refactoring
		List<CtExecutable<?>> execs = refactor.getTargetExecutables();
		execs.forEach(exec->{
			//check that each to be modified method has one parameter
			assertEquals(1, exec.getParameters().size());
		});
		refactor.refactor();
		execs.forEach(exec->{
			//check that each to be modified method has no parameter after refactoring
			assertEquals(0, exec.getParameters().size());
		});
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt("./target/spooned/"+testPackagePath, 8);
	}
	@Test
	public void testCtParameterRemoveRefactoringValidationCheck() throws FileNotFoundException {
		String testPackagePath = "spoon/test/refactoring/parameter/testclasses";
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSource(SpoonResourceHelper.createResource(new File("./src/test/java/"+testPackagePath)));
		comp.build();
		Factory factory = comp.getFactory();
		
		CtType<?> typeR = factory.Class().get(TypeR.class);
		
		CtMethod<?> methodTypeR_method1 = typeR.getMethodsByName("method1").get(0);
		CtParameterRemoveRefactoring refactor = new CtParameterRemoveRefactoring().setTarget(methodTypeR_method1.getParameters().get(0));
		refactor.setTarget(methodTypeR_method1.getParameters().get(0));
		//check that each to be refactored method has one parameter
		List<CtExecutable<?>> execs = refactor.getTargetExecutables();
		execs.forEach(exec->{
			//check that each to be modified method has one parameter
			assertEquals(1, exec.getParameters().size());
		});
		//try refactor
		try {
			refactor.refactor();
			fail();
		} catch (RefactoringException e) {
			this.getClass();
		}
	}
}
