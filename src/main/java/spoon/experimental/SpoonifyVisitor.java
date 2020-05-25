/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
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
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtInheritanceScanner;
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
//public class SpoonifyVisitor extends CtInheritanceScanner {
	StringBuilder result = new StringBuilder();
	Map<String, Integer> variableCount = new HashMap<>();
	Stack<String> parentName = new Stack<>();
	Stack<Map<CtRole, String>> roleContainer = new Stack<>();

	PropertyScanner propertyScanner = new PropertyScanner();

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

	private boolean isLeafTypeReference(CtElement element) {
		if (!(element instanceof CtTypeReference)) {
			return false;
		}
		if (element instanceof CtArrayTypeReference
				|| element instanceof CtWildcardReference
				|| element instanceof CtTypeParameterReference
				|| element instanceof CtIntersectionTypeReference) {
			return false;
		}
		CtTypeReference reference = (CtTypeReference) element;
		return reference.getDeclaringType() == null
				&& reference.getActualTypeArguments().isEmpty()
				&& reference.getAnnotations().isEmpty()
				&& reference.getComments().isEmpty();
	}


	public void enter(CtElement element) {
		if (element instanceof CtPackageReference
				&& isLeafTypeReference(element.getParent())) {
			return;
		}
		tabs++;

		String elementClass = element.getClass().getSimpleName();
		if (elementClass.endsWith("Impl")) {
			elementClass = elementClass.replace("Impl", "");
		}

		String variableName = null;
		if (isLeafTypeReference(element)) {
			CtTypeReference typeRef = (CtTypeReference) element;
			if (typeRef.isPrimitive()) {
				switch (typeRef.getSimpleName()) {
					case "int":
						variableName = "factory.Type().INTEGER_PRIMITIVE";
						break;
					case "char":
						variableName = "factory.Type().CHARACTER_PRIMITIVE";
						break;
					default:
						variableName = "factory.Type()." + typeRef.getSimpleName().toUpperCase() + "_PRIMITIVE";
				}
			} else if (typeRef.getSimpleName().equals("<nulltype>")) {
				variableName = "factory.Type().NULL_TYPE";
			} else if (typeRef.getPackage().isImplicit()) {
				variableName =  "factory.Type().createSimplyQualifiedReference(\"" + typeRef.getQualifiedName() + "\")";
			} else {
				variableName =  "factory.Type().createReference(\"" + typeRef.getQualifiedName() + "\")";
			}
		} else {
			variableName = getVariableName(elementClass);
			result.append(printTabs() + elementClass + " " + variableName + " = factory.create" + elementClass.replaceFirst("Ct", "") + "();");
			result.append("\n");

			if (element.isImplicit()) {
				result.append(printTabs() + variableName + ".setImplicit(true);\n");
			}
			propertyScanner.variableName = variableName;
			element.accept(propertyScanner);
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

	public void exit(CtElement element) {
		if (element instanceof CtPackageReference
				&& isLeafTypeReference(element.getParent())) {
			return;
		}
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

	class PropertyScanner extends CtInheritanceScanner {
		public String variableName;

		/**
		 * visitLiteral
		 * @param element
		 */
		@Override
		public void visitCtLiteral(CtLiteral element) {
			if (element.getType().isPrimitive()) {
				result.append(printTabs() + variableName + ".setValue((" + element.getType().getSimpleName() + ") " + element.toString() + ");\n");
				if (element.getBase() != null) {
					result.append(printTabs() + variableName + ".setBase(LiteralBase." + element.getBase().name() + ");\n");
				}
			} else if (element.getType().getQualifiedName().equals("java.lang.String")) {
				result.append(printTabs() + variableName + ".setValue(\"" + StringEscapeUtils.escapeJava((String) element.getValue()) + "\");\n");
			}
			super.visitCtLiteral(element);
		}

		@Override
		public void visitCtBinaryOperator(CtBinaryOperator element) {
			result.append(printTabs() + variableName + ".setKind(BinaryOperatorKind." + element.getKind().name() + ");\n");
			super.visitCtBinaryOperator(element);
		}

		@Override
		public void visitCtUnaryOperator(CtUnaryOperator element) {
			result.append(printTabs() + variableName + ".setKind(UnaryOperatorKind." + element.getKind().name() + ");\n");
			super.visitCtUnaryOperator(element);
		}

		@Override
		public void visitCtOperatorAssignment(CtOperatorAssignment element) {
			result.append(printTabs() + variableName + ".setKind(BinaryOperatorKind." + element.getKind().name() + ");\n");
			super.visitCtOperatorAssignment(element);
		}

		@Override
		public void visitCtComment(CtComment element) {
			result.append(printTabs() + variableName + ".setCommentType(CtComment.CommentType." + element.getCommentType().name() + ");\n");
			result.append(printTabs() + variableName + ".setContent(\"" + StringEscapeUtils.escapeJava(element.getContent()) + "\");\n");
			super.visitCtComment(element);
		}

		@Override
		public void visitCtParameter(CtParameter element) {
			if (element.isVarArgs()) {
				result.append(printTabs() + variableName + ".setVarArgs(true);\n");
			}
			super.visitCtParameter(element);
		}

		@Override
		public void visitCtMethod(CtMethod element) {
			if (element.isDefaultMethod()) {
				result.append(printTabs() + variableName + ".setDefaultMethod(true);\n");
			}
			super.visitCtMethod(element);
		}

		@Override
		public void scanCtReference(CtReference element) {
			result.append(printTabs() + variableName + ".setSimpleName(\"" + element.getSimpleName() + "\");\n");
		}

		@Override
		public void scanCtNamedElement(CtNamedElement element) {
			result.append(printTabs() + variableName + ".setSimpleName(\"" + element.getSimpleName() + "\");\n");
		}

		@Override
		public void scanCtModifiable(CtModifiable element) {
			if (!element.getModifiers().isEmpty()) {
				result.append(printTabs() + "Set<ModifierKind> " + variableName + "Modifiers = new HashSet<>();\n");
				for (ModifierKind mod : element.getModifiers()) {
					result.append(printTabs() + variableName + "Modifiers.add(ModifierKind." + mod.name() + ");\n");
				}
				result.append(printTabs() + variableName + ".setModifiers(" + variableName + "Modifiers);\n");
			}
		}

		@Override
		public void scanCtStatement(CtStatement element) {
			if (element.getLabel() != null) {
				result.append(printTabs() + variableName + ".setLabel(\"" + element.getLabel() + "\");\n");
			}
		}

		@Override
		public void scanCtLabelledFlowBreak(CtLabelledFlowBreak element) {
			if (element.getTargetLabel() != null) {
				result.append(printTabs() + variableName + ".setTargetLabel(\"" + element.getTargetLabel() + "\");\n");
			}
		}
	}
}
