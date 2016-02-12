package spoon.test.model;

import org.junit.Test;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

public class AnonymousExecutableTest {

	@Test
	public void testStatements() throws Exception {
		CtType<?> type = build("spoon.test.model", "AnonymousExecutableClass");
		CtAnonymousExecutable anonexec =
			type.
			getElements(new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class)).
			get(0);
		List<CtStatement> stats = anonexec.getBody().getStatements();
		assertEquals(1, stats.size());
	}
}
