package import_static;

import java.util.List;
import static import_static.inner_class.Constants.InnerClass;

public class Main {
    public void fun() {
        List list = List.of(1, 2, 3);
        InnerClass innerClass = new InnerClass();
        innerClass.print();
    }
}
