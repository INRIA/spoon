package me.ccrama.redditslide;
import java.util.Set;
import java.util.HashSet;
/**
 * Created by carlo_000 on 10/16/2015.
 */
public class Hidden {
    public static final java.util.Set<java.lang.String> id = new java.util.HashSet<>();

    public static void setHidden(final net.dean.jraw.models.Contribution s) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void[] params) {
                try {
                    me.ccrama.redditslide.Hidden.id.add(s.getFullName());
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).hide(true, ((net.dean.jraw.models.Submission) (s)));
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void undoHidden(final net.dean.jraw.models.Contribution s) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void[] params) {
                try {
                    me.ccrama.redditslide.Hidden.id.remove(s.getFullName());
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).hide(false, ((net.dean.jraw.models.Submission) (s)));
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }
}