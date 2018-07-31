package spoon.test.processing.processors;

import spoon.reflect.declaration.CtClass;

/**
 * Created by urli on 10/08/2017.
 */
public class CtClassProcessor extends GenericCtTypeProcessor<CtClass> {

    public CtClassProcessor() {
        super(CtClass.class);
    }
}
