package compilation;
import static compilation.A.isFDroid;
import static compilation.A.targetMethod;

public class B {

    public static void methodOne() {
        if(isFDroid) {
            targetMethod();
        }
    }

    public static void methodTwo() {

    }

}