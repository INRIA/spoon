/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.position;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.sniper.internal.SourceFragment;
import spoon.support.sniper.internal.CollectionSourceFragment;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.test.position.testclasses.AnnonymousClassNewIface;
import spoon.test.position.testclasses.FooField;
import spoon.test.position.testclasses.FooSourceFragments;
import spoon.test.position.testclasses.NewArrayList;
import spoon.testing.utils.ModelUtils;

public class TestSourceFragment {

	@Test
	public void testSourcePositionFragment() {
		SourcePosition sp = new SourcePositionImpl(DUMMY_COMPILATION_UNIT, 10, 20, null);
		ElementSourceFragment sf = new ElementSourceFragment(() -> sp, null);
		assertEquals(10, sf.getStart());
		assertEquals(21, sf.getEnd());
		assertSame(sp, sf.getSourcePosition());
		assertNull(sf.getFirstChild());
		assertNull(sf.getNextSibling());
	}

	@Test
	public void testSourceFragmentAddChild() {
		//contract: check build of the tree of SourceFragments
		ElementSourceFragment rootFragment = createFragment(10, 20);
		ElementSourceFragment f;
		//add child
		assertSame(rootFragment, rootFragment.add(f = createFragment(10, 15)));
		assertSame(rootFragment.getFirstChild(), f);
		
		//add child which is next sibling of first child
		assertSame(rootFragment, rootFragment.add(f = createFragment(15, 20)));
		assertSame(rootFragment.getFirstChild().getNextSibling(), f);
		
		//add another child of same start/end, which has to be child of last child
		assertSame(rootFragment, rootFragment.add(f = createFragment(15, 20)));
		assertSame(rootFragment.getFirstChild().getNextSibling().getFirstChild(), f);

		//add another child of smaller start/end, which has to be child of last child
		assertSame(rootFragment, rootFragment.add(f = createFragment(16, 20)));
		assertSame(rootFragment.getFirstChild().getNextSibling().getFirstChild().getFirstChild(), f);

		//add next sibling of root element
		assertSame(rootFragment, rootFragment.add(f = createFragment(20, 100)));
		assertSame(rootFragment.getNextSibling(), f);
		
		//add prev sibling of root element. We should get new root
		f = createFragment(5, 10);
		assertSame(f, rootFragment.add(f));
		assertSame(f.getNextSibling(), rootFragment);
	}

	@Test
	public void testSourceFragmentAddChildBeforeOrAfter() {
		//contract: start / end of root fragment is moved when child is added
		ElementSourceFragment rootFragment = createFragment(10, 20);
		rootFragment.addChild(createFragment(5, 7));
		assertEquals(5, rootFragment.getStart());
		assertEquals(20, rootFragment.getEnd());
		rootFragment.addChild(createFragment(20, 25));
		assertEquals(5, rootFragment.getStart());
		assertEquals(25, rootFragment.getEnd());
	}

	@Test
	public void testSourceFragmentWrapChild() {
		//contract: the existing child fragment can be wrapped by a new parent 
		ElementSourceFragment rootFragment = createFragment(0, 100);
		ElementSourceFragment child = createFragment(50, 60);
		rootFragment.add(child);
		
		ElementSourceFragment childWrapper = createFragment(40, 60);
		rootFragment.add(childWrapper);
		assertSame(rootFragment.getFirstChild(), childWrapper);
		assertSame(rootFragment.getFirstChild().getFirstChild(), child);
	}

	@Test
	public void testSourceFragmentWrapChildrenAndSiblings() {
		//contract: the two SourceFragment trees merge correctly together 
		ElementSourceFragment rootFragment = createFragment(0, 100);
		ElementSourceFragment child = createFragment(50, 60);
		rootFragment.add(child);
		
		ElementSourceFragment childWrapper = createFragment(40, 70);
		ElementSourceFragment childA = createFragment(40, 50);
		ElementSourceFragment childB = createFragment(50, 55);
		ElementSourceFragment childC = createFragment(60, 65);
		ElementSourceFragment childD = createFragment(65, 70);
		//add all children and check the root is still childWrapper
		assertSame(childWrapper, childWrapper.add(childA).add(childB).add(childC).add(childD));
		//add childWrapper which has to merge with before added child, because childWrapper is parent of child
		rootFragment.add(childWrapper);
		assertSame(rootFragment.getFirstChild(), childWrapper);
		assertSame(childA, childWrapper.getFirstChild());
		assertSame(child, childA.getNextSibling());
		assertSame(childB, child.getFirstChild());
		assertSame(childC, child.getNextSibling());
		assertSame(childD, childC.getNextSibling());
	}

