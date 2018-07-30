package spoon.test.visitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class AssignmentsEqualsTest {

	@Test
	public void testEquals() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/visitor/Assignments.java");
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		List<CtAssignment> assignments = Query.getElements(factory, new TypeFilter<>(CtAssignment.class));

		assertTrue(assignments.size() == 10);
		assertFalse(assignments.get(0).equals(assignments.get(1)));
		assertFalse(assignments.get(2).equals(assignments.get(3)));
		assertFalse(assignments.get(4).equals(assignments.get(5)));
		assertFalse(assignments.get(6).equals(assignments.get(7)));
		assertTrue(assignments.get(8).equals(assignments.get(9)));
	}
}
