package spoon.test.literal;

import org.junit.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import static org.junit.Assert.assertEquals;

// bug case kindly provided by @Banbury
// in https://github.com/INRIA/spoon/issues/3203
public class UnicodeBugTest {

	@Test
	public void testUnicodeBug() {
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("class A { String s = \"Hellö \" + \"Wörld!\"; }"));
		launcher.buildModel();
		launcher.addProcessor(new StringConcatProcessor());
		launcher.process();
		CtModel model = launcher.getModel();
		CtField<?> field = model.getElements(new TypeFilter<>(CtField.class)).get(0);
		assertEquals(field.toString(), "java.lang.String s = \"Hellö Wörld!\";");
	}

	private class StringConcatProcessor extends AbstractProcessor<CtLiteral<?>> {
		@Override
		public boolean isToBeProcessed(CtLiteral<?> candidate) {
			return candidate.getType().isSubtypeOf(getFactory().Type().STRING) &&
					candidate.getParent() instanceof CtBinaryOperator;
		}

		@Override
		public void process(CtLiteral<?> element) {
			CtCodeElement s = ((CtBinaryOperator<?>) element.getParent()).partiallyEvaluate();
			element.getParent().replace(s);
		}
	}
}
