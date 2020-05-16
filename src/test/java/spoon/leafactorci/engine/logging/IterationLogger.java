package spoon.leafactorci.engine.logging;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a logger for IterationLogEntry's that occur during iteration
 */
public class IterationLogger {
    private List<IterationLogEntry> logs;

    /**
     * Constructor
     */
    public IterationLogger() {
        this.logs = new LinkedList<>();
    }

    /**
     * Gets a reference to the list of logs
     *
     * @return A reference to the list of logs
     */
    public List<IterationLogEntry> getLogs() {
        return logs;
    }
}
