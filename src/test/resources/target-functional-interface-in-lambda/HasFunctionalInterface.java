import java.util.function.IntFunction;

public class HasFunctionalInterface {
    public static void supplyOne(IntFunction<String> intFunction) {
        intFunction.apply(1);
    }
}