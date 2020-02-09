package me.ccrama.redditslide;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Carlos on 10/19/2016.
 */
public class CaseInsensitiveArrayList extends java.util.ArrayList<java.lang.String> {
    public CaseInsensitiveArrayList() {
        super();
    }

    public CaseInsensitiveArrayList(me.ccrama.redditslide.CaseInsensitiveArrayList strings) {
        super(strings);
    }

    public CaseInsensitiveArrayList(java.util.List<java.lang.String> strings) {
        super(strings);
    }

    @java.lang.Override
    public boolean contains(java.lang.Object o) {
        java.lang.String parameter = ((java.lang.String) (o));
        for (java.lang.String s : this) {
            if (parameter.equalsIgnoreCase(s))
                return true;

        }
        return false;
    }
}