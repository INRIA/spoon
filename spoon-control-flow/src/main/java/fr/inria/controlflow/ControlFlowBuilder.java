/**
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

package fr.inria.controlflow;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static fr.inria.controlflow.BranchKind.*;

/**
 * Builds the control graph for a given snippet of code
 * <p/>
 * Created by marodrig on 13/10/2015.
 */
public class ControlFlowBuilder implements CtVisitor {

    ControlFlowGraph result = new ControlFlowGraph(ControlFlowEdge.class);

    ControlFlowNode exitNode = new ControlFlowNode(null, result, EXIT);

    ControlFlowNode beginNode = new ControlFlowNode(null, result, BEGIN);

    ControlFlowNode lastNode = beginNode;

    HashMap<String, CtStatement> labeledStatement = new HashMap<>();

    //This stack pushes all the nodes to wich a break statement may jump to.
    Stack<ControlFlowNode> breakingBad = new Stack<>();
    //This stack pushes all the nodes to wich a continue statement may jump to.
    Stack<ControlFlowNode> continueBad = new Stack<>();

    public ControlFlowGraph getResult() {
        return result;
    }

    /**
     * Build the control graph
     *
     * @param s
     * @return
     */
    public ControlFlowGraph build(CtElement s) {
        s.accept(this);
        tryAddEdge(lastNode, exitNode);
        return result;
    }

    private void visitConditional(CtElement parent, CtConditional conditional) {
        ControlFlowNode branch = new ControlFlowNode(parent, result, BRANCH);
        tryAddEdge(lastNode, branch);

        ControlFlowNode convergenceNode = new ControlFlowNode(null, result, CONVERGE);
        lastNode = branch;
        if (conditional.getThenExpression() instanceof CtConditional)
            visitConditional(conditional, (CtConditional) conditional.getThenExpression());
        else {
            lastNode = new ControlFlowNode(conditional.getThenExpression(), result, STATEMENT);
            tryAddEdge(branch, lastNode);
        }
        tryAddEdge(lastNode, convergenceNode);

        lastNode = branch;
        if (conditional.getElseExpression() instanceof CtConditional)
            visitConditional(conditional, (CtConditional) conditional.getElseExpression());
        else {
            lastNode = new ControlFlowNode(conditional.getElseExpression(), result, STATEMENT);
            tryAddEdge(branch, lastNode);
        }
        tryAddEdge(lastNode, convergenceNode);
        lastNode = convergenceNode;
    }

    /**
     * Returns the first graph node representing the statement s construction.
     * <p/>
     * Usually an statement is represented by many blocks and branches.
     * This method returns the first of those blocks/branches.
     *
     * @param g         Graph in which the bloc is to be found
     * @param statement Statement for which the first block is needed
     * @return
     */
    public static ControlFlowNode firstNode(ControlFlowGraph g, CtElement statement) throws NotFoundException {

        if (statement == null) throw new NotFoundException("statement null");

        if (statement instanceof CtFor) {
            CtFor ctFor = (CtFor) statement;
            if ( ctFor.getForInit().size() > 0 ) return g.findNode(ctFor.getForInit().get(0));
            else return g.findNode(ctFor.getExpression());
        }
        else if (statement instanceof CtForEach)
            return g.findNode(((CtForEach) statement).getVariable());
        else if (statement instanceof CtWhile)
            return g.findNode(((CtWhile) statement).getLoopingExpression());
        else if (statement instanceof CtDo) {
            ControlFlowNode n = g.findNode(((CtDo) statement).getLoopingExpression());
            ControlFlowNode n1 = null;
            for (ControlFlowEdge e : g.outgoingEdgesOf(n))
                if (e.isBackEdge()) {
                    n1 = e.getTargetNode();
                    break;
                }
            if (n == n1 || n1 == null)
                throw new NotFoundException("cannot find initial node of do while loop");
            return n1;
        } else if (statement instanceof CtIf)
            return g.findNode(((CtIf) statement).getCondition());
        else if (statement instanceof CtSwitch)
            return g.findNode(((CtSwitch) statement).getSelector());
        else if (statement instanceof CtBlock) return g.findNode(((CtBlock)statement).getStatement(0));
        else return g.findNode(statement);
    }


    private void defaultAction(BranchKind kind, CtStatement st) {
        ControlFlowNode n = new ControlFlowNode(st, result, kind);
        tryAddEdge(lastNode, n);
        lastNode = n;
    }

    /**
     * Register the label of the statement
     *
     * @param st
     */
    private void registerStatementLabel(CtStatement st) {
        if (st.getLabel() == null || st.getLabel().isEmpty()) return;
        if (!labeledStatement.containsKey(st.getLabel())) {
            labeledStatement.put(st.getLabel(), st);
        }
    }

