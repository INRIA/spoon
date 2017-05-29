/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getBinaryOperatorKind;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getModifiers;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getUnaryOperator;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.isLhsAssignment;

import java.util.Collections;

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
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
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
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
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
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.support.comparator.CtLineElementComparator;

/**
 * A visitor for iterating through the parse tree.
 */
public class JDTTreeBuilder extends ASTVisitor {

	private final PositionBuilder position;

	private final ContextBuilder context;

	private final ParentExiter exiter;

	final ReferenceBuilder references;

	private final JDTTreeBuilderHelper helper;

	private final Factory factory;

	boolean skipTypeInAnnotation = false;

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

	public JDTTreeBuilderHelper getHelper() {
		return helper;
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
		this.helper = new JDTTreeBuilderHelper(this);
		LOGGER.setLevel(factory.getEnvironment().getLevel());
	}

	interface OnAccessListener {
		boolean onAccess(char[][] tokens, int index);
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
	}

	@Override
	public void endVisit(TypeParameter typeParameter, ClassScope scope) {
		context.exit(typeParameter);
	}

	@Override
	public void endVisit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		Collections.sort(((CtType) context.stack.peek().element).getTypeMembers(), new CtLineElementComparator());
		context.exit(localTypeDeclaration);
	}

	@Override
	public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		while (!context.stack.isEmpty() && context.stack.peek().node == memberTypeDeclaration) {
			Collections.sort(((CtType) context.stack.peek().element).getTypeMembers(), new CtLineElementComparator());
			context.exit(memberTypeDeclaration);
		}
	}

	@Override
	public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
		while (!context.stack.isEmpty() && context.stack.peek().node == typeDeclaration) {
			if (context.stack.peek().element instanceof CtType) {
				Collections.sort(((CtType) context.stack.peek().element).getTypeMembers(), new CtLineElementComparator());
			}
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
	public boolean visit(Javadoc javadoc, BlockScope scope) {
		// Use a custom compiler.
		return false;
	}

	@Override
	public boolean visit(Javadoc javadoc, ClassScope scope) {
		// Use a custom compiler.
		return false;
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
		context.enter(helper.createExecutableReferenceExpression(referenceExpression), referenceExpression);
		return true;
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
			lambda.setSimpleName(CharOperation.charToString(methodBinding.constantPoolName()));
		}
		context.isBuildLambda = true;
		context.enter(lambda, lambdaExpression);
		return true;
	}

	@Override
	public void endVisit(LambdaExpression lambdaExpression, BlockScope blockScope) {
		context.isBuildLambda = false;
		context.exit(lambdaExpression);
	}

	@Override
	public boolean visit(AllocationExpression allocationExpression, BlockScope scope) {
		CtConstructorCall constructorCall = factory.Core().createConstructorCall();
		constructorCall.setExecutable(references.getExecutableReference(allocationExpression));
		context.enter(constructorCall, allocationExpression);
		return true;
	}

	@Override
	public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {
		CtConstructorCall constructorCall;
		if (qualifiedAllocationExpression.anonymousType != null) {
			constructorCall = factory.Core().createNewClass();
		} else {
			constructorCall = factory.Core().createConstructorCall();
		}
		constructorCall.setExecutable(references.getExecutableReference(qualifiedAllocationExpression));
		context.enter(constructorCall, qualifiedAllocationExpression);
		return true;
	}

	@Override
	public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((and_and_Expression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, and_and_Expression);
		return true;
	}

	@Override
	public boolean visit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope) {
		CtAnnotationMethod<Object> ctAnnotationMethod = factory.Core().createAnnotationMethod();
		ctAnnotationMethod.setSimpleName(CharOperation.charToString(annotationTypeDeclaration.selector));
		context.enter(ctAnnotationMethod, annotationTypeDeclaration);
		return true;
	}

	@Override
	public boolean visit(Argument argument, BlockScope scope) {
		if (this.getContextBuilder().stack.peekFirst().element instanceof CtTry) {
			context.enter(factory.Core().createCatch(), argument);
			return true;
		}
		context.enter(helper.createParameter(argument), argument);
		return true;
	}

	@Override
	public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {
		context.enter(factory.Core().createNewArray(), arrayAllocationExpression);
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {
		context.enter(factory.Core().createNewArray(), arrayInitializer);
		return true;
	}

	@Override
	public boolean visit(ArrayReference arrayReference, BlockScope scope) {
		CtArrayAccess<?, ?> a;
		if (isLhsAssignment(context, arrayReference)) {
			a = factory.Core().createArrayWrite();
		} else {
			a = factory.Core().createArrayRead();
		}
		context.enter(a, arrayReference);
		return true;
	}

	@Override
	public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		final CtTypeAccess<Object> typeAccess = factory.Code().createTypeAccess(references.buildTypeReference(arrayTypeReference, scope));
		if (typeAccess.getAccessedType() instanceof CtArrayTypeReference) {
			((CtArrayTypeReference) typeAccess.getAccessedType()).getArrayType().setAnnotations(this.references.buildTypeReference(arrayTypeReference, scope).getAnnotations());
		}
		context.enter(typeAccess, arrayTypeReference);
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
		context.enter(factory.Core().createAssert(), assertStatement);
		return true;
	}

	@Override
	public boolean visit(Assignment assignment, BlockScope scope) {
		context.enter(factory.Core().createAssignment(), assignment);
		return true;
	}

	@Override
	public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
		CtOperatorAssignment<Object, Object> a = factory.Core().createOperatorAssignment();
		a.setKind(getBinaryOperatorKind(compoundAssignment.operator));
		context.enter(a, compoundAssignment);
		return true;
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
	public boolean visit(ConditionalExpression conditionalExpression, BlockScope scope) {
		context.enter(factory.Core().createConditional(), conditionalExpression);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		CtMethod<Object> m = factory.Core().createMethod();
		m.setSimpleName(CharOperation.charToString(methodDeclaration.selector));
		m.setModifiers(getModifiers(methodDeclaration.modifiers));
		m.setDefaultMethod(methodDeclaration.isDefaultMethod());

		context.enter(m, methodDeclaration);

		// Create block
		if (!methodDeclaration.isAbstract() && (methodDeclaration.modifiers & ClassFileConstants.AccNative) == 0) {
			context.enter(getFactory().Core().createBlock(), methodDeclaration);
			context.exit(methodDeclaration);
		}

		// We consider the receiver as a standard argument (i.e. as a parameter)
		Receiver receiver = methodDeclaration.receiver;
		if (receiver != null) {
			receiver.traverse(this, methodDeclaration.scope);
		}

		return true;
	}

	@Override
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		CtConstructor<Object> c = factory.Core().createConstructor();
		c.setModifiers(getModifiers(constructorDeclaration.modifiers));
		context.enter(c, constructorDeclaration);

		// Create block
		context.enter(factory.Core().createBlock(), constructorDeclaration);
		context.exit(constructorDeclaration);

		return true;
	}

	@Override
	public boolean visit(TypeParameter typeParameter, ClassScope scope) {
		return visitTypeParameter(typeParameter, scope);
	}

	@Override
	public boolean visit(TypeParameter typeParameter, BlockScope scope) {
		return visitTypeParameter(typeParameter, scope);
	}

	private boolean visitTypeParameter(TypeParameter typeParameter, Scope scope) {
		final CtTypeParameter typeParameterRef = factory.Core().createTypeParameter();
		typeParameterRef.setSimpleName(CharOperation.charToString(typeParameter.name));
		context.enter(typeParameterRef, typeParameter);
		return true;
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
		doubleLiteral.computeConstant();
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

	@Override
	public boolean visit(ExplicitConstructorCall explicitConstructor, BlockScope scope) {
		CtInvocation<Object> inv = factory.Core().createInvocation();
		inv.setImplicit(explicitConstructor.isImplicitSuper());
		inv.setExecutable(references.getExecutableReference(explicitConstructor.binding));
		inv.getExecutable().setType((CtTypeReference<Object>) inv.getExecutable().getDeclaringType());

		context.enter(inv, explicitConstructor);
		return true;
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
		} else {
			field = factory.Core().createEnumValue();
			if (fieldDeclaration.binding != null) {
				field.setType(references.getTypeReference(fieldDeclaration.binding.type));
			}
		}
		field.setSimpleName(CharOperation.charToString(fieldDeclaration.name));
		field.setModifiers(getModifiers(fieldDeclaration.modifiers));
		context.enter(field, fieldDeclaration);
		return true;
	}

	@Override
	public boolean visit(FieldReference fieldReference, BlockScope scope) {
		context.enter(helper.createFieldAccess(fieldReference), fieldReference);
		return true;
	}

	@Override
	public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {
		floatLiteral.computeConstant();
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
		context.enter(factory.Core().createFor(), forStatement);
		return true;
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
		intLiteral.computeConstant();
		CtLiteral<Integer> l = factory.Code().createLiteral(intLiteral.constant.intValue());
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
		v.setSimpleName(CharOperation.charToString(localDeclaration.name));
		v.setModifiers(getModifiers(localDeclaration.modifiers));
		context.enter(v, localDeclaration);
		return true;
	}

	@Override
	public boolean visit(LongLiteral longLiteral, BlockScope scope) {
		longLiteral.computeConstant();
		context.enter(factory.Code().createLiteral(longLiteral.constant.longValue()), longLiteral);
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
		CtInvocation<Object> inv = factory.Core().createInvocation();
		inv.setExecutable(references.getExecutableReference(messageSend));
		if (messageSend.binding instanceof ProblemMethodBinding) {
			// We are in a static complex in noclasspath mode.
			if (inv.getExecutable() != null && inv.getExecutable().getDeclaringType() != null) {
				inv.setTarget(factory.Code().createTypeAccess(inv.getExecutable().getDeclaringType(), inv.getExecutable().getDeclaringType().isAnonymous()));
			}
			if (messageSend.expectedType() != null) {
				inv.getExecutable().setType(references.getTypeReference(messageSend.expectedType()));
			}
		}
		context.enter(inv, messageSend);
		return true;
	}

	@Override
	public boolean visit(NullLiteral nullLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(null), nullLiteral);
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
		context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.buildTypeReference(parameterizedTypeReference, null)), parameterizedTypeReference);
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
	public boolean visit(QualifiedNameReference qualifiedNameRef, BlockScope scope) {
		if (qualifiedNameRef.binding instanceof FieldBinding || qualifiedNameRef.binding instanceof VariableBinding) {
			context.enter(helper.createVariableAccess(qualifiedNameRef), qualifiedNameRef);
			return true;
		} else if (qualifiedNameRef.binding instanceof TypeBinding) {
			context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference((TypeBinding) qualifiedNameRef.binding)), qualifiedNameRef);
			return true;
		} else if (qualifiedNameRef.binding instanceof ProblemBinding) {
			if (context.stack.peek().element instanceof CtInvocation) {
				context.enter(helper.createTypeAccessNoClasspath(qualifiedNameRef), qualifiedNameRef);
				return true;
			}
			context.enter(helper.createFieldAccessNoClasspath(qualifiedNameRef), qualifiedNameRef);
			return true;
		} else {
			context.enter(
					helper.createVariableAccess(
							factory.Core().createUnboundVariableReference().<CtUnboundVariableReference>setSimpleName(qualifiedNameRef.toString()),
							isLhsAssignment(context, qualifiedNameRef)),
					qualifiedNameRef
			);
			return true;
		}
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
			context.enter(helper.createCatchVariable(qualifiedTypeReference), qualifiedTypeReference);
			return true;
		}
		context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.buildTypeReference(qualifiedTypeReference, scope)), qualifiedTypeReference);
		return true;
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
			context.enter(helper.createFieldAccess(singleNameReference), singleNameReference);
		} else if (singleNameReference.binding instanceof VariableBinding) {
			context.enter(helper.createVariableAccess(singleNameReference), singleNameReference);
		} else if (singleNameReference.binding instanceof TypeBinding) {
			context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.getTypeReference((TypeBinding) singleNameReference.binding)), singleNameReference);
		} else if (singleNameReference.binding instanceof ProblemBinding) {
			if (context.stack.peek().element instanceof CtInvocation && Character.isUpperCase(CharOperation.charToString(singleNameReference.token).charAt(0))) {
				context.enter(helper.createTypeAccessNoClasspath(singleNameReference), singleNameReference);
			} else {
				context.enter(helper.createFieldAccessNoClasspath(singleNameReference), singleNameReference);
			}
		} else if (singleNameReference.binding == null) {
			CtExpression access = helper.createVariableAccessNoClasspath(singleNameReference);
			if (access == null) {
				access = helper.createTypeAccessNoClasspath(singleNameReference);
			}
			context.enter(access, singleNameReference);
		}
		return true;
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
		context.enter(helper.createCatchVariable(unionTypeReference), unionTypeReference);
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
			context.enter(helper.createCatchVariable(singleTypeReference), singleTypeReference);
			return true;
		}
		context.enter(factory.Code().createTypeAccessWithoutCloningReference(references.buildTypeReference(singleTypeReference, scope)), singleTypeReference);
		return true;
	}

	@Override
	public boolean visit(SingleTypeReference singleTypeReference, ClassScope scope) {
		return visit(singleTypeReference, (BlockScope) null);
	}

	@Override
	public boolean visit(StringLiteral stringLiteral, BlockScope scope) {
		context.enter(factory.Code().createLiteral(CharOperation.charToString(stringLiteral.source())), stringLiteral);
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
			helper.createType(localTypeDeclaration);
		}
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		helper.createType(memberTypeDeclaration);
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
			pack.addType(helper.createType(typeDeclaration));
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
