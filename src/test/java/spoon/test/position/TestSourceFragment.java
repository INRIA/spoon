package spoon.test.position;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.sniper.internal.SourceFragment;
import spoon.support.sniper.internal.CollectionSourceFragment;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.test.position.testclasses.FooSourceFragments;

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
				group("\n\t", "public", "\n\t", "@Deprecated", " ", "//c1 ends with tab and space\t ", "\n\t", "static", " "), "/*c2*/", " ",
				"<", group("T", ",", " ", "U"), ">",
				" ", "T", " ", "m3", "(", group("U param", ",", " ", "@Deprecated int p2"), ")", " ", "{\n" + 
						"		return null;\n" + 
						"	}");
		checkElementFragments(foo.getMethodsByName("m4").get(0).getBody().getStatement(0),"label",":"," ", "while", "(", "true", ")", ";");

		checkElementFragments(foo.getMethodsByName("m5").get(0).getBody().getStatement(0),"f", " ", "=", " ", "7.2", ";");
		checkElementFragments(((CtAssignment)foo.getMethodsByName("m5").get(0).getBody().getStatement(0)).getAssignment(),"7.2");
				 
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
			return item;
		}).collect(Collectors.toList()), groupedChildrenFragments.stream().map(item -> {
			if (item instanceof CollectionSourceFragment) {
				CollectionSourceFragment csf = (CollectionSourceFragment) item;
				return "group("+ toCodeStrings(csf.getItems()).toString() + ")";
			}
			return item.getSourceCode();
		}).collect(Collectors.toList()));
	}
	
	private static List<String> toCodeStrings(List<SourceFragment> csf) {
		return csf.stream().map(SourceFragment::getSourceCode).collect(Collectors.toList());
	}
}
