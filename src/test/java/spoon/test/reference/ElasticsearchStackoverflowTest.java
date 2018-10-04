package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ElasticsearchStackoverflowTest {

	private class Scanner extends CtScanner {
		@Override
		public <T> void visitCtExecutableReference(
				CtExecutableReference<T> reference) {
			super.visitCtExecutableReference(reference);
			reference.getDeclaration();
		}
	}

	@Test
	public void testStackOverflow() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/elasticsearch-stackoverflow");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtModel model = launcher.getModel();
		Scanner scanner = new Scanner();
		scanner.scan(model.getRootPackage());

		List<CtExecutableReference> executables = launcher.getModel().getElements(new TypeFilter<>(CtExecutableReference.class));
		assertFalse(executables.isEmpty());

		boolean result = false;
		for (CtExecutableReference execRef : executables) {
			if ("setParentTask".equals(execRef.getSimpleName())) {
				CtTypeReference typeRef = execRef.getDeclaringType();
				assertTrue(typeRef instanceof CtTypeParameterReference);
				assertEquals("ShardRequest", typeRef.getSimpleName());

				CtType typeRefDecl = typeRef.getDeclaration();
				assertEquals("BroadcastShardRequest", typeRefDecl.getSuperclass().getSimpleName());

				assertNull(execRef.getDeclaration());
				result = true;
			}
		}

		assertTrue(result);

	}

}
