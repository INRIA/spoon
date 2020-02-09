/**
 * Created by carlo_000 on 10/13/2015.
 */
package me.ccrama.redditslide.Autocache;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.CommentCacheAsync;
import me.ccrama.redditslide.util.NetworkUtil;
public class CacheAll extends android.content.BroadcastReceiver {
    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        android.content.Context c = context;
        if (me.ccrama.redditslide.util.NetworkUtil.isConnectedNoOverride(c)) {
            if (me.ccrama.redditslide.Reddit.cachedData.getBoolean("wifiOnly", false) && (!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(context)))
                return;

            new me.ccrama.redditslide.CommentCacheAsync(c, me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "").split(",")).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}