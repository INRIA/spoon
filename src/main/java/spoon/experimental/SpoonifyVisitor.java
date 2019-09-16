package spoon.experimental;

import org.apache.commons.lang3.StringEscapeUtils;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SpoonifyVisitor extends CtScanner {
	StringBuilder result = new StringBuilder();
	Map<String, Integer> variableCount = new HashMap<>();
	Stack<String> parentName = new Stack<>();
	Stack<Map<CtRole, String>> roleContainer = new Stack<>();

	public boolean printComments = true;
	public boolean printWeirdTabs = true;
	int tabs = 0;

	private String getVariableName(String className) {
		if (!variableCount.containsKey(className)) {
			variableCount.put(className, 0);
		}
		int count = variableCount.get(className);
		String variableName = className.substring(0, 1).toLowerCase() + className.substring(1) + count;
		variableCount.put(className, count + 1);
		return variableName;
	}

	@Override
	public void enter(CtElement element) {
		tabs++;

		String elementClass = element.getClass().getSimpleName();
		if (elementClass.endsWith("Impl")) {
			elementClass = elementClass.replace("Impl", "");
		}
		String variableName = getVariableName(elementClass);

		result.append(printTabs() + elementClass + " " + variableName + " = factory.create" + elementClass.replaceFirst("Ct", "") + "();");
		result.append(" //" + element.prettyprint().replace("\n", ""));
		result.append("\n");

		if (element instanceof CtNamedElement) {
			result.append(printTabs() + variableName + ".setSimpleName(\"" + ((CtNamedElement) element).getSimpleName() + "\");\n");
		}

		if (element instanceof CtReference) {
			result.append(printTabs() + variableName + ".setSimpleName(\"" + ((CtReference) element).getSimpleName() + "\");\n");
		}
		if (element instanceof CtModifiable && !((CtModifiable) element).getModifiers().isEmpty()) {
			result.append(printTabs() + "Set<ModifierKind> " + variableName + "Modifiers = new HashSet<>();\n");
			for (ModifierKind mod : ((CtModifiable) element).getModifiers()) {
				result.append(printTabs() + variableName + "Modifiers.add(ModifierKind." + mod.name() + ");\n");
			}
			result.append(printTabs() + variableName + ".setModifiers(" + variableName + "Modifiers);\n");
		}
		if (element instanceof CtLiteral) {
			if (((CtLiteral) element).getType().isPrimitive()) {
				result.append(printTabs() + variableName + ".setValue(" + ((CtLiteral) element).getValue() + ");\n");
			} else if (((CtLiteral) element).getType().getQualifiedName().equals("java.lang.String")) {
				result.append(printTabs() + variableName + ".setValue(\"" + StringEscapeUtils.escapeJava((String) ((CtLiteral) element).getValue()) + "\");\n");
			}
		}
		if (element instanceof CtBinaryOperator) {
			result.append(printTabs() + variableName + ".setKind(BinaryOperatorKind." + ((CtBinaryOperator) element).getKind().name() + ");\n");
		}
		if (element instanceof CtUnaryOperator) {
			result.append(printTabs() + variableName + ".setKind(UnaryOperatorKind." + ((CtUnaryOperator) element).getKind().name() + ");\n");
		}
		if (element instanceof CtComment) {
			result.append(printTabs() + variableName + ".setContent(\"" + ((CtComment) element).getContent() + "\");\n");

		}

		if (element.isImplicit()) {
			result.append(printTabs() + variableName + ".setImplicit(true);\n");
		}

		if (element.isParentInitialized() && !parentName.isEmpty()) {
			CtRole elementRoleInParent = element.getRoleInParent();

			CtElement parent = element.getParent();
			Object o  = parent.getValueByRole(elementRoleInParent);
			if (o instanceof Map) {
				handleContainer(element, elementRoleInParent, variableName, "Map");
			} else if (o instanceof List) {
				handleContainer(element, elementRoleInParent, variableName, "List");
			} else if (o instanceof Set) {
				handleContainer(element, elementRoleInParent, variableName, "Set");
			} else {
				result.append(printTabs() + parentName.peek() + ".setValueByRole(CtRole." + elementRoleInParent.name() + ", " + variableName + ");\n");
			}
		}
		parentName.push(variableName);
		roleContainer.push(new HashMap<>());
	}

	private void handleContainer(CtElement element, CtRole elementRoleInParent, String variableName, String container) {
		String concreteClass = null;

		switch (container) {
			case "Map":
				concreteClass = "HashMap";
				break;
			case "List":
				concreteClass = "ArrayList";
				break;
			case "Set":
				concreteClass = "HashSet";
				break;
		}

		String containerName;
		if (!roleContainer.peek().containsKey(elementRoleInParent)) {
			containerName = parentName.peek() + elementRoleInParent.toString().substring(0, 1).toUpperCase() + elementRoleInParent.toString().substring(1) + "s";
			roleContainer.peek().put(elementRoleInParent, containerName);
			result.append(printTabs() + container + " " + containerName + " = new " + concreteClass + "();\n");
		} else {
			containerName = roleContainer.peek().get(elementRoleInParent);
		}

		if (container.equals("Map")) {
			result.append(printTabs() + containerName + ".put(" + ((CtNamedElement) element).getSimpleName() + "," + variableName + ");\n");
		} else {
			result.append(printTabs() + containerName + ".add(" + variableName + ");\n");
		}
	}

	@Override
	public void exit(CtElement element) {
		if (!roleContainer.peek().isEmpty()) {
			for (CtRole role: roleContainer.peek().keySet()) {
				String variableName = roleContainer.peek().get(role);
				result.append(printTabs() + parentName.peek() + ".setValueByRole(CtRole." + role.name() + ", " + variableName + ");\n");
				//result.append(variableName + ".clear();\n");
			}
		}
		parentName.pop();
		roleContainer.pop();
		tabs--;
	}

	public String getResult() {
		return result.toString();
	}

	public String printTabs() {
		String res = "";
		if (printWeirdTabs) {
			for (int i = 0; i < tabs; i++) {
				res += "\t";
			}
		}
		return res;
	}
}
