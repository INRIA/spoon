package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.CheckForMail;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 9/28/2015.
 */
public class CancelSubNotifs extends android.app.Activity {
    public static final java.lang.String EXTRA_SUB = "sub";

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        android.content.Intent intent = getIntent();
        android.os.Bundle extras = intent.getExtras();
        java.lang.String subName;
        if (extras != null) {
            subName = extras.getString(me.ccrama.redditslide.Activities.CancelSubNotifs.EXTRA_SUB, "");
            subName = subName.toLowerCase(java.util.Locale.ENGLISH);
            java.util.ArrayList<java.lang.String> subs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, "").toLowerCase(java.util.Locale.ENGLISH));
            java.lang.String toRemove = "";
            for (java.lang.String s : subs) {
                if (s.startsWith(subName + ":")) {
                    toRemove = s;
                }
            }
            if (!toRemove.isEmpty()) {
                subs.remove(toRemove);
            }
            me.ccrama.redditslide.Reddit.appRestart.edit().putString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, me.ccrama.redditslide.Reddit.arrayToString(subs)).apply();
        }
        finish();
    }
}