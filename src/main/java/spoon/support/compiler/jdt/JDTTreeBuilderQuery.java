/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;

import java.util.HashSet;
import java.util.Set;

/** Helper class for JDTTreeBuilder. Package visible to reduce API surface. */
class JDTTreeBuilderQuery {
	private JDTTreeBuilderQuery() { }
	/**
	 * Searches a type from an entry-point according to a simple name.
	 *
	 * @param type
	 * 		Entry-point to search.
	 * @param simpleName
	 * 		Expected type name.
	 * @return type binding.
	 */
	static TypeBinding searchTypeBinding(ReferenceBinding type, String simpleName) {
		if (simpleName == null || type == null) {
			return null;
		}

		if (type.memberTypes() != null) {
			for (ReferenceBinding memberType : type.memberTypes()) {
				if (simpleName.equals(CharOperation.charToString(memberType.sourceName()))) {
					return memberType;
				}
			}
		}

		return searchTypeBinding(type.superclass(), simpleName);
	}

	/**
	 * Searches a type used in units declared in a compilation unit.
	 *
	 * @param qualifiedName
	 * 		Qualified name of the expected type.
	 * @param unitsToProcess
	 * 		Search the type expected in units.
	 * @return type binding.
	 */
	static TypeBinding searchTypeBinding(String qualifiedName, CompilationUnitDeclaration[] unitsToProcess) {
		if (qualifiedName == null) {
			return null;
		}
		for (CompilationUnitDeclaration unitToProcess : unitsToProcess) {
			if (unitToProcess.types != null) {
				for (TypeDeclaration type : unitToProcess.types) {
					if (qualifiedName.equals(CharOperation.toString(type.binding.compoundName))) {
						return type.binding;
					}
					if (type.memberTypes != null) {
						for (TypeDeclaration memberType : type.memberTypes) {
							if (qualifiedName.equals(CharOperation.toString(memberType.binding.compoundName))) {
								return type.binding;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches a type declared in imports.
	 *
	 * @param typeName
	 * 		Expected type name.
	 * @param imports
	 * 		Search the type in imports.
	 * @return qualified name of the expected type.
	 */
	static String searchType(String typeName, ImportReference[] imports) {
		if (typeName == null || imports == null) {
			return null;
		}
		for (ImportReference anImport : imports) {
			final String importType = CharOperation.charToString(anImport.getImportName()[anImport.getImportName().length - 1]);
			if (typeName.equals(importType)) {
				return CharOperation.toString(anImport.getImportName());
			}
		}
		return null;
	}

	/**
	 * Searches a package used in units declared in a compilation unit.
	 *
	 * @param packageName
	 * 		Package name.
	 * @param unitsToProcess
	 * 		Search the package expected in units.
	 * @return import reference which correspond to the package expected.
	 */
	static ImportReference searchPackage(char[][] packageName, CompilationUnitDeclaration[] unitsToProcess) {
		for (CompilationUnitDeclaration unit : unitsToProcess) {
			final ImportReference currentPackage = unit.currentPackage;
			if (currentPackage == null) {
				continue;
			}
			final char[][] tokens = currentPackage.tokens;
			if (packageName.length > tokens.length) {
				continue;
			}
			boolean isFound = true;
			for (int i = 0; i < packageName.length; i++) {
				char[] chars = packageName[i];
				if (!CharOperation.equals(chars, tokens[i])) {
					isFound = false;
					break;
				}
			}
			if (isFound) {
				return currentPackage;
			}
		}
		return null;
	}

	/**
	 * Checks in an annotation if a given type is present.
	 *
	 * @param a
	 * 		An annotation.
	 * @param elementType
	 * 		Expected element type of the annotation.
	 * @return true if the annotation is compatible with the given element type.
	 */
	static boolean hasAnnotationWithType(Annotation a, CtAnnotatedElementType elementType) {
		if (a.resolvedType == null) {
			return false;
		}

		// JLS says:
		//  "If an annotation of type java.lang.annotation.Target is not present on the declaration of an annotation type T,
		// then T is applicable in all declaration contexts except type parameter declarations, and in no type contexts."
		boolean shouldTargetAnnotationExists = (elementType == CtAnnotatedElementType.TYPE_USE || elementType == CtAnnotatedElementType.TYPE_PARAMETER);
		boolean targetAnnotationExists = false;

		for (AnnotationBinding annotation : a.resolvedType.getAnnotations()) {
			if (!"Target".equals(CharOperation.charToString(annotation.getAnnotationType().sourceName()))) {
				continue;
			}
			targetAnnotationExists = true;
			Object value = annotation.getElementValuePairs()[0].value;
			if (value == null) {
				continue;
			}
			if (value instanceof FieldBinding && elementType.name().equals(CharOperation.charToString(((FieldBinding) value).name))) {
				return true;
			}
			if (value.getClass().isArray()) {
				Object[] fields = (Object[]) value;
				for (Object field : fields) {
					if (field instanceof FieldBinding && elementType.name().equals(CharOperation.charToString(((FieldBinding) field).name))) {
						return true;
					}
				}
			}
		}

		// true here means that the target annotation is not mandatory and we have not found it
		return !shouldTargetAnnotationExists && !targetAnnotationExists;
	}

	/**
	 * Checks if the qualified name reference is a problem field binding and have a valid field.
	 *
	 * @param qualifiedNameReference
	 * 		Reference which should contains a problem field binding.
	 * @return true if the qualified name reference is a valid problem field binding.
	 */
	static boolean isValidProblemBindingField(QualifiedNameReference qualifiedNameReference) {
		return qualifiedNameReference.binding instanceof ProblemFieldBinding && !((FieldBinding) qualifiedNameReference.binding).declaringClass.isAnonymousType()
				&& qualifiedNameReference.tokens.length - 1 == ((FieldBinding) qualifiedNameReference.binding).declaringClass.compoundName.length && CharOperation
				.equals(CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1),
						((FieldBinding) qualifiedNameReference.binding).declaringClass.compoundName);
	}

	/**
	 * Check if the name reference is resolved in the JDT tree, i.e. that the declaration is available.
	 *
	 * @param qualifiedNameReference
	 * 		Reference which should contain a field binding.
	 * @return true if the field has been resolved by the jdt builder.
	 */
	static boolean isResolvedField(QualifiedNameReference qualifiedNameReference) {
		return qualifiedNameReference.binding instanceof FieldBinding
				&& ((FieldBinding) qualifiedNameReference.binding).original().sourceField() != null;
	}


	/**
	 * Checks if the last node in the stack in the context is an assignment and have a lhs equals to the given expression.
	 *
	 * @param context
	 * 		Context of the {@link JDTTreeBuilder}.
	 * @param lhs
	 * 		Potential lhs of the assignment.
	 * @return true if the lhs is equals to the given expression.
	 */
	static boolean isLhsAssignment(ContextBuilder context, Expression lhs) {
		return context.stack.peek().node instanceof Assignment && ((Assignment) context.stack.peek().node).lhs.equals(lhs);
	}

	/**
	 * Converts the unary operator from JDT to Spoon.
	 *
	 * @param operator
	 * 		Identifier of the unary operator.
	 * @return enum value of {@link UnaryOperatorKind}.
	 */
	static UnaryOperatorKind getUnaryOperator(int operator) {
		switch (operator) {
		case OperatorIds.PLUS:
			return UnaryOperatorKind.POS;
		case OperatorIds.MINUS:
			return UnaryOperatorKind.NEG;
		case OperatorIds.NOT:
			return UnaryOperatorKind.NOT;
		case OperatorIds.TWIDDLE:
			return UnaryOperatorKind.COMPL;
		}
		return null;
	}

	/**
	 * Converts the binary operator from JDT to Spoon.
	 *
	 * @param operator
	 * 		Identifier of the binary operator.
	 * @return enum value of {@link BinaryOperatorKind}.
	 */
	static BinaryOperatorKind getBinaryOperatorKind(int operator) {
		switch (operator) {
		case OperatorIds.EQUAL_EQUAL:
			return BinaryOperatorKind.EQ;
		case OperatorIds.LESS_EQUAL:
			return BinaryOperatorKind.LE;
		case OperatorIds.GREATER_EQUAL:
			return BinaryOperatorKind.GE;
		case OperatorIds.NOT_EQUAL:
			return BinaryOperatorKind.NE;
		case OperatorIds.LEFT_SHIFT:
			return BinaryOperatorKind.SL;
		case OperatorIds.RIGHT_SHIFT:
			return BinaryOperatorKind.SR;
		case OperatorIds.UNSIGNED_RIGHT_SHIFT:
			return BinaryOperatorKind.USR;
		case OperatorIds.OR_OR:
			return BinaryOperatorKind.OR;
		case OperatorIds.AND_AND:
			return BinaryOperatorKind.AND;
		case OperatorIds.PLUS:
			return BinaryOperatorKind.PLUS;
		case OperatorIds.MINUS:
			return BinaryOperatorKind.MINUS;
		case OperatorIds.NOT:
			return BinaryOperatorKind.NE;
		case OperatorIds.REMAINDER:
			return BinaryOperatorKind.MOD;
		case OperatorIds.XOR:
			return BinaryOperatorKind.BITXOR;
		case OperatorIds.AND:
			return BinaryOperatorKind.BITAND;
		case OperatorIds.MULTIPLY:
			return BinaryOperatorKind.MUL;
		case OperatorIds.OR:
			return BinaryOperatorKind.BITOR;
		case OperatorIds.DIVIDE:
			return BinaryOperatorKind.DIV;
		case OperatorIds.GREATER:
			return BinaryOperatorKind.GT;
		case OperatorIds.LESS:
			return BinaryOperatorKind.LT;
		case OperatorIds.QUESTIONCOLON:
			throw new RuntimeException("Unknown operator");
		case OperatorIds.EQUAL:
			return BinaryOperatorKind.EQ;
		}
		return null;
	}

	/**
	 * Converts the modifier from JDT to Spoon.
	 *
	 * @param modifier
	 * 		Identifier of the modifier.
	 * @param implicit True if the modifier is not explicit in the source code (e.g. a missing 'public' in an interface)
	 * @return Set of enum value of {@link CtExtendedModifier}.
	 */
	static Set<CtExtendedModifier> getModifiers(int modifier, boolean implicit, boolean isMethod) {
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		if ((modifier & ClassFileConstants.AccPublic) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.PUBLIC, implicit));
		}
		if ((modifier & ClassFileConstants.AccPrivate) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.PRIVATE, implicit));
		}
		if ((modifier & ClassFileConstants.AccProtected) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.PROTECTED, implicit));
		}
		if ((modifier & ClassFileConstants.AccStatic) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.STATIC, implicit));
		}
		if ((modifier & ClassFileConstants.AccFinal) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.FINAL, implicit));
		}
		if ((modifier & ClassFileConstants.AccSynchronized) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.SYNCHRONIZED, implicit));
		}
		if ((modifier & ClassFileConstants.AccVolatile) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.VOLATILE, implicit));
		}
		// a method can never be transient, but it can have the flag because of varArgs.
		// source: https://stackoverflow.com/questions/16233910/can-transient-keywords-mark-a-method
		if (!isMethod && (modifier & ClassFileConstants.AccTransient) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.TRANSIENT, implicit));
		}
		if ((modifier & ClassFileConstants.AccAbstract) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.ABSTRACT, implicit));
		}
		if ((modifier & ClassFileConstants.AccStrictfp) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.STRICTFP, implicit));
		}
		if ((modifier & ClassFileConstants.AccNative) != 0) {
			modifiers.add(new CtExtendedModifier(ModifierKind.NATIVE, implicit));
		}
		return modifiers;
	}
}
