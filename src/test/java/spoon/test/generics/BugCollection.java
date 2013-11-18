package spoon.test.generics;

import java.util.Iterator;
import java.util.Map;


public class BugCollection {
    // without SuppressWarnings
    public static final ACLass<?> INSTANCE = new ACLass();
    
    @SuppressWarnings("rawtypes")
    public static final ACLass<?> INSTANCE2 = new ACLass();
    
    Map.Entry x;

    Map.Entry<?,?> y;
    
    Map.Entry<String,Integer> z;
    
    void foo(){
        Map.Entry lx;

        Map.Entry<?,?> ly;
        
        Map.Entry<String,Integer> lz;
        
        Iterator<Map.Entry<?,?>> it;
    }


}

class ACLass<E>  {}

class ComparableComparator<E extends Comparable<? super E>>  {}
