package spoon.test.ctClass;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

import java.util.Set;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.ctClass.testclasses.Foo;
import spoon.test.ctClass.testclasses.Pozole;

public class CtClassTest {

    @Test
    public void getConstructor() throws Exception {
        final Factory build = build(Foo.class);
        final CtClass<?> foo = (CtClass<?>) build.Type().get(Foo.class);

		assertEquals(3, foo.getConstructors().size());

		CtTypeReference<Object> typeString = build.Code().createCtTypeReference(String.class);
		CtConstructor<?> constructor = foo.getConstructor(typeString);
		assertEquals(typeString, constructor.getParameters().get(0).getType());

		CtArrayTypeReference<Object> typeStringArray = build.Core().createArrayTypeReference();
		typeStringArray.setComponentType(typeString);
		constructor = foo.getConstructor(typeStringArray);
		assertEquals(typeStringArray, constructor.getParameters().get(0).getType());

		CtArrayTypeReference<Object> typeStringArrayArray = build.Core().createArrayTypeReference();
		typeStringArrayArray.setComponentType(typeStringArray);
		constructor = foo.getConstructor(typeStringArrayArray);
		assertEquals(typeStringArrayArray, constructor.getParameters().get(0).getType());
	}

	@Test
	public void testParentOfTheEnclosingClassOfStaticClass() throws Exception {
		// contract: When we have a static class which extends a superclass in the classpath,
		// the enclosing class don't have a superclass. This is probably a bug in JDT but good
		// luck to report a bug about noclasspath in their bugtracker. :)

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/InvariantChecker.java");
		launcher.addInputResource("./src/test/resources/noclasspath/FileIO.java");
		launcher.addInputResource("./src/test/resources/noclasspath/Daikon.java");
		launcher.setSourceOutputDirectory("./target/class");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("daikon.tools.InvariantChecker");

		final CtType<?> staticClass = aClass.getNestedType("InvariantCheckProcessor");
		assertNotNull(staticClass);
		assertEquals("InvariantCheckProcessor", staticClass.getSimpleName());
		assertNotNull(staticClass.getSuperclass());
		assertEquals("daikon.FileIO$Processor", staticClass.getSuperclass().getQualifiedName());
		assertNull(aClass.getSuperclass());

		canBeBuilt("./target/class", 8, true);
	}

	@Test
	public void testNoClasspathWithSuperClassOfAClassInAnInterface() throws Exception {
		// contract: When we specify a superclass which is declared in an interface and
		// where the visibility is okay, we must use it.

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/draw2d");
		launcher.setSourceOutputDirectory("./target/draw2d");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.eclipse.draw2d.parts.ScrollableThumbnail");
		final CtType<?> innerClass = aClass.getNestedType("ClickScrollerAndDragTransferrer");
		assertEquals("org.eclipse.draw2d.MouseMotionListener$Stub", innerClass.getSuperclass().getQualifiedName());

		canBeBuilt("./target/draw2d", 8, true);
	}

	@Test
	public void testAllTypeReferencesToALocalTypeShouldNotStartWithNumber() throws Exception {
		// contract: When we have a local type, we should never start with its number. But this
		// number isn't rewrite only in the class declaration but in all type references and in
		// the constructor.
		final CtType<Pozole> aPozole = buildClass(Pozole.class);
		final CtClass<?> cook = aPozole.getNestedType("1Cook");

		assertEquals("1Cook", cook.getSimpleName());
		assertEquals("spoon.test.ctClass.testclasses.Pozole$1Cook", cook.getQualifiedName());
		final Set<? extends CtConstructor<?>> constructors = cook.getConstructors();
		final String expectedConstructor = "public Cook() {" + System.lineSeparator() + "}";
		assertEquals(expectedConstructor, constructors.toArray(new CtConstructor[constructors.size()])[0].toString());
		assertEquals("final java.lang.Class<Cook> cookClass = Cook.class", cook.getMethod("m").getBody().getStatement(0).toString());

		Factory factory = aPozole.getFactory();

		aPozole.removeModifier(ModifierKind.PUBLIC);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();

		CtClass internalClass = factory.Core().createClass();
		internalClass.setSimpleName("Foo");
		cook.getParent(CtBlock.class).addStatement(internalClass);
		assertEquals("Foo", internalClass.getSimpleName());
		assertEquals("spoon.test.ctClass.testclasses.Pozole$Foo", internalClass.getQualifiedName());

		internalClass.addConstructor(factory.Core().createConstructor());
		CtConstructor cons = (CtConstructor) internalClass.getConstructors().toArray(new CtConstructor[0])[0];
		cons.setBody(factory.Core().createBlock());

		CtConstructorCall call = cook.getFactory().Core().createConstructorCall();
		call.setExecutable(cons.getReference());
		assertEquals(internalClass, internalClass.getReference().getDeclaration());
		assertEquals("new Foo()", call.toString());
		internalClass.insertAfter(call);

		factory.getEnvironment().setAutoImports(true);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();

		factory.getEnvironment().setAutoImports(false);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();
	}

	@Test
	public void testSpoonShouldInferImplicitPackageInNoClasspath() throws Exception {
    	// contract: in noClasspath, when a type is used and no import is specified, then Spoon
		// should infer that this type is in the same package as the current class.
		final Launcher launcher2 = new Launcher();
		launcher2.addInputResource("./src/test/resources/noclasspath/issue1293/com/cristal/ircica/applicationcolis/userinterface/fragments/TransporteurFragment.java");
		launcher2.getEnvironment().setNoClasspath(true);
		launcher2.buildModel();

		final CtClass<Object> aClass2 = launcher2.getFactory().Class().get("com.cristal.ircica.applicationcolis.userinterface.fragments.TransporteurFragment");
		final String type2 = aClass2.getSuperclass().getQualifiedName();

		CtField field = aClass2.getField("transporteurRadioGroup");
		assertThat(field.getType().getQualifiedName(), is("android.widget.RadioGroup"));

		assertThat(type2, is("com.cristal.ircica.applicationcolis.userinterface.fragments.CompletableFragment"));
	}
}
