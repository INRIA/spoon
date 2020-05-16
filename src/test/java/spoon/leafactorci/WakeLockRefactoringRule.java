package spoon.leafactorci;

import spoon.leafactorci.Cases.VariableDeclared;
import spoon.leafactorci.WakeLockCases.WakeLockAcquired;
import spoon.leafactorci.engine.*;
import spoon.leafactorci.engine.logging.IterationLogger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class WakeLockRefactoringRule extends AbstractProcessor<CtClass> implements RefactoringRule<CtClass> {
    private IterationLogger logger;

    public WakeLockRefactoringRule(IterationLogger logger) {
        this.logger = logger;
    }

    private boolean methodSignatureMatches(CtMethod method) {
        // SIGNATURE:
        // protected void onCreate(Bundle savedInstanceState)
        boolean nameMatch = method.getSimpleName().equals("onCreate");
        CtTypeReference type = method.getType();
        boolean returnTypeMatch = type != null && type.getSimpleName().equals("void");

        boolean hasSameNumberOfArguments = method.getParameters().size() == 1;
        if (hasSameNumberOfArguments) {
            List parameterList = method.getParameters();

            CtTypeReference firstArgumentType = ((CtParameter)parameterList.get(0)).getType();
            boolean firstArgumentTypeMatches = firstArgumentType.getSimpleName().endsWith("Bundle");

            return nameMatch &&
                    returnTypeMatch &&
                    firstArgumentTypeMatches;
        }

        return false;
    }

    @Override
    public void detectCase(DetectionPhaseContext context) {
        WakeLockAcquired wakeLockAcquired = WakeLockAcquired.detect(context);
        if (wakeLockAcquired != null) {
            context.caseOfInterestList.add(wakeLockAcquired);
        }

        VariableDeclared variableDeclared = VariableDeclared.detect(context);
        if (variableDeclared != null) {
            context.caseOfInterestList.add(variableDeclared);
        }
    }

    @Override
    public void transformCase(TransformationPhaseContext context) {
        CaseTransformer.createPassThroughTransformation().transformCase(context);
    }

    @Override
    public void processCase(RefactoringPhaseContext context) {
        if(context.caseOfInterest instanceof WakeLockAcquired) {
            WakeLockAcquired wakeLockAcquired = (WakeLockAcquired) context.caseOfInterest;
            Optional<VariableDeclared> optionalVariableDeclared = context.casesOfInterest.stream()
                    .filter(VariableDeclared.class::isInstance)
                    .map(VariableDeclared.class::cast)
                    .filter(variableDeclared -> variableDeclared.variable.getSimpleName()
                            .equals(wakeLockAcquired.variable.getVariable().getSimpleName())).findFirst();
            if(optionalVariableDeclared.isPresent()) {

                CtClass ctClass = RefactoringRule.getClosestClassParent(context.block);
                if(ctClass == null) {
                    return;
                }
                // There is a viewHolder - Check if the field is inside, create it if necessary
                List<CtField<?>> fields = ctClass.getFields();
                Optional<CtField<?>> optionalField = fields.stream().filter(field -> field.getSimpleName()
                        .equals(optionalVariableDeclared.get().variable.getSimpleName())).findFirst();
                if(optionalField.isPresent() && !optionalField.get().getType().getSimpleName()
                        .equals(optionalVariableDeclared.get().variable.getType().getSimpleName())) {
                    // If types do not match we ignore for now.
                    return;
                }
                if(!optionalField.isPresent()) {
                    CtTypeReference typeReference = optionalVariableDeclared.get().variable.getType();
                    CtField field = ctClass.getFactory().createCtField(optionalVariableDeclared.get().variable.getSimpleName(), typeReference,
                            "null", ModifierKind.PRIVATE);
                    ctClass.addField(field);

                    Factory factory = optionalVariableDeclared.get().getStatement().getFactory();
                    CtAssignment assignment = factory.createAssignment();
                    assignment.setAssigned(factory.createCodeSnippetExpression(optionalVariableDeclared.get().variable.getSimpleName()));
                    assignment.setAssignment(optionalVariableDeclared.get().variable.getDefaultExpression());
                    optionalVariableDeclared.get().getStatement().insertBefore(assignment);
                    context.block.removeStatement(optionalVariableDeclared.get().getStatement());
                    context.block.removeStatement(wakeLockAcquired.getStatement());
                }

            }

            // From here on we assume the variable is in the class as a field

            CtClass ctClass = RefactoringRule.getClosestClassParent(context.block);
            if(ctClass == null) {
                return;
            }

            Set<CtMethod<?>> methods = ctClass.getMethods();
            boolean hasOnPause = false;
            boolean hasOnResume = false;
            boolean hasOnDestroy = false;
            String variableName = wakeLockAcquired.variable.getVariable().getSimpleName();
            for (CtMethod<?> ctMethod : methods) {

                if(ctMethod.getParameters().size() != 0 || !ctMethod.getType().getSimpleName().equals("void")) {
                    continue;
                }

                switch(ctMethod.getSimpleName()) {
                    case "onPause":
                        hasOnPause = true;
                        boolean hasRelease = false;
                        for(CtStatement statement : ctMethod.getBody().getStatements()) {
                            if(statement instanceof CtInvocation) {
                                CtInvocation invocation = (CtInvocation) statement;
                                CtExpression target = invocation.getTarget();
                                if (target.toString().equals(variableName) &&
                                        invocation.getExecutable().getSimpleName().equals("release")) {
                                    hasRelease = true;
                                    break;
                                }
                            }
                        }
                        if(!hasRelease) {
                            ctMethod.getBody().addStatement(ctMethod.getFactory()
                                    .createCodeSnippetStatement(variableName + ".release()"));
                        }

                        break;
                    case "onResume":
                        hasOnResume = true;

                        boolean hasAcquire = false;
                        for(CtStatement statement : ctMethod.getBody().getStatements()) {
                            if(statement instanceof CtInvocation) {
                                CtInvocation invocation = (CtInvocation) statement;
                                CtExpression target = invocation.getTarget();
                                if (target.toString().equals(variableName) &&
                                        invocation.getExecutable().getSimpleName().equals("acquire")) {
                                    hasAcquire = true;
                                    break;
                                }
                            }
                        }
                        if(!hasAcquire) {
                            ctMethod.getBody().addStatement(ctMethod.getFactory()
                                    .createCodeSnippetStatement(variableName + ".acquire()"));
                        }

                        break;

                    case "onDestroy":
                        hasOnDestroy = true;

                        boolean hasDestroy = false;
                        for(CtStatement statement : ctMethod.getBody().getStatements()) {
                            if(statement instanceof CtInvocation) {
                                CtInvocation invocation = (CtInvocation) statement;
                                CtExpression target = invocation.getTarget();
                                if (target.toString().equals(variableName) &&
                                        invocation.getExecutable().getSimpleName().equals("release")) {
                                    hasDestroy = true;
                                    break;
                                }
                            }
                        }
                        if(!hasDestroy) {
                            ctMethod.getBody().addStatement(ctMethod.getFactory()
                                    .createCodeSnippetStatement(variableName + ".release()"));
                        }

                        break;
                }
            }

            Factory factory = ctClass.getFactory();

            if(!hasOnPause) {
                CtMethod method = factory.createMethod();
                method.setType(factory.Type().voidPrimitiveType());
                method.setSimpleName("onPause");
                CtAnnotation<Annotation> annotation = factory.Code()
                        .createAnnotation(getFactory().Code().createCtTypeReference(Override.class));
                method.addAnnotation(annotation);
                method.setBody(factory.createBlock());
                method.getBody().addStatement(factory.createCodeSnippetStatement("super.onPause()"));
                method.getBody().addStatement(factory.createCodeSnippetStatement(variableName + ".release()"));
                ctClass.addMethod(method);
            }

            if(!hasOnResume) {
                CtMethod method = factory.createMethod();
                method.setType(factory.Type().voidPrimitiveType());
                method.setSimpleName("onResume");
                CtAnnotation<Annotation> annotation = factory.Code()
                        .createAnnotation(getFactory().Code().createCtTypeReference(Override.class));
                method.addAnnotation(annotation);
                method.setBody(factory.createBlock());
                method.getBody().addStatement(factory.createCodeSnippetStatement("super.onResume()"));
                method.getBody().addStatement(factory.createCodeSnippetStatement(variableName + ".acquire()"));
                ctClass.addMethod(method);
            }

            if(!hasOnDestroy) {
                CtMethod method = factory.createMethod();
                method.setType(factory.Type().voidPrimitiveType());
                CtAnnotation<Annotation> annotation = factory.Code()
                        .createAnnotation(getFactory().Code().createCtTypeReference(Override.class));
                method.addAnnotation(annotation);
                method.setSimpleName("onDestroy");
                method.setBody(factory.createBlock());
                method.getBody().addStatement(factory.createCodeSnippetStatement("super.onDestroy()"));
                method.getBody().addStatement(factory.createCodeSnippetStatement(variableName + ".release()"));
                ctClass.addMethod(method);
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