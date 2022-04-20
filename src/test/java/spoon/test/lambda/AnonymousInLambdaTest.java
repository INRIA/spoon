package spoon.test.lambda;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
					assertFalse(t.getSimpleName().contains("<unknown>"), t.getSimpleName() + " is not good " + t.getPosition());
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
