package compilation.subpackage;

//import static compilation.ClassWithStaticFields.methodOne;
//import static compilation.ClassWithStaticFields.methodTwo;
import static compilation.ClassWithStaticFields.*;


public class ClassUsingStaticFields {

    public void method() {
        methodOne();
        methodTwo();
    }

}