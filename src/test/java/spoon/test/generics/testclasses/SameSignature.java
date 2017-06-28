package spoon.test.generics.testclasses;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by urli on 21/06/2017.
 */
public class SameSignature<T extends String> implements Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {

    }
}
