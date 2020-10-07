package spoon.test.template.testclasses;

import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;


public class AssertToIfAssertedStatementTemplate extends StatementTemplate {
    @Parameter
    public CtExpression<Boolean> _asserted_;
    
    @Local
    public AssertToIfAssertedStatementTemplate(CtAssert<?> ctAssert) {
        this._asserted_ = ctAssert.getAssertExpression();
    }
    
    public void statement() {
        if (_asserted_.S())
            assert true;
        else
            assert false;
    }
}
