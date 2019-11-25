package compilation;


/**
 * Some test data for the JavaPerformanceDetector
 */
@SuppressWarnings("unused")
public class A {
    public static interface OnCheckedChangeListener {
        // teste
        void onCheckedChanged(String x, int i);//test
    }

    public void method1(OnCheckedChangeListener onCheckedChangeListener) {
        onCheckedChangeListener.onCheckedChanged("test", 2);
    }

    public void method2(A a) {
        a.method1(new OnCheckedChangeListener() {
            //Teste
            /*
            MOre theste
             */
            @Override
            public void onCheckedChanged(String x, int i) {
                // hello
                System.out.println(x);
            }
        });
        a.method1((String x, int i) -> {
                // hello
                System.out.println(x);
            });
    }
}
