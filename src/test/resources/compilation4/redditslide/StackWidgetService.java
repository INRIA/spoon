package me.ccrama.redditslide;
import me.ccrama.redditslide.util.LogUtil;
import java.util.ArrayList;
import java.util.List;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * Created by ccrama on 10/2/2015.
 */
public class StackWidgetService extends android.widget.RemoteViewsService {
    @java.lang.Override
    public android.widget.RemoteViewsService.RemoteViewsFactory onGetViewFactory(android.content.Intent intent) {
        return new me.ccrama.redditslide.StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}