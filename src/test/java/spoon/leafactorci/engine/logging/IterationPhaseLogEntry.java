package spoon.leafactorci.engine.logging;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a log entry for a iteration phase
 */
public interface IterationPhaseLogEntry extends IterationLogEntry {
    Instant getStartPhaseTimestamp();

    Instant getEndPhaseTimestamp();

    Duration getPhaseDuration();
}
