package spoon.test.processing.testclasses;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urli on 10/08/2017.
 */
public abstract class GenericCtTypeProcessor<T extends CtType> extends AbstractProcessor<T> {

    public GenericCtTypeProcessor(Class<T> zeClass) {
        super.addProcessedElementType(zeClass);
    }

    public List<T> elements = new ArrayList<T>();

    @Override
    public void process(T element) {
        elements.add(element);
    }
}
