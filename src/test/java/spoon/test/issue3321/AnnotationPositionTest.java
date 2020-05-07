package spoon.test.issue3321;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.test.issue3321.testclasses.AnnoUser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnnotationPositionTest {

	@Test
	public void testUsageOfTypeAnnotationOnParameterInMethod() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/issue3321/testclasses");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnoUser.class);

		CtMethod m1 = ctClass.getMethod("m1", factory.Type().STRING);
		CtParameter pm1 = (CtParameter) m1.getParameters().get(0);
		SourcePosition paramPos1 = pm1.getType().getPosition();
		SourcePosition typeRefPos1 = pm1.getType().getPosition();
		SourcePosition annoPos1 = pm1.getType().getAnnotations().get(0).getPosition();
		assertTrue(contains(paramPos1,annoPos1));
		assertTrue(contains(typeRefPos1,annoPos1));


		CtMethod m2 = ctClass.getMethod("m2", factory.Type().STRING);
		CtParameter pm2 = (CtParameter) m2.getParameters().get(0);
		SourcePosition paramPos2 = pm2.getType().getPosition();
		SourcePosition typeRefPos2 = pm2.getType().getPosition();
		SourcePosition annoPos2 = pm2.getAnnotations().get(0).getPosition();
		assertTrue(contains(paramPos2,annoPos2));
		assertFalse(contains(typeRefPos2,annoPos2));

		CtMethod m3 = ctClass.getMethod("m3", factory.Type().STRING);
		CtParameter pm3 = (CtParameter) m3.getParameters().get(0);
		SourcePosition paramPos3 = pm3.getType().getPosition();
		SourcePosition typeRefPos3 = pm3.getType().getPosition();
		SourcePosition annoPos31 = pm3.getType().getAnnotations().get(0).getPosition();
		SourcePosition annoPos32 = pm3.getAnnotations().get(0).getPosition();
		assertTrue(contains(paramPos3,annoPos31));
		assertTrue(contains(paramPos3,annoPos32));
		assertTrue(contains(typeRefPos3,annoPos32));




		System.out.println("Hello");
	}

	public static boolean contains(SourcePosition p1, SourcePosition p2) {
		if(p1.getFile() != p2.getFile()) return false;
		if(!(p1.getLine() <= p2.getLine() && p1.getEndLine() >= p2.getEndLine())) return false;

		if(!(p1.getColumn() <= p2.getColumn() && p1.getEndColumn() >= p2.getEndColumn())) return false;
		return true;
	}
}
