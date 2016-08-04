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

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.computeAnonymousName;
import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.createTypeName;
import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.getBinaryOperatorKind;
import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.getModifiers;
import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.getUnaryOperator;
import static spoon.support.compiler.jdt.JDTTreeBuilderHelper.substituteAnnotation;

/**
 * A visitor for iterating through the parse tree.
 */
public class JDTTreeBuilder extends ASTVisitor {

	private final PositionBuilder position;

	private final ContextBuilder context;

	private final ParentExiter exiter;

	private final ReferenceBuilder references;

	private final Factory factory;

	public boolean template = false;

	boolean skipTypeInAnnotation = false;

	boolean defaultValue;

	public static Logger getLogger() {
		return LOGGER;
	}

	private static final Logger LOGGER = Logger.getLogger(JDTTreeBuilder.class);

	public PositionBuilder getPositionBuilder() {
		return position;
	}

	public ContextBuilder getContextBuilder() {
		return context;
	}

	public ReferenceBuilder getReferencesBuilder() {
		return references;
	}

	public ParentExiter getExiter() {
		return exiter;
	}

	public Factory getFactory() {
		return factory;
	}

	public JDTTreeBuilder(Factory factory) {
		super();
		this.factory = factory;
		this.position = new PositionBuilder(this);
		this.context = new ContextBuilder(this);
		this.exiter = new ParentExiter(this);
		this.references = new ReferenceBuilder(this);
		this.LOGGER.setLevel(factory.getEnvironment().getLevel());
	}

	interface OnAccessListener {
		boolean onAccess(char[][] tokens, int index);
	}

	/**
	 * Sets {@code declaring} as inner of {@code ref}, as either the package or the declaring type
	 */
	void setPackageOrDeclaringType(CtTypeReference<?> ref, CtReference declaring) {
		if (declaring instanceof CtPackageReference) {
			ref.setPackage((CtPackageReference) declaring);
		} else if (declaring instanceof CtTypeReference) {
			ref.setDeclaringType((CtTypeReference) declaring);
		} else if (declaring == null) {
			ref.setPackage(factory.Package().topLevel());
		} else {
			throw new AssertionError(
					"unexpected declaring type: " + declaring.getClass() + " of " + declaring);
		}
	}

	CtType<?> createType(TypeDeclaration typeDeclaration) {
		CtType<?> type = null;
		if ((typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0) {
			type = factory.Core().<java.lang.annotation.Annotation>createAnnotationType();
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccEnum) != 0) {
			type = factory.Core().createEnum();
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccInterface) != 0) {
			type = factory.Core().createInterface();
		} else {
			type = factory.Core().createClass();
		}
		context.enter(type, typeDeclaration);

		if (typeDeclaration.superInterfaces != null) {
			for (TypeReference ref : typeDeclaration.superInterfaces) {
				final CtTypeReference superInterface = this.references.buildTypeReference(ref, null);
				type.addSuperInterface(superInterface);
			}
		}

		if (type instanceof CtClass) {
			if (typeDeclaration.superclass != null && typeDeclaration.superclass.resolvedType != null && typeDeclaration.enclosingType != null && !new String(
					typeDeclaration.superclass.resolvedType.qualifiedPackageName()).equals(new String(typeDeclaration.binding.qualifiedPackageName()))) {

				// Sorry for this hack but see the test case ImportTest#testImportOfAnInnerClassInASuperClassPackage.
				// JDT isn't smart enough to return me a super class available. So, I modify their AST when
				// superclasses aren't in the same package and when their visibilities are "default".
				List<ModifierKind> modifiers = Arrays.asList(ModifierKind.PUBLIC, ModifierKind.PROTECTED);
				final TypeBinding resolvedType = typeDeclaration.superclass.resolvedType;
				if ((resolvedType instanceof MemberTypeBinding || resolvedType instanceof BinaryTypeBinding)
						&& resolvedType.enclosingType() != null
						&& typeDeclaration.enclosingType.superclass != null
						&& Collections.disjoint(modifiers, getModifiers(resolvedType.enclosingType().modifiers))) {
					typeDeclaration.superclass.resolvedType = new SpoonReferenceBinding(typeDeclaration.superclass.resolvedType.sourceName(),
							(ReferenceBinding) typeDeclaration.enclosingType.superclass.resolvedType);
				}
			}
			if (typeDeclaration.superclass != null) {
				((CtClass) type).setSuperclass(this.references.buildTypeReference(typeDeclaration.superclass, typeDeclaration.scope));
			}
			if (typeDeclaration.binding.isAnonymousType() || (typeDeclaration.binding instanceof LocalTypeBinding && typeDeclaration.binding.enclosingMethod() != null)) {
				type.setSimpleName(computeAnonymousName(typeDeclaration.binding));
			} else {
				type.setSimpleName(new String(typeDeclaration.name));
			}
		} else {
			type.setSimpleName(new String(typeDeclaration.name));
		}

		// Setting modifiers
		type.setModifiers(getModifiers(typeDeclaration.modifiers));

