package spoon.test.template.testclasses;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class InsertAllFieldsTemplate extends StatementTemplate {
    @Parameter
    String _parameter_;
    String testString = "goodName";

    @Local
    public InsertAllFieldsTemplate(String parameter) {
        _parameter_ = parameter;
    }

    @Override
    public void statement() {
        System.out.println(_parameter_);
    }
}