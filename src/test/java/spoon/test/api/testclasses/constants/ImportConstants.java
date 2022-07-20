package spoon.test.api.testclasses.constants;

import spoon.test.api.testclasses.constants.Constants;
import static spoon.test.api.testclasses.constants.Constants.LAST;

public class ImportConstants {

    public String concatConstants(){
        var middle = Constants.MIDDLE;
        return concat(Constants.FIRST, middle, LAST);
    }

    public String concat(String prefix, String middle, String suffix){
        return prefix + middle + suffix;
    }
}
