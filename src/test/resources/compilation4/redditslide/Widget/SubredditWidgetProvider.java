/**
 * Created by carlo_000 on 5/4/2016.
 */
package me.ccrama.redditslide.Widget;
import me.ccrama.redditslide.Activities.SetupWidget;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Activities.SubredditView;
public class SubredditWidgetProvider extends android.appwidget.AppWidgetProvider {
    public static final java.lang.String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final java.lang.String SUBMISSION = "SUBMISSION";

    public static final java.lang.String REFRESH = "REFRESH";

    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        android.appwidget.AppWidgetManager mgr = android.appwidget.AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(me.ccrama.redditslide.Widget.SubredditWidgetProvider.UPDATE_MEETING_ACTION)) {
            int appWidgetId = intent.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            mgr.notifyAppWidgetViewDataChanged(appWidgetId, me.ccrama.redditslide.R.id.list_view);
            int view = me.ccrama.redditslide.R.layout.widget;
            switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(appWidgetId, context)) {
                case 1 :
                    view = me.ccrama.redditslide.R.layout.widget_dark;
                    break;
                case 2 :
                    view = me.ccrama.redditslide.R.layout.widget_light;
                    break;
            }
            android.widget.RemoteViews rv = new android.widget.RemoteViews(context.getPackageName(), view);
            rv.setViewVisibility(me.ccrama.redditslide.R.id.loading, android.view.View.GONE);
            rv.setViewVisibility(me.ccrama.redditslide.R.id.refresh, android.view.View.VISIBLE);
            mgr.partiallyUpdateAppWidget(appWidgetId, rv);
        } else if (intent.getAction().contains(me.ccrama.redditslide.Widget.SubredditWidgetProvider.REFRESH)) {
            int appWidgetId = intent.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            updateFromIdPartially(appWidgetId, context, mgr);
        } else if (intent.getAction().contains(me.ccrama.redditslide.Widget.SubredditWidgetProvider.SUBMISSION)) {
            int appWidgetId = intent.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            android.content.Intent i = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
            i.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSubFromId(appWidgetId, context));
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

    public static java.lang.String lastDone;

    public static java.lang.String getSubFromId(int id, android.content.Context mContext) {
        java.lang.String sub = mContext.getSharedPreferences("widget", 0).getString(id + "_sub", "");
        if (sub.isEmpty() && (me.ccrama.redditslide.Widget.SubredditWidgetProvider.lastDone != null)) {
            return me.ccrama.redditslide.Widget.SubredditWidgetProvider.lastDone;
        } else {
            return sub;
        }
    }

    public static int getThemeFromId(int id, android.content.Context mContext) {
        return mContext.getSharedPreferences("widget", 0).getInt(id + "_sub_theme", 0);
    }

    public static int getViewType(int id, android.content.Context mContext) {
        return mContext.getSharedPreferences("widget", 0).getInt(id + "_sub_view", 0);
    }

    public static void setSubFromid(int id, java.lang.String sub, android.content.Context mContext) {
        mContext.getSharedPreferences("widget", 0).edit().putString(id + "_sub", sub).apply();
    }

    public static void setThemeToId(int id, int theme, android.content.Context mContext) {
        mContext.getSharedPreferences("widget", 0).edit().putInt(id + "_sub_theme", theme).apply();
    }

    public static void setViewType(int id, int checked, me.ccrama.redditslide.Activities.SetupWidget mContext) {
        mContext.getSharedPreferences("widget", 0).edit().putInt(id + "_sub_view", checked).apply();
    }

    public static void setSorting(int id, int sorting, me.ccrama.redditslide.Activities.SetupWidget mContext) {
        mContext.getSharedPreferences("widget", 0).edit().putInt(id + "_sub_sort", sorting).apply();
    }

    public static int getSorting(int id, android.content.Context mContext) {
        return mContext.getSharedPreferences("widget", 0).getInt(id + "_sub_sort", 0);
    }

    public static void setSortingTime(int id, int sorting, me.ccrama.redditslide.Activities.SetupWidget mContext) {
        mContext.getSharedPreferences("widget", 0).edit().putInt(id + "_sub_time", sorting).apply();
    }

    public static int getSortingTime(int id, android.content.Context mContext) {
        return mContext.getSharedPreferences("widget", 0).getInt(id + "_sub_time", 0);
    }

    public void onUpdate(android.content.Context context, android.appwidget.AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            updateFromId(appWidgetId, context, appWidgetManager);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void updateFromId(int appWidgetId, android.content.Context context, android.appwidget.AppWidgetManager appWidgetManager) {
        // Set up the intent that starts the ListViewService, which will
        // provide the views for this collection.
        android.content.Intent intent = new android.content.Intent(context, me.ccrama.redditslide.Widget.ListViewWidgetService.class);
        // Add the app widget ID to the intent extras.
        android.net.Uri data = android.net.Uri.withAppendedPath(android.net.Uri.parse("slide" + "://widget/id/"), java.lang.String.valueOf(appWidgetId));
        intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(android.net.Uri.parse(intent.toUri(android.content.Intent.URI_INTENT_SCHEME)));
        // Instantiate the RemoteViews object for the app widget layout.
        int view = me.ccrama.redditslide.R.layout.widget;
        switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(appWidgetId, context)) {
            case 1 :
                view = me.ccrama.redditslide.R.layout.widget_dark;
                break;
            case 2 :
                view = me.ccrama.redditslide.R.layout.widget_light;
                break;
        }
        android.widget.RemoteViews rv = new android.widget.RemoteViews(context.getPackageName(), view);
        // Set up the RemoteViews object to use a RemoteViews adapter.
        // This adapter connects
        // to a RemoteViewsService  through the specified intent.
        // This is how you populate the data.
        rv.setRemoteAdapter(appWidgetId, me.ccrama.redditslide.R.id.list_view, intent);
        // Trigger listview item click
        java.lang.String sub = me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSubFromId(appWidgetId, context);
        android.content.Intent startActivityIntent = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
        startActivityIntent.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, sub);
        // todo go to sub
        {
            android.content.Intent refreshIntent = new android.content.Intent(context, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
            refreshIntent.setData(data);
            refreshIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.SUBMISSION);
            refreshIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            android.app.PendingIntent pendingRefresh = android.app.PendingIntent.getBroadcast(context, 0, refreshIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(me.ccrama.redditslide.R.id.open, pendingRefresh);
        }
        {
            android.content.Intent refreshIntent = new android.content.Intent(context, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
            refreshIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.REFRESH);
            refreshIntent.setData(data);
            refreshIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            android.app.PendingIntent pendingRefresh = android.app.PendingIntent.getBroadcast(context, 0, refreshIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(me.ccrama.redditslide.R.id.refresh, pendingRefresh);
        }
        // The empty view is displayed when the collection has no items.
        // It should be in the same layout used to instantiate the RemoteViews  object above.
        rv.setEmptyView(me.ccrama.redditslide.R.id.list_view, me.ccrama.redditslide.R.id.empty_view);
        rv.setTextViewText(me.ccrama.redditslide.R.id.subreddit, sub);
        rv.setTextColor(me.ccrama.redditslide.R.id.subreddit, me.ccrama.redditslide.Visuals.Palette.getColor(sub));
        // 
        // Do additional processing specific to this app widget...
        // 
        final android.content.Intent activityIntent = new android.content.Intent(context, me.ccrama.redditslide.Activities.OpenContent.class);
        final android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(context, 0, activityIntent, 0);
        rv.setPendingIntentTemplate(me.ccrama.redditslide.R.id.list_view, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    public void updateFromIdPartially(int appWidgetId, android.content.Context context, android.appwidget.AppWidgetManager appWidgetManager) {
        // Set up the intent that starts the ListViewService, which will
        // provide the views for this collection.
        android.content.Intent intent = new android.content.Intent(context, me.ccrama.redditslide.Widget.ListViewWidgetService.class);
        // Add the app widget ID to the intent extras.
        android.net.Uri data = android.net.Uri.withAppendedPath(android.net.Uri.parse("slide" + "://widget/id/"), java.lang.String.valueOf(appWidgetId) + java.lang.System.currentTimeMillis());
        intent.setData(data);
        intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(android.net.Uri.parse(intent.toUri(android.content.Intent.URI_INTENT_SCHEME)));
        // Instantiate the RemoteViews object for the app widget layout.
        int view = me.ccrama.redditslide.R.layout.widget;
        switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(appWidgetId, context)) {
            case 1 :
                view = me.ccrama.redditslide.R.layout.widget_dark;
                break;
            case 2 :
                view = me.ccrama.redditslide.R.layout.widget_light;
                break;
        }
        android.widget.RemoteViews rv = new android.widget.RemoteViews(context.getPackageName(), view);
        // Set up the RemoteViews object to use a RemoteViews adapter.
        // This adapter connects
        // to a RemoteViewsService  through the specified intent.
        // This is how you populate the data.
        rv.setRemoteAdapter(appWidgetId, me.ccrama.redditslide.R.id.list_view, intent);
        rv.setViewVisibility(me.ccrama.redditslide.R.id.loading, android.view.View.VISIBLE);
        rv.setViewVisibility(me.ccrama.redditslide.R.id.refresh, android.view.View.GONE);
        // Trigger listview item click
        java.lang.String sub = me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSubFromId(appWidgetId, context);
        android.content.Intent startActivityIntent = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
        startActivityIntent.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, sub);
        // todo go to sub
        {
            android.content.Intent refreshIntent = new android.content.Intent(context, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
            refreshIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.SUBMISSION);
            refreshIntent.setData(data);
            refreshIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            android.app.PendingIntent pendingRefresh = android.app.PendingIntent.getBroadcast(context, 0, refreshIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(me.ccrama.redditslide.R.id.open, pendingRefresh);
        }
        {
            android.content.Intent refreshIntent = new android.content.Intent(context, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
            refreshIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.REFRESH);
            refreshIntent.setData(data);
            refreshIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            android.app.PendingIntent pendingRefresh = android.app.PendingIntent.getBroadcast(context, 0, refreshIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(me.ccrama.redditslide.R.id.refresh, pendingRefresh);
        }
        // The empty view is displayed when the collection has no items.
        // It should be in the same layout used to instantiate the RemoteViews  object above.
        rv.setEmptyView(me.ccrama.redditslide.R.id.list_view, me.ccrama.redditslide.R.id.empty_view);
        rv.setTextViewText(me.ccrama.redditslide.R.id.subreddit, sub);
        rv.setTextColor(me.ccrama.redditslide.R.id.subreddit, me.ccrama.redditslide.Visuals.Palette.getColor(sub));
        // 
        // Do additional processing specific to this app widget...
        // 
        final android.content.Intent activityIntent = new android.content.Intent(context, me.ccrama.redditslide.Activities.OpenContent.class);
        final android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(context, 0, activityIntent, 0);
        rv.setPendingIntentTemplate(me.ccrama.redditslide.R.id.list_view, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
}