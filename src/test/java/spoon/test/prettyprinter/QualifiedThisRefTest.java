package spoon.test.prettyprinter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtFieldAccessImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.test.delete.testclasses.Adobada;
import spoon.test.prettyprinter.testclasses.QualifiedThisRef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static javax.swing.text.html.HTML.Tag.HEAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class QualifiedThisRefTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/prettyprinter/testclasses/QualifiedThisRef.java"))
				.build();
		factory.getEnvironment().setAutoImports(true);
	}

	@Test
	public void testQualifiedThisRef() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		CtType<?> ctClass = factory.Type().get(QualifiedThisRef.class);
		Collection<CtReference> imports = printer.computeImports(ctClass);
		final List<CtType<?>> ctTypes = new ArrayList<>();
		ctTypes.add(ctClass);
		printer.getElementPrinterHelper().writeHeader(ctTypes, imports);
		printer.scan(ctClass);
		Assert.assertTrue(printer.getResult().contains("Object o = this"));
		Assert.assertTrue(printer.getResult().contains("Object o2 = QualifiedThisRef.this"));
	}

	@Test
	public void testCloneThisAccess() throws Exception {
		// contract: the target of "this" is correct and can be cloned
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);
		final CtMethod<?> m2 = adobada.getMethod("methodUsingjlObjectMethods");

		CtThisAccess th = (CtThisAccess) m2.getElements(new TypeFilter(CtThisAccess.class)).get(0);
		assertEquals(true,th.isImplicit());
		assertEquals("notify()",th.getParent().toString());
		CtInvocation<?> clone = m2.clone().getBody().getStatement(0);
		// clone preserves implicitness
		assertEquals(true, clone.getTarget().isImplicit());
		assertEquals("notify()", clone.toString()); // the original bug

		// note that this behavior means that you can only keep cloned "this" in the same class,
		// and you cannot "transplant" a cloned "this" to another class
		// it makes perfectly sense about the meaning of this.
		// to "transplant" a this, you have to first set the target to null
	}

	@Test
	public void testPrintCtFieldAccessWorkEvenWhenParentNotInitialized() throws Exception {
		CtClass zeclass = factory.Class().get(QualifiedThisRef.class);

		List<CtMethod> methods = zeclass.getMethodsByName("bla");

		assertEquals(1, methods.size());

		CtStatement invocation = methods.get(0).getBody().getStatement(0);

		assertTrue(invocation instanceof CtInvocation);
		CtInvocation<?> arg0 = (CtInvocation) invocation;

		CtExpression param = arg0.getArguments().get(0);
		CtExecutableReference execref = factory.Core().createExecutableReference();
		execref.setDeclaringType(factory.Type().createReference("java.util.Map"));
		execref.setSimpleName("exorcise");
		execref.setStatic(true);

		CtTypeReference tmp = param.getType();

		CtExpression arg = null;
		CtFieldReference ctfe = factory.createFieldReference();
		ctfe.setSimpleName("class");
		ctfe.setDeclaringType(tmp.box());
		arg = factory.Core().createFieldRead();
		((CtFieldAccessImpl) arg).setVariable(ctfe);


		CtLiteral location = factory.Core().createLiteral();
		location.setType(factory.Type().createReference(String.class));

		CtTypeReference tmpref = factory.Core().clone(tmp);

		CtInvocation invoc = factory.Core().createInvocation();
		invoc.setExecutable(execref);
		invoc.setArguments(Arrays.asList(new CtExpression[]{param,arg,location}));
		execref.setActualTypeArguments(Arrays.asList(new CtTypeReference<?>[]{tmpref}));

		// succeeds
		arg0.getArguments().set(0, invoc);

		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		printer.visitCtClass(zeclass);

		assertFalse(printer.getResult().isEmpty());

	}
}
