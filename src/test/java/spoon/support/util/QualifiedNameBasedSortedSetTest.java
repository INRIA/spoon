package spoon.support.util;

import org.junit.Test;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.java.JavaReflectionTreeBuilder;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QualifiedNameBasedSortedSetTest {
    @Test
    public void testIteratorOrdering() {
        // contract: elements without source position should appear before any element with source position,
        // and be ordered between themselves by qualified name. Elements with source position should be ordered
        // between themselves by source position.
        final CtType<?> linkedListType = new JavaReflectionTreeBuilder(ModelUtils.createFactory())
                .scan(LinkedList.class);

        final MockSourcePosition smallerSourcePos = new MockSourcePosition(10, 12);
        final MockSourcePosition largerSourcePos = new MockSourcePosition(14, 15);

        QualifiedNameBasedSortedSet<CtTypeReference<?>> superInterfaces = new QualifiedNameBasedSortedSet<>(
                linkedListType.getSuperInterfaces());
        List<CtTypeReference<?>> expectedInterfaceOrder = new ArrayList<>(superInterfaces);

        CtTypeReference<?> largerSourcePosElem = expectedInterfaceOrder.remove(0);
        CtTypeReference<?> smallerSourcePosElem = expectedInterfaceOrder.remove(0);
        // setting the source positions will reorder the elements when fetched from the original set
        largerSourcePosElem.setPosition(largerSourcePos);
        smallerSourcePosElem.setPosition(smallerSourcePos);
        expectedInterfaceOrder.add(smallerSourcePosElem);
        expectedInterfaceOrder.add(largerSourcePosElem);

        Iterator<CtTypeReference<?>> expected = expectedInterfaceOrder.iterator();
        Iterator<CtTypeReference<?>> actual = superInterfaces.iterator();

        assertTrue(expected.hasNext());
        while (expected.hasNext()) {
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(actual.hasNext());
    }


    private static class MockSourcePosition implements SourcePosition {
        final int sourceStart;
        final int sourceEnd;

        public MockSourcePosition(int sourceStart, int sourceEnd) {
            this.sourceStart = sourceStart;
            this.sourceEnd = sourceEnd;
        }

        @Override
        public boolean isValidPosition() {
            return true;
        }

        @Override
        public int getSourceEnd() {
            return sourceEnd;
        }

        @Override
        public int getSourceStart() {
            return sourceStart;
        }

        /*
         * Methods below here don't matter
         */

        @Override
        public File getFile() {
            return null;
        }

        @Override
        public CompilationUnit getCompilationUnit() {
            return null;
        }

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getEndLine() {
            return 0;
        }

        @Override
        public int getColumn() {
            return 0;
        }

        @Override
        public int getEndColumn() {
            return 0;
        }

    }
}
