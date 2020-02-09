package me.ccrama.redditslide.SubmissionViews;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import me.ccrama.redditslide.Activities.CommentsScreenSingle;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.Arrays;
import me.ccrama.redditslide.OpenRedditLink;
public class OpenVRedditTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
    private java.lang.ref.WeakReference<android.app.Activity> contextActivity;

    private java.lang.String subreddit;

    public OpenVRedditTask(android.app.Activity contextActivity, java.lang.String subreddit) {
        this.contextActivity = new java.lang.ref.WeakReference<>(contextActivity);
        this.subreddit = subreddit;
    }

    protected java.lang.Void doInBackground(java.lang.String... urls) {
        java.lang.String url = urls[0];
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        java.lang.String hash = url.substring(url.lastIndexOf("/"), url.length());
        try {
            java.net.URL newUrl = new java.net.URL("https://www.reddit.com/video" + hash);
            java.net.HttpURLConnection ucon = ((java.net.HttpURLConnection) (newUrl.openConnection()));
            ucon.setInstanceFollowRedirects(false);
            java.lang.String secondURL = new java.net.URL(ucon.getHeaderField("location")).toString();
            me.ccrama.redditslide.util.LogUtil.v(secondURL);
            me.ccrama.redditslide.OpenRedditLink.openUrl(contextActivity.get(), secondURL, true);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            me.ccrama.redditslide.util.LinkUtil.openUrl(url, me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), contextActivity.get());
        }
        return null;
    }
}