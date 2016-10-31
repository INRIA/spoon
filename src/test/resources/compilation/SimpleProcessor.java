package compilation;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtType;

/**
 * Simple Processor to demonstrate <code>--precompile</code> issue
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public class SimpleProcessor extends AbstractProcessor<CtType<?>> {

    @Override
    public void process(CtType<?> element) {
        System.out.println(">> Hello: " + element.getSimpleName() + " <<");
    }
}
