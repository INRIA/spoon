package spoon.smpl.operation;

/**
 * OperationCategory defines categories for prioritization and ordering of operations.
 * <p>
 * A class implementing Operation contains a method accept() that takes a value of
 * OperationCategory. The Transformer provides calls to these Operations with OperationCategory
 * values for the Operations to decide for themselves whether or not to act on the call.
 * <p>
 * The current set defines the following order of application:
 * 1) PREPEND operations, in stored order.
 * 2) APPEND operations, in reverse stored order.
 * 3) DELETE operations, in stored order.
 */
public enum OperationCategory {
	PREPEND, APPEND, DELETE;
}
