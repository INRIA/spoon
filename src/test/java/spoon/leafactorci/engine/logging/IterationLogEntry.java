package spoon.leafactorci.engine.logging;

import spoon.leafactorci.engine.RefactoringRule;

import java.time.Instant;

/**
 * Represents a log entry for the iteration process
 */
public interface IterationLogEntry {
    RefactoringRule getRule();

    String getName();

    String getDescription();

    Instant getTimeStamp();
}
