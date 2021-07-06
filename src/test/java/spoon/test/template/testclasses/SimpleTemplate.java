package spoon.test.template.testclasses;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;
import spoon.template.Substitution;
import spoon.template.Template;

import java.sql.Statement;

/**
 * Created by urli on 31/05/2017.
 */
public class SimpleTemplate extends StatementTemplate {
    // template parameter fields
    @Parameter
    String _parameter_;

    // parameters binding
    @Local
    public SimpleTemplate(String parameter) {
        _parameter_ = parameter;
    }

    @Override
    public CtClass apply(CtType targetType) {
        Substitution.insertAll(targetType, this);

        return (CtClass) targetType;
    }

    @Override
    public void statement() throws Throwable {
        System.out.println(_parameter_);
    }
}