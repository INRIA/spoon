package spoon.test.processing.testclasses.counter;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

/**
 * Created by urli on 19/09/2017.
 */
public class CounterProcessor extends AbstractProcessor<CtType> {

    public int nbProcessed = 0;

    @Override
    public void process(CtType element) {
        if (element instanceof CtClass || element instanceof CtInterface) {
            nbProcessed++;
        }
    }
}
