package spoon.test.imports.testclasses2;

import java.util.List;

public final class Interners {
    private Interners() {
    }

    private static class WeakInterner<E>  {
        private enum Dummy {
        	VALUE;        
        }
        List<Dummy> list;
    }
}

