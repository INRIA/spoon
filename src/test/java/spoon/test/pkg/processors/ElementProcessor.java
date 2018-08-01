package spoon.test.pkg.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;

/**
 * Created by urli on 11/09/2017.
 */
public class ElementProcessor extends AbstractProcessor<CtElement> {
    public void process(CtElement element) {
        if(element instanceof CtPackage){
            ((CtPackage) element).setSimpleName("newtest");
        }
    }
}

