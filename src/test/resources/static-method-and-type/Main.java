package static_method_and_type;

import static static_method_and_type.imports_are_here.Bar.foo;

public class Main {
    public void fun() {
        foo();
    }

    static class another_bar extends foo { }

}
