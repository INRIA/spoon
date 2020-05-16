package spoon.leafactorci;

import spoon.leafactorci.Cases.VariableDeclared;
import spoon.leafactorci.ViewHolderCases.*;
import spoon.leafactorci.engine.*;
import spoon.leafactorci.engine.logging.IterationLogger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ViewHolderRefactoringRule  extends AbstractProcessor<CtClass> implements RefactoringRule<CtClass> {

    private IterationLogger logger;

    public ViewHolderRefactoringRule(IterationLogger logger) {
        this.logger = logger;
    }

    private class RefactoringPhaseExtra {
        String viewVariableName = null;
        String argumentName = null;
        Factory factory = null;
        boolean convertViewInflated = false; // If true the convertView is properly null checked and assigned the layout
        boolean hasViewHolderInstance = false; // If true at this point there is a viewHolderInstance
        boolean isPopulatingViewHolder = false; // If true there is a condition for populating the viewHolder
        boolean hasIfPreamble = false;
        boolean hasIfStmt = false;
        CtIf ifStmt = null;
        CtBlock thenBlock = null;
        String viewHolderInstanceName = "viewHolderItem";
        CtIf createIfStatement(RefactoringPhaseContext context) {
            CtIf ifStmt = factory.createIf();

            ifStmt.setCondition(factory.createCodeSnippetExpression(String.format("%s == null", this.viewHolderInstanceName)));
            CtStatement st1 = factory.createCodeSnippetStatement(String.format("viewHolderItem = new ViewHolderItem()", this.viewHolderInstanceName));
            CtStatement st2 = factory.createCodeSnippetStatement(String.format("%s.setTag(viewHolderItem)", argumentName));

            CtBlock thenBlock = factory.createBlock();
            thenBlock.addStatement(st1);
            thenBlock.addStatement(st2);
            ifStmt.setThenStatement(thenBlock);

            this.isPopulatingViewHolder = true;
            this.ifStmt = ifStmt;
            this.thenBlock = thenBlock;
            this.hasIfStmt = true;

            return ifStmt;
        }
    }

    @Override
    public void detectCase(DetectionPhaseContext context) {
        VariableDeclared variableDeclared = VariableDeclared.detect(context);
        if (variableDeclared != null) {
            context.caseOfInterestList.add(variableDeclared);
        }
        ConvertViewReassignInflator convertViewReassignInflator = ConvertViewReassignInflator.detect(context);
        if(convertViewReassignInflator != null) {
            context.caseOfInterestList.add(convertViewReassignInflator);
        }
        ConvertViewReuseWithTernary convertViewReuseWithTernary = ConvertViewReuseWithTernary.detect(context);
        if(convertViewReuseWithTernary != null) {
            context.caseOfInterestList.add(convertViewReuseWithTernary);
        }
        VariableAssignedGetTag variableAssignedGetTag = VariableAssignedGetTag.detect(context);
        if(variableAssignedGetTag != null) {
            context.caseOfInterestList.add(variableAssignedGetTag);
        }
        VariableAssignedFindViewById variableAssignedFindViewById = VariableAssignedFindViewById.detect(context);
        if(variableAssignedFindViewById != null) {
            context.caseOfInterestList.add(variableAssignedFindViewById);
        }
        VariableAssignedInflator variableAssignedInflator = VariableAssignedInflator.detect(context);
        if(variableAssignedInflator != null) {
            context.caseOfInterestList.add(variableAssignedInflator);
        }
        VariableCheckNull variableCheckNull = VariableCheckNull.detect(context);
        if(variableCheckNull != null) {
            context.caseOfInterestList.add(variableCheckNull);
        }
    }

    @Override
    public void transformCase(TransformationPhaseContext context) {
        // No need for transformations in this case
        CaseTransformer.createPassThroughTransformation().transformCase(context);
    }

    @Override
    public void processCase(RefactoringPhaseContext context) {
        // Variables for easier access
        RefactoringPhaseExtra extra = (RefactoringPhaseExtra) context.extra;

        if(context.caseOfInterest instanceof VariableAssignedInflator) {
            // In this case we want to check where the inflated view is stored
            VariableAssignedInflator variableAssignedInflator = (VariableAssignedInflator) context.caseOfInterest;
            if(variableAssignedInflator.variable.getSimpleName().equals(extra.argumentName)) {
                return;
            }
            CtConditional conditional = extra.factory.createConditional();
            conditional.setCondition(extra.factory.createCodeSnippetExpression(extra.argumentName + " == null"));
            conditional.setElseExpression(extra.factory.createCodeSnippetExpression(extra.argumentName));
            if(variableAssignedInflator.getStatement() instanceof CtVariable) {
                CtVariable variable = ((CtVariable)variableAssignedInflator.getStatement());
                conditional.setThenExpression(variable.getDefaultExpression());
                variable.setDefaultExpression(conditional);
            } else if (variableAssignedInflator.getStatement() instanceof CtAssignment) {
                CtAssignment assignment = (CtAssignment) variableAssignedInflator.getStatement();
                conditional.setThenExpression(assignment.getAssignment());
                assignment.setAssignment(conditional);
            }
            extra.viewVariableName = variableAssignedInflator.variable.getSimpleName();
            extra.convertViewInflated = true;
        } else if(context.caseOfInterest instanceof ConvertViewReassignInflator) {
            ConvertViewReassignInflator convertViewReassignInflator = (ConvertViewReassignInflator) context.caseOfInterest;
            CtExpression assignment = convertViewReassignInflator.assignment.getAssignment();
            CtConditional conditional = extra.factory.createConditional();
            conditional.setCondition(extra.factory.createCodeSnippetExpression(extra.argumentName + " == null"));
            conditional.setThenExpression(assignment);
            conditional.setElseExpression(extra.factory.createCodeSnippetExpression(extra.argumentName));
            convertViewReassignInflator.assignment.setAssignment(conditional);
            extra.viewVariableName = extra.argumentName;
            extra.convertViewInflated = true;
        } else if(context.caseOfInterest instanceof ConvertViewReuseWithTernary) {
            // In this case everything is well no need to worry about the convertView anymore.
            extra.viewVariableName = extra.argumentName;
            extra.convertViewInflated = true;
        } else if(context.caseOfInterest instanceof VariableAssignedGetTag) {
            // In this case we want to check the name of the viewHolderInstance
            VariableAssignedGetTag variableAssignedGetTag = (VariableAssignedGetTag) context.caseOfInterest;
            extra.viewHolderInstanceName = variableAssignedGetTag.variable.getSimpleName();
            extra.hasViewHolderInstance = true;
        } else if(context.caseOfInterest instanceof VariableCheckNull) {
            VariableCheckNull variableCheckNull = (VariableCheckNull) context.caseOfInterest;
            if(variableCheckNull.variable.getSimpleName().equals(extra.viewHolderInstanceName)) {
                extra.hasIfStmt = true;
                extra.ifStmt = variableCheckNull.ifStmt;
                extra.thenBlock = variableCheckNull.ifStmt.getThenStatement();
            }
        } else if(context.caseOfInterest instanceof VariableAssignedFindViewById && extra.convertViewInflated) { // The convert view must be inflated otherwise we do not want any changes
            VariableAssignedFindViewById variableAssignedFindViewById = (VariableAssignedFindViewById) context.caseOfInterest;
            // Find the Class that contains this case
            CtClass rootClass = RefactoringRule.getClosestClassParent(variableAssignedFindViewById.getStatement());
            // Find every inner class inside the root class that matches the viewHolder description
            List<CtClass> viewHolderItemClasses = RefactoringRule.getCtElementsOfInterest(rootClass, node -> {
                // TODO - We have a situation where class could be declared inside another inner class, we should search only narrowly
                if (node instanceof CtClass) {
                    CtClass ctClass = (CtClass) node;
//                    boolean isStatic = classOrInterfaceDeclaration.isStatic();
                    return ctClass.getSimpleName().equals("ViewHolderItem");
                }
                return false;
            }, CtClass.class);
            CtClass ctClass;
            if(viewHolderItemClasses.size() == 0) {
                // There isn't a viewHolder - Create it with the field inside it
                ctClass = extra.factory.createClass("ViewHolderItem");
                ctClass.addModifier(ModifierKind.STATIC);
                CtTypeReference typeReference = variableAssignedFindViewById.variable.getType();
                CtField field = extra.factory.createCtField(variableAssignedFindViewById.variable.getSimpleName(), typeReference,
                        "null", ModifierKind.PUBLIC);
                ctClass.addTypeMember(field);
                CtMethod method = RefactoringRule.getClosestMethodParent(variableAssignedFindViewById.getStatement());
                int methodIndex = rootClass.getTypeMembers().indexOf(method);
                rootClass.addTypeMemberAt(methodIndex, ctClass);
            } else {
                // There is a viewHolder - Check if the field is inside, create it if necessary
                ctClass = viewHolderItemClasses.get(0);
                List<CtField<?>> fields = ctClass.getFields();
                Optional<CtField<?>> optionalField = fields.stream().filter(field -> field.getSimpleName()
                        .equals(variableAssignedFindViewById.variable.getSimpleName())).findFirst();
                if(optionalField.isPresent() && !optionalField.get().getType().getSimpleName()
                        .equals(variableAssignedFindViewById.variable.getType().getSimpleName())) {
                    // If types do not match we ignore for now.
                    return;
                }
                if(!optionalField.isPresent()) {
                    CtTypeReference typeReference = variableAssignedFindViewById.variable.getType();
                    CtField field = extra.factory.createCtField(variableAssignedFindViewById.variable.getSimpleName(), typeReference,
                            "null", ModifierKind.PUBLIC);
                    ctClass.addField(field); // TODO - Printer leaves a blank line after the field.
                }
            }

            // MILESTONE: From this point we know that we have a ViewHolder class and a field variable matching the assigned variable

            // New variables for easier access
            Set<CtTypeReference<?>> ctTypeReferences = variableAssignedFindViewById.resource.getReferencedTypes();

            if(!extra.hasViewHolderInstance) {
                // Then we need to create it.
                // Create a statement to get the viewHolder instance if it exists.
                CtStatement newStatement1 = extra.factory.createCodeSnippetStatement(String.format(
                        "ViewHolderItem %s = (ViewHolderItem) %s.getTag()",
                        extra.viewHolderInstanceName, extra.viewVariableName));
                if(extra.hasIfStmt) {
                    extra.ifStmt.insertBefore(newStatement1);
                } else {
                    context.caseOfInterest.getStatement().insertBefore(newStatement1);
                }
                extra.hasViewHolderInstance = true;
            }

            if(!extra.isPopulatingViewHolder) {
                // We have reached this point which means that the there is no if statement for populating the ViewHolder
                CtIf ctIf = extra.createIfStatement(context);
                context.caseOfInterest.getStatement().insertBefore(ctIf);
            }

            // TODO - we need to check if refactoringPhaseExtra.thenBlock already populates the viewHolder, we don't want duplicates
            if(true) {
                System.out.println("variableAssignedFindViewById.resource - " + variableAssignedFindViewById.resource);
                extra.thenBlock.addStatement(extra.factory.createCodeSnippetStatement(
                        String.format("%s.%s = (TextView) %s.findViewById(%s)",
                                extra.viewHolderInstanceName,
                                variableAssignedFindViewById.variable.getSimpleName(),
                                extra.viewVariableName, variableAssignedFindViewById.resource.toString())));
            }

            // Replace the assignment of the variable with the ViewHolder field
            CtStatement statement = variableAssignedFindViewById.getStatement();
            CtExpression assignmentExpression = extra.factory
                    .createCodeSnippetExpression(String.format("%s.%s",
                            extra.viewHolderInstanceName,
                            variableAssignedFindViewById.variable.getSimpleName()));
            if(statement instanceof CtVariable) {
                CtVariable ctVariable = ((CtVariable) statement);
                ctVariable.setDefaultExpression(assignmentExpression);
            } else if (statement instanceof CtAssignment) {
                CtAssignment assignment = ((CtAssignment) statement);
                assignment.setAssignment(assignmentExpression);
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
        // Create a Refactoring phase extra for data support
        RefactoringPhaseExtra extra = new RefactoringPhaseExtra();
        context.extra = extra;
        if(context.casesOfInterest.size() > 0) {
            String argumentName = ((CtParameter) Objects.requireNonNull(
                    RefactoringRule.getClosestMethodParent(context.casesOfInterest.get(0).getStatement()))
                    .getParameters().get(1)).getSimpleName();
            Factory factory = Objects.requireNonNull(context.block).getFactory();
            extra.argumentName = argumentName;
            extra.viewVariableName = argumentName;
            extra.factory = factory;
        }
    }

    @Override
    public void onWillRefactorCase(RefactoringPhaseContext context) {

    }

    @Override
    public void onDidRefactorCase(RefactoringPhaseContext context) {

    }

    @Override
    public void process(CtClass element) {
        Set methods = element.getMethods();
        for (Object method : methods) {
            if (method instanceof CtMethod) {
                refactor((CtMethod) method);
            }
        }
    }

    private void refactor(CtMethod method) {
        if (!methodSignatureMatches(method)) {
            return;
        }
        Iteration.iterateMethod(this, logger, method,false);
    }

    private boolean methodSignatureMatches(CtMethod method) {
        // SIGNATURE:
        // public View getView(final int position, final View convertView, final ViewGroup parent)
        boolean nameMatch = method.getSimpleName().equals("getView");
        CtTypeReference type = method.getType();
        boolean returnTypeMatch = type.getSimpleName().equals("View");

        boolean isPublic = method.getModifiers().contains(ModifierKind.PUBLIC);
        boolean hasSameNumberOfArguments = method.getParameters().size() == 3;
        if (hasSameNumberOfArguments) {
            List parameterList = method.getParameters();

            CtTypeReference firstArgumentType = ((CtParameter)parameterList.get(0)).getType();
            boolean firstArgumentTypeMatches = firstArgumentType.isPrimitive() &&
                    firstArgumentType.getSimpleName().equals("int");

            CtTypeReference secondArgumentType = ((CtParameter)parameterList.get(1)).getType();
            boolean secondArgumentTypeMatches = secondArgumentType.getSimpleName().equals("View");

            CtTypeReference thirdArgumentType = ((CtParameter)parameterList.get(2)).getType();
            boolean thirdArgumentTypeMatches = thirdArgumentType.getSimpleName().equals("ViewGroup");

            return isPublic &&
                    nameMatch &&
                    returnTypeMatch &&
                    firstArgumentTypeMatches &&
                    secondArgumentTypeMatches &&
                    thirdArgumentTypeMatches;
        }

        return false;
    }
}
