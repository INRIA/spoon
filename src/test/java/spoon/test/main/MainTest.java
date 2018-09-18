package spoon.test.main;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spoon.Launcher;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtPathStringBuilder;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.parent.ParentTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MainTest {

	static Launcher launcher;
	static CtPackage rootPackage;

	/**
	 * load model once into static variable and use it for more read-only tests
	 */
	@BeforeClass
	public static void loadModel() {
		// we have to remove the test-classes folder
		// so that the precondition of --source-classpath is not violated
		// (target/test-classes contains src/test/resources which itself contains Java files)
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		launcher = new Launcher();

		launcher.setArgs(new String[] {
				"-i", "src/main/java",
				"-o", "target/spooned",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath,
				"--compile", // compiling Spoon code itself on the fly
				"--compliance", "8",
				"--level", "OFF",
				"--enable-comments"
		});
		
		launcher.buildModel();

		rootPackage = launcher.getFactory().Package().getRootPackage();
	}

	@Test
	public void testMain_ModelPrintAndCompile() {
		//contract: check that spoon sources can be printed
		launcher.prettyprint();
		//contract: check that spoon sources can be compiled
		launcher.getModelBuilder().compile(InputType.CTTYPES);
	}

	@Test
	public void testMain_checkGenericContracts() {
		checkGenericContracts(rootPackage);
	}

	@Test
	public void testMain_checkShadow() {
		checkShadow(rootPackage);
	}

	@Test
	public void testMain_checkParentConsistency() {
		checkParentConsistency(rootPackage);
	}

	@Test
	public void testMain_checkModifiers() {
		// the explicit modifier should be present in the original source code
		for (CtModifiable modifiable: rootPackage.getElements(new TypeFilter<>(CtModifiable.class))) {
			for (CtExtendedModifier modifier: modifiable.getExtendedModifiers()) {
				if (modifier.isImplicit()) {
					continue;
				}
				SourcePosition position = modifier.getPosition();
				CompilationUnit compilationUnit = position.getCompilationUnit();
				String originalSourceCode = compilationUnit.getOriginalSourceCode();
				assertEquals(modifier.getKind().toString(), originalSourceCode.substring(position.getSourceStart(), position.getSourceEnd() + 1));
			}
		}
	}

	public void checkGenericContracts(CtPackage pack) {
		// parent
		ParentTest.checkParentContract(pack);

		// assignments
		checkAssignmentContracts(pack);

		// scanners
		checkContractCtScanner(pack);

		// type parameter reference.
		checkBoundAndUnboundTypeReference(pack);
	}

	private void checkBoundAndUnboundTypeReference(CtPackage pack) {
		new CtScanner() {
			@Override
			public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
				CtTypeParameter declaration = ref.getDeclaration();
				if (declaration != null) {
					assertEquals(ref.getSimpleName(), declaration.getSimpleName());
				}
				super.visitCtTypeParameterReference(ref);
			}
		}.scan(pack);
	}

	public static void checkShadow(CtPackage pack) {
		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null && CtShadowable.class.isAssignableFrom(element.getClass())) {
					assertFalse(((CtShadowable) element).isShadow());
				}
				super.scan(element);
			}

			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				assertNotNull(reference);
				if (CtTypeReference.NULL_TYPE_NAME.equals(reference.getSimpleName()) || "?".equals(reference.getSimpleName())) {
					super.visitCtTypeReference(reference);
					return;
				}
				final CtType<T> typeDeclaration = reference.getTypeDeclaration();
				assertNotNull(typeDeclaration);
				assertEquals(reference.getSimpleName(), typeDeclaration.getSimpleName());
				assertEquals(reference.getQualifiedName(), typeDeclaration.getQualifiedName());

				if (reference.getDeclaration() == null) {
					assertTrue(typeDeclaration.isShadow());
				}
				super.visitCtTypeReference(reference);
			}

			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
				super.visitCtExecutableReference(reference);
				assertNotNull(reference);
				if (isLanguageExecutable(reference)) {
					return;
				}
				final CtExecutable<T> executableDeclaration = reference.getExecutableDeclaration();
 				assertNotNull("cannot find decl for " + reference.toString(),executableDeclaration);
				assertEquals(reference.getSimpleName(), executableDeclaration.getSimpleName());

				// when a generic type is used in a parameter and return type, the shadow type doesn't have these information.
				for (int i = 0; i < reference.getParameters().size(); i++) {
					//TODO assertions which are checking lambdas. Till then ignore lambdas.
					if (executableDeclaration instanceof CtLambda) {
						return;
					}
					CtTypeReference<?> methodParamTypeRef = executableDeclaration.getParameters().get(i).getType();
					assertEquals(reference.getParameters().get(i).getQualifiedName(), methodParamTypeRef.getTypeErasure().getQualifiedName());
				}

				// contract: the reference and method signature are the same
				if (reference.getActualTypeArguments().isEmpty()
						&& executableDeclaration instanceof CtMethod
						&& !((CtMethod) executableDeclaration).getFormalCtTypeParameters().isEmpty()
						) {
					assertEquals(reference.getSignature(), executableDeclaration.getSignature());
				}

				// contract: the reference and constructor signature are the same
				if (reference.getActualTypeArguments().isEmpty()
						&& executableDeclaration instanceof CtConstructor
						&& !((CtConstructor) executableDeclaration).getFormalCtTypeParameters().isEmpty()
						) {
					assertEquals(reference.getSignature(), executableDeclaration.getSignature());
				}

				if (reference.getDeclaration() == null && CtShadowable.class.isAssignableFrom(executableDeclaration.getClass())) {
					assertTrue(((CtShadowable) executableDeclaration).isShadow());
				}

			}

			private <T> boolean isLanguageExecutable(CtExecutableReference<T> reference) {
				return "values".equals(reference.getSimpleName());
			}

			@Override
			public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
				assertNotNull(reference);
				if (isLanguageField(reference) || isDeclaredInSuperClass(reference)) {
					super.visitCtFieldReference(reference);
					return;
				}
				final CtField<T> fieldDeclaration = reference.getFieldDeclaration();
				assertNotNull(fieldDeclaration);
				assertEquals(reference.getSimpleName(), fieldDeclaration.getSimpleName());
				assertEquals(reference.getType().getQualifiedName(), fieldDeclaration.getType().getQualifiedName());

				if (reference.getDeclaration() == null) {
					assertTrue(fieldDeclaration.isShadow());
				}
				super.visitCtFieldReference(reference);
			}

			private <T> boolean isLanguageField(CtFieldReference<T> reference) {
				return "class".equals(reference.getSimpleName()) || "length".equals(reference.getSimpleName());
			}

			private <T> boolean isDeclaredInSuperClass(CtFieldReference<T> reference) {
				final CtType<?> typeDeclaration = reference.getDeclaringType().getTypeDeclaration();
				return typeDeclaration != null && typeDeclaration.getField(reference.getSimpleName()) == null;
			}
		}.visitCtPackage(pack);
	}

	@Test
	public void test() {
		final Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		spoon.addInputResource("./src/test/java/spoon/test/main/testclasses");
		spoon.addInputResource("./src/main/java/spoon/template/Parameter.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.run();

		checkShadow(spoon.getFactory().Package().getRootPackage());

		checkParentConsistency(spoon.getFactory().Package().getRootPackage());
	}

	private void checkContractCtScanner(CtPackage pack) {
		class Counter {
			int scan;
			int enter;
			int exit;
		}

		final Counter counter = new Counter();
		final Counter counterInclNull = new Counter();

		new CtScanner() {

			@Override
			public void scan(CtElement element) {
				counterInclNull.scan++;
				if (element != null) {
					counter.scan++;
				}
				super.scan(element);
			}

			@Override
			public void enter(CtElement element) {
				counter.enter++;
				super.enter(element);
			}

			@Override
			public void exit(CtElement element) {
				counter.exit++;
				super.exit(element);
			}

		}.scan(pack);

		// contract: when enter is called, exit is also called
		assertTrue(counter.enter == counter.exit);

		// contract: all scanned elements call enter
		assertTrue(counter.enter == counter.scan);

		Counter counterBiScan = new Counter();
		class ActualCounterScanner extends CtBiScannerDefault {
			@Override
			public void biScan(CtElement element, CtElement other) {
				counterBiScan.scan++;
				if (element == null) {
					if (other != null) {
						Assert.fail("element can't be null if other isn't null.");
					}
				} else if (other == null) {
					Assert.fail("other can't be null if element isn't null.");
				} else {
					// contract: all elements have been cloned and are still equal
					assertEquals(element, other);
					assertNotSame(element, other);
				}
				super.biScan(element, other);
			}
		}
		final ActualCounterScanner actual = new ActualCounterScanner();
		actual.biScan(pack, pack.clone());

		// contract: scan and biscan are executed the same number of times
		assertEquals(counterInclNull.scan, counterBiScan.scan);

		// for pure beauty: parallel visit of the same tree!
		Counter counterBiScan2 = new Counter();
		new CtBiScannerDefault() {
			@Override
			public void biScan(CtElement element, CtElement other) {
				counterBiScan2.scan++;
				// we have the exact same element
				assertSame(element, other);
				super.biScan(element, other);
			}
		}.biScan(pack, pack);
		// contract: scan and biscan are executed the same number of times
		assertEquals(counterInclNull.scan, counterBiScan2.scan);
	}

	public static void checkAssignmentContracts(CtElement pack) {
		for (CtAssignment assign : pack.getElements(new TypeFilter<>(CtAssignment.class))) {
			CtExpression assigned = assign.getAssigned();
			if (!(assigned instanceof CtFieldWrite
					|| assigned instanceof CtVariableWrite || assigned instanceof CtArrayWrite)) {
				throw new AssertionError("AssignmentContract error:" + assign.getPosition()+"\n"+assign.toString()+"\nAssigned is "+assigned.getClass());
			}
		}
	}

	public static void checkParentConsistency(CtElement ele) {
		final Set<CtElement> inconsistentParents = new HashSet<>();
		new CtScanner() {
			private Deque<CtElement> previous = new ArrayDeque();
			@Override
			protected void enter(CtElement e) {
				if (e != null) {
					if (!previous.isEmpty()) {
						try {
							if (e.getParent() != previous.getLast()) {
								inconsistentParents.add(e);
							}
						} catch (ParentNotInitializedException ignore) {
							inconsistentParents.add(e);
						}
					}
					previous.add(e);
				}
				super.enter(e);
			}

			@Override
			protected void exit(CtElement e) {
				if (e == null) {
					return;
				}
				if (e.equals(previous.getLast())) {
					previous.removeLast();
				} else {
					throw new RuntimeException("Inconsistent stack");
				}
				super.exit(e);
			}
		}.scan(ele);
		assertEquals("All parents have to be consistent", 0, inconsistentParents.size());
	}

	/*
	 * contract: each element is used only once
	 * For example this is always true: field.getType() != field.getDeclaringType()
	 */
	@Test
	public void checkModelIsTree() {
		Exception dummyException = new Exception("STACK");
		PrinterHelper problems = new PrinterHelper(rootPackage.getFactory().getEnvironment());
		Map<CtElement, Exception> allElements = new IdentityHashMap<>();
		rootPackage.filterChildren(null).forEach((CtElement ele) -> {
			//uncomment this line to get stacktrace of real problem. The dummyException is used to avoid OutOfMemoryException
//			Exception secondStack = new Exception("STACK");
			Exception secondStack = dummyException;
			Exception firstStack = allElements.put(ele, secondStack);
			if (firstStack != null) {
				if(firstStack == dummyException) {
					Assert.fail("The Spoon model is not a tree. The " + ele.getClass().getSimpleName() + ":" + ele.toString() + " is shared");
				}
				//the element ele was already visited. It means it used on more places
				//report the stacktrace of first and second usage, so that place can be found easily
				problems.write("The element " + ele.getClass().getSimpleName()).writeln()
				.incTab()
				.write(ele.toString()).writeln()
				.write("Is linked by these stacktraces").writeln()
				.write("1) " + getStackTrace(firstStack)).writeln()
				.write("2) " + getStackTrace(secondStack)).writeln()
				.decTab();
			}
		});
		
		String report = problems.toString();
		if (!report.isEmpty()) {
			Assert.fail(report);
		}
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	@Test
	public void testMyRoleInParent() {
		rootPackage.accept(new CtScanner() {
			@Override
			public void scan(CtRole role, CtElement element) {
				if (element != null) {
					//contract: getMyRoleInParent returns the expected parent
					assertSame(role, element.getRoleInParent());
				}
				super.scan(role, element);
			}
		});
	}

	@Test
	public void testSourcePositionTreeIsCorrectlyOrdered() {
		/*
		 * contract: the tree of ElementSourceFragments of all spoon types (= sample set of sources) can be built.
		 * contract: the tree of ElementSourceFragments is correctly organized. It means:
		 * - source positions of children elements are smaller or equal to their parents
		 * - source positions of next siblings are after their previous siblings
		 * - 
		 */
		List<CtType> types = rootPackage.filterChildren(new TypeFilter<>(CtType.class)).filterChildren((CtType t) -> t.isTopLevel()).list();
		int totalCount = 0;
		boolean hasComment = false;
		for (CtType type : types) {
			SourcePosition sp = type.getPosition();
			totalCount += assertSourcePositionTreeIsCorrectlyOrder(sp.getCompilationUnit().getOriginalSourceFragment(), 0, sp.getCompilationUnit().getOriginalSourceCode().length());
			hasComment = hasComment || type.getComments().size() > 0; 
		};
		assertTrue(totalCount > 1000);
		assertTrue(hasComment);
	}

	/**
	 * Asserts that all siblings and children of sp are well ordered
	 * @param sourceFragment
	 * @param minOffset TODO
	 * @param maxOffset TODO
	 * @return number of checked {@link SourcePosition} nodes
	 */
	private int assertSourcePositionTreeIsCorrectlyOrder(ElementSourceFragment sourceFragment, int minOffset, int maxOffset) {
		int nr = 0;
		int pos = minOffset;
		while (sourceFragment != null) {
			nr++;
			assertTrue("min(" + pos + ") <= fragment.start(" + sourceFragment.getStart() + ")", pos <= sourceFragment.getStart());
			assertTrue("fragment.start(" + sourceFragment.getStart() + ") <= fragment.end(" + sourceFragment.getEnd() + ")", sourceFragment.getStart() <= sourceFragment.getEnd());
			pos = sourceFragment.getEnd();
			nr += assertSourcePositionTreeIsCorrectlyOrder(sourceFragment.getFirstChild(), sourceFragment.getStart(), sourceFragment.getEnd());
			sourceFragment = sourceFragment.getNextSibling();
		}
		assertTrue("lastFragment.end(" + pos + ") <= max(" + maxOffset + ")", pos <= maxOffset);
		return nr;
	}

	@Test
	public void testElementToPathToElementEquivalency() {

		rootPackage.accept(new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null) {
					CtPath path = element.getPath();
					String pathStr = path.toString();
					try {
						CtPath pathRead = new CtPathStringBuilder().fromString(pathStr);
						Collection<CtElement> returnedElements = pathRead.evaluateOn(rootPackage);
						//contract: CtUniqueRolePathElement.evaluateOn() returns a unique elements if provided only a list of one inputs
						assertEquals(1, returnedElements.size());
						CtElement actualElement = (CtElement) returnedElements.toArray()[0];
						//contract: Element -> Path -> String -> Path -> Element leads to the original element
						assertSame(element, actualElement);
					} catch (CtPathException e) {
						fail("Path is either incorrectly generated or incorrectly read");
					}
				}
				super.scan(element);
			}
		});
	}

	@Test
	public void testElementIsContainedInAttributeOfItsParent() {
		rootPackage.accept(new CtScanner() {
			@Override
			public void scan(CtRole role, CtElement element) {
				if (element != null) {
					//contract: element is contained in attribute of element's parent
					CtElement parent = element.getParent();
					Object attributeOfParent = parent.getValueByRole(role);
					if(attributeOfParent instanceof CtElement) {
						assertSame("Element of type " + element.getClass().getName()
								+ " is not the value of attribute of role " + role.name()
								+ " of parent type " + parent.getClass().getName(), element, attributeOfParent);
					} else if (attributeOfParent instanceof Collection) {
						assertTrue("Element of type " + element.getClass().getName()
								+ " not found in Collection value of attribute of role " + role.name() 
								+ " of parent type " + parent.getClass().getName(), 
								((Collection<CtElement>) attributeOfParent).stream().anyMatch(e -> e == element));
					} else if (attributeOfParent instanceof Map){
						assertTrue("Element of type " + element.getClass().getName()
								+ " not found in Map#values of attribute of role " + role.name() 
								+ " of parent type " + parent.getClass().getName(), 
								((Map<String, ?>) attributeOfParent).values().stream().anyMatch(e -> e == element));
					} else {
						fail("Attribute of Role " + role + " not checked");
					}
				}
				super.scan(role, element);
			}
		});
	}

	@Test
	public void testTest() {
		// the tests should be spoonable
		Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "src/test/java",
				"-o", "target/spooned",
				"--noclasspath",
				"--disable-comments",
				"--compliance", "8",
				"--level", "OFF"
		});

		checkGenericContracts(launcher.getFactory().Package().getRootPackage());

		// contract: all test classes, as well as template/processors/etc used in tests have "test" in their fully qualified name
		// if one analyzes src/main/java and src/test/java at the same time
		// this helps a lot to easily automatically differentiate app classes and test classes
		for (CtType t : launcher.getFactory().getModel().getAllTypes()) {
			if ("spoon.metamodel".equals(t.getPackage().getQualifiedName())
					|| t.getPackage().getQualifiedName().startsWith("spoon.generating")) {
				//Meta model classes doesn't have to follow test class naming conventions
				continue;
			}
			assertTrue(t.getQualifiedName() + " is not clearly a test class, it should contain 'test' either in its package name or class name", t.getQualifiedName().matches("(?i:.*test.*)"));
		}
	}

	@Test
	public void testResourcesCopiedInTargetDirectory() {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		spoon.Launcher.main(new String[] {
				"-i", "src/test/resources/no-copy-resources/",
				"-o", "target/spooned-with-resources",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath, "--compile" });

		assertTrue(new File("src/test/resources/no-copy-resources/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/fr/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/inria/package.html").exists());
		assertTrue(new File("target/spooned-with-resources/fr/inria/package.html").exists());
	}

	@Test
	public void testResourcesNotCopiedInTargetDirectory() {
		StringBuilder classpath = new StringBuilder();
		for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			if (!classpathEntry.contains("test-classes")) {
				classpath.append(classpathEntry);
				classpath.append(File.pathSeparator);
			}
		}
		String systemClassPath = classpath.substring(0, classpath.length() - 1);

		spoon.Launcher.main(new String[] {
				"-i", "src/test/resources/no-copy-resources",
				"-o", "target/spooned-without-resources",
				"--destination","target/spooned-build",
				"--source-classpath", systemClassPath, "--compile",
				"-r" });

		assertTrue(new File("src/test/resources/no-copy-resources/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/package.html").exists());
		assertTrue(new File("src/test/resources/no-copy-resources/fr/inria/package.html").exists());
		assertFalse(new File("target/spooned-without-resources/fr/inria/package.html").exists());
	}

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Test
	public void testLauncherWithoutArgumentsExitWithSystemExit() {
		exit.expectSystemExit();

		final PrintStream oldErr = System.err;
		System.setErr(new PrintStream(errContent));
		exit.checkAssertionAfterwards(new Assertion() {
			@Override
			public void checkAssertion() {
				assertTrue(errContent.toString().contains("Usage: java <launcher name> [option(s)]"));
				System.setErr(oldErr);
			}
		});

		new Launcher().run(new String[] { });
	}
}
