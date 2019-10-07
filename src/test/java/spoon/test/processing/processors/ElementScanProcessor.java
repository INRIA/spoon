package spoon.test.processing.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

public class ElementScanProcessor extends AbstractProcessor<CtElement> {

    public ElementScanProcessor(){}

    @Override
    public boolean isToBeProcessed(CtElement candidate)
    {
        if (candidate instanceof CtBinaryOperator){
            return true;
        }
        return false;
    }
    @Override
    public void process(CtElement element) {
        CtBinaryOperator bo = (CtBinaryOperator)element;
        System.out.println(bo);
    }
}