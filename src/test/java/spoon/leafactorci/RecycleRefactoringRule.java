package spoon.leafactorci;

import spoon.leafactorci.Cases.VariableDeclared;
import spoon.leafactorci.Cases.VariableReassigned;
import spoon.leafactorci.Cases.VariableUsed;
import spoon.leafactorci.RecycleCases.VariableLost;
import spoon.leafactorci.RecycleCases.VariableRecycled;
import spoon.leafactorci.engine.*;
import spoon.leafactorci.engine.logging.IterationLogger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

import java.util.*;
import java.util.stream.Collectors;

public class RecycleRefactoringRule extends AbstractProcessor<CtClass> implements RefactoringRule<CtClass> {
    // List of classes that need to be recycled
    private Map<String, String> opportunities = new LinkedHashMap<>();
    private IterationLogger logger;

    public RecycleRefactoringRule(IterationLogger logger) {
        this.logger = logger;
        // todo - Should detect with the full namespace.
        opportunities.put("TypedArray", "recycle");
        opportunities.put("Bitmap", "recycle");
        opportunities.put("Cursor", "close");
        opportunities.put("VelocityTracker", "recycle");
        opportunities.put("Message", "recycle");
        opportunities.put("MotionEvent", "recycle");
        opportunities.put("Parcel", "recycle");
        opportunities.put("ContentProviderClient", "release");
    }

    @Override
    public void detectCase(DetectionPhaseContext context) {
        // Detect variables declared
        VariableDeclared variableDeclared = VariableDeclared.detect(context);
        if (variableDeclared != null) {
            String typeName = variableDeclared.variable.getType().getSimpleName();
            if (opportunities.containsKey(typeName)) {
                context.caseOfInterestList.add(variableDeclared);
            }
        }
        // Detect variables reassigned
        VariableReassigned variableReassigned = VariableReassigned.detect(context);
        if (variableReassigned != null) {
            CtExpression lhs = variableReassigned.assignment.getAssigned();
            if (lhs instanceof CtVariableWrite) {
                context.caseOfInterestList.add(variableReassigned);
            }
        }
        // Detect variables usage
        VariableUsed variableUsed = VariableUsed.detect(context);
        if (variableUsed != null) {
            context.caseOfInterestList.add(variableUsed);
        }
        // Detect lost variables
        VariableLost variableLost = VariableLost.detect(context);
        if (variableLost != null) {
            context.caseOfInterestList.add(variableLost);
        }

        // Detect recycled variables
        VariableRecycled variableRecycled = VariableRecycled.detect(context, opportunities);
        if (variableRecycled != null) {
            context.caseOfInterestList.add(variableRecycled);
        }
    }

    @Override
    public void transformCase(TransformationPhaseContext context) {
        List<VariableDeclared> variables = context.caseOfInterestList.stream()
            .filter(VariableDeclared.class::isInstance)
            .map(VariableDeclared.class::cast).collect(Collectors.toList());
        if(context.caseOfInterest instanceof VariableUsed) {
            // Filtering considering the variables declared
            VariableUsed variableUsed = (VariableUsed) context.caseOfInterest;
            boolean interesting = variables.stream().anyMatch(variableDeclared -> variableUsed.variableAccesses.stream()
                    .anyMatch(ctVariableAccess -> ctVariableAccess.getVariable().getSimpleName()
                            .equals(variableDeclared.variable.getSimpleName())));
            if (interesting) {
                context.accept(context.caseOfInterest);
            }
        } else if(context.caseOfInterest instanceof VariableReassigned) {
            // Filtering considering the variables declared
            VariableReassigned variableReassigned = (VariableReassigned) context.caseOfInterest;
            boolean interesting = variables.stream().anyMatch(variableDeclared -> {
                CtExpression assigned = variableReassigned.assignment.getAssigned();
                return (assigned instanceof CtVariableWrite &&
                        ((CtVariableWrite)assigned).getVariable().getSimpleName()
                                .equals(variableDeclared.variable.getSimpleName()));
            });
            if (interesting) {
                context.accept(context.caseOfInterest);
            }
        } else {
            CaseTransformer.createPassThroughTransformation().transformCase(context);
        }
    }