    /**
     * Tries to add an edge. If source or target are not null and the vertex is unique
     *
     * @param source Source of the vertex
     * @param target Target of the vertex
     */
    private void tryAddEdge(ControlFlowNode source, ControlFlowNode target) {
        tryAddEdge(source, target, false, false);
    }

    /**
     * Tries to add an edge. If source or target are not null and the vertex is unique
     *
     * @param source     Source of the vertex
     * @param target     Target of the vertex
     * @param isLooping  indicate that the edge is a back edge
     * @param breakDance indicates that the edge is a jump out of the block
     */
    private void tryAddEdge(ControlFlowNode source, ControlFlowNode target, boolean isLooping, boolean breakDance) {

        boolean isBreak = source != null && source.getStatement() instanceof CtBreak;
        boolean isContinue = source != null && source.getStatement() instanceof CtContinue;

        if (source != null && target != null &&
                !result.containsEdge(source, target) &&
                (isLooping || breakDance || !(isBreak || isContinue))) {
            ControlFlowEdge e = result.addEdge(source, target);
            e.setBackEdge(isLooping);
        }

    }


    @Override
    public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {

    }

    @Override
    public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {

    }

    @Override
    public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {

    }

    @Override
    public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {

    }

    @Override
    public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {

    }

    @Override
    public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {

    }

    @Override
    public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {

    }

    @Override
    public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {

    }

    @Override
    public <T> void visitCtAssert(CtAssert<T> asserted) {
        defaultAction(STATEMENT, asserted);
    }

    @Override
    public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {

        registerStatementLabel(assignement);

        if (assignement.getAssignment() instanceof CtConditional) {
            visitConditional(assignement, (CtConditional) assignement.getAssignment());
        } else defaultAction(STATEMENT, assignement);
    }

    @Override
    public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {

    }

    private <R> void travelStatementList(List<CtStatement> statements) {
        ControlFlowNode begin = new ControlFlowNode(null, result, BLOCK_BEGIN);
        tryAddEdge(lastNode, begin);
        lastNode = begin;
        for (CtStatement s : statements) {
            registerStatementLabel(s);
            s.accept(this); // <- This should modify last node
            //tryAddEdge(before, lastNode); //Probably the link is already added
        }
        ControlFlowNode end = new ControlFlowNode(null, result, BLOCK_END);
        tryAddEdge(lastNode, end);
        lastNode = end;
    }

    @Override
    public <R> void visitCtBlock(CtBlock<R> block) {
        travelStatementList(block.getStatements());
    }

    @Override
    public void visitCtBreak(CtBreak breakStatement) {
        ControlFlowNode to;
        try {
            to = firstNode(lastNode.getParent(), labeledStatement.get(breakStatement.getTargetLabel()));
        } catch (NotFoundException e) {
            to = null;
        }
        if (to != null) {
            defaultAction(STATEMENT, breakStatement);
            tryAddEdge(lastNode, to, true, false);
        } else if (!breakingBad.empty()) {
            //Jump to the last guy who said I can jump to...
            defaultAction(STATEMENT, breakStatement);
            tryAddEdge(lastNode, breakingBad.peek(), false, true);
        }
    }

    @Override
    public void visitCtCatch(CtCatch catchBlock) {

    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        defaultAction(STATEMENT, ctClass);
    }

