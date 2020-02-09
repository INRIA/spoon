package me.ccrama.redditslide.Activities;
import java.net.URL;
import java.net.MalformedURLException;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by ccrama on 9/28/2015.
 */
public class MakeExternal extends android.app.Activity {
    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        java.lang.String url = getIntent().getStringExtra("url");
        if (url != null) {
            try {
                java.net.URL u = new java.net.URL(url);
                me.ccrama.redditslide.SettingValues.alwaysExternal.add(u.getHost());
                android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, me.ccrama.redditslide.SettingValues.alwaysExternal);
                e.apply();
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}