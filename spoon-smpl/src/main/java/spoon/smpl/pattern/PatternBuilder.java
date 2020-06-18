package spoon.smpl.pattern;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.CtVisitor;
import org.apache.commons.lang3.NotImplementedException;
import spoon.smpl.SmPLJavaDSL;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Part of temporary substitute for spoon.pattern
 */
public class PatternBuilder implements CtVisitor {
    public PatternBuilder() {
        this(new ArrayList<>());
    }

    /**
     * Create a new PatternBuilder using a list of parameter identifiers.
     * Elements identified by a String (e.g variables and types) that match an entry
     * in the list of parameters will generate parameter nodes.
     *
     * @param params List of parameter identifiers
     */
    public PatternBuilder(List<String> params) {
        this.params = params;
        resultStack = new Stack<>();
    }

    public PatternNode getResult() {
        return resultStack.pop();
    }

    @Override
    public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> ctAnnotation) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> ctCodeSnippetExpression) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtCodeSnippetStatement(CtCodeSnippetStatement ctCodeSnippetStatement) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> ctAnnotationType) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtAnonymousExecutable(CtAnonymousExecutable ctAnonymousExecutable) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtArrayRead(CtArrayRead<T> ctArrayRead) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtArrayWrite(CtArrayWrite<T> ctArrayWrite) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> ctArrayTypeReference) {
        String typename = ctArrayTypeReference.getSimpleName();

        if (params.contains(typename)) {
            resultStack.push(new ParamNode(typename));
        } else {
            ElemNode result = new ElemNode(ctArrayTypeReference);
            result.sub.put("typename", new ValueNode(ctArrayTypeReference.getSimpleName(), ctArrayTypeReference));
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtAssert(CtAssert<T> ctAssert) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> ctAssignment) {
        ElemNode result = new ElemNode(ctAssignment);

        ctAssignment.getAssigned().accept(this);
        result.sub.put("lhs", resultStack.pop());

        ctAssignment.getAssignment().accept(this);
        result.sub.put("rhs", resultStack.pop());

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtBinaryOperator(CtBinaryOperator<T> ctBinaryOperator) {
        ctBinaryOperator.getLeftHandOperand().accept(this);
        PatternNode lhs = resultStack.pop();

        ctBinaryOperator.getRightHandOperand().accept(this);
        PatternNode rhs = resultStack.pop();

        ElemNode result = new ElemNode(ctBinaryOperator);
        result.sub.put("kind", new ValueNode(ctBinaryOperator.getKind(), ctBinaryOperator.getKind()));
        result.sub.put("lhs", lhs);
        result.sub.put("rhs", rhs);

        resultStack.push(result);
    }

    @Override
    public <R> void visitCtBlock(CtBlock<R> ctBlock) {
        ElemNode result = new ElemNode(ctBlock);

        // TODO: should probably add a type ListNode and get rid of this hack
        result.sub.put("numstatements", new ValueNode(ctBlock.getStatements().size(), null));

        int n = 0;

        for (CtStatement stm : ctBlock.getStatements()) {
            stm.accept(this);
            result.sub.put("statement" + Integer.toString(n), resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public void visitCtBreak(CtBreak ctBreak) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <S> void visitCtCase(CtCase<S> ctCase) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtCatch(CtCatch ctCatch) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtTypeParameter(CtTypeParameter ctTypeParameter) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtConditional(CtConditional<T> ctConditional) {
        ElemNode result = new ElemNode(ctConditional);

        ctConditional.getCondition().accept(this);
        result.sub.put("cond", resultStack.pop());

        ctConditional.getThenExpression().accept(this);
        result.sub.put("then", resultStack.pop());

        ctConditional.getElseExpression().accept(this);
        result.sub.put("else", resultStack.pop());

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> ctConstructor) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtContinue(CtContinue ctContinue) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtDo(CtDo ctDo) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtExecutableReference(CtExecutableReference<T> ctExecutableReference) {
        ElemNode result = new ElemNode(ctExecutableReference);

        /*CtTypeReference<?> declType = ctExecutableReference.getDeclaringType();

        if (declType != null && !declType.getSimpleName().equals(SmPLJavaDSL.getUnspecifiedElementOrTypeName())) {
            result.sub.put("declaringtype", new ValueNode(ctExecutableReference.getDeclaringType().getSimpleName(), ctExecutableReference.getDeclaringType()));
        }*/

        String exename = ctExecutableReference.getSimpleName();

        if (params.contains(exename)) {
            result.sub.put("name", new ParamNode(exename));
        } else {
            result.sub.put("name", new ValueNode(exename, ctExecutableReference));
        }


        resultStack.push(result);
    }

    @Override
    public <T> void visitCtField(CtField<T> ctField) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtEnumValue(CtEnumValue<T> ctEnumValue) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtThisAccess(CtThisAccess<T> ctThisAccess) {
        resultStack.push(new ValueNode("CtThisAccess", ctThisAccess));
    }

    @Override
    public <T> void visitCtFieldReference(CtFieldReference<T> ctFieldReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> ctUnboundVariableReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtFor(CtFor ctFor) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtForEach(CtForEach ctForEach) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtIf(CtIf ctIf) {
        ElemNode result = new ElemNode(ctIf);

        ctIf.getCondition().accept(this);
        result.sub.put("cond", resultStack.pop());

        ctIf.getThenStatement().accept(this);
        result.sub.put("then", resultStack.pop());

        if (ctIf.getElseStatement() != null) {
            ctIf.getElseStatement().accept(this);
            result.sub.put("else", resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> ctInvocation) {
        ElemNode result = new ElemNode(ctInvocation);

        ctInvocation.getExecutable().accept(this);
        result.sub.put("executable", resultStack.pop());

        if (ctInvocation.getTarget() != null) {
            ctInvocation.getTarget().accept(this);
            result.sub.put("target", resultStack.pop());
        }

        int numargs = ctInvocation.getArguments().size();

        result.sub.put("numargs", new ValueNode(numargs, numargs));

        for (int i = 0; i < numargs; ++i) {
            // TODO: also include argument types?
            ctInvocation.getArguments().get(i).accept(this);
            result.sub.put("arg" + Integer.toString(i), resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtLiteral(CtLiteral<T> ctLiteral) {
        ElemNode result = new ElemNode(ctLiteral);
        result.sub.put("value", new ValueNode(ctLiteral.getValue(), ctLiteral));
        resultStack.push(result);
    }

    @Override
    public <T> void visitCtLocalVariable(CtLocalVariable<T> ctLocalVariable) {
        ElemNode result = new ElemNode(ctLocalVariable);

        String varname = ctLocalVariable.getReference().getSimpleName();
        String typename = ctLocalVariable.getType().getSimpleName();

        if (params.contains(varname)) {
            result.sub.put("variable", new ParamNode(varname));
        } else {
            result.sub.put("variable", new ValueNode(varname, ctLocalVariable.getReference()));
        }

        if (params.contains(typename)) {
            result.sub.put("type", new ParamNode(typename));
        } else {
            result.sub.put("type", new ValueNode(typename, ctLocalVariable.getType()));
        }

        if (ctLocalVariable.getDefaultExpression() != null) {
            ctLocalVariable.getDefaultExpression().accept(this);
            result.sub.put("expr", resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> ctLocalVariableReference) {
        String varname = ctLocalVariableReference.getSimpleName();

        if (params.contains(varname)) {
            resultStack.push(new ParamNode(varname));
        } else {
            ElemNode result = new ElemNode(ctLocalVariableReference);
            result.sub.put("variable", new ValueNode(varname, ctLocalVariableReference.getSimpleName()));
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtCatchVariable(CtCatchVariable<T> ctCatchVariable) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> ctCatchVariableReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> ctMethod) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> ctAnnotationMethod) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtNewArray(CtNewArray<T> ctNewArray) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
        ElemNode result = new ElemNode(ctConstructorCall);

        ctConstructorCall.getExecutable().accept(this);
        result.sub.put("executable", resultStack.pop());

        int numargs = ctConstructorCall.getArguments().size();

        result.sub.put("numargs", new ValueNode(numargs, numargs));

        for (int i = 0; i < numargs; ++i) {
            // TODO: also include argument types?
            ctConstructorCall.getArguments().get(i).accept(this);
            result.sub.put("arg" + Integer.toString(i), resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtNewClass(CtNewClass<T> ctNewClass) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtLambda(CtLambda<T> ctLambda) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> ctExecutableReferenceExpression) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> ctOperatorAssignment) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtPackage(CtPackage ctPackage) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtPackageReference(CtPackageReference ctPackageReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtParameter(CtParameter<T> ctParameter) {
        ElemNode result = new ElemNode(ctParameter);

        ctParameter.getType().accept(this);
        result.sub.put("type", resultStack.pop());

        ctParameter.getReference().accept(this);
        result.sub.put("reference", resultStack.pop());

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtParameterReference(CtParameterReference<T> ctParameterReference) {
        String paramname = ctParameterReference.getSimpleName();

        if (params.contains(paramname)) {
            resultStack.push(new ParamNode(paramname));
        } else {
            ElemNode result = new ElemNode(ctParameterReference);
            result.sub.put("paramname", new ValueNode(paramname, ctParameterReference));
            resultStack.push(result);
        }
    }

    @Override
    public <R> void visitCtReturn(CtReturn<R> ctReturn) {
        ElemNode result = new ElemNode(ctReturn);

        result.sub.put("is_void", new ValueNode(ctReturn.getReturnedExpression() == null, null));

        if (ctReturn.getReturnedExpression() != null) {
            ctReturn.getReturnedExpression().accept(this);
            result.sub.put("expr", resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public <R> void visitCtStatementList(CtStatementList ctStatementList) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <S> void visitCtSwitch(CtSwitch<S> ctSwitch) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> ctSwitchExpression) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtSynchronized(CtSynchronized ctSynchronized) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtThrow(CtThrow ctThrow) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtTry(CtTry ctTry) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtTryWithResource(CtTryWithResource ctTryWithResource) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtTypeParameterReference(CtTypeParameterReference ctTypeParameterReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtWildcardReference(CtWildcardReference ctWildcardReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> ctIntersectionTypeReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> ctTypeReference) {
        String typename = ctTypeReference.getSimpleName();

        if (params.contains(typename)) {
            resultStack.push(new ParamNode(typename));
        } else {
            ElemNode result = new ElemNode(ctTypeReference);
            result.sub.put("typename", new ValueNode(ctTypeReference.getSimpleName(), ctTypeReference));
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtTypeAccess(CtTypeAccess<T> ctTypeAccess) {
        String typename = ctTypeAccess.getAccessedType().getSimpleName();

        if (params.contains(typename)) {
            resultStack.push(new ParamNode(typename));
        } else {
            ElemNode result = new ElemNode(ctTypeAccess);
            result.sub.put("typename", new ValueNode(typename, ctTypeAccess));
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtUnaryOperator(CtUnaryOperator<T> ctUnaryOperator) {
        ElemNode result = new ElemNode(ctUnaryOperator);
        result.sub.put("kind", new ValueNode(ctUnaryOperator.getKind(), ctUnaryOperator.getKind()));

        ctUnaryOperator.getOperand().accept(this);
        result.sub.put("operand", resultStack.pop());

        resultStack.push(result);
    }

    @Override
    public <T> void visitCtVariableRead(CtVariableRead<T> ctVariableRead) {
        String varname = ctVariableRead.getVariable().getSimpleName();

        if (params.contains(varname)) {
            resultStack.push(new ParamNode(varname));
        } else {
            ElemNode result = new ElemNode(ctVariableRead);
            result.sub.put("variable", new ValueNode(varname, ctVariableRead.getVariable()));
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtVariableWrite(CtVariableWrite<T> ctVariableWrite) {
        String varname = ctVariableWrite.getVariable().getSimpleName();

        if (params.contains(varname)) {
            resultStack.push(new ParamNode(varname));
        } else {
            ElemNode result = new ElemNode(ctVariableWrite);
            result.sub.put("variable", new ValueNode(varname, ctVariableWrite.getVariable()));
            resultStack.push(result);
        }
    }

    @Override
    public void visitCtWhile(CtWhile ctWhile) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> ctAnnotationFieldAccess) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public <T> void visitCtFieldRead(CtFieldRead<T> ctFieldRead) {
        if (ctFieldRead.getTarget() == null || (ctFieldRead.getTarget() instanceof CtThisAccess<?>)) {
            CtVariableRead<T> ctVariableRead = ctFieldRead.getFactory().createVariableRead();
            CtVariableReference<T> ctVariableReference = ctFieldRead.getFactory().createLocalVariableReference();
            ctVariableReference.setSimpleName(ctFieldRead.getVariable().getSimpleName());
            ctVariableRead.setVariable(ctVariableReference);
            ctVariableRead.setType(ctFieldRead.getType());
            visitCtVariableRead(ctVariableRead);
        } else {
            String fieldName = ctFieldRead.getVariable().getSimpleName();
            ElemNode result = new ElemNode(ctFieldRead);

            if (params.contains(fieldName)) {
                result.sub.put("field", new ParamNode(fieldName));
            } else {
                result.sub.put("field", new ValueNode(fieldName, ctFieldRead.getVariable()));
            }

            ctFieldRead.getTarget().accept(this);
            result.sub.put("target", resultStack.pop());
            resultStack.push(result);
        }
    }

    @Override
    public <T> void visitCtFieldWrite(CtFieldWrite<T> ctFieldWrite) {
        CtVariableWrite<T> ctVariableWrite = ctFieldWrite.getFactory().createVariableWrite();
        CtVariableReference<T> ctVariableReference = ctFieldWrite.getFactory().createLocalVariableReference();
        ctVariableReference.setSimpleName(ctFieldWrite.getVariable().getSimpleName());
        ctVariableWrite.setVariable(ctVariableReference);
        visitCtVariableWrite(ctVariableWrite);
    }

    @Override
    public <T> void visitCtSuperAccess(CtSuperAccess<T> ctSuperAccess) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtComment(CtComment ctComment) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtJavaDoc(CtJavaDoc ctJavaDoc) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtJavaDocTag(CtJavaDocTag ctJavaDocTag) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtImport(CtImport ctImport) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtModule(CtModule ctModule) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtModuleReference(CtModuleReference ctModuleReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtPackageExport(CtPackageExport ctPackageExport) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtModuleRequirement(CtModuleRequirement ctModuleRequirement) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtProvidedService(CtProvidedService ctProvidedService) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtUsedService(CtUsedService ctUsedService) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtCompilationUnit(CtCompilationUnit ctCompilationUnit) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtPackageDeclaration(CtPackageDeclaration ctPackageDeclaration) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference ctTypeMemberWildcardImportReference) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void visitCtYieldStatement(CtYieldStatement ctYieldStatement) {
        throw new NotImplementedException("Not implemented");
    }

    private List<String> params;
    private Stack<PatternNode> resultStack;
}