	@Test
	public void testElementSourceFragment_getSourceFragmentOf() {
		//contract: ElementSourceFragment#getSourceFragmentOf returns expected results
		ElementSourceFragment rootFragment = createFragment(0, 100);
		ElementSourceFragment x;
		rootFragment.add(createFragment(50, 60));
		rootFragment.add(createFragment(60, 70));
		rootFragment.add(x = createFragment(50, 55));
		
		assertSame(x, rootFragment.getSourceFragmentOf(null, 50, 55));
		assertSame(rootFragment, rootFragment.getSourceFragmentOf(null, 0, 100));
		assertSame(rootFragment.getFirstChild(), rootFragment.getSourceFragmentOf(null, 50, 60));
		assertSame(rootFragment.getFirstChild().getNextSibling(), rootFragment.getSourceFragmentOf(null, 60, 70));
	}

	private static final CompilationUnit DUMMY_COMPILATION_UNIT = new CompilationUnitImpl();
	
	private ElementSourceFragment createFragment(int start, int end) {
		SourcePosition sp = new SourcePositionImpl(DUMMY_COMPILATION_UNIT, start, end - 1, null);
		return new ElementSourceFragment(() -> sp, null);
	}

	@Test
	public void testExactSourceFragments() throws Exception {
		//contract: SourceFragments of some tricky sources are as expected
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setCommentEnabled(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + FooSourceFragments.class.getName().replace('.', '/') + ".java"));
		comp.build();
		Factory f = comp.getFactory();
		
		final CtType<?> foo = f.Type().get(FooSourceFragments.class);

		// contract: the fragment returned by getOriginalSourceFragment are correct
		checkElementFragments(foo.getMethodsByName("m1").get(0).getBody().getStatement(0),
				"if", "(", "x > 0", ")", "{this.getClass();}", "else", "{/*empty*/}");
		checkElementFragments(foo.getMethodsByName("m2").get(0).getBody().getStatement(0),
				"/*c0*/", " ", "if", "  ", "/*c1*/", "\t", "(", " ", "//c2", "\n\t\t\t\t", "x > 0", " ", "/*c3*/", " ", ")", " ", "/*c4*/", " ", "{ \n" + 
						"			this.getClass();\n" + 
						"		}", " ", "/*c5*/ else /*c6*/ {\n" + 
						"			/*empty*/\n" + 
						"		}", " ", "/*c7*/");
		checkElementFragments(foo.getMethodsByName("m3").get(0),
				"/**\n" + 
				"	 * c0\n" + 
				"	 */", 
				group("\n\t", "public", "\n\t", "@Deprecated", " ", "//c1 ends with tab and space\t ", "\n\t", "static"), " ", "/*c2*/", " ",
				"<", group("T", ",", " ", "U"), ">",
				" ", "T", " ", "m3", "(", group("U param", ",", " ", "@Deprecated int p2"), ")", " ", "{\n" + 
						"		return null;\n" + 
						"	}");
		checkElementFragments(foo.getMethodsByName("m4").get(0).getBody().getStatement(0),"label",":"," ", "while", "(", "true", ")", ";");

