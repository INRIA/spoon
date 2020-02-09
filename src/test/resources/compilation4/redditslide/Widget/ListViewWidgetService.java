package me.ccrama.redditslide.Widget;
import java.util.Set;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.Authentication;
import java.util.HashSet;
/**
 * Created by carlo_000 on 5/4/2016.
 */
public class ListViewWidgetService extends android.widget.RemoteViewsService {
    public android.widget.RemoteViewsService.RemoteViewsFactory onGetViewFactory(android.content.Intent intent) {
        return new me.ccrama.redditslide.Widget.ListViewRemoteViewsFactory(this.getApplicationContext(), intent, "android", intent.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, 0));
    }
}