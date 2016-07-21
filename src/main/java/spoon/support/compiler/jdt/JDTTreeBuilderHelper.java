/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;

import java.util.EnumSet;
import java.util.Set;

class JDTTreeBuilderHelper {

	static UnaryOperatorKind getUnaryOperator(int op) {
		switch (op) {
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

	static Set<ModifierKind> getModifiers(int mod) {
		Set<ModifierKind> ret = EnumSet.noneOf(ModifierKind.class);
		if ((mod & ClassFileConstants.AccPublic) != 0) {
			ret.add(ModifierKind.PUBLIC);
		}
		if ((mod & ClassFileConstants.AccPrivate) != 0) {
			ret.add(ModifierKind.PRIVATE);
		}
		if ((mod & ClassFileConstants.AccProtected) != 0) {
			ret.add(ModifierKind.PROTECTED);
		}
		if ((mod & ClassFileConstants.AccStatic) != 0) {
			ret.add(ModifierKind.STATIC);
		}
		if ((mod & ClassFileConstants.AccFinal) != 0) {
			ret.add(ModifierKind.FINAL);
		}
		if ((mod & ClassFileConstants.AccSynchronized) != 0) {
			ret.add(ModifierKind.SYNCHRONIZED);
		}
		if ((mod & ClassFileConstants.AccVolatile) != 0) {
			ret.add(ModifierKind.VOLATILE);
		}
		if ((mod & ClassFileConstants.AccTransient) != 0) {
			ret.add(ModifierKind.TRANSIENT);
		}
		if ((mod & ClassFileConstants.AccAbstract) != 0) {
			ret.add(ModifierKind.ABSTRACT);
		}
		if ((mod & ClassFileConstants.AccStrictfp) != 0) {
			ret.add(ModifierKind.STRICTFP);
		}
		if ((mod & ClassFileConstants.AccNative) != 0) {
			ret.add(ModifierKind.NATIVE);
		}
		return ret;
	}

	static BinaryOperatorKind getBinaryOperatorKind(int bits) {
		// switch ((bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
		switch (bits) {
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
	 * Searches a type from an entry-point according to a simple name.
	 *
	 * @param type       Entry-point to search.
	 * @param simpleName Expected type name.
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
	 * Searches a type in the project.
	 *
	 * @param qualifiedName Qualified name of the expected type.
	 * @return type binding.
	 */
	static TypeBinding searchTypeBinding(String qualifiedName, TreeBuilderCompiler treeBuilderCompiler) {
		if (qualifiedName == null) {
			return null;
		}
		for (CompilationUnitDeclaration unitsToProcess : treeBuilderCompiler.unitsToProcess) {
			for (TypeDeclaration type : unitsToProcess.types) {
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
		return null;
	}

	static String computeAnonymousName(SourceTypeBinding binding) {
		final String poolName = String.valueOf(binding.constantPoolName());
		final int lastIndexSeparator = poolName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR);
		return poolName.substring(lastIndexSeparator + 1, poolName.length());
	}

	static String createTypeName(char[][] typeName) {
		String s = "";
		for (int i = 0; i < typeName.length - 1; i++) {
			s += new String(typeName[i]) + ".";
		}
		s += new String(typeName[typeName.length - 1]);
		return s;
	}


	/**
	 * Checks to know if a name is a package or not.
	 *
	 * @param packageName Package name.
	 * @return boolean
	 */
	static boolean isPackage(char[][] packageName, TreeBuilderCompiler treeBuilderCompiler) {
		for (CompilationUnitDeclaration unit : treeBuilderCompiler.unitsToProcess) {
			final char[][] tokens = unit.currentPackage.tokens;
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
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a type is specified in imports.
	 *
	 * @param typeName Type name.
	 * @return qualified name of the expected type.
	 */
	static String hasTypeInImports(String typeName, ContextBuilder context) {
		if (typeName == null) {
			return null;
		} else if (context.compilationunitdeclaration.imports == null) {
			return null;
		}
		for (ImportReference anImport : context.compilationunitdeclaration.imports) {
			final String importType = CharOperation.charToString(anImport.getImportName()[anImport.getImportName().length - 1]);
			if (importType != null && importType.equals(typeName)) {
				return CharOperation.toString(anImport.getImportName());
			}
		}
		return null;
	}

	/**
	 * Check if the annotation is declared with the given element type.
	 *
	 * @param a           An annotation.
	 * @param elementType Type of the annotation.
	 * @return true if the annotation is compatible with the given element type.
	 */
	private static boolean hasAnnotationWithType(Annotation a, CtAnnotatedElementType elementType) {
		if (a.resolvedType == null) {
			return false;
		}
		for (AnnotationBinding annotation : a.resolvedType.getAnnotations()) {
			if (!"Target".equals(CharOperation.charToString(annotation.getAnnotationType().sourceName()))) {
				continue;
			}
			Object value = annotation.getElementValuePairs()[0].value;
			if (value == null || !value.getClass().isArray()) {
				continue;
			}
			Object[] fields = (Object[]) value;
			for (Object field : fields) {
				if (field instanceof FieldBinding && elementType.name().equals(CharOperation.charToString(((FieldBinding) field).name))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * When an annotation is specified on a method, a local declaration or an argument, JDT doesn't know
	 * if the annotation is specified on the type or on the element. Due to this method, if the annotation
	 * is compatible with types, we substitute the annotation from the element to the type of this element.
	 *
	 * @param typedElement Element annotated.
	 * @param a            Annotation of the element.
	 * @param elementType  Type of the annotation.
	 */
	static void substituteAnnotation(CtTypedElement<Object> typedElement, Annotation a, CtAnnotatedElementType elementType) {
		if (JDTTreeBuilderHelper.hasAnnotationWithType(a, elementType)) {
			CtAnnotation<? extends java.lang.annotation.Annotation> targetAnnotation = typedElement.getAnnotations().get(typedElement.getAnnotations().size() - 1);
			typedElement.removeAnnotation(targetAnnotation);
			typedElement.getType().addAnnotation(targetAnnotation);
		}
	}
}
