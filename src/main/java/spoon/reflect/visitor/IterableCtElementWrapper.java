package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Wrapper for CtElement instances, to be able to iterate through them using
 * range-based for loops, in depth-first search order.
 */
public class IterableCtElementWrapper implements Iterable<CtElement> {


    /**
     * The CtElement this class wraps around.
     */
    private CtElement ctElement;

    /**
     * The CtIterator used to iterate through the wrapped CtElement.
     */
    private CtIterator ctIterator;

    /**
     * Constructor from a CtElement.
     * @param ctElement
     */
    public IterableCtElementWrapper(CtElement ctElement) {
        this.ctElement = ctElement;
        this.ctIterator = new CtIterator(ctElement);
    }

    /**
     * @return Returns an iterator over elements of type CtElement.
     */
    @Override
    public Iterator<CtElement> iterator() {
        return ctIterator;
    }

    /**
     * Performs the given action over this iterable's range, in order of iteration.
     * @param action The action to be performed element-wise.
     */
    @Override
    public void forEach(Consumer<? super CtElement> action) {
        for (CtElement elem : this) {
            action.accept(elem);
        }
    }

    /**
     * Creates a Spliterator over the elements described by this Iterable.
     * @return a Spliterator over the elements described by this Iterable.
     */
    @Override
    public Spliterator<CtElement> spliterator() {
        return Spliterators.spliteratorUnknownSize(ctIterator, Spliterator.ORDERED);
    }
}
