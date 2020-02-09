package me.ccrama.redditslide.util;
import java.net.URL;
import java.util.Set;
import java.net.MalformedURLException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
/**
 * Created by Carlos on 8/12/2016.
 *
 * Code adapted from http://www.hidroh.com/2016/05/19/hacking-up-ad-blocker-android/
 */
public class AdBlocker {
    private static final java.lang.String DOMAINS_FILE = "adblocksources.txt";

    private static final java.util.Set<java.lang.String> DOMAINS = new java.util.HashSet<>();// Use hash set for performance


    public static void init(final android.content.Context context) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... voids) {
                try {
                    me.ccrama.redditslide.util.AdBlocker.loadFromAssets(context);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @android.support.annotation.WorkerThread
    private static void loadFromAssets(android.content.Context context) throws java.io.IOException {
        try {
            java.io.InputStream stream = context.getAssets().open(me.ccrama.redditslide.util.AdBlocker.DOMAINS_FILE);
            okio.BufferedSource buffer = okio.Okio.buffer(okio.Okio.source(stream));
            java.lang.String line;
            while ((line = buffer.readUtf8Line()) != null) {
                me.ccrama.redditslide.util.AdBlocker.DOMAINS.add(line);
            } 
            buffer.close();
            stream.close();
        } catch (java.lang.Exception ignored) {
        }
    }

    public static boolean isAd(java.lang.String url, android.content.Context context) {
        me.ccrama.redditslide.util.AdBlocker.init(context);
        try {
            java.lang.String host = new java.net.URL(url).getHost();
            return (host != null) && me.ccrama.redditslide.util.AdBlocker.hostMatches(host);
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean hostMatches(java.lang.String host) {
        if (host.isEmpty())
            return false;

        int firstPeriod = host.indexOf(".");
        return me.ccrama.redditslide.util.AdBlocker.DOMAINS.contains(host) || (((firstPeriod + 1) < host.length()) && me.ccrama.redditslide.util.AdBlocker.DOMAINS.contains(host.substring(firstPeriod + 1)));
    }

    public static android.webkit.WebResourceResponse createEmptyResource() {
        return new android.webkit.WebResourceResponse("text/plain", "utf-8", new java.io.ByteArrayInputStream("".getBytes()));
    }
}