package spoon.leafactorci.rules;

import spoon.leafactorci.DrawAllocationCases.ObjectAllocation;
import spoon.leafactorci.engine.*;
import spoon.leafactorci.engine.logging.IterationLogger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class DrawAllocationRefactoringRule extends AbstractProcessor<CtClass> implements RefactoringRule<CtClass> {
    private IterationLogger logger;

    public DrawAllocationRefactoringRule(IterationLogger logger) {
        this.logger = logger;
    }

    private boolean methodSignatureMatches(CtMethod method) {
        // SIGNATURE:
        // public void onDraw(Canvas canvas)
        boolean nameMatch = method.getSimpleName().equals("onDraw");
        CtTypeReference type = method.getType();
        boolean returnTypeMatch = type != null && type.getSimpleName().equals("void");

        boolean hasSameNumberOfArguments = method.getParameters().size() == 1;
        if (hasSameNumberOfArguments) {
            List parameterList = method.getParameters();

            CtTypeReference firstArgumentType = ((CtParameter)parameterList.get(0)).getType();
            boolean firstArgumentTypeMatches = firstArgumentType.getSimpleName().endsWith("Canvas");

            return nameMatch &&
                    returnTypeMatch &&
                    firstArgumentTypeMatches;
        }

        return false;
    }

    @Override
    public void detectCase(DetectionPhaseContext context) {
        // Detect object allocations
        ObjectAllocation objectAllocation = ObjectAllocation.detect(context);
        if (objectAllocation != null) {
            context.caseOfInterestList.add(objectAllocation);
        }
    }

    @Override
    public void transformCase(TransformationPhaseContext context) {
        CaseTransformer.createPassThroughTransformation().transformCase(context);
    }

    @Override
    public void processCase(RefactoringPhaseContext context) {
        if(context.caseOfInterest instanceof ObjectAllocation) {
            ObjectAllocation objectAllocation = (ObjectAllocation) context.caseOfInterest;
            if(objectAllocation.getStatement() instanceof CtVariable) {
                // We are declaring a variable, pull the declaration out of the scope.
                CtClass ctClass = RefactoringRule.getClosestClassParent(context.block);
                if(ctClass == null) {
                    return;
                }
                // There is a viewHolder - Check if the field is inside, create it if necessary
                List<CtField<?>> fields = ctClass.getFields();
                Optional<CtField<?>> optionalField = fields.stream().filter(field -> field.getSimpleName()
                        .equals(objectAllocation.variable.getSimpleName())).findFirst();
                if(optionalField.isPresent() && !optionalField.get().getType().getSimpleName()
                        .equals(objectAllocation.variable.getType().getSimpleName())) {
                    // If types do not match we ignore for now.
                    return;
                }
                if(!optionalField.isPresent()) {
                    CtTypeReference typeReference = objectAllocation.variable.getType();
                    CtField field = ctClass.getFactory().createCtField(objectAllocation.variable.getSimpleName(), typeReference,
                            objectAllocation.constructorCall.toString(), ModifierKind.PRIVATE);
                    ctClass.addField(field);
                    if(ObjectAllocation.isClearable(typeReference)) {
                        objectAllocation.getStatement().insertBefore(
                                ctClass.getFactory().createCodeSnippetStatement(
                                        objectAllocation.variable.getSimpleName() + ".clear()"));
                    }
                    context.block.removeStatement(objectAllocation.getStatement());
                }
            }
        }
    }

    private void refactor(CtMethod method) {
        if (!methodSignatureMatches(method)) {
            return;
        }
        List<CtBlock> blocks = RefactoringRule.getCtElementsOfInterest(method, CtBlock.class::isInstance, CtBlock.class);
        for (CtBlock block : blocks) {
            Iteration.iterateBlock(this, logger, block,false, 0);
        }
    }

    public void process(CtClass element) {
        Set methods = element.getMethods();
        for (Object method : methods) {
            if (method instanceof CtMethod) {
                refactor((CtMethod) method);
            }
        }
    }

    @Override
    public void onSetup(DetectionPhaseContext context) {

    }

    @Override
    public void onWillIterate(DetectionPhaseContext context) {

    }

    @Override
    public void onDidIterate(DetectionPhaseContext context) {

    }

    @Override
    public void onWillTransform(TransformationPhaseContext context) {

    }

    @Override
    public void onWillTransformCase(TransformationPhaseContext context) {

    }

    @Override
    public void onDidTransformCase(TransformationPhaseContext context) {

    }

    @Override
    public void onWillRefactor(RefactoringPhaseContext context) {

    }

    @Override
    public void onWillRefactorCase(RefactoringPhaseContext context) {

    }

    @Override
    public void onDidRefactorCase(RefactoringPhaseContext context) {

    }
}