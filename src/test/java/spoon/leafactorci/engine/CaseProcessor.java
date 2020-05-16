package spoon.leafactorci.engine;

/**
 * Represents the behavior of a case detector, it works iteratively by means of a DetectionPhaseContext.
 */
public interface CaseProcessor {
    /**
     * Processes cases of interest
     *
     * @param context The iteration context of the current iteration in progress
     */
    void processCase(RefactoringPhaseContext context);

    /**
     * Compiles a list of case detectors into a single case detector
     *
     * @param caseProcessors A list of case detectors that we wish to join
     * @return The compiled CaseDetector
     */
    static CaseProcessor combineCaseProcessor(CaseProcessor... caseProcessors) {
        return context -> {
            for (CaseProcessor caseProcessor : caseProcessors) {
                caseProcessor.processCase(context);
            }
        };
    }
}
