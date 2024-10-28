package spoon.support.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

public class SortedListTest {
    public class BasicComparator implements Comparator<CtElement> {

        private final List<CtElement> myOrdering;
        
        public BasicComparator(List<CtElement> orderList) {
            this.myOrdering = orderList;
        }
        
        @Override
        public int compare(CtElement o1, CtElement o2) {
            int idxFirst = - 1;
            int idxSecond = -1;
            int idx = 1;
            for(CtElement clazz : myOrdering) {
                if (o1 == o2 && o1 == clazz) {
                    return 0;
                } else if (clazz == o1) {
                    if (idxFirst == -1) {
                        idxFirst = idx;
                        idx++;    
                    } else {
                        throw new IllegalStateException();
                    }
                    
                } else if (clazz == o2) {
                    if (idxSecond == -1) {
                        idxSecond = idx;
                        idx++;
                    } else {
                        throw new IllegalStateException();
                    }
                };
            }
            
            return idxFirst - idxSecond;
        }
        
    }
    
    @Test
    public void sortedListUsesComparatorTestPass() {
        Comparator<CtClass<?>> cmp1 = mock();
        SortedList<CtClass<?>> sl1 = new SortedList<>(cmp1);
        CtClass<?> lc1 = mock();
        CtClass<?> lc2 = mock();
        assertTrue(sl1.add(lc1), "Element should have been inserted");
        assertTrue(sl1.add(lc2), "Element should have been inserted");
        verify(cmp1).compare(lc2, lc1);
    }
    
    @Test
    public void verifyAddWithIndexThrowsException() {
        Comparator<CtClass<?>> cmp1 = mock();
        SortedList<CtClass<?>> sl1 = new SortedList<>(cmp1);
        CtClass<?> lc1 = mock();
        IllegalArgumentException ex = assertThrows( 
                IllegalArgumentException.class, 
                new Executable() {

                    @Override
                    public void execute() throws Throwable {
                        sl1.add(1, lc1);
                    }
                    
                },
                "Add with index should trhow IllegalArgumentException");
        assertEquals(
                "cannot force a position with a sorted list that has its own ordering",
                ex.getMessage(),
                "Exception message does not match the expected");
    }
    
    @Test
    public void verifyThatGetComparatorReturnsTheComparatorTestPass() {
        Comparator<CtClass<?>> cmp1 = mock();
        SortedList<CtClass<?>> sl1 = new SortedList<>(cmp1);
        assertTrue(cmp1 == sl1.getComparator(), "Comparator does not match the expected");
    }
    
    @Test
    public void verifyThatComparatorCanBeReplacedTestPass() {
        Comparator<? super CtClass<?>> cmp1 = mock();
        SortedList<CtClass<?>> sl1 = new SortedList<>(cmp1);
        BasicComparator bc1 = new BasicComparator(null);
        sl1.setComparator(bc1);
        assertTrue(bc1 == sl1.getComparator(), "Comparator does not match the expected");
    }
    
    @Test
    public void addAllInsertsAllElementsAndTheyAreOrderedTestPass() {
        List<CtElement> orderedList = new ArrayList<>(3);        
        CtClass<?> lc1 = mock();
        CtClass<?> lc2 = mock();
        CtClass<?> lc3 = mock();
        orderedList.add(lc1);
        orderedList.add(lc2);
        orderedList.add(lc3);
        
        BasicComparator bc1 = new BasicComparator(orderedList);
        SortedList<CtElement> sl1 = new SortedList<>(bc1);
        
        List<CtElement> unorderedList = new ArrayList<>(3);
        unorderedList.add(lc3);
        unorderedList.add(lc1);
        unorderedList.add(lc2);
        assertTrue(sl1.addAll(unorderedList), "All elements should have been added");
        
        //Verify ordering
        assertTrue(sl1.get(0) == lc1, 
                "First element does not match the expected ordering");
        assertTrue(sl1.get(1) == lc2, 
                "Second element does not match the expected ordering");
        assertTrue(sl1.get(2) == lc3,
                "Third element does not match the expected ordering");
    }
 
    @Test
    public void addAllWithEmptyListShouldLeaveTheListUnchangedTestPass() {
        List<CtElement> orderedList = new ArrayList<>(3);        
        CtClass<?> lc1 = mock();
        CtClass<?> lc2 = mock();
        CtClass<?> lc3 = mock();
        orderedList.add(lc1);
        orderedList.add(lc2);
        orderedList.add(lc3);
        
        BasicComparator bc1 = new BasicComparator(orderedList);
        SortedList<CtElement> sl1 = new SortedList<>(bc1);

        assertTrue(sl1.add(lc1), "List should have changed");
        assertFalse(sl1.addAll(new LinkedList<>()), "List should not have changed");
    }
}