    private List<CaseOfInterest> getCasesByVariableName(String variableName, List<CaseOfInterest> caseOfInterests) {
        return caseOfInterests.stream().filter(caseOfInterest -> {
            if(caseOfInterest instanceof VariableDeclared) {
                return ((VariableDeclared) caseOfInterest).variable.getSimpleName().equals(variableName);
            } else if(caseOfInterest instanceof VariableReassigned) {
                CtExpression assigned = ((VariableReassigned) caseOfInterest).assignment.getAssigned();
                if(assigned instanceof CtVariableWrite) {
                    return ((CtVariableWrite) assigned).getVariable().getSimpleName().equals(variableName);
                }
            } else if(caseOfInterest instanceof VariableUsed) {
                return ((VariableUsed) caseOfInterest).variableAccesses.stream()
                        .anyMatch(ctVariableAccess -> ctVariableAccess.getVariable().getSimpleName()
                                .equals(variableName));
            } else if(caseOfInterest instanceof VariableLost) {
                return ((VariableLost) caseOfInterest).variableAccesses.stream()
                        .anyMatch(ctVariableAccess -> ctVariableAccess.getVariable().getSimpleName()
                                .equals(variableName));
            } else if(caseOfInterest instanceof VariableRecycled) {
                return ((VariableRecycled) caseOfInterest).variableAccesses.stream()
                        .anyMatch(ctVariableAccess -> ctVariableAccess.getVariable().getSimpleName()
                                .equals(variableName));
            }
            return false;
        }).collect(Collectors.toList());
    }

    private String getTypeByVariableName(String variableName, List<CaseOfInterest> caseOfInterests) {
        Optional<VariableDeclared> match = caseOfInterests.stream().filter(VariableDeclared.class::isInstance)
                .map(VariableDeclared.class::cast)
                .filter(variableDeclared -> variableDeclared.variable.getSimpleName().equals(variableName))
                .findFirst();
        if(!match.isPresent()) {
            return null;
        }
        return match.get().variable.getType().getSimpleName();
    }

    private boolean isVariableUnderControl(String variableName, RefactoringPhaseContext context) {
        List<CaseOfInterest> filtered = getCasesByVariableName(variableName, context.casesOfInterest);
        // NOTE: Only check up to this point in the phase
        filtered = filtered.stream()
                .filter(VariableLost.class::isInstance)
                .map(VariableLost.class::cast)
                .filter(variableLost -> variableLost.getStatementIndex() < context.caseOfInterest.getStatementIndex())
                .collect(Collectors.toList());
        return filtered.size() == 0;
    }

    private boolean wasVariableRecycled(String variableName, RefactoringPhaseContext context) {
        List<CaseOfInterest> filtered = getCasesByVariableName(variableName, context.casesOfInterest);
        int index = filtered.indexOf(context.caseOfInterest);
        if(context.caseOfInterest instanceof VariableReassigned) {
            // We do not want to consider this case of interest
            index --;
        }
        filtered = filtered.subList(0, index);
        for(int i = filtered.size() - 1; i >= 0; i --) {
            CaseOfInterest current = filtered.get(i);
            // NOTE: Only check up to this point in the phase and after the last declaration or redeclaration of this variable
            if(current instanceof VariableReassigned || current instanceof VariableDeclared) {
                break;
            } else if(current instanceof VariableRecycled) {
                return true;
            }
        }
        return false;
    }

    private void recycleVariableDeclared(RefactoringPhaseContext context) {
        if(!(context.caseOfInterest instanceof VariableDeclared)) {
            return;
        }
        VariableDeclared variableDeclared = (VariableDeclared) context.caseOfInterest;
        String variableName = variableDeclared.variable.getSimpleName();
        String typeName = opportunities.get(getTypeByVariableName(variableName, context.casesOfInterest));
        if(typeName == null) {
            return;
        }
        List<CaseOfInterest> casesOfInterest = getCasesByVariableName(variableName, context.casesOfInterest); // TODO - EXCLUDE RETURN STATEMENTS
        boolean isLast = casesOfInterest.get(casesOfInterest.size() - 1).equals(context.caseOfInterest);
        if(!isLast) {
            return;
        }
        Factory factory = context.caseOfInterest.getStatement().getFactory();
        CtIf ctIf = factory.createIf();
        CtBlock ctBlock = factory.createBlock();
        ctBlock.addStatement(factory
                .createCodeSnippetStatement(String.format("%s.%s()", variableName, typeName)));
        ctIf.setThenStatement(ctBlock);
        ctIf.setCondition(factory
                .createCodeSnippetExpression(String.format("%s != null", variableName)));
        context.caseOfInterest.getStatement().insertAfter(ctIf);
    }

