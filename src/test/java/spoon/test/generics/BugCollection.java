package spoon.test.generics;


public class BugCollection {
    // without SuppressWarnings
    public static final ACLass<?> INSTANCE = new ACLass();
    
    @SuppressWarnings("rawtypes")
    public static final ACLass<?> INSTANCE2 = new ACLass();
}

class ACLass<E>  {}

class ComparableComparator<E extends Comparable<? super E>>  {}
