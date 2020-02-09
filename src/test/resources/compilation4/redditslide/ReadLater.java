package me.ccrama.redditslide;
/**
 * Created by ccrama on 7/19/2015.
 */
public class ReadLater {
    public static void setReadLater(net.dean.jraw.models.Submission s, boolean readLater) {
        if (readLater) {
            com.lusfold.androidkeyvaluestore.KVStore.getInstance().insert("readLater" + s.getFullName(), java.lang.String.valueOf(java.lang.System.currentTimeMillis()));
        } else if (!com.lusfold.androidkeyvaluestore.KVStore.getInstance().getByContains("readLater" + s.getFullName()).isEmpty()) {
            com.lusfold.androidkeyvaluestore.KVStore.getInstance().delete("readLater" + s.getFullName());
        }
    }

    public static boolean isToBeReadLater(net.dean.jraw.models.Submission s) {
        return !com.lusfold.androidkeyvaluestore.KVStore.getInstance().getByContains("readLater" + s.getFullName()).isEmpty();
    }
}