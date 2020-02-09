package me.ccrama.redditslide;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
/**
 * Created by ccrama on 7/19/2015.
 */
public class LastComments {
    public static java.util.HashMap<java.lang.String, java.lang.Integer> commentsSince;

    public static void setCommentsSince(java.util.List<net.dean.jraw.models.Submission> submissions) {
        if (me.ccrama.redditslide.LastComments.commentsSince == null) {
            me.ccrama.redditslide.LastComments.commentsSince = new java.util.HashMap<>();
        }
        com.lusfold.androidkeyvaluestore.core.KVManger m = com.lusfold.androidkeyvaluestore.KVStore.getInstance();
        try {
            for (net.dean.jraw.models.Submission s : submissions) {
                java.lang.String fullname = s.getFullName();
                // Check if KVStore has a key containing comments + the fullname
                // This is necessary because the KVStore library is limited and Carlos didn't realize the performance impact
                android.database.Cursor cur = m.execQuery("SELECT * FROM ? WHERE ? LIKE '%?%' LIMIT 1", new java.lang.String[]{ com.lusfold.androidkeyvaluestore.core.KVManagerImpl.TABLE_NAME, com.lusfold.androidkeyvaluestore.core.KVManagerImpl.COLUMN_KEY, "comments" + fullname });
                boolean contains = (cur != null) && (cur.getCount() > 0);
                com.lusfold.androidkeyvaluestore.utils.CursorUtils.closeCursorQuietly(cur);
                if (contains) {
                    me.ccrama.redditslide.LastComments.commentsSince.put(fullname, java.lang.Integer.valueOf(m.get("comments" + fullname)));
                }
            }
        } catch (java.lang.Exception ignored) {
        }
    }

    public static int commentsSince(net.dean.jraw.models.Submission s) {
        if ((me.ccrama.redditslide.LastComments.commentsSince != null) && me.ccrama.redditslide.LastComments.commentsSince.containsKey(s.getFullName()))
            return s.getCommentCount() - me.ccrama.redditslide.LastComments.commentsSince.get(s.getFullName());

        return 0;
    }

    public static void setComments(net.dean.jraw.models.Submission s) {
        if (me.ccrama.redditslide.LastComments.commentsSince == null) {
            me.ccrama.redditslide.LastComments.commentsSince = new java.util.HashMap<>();
        }
        com.lusfold.androidkeyvaluestore.KVStore.getInstance().insertOrUpdate("comments" + s.getFullName(), java.lang.String.valueOf(s.getCommentCount()));
        me.ccrama.redditslide.LastComments.commentsSince.put(s.getFullName(), s.getCommentCount());
    }
}