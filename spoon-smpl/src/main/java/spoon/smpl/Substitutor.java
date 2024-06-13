/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;


import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Substitutor traverses a given AST and replaces elements that match bindings in a given
 * mapping between metavariable names and values.
 * <p>
 * For example, given a map of bindings containing the key "x" mapped to a CtVariableReference
 * with simple name "y", the substitution should replace any occurences of a CtVariableReference
 * with simple name "x" found in the given AST with the CtVariableReference with simple name "y".
 */
public class Substitutor implements CtVisitor {
	/**
	 * Create a new Substitutor for a given AST element using a given set of metavariable bindings.
	 * The given element is cloned so the input will not be mutated.
	 *
	 * @param element  Element to perform substitutions on
	 * @param bindings Metavariable bindings, the string is the simpe name of local variables
	 */
	public Substitutor(CtElement element, Map<String, Object> bindings) {
		this.element = element.clone();
		this.bindings = bindings;
	}

	/**
	 * Get the result of applying the substitutions.
	 *
	 * @return Element with substitutions applied
	 */
	public CtElement getResult() {
		return element;
	}

	/**
	 * Apply substitutions to a given AST element using a given set of metavariable bindings.
	 * The given element is cloned so the input will not be mutated.
	 *
	 * @param element  Element to apply substitutions to
	 * @param bindings Metavariable bindings
	 * @return Element with substitutions applied
	 */
	public static CtElement apply(CtElement element, Map<String, Object> bindings) {
		Substitutor sub = new Substitutor(element, bindings);
		sub.run();
		return sub.getResult();
	}

	/**
	 * Initiate the processing.
	 */
	private void run() {
		element.accept(this);
	}

	/**
	 * Element to apply substitutions to (in-place, so also holds the result).
	 */
	private CtElement element;

	/**
	 * Metavariable bindings to use.
	 */
	private Map<String, Object> bindings;

	@Override
	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignment) {
		assignment.getAssigned().accept(this);
		assignment.getAssignment().accept(this);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		operator.getLeftHandOperand().accept(this);
		operator.getRightHandOperand().accept(this);
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		for (CtStatement stmt : block.getStatements()) {
			stmt.accept(this);
		}
	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <S> void visitCtCase(CtCase<S> caseStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		ifElement.getCondition().accept(this);
		ifElement.getThenStatement().accept(this);

		if (ifElement.getElseStatement() != null) {
			ifElement.getElseStatement().accept(this);
		}
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		List<CtExpression<?>> arguments = invocation.getArguments();

		for (int i = 0; i < arguments.size(); ++i) {
			arguments.get(i).accept(this);
		}

		if (invocation.getTarget() != null) {
			invocation.getTarget().accept(this);
		}
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
	}

	@Override
	public void visitCtTextBlock(CtTextBlock ctTextBlock) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		localVariable.getType().accept(this);

		// Since CtLocalVariable doesnt actually store a variable reference we cant do .getReference().replace
		String variableName = localVariable.getSimpleName();

		if (bindings.containsKey(variableName)) {
			CtElement replacement = (CtElement) bindings.get(variableName);

			if (replacement instanceof CtVariableReference) {
				localVariable.setSimpleName(((CtVariableReference<?>) replacement).getSimpleName());
			} else {
				throw new IllegalArgumentException("unhandled type " + replacement.getClass().toString());
			}
		}

		if (localVariable.getDefaultExpression() != null) {
			localVariable.getDefaultExpression().accept(this);
		}
	}

	@Override
	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		if (bindings.containsKey(reference.getSimpleName()) && bindings.get(reference.getSimpleName()) instanceof CtLocalVariableReference<?>) {
			reference.replace((CtElement) bindings.get(reference.getSimpleName()));
		}
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		// TODO: identifier substitution

		ctConstructorCall.getArguments().forEach(e -> e.accept(this));
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> expression) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		if (bindings.containsKey(reference.getSimpleName())) {
			reference.replace((CtElement) bindings.get(reference.getSimpleName()));
		}
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		returnStatement.getReturnedExpression().accept(this);
	}

	@Override
	public <R> void visitCtStatementList(CtStatementList statements) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		String typename = reference.getSimpleName();

		if (bindings.containsKey(typename)) {
			if (!reference.isParentInitialized()) {
				// TODO: why does this case occur? e.g method header transform using type metavar
				reference.setSimpleName(((CtTypeReference<?>) bindings.get(typename)).getSimpleName());
			} else {
				reference.replace((CtElement) bindings.get(typename));
			}
		}
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		String typename = typeAccess.getAccessedType().getSimpleName();

		if (bindings.containsKey(typename)) {
			typeAccess.replace((CtElement) bindings.get(typename));
		}
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		operator.getOperand().accept(this);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		variableRead.getVariable().accept(this);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		variableWrite.getVariable().accept(this);
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		if (fieldRead.getTarget() != null) {
			fieldRead.getTarget().accept(this);
		}

		String varname = fieldRead.getVariable().getSimpleName();

		if (!bindings.containsKey(varname)) {
			return;
		}

		Object boundValue = bindings.get(varname);

		if (boundValue instanceof CtVariableReference<?>) {
			fieldRead.getVariable().setSimpleName(((CtVariableReference<?>) bindings.get(varname)).getSimpleName());
		} else if (boundValue instanceof CtExpression<?>) {
			fieldRead.replace((CtExpression<?>) boundValue);
		}
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		String varname = fieldWrite.getVariable().getSimpleName();

		if (bindings.containsKey(varname) && bindings.get(varname) instanceof CtVariableReference<?>) {
			fieldWrite.getVariable().setSimpleName(((CtVariableReference<?>) bindings.get(varname)).getSimpleName());
		}
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtComment(CtComment comment) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc comment) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtJavaDocTag(CtJavaDocTag docTag) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtImport(CtImport ctImport) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtModule(CtModule module) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtYieldStatement(CtYieldStatement ctYieldStatement) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtTypePattern(CtTypePattern pattern) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtRecord(CtRecord recordType) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtRecordComponent(CtRecordComponent recordComponent) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtCasePattern(CtCasePattern casePattern) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtRecordPattern(CtRecordPattern recordPattern) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtUnnamedPattern(CtUnnamedPattern unnamedPattern) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public void visitCtReceiverParameter(CtReceiverParameter receiverParameter) {
		throw new NotImplementedException("Not implemented");
	}
}
