package spoon.test.lambda;

import org.junit.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class AnonymousInLambdaTest {
	@Test
	public void testAnonymousInLambda() {



		Launcher run = new Launcher();
		run.getEnvironment().setNoClasspath(true);
		run.addInputResource("src/test/resources/noclasspath/lambdAnonymous/AnoHolder.java");
		ClassProcessor cp = new ClassProcessor();
		run.addProcessor(cp);
		run.run();

		cp.classes.forEach(
				t -> {
					assertFalse(t.getSimpleName() + " is not good " + t.getPosition(), t.getSimpleName().contains("<unknown>"));
				}
		);
	}

	class ClassProcessor extends AbstractProcessor<CtClass> {
		public Set<CtClass> classes = new HashSet<>();
		@Override
		public boolean isToBeProcessed(CtClass candidate) {
			return super.isToBeProcessed(candidate) ;
		}

		public void process(CtClass element) {
			classes.add(element);
		}
	}
}
