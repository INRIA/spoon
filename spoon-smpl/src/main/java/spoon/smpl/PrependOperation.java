package spoon.smpl;

/**
 * PrependOperation defines a prioritized category of operations that when present in a list
 * of operations must be:
 * 1) processed BEFORE any AppendOperation
 * 2) processed BEFORE any DeleteOperation
 * 3) processed BEFORE any non-prioritized class of Operation
 * 4) processed in the same order relative to the order of elements in the list
 */
public interface PrependOperation extends Operation {
}
