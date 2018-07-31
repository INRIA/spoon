package spoon.test.processing.processors;

import spoon.reflect.declaration.CtType;

/**
 * Created by urli on 31/08/2017.
 */
public class CtTypeProcessor extends GenericCtTypeProcessor<CtType> {

    public CtTypeProcessor() {
        super(CtType.class);
    }
}
