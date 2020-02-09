/**
 * Created by carlo_000 on 10/13/2015.
 */
package me.ccrama.redditslide;
public class CheckInstall extends android.content.BroadcastReceiver {
    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        java.lang.String packageName = intent.getDataString();
        if (packageName.equals(context.getString(me.ccrama.redditslide.R.string.youtube_plugin_package)) || packageName.equals(context.getString(me.ccrama.redditslide.R.string.ui_unlock_package))) {
            com.jakewharton.processphoenix.ProcessPhoenix.triggerRebirth(context.getApplicationContext());
        }
    }
}