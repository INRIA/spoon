package spoon.test.visitor;

import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.ModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AssignmentsEqualsTest {

	@ModelTest("./src/test/resources/spoon/test/visitor/Assignments.java")
	public void testEquals(Factory factory) {
		List<CtAssignment> assignments = Query.getElements(factory, new TypeFilter<>(CtAssignment.class));
		assertEquals(assignments.size(), 10);
		assertNotEquals(assignments.get(0), assignments.get(1));
		assertNotEquals(assignments.get(2), assignments.get(3));
		assertNotEquals(assignments.get(4), assignments.get(5));
		assertNotEquals(assignments.get(6), assignments.get(7));
		assertEquals(assignments.get(8), assignments.get(9));
	}
}
