// this class uses a static method from its parent class, which previously caused the ImportCleaner
// to statically import the method, even though it's inherited.
public class Derived extends Base {
    public static void main(String[] args) {
        System.out.println(getMeaning());
    }
}