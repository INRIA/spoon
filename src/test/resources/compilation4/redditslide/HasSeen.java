package me.ccrama.redditslide;
import java.util.HashMap;
import java.util.List;
import me.ccrama.redditslide.Synccit.SynccitRead;
import java.util.HashSet;
/**
 * Created by ccrama on 7/19/2015.
 */
public class HasSeen {
    public static java.util.HashSet<java.lang.String> hasSeen;

    public static java.util.HashMap<java.lang.String, java.lang.Long> seenTimes;

    public static void setHasSeenContrib(java.util.List<net.dean.jraw.models.Contribution> submissions) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        com.lusfold.androidkeyvaluestore.core.KVManger m = com.lusfold.androidkeyvaluestore.KVStore.getInstance();
        for (net.dean.jraw.models.Contribution s : submissions) {
            if (s instanceof net.dean.jraw.models.Submission) {
                java.lang.String fullname = s.getFullName();
                if (fullname.contains("t3_")) {
                    fullname = fullname.substring(3);
                }
                // Check if KVStore has a key containing the fullname
                // This is necessary because the KVStore library is limited and Carlos didn't realize the performance impact
                android.database.Cursor cur = m.execQuery("SELECT * FROM ? WHERE ? LIKE '%?%' LIMIT 1", new java.lang.String[]{ com.lusfold.androidkeyvaluestore.core.KVManagerImpl.TABLE_NAME, com.lusfold.androidkeyvaluestore.core.KVManagerImpl.COLUMN_KEY, fullname });
                boolean contains = (cur != null) && (cur.getCount() > 0);
                com.lusfold.androidkeyvaluestore.utils.CursorUtils.closeCursorQuietly(cur);
                if (contains) {
                    me.ccrama.redditslide.HasSeen.hasSeen.add(fullname);
                    java.lang.String value = m.get(fullname);
                    try {
                        if (value != null)
                            me.ccrama.redditslide.HasSeen.seenTimes.put(fullname, java.lang.Long.valueOf(value));

                    } catch (java.lang.Exception e) {
                    }
                }
            }
        }
    }

    public static void setHasSeenSubmission(java.util.List<net.dean.jraw.models.Submission> submissions) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        com.lusfold.androidkeyvaluestore.core.KVManger m = com.lusfold.androidkeyvaluestore.KVStore.getInstance();
        for (net.dean.jraw.models.Contribution s : submissions) {
            java.lang.String fullname = s.getFullName();
            if (fullname.contains("t3_")) {
                fullname = fullname.substring(3);
            }
            // Check if KVStore has a key containing the fullname
            // This is necessary because the KVStore library is limited and Carlos didn't realize the performance impact
            android.database.Cursor cur = m.execQuery("SELECT * FROM ? WHERE ? LIKE '%?%' LIMIT 1", new java.lang.String[]{ com.lusfold.androidkeyvaluestore.core.KVManagerImpl.TABLE_NAME, com.lusfold.androidkeyvaluestore.core.KVManagerImpl.COLUMN_KEY, fullname });
            boolean contains = (cur != null) && (cur.getCount() > 0);
            com.lusfold.androidkeyvaluestore.utils.CursorUtils.closeCursorQuietly(cur);
            if (contains) {
                me.ccrama.redditslide.HasSeen.hasSeen.add(fullname);
                java.lang.String value = m.get(fullname);
                try {
                    if (value != null)
                        me.ccrama.redditslide.HasSeen.seenTimes.put(fullname, java.lang.Long.valueOf(value));

                } catch (java.lang.Exception ignored) {
                }
            }
        }
    }

    public static boolean getSeen(net.dean.jraw.models.Submission s) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        java.lang.String fullname = s.getFullName();
        if (fullname.contains("t3_")) {
            fullname = fullname.substring(3);
        }
        return ((me.ccrama.redditslide.HasSeen.hasSeen.contains(fullname) || me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.contains(fullname)) || (s.getDataNode().has("visited") && s.getDataNode().get("visited").asBoolean())) || (s.getVote() != net.dean.jraw.models.VoteDirection.NO_VOTE);
    }

    public static boolean getSeen(java.lang.String s) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        android.net.Uri uri = me.ccrama.redditslide.OpenRedditLink.formatRedditUrl(s);
        java.lang.String fullname = s;
        if (uri != null) {
            java.lang.String host = uri.getHost();
            if (host.startsWith("np")) {
                uri = uri.buildUpon().authority(host.substring(2)).build();
            }
            me.ccrama.redditslide.OpenRedditLink.RedditLinkType type = me.ccrama.redditslide.OpenRedditLink.getRedditLinkType(uri);
            java.util.List<java.lang.String> parts = uri.getPathSegments();
            switch (type) {
                case SHORTENED :
                    {
                        fullname = parts.get(0);
                        break;
                    }
                case COMMENT_PERMALINK :
                    {
                        fullname = parts.get(3);
                        break;
                    }
                case SUBMISSION :
                    {
                        fullname = parts.get(3);
                        break;
                    }
                case SUBMISSION_WITHOUT_SUB :
                    {
                        fullname = parts.get(1);
                        break;
                    }
            }
        }
        if (fullname.contains("t3_")) {
            fullname = fullname.substring(3);
        }
        me.ccrama.redditslide.HasSeen.hasSeen.add(fullname);
        return me.ccrama.redditslide.HasSeen.hasSeen.contains(fullname) || me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.contains(fullname);
    }

    public static long getSeenTime(net.dean.jraw.models.Submission s) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        java.lang.String fullname = s.getFullName();
        if (fullname.contains("t3_")) {
            fullname = fullname.substring(3);
        }
        if (me.ccrama.redditslide.HasSeen.seenTimes.containsKey(fullname)) {
            return me.ccrama.redditslide.HasSeen.seenTimes.get(fullname);
        } else {
            try {
                return java.lang.Long.valueOf(com.lusfold.androidkeyvaluestore.KVStore.getInstance().get(fullname));
            } catch (java.lang.NumberFormatException e) {
                return 0;
            }
        }
    }

    public static void addSeen(java.lang.String fullname) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
        }
        if (me.ccrama.redditslide.HasSeen.seenTimes == null) {
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        if (fullname.contains("t3_")) {
            fullname = fullname.substring(3);
        }
        me.ccrama.redditslide.HasSeen.hasSeen.add(fullname);
        me.ccrama.redditslide.HasSeen.seenTimes.put(fullname, java.lang.System.currentTimeMillis());
        long result = com.lusfold.androidkeyvaluestore.KVStore.getInstance().insert(fullname, java.lang.String.valueOf(java.lang.System.currentTimeMillis()));
        if (result == (-1)) {
            com.lusfold.androidkeyvaluestore.KVStore.getInstance().update(fullname, java.lang.String.valueOf(java.lang.System.currentTimeMillis()));
        }
        if (!fullname.contains("t1_")) {
            me.ccrama.redditslide.Synccit.SynccitRead.newVisited.add(fullname);
            me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.add(fullname);
        }
    }

    public static void addSeenScrolling(java.lang.String fullname) {
        if (me.ccrama.redditslide.HasSeen.hasSeen == null) {
            me.ccrama.redditslide.HasSeen.hasSeen = new java.util.HashSet<>();
        }
        if (me.ccrama.redditslide.HasSeen.seenTimes == null) {
            me.ccrama.redditslide.HasSeen.seenTimes = new java.util.HashMap<>();
        }
        if (fullname.contains("t3_")) {
            fullname = fullname.substring(3);
        }
        me.ccrama.redditslide.HasSeen.hasSeen.add(fullname);
        me.ccrama.redditslide.HasSeen.seenTimes.put(fullname, java.lang.System.currentTimeMillis());
        com.lusfold.androidkeyvaluestore.KVStore.getInstance().insert(fullname, java.lang.String.valueOf(java.lang.System.currentTimeMillis()));
        if (!fullname.contains("t1_")) {
            me.ccrama.redditslide.Synccit.SynccitRead.newVisited.add(fullname);
            me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.add(fullname);
        }
    }
}