		return type;
	}

	class SpoonReferenceBinding extends ReferenceBinding {
		private ReferenceBinding enclosingType;

		SpoonReferenceBinding(char[] sourceName, ReferenceBinding enclosingType) {
			this.sourceName = sourceName;
			this.enclosingType = enclosingType;
		}

		@Override
		public ReferenceBinding enclosingType() {
			return enclosingType;
		}
	}


	@Override
	public void endVisit(AllocationExpression allocationExpression, BlockScope scope) {
		context.exit(allocationExpression);
	}

	@Override
	public void endVisit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		context.exit(and_and_Expression);
	}

	@Override
	public void endVisit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope) {
		context.exit(annotationTypeDeclaration);
	}

	@Override
	public void endVisit(Argument argument, BlockScope scope) {
		context.exit(argument);
	}

	@Override
	public void endVisit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {
		context.exit(arrayAllocationExpression);
	}

	@Override
	public void endVisit(ArrayInitializer arrayInitializer, BlockScope scope) {
		context.exit(arrayInitializer);
	}

	@Override
	public void endVisit(ArrayReference arrayReference, BlockScope scope) {
		context.exit(arrayReference);
	}

	@Override
	public void endVisit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		context.exit(arrayTypeReference);
	}

	@Override
	public void endVisit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
		context.exit(arrayTypeReference);
	}

	@Override
	public void endVisit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope) {
		context.exit(arrayQualifiedTypeReference);
	}

	@Override
	public void endVisit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope) {
		context.exit(arrayQualifiedTypeReference);
	}

	@Override
	public void endVisit(AssertStatement assertStatement, BlockScope scope) {
		context.exit(assertStatement);
	}

	@Override
	public void endVisit(Assignment assignment, BlockScope scope) {
		context.exit(assignment);
	}

	@Override
	public void endVisit(BinaryExpression binaryExpression, BlockScope scope) {
		context.exit(binaryExpression);
	}

	@Override
	public void endVisit(Block block, BlockScope scope) {
		context.exit(block);
	}

	@Override
	public void endVisit(BreakStatement breakStatement, BlockScope scope) {
		context.exit(breakStatement);
	}

	@Override
	public void endVisit(CaseStatement caseStatement, BlockScope scope) {
	}

	@Override
	public void endVisit(CharLiteral charLiteral, BlockScope scope) {
		context.exit(charLiteral);
	}

	@Override
	public void endVisit(ClassLiteralAccess classLiteral, BlockScope scope) {
		context.exit(classLiteral);
	}

	@Override
	public void endVisit(CompoundAssignment compoundAssignment, BlockScope scope) {
		context.exit(compoundAssignment);
	}

	@Override
	public void endVisit(ConditionalExpression conditionalExpression, BlockScope scope) {
		context.exit(conditionalExpression);
	}

	@Override
	public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		context.exit(constructorDeclaration);
		if (context.stack.peek().node == constructorDeclaration) {
			context.exit(constructorDeclaration);
		}
	}

	@Override
	public void endVisit(ContinueStatement continueStatement, BlockScope scope) {
		context.exit(continueStatement);
	}

	@Override
	public void endVisit(DoStatement doStatement, BlockScope scope) {
		context.exit(doStatement);
	}

	@Override
	public void endVisit(DoubleLiteral doubleLiteral, BlockScope scope) {
		context.exit(doubleLiteral);
	}

	@Override
	public void endVisit(EqualExpression equalExpression, BlockScope scope) {
		context.exit(equalExpression);
	}

	@Override
	public void endVisit(ExplicitConstructorCall explicitConstructor, BlockScope scope) {
		context.exit(explicitConstructor);
	}

	@Override
	public void endVisit(ExtendedStringLiteral extendedStringLiteral, BlockScope scope) {
		context.exit(extendedStringLiteral);
	}

	@Override
	public void endVisit(FalseLiteral falseLiteral, BlockScope scope) {
		context.exit(falseLiteral);
	}

	@Override
	public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		context.exit(fieldDeclaration);
	}

	@Override
	public void endVisit(FieldReference fieldReference, BlockScope scope) {
		context.exit(fieldReference);
	}

	@Override
	public void endVisit(FloatLiteral floatLiteral, BlockScope scope) {
		context.exit(floatLiteral);
	}

	@Override
	public void endVisit(ForeachStatement forStatement, BlockScope scope) {
		context.exit(forStatement);
	}

	@Override
	public void endVisit(ForStatement forStatement, BlockScope scope) {
		context.exit(forStatement);
	}

	@Override
	public void endVisit(IfStatement ifStatement, BlockScope scope) {
		context.exit(ifStatement);
	}

	@Override
	public void endVisit(Initializer initializer, MethodScope scope) {
		context.exit(initializer);
	}

	@Override
	public void endVisit(InstanceOfExpression instanceOfExpression, BlockScope scope) {
		context.exit(instanceOfExpression);
	}

	@Override
	public void endVisit(IntLiteral intLiteral, BlockScope scope) {
		context.exit(intLiteral);
	}

	@Override
	public void endVisit(LocalDeclaration localDeclaration, BlockScope scope) {
		context.exit(localDeclaration);
	}

	@Override
	public void endVisit(LongLiteral longLiteral, BlockScope scope) {
		context.exit(longLiteral);
	}

	@Override
	public void endVisit(NormalAnnotation annotation, ClassScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(MarkerAnnotation annotation, ClassScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(MarkerAnnotation annotation, BlockScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(MemberValuePair pair, ClassScope scope) {
		if (!context.annotationValueName.pop().equals(new String(pair.name))) {
			throw new RuntimeException("Unconsistant Stack");
		}
	}

	@Override
	public void endVisit(MemberValuePair pair, BlockScope scope) {
		if (!context.annotationValueName.pop().equals(new String(pair.name))) {
			throw new RuntimeException("Unconsistant Stack");
		}
	}

	@Override
	public void endVisit(MessageSend messageSend, BlockScope scope) {
		context.exit(messageSend);
	}

	@Override
	public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
		// Exit from method and Block
		context.exit(methodDeclaration);
		if (context.stack.peek().node == methodDeclaration) {
			context.exit(methodDeclaration);
		}
	}

	@Override
	public void endVisit(NormalAnnotation annotation, BlockScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(NullLiteral nullLiteral, BlockScope scope) {
		context.exit(nullLiteral);
	}

	@Override
	public void endVisit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		context.exit(or_or_Expression);
	}

	@Override
	public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedQualifiedTypeReference);
	}

	@Override
	public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedQualifiedTypeReference);
	}

	@Override
	public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedSingleTypeReference);
	}

	@Override
	public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedSingleTypeReference);
	}

	@Override
	public void endVisit(PostfixExpression postfixExpression, BlockScope scope) {
		context.exit(postfixExpression);
	}

	@Override
	public void endVisit(PrefixExpression prefixExpression, BlockScope scope) {
		context.exit(prefixExpression);
	}

	@Override
	public void endVisit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {
		endVisit((AllocationExpression) qualifiedAllocationExpression, scope);
	}

	@Override
	public void endVisit(QualifiedNameReference qualifiedNameReference, BlockScope scope) {
		if (context.stack.peek().node == qualifiedNameReference) {
			context.exit(qualifiedNameReference);
		}
	}

	@Override
	public void endVisit(QualifiedThisReference qualifiedThisReference, BlockScope scope) {
		endVisit((ThisReference) qualifiedThisReference, scope);
	}

	@Override
	public void endVisit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(qualifiedTypeReference);
	}

	@Override
	public void endVisit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope) {
		endVisit(qualifiedTypeReference, (BlockScope) null);
	}

	@Override
	public void endVisit(ReturnStatement returnStatement, BlockScope scope) {
		context.exit(returnStatement);
	}

	@Override
	public void endVisit(SingleMemberAnnotation annotation, BlockScope scope) {
		if (!context.annotationValueName.pop().equals("value")) {
			throw new RuntimeException("unconsistant Stack");
		}
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(SingleNameReference singleNameReference, BlockScope scope) {
		if (context.stack.peek().node == singleNameReference) {
			context.exit(singleNameReference);
		}
	}

	@Override
	public void endVisit(SingleTypeReference singleTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(singleTypeReference);
	}

	@Override
	public void endVisit(SingleTypeReference singleTypeReference, ClassScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(singleTypeReference);
	}

	@Override
	public void endVisit(StringLiteral stringLiteral, BlockScope scope) {
		context.exit(stringLiteral);
	}

	@Override
	public void endVisit(StringLiteralConcatenation literal, BlockScope scope) {
		context.exit(literal);
	}

	@Override
	public void endVisit(QualifiedSuperReference qualifiedsuperReference, BlockScope scope) {
		context.exit(qualifiedsuperReference);
	}

	@Override
	public void endVisit(SuperReference superReference, BlockScope scope) {
		context.exit(superReference);
	}

	@Override
	public void endVisit(QualifiedThisReference qualifiedThisReference, ClassScope scope) {
		super.endVisit(qualifiedThisReference, scope);
		context.exit(qualifiedThisReference);
	}

	@Override
	public void endVisit(ThisReference thisReference, BlockScope scope) {
		context.exit(thisReference);
	}

	@Override
	public void endVisit(SwitchStatement switchStatement, BlockScope scope) {
		if (context.stack.peek().node instanceof CaseStatement) {
			context.exit(context.stack.peek().node);
		}
		context.exit(switchStatement);
	}

	@Override
	public void endVisit(SynchronizedStatement synchronizedStatement, BlockScope scope) {
		context.exit(synchronizedStatement);
	}

	@Override
	public void endVisit(ThrowStatement throwStatement, BlockScope scope) {
		context.exit(throwStatement);
	}

	@Override
	public void endVisit(TrueLiteral trueLiteral, BlockScope scope) {
		context.exit(trueLiteral);
	}

	@Override
	public void endVisit(TryStatement tryStatement, BlockScope scope) {
		context.exit(tryStatement);
	}

	@Override
	public void endVisit(TypeParameter typeParameter, BlockScope scope) {
		context.exit(typeParameter);
		context.isTypeParameter = false;
	}

	@Override
	public void endVisit(TypeParameter typeParameter, ClassScope scope) {
		context.exit(typeParameter);
		context.isTypeParameter = false;
	}

	@Override
	public void endVisit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		context.exit(localTypeDeclaration);
	}

	@Override
	public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		while (!context.stack.isEmpty() && context.stack.peek().node == memberTypeDeclaration) {
			context.exit(memberTypeDeclaration);
		}
	}

	@Override
	public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
		while (!context.stack.isEmpty() && context.stack.peek().node == typeDeclaration) {
			context.exit(typeDeclaration);
		}
	}

	@Override
	public void endVisit(UnaryExpression unaryExpression, BlockScope scope) {
		context.exit(unaryExpression);
	}

	@Override
	public void endVisit(WhileStatement whileStatement, BlockScope scope) {
		context.exit(whileStatement);
	}

	@Override
	public void endVisit(CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope) {
		context.compilationunitdeclaration = null;
		context.compilationUnitSpoon = null;
	}

	@Override
	public boolean visit(CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope) {
		context.compilationunitdeclaration = scope.referenceContext;
		context.compilationUnitSpoon = getFactory().CompilationUnit().create(new String(context.compilationunitdeclaration.getFileName()));
		context.compilationUnitSpoon.setDeclaredPackage(getFactory().Package().getOrCreate(CharOperation.toString(scope.currentPackageName)));
		return true;
	}

	@Override
	public boolean visit(ReferenceExpression referenceExpression, BlockScope blockScope) {
		context.enter(createExecutableReferenceExpression(referenceExpression), referenceExpression);
		return true;
	}

	private <T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression(ReferenceExpression referenceExpression) {
		CtExecutableReferenceExpression<T, E> executableRef = factory.Core().createExecutableReferenceExpression();
		CtExecutableReference<T> executableReference = references.getExecutableReference(referenceExpression.binding);
		if (executableReference == null) {
			// No classpath mode.
			executableReference = factory.Core().createExecutableReference();
			executableReference.setSimpleName(new String(referenceExpression.selector));
			executableReference.setDeclaringType(references.getTypeReference(referenceExpression.lhs.resolvedType));
		}
		final CtTypeReference<T> declaringType = (CtTypeReference<T>) executableReference.getDeclaringType();
		executableReference.setType(declaringType == null ? null : declaringType.clone());
		executableRef.setExecutable(executableReference);
		return executableRef;
	}

	@Override
	public void endVisit(ReferenceExpression referenceExpression, BlockScope blockScope) {
		context.exit(referenceExpression);
	}

	@Override
	public boolean visit(LambdaExpression lambdaExpression, BlockScope blockScope) {
		CtLambda<?> lambda = factory.Core().createLambda();

		final MethodBinding methodBinding = lambdaExpression.getMethodBinding();
		if (methodBinding != null) {
			lambda.setSimpleName(String.valueOf(methodBinding.constantPoolName()));
		}

		context.enter(lambda, lambdaExpression);

		final Argument[] arguments = lambdaExpression.arguments();
		if (arguments != null && arguments.length > 0) {
			for (Argument e : arguments) {
				e.traverse(this, blockScope);
			}
		}

		if (lambdaExpression.body() != null) {
			final boolean isBuildLambdaBack = context.isBuildLambda;
			context.isBuildLambda = true;
			lambdaExpression.body().traverse(this, blockScope);
			context.isBuildLambda = isBuildLambdaBack;
		}

		return false;
	}

	@Override
	public void endVisit(LambdaExpression lambdaExpression, BlockScope blockScope) {
		context.exit(lambdaExpression);
	}

	@Override
	public boolean visit(AllocationExpression allocationExpression, BlockScope scope) {
		buildCommonPartForCtNewClassAndCtConstructorCall(allocationExpression, scope, factory.Core().createConstructorCall());
		return false;
	}

	private <T extends CtConstructorCall<Object>> T buildCommonPartForCtNewClassAndCtConstructorCall(AllocationExpression allocationExpression, BlockScope scope, T constructorCall) {
		context.enter(constructorCall, allocationExpression);

		if (allocationExpression.binding != null) {
			constructorCall.setExecutable(references.getExecutableReference(allocationExpression.binding));
		} else {
			final CtExecutableReference<Object> ref = factory.Core().createExecutableReference();
			ref.setSimpleName(CtExecutableReference.CONSTRUCTOR_NAME);
			ref.setType(references.getTypeReference(null, allocationExpression.type));
			ref.setDeclaringType(references.getTypeReference(null, allocationExpression.type));

			final List<CtTypeReference<?>> parameters = new ArrayList<>(allocationExpression.argumentTypes.length);
			for (TypeBinding b : allocationExpression.argumentTypes) {
				parameters.add(references.getTypeReference(b));
			}
			ref.setParameters(parameters);
			constructorCall.setExecutable(ref);
		}

		if (allocationExpression.type != null && allocationExpression.type.resolvedType != null) {
			final TypeReference[][] typeArguments = allocationExpression.type.getTypeArguments();
			// If typeArguments are null or empty, we have an element with a generic type.
			if (typeArguments != null && typeArguments.length > 0) {
				context.isGenericTypeExplicit = true;
				// This loop is necessary because it is the only way to know if the generic type
				// is implicit or not.
				for (TypeReference[] typeArgument : typeArguments) {
					context.isGenericTypeExplicit = typeArgument != null && typeArgument.length > 0;
					if (context.isGenericTypeExplicit) {
						break;
					}
				}
			}
			constructorCall.getExecutable().setType(this.references.buildTypeReference(allocationExpression.type, scope));
			context.isGenericTypeExplicit = true;
		} else if (allocationExpression.expectedType() != null) {
			constructorCall.getExecutable().setType(references.getTypeReference(allocationExpression.expectedType()));
		}

		if (allocationExpression.typeArguments != null) {
			for (TypeReference typeArgument : allocationExpression.typeArguments) {
				constructorCall.addActualTypeArgument(this.references.buildTypeReference(typeArgument, scope));
			}
		}

		if (allocationExpression.enclosingInstance() != null) {
			context.target.push(constructorCall);
			allocationExpression.enclosingInstance().traverse(this, scope);
			context.target.pop();
		}

		context.pushArgument(constructorCall);
		if (allocationExpression.arguments != null) {
			for (Expression e : allocationExpression.arguments) {
				e.traverse(this, scope);
			}
		}
		context.popArgument(constructorCall);
		return constructorCall;
	}

	@Override
	public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((and_and_Expression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, and_and_Expression);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope) {
		CtField<Object> f = factory.Core().createField();
		f.setSimpleName(new String(annotationTypeDeclaration.selector));
		context.enter(f, annotationTypeDeclaration);

		f.setType(this.references.buildTypeReference(annotationTypeDeclaration.returnType, classScope));

		if (annotationTypeDeclaration.annotations != null) {
			for (Annotation a : annotationTypeDeclaration.annotations) {
				a.traverse(this, annotationTypeDeclaration.scope);
				substituteAnnotation(f, a, CtAnnotatedElementType.TYPE_USE);
			}
		}

		defaultValue = true;
		if (annotationTypeDeclaration.defaultValue != null) {
			annotationTypeDeclaration.defaultValue.traverse(this, annotationTypeDeclaration.scope);
		}
		defaultValue = false;
		return false;
	}

	@Override
	public boolean visit(Argument argument, BlockScope scope) {
		if (this.getContextBuilder().stack.peekFirst().element instanceof CtTry) {
			context.enter(factory.Core().createCatch(), argument);
			return true;
		}
		CtParameter<Object> p = factory.Core().createParameter();
		p.setSimpleName(new String(argument.name));
		p.setVarArgs(argument.isVarArgs());
		p.setModifiers(getModifiers(argument.modifiers));
		if (argument.binding != null && argument.binding.type != null) {
			context.isLambdaParameterImplicitlyTyped = argument.type != null;
			if (argument.binding.type instanceof WildcardBinding) {
				p.setType(references.getTypeReference((((WildcardBinding) argument.binding.type).bound)));
			} else {
				p.setType(references.getTypeReference((argument.binding.type)));
			}
			context.isLambdaParameterImplicitlyTyped = true;
		} else if (argument.type != null) {
			p.setType(references.getTypeReference(argument.type));
		}

		final TypeBinding receiverType = argument.type != null ? argument.type.resolvedType : null;
		if (receiverType != null && argument.type instanceof QualifiedTypeReference) {
			final QualifiedTypeReference qualifiedNameReference = (QualifiedTypeReference) argument.type;
			final CtTypeReference<Object> ref = this.references.getQualifiedTypeReference(qualifiedNameReference.tokens, receiverType, receiverType.enclosingType(), new OnAccessListener() {
				@Override
				public boolean onAccess(char[][] tokens, int index) {
					return true;
				}
			});
			if (ref != null) {
				p.setType(ref);
			}
		}

		context.enter(p, argument);
		if (argument.initialization != null) {
			argument.initialization.traverse(this, scope);
		}

		if (argument.annotations != null) {
			for (Annotation a : argument.annotations) {
				a.traverse(this, scope);
				substituteAnnotation(p, a, CtAnnotatedElementType.TYPE_USE);
			}
		}

		return false;
	}

	@Override
	public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {
		CtNewArray<Object> array = factory.Core().createNewArray();
		context.enter(array, arrayAllocationExpression);

		CtTypeReference<?> typeReference;
		if (arrayAllocationExpression.resolvedType != null) {
			typeReference = references.getTypeReference(arrayAllocationExpression.resolvedType.leafComponentType(), arrayAllocationExpression.type);
		} else {
			typeReference = references.getTypeReference(arrayAllocationExpression.type);
		}
		final CtArrayTypeReference arrayType = factory.Type().createArrayReference(typeReference, arrayAllocationExpression.dimensions.length);
		arrayType.getArrayType().setAnnotations(this.references.buildTypeReference(arrayAllocationExpression.type, scope).getAnnotations());
		array.setType(arrayType);

		context.pushArgument(array);
		if (arrayAllocationExpression.dimensions != null) {
			for (Expression e : arrayAllocationExpression.dimensions) {
				if (e != null) {
					e.traverse(this, scope);
				}
			}
		}
		context.popArgument(array);

		if (arrayAllocationExpression.initializer != null && arrayAllocationExpression.initializer.expressions != null) {
			for (Expression e : arrayAllocationExpression.initializer.expressions) {
				e.traverse(this, scope);
			}
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {
		context.enter(factory.Core().createNewArray(), arrayInitializer);
		return true;
	}

	@Override
	public boolean visit(ArrayReference arrayReference, BlockScope scope) {
		CtArrayAccess<?, ?> a;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			a = factory.Core().createArrayWrite();
		} else {
			a = factory.Core().createArrayRead();
		}
		context.enter(a, arrayReference);
		arrayReference.receiver.traverse(this, scope);
		context.arguments.push(a);
		arrayReference.position.traverse(this, scope);
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		final CtTypeAccess<Object> typeAccess = factory.Core().createTypeAccess();

		context.enter(typeAccess, arrayTypeReference);

		final CtArrayTypeReference<Object> arrayType = (CtArrayTypeReference<Object>) references.getTypeReference(arrayTypeReference.resolvedType);
		arrayType.getArrayType().setAnnotations(this.references.buildTypeReference(arrayTypeReference, scope).getAnnotations());
		typeAccess.setAccessedType(arrayType);

		return true;
	}

	@Override
	public boolean visit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
		return visit(arrayTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope) {
		final CtTypeAccess<Object> typeAccess = factory.Core().createTypeAccess();

		context.enter(typeAccess, arrayQualifiedTypeReference);

		final CtArrayTypeReference<Object> arrayType = (CtArrayTypeReference<Object>) references.getTypeReference(arrayQualifiedTypeReference.resolvedType);
		arrayType.getArrayType().setAnnotations(this.references.buildTypeReference(arrayQualifiedTypeReference, scope).getAnnotations());
		typeAccess.setAccessedType(arrayType);

		return true;
	}

	@Override
	public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope) {
		return visit(arrayQualifiedTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(AssertStatement assertStatement, BlockScope scope) {
		CtAssert<?> a = factory.Core().createAssert();
		context.enter(a, assertStatement);
		assertStatement.assertExpression.traverse(this, scope);
		context.arguments.push(a);
		if (assertStatement.exceptionArgument != null) {
			assertStatement.exceptionArgument.traverse(this, scope);
		}
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(Assignment assignment, BlockScope scope) {
		CtAssignment<Object, Object> assign = factory.Core().createAssignment();
		context.enter(assign, assignment);
		context.arguments.push(assign);
		context.assigned = true;
		if (assignment.lhs != null) {
			assignment.lhs.traverse(this, scope);
		}
		context.assigned = false;

		if (assignment.expression != null) {
			assignment.expression.traverse(this, scope);
		}
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((binaryExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, binaryExpression);
		return true;
	}

	@Override
	public boolean visit(Block block, BlockScope scope) {
		context.enter(factory.Core().createBlock(), block);
		return true;
	}

	@Override
	public boolean visit(BreakStatement breakStatement, BlockScope scope) {
		CtBreak b = factory.Core().createBreak();
		if (breakStatement.label != null) {
			b.setTargetLabel(new String(breakStatement.label));
		}
		context.enter(b, breakStatement);
		return true;
	}

	@Override
	public boolean visit(CastExpression castExpression, BlockScope scope) {
		context.casts.add(this.references.buildTypeReference(castExpression.type, scope));
		castExpression.expression.traverse(this, scope);
		return false;
	}

	@Override
	public boolean visit(CharLiteral charLiteral, BlockScope scope) {
		charLiteral.computeConstant();
		context.enter(factory.Code().createLiteral(charLiteral.constant.charValue()), charLiteral);
		return true;
	}

	@Override
	public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
		context.enter(factory.Code().createClassAccess(references.getTypeReference(classLiteral.targetType)), classLiteral);
		return false;
	}

	@Override
	public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
		CtOperatorAssignment<Object, Object> a = factory.Core().createOperatorAssignment();
		a.setKind(getBinaryOperatorKind(compoundAssignment.operator));
		context.enter(a, compoundAssignment);
		context.arguments.push(a);
		context.assigned = true;
		if ((compoundAssignment.lhs) != null) {
			compoundAssignment.lhs.traverse(this, scope);
		}

		context.assigned = false;
		if ((compoundAssignment.expression) != null) {
			compoundAssignment.expression.traverse(this, scope);
		}
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression conditionalExpression, BlockScope scope) {
		context.enter(factory.Core().createConditional(), conditionalExpression);
		return true;
	}

	@Override
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		CtConstructor<?> c = factory.Core().createConstructor();
		c.setModifiers(getModifiers(constructorDeclaration.modifiers));

		context.enter(c, constructorDeclaration);

		if (constructorDeclaration.annotations != null) {
			int annotationsLength = constructorDeclaration.annotations.length;
			for (int i = 0; i < annotationsLength; i++) {
				constructorDeclaration.annotations[i].traverse(this, constructorDeclaration.scope);
			}
		}

		context.pushArgument(c);
		if (constructorDeclaration.arguments != null) {
			int argumentLength = constructorDeclaration.arguments.length;
			for (int i = 0; i < argumentLength; i++) {
				constructorDeclaration.arguments[i].traverse(this, constructorDeclaration.scope);
			}
		}
		context.popArgument(c);

		if (constructorDeclaration.thrownExceptions != null) {
			for (TypeReference r : constructorDeclaration.thrownExceptions) {
				// Cast scope to BlockScope because some members values with potential
				// type annotations don't override traverse method with ClassScope.
				// Thanks JDT...
				CtTypeReference<? extends Throwable> throwType = this.references.buildTypeReference(r, (BlockScope) null);
				c.addThrownType(throwType);
			}
		}

		if (constructorDeclaration.typeParameters() != null) {
			for (TypeParameter typeParameter : constructorDeclaration.typeParameters()) {
				typeParameter.traverse(this, scope);
			}
		}

		// Create block
		if (!constructorDeclaration.isAbstract()) {
			CtBlock<?> b = factory.Core().createBlock();
			context.enter(b, constructorDeclaration);
		}

		if (constructorDeclaration.constructorCall != null) {
			constructorDeclaration.constructorCall.traverse(this, constructorDeclaration.scope);
		}

		if (constructorDeclaration.statements != null) {
			for (Statement s : constructorDeclaration.statements) {
				s.traverse(this, constructorDeclaration.scope);
			}
		}
		return false;
	}

	@Override
	public boolean visit(TypeParameter typeParameter, ClassScope scope) {
		return visitTypeParameter(typeParameter, scope);
	}

	@Override
	public boolean visit(TypeParameter typeParameter, BlockScope scope) {
		return visitTypeParameter(typeParameter, scope);
	}

	public boolean visitTypeParameter(TypeParameter typeParameter, Scope scope) {
		final CtTypeParameterReference typeParameterRef = factory.Core().createTypeParameterReference();
		context.isTypeParameter = true;
		context.enter(typeParameterRef, typeParameter);

		typeParameterRef.setSimpleName(CharOperation.charToString(typeParameter.name));

		// Extends of the generic type.
		typeParameterRef.setBoundingType(this.references.buildTypeReference(typeParameter.type, (BlockScope) null));

		// Annotations.
		if (typeParameter.annotations != null) {
			int length = typeParameter.annotations.length;

			for (int i = 0; i < length; ++i) {
				typeParameter.annotations[i].traverse(this, (BlockScope) null);
			}
		}

		// Bounds with the extend type: T extends String & Serializer
		if (typeParameter.bounds != null) {
			int length = typeParameter.bounds.length;

			final Set<CtTypeReference<?>> bounds = new TreeSet<>();
			bounds.add(typeParameterRef.getBoundingType());
			for (int i = 0; i < length; ++i) {
				bounds.add(this.references.buildTypeReference(typeParameter.bounds[i], (BlockScope) null));
			}
			typeParameterRef.setBoundingType(factory.Type().createIntersectionTypeReferenceWithBounds(bounds));
		}

		return false;
	}

	@Override
	public boolean visit(ContinueStatement continueStatement, BlockScope scope) {
		CtContinue c = factory.Core().createContinue();
		context.enter(c, continueStatement);
		if (continueStatement.label != null) {
			c.setTargetLabel(new String(continueStatement.label));
		}
		return true;
	}

	@Override
	public boolean visit(DoStatement doStatement, BlockScope scope) {
		context.enter(factory.Core().createDo(), doStatement);
		return true;
	}

	@Override
	public boolean visit(DoubleLiteral doubleLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(doubleLiteral.constant.doubleValue()), doubleLiteral);
		return true;
	}

	@Override
	public boolean visit(EqualExpression equalExpression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((equalExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, equalExpression);
		return true; // do nothing by default, keep traversing
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ExplicitConstructorCall explicitConstructor, BlockScope scope) {
		CtInvocation<Object> inv = factory.Core().createInvocation();
		if (explicitConstructor.isImplicitSuper()) {
			inv.setImplicit(true);
		}
		CtExecutableReference<Object> er = references.getExecutableReference(explicitConstructor.binding);
		inv.setExecutable(er);
		inv.getExecutable().setType((CtTypeReference<Object>) inv.getExecutable().getDeclaringType());

		if (explicitConstructor.genericTypeArguments() != null) {
			inv.getExecutable().setActualTypeArguments(references.getBoundedTypesReferences(explicitConstructor.genericTypeArguments()));
		}

		context.enter(inv, explicitConstructor);

		if (explicitConstructor.qualification != null) {
			explicitConstructor.qualification.traverse(this, scope);
		}
		if (explicitConstructor.typeArguments != null) {
			for (int i = 0, typeArgumentsLength = explicitConstructor.typeArguments.length; i < typeArgumentsLength; i++) {
				explicitConstructor.typeArguments[i].traverse(this, scope);
			}
		}

		context.arguments.push(inv);
		if (explicitConstructor.arguments != null) {
			for (int i = 0, argumentLength = explicitConstructor.arguments.length; i < argumentLength; i++) {
				explicitConstructor.arguments[i].traverse(this, scope);
			}
		}
		context.arguments.pop();

		return false;
	}

	@Override
	public boolean visit(ExtendedStringLiteral extendedStringLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(CharOperation.charToString(extendedStringLiteral.source())), extendedStringLiteral);
		return true;
	}

	@Override
	public boolean visit(FalseLiteral falseLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(false), falseLiteral);
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		CtField<Object> field;
		if (fieldDeclaration.type != null) {
			field = factory.Core().createField();
			context.enter(field, fieldDeclaration);
			field.setType(this.references.buildTypeReference(fieldDeclaration.type, scope));
		} else {
			field = factory.Core().createEnumValue();
			context.enter(field, fieldDeclaration);
			if (fieldDeclaration.binding != null) {
				field.setType(references.getVariableReference(fieldDeclaration.binding).getType());
			}
		}
		field.setSimpleName(new String(fieldDeclaration.name));
		field.setModifiers(getModifiers(fieldDeclaration.modifiers));

		if (fieldDeclaration.annotations != null) {
			int annotationsLength = fieldDeclaration.annotations.length;
			for (int i = 0; i < annotationsLength; i++) {
				fieldDeclaration.annotations[i].traverse(this, scope);
			}
		}

		if (fieldDeclaration.initialization != null) {
			fieldDeclaration.initialization.traverse(this, scope);
		}
		return false;
	}

	@Override
	public boolean visit(FieldReference fieldReference, BlockScope scope) {
		CtFieldAccess<Object> fieldAccess;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			fieldAccess = factory.Core().createFieldWrite();
		} else {
			fieldAccess = factory.Core().createFieldRead();
		}
		CtFieldReference<Object> variableReference = references.getVariableReference(fieldReference.binding);
		if (variableReference.getSimpleName() == null) {
			variableReference.setSimpleName(new String(fieldReference.token));
		}
		fieldAccess.setVariable(variableReference);
		fieldAccess.setType(references.getTypeReference(fieldReference.resolvedType));

		// Hmmm Maybe this should not be commented, but I cannot see why we need
		// it.
		// Anyway, the problem is that in jdt-core 3.5+ fieldReferences no
		// longer have a receiverType,
		// As far as I can tell, this if makes sure that pika.length if pika is
		// an array, gets the correct type.
		// if anything, I guess that the jdt-core now does not think that length
		// is a field... so we wouldn't need this anymore.

		// if (fieldReference.receiverType instanceof ArrayBinding
		// && new String(fieldReference.token).equals("length")) {
		// acc.getVariable().setDeclaringType(
		// references.getTypeReference(fieldReference.receiverType));
		// }
		context.enter(fieldAccess, fieldReference);

		context.target.push(fieldAccess);
		fieldReference.receiver.traverse(this, scope);
		context.target.pop();
		return false;
	}

	@Override
	public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(floatLiteral.constant.floatValue()), floatLiteral);
		return true;
	}

	@Override
	public boolean visit(ForeachStatement forStatement, BlockScope scope) {
		context.enter(factory.Core().createForEach(), forStatement);
		return true;
	}

	@Override
	public boolean visit(ForStatement forStatement, BlockScope scope) {
		CtFor for1 = factory.Core().createFor();
		context.enter(for1, forStatement);

		if (forStatement.initializations != null) {
			context.forinit = true;
			int initializationsLength = forStatement.initializations.length;
			for (int i = 0; i < initializationsLength; i++) {
				forStatement.initializations[i].traverse(this, scope);
			}
			context.forinit = false;
		}
		if (forStatement.condition != null) {
			forStatement.condition.traverse(this, scope);
		}

		if (forStatement.increments != null) {
			context.forupdate = true;
			int incrementsLength = forStatement.increments.length;
			for (int i = 0; i < incrementsLength; i++) {
				forStatement.increments[i].traverse(this, scope);
			}
			context.forupdate = false;
		}
		if (forStatement.action != null) {
			forStatement.action.traverse(this, scope);
		}

		return false;
	}

	@Override
	public boolean visit(IfStatement ifStatement, BlockScope scope) {
		context.enter(factory.Core().createIf(), ifStatement);
		return true;
	}

	@Override
	public boolean visit(Initializer initializer, MethodScope scope) {
		CtAnonymousExecutable b = factory.Core().createAnonymousExecutable();
		if (initializer.isStatic()) {
			b.addModifier(ModifierKind.STATIC);
		}
		context.enter(b, initializer);
		return true;
	}

	@Override
	public boolean visit(InstanceOfExpression instanceOfExpression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(BinaryOperatorKind.INSTANCEOF);
		context.enter(op, instanceOfExpression);
		return true;
	}

	@Override
	public boolean visit(IntLiteral intLiteral, BlockScope scope) {
		CtLiteral<Integer> l = factory.Core().createLiteral();
		l.setType(getFactory().Type().integerType());
		if (intLiteral.constant != null) { // check required for noclasspath mode
			l.setValue(intLiteral.constant.intValue());
		}
		context.enter(l, intLiteral);
		return true;
	}

	@Override
	public boolean visit(LabeledStatement labeledStatement, BlockScope scope) {
		context.label.push(new String(labeledStatement.label));
		return true;
	}

	@Override
	public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
		CtLocalVariable<Object> v = factory.Core().createLocalVariable();
		v.setSimpleName(new String(localDeclaration.name));
		v.setType(this.references.buildTypeReference(localDeclaration.type, scope));
		v.setModifiers(getModifiers(localDeclaration.modifiers));
		context.enter(v, localDeclaration);

		if (localDeclaration.initialization != null) {
			context.arguments.push(v);
			// resolve Literal#constant if null (by calling `resolveType`). Otherwise,
			// `localDeclaration.initialization.traverse(this, scope);` throws a
			// NullPointerException. Fixes #755.
			if (localDeclaration.initialization instanceof Literal
					// exclude StringLiterals if scope is null. In other words:
					// StringLiteral -> scope!=null <=> !StringLiteral v scope!=null.
					&& (!(localDeclaration.initialization instanceof StringLiteral) || scope != null)) {
				final Literal literal = (Literal) localDeclaration.initialization;
				if (literal.constant == null) {
					literal.resolveType(scope);
				}
			}
			localDeclaration.initialization.traverse(this, scope);
			context.arguments.pop();
		}

		if (localDeclaration.annotations != null) {
			for (Annotation a : localDeclaration.annotations) {
				a.traverse(this, scope);
				substituteAnnotation(v, a, CtAnnotatedElementType.TYPE_USE);
			}
		}

		return false;
	}

	@Override
	public boolean visit(LongLiteral longLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(longLiteral.constant.longValue()).<CtLiteral<Long>>setType(getFactory().Type().longType()), longLiteral);
		return true;
	}

	@Override
	public boolean visit(NormalAnnotation annotation, ClassScope scope) {
		return visitNormalAnnotation(annotation, scope);
	}

	@Override
	public boolean visit(NormalAnnotation annotation, BlockScope scope) {
		return visitNormalAnnotation(annotation, scope);
	}

	@Override
	public boolean visit(MarkerAnnotation annotation, ClassScope scope) {
		return visitMarkerAnnotation(annotation, scope);
	}

	@Override
	public boolean visit(MarkerAnnotation annotation, BlockScope scope) {
		return visitMarkerAnnotation(annotation, scope);
	}

	@Override
	public boolean visit(SingleMemberAnnotation annotation, BlockScope scope) {
		visitMarkerAnnotation(annotation, scope);
		context.annotationValueName.push("value");
		return true;
	}

	private <A extends java.lang.annotation.Annotation> boolean visitNormalAnnotation(NormalAnnotation annotation, Scope scope) {
		context.enter(factory.Code().createAnnotation(references.<A>getTypeReference(annotation.resolvedType)), annotation);
		skipTypeInAnnotation = true;
		return true;
	}

	private <A extends java.lang.annotation.Annotation> boolean visitMarkerAnnotation(Annotation annotation, Scope scope) {
		context.enter(factory.Code().createAnnotation(references.<A>getTypeReference(annotation.resolvedType, annotation.type)), annotation);
		skipTypeInAnnotation = true;
		return true;
	}

	@Override
	public boolean visit(MemberValuePair pair, ClassScope scope) {
		context.annotationValueName.push(new String(pair.name));
		return true;
	}

	@Override
	public boolean visit(MemberValuePair pair, BlockScope scope) {
		context.annotationValueName.push(new String(pair.name));
		return true;
	}

	@Override
	public boolean visit(MessageSend messageSend, BlockScope scope) {
		if (messageSend.actualReceiverType == null
				|| !messageSend.actualReceiverType.isAnnotationType()
				|| messageSend.binding instanceof MethodBinding) {
			CtInvocation<Object> inv = factory.Core().createInvocation();
			if (messageSend.binding != null) {
				inv.setExecutable(references.getExecutableReference(messageSend.binding));
				if (messageSend.binding instanceof ProblemMethodBinding) {
					// We are in a static complex in noclasspath mode.
					if (inv.getExecutable() != null && inv.getExecutable().getDeclaringType() != null) {
						final CtTypeAccess ta = factory.Code().createTypeAccess(inv.getExecutable().getDeclaringType());
						if (ta.getAccessedType().isAnonymous()) {
							ta.setImplicit(true);
						}
						inv.setTarget(ta);
					}
					if (messageSend.expectedType() != null) {
						inv.getExecutable().setType(references.getTypeReference(messageSend.expectedType()));
					}
				}
			} else {
				CtExecutableReference<Object> ref = factory.Core().createExecutableReference();
				ref.setSimpleName(new String(messageSend.selector));
				ref.setType(references.getTypeReference(messageSend.expectedType()));
				if (messageSend.receiver.resolvedType == null) {
					// It is crisis dude! static context, we don't have much more information.
					if (messageSend.receiver instanceof SingleNameReference) {
						CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
						typeReference.setSimpleName(messageSend.receiver.toString());
						final CtReference declaring = references.getDeclaringReferenceFromImports(((SingleNameReference) messageSend.receiver).token);
						setPackageOrDeclaringType(typeReference, declaring);
						ref.setDeclaringType(typeReference);
					} else if (messageSend.receiver instanceof QualifiedNameReference) {
						QualifiedNameReference qualifiedNameReference = (QualifiedNameReference) messageSend.receiver;

						// TODO try to determine package/class boundary by upper case
						char[][] packageName = CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1);
						char[][] className = CharOperation.subarray(qualifiedNameReference.tokens, qualifiedNameReference.tokens.length - 1, qualifiedNameReference.tokens.length);
						if (packageName.length > 0) {
							final PackageBinding aPackage = context.compilationunitdeclaration.scope.environment.createPackage(packageName);
							final MissingTypeBinding declaringType = context.compilationunitdeclaration.scope.environment.createMissingType(aPackage, className);

							ref.setDeclaringType(references.getTypeReference(declaringType));
						} else {
							final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
							typeReference.setSimpleName(messageSend.receiver.toString());
							ref.setDeclaringType(typeReference);
						}
					}
				} else {
					ref.setDeclaringType(references.getTypeReference(messageSend.receiver.resolvedType));
				}
				if (messageSend.arguments != null) {
					final List<CtTypeReference<?>> parameters = new ArrayList<>();
					for (Expression argument : messageSend.arguments) {
						parameters.add(references.getTypeReference(argument.resolvedType));
					}
					ref.setParameters(parameters);
				}
				inv.setExecutable(ref);
			}
			context.enter(inv, messageSend);
			if (messageSend.receiver.isImplicitThis()) {
				if (inv.getExecutable().getDeclaringType() != null && inv.getExecutable().isStatic()) {
					final CtTypeAccess<?> typeAccess = factory.Code().createTypeAccess(inv.getExecutable().getDeclaringType());
					if (typeAccess.getAccessedType().isAnonymous()) {
						typeAccess.setImplicit(true);
					}
					inv.setTarget(typeAccess);
				} else if (inv.getExecutable().getDeclaringType() != null && !inv.getExecutable().isStatic()) {
					messageSend.receiver.traverse(this, scope);
					if (inv.getTarget() instanceof CtThisAccess) {
						((CtThisAccess) inv.getTarget()).setTarget(factory.Code().createTypeAccess(inv.getExecutable().getDeclaringType()));
					}
				} else if (!(messageSend.binding() instanceof ProblemMethodBinding)) {
					messageSend.receiver.traverse(this, scope);
				}
			} else {
				messageSend.receiver.traverse(this, scope);
			}
			context.pushArgument(inv);
			if (messageSend.arguments != null) {
				for (Expression e : messageSend.arguments) {
					e.traverse(this, scope);
				}
			}
			if (messageSend.typeArguments != null) {
				for (TypeReference typeBinding : messageSend.typeArguments) {
					inv.getExecutable().addActualTypeArgument(references.getTypeReference(typeBinding.resolvedType));
				}
			}
			context.popArgument(inv);
			return false;

		} else {
			CtAnnotationFieldAccess<Object> acc = factory.Core().createAnnotationFieldAccess();
			acc.setVariable(references.getVariableReference(messageSend.binding));
			acc.setType(references.getTypeReference(messageSend.resolvedType));

			context.enter(acc, messageSend);

			context.target.push(acc);
			messageSend.receiver.traverse(this, scope);
			context.target.pop();

			return false;
		}
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		CtMethod<Object> m = factory.Core().createMethod();
		m.setSimpleName(new String(methodDeclaration.selector));
		m.setType(this.references.buildTypeReference(methodDeclaration.returnType, scope));
		m.setModifiers(getModifiers(methodDeclaration.modifiers));
		m.setDefaultMethod(methodDeclaration.isDefaultMethod());

		context.enter(m, methodDeclaration);

		if (methodDeclaration.thrownExceptions != null) {
			for (TypeReference r : methodDeclaration.thrownExceptions) {
				CtTypeReference<Throwable> throwType = this.references.buildTypeReference(r, scope);
				m.addThrownType(throwType);
			}
		}

		if (methodDeclaration.typeParameters() != null) {
			for (TypeParameter typeParameter : methodDeclaration.typeParameters()) {
				typeParameter.traverse(this, scope);
			}
		}

		if (methodDeclaration.annotations != null) {
			for (Annotation a : methodDeclaration.annotations) {
				a.traverse(this, methodDeclaration.scope);
				substituteAnnotation(m, a, CtAnnotatedElementType.TYPE_USE);
			}
		}

		if (methodDeclaration.arguments != null) {
			for (Argument a : methodDeclaration.arguments) {
				a.traverse(this, methodDeclaration.scope);
			}
		}

		// Create block
		if (!methodDeclaration.isAbstract() && (methodDeclaration.modifiers & ClassFileConstants.AccNative) == 0) {
			CtBlock<?> b = factory.Core().createBlock();
			context.enter(b, methodDeclaration);
		}

		if (methodDeclaration.statements != null) {
			for (Statement s : methodDeclaration.statements) {
				s.traverse(this, methodDeclaration.scope);
			}
		}
		return false;
	}

	@Override
	public boolean visit(NullLiteral nullLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(null).setType((CtTypeReference<Object>) factory.Type().nullType()), nullLiteral);
		return true;
	}

	@Override
	public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((or_or_Expression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, or_or_Expression);
		return true;
	}

	@Override
	public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {
		return createParameterizedType(parameterizedQualifiedTypeReference);
	}

	@Override
	public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {
		return createParameterizedType(parameterizedQualifiedTypeReference);
	}

	@Override
	public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {
		return createParameterizedType(parameterizedSingleTypeReference);
	}

	@Override
	public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
		return createParameterizedType(parameterizedSingleTypeReference);
	}

	private boolean createParameterizedType(TypeReference parameterizedTypeReference) {
		if (skipTypeInAnnotation) {
			return true;
		}
		context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference(parameterizedTypeReference.resolvedType)), parameterizedTypeReference);
		return true;
	}

	@Override
	public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		if (postfixExpression.operator == OperatorIds.PLUS) {
			op.setKind(UnaryOperatorKind.POSTINC);
		}
		if (postfixExpression.operator == OperatorIds.MINUS) {
			op.setKind(UnaryOperatorKind.POSTDEC);
		}
		context.enter(op, postfixExpression);
		return true;
	}

	@Override
	public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		if (prefixExpression.operator == OperatorIds.PLUS) {
			op.setKind(UnaryOperatorKind.PREINC);
		}
		if (prefixExpression.operator == OperatorIds.MINUS) {
			op.setKind(UnaryOperatorKind.PREDEC);
		}
		context.enter(op, prefixExpression);
		return true;
	}

	@Override
	public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {
		// anonymous class
		if (qualifiedAllocationExpression.anonymousType != null) {
			buildCommonPartForCtNewClassAndCtConstructorCall(qualifiedAllocationExpression, scope, factory.Core().createNewClass());
			qualifiedAllocationExpression.anonymousType.traverse(this, scope);
		} else {
			// constructor call
			buildCommonPartForCtNewClassAndCtConstructorCall(qualifiedAllocationExpression, scope, factory.Core().createConstructorCall());
		}
		if (qualifiedAllocationExpression.enclosingInstance != null) {
			qualifiedAllocationExpression.enclosingInstance.traverse(this, scope);
		}
		return false;
	}

	@Override
	public boolean visit(QualifiedNameReference qualifiedNameRef, BlockScope scope) {
		if (qualifiedNameRef.binding instanceof FieldBinding || qualifiedNameRef.binding instanceof VariableBinding) {
			context.enter(createVariableAccess(qualifiedNameRef), qualifiedNameRef);
			return true;
		} else if (qualifiedNameRef.binding instanceof TypeBinding) {
			context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference((TypeBinding) qualifiedNameRef.binding)), qualifiedNameRef);
			return true;
		} else if (qualifiedNameRef.binding instanceof ProblemBinding) {
			if (context.stack.peek().element instanceof CtInvocation) {
				context.enter(createInvocationNoClasspath(qualifiedNameRef), qualifiedNameRef);
				return true;
			}
			context.enter(createFieldAccessNoClasspath(qualifiedNameRef), qualifiedNameRef);
			return true;
		} else {
			context.enter(
					factory.Code().createVariableAccess(
							factory.Core().createUnboundVariableReference().<CtUnboundVariableReference>setSimpleName(qualifiedNameRef.toString()),
							context.stack.peek().element instanceof CtAssignment && context.assigned),
					qualifiedNameRef
			);
			return true;
		}
	}

	private <T> CtTypeAccess<T> createInvocationNoClasspath(QualifiedNameReference qualifiedNameReference) {
		final CtTypeReference<T> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(qualifiedNameReference.toString());
		return factory.Code().createTypeAccessWithoutCloningReference(typeReference);
	}

	private <T> CtFieldAccess<T> createFieldAccessNoClasspath(QualifiedNameReference qualifiedNameReference) {
		boolean fromAssignment = context.stack.peek().element instanceof CtAssignment && context.assigned;
		CtFieldAccess<T> fieldAccess = factory.Code().createFieldAccess(references.<T>getVariableReference((ProblemBinding) qualifiedNameReference.binding), null, fromAssignment);
		// In no classpath mode and with qualified name, the type given by JDT is wrong...
		final char[][] declaringClass = CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1);
		final MissingTypeBinding declaringType = context.compilationunitdeclaration.scope.environment.createMissingType(null, declaringClass);
		final CtTypeReference<T> declaringRef = references.getTypeReference(declaringType);
		fieldAccess.getVariable().setDeclaringType(declaringRef);
		fieldAccess.getVariable().setStatic(true);
		fieldAccess.setTarget(factory.Code().createTypeAccess(declaringRef));
		// In no classpath mode and with qualified name, the binding don't have a good name.
		fieldAccess.getVariable().setSimpleName(createTypeName(CharOperation.subarray(qualifiedNameReference.tokens, qualifiedNameReference.tokens.length - 1, qualifiedNameReference.tokens.length)));
		return fieldAccess;
	}

	private <T> CtVariableAccess<T> createVariableAccess(QualifiedNameReference qualifiedNameReference) {
		long[] positions = qualifiedNameReference.sourcePositions;

		int sourceStart = qualifiedNameReference.sourceStart();
		int sourceEnd = qualifiedNameReference.sourceEnd();
		if (qualifiedNameReference.indexOfFirstFieldBinding < positions.length) {
			sourceEnd = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding] >>> 32) - 2;
		}
		CtVariableAccess<T> va;
		CtVariableReference<T> ref;
		boolean fromAssignment = context.stack.peek().element instanceof CtAssignment && context.assigned;
		boolean isOtherBinding = qualifiedNameReference.otherBindings == null || qualifiedNameReference.otherBindings.length == 0;
		if (qualifiedNameReference.binding instanceof FieldBinding) {
			ref = references.getVariableReference(qualifiedNameReference.fieldBinding());
			ref.setPosition(this.position.buildPosition(sourceStart, sourceEnd));

			va = factory.Code().createFieldAccess(ref, createTargetFieldAccess(qualifiedNameReference, (CtFieldReference<Object>) ref), isOtherBinding && fromAssignment);
		} else {
			ref = references.getVariableReference((VariableBinding) qualifiedNameReference.binding);
			ref.setPosition(this.position.buildPosition(sourceStart, sourceEnd));

			va = factory.Code().createVariableAccess(ref, isOtherBinding && fromAssignment);
		}

		if (qualifiedNameReference.otherBindings != null) {
			int i = 0; //positions index;
			va.setPosition(ref.getPosition());
			sourceStart = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding - 1] >>> 32);
			for (FieldBinding b : qualifiedNameReference.otherBindings) {
				isOtherBinding = qualifiedNameReference.otherBindings.length == i + 1;
				CtFieldAccess<T> other = factory.Code().createFieldAccess(references.<T>getVariableReference(b, qualifiedNameReference.tokens[i + 1]), va, isOtherBinding && fromAssignment);
				//set source position of fa
				if (i + qualifiedNameReference.indexOfFirstFieldBinding >= qualifiedNameReference.otherBindings.length) {
					sourceEnd = qualifiedNameReference.sourceEnd();
				} else {
					sourceEnd = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding + i + 1] >>> 32) - 2;
				}
				other.setPosition(this.position.buildPosition(sourceStart, sourceEnd));
				va = other;
				i++;
			}
		} else if (!(qualifiedNameReference.binding instanceof FieldBinding) && qualifiedNameReference.tokens.length > 1) {
			sourceStart = (int) (positions[0] >>> 32);
			for (int i = 1; i < qualifiedNameReference.tokens.length; i++) {
				isOtherBinding = qualifiedNameReference.tokens.length == i + 1;
				CtFieldAccess<T> other = factory.Code().createFieldAccess(references.<T>getVariableReference(null, qualifiedNameReference.tokens[i]), va, isOtherBinding && fromAssignment);
				//set source position of va;
				CompilationUnit cu = factory.CompilationUnit().create(new String(context.compilationunitdeclaration.getFileName()));
				sourceEnd = (int) (positions[i]);
				final int[] lineSeparatorPositions = context.compilationunitdeclaration.compilationResult.lineSeparatorPositions;
				va.setPosition(factory.Core().createSourcePosition(cu, sourceStart, sourceStart, sourceEnd, lineSeparatorPositions));
				va = other;
			}
		}
		va.setPosition(this.position.buildPosition(qualifiedNameReference.sourceStart(), qualifiedNameReference.sourceEnd()));
		return va;
	}

	private CtExpression<?> createTargetFieldAccess(QualifiedNameReference qualifiedNameReference, CtFieldReference<Object> ref) {
		CtExpression<?> target = null;
		if (isValidProblemBindingField(qualifiedNameReference)) {
			target = createTargetTypeAccessNoClasspath(qualifiedNameReference);
		} else if (ref.isStatic()) {
			target = createTargetTypeAccess(qualifiedNameReference, ref);
		} else if (!ref.isStatic() && !ref.getDeclaringType().isAnonymous()) {
			target = createTargetThisAccess(qualifiedNameReference);
		}
		return target;
	}

	private <T> CtThisAccess<T> createTargetThisAccess(QualifiedNameReference qualifiedNameReference) {
		return factory.Code().createThisAccess(references.<T>getTypeReference(qualifiedNameReference.actualReceiverType), true);
	}

	private CtTypeAccess<?> createTargetTypeAccess(QualifiedNameReference qualifiedNameReference, CtFieldReference<?> fieldReference) {
		final TypeBinding receiverType = qualifiedNameReference.actualReceiverType;
		if (receiverType != null) {
			final CtTypeReference<Object> qualifiedRef = this.references.getQualifiedTypeReference(qualifiedNameReference.tokens, receiverType, qualifiedNameReference.fieldBinding().declaringClass.enclosingType(), new OnAccessListener() {
				@Override
				public boolean onAccess(char[][] tokens, int index) {
					return !CharOperation.equals(tokens[index + 1], tokens[tokens.length - 1]);
				}
			});
			if (qualifiedRef != null) {
				fieldReference.setDeclaringType(qualifiedRef);
			} else {
				fieldReference.setDeclaringType(references.getTypeReference(receiverType));
			}
		}

		CtTypeAccess<?> typeAccess = factory.Code().createTypeAccess(fieldReference.getDeclaringType());
		if (qualifiedNameReference.indexOfFirstFieldBinding > 1) {
			// the array sourcePositions contains the position of each element of the qualifiedNameReference
			// the last element contains the position of the field
			long[] positions = qualifiedNameReference.sourcePositions;
			typeAccess.setPosition(this.position.buildPosition(qualifiedNameReference.sourceStart(), (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding - 1] >>> 32) - 2));
		} else {
			typeAccess.setImplicit(true);
		}

		return typeAccess;
	}

	private <T> CtTypeAccess<T> createTargetTypeAccessNoClasspath(QualifiedNameReference qualifiedNameReference) {
		final ReferenceBinding declaringClass = ((FieldBinding) qualifiedNameReference.binding).declaringClass;
		final CtTypeAccess<T> typeAccess = factory.Code().createTypeAccess(references.<T>getTypeReference(declaringClass));

		long[] positions = qualifiedNameReference.sourcePositions;
		int sourceStart = qualifiedNameReference.sourceStart();
		int sourceEnd = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding - 1] >>> 32) - 2;
		typeAccess.setPosition(this.position.buildPosition(sourceStart, sourceEnd));

		return typeAccess;
	}

	private boolean isValidProblemBindingField(QualifiedNameReference qualifiedNameReference) {
		return qualifiedNameReference.binding instanceof ProblemFieldBinding
				&& !((FieldBinding) qualifiedNameReference.binding).declaringClass.isAnonymousType()
				&& qualifiedNameReference.tokens.length - 1 == ((FieldBinding) qualifiedNameReference.binding).declaringClass.compoundName.length
				&& CharOperation.equals(CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1), ((FieldBinding) qualifiedNameReference.binding).declaringClass.compoundName);
	}

	@Override
	public boolean visit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		if (context.stack.peekFirst().node instanceof UnionTypeReference) {
			context.enter(references.<Throwable>getTypeReference(qualifiedTypeReference.resolvedType), qualifiedTypeReference);
			return true;
		} else if (context.stack.peekFirst().element instanceof CtCatch) {
			final Argument jdtCatch = (Argument) context.stack.peekFirst().node;
			final Set<ModifierKind> modifiers = getModifiers(jdtCatch.modifiers);
			final CtCatchVariable<Throwable> catchVariable = factory.Code().createCatchVariable(//
					references.<Throwable>getTypeReference(qualifiedTypeReference.resolvedType), //
					CharOperation.charToString(jdtCatch.name), //
					modifiers.toArray(new ModifierKind[modifiers.size()]));
			context.enter(catchVariable, qualifiedTypeReference);
			return true;
		}
		final CtTypeAccess<Object> typeAccess = factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference(qualifiedTypeReference.resolvedType));
		context.enter(typeAccess, qualifiedTypeReference);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope) {
		return visit(qualifiedTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(ReturnStatement returnStatement, BlockScope scope) {
		context.enter(factory.Core().createReturn(), returnStatement);
		return true;
	}

	@Override
	public boolean visit(SingleNameReference singleNameReference, BlockScope scope) {
		if (singleNameReference.binding instanceof FieldBinding) {
			context.enter(createFieldAccess(singleNameReference), singleNameReference);
		} else if (singleNameReference.binding instanceof VariableBinding) {
			context.enter(createVariableAccess(singleNameReference), singleNameReference);
		} else if (singleNameReference.binding instanceof TypeBinding) {
			context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference((TypeBinding) singleNameReference.binding)), singleNameReference);
		} else if (singleNameReference.binding instanceof ProblemBinding) {
			if (context.stack.peek().element instanceof CtInvocation && Character.isUpperCase(CharOperation.charToString(singleNameReference.token).charAt(0))) {
				context.enter(createInvocationNoClasspath(singleNameReference), singleNameReference);
			} else {
				context.enter(createFieldAccessNoClasspath(singleNameReference), singleNameReference);
			}
		} else if (singleNameReference.binding == null) {
			context.enter(createVariableAccessNoClasspath(singleNameReference), singleNameReference);
		}
		return true;
	}

	private <T> CtTypeAccess<T> createInvocationNoClasspath(SingleNameReference singleNameReference) {
		CtTypeReference<T> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(new String(singleNameReference.binding.readableName()));
		setPackageOrDeclaringType(typeReference, references.getDeclaringReferenceFromImports(singleNameReference.token));
		return factory.Code().createTypeAccess(typeReference);
	}

	private <T> CtVariableAccess<T> createVariableAccess(SingleNameReference singleNameReference) {
		CtVariableAccess<T> va;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			va = factory.Core().createVariableWrite();
		} else {
			va = factory.Core().createVariableRead();
		}
		va.setVariable(references.<T>getVariableReference((VariableBinding) singleNameReference.binding));
		return va;
	}

	/**
	 * In this case, we are in no classpath so we don't know if the access is a variable, a field or a type.
	 * By default, we assume that when we don't have any information, we create a variable access.
	 */
	private <T> CtVariableAccess<T> createVariableAccessNoClasspath(SingleNameReference singleNameReference) {
		CtVariableAccess<T> va;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			va = factory.Core().createVariableWrite();
		} else {
			va = factory.Core().createVariableRead();
		}
		final String name = CharOperation.charToString(singleNameReference.token);
		CtVariableReference<T> ref;
		if (context.isBuildLambda) {
			ref = factory.Core().createParameterReference();
		} else {
			ref = factory.Core().createLocalVariableReference();
			((CtLocalVariableReference<T>) ref).setDeclaration(this.context.<T>getLocalVariableDeclaration(name));
		}
		ref.setSimpleName(name);
		va.setVariable(ref);
		return va;
	}

	private <T> CtFieldAccess<T> createFieldAccess(SingleNameReference singleNameReference) {
		CtFieldAccess<T> va;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			va = factory.Core().createFieldWrite();
		} else {
			va = factory.Core().createFieldRead();
		}
		va.setVariable(references.<T>getVariableReference(singleNameReference.fieldBinding().original()));
		if (va.getVariable() != null) {
			final CtFieldReference<T> ref = va.getVariable();
			if (ref.isStatic() && !ref.getDeclaringType().isAnonymous()) {
				va.setTarget(factory.Code().createTypeAccess(ref.getDeclaringType()));
			} else if (!ref.isStatic()) {
				va.setTarget(factory.Code().createThisAccess(references.getTypeReference(singleNameReference.actualReceiverType), true));
			}
		}
		return va;
	}

	private <T> CtFieldAccess<T> createFieldAccessNoClasspath(SingleNameReference singleNameReference) {
		CtFieldAccess<T> va;
		if (context.stack.peek().element instanceof CtAssignment && context.assigned) {
			va = factory.Core().createFieldWrite();
		} else {
			va = factory.Core().createFieldRead();
		}
		va.setVariable(references.<T>getVariableReference((ProblemBinding) singleNameReference.binding));
		final CtReference declaring = references.getDeclaringReferenceFromImports(singleNameReference.token);
		if (declaring instanceof CtTypeReference && va.getVariable() != null) {
			final CtTypeReference<Object> declaringRef = (CtTypeReference<Object>) declaring;
			va.setTarget(factory.Code().createTypeAccess(declaringRef));
			va.getVariable().setDeclaringType(declaringRef);
			va.getVariable().setStatic(true);
		}
		return va;
	}

	@Override
	public boolean visit(QualifiedSuperReference qualifiedSuperReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		context.enter(factory.Core().createSuperAccess(), qualifiedSuperReference);
		return true;
	}

	@Override
	public boolean visit(SuperReference superReference, BlockScope scope) {
		context.enter(factory.Core().createSuperAccess(), superReference);
		return true;
	}

	@Override
	public boolean visit(QualifiedThisReference qualifiedThisRef, BlockScope scope) {
		context.enter(factory.Code().createThisAccess(references.getTypeReference(qualifiedThisRef.qualification.resolvedType), qualifiedThisRef.isImplicitThis()), qualifiedThisRef);
		return true;
	}

	@Override
	public boolean visit(ThisReference thisReference, BlockScope scope) {
		context.enter(factory.Code().createThisAccess(references.getTypeReference(thisReference.resolvedType), thisReference.isImplicitThis()), thisReference);
		return true;
	}

	@Override
	public void endVisit(UnionTypeReference unionTypeReference, BlockScope scope) {
		context.exit(unionTypeReference);
	}

	@Override
	public void endVisit(UnionTypeReference unionTypeReference, ClassScope scope) {
		endVisit(unionTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(UnionTypeReference unionTypeReference, BlockScope scope) {
		if (!(context.stack.peekFirst().node instanceof Argument)) {
			throw new SpoonException("UnionType is only supported for CtCatch.");
		}
		final Argument jdtCatch = (Argument) context.stack.peekFirst().node;
		final Set<ModifierKind> modifiers = getModifiers(jdtCatch.modifiers);
		final CtCatchVariable<Throwable> catchVariable = factory.Code().createCatchVariable(//
				references.<Throwable>getTypeReference(unionTypeReference.resolvedType), //
				CharOperation.charToString(jdtCatch.name), //
				modifiers.toArray(new ModifierKind[modifiers.size()]));
		context.enter(catchVariable, unionTypeReference);
		return true;
	}

	@Override
	public boolean visit(UnionTypeReference unionTypeReference, ClassScope scope) {
		return visit(unionTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(SingleTypeReference singleTypeReference, BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		if (context.stack.peekFirst().node instanceof UnionTypeReference) {
			context.enter(references.<Throwable>getTypeReference(singleTypeReference.resolvedType), singleTypeReference);
			return true;
		} else if (context.stack.peekFirst().element instanceof CtCatch) {
			final Argument jdtCatch = (Argument) context.stack.peekFirst().node;
			final Set<ModifierKind> modifiers = getModifiers(jdtCatch.modifiers);
			final CtCatchVariable<Throwable> catchVariable = factory.Code().createCatchVariable(//
					references.<Throwable>getTypeReference(singleTypeReference.resolvedType), //
					CharOperation.charToString(jdtCatch.name), //
					modifiers.toArray(new ModifierKind[modifiers.size()]));
			context.enter(catchVariable, singleTypeReference);
			return true;
		}
		final CtTypeAccess<Object> typeAccess = factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference(singleTypeReference.resolvedType));
		context.enter(typeAccess, singleTypeReference);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(SingleTypeReference singleTypeReference, ClassScope scope) {
		return visit(singleTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(StringLiteral stringLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(CharOperation.charToString(stringLiteral.source())).setType(factory.Type().stringType()), stringLiteral);
		return true;
	}

	@Override
	public boolean visit(StringLiteralConcatenation literal, BlockScope scope) {
		context.enter(factory.Core().createBinaryOperator().<CtBinaryOperator>setKind(BinaryOperatorKind.PLUS), literal);
		return true;
	}

	@Override
	public boolean visit(CaseStatement caseStatement, BlockScope scope) {
		if (context.stack.peek().node instanceof CaseStatement) {
			context.exit(context.stack.peek().node);
		}

		context.enter(factory.Core().createCase(), caseStatement);
		return true;
	}

	@Override
	public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
		context.enter(factory.Core().createSwitch(), switchStatement);
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement synchronizedStatement, BlockScope scope) {
		context.enter(factory.Core().createSynchronized(), synchronizedStatement);
		return true;
	}

	@Override
	public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
		context.enter(factory.Core().createThrow(), throwStatement);
		return true;
	}

	@Override
	public boolean visit(TrueLiteral trueLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(true), trueLiteral);
		return true;
	}

	@Override
	public boolean visit(TryStatement tryStatement, BlockScope scope) {
		CtTry t;
		if (tryStatement.resources.length > 0) {
			t = factory.Core().createTryWithResource();
		} else {
			t = factory.Core().createTry();
		}
		context.enter(t, tryStatement);
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		CtType<?> t;
		if (localTypeDeclaration.binding == null) {
			// no classpath mode but JDT returns nothing. We create an empty class.
			t = factory.Core().createClass();
			t.setSimpleName(CtType.NAME_UNKNOWN);
			((CtClass) t).setSuperclass(references.getTypeReference(null, localTypeDeclaration.allocation.type));
			context.enter(t, localTypeDeclaration);
		} else {
			createType(localTypeDeclaration);
		}
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		createType(memberTypeDeclaration);
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
		if (new String(typeDeclaration.name).equals("package-info")) {
			context.enter(factory.Package().getOrCreate(new String(typeDeclaration.binding.fPackage.readableName())), typeDeclaration);
			return true;
		} else {
			CtPackage pack;
			if (typeDeclaration.binding.fPackage.shortReadableName() != null && typeDeclaration.binding.fPackage.shortReadableName().length > 0) {
				pack = factory.Package().getOrCreate(new String(typeDeclaration.binding.fPackage.shortReadableName()));
			} else {
				pack = factory.Package().getRootPackage();
			}
			context.enter(pack, typeDeclaration);
			pack.addType(createType(typeDeclaration));
			return true;
		}
	}

	@Override
	public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		op.setKind(getUnaryOperator((unaryExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, unaryExpression);
		return true;
	}

	@Override
	public boolean visit(WhileStatement whileStatement, BlockScope scope) {
		context.enter(factory.Core().createWhile(), whileStatement);
		return true;
	}
}
