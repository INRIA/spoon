package spoon.test.generics.testclasses3;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class BugCollection<K,V> {
    // without SuppressWarnings
    public static final ACLass<?> INSTANCE = new ACLass();
    
    @SuppressWarnings("rawtypes")
    public static final ACLass<?> INSTANCE2 = new ACLass();
    
    Map.Entry x;

    Map.Entry<?,?> y;
    
    Map.Entry<String,Integer> z;
    
    void foo(){
    	x=null;
        Map.Entry lx;

        Map.Entry<?,?> ly;
        
        Map.Entry<String,Integer> lz;
        
        Iterator<Map.Entry<?,?>> it;
        
    }
	
    class Foo implements Set<Map.Entry<K, V>>{

		@Override
		public int size() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Entry<K, V> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

    }

}

class ACLass<E>  {}

class ComparableComparator<E extends Comparable<? super E>>  {}
