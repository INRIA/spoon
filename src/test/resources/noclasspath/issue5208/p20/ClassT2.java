package p20;
import p8.GlobalExecutor;

public class ClassT2 extends ClassT1 {
    public void fun(){
        GlobalExecutor.executeByCommon(() -> {
            fun2(classT3);
        });
    }
}