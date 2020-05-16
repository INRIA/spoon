package spoon.leafactorci.engine;

import spoon.leafactorci.engine.logging.IterationLogger;
import spoon.leafactorci.engine.logging.SimpleIterationPhaseLogEntry;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an iteration over the block of statements
 */
public interface Iteration {

    /**
     * Lifecycle method that happens before the iteration starts. Helpful for setups.
     *
     * @param context The detection phase context
     */
    void onSetup(DetectionPhaseContext context);

    /**
     * Lifecycle method that happens in an individual iteration of the statements on the detection phase
     * and before detecting cases of interest in the particular iteration.
     *
     * @param context The detection phase context
     */
    void onWillIterate(DetectionPhaseContext context);


    /**
     * Lifecycle method that happens in an individual iteration of the statements on the detection phase
     * and after detecting cases of interest in the particular iteration.
     *
     * @param context The detection phase context
     */
    void onDidIterate(DetectionPhaseContext context);

    /**
     * Lifecycle method that happens when the detection phase ends and the transformation phase starts.
     *
     * @param context The transformation phase context
     */
    void onWillTransform(TransformationPhaseContext context);

    /**
     * Lifecycle method that happens in an individual iteration of the cases of interest found on the detection phase
     * and before the transformation is applied for that particular case of interest.
     *
     * @param context The transformation phase context
     */
    void onWillTransformCase(TransformationPhaseContext context);

    /**
     * Lifecycle method that happens in an individual iteration of the cases of interest found on the detection phase
     * and after the transformation is applied for that particular case of interest.
     *
     * @param context The transformation phase context
     */
    void onDidTransformCase(TransformationPhaseContext context);


    /**
     * Lifecycle method that happens when the detection phase ends and the refactoring phase starts.
     *
     * @param context The refactoring phase context
     */
    void onWillRefactor(RefactoringPhaseContext context);

    /**
     * Lifecycle method that happens in an individual iteration of the cases of interest found on the detection phase
     * and before refactoring is applied for that particular case of interest.
     *
     * @param context The refactoring phase context
     */
    void onWillRefactorCase(RefactoringPhaseContext context);

    /**
     * Lifecycle method that happens in an individual iteration of the cases of interest found on the detection phase
     * and after refactoring is applied for that particular case of interest.
     *
     * @param context The refactoring phase context
     */
    void onDidRefactorCase(RefactoringPhaseContext context);

    /**
     * Iterates over a particular method
     *
     * @param rule      The refactoring rule that is applied
     * @param logger    The logger for entry logs and performance data
     * @param method    The node of the method declaration
     * @param isDeep    A flag that describes whether the search will go into inner block's or not.
     */
    static void iterateMethod(
            RefactoringRule rule,
            IterationLogger logger,
            CtMethod method,
            boolean isDeep
    ) {
        iterateBlock(rule, logger, method.getBody(), isDeep, 0);
    }

    /**
     * Iterates over a particular method
     *
     * @param rule      The refactoring rule that is applied
     * @param logger    The logger for entry logs and performance data
     * @param block     The node of the method declaration
     * @param isDeep    A flag that describes whether the search will go into inner block's or not.
     * @param depth     An integer representing the current depth of the search
     */
    static void iterateBlock(
            RefactoringRule rule,
            IterationLogger logger,
            CtBlock block,
            boolean isDeep,
            int depth
    ) {
        SimpleIterationPhaseLogEntry setupLogEntry = new SimpleIterationPhaseLogEntry(rule, "Setting up iteration", "Logs the setup phase of an iteration");

        // SETUP PHASE
        setupLogEntry.start();
        SimpleIterationPhaseLogEntry detectionPhaseLogEntry = new SimpleIterationPhaseLogEntry(rule, "Detecting Patterns", "Logs the detection phase of an iteration");
        SimpleIterationPhaseLogEntry transformationPhaseLogEntry = new SimpleIterationPhaseLogEntry(rule, "Transforming Cases of Interest", "Logs the transformation phase of an iteration");
        SimpleIterationPhaseLogEntry refactoringPhaseLogEntry = new SimpleIterationPhaseLogEntry(rule, "Refactoring Cases of Interest", "Logs the refactoring phase of an iteration");
        DetectionPhaseContext detectionPhaseContext = new DetectionPhaseContext();
        detectionPhaseContext.block = block;
        rule.onSetup(detectionPhaseContext);
        setupLogEntry.stop();
        // END SETUP PHASE

        // DETECTION PHASE
        detectionPhaseLogEntry.start();
        for (int i = 0; i < block.getStatements().size(); i++) {
            detectionPhaseContext.statement = block.getStatements().get(i);
            detectionPhaseContext.statementIndex = i;
            rule.onWillIterate(detectionPhaseContext);
            if (isDeep && detectionPhaseContext.statement instanceof CtBlock) {
                CtBlock statementBlock = (CtBlock) detectionPhaseContext.statement;
//                Iteration.iterateBlock(rule, logger, statementBlock, true, depth + 1);
//              Todo: do something with the innerContext
            }
            rule.detectCase(detectionPhaseContext);
            rule.onDidIterate(detectionPhaseContext);
        }
        detectionPhaseLogEntry.stop();
        // END DETECTION PHASE

        // TRANSFORMATION PHASE
        transformationPhaseLogEntry.start();
        List<CaseOfInterest> copyCasesDetected = new ArrayList<>(detectionPhaseContext.caseOfInterestList);
        TransformationPhaseContext transformationPhaseContext = new TransformationPhaseContext();
        transformationPhaseContext.block = block;
        transformationPhaseContext.caseOfInterestList = copyCasesDetected;
        rule.onWillTransform(transformationPhaseContext);
        for (CaseOfInterest caseOfInterest : copyCasesDetected) {
            transformationPhaseContext.caseOfInterest = caseOfInterest;
            rule.onWillTransformCase(transformationPhaseContext);
            rule.transformCase(transformationPhaseContext);
            rule.onDidTransformCase(transformationPhaseContext);
        }
        transformationPhaseLogEntry.stop();
        // END TRANSFORMATION PHASE

        // REFACTORING PHASE
        refactoringPhaseLogEntry.start();
        List<CaseOfInterest> copyCasesFiltered = transformationPhaseContext.getResult();
        RefactoringPhaseContext refactoringPhaseContext = new RefactoringPhaseContext();
        refactoringPhaseContext.offset = 0;
        refactoringPhaseContext.block = block;
        refactoringPhaseContext.casesOfInterest = copyCasesFiltered;
        rule.onWillRefactor(refactoringPhaseContext);
        for (CaseOfInterest caseOfInterest : copyCasesFiltered) {
            refactoringPhaseContext.caseOfInterest = caseOfInterest;
            rule.onWillRefactorCase(refactoringPhaseContext);
            rule.processCase(refactoringPhaseContext);
            rule.onDidRefactorCase(refactoringPhaseContext);
        }
        refactoringPhaseLogEntry.stop();
        // END REFACTORING PHASE

        logger.getLogs().add(setupLogEntry);
        logger.getLogs().add(detectionPhaseLogEntry);
        logger.getLogs().add(transformationPhaseLogEntry);
        logger.getLogs().add(refactoringPhaseLogEntry);
    }
}