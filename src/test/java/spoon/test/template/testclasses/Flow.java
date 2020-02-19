package spoon.test.template.testclasses;

public class Flow {

    public Flow subFlow(Object o) {
        System.out.println(o);
        return (Flow) o;
    }

    public void f1() {
        Flow s1 = subFlow(new Flow());
        Flow s2 = (Flow) subFlow(new Flow());
    }
}
