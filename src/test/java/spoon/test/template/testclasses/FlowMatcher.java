package spoon.test.template.testclasses;

import spoon.template.TemplateParameter;

public class FlowMatcher extends Flow {

    public TemplateParameter<Flow> _subFlow_;

    private void subFlowMatcher() {
        subFlow(_subFlow_.S());
    }
}
