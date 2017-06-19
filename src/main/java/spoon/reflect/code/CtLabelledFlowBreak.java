package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.DerivedProperty;

import static spoon.reflect.path.CtRole.TARGET_LABEL;

/**
 * This abstract code element represents all the statements that break the
 * control flow of the program and which can support a label.
 */
public interface CtLabelledFlowBreak extends CtCFlowBreak {
    /**
     * Gets the label from which the control flow breaks (null if no label
     * defined).
     */
    @PropertyGetter(role = TARGET_LABEL)
    String getTargetLabel();

    /**
     * Sets the label from which the control flow breaks (null if no label
     * defined).
     */
    @PropertySetter(role = TARGET_LABEL)
    <T extends CtLabelledFlowBreak> T setTargetLabel(String targetLabel);

    @DerivedProperty
    CtStatement getLabelledStatement();
}
