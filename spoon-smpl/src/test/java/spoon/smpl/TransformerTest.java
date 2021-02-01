package spoon.smpl;

import org.junit.Before;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.operation.Operation;
import spoon.smpl.operation.OperationCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import static spoon.smpl.TestUtils.*;


public class TransformerTest {
	@Before
	public void before() {
		resetControlFlowNodeCounter();
	}

	@Test
	public void testEmpty() {

		// contract: calling transform() with an empty set of witnesses should not change the input

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "}\n");

		CFGModel model = new CFGModel(methodCfg(method));

		int pre = method.hashCode();
		Transformer.transform(model, new HashSet<>());

		assertEquals(pre, method.hashCode());
	}

	@Test
	public void testSingleOperation() {

		// contract: a witness can specify a single operation to be applied

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "    int y = 1;\n" +
										 "}\n");

		CFGModel model = new CFGModel(methodCfg(method));
		final List<String> messages = new ArrayList<>();
		Set<ModelChecker.Witness> witnesses = new HashSet<>();

		Operation operation = (category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("Operation applied to " + element.toString() + " with bindings " + bindings.toString());
			}
		};

		witnesses.add(witness(4, "x", "y1", witness(4, "whatever", Arrays.asList(operation))));
		witnesses.add(witness(5, "x", "y2", witness(5, "whatever", Arrays.asList(operation))));

		Transformer.transform(model, witnesses);

		assertTrue(messages.contains("Operation applied to int x = 1 with bindings {x=y1}"));
		assertTrue(messages.contains("Operation applied to int y = 1 with bindings {x=y2}"));
	}

	@Test
	public void testListOfOperations() {

		// contract: a witness can specify a list of operations to apply in order

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "}\n");

		CFGModel model = new CFGModel(methodCfg(method));
		final List<String> messages = new ArrayList<>();
		Set<ModelChecker.Witness> witnesses = new HashSet<>();

		List<Operation> operations = new ArrayList<>();

		operations.add((category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("hello");
			}
		});
		operations.add((category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("world");
			}
		});

		witnesses.add(witness(4, "whatever", operations));

		Transformer.transform(model, witnesses);

		assertEquals("[hello, world]", messages.toString());
	}

	@Test
	public void testPrioritizedOperations() {

		// contract: operations of type (Prepend|Append|Delete)Operation are applied in a specific order

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "}\n");

		CFGModel model = new CFGModel(methodCfg(method));
		final List<String> messages = new ArrayList<>();
		Set<ModelChecker.Witness> witnesses = new HashSet<>();

		List<Operation> operations = new ArrayList<>();

		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("prepend");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.APPEND) {
				messages.add("append");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.DELETE) {
				messages.add("delete");
			}
		});

		witnesses.add(witness(4, "whatever", operations));

		for (int i = 0; i < 100; ++i) {
			messages.clear();
			Collections.shuffle(operations);
			Transformer.transform(model, witnesses);
			assertEquals("[prepend, append, delete]", messages.toString());
		}
	}

	@Test
	public void testPrioritizedOperationsCategoryOrder() {

		// contract: (Prepend|Delete)Operations are applied in order, AppendOperations in reverse order

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "}\n");

		CFGModel model = new CFGModel(methodCfg(method));
		final List<String> messages = new ArrayList<>();
		Set<ModelChecker.Witness> witnesses = new HashSet<>();

		List<Operation> operations = new ArrayList<>();

		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("prepend1");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.PREPEND) {
				messages.add("prepend2");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.APPEND) {
				messages.add("append1");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.APPEND) {
				messages.add("append2");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.DELETE) {
				messages.add("delete1");
			}
		});
		operations.add((Operation) (category, element, bindings) -> {
			if (category == OperationCategory.DELETE) {
				messages.add("delete2");
			}
		});

		witnesses.add(witness(4, "whatever", operations));

		Transformer.transform(model, witnesses);
		assertEquals("[prepend1, prepend2, append2, append1, delete1, delete2]", messages.toString());
	}
}
