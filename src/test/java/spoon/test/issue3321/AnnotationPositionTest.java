package spoon.test.issue3321;

import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.test.GitHubIssue;
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
		SourcePosition paramPos2 = pm2.getPosition();
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

		CtMethod m4 = ctClass.getMethod("m4", factory.Type().STRING);
		CtParameter pm4 = (CtParameter) m4.getParameters().get(0);
		SourcePosition paramPos4 = pm4.getType().getPosition();
		SourcePosition typeRefPos4 = pm4.getType().getPosition();
		SourcePosition annoPos4 = pm4.getType().getAnnotations().get(0).getPosition();
		assertTrue(contains(paramPos4,annoPos4));
		assertTrue(contains(typeRefPos4,annoPos4));

		CtMethod m5 = ctClass.getMethod("m5", factory.Type().STRING);
		CtParameter pm5 = (CtParameter) m5.getParameters().get(0);
		SourcePosition paramPos5 = pm5.getType().getPosition();
		SourcePosition typeRefPos5 = pm5.getType().getPosition();
		SourcePosition annoPos5 = pm5.getType().getAnnotations().get(0).getPosition();
		assertTrue(contains(paramPos5,annoPos5));
		assertTrue(contains(typeRefPos5,annoPos5));
	}


	@Ignore("UnresolvedBug")
	@GitHubIssue(issueNumber = 3358)
	@Test
	public void testSneakyAnnotationsOnParameters() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/issue3321/testclasses");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnoUser.class);




		CtMethod m6 = ctClass.getMethod("m6", factory.Type().STRING);
		CtParameter pm6 = (CtParameter) m6.getParameters().get(0);

		SourcePosition paramPos6 = pm6.getType().getPosition();
		SourcePosition typeRefPos6 = pm6.getType().getPosition();

		SourcePosition annoPos61 = pm6.getType().getAnnotations().get(0).getPosition();
		SourcePosition annoPos62 = pm6.getAnnotations().get(0).getPosition();

		SourcePosition annoPos631 = pm6.getType().getAnnotations().get(1).getPosition();
		SourcePosition annoPos632 = pm6.getAnnotations().get(1).getPosition();

		assertTrue(contains(paramPos6,annoPos61));
		assertTrue(contains(typeRefPos6,annoPos61));

		assertTrue(contains(paramPos6,annoPos62));
		assertFalse(contains(typeRefPos6,annoPos62));

		assertTrue(contains(paramPos6,annoPos631));
		assertTrue(contains(paramPos6,annoPos632));
		assertTrue(contains(typeRefPos6,annoPos632));
	}

	public static boolean contains(SourcePosition p1, SourcePosition p2) {

		if(p1.getFile() != p2.getFile()) return false;
		if(!(p1.getSourceStart() <= p2.getSourceStart() && p1.getSourceEnd() >= p2.getSourceEnd())) return false;

		//if(!(p1.getLine() <= p2.getLine() && p1.getEndLine() >= p2.getEndLine())) return false;

		//if(!(p1.getColumn() <= p2.getColumn() && p1.getEndColumn() >= p2.getEndColumn())) return false;
		return true;
	}
}