		checkElementFragments(foo.getMethodsByName("m5").get(0).getBody().getStatement(0),"f", " ", "=", " ", "7.2", ";");
		checkElementFragments(((CtAssignment)foo.getMethodsByName("m5").get(0).getBody().getStatement(0)).getAssignment(),"7.2");
				 
	}
	
	@Test
	public void testSourceFragmentsOfCompilationUnit() throws Exception {
		//contract: SourceFragments of compilation unit children like, package declaration, imports, types
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setCommentEnabled(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + FooSourceFragments.class.getName().replace('.', '/') + ".java"));
		comp.build();
		Factory f = comp.getFactory();
		
		final CtType<?> foo = f.Type().get(FooSourceFragments.class);
		CtCompilationUnit compilationUnit = foo.getPosition().getCompilationUnit();
		
		ElementSourceFragment fragment = compilationUnit.getOriginalSourceFragment();
		List<SourceFragment> children = fragment.getChildrenFragments();
		assertEquals(11, children.size());
		assertEquals("/**\n" + 
				" * Javadoc at top of file\n" + 
				" */", children.get(0).getSourceCode());
		assertEquals("\n", children.get(1).getSourceCode());
		assertEquals("/* comment before package declaration*/\n" + 
				"package spoon.test.position.testclasses;", children.get(2).getSourceCode());
		assertEquals("\n\n", children.get(3).getSourceCode());
		assertEquals("/*\n" + 
				" * Comment before import\n" + 
				" */\n" + 
				"import java.lang.Deprecated;", children.get(4).getSourceCode());
		assertEquals("\n\n", children.get(5).getSourceCode());
		assertEquals("import java.lang.Class;", children.get(6).getSourceCode());
		assertEquals("\n\n", children.get(7).getSourceCode());
		assertTrue(((ElementSourceFragment) children.get(8)).getElement() instanceof CtClass);
		assertStartsWith("/*\n" + 
				" * Comment before type\n" + 
				" */\n" + 
				"public class FooSourceFragments", children.get(8).getSourceCode());
		assertEndsWith("//after last type member\n}", children.get(8).getSourceCode());
		assertEquals("\n\n", children.get(9).getSourceCode());
		assertEquals("//comment at the end of file", children.get(10).getSourceCode());
	}
	

	private void assertStartsWith(String expectedPrefix, String real) {
		assertEquals(expectedPrefix, real.substring(0, Math.min(expectedPrefix.length(), real.length())));
	}

	private void assertEndsWith(String expectedSuffix, String real) {
		int len = real.length();
		assertEquals(expectedSuffix, real.substring(Math.max(0, len - expectedSuffix.length()), len));
	}

	@Test
	public void testSourceFragmentsOfFieldAccess() throws Exception {
		//contract: SourceFragments of field access are as expected
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setCommentEnabled(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + FooField.class.getName().replace('.', '/') + ".java"));
		comp.build();
		Factory f = comp.getFactory();
		
		final CtType<?> foo = f.Type().get(FooField.class);
		
		CtAssignment<?, ?> assignment =  (CtAssignment<?, ?>) foo.getMethodsByName("m").get(0).getBody().getStatements().get(0);
		CtFieldWrite<?> fieldWrite = (CtFieldWrite<?>) assignment.getAssigned();
		CtFieldReference<?> fieldRef = fieldWrite.getVariable();
		CtFieldRead<?> fieldRead = (CtFieldRead<?>) fieldWrite.getTarget();
		CtFieldReference<?> fieldRef2 = fieldRead.getVariable();
		
		ElementSourceFragment fieldWriteSF = fieldWrite.getOriginalSourceFragment();
		List<SourceFragment> children = fieldWriteSF.getChildrenFragments();
		assertEquals(3, children.size());
		assertEquals("FooField.f", children.get(0).getSourceCode());
		assertEquals(".", children.get(1).getSourceCode());
		assertEquals("field2", children.get(2).getSourceCode());

		List<SourceFragment> children2 = ((ElementSourceFragment) children.get(0)).getChildrenFragments();
		assertEquals(3, children2.size());
		assertEquals("FooField", children2.get(0).getSourceCode());
		assertEquals(".", children2.get(1).getSourceCode());
		assertEquals("f", children2.get(2).getSourceCode());
	}

	@Test
	public void testSourceFragmentsOfNewArrayList() throws Exception {
		//contract: SourceFragments of constructor call is as expected
		final CtType<?> type = ModelUtils.buildClass(NewArrayList.class);
		
		CtConstructorCall<?> constCall =  (CtConstructorCall<?>) type.getMethodsByName("m").get(0).getBody().getStatements().get(0);
		//new ArrayList<>();
		ElementSourceFragment constCallSF = constCall.getOriginalSourceFragment();
		List<SourceFragment> children = constCallSF.getChildrenFragments();
		assertEquals(6, children.size());
		assertEquals("new", children.get(0).getSourceCode());
		assertEquals(" ", children.get(1).getSourceCode());
		assertEquals("ArrayList<>", children.get(2).getSourceCode());
		assertEquals("(", children.get(3).getSourceCode());
		assertEquals(")", children.get(4).getSourceCode());
		assertEquals(";", children.get(5).getSourceCode());
		
		List<SourceFragment> children2 = ((ElementSourceFragment) children.get(2)).getChildrenFragments();
		assertEquals(3, children2.size());
		assertEquals("ArrayList", children2.get(0).getSourceCode());
		assertEquals("<", children2.get(1).getSourceCode());
		assertEquals(">", children2.get(2).getSourceCode());
	}

	@Test
	public void testSourceFragmentsOfNewAnnonymousClass() throws Exception {
		//contract: it is possible to build source fragment of new anonymous class
		final CtType<?> type = ModelUtils.buildClass(AnnonymousClassNewIface.class);
		CtLocalVariable<?> locVar =  (CtLocalVariable<?>) type.getMethodsByName("m").get(0).getBody().getStatements().get(0);
		CtNewClass<?> newClass = (CtNewClass<?>) locVar.getDefaultExpression();
		{
			ElementSourceFragment newClassSF = newClass.getOriginalSourceFragment();
			List<SourceFragment> children = newClassSF.getChildrenFragments();
			assertEquals(7, children.size());
			assertEquals("new", children.get(0).getSourceCode());
			assertEquals(" ", children.get(1).getSourceCode());
			assertEquals("Consumer<Set<?>>", children.get(2).getSourceCode());
			assertEquals("(", children.get(3).getSourceCode());
			assertEquals(")", children.get(4).getSourceCode());
			assertEquals(" ", children.get(5).getSourceCode());
			assertEquals("{" + 
					"			@Override" + 
					"			public void accept(Set<?> t) {" + 
					"			}" + 
					"		}", children.get(6).getSourceCode().replaceAll("\\r|\\n", ""));
		}
		{
			ElementSourceFragment typeSourceFragment = newClass.getExecutable().getType().getOriginalSourceFragment();
			assertEquals("Consumer<Set<?>>", typeSourceFragment.getSourceCode());
		}
		{
			ElementSourceFragment newAnnClassSF = newClass.getAnonymousClass().getOriginalSourceFragment();
			List<SourceFragment> children = newAnnClassSF.getChildrenFragments();
			assertEquals(5, children.size());
			assertEquals("{", children.get(0).getSourceCode());
			assertEquals("			", children.get(1).getSourceCode().replaceAll("\\r|\\n", ""));
			assertEquals("@Override" + 
					"			public void accept(Set<?> t) {" + 
					"			}", children.get(2).getSourceCode().replaceAll("\\r|\\n", ""));
			assertEquals("		", children.get(3).getSourceCode().replaceAll("\\r|\\n", ""));
			assertEquals("}", children.get(4).getSourceCode());
			
			
		}
	}

	private void checkElementFragments(CtElement ele, Object... expectedFragments) {
		ElementSourceFragment fragment = ele.getOriginalSourceFragment();
		List<SourceFragment> children = fragment.getChildrenFragments();

		// calls getSourceCode on on elements of children
		assertEquals(expandGroup(new ArrayList<>(), expectedFragments), toCodeStrings(children));

		assertGroupsEqual(expectedFragments, fragment.getGroupedChildrenFragments());
	}

	private String[] group(String ...str) {
		return str;
	}

	private List<String> expandGroup(List<String> result, Object[] items) {
		for (Object object : items) {
			if (object instanceof String[]) {
				String[] strings = (String[]) object;
				expandGroup(result, strings);
			} else {
				result.add((String) object);
			}
		}
		return result;
	}

	private static void assertGroupsEqual(Object[] expectedFragments, List<SourceFragment> groupedChildrenFragments) {
		assertEquals(Arrays.stream(expectedFragments).map(item->{
			if (item instanceof String[]) {
				return "group("+Arrays.asList((String[]) item).toString() + ")";
			}
			return "\"" + item.toString() + "\"";
		}).collect(Collectors.joining(";")), groupedChildrenFragments.stream().map(item -> {
			if (item instanceof CollectionSourceFragment) {
				CollectionSourceFragment csf = (CollectionSourceFragment) item;
				return "group("+ toCodeStrings(csf.getItems()).toString() + ")";
			}
			return "\"" + item.getSourceCode() + "\"";
		}).collect(Collectors.joining(";")));
	}
	
	private static List<String> toCodeStrings(List<SourceFragment> csf) {
		return csf.stream().map(SourceFragment::getSourceCode).collect(Collectors.toList());
	}
}