    private void recycleVariableReassigned(RefactoringPhaseContext context) {
        if(!(context.caseOfInterest instanceof VariableReassigned)) {
            return;
        }
        // We consider reassigns because there could be no usage in between Declarations and Reassignments
        CtExpression assigned = ((VariableReassigned) context.caseOfInterest).assignment.getAssigned();
        if(assigned instanceof CtVariableWrite) {
            String variableName = ((CtVariableWrite) assigned).getVariable().getSimpleName();
            String typeName = opportunities.get(getTypeByVariableName(variableName, context.casesOfInterest));
            if(typeName == null) {
                return;
            }

            boolean wasVariableRecycled = wasVariableRecycled(variableName, context);
            if(wasVariableRecycled) {
                return;
            }

            boolean isInControl = isVariableUnderControl(variableName, context);
            if(!isInControl) {
                return;
            }

            Factory factory = assigned.getFactory();
            CtIf ctIf2 = factory.createIf();
            CtBlock ctBlock2 = factory.createBlock();
            ctBlock2.addStatement(factory
                    .createCodeSnippetStatement(String.format("%s.%s()", variableName, typeName)));
            ctIf2.setThenStatement(ctBlock2);
            ctIf2.setCondition(factory
                    .createCodeSnippetExpression(String.format("%s != null", variableName)));
            context.caseOfInterest.getStatement().insertBefore(ctIf2);


            List<CaseOfInterest> casesOfInterest = getCasesByVariableName(variableName, context.casesOfInterest);
            boolean isLast = casesOfInterest.get(casesOfInterest.size() - 1).equals(context.caseOfInterest);
            if(!isLast) {
                return;
            }

            CtIf ctIf = factory.createIf();
            CtBlock ctBlock = factory.createBlock();
            ctBlock.addStatement(factory
                    .createCodeSnippetStatement(String.format("%s.%s()", variableName, typeName)));
            ctIf.setThenStatement(ctBlock);
            ctIf.setCondition(factory
                    .createCodeSnippetExpression(String.format("%s != null", variableName)));
            context.caseOfInterest.getStatement().insertAfter(ctIf);


        }
    }

    private void recycleVariableUsed(RefactoringPhaseContext context) {
        if(!(context.caseOfInterest instanceof VariableUsed)) {
            return;
        }
        List<CtVariableAccess> variableAccesses = ((VariableUsed) context.caseOfInterest).variableAccesses;
        Set<String> alreadyRecycles = new HashSet<>();
        variableAccesses.forEach(ctVariableAccess -> {
            String variableName = ctVariableAccess.getVariable().getSimpleName();

            if(alreadyRecycles.contains(variableName)) {
                return;
            }

            String typeName = opportunities.get(getTypeByVariableName(variableName, context.casesOfInterest));
            if(typeName == null) {
                return;
            }
            List<CaseOfInterest> casesOfInterest = getCasesByVariableName(variableName, context.casesOfInterest);
            boolean isLast = casesOfInterest.get(casesOfInterest.size() - 1).equals(context.caseOfInterest);
            if(!isLast) {
                return;
            }
            boolean wasVariableRecycled = wasVariableRecycled(variableName, context);
            if(wasVariableRecycled) {
                return;
            }

            boolean isInControl = isVariableUnderControl(variableName, context);
            if(!isInControl) {
                return;
            }

            Factory factory = ctVariableAccess.getFactory();
            CtIf ctIf = factory.createIf();
            CtBlock ctBlock = factory.createBlock();
            ctBlock.addStatement(factory
                    .createCodeSnippetStatement(String.format("%s.%s()", variableName, typeName)));
            ctIf.setThenStatement(ctBlock);
            ctIf.setCondition(factory
                    .createCodeSnippetExpression(String.format("%s != null", variableName)));
            context.caseOfInterest.getStatement().insertAfter(ctIf);
            alreadyRecycles.add(variableName);
        });
    }

    @Override
    public void processCase(RefactoringPhaseContext context) {
//        System.out.println("BEFORE: " + context.block.toStringDebug());
        recycleVariableDeclared(context);
        recycleVariableReassigned(context);
        recycleVariableUsed(context);
//        System.out.println("AFTER: " + context.block.toStringDebug());
    }

    private void refactor(CtMethod method) {
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