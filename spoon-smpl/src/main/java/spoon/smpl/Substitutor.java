package spoon.smpl;


import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Substitutor traverses a given AST and replaces elements that match bindings in a given
 * mapping between metavariable names and values.
 *
 * For example, given a map of bindings containing the key "x" mapped to a CtVariableReference
 * with simple name "y", the substitution should replace any occurences of a CtVariableReference
 * with simple name "x" found in the given AST with the CtVariableReference with simple name "y".
 */
public class Substitutor implements CtVisitor {
    /**
     * Create a new Substitutor for a given AST element using a given set of metavariable bindings.
     * The given element is cloned so the input will not be mutated.
     * @param element Element to perform substitutions on
     * @param bindings Metavariable bindings
     */
    public Substitutor(CtElement element, Map<String, Object> bindings) {
        this.element = element.clone();
        this.bindings = bindings;
    }

    /**
     * Get the result of applying the substitutions.
     * @return Element with substitutions applied
     */
    public CtElement getResult() {
        return element;
    }

    /**
     * Apply substitutions to a given AST element using a given set of metavariable bindings.
     * The given element is cloned so the input will not be mutated.
     * @param element Element to apply substitutions to
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
    public <T> void visitCtLiteral(CtLiteral<T> literal) {}

    @Override
    public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
        localVariable.getType().accept(this);
        localVariable.getReference().accept(this);

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
        throw new NotImplementedException("Not implemented");
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
            reference.replace((CtElement) bindings.get(typename));
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
}
