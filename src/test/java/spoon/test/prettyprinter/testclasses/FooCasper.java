package spoon.test.prettyprinter.testclasses;

public class FooCasper {
    //	public static void main(String[] args) {
////		new FooCasper().selfTest();
//	}
    FooCasper f;

    public FooCasper bug1() {
        if (new FooCasper(1).foo() != null) {
            throw new Error();
        }
        FooCasper g = new FooCasper(1).foo();
        f=g;
        System.out.println(f);
        // the NPE
        f.bar();
        return null;
    }

    public FooCasper foo() {
        return foo2();
//		return null;
    }

    public FooCasper foo2() {
        return null;
    }

    public FooCasper foo3() {
        return f;
    }

    public void bar() {
    }

    public FooCasper foo5(FooCasper o) {
        return o;
    }

    public void bug2() {
        foo5(null).f.bar();
    }

    public void bug3() {
        FooCasper[] tab = null;
        if (0==1) {tab = new FooCasper[0];}
        tab[0].bar();
    }

    // testing the given ObjectNullified
    public void bug4() {
        Object tab  =null;
        if (0==1) {tab = new Object();}
        tab.toString();
    }

    public FooCasper(int i) {
    }

    public FooCasper() {
    }

    // toString support
    public void toString_support() {
        FooCasper o = null;
        o.toString();
    }

    // testing arrays
    public void array_support() {
        FooCasper o = null;
        FooCasper[] array = new FooCasper[10];
        array[1] = o;
        array[2] = array[1];
        array[2].bar();
    }

    public void literal() {
        FooCasper tab = null;
        tab.literal();
    }

    public void literal2() {
        FooCasper tab = new FooCasper();
        tab = null;
        tab.literal();
    }
}

