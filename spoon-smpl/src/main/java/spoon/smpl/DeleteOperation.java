package spoon.smpl;

/**
 * AppendOperation defines a prioritized category of operations that when present in a list
 * of operations must be:
 * 1) processed AFTER any PrependOperation
 * 2) processed AFTER any AppendOperation
 * 3) processed BEFORE any non-prioritized class of Operation
 * 4) processed in the same order relative to the order of elements in the list
 */
public interface DeleteOperation extends Operation {
}
