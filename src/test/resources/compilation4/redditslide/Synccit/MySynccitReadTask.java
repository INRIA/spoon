package me.ccrama.redditslide.Synccit;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import java.util.HashSet;
public class MySynccitReadTask extends me.ccrama.redditslide.Synccit.SynccitReadTask {
    private static final java.lang.String MY_DEV_NAME = "slide_for_reddit";

    private me.ccrama.redditslide.Adapters.SubmissionDisplay displayer;

    public MySynccitReadTask(me.ccrama.redditslide.Adapters.SubmissionDisplay displayer) {
        super(me.ccrama.redditslide.Synccit.MySynccitReadTask.MY_DEV_NAME);
        this.displayer = displayer;
    }

    public MySynccitReadTask() {
        super(me.ccrama.redditslide.Synccit.MySynccitReadTask.MY_DEV_NAME);
    }

    @java.lang.Override
    protected void onVisited(java.util.HashSet<java.lang.String> visitedThreadIds) {
        me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.addAll(visitedThreadIds);
        // save the newly "seen" synccit posts to SEEN
        if (me.ccrama.redditslide.SettingValues.storeHistory) {
            for (java.lang.String id : visitedThreadIds) {
                me.ccrama.redditslide.HasSeen.addSeen(id);
            }
        }
    }

    @java.lang.Override
    protected java.lang.String getUsername() {
        return me.ccrama.redditslide.SettingValues.synccitName;
    }

    @java.lang.Override
    protected java.lang.String getAuth() {
        return me.ccrama.redditslide.SettingValues.synccitAuth;
    }

    @java.lang.Override
    protected java.lang.String getUserAgent() {
        return "slide_for_reddit";
    }

    @java.lang.Override
    public void onPostExecute(me.ccrama.redditslide.Synccit.SynccitResponse result) {
        super.onPostExecute(result);
        if (displayer != null)
            displayer.updateViews();

    }
}