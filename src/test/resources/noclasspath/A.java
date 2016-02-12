import example.B;

class A extends B {

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