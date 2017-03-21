package spoon.test.generics.testclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by urli on 03/03/2017.
 */
public class ImplicitAndJoker {

    private static <T> Set<T> filter(Iterable<T> elements, String... predicates) {
        Set<T> result = new HashSet<T>();
        List<String> listStr = Arrays.asList(predicates);

        for (T element : elements) {
            if (listStr.contains(element.toString())) {
                result.add(element);
            }
        }
        return result;
    }

    public static <T> List<Class<? extends T>> forNames(Iterable<T> classes, ClassLoader... classLoaders) {
        ArrayList result = new ArrayList();
        Iterator i$ = classes.iterator();

        while(i$.hasNext()) {
            String className = i$.next().toString();
            for (ClassLoader loader : classLoaders) {
                try {
                    Class<?> zeClass = loader.loadClass(className);
                    result.add(zeClass);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }

        }

        return result;
    }

    public void testBidule() {
        List<Object> stringClass = Arrays.asList(new Object[] {"ImplicitAndJoker"});
        Iterable<Class<?>> filter = filter(forNames(stringClass, ClassLoader.getSystemClassLoader()), "ImplicitAndJoker");
    }
}
