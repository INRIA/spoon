package spoon.leafactorci.engine;

/**
 * Represents the behavior of a case detector, it works iteratively by means of a DetectionPhaseContext.
 */
public interface CaseDetector {
    /**
     * Detects cases of interest
     *
     * @param context The iteration context of the current iteration in progress
     */
    void detectCase(DetectionPhaseContext context);

    /**
     * Compiles a list of case detectors into a single case detector
     *
     * @param caseDetectorList A list of case detectors that we wish to join
     * @return The compiled CaseDetector
     */
    static CaseDetector combineCaseDetector(CaseDetector... caseDetectorList) {
        return context -> {
            for (CaseDetector caseDetector : caseDetectorList) {
                caseDetector.detectCase(context);
            }
        };
    }
}
