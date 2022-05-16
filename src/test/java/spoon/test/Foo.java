package spoon.test;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Foo {

	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.addInputResource("/tmp/test/Test.java");
		CtModel ctModel = launcher.buildModel();

		Map<CtVariable<?>, List<CtVariableAccess<?>>> varUsages = new HashMap<>();

		ctModel.processWith(new AbstractProcessor<CtVariableAccess<?>>() {
			@Override
			public void process(CtVariableAccess<?> element) {
				CtVariable<?> variable = element.getVariable().getDeclaration();
				varUsages.computeIfAbsent(variable, it -> new ArrayList<>())
					.add(element);
			}
		});

		ctModel.processWith(new AbstractProcessor<CtInvocation<?>>() {
			@Override
			public void process(CtInvocation<?> invocation) {
				if (!invocation.getExecutable().getSimpleName().equals("executeQuery")) {
					return;
				}
				CtVariableRead<?> queryArgument = (CtVariableRead<?>) invocation.getArguments().get(0);
				CtExpression<?> sbToStringCall = queryArgument.getVariable().getDeclaration().getDefaultExpression();
				CtVariable<?> sb = sbToStringCall.getElements(new TypeFilter<>(CtVariableAccess.class))
					.get(0)
					.getVariable()
					.getDeclaration();

				String query = varUsages.get(sb).stream()
					.filter(it -> it.getParent(CtInvocation.class).getExecutable().getSimpleName().equals("append"))
					.map(this::stringFromSbAppend)
					.collect(Collectors.joining());
				System.out.println(query);
			}

			private String stringFromSbAppend(CtVariableAccess<?> access) {
				CtInvocation<?> invocation = access.getParent(CtInvocation.class);
				CtVariable<?> variable = ((CtVariableAccess<?>) invocation.getArguments().get(0))
					.getVariable()
					.getDeclaration();

				CtLiteral<String> stringVal = (CtLiteral<String>) variable.getDefaultExpression();
				return stringVal.getValue();
			}
		});
	}
}
