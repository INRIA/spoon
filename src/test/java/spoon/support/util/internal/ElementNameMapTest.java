package spoon.support.util.internal;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ElementNameMapTest {

    static class SimpleElementNameMap extends ElementNameMap<CtElement> {
        final CtElement owner;

        SimpleElementNameMap(CtElement owner) {
            this.owner = owner;
        }
        @Override
        protected CtElement getOwner() {
            return owner;
        }

        @Override
        protected CtRole getRole() {
            return null;
        }
    }

    @Test
    void entrySet_returnsElementsInInsertionOrder() {
        // contract: entrySet() should return elements in insertion order. This test is fairly weak but it at least
        // guards against the output being sorted by key or value, which was the case previously.

        Factory factory = new Launcher().getFactory();
        List<Map.Entry<String, CtLiteral<String>>> entries = Stream.of("c", "a", "b")
                .map(factory::createLiteral)
                .map(literal -> Map.entry(literal.getValue(), literal))
                .collect(Collectors.toList());

        var map = new SimpleElementNameMap(factory.createClass());
        entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        var fetchedEntries = new ArrayList<>(map.entrySet());

        assertEquals(fetchedEntries, entries);
    }
}
