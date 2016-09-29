import example.B;
import example.C;

class A3 extends B implements C {

    private Runnable runnable;

    public void foo() {
        synchronized (lock) {
           runnable = new Runnable() {

                @Override
                public void run() {

                }
            };
        }
    }
}