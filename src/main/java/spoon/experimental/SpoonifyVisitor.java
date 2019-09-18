/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.experimental;

import org.apache.commons.lang3.StringEscapeUtils;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Visitor that generates factory calls to recreate the AST visited.
 *
 */
public class SpoonifyVisitor extends CtScanner {
	StringBuilder result = new StringBuilder();
	Map<String, Integer> variableCount = new HashMap<>();
	Stack<String> parentName = new Stack<>();
	Stack<Map<CtRole, String>> roleContainer = new Stack<>();

	public boolean printTabs;
	int tabs = 0;

	/**
	 * Default constructor for SpoonifyVisitor.
	 * Print with tabulations.
	 */
	public SpoonifyVisitor() {
		this(true);
	}

	/**
	 * @param printTabs if set to true, tabulations will be printed to match the structure of the AST constructed.
	 */
	public SpoonifyVisitor(boolean printTabs) {
		this.printTabs = printTabs;
	}

	/**
	 * @return the generated code.
	 *
	 * Note that this code assume a variable Factory factory is already aessible in the scope.
	 */
	public String getResult() {
		return result.toString();
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
		result.append("\n");


		//TODO: rewrite this list of special cases by checking for annotations of the meta-model
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
				result.append(printTabs() + variableName + ".setValue((" + ((CtLiteral) element).getType().getSimpleName() + ") " + ((CtLiteral) element).toString() + ");\n");
				if (((CtLiteral) element).getBase() != null) {
					result.append(printTabs() + variableName + ".setBase(LiteralBase." + ((CtLiteral) element).getBase().name() + ");\n");
				}
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
		if (element instanceof CtOperatorAssignment) {
			result.append(printTabs() + variableName + ".setKind(BinaryOperatorKind." + ((CtOperatorAssignment) element).getKind().name() + ");\n");
		}
		if (element instanceof CtComment) {
			result.append(printTabs() + variableName + ".setCommentType(CtComment.CommentType." + ((CtComment) element).getCommentType().name() + ");\n");
			result.append(printTabs() + variableName + ".setContent(\"" + StringEscapeUtils.escapeJava(((CtComment) element).getContent()) + "\");\n");
		}
		if (element instanceof CtParameter && ((CtParameter) element).isVarArgs()) {
			result.append(printTabs() + variableName + ".setVarArgs(true);\n");
		}
		if (element instanceof CtMethod && ((CtMethod) element).isDefaultMethod()) {
			result.append(printTabs() + variableName + ".setDefaultMethod(true);\n");
		}
		if (element instanceof CtStatement && ((CtStatement) element).getLabel() != null) {
			result.append(printTabs() + variableName + ".setLabel(\"" + ((CtStatement) element).getLabel() + "\");\n");
		}
		if (element instanceof CtLabelledFlowBreak && ((CtLabelledFlowBreak) element).getTargetLabel() != null) {
			result.append(printTabs() + variableName + ".setTargetLabel(\"" + ((CtLabelledFlowBreak) element).getTargetLabel() + "\");\n");
		}

		if (element.isImplicit()) {
			result.append(printTabs() + variableName + ".setImplicit(true);\n");
		}

		if (element.isParentInitialized() && !parentName.isEmpty()) {
			CtRole elementRoleInParent = element.getRoleInParent();

			CtElement parent = element.getParent();
			Object o  = parent.getValueByRole(elementRoleInParent);
			if (o instanceof Map) {
				handleContainer(element, parent, elementRoleInParent, variableName, "Map");
			} else if (o instanceof List) {
				handleContainer(element, parent, elementRoleInParent, variableName, "List");
			} else if (o instanceof Set) {
				handleContainer(element, parent, elementRoleInParent, variableName, "Set");
			} else {
				result.append(printTabs() + parentName.peek() + ".setValueByRole(CtRole." + elementRoleInParent.name() + ", " + variableName + ");\n");
			}
		}
		parentName.push(variableName);
		roleContainer.push(new HashMap<>());
	}

	private String getVariableName(String className) {
		if (!variableCount.containsKey(className)) {
			variableCount.put(className, 0);
		}
		int count = variableCount.get(className);
		String variableName = className.substring(0, 1).toLowerCase() + className.substring(1) + count;
		variableCount.put(className, count + 1);
		return variableName;
	}

	private void handleContainer(CtElement element, CtElement parent, CtRole elementRoleInParent, String variableName, String container) {
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
			//This is going to be dirty.
			//In case where different keys point toward the same value,
			//some useless variable will be created.
			List<String> keys = new ArrayList<>();
			Map m = parent.getValueByRole(elementRoleInParent);
			for (Object e : m.entrySet()) {
				Map.Entry entry = (Map.Entry) e;
				if (entry.getValue().equals(element)) {
					keys.add((String) entry.getKey());
				}
			}
			for (String key: keys) {
				result.append(printTabs() + containerName + ".put(\"" + key + "\", " + variableName + ");\n");
			}

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
			}
		}
		parentName.pop();
		roleContainer.pop();
		tabs--;
	}

	private String printTabs() {
		String res = "";
		if (printTabs) {
			for (int i = 0; i < tabs; i++) {
				res += "\t";
			}
		}
		return res;
	}
}
