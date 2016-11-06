package spoon.test.generics.testclasses;

@SuppressWarnings(value = { "unchecked" , "unused" , "rawtypes" })
public class Spaghetti<B> {
    public interface This<K, V> {    }

    public class That<K, V> implements This<K, V> {    }

    public class Tester implements This<String, B> {    }

    private Spaghetti<B>.Tester tester;
    private Tester tester1;

    private That<String, String> field;
    private Spaghetti<String>.That<String, String> field1;
    private Spaghetti<Number>.That<String, String> field2;
}
