package spoon.leafactorci.engine;

/**
 * Represents the behavior of a case filter, it works iteratively by means of a DetectionPhaseContext.
 */
public interface CaseTransformer {
    /**
     * Processes cases of interest
     *
     * @param context The iteration context of the current iteration in progress
     */
    void transformCase(TransformationPhaseContext context);

    /**
     * Leaves intact
     *
     * @return The compiled CaseDetector
     */
    static CaseTransformer createPassThroughTransformation() {
        return context -> context.accept(context.caseOfInterest);
    }

    /**
     * Compiles a list of case detectors into a single case detector
     *
     * @param caseTransformers A list of case detectors that we wish to join
     * @return The compiled CaseDetector
     */
    static CaseTransformer combineCaseProcessor(CaseTransformer... caseTransformers) {
        return context -> {
            for (CaseTransformer caseTransformer : caseTransformers) {
                caseTransformer.transformCase(context);
            }
        };
    }
}
