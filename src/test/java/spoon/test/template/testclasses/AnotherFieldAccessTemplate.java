package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Parameter;

/**
 * Created by urli on 06/07/2017.
 */
public class AnotherFieldAccessTemplate extends ExtensionTemplate {

    @Parameter("$name$")
    String name = "x";

    int $name$;
    int m_$name$;
    {
        System.out.println($name$+m_$name$);
    }
}
