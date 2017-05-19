package spoon.test.refactoring;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.SubInheritanceHierarchyFunction;
import spoon.test.refactoring.parameter.testclasses.IFaceB;
import spoon.test.refactoring.parameter.testclasses.IFaceK;
import spoon.test.refactoring.parameter.testclasses.IFaceL;
import spoon.test.refactoring.parameter.testclasses.TypeA;
import spoon.test.refactoring.parameter.testclasses.TypeB;
import spoon.test.refactoring.parameter.testclasses.TypeC;
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
}
