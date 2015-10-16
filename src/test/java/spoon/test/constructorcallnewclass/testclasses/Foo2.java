package spoon.test.constructorcallnewclass.testclasses;

/**
 * Created by thomas on 14/10/15.
 */
public class Foo2 {

    public void exec() {
        AbstractClass abstractClass1 = new AbstractClass(1) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass2 = new AbstractClass(1) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass3 = new AbstractClass(2) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass4 = new AbstractClass(3) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass5 = new AbstractClass(4) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass6 = new AbstractClass(6) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass7 = new AbstractClass(7) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass8 = new AbstractClass(8) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass9 = new AbstractClass(9) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass10 = new AbstractClass(10) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };
        AbstractClass abstractClass11 = new AbstractClass(11) {
            @Override
            public double getValue(double[] value) {
                return 0;
            }
        };

        AbstractClass abstractClass12 = new AbstractClass(12) {
            @Override
            public double getValue(double[] value) {
                return new AbstractClass(12) {
                    @Override
                    public double getValue(double[] value) {
                        return 0;
                    }
                }.getValue(value);
            }
        };
    }

    private abstract class AbstractClass {
        private int i;
        AbstractClass(int i) {
            this.i=i;
        }

        public abstract double getValue(double[] value);
    }
}