    @Override
    public <T> void visitCtConditional(CtConditional<T> conditional) {

    }

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> c) {

    }

    @Override
    public void visitCtContinue(CtContinue continueStatement) {
        ControlFlowNode to;
        try {
            to = firstNode(result, labeledStatement.get(continueStatement.getTargetLabel()));
        } catch (NotFoundException ex) {
            to = continueBad.peek();
        }
        if (to != null) {
            defaultAction(STATEMENT, continueStatement);
            tryAddEdge(lastNode, to, true, false);
        }
    }

    @Override
    public void visitCtDo(CtDo doLoop) {
        registerStatementLabel(doLoop);

        ControlFlowNode convergenceNode = new ControlFlowNode(null, result, CONVERGE);
        continueBad.push(convergenceNode);
        //to break out of the do loop
        ControlFlowNode convergenceNodeOut = new ControlFlowNode(null, result, CONVERGE);
        breakingBad.push(convergenceNodeOut);


        tryAddEdge(lastNode, convergenceNode);
        ControlFlowNode branch = new ControlFlowNode(doLoop.getLoopingExpression(), result, BRANCH);
        tryAddEdge(branch, convergenceNode, true, false);
        tryAddEdge(branch, convergenceNodeOut);

        lastNode = convergenceNode;
        doLoop.getBody().accept(this);
        tryAddEdge(lastNode, branch);

        lastNode = convergenceNodeOut;

        //Remove do out of the breaking and continuing stack
        breakingBad.pop();
        continueBad.pop();
    }

    @Override
    public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {

    }

    @Override
    public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {

    }

    @Override
    public <T> void visitCtField(CtField<T> f) {

    }

    @Override
    public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {

    }

    @Override
    public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {

    }

    @Override
    public <T> void visitCtFieldReference(CtFieldReference<T> reference) {

    }

    @Override
    public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {

    }

    @Override
    public void visitCtFor(CtFor forLoop) {
        registerStatementLabel(forLoop);

        //Add the initialization code
        if (forLoop.getForInit() != null) {
            if (forLoop.getForInit().size() > 1)
                travelStatementList(forLoop.getForInit());
            else if (forLoop.getForInit().size() > 0) forLoop.getForInit().get(0).accept(this);
        }

        ControlFlowNode convergence = new ControlFlowNode(forLoop.getExpression(), result, CONVERGE);
        breakingBad.push(convergence);

        //Next the branch
        ControlFlowNode branch = new ControlFlowNode(forLoop.getExpression(), result, BRANCH);
        tryAddEdge(lastNode, branch);

        //Node continue statements can continue to
        continueBad.push(branch);

        //Body
        lastNode = branch;
        if (forLoop.getBody() != null) forLoop.getBody().accept(this);

        //Append the update at the end
        if (forLoop.getForUpdate() != null) {
            if (forLoop.getForUpdate().size() > 1)
                travelStatementList(forLoop.getForUpdate());
            else if (forLoop.getForUpdate().size() > 0) forLoop.getForUpdate().get(0).accept(this);
        }

        //Link to the branch
        tryAddEdge(lastNode, branch, true, false);

        //Add a convergence node to quit the loop
        lastNode = convergence;
        tryAddEdge(branch, lastNode);

        continueBad.pop();
        breakingBad.pop();
    }


    @Override
    public void visitCtForEach(CtForEach foreach) {
        registerStatementLabel(foreach);

        ControlFlowNode convergence = new ControlFlowNode(null, result, CONVERGE);
        breakingBad.push(convergence);

        ControlFlowNode init = new ControlFlowNode(foreach.getVariable(), result, STATEMENT);
        tryAddEdge(lastNode, init);
        lastNode = init;

        ControlFlowNode branch = new ControlFlowNode(foreach.getExpression(), result, BRANCH);
        continueBad.push(branch);
        tryAddEdge(lastNode, branch);

        //Body
        lastNode = branch;
        foreach.getBody().accept(this);

        tryAddEdge(lastNode, branch, true, false);

        //Exit node
        lastNode = convergence;
        tryAddEdge(branch, lastNode);

        breakingBad.pop();
        continueBad.pop();
    }

    @Override
    public void visitCtIf(CtIf ifElement) {
        registerStatementLabel(ifElement);

        ControlFlowNode branch = new ControlFlowNode(ifElement.getCondition(), result, BRANCH);
        tryAddEdge(lastNode, branch);

        ControlFlowNode convergenceNode = new ControlFlowNode(null, result, CONVERGE);
        if (ifElement.getThenStatement() != null) {
            lastNode = branch;
            ifElement.getThenStatement().accept(this);
            tryAddEdge(lastNode, convergenceNode);
        }

        if (ifElement.getElseStatement() != null) {
            lastNode = branch;
            ifElement.getElseStatement().accept(this);
            tryAddEdge(lastNode, convergenceNode);
        } else {
            tryAddEdge(branch, convergenceNode);
        }
        lastNode = convergenceNode;
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> intrface) {

    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        registerStatementLabel(invocation);
        defaultAction(STATEMENT, invocation);
    }

    @Override
    public <T> void visitCtLiteral(CtLiteral<T> literal) {

    }

    @Override
    public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
        registerStatementLabel(localVariable);
        if (localVariable.getDefaultExpression() instanceof CtConditional) {
            visitConditional(localVariable, (CtConditional) localVariable.getDefaultExpression());
        } else defaultAction(STATEMENT, localVariable);
    }

    @Override
    public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {

    }

    @Override
    public <T> void visitCtCatchVariable(CtCatchVariable<T> ctCatchVariable) {

    }

    @Override
    public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> ctCatchVariableReference) {

    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> m) {
        m.getBody().accept(this);
    }

    @Override
    public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {

    }

    @Override
    public <T> void visitCtNewArray(CtNewArray<T> newArray) {

    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {

    }

    @Override
    public <T> void visitCtNewClass(CtNewClass<T> newClass) {

    }

    @Override
    public <T> void visitCtLambda(CtLambda<T> lambda) {

    }

    @Override
    public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> expression) {

    }

    @Override
    public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
        registerStatementLabel(assignment);
        defaultAction(STATEMENT, assignment);
    }

    @Override
    public void visitCtPackage(CtPackage ctPackage) {

    }

    @Override
    public void visitCtPackageReference(CtPackageReference reference) {

    }

    @Override
    public <T> void visitCtParameter(CtParameter<T> parameter) {

    }

    @Override
    public <T> void visitCtParameterReference(CtParameterReference<T> reference) {

    }

    @Override
    public <R> void visitCtReturn(CtReturn<R> returnStatement) {
        registerStatementLabel(returnStatement);
        ControlFlowNode n = new ControlFlowNode(returnStatement, result, STATEMENT);
        tryAddEdge(lastNode, n);
        tryAddEdge(n, exitNode);
        lastNode = null; //Special case in which this node does not connect with the next, because is a return
    }

    @Override
    public <R> void visitCtStatementList(CtStatementList statements) {

    }

    @Override
    public <S> void visitCtCase(CtCase<S> caseStatement) {
        registerStatementLabel(caseStatement);
        ControlFlowNode caseNode = new ControlFlowNode(caseStatement.getCaseExpression(), result, STATEMENT);
        tryAddEdge(lastNode, caseNode);
        lastNode = caseNode;
        travelStatementList(caseStatement.getStatements());
    }

    @Override
    public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
        registerStatementLabel(switchStatement);
        //Push the condition
        ControlFlowNode switchNode = new ControlFlowNode(switchStatement.getSelector(), result, BRANCH);
        tryAddEdge(lastNode, switchNode);

        //Create a convergence node for all the branches to converge after this
        ControlFlowNode convergenceNode = new ControlFlowNode(null, result, CONVERGE);
        //Push the convergence node so all non labeled breaks jumps there
        breakingBad.push(convergenceNode);

        lastNode = switchNode;
        for (CtCase caseStatement : switchStatement.getCases()) {
            //lastNode = switchNode;

            //Visit Case
            registerStatementLabel(caseStatement);
            ControlFlowNode cn = new ControlFlowNode(caseStatement.getCaseExpression(), result, STATEMENT);
            tryAddEdge(lastNode, cn);
            if (lastNode != switchNode) tryAddEdge(switchNode, cn);
            lastNode = cn;
            travelStatementList(caseStatement.getStatements());
            if (lastNode.getStatement() instanceof CtBreak) lastNode = switchNode;
        }

        //Return as last node the convergence node
        lastNode = convergenceNode;
        breakingBad.pop();
    }

    @Override
    public void visitCtSynchronized(CtSynchronized synchro) {

    }

    @Override
    public void visitCtThrow(CtThrow throwStatement) {
        //TODO:implement this
    }

    @Override
    public void visitCtTry(CtTry tryBlock) {
        //TODO:implement this
    }

    @Override
    public void visitCtTryWithResource(CtTryWithResource ctTryWithResource) {

    }

    @Override
    public void visitCtTypeParameter(CtTypeParameter typeParameter) {

    }

    @Override
    public void visitCtTypeParameterReference(CtTypeParameterReference ref) {

    }

    @Override
    public void visitCtWildcardReference(CtWildcardReference wildcardReference) {

    }

    @Override
    public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {

    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> reference) {

    }

    @Override
    public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {

    }

    @Override
    public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
        defaultAction(STATEMENT, operator);
    }

    @Override
    public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {

    }

    @Override
    public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {

    }

    @Override
    public void visitCtWhile(CtWhile whileLoop) {
        registerStatementLabel(whileLoop);

        ControlFlowNode convergenceNode = new ControlFlowNode(null, result, CONVERGE);
        breakingBad.push(convergenceNode);

        ControlFlowNode branch = new ControlFlowNode(whileLoop.getLoopingExpression(), result, BRANCH);
        continueBad.push(branch);

        tryAddEdge(lastNode, branch);
        tryAddEdge(branch, convergenceNode);
        lastNode = branch;
        whileLoop.getBody().accept(this);
        tryAddEdge(lastNode, branch, true, false);
        lastNode = convergenceNode;

        breakingBad.pop();
        continueBad.pop();
    }

    @Override
    public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {

    }

    @Override
    public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {

    }

    @Override
    public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {

    }

    @Override
    public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {

    }

    @Override
    public void visitCtComment(CtComment comment) {

    }

    @Override
    public void visitCtJavaDoc(CtJavaDoc comment) {

    }

    @Override
    public void visitCtJavaDocTag(CtJavaDocTag docTag) {

    }

    @Override
    public void visitCtImport(CtImport ctImport) {

    }

    @Override
    public void visitCtModule(CtModule module) {

    }

    @Override
    public void visitCtModuleReference(CtModuleReference moduleReference) {

    }

    @Override
    public void visitCtPackageExport(CtPackageExport moduleExport) {

    }

    @Override
    public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {

    }

    @Override
    public void visitCtProvidedService(CtProvidedService moduleProvidedService) {

    }

    @Override
    public void visitCtUsedService(CtUsedService usedService) {

    }

    @Override
    public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {

    }

    @Override
    public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {

    }

    @Override
    public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {

    }
}
