package spoon.test.ctType;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.ctType.testclasses.X;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtTypeTest {
	@Test
	public void testHasMethodInDirectMethod() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertTrue(clazz.hasMethod(clazz.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodNotHasMethod() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		CtClass<?> clazz2 = factory.Code().createCodeSnippetStatement(
			"class Y { public void foo2() {} }").compile();
		assertFalse(clazz.hasMethod(clazz2.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodOnNull() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertFalse(clazz.hasMethod(null));
	}

	@Test
	public void testHasMethodInSuperClass() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.run();

		final CtClass<?> xClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.X");
		final CtClass<?> yClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.Y");
		final CtMethod<?> superMethod = xClass.getMethods().iterator().next();

		assertTrue(yClass.hasMethod(superMethod));
	}

	@Test
	public void testHasMethodInDefaultMethod() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.run();

		final CtClass<?> x = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.W");
		final CtInterface<?> z = launcher.getFactory().Interface().get("spoon.test.ctType.testclasses.Z");
		final CtMethod<?> superMethod = z.getMethods().iterator().next();

		assertTrue(x.hasMethod(superMethod));
	}

	@Test
	public void testIsSubTypeOf() throws Exception {
		CtType<X> xCtType = buildClass(X.class);
		CtType<?> yCtType = xCtType.getFactory().Type().get("spoon.test.ctType.testclasses.Y");

		assertFalse(xCtType.isSubtypeOf(yCtType.getReference()));
		assertTrue(yCtType.isSubtypeOf(xCtType.getReference()));
		//contract: x isSubtypeOf x
		//using CtTypeReference implementation
		assertTrue(xCtType.getReference().isSubtypeOf(xCtType.getReference()));
		//using CtType implementation
		assertTrue(xCtType.isSubtypeOf(xCtType.getReference()));
	}
	
	@Test
	public void testIsSubTypeOfonTypeParameters() throws Exception {
		CtType<X> xCtType = buildClass(X.class);
		Factory factory = xCtType.getFactory();

		CtType<?> oCtType = factory.Type().get("spoon.test.ctType.testclasses.O");
		CtType<?> pCtType = factory.Type().get("spoon.test.ctType.testclasses.P");
		CtTypeReference<?> objectCtTypeRef = factory.Type().OBJECT;

		List<CtTypeParameter> oTypeParameters = oCtType.getFormalCtTypeParameters();
		assertTrue(oTypeParameters.size() == 1);
		List<CtTypeParameter> pTypeParameters = pCtType.getFormalCtTypeParameters();
		assertTrue(pTypeParameters.size() == 2);

		CtType<?> O_A_CtType = oTypeParameters.get(0);
		CtType<?> P_D_CtType = pTypeParameters.get(0);
		CtType<?> P_F_CtType = pTypeParameters.get(1);

		CtMethod<?> O_FooMethod = oCtType.filterChildren(new NameFilter<>("foo")).first();
		CtMethod<?> P_FooMethod = pCtType.filterChildren(new NameFilter<>("foo")).first();

		CtType<?> O_B_CtType = O_FooMethod.getType().getDeclaration();
		CtType<?> P_E_CtType = P_FooMethod.getType().getDeclaration();

		assertTrue(O_B_CtType.isSubtypeOf(xCtType.getReference()));
		assertTrue(O_B_CtType.isSubtypeOf(O_A_CtType.getReference()));
		
		assertTrue(P_E_CtType.isSubtypeOf(xCtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(P_D_CtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(O_A_CtType.getReference()));
		
		assertTrue(P_D_CtType.isSubtypeOf(O_A_CtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(O_B_CtType.getReference()));

		assertTrue(P_E_CtType.isSubtypeOf(objectCtTypeRef));
		assertTrue(P_F_CtType.isSubtypeOf(objectCtTypeRef));
	}
	
	@Test
	public void testIsSubTypeOfonTypeReferences() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"-c"});
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/SubtypeModel.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		
		CtType<?> oCtType = factory.Class().get("spoon.test.ctType.testclasses.SubtypeModel");
		CtMethod<?> O_FooMethod = oCtType.filterChildren(new NameFilter<>("foo")).first();

		Map<String, CtTypeReference<?>> nameToTypeRef = new HashMap<>();
		O_FooMethod.filterChildren(new TypeFilter<>(CtLocalVariable.class)).forEach((CtLocalVariable var)->{
			nameToTypeRef.put(var.getSimpleName(), var.getType());
		});
		
		int[] count = new int[1];

		O_FooMethod.filterChildren(new TypeFilter<>(CtAssignment.class)).forEach((CtAssignment ass)->{
			for (CtComment comment : ass.getComments()) {
				checkIsNotSubtype(comment, nameToTypeRef);
				count[0]++;
			};
			count[0]++;
			checkIsSubtype(((CtVariableAccess) ass.getAssigned()).getVariable().getType(), ((CtVariableAccess) ass.getAssignment()).getVariable().getType(), nameToTypeRef);
		});
		
		assertTrue(count[0]>(9*8));
	}

	private void checkIsSubtype(CtTypeReference superType, CtTypeReference subType, Map<String, CtTypeReference<?>> nameToTypeRef) {
		String msg = getTypeName(subType)+" isSubTypeOf "+getTypeName(superType);
		assertTrue(msg, subType.isSubtypeOf(superType));
	}

	private static final Pattern assignment = Pattern.compile("\\s*(\\w+)\\s*=\\s*(\\w+);");
	private void checkIsNotSubtype(CtComment comment, Map<String, CtTypeReference<?>> nameToTypeRef) {
		Matcher m = assignment.matcher(comment.getContent());
		assertTrue(m.matches());
		CtTypeReference<?> superType = nameToTypeRef.get(m.group(1));
		CtTypeReference<?> subType = nameToTypeRef.get(m.group(2));
		String msg = getTypeName(subType)+" is NOT SubTypeOf "+getTypeName(superType);
		assertFalse(msg, subType.isSubtypeOf(superType));
	}
	
	private String getTypeName(CtTypeReference<?> ref) {
		String name;
		CtReference r= ref.getParent(CtReference.class);
		if(r!=null) {
			name = r.getSimpleName();
		} else {
			name = ref.getParent(CtNamedElement.class).getSimpleName();
		}
		return ref.toString()+" "+name;
	}
}
