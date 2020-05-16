package spoon.leafactorci.engine.logging;

import spoon.leafactorci.engine.RefactoringRule;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a simple general purpose implementation of the IterationPhaseLogEntry interface
 */
public class SimpleIterationPhaseLogEntry implements IterationPhaseLogEntry {
    private RefactoringRule rule;
    private String name;
    private String description;
    private Instant startPhaseTimestamp;
    private Instant endPhaseTimestamp;
    private Duration phaseDuration;

    /**
     * Constructor
     *
     * @param rule        The rule that was applied in the iteration
     * @param name        The name of the log entry
     * @param description The description of the log entry
     */
    public SimpleIterationPhaseLogEntry(RefactoringRule rule, String name, String description) {
        this.rule = rule;
        this.name = name;
        this.description = description;
    }

    /**
     * Marks the start of a phase
     */
    public void start() {
        startPhaseTimestamp = Instant.now();
        endPhaseTimestamp = null;
        phaseDuration = null;
    }

    /**
     * Marks the end of a phase
     */
    public void stop() {
        endPhaseTimestamp = Instant.now();
        phaseDuration = Duration.between(startPhaseTimestamp, endPhaseTimestamp);
    }

    /**
     * Getter for the refactoring rule
     *
     * @return The refactoring rule that was applied
     */
    @Override
    public RefactoringRule getRule() {
        return rule;
    }

    /**
     * Getter for the name of the entry
     *
     * @return The name of the entry
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Getter for the description of the entry
     *
     * @return The description of the entry
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the timestamp of the entry, which is the timestamp of the start of the phase
     *
     * @return The timestamp of the entry, which is the timestamp of the start of the phase
     */
    @Override
    public Instant getTimeStamp() {
        return getStartPhaseTimestamp();
    }

    /**
     * Getter for the timestamp of the start of the phase
     *
     * @return The timestamp of the start of the phase
     */
    @Override
    public Instant getStartPhaseTimestamp() {
        return startPhaseTimestamp;
    }

    /**
     * Getter for the timestamp of the end of the phase
     *
     * @return The timestamp of the end of the phase
     */
    @Override
    public Instant getEndPhaseTimestamp() {
        return endPhaseTimestamp;
    }

    /**
     * Getter for the elapsed time between the start and the end of the phase
     *
     * @return The elapsed time between the start and the end of the phase
     */
    @Override
    public Duration getPhaseDuration() {
        return phaseDuration;
    }
}
