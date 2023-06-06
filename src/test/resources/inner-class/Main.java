package inner_class;

import java.util.List;
import static inner_class.constants.Constants.InnerClass;

public class Main {
    public void fun() {
        List list = List.of(1, 2, 3);
        InnerClass innerClass = new InnerClass();
        innerClass.print();
    }
}
