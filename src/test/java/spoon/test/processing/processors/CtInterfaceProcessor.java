package spoon.test.processing.processors;

import spoon.reflect.declaration.CtInterface;

/**
 * Created by urli on 31/08/2017.
 */
public class CtInterfaceProcessor extends GenericCtTypeProcessor<CtInterface> {
    public CtInterfaceProcessor() {
        super(CtInterface.class);
    }
}
